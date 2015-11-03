/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Units;

import battlecode.common.RobotController;

/**
 *
 * @author alexhuleatt
 */
public class Generator extends Unit {

    public Generator(RobotController rc) {
        super(rc);
    }
    
        @Override
    public void run() throws Exception {
        radC.unitReport("GEN_COUNT_OFFSET");
        rc.yield();
    }
    
}
