/*#############################################################
Name: Niceforms
Version: 1.0
Author: Lucian Slatineanu
URL: http://www.badboy.ro/

Feel free to use and modify but please provide credits.
#############################################################*/

//Global Variables
var niceforms = document.getElementsByTagName('form'); var inputs = new Array(); var labels = new Array(); var radios = new Array(); var radioLabels = new Array(); var checkboxes = new Array(); var checkboxLabels = new Array(); var texts = new Array(); var textareas = new Array(); var selects = new Array(); var selectText = "please select"; var agt = navigator.userAgent.toLowerCase(); this.ie = ((agt.indexOf("msie") != -1) && (agt.indexOf("opera") == -1)); var hovers = new Array(); var buttons = new Array(); var isMac = new RegExp('(^|)'+'Apple'+'(|$)');

//Theme Variables - edit these to match your theme
var selectRightSideWidth = 21;
var selectLeftSideWidth = 8;
var selectAreaHeight = 21;
var selectAreaOptionsOverlap = 2;
var imagesPath = "images/";

//Initialization function - if you have any other 'onload' functions, add them here
function init() {
	if(!document.getElementById) {return false;}
	preloadImages();
	getElements();
	separateElements();
	replaceRadios();
	replaceCheckboxes();
	replaceSelects();
	if(!isMac.test(navigator.vendor)) {
		replaceTexts();
		replaceTextareas();
		buttonHovers();
	}
}


//preloading required images
function preloadImages() {
	preloads = new Object();
	preloads[0] = new Image(); preloads[0].src = imagesPath + "button_left_xon.gif";
	preloads[1] = new Image(); preloads[1].src = imagesPath + "button_right_xon.gif";
	preloads[2] = new Image(); preloads[2].src = imagesPath + "input_left_xon.gif";
	preloads[3] = new Image(); preloads[3].src = imagesPath + "input_right_xon.gif";
	preloads[4] = new Image(); preloads[4].src = imagesPath + "txtarea_bl_xon.gif";
	preloads[5] = new Image(); preloads[5].src = imagesPath + "txtarea_br_xon.gif";
	preloads[6] = new Image(); preloads[6].src = imagesPath + "txtarea_cntr_xon.gif";
	preloads[7] = new Image(); preloads[7].src = imagesPath + "txtarea_l_xon.gif";
	preloads[8] = new Image(); preloads[8].src = imagesPath + "txtarea_tl_xon.gif";
	preloads[9] = new Image(); preloads[9].src = imagesPath + "txtarea_tr_xon.gif";
}
//getting all the required elements
function getElements() {
	var re = new RegExp('(^| )'+'niceform'+'( |$)');
	for (var nf = 0; nf < document.getElementsByTagName('form').length; nf++) {
		if(re.test(niceforms[nf].className)) {
			for(var nfi = 0; nfi < document.forms[nf].getElementsByTagName('input').length; nfi++) {inputs.push(document.forms[nf].getElementsByTagName('input')[nfi]);}
			for(var nfl = 0; nfl < document.forms[nf].getElementsByTagName('label').length; nfl++) {labels.push(document.forms[nf].getElementsByTagName('label')[nfl]);}
			for(var nft = 0; nft < document.forms[nf].getElementsByTagName('textarea').length; nft++) {textareas.push(document.forms[nf].getElementsByTagName('textarea')[nft]);}
			for(var nfs = 0; nfs < document.forms[nf].getElementsByTagName('select').length; nfs++) {selects.push(document.forms[nf].getElementsByTagName('select')[nfs]);}
		}
	}
}
//separating all the elements in their respective arrays
function separateElements() {
	var r = 0; var c = 0; var t = 0; var rl = 0; var cl = 0; var tl = 0; var b = 0;
	for (var q = 0; q < inputs.length; q++) {
		if(inputs[q].type == 'radio') {
			radios[r] = inputs[q]; ++r;
			for(var w = 0; w < labels.length; w++) {if(labels[w].htmlFor == inputs[q].id) {if(inputs[q].checked) {labels[w].className = "chosen";} radioLabels[rl] = labels[w]; ++rl;}}
		}
		if(inputs[q].type == 'checkbox') {
			checkboxes[c] = inputs[q]; ++c;
			for(var w = 0; w < labels.length; w++) {if(labels[w].htmlFor == inputs[q].id) {if(inputs[q].checked) {labels[w].className = "chosen";} checkboxLabels[cl] = labels[w]; ++cl;}}
		}
		if((inputs[q].type == "text") || (inputs[q].type == "password")) {texts[t] = inputs[q]; ++t;}
		if((inputs[q].type == "submit") || (inputs[q].type == "button")) {buttons[b] = inputs[q]; ++b;}
	}
}
function replaceRadios() {
	for (var q = 0; q < radios.length; q++) {
		//move radios out of the way
		radios[q].className = "outtaHere";
		//create div
		var radioArea = document.createElement('div');
		if(radios[q].checked) {radioArea.className = "radioAreaChecked";} else {radioArea.className = "radioArea";}
		radioArea.style.left = findPosX(radios[q]) + 'px';
		radioArea.style.top = findPosY(radios[q]) + 'px';
		radioArea.style.margin = "1px";
		radioArea.id = "myRadio" + q;
		//insert div
		radios[q].parentNode.insertBefore(radioArea, radios[q]);
		//assign actions
		radioArea.onclick = new Function('rechangeRadios('+q+')');
		radioLabels[q].onclick = new Function('rechangeRadios('+q+')');
		if(!this.ie) {radios[q].onfocus = new Function('focusRadios('+q+')'); radios[q].onblur = new Function('blurRadios('+q+')');}
		radios[q].onclick = radioEvent;
	}
	return true;
}
function focusRadios(who) {
	var what = document.getElementById('myRadio'+who);
	what.style.border = "1px dotted #333"; what.style.margin = "0";
	return false;
}
function blurRadios(who) {
	var what = document.getElementById('myRadio'+who);
	what.style.border = "0"; what.style.margin = "1px";
	return false;
}
function checkRadios(who) {
	var what = document.getElementById('myRadio'+who);
	others = document.getElementsByTagName('div');
	for(var q = 0; q < others.length; q++) {if((others[q].className == "radioAreaChecked")&&(others[q].nextSibling.name == radios[who].name)) {others[q].className = "radioArea";}}
	what.className = "radioAreaChecked";
}
function changeRadios(who) {
	if(radios[who].checked) {
		for(var q = 0; q < radios.length; q++) {if(radios[q].name == radios[who].name) {radios[q].checked = false; radioLabels[q].className = "";}} 
		radios[who].checked = true; radioLabels[who].className = "chosen";
		checkRadios(who);
	}
}
function rechangeRadios(who) {
	if(!radios[who].checked) {
		for(var q = 0; q < radios.length; q++) {if(radios[q].name == radios[who].name) {radios[q].checked = false; radioLabels[q].className = "";}}
		radios[who].checked = true; radioLabels[who].className = "chosen";
		checkRadios(who);
	}
}
function radioEvent(e) {
	if (!e) var e = window.event;
	if(e.type == "click") {for (var q = 0; q < radios.length; q++) {if(this == radios[q]) {changeRadios(q); break;}}}
}
function replaceCheckboxes() {
	for (var q = 0; q < checkboxes.length; q++) {
		//move checkboxes out of the way
		checkboxes[q].className = "outtaHere";
		//create div
		var checkboxArea = document.createElement('div');
		if(checkboxes[q].checked) {checkboxArea.className = "checkboxAreaChecked";} else {checkboxArea.className = "checkboxArea";}
		checkboxArea.style.left = findPosX(checkboxes[q]) + 'px';
		checkboxArea.style.top = findPosY(checkboxes[q]) + 'px';
		checkboxArea.style.margin = "1px";
		checkboxArea.id = "myCheckbox" + q;
		//insert div
		checkboxes[q].parentNode.insertBefore(checkboxArea, checkboxes[q]);
		//asign actions
		checkboxArea.onclick = new Function('rechangeCheckboxes('+q+')');
		if(!isMac.test(navigator.vendor)) {checkboxLabels[q].onclick = new Function('changeCheckboxes('+q+')');}
		else {checkboxLabels[q].onclick = new Function('rechangeCheckboxes('+q+')');}
		if(!this.ie) {checkboxes[q].onfocus = new Function('focusCheckboxes('+q+')'); checkboxes[q].onblur = new Function('blurCheckboxes('+q+')');}
		checkboxes[q].onkeydown = checkEvent;
	}
	return true;
}
function focusCheckboxes(who) {
	var what = document.getElementById('myCheckbox'+who);
	what.style.border = "1px dotted #333"; what.style.margin = "0";
	return false;
}
function blurCheckboxes(who) {
	var what = document.getElementById('myCheckbox'+who);
	what.style.border = "0"; what.style.margin = "1px";
	return false;
}
function checkCheckboxes(who, action) {
	var what = document.getElementById('myCheckbox'+who);
	if(action == true) {what.className = "checkboxAreaChecked";}
	if(action == false) {what.className = "checkboxArea";}
}
function changeCheckboxes(who) {
	if(checkboxLabels[who].className == "chosen") {
		checkboxes[who].checked = true;
		checkboxLabels[who].className = "";
		checkCheckboxes(who, false);
	}
	else if(checkboxLabels[who].className == "") {
		checkboxes[who].checked = false;
		checkboxLabels[who].className = "chosen";
		checkCheckboxes(who, true);
	}
}
function rechangeCheckboxes(who) {
	var tester = false;
	if(checkboxLabels[who].className == "chosen") {
		tester = false;
		checkboxLabels[who].className = "";
	}
	else if(checkboxLabels[who].className == "") {
		tester = true;
		checkboxLabels[who].className = "chosen";
	}
	checkboxes[who].checked = tester;
	checkCheckboxes(who, tester);
}
function checkEvent(e) {
	if (!e) var e = window.event;
	if(e.keyCode == 32) {for (var q = 0; q < checkboxes.length; q++) {if(this == checkboxes[q]) {changeCheckboxes(q);}}} //check if space is pressed
}
function replaceSelects() {
    for(var q = 0; q < selects.length; q++) {
		//create and build div structure
		var selectArea = document.createElement('div');
		var left = document.createElement('div');
		var right = document.createElement('div');
		var center = document.createElement('div');
		var button = document.createElement('a');
		var text = document.createTextNode(selectText);
		center.id = "mySelectText"+q;
		var selectWidth = parseInt(selects[q].className.replace(/width_/g, ""));
		center.style.width = selectWidth - 10 + 'px';
		selectArea.style.width = selectWidth + selectRightSideWidth + selectLeftSideWidth + 'px';
		button.style.width = selectWidth + selectRightSideWidth + selectLeftSideWidth + 'px';
		button.style.marginLeft = - selectWidth - selectLeftSideWidth + 'px';
		button.href = "javascript:showOptions("+q+")";
		button.onkeydown = selectEvent;
		button.className = "selectButton"; //class used to check for mouseover
		selectArea.className = "selectArea";
		selectArea.id = "sarea"+q;
		left.className = "left";
		right.className = "right";
		center.className = "center";
		right.appendChild(button);
		center.appendChild(text);
		selectArea.appendChild(left);
		selectArea.appendChild(right);
		selectArea.appendChild(center);
		//hide the select field
        selects[q].style.display='none'; 
		//insert select div
		selects[q].parentNode.insertBefore(selectArea, selects[q]);
		//build & place options div
		var optionsDiv = document.createElement('div');
		optionsDiv.style.width = selectWidth + 1 + 'px';
		optionsDiv.className = "optionsDivInvisible";
		optionsDiv.id = "optionsDiv"+q;
		optionsDiv.style.left = findPosX(selectArea) + 'px';
		optionsDiv.style.top = findPosY(selectArea) + selectAreaHeight - selectAreaOptionsOverlap + 'px';
		//get select's options and add to options div
		for(var w = 0; w < selects[q].options.length; w++) {
			var optionHolder = document.createElement('p');
			var optionLink = document.createElement('a');
			var optionTxt = document.createTextNode(selects[q].options[w].text);
			optionLink.href = "javascript:showOptions("+q+"); selectMe('"+selects[q].id+"',"+w+","+q+");";
			optionLink.appendChild(optionTxt);
			optionHolder.appendChild(optionLink);
			optionsDiv.appendChild(optionHolder);
			//check for pre-selected items
			if(selects[q].options[w].selected) {selectMe(selects[q].id,w,q);}
		}
		//insert options div
		document.getElementsByTagName("body")[0].appendChild(optionsDiv);
	}
}
function showOptions(g) {
		elem = document.getElementById("optionsDiv"+g);
		if(elem.className=="optionsDivInvisible") {elem.className = "optionsDivVisible";}
		else if(elem.className=="optionsDivVisible") {elem.className = "optionsDivInvisible";}
		elem.onmouseout = hideOptions;
}
function hideOptions(e) { //hiding the options on mouseout
	if (!e) var e = window.event;
	var reltg = (e.relatedTarget) ? e.relatedTarget : e.toElement;
	if(((reltg.nodeName != 'A') && (reltg.nodeName != 'DIV')) || ((reltg.nodeName == 'A') && (reltg.className=="selectButton") && (reltg.nodeName != 'DIV'))) {this.className = "optionsDivInvisible";};
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
}
function selectMe(selectFieldId,linkNo,selectNo) {
	//feed selected option to the actual select field
	selectField = document.getElementById(selectFieldId);
	for(var k = 0; k < selectField.options.length; k++) {
		if(k==linkNo) {selectField.options[k].selected = "selected";}
		else {selectField.options[k].selected = "";}
	}
	//show selected option
	textVar = document.getElementById("mySelectText"+selectNo);
	var newText = document.createTextNode(selectField.options[linkNo].text);
	textVar.replaceChild(newText, textVar.childNodes[0]);
}
function selectEvent(e) {
	if (!e) var e = window.event;
	var thecode = e.keyCode;
	switch(thecode){
		case 40: //down
			var fieldId = this.parentNode.parentNode.id.replace(/sarea/g, "");
			var linkNo = 0;
			for(var q = 0; q < selects[fieldId].options.length; q++) {if(selects[fieldId].options[q].selected) {linkNo = q;}}
			++linkNo;
			if(linkNo >= selects[fieldId].options.length) {linkNo = 0;}
			selectMe(selects[fieldId].id, linkNo, fieldId);
			break;
		case 38: //up
			var fieldId = this.parentNode.parentNode.id.replace(/sarea/g, "");
			var linkNo = 0;
			for(var q = 0; q < selects[fieldId].options.length; q++) {if(selects[fieldId].options[q].selected) {linkNo = q;}}
			--linkNo;
			if(linkNo < 0) {linkNo = selects[fieldId].options.length - 1;}
			selectMe(selects[fieldId].id, linkNo, fieldId);
			break;
		default:
			break;
	}
}
function replaceTexts() {
	for(var q = 0; q < texts.length; q++) {
		texts[q].style.width = texts[q].size * 10 + 'px';
		txtLeft = document.createElement('img'); txtLeft.src = imagesPath + "input_left.gif"; txtLeft.className = "inputCorner";
		txtRight = document.createElement('img'); txtRight.src = imagesPath + "input_right.gif"; txtRight.className = "inputCorner";
		texts[q].parentNode.insertBefore(txtLeft, texts[q]);
		texts[q].parentNode.insertBefore(txtRight, texts[q].nextSibling);
		texts[q].className = "textinput";
		//create hovers
		texts[q].onfocus = function() {
			this.className = "textinputHovered";
			this.previousSibling.src = imagesPath + "input_left_xon.gif";
			this.nextSibling.src = imagesPath + "input_right_xon.gif";
		}
		texts[q].onblur = function() {
			this.className = "textinput";
			this.previousSibling.src = imagesPath + "input_left.gif";
			this.nextSibling.src = imagesPath + "input_right.gif";
		}
	}
}
function replaceTextareas() {
	for(var q = 0; q < textareas.length; q++) {
		var where = textareas[q].parentNode;
		var where2 = textareas[q].previousSibling;
		textareas[q].style.width = textareas[q].cols * 10 + 'px';
		textareas[q].style.height = textareas[q].rows * 10 + 'px';
		//create divs
		var container = document.createElement('div');
		container.className = "txtarea";
		container.style.width = textareas[q].cols * 10 + 20 + 'px';
		container.style.height = textareas[q].rows * 10 + 20 + 'px';
		var topRight = document.createElement('div');
		topRight.className = "tr";
		var topLeft = document.createElement('img');
		topLeft.className = "txt_corner";
		topLeft.src = imagesPath + "txtarea_tl.gif";
		var centerRight = document.createElement('div');
		centerRight.className = "cntr";
		var centerLeft = document.createElement('div');
		centerLeft.className = "cntr_l";
		if(!this.ie) {centerLeft.style.height = textareas[q].rows * 10 + 10 + 'px';}
		else {centerLeft.style.height = textareas[q].rows * 10 + 12 + 'px';}
		var bottomRight = document.createElement('div');
		bottomRight.className = "br";
		var bottomLeft = document.createElement('img');
		bottomLeft.className = "txt_corner";
		bottomLeft.src = imagesPath + "txtarea_bl.gif";
		//assemble divs
		container.appendChild(topRight);
		topRight.appendChild(topLeft);
		container.appendChild(centerRight);
		centerRight.appendChild(centerLeft);
		centerRight.appendChild(textareas[q]);
		container.appendChild(bottomRight);
		bottomRight.appendChild(bottomLeft);
		//insert structure
		where.insertBefore(container, where2);
		//create hovers
		textareas[q].onfocus = function() {
			this.previousSibling.className = "cntr_l_xon";
			this.parentNode.className = "cntr_xon";
			this.parentNode.previousSibling.className = "tr_xon";
			this.parentNode.previousSibling.getElementsByTagName("img")[0].src = imagesPath + "txtarea_tl_xon.gif";
			this.parentNode.nextSibling.className = "br_xon";
			this.parentNode.nextSibling.getElementsByTagName("img")[0].src = imagesPath + "txtarea_bl_xon.gif";
		}
		textareas[q].onblur = function() {
			this.previousSibling.className = "cntr_l";
			this.parentNode.className = "cntr";
			this.parentNode.previousSibling.className = "tr";
			this.parentNode.previousSibling.getElementsByTagName("img")[0].src = imagesPath + "txtarea_tl.gif";
			this.parentNode.nextSibling.className = "br";
			this.parentNode.nextSibling.getElementsByTagName("img")[0].src = imagesPath + "txtarea_bl.gif";
		}
	}
}
function buttonHovers() {
	for (var i = 0; i < buttons.length; i++) {
		buttons[i].className = "buttonSubmit";
		var buttonLeft = document.createElement('img');
		buttonLeft.src = imagesPath + "button_left.gif";
		buttonLeft.className = "buttonImg";
		buttons[i].parentNode.insertBefore(buttonLeft, buttons[i]);
		var buttonRight = document.createElement('img');
		buttonRight.src = imagesPath + "button_right.gif";
		buttonRight.className = "buttonImg";
		if(buttons[i].nextSibling) {buttons[i].parentNode.insertBefore(buttonRight, buttons[i].nextSibling);}
		else {buttons[i].parentNode.appendChild(buttonRight);}
		buttons[i].onmouseover = function() {
			this.className += "Hovered";
			this.previousSibling.src = imagesPath + "button_left_xon.gif";
			this.nextSibling.src = imagesPath + "button_right_xon.gif";
		}
		buttons[i].onmouseout = function() {
			this.className = this.className.replace(/Hovered/g, "");
			this.previousSibling.src = imagesPath + "button_left.gif";
			this.nextSibling.src = imagesPath + "button_right.gif";
		}
	}
}
//Useful functions
function findPosY(obj) {
	var posTop = 0;
	while (obj.offsetParent) {posTop += obj.offsetTop; obj = obj.offsetParent;}
	return posTop;
}
function findPosX(obj) {
	var posLeft = 0;
	while (obj.offsetParent) {posLeft += obj.offsetLeft; obj = obj.offsetParent;}
	return posLeft;
}

window.onload = init;