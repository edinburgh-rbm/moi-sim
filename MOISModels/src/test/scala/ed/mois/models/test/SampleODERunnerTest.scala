/*
 * Contains tests for the storm simulator.
 *
 * Authors: 
 * - Dominik Bucher, ETH Zurich, Github: dominikbucher
 */

package ed.mois.models.test

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

import grizzled.slf4j.Logger

import ed.mois.core.storm.StormSim 
import ed.mois.core.storm.strategies.IndepTimeScaleStrategy
import ed.mois.models.storm.SampleODEModel
import ed.mois.core.util.Log

import org.scalatest._

class SampleODEModelTest extends FlatSpec {
  "Sample ODE Model" should "run" in {
//  ignore should "sample ODE model" in {
    val logger = Logger[this.type]

    val sim = new StormSim {
      override val simulationStrategy = () => new IndepTimeScaleStrategy(50.0, 0.05)
      val model = new SampleODEModel
      override val writeData = false
      override val printGnu = false
    }
    val results = sim.runSim
    Await.result(results, 60 seconds)

    results onComplete {
      case Success(data) => {
	assert(data.size == 1002)
	logger.info(s"last data ${data.last._2.fields}")
      }
      case Failure(t) => {
	throw t // should not happen
      }
    }
  }
}

