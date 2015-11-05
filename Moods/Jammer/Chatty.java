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
    int startChan;
    int chatSize;

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
        getChatRoom();

        int chan = startChan;
        while(Clock.getBytecodeNum() < 5000 && chan <= chatSize+startChan && chan < 65535) {
            chat(chan);
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

    private void getChatRoom() throws Exception {
        int num = radC.read("TOTAL_CHATTERS", Clock.getRoundNum());
        int chatSize;
        if (num == 0) chatSize = 65534;
        else chatSize = 65534/num;
        int index = radC.read("CHAT_INDEX",  Clock.getRoundNum());
        startChan = index * chatSize;

        // System.out.println("Index: " + index);

        radC.write("CHAT_INDEX", index+1, Clock.getRoundNum());

        // TODO: this shouldn't be necessary, but it is
        if (chatSize + startChan > 65534) {
            chatSize = 0;
            startChan = 0;
        }
        // System.out.println("Covering " + chatSize + " channels starting at " + startChan);
    }


    @Override
    public String toString() {return "T:  (?°?°??? ???";}
}
