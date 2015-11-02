package team016.Moods.AllIn;

import team016.Moods.Zerg.*;
import team016.Const;
import team016.Units.Soldier;
import team016.Moods.Mood;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Team;

public class Desperate extends Mood {

    public Desperate(Soldier s) {
        super(s);
    }

    public Desperate(Soldier s, Robot[] a, Robot[] e) {
        super(s);
        allies = a;
        enemies = e;

    }

    @Override
    public Mood swing() throws Exception {
        Mood sp = super.swing();
        if (sp != null) {
            return sp;
        }
        // - If unit has low health and few allies, transition to Zerg.Hurt
        // - If there are no sensable unit around, transition to Zerg.Rushing
        // - Else return Aggro
        getNearbyRobots(25);
        if (enemies.length == 0) {
            return new DRushing((Soldier) u);
        }
        return null;

    }

    @Override
    public void act() throws Exception {
        // TODO: move towards closest enemy
        if (enemies == null || enemies.length == 0) {
            System.out.println("In aggro state with no enemies");
            return;
        }
        RobotInfo bot = Const.getClosest(rc.getLocation(), enemies, rc);
        if (bot == null) {
            System.out.println("Aggro couldn't find closest bot!");
        }
        MapLocation closest = bot.location;
        Direction dir = rc.getLocation().directionTo(closest);
        int dist = rc.getLocation().distanceSquaredTo(closest);
        if (dist > 15) {
            moveTowards(closest);
        } else {
            moveish(dir);
        }
        if (rc.isActive()) {
            mine(dir);
        }
        return;
    }

//    private void mine(Direction d) throws Exception {
//        int dir = Const.directionToInt(d);
//        MapLocation me = rc.getLocation();
//
//        MapLocation mine;
//        Team mTeam;
//        for (int i = 0; i < 1; i++) {
//            mine = me.add(Const.directions[((dir + i) + 8) % 8]);
//            mTeam = rc.senseMine(mine);
//            if (mTeam == Team.NEUTRAL) {
//                rc.defuseMine(mine);
//                return;
//            } else if (mTeam == rc.getTeam().opponent()) {
//                // TODO: check if mine location is safe
//            }
//        }
//
//    }

    @Override
    public String toString() {
        return "D: >:(";
    }
}
