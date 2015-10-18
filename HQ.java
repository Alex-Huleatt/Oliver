package Oliver;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.Upgrade;

/* Should probably comment or something */
public class HQ {

    RobotController rc;
    Team team;
    MapLocation me;
    public HQ(RobotController bot) {
        rc = bot;
        this.team = rc.getTeam();
        me = rc.getLocation();
    }

    public void run() throws Exception {
        if (me.distanceSquaredTo(rc.senseEnemyHQLocation()) > 300 && rc.isActive() && !rc.hasUpgrade(Upgrade.DEFUSION)) {
            rc.researchUpgrade(Upgrade.DEFUSION);
        }
        
        if (rc.isActive()) {
            // Spawn a soldier
            Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
            if (rc.canMove(dir)) {
                rc.spawn(dir);
            }
            
        }
        
        GameObject[] things = rc.senseNearbyGameObjects(Robot.class, RobotType.HQ.sensorRadiusSquared, team.opponent());
        if (things.length > 0) {
            Robot[] ri_arr = Const.robotFilter(things, team.opponent());
            if (ri_arr.length > 1) {
                //Broadcast SoS
                rc.broadcast(0, 1);
                rc.setIndicatorString(0, ":O");
            } else if (ri_arr.length == 0) {
                rc.setIndicatorString(0, "^.^");
                rc.broadcast(0, 0);
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
