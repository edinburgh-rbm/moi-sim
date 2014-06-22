/*
 * Contains a Karrlike system to show some storm simulator aspects.
 *
 * Authors: 
 * - Dominik Bucher, ETH Zurich, Github: dominikbucher
 */

package ed.mois.models.storm.karrlike

import ed.mois.core.storm.strategies._
import ed.mois.core.storm._

/**
 * Model that describes an (empty) large model, comparable to the Karr model (though the Karr model 
 * uses even more variables, lots of which are not really necessary though).
 */
class KarrlikeModel extends StormModel {
  type StateType = KarrlikeState

  val title = "A Karrlike System Modifying Several Things"
  val desc = "A Karrlike System to show properties of the simulator."
  val authors = "Dominik Bucher"
  val contributors = "Dominik Bucher"

  lazy val stateVector = KarrlikeState()
  this addProcess(() => new P1)
  this addProcess(() => new P2)
  this addProcess(() => new P3)
  this addProcess(() => new P4)
  this addProcess(() => new P5)
  this addProcess(() => new P6)
  this addProcess(() => new P7)
  this addProcess(() => new P8)
  this addProcess(() => new P9)
  this addProcess(() => new P10)
  this addProcess(() => new P11)
  this addProcess(() => new P12)
  this addProcess(() => new P13)
  this addProcess(() => new P14)
  this addProcess(() => new P15)
  this addProcess(() => new P16)
  this addProcess(() => new P17)
  this addProcess(() => new P18)
  this addProcess(() => new P19)
  this addProcess(() => new P20)
  this addProcess(() => new P21)
  this addProcess(() => new P22)
  this addProcess(() => new P23)
  this addProcess(() => new P24)
  this addProcess(() => new P25)
  this addProcess(() => new P26)

  /*lazy val processes = Array(
    () => new P1, 
    () => new P2, 
    () => new P3, 
    () => new P4, 
    () => new P5, 
    () => new P6)*/

  import stateVector._
  override val observables = List(r1, r2)
  def calcDependencies(st: KarrlikeState) = {}
}
