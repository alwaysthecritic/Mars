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
        situation match {
           case happy @ HappyRobotSituation(x, y, facing) => left(happy)
           case lost @ LostRobotSituation(_,_,_) => lost
        }
    }
    
    private def left(situation: HappyRobotSituation): RobotSituation = {
        val newDirection = situation.facing match {
            case North => West
            case East => North
            case South => East
            case West => South
        }
        HappyRobotSituation(situation.x, situation.y, newDirection)
    }
    
    // qq Could have done something clever with enum.id % 4, but simple case mapping is more robust and insulated.
    private def rightCommand(situation: RobotSituation): RobotSituation = {
        situation match {
           case happy @ HappyRobotSituation(x, y, facing) => right(happy)
           case lost @ LostRobotSituation(_,_,_) => lost
        }
    }
    
    private def right(situation: HappyRobotSituation): RobotSituation = {
        val newDirection = situation.facing match {
            case North => East
            case East => South
            case South => West
            case West => North
        }
        HappyRobotSituation(situation.x, situation.y, newDirection)
    }
    
    // qq Would be nice to be monadic about this, so lost robot takes care of itself without
    //    explicit checks through the code.
    private def forwardCommand(situation: RobotSituation): RobotSituation = {
        situation match {
           case happy @ HappyRobotSituation(x, y, facing) => forward(happy)
           case lost @ LostRobotSituation(_,_,_) => lost
        }
    }
    
    private def forward(situation: HappyRobotSituation): RobotSituation = {
        val potentialNewSituation = situation.facing match {
            case North => HappyRobotSituation(situation.x, situation.y + 1, situation.facing)
            case East => HappyRobotSituation(situation.x + 1, situation.y, situation.facing)
            case South => HappyRobotSituation(situation.x, situation.y - 1, situation.facing)
            case West => HappyRobotSituation(situation.x - 1, situation.y, situation.facing)
        }
        
        def inBounds(sit: RobotSituation, maxX: Int, maxY: Int): Boolean = {
            sit.x >= 0 && sit.x <= maxX && sit.y >= 0 && sit.y <= maxY
        }
            
        // qq Use pattern matching or some other functional approach to avoid nested ifs?
        //    That will probably work better as a data pipeline once scent map is being passed immutable too.
        if (!inBounds(potentialNewSituation, config.maxX, config.maxY)) {
            // If previous position was scent-marked, simply ignore the forward command.
            if (scentMap(situation.x, situation.y)) {
                situation
            } else {
                scentMap.add((situation.x, situation.y))
                LostRobotSituation(situation.x, situation.y, situation.facing)
            }
        } else {
            potentialNewSituation
        }
    }
}