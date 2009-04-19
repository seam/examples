var webServices = new Object();
var groups = new Object();

ServiceParam = function(name, key)
{
  this.name = name;
  this.key = key; 
  this.value = "#{" + key + "}";
}

ServiceMetadata = function(name, group)
{
  this.name = name;
  this.group = group;
  this.parameters = new Array();
  this.conversational = false;

  webServices[name] = this;

  ServiceMetadata.prototype.setDescription = function(description) { this.description = description; };  
  ServiceMetadata.prototype.getDescription = function() { return this.description; };
  ServiceMetadata.prototype.addParameter = function(param) { this.parameters.push(param); };
  ServiceMetadata.prototype.setRequest = function(request) { this.request = request; };
  ServiceMetadata.prototype.getRequest = function() { return this.request; };
  ServiceMetadata.prototype.setConversational = function(val) { this.conversational = val; };
  ServiceMetadata.prototype.isConversational = function() { return this.conversational; };
}

// start of web service definitions

var svc = new ServiceMetadata("listCategories", "General");
svc.setDescription("List Categories");
svc.setRequest("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
               "\n    xmlns:seam=\"http://seambay.example.seam.jboss.org/\">\n  <soapenv:Header/>" +
               "\n  <soapenv:Body>\n    <seam:listCategories/>\n  </soapenv:Body>\n</soapenv:Envelope>");
               
var svc = new ServiceMetadata("findAuctions", "General");
svc.setDescription("Find Auctions");
svc.setRequest("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
               "\n    xmlns:seam=\"http://seambay.example.seam.jboss.org/\">\n  <soapenv:Header/>" +
               "\n  <soapenv:Body>" +
               "\n    <seam:findAuctions>" +
               "\n      <arg0>#{searchTerm}</arg0>" +
               "\n    </seam:findAuctions>" +
               "\n  </soapenv:Body>\n</soapenv:Envelope>");               
svc.addParameter(new ServiceParam("Search Term", "searchTerm"));               

svc = new ServiceMetadata("login", "Security");
svc.setDescription("Login");
svc.setRequest("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
               "\n    xmlns:sb=\"http://seambay.example.seam.jboss.org/\">\n  <soapenv:Header/>" +
               "\n  <soapenv:Body>" +
               "\n    <sb:login>" +
               "\n      <arg0>#{username}</arg0>" +
               "\n      <arg1>#{password}</arg1>" +
               "\n    </sb:login>" +
               "\n  </soapenv:Body>" +
               "\n</soapenv:Envelope>");
svc.addParameter(new ServiceParam("Username", "username"));
svc.addParameter(new ServiceParam("Password", "password"));    

svc = new ServiceMetadata("logout", "Security");
svc.setDescription("Logout");
svc.setRequest("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
               "\n    xmlns:sb=\"http://seambay.example.seam.jboss.org/\">\n  <soapenv:Header/>" +
               "\n  <soapenv:Body>" +
               "\n    <sb:logout/>" +
               "\n  </soapenv:Body>" +
               "\n</soapenv:Envelope>");  

svc = new ServiceMetadata("createAuction", "Create Auction");
svc.setDescription("Create new auction");
svc.setRequest("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
               "\n    xmlns:sb=\"http://seambay.example.seam.jboss.org/\">\n  <soapenv:Header/>" +
               "\n  <soapenv:Body>" +
               "\n    <sb:createAuction>" +
               "\n      <arg0>#{title}</arg0>" +
               "\n      <arg1>#{description}</arg1>" +
               "\n      <arg2>#{categoryId}</arg2>" +
               "\n    </sb:createAuction>" +
               "\n  </soapenv:Body>" +
               "\n</soapenv:Envelope>");
svc.addParameter(new ServiceParam("Auction title", "title"));
svc.addParameter(new ServiceParam("Description", "description"));
svc.addParameter(new ServiceParam("Category ID", "categoryId"));         

svc = new ServiceMetadata("updateAuctionDetails", "Create Auction");
svc.setDescription("Update auction details");
svc.setRequest("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
               "\n    xmlns:sb=\"http://seambay.example.seam.jboss.org/\">" +
               "\n  <soapenv:Header>" +
               "\n    <seam:conversationId xmlns:seam='http://www.jboss.org/seam/webservice'>#{conversationId}</seam:conversationId>" +
               "\n  </soapenv:Header>" +               
               "\n  <soapenv:Body>" +
               "\n    <sb:updateAuctionDetails>" +
               "\n      <arg0>#{title}</arg0>" +
               "\n      <arg1>#{description}</arg1>" +
               "\n      <arg2>#{categoryId}</arg2>" +
               "\n    </sb:updateAuctionDetails>" +
               "\n  </soapenv:Body>" +
               "\n</soapenv:Envelope>");
svc.addParameter(new ServiceParam("Auction title", "title"));
svc.addParameter(new ServiceParam("Description", "description"));
svc.addParameter(new ServiceParam("Category ID", "categoryId"));      
svc.setConversational(true);

svc = new ServiceMetadata("setAuctionDuration", "Create Auction");
svc.setDescription("Set auction duration");
svc.setRequest("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
               "\n    xmlns:sb=\"http://seambay.example.seam.jboss.org/\">" +
               "\n  <soapenv:Header>" +
               "\n    <seam:conversationId xmlns:seam='http://www.jboss.org/seam/webservice'>#{conversationId}</seam:conversationId>" +
               "\n  </soapenv:Header>" +
               "\n  <soapenv:Body>" +
               "\n    <sb:setAuctionDuration>" +
               "\n      <arg0>#{duration}</arg0>" +
               "\n    </sb:setAuctionDuration>" +
               "\n  </soapenv:Body>" +
               "\n</soapenv:Envelope>");
svc.addParameter(new ServiceParam("Duration in days", "duration"));
svc.setConversational(true);

svc = new ServiceMetadata("setAuctionPrice", "Create Auction");
svc.setDescription("Set starting price");
svc.setRequest("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
               "\n    xmlns:sb=\"http://seambay.example.seam.jboss.org/\">" +
               "\n  <soapenv:Header>" +
               "\n    <seam:conversationId xmlns:seam='http://www.jboss.org/seam/webservice'>#{conversationId}</seam:conversationId>" +
               "\n  </soapenv:Header>" +
               "\n  <soapenv:Body>" +
               "\n    <sb:setAuctionPrice>" +
               "\n      <arg0>#{price}</arg0>" +
               "\n    </sb:setAuctionPrice>" +
               "\n  </soapenv:Body>" +
               "\n</soapenv:Envelope>");
svc.addParameter(new ServiceParam("Starting price", "price"));
svc.setConversational(true);

svc = new ServiceMetadata("getNewAuctionDetails", "Create Auction");
svc.setDescription("Get the auction details");
svc.setRequest("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
               "\n    xmlns:sb=\"http://seambay.example.seam.jboss.org/\">" +
               "\n  <soapenv:Header>" +
               "\n    <seam:conversationId xmlns:seam='http://www.jboss.org/seam/webservice'>#{conversationId}</seam:conversationId>" +
               "\n  </soapenv:Header>" +
               "\n  <soapenv:Body>" +
               "\n    <sb:getNewAuctionDetails/>" +
               "\n </soapenv:Body>" +
               "\n</soapenv:Envelope>");
svc.setConversational(true);               

svc = new ServiceMetadata("confirmAuction", "Create Auction");
svc.setDescription("Confirm auction");
svc.setRequest("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
               "\n    xmlns:sb=\"http://seambay.example.seam.jboss.org/\">" +
               "\n  <soapenv:Header>" +
               "\n    <seam:conversationId xmlns:seam='http://www.jboss.org/seam/webservice'>#{conversationId}</seam:conversationId>" +
               "\n  </soapenv:Header>" +               
               "\n  <soapenv:Body>" +
               "\n    <sb:confirmAuction/>" +
               "\n  </soapenv:Body>" +
               "\n</soapenv:Envelope>");
svc.setConversational(true);

// end of web service definitions

function getEndpoint()
{
  return document.getElementById("endpoint").value; 
}

var selectedService = null;         

function setAllParams()
{
  if (!selectedService)
    return;
  
  var request = selectedService.request;  
  
  for (var i = 0; i < selectedService.parameters.length; i++)
  {
    var param = selectedService.parameters[i];
    var search = "#{" + param.key + "}";
    
    request = request.replace(search, param.value);
  } 
  
  // Set the conversation ID
  request = request.replace("#{conversationId}", document.getElementById("conversationId").value);
  
  document.getElementById("serviceRequest").value = request;  
}

function setParamValue(event)
{
  var ctl = null
  if (event.target)
    ctl = event.target;
  else if (window.event.srcElement)
    ctl = window.event.srcElement;
    
  var key = ctl.id;
  
  for (var i = 0; i < selectedService.parameters.length; i++)
  {
    var param = selectedService.parameters[i];
    if (param.key == key)
    {
      param.value = ctl.value;
      break;
    }
  } 
  
  setAllParams();
}

function selectService(serviceName)
{
  var svc = webServices[serviceName];
  
  if (!svc)
  {
    alert("Unknown service");
    return;
  }
  
  selectedService = svc;
  
  document.getElementById("selectedService").innerHTML = svc.getDescription();
  document.getElementById("serviceResponse").value = null;
  
  var ctl = document.getElementById("parameters");
  for (var i = ctl.childNodes.length - 1; i >= 0; i--)
  {
     ctl.removeChild(ctl.childNodes[i]);
  }

  var tbl = document.createElement("table");
  tbl.cellspacing = 0;
  tbl.cellpadding = 0;
  
  ctl.appendChild(tbl);
    
  for (var i = 0; i < svc.parameters.length; i++)
  {
     var row = tbl.insertRow(-1);
     
     var td = document.createElement("td");
     row.appendChild(td);
     td.innerHTML = svc.parameters[i].name;
          
     var inp = document.createElement("input");

     inp.id = svc.parameters[i].key;
     inp.value = svc.parameters[i].value;
     inp.onchange = setParamValue;
     inp.onkeyup = setParamValue;
     
     td = document.createElement("td");
     row.appendChild(td);
     td.appendChild(inp);
  }
  
  document.getElementById("conversationId").readOnly = !svc.isConversational();
  
  setAllParams();
}

function sendRequest()
{
  if (!selectedService)
  {
    alert("Please select a service first");
    return;
  }
  
  document.getElementById("serviceResponse").value = null;
  
  var req;
  if (window.XMLHttpRequest)
  {
    req = new XMLHttpRequest();
    if (req.overrideMimeType)
      req.overrideMimeType("text/xml");
  }
  else
    req = new ActiveXObject("Microsoft.XMLHTTP");
    
  req.onreadystatechange = function() { receiveResponse(req); };
  req.open("POST", getEndpoint(), true);
  req.setRequestHeader("Content-type", "text/xml");
  req.send(document.getElementById("serviceRequest").value);
}

function receiveResponse(req)
{
  if (req.readyState == 4)
  {
    if (req.responseText)
      document.getElementById("serviceResponse").value = req.responseText;
      
    if (req.responseXML)
    {
      var cid = extractConversationId(req.responseXML);
      
      if (cid)
      {
        document.getElementById("conversationId").value = cid;
      }
    }
      
    if (req.status != 200)
    {
      alert("There was an error processing your request.  Error code: " + req.status);      
    }
  }  
}

function extractConversationId(doc)
{
  var headerNode;

  if (doc.documentElement)
  {
    for (var i = 0; i < doc.documentElement.childNodes.length; i++)
    {
      var node = doc.documentElement.childNodes.item(i);
      if (node.localName == "Header")
        headerNode = node;
    }
  }

  if (headerNode)
  {
    for (var i = 0; i < headerNode.childNodes.length; i++)
    {
      var node = headerNode.childNodes.item(i);
      if (node.localName == "conversationId")
      {
        return node.firstChild.nodeValue;
      }
    }
  }    
}

function initServices()
{
  for (var i in webServices)
  {    
    var ws = webServices[i];
    
    var anchor = document.createElement("a");
    anchor.href = "javascript:selectService('" + ws.name + "')";  
    anchor.appendChild(document.createTextNode(ws.getDescription()));

    if (!groups[ws.group])
    {
      groups[ws.group] = document.createElement("div");
      var groupTitle = document.createElement("span");
      groupTitle.appendChild(document.createTextNode(ws.group));
      groups[ws.group].appendChild(groupTitle);
      document.getElementById("services").appendChild(groups[ws.group]); 
    }
    
    groups[ws.group].appendChild(anchor);    
  }
}

initServices();