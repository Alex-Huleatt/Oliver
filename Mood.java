/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Oliver;

import battlecode.common.RobotController;

/**
 *
 * @author alexhuleatt
 */
public interface Mood {
    
    public void act(RobotController rc);
    public Mood transition(RobotController rc);
    
}
