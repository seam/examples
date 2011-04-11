# Seam Wicket Publisher

The Seam Wicket Publisher is implemented using a little Java and mostly the Scala
language. Therefore you might need to install Scala IDE if you want to work on
the sources through e.g. Eclipse. However, there is no need to install anything
in order to compile and run the application.

The Seam Wicket Publisher demonstrates simple publishing for a web site.

## Running in Jetty

	mvn clean install
	
	mvn -Pjetty jetty:run

    http://localhost:9090/wicket-publish

### Known issues (QA, please read)

The integration tests are running under Jetty Eclipse version 8.0.M1, whereas
for running the deployment it's based on Jetty Mortbay version 6.1.26. There
is another profile added to this example POM named 'jetty7', which targets a
runnable deployment for Jetty Eclipse 8.0.M1. Because of some configuration
issue, possible in 'src/main/webapp/META-INF/jetty-env.xml', the container
reports an 'Object is not of type class org.eclipse.jetty.webapp.WebAppContext'.
In order to test it out, you'll need to swap the jetty-env.xml snippet inside
the configuration file in order to test the jetty7 profile. Please refer to the 
config file as it is self-explanatory.

Other knows issues are:

* Seam Persistence TransactionInterceptor won't install, hence prohibits Jetty to start
* Seam Servlet's Catch Filter won't install, hence prohibits Jetty to start
* Arquillian tests cases won't run, except for separately due to 'WELD-001303 No active contexts for scope type javax.enterprise.context.ConversationScoped' 

Due to the issues with the test cases, they are written to assert and prove these errors.
You may comment out the dummyTest and uncomment other tests method. If you run each separate,
it'll "somewhat" work.

The application itself works fine when running/deployed in Jetty 6.1.26.

## Running integration tests

The integration tests are based on Arquillian (http://arquillian.org)

    mvn test -Pitest-jetty-managed

