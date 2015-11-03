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
import team016.Units.Soldier;
import team016.Units.Unit;

/**
 *
 * @author alexhuleatt
 */
public class Helpful extends Mood {

    MapLocation goal;
    boolean acted;
    final static MapLocation nullGoal = new MapLocation(-1, 255);
    boolean encamping;
    RobotType encType;
    int close_time;

    public Helpful(Soldier s) throws Exception {
        super(s);
        findGoal();
        acted = false;
        encamping = false;
        close_time = 0;
    }

    public final void findGoal() throws Exception {
        goal = Const.intToLoc(radC.read("SUPPLY_SQUARE_POSN",
                Clock.getRoundNum()));
        if (!Const.validLoc(goal)) {
            radC.write("SUPPLY_REQUEST_NEW_POSN", 1, Clock.getRoundNum());
        }

        acted = false;
    }

    @Override
    public Mood swing() throws Exception {
        if (radC.read("MEDBAY_COUNT", Clock.getRoundNum() - 1) ==-1) {
            return new Weeoo((Soldier) u);
        }
        if (goal == null || goal.equals(nullGoal)) {
            findGoal();
        }
        return null;
    }

    @Override
    public void act() throws Exception {
        if (encamping) {
            if (Const.validLoc(goal)) {
                if (encType == RobotType.SUPPLIER) {
                    radC.unitReport("SUPPLY_COUNT_OFFSET");
                }
                if (encType == RobotType.GENERATOR) {
                    radC.unitReport("GEN_COUNT_OFFSET");
                }
            }
            return;
        }
        if (!acted) {
            acted = true;
            radC.write("SUPPLY_REQUEST_OFFSET", 0, Clock.getRoundNum());
        }
        //report my life
        if (Const.validLoc(goal)) {
            radC.unitReport("SUPPLY_COUNT_OFFSET");
            radC.unitReport("GEN_COUNT_OFFSET");
        } else {
            //radC.write("SUPPLY_REQUEST_NEW_POSN", 1, Clock.getRoundNum());
            findGoal();
            return;
        }
        if (me.distanceSquaredTo(goal) < 5) {
            close_time++;
        } else {
            close_time = 0;
        }

        if (close_time > 5 || goal.distanceSquaredTo(rc.senseHQLocation()) <= 4) {
            goal = null;
            radC.write("SUPPLY_REQUEST_NEW_POSN", 1, Clock.getRoundNum());
        }
        if (goal != null) {
            if (rc.isActive()) {
                if (me.distanceSquaredTo(goal) == 0
                        && rc.senseEncampmentSquare(goal)
                        && rc.getTeamPower() > GameConstants.CAPTURE_POWER_COST * (1.5 + rc.senseAlliedEncampmentSquares().length)) {
                    try {
                        encamping = true;
                        int supply_cnt = radC.read("SUPPLY_COUNT_OFFSET", Clock.getRoundNum() - 1);
                        int gen_cnt = radC.read("GEN_COUNT_OFFSET", Clock.getRoundNum() - 1);
                        if (supply_cnt < gen_cnt) {
                            encType = RobotType.SUPPLIER;
                        } else {
                            encType = RobotType.GENERATOR;
                        }
                        rc.captureEncampment(encType);
                    } catch (Exception e) {
                        encamping = false;
                    }
                    return;
                }
                if (me.distanceSquaredTo(goal) < 12) {
                    if (simpleMove(goal)) {
                        return;
                    }
                }
                if (rc.canSenseSquare(goal)) {
                    GameObject g = rc.senseObjectAtLocation(goal);
                    if (g instanceof Robot) {
                        RobotInfo ri = rc.senseRobotInfo((Robot) g);
                        if ((ri.type == RobotType.GENERATOR || ri.type == RobotType.SUPPLIER) && ri.team == team) {
                            goal = null;
                            radC.write("SUPPLY_REQUEST_NEW_POSN", 1, Clock.getRoundNum());
                        }
                    }
                }
                moveTowards(goal);
            }
        } else {
            findGoal();
        }
    }

    @Override
    public String toString() {
        return ":[ " + goal;
    }

}
