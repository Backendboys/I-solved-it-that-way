package models

import sys.process._
import scala.io.Source
import java.io.File

case class Teste(val codigo: Long, val arquivo: File,
  val questao: Questao) {

  override def toString(): String = {
      val fonte = Source.fromFile(this.arquivo)
      val dados = try fonte.mkString finally fonte.close()
      return dados
    }

}
