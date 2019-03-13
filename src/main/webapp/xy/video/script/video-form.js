var video_form = {
	dialog:{},
	init : function(){
		video_form._initTree();
		$("#btnSubmit").click(video_form._Submit);
		$("#btnCancel").click(video_form._Close);
		$("#btnSource").click(video_form._SourceSelect);
		$("#topicPicSmallDiv").click(channel_frame.picTopic);
		$("#topicPicMidDiv").click(channel_frame.picTopic);
		$("#topicPicBigDiv").click(channel_frame.picTopic);
		
		column_form.init();
		word_count.init();
	},
	_initTree : function(){
		col_tree.siteID = article.siteID;
		col_tree.ch = article.ch;
		col_tree.rootUrl = "../../xy/column/Tree.do?parentID=0";
		col_tree.check.chkStyle = "checkbox";
		col_tree.rootPath= "../column/";
		col_tree.check.enable = true;
		col_tree.init();
	},
	_GetVideoInfo : function(){
		var video = [];
		$.ajax({type: "POST", url: "./getVideoUrlInfo.do", async:false,
			data: {DocID:article.videoID,DocLibID:article.videoLibID},
			success: function(data) {
				if(data && data.success){
					video = [{
				    	url    : data.webUrl,
				    	urlApp : data.appUrl,
				    	videoID: article.videoLibID+","+article.videoID
				    }];
				}
			},
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				alert(errorThrown + ':' + textStatus);
			}
		});
		return video;
	},
	_getFormData: function(){
		var form = {};

		try {
			var form0 = $("#form select, #form input[type='hidden'], #form input:text, #form textarea").serializeArray();
		}catch(e){
			alert(e.message);
		}
        for(var i = 0; i < form0.length; i++){
            form[form0[i].name] = form0[i].value;
        }

        var form0 = $("#form input[type='checkbox']");
        for(var i = 0; i < form0.length; i++){
           //如果这个checkbox被选中了，在json中的这个对象后面添加
            var value = form[form0[i].name];
			if (!value) value = "";
			
            if (form0[i].checked){
                value = (value) ? value + "," + form0[i].value : form0[i].value;
            }
            form[form0[i].name] = value;
        }
		
		form["a_picSmall"] = channel_frame._imgSrc($("#picSmall").attr("src"));
		form["a_picMiddle"] = channel_frame._imgSrc($("#picMiddle").attr("src"));
		form["a_picBig"] = channel_frame._imgSrc($("#picBig").attr("src"));
		
		form["DocLibID"] = article.ch==0?article.webDocLibID:article.appDocLibID;
		
		return form;
	},
	_CollectData : function() {
		var channelData = {};
		channelData["widgets"] = null;
		channelData["pics"] = null;
		channelData["rels"] = null;
		channelData["files"] = null;
		channelData["jabbarArticle"] = "";
		
		var videos = video_form._GetVideoInfo();
		//channelData["videos"] = videos;
		channelData["form"] = video_form._getFormData();//稿签
		channelData["form"]["a_multimediaLink"] = videos[0].url;
		
		return channelData;
	},
	
	_Submit : function(){
		var columns = col_tree.getChecks();
		if (!columns || !columns[0]) {
			alert("请选择栏目");
			return;
		}
		var param = video_form._CollectData();
		if (!param) {
			return;
		}
		param["toPublish"] = 1;
		
		console.log(columns);
		console.log(param);
		var colIDs = columns[0].split(",");
		var colNames = columns[1].split(",");
		for(var i=0;i<colIDs.length;i++){
			if(i>0){
				param['form']['DocID']=video_form._getNextID();
			}
			param['form']['a_column']=colNames[i];
			param['form']['a_columnID']=colIDs[i];
			param['form']['currentColID']=colIDs[i];
			if (!$("#form").validationEngine("validate")){
				$("#form").validationEngine("updatePromptsPosition");
	            return false;
			}
			video_form._post(param);
		}
		video_form._Close();
	},
	_Close : function(){
		var url = "../../e5workspace/after.do?UUID=" + article.UUID;
		window.location.href = url;
	},
	_post : function(param) {
		//Post方式下参数是字符串形式
		var paramString = JSON.stringify(param);
		var paramData = {"param":paramString};
		//alert(paramString);
		$.ajax({type: "POST", url: "../article/ArticleSubmit.do", async:false,data: paramData,
			success: function(data){console.log('ok!');},
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				alert(errorThrown + ':' + textStatus);  // 错误处理
			}
		});
	},
	_getNextID : function(){
		var docID = 0;
		$.ajax({type: "POST", url: "./getNextDocID.do", async:false,dataType:"json",
			success: function(data) {docID = data.docID;}});
		return docID;
	},
	//打开来源选择
	_SourceSelect : function() {
		var url = "../../xy/GroupSelect.do?type=4&siteID=" + article.siteID;
		var pos = {left : "30px",top : "30px",width : "750px",height : "500px"};
		video_form.dialog = e5.dialog({type : "iframe", value : url}, {
			showTitle : true,
			title: "选择来源",
			width : "1000px",
			height : "500px",
			pos : pos,
			resizable : false
		});
		video_form.dialog.show();
	},
	sourceSelectOK: function(docLibID, docID) {
		video_form.dialog.close();
		$.get("../source/findSource.do", {docLibID:docLibID, docID:docID}, function(data){
			$("#findSourceInput").val(data.name);
			$("#a_source").val(data.name);
			$("#a_sourceID").val(data.id);
			$("#a_sourceUrl").val(data.url);
		});
	}
}
function groupSelectOK(docLibID,docID){
	video_form.sourceSelectOK(docLibID,docID);
}
function groupSelectCancel(){
	video_form.dialog.close();
}

/**
 * 模板指定
 * 1. 点击切换时，显示select和确定按钮
 * 2. 点击确定时，显示模版名称和切换按钮，给相应的属性赋值
 */
var column_form = {
    dc: "pc",
    articletype: 0,
    switched: false,
    init: function(){
        $("#btnChgTpt_pc").click(column_form.switchButtonListen);
        $("#btnChgTpt_app").click(column_form.switchButtonListen);
        $("#btnChgTpt_pc_clear").click(column_form.clearTemplate);
        $("#btnChgTpt_app_clear").click(column_form.clearTemplate);
        
        $("body").animate({scrollTop:0}, 5);
        $("html").animate({scrollTop:0}, 5);
    },
    
    winTmp : {},
    switchButtonListen: function(){
        var _dc = $(this).attr("data-dc");
        //隐藏模版名称
        $("#tptNameSpan_" + _dc).hide();
        var _type= 1;
        var _ckeckedID;
        var _channel;
		if(_dc=='pc'){
			_ckeckedID = document.getElementById("a_templateID").value;
			_channel = 0;
		}else{
			_ckeckedID = document.getElementById("a_templatePadID").value;
			_channel = 1;
		}

		if("undefined" != typeof t_type){ 
			if(undefined != t_type){
				_type=t_type;
			}
		}
		var _siteID = document.getElementById("a_siteID").value;
		var dataUrl = "../template/TemplateSelect.jsp?type=" + _type
				+ "&channel=" + _channel
				+ "&siteID=" + _siteID
				+ "&ckeckedID=" + _ckeckedID;
		// 顶点位置
		// var pos = {left : "100px",top : "500px",width : "1000px",height : "500px"};
		column_form.winTmp = e5.dialog({
			type : "iframe",
			value : dataUrl
		}, {
			showTitle : false,
			width : "350px",
			height : "250px",
			resizable : false
		});
		column_form.winTmp.show();
	
        $("#templateSelect_" + _dc).show();
        
    },
    
    closeTemplate : function(channel, type, name, docID) {
		//赋值
        if(channel==0){
        	$("#templateSelect_pc").val(name);
            $("#a_template").val(name);
            $("#a_templateID").val(docID);
        }else{
        	$("#templateSelect_app").val(name);
            $("#a_templatePad").val(name);
            $("#a_templatePadID").val(docID);
        }
		column_form.winTmp.hide();
	},
	
	cancelTemplate : function(type) {	
		column_form.winTmp.hide();	
	},
	clearTemplate : function(){
		var _dc = $(this).attr("data-dc");
		if(_dc == "pc"){
			$("#templateSelect_pc").val("");
			$("#a_template").val("");
			$("#a_templateID").val("");
		}else{
			$("#templateSelect_app").val("");
            $("#a_templatePad").val("");
            $("#a_templatePadID").val("");
		}
	}
};

/**
 * 字数统计
 * 包括链接标题，短标题
 */
var word_count = {
	init : function(){
		$("#a_linkTitle").bind('input propertychange', word_count.linkTitle_wordCount);
		$("#a_shortTitle").bind('input propertychange', word_count.shortTitle_wordCount);
	},
	linkTitle_wordCount: function(){
		var len = $("#a_linkTitle").val().length;
		if(len > 1024){
			word_count.cutTitle($("#a_linkTitle"));
			$("#a_ltwc_span").text(1024);
		}else{
			$("#a_ltwc_span").text(len);
		}
	},
	shortTitle_wordCount: function(){
		var len = $("#a_shortTitle").val().length;
		if(len > 1024){
			word_count.cutTitle($("#a_shortTitle"));
			$("#a_stwc_span").text(1024);
		}else{
			$("#a_stwc_span").text(len);
		}
	},
	cutTitle : function(obj){
	    var char = $(obj).val();
	    char = char.substr(0, 1024);
	    $(obj).val(char);
	}
};

var channel_frame = {
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
		var pos = {left : "10px",top : "10px",width : "880px",height : "500px"};

		//如果之前没有生成dialog
		channel_frame.titleDialog = e5.dialog({
			type : "iframe",
			value : '../../xy/ueditor/initTitleDialog.do?imagePath=' + _path
			+"&radio="+ _radio
			+ "&itype=" + _iType + "&siteID=" + article.siteID
		}, {
			showTitle : true,
			title: "设置标题图",
			width : "1170px",
			height : "610px",
			pos : pos,
			resizable : false
		});
		
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
		var haseUseOrignalImgOnly=sessionStorage.getItem("useOrignalImgOnly");
		if(haseUseOrignalImgOnly=='false'){
			if(!channel_frame.smallC && !channel_frame.midC && !channel_frame.bigC){
				alert("请选择裁剪区域！1");
				return;
			}
		}

        var imgObj={};
        imgObj={
            "imagePath" : channel_frame.titleImagePath,
            "docLibID" : (article.ch=="0"?article.webDocLibID:article.appDocLibID),
            "docID" : article.DocID,
            "widthRadio" : channel_frame.widthRadio,
            "heightRadio" : channel_frame.heightRadio,
            "smallCR" : channel_frame.smallCR,
            "midCR" : channel_frame.midCR,
            "bigCR" : channel_frame.bigCR,
            "imageName" : channel_frame.fileName,
            "isLocalFile" : channel_frame.isLocalFile
        };

        if(sessionStorage.getItem("useOrignalImgOnlySmall")=="false"){
            imgObj.smallCoords=JSON.stringify(channel_frame.smallC);
		}
		if(sessionStorage.getItem("useOrignalImgOnlyMid")=="false"){
            imgObj.midCoords= JSON.stringify(channel_frame.midC);
		}
		if(sessionStorage.getItem("useOrignalImgOnlyBig")=="false"){
            imgObj.bigCoords=JSON.stringify(channel_frame.bigC);
		}

        console.log(imgObj);

    	$.ajax({
			url : "../../xy/ueditor/createtitleImg.do",
			type : 'POST',
			async : true,
			data : imgObj,
			dataType : 'json',
			success : function(_msg, status) {
				channel_frame.handleTitleImgHtml(_msg);
			},
			error : function(xhr, textStatus, errorThrown) {
			}
		});

		channel_frame.titleDialog.close();
		//是否有单独的使用原图的功能
		sessionStorage.setItem("useOrignalImgOnly",false);



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
			if(sessionStorage.getItem("useOrignalImgOnlySmall")=="true"){
				console.log("小图使用原图")
			}else{
				$("#topicPicSmallDiv").html('<img id="picSmall" itype="small" src="../'+ decodeURI(_msg.imgSmall.substr(_msg.imgSmall.lastIndexOf('image.do?path=')))+'"/><span class="icon-remove"></span>')
				channel_frame.titleDelListener("#topicPicSmallDiv");
			}
		}
		if( _msg.imgMid ){
            if(sessionStorage.getItem("useOrignalImgOnlyMid")=="true"){
                console.log("中图使用原图")
            }else{
                $("#topicPicMidDiv").html('<img id="picMiddle" itype="mid" src="../'+decodeURI(_msg.imgMid.substr(_msg.imgMid.lastIndexOf('image.do?path=')))+'"/><span class="icon-remove"></span>')
                channel_frame.titleDelListener("#topicPicMidDiv");
            }

		}
		if( _msg.imgBig ){
            if(sessionStorage.getItem("useOrignalImgOnlyBig")=="true"){
                console.log("大图使用原图")
            }else {
                $("#topicPicBigDiv").html('<img id="picBig" itype="big" src="../'+decodeURI(_msg.imgBig.substr(_msg.imgBig.lastIndexOf('image.do?path=')))+'"/><span class="icon-remove"></span>')
                channel_frame.titleDelListener("#topicPicBigDiv");
            }
		}
        channel_frame.isSelectImg();

		//不关闭
		if(sessionStorage.getItem("useOrignalImgOnly")=="true"){
		}else{
			channel_frame.titleDialog.close();
		}
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
    }
	
};

var online_image = {
    //图片库选择：“确定”按钮
    picClose: function(docLibID, docIDs, imgPath){
        if (!docIDs) return;
        docIDs = docIDs.split(",");

        var list = [];
        for (var i = 0; i < docIDs.length; i++) {
            //根据索引图的DocLibID，DocID获取到这组的所有组图
            $.ajax({
                url: "../../xy/pic/getPics.do",
                dataType: "json",
                async: false,
                data : {
                    "DocID" : docIDs[i],
                    "DocLibID" : docLibID
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    alert(errorThrown + ':' + textStatus);
                },
                success: function (data) {
                    if(data && data instanceof Array){
                        list = list.concat(data);
                    }
                }
            });
        }
        var oldList = article_pic.upload.getDataList();
        var _list =[];
        if(!(oldList && oldList instanceof Array)){
            oldList = [];
        }

        for(var i = 0, li ; li = oldList[i++];){
            _list.push({
                path: li.imagePath,
                content: li.caption
            });
        }

        _list = _list.concat(list);
        article_pic.setPics(_list);
        article_pic.upload.resetWidth();
        if($("#picUploadDiv").find(".btn-piclib").size() == 0){
            $("#picUploadDiv").find(".btn-file").after("<button type='button' class='btn btn-primary btn-lg btn-piclib' style='margin-left: 10px;'>图片库</button>");
        }
        channel_frame["picLibDialog"].hide();
    },

    //图片库选择：“取消”按钮
    picCancel: function(){
        channel_frame["picLibDialog"].hide();
    }
};
