package samcarr.mars

object OutputGenerator {
  
    def render(robots: Seq[Robot]): String = {
        val lines = robots.map {
            _ match {
              case HappyRobot(x, y, facing) => "%d %d %s".format(x, y, facing)
              case LostRobot(x, y, facing) => "%d %d %s LOST".format(x, y, facing)
            }
        }
        lines.mkString("\r\n")
    }
}