package ed.mois.core.storm.adapter

import scala.collection.mutable.Map
import grizzled.slf4j.Logger

import uk.ac.ed.inf.mois.{Delta, Key, Process, State, Var}
import ed.mois.core.storm.{StormField, StormModel, StormProcess, StormSim, StormState}

case class ProcessModelState extends StormState[ProcessModelState] {
  var newStyleState: State = new State

  var var2field = Map.empty[Key, Int]
  var fieldNames_ = Map.empty[Int, String]
  override lazy val fieldNames = fieldNames_.toMap

  def createStormFields() {
    for ((_, v) <- newStyleState) {
      val name = (v.scope + "|" + v.identifier)
      val f = field(v.value)
      fieldNames_ += (f.id -> name)
      var2field += (v.key -> f.id)
    }
  }

  def setProcessState(state: State) {
    def setVar[T](r: Var[T], v: Any) {
      r := v.asInstanceOf[T]
    }
    for ((k, v) <- state) {
      setVar(v, fields(var2field(k)))
    }
  }

  def setStormState(state: State) {
    for ((k, v) <- state) {
      fields(var2field(k)) = v.value
    }
  }

  override def dupl = {
    setStormState(newStyleState)
    val ns = this.copy
    ns.newStyleState = newStyleState
    ns.fieldPtrs = fieldPtrs
    ns.fieldNames_ = fieldNames_
    ns.var2field = var2field
    for ((i,v) <- fields) {
      ns.fields(i) = v
    }
    ns
  }
}

class ProcessModel extends StormModel {
  type StateType = ProcessModelState


  val title = "ProcessModel Wrapper"
  val authors = "Edinburgh RBM"
  val contributors = ""
  val desc = "Wrapper for new-style processes"

  val logger = Logger[this.type]


  lazy val stateVector = new ProcessModelState

  def addProcess(proc: Process) {

    object wrapper extends StormProcess[ProcessModelState] {
      val name = proc.name
      def evolve(ss: ProcessModelState, t: Double, tau: Double) {
	ss.setProcessState(proc.state)
	proc(t, tau)
	ss.setStormState(proc.state)
      }
    }

    /* add process state to our new style state dictionary */
    for ((k,v) <- proc.state) {
      if (stateVector.newStyleState.table contains k)
	proc.state += stateVector.newStyleState(k)
      else
	stateVector.newStyleState += v
    }

    wrapper.id = processes.length
    processes = processes :+ (() => wrapper)

  }

  def calcDependencies(st: ProcessModelState) = {}
}

class ProcessSim(name: String) extends StormSim {
  override val model = new ProcessModel {
    override val title = name
  }
  override val printGnu = false
  
  def +=(proc: Process) = model.addProcess(proc)

  def runProcesses = {
    model.stateVector.createStormFields
    runSim
  }
}
