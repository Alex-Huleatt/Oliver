/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016;

import java.util.HashSet;
import team016.Comm.RadioController;


/**
 *
 * @author alexhuleatt
 */
public enum Consts {

    /**
     * These are radio consts
     */
    HQ_BLOCK,
    REPORT_BLOCK,
    SOLDIER_BLOCK,
    MISSION_BLOCK,
    DEMOCRACY_BLOCK,
    HALP_BLOCK,
    CHAT_BLOCK;
    
    public final int v;
    Consts() {
        v=0;
    }
    Consts(int v) {
        this.v=v;
    }


    public enum Channel {

        GLOBAL_STRAT(HQ_BLOCK,0), 
        HQ_STRAT(HQ_BLOCK,1),
        TARGET_OFFSET(HQ_BLOCK,2),
        REGROUP_FLAG(HQ_BLOCK,3), 
        RALLY_OFFSET(HQ_BLOCK,4),
        
        
        REPORT_ALIVE_OFFSET(REPORT_BLOCK,0),
        SUPPLY_COUNT_OFFSET(REPORT_BLOCK,1),
        SUPPLY_REQUEST_OFFSET(REPORT_BLOCK,2),
        SUPPLY_SQUARE_POSN(REPORT_BLOCK,3),
        SUPPLY_REQUEST_NEW_POSN(REPORT_BLOCK,4),
        GEN_COUNT_OFFSET(REPORT_BLOCK,5),
        MEDBAY_REQUEST(REPORT_BLOCK,6),
        MEDBAY_COUNT(REPORT_BLOCK,7),
        
        REQUESTED_SOLDIERS_OFFSET(MISSION_BLOCK,0),
        
        SOLDIER_IDEA_OFFSET(SOLDIER_BLOCK,0),
        SOLDIER_POSN_OFFSET(SOLDIER_BLOCK,1),
        SOLDIER_VOTE_OFFSET(SOLDIER_BLOCK,2),

        TOTAL_CHATTERS(CHAT_BLOCK, 0),
        CHAT_INDEX(CHAT_BLOCK, 1);
        
        
        public Consts c;
        public int o;
        Channel(Consts c, int o) {
            this.c=c;
            this.o=o;
        }
        
    }
    
    
    /**
     * Get nth chunk of block.
     * @param s channel name.
     * @param sz blocksize
     * @param num which block
     * @return 
     */
    public static Pair<Integer,Integer> c(String s, int sz, int num) {
        Pair<Integer,Integer> p = c(s);
        p.b+=sz*num;
        //System.out.println(s + " " + p);
        return p;
    }
    
    /**
     * Get Channel by name
     * @param s
     * @return 
     */
    public static Pair<Integer,Integer> c(String s) {
        Channel c = Channel.valueOf(s);
        Pair<Integer,Integer> p = chanToPair(c);
        //System.out.println(s +":"+p);
        return p;
    }
    
    private static Pair<Integer, Integer> chanToPair(Channel c) {
        int block = c.c.ordinal();
        int offset = c.o;
        return new Pair<Integer,Integer>(block,offset);
    }

    public static HashSet<Integer> allUsedChannels(RadioController radC, int round_num) {
        Channel[] cs = Channel.values();
        HashSet<Integer> taken = new HashSet<Integer>();
        //for each channel we will have 2 pairs
        for (Channel c : cs) {
            Pair<Integer,Integer> p = chanToPair(c);
            taken.add(radC.getChannel(p.a, p.b, round_num));
            taken.add(radC.getChannel(p.a, p.b, round_num-1));
        }
        return taken;
    }

    public static int[][] readQueue(String s) {
        return null;
    }
}
