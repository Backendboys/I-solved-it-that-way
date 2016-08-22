package forms

import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import org.joda.time.DateTime

import models.{ Questao, Resposta }

case class RespostaData(conteudo: String, compilador: String)

object RespostaForm {
  val form = Form(
    mapping(
      "conteudo" -> nonEmptyText,
      "compilador" -> nonEmptyText
    )(RespostaData.apply)(RespostaData.unapply)
  )
}
