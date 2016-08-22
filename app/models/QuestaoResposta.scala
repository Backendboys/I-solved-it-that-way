package models

import scala.collection.immutable.List

case class QuestaoResposta(questao: Questao, resposta: List[Resposta]) {
  def enviado: Boolean = resposta.length > 0
  def estado: String = resposta(0).estado
}
