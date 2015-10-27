package team016;

import battlecode.common.*;
import static team016.Const.intToLoc;
import static team016.Const.validLoc;

/* Should probably comment or something */
public class Const {

    public static int directionToInt(Direction d) {
        switch (d) {
            case NORTH:
                return 0;
            case NORTH_EAST:
                return 1;
            case EAST:
                return 2;
            case SOUTH_EAST:
                return 3;
            case SOUTH:
                return 4;
            case SOUTH_WEST:
                return 5;
            case WEST:
                return 6;
            case NORTH_WEST:
                return 7;
            default:
                return -1;
        }
    }

    public static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST,
        Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH,
        Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

    public static RobotInfo getClosest(MapLocation loc, RobotInfo[] obs) {
        if (obs.length == 0) {
            return null;
        }
        int min = loc.distanceSquaredTo(obs[0].location);
        int mindex = 0;
        for (int i = 1; i < obs.length; i++) {
            int dis = loc.distanceSquaredTo(obs[i].location);
            if (dis < min) {
                min = dis;
                mindex = i;
            }
        }
        return obs[mindex];
    }

    public static RobotInfo getClosest(MapLocation loc, Robot[] bots, RobotController rc) throws Exception {

        return getClosest(loc, getInfos(rc, bots));
    }

    public static RobotInfo[] getInfos(RobotController rc, Robot[] bots) throws Exception {
        if (bots == null || bots.length == 0) {
            return new RobotInfo[]{};
        }

        RobotInfo[] info = new RobotInfo[bots.length];
        for (int i = 0; i < bots.length; i++) {
            info[i] = rc.senseRobotInfo(bots[i]);
        }
        return info;
    }

    public static int getThreat(RobotController rc, Robot[] bots) throws Exception {
        RobotInfo[] infos = getInfos(rc, bots);
        int total = 0;
        for (RobotInfo info : infos) {
            total += getThreatLevel(info);
        }
        return total;
    }

    public static int getThreatLevel(RobotInfo info) {
        switch (info.type) {
            case SOLDIER:
                return 1;
            case ARTILLERY:
                return 4;
            default:
                return 0;
        }
    }

    /**
     * Finds an direction that is safe from enemy attack, returns the safe
     * direction that is closest to the goal, or null when no move should occur.
     *
     * Note: the direction opposite to the goal is only returned when the rc's
     * current location is unsafe.
     */
    public static Direction findSafeLoc(RobotController rc, Robot[] enemies, Direction goal, boolean ignoreMines) throws Exception {
        MapLocation me = rc.getLocation();
        RobotInfo[] infos = getInfos(rc, enemies);
        if (rc.canMove(goal) && isSafeLoc(me.add(goal), infos)) {
            return goal;
        }
        int dir = directionToInt(goal);
        Direction check = Direction.NORTH;
        for (int i = 1; i <= 3; i++) {
            check = directions[(dir + i) % 8];
            if (rc.canMove(check) && isSafeLoc(me.add(check), infos)) {
                if (ignoreMines || !isBadMine(rc, me.add(check))) {
                    return check;
                }
            }
            check = directions[((dir - i) + 8) % 8];
            if (rc.canMove(check) && isSafeLoc(me.add(check), infos)) {
                if (ignoreMines || !isBadMine(rc, me.add(check))) {
                    return check;
                }
            }
        }
        if (isSafeLoc(me, infos)) {
            return null;
        }
        if (rc.canMove(goal.opposite()) && isSafeLoc(me.add(goal.opposite()), infos)) {
            if (ignoreMines || !isBadMine(rc, me.add(check))) {
                return goal.opposite();
            }
        }
        return null;
    }

    public static boolean isSafeLoc(RobotController rc, MapLocation loc, Robot[] enemies) throws Exception {
        RobotInfo[] infos = getInfos(rc, enemies);
        return isSafeLoc(loc, infos);
    }

    public static boolean isSafeLoc(MapLocation loc, RobotInfo[] infos) {
        for (RobotInfo info : infos) {
            if (getThreatLevel(info) > 0 && loc.distanceSquaredTo(info.location) < 2) {
                return false;
            }
        }
        return true;
    }

    public static Robot[] robotFilter(GameObject[] obs) {
        Robot[] r = new Robot[obs.length];
        int r_count = 0;
        for (GameObject ob : obs) {
            if (ob instanceof Robot) {
                r[r_count++] = (Robot) ob;
            }
        }
        Robot[] ret = new Robot[r_count];
        System.arraycopy(r, 0, ret, 0, ret.length);
        return ret;
    }

    public static Robot[] robotFilter(GameObject[] obs, Team team) {
        Robot[] r = new Robot[obs.length];
        int count = 0;

        for (GameObject ob : obs) {
            if (ob instanceof Robot && ob.getTeam() == team) {
                r[count++] = (Robot) ob;
            }
        }
        Robot[] robs = new Robot[count];
        System.arraycopy(r, 0, robs, 0, robs.length);
        return robs;
    }

    public static Robot[] getEnemies(RobotController rc, int disSquared) {
        GameObject[] obs = rc.senseNearbyGameObjects(
                Robot.class,
                disSquared,
                rc.getTeam().opponent());
        return Const.robotFilter(obs);
    }

    /**
     * Bresenham's Line algorithm
     *
     * @param rc
     * @param p1
     * @param p2
     * @return
     * @throws GameActionException
     */
    public boolean scan(RobotController rc, MapLocation p1, MapLocation p2) throws Exception {
        if (p1.isAdjacentTo(p2)) {
            return false;
        }
        int x1 = p1.x;
        int y1 = p1.y;
        int x2 = p2.x;
        int y2 = p2.y;
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;
        while (true) {
            int e2 = err << 1;
            if (e2 > -dy) {
                err = err - dy;
                x1 = x1 + sx;
            }
            if (x1 == x2 && y1 == y2) {
                break;
            }
            if (isObstacle(rc, new MapLocation(x1, y1))) {
                return true;
            }
            if (e2 < dx) {
                err = err + dx;
                y1 = y1 + sy;
            }
            if (x1 == x2 && y1 == y2) {
                break;
            }
            if (isObstacle(rc, new MapLocation(x1, y1))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isObstacle(RobotController rc, MapLocation loc) throws Exception {
        TerrainTile tt = rc.senseTerrainTile(loc);
        if (tt == TerrainTile.VOID || tt == TerrainTile.OFF_MAP) {
            return true;
        }
        Team mine = rc.senseMine(loc);
        if (mine != null && mine != rc.getTeam()) {
            return true;
        }
        if (rc.canSenseSquare(loc)) {
            GameObject obj = rc.senseObjectAtLocation(loc);
            return (obj != null);
        }
        return false;
    }

    public static boolean isBadMine(RobotController rc, MapLocation loc) {
        Team mine = rc.senseMine(loc);
        if (mine != null && mine != rc.getTeam()) {
            return true;
        }
        return false;
    }

    public static boolean locOnLine(MapLocation start, MapLocation end, MapLocation loc) {
        if (start == null || end == null || loc == null) {
            // YOU DONE MESSED UP A-A-RON
            return false;
        }
        return disToLine(start, end, loc) < 2;
    }

    public static double disToLine(MapLocation start, MapLocation end, MapLocation loc) {
        int x1 = start.x;
        int y1 = start.y;
        int x2 = end.x;
        int y2 = end.y;
        int x0 = loc.x;
        int y0 = loc.y;
        int xdiff = x2 - x1;
        int ydiff = y2 - y1;
        int n = ydiff * x0 - xdiff * y0 + x2 * y1 - y2 * x1;

        return Math.abs(n) / Math.sqrt((ydiff * ydiff + xdiff * xdiff));
    }

    public static boolean isObstacle(RobotController rc, Direction dir) throws Exception {
        return isObstacle(rc, rc.getLocation().add(dir));
    }

    public static boolean isObstacle(RobotController rc, int dir) throws Exception {
        return isObstacle(rc, directions[dir]);
    }
    public static MapLocation midpoint(MapLocation start, MapLocation end) {
        return new MapLocation((start.x + end.x) / 2, (start.y + end.y) / 2);
    }

    public static MapLocation intToLoc(int l) {
        return new MapLocation(l >> 8, l & 255);
    }

    public static int locToInt(MapLocation m) {
        if (m == null) {
            return -1;
        }
        return m.x << 8 | m.y;
    }

    public static boolean validLoc(MapLocation m) {
        return m.x > 0 && m.x < 100 && m.y > 0 && m.y < 100;
    }

    public static boolean validLoc(int i) {
        return validLoc(intToLoc(i));
    }
}
