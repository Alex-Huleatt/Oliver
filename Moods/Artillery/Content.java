/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Oliver.Moods.Artillery;

import Oliver.Const;
import Oliver.Moods.Mood;
import Oliver.Units.Soldier;
import battlecode.common.GameConstants;
import battlecode.common.GameObject;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import static battlecode.common.RobotType.ARTILLERY;
import battlecode.common.Team;

/**
 *
 * @author alexhuleatt
 */
public class Content extends Mood {

    public Content(Soldier s) {
        super(s);
    }
    
    @Override
    public void act() throws Exception {
        
        GameObject[] allRi = rc.senseNearbyGameObjects(Robot.class, ARTILLERY.attackRadiusMaxSquared, team.opponent());
        RobotInfo ri = Const.getClosest(rc.getLocation(), Const.robotFilter(allRi), rc);
        if (rc.isActive()) {
            rc.attackSquare(ri.location);
        }
    }
    
    @Override
    public String toString() {
        return "A: ^_^";
    }
    
}
