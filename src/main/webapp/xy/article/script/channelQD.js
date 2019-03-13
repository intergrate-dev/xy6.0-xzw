$(function(){
	channel_frame.init();
});
//每个渠道的稿件主体js，控制开始的初始化，以及最后提交时收集数据
var channel_frame = {
	type : 0,
	content : "", //文章正文

	init : function() {
		channel_frame.setContent();
		channel_frame.setPics();
		channel_frame.setVideos();


		$(".picTopic").click(channel_frame.picTopic);

		article_form.init(); //表单初始化，主要是扩展字段
		if (typeof article_widget != "undefined")
			article_widget.init();//挂件初始化，准备几个TAB的点击事件

		if (typeof article_rel != "undefined")
			article_rel.init();

		channel_frame.titleDelListener("div[id^=topicPic]");

	},

	//编辑器加载内容
	setContent : function() {
		if (typeof UE == "undefined") {
			//if (article.canEditStyle != "true") {
			//	$("#btnTitleAdv").hide();
				//$("#SYS_TOPIC").keyup(channel_frame.titleCheck);//不编辑样式时，才显示标题输入字数
                //
				//初始化字体数据
				channel_frame.titleCheck();
				$("#SYS_TOPIC").on('keyup',function () {
					// $("#SYS_TOPIC").val()
					channel_frame.titleCheck()
				})
				// $("#SYS_TOPICDIV").on('keyup',function () {
				// 	$("#SYS_TOPIC").val($("#SYS_TOPICDIV").text())
				// 	channel_frame.titleCheck()
				// })



			//}
			return;
		}

		var useEditor = channel_frame._useEditor();
		var editorName = useEditor ? "editor" : "simpleEditor";

		var editor = UE.getEditor(editorName);
		editor.ready(function() {
			var content = channel_frame.decode(channel_frame.content);
			editor.setContent(content);
			if (useEditor) {
				//if (article.canEditStyle == "true") {
					$("#btnTitleAdv").show();
					$("#btnTitleAdv").click(article_all.editTitle);		//标题编辑样式按钮
				//} else {
				//	$("#btnTitleAdv").hide();
					//$("#SYS_TOPIC").keyup(channel_frame.titleCheck);//不编辑样式时，才显示标题输入字数
				//}
                $("#SYS_TOPICDIV").on('keyup input',function () {
                    $("#SYS_TOPIC").val($("#SYS_TOPICDIV").text())
                    channel_frame.titleCheck()
                });

				$("#SYS_TOPIC").val(channel_frame.decode(channel_frame.topic));
                $("#SYS_TOPICDIV").text(channel_frame.decode(channel_frame.topic));
				$("#SYS_AUTHORS").val(channel_frame.decode(channel_frame.author));
				$("#findSourceInput").val(channel_frame.decode(channel_frame.source));
				$("#a_sourceUrl").val(channel_frame.decode(channel_frame.sourceUrl));
				$("#a_copyright").prop("checked", (article.copyright == "1"));
				channel_frame.titleCheck();
				article_source.init();
			}else {
				//if (article.canEditStyle != "true") {
				//	$("#btnTitleAdv").hide();
					//$("#SYS_TOPIC").keyup(channel_frame.titleCheck);//不编辑样式时，才显示标题输入字数
					$("#SYS_TOPICDIV").on('keyup',function () {
						$("#SYS_TOPIC").val($("#SYS_TOPICDIV").text())
                        channel_frame.titleCheck()
					})
				//}
			}
			//editor.selection.removeAllRanges();
		});
	},
	decode : function(content) {
		content = content.replace(/&amp;/g, "&");
		content = content.replace(/&lt;/g, "<");
		content = content.replace(/&gt;/g, ">");
		//content = content.replace(/&quot;/g, "\"");
		content = content.replace(/&#034;/g, "\"");

		return content;
	},
	//控制标题栏的字数，以及实时统计标题框中的字数
	titleCheck : function() {

        //标题字数统计 去掉样式统计
        var contents = $("#SYS_TOPIC").val();

		//先过滤样式
        var reg=/\<[\s\S]*?\>/g;
        var _text=contents.replace(reg,"");

		//计算特殊字符
		var regs = /[`~!@#$%^&*()_+<>?:"{},.\/;'[\]a-zA-Z_0-9 ]/g;
		var countNum=0;
		otherOne = _text.replace(regs, function () {
				countNum++;
		});

        //去掉前后的空格
        // len = _text.replace(/\s/g,'').length;
        //var length = _text.replace(/\s/g,'').length - countNum + Math.ceil(countNum/2);
        var length = (_text.length) * 2 - countNum;
        var limited = 300;
		$("#lbwordcount").html(length + "/" + limited).css("color","black");
		if (length > limited){
			$("#lbwordcount").html("*最多输入" + limited + "个字").css("color","red");
			$("#SYS_TOPIC").val(_text.replace(/\s/g,'').substr(0, limited)); //超出范围的字截断
		}
	},
	//组图稿：显示组图
	setPics : function() {
		if (article.type != 1 && article.type != 11 || (article.isNew == "true" && jabbarArticle != "true")) return;

		var theURL = "./Pics.do?DocLibID=" + article.docLibID + "&DocIDs=" + article.docID;
		$.ajax({url: theURL, async:true,
			success: function(datas) {
				//pic_group.setPicInfo(datas);
				article_pic.setPics(datas);
			},
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				alert(errorThrown + ':' + textStatus);  // 错误处理
			}
		});
	},
	//视频稿：显示视频
	setVideos : function() {
		if ((article.type != 2) || article.isNew == "true") return;

		var theURL = "./Videos.do?DocLibID=" + article.docLibID + "&DocIDs=" + article.docID;
		$.ajax({url: theURL, async:true,
			success: function(datas) {
				video_form.setVideoInfo(datas);
			},
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				alert(errorThrown + ':' + textStatus);  // 错误处理
			}
		});
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
	smallCR: null,
	midCR: null,
	bigCR: null,
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

                channel_frame.isSelectImg();
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
        channel_frame.isSelectImg();


		channel_frame.titleDialog.close();
	},
	//是否有标记图片
	isSelectImg: function () {
        if($("#tab1 img").length != 0){
            for(var i = 0; i<$(".tabs").find('li').length; i++){
                if( $(".tabs li").eq(i).attr('tab') != 1 ){
                    continue;
                }
                $(".tabs li").eq(i).addClass('selectImg')
            }
        }else{
            $(".tabs li").removeClass('selectImg')
        }
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
	//读文章字数
	getWordCount : function() {
		if (typeof UE == "undefined") return 0;

		var editorName = channel_frame._useEditor() ? "editor" : "simpleEditor";
		var editor = UE.getEditor(editorName);
		return (editor.getWordcount ? editor.getWordcount() : 0);
	},
	getContentTxt : function() {
		if (typeof UE == "undefined") return "";

		var editorName = channel_frame._useEditor() ? "editor" : "simpleEditor";
		var editor = UE.getEditor(editorName);
		return editor.getContentTxt();
	},
	//验证
	isValid : function() {
		var ret = article_form.isValid();
		if (!ret) return ret;

		if (article.type == 1 || article.type == 11) {
			var data = channel_frame._getPicData();
			if (!data || data.length == 0) {
				alert("请上传图片");
				return false;
			}
		}
		if (article.type == 2) {
			var data = channel_frame._getVideoData();
			if (!data || data.length == 0) {
				alert("请选择视频");
				return false;
			}
		}
		return ret;
	},

	//组织要提交的数据
	getData : function() {
		var channelData = {};

		//挂件
		channelData["widgets"] = channel_frame._getWidgets();

		//正文附件：正文图片、正文视频、正文文档附件
		channelData["pics"] = channel_frame._getPicData();
		channelData["videos"] = channel_frame._getVideoData();
		channelData["files"] = channel_frame._getFileData();
		//相关稿件
		channelData["rels"] = channel_frame._getRelData();

		//表单属性，包括正文
		channelData["form"] = channel_frame._getFormData();//稿签
		
		channelData["jabbarArticle"] = jabbarArticle;

		return channelData;
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
		} else if (article.type == 1 || article.type == 11) {
			//组图稿中的图片
			//return pic_group.getPicInfo();
			return article_pic.getPics();
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
		} else if (article.type == 2) {
			//视频稿
			result = video_form.getVideoInfo();
		}
		return result;
	},
	_getFileData : function() {
		var result = null;
		if (channel_frame._useEditor()) {
			//文章稿中的视频，从编辑器中获得
			var editor = UE.getEditor("editor");
			
			result = editor.getContentFiles();
		} 
		return result;
	},
	//判断是否使用大编辑器（稿件、活动）
	_useEditor : function() {
		return (article.type == 0 || article.type == 7);
	},

	/**
	 * 返回格式：{pic:<id>,picLib:<id>,video:<id>,videoLib:<id>,attachments:[{path:, content:}…]}
	 */
	_getWidgets : function() {
		if (typeof article_widget != "undefined")
			return article_widget.widgetData();
		else
			return null;
	},

	/**
	 * 返回格式：[{id:<id>,lib:<id>}, ...]
	 */
	_getRelData	: function() {
		if (typeof article_rel != "undefined")
			return article_rel.relData();
		else
			return null;
	},

	//读表单项
	_getFormData : function() {
		var formData = article_form.formData();
		formData["a_content"] = channel_frame.getContent();//加上正文内容
		formData["a_wordCount"] = channel_frame.getWordCount();//加上字数

		formData["editorValue"] = "";//自动把编辑器的内容也读出来了，去掉它

		formData["a_picSmall"] = channel_frame._imgSrc($("#picSmall").attr("src"));
		formData["a_picMiddle"] = channel_frame._imgSrc($("#picMiddle").attr("src"));
		formData["a_picBig"] = channel_frame._imgSrc($("#picBig").attr("src"));

		return formData;
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
	},
	uploadFiles: function(){
		var ready = article_form.uploadFileBeforeSubmit();
		if (!ready){
			return false;
		}
		return true;
	}
}
