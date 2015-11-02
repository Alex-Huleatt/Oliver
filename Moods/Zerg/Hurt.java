package team016.Moods.Zerg;

import battlecode.common.Clock;
import team016.Const;
import team016.Units.Soldier;
import team016.Moods.Mood;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Direction;
import battlecode.common.MapLocation;

public class Hurt extends Mood {

    public Hurt(Soldier s) {
        super(s);
    }
    
    @Override
    public Mood swing() throws Exception{
        Mood sp = super.swing();
        if (sp != null) return sp;
        // - If unit has many allies around it, transition to:
        //  - Zerg.Aggro if there are enemy bots around
        //  - Zerg.Rushing if there are not
        // - Else stay Hurt
        
        getNearbyRobots(25);
        if (allies.length < 4 || enemies.length > allies.length) {
            return null;
        }
        if (enemies.length == 0) {
            return new Rushing((Soldier)u);
        }
        return new Aggro((Soldier)u);
    }

    @Override
    public void act() throws Exception {
        MapLocation target = Const.intToLoc(radC.read("TARGET_OFFSET", Clock.getRoundNum()));
        Direction goal = rc.getLocation().directionTo(target);
        Direction dir = Const.findSafeLoc(rc, enemies, goal, false);
        if (rc.isActive() && dir != null && !Const.isObstacle(rc, dir)) {
            rc.move(dir);
        }
    }


    @Override
    public String toString() {
        return "Z: :(";
    }
}