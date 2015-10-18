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
    public Mood swing() throws Exception {
        Mood sp = super.swing();
        if (sp != null) return sp;
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


    @Override
    public String toString() {
        return "Z: >:(";
    }
}