package team016.Units;

import battlecode.common.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import team016.Moods.DefaultMood;
import team016.Moods.Mood;
import team016.Moods.Zerg.Rushing;
import team016.Timer;

/* Should probably comment or something */
public class Soldier extends Unit {

    Mood emotion;

    public Soldier(RobotController bot) {
        super(bot);
        rc = bot;
        emotion = new DefaultMood(this);
    }

    @Override
    public void run() throws Exception {
        Mood trans;
        // Timer t = new Timer();
        while (true) {
            if (rc.getTeamPower() > 20) {
                emotion = ((trans = emotion.swing()) == null) ? emotion : trans;
                rc.setIndicatorString(0, emotion.toString());
                emotion.updateVars();
                //t.start();
                emotion.act(); //Perform
                //t.stop();
                //System.out.println(t.elaps + " " + emotion);
                //t.restart();
                emotion.report(); //Generate and send data to StratController.
                
            }
            rc.yield();
        }
    }

    public RobotController getRC() {
        return rc;
    }
}
