package rot

import org.scalajs.dom.{Event, Node}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

@JSName("ROT.Display")
@js.native
class Display(opts: js.Object) extends js.Object {
  def clear(): Unit = js.native

  @JSName("getOptions")
  def options(): DisplayOptions = js.native

  @JSName("setOptions")
  def options_=:(opts: js.Object): Unit = js.native

  @JSName("getContainer")
  def container(): Node = js.native

  def eventToPosition(event: Event): (Int, Int) = js.native

  def draw(x: Int, y: Int, ch: String, fg: String = "", bg: String = ""): Unit = js.native

  def drawText(x: Int, y: Int, text: String, maxWidth: Int = 0): Unit = js.native
}
