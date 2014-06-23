package ed.mois.test

import org.scalatest.FlatSpec
import ed.mois.core.storm.StormState

case class SST extends StormState[SST] {
  var x1 = field(0.0)
  var x2 = field(1.0)
  var x3 = field(0.0)
}


class StormStateTest extends FlatSpec {
  val sst = new SST
  ignore should "fields retrieve three fields" in {
    val fields = (for ((_, v) <- sst.fields) yield v).toList
    assert(fields.size == 3)
    assert(fields contains sst.x1)
    assert(fields contains sst.x2)
    assert(fields contains sst.x3)
  }

/*
  ignore should "return the field id" in {
    assert(sst.fieldId(sst.x1) == 0)
    assert(sst.fieldId(sst.x2) == 1)
    assert(sst.fieldId(sst.x3) == 2)
  }
*/

  "fieldNames" should "match the field names" in {
    val names = (for ((_,v) <- sst.fieldNames) yield v).toList
    assert(names.size == 3)
    assert(names contains "x1")
    assert(names contains "x2")
    assert(names contains "x3")
  }

/*
  "fieldNames" should "be correctly retrieved" in {
    assert(sst.fieldName(sst.x1) == "x1")
    assert(sst.fieldName(sst.x2) == "x2")
    assert(sst.fieldName(sst.x3) == "x3")
  }
*/

  "operations on numerical fields" should "work" in {
    sst.x1() += 0.4 * sst.x2()  
  }

  "dupl" should "duplicate a state" in {
    val n = sst.dupl

    assert(n.fields.size == 3)
    assert(sst.x1() == n.x1())
    assert(sst.x2() == n.x2())
    assert(sst.x3() == n.x3())
  }
}


