Seam Booking Example (Java EE Environment)
==========================================

This example demonstrates the use of Seam 3 in a Java EE 6 environment (or a
Java EE 5 environment enhanced with JSR-299 [Web Beans], JSF 2.0 and Bean
Validation). Contextual state management and dependency injection are handled
by JSR-299. Transaction and persistence context management is handled by the
EJB 3 container. Validation of input fields is handled by Bean Validation.

= Prerequisites

Please consult the Web Beans reference documentation for instructions on how to
deploy the Web Bean implementation to JBoss AS 5. To upgrade the JSF libraries
to JSF 2.0, go to the Seam jsf-upgrade-tool module and follow the instructions
contained there in pom.xml. You also need to have Bean Validation installed in 
the container (or added to the classpath) in order for UI validation to work. 
Installing Bean Validation is as simple as putting the JARs in the library 
directory of the application server.

You'll also need the 1.0.0-SNAPSHOT of the Web Beans logger extension. You can
find it in the Web Beans extensions source tree. Run 'mvn install' in that
directory to have it installed in your local Maven 2 repository.

= First steps

This example uses a Maven 2 build. The recommended version is 2.0.10. You can
use Maven 2.1.0, but know that the maven-cli-plugin recommended in this
tutorial is not compatible with it out of the box.

Execute this command to build the EJB and WAR and package them inside an EAR:

 mvn install

Now you're ready to deploy.

= Pointing Maven at your JBoss AS installation

First, set the JBOSS_HOME environment variable to the location of a JBoss AS 5
installation and start the server. You can optionally set the jboss.home Maven
property in an active profile defined in the $HOME/.m2/settings.xml file.

Maven assumes JBoss AS is running on port 8080. (This can be changed in the
plugin configuration). Once that's setup, you can deploy the application to
JBoss AS using Maven.

= Deploying an packaged or exploded archive to JBoss AS (the wicked fast way)

Since this build is based on Maven 2, you're probably thinking you'll need to
take a stroll to the kitchen for coffee while you wait for the build to run.
Well, you better have your coffee ready because this build is fast...and easy!

The secret? The Maven 2 CLI plugin: http://github.com/mrdon/maven-cli-plugin.
This plugin gives you a command console to execute maven goals and plugins.
Even better, it supports aliases, so no more having to type long Maven commands
with switches and properties. Here's how to get started.

First, rev up the command console:

 mvn cli:execute-phase

You'll be presented with a prompt:

maven2> 

At the prompt, you can type any of the following commands (a description is
provide for each command, but don't type that of course):

explode   -> deploy as an exploded archive to JBoss AS)
deploy    -> deploy as a packaged archive to JBoss AS
undeploy  -> undeploy the exploded or packaged archive from JBoss AS
restart   -> restart the exploded or packaged archive deployed to JBoss AS
reexplode -> undeploy then explode

You can prefix any command with "clean" if you want to do a clean build:

maven2> clean explode

Leave the console running and enjoy extremely fast deployments ;)

But fast isn't for all of us.

= Deploying a packaged archive to JBoss AS (the Slowskys' way)

Why work fast when you can work slow? Perhaps your boss is annoying you and you
want to irritate him with long builds. Or maybe you just fear speed. If that's
the case, this section is for you (or if you just want to know what all those
commands are doing above).

Putting JBOSS_HOME aside for the moment, you can deploy the application to
JBoss AS via JMX by executing this command:

 mvn -f seam-booking-ear/pom.xml jboss:deploy

You can undeploy the application via JMX using this command:

 mvn -f seam-booking-ear/pom.xml jboss:undeploy

Here's the chained restart command via JMX:

 mvn -f seam-booking-ear/pom.xml jboss:undeploy && mvn package && mvn -f seam-booking-ear/pom.xml jboss:deploy

If you would rather deploy more traditional way by copying the archive directly
to the deploy directory of the JBoss AS domain, use this command instead:

 mvn -f seam-booking-ear/pom.xml jboss:harddeploy

But likely during development, you're only interested in exploded archives.

= Deploying a packaged archive to JBoss AS (the Slowskys' way)

It's much better to use the antrun plugin over the jboss plugin since it's
smarter about what it copies to the server. The antrun plugin is bound to the
end of the package goal when the explode profile is active:

 mvn package -Pexplode

This profile executes an series of Ant tasks that copy the exploded WAR,
EJB-JAR, and EAR to the JBoss AS deploy directory.

You can force a restart of the application by activating the restart profile,
which executes an Ant task bound to the validate phase:

 mvn validate -Prestart

You can remove the archive by activating the undeploy profile, which also
executes an Ant task bound to the validate phase:

 mvn validate -Pundeploy

Finally, you can undeploy and explode all in one command using both the
undeploy and explode profiles with a standard package build:

 mvn package -Pundeploy,explode

Note that you can append -o to any command after you have run the command at
least once. This flag puts Maven in offline mode so that it doesn't perform
time consuming update checks. You can also include -o flag in the aliases
configured for the Maven CLI plugin documented above.

= Example highlights

- 3 module Maven 2 reactor project (ejb-jar, war, ear)
- establishes a standard for Seam 3 examples
- repeat elements are kept to a minimum in the Maven POM files (DRY)
- JBoss datasource is deployed with the EAR to simplify packaging
  (-ds.xml doesn't have to be deployed to JBoss AS separately)
- supports both a packaged and exploded deployment to JBoss AS
- Authentication with Seam Security by implementing the Authenticator interface

= Known issues

See WORKLOG

