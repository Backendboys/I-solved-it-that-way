package compilers

import sys.process._
import java.io.File

trait Compilador {
  def compilar(arquivo: File): String
}

object CompiladorFabrica {
  private class Scala extends Compilador {
    override def compilar(arquivo: File): String = {
      return f"scala ${arquivo.getPath()}" !!
    }
  }

  // TODO: Potigol compiler
  private class Potigol extends Compilador {
    override def compilar(arquivo: File): String = {
      return ""
    }
  }

  // INTERPRETADAS
  private class Ruby extends Compilador {
    override def compilar(arquivo: File): String = {
      return f"ruby ${arquivo.getPath()}" !!
    }
  }

  def apply(linguagem: String): Compilador = {
    linguagem match {
      case "scala" => return new Scala
      case "potigol" => return new Potigol
      case "ruby" => return new Ruby
    }
  }
}
