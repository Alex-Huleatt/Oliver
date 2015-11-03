/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Moods.Swarm;

import battlecode.common.MapLocation;
import team016.Moods.Mood;
import team016.Units.Soldier;

/**
 *
 * @author alexhuleatt
 */
public class Prepare extends IdeaMood {

    public Prepare(Soldier s) {
        super(s);
        idea_loc = rc.senseEnemyHQLocation();
    }

    public Prepare(Soldier s, MapLocation ml) {
        super(s,ml);
    }

    @Override
    public void act() throws Exception {
        if (idea_loc == null || idea_loc.equals(new MapLocation(0,0))) {
            idea_loc = rc.senseEnemyHQLocation();
        }
        moveTowards(idea_loc);
    }

    @Override
    public Idea getBaseType() {
        return Idea.PREPARE;
    }
    
}
