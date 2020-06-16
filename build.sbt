import sbtcrossproject.CrossType

lazy val attoVersion                 = "0.8.0"
lazy val catsVersion                 = "2.1.1"
lazy val collCompatVersion           = "2.1.6"
lazy val kindProjectorVersion        = "0.10.3"
lazy val monocleVersion              = "2.0.5"
lazy val catsTestkitScalaTestVersion = "1.0.1"
lazy val scalaJavaTimeVersion        = "2.0.0"
lazy val jtsVersion                  = "0.0.9"
lazy val svgdotjsVersion             = "0.0.1"

inThisBuild(Seq(
  homepage := Some(url("https://github.com/gemini-hlsw/gsp-math")),
  addCompilerPlugin("org.typelevel" %% "kind-projector" % kindProjectorVersion),
  Global / onChangedBuildSource := ReloadOnSourceChanges
) ++ gspPublishSettings)

skip in publish := true

lazy val math = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("modules/math"))
  .settings(
    name := "gsp-math",
    libraryDependencies ++= Seq(
      "org.tpolecat"               %%% "atto-core"               % attoVersion,
      "org.typelevel"              %%% "cats-core"               % catsVersion,
      "com.github.julien-truffaut" %%% "monocle-core"            % monocleVersion,
      "com.github.julien-truffaut" %%% "monocle-macro"           % monocleVersion,
      "org.scala-lang.modules"     %%% "scala-collection-compat" % collCompatVersion,
      "edu.gemini"                 %%% "gpp-jts"                 % jtsVersion
    )
  )
  .jvmConfigure(_.enablePlugins(AutomateHeaderPlugin))
  .jvmSettings(
    libraryDependencies ++= Seq(
      "edu.gemini" %%% "gpp-jts-awt" % jtsVersion
    )
  )
  .jsSettings(gspScalaJsSettings: _*)
  .jsSettings(
    libraryDependencies ++= Seq(
      "io.github.cquiroz" %%% "scala-java-time" % scalaJavaTimeVersion,
      "edu.gemini"        %%% "gpp-svgdotjs"    % svgdotjsVersion
    )
  )

lazy val testkit = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("modules/testkit"))
  .dependsOn(math)
  .settings(
    name := "gsp-math-testkit",
    libraryDependencies ++= Seq(
      "org.typelevel"              %%% "cats-testkit"           % catsVersion,
      "org.typelevel"              %%% "cats-testkit-scalatest" % catsTestkitScalaTestVersion,
      "com.github.julien-truffaut" %%% "monocle-law"            % monocleVersion
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

lazy val svgtests = project
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalaJSBundlerPlugin)
  .enablePlugins(AutomateHeaderPlugin)
  .in(file("modules/svgtests"))
  .dependsOn(math.js, tests.js)
  .settings(
    name := "gsp-math-svgtests",
    skip in publish := true
  )
  .settings(gspScalaJsSettings: _*)
  .settings(
    Test / requireJsDomEnv := true,
    version in installJsdom := "16.2.0",
    useYarn := true,
    version in webpack := "4.30.0",
    webpackConfigFile in Test := Some(
      baseDirectory.value / "src" / "test" / "test.webpack.config.js"
    ),
    libraryDependencies ++= Seq(
      "org.scalameta" %%% "munit" % "0.7.8"
    ),
    testFrameworks := Seq(new TestFramework("munit.Framework")),
    Compile / npmDependencies ++= Seq(
      "@svgdotjs/svg.js" -> "3.0.16"
    )
  )
