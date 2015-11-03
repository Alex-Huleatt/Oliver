/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016;

import battlecode.common.MapLocation;

/**
 *
 * @author alexhuleatt
 */
public class MapQueue {

    private final float[] costs;
    private final MapLocation[] q;
    private int index;
    public MapQueue() {
        costs = new float[5000];
        q = new MapLocation[5000];
        index = 0;
    }

    public void add(MapLocation p, float c) {
        int i = index;
        for (; i != 0 && c > costs[i - 1] && i > index - 300; --i) {
            costs[i] = costs[i - 1];
            q[i] = q[i - 1];
        }
        costs[i] = c;
        q[i] = p;
        index++;
    }
    public MapLocation pop() {
        if (index<1)return null;
        return q[--index];
    }
    
}
