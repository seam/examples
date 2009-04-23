Seam Booking Example
====================

This example demonstrates the use of Seam 3 in a Java EE 6 environment (or a
Java EE 5 environment enhanced with JSR-299 [Web Beans] and JSF 2.0).
Contextual state management and dependency injection are handled by JSR-299.
Transaction and persistence context management is handled by the EJB 3
container.

Please consult the Web Beans reference documentation for instructions on how to
deploy the Web Bean implementation to JBoss AS 5. To upgrade the JSF libraries
to JSF 2.0, go to the Seam jsf-updater-tool module and follow the instructions
there.

This example uses a Maven 2 build. To build the EJB and WAR and package them
inside an EAR, execute the following command:

 mvn

Then, set the JBOSS_HOME environment variable to the location of a JBoss AS 5
installation and start the server. Maven will assume that JBoss AS is running
on port 8080. Once that's done, you can deploy the application to JBoss AS via
JMX by executing this command:

 mvn -o -f seam-booking-ear/pom.xml jboss:deploy

You can undeploy the application via JMX using this command:

 mvn -o -f seam-booking-ear/pom.xml jboss:undeploy

Here's the chained restart command via JMX:

 mvn -o -f seam-booking-ear/pom.xml jboss:undeploy && mvn -o package && mvn -o -f seam-booking-ear/pom.xml jboss:deploy

If you would rather deploy more traditional way by copying the archive directly
to the deploy directory of the JBoss AS domain, use this command instead:

 mvn -o -f seam-booking-ear/pom.xml jboss:harddeploy

But it's better to use the antrun plugin since it is smarter about what it
copies, which is bound to the end of the package goal when the explode profile
is active:

 mvn -o package -Pexplode

You can force a restart of the application by activating the restart profile:

 mvn -o validate -Prestart

You can remove the archive by activating the undeploy profile:

 mvn -o validate -Pundeploy

Note that the -o puts Maven in offline mode so that it doesn't perform time
consuming update checks.

----------------------
Unfinished intructions
----------------------
When this profile is activated, the maven-antrun-plugin will copy the exploded
packages for the WAR, EJB-JAR, and EAR to the JBoss AS deploy directory. This
all happens in the maven package phase.
