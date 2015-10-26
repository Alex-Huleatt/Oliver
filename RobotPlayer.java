package team016;

import team016.Units.Soldier;
import team016.Units.HQ;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team016.Units.Supplier;

/**
 * The example funcs player is a player meant to demonstrate basic usage of the
 * most common commands. Robots will move around randomly, occasionally mining
 * and writing useless messages. The HQ will spawn soldiers continuously.
 */
public class RobotPlayer {

    public static void run(RobotController rc) {
        while (true) {
            try {
                switch (rc.getType()) {
                    case HQ:
                        (new HQ(rc)).run();
                        break;
                    case SOLDIER:
                        (new Soldier(rc)).run();
                        break;
                    case SUPPLIER:
                        (new Supplier(rc)).run();
                        break;
                    default:
                        System.out.println("Unknown rc type: " + rc.getType());
                        rc.yield();
                }
                // End turn
            } catch (Exception e) {
                rc.setIndicatorString(2, "ERROR!!");
                e.printStackTrace();
            }
        }
    }
}
