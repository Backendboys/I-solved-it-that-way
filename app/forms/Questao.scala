package forms

import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import java.io.File

import models.{ Questao, Teste }

object QuestaoForm {
  val form = Form(
    mapping(
      "codigo" -> ignored(-1L),
      "enunciado" -> nonEmptyText,
      "descricao" -> text
    )(Questao.apply)(Questao.unapply)
  )
}
