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
        while (true) {
            emotion.act();
            Mood trans = emotion.transition();
            this.emotion = (trans==null)?emotion:trans;
            rc.yield();
        }
    }
    
    public RobotController getRC() {return rc;}
    
    
    
}


