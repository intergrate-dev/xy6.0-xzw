$(document).ready(function(){
});
var srcForm;
var mainTdForForm;
var formData;
var formNum=1;
var clickTdId;
var validArrayLable = [];
var validArrayInput = [];
e5_form.dataReader = {
    //读值
    init : function() {
        $("form").each(function(i,n){
            var docID = this.DocID.value;
            var form = this;
            mainTdForForm = "cust_"+this.mainMemId.value;
            clickTdId = this.mainMemId.value;
            if (docID != "0") {
                var theURL = "e5workspace/manoeuvre/FormDocFetcher.do?FormID=" +this.FormID.value
                    + "&DocLibID=" + this.DocLibID.value
                    + "&DocID=" +this.DocID.value;
                theURL = e5_form.dealUrl(theURL);
                $.ajax({url: theURL, async:false, dataType:'json', success: function(datas) {
                    var fv = datas.value;
                    formData=fv;
                    for (var f in fv) {
                        var input = $(form).find("#" + f);                            
                        if (input.attr("oldValue"))
                            input.attr("oldValue", fv[f]);
                        else {
                            input.val(fv[f]);
                        }
                        if($(form).parent().attr("id")!=mainTdForForm){
                            input.prop("readonly", true);
                            $(input).prop("disabled",true);
                            addImgBtn(input);
                        } else {
                        	srcForm = form;
                        }
                        $(input).unbind("blur");
                    }
                   
                    var fields = datas.fields;
                    for (var i = 0; i < fields.length; i++) {
                        var input = $("#SPAN_" + fields[i]);
                        //input.css("display", "none");
                        input.remove();
                    }

                }});
            }
            //select选中项、checkbox的初始化
            initSelect(form);
            initCheck(form);
            if($(form).parent().attr("id")!=mainTdForForm){
                $(form).find("input[type='button']").each(function(i,n){
                    $(this).css("display","none");
                });
                $(form).find("input[type='submit']").each(function(i,n){
	                $(this).css("display","none");
	            });
                $(form).find("label").each(function(i,n){
                    if($(this).attr("class").indexOf("custform-label-require")>-1) {
                    	validArrayLable.push($(this).attr("id"));
                    }
                	$(this).removeAttr("for");
                    $(this).removeClass("custform-label-require");
                });
                $(form).find("input[type='text']").each(function(i,n){
                	if($(this).attr("class").indexOf("validate")>-1) {
                    	validArrayInput.push($(this).attr("id"));
                    }
                	$(this).removeClass("validate");
                });
                $(form).find("input[type='button']").each(function(i,n){
                    $(this).css("display","none");
                });
            }else{
                form.DocID.value = docID; //$("#DocIDs").val();
                form.FormID.value="0";
                $("#from_"+clickTdId).validationEngine({
                    autoPositionUpdate:true,
                    onValidationComplete:function(from,r){
                        if(r){
                            window.onbeforeunload=null;
                            $("#btnSave").prop("disabled",true);
                            $("#btnCancel").prop("disabled",true);
                            from[0].submit();
                        }
                    }
                });
            }
        });
    }
}
function initSelect(form){
    var sels = $(form).find("select");
    for (var idx = 0; idx < sels.length; idx++) {
        var sel = sels[idx];
        var oldValue = sel.getAttribute("oldValue");
        if (sel.multiple){//处理多选下拉框的选中
            var datas = new Array();
            if (oldValue != null && oldValue.length > 0){
                if(oldValue.indexOf(",")){
                    datas = oldValue.split(",");
                }else{
                    datas.push(oldValue);
                }
            }
            var options = sel.options;
            if (options != null && options.length > 0){
                for(var i=0;i<options.length;i++){
                    for(var j=0;j<datas.length;j++){
                        if(options[i].value==datas[j]){
                            options[i].selected="selected";
                        }
                    }
                }
            }
        } else if (oldValue != "-"){
            sel.value = oldValue;
        }
        if($(form).parent().attr("id")!=mainTdForForm){
            sel.disabled = "disabled";
        }
    }
    
    initRadio(form);
}
//把所有的radio按值做初始化选中
function initRadio(form) {
    var sels = $(form).find("input[radio='true']");
    for (var i = 0; i < sels.length; i++) {
        var sel = sels[i];
        var oldValue = sel.getAttribute("oldValue");
        if (oldValue != "-") {
            var idx = 0;
            while (true) {
                //var ck = document.getElementById(sel.id + "_" + idx++);
                var ck = $(form).find("#"+sel.id + "_" + idx);
                idx = idx+1;
                if (!ck || ck.length==0) break;
                
                if (ck[0].value == oldValue)
                    ck[0].checked="true"; 
                if($(form).parent().attr("id")!=mainTdForForm){
                    ck[0].disabled = "disabled";
                }
            }
        } else {
            //默认选中第一个选项
            //var ck = document.getElementById(sel.id + "_0");
            var ck = $(form).find("#"+sel.id + "_0" );
            if (ck) ck.checked="true"; 
        }
        if($(form).parent().attr("id")!=mainTdForForm){
            sel.disabled = "disabled";
        }
    }
}
function initCheck(form){
    var sels = $(form).find("input[type='checkbox']");
    for (var i = 0; i < sels.length; i++) {
        var sel = sels[i];
        var oldValue = sel.getAttribute("oldValue");
        if (oldValue=="1")
            sel.checked="true";
        if($(form).parent().attr("id")!=mainTdForForm){
            sel.disabled = "disabled";
        }
    }
    initMultiCheck(form);
}
function initMultiCheck(form){
    var sels = $(form).find("input[check='true']");
    for (var i = 0; i < sels.length; i++) {
        var sel = sels[i];
        var oldValue = sel.getAttribute("oldValue");
        if (oldValue != "-") {
            var id = sel.id;
            var values = oldValue.split(",");
            var idx = 0;
            while (true) {
                //var ck = document.getElementById(id + "_" + idx++);
                var ck = $(form).find("#"+id + "_" + idx);
                 idx = idx+1;
                if (!ck || ck.length==0) break;
                
                for (var j = 0; j < values.length; j++) {
                    if (values[j] == ck[0].value)
                        ck[0].checked="true"; 
                    if($(form).parent().attr("id")!=mainTdForForm){
                        ck[0].disabled = "disabled";
                    }
                }
            }
        }
        if($(form).parent().attr("id")!=mainTdForForm){
            sel.disabled = "disabled";
        }
    }
}


e5_form.dynamicReader = {
	//----------"动态读取数据"的初始化---------------
	init : function (){
        $("form").each(function(i,n){
            dynamicReader(this);
        });
	}
}

//----------"动态读取数据"的初始化---------------
function dynamicReader(form) {
	//定义有ID/NAME对的onchange事件："其它数据"类型、"单层分类"类型
	var sels = $(form).find("select[pair='true']");
	for (var i = 0; i < sels.length; i++) {
		var sel = sels[i];
		$(sel).change(function(event){changeDataName(event,form);});
	}
	//找<select url=””>元素
	_readSelectUrl(form);
	
	//找<input radio="true" url=””>元素
	_readRadioUrl(form);
	
	//找<input check="true" url=””>元素
	_readCheckUrl(form);
	
	//找<select catType=””>元素
	_readCatSelect(form);
}
function _readSelectUrl(form) {
	var sels = $(form).find("select[url]");
	for (var i = 0; i < sels.length; i++) {
		var sel = sels[i];
		var dataUrl = sel.getAttribute("url");
		if (!dataUrl) continue;
		
		while (sel.options.length > 0)
			sel.remove(0);
				
		dataUrl = e5_form.dealUrl(dataUrl);
		//返回数据格式：[{key:”…”, value:”…”},{….}, {…}]
		$.ajax({url: dataUrl, async:false, dataType:"json", success: function(datas) {
            if (datas != null){
                for (var i = 0; i < datas.length; i++) {
                    var op = document.createElement("OPTION");
                    op.value = datas[i].key;
                    op.text = datas[i].value;
                    sel.options.add(op);
                }
				
				var id = sel.getAttribute("id");
				var name = id.substring(0, id.length - 3);
				var hiddenID = document.getElementById(name);
				if (hiddenID && datas.length > 0) {
					hiddenID.value = datas[0].value;
				}
            }
		}});
	}
}
function _readRadioUrl(form) {
	var sels = $(form).find("input[radio='true']");
	for (var i = 0; i < sels.length; i++) {
		var sel = $(sels[i]);
		var dataUrl = sel.attr("url");
		if (!dataUrl) continue;
		
		dataUrl = e5_form.dealUrl(dataUrl);
		
		var parent = sel.parent();
		var fName = sel.attr("name");
		//返回数据格式：[{key:”…”, value:”…”},{….}, {…}]
		$.ajax({url: dataUrl, async:false, dataType:"json", success: function(datas) {
            if(datas!=null){
                for (var i = 0; i < datas.length; i++) {
                    var radio = $("<input type='radio'/>")
                            .val(datas[i].key)
                            .attr("name", fName)
                            .attr("id", fName + "_" + i);
                    var label = $("<label/>")
                            .html(datas[i].value)
                            .attr("for", fName + "_" + i);
                    parent.append(radio);
                    parent.append(label);
                }
            }
		}});
	}
}
function _readCheckUrl(form) {
	var sels = $(form).find("input[check='true']");
	for (var i = 0; i < sels.length; i++) {
		var sel = $(sels[i]);
		var dataUrl = sel.attr("url");
		if (!dataUrl) continue;
		
		dataUrl = e5_form.dealUrl(dataUrl);
		
		var parent = sel.parent();
		var fName = sel.attr("name");
		//返回数据格式：[{key:”…”, value:”…”},{….}, {…}]
		$.ajax({url: dataUrl, async:false, dataType:"json", success: function(datas) {
            if(datas!=null){
                for (var i = 0; i < datas.length; i++) {
                    var radio = $("<input type='checkbox'/>")
                            .val(datas[i].key)
                            .attr("name", fName)
                            .attr("id", fName + "_" + i);
                    var label = $("<label/>")
                            .html(datas[i].value)
                            .attr("for", fName + "_" + i);
                    parent.append(radio);
                    parent.append(label);
                }
            }
		}});
	}
}
function _readCatSelect(form) {
	//找单层分类
	var sels = $(form).find("select[catType]");
	for (var i = 0; i < sels.length; i++) {
		var sel = sels[i];
		var catType = sel.getAttribute("catType");
		if (!catType) continue;
		
		while (sel.options.length > 0)
			sel.remove(0);
			
		var dataUrl = "e5workspace/manoeuvre/CatFinder.do?action=single&catType=" + catType;
		dataUrl = e5_form.dealUrl(dataUrl);
		
		$.ajax({url: dataUrl, async:false, dataType:"json", success: function(datas) {
			if(datas != null && datas.length > 0){
				for (var i = 0; i < datas.length; i++) {
					var op = document.createElement("OPTION");
					op.value = datas[i].catID;
					op.text = datas[i].catName;
					sel.options.add(op);
				}
				
				var id = sel.getAttribute("id");
				var name = id.substring(0, id.length - 3);
				var hiddenID = document.getElementById(name);
				if (hiddenID && datas.length > 0) {
					hiddenID.value = datas[0].catName;
				}
			}
		}});
	}
}

//切换时，给NAME域赋值
function changeDataName(event,form) {
	var src = event.target;
	//"AAA_ID"去掉"_ID"，得到NAME字段的域
	var id = src.id;
	var nameField = id.substring(0, id.length - 3);
	var text = (src.selectedIndex >= 0) ? src.options[src.selectedIndex].text : "";
	$("#"+form.id+" #" + nameField).val(text);
}


function doCancel() {
	window.onbeforeunload = null;
	
	$("#btnSave").disabled = true;
	$("#btnCancel").disabled = true;
	beforeExit();
}

//关闭窗口。调after.do解锁
function beforeExit() {
	var uuid=$("#from_"+clickTdId+" #UUID").val();
	var dataUrl = "e5workspace/after.do?UUID="
	if(uuid==undefined) {
		uuid = $("#hdUUID").val();
		dataUrl = "../../e5workspace/after.do?UUID=" + uuid;
	} else {
		dataUrl = "e5workspace/after.do?UUID=" + uuid;
		dataUrl = e5_form.dealUrl(dataUrl);
	}
	document.getElementById("iframe"+formNum).contentWindow.location.href = dataUrl;
}

//分类选择树
function selectCat(nameField, idField, catType, multiple) {
	//把"1_23_323"转成"1~23~323"
	var catIDs = _transformCatIDs(document.getElementById(idField).value);
	var theURL = "e5workspace/manoeuvre/CatSelect.do?noPermission=1&catType=" + catType + "&catIDs=" + catIDs;
	if (!multiple) theURL += "&multiple=false";

	_select(nameField, idField, theURL);
}

function _select(nameField, idField, dataUrl){
	curIDField = idField;
	curNameField = nameField;
	
	//顶点位置
	var pos = _getDialogPos(document.getElementById(nameField));
	pos.left = $("#from_"+clickTdId+" #"+nameField).offset().left +"px"; 
	dataUrl = e5_form.dealUrl(dataUrl);
	
	curDialog = e5.dialog({type:"iframe", value:dataUrl}, 
			{showTitle:false, width:pos.width, height:pos.height, pos:pos,resizable:false});
	curDialog.show();
}
function _getDialogPos(el) {
	function Pos (x, y) {
		this.x = x;
		this.y = y;
	}
	function getPos(el) {
		var r = new Pos(el.offsetLeft, el.offsetTop);
		if (el.offsetParent) {
			var tmp = getPos(el.offsetParent);
			r.x += tmp.x;
			r.y += tmp.y;
		}
		return r;
	}
	var p = getPos(el);
	
	//决定弹出窗口的高度和宽度
	var dWidth = 400;
	var dHeight = 300;

	var sWidth = document.body.clientWidth; //窗口的宽和高
	var sHeight = document.body.clientHeight;
	
	if (dWidth + 10 > sWidth) dWidth = sWidth - 10;//用e5.dialog时会额外加宽和高
	if (dHeight + 30 > sHeight) dHeight = sHeight - 30;
	
	//顶点位置
	var pos = {left : p.x +"px", 
		top : (p.y + el.offsetHeight - 1)+"px",
		width : dWidth,
		height : dHeight
		};
	if (pos.left + dWidth > sWidth)
		pos.left = sWidth - dWidth;
	if (pos.top + dHeight > sHeight)
		pos.top = sHeight - dHeight;
	
	return pos;
}

//分类选择窗口点“确定”
function catWindowSelect(catIDs, catNames, cascadeIDs, cascadeNames) {
	var ids = _transformCatIDs(cascadeIDs, 1);
	var names = cascadeNames;

	$("#from_"+clickTdId).find("#"+curIDField).val(ids);
	$("#from_"+clickTdId).find("#"+curNameField).val(names);

	catWindowHidden();
}
//分类选择窗口点“取消”
function catWindowHidden() {
	userWindowHidden();
}
function _transformCatIDs(catIDs, direct) {
	if (!catIDs) return catIDs;

	if (direct) { //从分类选择返回
		catIDs = catIDs.replace(/~/g, "_");
		catIDs = catIDs.replace(/,/g, ";");
	}
	else {
		var catIDArr = catIDs.split(";");
		catIDs = "";
		for (var i = 0; i < catIDArr.length; i++) {
			var idArr = catIDArr[i].split("_");
			if ((idArr.length > 0) && idArr[idArr.length - 1])
				catIDs += idArr[idArr.length - 1] + ",";
		}
		if (catIDs) catIDs = catIDs.substring(0, catIDs.length - 1);
	}
	return catIDs;
}

//选中主会员后，其它会员属性为只读，且此会员所在form不显示确定取消按钮
function setMainMember(tdId){
	var mainTdId="cust_"+tdId;
	clickTdId=tdId;
	//formNum表示是第几个表单
	formNum = $("#from_"+tdId).attr("target").replace("iframe","");
	$("form").each(function(i,n){
		var form = this;
        var imgObj = $(form).find("img");
		for (var f in formData) {
			var input = $(form).find("#" + f);    
			if($(form).parent().attr("id")!=mainTdId){
	            input.prop("readonly", true);
	            $(input).prop("disabled",true);
	            
	            $(form).find("input[type='button']").each(function(i,n){
	                $(this).css("display","none");
	            });
	            $(form).find("input[type='submit']").each(function(i,n){
	                $(this).css("display","none");
	            });
	            $(form).find("label").each(function(i,n){
                	$(this).removeAttr("for");
                    $(this).removeClass("custform-label-require");
                });
                $(form).find("input[type='text']").each(function(i,n){
                	$(this).removeClass("validate");
                });
	            
	            if (imgObj.length==0)
            		addImgBtn(input);
	        } else {
	        	srcForm = form;
	        	input.prop("readonly", false);
	            $(input).prop("disabled",false);
	            
	            $(form).find("input[type='button']").each(function(i,n){
	                $(this).css("display","inline");
	            });
	            $(form).find("input[type='submit']").each(function(i,n){
	                $(this).css("display","inline");
	            });
	            $(form).find("label").each(function(i,n){
	                if($.inArray($(this).attr("id"), validArrayLable)>-1) {
	                	$(this).addClass("custform-label-require");
	                }
	            });
	            $(form).find("input[type='text']").each(function(i,n){
	            	if($.inArray($(this).attr("id"), validArrayInput)>-1) {
	            		$(this).addClass("validate");
	            	}
	            });
	        	delImgBtn(input);
	        	
	        	form.DocID.value = $("#DocIDs").val();
	            form.FormID.value="0";
	            $("#from_"+tdId).validationEngine({
	                autoPositionUpdate:true,
	                onValidationComplete:function(from,r){
	                    if(r){
	                        window.onbeforeunload=null;
	                        $("#btnSave").prop("disabled",true);
	                        $("#btnCancel").prop("disabled",true);
	                        from[0].submit();
	                    }
	                }
	            });
	        }
		}
		
		//select 
		var sels = $(form).find("select");
		for (var idx = 0; idx < sels.length; idx++) {
			var sel = sels[idx];
			if($(form).parent().attr("id")!=mainTdId){
		        sel.disabled = "disabled";
			} else {
				sel.disabled = false; 
			}
	    }
		
		//checkbox
		 var sels = $(form).find("input[type='checkbox']");
		 for (var idx = 0; idx < sels.length; idx++) {
			 var sel = sels[idx];
			 if($(form).parent().attr("id")!=mainTdId){
		         sel.disabled = "disabled";
			 } else {
				 sel.disabled = false; 
			}
        }
		 
		//
		
	});
	
}

//用后面的值替换或追加基本属性中的相应值
function addImgBtn(obj){
	if(obj.attr("type")!="hidden") {
		var tagName=obj[0].tagName;
		var img0 = "<img onclick='setAppendValue(this,\""+tagName+"\",\"0\");' src='../../amuc/img/arrowhead.gif' style='cursor:pointer;vertical-align:middle;padding-left:5px;' />";
		var img1 = "<img onclick='setAppendValue(this,\""+tagName+"\",\"1\");' src='../../amuc/img/arrowhead1.gif' style='cursor:pointer;vertical-align:middle;padding-left:8px;' />";
		obj.parent().append(img0);
		var id=obj.attr("id");
		if(id=="mEmail" || id=="mZip" || id=="mAddress" || id=="mHometown" || id=="mMemo")
			obj.parent().append(img1);
	}
}
function delImgBtn(obj){
	if(obj.attr("type")!="hidden") {
		var tagName=obj[0].tagName;
		obj.parent().find("img").remove();
	}
}
function setAppendValue(objImg,tagName,isAppend) {
	var obj;
	if(tagName=="INPUT"){
		obj=$(objImg).parent().find("input[type='text']");
	} else if(tagName=="SELECT") {
		obj=$(objImg).parent().find("select");
	} else if(tagName=="TEXTAREA") {
		obj=$(objImg).parent().find("textarea");
	}
	objId = obj.attr("id");
	var findCon=tagName+"[id='"+objId+"']";
	if(obj.val()==null || $.trim(obj.val())=="")
		alert("原值是空字符！");
	else {
		if(isAppend=="0") {  //把原内容直接替换相应的新属性内容
			$(srcForm).find(findCon).val(obj.val());
			if(tagName=="INPUT") { //分类取值，同时把值赋给新属性隐藏域
				var objBtn=$(objImg).parent().find("input[type='button']");
				if(objBtn.length > 0) {
					var objHidden=$(objImg).parent().find("input[type='hidden']");
					if(objId=="mOrg")
						$(srcForm).find("input[id='mOrg_ID']").val(objHidden.val());
					else 
						$(srcForm).find("input[id='"+objId+"ID']").val(objHidden.val());
				}
			} else if(tagName=="SELECT" && objId.indexOf("_ID")>-1) { //动态取值的下拉框，同时要取文本赋值给新属性
				var nameField = objId.substring(0, objId.length - 3);
				var srcText = obj.find("option:selected").text();
				findCon="input[id='"+nameField+"']";
				$(srcForm).find(findCon).val(srcText);
			} 
		}
		else { //所原内容追加到新属性的内容后面
			var srcVal = $(srcForm).find(findCon).val();
			$(srcForm).find(findCon).val(srcVal + obj.val());
		}
	}
}
