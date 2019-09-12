import sbtcrossproject.crossProject
import sbtcrossproject.CrossType

lazy val attoVersion                 = "0.7.0"
lazy val catsVersion                 = "2.0.0"
lazy val collCompatVersion           = "2.1.2"
lazy val kindProjectorVersion        = "0.10.3"
lazy val monocleVersion              = "2.0.0"
lazy val catsTestkitScalaTestVersion = "1.0.0-M2"

inThisBuild(Seq(
  homepage := Some(url("https://github.com/gemini-hlsw/gsp-math")),
  addCompilerPlugin("org.typelevel" %% "kind-projector" % kindProjectorVersion),
) ++ gspPublishSettings)

lazy val math = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("modules/math"))
  .settings(
    name := "gsp-math",
    libraryDependencies ++= Seq(
      "org.tpolecat"               %% "atto-core"               % attoVersion,
      "org.typelevel"              %% "cats-core"               % catsVersion,
      "com.github.julien-truffaut" %% "monocle-core"            % monocleVersion,
      "com.github.julien-truffaut" %% "monocle-macro"           % monocleVersion,
      "org.scala-lang.modules"     %% "scala-collection-compat" % collCompatVersion
    )
  )
  .jvmConfigure(_.enablePlugins(AutomateHeaderPlugin))
  .jsSettings(gspScalaJsSettings: _*)


lazy val testkit = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("modules/testkit"))
  .dependsOn(math)
  .settings(
    name := "gsp-math-testkit",
    libraryDependencies ++= Seq(
      "org.typelevel"              %% "cats-testkit"           % catsVersion,
      "org.typelevel"              %% "cats-testkit-scalatest" % catsTestkitScalaTestVersion,
      "com.github.julien-truffaut" %% "monocle-law"            % monocleVersion,
    )
  )
  .jvmConfigure(_.enablePlugins(AutomateHeaderPlugin))
  .jsSettings(gspScalaJsSettings: _*)


lazy val tests = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("modules/tests"))
  .dependsOn(math, testkit)
  .settings(
    name := "gsp-math-tests",
    skip in publish := true
  )
  .jvmConfigure(_.enablePlugins(AutomateHeaderPlugin))
  .jsSettings(gspScalaJsSettings: _*)

