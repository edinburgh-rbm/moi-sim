package ed.mois.core.util

import org.apache.log4j.BasicConfigurator
import org.apache.log4j.{Level, Logger}

object Log {
  def setup {
    BasicConfigurator.configure()
  }
  def debug(debug: Boolean) {
    val root = Logger.getRootLogger()
    if (debug)
      root.setLevel(Level.DEBUG)
    else 
      root.setLevel(Level.INFO)
  }
}
