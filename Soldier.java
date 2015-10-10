package Oliver;

import Oliver.Moods.Mood;
import Oliver.Moods.Noob;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/* Should probably comment or something */

public class Soldier {
    RobotController rc;
    Mood emotion;
    
    public Soldier(RobotController bot) {
        rc = bot;
        emotion = new Noob(this);
    }

    public void run() throws Exception {
        
        rc.yield();
    }
    
    public void nextMove() {
        // TODO: bug bug buggin
    }
    
    public static GameObject[] getEnemies(RobotController rc) {
        return null;
    }
    
    public static MapLocation[] getBadMines(RobotController rc) {
        return null;
    }
    
    public RobotController getRC() {
        return rc;
    }
    
    
    
}


