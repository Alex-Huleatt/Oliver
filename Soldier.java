package Oliver;

import battlecode.common.*;
import java.util.Random;

/* Should probably comment or something */

public class Soldier {
    RobotController rc;
    int goalSlope;
    MapLocation enemyHQ;
    Random rand = new Random();
    
    public Soldier(RobotController bot) {
        rc = bot;
    }

    public void run() throws Exception {
        enemyHQ = rc.senseEnemyHQLocation();
        while (true) {
            if (rc.isActive()) {
                if (Math.random() < 0.005) {
                    // Lay a mine
                    if (rc.senseMine(rc.getLocation()) == null)
                        rc.layMine();
                } else {
                    moveTowards(enemyHQ);
                }
            }

            if (Math.random() < 0.01 && rc.getTeamPower() > 5) {
                // Write the number 5 to a position on the message board corresponding to the robot's ID
                rc.broadcast(rc.getRobot().getID() % GameConstants.BROADCAST_MAX_CHANNELS, 5);
            }
            rc.yield();
        }
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
    
    public boolean isObstacle(MapLocation loc) {
        TerrainTile tt = rc.senseTerrainTile(loc);
        if (tt == TerrainTile.VOID) return true;
        if (rc.canSenseSquare(loc)) {
            
            if (ri != null && (rand.nextDouble() > .8 || ri.type == RobotType.HQ)) return true; //
        }
        return false;
    }
    
    public boolean isObstacle(Direction dir) {
        return isObstacle(rc.getLocation().add(dir));
    }
}


