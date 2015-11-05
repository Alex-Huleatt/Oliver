/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Moods.Supply;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import java.util.logging.Level;
import java.util.logging.Logger;
import team016.Comm.RadioController;
import team016.Const;
import team016.Moods.DefaultMood;
import team016.Moods.Mood;
import team016.Moods.Zerg.Rushing;
import team016.Units.Soldier;
import team016.Units.Unit;

/**
 *
 * @author alexhuleatt
 */
public class Weeoo extends Mood {

    MapLocation goal;
    boolean acted;
    final static MapLocation nullGoal = new MapLocation(-1, 255);
    boolean encamping;
    RobotType encType;
    int close_time;

    public Weeoo(Soldier s) throws Exception {
        super(s);
        goal = Const.intToLoc(radC.read("RALLY_OFFSET", Clock.getRoundNum()));
        acted = false;
        encamping = false;
        close_time = 0;
    }

    @Override
    public Mood swing() throws Exception {
        if (rc.canSenseSquare(goal)) {
            GameObject o = rc.senseObjectAtLocation(goal);
            if (o instanceof Robot) {
                RobotInfo ri = rc.senseRobotInfo((Robot) o);
                if (ri.team == team && ri.type == RobotType.MEDBAY) {
                    return new Rushing((Soldier) u);
                }
            }
        }
        int mcnt = radC.read("MEDBAY_COUNT", Clock.getRoundNum());
        if (mcnt==0) {
            return new Rushing((Soldier) u);
        }
        return null;
    }

    @Override
    public void act() throws Exception {
        if (encamping) {
            radC.unitReport("MEDBAY_COUNT");
            return;
        }
        radC.unitReport("MEDBAY_COUNT");

        int dis = me.distanceSquaredTo(goal);
        if (dis > 0) {
            if (dis < 10) {
                simpleMove(goal);
            } else {
                moveTowards(goal);
            }
        } else {
            if (rc.senseEncampmentSquare(goal)) {
                encamping = true;
                if (rc.isActive()) rc.captureEncampment(RobotType.MEDBAY);
            } else {
                goal = Const.intToLoc(radC.read("RALLY_OFFSET", Clock.getRoundNum()));
            }
        }
    }

    @Override
    public String toString() {
        return ":$ " + goal;
    }

}
