package samcarr.mars

import Direction._

// qq Find a better name?
// qq Move the left, right, forward logic into here. Lost robot can then ignore commands and not alter its pos.
//    Have a subclass that once lost does nothing more.
case class RobotSituation(val x: Int, val y: Int, val facing: Direction.Value) {
    
    def inBounds(maxX: Int, maxY: Int): Boolean = {
        x >= 0 && x <= maxX && y >= 0 && y <= maxY
    }
    
    def left(): RobotSituation = {
        val newDirection = facing match {
            case North => West
            case East => North
            case South => East
            case West => South
        }
        RobotSituation(x, y, newDirection)
    }
    
    def right(): RobotSituation = {
        val newDirection = facing match {
            case North => East
            case East => South
            case South => West
            case West => North
        }
        RobotSituation(x, y, newDirection)
    }
    
    def forward(): RobotSituation = {
        facing match {
            case North => RobotSituation(x, y + 1, facing)
            case East => RobotSituation(x + 1, y, facing)
            case South => RobotSituation(x, y - 1, facing)
            case West => RobotSituation(x - 1, y, facing)
        }
    }
    
    def lost(): RobotSituation = new LostRobotSituation(x, y, facing)
}

// qq Is it OK to subclass a case like this?
//    Could have a single sealed super-class with two cases? Algebraic data types...
class LostRobotSituation(x: Int, y: Int, facing: Direction.Value) extends RobotSituation(x, y, facing) {
    override def left(): RobotSituation = { this }
    override def right(): RobotSituation = { this }
    override def forward(): RobotSituation = { this }
}
