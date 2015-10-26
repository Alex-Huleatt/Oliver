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
    
    public static final int SUPPLY_COUNT_OFFSET = 2;
    public static final int SUPPLY_REQUEST_OFFSET = 3;
    public static final int SUPPLY_SQUARE_POSN = 4;

    //mission block
    public static final int MISSION_BLOCK = 2;
    public static final int REQUESTED_SOLDIERS_OFFSET = 0;
   

    public RadioController(RobotController rc) {
        this.rc = rc;
    }

    private static int getMask(int round_num, int block) {
        return ((int) xorshiftstar(round_num+block)) << 24;
    }

    static long xorshiftstar(int x) {
        x ^= x >> 12;
        x ^= x << 25;
        x ^= x >> 27;
        return x * 2685821657736338717l;
    }

    private int myHash(int block, int seed) {
        long rand = Math.abs(xorshiftstar(seed * ((rc.getTeam() == Team.A) ? 17 : 37)));
        return (int) ((rand * (1 + block)) % GameConstants.BROADCAST_MAX_CHANNELS);
    }

    private int getBaseChannel(int block, int round_num) {
        return Math.abs(myHash(block, round_num));
    }

    private static int signMessage(int message, int round_num, int block) {
        return message | getMask(round_num,block);
    }

    private static boolean isSigned(int message, int round_num,int block) {
        //System.out.println(message + " " + (message&MASK) + " " + MASK);\
        int mask = getMask(round_num,block);
        return (message & mask) == mask;
    }

    private static int unsign(int message, int round_num, int block) {
        return message ^ getMask(round_num, block);
    }

    /**
     * HERE BE PUBLIC FUNCTIONS
     */
    /**
     *
     * @param round_num
     * @return @throws Exception
     */
    public StratType curStrat(int round_num) throws Exception {
        int strat_index = read(HQ_BLOCK, RadioController.STRAT_OFFSET, round_num);
        if (strat_index <= 0 || strat_index > StratType.values().length) {
            return null;
        }
        return StratType.values()[strat_index];
    }

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

    /**
     * Reports the existence of a unit, by type. Writes to the REPORT_BLOCK
     * @param offset Which unit type to report
     * @throws Exception 
     */
    public void unitReport(int offset) throws Exception {
        int old = read(REPORT_BLOCK, offset, Clock.getRoundNum());
        if (old == -1) old = 0;
        write(REPORT_BLOCK, offset, old+1, Clock.getRoundNum());
    }
    
    public int[] readBlock(int block_num, int block_size, int round_num) throws Exception {
        int[] block = new int[block_size];
        for (int i = 0; i < block.length; i++) {
            block[i] = read(block_num, i, round_num);
        }
        return block;
    }

    public int read(int block_num, int offset, int round_num) throws Exception {

        int chan = getChannel(block_num, offset, round_num);
        int raw = rc.readBroadcast(chan);
        //System.out.println("Read:" + chan + ", " + raw);
        if (!isSigned(raw, round_num, block_num)) {

            return -1;
        } else {
            return unsign(raw, round_num, block_num);
        }

    }
    
    public int getMask(int[] arrrrrrr) {
        if (arrrrrrr.length == 0) {
            return 0;
        }
        int n = arrrrrrr[0];
        for (int i = 1; i < arrrrrrr.length; i++) n&=arrrrrrr[i];
        return n;
    }

    public int getChannel(int block_num, int offset, int round_num) {
        int base = getBaseChannel(block_num, round_num);
        
        return (base + offset) % GameConstants.BROADCAST_MAX_CHANNELS;
    }

    public void write(int block_num, int offset, int message, int round_num) throws Exception {
        int signed = signMessage(message, block_num, round_num);
        int chan = getChannel(block_num, offset, round_num);
        rc.broadcast(chan, signed);
        //System.out.println("Write:" + chan + ", " + signed);
    }
}
