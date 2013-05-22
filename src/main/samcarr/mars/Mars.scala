package samcarr.mars

import scala.io.Source

object Mars {
    def main(args: Array[String]) {
        println("Welcome to Mars!")
        
        // Note that this will respect any line endings: /r, /n, /r/n.
        val lines = Source.fromFile("qq").getLines
    }
}