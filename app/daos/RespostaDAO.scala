package daos

import play.api._
import play.api.db._
import scala.collection.immutable.List
import java.sql.Statement
import java.io.File
import java.sql.Timestamp
import org.joda.time.DateTime

import models.{ Resposta, Questao }

class RespostaDAO(db: Database) {

  def list(questao_codigo: Option[Long] = None): List[Resposta] = {
    var respostas: List[Resposta] = List()
    val query = questao_codigo match {
      case Some(value) => f"SELECT * FROM Resposta WHERE questao_codigo = $value"
      case None => "SELECT * FROM Resposta"
    }
    val conn = db.getConnection()

    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(query)

      while(rs.next()) {
        val codigo: Long = rs.getLong("codigo")
        val compilador: String = rs.getString("compilador")
        val estado: String = rs.getString("estado")
        var dataEnvio: DateTime = new DateTime(
          rs.getTimestamp("dataEnvio").getTime())
        var dataCompiladoStamp =
          rs.getTimestamp("dataCompilado")
        var dataCompilado: Option[DateTime] = None
        if (!rs.wasNull()) {
          dataCompilado = Some(new DateTime(
            dataCompiladoStamp.getTime()))
        }
        val arquivo: File = new File(rs.getString("arquivo"))
        val nomeAluno: String = rs.getString("nomeAluno")
        val questao_codigo: Long = rs.getLong("questao_codigo")
        val questao = new QuestaoDAO(db).get(questao_codigo)

        respostas = new Resposta(codigo, compilador, estado, dataEnvio,
          dataCompilado, arquivo, questao, nomeAluno) :: respostas
      }
    } finally {
      conn.close()
    }

    return respostas
  }

  def insert(compilador: String, estado: String, dataEnvio: DateTime,
      dataCompilado: Option[DateTime], arquivo: File,
      questao: Questao, nomeAluno: String): Option[Resposta] = {
    val dataEnvioTimestamp = new Timestamp(dataEnvio.getMillis())
    var dataCompiladoVStr = ""
    var dataCompiladoCStr = ""
    var dataCompiladoTimestamp: Timestamp = null
    dataCompilado match {
      case Some(date) => {
        dataCompiladoCStr = "dataCompilado, "
        dataCompiladoVStr = "?, "
        dataCompiladoTimestamp = new Timestamp(date.getMillis())
      }
      case None =>
    }
    val query = "INSERT INTO Resposta(compilador, estado, dataEnvio, " +
      f"$dataCompiladoCStr arquivo, questao_codigo, nomeAluno) VALUES " +
      f"(?, '$estado', ?, $dataCompiladoVStr '${arquivo.getPath()}', '${questao.codigo}', ?)"
    val conn = db.getConnection()

    var resposta: Option[Resposta] = None

    try {
      val stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
      stmt.setString(1, compilador)
      stmt.setTimestamp(2, dataEnvioTimestamp)
      dataCompilado match {
        case Some(date) => {
          stmt.setTimestamp(3, dataCompiladoTimestamp)
          stmt.setString(4, nomeAluno)
        }
        case None => stmt.setString(3, nomeAluno)
      }
      stmt.executeUpdate()
      val rs = stmt.getGeneratedKeys()
      while(rs.next()) {
        val codigo = rs.getInt(1)
        resposta = Some(new Resposta(codigo, compilador, estado, dataEnvio,
          dataCompilado, arquivo, questao, nomeAluno))
      }
    } finally {
      conn.close()
    }

    return resposta
  }

  def update(instance: Resposta): Option[Resposta] = {
    val dataEnvioTimestamp = new Timestamp(instance.dataEnvio.getMillis())
    var dataCompiladoVStr = ""
    var dataCompiladoCStr = ""
    var dataCompiladoTimestamp: Timestamp = null
    instance.dataCompilado match {
      case Some(date) => {
        dataCompiladoCStr = "dataCompilado, "
        dataCompiladoVStr = "?, "
        dataCompiladoTimestamp = new Timestamp(date.getMillis())
      }
      case None =>
    }
    val query = "UPDATE Resposta SET (compilador, estado, dataEnvio, " +
      f"$dataCompiladoCStr arquivo, questao_codigo, nomeAluno) = " +
      f"(?, ?, ?, $dataCompiladoVStr ?, ?, ?) WHERE codigo = ${instance.codigo}"
    val conn = db.getConnection()

    var resposta: Option[Resposta] = None

    try {
      val stmt = conn.prepareStatement(query)
      stmt.setString(1, instance.compilador)
      stmt.setString(2, instance.estado)
      stmt.setTimestamp(3, dataEnvioTimestamp)
      instance.dataCompilado match {
        case Some(date) => {
          stmt.setTimestamp(4, dataCompiladoTimestamp)
          stmt.setString(5, instance.arquivo.getPath())
          stmt.setLong(6, instance.questao.codigo)
          stmt.setString(7, instance.nomeAluno)
        }
        case None => {
          stmt.setString(4, instance.arquivo.getPath())
          stmt.setLong(5, instance.questao.codigo)
          stmt.setString(6, instance.nomeAluno)
        }
      }
      val rs = stmt.executeUpdate()
      return Some(instance)
    } finally {
      conn.close()
    }

    return None
  }



  def count(questao_codigo: Option[Long] = None): Int = {
    var total = 0
    val query = questao_codigo match {
      case Some(value) => "SELECT count(codigo) AS total FROM Resposta WHERE " +
      f"questao_codigo = $value"
      case None => "SELECT count(codigo) AS total FROM Resposta"
    }
    val conn = db.getConnection()

    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(query)

      while(rs.next()) {
        total = rs.getInt("total")
      }
    } finally {
      conn.close()
    }

    return total
  }

  def get(codigo: Long): Resposta = {
    var resposta: Resposta = null
    val query = f"SELECT * FROM Resposta WHERE codigo = $codigo"
    val conn = db.getConnection()

    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(query)
      rs.next()

      val codigo: Long = rs.getLong("codigo")
      val compilador: String = rs.getString("compilador")
      val estado: String = rs.getString("estado")
      var dataEnvio: DateTime = new DateTime(
        rs.getTimestamp("dataEnvio").getTime())
      var dataCompiladoStamp =
          rs.getTimestamp("dataCompilado")
      var dataCompilado: Option[DateTime] = None
      if (!rs.wasNull()) {
        dataCompilado = Some(new DateTime(
          dataCompiladoStamp.getTime()))
      }
      val arquivo: File = new File(rs.getString("arquivo"))
      val nomeAluno: String = rs.getString("nomeAluno")
      val questao_codigo: Long = rs.getLong("questao_codigo")
      val questao = new QuestaoDAO(db).get(questao_codigo)

      resposta = new Resposta(codigo, compilador, estado, dataEnvio,
        dataCompilado, arquivo, questao, nomeAluno)

      return resposta
    }
  }

}
