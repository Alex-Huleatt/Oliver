package team016.Units;

import battlecode.common.*;
import java.util.Random;
import team016.Moods.DefaultMood;
import team016.Moods.Mood;
import team016.Moods.Zerg.Rushing;

/* Should probably comment or something */

public class Soldier implements Unit {
    RobotController rc;
    Mood emotion;
    public Soldier(RobotController bot) {
        rc = bot;
        emotion = new DefaultMood(this);
    }
    public void run() throws Exception {
        Mood trans;
        while (true) {
            if (emotion==null) continue;
            this.emotion=((trans=emotion.swing())==null)?emotion:trans;
            rc.setIndicatorString(0, emotion.toString());
            emotion.act(); //Perform
            emotion.report(); //Generate and send data to StratController.
            rc.yield();
        }
    }
    public RobotController getRC() {return rc;}
}