/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Moods.HQ;

import team016.Const;
import team016.Units.HQ;
import team016.Moods.Mood;
import team016.Units.Unit;
import battlecode.common.Direction;
import battlecode.common.GameObject;
import battlecode.common.Robot;
import battlecode.common.RobotType;

/**
 *
 * @author alexhuleatt
 */
public class Threatened extends Mood {

    public Threatened(Unit u) {
        super(u);
    }

    @Override
    public Mood swing() throws Exception {
        GameObject[] things = rc.senseNearbyGameObjects(Robot.class, RobotType.HQ.sensorRadiusSquared, team.opponent());
        if (things.length > 0) {
            Robot[] ri_arr = Const.robotFilter(things, team.opponent());
            if (ri_arr.length == 0) {
                rc.broadcast(0, 0);
                return new Happy((HQ) u);
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
    
    public String toString() {return ":O";}

}
