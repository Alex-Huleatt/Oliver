/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Oliver.Moods.HQ;

import Oliver.Const;
import Oliver.HQ;
import Oliver.Moods.Mood;
import Oliver.Soldier;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.Robot;
import battlecode.common.RobotType;
import battlecode.common.Upgrade;
import battlecode.common.Clock;

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
    public void act() throws Exception {
        if (Clock.getRoundNum() == 50 && rc.readBroadcast(0) != 1) {
            double numMines = rc.senseNonAlliedMineLocations(rc.getLocation(), 100000).length;
            double mapSize =  rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation());
            defusion = numMines*3/(mapSize - 2000) > 0.6;
            rc.setIndicatorString(2, "Ratio: " + (numMines*3/(mapSize - 2000)));
        }

        if (defusion && rc.isActive() && !rc.hasUpgrade(Upgrade.DEFUSION)) {
            rc.researchUpgrade(Upgrade.DEFUSION);
        }

        if (rc.isActive()) {
            // Spawn a soldier
            Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
            if (rc.canMove(dir)) {
                rc.spawn(dir);
            }

        }

        GameObject[] things = rc.senseNearbyGameObjects(Robot.class, RobotType.HQ.sensorRadiusSquared, team.opponent());
        if (things.length > 0) {
            Robot[] ri_arr = Const.robotFilter(things, team.opponent());
            if (ri_arr.length > 1) {
                //Broadcast SoS
                rc.broadcast(0, 1);
                rc.setIndicatorString(0, ":O");
            } else if (ri_arr.length == 0) {
                rc.setIndicatorString(0, "^.^");
                rc.broadcast(0, 0);
            }
        }

        if (rc.isActive()) {
            simpleAttack();
        }
        rc.yield();

    }

}
