package team016.Moods.Jammer;

import battlecode.common.MapLocation;
import battlecode.common.Robot;
import team016.Comm.RadioController;
import team016.Const;
import team016.Consts;
import team016.Moods.Mood;
import team016.Units.Unit;

import battlecode.common.Clock;

/**
 * @author MacKenzie O'Bleness
 */
public class Chatty extends Mood {
    int[] newMasks;
    boolean scared = false;

    public Chatty(Unit u) {
        super(u);
        newMasks = new int[Consts.values().length];
    }

    @Override
    public Mood swing() throws Exception {return this;}

    @Override
    public void act() throws Exception {
        checkScared();
        updateMasks();
        int chan = (int) (Math.random() * 65534);
        while(Clock.getBytecodeNum() < 5000) {
            chat(chan);
            chan = (chan + 1) % 65535;
        }
    }

    private void chat(int chan) throws Exception{
        int message = rc.readBroadcast(chan);
        if (message == 0 || message == Integer.MAX_VALUE) return;
        if (isOurs(newMasks, message)) return;

        // System.out.println("I overrote dat channel!");
        rc.broadcast(chan, Integer.MAX_VALUE);
    }

    private boolean isOurs(int[] masks, int message) {
        for(int i = 0; i < masks.length; i++) {
            if ((message & masks[i]) == masks[i]) return true;
        }
        return false;
    }

    private void updateMasks() throws Exception {
        for (int i = 0; i < Consts.values().length; i++) {
            newMasks[i] = RadioController.getMask(Clock.getRoundNum(), i);
        }
    }

    private void checkScared() throws Exception {
        int enemies  = rc.senseNearbyGameObjects(
                Robot.class,
                16,
                team.opponent()).length;
        MapLocation me = rc.getLocation();
        if (enemies == 0) {
            if (!scared) return;
            scared = false;
            if (Const.intToLoc(radC.read("THREAT_LOC", Clock.getRoundNum())) == me) {
                radC.write("THREAT_COUNT", 0, Clock.getRoundNum());
            }
            return;
        }

        int curThreat = radC.read("THREAT_COUNT", Clock.getRoundNum());
        if (curThreat < 0) return;
        int curIntLoc = radC.read("THREAT_LOC", Clock.getRoundNum());
        if (curIntLoc == -1) return;
        MapLocation curLoc = Const.intToLoc(curIntLoc);

        if (me.distanceSquaredTo(rc.senseHQLocation()) < curLoc.distanceSquaredTo(rc.senseHQLocation()) || enemies/curThreat > 0.5) {
            radC.write("THREAT_COUNT", enemies, Clock.getRoundNum());
            radC.write("THREAT_LOC", Const.locToInt(me), Clock.getRoundNum());
            rc.setIndicatorString(2, "DISTRESSED");
            scared = true;
        }
    }


    @Override
    public String toString() {return "T:  ;)";}
}
