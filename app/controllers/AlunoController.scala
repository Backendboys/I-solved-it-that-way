package controllers

import javax.inject._
import java.io._
import java.security.MessageDigest
import play.api._
import play.api.db._
import play.api.mvc._
import scala.collection.immutable.List
import scala.util.control.Breaks._
import org.joda.time.DateTime

import models.{ Questao, QuestaoResposta, Teste }
import daos.{ QuestaoDAO, RespostaDAO, TesteDAO }
import forms.RespostaForm

@Singleton
class AlunoController @Inject()(db: Database) extends Controller {

  def home = Action { implicit request =>
    request.session.get("role") match {
    case Some(role) =>
      var dao = new QuestaoDAO(db)
      var daoResposta = new RespostaDAO(db)
      var questoes = dao.list
      var composto: List[QuestaoResposta] = questoes map { questao =>
        new QuestaoResposta(questao, daoResposta.list(Some(questao.codigo)))
      }
      Ok(views.html.aluno.home(composto))
    case None       =>
      Unauthorized("Usuário não logado!")
    }
  }

  def responder(codigo: Long) = Action { implicit request =>
    Ok(views.html.aluno.responder(RespostaForm.form))
  }

  def submit(codigo: Long) = Action { implicit request =>
    RespostaForm.form.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.aluno.responder(formWithErrors))
      },
      userData => {
        val dao = new RespostaDAO(db)
        val questaoDao = new QuestaoDAO(db)
        val testeDao = new TesteDAO(db)
        var nomeAluno = "Desconhecido"
        request.session.get("nome") match {
          case Some(nome) => nomeAluno = nome
          case None => BadRequest(views.html.aluno.responder(RespostaForm.form))
        }
        val md5 = MessageDigest.getInstance("MD5")
        val hash = md5.digest(nomeAluno.getBytes).map("%02x".format(_)).mkString

        val dir = new File("./app/respostas/" +
          f"$hash/$codigo%02d")
        dir.mkdirs()

        var index = dao.count(Some(codigo)) + 1

        val arquivo = new File(dir.getPath() + "/" + f"$index%02d")
        val writer = new PrintWriter(arquivo)

        writer.write(userData.conteudo)
        writer.close()

        val questao = questaoDao.get(codigo)

        val resposta = dao.insert(userData.compilador, "U", DateTime.now(), None,
          arquivo, questao, nomeAluno)

        resposta match {
          case Some(value) => {
            val testes = testeDao.list(Some(codigo))
            breakable {
              for (teste <- testes) {
                val resultado = value == teste
                dao.update(value)
                if(!resultado) {
                  break
                }
              }
            }
          }
          case None => println("Não foi possível salvar a resposta")
        }
      })

    Redirect(routes.AlunoController.home)
  }

}

