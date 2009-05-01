Seam Booking Example (Java EE 5)
================================

This example demonstrates the use of Seam 3 in a Java EE 6 environment (or a
Java EE 5 environment enhanced with JSR-299 [Web Beans] and JSF 2.0).
Contextual state management and dependency injection are handled by JSR-299.
Transaction and persistence context management is handled by the EJB 3
container. Validation of input fields is handled by Bean Validation.

= Prerequisites

Please consult the Web Beans reference documentation for instructions on how to
deploy the Web Bean implementation to JBoss AS 5. To upgrade the JSF libraries
to JSF 2.0, go to the Seam jsf-updater-tool module and follow the instructions
there. You will also need to have Bean Validation installed in the container
(or added to the classpath) in order for UI validation to work. Installing Bean
Validation is as simple as putting the JARs in the library directory of the
application server.

= First steps

This example uses a Maven 2 build. To build the EJB and WAR and package them
inside an EAR, execute the following command:

 mvn

Now you're ready to deploy.

= Deploying a packaged archive to JBoss AS

First, set the JBOSS_HOME environment variable to the location of a JBoss AS 5
installation and start the server. You can optionally set the jboss.home Maven
property in a settings.xml file. Maven assumes JBoss AS is running on port
8080. (This can be changed in the plugin configuration). Once that's setup, you
can deploy the application to JBoss AS via JMX by executing this command:

 mvn -o -f seam-booking-ear/pom.xml jboss:deploy

You can undeploy the application via JMX using this command:

 mvn -o -f seam-booking-ear/pom.xml jboss:undeploy

Here's the chained restart command via JMX:

 mvn -o -f seam-booking-ear/pom.xml jboss:undeploy && mvn -o package && mvn -o -f seam-booking-ear/pom.xml jboss:deploy

If you would rather deploy more traditional way by copying the archive directly
to the deploy directory of the JBoss AS domain, use this command instead:

 mvn -o -f seam-booking-ear/pom.xml jboss:harddeploy

For ease of development, you probably want to use an exploded archive instead.

= Deploying an exploded archive to JBoss AS

It's much better to use the antrun plugin over the jboss plugin since it's
smarter about what it copies to the server. The antrun plugin is bound to the
end of the package goal when the explode profile is active:

 mvn -o package -Pexplode

This profile executes an series of Ant tasks that copy the exploded WAR,
EJB-JAR, and EAR to the JBoss AS deploy directory.

You can force a restart of the application by activating the restart profile,
which executes an Ant task bound to the validate phase:

 mvn -o validate -Prestart

You can remove the archive by activating the undeploy profile, which also
executes an Ant task bound to the validate phase:

 mvn -o validate -Pundeploy

Finally, you can undeploy and explode all in one command using both the
undeploy and explode profiles with a standard package build:

 mvn -o package -Pundeploy,explode

Note that the -o puts Maven in offline mode so that it doesn't perform time
consuming update checks.

= Highlights

- 3 module Maven 2 reactor project (ejb-jar, war, ear)
- establishes a standard for Seam 3 examples
- repeat elements are kept to a minimum in the Maven POM files (DRY)
- JBoss datasource is deployed with the EAR to simplify packaging
  (-ds.xml doesn't have to be deployed to JBoss AS separately)
- supports both a packaged and exploded deployment to JBoss AS

= Known issues

(1) Clicking on logout throws an exception
    java.lang.IllegalStateException: getAttribute: Session already invalidated
      at org.apache.catalina.session.StandardSession.getAttribute(StandardSession.java:1032)

(2) An attempt to login after logging out throws an exception
    javax.faces.el.EvaluationException: java.lang.reflect.InvocationTargetException
     at javax.faces.component.MethodBindingMethodExpressionAdapter.invoke(MethodBindingMethodExpressionAdapter.java:102)

(3) List of bookings not refreshed after a booking is created

(4) Ajax is not working on blur in p:edit form fields (had to disable) 

(5) No list of workspaces

(6) Cannot use <f:view> in template or else it will remove the conversation id token from the view root

= Open questions

- How do I clear a contextual bean from a scope, in particular the session scope? This causes issue #1 and #3 above.

- How do I inject an Event object into a stateful component? I get an error that there is a reference to a
  non-serializable object from a bean declaring a non-passivating scope. This is the reason for issue #3.

= TODO

- use Cargo plugin to support deployment to other Java EE servers (GlassFish)

- refactor the password/confirm password into a reusable component (needed on
  registration and change password)
