'use strict';
var article_all = {
	artDialog : null,
	init : function() {
		$("#btnSave").click(article_all.save);
		$("#tobottomSave").click(article_all.save);
		$("#btnCancel").click(e5_form_event.doCancel);
		$("#tobottomCancel").click(e5_form_event.doCancel);
		
		$("#selectEditor").click(article_all.selectEditor);
		
		$("#toxyuser").click(article_all.clickRadio);
		
		window.onbeforeunload = e5_form_event.beforeExit;
		$(document).keydown(article_all.refreshF5); //F5刷
		if(topic.isNew&&topic.isNew!='false'){
           $("#topicId").css("display","none");
		}else{
			$("#topicId").css("display","block");
		}
	},
	//updateInput: function (obj) {
	//	//清空显示信息
	//	if(obj){
	//		$("#"+ obj).val('');
	//		$("#a_icon").attr('src','../image.do?path=')
	//	}
	//},

	//点击保存按钮
	save : function() {
		var topicName = $("#topic").val().trim();
		$("#topic").val(topicName);
		$("#a_color").val();
		$(".showColor").css('background-color');
        $("#a_status").val($("input[name='topicStatus']:checked").val());
		if(topicName==""){
			alert("请输入话题名称");
			return false;
		} else if (topicName.length > 50){
			alert('话题名字最大长度不能超过50');
			return false;
		} else {
			$.ajaxSettings.async = false;
			var params = {
				DocLibID: topic.docLibID,
				DocIDs: topic.docID,
				siteID: topic.siteID,
				value: topicName
			}
			var topicBoolean = false;
			$.get('TopicDuplicate.do', params, function(data) {
				if (parseInt(data) == 1) {
					topicBoolean = true;
				}
			});
			if (topicBoolean) {
				alert('当前话题名称已存在，请不要重复创建。');
				return false;
			}
		}
		if($("#a_color").val()==""){
			alert("请输入话题颜色");
			return false;
		}
		// console.log($("#a_icon").val());
		if($("#picBig").attr("src")!=undefined){
			$("#a_icon").val($("#picBig").attr("src"));
		}
		// console.log($("#picBig").val(channel_frame._imgSrc($("#picBig").attr("src"))));
		// console.log(channel_frame._imgSrc($("#picBig").attr("src")));
		// console.log($("#picBig").attr("src"));
		// console.log($("#a_icon").val());
		if (!$("#a_icon").val()) {
			if (!confirm("还没有上传话题图片，是否提交")) return;
		}

    window.onbeforeunload = null;
		$("#form").submit();
	},
  getContent: function (content) {
    return $.trim(content);
	  console.log(content);
	  console.log($.trim(content));
  },
	//收集附件数据（正文图片、正文视频、标题图）
	_getAttachments : function() {
		var atts = {pics:[], videos:[]};
		
		//加入标题图
		if ($("#picBig").val()) atts.picBig = $("#picBig").val();
		if ($("#picMiddle").val()) atts.picMiddle = $("#picMiddle").val();
		if ($("#picSmall").val()) atts.picSmall = $("#picSmall").val();
		
		if (pics) {
			for (var i = 0; i < pics.length; i++) {
				atts["pics"].push(pics[i].path); //互动附件比稿件的附件简单，只取路径。
			}
		}
		$("#a_attachments").val(JSON.stringify(atts));
	},
	initUserInfo : function(docLibID, docID, flag) {
		//$("#askxyuser").prop('checked', true);
		var urlStr = "../../xy/user/getUserInfo.do";
		$.ajax({
	        url: urlStr,
	        type: 'POST',
	        data: {
	            "docIDs": docID,
	            "docLibID": docLibID,
	        },
	        success: function (msg) {
	        	$("#picIcon").attr('src', topic.logoUrl);
	        	$("#askToTittle").val("答你我所知");
	        	if(!flag)$("#commentss").val(msg.userDescript);
	        	$("#toxyuser").val(msg.userName);
	        	//隐藏域赋值
	        	$("#checkID").val(docID);//选中的小编或用户ID
	        	$("#checkName").val(msg.userName);//选中的小编或用户名字
	        	$("#checkType").val(msg.askType);//选中的小编或用户类型
		
		        //关闭弹窗
		        groupSelectCancel();
				console.log(msg);
			}
	    });
	},
	//按F5的响应
	refreshF5 : function(evt) {
		if (evt.keyCode == 116) { //F5
			window.onbeforeunload = null;
		}
	},
};

function groupSelectOK(docLibID,docID) {
    if(docID.split(",").length>1) {
        alert("只能选择一个！");
        return;
    }
    article_all.initUserInfo(docLibID,docID,false);
}

function groupSelectCancel(){
	article_all.artDialog.hide();
}

//复制以使用表单定制中的方法
var e5_form_event = {
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
		var dataUrl = "../../e5workspace/after.do?UUID=" + topic.UUID;
		//若是直接点窗口关闭，并且是chrome浏览器，则单独打开窗口
		if (e && e5_form_event.isChrome())
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

//每个渠道的稿件主体js，控制开始的初始化，以及最后提交时收集数据
var channel_frame = {

	init : function() {
		$(".picTopic").click(channel_frame.picTopic);
		//给删除添加事件
        channel_frame.titleDelListener("div[id^=topicPic]");
	},

	picTopic : function(e) {
		//获得图片的路径以及类型
		var _path = channel_frame._imgSrc($(this).find("img").attr("src"));
		//获取预览图的原始宽高
		$(this).find("img").css("max-width",'none');
		var imgWidth = parseInt(channel_frame._imgSrc($(this).find("img").css("width")));
		var imgHeight = parseInt(channel_frame._imgSrc($(this).find("img").css("height")));
		$(this).find("img").css("max-width",'100%');
		var _radio = imgWidth+"*"+imgHeight;
		// var _iType = $(this).attr("itype");
		//如果有图片被设置了大中小任意一个标题图，则点击显示的时候只显示被选中的图片
		// if(_path.indexOf("/t0_(")==-1 && _path.indexOf("/t1_(")==-1 && _path.indexOf("/t2_(")==-1){
		// 	_iType = "all";
		// }
		//只显示大图的展示
        var _iType = "big";
		var pos = {left : "0px",top : "0px",width : "1200px",height : "580px"};

		channel_frame.titleDialog = e5.dialog({
			type : "iframe",
			value : '../../xy/ueditor/initTitleDialog.do?imagePath=' + _path
			+"&radio="+ _radio
			+ "&itype=" + _iType + "&siteID=" + topic.siteID
		}, {
			showTitle : true,
			title: "设置话题图片",
			width : "1200px",
			height : "580px",
			pos : pos,
			resizable : false
		});
		//}
		channel_frame.titleDialog.show();
    },
	titleDialog : null,
	smallC : "",
	midC : "",
	bigC : "",
	titleImagePath : "",
	widthRadio: 0,
	heightRadio:0,
	assignedProperties: false,
	isLocalFile:false,
	fileName: "",
	submitCount: 0,
	setImageProperties : function(_smallC, _midC, _bigC, _imagePath, _widthRadio, _heightRadio, smallCR, midCR, bigCR){

		channel_frame.smallC = _smallC;
		channel_frame.midC = _midC;
		channel_frame.bigC = _bigC;
		channel_frame.titleImagePath = _imagePath;
		channel_frame.widthRadio = _widthRadio;
		channel_frame.heightRadio = _heightRadio;
		channel_frame.smallCR = smallCR;
		channel_frame.midCR = midCR;
		channel_frame.bigCR = bigCR;
	},
	resetImageProperties: function(){
		channel_frame.smallC = null;
		channel_frame.midC = null;
		channel_frame.bigC = null;
		channel_frame.titleImagePath = null;
		channel_frame.widthRadio = null;
		channel_frame.heightRadio = null;
		channel_frame.smallCR = null;
		channel_frame.midCR = null;
		channel_frame.bigCR = null;
	},
	setTitleImage : function(){
		if(!channel_frame.smallC && !channel_frame.midC && !channel_frame.bigC){
			alert("请选择裁剪区域！");
			return;
		}
		$.ajax({
			url : "../../xy/ueditor/createtitleImg.do",
			type : 'POST',
			async : true,
			data : {
				"imagePath" : channel_frame.titleImagePath,
				"docLibID" : topic.docLibID,
				"docID" : topic.docID,
				"smallCoords" : JSON.stringify(channel_frame.smallC),
				"midCoords" : JSON.stringify(channel_frame.midC),
				"bigCoords" : JSON.stringify(channel_frame.bigC),
				"widthRadio" : channel_frame.widthRadio,
				"heightRadio" : channel_frame.heightRadio,
				"smallCR" : channel_frame.smallCR,
				"midCR" : channel_frame.midCR,
				"bigCR" : channel_frame.bigCR,
				"imageName" : channel_frame.fileName,
				"isLocalFile" : channel_frame.isLocalFile
			},
			dataType : 'json',
			success : function(_msg, status) {
				channel_frame.handleTitleImgHtml(_msg);
			},
			error : function(xhr, textStatus, errorThrown) {
			}
		});
	},
	//标题图的删除功能模块
	titleDelListener : function(_id){
		$(_id).unbind("mouseover").unbind("mouseout");
		$(_id).has("img:[src!='']").each(function(){
			var _$this = $(this);
			var _type = _$this.attr("itype");
			//获得小中大
			var _name = _type=="small" ? "小" : ( _type == "mid" ? "中" : ( _type=="big" ? "大" : ""));
			//给里面的删除图片添加一个点击事件
			_$this.find("span").click(function(e){
				e.stopPropagation();
				//阻止父级的事件冒泡
				_$this.html("<p class='plus'>+</p>")
				// _$this.html("<p class='plus'>+</p>" + "<p class='word'>"+ _name +"图</p>")
			});
			//添加鼠标悬浮事件
			_$this.mouseover(function(){
				$(this).find("span").show();
			});
			_$this.mouseout(function(){
				$(this).find("span").hide();
			});
		});
	},
	//设置标题图 - 使用原图
	handleTitleImgHtml : function(_msg){
		if( _msg.imgSmall ){
			$("#topicPicSmallDiv").html('<img id="picSmall" itype="small" src="../'+ decodeURI(_msg.imgSmall.substr(_msg.imgSmall.lastIndexOf('image.do?path=')))+'"/><span class="icon-remove"></span>')
			channel_frame.titleDelListener("#topicPicSmallDiv");
		}
		if( _msg.imgMid ){
			$("#topicPicMidDiv").html('<img id="picMiddle" itype="mid" src="../'+decodeURI(_msg.imgMid.substr(_msg.imgMid.lastIndexOf('image.do?path=')))+'"/><span class="icon-remove"></span>')
			channel_frame.titleDelListener("#topicPicMidDiv");
		}
		if( _msg.imgBig ){
			$("#topicPicBigDiv").html('<img id="picBig" itype="big" src="../'+decodeURI(_msg.imgBig.substr(_msg.imgBig.lastIndexOf('image.do?path=')))+'"/><span class="icon-remove"></span>')
			channel_frame.titleDelListener("#topicPicBigDiv");
		}

		channel_frame.titleDialog.close();
	},
	_imgSrc : function(src) {
		if (src) {
			var selector = "image.do?path=";
			var pos = src.indexOf(selector);
			if (pos > 0) {
				return src.substring(pos + selector.length);
			} else if (src.indexOf("/ueditor/") < 0){
				return src; //外网图片
			}
		}
		return "";
	}
}

$(function() {
	article_all.init();
	channel_frame.init();
});
