package Oliver;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/* Should probably comment or something */

public class Soldier {
    RobotController rc;
    
    public Soldier(RobotController bot) {
        rc = bot;
    }

    public void run() throws Exception {
        if (rc.isActive()) {
            if (Math.random() < 0.005) {
                // Lay a mine
                if (rc.senseMine(rc.getLocation()) == null)
                    rc.layMine();
            } else {
                // Choose a random direction, and move that way if possible
                Direction dir = Direction.values()[(int) (Math.random() * 8)];
                if (rc.canMove(dir)) {
                    rc.move(dir);
                    rc.setIndicatorString(0, "Last direction moved: " + dir.toString());
                }
            }
        }

        if (Math.random() < 0.01 && rc.getTeamPower() > 5) {
            // Write the number 5 to a position on the message board corresponding to the robot's ID
            rc.broadcast(rc.getRobot().getID() % GameConstants.BROADCAST_MAX_CHANNELS, 5);
        }
        rc.yield();
    }
    
    public void nextMove() {
        // TODO: bug bug buggin
    }
}


