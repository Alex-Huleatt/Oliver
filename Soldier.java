package Oliver;

import battlecode.common.*;
import java.util.Random;
import Oliver.Moods.Mood;
import Oliver.Moods.Scrub;

/* Should probably comment or something */

public class Soldier {
    RobotController rc;
    Mood emotion;
    public Soldier(RobotController bot) {
        rc = bot;
        emotion = new Scrub(this);
    }

    public void run() throws Exception {
        Mood trans;
        while (true) {
            rc.setIndicatorString(0, emotion.toString());
            emotion.act();
            this.emotion=((trans=emotion.transition())==null)?emotion:trans;
            rc.yield();
        }
    }
    public RobotController getRC() {return rc;}
}