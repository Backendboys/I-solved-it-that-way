package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models.Questao

@Singleton
class ProfController @Inject() extends Controller {
  var questoes: List[Questao] = Nil;
  var cod = 0

  def getCampo(field: String) (implicit request: Request[AnyContent]) = {
    request.body.asFormUrlEncoded.get(field)(0)
  }

  def homeProf = Action { implicit request =>
    request.session.get("role") match {
    case Some(user) =>
      Ok(views.html.homeProf(request.session.get("role").getOrElse("")))
    case None       =>
      Unauthorized("Usuário não logado!")
    }
  }

  def create = Action { implicit request =>
    Ok(views.html.create())
  }

  def save = Action { implicit request =>
    questoes = questoes :+ recoverForm('s')
    Redirect(routes.ProfController.homeProf)
  }

  def recoverForm(t: Char)(implicit request: Request[AnyContent]) = {
    val campos = request.body.asFormUrlEncoded.getOrElse(Map())
    var id = 0
    var enunciado = ""
    var resposta = ""

    for(campo <- campos) yield {
        val chave = campo._1
        val valor = campo._2.mkString(", ")

        if (chave == "codigo")
          id = valor.toInt
        else if (chave == "enunciado")
          enunciado = valor
        else if (chave == "resposta")
          resposta = valor

    }

    if (t != 'e') { // create
      cod = cod + 1
    }
    var c = Questao(cod, enunciado, resposta)
    c
  }

}

