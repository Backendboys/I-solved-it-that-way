package models

import java.io.File
import scala.io.Source
import org.joda.time.DateTime


case class Resposta(
  val codigo: Long,
  var dataEnvio: DateTime = DateTime.now(),
  var dataCompilado: DateTime,
  var arquivo: File,
  var teste: Teste,
  var nomeAluno: String) {
    override def toString(): String = {
      val fonte = Source.fromFile(this.arquivo)
      val dados = try fonte.mkString finally fonte.close()
      return dados
    }
  }

