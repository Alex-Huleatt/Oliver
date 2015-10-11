/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Oliver.Moods;

import Oliver.Const;
import Oliver.Soldier;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

/**
 *
 * @author alexhuleatt
 */
public class Angry extends Mood {

    public Angry(Soldier s) {
        super(s);
    }
    
    @Override
    public void act() throws Exception {
        RobotInfo[] enemies = getEnemies(rc);
        RobotInfo closest = Const.getClosest(rc.getLocation(), enemies);
        rc.attackSquare(closest.location);
    }
    
    
}
