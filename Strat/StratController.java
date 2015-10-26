/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Strat;

import battlecode.common.Clock;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.Upgrade;
import team016.Comm.RadioController;
import team016.Const;
import team016.MapQueue;
import team016.Timer;

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
    public StratType hqStrat;

    public MapLocation[] allEncamps;
    int range = 1;
    int encamps_index;
    MapQueue mq;

    MapLocation me;

    public interface Datum {

        public boolean on() throws Exception;
    }

    //this line is going to be long.
    public Datum closeHQs, midGame, lateGame, spookedHQ, earlyGame, enemyNuke,
            shouldDefuse, shouldReactor, shouldVision, needSupply;

    public StratController(RobotController rct) throws Exception {
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
        shouldDefuse = new Datum() {
            public boolean on() throws Exception {
                if (!rc.hasUpgrade(Upgrade.DEFUSION)) {
                    double numMines = rc.senseNonAlliedMineLocations(rc.getLocation(), 100000).length;
                    double mapSize = rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation());
                    return numMines * 3 / (mapSize - 2000) > 0.6;
                }
                return false;
            }
        };
        shouldReactor = new Datum() {

            public boolean on() throws Exception {
                return !rc.hasUpgrade(Upgrade.FUSION);
            }

        };
        needSupply = new Datum() {
            public boolean on() throws Exception {
                if (Clock.getRoundNum() == 0) {
                    return true;
                }
                return radC.read(RadioController.REPORT_BLOCK, 
                        RadioController.SUPPLY_COUNT_OFFSET, 
                        Clock.getRoundNum() - 1) < 1 && 
                        rc.getTeamPower() > 
                        GameConstants.CAPTURE_POWER_COST*(2+rc.senseAlliedEncampmentSquares().length);
                
            }

        };
        radC = new RadioController(rc);
        
        me = rc.senseHQLocation();
        allEncamps = rc.senseEncampmentSquares(me, range, Team.NEUTRAL);
        encamps_index = 0;
        mq = new MapQueue();
        
    }

    public void majorStrat() throws Exception {
        StratType toStrat;
        lbl:
        {
            if (spookedHQ.on()) {
                toStrat = StratType.SOS;
                break lbl;
            }
            if (earlyGame.on() && closeHQs.on()) {
                toStrat = StratType.ZERG;
                break lbl;
            }

//            if (lateGame.on() || enemyNuke.on()) {
//                toStrat = StratType.ALL_IN;
//                break lbl;
//            }
            toStrat = StratType.ZERG;
        }
        if (curStrat != toStrat) {
            System.out.println(toStrat);
        }
        curStrat = toStrat;
        sendStrat(toStrat);

    }

    private void sendStrat(StratType st) throws Exception {
        radC.write(RadioController.HQ_BLOCK, RadioController.STRAT_OFFSET, st.ordinal(), Clock.getRoundNum());
    }

    public void setHQStrat() throws Exception {
        StratType toStrat = StratType.NO_STRAT;
        //lbl:
        {
            if (shouldDefuse.on() && !spookedHQ.on() && earlyGame.on()) {
                toStrat = StratType.RUSH_DEFUSION;
            }

//            if (shouldReactor.on() && !spookedHQ.on() && earlyGame.on()) {
//                toStrat = StratType.RUSH_REACTOR;
//            }
        }
        hqStrat = toStrat;

    }

    /**
     * TODO
     */
    public void minorStrat() throws Exception {
        if (needSupply.on()) {
            int sup = findGoodSupplySquare();
            if (sup != -1) {
                //System.out.println(RadioController.REPORT_BLOCK + " " + RadioController.SUPPLY_REQUEST_OFFSET + " " + Clock.getRoundNum());
                radC.write(RadioController.REPORT_BLOCK,
                        RadioController.SUPPLY_REQUEST_OFFSET,
                        1,
                        Clock.getRoundNum());
                radC.write(RadioController.REPORT_BLOCK,
                        RadioController.SUPPLY_SQUARE_POSN,
                        sup,
                        Clock.getRoundNum());

            } else {
                while (encamps_index>=allEncamps.length) {
                    range*=2;
                    allEncamps = rc.senseEncampmentSquares(me, range, Team.NEUTRAL);
                    encamps_index=0;
                }
            }
        }

        //System.out.println(Const.intToLoc(sqr));
    }

    /**
     * TODO
     *
     * @return
     */
    public int[] unitReports() {

        return null;
    }

    /**
     * TODO*
     */
    public void encamp() throws Exception {
        //rc.senseAllEncampmentSquares();
        //MapLocation[] encamps = rc.senseAllEncampmentSquares();

    }

    /**
     * TODO*
     */
    private int findGoodSupplySquare() throws Exception {
        Timer t = new Timer();
        t.start();
        for (int i = encamps_index; i < encamps_index + 20 && i < allEncamps.length; i++) {
            mq.add(allEncamps[i], (float) supplyHeuristic(allEncamps[i]));
        }
        t.stop();
        encamps_index += 10;
        return Const.locToInt(mq.pop());
    }

    private double supplyHeuristic(MapLocation m) {
        return .5 * m.distanceSquaredTo(me) * 1.0/Const.disToLine(me, rc.senseEnemyHQLocation(), m);
    }

    /**
     * TODO*
     */
    private static boolean goodArtillerySquare(RadioController rc, MapLocation loc) {
        return false;
    }

}
