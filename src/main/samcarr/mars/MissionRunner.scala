package samcarr.mars

import scala.collection.mutable
import Direction._

class MissionRunner(config: Configuration) {
    
    // Use a set to keep track of grid positions where a robot has left a scent, on the presumption
    // that scented positions are sparse - certainly only at the edges of the grid.
    val scentMap = new mutable.HashSet[(Int, Int)]

    val leftTurnMap = Map(North -> West, East -> North, South -> East, West -> South)
    val rightTurnMap = Map(North -> East, East -> South, South -> West, West -> North)

    def runMissions(): Seq[Robot] = {
        config.missions.map { mission =>
            // Map the command characters (L, R, F) into functions and then apply them in sequence,
            // taking an initial Robot through to a finish Robot.
            val commands = mission.commands.map(functionForChar(_))
            commands.foldLeft(mission.start) { (robot, command) => command(robot) }
        }
    }
    
    private def functionForChar(command: Char): (Robot => Robot) = command match {
        case 'L' => ifNotLost(left)
        case 'R' => ifNotLost(right)
        case 'F' => ifNotLost(forward)
    }

    // This is setup to allow currying: effectively wrapping the specific robot commands
    // and ignoring them if the robot has already been lost.
    private def ifNotLost(func: (HappyRobot => Robot))(robot: Robot) = robot match {
        case happy @ HappyRobot(_, _, _) => func(happy)
        case lost @ LostRobot(_,_,_) => lost
    }
    
    private def left(robot: HappyRobot) = HappyRobot(robot.x, robot.y, leftTurnMap(robot.facing))
    
    private def right(robot: HappyRobot) = HappyRobot(robot.x, robot.y, rightTurnMap(robot.facing))
    
    private def forward(robot: HappyRobot) = {
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