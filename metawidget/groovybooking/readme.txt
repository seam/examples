Seam Metawidget GroovyBooking Example
=====================================

This is the Hotel Booking example implemented in Groovy Beans and Hibernate JPA
and uses Metawidget to dynamically generate the forms at runtime.

This application runs on JBoss AS, but is deployed as a WAR rather than an EAR.
Thus, you prefix all the typical targets (explode, restart, unexplode) with
"jbosswar." (e.g., jbosswar.explode, jbosswar.restart, jbosswar.unexplode).

Please note that you need to uncomment the loadPersistenceUnits=true property
in build.properties when deploying to JBoss AS 5.

The source files in this example are just the overrides needed to utilize
metawidget. Before the example is built, these overrides are merged with the
original groovybooking source code in a staging directory. This step is performed
by following command:

  ant stage

The stage command is automatically called on any Ant build, so you can simply run:

  ant explode

When editing Groovy files from the src/action directory, you can run "ant
build jbosswar.explode" to see your changes take effect.  When editing Groovy files
from src/model, you need to run "ant build jbosswar.explode jbosswar.restart"

Access the application at http://localhost:8080/jboss-seam-metawidget-groovybooking

For further Metawidget documentation see http://metawidget.org/documentation.html.
