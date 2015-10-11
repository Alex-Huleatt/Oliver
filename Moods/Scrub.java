/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Oliver.Moods;

import Oliver.Const;
import Oliver.Soldier;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

/**
 *
 * @author alexhuleatt
 */
public class Scrub extends Mood {
    
    public Scrub(Soldier s) {
        super(s);
    }

    @Override
    public void act() throws Exception {
        Robot[] enemies = getEnemies(rc, RobotType.SOLDIER.attackRadiusMaxSquared);
        if (enemies.length > 0) {
            RobotInfo[] inf = new RobotInfo[enemies.length];
            for (int i = 0; i < enemies.length; i++) {
                inf[i] = rc.senseRobotInfo(enemies[i]);
            }
            RobotInfo closest = Const.getClosest(rc.getLocation(), inf);
            if (rc.canAttackSquare(closest.location)) rc.attackSquare(closest.location);
        }

        moveTowards(enemyHQ);
    }

    @Override
    public String toString() {
        return ":|";
    }
    
}
