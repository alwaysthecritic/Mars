package samcarr.mars

import scala.collection.mutable
import Direction._

// qq Uses as functional an approach as it can muster - partly as an experiment to see how it
// pans out!
class MarsMissionRunner(config: Configuration) {
    
    // Use a set to keep track of grid positions where a robot has left a scent, on the presumption
    // that scented positions are sparse - certainly only at the edges of the grid.
    val scentMap = new mutable.HashSet[(Int, Int)]
    
    def runMissions(): Seq[RobotSituation] = {
        config.missions.map(runMission(_))
    }
    
    private def runMission(mission: Mission): RobotSituation = {
        val commands = mission.commands.map(robotCommandFunction(_))
        commands.foldLeft(mission.startSituation) { (situation, command) => command(situation) }
    }
    
    private def robotCommandFunction(command: Char): (RobotSituation => RobotSituation) = {
        command match {
            case 'L' => leftCommand
            case 'R' => rightCommand
            case 'F' => forwardCommand
        }
    }
    
    // qq Move elsewhere? Maybe better off here for locality of reference.
    // qq Find a way to get commands statically typed in a descriptive way: type alias?
    // qq Perhaps make this a pure function by passing the scentMap through too (immutable) so
    // returning a pair of sit and scentMap.
    // qq Need to represent whether robot was lost or not - perhaps in situation?
    //     Introduce new type to cover this, use pattern matching?
    private def leftCommand(situation: RobotSituation): RobotSituation = {
        situation.left()
    }
    
    // qq Could have done something clever with enum.id % 4, but simple case mapping is more robust and insulated.
    private def rightCommand(situation: RobotSituation): RobotSituation = {
        situation.right()
    }
      
    private def forwardCommand(situation: RobotSituation): RobotSituation = {
        val newSituation = situation.forward()
        
        // qq Use pattern matching or some othe functional approach to avoid nest ifs?
        //    That will probably work better as a data pipeline once scent map is being passed too.
        if (!newSituation.inBounds(config.maxX, config.maxY)) {
            // If previous position was scent-marked, simply ignore the forward command.
            if (scentMap(situation.x, situation.y)) {
                situation
            } else {
                scentMap.add((situation.x, situation.y))
                situation.lost()
            }
        } else {
            newSituation
        }
    }
}