package samcarr.mars

case class Configuration(maxX:Int, maxY:Int, missions:Seq[Mission])
    
// Leave commands as a simple string (e.g. "LRRFFL") as it's easier to handle, debug and test.
case class Mission(startSituation: RobotSituation, commands:String)