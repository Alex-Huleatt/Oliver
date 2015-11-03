/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Comm.Channels;

import team016.Comm.RadioController;
import team016.Pair;

/**
 *
 * @author alexhuleatt
 */
public abstract class StdQueue extends Block {
    
    static enum myChans {
        HEAD,TAIL
    }
    
    protected static int getChan(String st) {
        return myChans.valueOf(st).ordinal();
    }
    
    public static final int pop(RadioController radC, int cnum) throws Exception {
        Pair<Integer,Integer> head = getPair("HEAD");
        int to_ret = radC.read(head, cnum);
        
        ///NEED TO INC HEAD
        return to_ret;
    }
    
    public static final void push(RadioController radC) throws Exception {
        
    }
    
    
    
    
    
}
