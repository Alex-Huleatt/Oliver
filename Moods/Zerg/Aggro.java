package Oliver.Moods.Zerg;

import Oliver.Const;
import Oliver.Soldier;
import Oliver.Moods.Mood;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Team;


public class Aggro extends Mood {

    public Aggro(Soldier s) {
        super(s);
    }
    
    public Aggro(Soldier s, Robot[] a, Robot[] e) {
        super(s);
        allies = a;
        enemies = e;
        
    }

    @Override
    public Mood swing() {
        // - If unit has low health and few allies, transition to Zerg.Hurt
        // - If there are no sensable unit around, transition to Zerg.Rushing
        // - Else return Aggro
        getNearbyRobots(25);
        if (enemies.length == 0) {
            return new Rushing(s);
        }
        if (rc.getEnergon() < 20) {
            return new Hurt(s);
        }
        
        return null;
        
    }

    @Override
    public void act() throws Exception {
        // TODO: move towards closest enemy
        if (enemies == null || enemies.length == 0) {
            System.out.println("In aggro state with no enemies");
            return;
        }
        RobotInfo bot = Const.getClosest(rc.getLocation(), enemies, rc);
        if (bot == null) {
            System.out.println("Aggro couldn't find closest bot!");
        }
        MapLocation closest = bot.location;
        Direction dir = rc.getLocation().directionTo(closest);
        int dist = rc.getLocation().distanceSquaredTo(closest);
        if (Const.getThreat(rc, enemies) < Const.getThreat(rc, allies) + 1) {
            if (dist > 15) moveTowards(closest);
            else moveish(dir);
            if (rc.isActive()) {
                mine(dir);
            }
            return;
        } else if (dist < 10) {
            moveish(dir.opposite());
        }
    }
    private boolean moveish(Direction d) throws Exception{
        if (!rc.isActive()) {
            System.out.println("rc wasn't active in moveish");
            return false;
        }
        if (rc.canMove(d) && !Const.isObstacle(rc, d)) {
            rc.move(d);
            return false;
        }
        int dir = Const.directionToInt(d);
        for (int i = 1; i < 3; i++) {
            Direction left = Const.directions[((dir - i) + 8) % 8];
            if (rc.canMove(left) && !Const.isObstacle(rc, left)) {
                rc.move(left);
                return false;
            }
            Direction right = Const.directions[(dir + i) % 8];
            if (rc.canMove(right) && !Const.isObstacle(rc, d)){
                rc.move(right);
                return false;
            }
        }
        
        return true;
    }
    
    private void mine(Direction d) throws Exception {
        int dir = Const.directionToInt(d);
        MapLocation me = rc.getLocation();
        
        MapLocation mine;
        Team mTeam;
        for (int i = 0; i < 1; i++) {
            mine = me.add(Const.directions[((dir + i) + 8) % 8]);
            mTeam = rc.senseMine(mine);
            if (mTeam == Team.NEUTRAL) {
                rc.defuseMine(mine);
                return;
            } else if (mTeam == rc.getTeam().opponent()) {
                // TODO: check if mine location is safe
            }
        }
        
        
    }

    @Override
    public String toString() {
        return "Z: >:(";
    }
}