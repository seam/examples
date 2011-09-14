<%--
  JBoss, Home of Professional Open Source
  Copyright 2011, Red Hat, Inc., and individual contributors
  by the @authors tag. See the copyright.txt in the distribution for a
  full listing of individual contributors.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<fmt:bundle basename="org.jboss.seam.exception.examples.basicservlet.messages">
   <html>
   <head>
      <title><fmt:message key="index_title"/></title>
   </head>
   <body>
   <h1></h1>

   <p><fmt:message key="index_links_desc"/></p>
   <ul>
      <li><a href="Navigation/NullPointerException"><fmt:message key="index_links_nullpointer"/></a></li>
      <li><a href="Navigation/AssertionError"><fmt:message key="index_links_assertionerror"/></a></li>
      <li><a href="Navigation/WrappedIllegalArg"><fmt:message key="index_links_wrappedillegalarg"/></a></li>
      <li><a href="Navigation/IOException"><fmt:message key="index_links_ioexception"/></a></li>
   </ul>
   <h3><fmt:message key="index_handler_declartions"/></h3>
   <code>
            <pre>
   @HandlesExceptions
   public class StandardHandlerDeclaration
   {
      final ResourceBundle messages =
      ResourceBundle.getBundle("org.jboss.seam.exception.examples.basicservlet.messages");

      public void throwableHandler(@Handles(during = TraversalMode.BREADTH_FIRST) CaughtException &lt;Throwable&gt; event, HttpServletResponse response)
      {
         HandlerOutput.printToResponse(this.messages, event.getException(), response, "throwableHandler",
         "markHandled");

         event.unmute();
         event.markHandled();
      }

      public void assertionErrorHandler(@Handles CaughtException &lt;AssertionError&gt; event, HttpServletResponse response)
      {
         HandlerOutput.printToResponse(this.messages, event.getException(), response, "assertionErrorHandler",
         "rethrow");

         event.rethrow();
      }

      public void nullPointerHandler(@Handles CaughtException &lt;NullPointerException&gt; event, HttpServletResponse response)
      {
         HandlerOutput.printToResponse(this.messages, event.getException(), response, "nullPointerHandler", "handled");

         event.handled();
      }

      public void illegalArgumenBreadthFirsttHandler(@Handles(during = TraversalMode.BREADTH_FIRST) CaughtException &lt;IllegalArgumentException&gt; event, HttpServletResponse response)
      {
         HandlerOutput.printToResponse(this.messages, event.getException(), response, "illegalArgumentBreadthFirstHandler", "dropCause");

         event.dropCause();
      }

      public void illegalArgumentHandler(@Handles CaughtException &lt;IllegalArgumentException&gt; event, HttpServletResponse response)
      {
         HandlerOutput.printToResponse(this.messages, event.getException(), response, "illegalArgumentHandler", "handled");

         event.handled();
      }

      public void illegalStateHandler(@Handles CaughtException &lt;IllegalStateException&gt; event, HttpServletResponse response)
      {
         HandlerOutput.printToResponse(this.messages, event.getException(), response, "illegalStateHandler", "abort");

         event.abort();
      }

      public void ioExceptionHandler(@Handles CaughtException &lt;IOException&gt; event, HttpServletResponse response)
      {
         HandlerOutput.printToResponse(this.messages, event.getException(), response, "illegalStateHandler", "rethrow(new ArithmeticException)");

         event.rethrow(new ArithmeticException("Re-thrown"));
      }
   }
            </pre>
   </code>
   </body>
   </html>
</fmt:bundle>
