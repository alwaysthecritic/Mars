package samcarr.mars

case class Configuration(maxX:Int, maxY:Int, missions:Seq[Mission])
    
// Leave commands as a simple string (e.g. "LRRFFL") as it's easier to handle and debug.
case class Mission(startX:Int, startY:Int, facing:Direction.Value, commands:String)

object Direction extends Enumeration {
  val North = Value("N")
  val South = Value("S")
  val East = Value("E")
  val West = Value("W")
}