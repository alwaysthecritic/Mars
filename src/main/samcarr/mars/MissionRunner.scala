package samcarr.mars

import scala.collection.mutable
import Direction._

class MissionRunner(config: Configuration) {
    
    // Use a set to keep track of grid positions where a robot has left a scent, on the presumption
    // that scented positions are sparse - certainly only at the edges of the grid.
    val scentMap = new mutable.HashSet[(Int, Int)]

    def runMissions(): Seq[Robot] = {
        config.missions.map { mission =>
            val commands = mission.commands.map(commandForChar(_))
            commands.foldLeft(mission.start) { (robot, command) => command(robot) }
        }
    }
    
    private def commandForChar(char: Char): (Robot => Robot) = char match {
        case 'L' => ifNotLost(_.left)
        case 'R' => ifNotLost(_.right)
        case 'F' => ifNotLost(attemptForward)
    }

    // qq This looks like it could be converted to a monadic approach, which would be an interesting experiment.
    //    And maybe the scent map could be rolled into that too.
    private def ifNotLost(func: (HappyRobot => Robot))(robot: Robot) = robot match {
        case happy: HappyRobot => func(happy)
        case lost: LostRobot => lost
    }
    
    private def attemptForward(robot: HappyRobot) = {       
        val forwardRobot = robot.forward
        
        if (!inBounds(forwardRobot)) {
            if (scentMap(robot.x, robot.y)) {
                // Previous position was scent-marked - simply ignore the forward command.
                robot
            } else {
                // No scent-mark, so our robot is now lost, but adds a scent-mark.
                scentMap.add((robot.x, robot.y))
                LostRobot(robot.x, robot.y, robot.facing)
            }
        } else {
            forwardRobot
        }
    }
    
    private def inBounds(r: Robot) = r.x >= 0 && r.x <= config.maxX && r.y >= 0 && r.y <= config.maxY
}