<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://jboss.com/products/seam/taglib" prefix="s"%>
<html>
<head>
<title>Todo List</title>
</head>
<body>
<h1>Todo List</h1>
<f:view>
   <h:form id="list">
      <div>
         <h:outputText id="noItems" value="There are no todo items." rendered="#{empty taskInstancePriorityList}"/>
         <h:dataTable id="items" value="#{taskInstancePriorityList}" var="task" rendered="#{not empty taskInstancePriorityList}">
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Description"/>
                </f:facet>
                <h:inputText id="description" value="#{task.description}" style="width: 400"/>
            </h:column>
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Created"/>
                </f:facet>
                <h:outputText value="#{task.taskMgmtInstance.processInstance.start}">
                    <s:convertDateTime type="date"/>
                </h:outputText>
            </h:column>
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Priority"/>
                </f:facet>
                <h:inputText id="priority" value="#{task.priority}" style="width: 30"/>
            </h:column>
            <h:column>
                <f:facet name="header">
                    <h:outputText value="Due Date"/>
                </f:facet>
                <h:inputText id="dueDate" value="#{task.dueDate}" style="width: 100">
                    <s:convertDateTime type="date" dateStyle="short"/>
                </h:inputText>
            </h:column>
            <h:column>
                <s:button id="done" action="#{todoList.done}" taskInstance="#{task}" value="Done"/>
            </h:column>
         </h:dataTable>
      </div>
      <div>
      <h:messages/>
      </div>
      <div>
         <h:commandButton id="update" value="Update Items" rendered="#{not empty taskInstanceList}"/>
      </div>
   </h:form>
   <h:form id="new">
      <div>
         <h:inputText id="description" value="#{todoList.description}" style="width: 400"/>
         <h:commandButton id="create" value="Create New Item" action="#{todoList.createTodo}"/>
      </div>
   </h:form>
</f:view>
</body>
</html>
