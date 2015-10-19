/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Oliver.Moods.Defense;

import Oliver.Const;
import Oliver.Moods.Mood;
import Oliver.Moods.Zerg.Aggro;
import Oliver.Soldier;
import Oliver.Unit;
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
    }
    
    @Override
    public Mood swing() throws Exception {
        if (rc.readBroadcast(0) == 0) {
            return new Aggro((Soldier)u);
        } else {
            return null;
        }
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
            } else if (dist < 10) {
                moveTowards(rc.senseHQLocation());
            }
        }
    }
    
    @Override
    public String toString() {
        return ":O";
    }
}
