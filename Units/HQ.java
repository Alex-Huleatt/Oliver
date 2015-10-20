package team016.Units;

import team016.Moods.HQ.Happy;
import team016.Moods.Mood;
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
import team016.Comm.RadioController;
import team016.Strat.StratController;
import team016.Strat.StratType;

/* Should probably comment or something */
public class HQ implements Unit{

    RobotController rc;
    Team team;
    MapLocation me;
    Mood emotion;
    StratController sc;
    public HQ(RobotController bot) {
        rc = bot;
        this.team = rc.getTeam();
        me = rc.getLocation();
        emotion = new Happy(this);
        sc = new StratController(rc);
    }

    public void run() throws Exception {
        Mood trans;
        while (true) {
            sc.majorStrat();
            sc.minorStrat();
            this.emotion=((trans=emotion.swing())==null)?emotion:trans;
            emotion.act();
            rc.yield();
        }
        
    }
    
    public RobotController getRC(){return rc;}
}
