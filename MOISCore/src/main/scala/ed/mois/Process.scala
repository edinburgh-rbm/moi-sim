package ed.mois


class NamedParameter[T](var name: String, var value: T) {
  def apply(): T = value
  def setName(newName: String) = { name = newName; this }
  def update(newValue: T) = {
    value = newValue
  }
  override def clone() = new NamedParameter[T](name, value)
  override def toString = name + ": " + value.toString

  /*
   * Greater or equals than restriction.
   */
  var geq: Option[T] = None
  def >=(t: T) = {
    geq = Some(t)
    this
  }

  /*
   * Lesser or equals than restriction.
   */
  var leq: Option[T] = None
  def <=(t: T) = {
    leq = Some(t)
    this
  }

  /*
   * Provide an implementation of - for some common types
   */
  def -[T](t: T): T = {
    value match {
      case d: Double => {
	return new NamedParameter("d"+name, d - t.asInstanceOf[NamedParameter[Double]].value).asInstanceOf[T]
      }
      case i: Int => {
	return new NamedParameter("d"+name, i - t.asInstanceOf[NamedParameter[Int]].value).asInstanceOf[T]
      }
      case b: Boolean => {
	return new NamedParameter("d"+name, b != t.asInstanceOf[NamedParameter[Boolean]].value).asInstanceOf[T]
      }
      case a: Any => {
	return new NamedParameter("d"+name, a).asInstanceOf[T]
      }
    }
  }
}

abstract class Process(val name: String) {
  var _params = List[NamedParameter[_]]()

  def param[T](value: T): NamedParameter[T] = {
    val p = new NamedParameter[T]("", value)
    _params = _params :+ p
    p
  }

  lazy val params = {
    val paramDict = (Map[NamedParameter[_], String]() /: this.getClass.getDeclaredFields) { (a, p) =>
      p.setAccessible(true)
      if (p.get(this).isInstanceOf[NamedParameter[_]])
        a + (p.get(this).asInstanceOf[NamedParameter[_]] -> p.getName)
      else a
    }
    for (p <- _params) yield p.setName(paramDict(p))
  }

  override def toString = {
    name + "(" + params.mkString(", ") + ")"
  }

  def step(t: Double, dt: Double) 

  /*
   * A wrapper around the user defined step function to calculate changes
   */
  def _step(t: Double, dt: Double): List[NamedParameter[_]] = {
    val startValues = for (p <- params) yield p.clone()
    step(t, dt)
    val endValues = for (p <- params) yield p.clone()
    for ( (e,s) <- endValues zip startValues ) yield (e-s)
  }
}  
