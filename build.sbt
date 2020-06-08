name := "scalafix-codegen2"

// rules/clean; rules/compile; in/clean; out/clean; out/compile
// Don't forget to restart sbt shell in IntelliJ after rule change (sbt reload is not enough!)

inThisBuild(
  List(
    scalaVersion := "2.13.2",
    addCompilerPlugin(scalafixSemanticdb),
    scalacOptions ++= List(
      "-Yrangepos"
    )
  )
)

lazy val rules = project
  .settings(
    libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % "0.9.16"
  )

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
//    "com.chuusai" %% "shapeless" % "2.4.0-M1",
    scalaOrganization.value % "scala-reflect" % scalaVersion.value % "provided",
    scalaOrganization.value % "scala-compiler" % scalaVersion.value % "provided",
  )
)

lazy val in = project
  .settings(
    commonSettings,
  )

lazy val out = project
  .settings(
    commonSettings,

    sourceGenerators.in(Compile) += Def.taskDyn {
      val root = baseDirectory.in(ThisBuild).value.toURI.toString
      val from = sourceDirectory.in(in, Compile).value
      val to = sourceManaged.in(Compile).value
      val outFrom = from.toURI.toString.stripSuffix("/").stripPrefix(root)
      val outTo = to.toURI.toString.stripSuffix("/").stripPrefix(root)
      Def.task {
        scalafix
          .in(in, Compile)
//          .toTask(s" ProcedureSyntax --out-from=$outFrom --out-to=$outTo")
          .toTask(s" --rules=file:rules/src/main/scala/MyRule.scala --out-from=$outFrom --out-to=$outTo")
          .value
        (to ** "*.scala").get
      }
    }.taskValue
  )