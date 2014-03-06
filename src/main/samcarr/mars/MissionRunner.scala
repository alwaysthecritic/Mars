package samcarr.mars

import scala.collection.mutable
import Direction._

class MissionRunner(config: Configuration) {
    
    type ARobot = Either[LostRobot, HappyRobot]
    
    // Use a set to keep track of grid positions where a robot has left a scent, on the presumption
    // that scented positions are sparse - certainly only at the edges of the grid.
    val scentMap = new mutable.HashSet[(Int, Int)]

    def runMissions(): Seq[Robot] = {
        config.missions.map { mission =>
            val commands = mission.commands.map(commandForChar(_))
            commands.foldLeft(Right(mission.initialRobot): ARobot) { (robot, command) => robot.right.flatMap(command) }
        }.map (_.merge)
    }
    
    private def commandForChar(char: Char): (HappyRobot => ARobot) = char match {
        case 'L' => (robot) => Right(robot.left())
        case 'R' => (robot) => Right(robot.right())
        case 'F' => attemptForward
    }
    
    private def attemptForward(robot: HappyRobot): ARobot = {       
        val forwardRobot = robot.forward
        
        if (!inBounds(forwardRobot)) {
            if (scentMap(robot.x, robot.y)) {
                // Previous position was scent-marked - simply ignore the forward command.
                Right(robot)
            } else {
                // No scent-mark, so our robot is now lost, but adds a scent-mark.
                scentMap.add((robot.x, robot.y))
                Left(LostRobot(robot.x, robot.y, robot.facing))
            }
        } else {
            Right(forwardRobot)
        }
    }
    
    private def inBounds(r: Robot) = r.x >= 0 && r.x <= config.maxX && r.y >= 0 && r.y <= config.maxY
}