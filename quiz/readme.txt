To run the example with embedded jetty:

mvn jetty:run -Pjetty

Then navigate to:

http://localhost:8080/config-quiz/home.jsf

To deploy the example to jbossas 7:

export JBOSS_HOME=/path/to/jboss
mvn clean package arquillian:run -Pjavaee -Darquillian=jbossas-managed-7

To deploy the example to jbossas 6:

export JBOSS_HOME=/path/to/jboss
mvn clean package arquillian:run -Pjavaee -Darquillian=jbossas-managed-6

To deploy the example to Glassfish:

mvn clean package arquillian:run -Pjavaee -Darquillian=glassfish-remote-3.1

and then copy the resulting war to the Glassfish deploy directory.

To run ftest following congigurations are supported:

mvn clean verify -Pjavaee -Darquillian=jbossas-managed-7
mvn clean verify -Pjavaee -Darquillian=jbossas-remote-7

mvn clean verify -Pjavaee -Darquillian=jbossas-managed-6
mvn clean verify -Pjavaee -Darquillian=jbossas-remote-6

mvn clean verify -Pjavaee -Darquillian=glassfish-remote-3.1
