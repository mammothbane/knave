package rot

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSName}

@js.native
@JSGlobal("ROT.RNG")
object RNG extends RNG {}

@js.native
trait RNG extends js.Object {
  def getSeed(): Double = js.native

  def setSeed(s: Double): Unit = js.native

  def getState(): js.Dynamic = js.native

  def setState(s: js.Dynamic): Unit = js.native

  @JSName("getUniform")
  def uniform(): Double = js.native

  @JSName("getUniformInt")
  def int(lower: Int, upper: Int): Int = js.native

  @JSName("getNormal")
  def normal(): Double = js.native

  @JSName("getPercentage")
  def percent(): Int = js.native

  override def clone(): RNG = js.native
}
