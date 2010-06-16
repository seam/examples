You will need to start JavaDB to make the application run

$GLASSFISH_HOME/bin/asadmin start-database

However, there should be an option with the GlassFish server plugin in both
NetBeans and Eclipse to automatically start it when GlassFish starts. Unlike
JBoss AS, the default data source is a client/server instance.

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
