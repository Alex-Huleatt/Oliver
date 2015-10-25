/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Moods.HQ;

import team016.Const;
import team016.Units.HQ;
import team016.Moods.Mood;
import battlecode.common.Direction;
import battlecode.common.GameObject;
import battlecode.common.Robot;
import battlecode.common.RobotType;
import team016.Comm.RadioController;
import team016.Units.Soldier;
import team016.Units.Unit;

/**
 *
 * @author alexhuleatt
 */
public class Threatened extends Mood {

    public Threatened(Soldier s) {
        super(s);
    }

    @Override
    public Mood swing() throws Exception {
        return null;
    }

    @Override
    public void act() throws Exception {
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

    public String toString() {
        return ":O";
    }

}
