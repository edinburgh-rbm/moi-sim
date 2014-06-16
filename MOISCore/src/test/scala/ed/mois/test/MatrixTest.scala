/*
 * Contains tests for the simulator.
 *
 * Authors: 
 * - Dominik Bucher, ETH Zurich, Github: dominikbucher
 */

package ed.mois.test

import org.scalatest.FlatSpec
import ed.mois.core.math.AnyMatrix

class MatrixTest extends FlatSpec {

  "AnyMatrix" should "remove rows nicely" in {
    var matr = new AnyMatrix(5, 5)
    for {
      i <- 0 until matr.nRows
      j <- 0 until matr.nCols
    } {
      matr(i, j) = i + j
    }
    println(matr.toString)
    matr.fields = matr.removeRows(List(2, 3))
    println(matr.toString)
  }

  "AnyMatrix" should "remove columns nicely" in {
    var matr = new AnyMatrix(5, 5)
    for {
      i <- 0 until matr.nRows
      j <- 0 until matr.nCols
    } {
      matr(i, j) = i + j
    }
    println(matr.toString)
    matr.fields = matr.removeCols(List(2, 3))
    println(matr.toString)
  }
}

