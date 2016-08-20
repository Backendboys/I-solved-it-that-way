package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import models.Questao

@Singleton
class QuestaoController @Inject() extends Controller {

  def show(codigo: Long) = Action { implicit request =>
    Ok(views.html.questao(codigo))
  }

  def edit(codigo: Long) = Action { implicit request =>
    Ok(views.html.edit())
  }

}
