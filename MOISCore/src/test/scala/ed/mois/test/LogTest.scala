package ed.mois.test

import org.scalatest.FlatSpec
import ed.mois.core.util.Log

class LogTest extends FlatSpec {
  "logging" should "now be set up" in {
    Log.setup
  }
}


