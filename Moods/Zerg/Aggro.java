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
    public Mood transition() {
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
        int dist = rc.getLocation().distanceSquaredTo(closest);
        if (enemies.length < allies.length) {
            if (dist > 15) moveTowards(closest);
            else moveish(rc.getLocation().directionTo(closest));
        } else if (dist < 10) {
            moveish(rc.getLocation().directionTo(closest).opposite());
        }
    }
    private void moveish(Direction d) throws Exception{
        if (!rc.isActive()) {
            System.out.println("rc wasn't active in moveish");
            return;
        }
        if (rc.canMove(d) && !Const.isObstacle(rc, d)) {
            rc.move(d);
            return;
        }
        int dir = Const.directionToInt(d);
        for (int i = 1; i < 3; i++) {
            Direction left = Const.directions[((dir - i) + 8) % 8];
            if (rc.canMove(left) && !Const.isObstacle(rc, left)) {
                rc.move(left);
                return;
            }
            Direction right = Const.directions[(dir + i) % 8];
            if (rc.canMove(right) && !Const.isObstacle(rc, d)){
                rc.move(right);
                return;
            }
        }
        
    }

    @Override
    public String toString() {
        return "Z: >:(";
    }
}