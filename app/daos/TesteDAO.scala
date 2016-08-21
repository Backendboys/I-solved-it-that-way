package daos

import play.api._
import play.api.db._
import scala.collection.immutable.List
import java.sql.Statement
import java.io.File

import models.{ Teste, Questao }

class TesteDAO(db: Database) {

  def list(questao_codigo: Option[Long] = None): List[Teste] = {
    var testes: List[Teste] = List()
    val query = questao_codigo match {
      case Some(value) => f"SELECT * FROM Teste WHERE questao_codigo = $value"
      case None => "SELECT * FROM Teste"
    }
    val conn = db.getConnection()

    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(query)

      while(rs.next()) {
        val codigo: Long = rs.getLong("codigo")
        val arquivo: File = new File(rs.getString("arquivo"))
        val estado: String = rs.getString("estado")
        val questao_codigo: Long = rs.getLong("questao_codigo")
        val questao = new QuestaoDAO(db).get(questao_codigo)

        testes = new Teste(codigo, arquivo, estado, questao) :: testes
      }
    } finally {
      conn.close()
    }

    return testes
  }

  def insert(arquivo: File, estado: String,
    questao: Questao): Option[Teste] = {
    val query = "INSERT INTO Teste(arquivo, estado, questao_codigo) VALUES (" +
      f"'${arquivo.getPath()}', '$estado', '${questao.codigo}')"
    val conn = db.getConnection()

    var teste: Option[Teste] = None

    try {
      val stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
      stmt.executeUpdate()
      val rs = stmt.getGeneratedKeys()
      while(rs.next()) {
        val codigo = rs.getInt(1)
        teste = Some(new Teste(codigo, arquivo, estado, questao))
      }
    } finally {
      conn.close()
    }

    return teste
  }

  def count(questao_codigo: Option[Long] = None): Int = {
    var total = 0
    val query = questao_codigo match {
      case Some(value) => "SELECT count(codigo) AS total FROM Teste WHERE " +
      f"questao_codigo = $value"
      case None => "SELECT count(codigo) AS total FROM Teste"
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

  def get(codigo: Long): Teste = {
    var teste: Teste = null
    val query = f"SELECT * FROM Teste WHERE codigo = $codigo"
    val conn = db.getConnection()

    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(query)
      rs.next()

      val codigo: Long = rs.getLong("codigo")
      val arquivo: File = new File(rs.getString("arquivo"))
      val estado: String = rs.getString("estado")
      val questao_codigo: Long = rs.getLong("questao_codigo")
      val questao = new QuestaoDAO(db).get(questao_codigo)

      teste = new Teste(codigo, arquivo, estado, questao)

      return teste
    }
  }

  def update(instance: Teste): Option[Teste] = {
    var teste: Teste = null
    val query = "UPDATE Teste SET (arquivo, estado, questao_codigo) = " +
      f"('${instance.arquivo.getPath()}', '${instance.estado}', " +
      f"${instance.questao.codigo}) WHERE codigo = ${instance.codigo}"
    val conn = db.getConnection()

    try {
      val stmt = conn.createStatement
      val rs = stmt.executeUpdate(query)
      return Some(instance)
    }

    return None
  }

}
