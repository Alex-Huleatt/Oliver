/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Moods.Swarm;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import java.util.ArrayList;
import java.util.Arrays;
import team016.Consts;
import team016.Comm.RadioController;
import team016.Const;
import team016.Moods.Defense.Spooked;
import team016.Moods.Mood;
import team016.Pair;
import team016.T;
import team016.Timer;
import team016.Units.Soldier;
import team016.Units.Unit;

/**
 *
 * @author alexhuleatt
 */
public abstract class IdeaMood extends Mood {

    public int old_idea_count;
    public int new_idea_count;
    public static int MAX_IDEA_DIS = 1000;
    MapLocation idea_loc;

    public IdeaMood(Soldier s) {
        super(s);
    }

    public IdeaMood(Soldier s, MapLocation idea_loc) {
        super(s);
        this.idea_loc = idea_loc;
    }

    private Idea getIdea(int num, int round_num) throws Exception {
        Pair<Integer, Integer> p = Consts.c("SOLDIER_IDEA_OFFSET", Consts.IDEA_SIZE.v, num);
        int n = radC.read(p, round_num);
        if (n == -1 || n > Idea.values().length || n < 0) {
            return Idea.NOCLUE;
        }
        return Idea.values()[n];
    }

    @Override
    public void updateVars() throws Exception {
        Timer t = new Timer();
        t.start();
        me = rc.getLocation();
        getNearbyRobots(RobotType.SOLDIER.sensorRadiusSquared);

        int num = 0;
        while (true) {
            Idea id = getIdea(num, Clock.getRoundNum());
            if (id == Idea.NOCLUE || id == null) {
                break;
            }
            num++;
        }
        t.stop();
        //System.out.println(t.elaps + " " + num);
        new_idea_count = num;
        num = 0;
        while (true) {
            Idea id = getIdea(num, Clock.getRoundNum() - 1);
            if (id == Idea.NOCLUE || id == null) {
                break;
            }
            num++;
        }
        old_idea_count = num;
        
        

    }

    private MapLocation getIdeaLoc(int num, int round_num) throws Exception {
        Pair<Integer, Integer> p = Consts.c("SOLDIER_POSN_OFFSET", Consts.IDEA_SIZE.v, num);
        int n = radC.read(p, round_num);
        if (n == -1) {
            return null;
        }
        return Const.intToLoc(n);
    }

    private int getIdeaVotes(int num, int round_num) throws Exception {
        Pair<Integer, Integer> p = Consts.c("SOLDIER_VOTE_OFFSET", Consts.IDEA_SIZE.v, num);
        int n = radC.read(p, round_num);
        if (n == -1) {
            return -1;
        }
        return n;
    }

    private void writeIdea(Idea id, MapLocation m, int votes) throws Exception {
        int idnum = id.ordinal();
        int mnum = Const.locToInt(m);
        radC.write(Consts.c("SOLDIER_IDEA_OFFSET", Consts.IDEA_SIZE.v, new_idea_count), idnum, Clock.getRoundNum());
        radC.write(Consts.c("SOLDIER_POSN_OFFSET", Consts.IDEA_SIZE.v, new_idea_count), mnum, Clock.getRoundNum());
        radC.write(Consts.c("SOLDIER_VOTE_OFFSET", Consts.IDEA_SIZE.v, new_idea_count), votes, Clock.getRoundNum());
    }

    private void addIdea(Idea id) throws Exception {
        writeIdea(id, me, 1);
    }

    private Idea lightbulb() throws Exception {
        if (enemies.length > 0) {
            if (Const.shouldAttack(rc, me, enemies, allies)) {
                //addIdea(Idea.ATTACK);
                //System.out.println("LET'S ATTACK GUISE");
                return Idea.ATTACK;
            } else {
                //System.out.println("LET'S RUN GUISE");
                //addIdea(Idea.OHGODPLEASENO);
                return Idea.OHGODPLEASENO;
            }
        }
        return null;

    }

    public void voteForIdea(int num) throws Exception {
        int votes = getIdeaVotes(num, Clock.getRoundNum());
    }

    public Pair<Idea, Integer> crowdSource() throws Exception {
        Idea id;
        MapLocation id_loc;
        int best_votes = 0;
        int votes;
        Idea best_id = Idea.NOCLUE;
        int i;
        for (i = 0; i < old_idea_count; i++) {
            id = getIdea(i, Clock.getRoundNum());
            id_loc = getIdeaLoc(i, Clock.getRoundNum());
            
            votes = getIdeaVotes(i, Clock.getRoundNum());
            if (id_loc != null && id != Idea.NOCLUE) {
                if (me.distanceSquaredTo(id_loc) < MAX_IDEA_DIS && votes >= best_votes) {
                    best_votes = votes;
                    best_id = id;
                }
            }
        }

        if (best_id != Idea.NOCLUE) {
            return new Pair<Idea, Integer>(best_id, i);
        }
        return null;
    }

    public Pair<Idea, Integer> crowdsource_pt2() throws Exception {
        Idea id;
        MapLocation id_loc;
        int best_votes = 0;
        int votes;
        Idea best_id = Idea.NOCLUE;
        int i;
        for (i = 0; i < new_idea_count; i++) {
            id = getIdea(i, Clock.getRoundNum() - 1);
            id_loc = getIdeaLoc(i, Clock.getRoundNum() - 1);
            votes = getIdeaVotes(i, Clock.getRoundNum() - 1);
            if (id_loc != null && id != Idea.NOCLUE) {
                if (me.distanceSquaredTo(id_loc) < MAX_IDEA_DIS && votes >= best_votes) {
                    best_votes = votes;
                    best_id = id;
                }
            }
        }
        if (best_id != Idea.NOCLUE) {
            return new Pair<Idea, Integer>(best_id, i);
        }
        return null;
    }

    public IdeaMood ideaToMood(Pair<Idea, Integer> p) throws Exception {
        Idea id = p.a;
        switch (id) {
            case ATTACK:
                return new Extroverted((Soldier) u, getIdeaLoc(p.b, Clock.getRoundNum()));
            case OHGODPLEASENO:
                return new Introverted((Soldier) u, getIdeaLoc(p.b, Clock.getRoundNum()));
            default:
                return null;
        }
    }

    public Idea getBaseType() {
        return Idea.NOCLUE;
    }

    @Override
    public Mood swing() throws Exception {
        Mood sup = super.swing();
        if (sup instanceof Spooked) {
            return sup;
        }
        if (cd > 0) {
            --cd;
            return null;
        }
        if (sup != null) {
            return sup;
        }
        if (idea_loc == null) {
            idea_loc = rc.senseEnemyHQLocation();
        }
        if (me.distanceSquaredTo(idea_loc) >= 20 && enemies.length == 0) {
            return new Prepare((Soldier) u, idea_loc);
        }
        Pair<Idea, Integer> p = crowdSource();
        if (p == null) {
            p = crowdsource_pt2();
            if (p == null) {
                Idea id = lightbulb();
                if (id == null) {
                    return null;
                }
                IdeaMood m = ideaToMood(new Pair<Idea,Integer>(id,new_idea_count));
                if (m==null) {
                    System.exit(0);
                }
                addIdea(id);
                return m;
            }
            return ideaToMood(p);
        }
        return ideaToMood(p);
    }

}
