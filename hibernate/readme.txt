Seam Hibernate Example
=======================

This is the Hotel Booking example implemented in Seam and Hibernate POJOs.
It can be deployed in JBoss AS 4.x, WebLogic, GlassFish and Tomcat without
the EJB3 container.

JBoss AS 4.2.x:
  * Install JBoss AS with the default profile
  * ant jboss
  * Start JBoss AS 
  * Access the app at http://localhost:8080/jboss-seam-hibernate/
  * ant jboss.undeploy

JBoss AS 4.0.5.GA:
  * Install JBoss AS with the default profile (with or without EJB3)
  * ant jboss405
  * Deploy dist-jboss/jboss-seam-hibernate.war
  * Start JBoss AS 
  * Access the app at http://localhost:8080/jboss-seam-hibernate/

WebLogic 9.2:
  * Install WebLogic 9.2
  * ant weblogic92
  * Start the WebLogic "examples" server
  * Load the admin console http://localhost:7001/console/
  * Deploy dist-weblogic92/jboss-seam-hibernate.war
  * Access the app at http://localhost:7001/jboss-seam-hibernate/

WebSphere 6.1: (Special thanks to Denis Forveille)

  * Install and run WebSphere 6.1
  * In Application Servers -> <server> -> Web Container Settings -> Web Container -> Custom Properties, set "com.ibm.ws.webcontainer.invokefilterscompatibility" to "true"
  * ant websphere61
  * Install dist-websphere61/jboss-seam-hibernate.war and specify a context_root
  * From the "Enterprise Applications" list select: "jboss-seam-hibernate_war" --> "Manager Modules" --> "jboss-seam-hibernate.war" --> "Classes loaded with application class loader first", and then Apply
  * Start the application
  * Access it at http://localhost:9080/context_root/index.html

Plain Tomcat (special thanks to Ralph Schaer)
  * Install Tomcat 5.5 or Tomcat 6
  * Copy the lib/hsqldb.jar into $TOMCAT_HOME/common/lib (Tomcat 5.5) or $TOMCAT_HOME/lib (Tomcat 6)
  * ant tomcat
  * Deploy dist-tomcat55/jboss-seam-hibernate.war or dist-tomcat6/jboss-seam-hibernate.war to $TOMCAT_HOME/webapps/jboss-seam-hibernate.war
  * Start Tomcat
  * Access the app at http://localhost:8080/jboss-seam-hibernate/

Tomcat with embeddable JBoss:
  * Install Tomcat
  * Install Embeddable JBoss
  * ant jboss-embedded
  * Deploy dist-jboss-embedded/jboss-seam-hibernate.war
  * Start Tomcat
  * Access the app at http://localhost:8080/jboss-seam-hibernate/

GlassFish
  * Install GlassFish V1 or V2
  * ant glassfish
  * Start GlassFish and the Embedded Derby Database
  * Load the admin console http://localhost:4848/
  * Deploy dist-glassfish/jboss-seam-hibernate.war as Web App
  * Access the app at http://localhost:8080/jboss-seam-hibernate/

GlassFish:
  * Install GlassFish V1 or V2
  * ant glassfish
  * Start GlassFish and the Embedded Derby Database
  * Load the admin console http://localhost:4848/
  * Deploy dist-glassfish/jboss-seam-hibernate.war in the Admin Console (Applications > Web Applications)
    or using the command $GLASSFISH_HOME/bin/asadmin deploy dist-glassfish/jboss-seam-hibernate.war
  * Access the app at http://localhost:8080/jboss-seam-hibernate/

NOTES FOR GLASSFISH USERS:
  In order for the app to work out of the box with GlassFish, we have
  used the Derby (i.e., Java DB) database in GlassFish. The included
  WEB-INF/classes/GlassfishDerbyDialect.class is a special hack to get
  around a Derby bug in GlassFish V2. You must use it as your Hibernate
  dialect if you use Derby with GlassFish.
  
  However, we strongly recommend you to use a non-Derby data source if
  possible (for example, HSQL is a much better embedded DB).
