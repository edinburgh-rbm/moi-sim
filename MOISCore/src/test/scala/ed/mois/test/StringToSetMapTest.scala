package ed.mois.test

import org.scalatest.FlatSpec
import ed.mois.core.util.StringToSetMap

class StringToSetMapTest extends FlatSpec {
  "stsm" should "throw NoSuchElementException with absent values" in {
    val s = StringToSetMap[Int]
    intercept[NoSuchElementException] {
      s("x")
    }
  }

  "stsm" should "stored the empty set if no value is given" in {
    val s = StringToSetMap[Int]

    s.add("x")
    assert(s("x").size == 0)
  }
   
  "stsm" should "return values that are given it" in {
    val s = StringToSetMap[Int]

    s.add("x", 1)
    val v = s("x")
    assert(v.size == 1)
    assert(v(1))

    s.add("x", 2)
    val u = s("x")
    assert(u.size == 2)
    assert(u(1) && u(2))
  }

  "stsm" should "remove values as required" in {
    val s = StringToSetMap[Int]

    s.add("x", 1)
    s.add("x", 2)
    s.add("x", 3)

    s.remove("x", 1)
    val v = s("x")
    assert(!v(1) && v(2) && v(3))

    s.remove("x")
    intercept[NoSuchElementException] {
      s("x")
    }
  }
}
