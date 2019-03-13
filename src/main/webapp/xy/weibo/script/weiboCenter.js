var maxstrlen = 140;
var begin = 0;
var count = 30;
var commentsCurPage = 1;// 获取评论初始页是第一页
var repostsCurPage = 1;// 获取转发初始页是第一页
var timingFlag = 0;// 0-不定时发布；1-定时发布
var superDocid = 0;

var weibo_center = {
	// 未发布微博：修改操作，显示编辑框
	weiboEdit : function(event){
		var parent = $(event.target).parent();
		var docID = parent.attr("docID");
		
		superDocid = docID;
		// 根据ID查询内容
		$(".unpublic .text").show();
		$("#modifyDocID").attr("value", docID);
		
		var params = {
			docID : docID,
			docLibID : main_param.docLibID,
		}
		$.get("weibo/getWeibo.do", params, function(obj){
			$("#docId").val(docID);
			$("#modifyWeiboText").val($.trim(obj.content));
			$("#modifyWeiboText").trigger("click");
			
			var time = obj.publishTime;
			if (!time){
				$("#modifyTiming").val('');
				$('#nopubsendtime').hide();
				setTiming(0);
			}else{
				$("#modifyTiming").val(obj.publishTime);
				$('#nopubsendtime').show();
				setTiming(1);
			}
			// 如有图片加载页面，先关闭
			closeUploadMoreImages();
			
			if (obj.attachments) {
				var pics = obj.attachments.pics;
				if (pics && pics.length > 0) {
					$(this).uploadPic03("#nopubImgBtn");
					SUDOKUFILE.funLoadImages(pics);
				}
			}
		});
	},
	// 未发布微博：发布操作，显示编辑框
	weiboPublish : function(event){
		var parent = $(event.target).parent();
		var docID = parent.attr("docID");
		
		var params = {
			DocIDs : docID,
			DocLibID : main_param.docLibID,
		}
		$.post("weibo/publish.do", params, function(data){
			if (data) {
				alert(data);
			} else {
				weibo_center.refreshPage();
			}
		});
	},
	//未发布微博：再次显示图片（之前关闭了）。先从后台读一下
	reshowPics : function(id){
		$("#docId").val(id);
		
		// 如有图片加载页面，先关闭
		closeUploadMoreImages();
		
		var params = {
			docID : id,
			docLibID : main_param.docLibID,
		}
		$.get("weibo/getWeibo.do", params, function(obj){
			if (obj.attachments) {
				var pics = obj.attachments.pics;
				if (pics && pics.length > 0) {
					$(this).uploadPic03("#nopubImgBtn");
					SUDOKUFILE.funLoadImages(pics);
					return;
				}
			}
			$(this).uploadPic01("#nopubImgBtn");
		});
	},
	//加载文章原稿
	loadManuscript : function(){
		$.post("weibo/loadManuscript.do",{docid:superDocid},function(data){
			if (!data){
				alert('没有可加载的文章原稿');
				return;
			}
			$("#modifyWeiboText").val($.trim(data));
			$("#modifyWeiboText").trigger("click");
		});
	},
	//刷新列表
	refreshPage : function() {
		//刷新列表
		var doclist = e5.mods["workspace.doclist"];
		doclist.self.refreshPage();
	}
}

$(function(){
	/** 定时插件处理start */
	// 首页点击定时发布触发修改参数timingFlag的状态值
	$("#wb-content").delegate("#timingsend", "click", function(){
		$("#sendtime").toggle();
		
		if($('#sendtime').is(':visible')) {
			timingFlag = 1;
		} else {
			timingFlag = 0;
			$('#timing').val("");
		}
		$('#timingFlag').val(timingFlag);
	});
	
	//未发布编辑微博框中的定时发布
	$("#wb-content").delegate("#timeSendBtn", "click", function(){
		$("#nopubsendtime").toggle();
		
		if($("#nopubsendtime").is(":visible")) {
			timingFlag = 1;
		} else {
			timingFlag = 0;
			$("#timing").val("");
		}
		$("#timingFlag").val(timingFlag);
	});
	/** 定时插件处理end */
	
	/** #话题 */
	$("#topicBtn").click(function(){
		// 首页的话题功能=1，未发布的话题功能=2
		addConversation(1);
	});
	
	$("#nopubTopicBtn").click(function(){
		// 首页的话题功能=1，未发布的话题功能=2
		addConversation(2);
	});
	
	// 表情
	$('#wb-content').delegate(".face", "click" , function(event) {
		event.preventDefault();
		event.stopPropagation();
		
		var faceID = $(this).attr("id");
		if(faceID == 'homeFace') {
			$(this).sinaEmotion('#weiboText');
		}else if(faceID == 'commentFace') {
			$(this).sinaEmotion('#commentText');
		}else if(faceID == 'modifyFace') {
			$(this).sinaEmotion('#modifyWeiboText');
		}else if(faceID == 'replyFace') {
			$(this).sinaEmotion('#replyText');
		}
		return false;
	});
	
	//内容字数检查
	$('#weiboText').bind({
		click: function(){
			checkWord(this,'weiboText');
		},
		change: function(){
			checkWord(this,'weiboText');
		}
	});
	$('#weiboText').keyup(function(){
		checkWord(this,'weiboText');
	});
	
	// 编辑微博绑定字数限制
	$('#modifyWeiboText').bind({
		click: function(){
			checkWord(this,'modifyWeiboText');
		},
		change: function(){
			checkWord(this,'modifyWeiboText');
		}
	});
	$('#modifyWeiboText').keyup(function(){
		checkWord(this,'modifyWeiboText');
	});
	
	//定时发布日期选择控件
	var datetimeOptions = {
		language : 'zh-CN',
		weekStart : 0,
		todayBtn : 1,
		autoclose : 1,
		todayHighlight : true,
		startView : 2,
		minView : 0,
		disabledDaysOfCurrentMonth : 0,
		forceParse : 0,
		pickerPosition: "bottom-left",
		format : 'yyyy-mm-dd hh:ii'
	}
	$('.Wdate').datetimepicker(datetimeOptions);
	$('.Wdate').datetimepicker().on('changeDate', function(ev) {
	});
	
	//列表上一条未发布微博的“修改”操作
	$('#doclistframe').delegate(".opedit", "click" , weibo_center.weiboEdit);
	//列表上一条未发布微博的“发布”操作
	$('#doclistframe').delegate(".oppublish", "click" , weibo_center.weiboPublish);

	/** 九宫格图片处理 start */
	$('#layer_send_pic').hide();
	$('#sudokuUpload').hide();
	
	// 点击图片按钮，展示单图/多图页面
	$('#wb-content').delegate(".uploadPic", "click" , function(event) {
		event.preventDefault();
		event.stopPropagation();
		
		var imgID = $(this).attr("id");
		if(imgID == 'imgBtn') {
			$(this).uploadPic01('#imgBtn');
		}else if(imgID == 'nopubImgBtn') {
			var modifyDocID = $("#modifyDocID").val();
			// 获取图片，有则uploadPic03,无则uploadPic01;
			weibo_center.reshowPics(modifyDocID)
		}
		return false;
	});
	
	// 点击“单图/多图”，显示上传组件页面
	$('#wb-content').delegate(".layer_send_pic", "click" , function(event) {
		event.preventDefault();
		event.stopPropagation();
		
		var uploadPicid = $('a[class="uploadPic"]:visible').attr("id");
		uploadPicid = (uploadPicid == "nopubImgBtn") ? "#nopubImgBtn" : "#imgBtn";
		
		$(this).uploadPic02(uploadPicid);
		
		$("#fileImage").click();
		$("#layer_send_pic").hide();
		return false;
	});
	
    var w = $(window).width();
	$(".text").css({
		"left":(w/2)
	})
	
	// 为某个元素设置点击事件，点击弹出窗口
	$.fn.uploadPic01 = function(target) {
		target = target || function(){
			return $(this).parents('form').find('textarea,input[type=text]').eq(0);
		};
		var $that = $(target).last();
		var offset = $that.offset();
		var w = $(window).width();
		if ($that.is(':visible')){
			if (typeof target == 'function'){
				$target = target.call($that);
			}else{
				$target = $(target);
			}
			// 首页
			if($target.attr("id") == 'imgBtn'){
				$('#layer_send_pic').css({
					top: offset.top + 30 ,
//					left: (w/2)-450,
					"position":"fixed"
				}).show();
			}
			// 未发布
			if($target.attr("id") == 'nopubImgBtn'){
				$('#layer_send_pic').css({
					"top": "385px",
					left: (w/2)+70,
					"position":"fixed"
				}).show();
			}
		}
		return this;
	};
	// 显示图片区域，弹出图片上传窗口
	$.fn.uploadPic02 = function(target) {
		target = target || function(){
			return $(this).parents('form').find('textarea,input[type=text]').eq(0);
		};
		var $that = $(this).last();
		var offset = $that.offset();
		var w = $(window).width();
		if($that.is(':visible')){
			if(typeof target == 'function'){
				$target = target.call($that);
			}else{
				$target = $(target);
			}
			// 首页
			if($target.attr('id') == 'imgBtn'){
				$('#sudokuUpload').css({
					top: offset.top + 0 ,
					left: (w/2)-450,
					"position":"fixed"
				}).show();
			// 未发布
			} else if($target.attr('id') == 'nopubImgBtn') {
				$('#sudokuUpload').css({
					"top": "385px",
					left: (w/2)+70,
					"position":"fixed"
				}).show();
			}
		}
		uploadMoreImages();// 初始化上传图片插件
		return this;
	};
	// 未发布加载稿件图片，弹出九宫格窗口
	$.fn.uploadPic03 = function(target) {
		target = target || function(){
			return $(this).parents('form').find('textarea,input[type=text]').eq(0);
		};
		
		var $that = $(target).last();
		var offset = $that.offset();
		if($that.is(':visible')){
			if(typeof target == 'function'){
				$target = target.call($that);
			}else{
				$target = $(target);
			}
			if($target.attr('id') == 'nopubImgBtn') {
				$('#sudokuUpload').css({
					"top": "355px",
					left: (w/2) - 15,
					"position":"fixed"					
				}).show();
			}
		}
		uploadMoreImages();// 初始化上传图片插件
		return this;
	};
	
	// 点击div之外区域，隐藏div。 <div id="layer_send_pic" class="layer_send_pic">
	$(document).bind("click",function(e){
		var target = $(e.target);
		if (target.closest(".layer_send_pic").length == 0){
			$(".layer_send_pic").hide();
		}
	});
	// 关闭上传图片插件
	$('#sudokuUpload').delegate('#close','click',function(){
		closeUploadMoreImages();
	});
	/** 图片 end */
	
	// 点击图片，默认编辑器无内容则填入#分享图片#
	$('#wb-home').delegate('#imgBtn', 'click', function(){
		$('#weiboText').html('分享图片');
	});
	
	// 动态绑定评论事件
	$('.weibo-feed').delegate('.commentBtn', 'click', function() {
		submitComment();
	});
	// 动态绑定回复事件
	$('.weibo-feed').delegate('.replyBtn', 'click', function() {
		submitReply();
	});
	// 动态绑定点击回复出现或隐藏回复框
	$('.weibo-feed').delegate('.reply', 'click', function() {
		showReply(this);
	});
	
	//微博修改窗口的隐藏事件
	$(".unpublic .img").click(function() {
		$(".unpublic .text").hide();
		closeUploadMoreImages();
	});
	
	//评论列表和转发列表窗口的隐藏事件
	$(".weibo-feed .closeBtn").click(function() {
		$(".weibo-comments").hide();
		$(".weibo-reposts").hide();
	});
});

//加载文章原稿
$("#loadManuscript").click(weibo_center.loadManuscript);

// 定时发送赋值
function setTiming(tFlag){
	timingFlag = (tFlag == null ? 0 : tFlag) ;
	$('#timingFlag').val(timingFlag);
}

/** 检查字数 */
function checkWord(c,id) {
	len = maxstrlen;
	var str = c.value;
	var myLen = getStrLen(str);
	if (myLen > maxstrlen * 2) {
		if('weiboText'==id){
			var wck = document.getElementById("wordCheck");
			$("#wordType").text("已超过");
			wck.innerHTML = Math.round((myLen - len * 2) / 2);
			$("#pubButton").attr("disabled", "disabled");
		}else{
			// 修改微博弹出框
			$("#modifyFlag").html("已超过");
			$("#modifySurplusWord").html(Math.round((myLen - len * 2) / 2));
			$("#modifyPubButton").attr("disabled", "disabled");
			$("#modifyPubButton").css("background", "#58BFED");
			$("#saveBtn").attr("disabled", "disabled");
			$("#saveBtn").css("background", "#E6E6E6");
		}
	} else {
		if('weiboText'==id){
			var wck = document.getElementById("wordCheck");
			$("#wordType").text("还可以输入");
			wck.innerHTML = Math.floor((len * 2 - myLen) / 2);
			$("#pubButton").removeAttr("disabled");
		}else{
			// 修改微博弹出框
			$("#modifyFlag").html("还可以输入");
			$("#modifySurplusWord").html(Math.floor((len * 2 - myLen) / 2));
			$("#modifyPubButton").removeAttr("disabled");
			$("#modifyPubButton").css("background", "#00a0e6");
			$("#saveBtn").removeAttr("disabled");
			$("#saveBtn").css("background", "#faa424");
		}
	}
}
function getStrLen(str) {
	myLen = 0;
	i = 0;
	for (; i < str.length; i++) {
		if (str.charCodeAt(i) > 0 && str.charCodeAt(i) < 128)
			myLen++;
		else
			myLen += 2;
	}
	return myLen;
}
// 添加#话题
function addConversation(type){
	var str = "#在这里输入你想要说的话题#";
	if (1==type){
		$('#weiboText').val(str);
	}else{
		$('#modifyWeiboText').val(str);
	}
}

//微博提交
function weiboSubmit(type){
	var weiboText,isPublish;
	
	// type：1首页发布，2未发布微博的发布，3未发布微博的保存
	var docId = $("#docId").val();
	var timing = $("#timing").val();
	var weiboType = $("#weiboType").val();
	if (!weiboType) weiboType = "1";// 默认文字
	
	var picArr = new Array();
	// 如果发布的是图片稿件，则取上传图片的guid 
	// TODO:改成翔宇的图片上传，收集picPath
	if (weiboType == 2) {
		$("div[id^='uploadList_']:visible").each(function(){
			var picPath = $(this).children("p[id^='uploadGuid_']").attr("guid");
			if (picPath != null){
				picArr.push(picPath);
			}
		});
	}
	// 检查定时发
	if (timingFlag == 1) {
		if (type == 1 && !$('#timing').val()
			|| type > 1 && !$('#modifyTiming').val()){
			alert('定时发送的时间不能为空。');
			return false;
		}
		isPublish = 0;// 不发布，保存
	} else {
		isPublish = (type < 3) ? 1 : 0;
	}
	
	if (1==type){
		weiboText = $("#weiboText").val();
		docId = 0;// 新建微博，初始化为0；
	} else if (type == 2 || type == 3){
		weiboText = $("#modifyWeiboText").val();
		timing = $('#modifyTiming').val();
	}else{
		alert("系统错误，请联系管理员");
		return false;
	}
	
	var theURL = "weibo/formSave.do";
	var attachments = {
		pics : picArr,
		videos : []
	}
	var params = {
		accountID: main_param.groupID,
		docLibID : main_param.docLibID,
		docId: docId,
		content: weiboText,
		isPublish: isPublish,
		pubTimer: timingFlag,
		pubTime: timing,
		attachments: JSON.stringify(attachments)
	};
	$.post(theURL, params, function(error){
		if (error) {
			alert(error);
		}
		//刷新列表
		weibo_center.refreshPage();
		
		if (1==type){
			$('#weiboText').val("");// 清空微博编辑器内容
			$('#timing').val("");
		} else {
			$('#modifyWeiboText').val("");// 清空微博编辑器内容
			$('#modifyTiming').val("");
		}
		$(".unpublic .text").hide();
		
		if(weiboType == 2) closeUploadMoreImages();
	});
	return false;
}

//---------------------转发--------------------------
//查看转发列表
function showReposts(evt){
	evt = evt || event;
	var parent = $(evt.target).parent();
	var docID = parent.attr("docID");
	
	// 如果#weibo-reposts可见
	if($('#weibo-reposts').is(':visible')){
		$("#weibo-reposts").hide();
		$(".weibo-reposts-1").html("");
		return true;
	} else {
		$("#weibo-reposts").show();
		$("#weibo-comments").hide();
		
		$("#cBelongtoDocId").val(docID);
		
		var params = {
			docID : docID,
			docLibID : main_param.docLibID,
			page:repostsCurPage
		}
		$.get("weibo/getReposts.do", params, showRepostList);
		return false;
	}
}

//显示页面上的转发列表（一页）
function showRepostList(data) {
	var weibRepostsHTML = "";
	
	if (data) data = data.reposts;
	if (data && data.length > 0) {
		for(var i = 0; i < data.length; i++) {
			weibRepostsHTML += '<div id="repost_' +  data[i].id+'" class="weibo-reposts-list-one">'
			+ '<div class="left wbpoto_Left">'
			+ 	'<img src="' + data[i].user.profile_image_url + '">'
			+ '</div>'
			+ '<div class="left ment_Right">'
			+   	'<a class="yellow">' + data[i].user.screen_name + ':</a><a class="black">' + data[i].text + '</a><br>'
			+   	'<a class="left Grey"><span class="right wbtime">' + getFormatDate(data[i].created_at) + '</span>';
			weibRepostsHTML += '</a>'
			+  '</div>'
			+  '<div class="clear weibo-reposts-line"></div>'
		+ '</div>'
		}
		$(".weibo-page").show();
	}
	$(".weibo-reposts-1").html(weibRepostsHTML);
}
//转发列表翻页
function repostsTurnPage(){
	var docID = $("#cBelongtoDocId").val();
	var params = {
		docID : docID,
		docLibID : main_param.docLibID,
		page:repostsCurPage
	}
	$.get("weibo/getReposts.do",params, function(data){
		if (data && data.reposts && data.reposts.length > 0) {
			showRepostList(data);
		}else{
			decRepostsCurPage();
			alert('已经是最后一页了');
		}
	});
	return false;
}
//repostsCurPage
function initRepostsCurPage(){
	repostsCurPage = 1;
}
// 设置读取页数，滚动改变page值,加一
function incRepostsCurPage(){
	repostsCurPage ++ ;
}
//设置读取页数，滚动改变page值，减一
function decRepostsCurPage(){
	repostsCurPage = (repostsCurPage --) <= 1 ? 1 : repostsCurPage -- ;
}
//上一页
function preRepostsPage(){
	if (repostsCurPage == 1){
		alert('当前页是第一页');
	}else{
		decRepostsCurPage();
		repostsTurnPage();
	}
}
// 下一页
function nextRepostsPage(){
	incRepostsCurPage();
	repostsTurnPage();
}

//----------------------评论-------------------------
// 点击评论触发事件
function showComments(evt){
	evt = evt || event;
	var parent = $(evt.target).parent();
	var docID = parent.attr("docID");
	
	var commentList = $("#weibo-comments");
	if (commentList.is(':visible')){
		commentList.hide();
		$(".weibo-comment-1").html("");
		return true;
	} else {
		$("#weibo-reposts").hide();
		
		commentList.show();
		
		// 初始化评论所属微博docID
		$("#cBelongtoDocId").val(docID);
			
		var params = {
			docID : docID,
			docLibID : main_param.docLibID,
			page: commentsCurPage
		}
		
		$.get("weibo/getComments.do", params, showCommentList);
		return false;
	}
}
//生成页面上的评论列表（一页）
function showCommentList(data) {
	var weibCommentHTML = "";
	
	if (data) data = data.comments;
	if (data && data.length > 0) {
		for(var i = 0; i < data.length; i++) {
			weibCommentHTML += '<div id="comment_' + data[i].id  + '" class="left weibo-comment">'
			+	'<div class="left wbpoto_Left"><img src="' + data[i].user.profile_image_url + '"/></div>'
			+	'<div class="left ment_Right">'
			+ 		'<a class="yellow">' + data[i].user.screen_name + ':</a>'
			+		'<a class="black">' + data[i].text + '</a><br />'
			+		'<a class="left Grey">'
			+			'<span class="right wbtime">' + getFormatDate(data[i].created_at) + '</span>';
			
			weibCommentHTML += '</a>'
			+		'<a class="right laud">'
			+			'<span class="reply" id="reply_' + data[i].id + '" commentid="' + data[i].id + '"docLibID="" docID="">回复</span>'	
			+		'</a>'
			+	'</div>'  
			+	'<div class="clear weibo_comment_line"></div>'
			+'</div>';
		}
	}
	$(".weibo-page").show();
	
	$(".weibo-comment-1").html(weibCommentHTML);
}
function getFormatDate(value) {
	var date = new Date(value);
	return date.toLocaleString();
}

// 提交评论
function submitComment(){
	var docID = $("#cBelongtoDocId").val();
	var commentText = $("#commentText").val();
	
	if (!docID) { 
		alert("docID null?");
		return false;
	}
	if ( !commentText) {
		alert("评论内容不能为空。");
		return false;
	}

	// 发布评论url
	var theURL = "weibo/addComment.do";
	var params = {
		docID : docID,
		docLibID : main_param.docLibID,
		content: commentText
	}
	$.post(theURL, params, function(data){
		if (data > 0){
			alert("评论发布成功！");
			commentsRefresh();
		} else {
			alert("评论发布失败，请重新发布！")
		}
		$('#commentText').val("");// 清空评论框内容
	});
	return false;
}

//显示评论的回复
function showReply(src){
	src = $(src);
	
	var curcommentid = src.attr('commentid');
	var curprediv = $('#comment_' + curcommentid).find('.ment_Right');		
	var curweiboreply = $('#comment_' + curcommentid).find('.weibo-reply');
	var outerweiboreply = $('.weibo-feed').find('.weibo-reply');
	var replyhtml = "";
	
	if (outerweiboreply.length > 0){
		if(curweiboreply.length > 0){
			curweiboreply.remove();
			return false;
		} else {
			outerweiboreply.remove();
		}
	}
	replyhtml = creatReplyForm();
	// 在prediv之后添加weiboreply
	$(curprediv).after(replyhtml);
}

//创建评论回复窗口
function creatReplyForm(){
	var html = ""; 
	html += '<div class="clear weibo-reply">'
	+	'<form  class="weibo-reply-form"  id="addReplyForm" action="" method="post" role="form3">'
	+		'<div>'
	+			'<input class="wb_reply_text" type="text" id="replyText" />'
	+		'</div>'
	+		'<div class="we_reply_editor">'
	+			'<div class="left">'
	+				'<span class="">'
	+					'<a class="face" href="" id="replyFace">'
	+						'<img src="./weibo/images/6.png" alt=""/>'
	+						'<span>表情</span>'
	+					'</a>'
	+				'</span>'
	+			'</div>'
	+			'<div class="right">'
	+				'<button type="button" class="btn btn-default replyBtn" name="replyBtn">评论</button>'
	+			'</div>'
	+		'</div>'
	+	'</form>'
	+'</div>';
	return html;
}

//提交评论的回复
function submitReply(){
	var docID = $("#cBelongtoDocId").val();
	var replyText = $("#replyText").val();
	var commentID = $('.weibo-reply').parent().attr('id');
	commentID = commentID.replace('comment_','');
	
	if (!docID) { 
		alert("docID is null?");
		return false;
	}
	if( !replyText) {
		alert("评论内容不能为空。");
		return false;
	}
	
	// 发布回复url
	var theURL = "weibo/addReply.do";
	var params = {
		docID: docID,
		docLibID : main_param.docLibID,
		cid: commentID,
		content: replyText
	}
	$.post(theURL, params, function(data){
		if (data > 0){
			alert("回复发布成功！");
			commentsRefresh();
		} else {
			alert("回复发布失败，请重新发布！")
		}
		$('#replyText').val("");// 清空评论框内容
	});
	return false;
}
//评论列表翻页
function commentsTurnPage(){
	var docID = $("#cBelongtoDocId").val();
	var params = {
		docID : docID,
		docLibID : main_param.docLibID,
		page: commentsCurPage
	}
	
	$.get("weibo/getComments.do",params, function(data){
		if (data && data.comments && data.comments.length > 0) {
			showCommentList(data);
		} else {
			decCommentsCurPage();
			alert('已经是最后一页了');
		}
	});
	return false;
}
//刷新评论列表（提交评论或回复后）
function commentsRefresh() {
	initCommentsCurPage();
	commentsTurnPage();
}

// 初始化commentsCurPage
function initCommentsCurPage(){
	commentsCurPage = 1;
}
// 设置读取评论的页数，滚动改变page值,加一
function incCommentsCurPage(){
	commentsCurPage ++ ;
}
//设置读取评论的页数，滚动改变page值，减一
function decCommentsCurPage(){
	commentsCurPage = (commentsCurPage --) <= 1 ? 1 : commentsCurPage -- ;
}
//上一页
function preCommentsPage(){
	if(commentsCurPage == 1) {
		alert('当前页已是第一页');
	}else{
		decCommentsCurPage();
		commentsTurnPage();
	}
}
// 下一页
function nextCommentsPage(){
	incCommentsCurPage();
	commentsTurnPage();
}
