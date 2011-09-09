resolvers += { 
  val ivyPattern = "[organisation]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext] " 
  val ivySbtPluginPattern = "[organisation]/[module]/[sbtversion]/[revision]/[type]s/[artifact](-[class ifier])-[revision].[ext]" 
  val patterns = Patterns(false, ivyPattern, ivySbtPluginPattern) 
  Resolver.url("Typesafe Ivy Repository", url("http://repo.typesafe.com/typesafe/ivy-releases"))(patterns) 
}

resolvers += "Web plugin repo" at "http://siasia.github.com/maven2"

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % ("0.1.0-"+v))