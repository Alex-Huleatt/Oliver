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

    public RadioController(RobotController rc) {
        this.rc = rc;
    }

    public int getBaseChannel(int block) {
        int round_num = Clock.getRoundNum();
        return ((block + 17) * round_num * round_num
                * ((rc.getTeam() == Team.A) ? 97 : 117) * 37 + GameConstants.BROADCAST_MAX_CHANNELS)
                % GameConstants.BROADCAST_MAX_CHANNELS;
    }

    private static int signMessage(int message) {
        return message | MASK;
    }

    private static boolean isSigned(int message) {
        return (message & MASK) == MASK;
    }

    private static int unsign(int message) {
        return message ^ MASK;
    }

    public int read(int block_num, int offset) throws Exception {
        int chan = getChannel(block_num, offset);
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

    public int getChannel(int block_num, int offset) {
        return (getBaseChannel(block_num) + offset) % GameConstants.BROADCAST_MAX_CHANNELS;
    }

    public void write(int block_num, int offset, int message) throws Exception {
        int signed = signMessage(message);
        int chan = getChannel(block_num, offset);
        rc.broadcast(chan, signed);
    }
    
    public void write(int chan,int message) throws Exception {
        int signed = signMessage(message);
        rc.broadcast(chan, signed);
    }

    private void increment(int chan) throws Exception {
        write(chan,read(chan)+1);
    }

    public MissionType getMissionType(int block_num) throws Exception {
        int type = read(block_num, MISSION_TYPE_OFFSET);
        if (type >= 0 && type < MissionType.values().length) {
            return MissionType.values()[type];
        }
        return MissionType.CORRUPTED;
    }

    public MapLocation getMissionLoc(int block_num) throws Exception {
        int x = read(block_num, X_POSN_OFFSET);
        int y = read(block_num, Y_POSN_OFFSET);
        if (x==-1 || y ==-1) {
            return null;
        }
        return new MapLocation(x, y);
    }
    
    public void reportAlive(int block_num) throws Exception {
        increment(getChannel(block_num, REPORT_ALIVE_OFFSET));
    }


}
