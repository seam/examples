JBoss AS
--------
You'll need the latest nightly build of JBoss AS 6.0.x to run this application.

http://hudson.jboss.org/hudson/view/JBoss%20AS/job/JBoss-AS-6.0.x/lastSuccessfulBuild/artifact/JBossAS_6_0/build/target/jboss-6.0.x.zip

Extract the zip, set the JBOSS_HOME environment variable and execute the startup script.

You can then deploy the application:

 mvn package jboss:hard-deploy

That command will deploy two files, booking-ds.xml and booking.war. You can undeploy using:

 mvn jboss:hard-undeploy

GlassFish
---------
You will need to install Hibernate 3.5 into GlassFish as there is a bug in
EclipseLink that prevents the extended persistence context from working. See
instructions below.

The simplest way to run the application on GlassFish is to:

 1. Start NetBeans 6.8 or later
 2. Open the project folder (it will autodetect the Maven project)
 3. Right click on the project
 4. Select the Run option

If you are not using NetBeans, you can start GlassFish and JavaDB using these commands:

 $GLASSFISH_HOME/bin/asadmin start-database
 $GLASSFISH_HOME/bin/asadmin start-domain domain1

Then you can package the project (mvn package) and deploy the WAR using the admin console:

Note about JavaDB:

You will need to start JavaDB to make the application run on GlassFish. Unlike
JBoss AS, the default data source is a client/server instance.

There are option provided by both the GlassFish server plugin in NetBeans and
Eclipse to automatically start JavaDB when GlassFish starts.

Eclipse:
Window > Preferences > GlassFish Preferences > Start the JavaDB database process when starting GlassFish Server

NetBeans:
Services (View) > Services (Node) > GlassFish (Entry) > Properties (Context menu item) > Start Registered Derby Server

Installing Hibernate 3.5 (JPA 2) on GlassFish 3
-----------------------------------------------
wget http://dlc.sun.com.edgesuite.net/glassfish/v3.0.1/promoted/glassfish-3.0.1-b22-unix.sh
sh glassfish-3.0.1-b22-unix.sh
cd $GLASSFISH_HOME
./bin/updatetool (may have to run it twice if it's not already installed)
GlassFish Server Open Source Edition > Available Add-ons > Hibernate JPA
Install

GlassFish settings
------------------
Disable preserving sessions across server restarts

Viewing data in JavaDB
----------------------
Create a database connection to the following database

URL: jdbc:derby://localhost:1527/sun-appserv-samples
Username: APP
Password: APP
