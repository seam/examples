DEPLOY
====================
For JBoss AS don't forget to set the JBOSS_HOME variable to your jboss installation 
Deploy to jbossas 7:
---------------------

mvn clean package arquillian:run -Darquillian=jbossas-managed-7

Deploy to jbossass 6:
---------------------

mvn clean package arquillian:run -Darquillian=jbossas-managed-6

Deploy to glassfish :
---------------------

mvn clean package arquillian:run -Darquillian=glassfish-remote-3.1


RUN FTEST
=====================
You can run functional tests using following configurations:

mvn clean verify -Darquillian=jbossas-managed-7
mvn clean verify -Darquillian=jbossas-remote-7

mvn clean verify -Darquillian=jbossas-managed-6
mvn clean verify -Darquillian=jbossas-remote-6 

mvn clean verify -Darquillian=glassfish-remote-3.1
