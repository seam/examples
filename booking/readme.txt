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
installation. Once that's done, execute the following command to deploy the
application to JBoss AS via JMX:

 mvn -f seam-booking-ear/pom.xml jboss:deploy

You can undeploy the application via JMX using this command:

 mvn -f seam-booking-ear/pom.xml jboss:undeploy

Here's the chained restart command:

 mvn -f seam-booking-ear/pom.xml jboss:undeploy && mvn package && mvn -f seam-booking-ear/pom.xml jboss:deploy

If you would rather deploy more traditional way by copying the archive directly
to the deploy directory of the JBoss AS domain, use this command instead:

 mvn -f seam-booking-ear/pom.xml jboss:harddeploy

