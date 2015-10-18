package Oliver.Moods.Zerg;

import Oliver.Const;
import Oliver.Soldier;
import Oliver.Moods.Mood;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class Hurt extends Mood {

    public Hurt(Soldier s) {
        super(s);
    }
    
    @Override
    public Mood transition() {
        // - If unit has many allies around it, transition to:
        //  - Zerg.Aggro if there are enemy bots around
        //  - Zerg.Rushing if there are not
        // - Else stay Hurt
        
        getNearbyRobots(25);
        if (allies.length < 4 || enemies.length > allies.length) {
            return null;
        }
        if (enemies.length == 0) {
            return new Rushing(s);
        }
        return new Aggro(s, allies, enemies);
    }

    @Override
    public void act() throws Exception {
        // TODO: move around enemies
        moveTowards(enemyHQ);
    }


    @Override
    public String toString() {
        return "Z: :(";
    }
}