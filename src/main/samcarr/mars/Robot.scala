package samcarr.mars

import Direction._

sealed abstract class Robot {
    val x: Int
    val y: Int
    val facing: Direction.Value
}

case class LostRobot(val x: Int, val y: Int, val facing: Direction.Value) extends Robot

case class HappyRobot(val x: Int, val y: Int, val facing: Direction.Value) extends Robot {
    private val leftTurnMap = Map(North -> West, East -> North, South -> East, West -> South)
    private val rightTurnMap = Map(North -> East, East -> South, South -> West, West -> North)
        
    def left() = copy(facing = leftTurnMap(facing));
    
    def right() = copy(facing = rightTurnMap(facing));
    
    def forward() = facing match {
        case North => HappyRobot(x, y + 1, facing)
        case East => HappyRobot(x + 1, y, facing)
        case South => HappyRobot(x, y - 1, facing)
        case West => HappyRobot(x - 1, y, facing)
    }
}
