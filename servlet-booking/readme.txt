Seam Booking Example
====================

This example demonstrates the use of Seam 3 in a Servlet container environment
(Tomcat 6 or Jetty 6). Contextual state management and dependency injection are
handled by JSR-299. Transaction and persistence context management is handled
by the EJB 3 container. No alterations are expected to be made to the Servlet
container. All services are self-contained within the deployment.

This example uses a Maven 2 build. Execute the following command to build the
WAR. The WAR will will be located in the target directory after completion of
the build.

 mvn

Run this command to execute the application in an embedded Jetty 6 container:

 mvn jetty:run

You can also execute the application in an embedded Tomcat 6 container:

 mvn tomcat:run

In both cases, any changes to assets in src/main/webapp will take affect
immediately. If a change to a configuration file is made, the application will
automatically redeploy. The redeploy behavior can be fined tuned in the plugin
configuration (at least for Jetty).

If you want to run the application on a standalone Tomcat 6, first download and
extract Tomcat 6.  This build assumes you will be running Tomcat in its default
configuration, with a hostname of localhost and port 8080. Before starting
Tomcat, add the following line to conf/tomcat-users.xml to allow the Maven
Tomcat plugin to access the manager application, then start Tomcat:

 <user username="admin" password="" roles="manager"/>

You can deploy the packaged archive to Tomcat via HTTP PUT using this command:

 mvn package tomcat:deploy

Then you use this command to undeploy the application:

 mvn tomcat:undeploy

Instead of packaging the WAR, you can deploy it as an exploded archive
immediately after the war goal is finished assembling the exploded structure:

 mvn compile war:exploded tomcat:exploded

Once the application is deployed, you can redeploy it using the following command:

 mvn tomcat:redeploy

But likely you want to run one or more build goals first before you redeploy:

 mvn compile tomcat:redeploy
 mvn war:exploded tomcat:redeploy
 mvn compile war:exploded tomcat:redeploy

Use of the war:inplace + tomcat:inplace goals are not recommended as it causes
files to be copied to your src/main/webapp directory. You may accidently check
them into the source repository or include them in the deployable archive.

---
Have to decide if you want war:inplace which mixes compiled files w/ source
files but gives you instant change or change the warSourceDirectory and require
war:exploded to be run to see changes take affect.
