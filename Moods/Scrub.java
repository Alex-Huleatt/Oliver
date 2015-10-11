/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Oliver.Moods;

import Oliver.Soldier;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;

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
        moveTowards(enemyHQ);
    }

    
    
}
