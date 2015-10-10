/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Oliver.Moods;

import Oliver.Soldier;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

/**
 *
 * @author alexhuleatt
 */
public abstract class Mood {
    
    protected Soldier s;
    protected RobotController rc;
    
    public Mood(Soldier s) {
        this.s = s;
        this.rc = s.getRC();
    }
    
    public void act() throws Exception {
        
    }
    
    public Mood transition(){
        return this;
    }
    
    public RobotInfo[] getEnemies(RobotController rc) {
        return null;
    }
    
    public static MapLocation[] getBadMines(RobotController rc) {
        return null;
    }
    
}
