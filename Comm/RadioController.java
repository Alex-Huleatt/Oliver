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

    public static final int MAX_MISSIONS = 5;
    public static final int REPORT_ALIVE_OFFSET = 2;

    //block zero shenanigans
    public static final int HQ_BLOCK = 0;
    public static final int STRAT_OFFSET = 1;
    public static final int HQ_STRAT_OFFSET = 2;

    //block one shenanigans
    public static final int REPORT_BLOCK = 1;

    //mission block
    public static final int MISSION_BLOCK = 2;
    public static final int REQUESTED_SOLDIERS_OFFSET = 0;

    
    
    public RadioController(RobotController rc) {
        this.rc = rc;
    }

    private static int getMask() {
        return Clock.getRoundNum() << 24;
    }
    
    long xorshiftstar(int x) {
        x ^= x >> 12;
        x ^= x << 25;
        x ^= x >> 27;
        return x * 2685821657736338717l;
    }

    private int myHash(int block, int seed) {
        long rand = Math.abs(xorshiftstar(seed * ((rc.getTeam() == Team.A) ? 17 : 37)));
        return (int) ((rand * (1 + block)) % GameConstants.BROADCAST_MAX_CHANNELS);
    }

    private int getBaseChannel(int block) {
        return myHash(block, Clock.getRoundNum());
    }

    private static int signMessage(int message) {
        return message | getMask();
    }

    private static boolean isSigned(int message) {
        //System.out.println(message + " " + (message&MASK) + " " + MASK);
        return (message & getMask()) == getMask();
    }

    private static int unsign(int message) {
        return message ^ getMask();
    }
    
    public int getChannel(int block_num, int offset) {
        int base = getBaseChannel(block_num);
        return (base + offset) % GameConstants.BROADCAST_MAX_CHANNELS;
    }

    public void write(int block_num, int offset, int message) throws Exception {
        int signed = signMessage(message);
        int chan = getChannel(block_num, offset);
        rc.broadcast(chan, signed);
        System.out.println("Write:" + chan + ", " + signed);
    }

    /**
     * HERE BE PUBLIC FUNCTIONS
     */
    /**
     *
     * @return @throws Exception
     */
    public StratType curStrat() throws Exception {
        int strat_index = read(HQ_BLOCK, RadioController.STRAT_OFFSET);
        if (strat_index <= 0) {
            return null;
        }
        return StratType.values()[strat_index];
    }

//    public void sendReport() throws Exception {
//        increment(getChannel(REPORT_BLOCK, REPORT_ALIVE_OFFSET));
//    }
    /**
     * TODO Write a mission to the mission block.
     *
     * @param m
     * @param loc
     * @param block_num
     * @param num_units
     */
    public void createMission(MissionType m, MapLocation loc, int block_num, int num_units) {

    }

    /**
     * TODO Clears a block so every channel is zero.
     *
     * @param block_num
     * @param block_size
     */
    public void clearBlock(int block_num, int block_size) {

    }

    public int[] readBlock(int block_num, int block_size) throws Exception {
        int[] block = new int[block_size];
        for (int i = 0; i < block.length; i++) {
            block[i] = read(block_num, i);
        }
        return block;
    }

    public int read(int block_num, int offset) throws Exception {

        int chan = getChannel(block_num, offset);
        int raw = rc.readBroadcast(chan);
        System.out.println("Read:" + chan + ", " + raw);
        if (!isSigned(raw)) {

            return -1;
        } else {
            return unsign(raw);
        }

    }
}
