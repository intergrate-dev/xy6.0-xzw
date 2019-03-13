$(function(){
	xy_live.init();
});

xy_live = {
	init : function() {
		$("#btnSave").click(xy_live.doSave);
		$("#btnCancel").click(xy_live.doCancel);
		
		//新帖（话题或继续报道），需要自动设置直播员
		if (article.isNew) {
			$("#a_siteID").val(article.siteID);
			$("#SYS_AUTHORS").val(article.author);
		}
		//不是主贴，不显示标题、位置、是否关闭评论
		if ($("#parentID").val() > 0) {
			$("#SPAN_SYS_TOPIC").parent().parent().hide();
			$("#SPAN_a_location").parent().parent().hide();
			$("#SPAN_a_discussClosed").parent().parent().hide();
			
			if (article.isNew) $("#SYS_TOPIC").val(article.topic);
		}
		//附件
		var atts = $("#a_attachments").val();
		if (atts)
			atts = eval("(" + atts + ")");
		else
			atts = {pics:[], videos:[]};
		//atts.pics:图片列表，atts.videos：视频列表
		xy_live.initPics(atts.pics);
		xy_live.initVideos(atts.videos);

        if(article.isActivity){
            var _html = $("#a_otherInfo").val();
            UE.setOuterContent(_html, "activityEditor");

			/*var startTime = $("#a_startTime").attr("class");
			startTime = startTime.substr(0, startTime.lastIndexOf("]") );
			startTime += ",dateRange[#a_endTime]]";
			alert(startTime);
			$("#a_startTime").attr("class", startTime);*/


        }

	},
	//修改操作时，先把已有图片显示出来
	initPics : function(pics) {
		var datas = [];
		for (var i = 0; i < pics.length; i++) {
			var data = {
				"isIndex":false,
				"path":pics[i],
				"pic":"",
				"content":""
			};
			datas.push(data);
		}
		pic_group.setPicInfo(datas);
	},
	//修改操作时，先把已有视频显示出来
	initVideos : function(videos) {
		var datas  = [];
		//视频只有Url地址，模拟成视频显示时需要的对象
		for (var i = 0; i < videos.length; i++) {
			var v = {};
			v.url = videos[i];
			v.urlApp = videos[i];
			
			datas.push(v);
		}
		video_form.setVideoInfo(datas);
	},
	
	//保存提交

	doSave : function(){
        if (!$("#form").validationEngine("validate")){
            // 验证提示
            $("#form").validationEngine("updatePromptsPosition");
            return false;
        }
		var picList = xy_live.getPics();
		var attachments = {
			pics : picList,
			videos : xy_live.getVideos()
		};
		
        var _json = JSON.stringify(attachments);
        $("#a_attachments").val( _json);

        if(article.isActivity){

			if(!picList || picList == "" || picList.length ==0){
				alert("请上传一张图片！");
				return;
			}
			var sTime = $("#a_startTime").val();
			var eTime= $("#a_endTime").val();
			if(sTime > eTime){
				alert("开始时间需要小于截止时间！");
				return;
			}

            var editor = UE.getEditor("activityEditor");
            var _html = editor.getContent();
            $("#a_otherInfo").val(_html);
            $("#a_otherInfo").html(_html);
        }

        $("#form").submit();
    },
	//退出按钮
	doCancel : function() {
		window.onbeforeunload = null;
		var dataUrl = "../../e5workspace/after.do?UUID=" + $("#form #UUID").val();
		window.location.href = dataUrl;
	},
	//保存前组织图片数据
	getPics : function() {
		var picList = new Array();
        $("#ul1").find("img:not(.imgsize1)").each(function(){
            picList.push(decodeURI(xy_live.getImgSrc(this.src)));
        });
		return picList;
	},
    getImgSrc: function(_path){
        if (_path) {
            _path = _path.substr( _path.lastIndexOf('image.do?path=')).replace("image.do?path=","");
			//可能显示的是.0小图
			if (_path.substring(_path.length - 2, _path.length) == ".0")
				_path = _path.substring(0, _path.length - 2);
			else if (_path.substring(_path.length - 6, _path.length) == ".0.jpg")
				_path = _path.substring(0, _path.length - 6);
		}
		
        return _path || "";
    },
	//保存前组织视频数据
	getVideos : function() {
		var vidoes = new Array();
		
		var vs = video_form.getVideoInfo();
		if (vs && vs.length > 0) {
			for (var i = 0; i < vs.length; i++) {
				vidoes.push(vs[i].urlApp);
			}
		}
		return vidoes;
	}

};
//填充单层分类下拉框的Option。读话题分类时加siteID
e5_form.dynamicReader._readCatUrl = function(catType) {
	var dataUrl = "e5workspace/manoeuvre/CatFinder.do?action=single&catType=" + catType;
	if (catType == "5") {
		//改变读数据的url
		dataUrl = "xy/Cats.do?catType=" + catType + "&siteID=" + e5_form.getParam("siteID");
	}
	dataUrl = e5_form.dealUrl(dataUrl);
	return dataUrl;
};
