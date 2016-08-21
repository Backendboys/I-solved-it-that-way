package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import models.Questao

@Singleton
class AlunoController @Inject() extends Controller {

  def home = Action { implicit request =>
    request.session.get("role") match {
    case Some(user) =>
      Ok(views.html.aluno.home())
    case None       =>
      Unauthorized("Usuário não logado!")
    }
  }

}

