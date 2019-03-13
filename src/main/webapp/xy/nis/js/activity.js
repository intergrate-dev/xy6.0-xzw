var article_all = {
	init : function() {
		$("#btnSave").click(article_all.save);
		$("#btnCancel").click(e5_form_event.doCancel);
		
		window.onbeforeunload = e5_form_event.beforeExit;
		$(document).keydown(article_all.refreshF5); //F5刷新
		
		article_all.initDateTimePicker();
		
		//设置验证
		$("#form").validationEngine({
			autoPositionUpdate:true,
			promptPosition:"bottomLeft",
			scroll:true
		});
	},
	initDateTimePicker : function(){
		$('#a_endTime').datetimepicker({
            language: 'zh-CN',
            weekStart: 1,
            todayBtn: 1,
            autoclose: 1,
            todayHighlight: 1,
            startView: 2,
            forceParse: 0,
            showMeridian: 1,
            format: 'yyyy-mm-dd hh:ii:ss',
            startDate:new Date(),
            minView: 0
        }).on('changeDate', function(ev){
            var $this = $("#a_endTime");
            $this.attr("class", "artMeg validate[required,custom[dateTimeFormat1]]");
            var str = $this.val();
            str = str.substring(0, str.length - 2) + "00";
            $this.val(str);
            $('#a_startTime').datetimepicker('setEndDate', $this.val());
        });

        $('#a_startTime').datetimepicker({
            language: 'zh-CN',
            weekStart: 1,
            todayBtn: 1,
            autoclose: 1,
            todayHighlight: 1,
            startView: 2,
            forceParse: 0,
            showMeridian: 1,
            format: 'yyyy-mm-dd hh:ii:ss',
            // startDate:new Date(),
            minView: 0
        }).on('changeDate', function(ev){
            var $this = $("#a_startTime");
            $this.attr("class", "artMeg validate[required,custom[dateTimeFormat1]]");
            var str = $this.val();
            str = str.substring(0, str.length - 2) + "00";
            $this.val(str);
            $('#a_endTime').datetimepicker('setStartDate', $this.val());
        });
	},
	//点击保存按钮
	save : function() {
		$("#a_startTime").attr("class", "artMeg validate[required,custom[dateTimeFormat1]]");
        $("#a_endTime").attr("class", "artMeg validate[required,custom[dateTimeFormat1]]");
        //确保开始时间小于结束时间
        var beginTime=$("#a_startTime").val();
        var endTime=$("#a_endTime").val();
        var beginTimeNum= new Date(beginTime);
        var endTimeNum= new Date(endTime);
        if(beginTimeNum.getTime()>endTimeNum.getTime()){
            alert("活动开始时间不能晚于结束时间，请重新选择！");
            return
        };
		$("#SYS_TOPIC").val($("#SYS_TOPIC").val().trim());
		if (!$("#form").validationEngine("validate")){
            // 验证提示
            $("#form").validationEngine("updatePromptsPosition");
            return false;
        }

		$("#a_picSmall").val(channel_frame._imgSrc($("#picSmall").attr("src")));
		$("#a_picMiddle").val(channel_frame._imgSrc($("#picMiddle").attr("src")));
		$("#a_picBig").val(channel_frame._imgSrc($("#picBig").attr("src")));
		
		if (!$("#a_picBig").val() && !$("#a_picSmall").val() && !$("#a_picMiddle").val()) {
			if (!confirm("还没有上传标题图片，是否提交？")) return;
		}
		
		$("#a_content").val(channel_frame.getContent());//加上正文内容
		
		article_all._getAttachments();
		//$("#editorValue").val("");//自动把编辑器的内容也读出来了，去掉它
		
		window.onbeforeunload = null;
		$("#form").submit();
	},
	//收集附件数据（正文图片、正文视频、标题图）
	_getAttachments : function() {
		var atts = {pics:[], videos:[]};
		
		//加入标题图
		if ($("#a_picBig").val()) atts.picBig = $("#a_picBig").val();
		if ($("#a_picMiddle").val()) atts.picMiddle = $("#a_picMiddle").val();
		if ($("#a_picSmall").val()) atts.picSmall = $("#a_picSmall").val();
		
		//加入正文图片
		var pics = channel_frame._getPicData();
		if (pics) {
			for (var i = 0; i < pics.length; i++) {
				atts["pics"].push(pics[i].path); //互动附件比稿件的附件简单，只取路径。
			}
		}
		//加正文视频
		atts["videos"] = channel_frame._getVideoData();
		
		$("#a_attachments").val(JSON.stringify(atts));
	},
	//按F5的响应
	refreshF5 : function(evt) {
		if (evt.keyCode == 116) { //F5
			window.onbeforeunload = null;
		}
	}
};

//复制以使用表单定制中的方法
e5_form_event = {
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

//----------------------------------------
//每个渠道的稿件主体js，控制开始的初始化，以及最后提交时收集数据
var channel_frame = {
	content : "", //文章正文

	init : function() {
		channel_frame.setContent();
		$(".picTopic").click(channel_frame.picTopic);
	},

	//编辑器加载内容
	setContent : function() {
		if (typeof UE == "undefined") {
			return;
		}

		var useEditor = channel_frame._useEditor();
		var editorName = useEditor ? "editor" : "simpleEditor";

		var editor = UE.getEditor(editorName);
		editor.ready(function() {
			var content = channel_frame.decode(channel_frame.content);
			editor.setContent(content);
		});
		$('.picTopic').each(function(index, el) {
			channel_frame.titleDelListener('#' + this.id);
		});
	},
	decode : function(content) {
		content = content.replace(/&amp;/g, "&");
		content = content.replace(/&lt;/g, "<");
		content = content.replace(/&gt;/g, ">");
		content = content.replace(/&#034;/g, "\"");

		return content;
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
		var _iType = $(this).attr("itype");
		//如果有图片被设置了大中小任意一个标题图，则点击显示的时候只显示被选中的图片
		if(_path.indexOf("/t0_(")==-1 && _path.indexOf("/t1_(")==-1 && _path.indexOf("/t2_(")==-1){
			_iType = "all";
		}
		var pos = {left : "100px",top : "50px",width : "1000px",height : "500px"};

		//如果之前没有生成dialog
		//if(! channel_frame.titleDialog){
		channel_frame.titleDialog = e5.dialog({
			type : "iframe",
			value : '../../xy/ueditor/initTitleDialog.do?imagePath=' + _path
			+"&radio="+ _radio
			+ "&itype=" + _iType + "&siteID=" + article.siteID
		}, {
			showTitle : true,
			title: "设置标题图",
			width : "1000px",
			height : "610px",
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
		//+console.info(channel_frame.smallC, channel_frame.midC,channel_frame.bigC);
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
				"docLibID" : article.docLibID,
				"docID" : article.docID,
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
				/*if( _msg.imgSmall ){
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

				 channel_frame.titleDialog.close();*/
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
				_$this.html("<p class='plus'>+</p>" + "<p class='word'>"+ _name +"图</p>")
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
	//读编辑器的内容
	getContent : function() {
		if (typeof UE == "undefined") return "";

		var editorName = channel_frame._useEditor() ? "editor" : "simpleEditor";
		var editor = UE.getEditor(editorName);
		// 如果当前是源码编辑模式，=1，否则=0
		if(editor.queryCommandState("source") == 1){
			editor.execCommand("source");
		}
		return editor.getContent();
	},

	/**
	 * 正文图片的返回格式：[{path:<path>}, {....}, .....]
	 * 组图的返回格式：[{path:<path>,content:<content>,isIndexed:<0/1>}, {....}, .....]
	 */
	_getPicData : function() {
		if (channel_frame._useEditor()) {
			//文章稿中的图片，从编辑器中获得
			var editor = UE.getEditor("editor");
			/*var imgs=editor.document.getElementsByTagName("img");
			for(var i = 0; _img = imgs[i]; i++){
				_src = _img.src;
        		if(_src.indexOf("../")==-1){
        			_src = "../../" + _src.substring(_src.indexOf("xy/image.do?path="));
        			_img.src = _src;
        		}

        	}*/
			channel_frame._filterImgSrc();
			var pics = editor.getContentPics();

			var result = [];
			for (var i = 0; i < pics.length; i++) {
				//把图片Url的前缀部分去掉
				var imgurl = channel_frame._imgSrc(pics[i].path);
				if (imgurl) {
					pics[i].path = imgurl;
					result.push(pics[i]);
				}
			}
			return result;
		}
	},
	/**
	 * 过滤编译器里面的src
	 */
	_filterImgSrc : function(){
		//获得编译器对象
		var editor = UE.getEditor("editor");

		(function(_editor){
			//内容
			var backContent = _editor.getContent();
			var resultContent = "";
			while(-1 != (cutNum = backContent.indexOf("xy/image.do"))){
				frontContent = backContent.substr(0, cutNum);
				targetCutNum = frontContent.lastIndexOf("\"") + 1;
				targetContent = frontContent.substr(targetCutNum);
				if(targetContent != "../../"){
					frontContent = frontContent.substr(0, targetCutNum) + "../../";
				}
				resultContent += frontContent + "xy/image.do";
				backContent = backContent.substr(cutNum + 11);

			}
			resultContent += backContent;
			_editor.setContent(resultContent);
		})(editor);
	},

	/**
	 * 返回格式：[{url:"http://.....", urlApp:"http://.....", videoID:"12,2355"}, {...},...]
	 */
	_getVideoData : function() {
		var result = null;
		if (channel_frame._useEditor()) {
			//文章稿中的视频，从编辑器中获得
			var editor = UE.getEditor("editor");

			result = editor.getContentVideos();
		}
		return result;
	},
	//判断是否使用大编辑器（稿件、活动）
	_useEditor : function() {
		return true;
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
