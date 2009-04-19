<%@ page import="java.io.PrintWriter" %>
<%@ page import="org.jboss.seam.wiki.util.WikiUtil" %>
<html>
<head>
  <meta HTTP-EQUIV="Content-Type" CONTENT="text/html;charset=UTF-8" />
  <title>Generic error message</title>
</head>
<body>
<style>
body {
	font-family : arial, verdana, Geneva, Arial, Helvetica, sans-serif;
    font-size : 1.1em;
}
.errorHeader {
	font-size: 1.6em;
	background-color: #6392C6;
	color: white;
	font-weight: bold;
	padding: 3px;
	margin-bottom: 10px;
}

.errorFooter {
	font-size: 0.8em;
	background-color: #6392C6;
	color: white;
	font-style: italic;
	padding: 3px;
	margin-top: 5px;
}

.errorMessage {
	color: red;
	font-weight: bold;
}
.errorExceptions {
}
.errorExceptionStack {
	margin-top: 5px;
	padding: 3px;
	border-style: solid;
	border-width: 1px;
	border-color: #9F9F9F;
	background-color: #E0E0E0;
}
.errorExceptionCause {
	font-size: 1.1em;
	padding: 3px;
	border-style: solid;
	border-width: 1px;
	border-color: #9F9F9F;
	background-color: #E0E0E0;
}
.errorException {
	font-size: 1.0em;
}
</style>
<div class="errorHeader">Your request was not successful, server-side error encountered.</div>

<%
Object exception = request.getAttribute("javax.servlet.error.exception");
if (exception != null)
{
	Throwable throwable = WikiUtil.unwrap((Throwable)exception);
	String exceptionMessage = throwable.getMessage();

	%>Message: <span class="errorMessage"><%=exceptionMessage%></span><%

	PrintWriter pw = new PrintWriter(out);

 	%><br/><%
	%><span id="errorDetails" class="errorExceptions"><%
		%><pre class="errorExceptionCause"><%
		throwable.printStackTrace(pw);
		%></pre><%

	 	%><input type="button" value="More Details>>" onclick="document.getElementById('errorMoreDetails').style.display=''"/><%
	 	%><div id="errorMoreDetails" style="display:none" class="errorExceptionStack"><%

			%><pre class="errorException"><%
			throwable.printStackTrace(pw);
			%></pre><%

		%></div><%
	%></span><%
}
else
{
	%>Unknown error<%
}
%>
<div class="errorFooter">End of exception report</div>
</body>
</html>