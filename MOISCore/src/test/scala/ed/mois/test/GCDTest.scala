package ed.mois.test

import org.scalatest.FlatSpec
import ed.mois.core.math.CMMath

class CMMathTest extends FlatSpec {

  "gcd" should "find the right gcd from two numbers" in {
    val t = new Object with CMMath

    assert(t.gcd(12,9) == 3)
    assert(t.gcd(9,12) == 3)
    assert(t.gcd(5,2) == 1)
    assert(t.gcd(6,3) == 3)
    assert(t.gcd(10,15) == 5)
  }


  "gcd" should "find the right gcd from a list of numbers" in {
    val t = new Object with CMMath

    assert(t.gcd(Array(10, 15, 20)) == 5)
    assert(t.gcd(Array(6, 4, 30, 12)) == 2)
  }

  "someone" should "explain why gcd is a mix-in class and not a simple function" in {
  }  
}


