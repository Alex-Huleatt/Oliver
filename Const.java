package Oliver;

import battlecode.common.*;

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
            if (x1 == x2 && y1 == y2) break;
            if (isObstacle(rc, new MapLocation(x1, y1))) return true;
            if (e2 < dx) {
                err = err + dx;
                y1 = y1 + sy;
            }
            if (x1 == x2 && y1 == y2) break;
            if (isObstacle(rc, new MapLocation(x1, y1)))return true;
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
            Robot ri = null;
            GameObject obj = rc.senseObjectAtLocation(loc);
            return (obj != null);
            //if (obj instanceof Robot) ri = (Robot) obj;
            //return (ri != null && (rc.senseRobotInfo(ri).type == RobotType.HQ));
        }
        return false;
    }

    public static boolean locOnLine(MapLocation start, MapLocation end, MapLocation loc) {
        if (start == null || end == null || loc == null) {
            // YOU DONE MESSED UP A-A-RON
            return false;
        }
        return disToLine(start,end,loc) < 2;
    }
    
    public static double disToLine(MapLocation start, MapLocation end, MapLocation loc) {
        int x1 = start.x;
        int y1 = start.y;
        int x2 = end.x;
        int y2 = end.y;
        int x0 = loc.x;
        int y0 = loc.y;

        int xdiff = x2-x1;
        int ydiff = y2-y1;
        int n = ydiff*x0 - xdiff*y0 +x2*y1-y2*x1;
        
        return Math.abs(n)/Math.sqrt((ydiff*ydiff + xdiff*xdiff));
    }

    public static boolean isObstacle(RobotController rc, Direction dir) throws Exception {
        return isObstacle(rc, rc.getLocation().add(dir));
    }
}
