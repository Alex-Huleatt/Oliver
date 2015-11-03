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
import java.util.HashSet;
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
    MapLocation sup;
    MapLocation old_sup;
    MapLocation rally;

    public interface Datum {

        public boolean on() throws Exception;
    }

    //this line is going to be long.
    public Datum closeHQs, midGame, lateGame, spookedHQ, earlyGame, enemyNuke,
            shouldDefuse, shouldReactor, shouldVision, needSupply, needNewSupplyLoc, bigMap;

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

                return !rc.hasUpgrade(Upgrade.FUSION) && bigMap.on() && rc.getTeamPower() < 100;
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
                        > GameConstants.CAPTURE_POWER_COST;
            }

        };
        needNewSupplyLoc = new Datum() {

            public boolean on() throws Exception {
                return 1 == radC.read("SUPPLY_REQUEST_NEW_POSN", Clock.getRoundNum() - 1);
            }

        };
        bigMap = new Datum() {

            public boolean on() throws Exception {
                double mapSize = rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation());
                return mapSize > 400;
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
        rally = getRally(500);
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

            if (shouldReactor.on() && !spookedHQ.on()) {
                toStrat = StratType.RUSH_REACTOR;
            }
        }
        hqStrat = toStrat;

    }

    public void minorStrat() throws Exception {

        if (needNewSupplyLoc.on() || sup == null) {
            sup = findGoodSupplySquare();
            if (sup == null) {
                resetSupplyLocs();
                sup = findGoodSupplySquare();
            }
            radC.write("SUPPLY_SQUARE_POSN",
                    Const.locToInt(sup),
                    Clock.getRoundNum());
        }
        if (needSupply.on()) {
            if (sup != null) {
                radC.write("SUPPLY_REQUEST_OFFSET",
                        1,
                        Clock.getRoundNum());
                radC.write("SUPPLY_SQUARE_POSN",
                        Const.locToInt(sup),
                        Clock.getRoundNum());
            }
        }
        //do medbay logic here
        if (radC.read("MEDBAY_COUNT", Clock.getRoundNum() - 1) < 1 && bigMap.on()) {
            radC.write("MEDBAY_REQUEST", 1, Clock.getRoundNum());
        }
        if (rc.canSenseSquare(rally)) {
            GameObject o = rc.senseObjectAtLocation(rally);
            if (o != null && o instanceof Robot) {
                Robot r = (Robot) o;
                if (rc.senseRobotInfo(r).team != rc.getTeam()) {
                    curTarget = rally;
                }
            }
        }
        System.out.println("1.");
        if (curTarget != null && rc.canSenseSquare(curTarget)) {
            System.out.println("2.");
            GameObject o = rc.senseObjectAtLocation(curTarget);
            if (o != null) {
                if (o instanceof Robot) {
                    RobotInfo ri = rc.senseRobotInfo((Robot) o);
                    if (ri.team == rc.getTeam()) {
                        if (validTarget) {
                            targetCD = 17;
                            validTarget = false;
                        } else {
                            curTarget = enemyEncamps.pop();
                        }
                    } else {
                        validTarget = true;
                    }
                }
            } else {
                if (validTarget) {
                    targetCD = 17;
                    validTarget = false;
                }
            }
        }
        if (targetCD > 0) {
            --targetCD;
            radC.write("REGROUP_FLAG", 1, Clock.getRoundNum());
        } else {
            if (lateGame.on() || enemyNuke.on()) {
                curTarget = rc.senseEnemyHQLocation();
            } else {
                if (curTarget == null && targetCD == 0) {
                    curTarget = enemyEncamps.pop();
                } else if (curTarget == null) {
                    curTarget = enemyEncamps.pop();
                    if (curTarget == null) {
                        findEnemyEncamps();
                        curTarget = enemyEncamps.pop();
                    }
                }
            }
        }
        radC.write(
                "TARGET_OFFSET", Const.locToInt(curTarget), Clock.getRoundNum());
        radC.write(
                "RALLY_OFFSET", Const.locToInt(rally), Clock.getRoundNum());
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
    private MapLocation findGoodSupplySquare() throws Exception {
        for (int i = encamps_index; i < encamps_index + 20 && i < allEncamps.length; i++) {
            float h = (float) supplyHeuristic(allEncamps[i]);
            if (h != 0) {
                mq.add(allEncamps[i], h);
            }
        }
        //t.stop();
        encamps_index += 20;
        MapLocation m = mq.pop();

        if (m == null) {
            return null;
        }
        if (m.equals(rally)) {
            m = mq.pop();
        }
        return m;
    }

    private void resetSupplyLocs() throws Exception {
        mq = new MapQueue();
        MapLocation[] neutrals = new MapLocation[0];
        range *= 2;
        while ((neutrals = rc.senseEncampmentSquares(me, range, Team.NEUTRAL)).length < 1) {
            range *= 2;
        }
        allEncamps = neutrals;
        encamps_index = 0;
    }

    private double supplyHeuristic(MapLocation m) {
        return 1.0 * m.distanceSquaredTo(me);// - 0.0 * Math.pow(Const.disToLine(me, rc.senseEnemyHQLocation(), m), 2);
    }

    private MapLocation getRally(int range) throws Exception {
        //get one closest to center
        int k = 4;
        MapLocation enHQ = rc.senseEnemyHQLocation();
        int x = (k * me.x + enHQ.x) / (k + 1);
        int y = (k * me.y + enHQ.y) / (k + 1);
        MapLocation midpoint = new MapLocation(x, y);
        MapLocation minPoint = null;
        int min_dis = Integer.MAX_VALUE;
        MapLocation[] neuts = rc.senseEncampmentSquares(midpoint, range, Team.NEUTRAL);
        int count = 0;
        for (MapLocation m : neuts) {
            int dis = m.distanceSquaredTo(midpoint);
            if (dis < min_dis) {
                min_dis = dis;
                minPoint = m;
            }
            count++;
            if (count > 30) {
                break;
            }
        }
        if (minPoint == null) {
            return getRally(range * 2);
        }
        return minPoint;
    }

    private void findEnemyEncamps() throws Exception {
        MapLocation[] neutrals = rc.senseEncampmentSquares(rc.senseEnemyHQLocation(), 800, Team.NEUTRAL);
        for (MapLocation m : neutrals) {
            enemyEncamps.add(m, me.distanceSquaredTo(m));
        }

        //most likely encampments are near enemy base.
    }

}
