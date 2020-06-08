import scalafix.v1._
import scala.meta._

class MyRule extends SemanticRule("MyRule") {
  override def isRewrite: Boolean = true

  override def description: String = "My Rule"

  override def fix(implicit doc: SemanticDocument): Patch = {

    val `shapeless.HList.unsafeGet` = SymbolMatcher.normalized("shapeless.HList.unsafeGet")
    doc.tree.traverse {
      case `shapeless.HList.unsafeGet`(tree) =>
        println(s"tree=$tree")
        tree match {
          case q"..$mods def $ename[..$tparams](...$paramss): $tpeopt = $expr" =>
            println(s"expr=$expr")
          case _ => ()
        }
    }

    Patch.empty
  }
}