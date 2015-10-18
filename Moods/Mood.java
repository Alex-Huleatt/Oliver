/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Oliver.Moods;

import Oliver.Const;
import Oliver.Soldier;
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

/**
 *
 * @author alexhuleatt
 */
public abstract class Mood {

    public Soldier s;
    public RobotController rc;
    public MapLocation enemyHQ;
    public Robot[] enemies;
    public Robot[] allies;
    
    protected MapLocation start;
    protected MapLocation end;
    protected int closest;
    protected boolean bug = false;
    protected Random rand = new Random();
    protected int dir;
    protected boolean onRight;
    protected int pathAllowance = 10;

    public Mood(Soldier s) {
        this.s = s;
        this.rc = s.getRC();
        this.enemyHQ = rc.senseEnemyHQLocation();
        this.onRight = false;
    }

    public void act() throws Exception {}

    public Mood transition() {return null;}

    public Robot[] getEnemies(RobotController rc, int disSquared) {
        GameObject[] obs = rc.senseNearbyGameObjects(
                Robot.class, 
                disSquared, 
                rc.getTeam().opponent());
        return Const.robotFilter(obs);
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
        if (rc.canMove(Const.directions[dir])) {
            rc.move(Const.directions[dir]);
        } else {
            System.out.println("rc failed to move in dir");
        }
    }

    /**
     * The most godawful,  but highly functional tangent bugger.
     * Doesn't handle every map very well.
     * Guaranteed complete if the map doesn't change.
     * The map does change so that guarantee means literally nothing.
     * @param goal
     * @throws Exception 
     */
    public void moveTowards(MapLocation goal) throws Exception {
        MapLocation me = rc.getLocation();
        if (Const.locOnLine(start, goal, me) 
                && me.distanceSquaredTo(end) < closest) {
            closest = me.distanceSquaredTo(end);
        }
        if (start == null || end == null || !goal.equals(end)) {
            // setup
            start = rc.getLocation();
            end = goal;
            bug = false;
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
        } else if (bug) {
            if (Const.locOnLine(start, goal, me)
                    && me.distanceSquaredTo(goal) == closest) {
                // we can stop bugging
                bug = false;
                moveTowards(goal);
                return;
            }
            // if we are too far away from the path, start mining
            MapLocation mid = Const.midpoint(start, goal);
            if (me.distanceSquaredTo(mid) > pathAllowance) {
                rc.setIndicatorString(1, "Too far off path!");
                MapLocation mineLoc = me.add(me.directionTo(goal)); // TODO: we need to move somewhere between line and goal
                Team mine = rc.senseMine(mineLoc);
                if(mine != null && mine != rc.getTeam()) {
                    if (rc.isActive()) rc.defuseMine(mineLoc);
                    return;
                } else if (!Const.isObstacle(rc, mineLoc)) {
                    move(me.directionTo(goal));
                    return;
                }
            }
        }
        if (!bug) onRight = getOnRight(goal);
        bug = true;
        MapLocation nextMove = trace(onRight);
        if (nextMove != null) {
            move(me.directionTo(nextMove));
        } else {
            dir = (dir+4)%8; //let us turn around
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
    
    public boolean getOnRight(MapLocation goal) throws Exception {
        MapLocation me = rc.getLocation();
        int offset = Math.min(8-dir, Math.abs((8-dir-8)));
        int dir_to_obs = Const.directionToInt(me.directionTo(goal));
        return ((dir_to_obs+offset) % 8) < 4;
    }

    public MapLocation trace(boolean reverse) throws Exception {
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
        if (mindir == -1) return null;
        else return me.add(Const.directions[mindir]);
    }

    public void simpleAttack() throws Exception {
        Robot[] enemies = getEnemies(rc, 
                RobotType.SOLDIER.attackRadiusMaxSquared);
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
