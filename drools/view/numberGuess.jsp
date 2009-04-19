<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<html>
<head>
<title>Guess a number...</title>
</head>
<body>
<h1>Guess a number...</h1>
<f:view>
	<h:form id="NumberGuessMain">
	    <h:outputText value="Higher!" rendered="#{randomNumber>guess.value}" />
	    <h:outputText value="Lower!" rendered="#{randomNumber<guess.value}" />
		<br />
        I'm thinking of a number between <h:outputText id="Smallest" value="#{game.smallest}" /> and 
        <h:outputText id="Biggest" value="#{game.biggest}" />.
        <br />
        Your guess: 
        <h:inputText value="#{guess.value}" id="inputGuess" required="true">
            <f:validateLongRange
                maximum="#{game.biggest}" 
                minimum="#{game.smallest}"/>
        </h:inputText>
		<h:commandButton id="GuessButton" type="submit" value="Guess" action="guess" />
		<br/>
        <h:message for="guess" style="color: red"/>
	</h:form>
</f:view>
</body>
</html>
