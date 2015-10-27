/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Comm;

import team016.Pair;

/**
 *
 * @author alexhuleatt
 */
public enum CConsts {

    HQ_BLOCK,
    REPORT_BLOCK,
    SOLDIER_BLOCK,
    MISSION_BLOCK,
    
    IDEA_SIZE(4);
    
    public final int v;
    CConsts() {
        v=0;
    }
    CConsts(int o) {
        this.v=o;
    }


    public enum Channel {

        GLOBAL_STRAT(HQ_BLOCK,0), HQ_STRAT(HQ_BLOCK,1), 
        
        REPORT_ALIVE_OFFSET(REPORT_BLOCK,0),
        SUPPLY_COUNT_OFFSET(REPORT_BLOCK,1),
        SUPPLY_REQUEST_OFFSET(REPORT_BLOCK,2),
        SUPPLY_SQUARE_POSN(REPORT_BLOCK,3),
        
        REQUESTED_SOLDIERS_OFFSET(MISSION_BLOCK,0),
        
        SOLDIER_IDEA_SIZE(SOLDIER_BLOCK,0),
        SOLDIER_IDEA_OFFSET(SOLDIER_BLOCK,1),
        SOLDIER_POSN_OFFSET(SOLDIER_BLOCK,2),
        SOLDIER_VOTE_OFFSET(SOLDIER_BLOCK,3);
        
        public CConsts c;
        public int o;
        Channel(CConsts c, int o) {
            this.c=c;
        }
    }
    public static Pair<Integer,Integer> c(String s, int sz, int num) {
        Pair<Integer,Integer> p = c(s);
        p.b+=sz*num;
        return p;
    }
    
    public static Pair<Integer,Integer> c(String s) {
        Channel c = Channel.valueOf(s);
        int block = c.c.ordinal();
        int offset = c.ordinal();
        return new Pair<Integer,Integer>(block,offset);
    }

}
