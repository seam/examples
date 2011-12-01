OPENID-RP EXAMPLE


What is it? 
===========

This demo web application shows how to turn your application into an OpenID
provider (OP). It makes use of the OpenID submodule of Seam Security.


How to deploy it? 
=================

The application is packaged as a war file and should run in any JEE6
environment. It has been tested on JBoss AS 6. Before deploying the application,
you need to map this host name to the localhost:

www.openid-op.com

On Unix based systems, you do this by putting the following lines in
'/etc/hosts':

127.0.0.1	www.openid-op.com


Some background info
====================

The Identity Provider is preconfigured to run at port 8080 and to use the http
protocol for communicating with Relying Parties (RPs). These settings are ok 
for a test setup, but please be aware that in production, you'd use http on
port 443. In the test application these settings are done programmatically
(by the OpenIdProviderCustomizer).


How to use the application
==========================

Start the application and fetch this URL in your browser:

http://www.openid-op.com:8080/openid-op

There you can login and logout locally. If you also install the example OpenID
relying party, you can experience delegated and single logon.