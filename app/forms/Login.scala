package forms

import play.api.data._
import play.api.data.Forms._

case class LoginData(nome: String, role: String)

object LoginForm {

  val form = Form(
    mapping(
      "nome" -> nonEmptyText,
      "role" -> nonEmptyText
    )(LoginData.apply)(LoginData.unapply)
  )
}
