package daos

import play.api._
import play.api.db._
import scala.collection.immutable.List
import java.sql.Statement
import java.io.File
import java.sql.Timestamp

import models.{ Resposta, Teste }

class RespostaDAO(db: Database) {

  def list(teste_codigo: Option[Long] = None): List[Resposta] = {
    var respostas: List[Resposta] = List()
    val query = teste_codigo match {
      case Some(value) => f"SELECT * FROM Resposta WHERE teste_codigo = $value"
      case None => "SELECT * FROM Resposta"
    }
    val conn = db.getConnection()

    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(query)

      while(rs.next()) {
        val codigo: Long = rs.getLong("codigo")
        var dataEnvio: DateTime = DateTime(
          rs.getTimestamp("dataEnvio").getTime())
        var dataCompilado: DateTime = DateTime(
          rs.getTimestamp("dataCompilado").getTime())
        val arquivo: File = new File(rs.getString("arquivo"))
        val nomeAluno: String = rs.getString("nomeAluno")
        val teste_codigo: Long = rs.getLong("teste_codigo")
        val teste = new TesteDAO(db).get(teste_codigo)

        respostas = new Resposta(codigo, dataEnvio, dataCompilado, arquivo, teste, nomeAluno) :: respostas
      }
    } finally {
      conn.close()
    }

    return respostas
  }

  def insert(dataEnvio: DateTime,
      dataCompilado: DateTime, arquivo: File,
      teste: Teste, nomeAluno: string): Option[Resposta] = {
    val dataEnvioTimestamp = new Timestamp(dataEnvio.getMillis())
    val dataCompiladoTimestamp = new Timestamp(dataCompilado.getMillis())
    val query = "INSERT INTO Resposta(dataEnvio, dataCompilado, arquivo" +
      "teste_codigo, nomeAluno) VALUES (?, ?, " +
      f"'${arquivo.getPath()}', '${teste.codigo}', '$nomeAluno'" + ")"
    val conn = db.getConnection()

    var resposta: Option[Resposta] = None

    try {
      val stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
      stmt.setTimestamp(1, dataEnvioTimestamp)
      stmt.setTimestamp(2, dataCompiladoTimestamp)
      stmt.executeUpdate()
      val rs = stmt.getGeneratedKeys()
      while(rs.next()) {
        val codigo = rs.getInt(1)
        resposta = Some(new Resposta(codigo, dataEnvio, dataCompilado,
          arquivo, teste, nomeAluno))
      }
    } finally {
      conn.close()
    }

    return resposta
  }

  def count(teste_codigo: Option[Long] = None): Int = {
    var total = 0
    val query = teste_codigo match {
      case Some(value) => "SELECT count(codigo) AS total FROM Resposta WHERE " +
      f"teste_codigo = $value"
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
      var dataEnvio: DateTime = DateTime(
        rs.getTimestamp("dataEnvio").getTime())
      var dataCompilado: DateTime = DateTime(
        rs.getTimestamp("dataCompilado").getTime())
      val arquivo: File = new File(rs.getString("arquivo"))
      val nomeAluno: String = rs.getString("nomeAluno")
      val teste_codigo: Long = rs.getLong("teste_codigo")
      val teste = new TesteDAO(db).get(teste_codigo)

      resposta = new Resposta(codigo, dataEnvio, dataCompilado, arquivo, teste, nomeAluno)

      return resposta
    }
  }

}
