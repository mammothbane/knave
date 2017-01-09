package rot

import scala.scalajs.js

trait DisplayOptions extends js.Object {
  val width: Int = js.native
  val height: Int = js.native
  val fg: String = js.native
  val bg: String = js.native
  val transpose: Boolean = js.native
  val fontSize: Int = js.native
  val fontFamily: String = js.native
  val fontStyle: String = js.native
  val spacing: Float = js.native
  val border: Int = js.native
  val layout: String = js.native
  val forceSquareAspectRatio: Boolean = js.native
  val tileWidth: Int = js.native
  val tileHeight: Int = js.native
  val tileMap: js.Object = js.native
  val tileSet: js.Any = js.native
  val tileColorize: Boolean = js.native
  val termColor: String = js.native
}
