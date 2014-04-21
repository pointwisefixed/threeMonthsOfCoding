import AssemblyKeys._

assemblySettings

jarName in assembly := "Todo.jar"

name := "TheFirst"

version := "1.0"

libraryDependencies ++= "com.h2database" % "h2" % "1.3.170" :: "com.typesafe.slick" %% "slick" % "2.0.1" :: Nil



    