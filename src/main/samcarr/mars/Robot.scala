package samcarr.mars

import Direction._

case class Robot(val x: Int, val y: Int, val facing: Direction.Value) {
    private val leftTurnMap = Map(North -> West, East -> North, South -> East, West -> South)
    private val rightTurnMap = Map(North -> East, East -> South, South -> West, West -> North)
        
    def left() = copy(facing = leftTurnMap(facing));
    
    def right() = copy(facing = rightTurnMap(facing));
    
    def forward() = facing match {
        case North => Robot(x, y + 1, facing)
        case East => Robot(x + 1, y, facing)
        case South => Robot(x, y - 1, facing)
        case West => Robot(x - 1, y, facing)
    }
}
