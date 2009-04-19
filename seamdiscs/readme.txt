SeamDiscs Example
=================================

This example runs on JBoss AS 4.2.2.GA or later as an EAR and Tomcat with
Embedded JBoss as a WAR.

Visit http://localhost:8080/seam-discs (on JBoss AS) or 
http://localhost:8080/jboss-seam-discs (on Tomcat).

The seamdiscs example is a simple example built using the Seam Application 
Framework which allows you to record your favourite albums and artists.  It 
uses a mix of RichFaces and Trinidad components. It also uses the "inplace 
editing" pattern; the same facelets are used for editing and display of data 
(login to edit a disc or artist).

The Seam-Trinidad integration (for now built into the example) provides a 
DataModel which, when backed by a Seam Application Framework Query, provides 
lazy loading of data for paging, sorting in the Persistence Context and strong 
row keys.  If you want to use, you'll need to copy the classes in 
org.jboss.seam.trinidad to your own project.  This may be offered as a 
standalone jar in the Seam project in the future.  One caveat is that you must 
ensure the rows property on the <tr:table> is the same as the maxResults 
property on the Query.

Example

<tr:table value="#{discs.dataModel}" rows="#{discs.maxResults}">
  <tr:column>
     ...
  </tr:column
</tr:table>
