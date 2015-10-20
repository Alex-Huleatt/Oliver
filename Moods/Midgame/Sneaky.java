/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Moods.Midgame;

import battlecode.common.MapLocation;
import java.util.ArrayList;
import team016.Moods.Mood;
import team016.Units.Unit;

/**
 * We've been assigned to a dangerous mission. 
 * Find a target and destroy it, similar to zerg.
 * 
 * @author alexhuleatt
 */
public class Sneaky extends Mood {

    public Sneaky(Unit u) {
        super(u);
    }
    
    @Override
    public void act() {
        MapLocation[] encampments = rc.senseAllEncampmentSquares();
        //
    }
    
}
