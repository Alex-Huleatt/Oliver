package Oliver;

import battlecode.common.GameObject;
import battlecode.common.MapLocation;
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
}