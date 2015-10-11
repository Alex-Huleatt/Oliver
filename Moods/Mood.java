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
        if(!isObstacle(myLoc.directionTo(enemyHQ))) {
            move(myLoc.directionTo(enemyHQ));
            return;
        }
        
        // bugggg
        int obsOne, obsTwo;
        for (int i = 0; i < 8; i++) {
            if (isObstacle(Const.directions[i])) {
                continue;
            }
            obsOne = (i + 1) % 8;
            obsTwo = (i + 2) % 8;
            if (isObstacle(Const.directions[obsOne]) && (i%2 == 1 || isObstacle(Const.directions[obsTwo]))){
                move(Const.directions[i]);
                return;
            }
        }
        System.out.println("Could not bug :(");
    }
    
    public boolean isObstacle(MapLocation loc) throws Exception {
        TerrainTile tt = rc.senseTerrainTile(loc);
        if (tt == TerrainTile.VOID) return true;
        Team mine = rc.senseMine(loc);
        if (mine != null && mine != rc.getTeam()) return true;
        if (rc.canSenseSquare(loc)) {
            Robot ri = null;
            GameObject obj = rc.senseObjectAtLocation(loc);
            if (obj instanceof Robot) ri = (Robot) obj;
            if (ri != null && (rand.nextDouble() > .8 || rc.senseRobotInfo(ri).type == RobotType.HQ)) return true;
        }
        return false;
    }
    
    public boolean isObstacle(Direction dir) throws Exception {
        return isObstacle(rc.getLocation().add(dir));
    }
    
    
}
