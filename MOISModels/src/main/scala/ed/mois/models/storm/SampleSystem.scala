/*
 * Contains a sample system to show some storm simulator aspects.
 *
 * Authors: 
 * - Dominik Bucher, ETH Zurich, Github: dominikbucher
 */

package ed.mois.models.storm

import akka.actor._

import scala.concurrent._
import scala.concurrent.duration._

import ed.mois.core.storm.strategies._
import ed.mois.core.storm.adapter._
import ed.mois.core.storm._
import ed.mois.core.util.Log

object SampleSimRunner extends App {
  Log setup
  val sim = new StormSim {
    val model = new SampleModel
    // Override the default simulation strategy to use a smash strategy with debug output
    override val simulationStrategy = () => new DistrSimPosStepAdaptionStrategy(model, 50.0, 1.0)
  }

  sim.system.shutdown

  // val results = sim.runSim
  // Await.result(results, 60 seconds)
}

case class SampleState extends StormState[SampleState] {
  var r0 = field(500.0) >= 0.0
  var r1 = field(250.0) >= 0.0
  var r2 = field(250.0) >= 0.0
  var r3 = field("i")
  var r4 = field(true)
  var r5 = field(new Object)

  override def print = s"r0: $r0, r1: $r1, r2: $r2, r3: $r3, r4: $r4, r5: $r5"
}

/**
 * An example system that does random and differential stuff on the state. 
 * Used primarily for debugging and showing how strategies work.
 */
class SampleModel extends StormModel {
  type StateType = SampleState

  def REACT_CONST = 0.00003
  def PROB_CONST = 0.3

  val title = "A Sample System Modifying Several Things"
  val desc = "A Sample System to show some nice properties of the simulator."
  val authors = "Dominik Bucher"
  val contributors = "Dominik Bucher"

  lazy val stateVector = SampleState()
  //this addProcess(() => new MatlabP)
  val mp = new MatlabP
  mp.test
  mp.disconnect
  // this addProcess(() => new P1)
  // this addProcess(() => new P2)
  // this addProcess(() => new P3)
  // this addProcess(() => new P4)
  // this addProcess(() => new P5)
  // this addProcess(() => new P6)

  /*lazy val processes = Array(
    () => new P1, 
    () => new P2, 
    () => new P3, 
    () => new P4, 
    () => new P5, 
    () => new P6)*/

  import stateVector._
  override val observables = List(r0, r1, r2)
  def calcDependencies(st: SampleState) = {}

  class MatlabP extends StormProcess[SampleState] with MatlabAdapter[SampleState] {
    def name = "MatlabP"
    val copyPropIds = List(r0, r1)
    val functionName = "ExampleMatlabFunction"
  }

  class P1 extends StormProcess[SampleState] {
    def name = "P1"
    def evolve(state: SampleState, t: Double, dt: Double) {
      import state._

      val need = REACT_CONST * r0() * r1() * dt
      r0() -= need
      r1() -= need
      //println("S1: " + state.print)
    }
  }

  class P2 extends StormProcess[SampleState] {
    def name = "P2"
    def evolve(state: SampleState, t: Double, dt: Double) {
      import state._

      val need = REACT_CONST * r0() * r2() * dt
      r0() -= need
      r2() -= need
    }
  }

  class P3 extends StormProcess[SampleState] {
    def name = "P3"
    def evolve(state: SampleState, t: Double, dt: Double) {
      import state._

      val doIt = (PROB_CONST * dt >= math.random)
      if (doIt) r3() = "3"
      if (doIt) r4() = !r4()
    }
  }

  class P4 extends StormProcess[SampleState] {
    def name = "P4"
    def evolve(state: SampleState, t: Double, dt: Double) {
      import state._

      val doIt = (PROB_CONST * dt >= math.random)
      if (doIt) r3() = "4"
      if (doIt) r4() = !r4()
    }
  }

  class P5 extends StormProcess[SampleState] {
    def name = "P5"
    def evolve(state: SampleState, t: Double, dt: Double) {
      import state._

      val doIt = (PROB_CONST * dt >= math.random)
      if (doIt) r5() = new Object
    }
  }

  class P6 extends StormProcess[SampleState] {
    def name = "P6"
    def evolve(state: SampleState, t: Double, dt: Double) {
      import state._

      val doIt = (PROB_CONST * dt >= math.random)
      if (doIt) r5() = "Hi:)"
    }
  }
}
