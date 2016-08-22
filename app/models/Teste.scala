package models

import sys.process._
import scala.io.Source
import java.io.File

case class Teste(val codigo: Long, val arquivo: File, var estado: String,
  val questao: Questao) {

  override def toString(): String = estado;

  def compare(_resposta: String): Boolean = {

    val resposta = _resposta + "\n"

    val fonte = Source.fromFile(this.arquivo)
    val dados = try fonte.mkString finally fonte.close()

    if (resposta.equals(dados)) {
      this.estado = "."
      return true
    }

    if (resposta.equalsIgnoreCase(dados)) {
      this.estado = "C"
      return false
    }

    val resposta_ws = resposta.replaceAll("\\s+", "")
    val dados_ws = dados.replaceAll("\\s+", "")

    if (resposta_ws.equals(dados_ws)) {
      this.estado = "S"
      return false
    }

    if (resposta_ws.equalsIgnoreCase(dados_ws)) {
      this.estado = "CS"
      return false
    }

    this.estado = "E"
    return false

  }

  override def equals(that: Any): Boolean =
    that match {
      case that: String => this.compare(that)
      case _ => false
    }
}
