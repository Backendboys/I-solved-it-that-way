package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._


import forms.LoginForm

@Singleton
class AuthController @Inject() extends Controller {
  private val SUCESSO = "sucesso"

  def index = Action {
    Ok(views.html.login(LoginForm.form))
  }

  def auth = Action { implicit request =>
    LoginForm.form.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.login(formWithErrors))
      },
      userData => {
        userData.role match {
          case "professor" =>
            Redirect(routes.ProfController.homeProf)
            .withSession("role" -> "professor")
            .flashing(SUCESSO -> "Login realizado com sucesso.")
          case "aluno" =>
            Redirect(routes.AlunoController.homeAluno)
            .withSession("role" -> "aluno")
            .flashing(SUCESSO -> "Login realizado com sucesso.")
          case _ => Redirect(routes.AuthController.index)
        }
      }
    )
  }
}

