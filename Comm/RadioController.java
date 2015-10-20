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
import team016.Scheme.MissionType;
import team016.Scheme.StratType;

/**
 *
 * @author alexhuleatt
 */
public class RadioController {

    RobotController rc;
    private static final int MASK = 0x93000;

    public static final int MISSION_TYPE_OFFSET = 1;
    public static final int REPORT_ALIVE_OFFSET = 2;
    public static final int X_POSN_OFFSET = 3;
    public static final int Y_POSN_OFFSET = 4;

    public static final int STRAT_OFFSET = 1;
    public static final int HQ_BLOCK = 1;
    public static final int MAX_MISSIONS = 5;

    public RadioController(RobotController rc) {
        this.rc = rc;
    }

    long xorshiftstar(int x) {
        x+=1;
        x ^= x >> 12;
        x ^= x << 25;
        x ^= x >> 27;
        return x * 2685821657736338717l;
    }
    
    private int myHash(int block, int seed) {
        long rand = Math.abs(xorshiftstar(seed));
        return (int) ((rand * (1+block)) % GameConstants.BROADCAST_MAX_CHANNELS);
    }

    private int getBaseChannel_write(int block) {
        int chan = myHash(block,Clock.getRoundNum());
        return chan;
        
    }

    private int getBaseChannel_read(int block) {
        
        return myHash(block,Clock.getRoundNum());
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
        int base = (writing)?
                getBaseChannel_write(block_num):
                getBaseChannel_read(block_num);
        return ( base + offset) % GameConstants.BROADCAST_MAX_CHANNELS;
    }

    public void write(int block_num, int offset, int message) throws Exception {
        int signed = signMessage(message);
        int chan = getChannel(block_num, offset,true);
        rc.broadcast(chan, signed);
    }

    public void write(int chan, int message) throws Exception {
        int signed = signMessage(message);
        rc.broadcast(chan, signed);
    }

    public MissionType getMissionType(int block_num) throws Exception {
        int type = read(block_num, MISSION_TYPE_OFFSET, true);
        if (type >= 0 && type < MissionType.values().length) {
            return MissionType.values()[type];
        }
        return MissionType.CORRUPTED;
    }

    public MapLocation getMissionLoc(int block_num) throws Exception {
        int x = read(block_num, X_POSN_OFFSET, true);
        int y = read(block_num, Y_POSN_OFFSET, true);
        if (x == -1 || y == -1) {
            return null;
        }
        return new MapLocation(x, y);
    }
    
    public StratType curStrat() throws Exception {
        int strat_index = read(0, RadioController.STRAT_OFFSET, true);
        if (strat_index <= 0) return null;
       return StratType.values()[strat_index];
    }

    public void reportAlive(int block_num) throws Exception {
        increment(getChannel(block_num, REPORT_ALIVE_OFFSET,true));
    }

    public void writeMission(MissionType m, MapLocation loc, int block_num) {

    }

}
