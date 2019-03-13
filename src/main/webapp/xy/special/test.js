/**
 * Created by isaac_gu on 2016/5/12.
 */
$(function(){
    /*var id = "le_Tabs_4";
     var css = "color";
     var value = "#000000";

     //setfontColor(id, "color", value);
     //如果是hover的时候

     id = "#" + id + " " + selector + " a:hover";
     setfontColor(id, "backgroundColor", value);
     var _v = getStyle("#le_Tabs_0 .ui-tabs-nav a", "color")

     console.info(_v);
     removeStyle("le_Tabs_0");*/
    /*var id = "le_Tabs_4";
     var value = "#000000";
     var selector = ".ui-state-hover";
     //var selector = "#" + id + " " + selector + " a:hover";
     var sm = new StyleManager();
     var id = "#" + id + " " + selector + " a:hover";

     sm.setStyle(id, "backgroundColor", value);  //增 改
     //sm.removeStyle();                         */
    /*删*/
    /*
     var aa=sm.getStyle(id,"color");
     alert(aa);*/
    //testGetStyle();
    /*testSetStyle();
     testAddStyle()*/
    /*Array.prototype.contains = function(item){
     return RegExp("\\b"+item+"\\b").test(this);
     };*/
    /*var arr = [];
    for(var i = 10; i < 15; i++){
        arr.push(i);
    }*/
    /*alert(arr.contains(4));  //false
     alert(arr.contains(14)); //true*/

});

function contains(item, array){
    return RegExp("\\b" + item + "\\b").test(array);
}

function testGetStyle(){
    var id = "#le_Tabs_4 .ui-state-hover a:hover";
    var sm = new StyleManager();

    var expect = sm.getStyle(id, "color");
    if(expect == "rgb(0, 105, 214)"){
        throw new Error("getStyle()方法异常: ");
    }
}

function testSetStyle(){
    var id = "#le_Tabs_4 .ui-state-hover a:hover";
    var sm = new StyleManager();
    sm.setStyle(id, "fontSize", "20px")
}

function testAddStyle(){
    var id = "#le_Tabs_5";
    var sm = new StyleManager();
    sm.removeStyle(id)
}
var $style = $("#custom_style");
/*var selector = ".ui-state-hover";
 function setfontColor(id, css, value){
 //如果是hover的时候
 if(selector.indexOf(".ui-state-hover") != -1){
 selector = "#" + id + " " + selector + " a:hover";
 }
 //判断当前的style是否存在, 如果存在，修改；如果不存在，添加
 //存在
 if(isExist(selector, css, value)){
 modifyStyle(selector, css, value);
 } else{//不存在，添加
 $style.append(selector + "{" + css + ":" + value + ";}");
 }
 }*/

function modifyStyle(selector, css, value){
    //获得style标签里面的所有对象
    var sheet = document.styleSheets[document.styleSheets.length - 1];
    //获得所有的rules
    var rules = sheet.cssRules || sheet.rules;
    var rule = getRule(selector);

    var _style = rule.style;
    _style[css] = value;
    var _htmlArr = [];
    //把改变后的rules帖到style标签里
    for(var i = 0, len = rules.length; i < len; i++){
        _htmlArr.push(rules[i].cssText);
    }

    $style.html(_htmlArr.join(""));
    /**/
}

/**
 * 判断当前的style是否存在
 * @param selector
 */
function isExist(selector){
    var _html = $style.html();
    return _html.indexOf(selector) != -1;
}

function getRule(selector){
    //获得style标签里面的所有对象
    var sheet = document.styleSheets[document.styleSheets.length - 1];
    //获得所有的rules
    var rules = sheet.cssRules || sheet.rules;
    //获得目标rule
    for(var i in rules){
        if(rules[i].selectorText == selector){
            return rules[i];
        }
    }
    return null;
}

function getStyle(selector, css){
    var rule = getRule(selector);
    return rule.style[css];
}

function removeStyle(id){
    //获得style标签里面的所有对象
    var sheet = document.styleSheets[document.styleSheets.length - 1];
    //获得所有的rules
    var rules = sheet.cssRules || sheet.rules;
    var _htmlArr = [];
    //获得目标rule
    for(var i in rules){
        var _st = rules[i].selectorText + "";
        if(_st.indexOf(id) == -1){
            _htmlArr.push(rules[i].cssText);
        }
    }
    $style.html(_htmlArr.join(""));
}

function StyleManager(){
}
StyleManager.prototype = {
    setStyle: function(selector, css, value){
        //判断当前的style是否存在, 如果存在，修改；如果不存在，添加
        //存在
        if(this.isExist(selector, css, value)){
            this.modifyStyle(selector, css, value);
        } else{//不存在，添加
            $style.append(selector + "{" + css + ":" + value + ";}");
        }
    },
    removeStyle: function(id){
        //获得style标签里面的所有对象
        var sheet = document.styleSheets[document.styleSheets.length - 1];
        //获得所有的rules
        var rules = sheet.cssRules || sheet.rules;
        var _htmlArr = [];
        //获得目标rule
        for(var i in rules){
            var _st = rules[i].selectorText + "";
            if(_st.indexOf(id) == -1){
                _htmlArr.push(rules[i].cssText);
            }
        }
        $style.html(_htmlArr.join(""));
    },
    getStyle: function(selector, css){
        var rule = this.getRule(selector);
        return rule.style[css];
    },
    modifyStyle: function(selector, css, value){
        //获得style标签里面的所有对象
        var sheet = document.styleSheets[document.styleSheets.length - 1];
        //获得所有的rules
        var rules = sheet.cssRules || sheet.rules;
        var rule = this.getRule(selector);

        var _style = rule.style;
        _style[css] = value;
        var _htmlArr = [];
        //把改变后的rules帖到style标签里
        for(var i = 0, len = rules.length; i < len; i++){
            _htmlArr.push(rules[i].cssText);
        }

        $style.html(_htmlArr.join(""));
        /**/
    },
    getRule: function(selector){
        //获得style标签里面的所有对象
        var sheet = document.styleSheets[document.styleSheets.length - 1];
        //获得所有的rules
        var rules = sheet.cssRules || sheet.rules;
        //获得目标rule
        for(var i in rules){
            if(rules[i].selectorText == selector){
                return rules[i];
            }
        }
        return null;
    },
    isExist: function(selector){
        var _html = $style.html();
        return _html.indexOf(selector) != -1;
    }
};