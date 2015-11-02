/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Strat;

import battlecode.common.Clock;
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
import team016.Const;
import team016.Consts;
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

    MapQueue enemyEncamps;
    MapLocation curTarget;
    int targetCD;
    boolean validTarget;

    MapLocation me;

    public interface Datum {

        public boolean on() throws Exception;
    }

    //this line is going to be long.
    public Datum closeHQs, midGame, lateGame, spookedHQ, earlyGame, enemyNuke,
            shouldDefuse, shouldReactor, shouldVision, needSupply, needNewSupplyLoc;

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
                return radC.read("SUPPLY_COUNT_OFFSET",
                        Clock.getRoundNum() - 1) < 1
                        && rc.getTeamPower()
                        > GameConstants.CAPTURE_POWER_COST * (1 + rc.senseAlliedEncampmentSquares().length);
            }

        };
        needNewSupplyLoc = new Datum() {

            public boolean on() throws Exception {
                return 1 == radC.read("SUPPLY_REQUEST_NEW_POSN", Clock.getRoundNum() - 1);
            }

        };
        radC = new RadioController(rc);

        me = rc.senseHQLocation();
        allEncamps = rc.senseEncampmentSquares(me, range, Team.NEUTRAL);
        encamps_index = 0;
        mq = new MapQueue();
        enemyEncamps = new MapQueue();
        findEnemyEncamps();
        targetCD = 0;
        validTarget = false;

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
        radC.write("GLOBAL_STRAT", st.ordinal(), Clock.getRoundNum());
    }

    public void setHQStrat() throws Exception {
        StratType toStrat = StratType.NO_STRAT;
        //lbl:
        {
            if (shouldDefuse.on() && !spookedHQ.on() && earlyGame.on()) {
                toStrat = StratType.RUSH_DEFUSION;
            }

//            if (shouldReactor.on() && !spookedHQ.on()) {
//                toStrat = StratType.RUSH_REACTOR;
//            }
        }
        hqStrat = toStrat;

    }

    /**
     * TODO
     *
     * @throws java.lang.Exception
     */
    public void minorStrat() throws Exception {
        int sup;
        if (needNewSupplyLoc.on()) {
            sup = findGoodSupplySquare();
            radC.write("SUPPLY_SQUARE_POSN",
                    sup,
                    Clock.getRoundNum());
        }
        if (needSupply.on()) {
            sup = findGoodSupplySquare();
            if (sup != -1) {
                //System.out.println(RadioController.REPORT_BLOCK + " " + RadioController.SUPPLY_REQUEST_OFFSET + " " + Clock.getRoundNum());
                radC.write("SUPPLY_REQUEST_OFFSET",
                        1,
                        Clock.getRoundNum());
//                System.out.println("Tried to write request to: " + 
//                        radC.getChannel(Consts.c("SUPPLY_REQUEST_OFFSET"), Clock.getRoundNum()));

                radC.write("SUPPLY_SQUARE_POSN",
                        sup,
                        Clock.getRoundNum());

            } else {
                while (encamps_index >= allEncamps.length) {
                    range *= 2;
                    allEncamps = rc.senseEncampmentSquares(me, range, Team.NEUTRAL);
                    encamps_index = 0;
                }

            }
        }
        if (curTarget != null && rc.canSenseSquare(curTarget)) {
            GameObject o = rc.senseObjectAtLocation(curTarget);
            if (o != null) {
                if (o instanceof Robot) {
                    RobotInfo ri = rc.senseRobotInfo((Robot) o);
                    if (ri.team == rc.getTeam()) {
                        curTarget = enemyEncamps.pop();
                        if (validTarget) {
                            targetCD = Math.max(20 - me.distanceSquaredTo(rc.senseEnemyHQLocation()),7);
                            validTarget = false;
                        }
                    } else {
                        validTarget = true;
                    }
                }
            } else {
                curTarget = enemyEncamps.pop();
                if (validTarget) {
                    targetCD = Math.max(20 - me.distanceSquaredTo(rc.senseEnemyHQLocation()),7);
                    validTarget = false;
                }
            }
        }
        if (lateGame.on()) {
            radC.write("TARGET_OFFSET", Const.locToInt(rc.senseEnemyHQLocation()), Clock.getRoundNum());
        } else {
            if (targetCD == 0) {
                if (curTarget == null) {
                    curTarget = enemyEncamps.pop();
                    if (curTarget == null) {
                        findEnemyEncamps();
                        curTarget = enemyEncamps.pop();
                        if (curTarget == null) {
                            radC.write("TARGET_OFFSET", Const.locToInt(rc.senseEnemyHQLocation()), Clock.getRoundNum());
                            return;
                        }
                    }

                    radC.write("TARGET_OFFSET", Const.locToInt(curTarget), Clock.getRoundNum());
                } else {
                    radC.write("TARGET_OFFSET", Const.locToInt(curTarget), Clock.getRoundNum());
                }
            } else {
                --targetCD;
                radC.write("TARGET_OFFSET", Const.locToInt(me), Clock.getRoundNum());
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
     * TODO
     *
     * @throws java.lang.Exception
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
            float h = (float) supplyHeuristic(allEncamps[i]);
            if (h != -1) {
                mq.add(allEncamps[i], h);
            }
        }
        t.stop();
        encamps_index += 10;
        MapLocation m = mq.pop();
        return Const.locToInt(m);
    }

    private double supplyHeuristic(MapLocation m) {
        if (m.distanceSquaredTo(rc.getLocation()) < 2) {
            return -1;
        }
        return -.8 * rc.senseEnemyHQLocation().distanceSquaredTo(m) + 1.0 * m.distanceSquaredTo(me) - 0.9 * Math.pow(Const.disToLine(me, rc.senseEnemyHQLocation(), m), 2);
    }

    /**
     * TODO*
     */
    private static boolean goodArtillerySquare(RadioController rc, MapLocation loc) {
        return false;
    }

    private void findEnemyEncamps() throws Exception {
        MapLocation[] neutrals = rc.senseEncampmentSquares(rc.senseEnemyHQLocation(), 500, Team.NEUTRAL);
        for (MapLocation m : neutrals) {
            enemyEncamps.add(m, (float) Const.disToLine(me, rc.senseEnemyHQLocation(), m) + -1 * rc.senseEnemyHQLocation().distanceSquaredTo(m));
        }

        //most likely encampments are near enemy base.
    }

}
