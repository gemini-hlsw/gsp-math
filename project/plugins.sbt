resolvers += Resolver.sonatypeRepo("public")

addSbtPlugin("edu.gemini"            % "sbt-lucuma"               % "0.3.6")
addSbtPlugin("com.geirsson"          % "sbt-ci-release"           % "1.5.7")
addSbtPlugin("org.scala-js"          % "sbt-scalajs"              % "1.5.1")
addSbtPlugin("org.portable-scala"    % "sbt-scalajs-crossproject" % "1.0.0")
addSbtPlugin("com.timushev.sbt"      % "sbt-updates"              % "0.5.2")
addSbtPlugin("org.foundweekends"     % "sbt-bintray-remote-cache" % "0.6.1")
