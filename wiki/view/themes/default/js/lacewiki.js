/* Cachable global scripts */

// ###################### COOKIE SUPPORT #################################

function checkCookieSupport() {
    if(!document.cookie) {
        jQuery("body")
            .prepend("<div class='cookieJavaScriptWarning'>" +
                     "Please enable cookie support in your browser, otherwise " +
                     "some features of this website will not work. Cookies are " +
                     "used only for temporary client identification and destroyed " +
                     "when you quit the browser." +
                     "</div>");
    }
}

// ###################### Log #################################

function log(message) {
    return; // Remove to enable logging
    if (!log.window_ || log.window_.closed) {
        var win = window.open("", null, "width=400,height=200," +
                                        "scrollbars=yes,resizable=yes,status=no," +
                                        "location=no,menubar=no,toolbar=no");
        if (!win) return;
        var doc = win.document;
        doc.write("<html><head><title>Debug Log</title></head><body style='font-family: monospace'></body></html>");
        doc.close();
        log.window_ = win;
    }
    var logLine = log.window_.document.createElement("div");
    logLine.appendChild(log.window_.document.createTextNode(message));
    log.window_.document.body.appendChild(logLine);
}

// ###################### Seam Remoting #################################

Seam.Remoting.displayLoadingMessage = function() {};
Seam.Remoting.hideLoadingMessage = function() {};

// ###################### jQuery Integration #################################

jQuery.noConflict(); // Avoid conflicts with the RichFaces/Prototype library

function jsf(id) {
    // Find the dynamic JSF client identifier by looking up
    // the static identifier of its j4j proxy child element
    if (document.getElementById(id) == null) { alert("Couldn't find JSF element: " + id); }
    var realId = document.getElementById(id).title;
    var element = document.getElementById(realId);
    return jQuery(element);
}

// ###################### Form helpers #################################

function actionConfirmation(message, actionFunctionName) {
    if (confirm(message)) {
        eval(actionFunctionName+"()");
    }
}

function onAjaxRequestComplete() {
    resetSessionTimeoutCheck();
    wrapBoxes();
}

function selectCheckBoxes(styleClass) {
    jQuery("."+styleClass).attr("checked", "true");
}
function deselectCheckBoxes(styleClass) {
    jQuery("."+styleClass).removeAttr("checked");
}

function clickClear(thisfield, defaulttext) {
    if (thisfield.value == defaulttext) {
        thisfield.value = "";
    }
}
function clickRecall(thisfield, defaulttext) {
    if (thisfield.value == '') {
        thisfield.value = defaulttext;
    }
}

function trimString(s) {
    return s.replace(/(^\s+|\s+$)/g, "");
}

function stringEndsWith(s, suffix) {
    return s.substring(s.length - suffix.length) == suffix;
}

function formatText(textArea, formatString) {
    var inlinePlaceholder = "{i}";
    var blockPlaceholder = "{b}";
    var inline = formatString.indexOf(inlinePlaceholder) != -1;
    var block = formatString.indexOf(blockPlaceholder) != -1;
    if (!(inline || block)) return;
    var prefix = formatString.substring(0, formatString.indexOf(inline ? inlinePlaceholder : blockPlaceholder));
    var suffix = formatString.substring(formatString.indexOf(inline ? inlinePlaceholder : blockPlaceholder)+3, formatString.length);
    if (block) {
        prefix = "\n" + prefix;
        suffix = suffix + "\n";
    }

    if (typeof(textArea.caretPos) != "undefined" && textArea.createTextRange) {
        var caretPos = textArea.caretPos;
        caretPos.text = caretPos.text.charAt(caretPos.text.length - 1) == ' ' ? prefix + caretPos.text + suffix + ' ' : prefix + caretPos.text + suffix;
        caretPos.select();
    } else if (typeof(textArea.selectionStart) != "undefined") {
        var begin = textArea.value.substr(0, textArea.selectionStart);
        var selection = textArea.value.substr(textArea.selectionStart, textArea.selectionEnd - textArea.selectionStart);
        var end = textArea.value.substr(textArea.selectionEnd);
        var newCursorPos = textArea.selectionStart;
        var scrollPos = textArea.scrollTop;
        textArea.value = begin + prefix + selection + suffix + end;
        if (textArea.setSelectionRange) {
            if (selection.length == 0)
                textArea.setSelectionRange(newCursorPos + prefix.length, newCursorPos + prefix.length);
            else
                textArea.setSelectionRange(newCursorPos, newCursorPos + prefix.length + selection.length + suffix.length);
            textArea.focus();
        }
        textArea.scrollTop = scrollPos;
    } else {
        textArea.value += prefix + suffix;
        textArea.focus(textArea.value.length - 1);
    }
}

function makeTextAreaResizable(textAreaId) {
    if(jQuery.browser.mozilla){
        var textAreaDivId = textAreaId + "Div";
        var textAreaResizeHandleId = textAreaId + "ResizeHandle";
        jQuery(textAreaDivId).Resizable({
            minHeight: 50,
            minWidth: 250,
            handlers: {
                se: textAreaResizeHandleId
            },
            onResize: function(size) {
                jQuery(textAreaId).height(size.height-10);
                jQuery(textAreaId).width(size.width-10);
            }
        }).width(jQuery(textAreaId).width()+10);
        jQuery(textAreaResizeHandleId).show();
    };

}

var editorCursorPosition;
var editorScrollPosition;
var editorSizeX;
var editorSizeY;
function storeEditorSettings(textAreaId) {
    var textArea = jQuery(textAreaId)[0];
    if (typeof(textArea.caretPos) != "undefined" && textArea.createTextRange) {
        cursorPosition = textArea.caretPos;
    } else if (typeof(textArea.selectionStart) != "undefined") {
        cursorPosition = textArea.selectionStart;
        scrollPosition = textArea.scrollTop;
    } else {
        cursorPosition = textArea.value.length - 1;
    }
    editorSizeX = jQuery(textAreaId).width();
    editorSizeY = jQuery(textAreaId).height();

}
function recallEditorSettings(textAreaId) {
    var textArea = jQuery(textAreaId)[0];
    if(textArea.createTextRange) {
        var range = textArea.createTextRange();
        range.move("character", cursorPosition);
        range.select();
    } else if(textArea.selectionStart) {
        textArea.focus();
        textArea.setSelectionRange(cursorPosition, cursorPosition);
        textArea.scrollTop = scrollPosition;
    } else {
        textArea.focus(cursorPosition);
    }
    jQuery(textAreaId).width(editorSizeX);
    jQuery(textAreaId).height(editorSizeY);
}

function selectText(textAreaId, position, padding) {
    var textArea = jQuery(textAreaId)[0];

    // We highlight characters before and after position, if possible
    var beginPosition = position;
    var endPosition = position;
    var i = 0;
    while (beginPosition > 0 && i < padding) {
        i++;
        beginPosition--;
    }
    i = 0;
    while (endPosition < textArea.value.length && i < padding) {
        i++;
        endPosition++;
    }

    if (textArea.createTextRange) {
        var oRange = textArea.createTextRange();
        oRange.moveStart("character", beginPosition);
        oRange.moveEnd("character", endPosition);
        oRange.select();
    } else if (textArea.setSelectionRange) {
        textArea.focus();
        textArea.setSelectionRange(beginPosition, endPosition);
    }
}

function scrollToText(textAreaId, position, charWidth, charHeight) {
    // This is all guesswork, there is no realiable way to do this
    var ta = jQuery(textAreaId);
    var scroll = {
          taWidthCenter : Math.floor(ta.innerWidth()/2.0),
          taHeightCenter : Math.floor(ta.innerHeight()/2.0),
          taNumRows : Math.floor(ta.innerHeight()/(charHeight-3)),
          taCharsInRow : Math.floor(ta.innerWidth()/(charWidth-4)),
          taHeight : ta.innerHeight(),
          taWidth : ta.innerWidth()
    };

    var btxt = ta.val().substr(0, position);
    if (btxt && btxt.length > 1) {
        var regex = new RegExp(".{1,"+scroll.taCharsInRow+"}|\n(?=\n)", "g");
        var gap = (btxt.match(regex).length) * scroll.taNumRows;
        if (gap > scroll.taHeight) {
            ta.scrollTop((gap-scroll.taHeightCenter));
        } else {
            ta.scrollTop(0);
        }
    }
}
