OPENID-RP EXAMPLE


What is it? 
===========

This demo shows a web application that uses OpenID to authenticate users (i.e. a
web application that acts as an OpenID Relying Party).


How to deploy it? 
=================

The application is packaged as a war file and should run in any JEE6
environment. It has been tested on JBoss AS 6. Before deploying the application,
you need to map this host name to the localhost:

www.openid-rp.com

On Unix based systems, you do this by putting the following lines in
'/etc/hosts':

127.0.0.1	www.openid-rp.com


Some background info
====================

The Relying Party is preconfigured to run at port 8080, and to use the
http protocol for communicating with OPs. These settings are ok for a test setup,
but please be aware that in production, you'd use http on port 443. In the test 
application these settings are done programmatically (by the
OpenIdRelyingPartyCustomizer).


How to use the application
==========================

Start the application and fetch this URL in your browser:

http://www.openid-rp.com:8080/openid-rp

Go to the login page. There you'll find three OpenID Providers (OPs) to choose
from. Just choose one where you have an account, and you'll be redirected to the
site of that provider. There you authenticate, and there you grant the OpenID
provider to send your OpenID identifier, and your e-mail address, to the relying
party, which is the sample application in this case (which won't do anything
with your e-mail address except from displaying it).

Some OpenID providers will prove reluctant for sending the e-mail attribute, or
will advice you not to grant permissions to this relying party. In a production
scenario you won't have this, because you would:
- use https instead of http
- use a host name that is registered in DNS
- use an HTML meta-tag in the file https://www.openid-rp.com to refer to the
XRDS file that describes the relying party service endpoint

You could have a look at the Configuration page to see what is the realm of the
OpenID Relying party (it's derived from the host name). On that page you'll also
find a link to the XRDS file that contains meta data about the relying party.