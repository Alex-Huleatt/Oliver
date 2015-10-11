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
    protected MapLocation start;
    protected MapLocation end;
    protected int closest;
    protected boolean bug = false; 
    protected MapLocation enemyHQ;
    protected Random rand = new Random();
    
    public Mood(Soldier s) {
        this.s = s;
        this.rc = s.getRC();
        this.enemyHQ = rc.senseEnemyHQLocation();
    }
    
    public void act() throws Exception { }
    
    public Mood transition(){return null;}
    
    public Robot[] getEnemies(RobotController rc, int disSquared) {
        GameObject[] obs = rc.senseNearbyGameObjects(Robot.class, disSquared, rc.getTeam().opponent());
        return Const.robotFilter(obs);
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
        if (start == null || end == null || !goal.equals(end)) {
            // setup
            start = rc.getLocation();
            end = goal;
            bug = false;
            closest = Integer.MAX_VALUE;
        }
        if (rc.getLocation().distanceSquaredTo(end) < 2) {
            // you did it! now what?
            return;
        }
        if(!bug && !Const.isObstacle(rc, start.directionTo(enemyHQ))) {
            // we can move straight on the line
            move(start.directionTo(enemyHQ));
            return;
        } else if (bug && Const.locOnLine(start, end, rc.getLocation()) && rc.getLocation().distanceSquaredTo(end) < closest) {
            // we can stop bugging
            bug = false;
            moveTowards(end);
            return;
        }
        closest = rc.getLocation().distanceSquaredTo(end);
        bug = true;
        int obsOne, obsTwo;
        for (int i = 0; i < 8; i++) {
            if (Const.isObstacle(rc, Const.directions[i])) continue;
            obsOne = (i + 1) % 8;
            obsTwo = (i + 2) % 8;
            if (Const.isObstacle(rc, Const.directions[obsOne]) && (i%2 == 1 || Const.isObstacle(rc, Const.directions[obsTwo]))){
                move(Const.directions[i]);
                return;
            }
        }
        System.out.println("Could not bug :(");
    }
    
    public void simpleAttack() throws Exception{
        Robot[] enemies = getEnemies(rc, RobotType.SOLDIER.attackRadiusMaxSquared);
        if (enemies.length > 0) {
            RobotInfo[] inf = new RobotInfo[enemies.length];
            for (int i = 0; i < enemies.length; i++) {
                inf[i] = rc.senseRobotInfo(enemies[i]);
            }
            RobotInfo closest = Const.getClosest(rc.getLocation(), inf);
            if (rc.canAttackSquare(closest.location)) rc.attackSquare(closest.location);
        }
    }

    
    
}
