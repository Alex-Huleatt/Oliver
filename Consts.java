/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016;


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
    IDEA_SIZE(4);
    
    public final int v;
    Consts() {
        v=0;
    }
    Consts(int v) {
        this.v=v;
    }


    public enum Channel {

        GLOBAL_STRAT(HQ_BLOCK,0), HQ_STRAT(HQ_BLOCK,1), 
        
        REPORT_ALIVE_OFFSET(REPORT_BLOCK,0),
        SUPPLY_COUNT_OFFSET(REPORT_BLOCK,1),
        SUPPLY_REQUEST_OFFSET(REPORT_BLOCK,2),
        SUPPLY_SQUARE_POSN(REPORT_BLOCK,3),
        SUPPLY_REQUEST_NEW_POSN(REPORT_BLOCK,4),
        
        REQUESTED_SOLDIERS_OFFSET(MISSION_BLOCK,0),
        
        SOLDIER_IDEA_OFFSET(SOLDIER_BLOCK,1),
        SOLDIER_POSN_OFFSET(SOLDIER_BLOCK,2),
        SOLDIER_VOTE_OFFSET(SOLDIER_BLOCK,3);
        
        public Consts c;
        public int o;
        Channel(Consts c, int o) {
            this.c=c;
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
        return p;
    }
    
    /**
     * Get Channel by name
     * @param s
     * @return 
     */
    public static Pair<Integer,Integer> c(String s) {
        Channel c = Channel.valueOf(s);
        int block = c.c.ordinal();
        int offset = c.ordinal();
        return new Pair<Integer,Integer>(block,offset);
    }

}
