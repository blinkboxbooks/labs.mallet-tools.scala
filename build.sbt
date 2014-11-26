name := "mallet-tools"

version := "0.0.1"

organization := "com.blinkbox.books.labs"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "org.scalatest"     %% "scalatest"            % "2.2.1" % Test,
  "junit"              % "junit"                % "4.11" % Test,
  "com.novocode"       % "junit-interface"      % "0.10" % Test,
  "com.typesafe"       % "config"               % "1.0.2",
  "org.mockito"        % "mockito-core"         % "1.9.5" % Test,
  "org.json4s"        %% "json4s-jackson"       % "3.2.9",
  "cc.mallet"          % "mallet"               % "2.0.7",
  "net.sourceforge.htmlcleaner" %  "htmlcleaner"       % "2.2",
  "org.jsoup"                   %  "jsoup"             % "1.8.1",
  "commons-net"                 %  "commons-net"       % "3.3",
  "com.jcraft"                  %  "jsch"              % "0.1.51",
  "org.slf4j"                   %  "slf4j-api"         % "1.7.7"
)

parallelExecution := false
