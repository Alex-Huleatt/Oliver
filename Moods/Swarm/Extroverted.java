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
import team016.Comm.RadioController;
import team016.Const;
import team016.Moods.Mood;
import team016.Timer;
import team016.Units.Soldier;
import team016.Units.Unit;

/**
 * Group and attack
 *
 * @author alexhuleatt
 */
public class Extroverted extends IdeaMood {

    public Extroverted(Soldier s) {
        super(s);
    }

    public Extroverted(Soldier s, MapLocation ml) {
        super(s, ml);
    }

    @Override
    public void act() throws Exception {
//        if (enemies == null || enemies.length == 0) {
//            //System.out.println("In aggro state with no enemies");
//            return;
//        }
        getNearbyRobots(RobotType.SOLDIER.sensorRadiusSquared);
        if (idea_loc == null) {
            RobotInfo bot = Const.getClosest(me, enemies, rc);
          
            if (bot==null) return;
            idea_loc = bot.location;
        }
        rc.setIndicatorString(1, idea_loc + "");
        moveTowards(idea_loc);
    }
    
    @Override
    public Mood swing() throws Exception {
        Mood sup = super.swing();
        if (idea_loc == null) return new Prepare((Soldier)u);
        return sup;
    }

    @Override
    public Idea getBaseType() {
        return Idea.ATTACK;
    }
}
