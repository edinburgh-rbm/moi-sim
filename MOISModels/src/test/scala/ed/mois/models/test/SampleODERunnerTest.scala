/*
 * Contains tests for the storm simulator.
 *
 * Authors: 
 * - Dominik Bucher, ETH Zurich, Github: dominikbucher
 */

package ed.mois.models.test

import scala.concurrent.Await
import scala.concurrent.duration._
import org.scalatest.FlatSpec
import ed.mois.core.storm.StormSim 
import ed.mois.core.storm.strategies.IndepTimeScaleStrategy
import ed.mois.models.storm.SampleODEModel

class SampleODEModelTest extends FlatSpec {
  "Sample ODE Model" should "run" in {
    val sim = new StormSim {
      override val simulationStrategy = () => new IndepTimeScaleStrategy(50.0, 0.05) {override val debug = false}
      val model = new SampleODEModel
    }
    val results = sim.runSim
    Await.result(results, 60 seconds)
  }
}

