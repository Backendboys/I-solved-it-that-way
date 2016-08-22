package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.db._
import scala.collection.immutable.List

import daos.QuestaoDAO
import models.Questao

@Singleton
class ProfessorController @Inject()(db: Database) extends Controller {
  def home = Action { implicit request =>
    request.session.get("role") match {
    case Some(user) =>
      Ok(views.html.professor.home(new QuestaoDAO(db).list))
    case None       =>
      Unauthorized("Usuário não logado!")
    }
  }
}

