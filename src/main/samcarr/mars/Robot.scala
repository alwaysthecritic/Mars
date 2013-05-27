package samcarr.mars

import scala.collection.mutable
import Direction._

// qq should these be vals? How does subclassing handle the fields?
sealed abstract class Robot(val x: Int, val y: Int, val facing: Direction.Value)

// qq are all these override vals really the best way?
//    perhaps ADTs are not really supposed to have shared state like this?
case class HappyRobot(override val x: Int, override val y: Int, override val facing: Direction.Value) extends Robot(x, y, facing)

case class LostRobot(override val x: Int, override val y: Int, override val facing: Direction.Value) extends Robot(x, y, facing)