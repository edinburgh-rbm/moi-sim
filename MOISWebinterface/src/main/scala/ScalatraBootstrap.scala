/*
 * Authors: 
 * - Dominik Bucher, ETH Zurich, Github: dominikbucher
 */

import akka.actor._
import ed.mois.web._
import org.scalatra._
import javax.servlet.ServletContext
//import ed.mois.core.comm._
//import ed.mois.core.sim.Simulator

class ScalatraBootstrap extends LifeCycle {
  //val simulator = Simulator.system.actorOf(Props(new SimulatorRefImpl1(25)), name = "simulator")

  override def init(context: ServletContext) {
    context.setInitParameter("useFileMappedBuffer", "false")
    // Set to production mode
    //context.initParameters("org.scalatra.environment") = "production"
    context.mount(new IMSControlServlet, "/*")
  }

/*
 * XXX Where has Simulator gone? Disappeared after merging of rhz tree
  override def destroy(context: ServletContext) {
    Simulator.system.shutdown()
  }
  */
}
