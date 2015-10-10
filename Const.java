package Oliver;

import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

/* Should probably comment or something */

public class Const {
    
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