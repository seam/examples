<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<html>
<head>
<title>You won!</title>
</head>
<body>
<h1>You won!</h1>
<f:view>
    Yes, the answer was <h:outputText value="#{randomNumber}" />.
    It took you <h:outputText value="#{game.guessCount}" /> guesses.
    Would you like to <a href="numberGuess.seam">play again</a>?
  </f:view>
</body>
</html>
