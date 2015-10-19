package Oliver.Units;

import battlecode.common.*;
import java.util.Random;
import Oliver.Moods.*;
import Oliver.Moods.Zerg.*;

/* Should probably comment or something */

public class Soldier implements Unit {
    RobotController rc;
    Mood emotion;
    public Soldier(RobotController bot) {
        rc = bot;
        emotion = new Rushing(this);
    }

    public void run() throws Exception {
        Mood trans;
        while (true) {
            this.emotion=((trans=emotion.swing())==null)?emotion:trans;
            rc.setIndicatorString(0, emotion.toString());
            emotion.act();
            rc.yield();
        }
    }
    public RobotController getRC() {return rc;}
}