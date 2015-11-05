/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Strat;

/**
 *
 * @author alexhuleatt
 */
public class HeatMap {
    
    //we can really only have like 16, 8 positives, 8 negations.

    
    //we're going to represent the map as 8 integers.
    //Each little section gets 1 byte to represent it's whatnot.
    byte[][] tants = new byte[8][8];
    
    int[] serialize() {
        int temp;
        int[] to_send = new int[8];
        for (int i = 0; i < 8; i++) {
            temp = 0;
            for (int j=0; j < 8; j++) {
                temp = (temp | tants[i][j])<<8;
            }
            to_send[i] = temp;
        }
        return to_send;
    }
    
    //we can say each bit in the byte represents some piece of info. Like artillery, etc.
    
    public byte getByte(int nth) {
        return tants[nth/8][nth%8];
    }
    
    //given xy coord, return which tant it is in
    public static int getNth(int x, int y, int w, int h) {
        int width = w/8;
        int hight = h/8;
        
        int row = y/hight;
        int col = x/width;
        
        return 8*row+col;
    }
    
    public void report(int nth, Report r) {
        switch(r) {
            case ARTILLERY: {
               
                break;
            }
            case NO_ARTILLERY: {
                
                break;
            }
            case MEDBAY: {
                
                break;
            }
            case NO_MEDBAY: {
                
                break;
            }
            case MINES: {
                
                break;
            }
            case NO_MINES: {
                
                break;
            }
        }
    }
    
}
