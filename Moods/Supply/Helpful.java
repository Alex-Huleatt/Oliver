/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Moods.Supply;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import battlecode.common.Team;
import java.util.logging.Level;
import java.util.logging.Logger;
import team016.Moods.DefaultMood;
import team016.Moods.Mood;
import team016.Units.Soldier;

/**
 *
 * @author alexhuleatt
 */
public class Helpful extends Mood {

    MapLocation goal;

    public Helpful(Soldier s) throws Exception {
        super(s);
        MapLocation[] encamps
                = rc.senseEncampmentSquares(rc.getLocation(), 100000, Team.NEUTRAL);
        System.out.println(encamps.length);
        for (MapLocation m : encamps) {
            if (goodSupplySquare(m)) {
                goal = m;
                break;
            }
        }
    }

    /**
     * TOD
     *
     * @param m
     * @param mO
     *
     * @return
     */
    public final boolean goodSupplySquare(MapLocation m) {
        return true;
    }

    @Override
    public Mood swing() {
        if (goal == null) {
            return new DefaultMood((Soldier)u);
        }
        return null;
    }

    @Override
    public void act() throws Exception {
        //report my life
        if (goal != null) {
            MapLocation me = rc.getLocation();

            if (me.distanceSquaredTo(goal) == 0) {
                rc.captureEncampment(RobotType.SUPPLIER);
                return;
            }

            moveTowards(goal);
        }
    }

}
