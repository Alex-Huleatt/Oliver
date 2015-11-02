package team016.Moods.AllIn;

import team016.Moods.Zerg.*;
import team016.Const;
import team016.Units.Soldier;
import team016.Moods.Mood;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class DRushing extends Mood {

    public DRushing(Soldier s) {
        super(s);
    }

    @Override
    public Mood swing() throws Exception {
        Mood sp = super.swing();
        if (sp != null) return sp;
        // TODO: transition states:
        // - If an enemy unit is sensed within range X, Zerg.Aggro
        getNearbyRobots(25);
        if (enemies.length > 0) {
            return new Aggro((Soldier)u);
        }
        return null;
    }

    @Override
    public void act() throws Exception {
        moveTowards(enemyHQ);
    }

    @Override
    public String toString() {
        return "D: >:D";
    }
}
