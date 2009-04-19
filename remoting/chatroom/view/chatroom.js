// Returns a reference to an object by its id
function getObject(objectId) {
  if (document.getElementById && document.getElementById(objectId))
    return document.getElementById(objectId);
  else if (document.all && document.all(objectId))
    return document.all(objectId);
  else if (document.layers && document.layers[objectId])
    return document.layers[objectId];
  else
    return false;
}

var username = null;
var connectedFlag = false;
var chatroom = Seam.Component.getInstance("chatroomAction");

function connect() {
  var nameCtl = getObject("username");
  username = nameCtl.value;

  var connectCallback = function(connected, context) {
    connectedFlag = true;
    setInterfaceState(connected);
    getObject("username").value = username;
    Seam.Remoting.getContext().setConversationId(context.getConversationId());
  };

  var listUsersCallback = function(users) {
    for (var i = 0; i < users.length; i++)
      addUser(users[i]);
  };

  Seam.Remoting.startBatch();
  chatroom.connect(username, connectCallback);
  chatroom.listUsers(listUsersCallback);
  Seam.Remoting.executeBatch();  
  Seam.Remoting.subscribe("chatroomTopic", channelMessageCallback);  
}

function disconnect() {
  connectedFlag = false;
  Seam.Remoting.unsubscribe("chatroomTopic");
  setInterfaceState(false);
  chatroom.disconnect();
  getObject("userList").options.length = 0;
}

function channelMessageCallback(message) {
  var ctl = getObject("channelDisplay");

  var actionDTO = message.getValue();

  if (actionDTO.action == "message")
    ctl.innerHTML += "<span style='font-weight:bold" + (actionDTO.getUser() == username ? ";color:green" : "") + "'>" + actionDTO.getUser() + "></span> " + actionDTO.getData() + "<br/>";
  else if (actionDTO.action == "connect")
  {
    addUser(actionDTO.getUser());
    ctl.innerHTML += "<span style='font-weight:bold;color:red'>" + actionDTO.getUser() + " connected.</span><br/>";
  }
  else if (actionDTO.action == "disconnect")
  {
    removeUser(actionDTO.getUser());
    ctl.innerHTML += "<span style='font-weight:bold;color:red'>" + actionDTO.getUser() + " disconnected.</span><br/>";
  }

  ctl.scrollTop = ctl.scrollHeight;
}

function addUser(user) {
  var ctl = getObject("userList");
  var found = false;

  for (var i = 0; i < ctl.options.length; i++) {
    if (ctl.options[i].value == user)
    {
      found = true;
      break;
    }
  }

  if (!found)
    ctl.options[ctl.options.length] = new Option(user, user);
}

function removeUser(user) {
  var ctl = getObject("userList");

  for (var i = 0; i < ctl.options.length; i++) {
    if (ctl.options[i].value == user)
      ctl.options[i] = null;
  }
}

function setInterfaceState(connected) {
  getObject("username").readOnly = connected;
  getObject("btnConnect").disabled = connected;
  getObject("btnDisconnect").disabled = !connected;
}

function sendMessage() {
  if (!connectedFlag) {
    alert("Not connected");
    return;
  }

  var ctl = getObject("messageText");
  chatroom.sendMessage(ctl.value);
  ctl.value = "";
  // Force a poll so that we see our new message straight away
  Seam.Remoting.poll();
}

function checkEnterPressed(e) {
  if ((window.event && window.event.keyCode == 13) ||
      (e && e.which == 13))
  {
    sendMessage();

    if (navigator.userAgent.indexOf("MSIE") != -1)
    {
      window.event.cancelBubble = true;
      window.event.returnValue = false;
    }
    else
      e.preventDefault();
  }
}
