package controllers

import javax.inject._
import java.io.File
import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc.MultipartFormData.FilePart
import play.api.libs.Files.TemporaryFile
import play.api.data._
import play.api.data.Forms._
import play.api.db._
import scala.collection.immutable.List
import scala.collection.Seq

import models.{ Questao, Teste }
import daos.{ QuestaoDAO, TesteDAO }
import forms.QuestaoForm

@Singleton
class QuestaoController @Inject()(db: Database) extends Controller {

  def add = Action { implicit request =>
    Ok(views.html.questao.create(QuestaoForm.form))
  }

  def create = Action { implicit request =>
    QuestaoForm.form.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.questao.create(formWithErrors))
      },
      userData => {

        val dao = new QuestaoDAO(db)
        val testeDao = new TesteDAO(db)
        val questao: Option[Questao] = dao.insert(
          userData.enunciado, userData.descricao)

        println("Questão adicionada: ")
        println(questao)

        var index = 1

        questao match {
          case Some(value) => {
            // SALVANDO ARQUIVOS DE TESTE
            val arquivosTeste: Option[Seq[FilePart[TemporaryFile]]] =
              request.body.asMultipartFormData.map(_.files)
            val arquivosTesteIO: Option[Seq[File]] =
              arquivosTeste.map { fileSeq =>
                fileSeq map { file =>
                  val dir = new File("./app/testfiles/" +
                    f"${value.codigo}%02d")
                  dir.mkdirs()
                  val newFile = new File(dir.getPath() + "/" + f"$index%02d")
                  index += 1
                  val teste: Option[Teste] = testeDao.insert(newFile, "U", value)
                  println("Teste adicionado:")
                  println(teste)
                  file.ref.moveTo(newFile)
              }
            }
          }
          case None => println("Erro ao inserir questão")
        }

        Redirect(routes.ProfessorController.home)

      }
    )
  }

  def detail(codigo: Long) = Action { implicit request =>
    Ok(views.html.questao.detail(codigo))
  }

  def edit(codigo: Long) = Action { implicit request =>
    Ok(views.html.questao.edit())
  }

}

