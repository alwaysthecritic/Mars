package samcarr.mars

case class Configuration(maxX:Int, maxY:Int, missions:List[Mission])
    
// Leave commands as a simple string (e.g. "LRRFFL") as it's easier to handle, debug and test.
case class Mission(start: Robot, commands:String)