package Oliver;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/* Should probably comment or something */

public class HQ {
    RobotController rc;
    
    public HQ(RobotController bot) {
        rc = bot;
    }
    public void run() throws Exception {
        if (rc.isActive()) {
            // Spawn a soldier
            Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
            if (rc.canMove(dir))
                rc.spawn(dir);
        }
        rc.yield();
    }
}