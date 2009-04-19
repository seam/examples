function getObject(objectId)
{
  if (document.getElementById && document.getElementById(objectId))
    return document.getElementById(objectId);
  else if (document.all && document.all(objectId))
    return document.all(objectId);
  else if (document.layers && document.layers[objectId])
    return document.layers[objectId];
  else
    return null;
};

function getStyleObject(objectId) 
{
  if(document.getElementById && document.getElementById(objectId)) 
	  return document.getElementById(objectId).style;
  else if (document.all && document.all(objectId))
  	return document.all(objectId).style;
  else if (document.layers && document.layers[objectId])
    return document.layers[objectId];
  else
	  return null;
};

// Returns the location and dimensions of a control
function __getControlDim(ctl)
{
  var width = ctl.offsetWidth;
  var height = ctl.offsetHeight;
	for (var lx = 0, ly = 0; ctl != null; lx += ctl.offsetLeft, ly += ctl.offsetTop, ctl = ctl.offsetParent);
	return {x:lx, y:ly, width:width, height:height};
};

function inverseRGB(rgbString)
{
  if (!rgbString || (rgbString = ""))
    rgbString = "rgb(0,0,0)";
    
  var elts = rgbString.slice(4, rgbString.length - 1).split(",");
  return "rgb(" + (255 - elts[0]) + "," + (255 - elts[1]) + "," + (255 - elts[2]) + ")";    
};

function __TagAttribute(name, value)
{
  this.name = name;
  this.value = value; 
};

function __Tag(name)
{
  this.name = name; 
  this.attributes = new Array();
  this.childTags = new Array();
  this.text = "";
  
  __Tag.prototype.addChildTag = function(name)
  {
    var childTag = new __Tag(name);
    this.appendChild(childTag);
    return childTag; 
  };
  
  __Tag.prototype.appendChild = function(childTag)
  {
    this.childTags[this.childTags.length] = childTag;
  };
  
  __Tag.prototype.setAttribute = function(name, value)
  {
    this.attributes[this.attributes.length] = new __TagAttribute(name, value);
  };
  
  __Tag.prototype.setText = function(text)
  {
    this.text = text; 
  };
  
  __Tag.prototype.getHTML = function()
  {
    var html = "<" + this.name;
    for (var i = 0; i < this.attributes.length; i++)
    {
      var tagAttribute = this.attributes[i];
      html += " " + tagAttribute.name + "=\"" + tagAttribute.value + "\"";
    } 
    html += ">" + this.text;
    
    for (var i = 0; i < this.childTags.length; i++)
    {
      html += this.childTags[i].getHTML();  
    }
    
    html += "</" + this.name + ">";
    return html;
  };
};

function __SliderFactory()
{
  this.sliders = new Array();
  
  __SliderFactory.prototype.addSlider = function (divId)
  {
  	var slider = new __Slider(divId);
  	this.sliders[this.sliders.length] = slider;
  	return slider;
  };
  
  __SliderFactory.prototype.getSliderByName = function (name)
  {
    for (var i = 0; i < this.sliders.length; i++)
    {
      if (this.sliders[i].sliderName == name)
        return this.sliders[i];
    }
    return null;
  };  
}


function qfSliderLimits(top, right, bottom, left)
{
  this.top = top;
  this.right = right;
  this.bottom = bottom;
  this.left = left;
}

// TODO percentage indicator
// TODO verticial (bottom to top)
// TODO reverse painting Top to Bottom
// TODO snap to closest value (multiple of abs(step))

var qfSlider_VERTICAL = "vertical";
var qfSlider_HORIZONTAL = "horizontal";

function __Slider(divId)
{
  this.sliderName = divId;
  this.divId = "__" + divId; 
  this.divCtl = getObject(divId);
  this.width = 0;
  this.height = 0;
  this.minValue = 0;
  this.maxValue = 0;
  this.position = 0;
  this.stepAmt = 1;
  this.horizontal = true;
  this.onChange = null;
  this.className = "";
  this.thumbClassName = "";
  this.showProgress = false;
  this.progressClassName = "";
  this.reversePaint = false;

  this.showThumb = false;
  this.thumbLimits = new qfSliderLimits(0, 0, 0, 0);

  this.showUsed = true;
  this.usedLimits = new qfSliderLimits(0, 0, 0, 0);
  this.usedColour = "navy";
  this.usedClassName = "";
  
  this.showRemain = false;
  this.remainLimits = new qfSliderLimits(0, 0, 0, 0);
  this.remainColour = "white";
  this.remainClassName = "";

  this.innerDivId = this.divId + "_inner";
  this.thumbDivId = this.divId + "_thumb";
  this.usedDivId = this.divId + "_used";
  this.remainDivId = this.divId + "_rem";
  this.progressUsedDivId = this.divId + "p_used";
  this.progressRemainDivId = this.divId + "p_remain";
  
  __Slider.prototype.setWidth = function(value) 
  { 
    this.width = 1 * value; 
  };
  
  __Slider.prototype.setHeight = function(value) 
  { 
    this.height = 1 * value; 
  };

  __Slider.prototype.setMinValue = function(value) 
  { 
    this.minValue = 1 * value; 
  };

  __Slider.prototype.setMaxValue = function(value) 
  { 
    this.maxValue = 1 * value; 
  };

  __Slider.prototype.setStep = function(value) 
  { 
    this.stepAmt = 1 * value; 
  };

  __Slider.prototype.setOrientation = function(value) 
  { 
    this.horizontal = (value == qfSlider_HORIZONTAL); 
  };

  __Slider.prototype.setOnChange = function(func) 
  { 
    this.onChange = func; 
  };

  __Slider.prototype.setPosition = function(value) 
  { 
    this.position = 1 * value;
    this.step(0); 
  };
  
  __Slider.prototype.setClassName = function(value) 
  { 
    this.className = value; 
  };

  __Slider.prototype.setThumbClassName = function(value) 
  { 
    this.thumbClassName = value; 
  };
  
  __Slider.prototype.setShowThumb = function(value) 
  { 
    this.showThumb = value; 
  };

  __Slider.prototype.setThumbLimits = function(value) 
  { 
    this.thumbLimits = value; 
  };

  __Slider.prototype.setShowUsed = function(value) 
  { 
    this.showUsed = value; 
  };

  __Slider.prototype.setUsedLimits = function(value) 
  { 
    this.usedLimits = value; 
  };

  __Slider.prototype.setUsedColour = function(value) 
  { 
    this.usedColour = value; 
  };

  __Slider.prototype.setUsedClassName = function(value) 
  { 
    this.usedClassName = value; 
  };

  __Slider.prototype.setShowRemain = function(value) 
  { 
    this.showRemain = value; 
  };

  __Slider.prototype.setRemainLimits = function(value) 
  { 
    this.remainLimits = value; 
  };

  __Slider.prototype.setRemainColour = function(value) 
  { 
    this.remainColour = value; 
  };
  
  __Slider.prototype.setRemainClassName = function(value) 
  { 
    this.remainClassName = value; 
  };

  __Slider.prototype.setShowProgress = function(value) 
  { 
    this.showProgress = value; 
  };

  __Slider.prototype.setProgressClassName = function(value) 
  { 
    this.progressClassName = value; 
  };

  __Slider.prototype.setReversePaint = function(value) 
  { 
    this.reversePaint = value; 
  };

  __Slider.prototype.getHTML = function()
  {
    var html = "";
    
    // Used for internal sizings of the div (excluding the border).
    var innerDivTag = new __Tag("div");
    innerDivTag.setAttribute("id", this.innerDivId);
    innerDivTag.setAttribute("style", "position:relative;width:100%;height:100%"); 
    html += innerDivTag.getHTML();
    
	  // Used Div
	  var usedDivTag = new __Tag("div");
	  usedDivTag.setAttribute("id", this.usedDivId);
	  var usedStyle = "visibility:hidden;position:absolute;";
	  
	  if (this.usedClassName != "")
	    usedDivTag.setAttribute("class", this.usedClassName);
	  else if (this.usedColor != "")
	    usedStyle += "background-color:" + this.usedColour;
	    
    usedDivTag.setAttribute("style", usedStyle);
    html += usedDivTag.getHTML();

    // Remaining Div
	  var remainDivTag = new __Tag("div");
	  remainDivTag.setAttribute("id", this.remainDivId);
	  
	  var remainStyle = "visibility:hidden;position:absolute;";
	  
	  if (this.remainClassName != "")
	    remainDivTag.setAttribute("class", this.remainClassName);
	  else if (this.remainColor != "")
	    remainStyle += "background-color:" + this.remainColour;
	    
    remainDivTag.setAttribute("style", remainStyle);
    html += remainDivTag.getHTML();
    
    // Progress Text
    var progDivTag = new __Tag("div");
	  progDivTag.setAttribute("id", this.progressUsedDivId);
	  var progStyle = "visibility:hidden;position:absolute;";
	  if (this.progressClassName != "")
	    progDivTag.setAttribute("class", this.progressClassName);
    progDivTag.setAttribute("style", progStyle);
    html += progDivTag.getHTML();

    progDivTag = new __Tag("div");
	  progDivTag.setAttribute("id", this.progressRemainDivId);
	  progStyle = "visibility:hidden;position:absolute;";
	  if (this.progressClassName != "")
	    progDivTag.setAttribute("class", this.progressClassName);
    progDivTag.setAttribute("style", progStyle);
    html += progDivTag.getHTML();
	
    // Thumb Div
	  var thumbDivTag = new __Tag("div");
	  thumbDivTag.setAttribute("id", this.thumbDivId);
	  thumbDivTag.setAttribute("class", this.thumbClassName); 
    thumbDivTag.setAttribute("style", "visibility:hidden;position:absolute;");
    html += thumbDivTag.getHTML();

    return html;
  };
  
  __Slider.prototype.getSliderProperties = function(limits)
  {
    var population = Math.abs(this.maxValue - this.minValue);
    var min = Math.min(this.minValue, this.maxValue);
    var max = Math.max(this.minValue, this.maxValue);

    var percent = (this.position - min)/population;
    
    var sliderDims = __getControlDim(getObject(this.innerDivId));
    
    if (limits)
    {
      sliderDims.width = sliderDims.width - limits.right - limits.left;
      sliderDims.height = sliderDims.height - limits.bottom - limits.top;
    }
    
    var sliderSize = 0;
    if (this.horizontal)
      sliderSize = Math.round(sliderDims.width * percent);
    else
      sliderSize = Math.round(sliderDims.height * percent);
               
    if (this.minValue > this.maxValue)
      sliderSize = sliderDims.width - sliderSize;
      
    return ( {dims:sliderDims, size:sliderSize, population:population, percent:percent, min:min, max:max} );
  };
  
  __Slider.prototype.repaint = function()
  {
    this.divCtl.className = this.className;
    this.divCtl.innerHTML = this.getHTML();
    
    this.divCtl.style.width = this.width + "px";
    
    if (this.showThumb)
    {
      // Allow the thumb to be dragged.
      var thumbObject = getObject(this.thumbDivId);
      __browser.chainEvent(thumbObject, "mousedown", dragStart);

      var props = this.getSliderProperties(this.thumbLimits);
      var styleObject = getStyleObject(this.thumbDivId);
      
      if (this.horizontal)
      {
        if (this.reversePaint)
          styleObject.left = this.thumbLimits.left + (props.dims.width - props.size - (styleObject.width / 2));
        else
          styleObject.left = this.thumbLimits.left + (props.size - (styleObject.width / 2));

        styleObject.top = this.thumbLimits.top;
      }
      else
      {
        if (this.reversePaint)
          styleObject.top = this.thumbLimits.top + (props.size - (styleObject.height / 2));
        else
          styleObject.top = this.thumbLimits.top + (props.dims.height - props.size - (styleObject.height / 2));

        styleObject.left = this.thumbLimits.left;
      }
      styleObject.visibility = "visible";
    }

    if (this.showUsed)
    {
      var props = this.getSliderProperties(this.usedLimits);
      var styleObject = getStyleObject(this.usedDivId);
      
      if (this.horizontal)
      {
        if (this.reversePaint)
          styleObject.left = this.usedLimits.left + props.dims.width - props.size;
        else
          styleObject.left = this.usedLimits.left;
        styleObject.top = this.usedLimits.top;
        styleObject.height = props.dims.height + "px";
        styleObject.fontSize = "1px"; // Hack for IE
        styleObject.width = props.size + "px";
      }
      else
      {
        if (this.reversePaint)
          styleObject.top = this.usedLimits.top;
        else
          styleObject.top = this.usedLimits.top + props.dims.height - props.size;
        styleObject.left = this.usedLimits.left;
        styleObject.width = props.dims.width + "px";
        styleObject.fontSize = "1px"; // Hack for IE
        styleObject.height = props.size + "px";
      }
      styleObject.visibility = "visible";
    }

    if (this.showRemain)
    {
      var props = this.getSliderProperties(this.remainLimits);
      var styleObject = getStyleObject(this.remainDivId);
      
      if (this.horizontal)
      {
        if (this.reversePaint)
          styleObject.left = this.remainLimits.left;
        else
          styleObject.left = this.remainLimits.left + props.size;
        styleObject.top = this.remainLimits.top;
        styleObject.height = props.dims.height + "px";
        styleObject.fontSize = "1px"; // Hack for IE
        styleObject.width = (props.dims.width - props.size) + "px";
      }
      else
      {
        if (this.reversePaint)
          styleObject.top = this.remainLimits.top + props.size;
        else
          styleObject.top = this.remainLimits.top;
        styleObject.left = this.remainLimits.left;
        styleObject.width = props.dims.width + "px";
        styleObject.fontSize = "1px"; // Hack for IE
        styleObject.height = (props.dims.height - props.size) + "px";
      }
      
      styleObject.visibility = "visible";
    }
    
    if (this.showProgress)
    {
      var props = this.getSliderProperties(this.usedLimits);
      var usedStyleObject = getStyleObject(this.usedDivId);
      var remainStyleObject = getStyleObject(this.remainDivId);
      var baseStyleObject = this.divCtl.style;
      
      // Used side
      var styleObject = getStyleObject(this.progressUsedDivId);
      
      styleObject.left = usedStyleObject.left;
      styleObject.top = usedStyleObject.top;
      styleObject.height = usedStyleObject.height;
      
      if (this.showRemain)
        styleObject.color = remainStyleObject.backgroundColor;
      else
        styleObject.color = inverseRGB(baseStyleObject.backgroundColor);
        
      if (this.horizontal)
      {
        styleObject.width = props.dims.width + "px";
        styleObject.textAlign = "center";
        styleObject.clip = "rect(0px, " + props.size + "px, " + props.dims.height + "px, 0px)";
        getObject(this.progressUsedDivId).innerHTML = Math.round(props.percent * 100) + "%";
      }
      else
      {
        // TODO vertical clipping
      }
      styleObject.visibility = "visible";    
      
      // Remain side
      props = this.getSliderProperties(this.remainsLimits);
      styleObject = getStyleObject(this.progressRemainDivId);
      
      if (this.horizontal)
      {
        styleObject.left = this.remainLimits.left;
        styleObject.top = this.remainLimits.top;
        styleObject.height = props.dims.height + "px";
        styleObject.width = props.dims.width + "px";

        if (this.showUsed)
          styleObject.color = usedStyleObject.backgroundColor;
        else
          styleObject.color = inverseRGB(baseStyleObject.backgroundColor);

        styleObject.textAlign = "center";
        styleObject.clip = "rect(0px, " + props.dims.width + "px, " + props.dims.height + "px, " + props.size + "px)";
        getObject(this.progressRemainDivId).innerHTML = Math.round(props.percent * 100) + "%";
      }
      else
      {
        // TODO vertical clipping
      }
     
      styleObject.visibility = "visible";          
    }
  };

  __Slider.prototype.step = function(delta)
  {
    this.position = this.position + (delta * this.stepAmt);
    
    var min = Math.min(this.minValue, this.maxValue);
    var max = Math.max(this.minValue, this.maxValue);

    if (this.position < min) 
      this.position = min;
      
    if (this.position > max) 
      this.position = max;
    
    this.repaint();

    if (this.onChange)
      this.onChange(this);
  };
  
  var dragDiv;
  var dragDivId;
  var dragStartMousePos;
  var dragStartSliderPos;

  function dragStart(event)
  {
    var thumbDiv = __browser.getEventTarget(event);
    dragDiv = thumbDiv;
    dragStartMousePos = __browser.getMousePos(event);
    
    var slider = qfSliderFactory.getSliderByName(thumbDiv.id.split("_")[2]);
    dragStartSliderPos = slider.position;
    
    __browser.chainEvent(document, "mousemove", dragMove);
    __browser.chainEvent(document, "mouseup", dragDrop);
    __browser.stopEventBubble(event);
  }
  
  function dragMove(event)
  {
	  var mousePos = __browser.getMousePos(event);

    var thumbDiv = dragDiv;
    var slider = qfSliderFactory.getSliderByName(thumbDiv.id.split("_")[2]);
    
    var styleObject = getStyleObject(thumbDiv.id);
    var thumbDims = __getControlDim(getObject(thumbDiv.id));
    var props = slider.getSliderProperties(slider.thumbLimits);
    
		// Limit movement of thumb within boundaries of the containing thumb limits
		if (slider.horizontal)
		{
    	var newLeft = mousePos.x - props.dims.x - thumbDims.width / 2;
  	  
  		if (newLeft < slider.thumbLimits.left)
  		  newLeft = slider.thumbLimits.left;
  	  if (newLeft > (slider.thumbLimits.left + props.dims.width))
  	    newLeft = slider.thumbLimits.left + props.dims.width;
  	    
  	  var percent = (newLeft - slider.thumbLimits.left) / props.dims.width;
 	    if (this.reversePaint)
 	      percent = 1 - percent;
      
      var min = Math.min(slider.minValue, slider.maxValue);
      var max = Math.max(slider.minValue, slider.maxValue);
      
      var pos = ((max - min) * percent).roundedValue(slider.stepAmt);
      
      slider.setPosition(pos);
      slider.repaint();
    }
    else
    {
      var min = Math.min(slider.minValue, slider.maxValue);
      var max = Math.max(slider.minValue, slider.maxValue);
      var pos = (dragStartSliderPos - (mousePos.y - dragStartMousePos.y) / props.dims.height * (max - min)).roundedValue(slider.stepAmt);
      
      slider.setPosition(pos);
      slider.repaint();
    }
	  
	  __browser.stopEventBubble(event);
  };  

  function dragDrop(event)
  {
    __browser.unchainEvent(document, "mousemove", dragMove);
    __browser.unchainEvent(document, "mouseup", dragDrop);
  };
} 


var qfSliderFactory = new __SliderFactory();