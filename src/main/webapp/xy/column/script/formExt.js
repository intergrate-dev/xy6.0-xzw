//栏目扩展属性
var column_form = {
	ch : 0,
	templateFields : [
		["col_template", "col_templateArticle", "col_templatePic", "col_templateVideo"],
		["col_templatePad", "col_templateArticlePad", "col_templatePicPad", "col_templateVideoPad"]
	],
	init : function() {
		column_form.ch = column_form.getParam("ch");
        $("#col_color_ID").val("");
        $("#col_color").val();
        console.log($("#col_color").val());
        $("#col_color_ID").css("background",$("#col_color").val());
        $("#col_color_ID").prop("readonly", true);
        $("#col_template").prop("readonly", true); // 栏目模板
		$("#col_templateArticle").prop("readonly", true); // 文章模板
		$("#col_templatePic").prop("readonly", true);
		$("#col_templateVideo").prop("readonly", true);
		$("#col_templatePad").prop("readonly", true); // 触屏版栏目模板
		$("#col_templateArticlePad").prop("readonly", true); // 触屏版文章模板
		$("#col_templatePicPad").prop("readonly", true);
		$("#col_templateVideoPad").prop("readonly", true);
		$("#col_pubRule").prop("readonly", true); // 发布规则
		$("#col_pubRulePad").prop("readonly", true); // 触屏版发布规则

        $("#col_color").prop("readonly", true); // 栏目颜色

		if($("#chooseColor").length > 0) $("#chooseColor").colpick();


        $("#col_topCount").addClass("validate[min[0],max[100]]");//App栏目头条个数>=0

		$("#selectAll").click(column_form.selectAll);
        $("#selectAllApp").click(column_form.selectAllApp);
		$("#btnSync").click(column_form.sync2Children);
		
		$("[templateType]").click(column_form.selTemplate);
		$("[ruleType]").click(column_form.selRule);
        $("[colorType]").click(column_form.selColor);
		
		$("#form").attr("target", "iframe");
		$("#form").attr("action", "../../xy/column/FormSubmit.do");
		
		$("#col_fileName").addClass("validate[funcCall[checkValidDir]]"); //文件名的特殊符号限制
		$("#col_fileNamePad").addClass("validate[funcCall[checkValidDir]]");
		
		// 初始化图标
		column_form.initIcon();
		
		column_form.changeSave();
		
		column_form.addClearBtns();
	},
	// 初始化图标
	initIcon : function() {
		icon_form.showIcon("col_iconBig", 0);
		icon_form.showIcon("col_iconSmall", 1);
	},

	//保存修改：对于属性设置表单，保存前检查网站发布规则和触屏发布规则是否会导致发布时覆盖
	changeSave : function(){
		var code = e5_form.getParam("code");
		if (code == "formColumnExt0" || code == "formColumnExt1") {
			$("#btnFormSave")[0].onclick = column_form.doSave;
		}
	},
	//在发布规则和模板输入框上添加清理按钮
	addClearBtns : function(){
		var suffix = "";
		
		var code = e5_form.getParam("code");
		if (code == "formColumnExt0") {
			suffix = "";
		} else if (code == "formColumnExt1") {
			suffix = "Pad";
		} else return;
		
		column_form.addClearBtn("col_pubRule" + suffix);
		column_form.addClearBtn("col_template" + suffix);
		column_form.addClearBtn("col_templateArticle" + suffix);
		column_form.addClearBtn("col_templatePic" + suffix);
		column_form.addClearBtn("col_templateVideo" + suffix);
        // column_form.addClearBtn("col_color" + suffix);
	},
	//添加清理按钮
	addClearBtn : function(name1){
		var clearBtn = $('<img class="close" src="../../images/tab_del.gif"/>')
			.attr("for", name1)
			.attr("id", "cb_" + name1)
			.click(column_form.doClear);
		var input = $("#" + name1);
		
		input.after(clearBtn);
		input.mouseover(function(){
			if ($(this).val())
				$("#cb_" + name1).show();
		});
		input.mouseout(function(){
			if ($(this).val())
				$("#cb_" + name1).hide();
		});
		$("#DIV_" + name1 + " img").hover(function(){
			$(this).toggle();
		});
	},
	//清理事件响应
	doClear : function(evt) {
		var name = $(evt.target).attr("for");
		$("#" + name).val("");
		$("#" + name + "_ID").val("");
	},
	
	//保存前检查网站发布规则和触屏发布规则是否会导致发布时覆盖
	doSave : function() {
		if (!$("#form").validationEngine("validate")) {
			// 验证提示
			$("#form").validationEngine("updatePromptsPosition");
			return false;
		}
		
		var result = column_form._checkValidRule();
		if (result == 1) {
			alert("发布设置和触屏发布设置的发布规则相同，请修改");
			return false;
		} else if (result == 2) {
			alert("发布设置和触屏发布设置的稿件发布地址相同，请修改");
			return false;
		} else if (result == 3) {
			alert("发布设置和触屏发布设置的栏目发布地址相同，请修改");
			return false;
		} else if (result == 4) {
			alert("存在同一发布规则下的同名栏目文件，请修改");
			return false;
		}
		
		window.onbeforeunload = null;
	},
	//验证发布文件是否会覆盖
	_checkValidRule : function() {
		var ruleID, fileName, type;
		
		var code = e5_form.getParam("code");
		if (code == "formColumnExt0") {
			ruleID = $("#col_pubRule_ID").val();
			fileName = $("#col_fileName").val();
			type = "0";
		} else {
			ruleID = $("#col_pubRulePad_ID").val();
			fileName = $("#col_fileNamePad").val();
			type = "1";
		}
		if (ruleID) {
			var theURL = "../../xy/column/CheckRule.do?DocLibID=" + $("#DocLibID").val()
					+ "&DocIDs=" + $("#DocID").val()
					+ "&ruleID=" + ruleID
					+ "&type=" + type
					+ "&fileName=" + e5_form.encode(fileName);
			var result = null;
			$.ajax({url: theURL, async:false, success: function (data) {
				result = parseInt(data);
			}});
			return result;
		} else {
			return 0;
		}
	},
	
	// 选择模板
	winTmp : {},
	selTemplate : function(evt) {
		var type = $(evt.target).attr("templateType");
		var channel = $(evt.target).attr("channel");	
		if (!channel) channel = 0;
		var nameField = column_form._fieldTemplate(channel, type);
		var ckeckedID = document.getElementById(nameField+"_ID").value;
		
		// 根据类型不同，显示出不同的模板选择树，以组为第一级
		if (column_form.winTmp[type]) {
			column_form.winTmp[type].show();
		} else {
			var siteID = e5_form.getParam("siteID");
			if (column_form.ch == 1 && channel == 0) channel = 2; //app栏目设置主发布模板时，取app渠道类型的模板
			
			var dataUrl = "xy/template/TemplateSelect.jsp?type=" + type
					+ "&channel=" + channel
					+ "&siteID=" + siteID
					+ "&ckeckedID=" + ckeckedID;
			dataUrl = e5_form.dealUrl(dataUrl);
			// 顶点位置
			var pos = e5_form.event._getDialogPos(document.getElementById(nameField));

			column_form.winTmp[type] = e5.dialog({
				type : "iframe",
				value : dataUrl
			}, {
				showTitle : false,
				width : "350px",
				height : pos.height,
				pos : pos,
				resizable : false
			});
			column_form.winTmp[type].show();
		}
			
		//隐藏input目录框上的验证信息
		$("#" + nameField).validationEngine("hide");
	},
	closeTemplate : function(channel, type, name, docID) {
		if (channel == 2) channel = 0;
		
		var nameField = column_form._fieldTemplate(channel, type);
		var idField = nameField + "_ID";
		
		$("#" + idField).val(docID);
		$("#" + nameField).val(name);
				
		if (column_form.winTmp[type]) {
			column_form.winTmp[type].hide(); // 调用dialog.close()会造成浏览器无响应，因此改成hide
		}
	},
	cancelTemplate : function(type) {
		if (column_form.winTmp[type]) {
			column_form.winTmp[type].hide(); // 调用dialog.close()会造成浏览器无响应，因此改成hide
		}
	},
	//确定当前要的是哪个模板字段
	_fieldTemplate : function(channel, type) {
		channel = parseInt(channel);
		type = parseInt(type);
		
		return column_form.templateFields[channel][type];
	},
	// ----以下选择发布规则---
	winRule : {},
	selRule : function(evt) {
		var type = $(evt.target).attr("ruleType");
		var nameField = (type == "0") ? "col_pubRule" : "col_pubRulePad";
		
		if (column_form.winRule[type]) {
			column_form.winRule[type].show();
		} else {
			var siteID = e5_form.getParam("siteID");
			var dataUrl = "xy/MainRuleTree.do?type=" + type + "&siteID=" + siteID;
			dataUrl = e5_form.dealUrl(dataUrl);

			// 顶点位置
			var pos = e5_form.event._getDialogPos(document.getElementById(nameField));
			column_form.winRule[type] = e5.dialog({
				type : "iframe",
				value : dataUrl
			}, {
				showTitle : false,
				width : "900px",
				height : "420px",
				pos : pos,
				resizable : false
			});
			column_form.winRule[type].show();
		}
			
		//隐藏input目录框上的验证信息
		$("#" + nameField).validationEngine("hide");
	},
	closeRule : function(type, name, docID) {
		var nameField = (type == "0") ? "col_pubRule" : "col_pubRulePad";
		
		$("#" + nameField + "_ID").val(docID);
		$("#" + nameField).val(name);
		
		column_form.cancelRule(type);
	},
	cancelRule : function(type) {
		if (column_form.winRule[type]) {
			column_form.winRule[type].hide(); // 调用dialog.close()会造成浏览器无响应，因此改成hide
		}
	},

    // ----以下选择栏目颜色---
    winColor : {},
    // selColor : function(){
	//     var type = 0;
    //     var nameField = "col_color";
    //     if (column_form.winColor[type]) {
    //         column_form.winColor[type].show();
    //     } else {
    //         var siteID = e5_form.getParam("siteID");
    //         var dataUrl = "xy/color/ChooseColor.jsp";
    //         dataUrl = e5_form.dealUrl(dataUrl);
	//
    //         // 顶点位置
    //         var pos = e5_form.event._getDialogPos(document.getElementById(nameField));
    //         column_form.winColor[type] = e5.dialog({
    //             type : "iframe",
    //             value : dataUrl
    //         }, {
    //             showTitle : false,
    //             width : "350px",
    //             height : pos.height,
    //             pos : pos,
    //             resizable : false
    //         });
    //         column_form.winColor[type].show();
    //     }
	//
    //     //隐藏input目录框上的验证信息
    //     $("#" + nameField).validationEngine("hide");
    // },
    closeColor : function(type, name, docID) {
        var nameField = "col_color";

        $("#" + nameField + "_ID").val(docID);
        $("#" + nameField).val(name);

        column_form.cancelColor(type);
    },
    cancelColor : function(type) {
        if (column_form.winColor[type]) {
            column_form.winColor[type].hide(); // 调用dialog.close()会造成浏览器无响应，因此改成hide
        }
    },
    setColor : function(color_ID, color){
        $("#col_color_ID").val(color_ID);
        $("#col_color").val(color);
    },
    //颜色触发函数

    // chooseColor:function (){
    //
    // },
	//表单保存后仍保留页面
	refresh : function() {
		/*
		$("#btnFormSave").attr("disabled", false);
		$("#btnFormCancel").attr("disabled", false);
		*/
		window.onbeforeunload = "javascript:void(0);";
		window.location.reload();
	},
	//同步项全选
	selectAll : function(){
		/*
		var _checkedCount = $('input:checked').length;
		if(_checkedCount < 5){
			$(":checkbox").prop("checked",true);
		}else if(_checkedCount = 5){
			$(":checkbox").prop("checked",false);
		}
		*/

        if($("#siterule").attr('checked') && $("#columnpl").attr('checked') && $("#picpl").attr('checked') && $("#articlepl").attr('checked')&& $("#videopl").attr('checked')&& $("#isShowInNav").attr('checked')){
            $("#siterule").prop("checked",false);
            $("#columnpl").prop("checked",false);
            $("#picpl").prop("checked",false);
            $("#articlepl").prop("checked",false);
            $("#videopl").prop("checked",false);
            $("#isShowInNav").prop("checked",false);
        }else{
            $("#siterule").prop("checked",true);
            $("#columnpl").prop("checked",true);
            $("#picpl").prop("checked",true);
            $("#articlepl").prop("checked",true);
            $("#videopl").prop("checked",true);
            $("#isShowInNav").prop("checked",true);
        }
		
	},
    //App属性同步项全选
    selectAllApp : function(){
        if($("#columntype").attr('checked') && $("#columnstyle").attr('checked') && $("#columntopcount").attr('checked')){
            $("#columntype").prop("checked",false);
            $("#columnstyle").prop("checked",false);
            $("#columntopcount").prop("checked",false);
        }else{
            $("#columntype").prop("checked",true);
            $("#columnstyle").prop("checked",true);
            $("#columntopcount").prop("checked",true);
        }

    },
	//同步到子孙栏目
	sync2Children : function(evt) {
		if (!confirm("确定要把属性同步到子孙栏目吗？")) {
			return;
		}
		var formType = $(evt.target).attr("formtype");


        if("3" == formType){
            var columntype =0,columnstyle=0,columntopcount=0;
            if($("#columntype").attr('checked')){

                columntype = 1;
            }
            if($("#columnstyle").attr('checked')){

                columnstyle = 1;
            }
            if($("#columntopcount").attr('checked')){

                columntopcount = 1;
            }
            if(columntype==0&&columnstyle==0&&columntopcount==0){
                alert("请先选择需要的同步项！");
                return;
            }
        }
        else{
            var siterule =0,columnpl=0,articlepl=0,picpl=0,videopl=0,isShowInNav=0;
            if($("#siterule").attr('checked')){
                siterule =1;
            }
            if($("#columnpl").attr('checked')){
                columnpl =1;
            }
            if($("#picpl").attr('checked')){
                picpl =1;
            }
            if($("#articlepl").attr('checked')){
                articlepl =1;
            }
            if($("#videopl").attr('checked')){
                videopl =1;
            }
            if($("#isShowInNav").attr('checked')){
                isShowInNav =1;
            }
            if(siterule==0&&columnpl==0&&articlepl==0&&picpl==0&&videopl==0&&isShowInNav==0){
                alert("请先选择需要的同步项！");
                return;
            }
		}
		var theURL = "../../xy/column/Sync.do?DocLibID=" + e5_form.getParam("DocLibID")
				+ "&DocIDs=" + e5_form.getParam("DocIDs")
				+ "&SyncType=" + formType
				+ "&siterule=" + siterule
				+ "&columnpl=" + columnpl
				+ "&articlepl=" + articlepl
				+ "&picpl=" + picpl
				+ "&videopl=" + videopl
            	+ "&isShowInNav=" + isShowInNav
            	+ "&columntype=" + columntype
            	+ "&columnstyle=" + columnstyle
            	+ "&columntopcount=" + columntopcount;
				
		$.ajax({type: "POST", url: theURL, async:false, 
			success: function (data) {
				if (data == "ok") {
					alert("同步完成");
				} else {
					alert("操作失败：" + data);
				}
			}
		});
	},
	getParam : function(name) {
		var params = window.location.href;
		params = params.substring(params.indexOf("?") + 1, params.length);
		params = params.split("&");
		
		for (var i = 0; i < params.length; i++) {
			var arr = params[i].split("=");
			if (arr[0] == name) {
				return params[i].substring(name.length + 1, params[i].length);
			}
		}
		return null;
	}
}
var icon_form = {
	//显示头像
	showIcon : function(id, flag) {
		$(".labelIconImg").hide(); //提示Label去掉
		
		var img = $("#iconImg" + flag);
		
		var url = $("#" + id).attr("oldvalue");
		if (url && url != "-") {
			img.attr("src", url);
			img.css("max-width", 150);
			img.css("max-height", 150);
			
			$("#img_" + id).hide()
		} else {
			img.hide();
		}
	}
}
e5_form.event.otherValidate = function() {
	if (e5_form.file.notImgFile("col_iconBig")){
		alert("请选择jpg, png, gif类型的文件");
		return false;
	}
	if (e5_form.file.notImgFile("col_iconSmall")){
		alert("请选择jpg, png, gif类型的文件");
		return false;
	}
	return true;
}
//填充单层分类下拉框的Option。读栏目类型、栏目样式时加siteID
e5_form.dynamicReader._readCatUrl = function(catType) {
	var dataUrl = "e5workspace/manoeuvre/CatFinder.do?action=single&catType=" + catType;
	{
		//改变读数据的url
		dataUrl = "xy/Cats.do?catType=" + catType + "&siteID=" + e5_form.getParam("siteID");
	}
	dataUrl = e5_form.dealUrl(dataUrl);
	return dataUrl;
}
$(function() {
	column_form.init();
});

