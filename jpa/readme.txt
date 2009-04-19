Seam JPA Example
================

This is the Hotel Booking example implemented in Seam POJO and Hibernate JPA.
It can be deployed in JBoss AS 4.x, WebLogic, GlassFish, Tomcat (both with and 
without the EJB3 container).

JBoss AS 4.2.0:
  * Install JBoss AS 4.2.0 GA
  * ant jboss
  * Start JBoss AS 
  * Access the app at http://localhost:8080/jboss-seam-jpa/
  * ant jboss.undeploy

JBoss AS 4.0.5 (with or without EJB3):
  * Install JBoss AS 4.0.5 with the default J2EE profile
  * ant jboss405
  * Deploy dist-jboss405/jboss-seam-jpa.war
  * Start JBoss AS 
  * Access the app at http://localhost:8080/jboss-seam-jpa/

WebLogic 9.2:
  * Install WebLogic 9.2
  * ant weblogic92
  * Start the WebLogic "examples" server
  * Load the admin console http://localhost:7001/console/
  * Deploy dist-weblogic92/jboss-seam-jpa.war
  * Access the app at http://localhost:7001/jboss-seam-jpa/

WebLogic 10.X:
  * Install WebLogic 10.X
  * Create an hsql datasource called 'seam-jpa-ds" (see reference guide)
  * Deploy Weblogics jsf-1.2.war shared library for JSF 1.2 support. (see reference guide)
  * ant weblogic10
  * Start the WebLogic domain you created or the "examples" domain if installed.
  * Load the admin console http://localhost:7001/console/
  * Deploy dist-weblogic10/jboss-seam-jpa.war
  * Access the app at http://localhost:7001/jboss-seam-jpa/
  * See Weblogic reference guide chapter for full details.

WebSphere 6.1:

  * Install and run WebSphere 6.1
  * Set a Websphere web container custom property "com.ibm.ws.webcontainer.invokefilterscompatibility" to true.  See Seam reference guide chapter for details.
  * Set a Websphere web container custom property "prependSlashToResource" to true.  See Seam reference guide chapter for details.
  * ant websphere61
  * Deploy dist-websphere61/jboss-seam-jpa.war and specify a context_root
  * From the "Enterprise Applications" list select: "jboss-seam-jpa" --> "Manager Modules" --> "jboss-seam-jpa.war" --> "Classes loaded with application class loader first", and then Apply
  * Start the application
  * Access it at http://localhost:9080/context_root/index.html

Tomcat with JBoss Embedded:
  * Install Tomcat
  * Install JBoss Embedded
  * ant jboss-embedded
  * Deploy dist-jboss-embedded/jboss-seam-jpa.war
  * Start Tomcat
  * Access the app at http://localhost:8080/jboss-seam-jpa/
  
Tomcat (5.5 or 6) without JBoss Embedded:
  * Install Tomcat
  * Copy lib/hsqldb.jar from this distribution into $TOMCAT_HOME/common/lib (Tomcat 5.5) or $TOMCAT_HOME/lib (Tomcat 6)
  * ant tomcat55 or ant tomcat6
  * Deploy dist-jboss/jboss-seam-jpa.war
  * Start Tomcat
  * Access the app at http://localhost:8080/jboss-seam-jpa/

GlassFish:
  * Install GlassFish V2
  * ant glassfish
  * Start GlassFish and the Embedded Derby Database
  * Load the admin console http://localhost:4848/
  * Deploy dist-glassfish/jboss-seam-jpa.war in the admin console (Applications > Web Applications)
    or using the command $GLASSFISH_HOME/bin/asadmin deploy dist-glassfish/jboss-seam-jpa.war
  * Access the app at http://localhost:8080/jboss-seam-jpa/

NOTES FOR GLASSFISH USERS:
  In order for the app to work out of the box with GlassFish, we have
  used the Derby (i.e., Java DB) database in GlassFish. The included
  WEB-INF/classes/GlassfishDerbyDialect.class is a special hack to get
  around a Derby bug in GlassFish V2. You must use it as your Hibernate
  dialect if you use Derby with GlassFish.
  
  However, we strongly recommend you to use a non-Derby data source if
  possible (for example, HSQL is a much better embedded DB).
