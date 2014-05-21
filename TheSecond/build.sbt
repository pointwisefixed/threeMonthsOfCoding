name := "TheSecond"

version := "1.0"

scalaVersion := "2.10.3"

seq(webSettings :_*)

libraryDependencies ++= {
  val liftVersion = "2.5.1"
  Seq(
   "net.liftweb" %% "lift-mongodb-record" % liftVersion,
   "com.foursquare" %% "rogue-field" % "2.2.0" intransitive(),
   "com.foursquare" %% "rogue-core" % "2.2.0" intransitive(),
   "com.foursquare" %% "rogue-lift" % "2.2.0" intransitive(),
   "com.foursquare" %% "rogue-index" % "2.2.0" intransitive(),
   "net.liftweb" %% "lift-webkit" % liftVersion % "compile",
   "org.eclipse.jetty" % "jetty-webapp" % "8.1.7.v20120910"  %
   "container,test",
   "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" %
      "container,compile" artifacts Artifact("javax.servlet", "jar", "jar")
  )
}

port in container.Configuration := 3000

