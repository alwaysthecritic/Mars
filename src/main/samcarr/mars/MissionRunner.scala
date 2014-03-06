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
            val commands = mission.commands.map(commandForChar(_))
            commands.foldLeft(mission.start) { (robot, command) => command(robot) }
        }
    }
    
    private def commandForChar(char: Char): (Robot => Robot) = char match {
        case 'L' => ifNotLost(left)
        case 'R' => ifNotLost(right)
        case 'F' => ifNotLost(forward)
    }

    // qq This looks like it could be converted to a monadic approach, which would be an interesting experiment.
    private def ifNotLost(func: (HappyRobot => Robot))(robot: Robot) = robot match {
        case happy: HappyRobot => func(happy)
        case lost: LostRobot => lost
    }
    
    private def left(robot: HappyRobot) = robot.copy(facing = leftTurnMap(robot.facing));
    
    private def right(robot: HappyRobot) = robot.copy(facing = rightTurnMap(robot.facing));
    
    private def forward(robot: HappyRobot) = {
        val potentialNewSituation = robot.facing match {
            case North => HappyRobot(robot.x, robot.y + 1, robot.facing)
            case East => HappyRobot(robot.x + 1, robot.y, robot.facing)
            case South => HappyRobot(robot.x, robot.y - 1, robot.facing)
            case West => HappyRobot(robot.x - 1, robot.y, robot.facing)
        }
        
        def inBounds(r: Robot, maxX: Int, maxY: Int): Boolean = {
            r.x >= 0 && r.x <= maxX && r.y >= 0 && r.y <= maxY
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