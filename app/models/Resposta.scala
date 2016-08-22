package models

import java.io.File
import scala.io.Source
import org.joda.time.DateTime

import compilers.CompiladorFabrica


case class Resposta(
  val codigo: Long,
  val compilador: String,
  var estado: String,
  var dataEnvio: DateTime = DateTime.now(),
  var dataCompilado: Option[DateTime],
  var arquivo: File,
  var questao: Questao,
  var nomeAluno: String) {
    override def toString(): String = {
      val fonte = Source.fromFile(this.arquivo)
      val dados = try fonte.mkString finally fonte.close()
      return dados
    }

    def compare(teste: String): Boolean = {

      val dados = CompiladorFabrica(this.compilador).compilar(this.arquivo)

      this.dataCompilado = Some(DateTime.now())

      if (teste.equals(dados)) {
        this.estado = "."
        return true
      }

      if (teste.equalsIgnoreCase(dados)) {
        this.estado = "C"
        return false
      }

      val teste_ws = teste.replaceAll("\\s+", "")
      val dados_ws = dados.replaceAll("\\s+", "")

      if (teste_ws.equals(dados_ws)) {
        this.estado = "S"
        return false
      }

      if (teste_ws.equalsIgnoreCase(dados_ws)) {
        this.estado = "CS"
        return false
      }

      this.estado = "E"
      return false

    }

    override def equals(that: Any): Boolean =
      that match {
        case that: String => this.compare(that)
        case that: Teste => this.compare(that.toString())
        case _ => false
      }
  }

