package daos

import play.api._
import play.api.db._
import scala.collection.immutable.List
import java.sql.Statement
import java.io.File

import models.{ Teste, Questao }

class TesteDAO(db: Database) {

  def list: List[Teste] = {
    var testes: List[Teste] = List()
    val query = "SELECT * FROM Teste"
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

}
