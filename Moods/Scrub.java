/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Oliver.Moods;

import Oliver.Const;
import Oliver.Soldier;
import battlecode.common.Direction;
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
        moveTowards(enemyHQ);
    }

    @Override
    public String toString() {
        return ":|";
    }
    
}
