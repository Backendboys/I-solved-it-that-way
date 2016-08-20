package controllers

import javax.inject._
import play.api._
import play.api.mvc._

case class Questao(codigo: Long, var enunciado: String, var resposta: String)

@Singleton
class HomeController @Inject() extends Controller {
  private val SUCESSO = "sucesso"

  def start = Action {
    Redirect(routes.HomeController.login)
  }

  def login = Action {
    Ok(views.html.login())
  }

  def index = Action { implicit request =>
    if (getCampo("usuario") == "professor") {
      Redirect(routes.ProfController.homeProf)
      .withSession("user" -> getCampo("usuario"))
      .flashing(SUCESSO -> "Login realizado com sucesso.")
    }
    else {
      Redirect(routes.AlunoController.homeAluno)
      .withSession("user" -> getCampo("usuario"))
      .flashing(SUCESSO -> "Login realizado com sucesso.")
    }
  }

  def getCampo(field: String) (implicit request: Request[AnyContent]) = {
    request.body.asFormUrlEncoded.get(field)(0)
  }

}
