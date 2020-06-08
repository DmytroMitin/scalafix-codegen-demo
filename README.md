# scalafix-codegen-demo

https://gitter.im/scalameta/scalameta?at=5edeb50e9da05a060a5d567b

I used @olafurpg's scalafix code-generation [project](https://github.com/olafurpg/scalafix-codegen). Suppose I'm interested in the RHS of method `shapeless.HList.unsafeGet` from Shapeless library. I put its sources to `in`. I added Shapeless dependencies to `build.sbt` (they are `scala-reflect` and `scala-compiler`). (There are also auto-generated sources in Shapeless so I put them to `in` too.) I created custom rewriting rule and put it to rules.

```scala
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
```

I added `.toTask(s" --rules=file:rules/src/main/scala/MyRule.scala --out-from=$outFrom --out-to=$outTo")` to `build.sbt`. Now after `sbt out/compile` I got the following to console
```scala
(l: @unchecked) match {
   case hd :: tl if i == 0 => hd
   case hd :: tl => unsafeGet(tl, i-1)
 }
```
which is RHS of `shapeless.HList.unsafeGet`. `out` is empty because I wrote `Patch.empty` in `MyRule`. Otherwise there would be transformed sources in `out`.
