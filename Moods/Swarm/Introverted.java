/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Moods.Swarm;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import team016.Const;
import team016.Moods.Mood;
import team016.Timer;
import team016.Units.Soldier;

/**
 * Spread and run
 *
 * @author alexhuleatt
 */
public class Introverted extends IdeaMood {

    public MapLocation from;

    public Introverted(Soldier s) {
        super(s);
    }

    public Introverted(Soldier s, MapLocation ml) {
        super(s, ml);
    }

    @Override
    public Idea getBaseType() {
        return Idea.OHGODPLEASENO;
    }

    @Override
    public void act() throws Exception {
        me = rc.getLocation();
        if (enemies == null || enemies.length == 0) {
            //System.out.println("In aggro state with no enemies");
            return;
        }
        //getNearbyRobots(RobotType.SOLDIER.sensorRadiusSquared);
        RobotInfo bot = Const.getClosest(me, enemies, rc);
        if (bot == null) {
            if (me.distanceSquaredTo(idea_loc) < 30) {
                moveish(me.directionTo(idea_loc).opposite());
            }
            return;
        }
        MapLocation closest = bot.location;

        Direction dir = me.directionTo(closest);
        moveish(dir.opposite());

    }
}
