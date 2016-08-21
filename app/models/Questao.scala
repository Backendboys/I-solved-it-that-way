package models

import scala.collection.immutable.List

case class Questao(
  val codigo: Long,
  var enunciado: String,
  var descricao: String)

