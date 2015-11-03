/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Comm.Channels;

import team016.Pair;

/**
 *
 * @author alexhuleatt
 */
public abstract class Block {
    
    static enum myChans {};
    
    protected static int getChan(String st) {
        return myChans.valueOf(st).ordinal();
    }
    
    protected static int getID(){return 0;}
    
    protected static final Pair<Integer,Integer> getPair(String st) {
        return new Pair<Integer,Integer>(getID(),getChan(st));
    }
    
    
}
