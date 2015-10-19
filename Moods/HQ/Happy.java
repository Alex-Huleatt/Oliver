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

/**
 *
 * @author alexhuleatt
 */
public class Happy extends Mood {

    public Happy(HQ hq) {
        super(hq);
    }
    
    @Override
    public Mood swing() throws Exception {
        GameObject[] things = rc.senseNearbyGameObjects(Robot.class, RobotType.HQ.sensorRadiusSquared, team.opponent());
        if (things.length > 0) {
            Robot[] ri_arr = Const.robotFilter(things, team.opponent());
            if (ri_arr.length > 1) {
                //Broadcast SoS
                rc.broadcast(0, 1);
                return new Threatened(u);
            }
        }
        return null;
    }

    @Override
    public void act() throws Exception {
//        if (rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation()) > 300 && rc.isActive() && !rc.hasUpgrade(Upgrade.DEFUSION)) {
//            rc.researchUpgrade(Upgrade.DEFUSION);
//        }

        if (rc.isActive()) {
            // Spawn a soldier
            Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
            if (rc.canMove(dir)) {
                rc.spawn(dir);
            }
        }
        if (rc.isActive()) {
            simpleAttack();
        }
        rc.yield();
    }
    
    @Override
    public String toString() {return "^.^";}

}
