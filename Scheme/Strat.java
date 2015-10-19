/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Scheme;

import battlecode.common.RobotController;

/**
 * This should be the core of everything. 
 * HQ will have this instance, this should broadcast major changes 
 * in macro strats as well as important info like which types of encampments we
 * want.
 * @author alexhuleatt
 */
public class Strat {
    
    RobotController rc; //hq
    
    public Strat(RobotController rc) {
        this.rc = rc;
    }
    
    
}
