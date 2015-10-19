/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Moods;

import team016.Const;
import team016.Moods.Defense.Spooked;
import team016.Units.Soldier;
import team016.Units.Unit;
import battlecode.common.Direction;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TerrainTile;
import java.util.Random;
import team016.Comm.RadioController;

/**
 *
 * @author alexhuleatt
 */
public abstract class Mood {

    public Unit u;
    public RobotController rc;
    public MapLocation enemyHQ;
    public Robot[] enemies;
    public Robot[] allies;
    public Team team;
    public RadioController radC;
    
    MapLocation start;
    MapLocation end;
    int closest;
    boolean bug = false;
    boolean mining = false;
    int dir;
    boolean onRight;
    int pathAllowance = 30;

    public Mood(Unit u) {
        this.u = u;
        this.rc = u.getRC();
        this.enemyHQ = rc.senseEnemyHQLocation();
        this.team = rc.getTeam();
        this.onRight = false;
        this.radC = new RadioController(rc);
    }

    public void act() throws Exception {
    }

    public Mood swing() throws Exception {
        if (u instanceof Soldier && radC.read(RadioController.HQ_BLOCK, RadioController.SOS_OFFSET)==1 && rc.getLocation().distanceSquaredTo(rc.senseHQLocation()) < 100) {
            return new Spooked((Soldier) u);
        } else {
            return null;
        }
    }

    public void getNearbyRobots(int disSquared) {
        Robot[] robots = rc.senseNearbyGameObjects(
                Robot.class,
                disSquared);
        enemies = Const.robotFilter(robots, rc.getTeam().opponent());
        allies = Const.robotFilter(robots, rc.getTeam());
    }

    public static MapLocation[] getBadMines(RobotController rc) {
        return null;
    }

    public static MapLocation[] getBadMines(RobotController rc, int disSquared) {
        return null;
    }

    public void move(Direction dir) throws Exception {
        if (rc.isActive() && rc.canMove(dir)) {
            rc.move(dir);
            this.dir = Const.directionToInt(dir);
        } else {
            System.out.println("rc failed to move in dir");
        }
    }

    public void move(int dir) throws Exception {
        if (rc.isActive() && rc.canMove(Const.directions[dir])) {
            rc.move(Const.directions[dir]);
            this.dir = dir;
        } else {
            System.out.println("rc failed to move in dir");
        }
    }

    /**
     * The most godawful, but highly functional tangent bugger. Doesn't handle
     * every map very well. Guaranteed complete if the map doesn't change. The
     * map does change so that guarantee means literally nothing.
     *
     * @param goal
     * @throws Exception
     */
    public void moveTowards(MapLocation goal) throws Exception {
        MapLocation me = rc.getLocation();
        if (start == null || end == null || !goal.equals(end)) {
            // setup
            start = rc.getLocation();
            end = goal;
            bug = false;
            mining = false;
            closest = Integer.MAX_VALUE;
            // return;
        }

        if (me.distanceSquaredTo(goal) < 2) {
            // you did it! now what?
            return;
        }
        if (!bug && !Const.isObstacle(rc, me.directionTo(goal))
                && Const.locOnLine(start, goal, me)) {
            // we can move straight on the line
            move(me.directionTo(goal));
            return;
        }
        if (bug && Const.locOnLine(start, goal, me)
                && me.distanceSquaredTo(goal) < closest) {
            // we can stop bugging.
            bug = false;
            dir = Const.directionToInt(me.directionTo(goal));
            moveTowards(goal);
            return;
        }

        if (me.distanceSquaredTo(end) < closest) {
            closest = me.distanceSquaredTo(end);
        }

        if (mining || me.distanceSquaredTo(goal) > pathAllowance + closest) {
            rc.setIndicatorString(1, "Too far off path!");
            mining = true;
            if (Const.locOnLine(start, end, me) && me.distanceSquaredTo(goal) < closest) {
                mining = false;
                moveTowards(goal);
                return;
            }
            int perp = getDir(me, start, goal);
            //Now we need to determine which orientation
            MapLocation min = null;
            int mindis = Integer.MAX_VALUE;
            MapLocation td;
            int d;
            for (int i = -2; i <= 2; i++) {
                td = me.add(Const.directions[(perp + 8 + i) % 8]);
                d = td.distanceSquaredTo(goal);
                if (d < mindis) {
                    mindis = d;
                    min = td;
                }
                td = me.add(Const.directions[(perp + 4 + i) % 8]);
                d = td.distanceSquaredTo(goal);
                if (d < mindis) {
                    mindis = d;
                    min = td;
                }
            }
            Team mine = rc.senseMine(min);
            if (mine != null && mine != rc.getTeam()) {
                if (rc.isActive()) {
                    rc.defuseMine(min);
                }
                return;
            } else if (!Const.isObstacle(rc, min)) {
                move(me.directionTo(min));
                return;
            }
        }
        if (!bug) { //the initial transition. 2Spooky4me.
            //the initial move is important.
            MapLocation t_clos = null;
            int minDis = Integer.MAX_VALUE;
            for (int i = 0; i <= 7; i++) {
                MapLocation t = me.add(Const.directions[(dir + i + 8) % 8]);
                int t_dis = t.distanceSquaredTo(goal);
                if (!Const.isObstacle(rc, t) && t_dis < minDis) {
                    t_clos = t;
                    minDis = t_dis;
                }
            }
            if (t_clos == null) {
                return;
            }
            move(me.directionTo(t_clos));
            onRight = getOnRight(goal);
            bug = true;
            rc.setIndicatorString(0, "initial transition");
            return;
        }
        MapLocation nextMove = trace(onRight);
        if (nextMove != null) {
            move(me.directionTo(nextMove));
        } else {
            dir = (dir + 4) % 8; //let us turn around
            nextMove = trace(onRight);
            if (nextMove != null) {
                move(me.directionTo(nextMove));
            } else {
                //failure entirely. This should only happen with changing maps.
                //We'll restart bugging from the start.
                start = null;
                moveTowards(goal);
            }
        }
    }

    public int getDir(MapLocation me, MapLocation start, MapLocation goal) throws IllegalStateException {
        int dx = goal.x - start.x;
        int dy = goal.y - start.y;

        //the vector from start to goal is along [dx,dy]
        //direction perpendicular to that is [-dy,dx]
        //Let's go that way to start.
        int gx = -1 * dy;
        int gy = dx;
        if (gx != 0) {
            gx /= Math.abs(gx);
        }
        if (gy != 0) {
            gy /= Math.abs(gy);
        }
        //if gx 1 -> 1,2,3
        //if gx 0 -> 0,4
        //if gx -1 -> 5,6,7
        if (gx == 0) {
            switch (gy) {
                case -1:
                    return 0;
                case 1:
                    return 4;
            }
        }

        if (gx == 1) {
            switch (gy) {
                case -1:
                    return 1;
                case 0:
                    return 2;
                case 1:
                    return 3;
            }
        }

        if (gx == -1) {
            switch (gy) {
                case -1:
                    return 7;
                case 0:
                    return 6;
                case 1:
                    return 5;
            }
        }
        throw new IllegalStateException();
    }

    private boolean getOnRight(MapLocation goal) throws Exception {
        MapLocation me = rc.getLocation();
        int offset = Math.min(8 - dir, dir);
        int dir_to_obs = Const.directionToInt(me.directionTo(goal));
        return ((dir_to_obs + offset) % 8) < 4;
    }

    private MapLocation trace(boolean reverse) throws Exception {
        MapLocation me = rc.getLocation();
        MapLocation temp;
        int mindir = -1;
        int mindis = Integer.MAX_VALUE;
        int sd = (reverse) ? 1 : -1;
        for (int i = -2; i != 3; i++) {
            int d = (dir + i + 8) % 8;
            if (Const.isObstacle(rc, Const.directions[d])) {
                continue;
            }
            temp = me.add(Const.directions[d]);
            int dis = temp.distanceSquaredTo(
                    me.add(Const.directions[((dir + 2 * sd) + 8) % 8]));
            if ((Const.isObstacle(rc,
                    Const.directions[(d + sd + 8) % 8]) && dis < mindis)) {
                mindir = d;
                mindis = dis;
            }
        }
        if (mindir == -1) {
            return null;
        } else {
            return me.add(Const.directions[mindir]);
        }
    }

    public boolean moveish(Direction d) throws Exception {
        if (!rc.isActive()) {
            System.out.println("rc wasn't active in moveish");
            return false;
        }
        if (rc.canMove(d) && !Const.isObstacle(rc, d)) {
            rc.move(d);
            return false;
        }
        int local_dir = Const.directionToInt(d);
        for (int i = 1; i < 3; i++) {
            Direction left = Const.directions[((local_dir - i) + 8) % 8];
            if (rc.canMove(left) && !Const.isObstacle(rc, left)) {
                rc.move(left);
                return false;
            }
            Direction right = Const.directions[(local_dir + i) % 8];
            if (rc.canMove(right) && !Const.isObstacle(rc, d)) {
                rc.move(right);
                return false;
            }
        }

        return true;
    }

    protected void simpleAttack() throws Exception {
        Robot[] enemies = Const.getEnemies(rc,
                RobotType.HQ.attackRadiusMaxSquared);
        if (enemies.length > 0) {
            RobotInfo[] inf = new RobotInfo[enemies.length];
            for (int i = 0; i < enemies.length; i++) {
                inf[i] = rc.senseRobotInfo(enemies[i]);
            }
            RobotInfo mah_closest = Const.getClosest(rc.getLocation(), inf);
            if (rc.canAttackSquare(mah_closest.location)) {
                rc.attackSquare(mah_closest.location);
            }
        }
    }
}
