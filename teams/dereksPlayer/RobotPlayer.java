package dereksPlayer;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {
	public static void run(RobotController rc) {
        BaseBot myself;

        if (rc.getType() == RobotType.HQ) {
            myself = new HQ(rc);
        } else if (rc.getType() == RobotType.BEAVER) {
            myself = new Beaver(rc);
        } else if (rc.getType() == RobotType.BARRACKS) {
            myself = new Barracks(rc);
        } else if (rc.getType() == RobotType.SOLDIER) {
            myself = new Soldier(rc);
        } else if (rc.getType() == RobotType.TOWER) {
            myself = new Tower(rc);
        } else {
            myself = new BaseBot(rc);
        }

        while (true) {
            try {
                myself.go();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	
	public static class BaseBot {
        protected RobotController rc;
        protected MapLocation myHQ, theirHQ;
        protected Team myTeam, theirTeam;

        public BaseBot(RobotController rc) {
            this.rc = rc;
            this.myHQ = rc.senseHQLocation();
            this.theirHQ = rc.senseEnemyHQLocation();
            this.myTeam = rc.getTeam();
            this.theirTeam = this.myTeam.opponent();
        }

        public Direction[] getDirectionsToward(MapLocation dest) {
            Direction toDest = rc.getLocation().directionTo(dest);
            Direction[] dirs = {toDest,
		    		toDest.rotateLeft(), toDest.rotateRight(),
				toDest.rotateLeft().rotateLeft(), toDest.rotateRight().rotateRight()};

            return dirs;
        }

        public Direction getMoveDir(MapLocation dest) {
            Direction[] dirs = getDirectionsToward(dest);
            for (Direction d : dirs) {
                if (rc.canMove(d)) {
                    return d;
                }
            }
            return null;
        }

        public Direction getSpawnDirection(RobotType type) {
            Direction[] dirs = getDirectionsToward(this.theirHQ);
            for (Direction d : dirs) {
                if (rc.canSpawn(d, type)) {
                    return d;
                }
            }
            return null;
        }
        
        public Direction getBeaverSpawnDirection() {
        	Direction[] allDirs = {Direction.NORTH, Direction.NORTH_WEST, Direction.WEST, Direction.SOUTH_WEST, Direction.SOUTH, Direction.SOUTH_EAST, Direction.EAST, Direction.NORTH_EAST};
        	//shuffleArray(allDirs);
        	for (Direction d : allDirs) {
        		if (rc.canSpawn(d, RobotType.BEAVER)){
        			return d;
        		}
        	}
        	return null;
        }

        public Direction getBuildDirection(RobotType type) {
            Direction[] dirs = getDirectionsToward(this.theirHQ);
            for (Direction d : dirs) {
                if (rc.canBuild(d, type)) {
                    return d;
                }
            }
            return null;
        }

        public RobotInfo[] getAllies() {
            RobotInfo[] allies = rc.senseNearbyRobots(Integer.MAX_VALUE, myTeam);
            return allies;
        }

        public RobotInfo[] getEnemiesInAttackingRange() {
            RobotInfo[] enemies = rc.senseNearbyRobots(RobotType.SOLDIER.attackRadiusSquared, theirTeam);
            return enemies;
        }

        public void attackLeastHealthEnemy(RobotInfo[] enemies) throws GameActionException {
            if (enemies.length == 0) {
                return;
            }

            double minEnergon = Double.MAX_VALUE;
            MapLocation toAttack = null;
            for (RobotInfo info : enemies) {
                if (info.health < minEnergon) {
                    toAttack = info.location;
                    minEnergon = info.health;
                }
            }

            rc.attackLocation(toAttack);
        }

        public void beginningOfTurn() {
            if (rc.senseEnemyHQLocation() != null) {
                this.theirHQ = rc.senseEnemyHQLocation();
            }
        }

        public void endOfTurn() {
        }

        public void go() throws GameActionException {
            beginningOfTurn();
            execute();
            endOfTurn();
        }

        public void execute() throws GameActionException {
            rc.yield();
        }
    }

	//----- HQ -----//
    public static class HQ extends BaseBot {
        public HQ(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            int numBeavers = rc.readBroadcast(2);

            if (rc.isCoreReady() && rc.getTeamOre() > 100 && numBeavers < 10) {
                Direction newDir = getBeaverSpawnDirection();
                if (newDir != null) {
                    rc.spawn(newDir, RobotType.BEAVER);
                    rc.broadcast(2, numBeavers + 1);
                }
            }
            
            //Sets rally to halfway until T=600, then charges
//            MapLocation rallyPoint;
//            if (Clock.getRoundNum() < 600) {
//                rallyPoint = new MapLocation( (this.myHQ.x + this.theirHQ.x) / 2,
//                                              (this.myHQ.y + this.theirHQ.y) / 2);
//            }
//            else {
//                rallyPoint = this.theirHQ;
//            }
//            
//            rc.broadcast(0, rallyPoint.x);
//            rc.broadcast(1, rallyPoint.y);

            rc.yield();
        }
    }

    //----- Beaver -----//
    public static class Beaver extends BaseBot {
        public Beaver(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
//            if (rc.isCoreReady()) {
//                if (rc.getTeamOre() < 500) {
//                    //mine
//                    if (rc.senseOre(rc.getLocation()) > 0) {
//                        rc.mine();
//                    }
//                    else {
//                        Direction newDir = getMoveDir(this.theirHQ);
//
//                        if (newDir != null) {
//                            rc.move(newDir);
//                        }
//                    }
//                }
//                else {
//                    //build barracks
//                    Direction newDir = getBuildDirection(RobotType.BARRACKS);
//                    if (newDir != null) {
//                        rc.build(newDir, RobotType.BARRACKS);
//                    }
//                }
//            }

            rc.yield();
        }
    }

    //----- Barracks -----//
    public static class Barracks extends BaseBot {
        public Barracks(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
//            if (rc.isCoreReady() && rc.getTeamOre() > 200) {
//                Direction newDir = getSpawnDirection(RobotType.SOLDIER);
//                if (newDir != null) {
//                    rc.spawn(newDir, RobotType.SOLDIER);
//                }
//            }

            rc.yield();
        }
    }

    //----- Soldier -----//
    public static class Soldier extends BaseBot {
        public Soldier(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
//            RobotInfo[] enemies = getEnemiesInAttackingRange();
//
//            if (enemies.length > 0) {
//                //attack!
//                if (rc.isWeaponReady()) {
//                    attackLeastHealthEnemy(enemies);
//                }
//            }
//            else if (rc.isCoreReady()) {
//                int rallyX = rc.readBroadcast(0);
//                int rallyY = rc.readBroadcast(1);
//                MapLocation rallyPoint = new MapLocation(rallyX, rallyY);
//
//                Direction newDir = getMoveDir(rallyPoint);
//
//                if (newDir != null) {
//                    rc.move(newDir);
//                }
//            }
            rc.yield();
        }
    }
    
  //----- Tower -----//
    public static class Tower extends BaseBot {
        public Tower(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            rc.yield();
        }
    }
}