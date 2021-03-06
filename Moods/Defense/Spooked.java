/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Moods.Defense;

import team016.Const;
import team016.Moods.Mood;
import team016.Moods.Zerg.Aggro;
import team016.Units.Soldier;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

/**
 *
 * @author alexhuleatt
 */
public class Spooked extends Mood {

    public Spooked(Soldier s) {
        super(s);
        cd = 35;
    }
    
    @Override
    public void act() throws Exception {
        //defend HQ
        
        getNearbyRobots(RobotType.SOLDIER.sensorRadiusSquared);
        RobotInfo bot = Const.getClosest(rc.getLocation(), enemies, rc);
        if (bot == null) {
            if (rc.isActive()) moveTowards(rc.senseHQLocation());
        } else {
            MapLocation closest = bot.location;
            int dist = rc.getLocation().distanceSquaredTo(closest);
            if (enemies.length < allies.length) {
                if (dist > 15) moveTowards(closest);
                else moveish(rc.getLocation().directionTo(closest));
            } else moveTowards(rc.senseHQLocation());
        }
    }
    
    @Override
    public String toString() {
        return ":O";
    }
}
