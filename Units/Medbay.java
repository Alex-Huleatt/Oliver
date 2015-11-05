/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Units;

import battlecode.common.RobotController;
import team016.Moods.Jammer.Chatty;
import team016.Moods.Mood;

/**
 *
 * @author alexhuleatt
 */
public class Medbay extends Unit  {

    Mood emotion;

    public Medbay(RobotController rc) {
        super(rc);
        emotion = new Chatty(this);
    }
    
    @Override
    public void run() throws Exception {
        Mood trans;
        while (true) {
            emotion = ((trans = emotion.swing()) == null) ? emotion : trans;
            rc.setIndicatorString(0, emotion.toString());
            emotion.updateVars();
            radC.unitReport("MEDBAY_COUNT");

            emotion.act();

            rc.yield();
        }
    }
    
}
