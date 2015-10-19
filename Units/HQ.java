package Oliver.Units;

import Oliver.Moods.HQ.Happy;
import Oliver.Moods.Mood;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.Upgrade;

/* Should probably comment or something */
public class HQ implements Unit{

    RobotController rc;
    Team team;
    MapLocation me;
    Mood emotion;
    public HQ(RobotController bot) {
        rc = bot;
        this.team = rc.getTeam();
        me = rc.getLocation();
        emotion = new Happy(this);
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
    
    public RobotController getRC(){return rc;}
}
