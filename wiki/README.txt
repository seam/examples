This is not a regular "Seam example", its a standalone project structure. The build.xml
references the existing Seam libraries and thirdparty libs, however, to avoid duplication.

RUNNING THE UNIT TESTS
==========================================================================================

- Call 'ant test'

- Verify the rest results in build/test-output/


INSTALLATION WITH MYSQL (development profile)
==========================================================================================

- Install JBoss Application Server 4.2.3 GA

- Edit build.properties

- Upgrade/downgrade the Hibernate libraries to the ones bundled with this application:
  'ant upgradehibernate' will replace the libraries in server/default/lib of JBoss AS 4.2.3
  and also copy the required ehcache.jar.
  (Sorry, but no other version works currently and the 4.2.3 bundled libraries are too old.)

- Install MySQL 5.x and start it

- Obtain the correct JDBC driver for your MySQL version and copy it into server/default/lib/ of JBoss AS

- The 'dev' default deployment profile will use the default MySQL 'test' database with user 'test' and
  no password, a fresh database schema will be exported to the database on each redeploy

- Call 'ant deploy'

- Start (if you haven't done so already) JBoss AS and access http://localhost:8080/wiki/ and use the
  default login 'admin' with password 'admin'


INSTALLATION WITH POSTGRESQL (development profile)
==========================================================================================

Follow the installation steps for MySQL but edit the following files and uncomment PostgreSQL 8.3 support:

- src/etc/wiki-dev-ds.xml
- src/etc/META-INF/components-dev.xml
- src/etc/META-INF/persistence-dev-war.xml
- src/main/org/jboss/seam/wiki/core/model/package-info.java

For production packaging (see 'dist' target below), edit the src/etc/**/*-prod* files.


INSTALLATION WITH MYSQL (production profile)
==========================================================================================

- Install JBoss Application Server 4.2.3 GA

- Upgrade/downgrade the Hibernate libraries bundled with JBoss AS to the libraries bundled
  with this application. Follow the steps outlined above (edit build.properties, call
  'ant upgradehibernate') or copy them manually.

- Install MySQL 5.x and start it

- Obtain the correct JDBC driver for your MySQL version and copy it into server/default/lib/ of JBoss AS

- Call 'ant -Dprofile=prod dist'

- Deploy the SQL schema generated in dist/wiki-ddl.sql on your MySQL database

- Customize the SQL default data in dist/wiki-data.sql before you apply it (especially the baseUrl setting)

- Deploy the SQL data generated in dist/wiki-data.sql on your MySQL database

- Copy the file dist/wiki-ds.xml to server/default/deploy/ directory and edit your MySQL connection settings

- Deploy the dist/wiki.war file by copying it into the server/default/deploy/ directory

- Start (if you haven't done so already) JBoss AS and access http://localhost:8080/wiki (or the
  baseUrl you specified on data import)

- Login as admin/admin and update the Lucene index in the 'Administration' screen

NOTE: The Lucene index directory for full-text searching is named "lacewikiIndex" and located in the
current directory. This is the current directory from which you started the application server! If you want
to change this setting, unpack the WAR and change the META-INF/persistence.xml configuration file.


INSTALLATION WITH UNICODE SUPPORT ON MYSQL
==========================================================================================

The database tables in wiki-ddl.sql are automatically created with UTF8 support.

Note that due to URL rewriting rules, stored wiki items (documents, uploaded files) MUST have
at least three latin1 characters in their name! The application will prompt you with a validation
error message when you forget that limitation and enter only non-latin1 characters in a form.

Furthermore, the wiki search engine passes search terms as request parameters in the URI and allows bookmarking
of search terms. If you  require unicode support for search terms, you need to set an option in Tomcat to
enable the correct decoding of URL-encoded request parameter values to UTF-8. To do that, edit

  ${JBOSS_HOME}/server/(default)/deploy/jboss-web.deployer/server.xml

and add

  URIEncoding="UTF-8"

to the <connector> declaration.

