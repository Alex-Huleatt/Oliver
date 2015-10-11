/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Oliver.Moods;

import Oliver.Const;
import Oliver.Soldier;
import battlecode.common.Direction;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TerrainTile;
import java.util.Random;

/**
 *
 * @author alexhuleatt
 */
public abstract class Mood {
    
    protected Soldier s;
    protected RobotController rc;
    protected int goalSlope;
    protected MapLocation enemyHQ;
    protected Random rand = new Random();
    
    public Mood(Soldier s) {
        this.s = s;
        this.rc = s.getRC();
        this.enemyHQ = rc.senseEnemyHQLocation();
    }
    
    public void act() throws Exception { }
    
    public Mood transition(){
        return null;
    }
    
    public RobotInfo[] getEnemies(RobotController rc) {
        return null;
    }
    
    public static MapLocation[] getBadMines(RobotController rc) {
        return null;
    }
    
    public static MapLocation[] getBadMines(RobotController rc, int disSquared) {
        return null;
    }
    
    public void move(Direction dir) throws Exception{
        if (rc.canMove(dir)) {
            rc.move(dir);
        } else {
            System.out.println("rc failed to move in dir");
        }
    }
    
    public void moveTowards(MapLocation goal) throws Exception {
        MapLocation myLoc = rc.getLocation();
        if(!Const.isObstacle(rc, myLoc.directionTo(enemyHQ))) { //TODO: NOT THIS.
            move(myLoc.directionTo(enemyHQ));
            return;
        }
        
        // bugggg
        int obsOne, obsTwo;
        for (int i = 0; i < 8; i++) {
            if (Const.isObstacle(rc, Const.directions[i])) {
                continue;
            }
            obsOne = (i + 1) % 8;
            obsTwo = (i + 2) % 8;
            if (Const.isObstacle(rc, Const.directions[obsOne]) && (i%2 == 1 || Const.isObstacle(rc, Const.directions[obsTwo]))){
                move(Const.directions[i]);
                return;
            }
        }
        System.out.println("Could not bug :(");
    }
    

    
    
}
