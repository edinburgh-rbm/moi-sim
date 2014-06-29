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

import ed.mois.core.storm.adapter.ProcessSim
import ed.mois.core.storm.strategies.IndepTimeScaleStrategy
import ed.mois.core.util.Log

import uk.ac.ed.inf.mois.{Process, ProcessODE}
import uk.ac.ed.inf.mois.Conversions._

import org.scalatest._

/*
 * Directly copied ODE system from Dominik's stuff
 */ 
object sampleEuler1 extends Process("sampleEuler1") {
  val x1 = Var(25.0, "ex:x1")
  val x2 = Var(50.0, "ex:x2")

  def step(t: Double, tau: Double) {
    x1 += (-0.3*x1 - 0.4*x2) * tau
  }
}

object sampleEuler2 extends Process("sampleEuler2") {
  val x1 = Var(25.0, "ex:x1")
  val x2 = Var(50.0, "ex:x2")

  def step(t: Double, tau: Double) {
    x2 += (-0.5*x1 - 0.8*x2) * tau
  }
}

/*
 * Version of same that does not use Euler's method and instead
 * uses whatever the apache commons math suite says is best
 */ 
object sampleApache1 extends ProcessODE("sampleApache1") {
  integral(
    Var(25.0, "ex:x1")
  )
  val x2 = Var(50.0, "ex:x2")
  
  def computeDerivatives(t: Double, y: Array[Double], ẏ: Array[Double]) {
    ẏ(0) = -0.3*y(0) - 0.4*x2
  }
}

object sampleApache2 extends ProcessODE("sampleApache2") {
  val x1 = Var(25.0, "ex:x1")
  integral(
    Var(50.0, "ex:x2")
  )

  def computeDerivatives(t: Double, y: Array[Double], ẏ: Array[Double]) {
    ẏ(0) = -0.5*x1 - 0.8*y(0)
  }
}

/*
 * Run the two systems, and check results
 */
class ProcessModelTest extends FlatSpec {
  Log.setup
  Log.debug(false)
  
  val logger = Logger[this.type]

  "Sample ODE Model (Euler)" should "give the same results as Dominik's" in {
    val sim = new ProcessSim("Sample ODE Model (Euler)") {
      override val simulationStrategy = () => new IndepTimeScaleStrategy(50, 0.05)
      override val writeData = false
    }
    sim += sampleEuler1
    sim += sampleEuler2

    val results = sim.runProcesses
    Await.result(results, 600 seconds)
    
    results onComplete {
      case Success(data) => {
	//assert(data.size == 1002)
	logger.info(s"Euler last data:  ${data.last._2.fields}")
	logger.info(s"Euler last state: ${sim.model.stateVector.newStyleState}")
      }
      case Failure(t) => {
	throw t // should not happen
      }
    }
  }

  "Sample ODE Model (Apache)" should "give similar results to Dominik's" in {
    val sim = new ProcessSim("Sample ODE Model (Apache)") {
      override val simulationStrategy = () => new IndepTimeScaleStrategy(50, 0.05)
      override val writeData = false
    }
    sim += sampleApache1
    sim += sampleApache2

    val results = sim.runProcesses
    Await.result(results, 600 seconds)

    results onComplete {
      case Success(data) => {
	//assert(data.size == 1002)
	logger.info(s"Apache data:  ${data.last._2.fields}")
	logger.info(s"Apache state: ${sim.model.stateVector.newStyleState}")
      }
      case Failure(t) => {
	throw t // should not happen
      }
    }
  }
}

