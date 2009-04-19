Seam JEE5 Examples
=================

The examples in this directory showcases how to build Java EE 5 compliant Seam 
applications. The application should run on all Java EE 5 compliant 
application servers with minimal changes in code and configuration files. The 
default build script builds a deployable EAR for GlassFish. See Seam reference
guide for instructions for other containers.

GlassFish V2 
------------

1. Modify the following files in the project.

   * resources/META-INF/persistence.xml: Change the jta-persistence-provider to the commented
     out Glassfish property

2.  Build the demo app by running Ant. The build target is 
    "dist/jboss-seam-jee5.ear" 

3.  Download GlassFish V2 Final Build

4.  Install it: java -Xmx256m -jar glassfish-installer-xxx.jar

5.  Setup glassfish: cd glassfish; ant -f setup.xml;

6.  Start the GlassFish server: $GLASSFISH_HOME/bin/asadmin start-domain domain1

7.  Start the embedded JavaDB: $GLASSFISH_HOME/bin/asadmin start-database

8.  Load the admin console: http://localhost:4848/

9.  Login using username/password: admin / adminadmin

10.  Deploy the "Enterprise Application" in the admin console
    or using the command $GLASSFISH_HOME/bin/asadmin deploy dist/jboss-seam-jee5.ear

11. Checkout the app at: http://localhost:8080/seam-jee5/

12. Stop the server and database: 
    $GLASSFISH_HOME/bin/asadmin stop-domain domain1; $GLASSFISH_HOME/bin/asadmin stop-database


OC4J 11g Technology Preview
---------------------------

1.  Modify the following files in the project.
  
    * build.xml: Un-comment the OC4J-related libraries
    * resources/META-INF/persistence.xml: un-comment the OC4J properties

2.  Build the demo app by running ANT. The build target is 
    "dist/jboss-seam-jee5.ear"

3.  Download OC4J 11g Technology Preview from here 
    http://www.oracle.com/technology/tech/java/oc4j/11/index.html

4.  Unzip the downloaded file

5.  Make sure you have $JAVA_HOME and $ORACLE_HOME set as environment 
    variables ($ORACLE_HOME is the directory to which you unzip OC4J). For 
    further information on installing OC4J, consult the Readme.txt distributed 
    with OC4J

6.  Edit the OC4J datasource $ORACLE_HOME/j2ee/home/config/data-sources.xml 
    and, inside <data-sources>, add

    <managed-data-source 
      connection-pool-name="jee5-connection-pool" 
      jndi-name="jdbc/__default" 
      name="jee5-managed-data-source"
      />
    <connection-pool name="jee5-connection-pool">
      <connection-factory 
        factory-class="org.hsqldb.jdbcDriver" 
        user="sa" 
        password="" url="jdbc:hsqldb:." 
        />
    </connection-pool>


7.  Edit $ORACLE_HOME/j2ee/home/config/server.xml and, inside 
    <application-server>, add

    <application 
      name="jboss-seam-jee5" 
      path="../../home/applications/jboss-seam-jee5.ear" 
      parent="default" 
      start="true" 
      />

8.  Edit $ORACLE_HOME/j2ee/home/config/default-web-site.xml, and, inside 
    <web-site>, add

    <web-app 
      application="jboss-seam-jee5" 
      name="jboss-seam-jee5" 
      load-on-startup="true" 
      root="/seam-jee5" 
      />

9.  Copy hsqldb.jar to OC4J: 
    cp ../../lib/hsqldb.jar $ORACLE_HOME/j2ee/home/applib/

10. Copy the application to OC4J: 
    cp build/jboss-seam-jee5.ear $ORACLE_HOME/j2ee/home/applications/

11. Start OC4J: $ORACLE_HOME/j2ee/home/java -jar -XX:MaxPermSize=256M oc4j.jar
    * You must override the default PermGen memory settings using above command
       * See http://www.oracle.com/technology/tech/java/oc4j/11/oc4j-relnotes.html
    * You will be asked to set the admin password if this is the first time 
      you've started OC4J
    * You may get an ClassNotFoundException relating to 
      org.jboss.logging.util.OnlyOnceErrorHandler, this doesn't impact on the 
      running of the app.  We are working to get rid of this error!

12. Checkout the app at: http://localhost:8888/seam-jee5

13. You can stop the server by pressing CTRL-c in the console on which the 
    server is running.


Workarounds for OC4J 11g
------------------------

* Set hibernate.query.factory_class=org.hibernate.hql.classic.ClassicQueryTranslatorFactory in
  persistence.xml - OC4J uses an incompatible (old) version of antlr in toplink which causes 
  hibernate to throw an exception (discussed here for Weblogic, but the same applies to OC4J - 
  http://hibernate.org/250.html#A23).  You can also work around this by putting the hibernate 
  jars in $ORACLE_HOME/j2ee/home/applib/
  
WebLogic 10.3
---------------------------
Weblogic support requires some specific patches from Oracle/BEA so that their
EJB3 support fuctions correctly.  Please refer to the Seam reference guide for 
additional information.
  
- http://seamframework.org/Documentation
  
WebSphere 6.1.0.13 with EJB3 feature pack
---------------------------
The instructions for integration with Websphere are fairly verbose.  Please 
refer to the Seam reference guide for additional information.

- http://seamframework.org/Documentation
