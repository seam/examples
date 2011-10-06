# Seam Booking

This is the classic Seam Booking example ported to Java EE 6 infused with Seam
3 portable extensions.  It's also been given a fresh new look. See features.txt
for a list of features that are demonstrated by this example.

## Running on JBoss AS 7
You'll need JBoss AS 7.0.0.Final or better to run this application on JBoss AS.

    http://jboss.org/jbossas

Extract the zip and set the JBOSS_HOME environment variable.

Run:
mvn clean package arquillian:run -Darquillian=jbossas-managed-7

Now visit this URL in the browser:

    http://localhost:8080/seam-booking

## Running on JBoss AS 6

Run:
mvn clean package arquillian:run -Darquillian=jbossas-managed-6

## Running on GlassFish

You'll need GlassFish 3.1 or better to run this application on GlassFish.

The simplest way to run the application on GlassFish is to:

1. Start NetBeans 6.8 or later
2. Open the project folder (it will autodetect the Maven project)
3. Enable "glassfish" maven profile
4. Right click on the project
5. Select the Run option

If you are not using NetBeans, you can start GlassFish and JavaDB using these commands:

    $GLASSFISH_HOME/bin/asadmin start-database
    $GLASSFISH_HOME/bin/asadmin start-domain domain1

Then you can package and deploy the project by running:

mvn clean package arquillian:run -Darquillian=glassfish-remote-3.1

### Known issues (QA, please read)

Something changed in Mojarra 2.1 (since 2.0) that causes input elements within
composite components to not be able to locate their parent form, resulting in
this message:

> The form component needs to have a UIForm in its ancestry. Suggestion:
> enclose the necessary components within <h:form>

It hasn't been determined whether or not this is a bug in booking or in Mojarra 2.1.

### A note about JavaDB

You'll need to start JavaDB to make the application run on GlassFish. Unlike
JBoss AS, the default data source is a client/server instance.

There are options provided by both the GlassFish server plugin in NetBeans and
Eclipse to automatically start JavaDB when GlassFish starts.

Eclipse:
Window > Preferences > GlassFish Preferences > Start the JavaDB database process when starting GlassFish Server

NetBeans:
Services (View) > Services (Node) > GlassFish (Entry) > Properties (Context menu item) > Start Registered Derby Server

### Recommended GlassFish settings

* Disable preserving sessions across server restarts

### Viewing data in JavaDB

Create a database connection to the following database

* URL: jdbc:derby://localhost:1527/sun-appserv-samples
* Username: APP
* Password: APP

### Installing Hibernate 3.5 (JPA 2) on GlassFish 3

We recommend that you switch from EclipseLink to Hibernate. If you want to 
run the application on GlassFish 3.0.1, you'll need to use Hibernate to work
around several bugs in that version of EclipseLink.

* wget http://dlc.sun.com.edgesuite.net/glassfish/v3.0.1/promoted/glassfish-3.0.1-b22-unix.sh
* sh glassfish-3.0.1-b22-unix.sh
* cd $GLASSFISH_HOME
* ./bin/updatetool (may have to run it twice if it's not already installed)
* GlassFish Server Open Source Edition > Available Add-ons > Hibernate JPA
* Install

## Running integration tests

The integration tests are based on Arquillian (http://arquillian.org)

With JBoss AS started, you can run the tests using the following command:

    mvn test -Pjbossas-remote-6

With GlassFish 3.1 started, you can run the tests using the following command:

    mvn test -Pglassfish-remote-3.1

## Functional tests
To run functional tests for the booking example, follow these steps:
The following configurations are supported:
mvn clean verify -Darquillian=jbossas-managed-6
mvn clean verify -Darquillian=jbossas-managed-7
mvn clean verify -Darquillian=glassfish-remote-3.1

mvn clean verify -Pjbossas6 -Darquillian=jbossas-remote-6
mvn clean verify -Pjbossas7 -Darquillian=jbossas-remote-7

Note that you need to set the JBOSS_HOME environment variable properly for the managed configurations.
Make sure that the application is not deployed before running the functional test.
