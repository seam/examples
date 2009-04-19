<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://jboss.com/products/seam/taglib" prefix="s" %>
<html>
 <head>
  <title>Messages</title>
 </head>
 <body>
  <f:view>
     <h2>Message List</h2>
     <h:outputText id="noMessages" value="No messages to display" rendered="#{messageList.rowCount==0}"/>
     <h:dataTable id="messages" var="msg" value="#{messageList}" rendered="#{messageList.rowCount>0}">
        <h:column>
           <f:facet name="header">
              <h:outputText value="Read"/>
           </f:facet>
           <h:selectBooleanCheckbox id="read" value="#{msg.read}" disabled="true"/>
        </h:column>
        <h:column>
           <f:facet name="header">
              <h:outputText value="Title"/>
           </f:facet>
           <s:link id="link" value="#{msg.title}" action="#{messageManager.select}"/>
        </h:column>
        <h:column>
           <f:facet name="header">
              <h:outputText value="Date/Time"/>
           </f:facet>
           <h:outputText id="date" value="#{msg.datetime}">
              <s:convertDateTime type="both" dateStyle="medium" timeStyle="short"/>
           </h:outputText>
        </h:column>
        <h:column>
           <s:button id="delete" value="Delete" action="#{messageManager.delete}"/>
        </h:column>
     </h:dataTable>
     <h3><h:outputText id="title" value="#{message.title}"/></h3>
     <div><h:outputText id="text" value="#{message.text}"/></div>
  </f:view>
 </body>
</html>