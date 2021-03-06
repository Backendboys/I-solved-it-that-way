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

  def logout = Action {
    Redirect("/")
      .withNewSession
  }

  def auth = Action { implicit request =>
    LoginForm.form.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.login(formWithErrors))
      },
      userData => {
        userData.role match {
          case "professor" =>
            Redirect(routes.ProfessorController.home)
              .withSession("role" -> "professor", "nome" -> userData.nome)
            .flashing(SUCESSO -> "Login realizado com sucesso.")
          case "aluno" =>
            Redirect(routes.AlunoController.home)
            .withSession("role" -> "aluno", "nome" -> userData.nome)
            .flashing(SUCESSO -> "Login realizado com sucesso.")
          case _ => Redirect(routes.AuthController.index)
        }
      }
    )
  }
}

