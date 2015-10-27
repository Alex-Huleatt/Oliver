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
import team016.Pair;
import team016.Strat.MissionType;
import team016.Strat.StratType;

/**
 *
 * @author alexhuleatt
 */
public class RadioController {

    RobotController rc;

    public static final int MAX_MISSIONS = 5;
    


    public RadioController(RobotController rc) {
        this.rc = rc;
    }

    private static int getMask(int round_num, int block) {
        return ((int) xorshiftstar(round_num + block)) << 24;
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
        return message | getMask(round_num, block);
    }

    private static boolean isSigned(int message, int round_num, int block) {
        //System.out.println(message + " " + (message&MASK) + " " + MASK);\
        int mask = getMask(round_num, block);
        return (message & mask) == mask;
    }

    private static int unsign(int message, int round_num, int block) {
        return message ^ getMask(round_num, block);
    }

    public void unitReport(String ch) throws Exception {
        int rnd = Clock.getRoundNum();
        int old = read(ch,rnd);
        if (old == -1) {
            write(ch,1,rnd);
        }
        write(ch,old+1,rnd);
        
    }

    /**
     *
     * @param round_num
     * @return @throws Exception
     */
    public StratType curStrat(int round_num) throws Exception {
        final Pair<Integer,Integer> p = CConsts.c("GLOBAL_STRAT");
        int strat_index = read(p.a, p.b, round_num);
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
    
    public int[] readBlock(int block_num, int block_size, int round_num) throws Exception {
        int[] block = new int[block_size];
        for (int i = 0; i < block.length; i++) {
            block[i] = read(block_num, i, round_num);
        }
        return block;
    }

    private int read(int block_num, int offset) throws Exception {
        int round_num = Clock.getRoundNum();
        int chan = getChannel(block_num, offset, round_num);
        int raw = rc.readBroadcast(chan);
        //System.out.println("Read:" + chan + ", " + raw);
        if (!isSigned(raw, round_num, block_num)) {

            return -1;
        } else {
            return unsign(raw, round_num, block_num);
        }

    }

    public int read(Pair<Integer,Integer> p, int round_num) throws Exception {
        return read(p.a,p.b,round_num);
    }
    public void write(Pair<Integer,Integer> p, int message,int round_num) throws Exception {
        write(p.a,p.b,message, round_num);
    }
    private int read(int block_num, int offset, int round_num) throws Exception {

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
        for (int i = 1; i < arrrrrrr.length; i++) {
            n &= arrrrrrr[i];
        }
        return n;
    }

    public int getChannel(int block_num, int offset, int round_num) {
        int base = getBaseChannel(block_num, round_num);

        return (base + offset) % GameConstants.BROADCAST_MAX_CHANNELS;
    }

    private void write(int block_num, int offset, int message, int round_num) throws Exception {
        int signed = signMessage(message, block_num, round_num);
        int chan = getChannel(block_num, offset, round_num);
        rc.broadcast(chan, signed);
        //System.out.println("Write:" + chan + ", " + signed);
    }

    public boolean getUsed(RobotController rc) {
        
        return false;
    }
    
    public void write(String ch, int message, int round_num) throws Exception {
        write(CConsts.c(ch),message,round_num);
    }
    
    public int read(String ch, int round_num) throws Exception {
        return read(CConsts.c(ch),round_num);
    }
    
//    public int read(Pair<Integer,Integer> p, int round_num) throws Exception {
//        return read(p.a,p.b,round_num);
//    }
}
