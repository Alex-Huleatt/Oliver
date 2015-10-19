package Oliver.Moods.Zerg;

import Oliver.Const;
import Oliver.Soldier;
import Oliver.Moods.Mood;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Direction;

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
        return new Aggro((Soldier)u, allies, enemies);
    }

    @Override
    public void act() throws Exception {
        Direction goal = rc.getLocation().directionTo(enemyHQ);
        Direction dir = Const.findSafeLoc(rc, enemies, goal, false);
        if (rc.isActive() && dir != null) {
            rc.move(dir);
        }
    }


    @Override
    public String toString() {
        return "Z: :(";
    }
}