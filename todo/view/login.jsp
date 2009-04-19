<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<html>
<head>
<title>Login</title>
</head>
<body>
<h1>Login</h1>
<f:view>
	<h:form id="login">
	  <div>
	    <h:inputText id="username" value="#{login.user}"/>
	    <h:commandButton id="submit" value="Login" action="#{login.login}"/>
	  </div>
	</h:form>
</f:view>
</body>
</html>
