package team016.Moods.Jammer;

import team016.Comm.RadioController;
import team016.Consts;
import team016.Moods.Mood;
import team016.Units.Unit;

import battlecode.common.Clock;

/**
 * @author MacKenzie O'Bleness
 */
public class Chatty extends Mood {

    int[] oldMasks;
    int[] newMasks;

    public Chatty(Unit u) {
        super(u);
        oldMasks = new int[Consts.values().length];
        newMasks = new int[Consts.values().length];
    }

    @Override
    public Mood swing() throws Exception {return this;}

    @Override
    public void act() throws Exception {
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
        if (isOurs(newMasks, message) || isOurs(oldMasks, message)) return;

        System.out.println("I overrote dat channel!");
        rc.broadcast(chan, Integer.MAX_VALUE);
    }

    private boolean isOurs(int[] masks, int message) {
        for(int i = 0; i < masks.length; i++) {
            if ((message & masks[i]) == masks[i]) return true;
        }
        return false;
    }

    private void updateMasks() throws Exception {
        System.arraycopy(newMasks, 0, oldMasks, 0, oldMasks.length);
        newMasks = new int[Consts.values().length];
        for (int i = 0; i < Consts.values().length; i++) {
            newMasks[i] = RadioController.getMask(Clock.getRoundNum(), i);
        }
    }


    @Override
    public String toString() {return "T:  ;)";}
}
