/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Moods.Swarm;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import team016.Comm.CConsts;
import team016.Comm.RadioController;
import team016.Const;
import team016.Moods.Mood;
import static team016.Moods.Mood.MAX_IDEA_DIS;
import team016.Pair;
import team016.Units.Soldier;
import team016.Units.Unit;

/**
 *
 * @author alexhuleatt
 */
public abstract class IdeaMoods extends Mood {

    public IdeaMoods(Soldier s) {
        super(s);
    }

    private Idea getIdea(int num) throws Exception {
        Pair<Integer,Integer> p = CConsts.c("SOLDIER_IDEA_OFFSET", CConsts.IDEA_SIZE.v, num);
        
        int n = radC.read(p, Clock.getRoundNum());
        if (n == -1) {
            return Idea.NOCLUE;
        }
        return Idea.values()[n];
    }

    private MapLocation getIdeaLoc(int num) throws Exception {
        Pair<Integer,Integer> p = CConsts.c("SOLDIER_POSN_OFFSET", CConsts.IDEA_SIZE.v, num);
        int n = radC.read(p,Clock.getRoundNum());
        if (n == -1) {
            return null;
        }
        return Const.intToLoc(n);
    }

    /**
     * TODO*
     */
    private Idea lightbulb() {
        return null;
    }

    public Idea getNearbyIdea() throws Exception {
        Idea id;
        int i = 0;
        boolean found_good = false;
        while (true) {
            id = getIdea(i);
            MapLocation id_loc = getIdeaLoc(i);
            if (id_loc != null && id != Idea.NOCLUE) {
                if (me.distanceSquaredTo(id_loc) < MAX_IDEA_DIS) {
                    found_good = true;
                    break;
                }
            } else {
                break;
            }
            i++;
        }

        if (found_good) {
            return id;
        } else {
            return null;
        }

    }

    public Mood ideaToMood(Idea id) {
        switch(id) {
            case ATTACK: return new Extroverted((Soldier)u);
            case OHGODPLEASENO: return new Introverted((Soldier)u);
        } 
        return null;
    }
    @Override
    public Mood swing() throws Exception {

        Idea id = getNearbyIdea();
        if (id != null) {
            return ideaToMood(id);
        } else {
            return ideaToMood(lightbulb());
        }
    }

}
