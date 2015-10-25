/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Moods.Supply;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
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
    public Helpful(Soldier s) throws Exception {
        super((Unit) s);
        findGoal();
        acted=false;
    }

    public final void findGoal() throws Exception {
        goal = Const.intToLoc(radC.read(RadioController.REPORT_BLOCK, 
                RadioController.SUPPLY_SQUARE_POSN, 
                Clock.getRoundNum()));
    }
    @Override
    public Mood swing() {
//        if (goal == null) {
//            return new DefaultMood((Soldier) u);
//        }
//        return null;
        return null;
    }

    @Override
    public void act() throws Exception {
        if(!acted) {
            acted=true;
            radC.write(RadioController.REPORT_BLOCK, RadioController.SUPPLY_REQUEST_OFFSET, 0, Clock.getRoundNum());
        }
        //report my life
        radC.unitReport(RadioController.SUPPLY_COUNT_OFFSET);
        
        if (goal != null) {
            MapLocation me = rc.getLocation();
            if (rc.isActive()) {
                if (me.distanceSquaredTo(goal) == 0 && rc.senseEncampmentSquare(goal)) {
                    rc.captureEncampment(RobotType.SUPPLIER);
                    return;
                }

                moveTowards(goal);
                if (Const.isObstacle(rc, goal)) {
                    findGoal();
                }
            }
        } else {
            findGoal();
        }
    }

}
