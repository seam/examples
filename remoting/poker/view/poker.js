var Poker = new Object();

Poker.login = function()
{
  var playerName = document.getElementById("playerName").value;
  Seam.Component.getInstance("playerAction").login(playerName, Poker.loginCallback);
}

Poker.loginCallback = function(success)
{
  if (success)
  {
    alert("login successful"); 
  } 
}