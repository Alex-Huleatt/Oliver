/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Moods.HQ;

import team016.Const;
import team016.Units.HQ;
import team016.Moods.Mood;
import team016.Units.Soldier;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.Robot;
import battlecode.common.RobotType;
import battlecode.common.Upgrade;
import battlecode.common.Clock;
import team016.Comm.RadioController;
import team016.Strat.StratType;

/**
 *
 * @author alexhuleatt
 */
public class Happy extends Mood {

    boolean defusion;

    public Happy(HQ hq) {
        super(hq);
    }

    @Override
    public Mood swing() throws Exception {

        return null;
    }

    @Override
    public void act() throws Exception {
        StratType mahStrat = ((HQ) u).sc.hqStrat;
        if (rc.isActive()) {

            if (mahStrat == StratType.RUSH_DEFUSION) {
                rc.researchUpgrade(Upgrade.DEFUSION);
            }

            if (rc.isActive()) {
                // Spawn a soldier
                for (int i = 0; i < 8; i++) {
                    Direction dir = Const.directions[i];
                    if (rc.canMove(dir)) {
                        rc.spawn(dir);
                        break;
                    }
                }

            }
        }
    }

    @Override
    public String toString() {
        return "^.^";
    }

}
