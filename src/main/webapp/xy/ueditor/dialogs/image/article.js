//写稿操作的最外层js
var article_all = {
	columnFor : null, // 选择栏目的对应input名
	editDialogType : 0,// 0为标题，1为摘要，2为副题
	editDialog : null,//主栏目对话框
	publishWhenSave : false, //给扩展字段的附件上传时用的标记
	onOffDelect : '', //判定在提交close时删除

	init : function() {
		//按钮的响应事件
		$(".btnColumn").click(article_all.selectColumn); 	//主栏目
		$("#btnColumn").hide();
		$("#btnColumnRel,#a_columnRel").click(article_all.selectColumns);//关联栏目
		$("#btnSave").click(article_all.save);				//提交按钮点击事件  //后期扩充保存草稿的功能
		$("#btnPublish").click(article_all.publish);		//保存按钮点击事件
		$("#btnCancel").click(e5_form_event.doCancel);		//取消按钮点击事件
		
		$("#btnTitleAdv").click(article_all.editTitle);		//标题编辑样式按钮
		$("#btnEditSubTitle").click(article_all.editSubTitle);		//副题编辑样式按钮
		$("#btnEditAbstract").click(article_all.editAbstract);		//摘要编辑样式按钮
        $("#btnEditLinkTitle").click(article_all.editLinkTitle);		//摘要编辑样式按钮
        $("#btnEditShortTitle").click(article_all.editShortTitle);		//短标题样式按钮
		$("#a_tag").focus(article_all.showHistoryTag);  //显示历史标签
		$("#a_tag").blur(article_all.hideHistoryTag);  //隐藏历史标签
		
		$("#btnlinkColName").click(article_all.selectLinkCol);	//直播稿/专题稿的选择栏目按钮
			
		//window.onbeforeunload = e5_form_event.beforeExit;
		$(window).on('beforeunload',function(){
			$(window).on('unload',function(){ 
				var dataUrl = "../../e5workspace/after.do?UUID=" + article.UUID;
				$.ajax({url:dataUrl,async:false});
				});
			return '您确定要关闭吗？';
			});
		$(document).keydown(article_all.refreshF5); //F5刷新
		
		article_all.initChannel();
		
		article_all.cookieColumns();
		
		article_all.initTabSelect();

		//设置验证
		$("#form").validationEngine({
			autoPositionUpdate:true,
			promptPosition:"bottomLeft",
			scroll:true
		});
	},
	initChannel : function() {
		//若只有一个渠道，则不必显示checkbox，且自动选中
		if ($("#channel0").length == 0) {
			$("#channel1").attr("checked", "checked");
			$("#channel1").hide();
		} else if ($("#channel1").length == 0) {
			$("#channel0").attr("checked", "checked");
			$("#channel0").hide();
		}
	},
	//读cookie设原稿的发布渠道，不超过10个渠道。cookie名:a_col0,a_col1,...
	cookieColumns : function() {
		for (var i = 0; i < 10; i++) {
			var col = $("#" + i + "_columnID");
			if (col.length > 0) {
				var value = xy_cookie.getCookie("a_col" + i);
				if (!value) continue;
				
				var pos = value.indexOf(",");
				value = [value.substring(0,pos), value.substring(pos+1)];
				if (value[1] == "undefined") continue;
				
				col.val(value[0]);
				$("#" + i + "_column").val(value[1]);
			}
		}
	},
	// 点击选择主栏目
	selectColumn : function(evt) {
		var src = $(evt.target);
		var name = src.attr("for");
		var id = name + "ID";
		var ch = src.attr("ch");
		
		article_all.columnFor = name;
		
		// 顶点位置
		var pos = e5_form_event._getDialogPos(document.getElementById(name));
		 
		var dataUrl = "../../xy/column/ColumnCheck.jsp?cache=1&type=radio&ids=" + $("#" + id).val()
				+ "&ch=" + ch + "&siteID=" + article.siteID;
		e5_form_event.curDialog = e5.dialog({
			type : "iframe",
			value : dataUrl
		}, {
			showTitle : false,
			width : pos.width,
			height : "400px",
			pos : pos,
			resizable : false,
			esc:true
		});
		e5_form_event.curDialog.show();
	},
	// 点击选择关联栏目
	selectColumns : function(evt) {
		var src = $(evt.target);
		article_all.columnFor = src.attr("for");
		var ch = src.attr("ch");
		
		// 顶点位置
		var pos = e5_form_event._getDialogPos(document.getElementById("a_columnRel"));
		 
		var dataUrl = "../../xy/column/ColumnFavorite.jsp?cache=1&type=op&ids=" + $("#a_columnRelID").val()
				+ "&ch=" + ch + "&siteID=" + article.siteID;
		console.log(dataUrl)
		e5_form_event.curDialog = e5.dialog({
			type : "iframe",
			value : dataUrl
		}, {
			showTitle : false,
			width : pos.width,
			height : "400px",
			pos : pos,
			resizable : false,
			esc:true
		});
		e5_form_event.curDialog.show();
	},
	// 关联栏目中，自动滤掉主栏目
	filterCol : function(col, cols) {
		if (!cols || !col)
			return cols;

		cols = "," + cols + ","; // 前后加上逗号, 格式变成",1,2,3,"的样子
		cols = cols.replace("," + col + ",", ","); // 替换掉",1,"

		// 去掉前后的逗号
		if (cols.charAt(0) == ',') {
			cols = cols.substring(1);
		}
		if (cols.charAt(cols.length - 1) == ',') {
			cols = cols.substring(0, cols.length - 1);
		}
		return cols;
	},
	//标签文本框获取焦点
	showHistoryTag : function() {
		$("#auto").show();
		var _cookies = xy_cookie.getCookie("a_tag");
		if(_cookies!=null){
			var websites = _cookies.split(",");
			var autoComplete=new AutoComplete('a_tag','auto',websites);
			autoComplete.start(event);
		}
	},
	//标签文本框失去焦点
	hideHistoryTag : function() {
		$("#auto").hide();
	},
	//点击保存按钮
	save : function() {
		/*if (article_all.noColumn()) {
			alert("请选择栏目");
			return;
		}*/
		article_all.publishWhenSave = false;
		
		var param = article_all._collectData();
		if (!param) return false;
		
		//保存标签到cookie
		article_all.saveTagCookie(param);
		//app发布库下，检查是否有标题图片
		if (article.ch == "1") {
			var form = param.form;
			if (!form["a_picBig"] && !form["a_picSmall"] && !form["a_picMiddle"]) {
				if (!confirm("还没有上传标题图片，是否提交？")) return;
			}
		}
		var onOff = true;
		article_all._post(param,onOff);
	},
	//点击发布按钮
	publish : function() {

		if (article_all.noColumn()) {
			alert("请选择栏目");
			return;
		}
		article_all.publishWhenSave = true;
		
		var param = article_all._collectData();
		if (!param) return false;

		//保存标签到cookie
		article_all.saveTagCookie(param);
		//app发布库下，检查是否有标题图片
		if (article.ch == "1") {
			var form = param.form;
			if (!form["a_picBig"] && !form["a_picSmall"] && !form["a_picMiddle"]) {
				if (!confirm("还没有上传标题图片，是否发布？")) return;
			}
		}
		param["toPublish"] = 1;
		article_all._post(param);
	},
	noColumn : function() {
		//没有渠道选择，则是在发布库写稿，已有主栏目
		if ($("#channel0").length == 0 && $("#channel1").length == 0)
			return false;
		
		//原稿库下的判断
		var hasColumn = ($("#channel0").attr("checked") == "checked" && $("#0_columnID").val()
				|| $("#channel1").attr("checked") == "checked" && $("#1_columnID").val());
		return !hasColumn;
	},
	saveTagCookie : function(param) {
		var form = param.form;
		if(form["a_tag"]==""){
			return;
		}
		var _cookie = xy_cookie.getCookie("a_tag");
		if(_cookie!=null&&_cookie.indexOf(form["a_tag"])>-1){
			return;
		}
		if(_cookie!=null&&_cookie!=""){
			xy_cookie.setCookie("a_tag",form["a_tag"]+","+_cookie,30);
		}else{
			xy_cookie.setCookie("a_tag",form["a_tag"],30);
		}
		
	},
	_collectData : function() {
		//提交之前，将批注隐藏
        $("#ueditor_0").contents().find(".selected-comment").css("background-color", "");
        $("#ueditor_0").contents().find(".selected-comment-img").css("background-color", "").css("border", "");

		if (!channel_frame.isValid()) {
			return null;
		}
		//判断是否需要检测敏感词和非法词
		//type: 写稿-0;  评论-1
		//a_isSensitive: 0-不含任何敏感词和非法词；1-含有敏感词但不含非法词；2-含有非法词但是不含敏感词；3-既含有敏感词又含有非法词
		if(article.requireSensitive==true||article.requireSensitive =="true"||article.requireIllegal==true||article.requireIllegal=="true") {
			var content = channel_frame.getContentTxt();

			if (content != null && content == "") {
				$("#a_isSensitive").val(0);
			} else {
				if ((article.requireSensitive == true || article.requireSensitive == "true"
					|| article.requireIllegal == true || article.requireIllegal == "true")) {
					if(article_all.checkSensitive($("#SYS_TOPIC").val(), "标题")){
						return;
					}
					if(article_all.checkSensitive(content)){
						return;
					}
				}
			}
		}

		//先上传附件
		var ready = channel_frame.uploadFiles();
		if (!ready){
			//如果没有上传完，退出
			if (article_all.publishWhenSave)
				setTimeout("article_all.publish()",100);
			else
				setTimeout("article_all.save()",100);
			return;
		}

		return channel_frame.getData();
	},
	checkSensitive: function(content, ta){
        var _ta = ta || "内容";
        if(article_sensitive.hasSensitive(content)){
            //如果开启了敏感词检测服务
            if((article.requireSensitive == true || article.requireSensitive == "true")){
                //判断是否有敏感词
                //如果有,弹出confirm，询问用户是否需要继续提交稿件
                //article_sensitive 在article-form.js中
                if(article_sensitive.senList != null && article_sensitive.senList.length != 0){
                    //显示敏感词
                    //如果用户点击确定，不做处理；否则，处理
                    if(!confirm(_ta + "当中存在以下敏感词：" + article_sensitive.senList.join("，") + "\n是否继续提交？")){
                        // article_sensitive.handleSensitive();
                        return true;
                    } else{
                        article_all.hasSen = true;
                        // article_sensitive.cleanSenStyle();
                    }
                } else{
                    //如果没有敏感词列表为空，即没有敏感词，就把这个字段设为0
                    article_all.hasSen = article_all.hasSen || false;
                }
            }
            //如果开启了非法词检测服务
            if((article.requireIllegal == true || article.requireIllegal == "true")){
                if(article_sensitive.illList != null && article_sensitive.illList.length != 0){
                    //如果用户点击确定，不做处理，马上提交；否则，处理，修改非法词
                    if(!confirm(_ta + "当中存在以下非法词：" + article_sensitive.illList.join("，") + "\n是否继续提交？")){
                        // article_sensitive.handleIllegal();
                        return true;
                    } else{
                        article_all.hasIll = true;
                        // article_sensitive.cleanIllStyle();
                    }
                } else{
                    //如果没有非法词列表为空，即没有非法词，就把这个字段设为0
                    article_all.hasIll = article_all.hasIll || false;
                }
            }

            var hs = article_all.hasSen ? 1 : 0;
            var hi = article_all.hasIll ? 2 : 0;

            $("#a_isSensitive").val(hs + hi);


            /*if(hasIll == 0) {
             if(hasSen == 1){
             //含有敏感词但是没有非法词
             $("#a_isSensitive").val(1);
             } else
             //没有敏感词也没有非法词
             $("#a_isSensitive").val(0);
             } else {
             if(hasSen == 1){
             //含有敏感词也有非法词
             $("#a_isSensitive").val(3);
             } else
             //不含有敏感词但是有非法词
             $("#a_isSensitive").val(2);
             }*/
        }
        var hs = article_all.hasSen ? 1 : 0;
        var hi = article_all.hasIll ? 2 : 0;

        $("#a_isSensitive").val(hs + hi);

        return false;
	},
	_post : function(param,isDelect) {
        this.onOffDelect = isDelect;
		article_all._writeCookie();

		//Post方式下参数是字符串形式
		var paramString = JSON.stringify(param);
		var paramData = {"param":paramString};
		//alert(paramString);
		$.ajax({type: "POST", url: "./ArticleSubmit.do", async:false,
			data: paramData,
			success: article_all.close,
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				alert(errorThrown + ':' + textStatus);  // 错误处理
			}
		});
	},
	//写cookie：原稿发布选择的栏目
	_writeCookie : function() {
		for (var i = 0; i < 10; i++) {
			var col = $("#" + i + "_columnID");
			if (col && col.val()) {
				col = col.val() + "," + $("#" + i + "_column").val();
				xy_cookie.setCookie("a_col" + i, col);
			}
		}
	},
	close : function(msg) {
		if (msg != "ok") {
			alert("保存时异常：" + msg);
		} else {
			//解决提交时保存草稿箱  发布时删除草稿
			if( !article_all.onOffDelect){
                article_all.delectDraft();
			}
			window.onbeforeunload = null;
			var tool = null;
			try {
				tool = window.opener.e5.mods["workspace.toolkit"];
			} catch (e) {
			}
			if (tool) {
				tool.self.closeOpDialog("OK", 1);
				window.close();
			} else {
				window.close();
			}
		}
	},
	//执行提交操作从草稿箱 删除此条数据
    delectDraft : function() {
		// alert(111111)
        var strDocID = ($("#headerButton .idName").text());

        var articleId1 = $("#edui4_body").attr('artileIdPerson');

        var articleId = '';
        if(articleId1 == undefined || articleId1 =='' || articleId1 == null){

            articleId = strDocID.split("：")[1];
        }else{
            articleId = articleId1;
		}
        $.ajax({
            url : "../../xy/article/DeleteDraft.do",
            type : "POST",
            data : {
                "docID" : articleId
            },
            dataType : "json",
            success : function(data){
				 
                // if(data.info){
                //     alert("删除成功")
                // }else{
                //     alert("删除失败")
                // }
            }
        })
    },
	//按F5的响应
	refreshF5 : function(evt) {
		if (evt.keyCode == 116) { //F5
			window.onbeforeunload = null;
		}
	},
	openEditDialog : function(){
		var content = '';
		if( article_all.editDialogType == 0 ){
			content = $("#SYS_TOPIC").val();
		} else if ( article_all.editDialogType == 1 ){
			content = $("#a_abstract").val();
		} else if ( article_all.editDialogType == 2 ){
			content = $("#a_subTitle").val();
		} else if ( article_all.editDialogType == 3 ){
			content = $("#a_linkTitle").val();
		} else if ( article_all.editDialogType == 4 ){
			content = $("#a_shortTitle").val();
		}
		var dataUrl = "../../../../xy/article/editStyle.jsp?editDialogType="+article_all.editDialogType+"&e_type=articlesetting";

		article_all.editDialog = e5.dialog({
			type : "iframe",
			value : dataUrl
		}, {
			title : "编辑样式",
			width : "650px",
			height : "320px",
			resizable : false,
			fixed : true
		});
		article_all.editDialog.show();
	},
	editTitle : function(){
		article_all.editDialogType = 0;
		article_all.openEditDialog();
	},
	editAbstract : function(){
		article_all.editDialogType = 1;
		article_all.openEditDialog();
	},
	editSubTitle : function(){
		article_all.editDialogType = 2;
		article_all.openEditDialog();
	},
    editLinkTitle : function(){
        article_all.editDialogType = 3;
        article_all.openEditDialog();
    },
    editShortTitle : function(){
        article_all.editDialogType = 4;
        article_all.openEditDialog();
    },
	selectLinkCol : function(evt){
		var src = $(evt.target);
		article_all.columnFor = "a_linkID";
		var ch = article.ch;
		
		// 顶点位置
		var pos = e5_form_event._getDialogPos(document.getElementById("a_linkName"));
		 
		var dataUrl = "../../xy/column/ColumnCheck.jsp?cache=1&type=radio&ids=" + $("#a_linkID").val()
				+ "&ch=" + ch + "&siteID=" + article.siteID;
		e5_form_event.curDialog = e5.dialog({
			type : "iframe",
			value : dataUrl
		}, {
			showTitle : false,
			width : pos.width,
			height : "400px",
			pos : pos,
			resizable : false,
			esc:true
		});
		e5_form_event.curDialog.show();
	},
	initTabSelect : function(){
		/*
		//打开链接、专题、直播、活动、广告稿件时候，默认打开标题图片tab
		if(article.type >= 3){
			$('#divRight .tabs li').eq(0).addClass("select"); 
			$("#divRight div[id=tab1]").show();
		}
		*/
	}

};

//复制以使用表单定制中的方法
e5_form_event = {
	//---各种事件、回调事件---
	curDialog : null,
	_getDialogPos : function(el) {
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
	},
	//取消按钮。调after.do解锁
	doCancel : function(e) {
		if (!confirm("您确定要关闭吗？"))
			return false;
			
		window.onbeforeunload = null;
		
		$("#btnSave").disabled = true;
		$("#btnCancel").disabled = true;
		
		e5_form_event.doExit();
	},
	//关闭窗口。调after.do解锁
	beforeExit : function(e) {
		if (!confirm("您确定要关闭吗？"))
			return false;
		
		e5_form_event.doExit();
	},
	doExit : function(e) { 
		var dataUrl = "../../e5workspace/after.do?UUID=" + article.UUID;
		if (jabbarArticle=="true")
			{window.opener='';
			window.close();
			}
		//若是直接点窗口关闭，并且是chrome浏览器，则单独打开窗口
		else if (e && e5_form_event.isChrome())
			window.open(dataUrl, "_blank", "width=10,height=10");
		else
			window.location.href = dataUrl;
	},
	isChrome : function() {
		var nav = e5_form_event.navigator();
		return nav.browser == "chrome";
	},
	navigator : function(){
		var ua = navigator.userAgent.toLowerCase();
		// trident IE11
		var re =/(trident|msie|firefox|chrome|opera|version).*?([\d.]+)/;
		var m = ua.match(re);
		
		var Sys = {};
		Sys.browser = m[1].replace(/version/, "'safari");
		Sys.ver = m[2];
		return Sys;
	}
};
// 栏目选择窗口关闭，回调函数
function columnClose(filterChecked, checks) {
	// [ids, names, cascadeIDs]
	var name = article_all.columnFor;
	if (article_all.columnFor == "a_columnRel") {
		// 关联栏目中，自动滤掉主栏目
		var colIDs = article_all.filterCol($("#a_columnID").val(), checks[0]);
		var cols = article_all.filterCol($("#a_column").val(), checks[1]);

		$("#a_columnRelID").val(colIDs);
		$("#a_columnRel").val(cols);
	} else if (article_all.columnFor == "a_linkID"){
		$("#a_linkID").val(checks[0]);
		$("#a_linkName").val(checks[1]);
	} else {
		$("#" + name + "ID").val(checks[0]);
		$("#" + name).val(checks[1]);
	}
	columnCancel();
}

function columnCancel() {
	if (e5_form_event.curDialog)
		e5_form_event.curDialog.closeEvt();
	
	e5_form_event.curDialog = null;
}
function editClose(contents){
    contents = contents.replace(/&nbsp;/g, " ")
	if( article_all.editDialogType == 0 ){
		//标题字数统计 去掉样式统计
		var reg=/\<[\s\S]*?\>/g;
		if(reg.test(contents)){
            var _text=contents.replace(reg,"");
		}else{
            var _text=contents;
		}

		//去掉空格()
        // _text = _text.replace(/&nbsp;/g, " ")
        var length = _text.replace(/\s/g,'').length;

        var limited = 300;
        $("#lbwordcount").html(length + "/" + limited).css("color","black");
        if (length > limited){
            $("#lbwordcount").html("*最多输入" + limited + "个字").css("color","red");
            $("#SYS_TOPIC").val(_text.replace(/\s/g,'').substr(0, limited)); //超出范围的字截断
        }
		$("#SYS_TOPIC").val(contents);
        $("#SYS_TOPICDIV").text(contents);
	} else if ( article_all.editDialogType == 1 ){
		$("#a_abstract").val(contents);
	} else if ( article_all.editDialogType == 2 ){
		$("#a_subTitle").val(contents);
	}else if ( article_all.editDialogType == 3 ){
        $("#a_linkTitle").val(contents);
    }else if ( article_all.editDialogType == 4 ){
    	$("#a_shortTitle").val(contents);
    }
	article_all.editDialog.close();
}

function editCancel(){
	article_all.editDialog.close();
}
$(function() {
	article_all.init();
	
});

function getContent(){
	var content = '';
	if( article_all.editDialogType == 0 ){
		content = $("#SYS_TOPIC").val();
	} else if ( article_all.editDialogType == 1 ){
		content = $("#a_abstract").val();
	} else if ( article_all.editDialogType == 2 ){
		content = $("#a_subTitle").val();
	}else if ( article_all.editDialogType == 3 ){
        content = $("#a_linkTitle").val();
    }else if ( article_all.editDialogType == 4 ){
        content = $("#a_shortTitle").val();
    }
	return content;
}