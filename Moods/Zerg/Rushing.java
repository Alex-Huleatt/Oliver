package team016.Moods.Zerg;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import team016.Const;
import team016.Units.Soldier;
import team016.Moods.Mood;

public class Rushing extends Mood {

    public Rushing(Soldier s) {
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
        boolean regroup_flag = radC.read("REGROUP_FLAG", Clock.getRoundNum())==1;
        if (regroup_flag) {
            if (radC.read("THREAT_COUNT", Clock.getRoundNum()) > 0) {
                MapLocation encampment = Const.intToLoc(radC.read("THREAT_LOC", Clock.getRoundNum()));
                rc.setIndicatorString(2, "Rescuing");
                moveTowards(encampment);
                return;
            }
            MapLocation rally = Const.intToLoc(radC.read("RALLY_OFFSET", Clock.getRoundNum()));
            rc.setIndicatorString(2, "Rallying");
            moveTowards(rally);
            return;
        }
        MapLocation target = Const.intToLoc(
                radC.read("TARGET_OFFSET", Clock.getRoundNum()));
        rc.setIndicatorString(2, ""+target);
        moveTowards(target);
    }

    @Override
    public String toString() {
        return "Z: >:D";
    }
}
