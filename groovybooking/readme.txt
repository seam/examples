Seam GroovyBooking Example
==========================

This is the Hotel Booking example implemented in Groovy Beans and Hibernate JPA.
This application runs on JBoss AS, but is deployed as a WAR rather than an EAR.
Thus, you prefix all the typical targets (explode, restart, unexplode) with
"jbosswar." (e.g., jbosswar.explode, jbosswar.restart, jbosswar.unexplode).

Please note that you need to uncomment the loadPersistenceUnits=true property
in build.properties when deploying to JBoss AS 5.

When editing Groovy files from the src/action directory, you can run ant
"jbosswar.explode" to see your changes take effect.  When editing Groovy files
from src/model, you need to run "ant jbosswar.explode jbosswar.restart"

Access the application at http://localhost:8080/jboss-seam-groovybooking
