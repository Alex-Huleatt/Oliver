package team016.Moods.Zerg;

import battlecode.common.Clock;
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
import team016.Timer;


public class Aggro extends Mood {

    public Aggro(Soldier s) {
        super(s);
    }
    
    public Aggro(Soldier s, Robot[] a, Robot[] e) {
        super(s);
        allies = a;
        enemies = e;
        
    }

    @Override
    public Mood swing() throws Exception {
        Mood sp = super.swing();
        if (sp != null) return sp;
        // - If unit has low health and few allies, transition to Zerg.Hurt
        // - If there are no sensable unit around, transition to Zerg.Rushing
        // - Else return Aggro
        getNearbyRobots(25);
        if (enemies.length == 0) {
            return new Rushing((Soldier)u);
        }
        if (rc.getEnergon() < 15 && (allies.length < 4 || enemies.length > allies.length)) {
            return new Hurt((Soldier)u);
        }
        
        return null;
        
    }

    @Override
    public void act() throws Exception {
        MapLocation me = rc.getLocation();
        if (enemies == null || enemies.length == 0) {
            //System.out.println("In aggro state with no enemies");
            return;
        }
        RobotInfo bot = Const.getClosest(me, enemies, rc);
        
        if (bot == null) {
            System.out.println("Aggro couldn't find closest bot!");
            return;
        }
        MapLocation closest = bot.location;
        
        Direction dir = me.directionTo(closest);
        int dist = me.distanceSquaredTo(closest);
        if (Const.getThreat(rc, enemies)  < Const.getThreat(rc, allies)) {
            if (dist > 15) {
                moveTowards(closest);
            }
            else {
                moveish(dir);
            }
            if (rc.isActive()) {
                mine(dir);
            }
            return;
        } else if (dist < 25) {
            moveish(dir.opposite());
        }
    }
    
    private void mine(Direction d) throws Exception {
        int dir = Const.directionToInt(d);
        MapLocation me = rc.getLocation();
        
        MapLocation mine;
        Team mTeam;
        for (int i = -1; i <= 1; i++) {
            mine = me.add(Const.directions[((dir + i) + 8) % 8]);
            mTeam = rc.senseMine(mine);
            if (mTeam == Team.NEUTRAL) {
                rc.defuseMine(mine);
                return;
            } else if (mTeam == rc.getTeam().opponent() && Const.isSafeLoc(rc, me, enemies)) {
                rc.defuseMine(mine);
                return;
            }
        }
        
        
    }

    @Override
    public String toString() {
        return "Z: >:(";
    }
}