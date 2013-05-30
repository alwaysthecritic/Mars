package samcarr.mars

import Direction._

// Algebraic data type.
sealed abstract class Robot {
    val x: Int
    val y: Int
    val facing: Direction.Value
}

case class HappyRobot(val x: Int, val y: Int, val facing: Direction.Value) extends Robot
case class LostRobot(val x: Int, val y: Int, val facing: Direction.Value) extends Robot
