/*
 * StormSim.scala
 *
 * This file contains the main entry point into the simulation machinery.
 * To use it in the simplest way, instantiate like this
 *
 *     val sim = new StormSim {
 *       val model = new SomeSortOfModel
 *     }
 *     val results = sim.runSim
 *     Await.result(results, 60 seconds)
 *     results onComplete {
 *       case Success(data) => {
 *         ... do something ...
 *       }
 *       case Failure(t) => {
 *         ... do something else ...
 *       }
 *     }
 *
 * Authors: 
 * - Dominik Bucher, ETH Zurich, Github: dominikbucher
 */

package ed.mois.core.storm

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.collection.immutable.TreeMap

// used for futures
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
// used for timeout
import scala.concurrent.duration._

import grizzled.slf4j.Logger

import ed.mois.core.storm.comm.RunSimulation
import ed.mois.core.storm.strategies.{SimulationStrategy, SmashStrategy}
import ed.mois.core.util.DataToFileWriter
import ed.mois.core.util.plot.s4gnuplot.Gnuplot

/**
 * Defines the outline of a Storm simulator. This class can be extended and filled in 
 * with the model, simulation and logging strategies. 
 */
abstract class StormSim {
  val DEF_MAX_TIME = 25.0
  val DEF_DELTA_TIME = 1.0
  val DEF_MIN_DELTA_TIME = 0.01
  val DEF_MAX_DELTA_TIME = 5.0

  /**
   * The simulation strategy defines how the model is simulated. There are various
   * strategies aiming for accuracy, simulation speed, ...
   */
  val simulationStrategy: () => SimulationStrategy = () => new SmashStrategy(DEF_MAX_TIME, DEF_DELTA_TIME)
  /**
   * The logging strategy tells the simulator what to do with the generated data.
   * It defaults to a simple file writer that writes stuff to "data.out" in a csv manner.
   */
  val loggingStrategy = new DataToFileWriter

  /**
   * Tells the simulator if gnu plot should be called in the end to plot observables. 
   */
  val printGnu = true

  /** 
   * Tells the simulator if data should be written to disk.
   */
  val writeData = true

  /**
   * The filename of the output data file. 
   */
  val fileName = "data.dat"

  /**
   * Overwrite to define the model to be simulated.
   */
  val model: StormModel
  // The actor system is used for distributed simulators. It is set up in the simulator
  // and shut down after the completion of the simulation.
  val system = ActorSystem("system")

  // Implicit timeout for dealing with actors and waiting for their results
  implicit val timeout = Timeout(360 seconds)

  val logger = Logger[this.type]

  /**
   * Main function that runs a simulation given the model and strategies.
   */
  def runSim: Future[TreeMap[Double, StormState[_]]] = {
    val initTime = System.currentTimeMillis
    logger.info(s"""Running simulation '${model.title}'""")

    // Create a future holding the simulation results
    val results: Future[TreeMap[Double, StormState[_]]] = {
      val result = system.actorOf(Props(simulationStrategy)) ? RunSimulation(model)
      result.mapTo[TreeMap[Double, StormState[_]]]
    } 

    // On completion of the simulation, display some statistics, do logging and graphing.
    results onSuccess {
      case endSim => {
        // Statistics
        val time = System.currentTimeMillis - initTime
        logger.info(s"""Simulation '${model.title}' took ${time.toDouble / 1000} seconds and resulted in ${endSim.size} data vectors of size ${endSim.head._2.fields.size}.""")

        // Logging
        if (!endSim.isEmpty && writeData) {
	  logger.info(s"""Writing simulation data to ${fileName}""")
          loggingStrategy.writeStormData(fileName, model, endSim)
        }

        // Observables
        val observables = model.observables
        if (!observables.isEmpty && printGnu) {
          logger.info("""Plotting data""")

          val gnuData = endSim.map(es => Seq(es._1) ++
            observables.map(o => es._2.fields(o.id).asInstanceOf[Double])).toSeq

          var plot = Gnuplot.newPlot
          .data(gnuData)
          .title(model.title)
          .xlabel("t")
          .ylabel("s(t)")
          .gridOn
          observables.zipWithIndex.foreach {
            case (o, i) =>
            plot = plot.line(_.title(model.stateVector.fieldNames(o.id)).using("1:" + (i + 2)).style("pt 7 ps 0.8"))
          }
          plot.plot
        }

	logger.info("""Done. Shutting down Actors.""")

        // Shut down system (in case it was even used...)
        system.shutdown

	logger.info("""Actors shut down.""")
      }

    }

    // Return future of results (the caller of the simulator can thus still modify the results further)
    results
  }
}
