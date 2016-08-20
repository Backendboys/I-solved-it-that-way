package forms

import play.api.data._
import play.api.data.Forms._

case class LoginData(role: String)

object LoginForm {

  val form = Form(
    mapping(
      "role" -> text
    )(LoginData.apply)(LoginData.unapply)
  )
}
