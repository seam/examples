// We put this constant here because facelets would otherwise interpret it as an EL expression
var CATEGORY_EXPR = "#{allCategories}";

var categories;

function loadCategories(result)
{
  categories = result;
  
  var catList = document.getElementById("rootCategory");
  
  catList.options.length = 0;
  
  for (var i = 0; i < categories.length; i++)
  {
    var cat = categories[i];
    
    if (cat.getParent() == null)
    { 
      // Set the tier for root categories to 1    
      cat.tier = 1;
      
      var option = new Option(cat.getName() + (isParent(cat) ? " >" : ""), cat.getCategoryId());
      var idx = catList.options.length;
      catList.options[idx] = option;
    }
  }   
  
  var catId = document.getElementById("sellForm:categoryId").value;  
  
  // If the category ID is valid, select it
  if ((typeof(catId) == "number" || !isNaN(parseInt(catId))))
  {
    var cats = new Array();
    var cat = findCategory(catId);
    
    while (cat.parent != null)
    {
       cats.push(cat);
       cat = cat.parent;
    }
    cats.push(cat);    
    
    // select the correct root category
    var rootCat = document.getElementById("rootCategory");
    for (var i = 0; i < rootCat.options.length; i++)
    {
      if (rootCat.options[i].value == cat.getCategoryId())
      {
        rootCat.options[i].selected = true;
        break; 
      } 
    }
    
    for (var i = cats.length - 1; i >= 0; i--)
    {
      if (i > 0)
        drawSubTier(cats[i], cats[i - 1].getCategoryId())
      else              
        drawSubTier(cats[i]); 
    }
  }
}

function findCategory(categoryId)
{
  for (var i = 0; i < categories.length; i++)
  {
    if (categories[i].getCategoryId() == categoryId)
      return categories[i]; 
  } 
  
  return null;
}

function drawSubTier(category, selectedCatId)
{
  var container = document.getElementById("container");
  
  if (isParent(category))
  {
    var subcats = document.createElement("select");
    subcats.size = 8;
    subcats.className = "categoryList";
    subcats.id = "tier" + (category.tier + 1);
    subcats.onchange = function() { selectCategory(subcats); };      
    
    for (var i = 0; i < categories.length; i++)
    {
      var subcat = categories[i];
      if (subcat.getParent() == category)
      {
        subcat.tier = category.tier + 1; 
        var option = new Option(subcat.getName() + (isParent(subcat) ? " >" : ""), subcat.getCategoryId());
        if (subcat.getCategoryId() == selectedCatId)
        {
          option.selected = true;
        }
        var idx = subcats.options.length;
        subcats.options[idx] = option;
      } 
    }
    
    var d = document.createElement("div");
    d.appendChild(subcats);
    container.appendChild(d);  
  }  
}

function isParent(category)
{
  if (category.isParent)
    return category.isParent;
    
  for (var i = 0; i < categories.length; i++)
  {
    if (categories[i].getParent() == category)
    {
      category.isParent = true;
      return true;
    }
  } 
  
  category.isParent = false;
  return false;
}

function getSelectedOption(ctl)
{
  for (var i = 0; i < ctl.options.length; i++)
  {
    if (ctl.options[i].selected)
      return ctl.options[i]; 
  }   
  return null;
}

function selectCategory(ctl)
{  
  var opt = getSelectedOption(ctl);
  
  var ctl = document.getElementById("sellForm:categoryId");
  var previous = findCategory(ctl.value);
  ctl.value = opt.value;
  
  var cat = findCategory(opt.value);
 
  if (previous)
  {
    // Prune the previously selected category
    var container = document.getElementById("container");  
    for (var i = previous.tier + 1; i > cat.tier; i--)
    {    
      var t = document.getElementById("tier" + i);
      if (t) container.removeChild(t.parentNode);
    }
  }
  
  drawSubTier(cat);
}