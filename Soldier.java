package Oliver;

import battlecode.common.*;
import java.util.Random;
import Oliver.Moods.Mood;
import Oliver.Moods.Noob;

/* Should probably comment or something */

public class Soldier {
    RobotController rc;
    int goalSlope;
    MapLocation enemyHQ;
    Random rand = new Random();
    Mood emotion;
    
    public Soldier(RobotController bot) {
        rc = bot;
        emotion = new Noob(this);
    }

    public void run() throws Exception {
        enemyHQ = rc.senseEnemyHQLocation();
        while (true) {
            emotion.act();
            Mood trans = emotion.transition();
            if (trans != null) {
                this.emotion = trans;
            }
            rc.yield();
        }
    }
    
    public RobotController getRC() {
        return rc;
    }
    
    
    
}


