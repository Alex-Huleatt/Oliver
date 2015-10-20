/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Strat;

import battlecode.common.Clock;
import battlecode.common.GameConstants;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import team016.Comm.RadioController;

/**
 * This should be the core of everything. HQ will have this instance, this
 * should broadcast major changes in macro strats as well as important info like
 * which types of encampments we want.
 *
 * @author alexhuleatt
 */
public class StratController {

    public final RobotController rc; //hq's rc
    public final RadioController radC;
    public StratType curStrat;

    public interface Datum {
        public boolean on() throws Exception;
    }
    
    //this line is going to be long.
    public Datum closeHQs, midGame, lateGame, spookedHQ, earlyGame, enemyNuke;

    public StratController(RobotController rct) {
        this.rc = rct;
        this.curStrat = StratType.NO_STRAT;
        closeHQs = new Datum() {
            public boolean on() {
                return (rc.senseHQLocation().distanceSquaredTo(rc.senseEnemyHQLocation())) < 1000;
            }
        };
        lateGame = new Datum() {
            public boolean on() {
                return Clock.getRoundNum() >= 1000;
            }
        };
        midGame = new Datum() {
            public boolean on() {
                return Clock.getRoundNum() > 500 && Clock.getRoundNum() < 1000;
            }
        };
        earlyGame = new Datum() {
            public boolean on() {
                return Clock.getRoundNum() < 500;
            }
        };
        spookedHQ = new Datum() {
            public boolean on() {
                Robot[] rarr = rc.senseNearbyGameObjects(
                        Robot.class,
                        rc.senseHQLocation(),
                        RobotType.HQ.sensorRadiusSquared,
                        rc.getTeam().opponent());
                return rarr.length > 0;
            }
        };
        enemyNuke = new Datum() {
            public boolean on() throws Exception {
                return rc.senseEnemyNukeHalfDone();
            } 
        };
        
        radC = new RadioController(rc);

    }

    public void majorStrat() throws Exception {
        if (spookedHQ.on()) {
            sendStrat(StratType.SOS);
            System.out.println("SOS!");
            return;
        }
        if (earlyGame.on() && closeHQs.on()) {
            sendStrat(StratType.ZERG);
            System.out.println("Zerg!");
            return;
        }
        
        if (lateGame.on() || enemyNuke.on()) {
            sendStrat(StratType.ALL_IN);
            System.out.println("Desperate!");
            return;
        }
        System.out.println("Default");
        sendStrat(StratType.ZERG);
    }

    private void sendStrat(StratType st) throws Exception {
        radC.write(RadioController.HQ_BLOCK, RadioController.STRAT_OFFSET, st.ordinal());
    }

    public void minorStrat() {

    }

    /**
     * TODO
     * @return 
     */
    public int[] unitReports() {
        
        return null;
    }
}
