package Oliver;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
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
            if (rc.canMove(dir)) {
                rc.spawn(dir);
            }
            
        }
        if (rc.isActive()) {
            simpleAttack();
        }
        rc.yield();

    }

    protected void simpleAttack() throws Exception {
        Robot[] enemies = Const.getEnemies(rc,
                RobotType.HQ.attackRadiusMaxSquared);
        if (enemies.length > 0) {
            RobotInfo[] inf = new RobotInfo[enemies.length];
            for (int i = 0; i < enemies.length; i++) {
                inf[i] = rc.senseRobotInfo(enemies[i]);
            }
            RobotInfo mah_closest = Const.getClosest(rc.getLocation(), inf);
            if (rc.canAttackSquare(mah_closest.location)) {
                rc.attackSquare(mah_closest.location);
            }
        }
    }
}
