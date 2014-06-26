package ed.mois.test

import org.scalatest.FlatSpec
import ed.mois.Process

class ProcessTest extends FlatSpec {
  val f = (hello1: Double, foo2: Double) => {
    (hello1, foo2)
  }

  object p extends Process("p") {
    val x1 = param(0.1)
    val x2 = param(0.2)
    val x3 = param(true)
    def step(t: Double, dt: Double) {
      x1() = x1() * x2()
    }
  }

  object q extends Process("q") {
    val x1 = param(1000.0)
    val privatething = 3
    def step(t: Double, dt: Double) {
    }
  }

//  runSim(<iv>,  p, q, r)

  "something" should "succeed" in {
    println(s"$p")
    val dx = p._step(0, 0)
    println(s"$p")
    println(s"$dx")
  }
}
