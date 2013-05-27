package samcarr.mars

import scala.collection.mutable
import Direction._

class MarsMissionRunner(config: Configuration) {
    
    // Use a set to keep track of grid positions where a robot has left a scent, on the presumption
    // that scented positions are sparse - certainly only at the edges of the grid.
    val scentMap = new mutable.HashSet[(Int, Int)]
    
    def runMissions(): Seq[Robot] = {
        config.missions.map(runMission(_))
    }
    
    private def runMission(mission: Mission): Robot = {
        // Map the command characters (L, R, F) into functions and then apply them in sequence.
        val commands = mission.commands.map(functionForChar(_))
        commands.foldLeft(mission.startSituation) { (robot, command) => command(robot) }
    }
    
    private def functionForChar(command: Char): (Robot => Robot) = {
        command match {
            case 'L' => ifNotLost(left)
            case 'R' => ifNotLost(right)
            case 'F' => ifNotLost(forward)
        }
    }

    // This is setup to allow currying: effectively wrapping the specific robot commands.
    private def ifNotLost(func: (HappyRobot => Robot))(robot: Robot) = {
        robot match {
            case happy @ HappyRobot(_, _, _) => func(happy)
            case lost @ LostRobot(_,_,_) => lost
        }
    }
    
    private def left(robot: HappyRobot): Robot = {
        val newDirection = robot.facing match {
            case North => West
            case East => North
            case South => East
            case West => South
        }
        HappyRobot(robot.x, robot.y, newDirection)
    }
    
    private def right(robot: HappyRobot): Robot = {
        val newDirection = robot.facing match {
            case North => East
            case East => South
            case South => West
            case West => North
        }
        HappyRobot(robot.x, robot.y, newDirection)
    }
    
    private def forward(robot: HappyRobot): Robot = {
        val potentialNewSituation = robot.facing match {
            case North => HappyRobot(robot.x, robot.y + 1, robot.facing)
            case East => HappyRobot(robot.x + 1, robot.y, robot.facing)
            case South => HappyRobot(robot.x, robot.y - 1, robot.facing)
            case West => HappyRobot(robot.x - 1, robot.y, robot.facing)
        }
        
        def inBounds(sit: Robot, maxX: Int, maxY: Int): Boolean = {
            sit.x >= 0 && sit.x <= maxX && sit.y >= 0 && sit.y <= maxY
        }
        
        if (!inBounds(potentialNewSituation, config.maxX, config.maxY)) {
            if (scentMap(robot.x, robot.y)) {
                // Previous position was scent-marked - simply ignore the forward command.
                robot
            } else {
                // No scent-mark, so our robot is now lost, but adds a scent-mark.
                scentMap.add((robot.x, robot.y))
                LostRobot(robot.x, robot.y, robot.facing)
            }
        } else {
            potentialNewSituation
        }
    }
}