package daos

import play.api._
import play.api.db._
import scala.collection.immutable.List
import java.sql.Statement

import models.Questao

class QuestaoDAO(db: Database) {

  def list: List[Questao] = {
    var questoes: List[Questao] = List()
    val query = "SELECT * FROM Questao"
    val conn = db.getConnection()

    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(query)

      while(rs.next()) {
        val codigo = rs.getLong("codigo")
        val enunciado = rs.getString("enunciado")
        val descricao = rs.getString("descricao")

        questoes = new Questao(codigo, enunciado, descricao) :: questoes
      }
    } finally {
      conn.close()
    }

    return questoes
  }

  def get(codigo: Long): Questao = {
    var questao: Questao = null
    val query = f"SELECT * FROM Questao WHERE codigo = $codigo"
    val conn = db.getConnection()

    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(query)
      rs.next()

      val codigo = rs.getLong("codigo")
      val enunciado = rs.getString("enunciado")
      val descricao = rs.getString("descricao")
      questao = new Questao(codigo, enunciado, descricao)

    } finally {
      conn.close()
    }

    return questao
  }

  def delete(codigo: Long) {
    val query = f"DELETE FROM Questao WHERE codigo = $codigo"
    val conn = db.getConnection()

    try {
      val stmt = conn.createStatement
      stmt.executeUpdate(query)
    } finally {
      conn.close()
    }
  }

  def insert(enunciado: String, descricao: String): Option[Questao] = {
    val query = "INSERT INTO Questao(enunciado, descricao) VALUES (?, ?)"
    val conn = db.getConnection()

    var questao: Option[Questao] = None

    try {
      val stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
      stmt.setString(1, enunciado)
      stmt.setString(2, descricao)
      stmt.executeUpdate()
      val rs = stmt.getGeneratedKeys()
      while(rs.next()) {
        val codigo = rs.getInt(1)
        questao = Some(new Questao(codigo, enunciado, descricao))
      }
    } finally {
      conn.close()
    }

    return questao
  }

  def update(instance: Questao): Option[Questao] = {
    val query = "UPDATE Questao SET (enunciado, descricao) = (?, ?)" +
      f"WHERE codigo = ${instance.codigo}"
    val conn = db.getConnection()

    try {
      val stmt = conn.prepareStatement(query)
      stmt.setString(1, instance.enunciado)
      stmt.setString(2, instance.descricao)
      val rs = stmt.executeUpdate()
      return Some(instance)
    }

    return None
  }

}
