/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Comm;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import team016.Strat.MissionType;
import team016.Strat.StratType;

/**
 *
 * @author alexhuleatt
 */
public class RadioController {

    RobotController rc;
    private static final int MASK = 0x93000;
    
    public static final int MAX_MISSIONS = 5;
    public static final int REPORT_ALIVE_OFFSET = 2;

    //block zero shenanigans
    public static final int HQ_BLOCK = 0;
    public static final int X_POSN_OFFSET = 3;
    public static final int Y_POSN_OFFSET = 4;
    public static final int STRAT_OFFSET = 1;
    
    //block one shenanigans
    public static final int REPORT_BLOCK = 1;

    public RadioController(RobotController rc) {
        this.rc = rc;
    }

    long xorshiftstar(int x) {
        x += 1;
        x ^= x >> 12;
        x ^= x << 25;
        x ^= x >> 27;
        return x * 2685821657736338717l;
    }

    private int myHash(int block, int seed) {
        long rand = Math.abs(xorshiftstar(seed));
        return (int) ((rand * (1 + block)) % GameConstants.BROADCAST_MAX_CHANNELS);
    }

    private int getBaseChannel_write(int block) {
        int chan = myHash(block, Clock.getRoundNum());
        return chan;

    }

    private int getBaseChannel_read(int block) {

        return myHash(block, Clock.getRoundNum());
    }

    private static int signMessage(int message) {
        return message | MASK;
    }

    private static boolean isSigned(int message) {
        //System.out.println(message + " " + (message&MASK) + " " + MASK);
        return (message & MASK) == MASK;
    }

    private static int unsign(int message) {
        return message ^ MASK;
    }

    private void increment(int chan) throws Exception {
        write(chan, read(chan) + 1);
    }

    public int read(int block_num, int offset, boolean turnOff) throws Exception {

        int chan = getChannel(block_num, offset, turnOff);
        int raw = rc.readBroadcast(chan);

        if (!isSigned(raw)) {
            return -1;
        } else {
            return unsign(raw);
        }

    }

    public int read(int chan) throws Exception {
        int raw = rc.readBroadcast(chan);
        if (!isSigned(raw)) {
            return -1;
        } else {
            return unsign(raw);
        }
    }

    public int getChannel(int block_num, int offset, boolean writing) {
        int base = (writing)
                ? getBaseChannel_write(block_num)
                : getBaseChannel_read(block_num);
        return (base + offset) % GameConstants.BROADCAST_MAX_CHANNELS;
    }

    public void write(int block_num, int offset, int message) throws Exception {
        int signed = signMessage(message);
        int chan = getChannel(block_num, offset, true);
        rc.broadcast(chan, signed);
    }

    public void write(int chan, int message) throws Exception {
        int signed = signMessage(message);
        rc.broadcast(chan, signed);
    }

    public StratType curStrat() throws Exception {
        int strat_index = read(HQ_BLOCK, RadioController.STRAT_OFFSET, true);
        if (strat_index <= 0) {
            return null;
        }
        return StratType.values()[strat_index];
    }

    public void sendReport() throws Exception {
        increment(getChannel(REPORT_BLOCK, REPORT_ALIVE_OFFSET, true));
    }

    public void createMission(MissionType m, MapLocation loc, int block_num) {

    }

}
