//写稿中一个渠道的挂件部分的逻辑
var article_widget = {
	/**
	 * 挂件区域初始化
	 * 本方法由外层init时调用
	 */
	pic : "",
	picLib : "",
	video : "",
	videoLib : "",
	vote : "",
	voteLib : "",
	artDialog : null,
	diatype : 0,//打开的对话框类型，0为组图，1为视频
	attas : [],//初始化时存放附件的数组
	FieldCount : 0,//附件数量计数器
	init : function() {
		if(article.isNew == "false"){
			article_widget.initwidgetData();
		}
		//分别加载挂件的初始化数据，包括组图、视频、附件
		
		var inited = false;
		$("#spanWidgetControl").click(function(){
			if(!$("#widgetDiv").is(':visible')){
                $("#widgetDiv").attr("expanded", "true").show();
                $(".icon-chevron-down").hide();
                $(".icon-chevron-up").show();
				/*$(".icon-chevron-up").click(function(e){
				 e.stopPropagation()
				 $("#widgetDiv").hide();
				 $(this).parent().css("border-bottom","1px solid #ddd")
				 $(this).hide();
				 $(".icon-chevron-down").show();
				 });*/
                $(this).css("border-bottom","0")
                //如果是第一次，不移动
                //if(inited){
                //article_widget.moveTo($("#spanWidgetControl"));
                //}
                if(!inited){
                    inited=true;
                    article_widget.loadPiclist(article_widget.picLib,article_widget.pic);
                    article_widget.loadVideolist(article_widget.videoLib,article_widget.video);
                    article_widget.loadVote(article_widget.voteLib,article_widget.vote);
                    article_widget.loadAttaInputs();
                    //组图稿件选择对话框
                    $("#widgetPicBtn").click(article_widget._picSelect);
                    //视频稿件选择对话框
                    $("#widgetVideoBtn").click(article_widget._videoSelect);
                    //投票选择对话框
                    $("#widgetVoteBtn").click(article_widget._voteSelect);

                    //打开挂件时，自动移动到挂件的位置

                    //图片挂件的触发事件
                    $("#widgetPicShowDiv a").click(function(){
                        //隐藏
                        $("#widgetPicDiv").show();
                        $("#widgetPicShowDiv").hide();

                        //修改参数
                        article_widget.pic = "0";
                        article_widget.picLib = "0";

                        $("#wp_img").attr("src",'');
                        $("#wp_Li_topic").html('');
                        $("#wp_Li_author").html('');
                        $("#wp_Li_createDate").html('');

                    });

                    //投票挂件的触发事件
                    $("#widgetVoteShowDiv a").click(function(){
                        //隐藏
                        $("#widgetVoteDiv").show();
                        $("#widgetVoteShowDiv").hide();

                        //修改参数
                        article_widget.vote = "0";
                        article_widget.voteLib = "0";

                        $("#wv_Li_topic").html('');
                        $("#wv_Li_author").html('');
                        $("#wv_Li_createDate").html('');

                    });

                    //视频挂件的触发事件
                    $("#widgetVideoShowDiv a").click(function(){
                        //隐藏
                        $("#widgetVideoDiv").show();
                        $("#widgetVideoShowDiv").hide();

                        //修改参数
                        article_widget.video = "0";
                        article_widget.videoLib = "0";

                        $("#wvd_Li_topic").html('');
                        $("#wvd_Li_author").html('');
                        $("#wvd_Li_createDate").html('');

                    });
                }
			}else{
                $("#widgetDiv").hide();
                $("#widgetDiv").parent().css("border-bottom","1px solid #ddd");
                $(".icon-chevron-down").show();
                $(".icon-chevron-up").hide();
			}

		});

		//选择附件的对话框
		$("#file_upload").uploadify({  
			'buttonText' : '+',  
			'height' : 30, 
			'swf' : '../../e5script/jquery/uploadify/uploadify.swf',  
			'uploader' : '../../xy/article/uploadFile.do;jsessionid='+article.sid,  
			'width' : 360,  
			'auto':true,
			'multi':false,
			"removeTimeout": 0,
			'fileObjName' : 'file',
			//限制文件大小，默认单位KB，若要限制100KB，属性值为'100KB'
            //'fileSizeLimit' : 2048,
            'onUploadStart': function (file) {
				var myself = this; 
				var fileName = file.name;
				var fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1,
						fileName.length);
				//不允许上传的文件类型填到数组中即可
                /*var unableFiles=["jsp","js","mov","MOV","mp4","MP4","mpeg","MPEG","mpg","MPG","mp3","MP3","avi","AVI","wmv","WMV","AI","ai"];
                for(var i in unableFiles){
                    if (fileExtension == unableFiles[i]){
                        alert("系统不允许上传"+unableFiles[i]+"文件");
                        myself.cancelUpload(file.id);
                        $('#' + file.id).remove();
                        break;
                    }
                }*/

				// 如果是js 或者是jsp不能提交fileExtension == "js" ||
				if (fileExtension == "jsp" || fileExtension == "js"){
					alert("系统不允许上传jsp、js文件");
					myself.cancelUpload(file.id);
					$('#' + file.id).remove();
				}
			},
			'onUploadSuccess' : function(file, data, response) {  
				var add = {};
				add.id = article_widget.FieldCount;
				add.path = data;
				add.content = file.name;
				
				article_widget.attas.push(add);
				article_widget.insertInput(article_widget.FieldCount,file.name,data);
				//用过后计数器加1
				article_widget.FieldCount++;
			}
		});

		/*if(article.isNew=="false"){

		}*/

		// $("#spanWidgetControl").click();
		
	},
	/**
	 * 组织出挂件数据，供外层调用
	 * 返回格式：{pic:<id>,picLib:<id>,video:<id>,videoLib:<id>,attachments:[{path:, content:}…]}
	 * 没展开时，返回null
	 */
	widgetData : function() {
		if ($("#widgetDiv").attr("expanded") == "true"){
			var result = {};
			result.pic = article_widget.pic;
			result.picLib = article_widget.picLib;
			result.video = article_widget.video;
			result.videoLib = article_widget.videoLib;
			result.vote = article_widget.vote;
			result.voteLib = article_widget.voteLib;
			result.attachments = [];	
			
			var attas = article_widget.attas;
			for (var i = 0; i < attas.length; i++) {
				var text = $("#content_" + attas[i].id).val();
				attas[i].content = text;
			}
			result.attachments = attas;
			
			return result;
		} else {
			return null;
		}
	},
	
	/**
	 * 初始化挂件数据
	 * 初始化数据格式：{pic:<id>,picLib:<id>,video:<id>,videoLib:<id>,attachments:[{path:, content:}…]}
	 */
	initwidgetData : function() {
		$.ajax({
			url: "../../xy/article/getWidgetInfo.do",
			dataType: "json",
			async: false,
			data : {
				"DocIDs" : article.docID,
				"DocLibID" : article.docLibID
			},
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				alert("读挂件失败。" + errorThrown + ':' + textStatus);  // 错误处理
			},
			success: function (data) {
				article_widget.pic = data[0].pic;
				article_widget.picLib = data[1].picLib;
				article_widget.video = data[2].video;
				article_widget.videoLib = data[3].videoLib;
				article_widget.vote = data[4].vote;
				article_widget.voteLib = data[5].voteLib;
				
				var atts = data[6].attachments;
				for (var i = 0; i < atts.length; i++) {
					atts[i].id = article_widget.FieldCount;
					article_widget.FieldCount++; //用过后计数器加1
				}
				article_widget.attas = atts;
			}
		});
	},
	//初始化添加挂件中的附件内容，初始化内容格式{path:, content:}…
	loadAttaInputs : function (){
		var attas = article_widget.attas;
		for (var i = 0; i < attas.length; i++) {
			if (!attas[i].path) continue;
			
			article_widget.insertInput(attas[i].id,attas[i].content,attas[i].path);
		}
	},
	//视频稿件列表供选择
	_videoSelect : function (){
		var dataUrl = "../../xy/MainArticle.do?siteID=" + article.siteID + "&colID="
		+ article.colID + "&type=" + 3 + "&ch=" + article.ch;
		//先关闭以往的dialog
		!article_widget.artDialog || article_widget.artDialog.close();
		article_widget.artDialog = e5.dialog({
			type : "iframe",
			value : dataUrl
		}, {
			title : "视频稿",
			width : "800px",
			height : "450px",
			resizable : false,
			fixed : true
		});
		article_widget.artDialog.show();
		article_widget.diatype = 1;
	},
	//组图稿件列表供选择
	_picSelect : function (){
		var dataUrl = "../../xy/MainArticle.do?siteID=" + article.siteID + "&colID="
		+ article.colID + "&type=" + 2 + "&ch=" + article.ch;

		//先关闭以往的dialog
		!article_widget.artDialog || article_widget.artDialog.close();

		article_widget.artDialog = e5.dialog({
			type : "iframe",
			value : dataUrl
		}, {
			title : "组图稿",
			width : "800px",
			height : "450px",
			resizable : false,
			fixed:true
		});

		article_widget.artDialog.show();
		article_widget.diatype = 0;
	},

	_voteSelect : function (){
		var dataUrl = "../../xy/VoteCheck.do?siteID=" + article.siteID;

		article_widget.artDialog = e5.dialog({
			type : "iframe",
			value : dataUrl
		}, {
			title : "投票",
			width : "800px",
			height : "450px",
			resizable : false,
			fixed:true
		});
		article_widget.artDialog.show();
		article_widget.diatype = 2;
	},
	//加载组图稿件中的图片
	loadPiclist : function (docLibID,docIDs){
		if( docLibID != "" && docIDs != ""){
			$.ajax({
				url: "../../xy/article/findAttachment.do",
				dataType: "json",
				async: false,
				data : {
					"DocIDs" : docIDs,
					"DocLibID" : docLibID,
					"type" : 0
				},
				error: function (XMLHttpRequest, textStatus, errorThrown) {
					alert("加载组图稿失败。" + errorThrown + ':' + textStatus);  // 错误处理
				},
				success: function (data) {
					$("#wp_img").attr("src",'../../xy/image.do?path='+data.imgPath);
					$("#wp_Li_topic").html(data.topic);
					$("#wp_Li_author").html("编辑："+data.author);
					$("#wp_Li_createDate").html("时间："+data.createDate);
					$("#widgetPicDiv").hide();
					$("#widgetPicShowDiv").show();
				}
			});
		}
	},
	
	//加载视频稿件中的图片
	loadVideolist : function (docLibID,docIDs){
		if( docLibID != "" && docIDs != ""){
			$.ajax({
				url: "../../xy/article/findAttachment.do",
				dataType: "json",
				async: false,
				data : {
					"DocIDs" : docIDs,
					"DocLibID" : docLibID,
					"type" : 1
				},
				error: function (XMLHttpRequest, textStatus, errorThrown) {
					alert("加载视频稿的图片失败。" + errorThrown + ':' + textStatus);  // 错误处理
				},
				success: function (data) {
					$("#wvd_img").attr("src",'../../xy/image.do?path='+data.imgPath);
					$("#wvd_Li_topic").html(data.topic);
					$("#wvd_Li_author").html("编辑："+data.author);
					$("#wvd_Li_createDate").html("时间："+data.createDate);
					$("#widgetVideoDiv").hide();
					$("#widgetVideoShowDiv").show();
				}
			});
		}

	},
	
	//加载投票信息
	loadVote : function (docLibID,docIDs){
		if( docLibID != "" && docIDs != ""){
			$.ajax({
				url: "../../xy/article/Votes.do",
				dataType: "json",
				async: false,
				data : {
					"DocIDs" : docIDs,
					"DocLibID" : docLibID
				},
				error: function (XMLHttpRequest, textStatus, errorThrown) {
					alert("加载投票信息失败。" + errorThrown + ':' + textStatus);  // 错误处理
				},
				success: function(data, status) {
					//console.info(data);
					$("#wv_Li_topic").html("【"+data.type+"】"+data.topic);
					$("#wv_Li_author").html("编辑："+data.author);
					$("#wv_Li_createDate").html("时间："+data.createDate);
					$("#widgetVoteDiv").hide();
					$("#widgetVoteShowDiv").show();
				}
			});
		}
	},
	
	//附件上传成功后，动态增加输入框信息
	insertInput : function(id,name,path){
	   //add input box
	   $("#InputsWrapper").append('<li><div id="'+id+'" style="  display: inline-block;" ><input type="text" style="margin-bottom:10px;width:360px;heigth:30px" name="content[]" id="content_'+ id +'" value="'+ name +'"/><a href="javascript:void(0);" style="display: block;margin-top: 7px;  margin-left: 10px;" class="removeClass close">×</a></div></li></li>');
		//绑定点击事件
		$("div[id="+id+"]").find("a").click(article_widget.removeAttachment);

		return false;
	},
	removeAttachment : function(){
		var divid = $(this).parent('div').attr('id');
		var attas = article_widget.attas;
		for (var i = 0; i < attas.length; i++) {
			if (attas[i].id == divid){
				article_widget.attas.splice(i, 1);
				break;
			}
		}
		
		$(this).parent('div').remove(); //remove text box
		return false;
	},
	moveTo: function($obj){
		$('html,body').animate({scrollTop: $obj.offset().top}, 800);
	}
}
//稿件列表“取消”按钮事件的响应
function articleCancel(){
	article_widget.artDialog.close();
}

//处理添加稿件对话框的关闭事件
function articleClose(docLibID,docIDs){
	//关闭稿件选择对话框后，处理选择的稿件的ID，加载他们的组图
	if(article_widget.diatype == 0){
		article_widget.loadPiclist(docLibID,docIDs);
		article_widget.pic = docIDs;
		article_widget.picLib = docLibID;
	} else if(article_widget.diatype == 1){
		article_widget.loadVideolist(docLibID,docIDs);
		article_widget.video = docIDs;
		article_widget.videoLib = docLibID;
	} else if(article_widget.diatype == 2){
		article_widget.loadVote(docLibID,docIDs);
		article_widget.vote = docIDs;
		article_widget.voteLib = docLibID;
	}
	
	article_widget.artDialog.close();
	//重新绑定事件必须放在关闭选择对话框后
	if(article_widget.diatype == 0){
		$("#widgetPicBtn").click(article_widget._picSelect);
	}else if(article_widget.diatype == 1){
		$("#widgetVideoBtn").click(article_widget._videoSelect);
	}else if(article_widget.diatype == 1){
		$("#widgetVoteBtn").click(article_widget._voteSelect);
	}
}
