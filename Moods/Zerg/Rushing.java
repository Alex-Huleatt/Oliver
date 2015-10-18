package Oliver.Moods.Zerg;

import Oliver.Const;
import Oliver.Soldier;
import Oliver.Moods.Mood;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class Rushing extends Mood {

    public Rushing(Soldier s) {
        super(s);
    }

    @Override
    public Mood transition() {
        // TODO: transition states:
        // - If an enemy unit is sensed within range X, Zerg.Aggro
        getNearbyRobots(25);
        if (enemies.length > 0) {
            return new Aggro(s, allies, enemies);
        }
        return null;
    }

    @Override
    public void act() throws Exception {
        moveTowards(enemyHQ);
    }

    @Override
    public String toString() {
        return "Z: >:D";
    }
}