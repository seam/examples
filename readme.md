Seam 3 integrated examples
======================

Integrated, use-case oriented examples for the Seam 3 project that demonstrate
functionality provided by multiple modules.

Seam Security Examples
======================
- `authorization`
- `idmconsole`
- `openid-op`
- `openid-rp`
- `simple`

Running the functional tests

- set `JBOSS_HOME` enviroment property to point to JBoss AS 7 installation
- in the example folder, run `mvn clean verify -Darquillian=jbossas-managed-7`

Besides, the following configurations are supported:

> mvn clean verify -Darquillian=jbossas-managed-6

> mvn clean verify -Darquillian=jbossas-managed-7

> mvn clean verify -Darquillian=glassfish-remote-3.1

> mvn clean verify -Pjbossas6 -Darquillian=jbossas-remote-6

> mvn clean verify -Pjbossas7 -Darquillian=jbossas-remote-7


Running a functional test for openid-rp example
-----------------------------------------------

In addition to the steps above you first need to do the following:

Map this host name to the localhost: `www.openid-rp.com`

On Unix based systems, you do this by putting the following lines in
`/etc/hosts`:

    127.0.0.1   www.openid-rp.com

Furthermore, add credentials for particular accounts (MyOpenID, Google, Yahoo) to:
`openid-rp/src/test/resources/ftest.properties` 
