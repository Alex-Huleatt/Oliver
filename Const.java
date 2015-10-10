package Oliver;

import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

/* Should probably comment or something */

public class Const {
    static int directionToInt(Direction d) {
        switch (d) {
            case NORTH:
                return 0;
            case NORTH_EAST:
                return 1;
            case EAST:
                return 2;
            case SOUTH_EAST:
                return 3;
            case SOUTH:
                return 4;
            case SOUTH_WEST:
                return 5;
            case WEST:
                return 6;
            case NORTH_WEST:
                return 7;
            default:
                return -1;
        }
    }

    static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST,
            Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH,
            Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    
    public static RobotInfo getClosest(MapLocation loc, RobotInfo[] obs) {
        int min = loc.distanceSquaredTo(obs[0].location);
        int mindex = 0;
        for (int i = 1; i < obs.length; i++) {
            int dis = loc.distanceSquaredTo(obs[i].location);
            if (dis < min) {
                min = dis;
                mindex = i;
            }
        }
        return obs[mindex];
    }
    
    public static Robot[] robotFilter(GameObject[] obs) {
        Robot[] r = new Robot[obs.length];
        int r_count = 0;
        for (GameObject ob : obs) {
            if (ob instanceof Robot) r[r_count++] = (Robot) ob;
        }
        Robot[] ret = new Robot[r_count];
        System.arraycopy(r, 0, ret, 0, ret.length);
        return ret;
    }
}