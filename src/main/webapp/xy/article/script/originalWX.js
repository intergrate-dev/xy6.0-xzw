$(function(){
/***************初始化编辑器数据******************/
//debugger;
//	window.flags = false;
	window.UEDITOR_HOME_URL="../plugin/ueditor";
	var ue = UE.getEditor('editor',{
		//initialFrameWidth :800,//设置编辑器宽度
		initialFrameHeight:525,//设置编辑器高度
		scaleEnabled:true
		});
	var appId;
	var tw_message = new Object();

	ue.ready(function(){
		appId = getUrlParam("docID");
		getTwMsgData();
	});

	/********************************添加特殊样式************************************/
	 $('.tab-content .template').on('click',function(){
		 var $obj= $(this).clone();
		 $obj.find('button').remove();
		 var range = ue.selection.getRange();
		 range.select();
		 var txt = ue.selection.getText();
		 if(txt.length>0){
			 $obj.find(".edit-title,.edit-content").text(txt);
		 }
		 ue.execCommand('insertHtml', '<p/></p>'+$obj.html().replace(/((?:\s|&nbsp;)+(<)|(>)(?:\s|&nbsp;)+)/gi,"$2$3")+'<p></p>');
	 });

	/***********************************************************************************/
	//设置主题颜色
	$('.edit-themes').each(function(){
		var color = $(this).css('background-color');
		$(this).click(function(){
			$('.edit-themes-border').each(function(){
				if($(this).css('border-color') && $(this).css('border-color')!='transparent'){

					$(this).css('border-color',color);
				}
				if($(this).css('border-top-color')&& $(this).css('border-top-color')!='transparent'){
					$(this).css('border-top-color',color);
				}
				if($(this).css('border-left-color')&& $(this).css('border-left-color')!='transparent'){
					$(this).css('border-left-color',color);
				}
				if($(this).css('border-right-color')&& $(this).css('border-right-color')!='transparent'){
					$(this).css('border-right-color',color);
				}
				if($(this).css('border-bottom-color')&& $(this).css('border-bottom-color')!='transparent'){
					$(this).css('border-bottom-color',color);
				}

				if($(this).css('color')){
					//$(this).css('color',color);
				}
			});
			$('.edit-themes-font').each(function(){
				$(this).css('color',color);
			});
			$('.edit-themes-background').each(function(){
				$(this).css('background-color',color);
			});
		});
	});
	$('#material-connect').on('click',function(){
		var content = $('#material-detail-body').html();
		var url = $('#material-detail-body').data("url");
		/*var $temp = $("#card").find(".template:first").clone();
		$temp.find('.edit-content').empty().html(content);
		addTemplate($temp);*/
		ue.execCommand('clearDoc');
		ue.execCommand('insertHtml', content);
		$("#message-link").val(url);

		$('#material-colse').click(); //点选用后，自动关闭素材显示页，返回素材列表
	});

	/********************文章列表操作******************/
	$('#material-detail').hide();
	$('#material-colse').click(function(){
		$('#material-list').show();
		$('#material-detail').hide();
	});

	/*******************上传图片*************************/
	//给所有添加图片按钮绑定事件   打开添加图片弹出层
	$('#message-list').delegate('.message-list-cover .tip,.message-list-sub .tip','click',function(){
		linkliclick();
	});

	//图片编辑事件
	$('#image-container').delegate('.intro-part2 .wx-edit-Left','click',function(){
		var $image = $(this).parent().prev();
		//var editId = $image.data('id');
		$image.find('.wx-text').removeAttr('disabled').focus();
		var $editbtn = $(this).parent();
		$editbtn.find('.wx-edit-Left,.wx-del-Right').hide();
		$editbtn.find('.wx-confirm-btn,.wx-cancel-btn').show();

	});
	//修改图片名
	$('#image-container').delegate('.intro-part2 .wx-confirm-btn','click',function(){
		var $image = $(this).parent().siblings('.intro-part1');
		var $editbtn = $(this).parent();
		var id = $image.data('id');
		var imgName = $image.find('.wx-text').val();
		$.post('updateImageName.do',{'id':id,'imageName':imgName},function(data){
			if(data.status=='success'){
				$editbtn.prev().find('.wx-text').attr('disabled','disabled');
				$editbtn.find('.wx-confirm-btn,.wx-cancel-btn').hide();
				$editbtn.find('.wx-edit-Left,.wx-del-Right').show();
				alert('修改成功！');
			}else{
				alert('修改失败！');
			}
		});
	});
	//取消编辑
	$('#image-container').delegate('.intro-part2 .wx-cancel-btn','click',function(){
		var $editbtn = $(this).parent();
		$editbtn.prev().find('.wx-text').attr('disabled','disabled');
		$editbtn.find('.wx-confirm-btn,.wx-cancel-btn').hide();
		$editbtn.find('.wx-edit-Left,.wx-del-Right').show();
	});

	//图片删除事件
	$('#image-container').delegate('.intro-part2 .wx-del-Right a','click',function(){
		var $del = $(this).parent().parent().prev();
		var id = $del.data('id');
		$.get('deleteImage.do',{'id':id},function(data){
			if(data.status=='success'){
				$del.parent().remove();
			}else{
				alert('删除失败！');
			}
		});
	});

	/*************************图文列表操作*******************/
	//保存图文消息的对象
	function getTwMsgData(){
		if (wxGroup_data.isNew == "true"){
			tw_message.articles = new Array();
			// 新建
            /*var contitle='<div id="editorconTitle"><input type="text" id="cover-title" maxlength="64"  placeholder="请输入标题" aria-describedby="basic-addon1" value="" style="border:1px dashed #ddd;width: 400px;color:#ccc;height: 35px;padding:0 15px;margin-bottom: 10px"><br></div>';
            ue.setContent(contitle+'<p> </p>');*/
           ue.setContent('<p> </p>');
		} else {
			var param = {
				docLibID : wxGroup_data.docLibID,
				docID : wxGroup_data.docID
			}
			$.get('ArticlesWX.do', param ,function(data){
				tw_message.articles = data;
				if(tw_message.articles.picBig != "") {
//					$("#topicPicDivAdd").append("<img id='picSmall' style='width:100%;height:100%;vertical-align: baseline;' itype='small' src='../image.do?path="+tw_message.articles.picBig+"'>");
//					$("#topicPicDivAdd .addPic").css({
//						display: 'none'
//					})
					$("#topicPicDiv").html("<img id='picSmall' itype='small' src='../image.do?path="+tw_message.articles.picBig+"' /><span class='icon-remove'></span>")
					channel_frame.titleDelListener("#topicPicDiv");
					$("#topicPicDivAdd").hide();
					$("#topicPicDiv").show();
				/*}else {
					$("#topicPicDivAdd .addPic").css({
						display: 'inline'
					})*/
				}
				initMessageList();

				//initEditorData(1);
			});
		}
	}

	//给非编辑的消息绑定事件--鼠标进入覆盖mask
	$('#message-list').delegate('li:not(.message-edit,.message-add)','mouseover',function(){
			$(this).find('div:last').show();
	});
	//给非编辑的消息绑定事件--鼠标进入隐藏mask
	$('#message-list').delegate('li:not(.message-edit,.message-add)','mouseout',function(){
			$(this).find('div:last').hide();
	});
	//给mask上的编辑按钮绑定事件
	$('#message-list').delegate('.message-cover-mask span, .message-sub-mask span','click',function(){
		var $li = $(this).parent().parent();
		var index = parseInt($li.attr('id').replace('message-menu-',''));
		//保存数据  验证数据
		if(!addMessage()){
			return false;
		};
		//设置编辑器和指向箭头的位置
		setArrow(index);
		//初始化数据
		//initEditorData();
	});
	//绑定图片编辑/删除事件
	$('#message-list').delegate('.message-cover-toolbar .glyphicon-pencil','click',function(){
		//$('#uploadImageModal').modal('show');
        chosenImg = $(this).parent().parent().find('.message-list-thumbnail img,.message-list-cover img')[0];

        var url = '../ueditor/dialogs/imagecrop/imagecrop.jsp';
        var title = "图片修改";
        var _cssRules = "width:800px;height:590px;";
        var _editor = UE.getEditor("editor");
        //var dialog = _editor.getDialog("picModify");
        var dialog =_editor.ui._dialogs["picModify"];
        if(!dialog){
            dialog = new UE.ui.Dialog({
                //指定弹出层中页面的路径，这里只能支持页面,因为跟addCustomizeDialog.js相同目录，所以无需加路径
                iframeUrl: url,
                //需要指定当前的编辑器实例
                editor:_editor,
                //指定dialog的名字
                name:"picModify",
                //dialog的标题
                title:title,
                //指定dialog的外围样式
                cssRules : _cssRules
            });
            dialog.render();
            _editor.ui._dialogs["picModify"] = dialog;
        }
        dialog.open();
	});
	$('#message-list').delegate('.message-cover-toolbar .glyphicon-trash','click',function(){
		var tip = ' <div class="tip" >'
					   +' <a href="#">'
							+' <span class=" glyphicon glyphicon-plus"></span>'
							+' <br/>缩略图'
					   +' </a>'
				   +' </div>';
		$(this).parent().parent().find('.message-list-thumbnail img,.message-list-cover img').replaceWith(tip);
		$(this).parent().hide();
	});
	

	
	function formatCSTDate(strDate,format){
		return formatDate(new Date(strDate),format);
	}
	
	function formatDate(date,format){
	    var paddNum = function(num){
	    num += "";
	    return num.replace(/^(\d)$/,"0$1");
		};
		//指定格式字符
		var cfg = {
		   yyyy : date.getFullYear() //年 : 4位
		   ,yy : date.getFullYear().toString().substring(2)//年 : 2位
		   ,M  : date.getMonth() + 1  //月 : 如果1位的时候不补0
		   ,MM : paddNum(date.getMonth() + 1) //月 : 如果1位的时候补0
		   ,d  : date.getDate()   //日 : 如果1位的时候不补0
		   ,dd : paddNum(date.getDate())//日 : 如果1位的时候补0
		   ,hh : paddNum(date.getHours())  //时
		   ,mm : paddNum(date.getMinutes()) //分
		   ,ss : paddNum(date.getSeconds()) //秒
		};
	    format || (format = "yyyy-MM-dd hh:mm:ss");
	    return format.replace(/([a-z])(\1)*/ig,function(m){return cfg[m];});
	}
	// 流程记录
    $('#flowInfo').click(function(){//流程记录
    	var wx_groupid = getParam("wx_groupid");
    	if(wxGroup_data.docidForhisAndFlow && wxGroup_data.docLibIdForhisAndFlow) {
    		var theURL = "../../xy/article/CensorshipLog.do?DocIDs=" + wxGroup_data.docidForhisAndFlow
            + "&DocLibID=" + wxGroup_data.docLibIdForhisAndFlow +"&wxGroupId="+wx_groupid+ "&Type=1";
    	}else {
    		var theURL = "../../xy/article/CensorshipLog.do?DocIDs=" + wxGroup_data.docID
    		    + "&DocLibID=" + wxGroup_data.docLibID + "&type=1";
    	}
        $.ajax({url:theURL, async:false, success:function(data){
                drawTable(data.logList);
            }});
    });

    function drawTable(data){
        if(data.length>0){
        	data = data.reverse();
            var logTableHtml = "<table class='table table-striped' border='1'><thead><tr><td>操作人</td>"
                + "<td>操作</td><td>操作时间</td><td>备注(校对意见和审核意见在此显示)</td></tr></thead><tbody>";
            for(var i=0;i<data.length;i++){
                logTableHtml += "<tr><td>" + data[i].operator + "</td>"
                    + "<td>" + data[i].fromPosition + data[i].operation + "</td>"
                    + "<td>" + formatCSTDate(data[i].startTime,"yyyy-MM-dd hh:mm:ss") + "</td>";
                if(data[i].detail==null){
                    logTableHtml += "<td></td></tr>";
                }else{
                    logTableHtml += "<td>" + data[i].detail + "</td></tr>";
                }
            }
            logTableHtml += "</tbody></table>";
            $(".rContent").html(logTableHtml);
        }
    }
    // //历史版本
    // historyShow();
    // // var HistoryInfopage = 1;
    // function historyShow(){
    //     var checkID = groupArticleInfo.checkID;
    //     var checkLibID = groupArticleInfo.checkLibID;
    //
    //     var theURL = "../../xy/article/HistoryInfo.do?DocIDs=" + checkID
    //         + "&DocLibID=" + checkLibID + "&Page=" + HistoryInfopage;
    //     $.ajax({url:theURL, async:false, success:function(data){
    //             drawHistoryTable(data);
    //         }});
    // }
    // //渲染历史版本
    // function drawHistoryTable(data){
    //     if(data.length>0){
    //         var logTableHtml = "<table class='table ' border='1' style='width:510px;background:#fff'><thead><tr><td>审核人</td>"
    //             + "<td>审核操作</td><td>审核时间</td></tr></thead><tbody>";
    //         for(var i=0;i<data.length;i++){
    //             logTableHtml += "<tr><td>" + data[i].operator + "</td>"
    //                 + "<td>" + data[i].operation + "</td>"
    //                 + "<td sytle='width:200px'>" + data[i].created+ "</td>";
    //         }
    //         logTableHtml += "</tbody></table>";
    //         $("#drawHistoryTable").html(logTableHtml);
    //     }
    // }
	//保存子消息到tw_message中
	function addMessage(){
		//var title = $('#cover-title').val();
		//alert($(ueditor_0.contentWindow.document.getElementById('cover-title')).val());
//      var title=$(ueditor_0.contentWindow.document.getElementById('cover-title')).val();
        var title=$(document.getElementById('cover-title')).val();
        var content = ue.getContent();
		/*var contents = ue.getContent();
		console.log(contents)
        var reg=/<p><input type="text" id="cover-title" maxlength="64" placeholder="[\s\S]*?" aria-describedby="basic-addon1" value="[\s\S]*?" style="border:1px dashed #ddd;width: 400px;color:#ccc;height: 35px;padding:0 15px;margin-bottom: 10px"\/><br\/><\/p>/g;
        content= contents.replace(reg,"");
		console.log(content)*/
		var content_source_url = $('#cover-url').val();//原文链接
		var author = $('#cover-author').val();
		var abstracts = $('#abstract_message').val();
		var copyright = 0;//原创声明
		var picBig = $("#picSmall").attr("src").split("path=")[1];
		if($("#copyright").prop('checked')){
			copyright = 1;
		}
		if(title==""){
			alert("标题不能为空！");
			return false;
		}else{
			tw_message.articles={
			   "url":content_source_url,
			   "title":title,
			   "abstracts":abstracts,
			   "content":content,
			   "copyright":copyright,
			   "author":author,
			   "picBig":picBig
			};
			return true;
		}
	}
	//初始化编辑器数据
	function initEditorData(index){
		var $editMessage = $('#message-list').find('.message-edit');
		var data = tw_message.articles[index-1];

		if(typeof data=="undefined"){
			ue.execCommand("cleardoc");
			$('#message-link').val('');
		}else{
			ue.execCommand("cleardoc");
			ue.execCommand('insertHtml', data.content);
			$('#message-link').val(data.content_source_url);
		}
		$('#message-list').find('.message-cover-toolbar').hide();
		if($editMessage.find("img").length>0){
			$editMessage.find('.message-cover-toolbar').show();
		}
	}
	//初始化图文列表
	function initMessageList(){

		$('#cover-title').val(tw_message.articles.title);
		$('#cover-url').val(tw_message.articles.url);
		$('#abstract_message').val(tw_message.articles.abstracts);
		$('#cover-author').val(tw_message.articles.author);
		var copyright = tw_message.articles.copyright;
		if(copyright == 1){
			$("#copyright").prop("checked", true);
		}else{
			$("#copyright").prop("checked", false);
		}
		/*var contitle='<p><input type="text" id="cover-title" maxlength="64"  placeholder="'+tw_message.articles.title+'" aria-describedby="basic-addon1" value="'+tw_message.articles.title+'" style="border:1px dashed #ddd;width: 400px;color:#ccc;height: 35px;padding:0 15px;margin-bottom: 10px"><br></p>';
		ue.setContent(contitle+""+tw_message.articles.content);*/
		ue.setContent(tw_message.articles.content);
		//批注数据回显到右侧面板
		ueditor_commment.initcomment();
      //  ue.setContent(tw_message.articles.content);
        //$(ueditor_0.contentWindow.document.getElementById('cover-title')).css({border:"1px solid #ddd"})
	}
	
	//设置编辑器和指向箭头的位置
	function setArrow(index){
		//设置mask显示隐藏状态
		var $editMenu = $('#message-menu-'+index);
		$('#message-list li').removeClass('message-edit');
		$editMenu.addClass('message-edit');
		$editMenu.find('div:last').hide();

		//计算编辑器和指向箭头的位置
		var editTop =index==1?0: (index-1)*100;
		var arrowTop = index==1?0: (index-1)*114+60;
		$('#message-edit').css('margin-top',editTop);
		$('#edit-arrow').css('top',arrowTop);
		//设置删除按钮显示和隐藏，当编辑第一条是隐藏删除按钮
		index==1?$('#message-del').hide():$('#message-del').show();

		initEditorData(index);
	}
	//绑定删除按钮事件
	$('#message-del').on('click',function(){
		var $editMessage = $('#message-list').find('.message-edit');
		var index = parseInt($editMessage.attr('id').replace('message-menu-',''));
		delMessage(index);
		$("#message-list .message-edit").remove();
		setArrow(1);
	});

	function delMessage(index){
		var array = tw_message.articles;
		tw_message.articles = new Array();
		for(var i=0;i<array.length;i++){
			if(i!=index-1){
				tw_message.articles[i]=array[i];
			}
		}
	}


	/***************************************编辑器保存/保存并发送/****************************************************/
	$('#edit-preview-doc').on('click',function(){
		previewDoc();
	});
	$('#edit-save-message').click(function(){
		saveMessage();
	});
	
	$('#edit-submit-message').click(function(){
		saveAndMake(1);//提交
	});
	
	$('#edit-check-message').click(function(){
		saveAndMake(2);//校对完成
	});

	$('#edit-censorship-message').click(function(){
		saveAndMake(3);//送审
	});
	
	$('#edit-censorpub-message').click(function(){
		saveAndMake(4);//预签并送审
	});
	
	$('#pubShow').click(function(){//签发情况
		if(wxGroup_data.status == 3) pubShow(1);
		if(wxGroup_data.status == 1 || wxGroup_data.status == 2){
			pubShow(0);
		}
	});
	
	$('#edit-preview-message').on('click',function(){
//		openPreviewDialog();
		if(wxGroup_data.docidForhisAndFlow && wxGroup_data.docLibIdForhisAndFlow) {
			window.open("../../xy/article/View.do?DocIDs=" + wxGroup_data.docidForhisAndFlow
						 + "&DocLibID=" + wxGroup_data.docLibIdForhisAndFlow)
		}else {
			window.open("../../xy/article/View.do?DocIDs=" + wxGroup_data.docID
						 + "&DocLibID=" + wxGroup_data.docLibID)
		}
	});
	$('#message-preview-send').on('click',function(){
		previewMessage();
	});

	$('#edit-cancel-message').on('click',function(){
		var dataUrl = "../../e5workspace/after.do?UUID=" + wxGroup_data.UUID;
		window.location.href = dataUrl;
		window.close();
	});

	function previewDoc(){
		var content = ue.getContent();
		$("#previewDoc-content").html(content);
		$("#previewDoc-dialog").modal('show');
	}
	$("#previewDoc-dialog").on('hidden.bs.modal',function(){
		$("#previewDoc-content").empty();
	});

	var twId="";
	function saveMessage(){//保存
		if(!addMessage()){
			return ;
		};
		if (!twId) twId = getUrlParam("twId");
		$("#edit-save-message").attr("disabled");
		
		wxGroup_data["data"] = JSON.stringify(tw_message.articles);
		$.post('SubmitWX.do', wxGroup_data, function(data){
			if (data == "ok"){
				alert("保存成功！");
				wxGroup_data.isNew = false;
				$("#edit-save-message").removeAttr('disabled');
			} else {
				alert('保存失败!错误消息:' + data);
				$("#edit-save-message").removeAttr('disabled');
			}
		});
	}

	function saveAndMake(saveType){//1提交 2校对完成 3送审 4预签并送审
		if(!addMessage()){
			return ;
		};
		if (!twId) twId = getUrlParam("twId");
		$("#edit-save-message").attr("disabled");
		
		wxGroup_data["data"] = JSON.stringify(tw_message.articles);
		wxGroup_data["saveType"] = saveType; // 解决在编辑器中执行校对完成时，会多一条修改的流程记录问题
		
//		var tempwindow = window.open("","_blank","top=300,left=400,width=800,height=400,menubar=yes,scrollbars=no,toolbar=yes,status=yes"); // 先打开页面
		var dataUrl = "";
		if(saveType == 2) {
				dataUrl = "CheckThrough.jsp?IsEditor=true&DocIDs=" + wxGroup_data.docID 
					+ "&DocLibID=" + wxGroup_data.docLibID;
			}
			if(saveType == 3) {
				dataUrl = "Censorship.jsp?type=0&IsEditor=true&DocIDs=" + wxGroup_data.docID
					+ "&DocLibID=" + wxGroup_data.docLibID;
			}
			if(saveType == 4) {
				dataUrl = "../../xy/article/OriginalCensorship.do?type=2&IsEditor=true&DocIDs=" + wxGroup_data.docID
					 + "&DocLibID=" + wxGroup_data.docLibID + "&siteID=" + wxGroup_data.siteID;
			}
			if(dataUrl.length > 0){
//				tempwindow.location.href = dataUrl; // 后更改页面地址
				window.open(dataUrl, "_blank",
					"top=300,left=400,width=800,height=400,menubar=yes,scrollbars=no,toolbar=yes,status=yes");
			}
		
		
		$.post('SubmitWX.do', wxGroup_data, function(data){
			wxGroup_data.isNew = false;
			$("#edit-save-message").removeAttr('disabled');
			
			//saveType: 1提交   2校对完成   3送审   4预签并送审
			if(saveType == 1) {
				$.ajax({type: "POST", url: "../../xy/article/OriginalEditorSend.do", async:false, 
					data : {
						"DocIDs" : wxGroup_data.docID,
						"DocLibID" : wxGroup_data.docLibID
					},
					success: function(data){	
						if(data == "success"){
							alert("提交成功！");
						}else if(data == "failure"){
							alert("稿件是已提交状态，请勿重复提交！");
						}
						//$("#edit-submit-message").hide();
					}
				});
			}
			
		});
	}
	
	function pubShow(signType){
    	var theURL = "../../xy/article/SignInfo.do?DocIDs=" + wxGroup_data.docID 
			+ "&DocLibID=" + wxGroup_data.docLibID
    		+ "&SignType=" + signType;
		$.ajax({url:theURL, async:false, success:function(data){
			if(data.signInfo){
				alert(data.signInfo);//微信01,微信02,微信03
			}
		}});
    }
	
	function openPreviewDialog(){
		if(!addMessage()){
			return ;
		};

		$('#previewMessageModal').on('show.bs.modal',function(){
			$("#preview-info").html("关注公众号后，才能接收图文消息预览");
			$('#message-preview-send').removeAttr('disabled');
			$('#preview-error').empty();

		});
		$('#previewMessageModal').on('shown.bs.modal',function(){
			$(document).on('keydown',function(e){
				if(e.keyCode==13){
					previewMessage();
				}
			});
		});
		$('#previewMessageModal').on('hide.bs.modal',function(){
			$(document).off('keydown');
		});
		$("#previewMessageModal").modal("show");
	}

	function previewMessage(){
		$('#message-preview-send').attr({disabled:"disabled"});
		$('#preview-info').html("发送中，请稍后……<img style='width:16px' src='/newsedit/images/wating.gif'>");
		var wx_name = $("#message-preview-wxname").val();

		wxGroup_data["data"] = JSON.stringify(tw_message.articles);
		wxGroup_data["wx_name"] = wx_name;
		$.post('GroupPreview.do', wxGroup_data, function(data){
			if (data == "ok"){
				$('#message-preview-send').removeAttr('disabled');
				$('#preview-info').html("预览成功！");
				setTimeout(function(){
					$('#previewMessageModal').modal('hide');
				},2000);

			}else{
				$('#preview-info').html('预览失败!');
				$('#preview-error').html(data);
				$('#message-preview-send').removeAttr('disabled');
			}
		});
	}
/********************************************************************************/

	window.h5 = new H5();
	
});

window.onbeforeunload = function(){
	var tool = window.opener.e5.mods["workspace.toolkit"];
	tool.self.closeOpDialog("OK", 1);
	
	return "确认稿件已保存并退出编辑器？";
};

//获取url中的参数
function getUrlParam(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
	var r = window.location.search.substr(1).match(reg);  //匹配目标参数
	if (r != null) return (r[2]); return null; //返回参数值
}

var weixinEditor = {
	//上传图片或选择图片库后，显示图片
	setImage : function(imagepath){
		var $editMessage = $("#message-list .message-edit");
		var index = parseInt($editMessage.attr('id').replace('message-menu-',''));

		var img = (index == 1)
			? '<div style="width:384px;height:180px;overflow: hidden;"><img src="'+imagepath+'" style="width:100%;margin-top: -10px;"/></div>'
			: '<img src="'+imagepath+'" style="max-height:90px;max-width:90px"/>';

		if ($editMessage.find('img').length>0){
			$editMessage.find('img').replaceWith(img);
		}
		if ($editMessage.find('.tip').length>0){
			$editMessage.find('.tip').replaceWith(img);
		}

		$editMessage.find('.message-cover-toolbar').show();
	},
	//请求详细页数据 隐藏列表显示项详细
	showDetail : function(libID, id){
		$.get('GroupMaterial.do',{"libID": libID, "id":id},function(data){
			var detail ="<div>"
							+"<div style='border-bottom: 1px solid #eeeeee;'>"
								+"<div>" + data.title + "</div>"
								/*+"<div>"
									+"<span>作者：" + data.author + "</span>&nbsp;&nbsp;"
									+"<span>来源：" + data.docSource + "</span>"
								+"</div>"*/
							+"</div>"
							+"<div style='overflow-y:auto;'>"
								+data.content
							+"</div>"
						+"</div>";
			$('#material-detail-body').empty().append(detail);
			$('#material-detail-body').data('url',data.url);
		});
		$('#material-list').hide();
		$('#material-detail').show();
	},
	
	senIll: function () {
		var content = ue.getContent();
		var $btnSenIll = $("#btnSenIll");
		var isSenIll = $btnSenIll.hasClass("btnNoSenIll");
        //清除上一次的敏感词查询 在文中显示的高亮记录
        covert_sensitive.cleanSenStyle();
        covert_sensitive.cleanIllStyle();
		if(isSenIll) {
            $btnSenIll.removeClass("btnNoSenIll");
            $btnSenIll.addClass("btnSenIll");
            if (content != null && content == "") {
                $("#a_isSensitive").val(0);
            } else {
                if ((wxGroup_data.hasSensitive || wxGroup_data.hasIllegal)) {
                    checkSensitive(content, "内容" ,"isCheck");
                }
            }
		} else {
            $btnSenIll.removeClass("btnSenIll");
            $btnSenIll.addClass("btnNoSenIll");
		}
    },
	checkSensitive: function(content, ta, isCheck){
        var _ta = ta || "内容";
        // 先清除上一次的敏感词高亮记录
        // article_sensitive.cleanSenStyle();
        // article_sensitive.cleanIllStyle();
        if(article_sensitive.hasSensitive(content, ta)){
            //如果开启了敏感词检测服务
            if((wxGroup_data.hasSensitive)){
                //判断是否有敏感词
                //如果有,弹出confirm，询问用户是否需要继续提交稿件
                //article_sensitive 在article-form.js中
                if(article_sensitive.senList != null && article_sensitive.senList.length != 0){
                    //显示敏感词
					if(isCheck && isCheck=="isCheck"){
                        article_sensitive.handleSensitive(_ta);
					} else {
                        //如果用户点击确定，不做处理；否则，处理
                        if(!confirm(_ta + "当中存在以下敏感词：" + article_sensitive.senList.join("，") + "\n是否继续提交？")){
                            article_sensitive.handleSensitive(_ta);
                            return true;
                        } else{
                            article_all.hasSen = true;
                            // article_sensitive.cleanSenStyle();
                        }
					}
                } else{
                    //如果没有敏感词列表为空，即没有敏感词，就把这个字段设为0
                    article_all.hasSen = article_all.hasSen || false;
                }
            }
            //如果开启了非法词检测服务
            if((wxGroup_data.hasIllegal)){
                if(article_sensitive.illList != null && article_sensitive.illList.length != 0){
                    if(isCheck && isCheck=="isCheck"){
                        article_sensitive.handleIllegal(_ta);
                    } else {
                        //如果用户点击确定，不做处理，马上提交；否则，处理，修改非法词
                        if(!confirm(_ta + "当中存在以下非法词：" + article_sensitive.illList.join("，") + "\n是否继续提交？")){
                            article_sensitive.handleIllegal(_ta);
                            return true;
                        } else{
                            article_all.hasIll = true;
                            // article_sensitive.cleanIllStyle();
                        }
					}
                } else{
                    //如果没有非法词列表为空，即没有非法词，就把这个字段设为0
                    article_all.hasIll = article_all.hasIll || false;
                }
            }

            var hs = article_all.hasSen ? 1 : 0;
            var hi = article_all.hasIll ? 2 : 0;

            $("#a_isSensitive").val(hs + hi);
        }
        var hs = article_all.hasSen ? 1 : 0;
        var hi = article_all.hasIll ? 2 : 0;

        $("#a_isSensitive").val(hs + hi);

        return false;
	},
	
	
}

//隐藏送审相关按钮
function censorshipHide(){
	$('#edit-censorship-message').hide();//送审按钮
	$('#edit-censorpub-message').hide();//预签并送审按钮
	$('#edit-submit-message').hide();//提交按钮
}

var H5 = function(){
	this.go();
};
H5.prototype = {
	dialog: null,
	go: function(){
		this.init();
	},
	init: function(){
		var _this = this;
		$("#btnH5").click(function(){
			var url = "../../xy/article/H5file.html?URL=" + article.url;
			var pos = {left: "260px", top: "50px", width: "1200px", height: "500px"};
			_this.dialog = e5.dialog({type: "iframe", value: url}, {
				showTitle: true,
				title: "选择H5设计",
				width: "1200px",
				height: "500px",
				pos: pos,
				resizable: false
			});
			_this.dialog.show();
		});

		$("#a_multimediaLink").attr("readonly", "readonly");
	},
	closeDialog: function(){
		this.dialog.close();
	},
	confirmDialog: function(json){
		/*$("#a_linkID").val(json.id);
		$("#a_linkName").val(json.title);*/
		$("#message-link").val(json.url);
		this.dialog.close();
	}
};
/**********************************************标题图********************************************/
var channel_frame = {
	titleDialog: null,
	smallC : "",
	midC : "",
	bigC : "",
	titleImagePath : "",
	widthRadio: 0,
	heightRadio:0,
	smallCR: null,
	midCR: null,
	bigCR: null,
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
		var pos = {left : "100px",top : "50px",width : "1170px",height : "500px"};

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
				width : "1170px",
				height : "610px",
				pos : pos,
				resizable : false
			});
		//}
		channel_frame.titleDialog.show();
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
	/*setImageProperties: function(){
		channel_frame.smallC = null;
		channel_frame.midC = null;
		channel_frame.bigC = null;
		channel_frame.titleImagePath = null;
		channel_frame.widthRadio = null;
		channel_frame.heightRadio = null;
		channel_frame.smallCR = null;
		channel_frame.midCR = null;
		channel_frame.bigCR = null;
	},*/
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

//		if(haseUseOrignalImgOnly=='false'){
			if(!channel_frame.smallC && !channel_frame.midC && !channel_frame.bigC){
				alert("请选择裁剪区域！");
				return;
			}
//		}

    	$.ajax({
			url : "../../xy/ueditor/createtitleImg.do",
			type : 'POST',
			async : true,
			data : {
				"imagePath" : channel_frame.titleImagePath,
				"docLibID" : wxGroup_data .docLibID,
				"docID" : wxGroup_data .docID,
				"smallCoords" : JSON.stringify(channel_frame.smallC),
				"midCoords" : JSON.stringify(channel_frame.midC),
				"bigCoords" : JSON.stringify(channel_frame.bigC),
				"widthRadio" : channel_frame.widthRadio,
				"heightRadio" : channel_frame.heightRadio,
				"smallCR" : channel_frame.smallCR,
				"midCR" : channel_frame.midCR,
				"bigCR" : channel_frame.bigCR,
				"imageName" :"",// channel_frame.fileName,
				"isLocalFile" :false// channel_frame.isLocalFile
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

		channel_frame.titleDialog.close();
		//是否有单独的使用原图的功能
		//sessionStorage.setItem("useOrignalImgOnly",false);
	},
	
	//设置标题图 - 使用原图
	handleTitleImgHtml : function(_msg){
		if( _msg.imgBig ){
			if(sessionStorage.getItem("useOrignalImgOnlySmall")=="true"){
					console.log("小图使用原图")
			}else{
					$("#topicPicDiv").html('<img id="picSmall" itype="small" src="../'+ decodeURI(_msg.imgBig.substr(_msg.imgBig.lastIndexOf('image.do?path=')))+'"/><span class="icon-remove"></span>')
					channel_frame.titleDelListener("#topicPicDiv");
					$("#topicPicDivAdd").hide();
					$("#topicPicDiv").show();
			}

		}
		//不关闭
		if(sessionStorage.getItem("useOrignalImgOnly")=="true"){
			// alert("暂时不关闭")
		}else{
			// alert("关闭")
			channel_frame.titleDialog.close();
		}
	},
	
	//标题图的删除功能模块
	titleDelListener : function(_id){
//		$(_id).unbind("mouseover").unbind("mouseout");
		$(_id).has("img[src!='']").each(function(){
			var _$this = $(this);
			var _type = _$this.attr("itype");
			//获得小中大
			var _name = _type=="small" ? "小" : ( _type == "mid" ? "中" : ( _type=="big" ? "大" : ""));
			//给里面的删除图片添加一个点击事件
			_$this.find("span").click(function(e){
				e.stopPropagation();
				//阻止父级的事件冒泡
				_$this.html("")
				$("#topicPicDivAdd").show();
//              channel_frame.isSelectImg();
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
}


/**********************************************批注********************************************/
var ueditor_commment = {
    	//初始化批注面板
		initcomment:function (){
			var ue = UE.getEditor("editor");
			var $commentList = $("#tab3 .list-group").empty();

			var _comments = $(ue.body).find(".selected-comment");

			if(!_comments.length) {
				return;
			}

			//$("#tab3").show().siblings().hide();
			//默认不显示批注
			if(!$("#tab3").is(':visible')){
				$("#ueditor_0").contents().find(".selected-comment").css("background-color", "");
				$("#ueditor_0").contents().find(".selected-comment-img").css("background-color", "").css("border", "");
			}
			// 收集所有的herfto属性 用于判断是否新增批注
			var herfto_Arr=[];
			_comments.each(function(){
				herfto_Arr.push($(this).attr('herfto'))
			});

			_comments.each(function(index){
			// 如果加批注的内容为空 删除
			var _reg=/\<[\s\S]*?\>/g;
			var _contents=$(this).html();
			var _text=_reg.test(_contents) ? _contents.replace(_reg,""):_contents;
			if($.trim(_text)){
				// 若有内容 添加批注
				var timestamp = $(this).attr("flag-id");
				var user = $(this).attr("data-user");
				var date = $(this).attr("data-date");
				var comment = $(this).attr("comment");
				var herfto=$(this).attr("herfto");

				// 确保相同的批注只出现一次
				if(herfto_Arr.indexOf(herfto)!=index) return;

				if(user == article.userName){
					$commentList.append('<a id="comment-' + timestamp + '" href="#' + timestamp + '" class="list-group-item">'
						+ '<dl style="margin-bottom: 0;">'
						+ '<dt>'
						+ '<span style="float: left;padding:5px">' + user + '</span>'
						+ '<span style="float: right;padding:5px">' + date + '</span>'
						+ '</dt>'
						+ '<dd>'
						+ '<textarea data-id="#' + timestamp + '" style="width: 95%;min-height:60px; overflow:hidden;resize:none ">' + comment + '</textarea>'
						+ '</dd>'
						+ '<dd>'
						+ '<button class="btn-xs btn-default" data-id="#' + timestamp + '" style="border: 1px solid #ddd;">移除</button>'
						+ '</dd>'
						+ '</dl>'
						+ '</a>');
				} else {
					//他人添加的批注  只可以查看 不可以修改
					$commentList.append('<a id="comment-' + timestamp + '" href="#' + timestamp + '" class="list-group-item-2">'
						+ '<dl style="margin-bottom: 0;">'
						+ '<dt>'
						+ '<span style="float: left;padding:5px">' + user + '</span>'
						+ '<span style="float: right;padding:5px">' + date + '</span>'
						+ '</dt>'
						+ '<dd>'
						+ '<textarea readonly data-id="#' + timestamp + '" style="width: 95%;min-height:60px; overflow:hidden;resize:none ">' + comment + '</textarea>'
						+ '</dd>'
						+ '<dd>'
						// + '<button disabled="disabled" class="btn-xs btn-default" data-id="#' + timestamp + '" style="border: 1px solid #ddd;">移除</button>'
						+ '</dd>'
						+ '</dl>'
						+ '</a>');
				}
			}else{
				$(this).after($(this).html());
				$(this).remove();
			}
		});

    	//绑定批注面板和标签的联动事件(点击批注标签同时选中批注面板)
		$(ue.body).delegate(".selected-comment", "click", function(){
			if($("#tabComment").hasClass("select")){
				$("ul.tabs #tabComment").trigger('click');
				$("#ueditor_0").contents().find(".selected-comment").css("background-color", "rgb(197, 191, 191)");
				$("#ueditor_0").contents().find(".selected-comment-img").css("background-color", "rgb(197, 191, 191)").css("border", "5px solid rgb(197, 191, 191)");
				if($(this).hasClass("selected-comment-img")){
					$(this).css("background-color", "rgb(249, 181, 82)").css("border", "5px solid rgb(249, 181, 82)");
				}else{
					$(this).css("background-color", "rgb(249, 181, 82)");
				}

				var id = $(this).attr("flag-id");
				var $commentList = $("#tab3 .list-group");
				$commentList.find("a").css("background-color", "");
				$commentList.find("a").css("color", "");
				$("#comment-" + id).css("background-color", "rgb(249, 181, 82)");
				$("#comment-" + id).css("color", "#333");

				$("#tab3").scrollTop($("#comment-" + id).position().top);

			}

		});

    	//绑定批注面板和标签的联动事件(点击批注面板同时选中批注标签)
		$("#tab3").delegate(".list-group a", "click", function(){
			var $commentList = $("#tab3 .list-group");
			$commentList.find("a").css("background-color", "");
			var id = $(this).attr("href").substr(1);
			$(this).css("background-color", "rgb(249, 181, 82)");
			$(this).css("color", "#333");
			var $ueBody = $(ue.body);
			$ueBody.find(".selected-comment").css("background-color", "rgb(197, 191, 191)");
			$ueBody.find(".selected-comment-img").css("background-color", "rgb(197, 191, 191)").css("border", "5px solid rgb(197, 191, 191)");
			if($ueBody.find("." + id).hasClass("selected-comment-img")){
				$ueBody.find("." + id).css("background-color", "rgb(249, 181, 82)").css("border", "5px solid rgb(249, 181, 82)");
			}else{
				$ueBody.find("." + id).css("background-color", "rgb(249, 181, 82)");
			}
			//$("html body")
			//console.log('document'+$(document).scrollTop())
			// console.log("html,body"+$("html,body").scrollTop())
			$("html,body").stop().animate({scrollTop: 90}, 100,function(){
				if($ueBody.find("." + id) && $ueBody.find("." + id).offset() && $ueBody.find("." + id).offset().top){
					$("#ueditor_0").contents().find("html,body").stop().animate({scrollTop: $ueBody.find("." + id).offset().top},0);
				}
			});
		});

		//批注编辑鼠标移动事件
		var canSelect=false;
		function prevent(){
			if(canSelect){
				return true;
			}else{
				return false;
			}
		}
		$("#tab3").bind("mousemove","a",prevent);
		$("#tab3").delegate("textarea", "focus", function(){
			canSelect=true;
		})
		$("#tab3").delegate("textarea", "blur", function(){
			canSelect=false;
		})
	}
}

// 阻止批注列表里的a链接的默认拖拽事件
$(function () {
	$("#tab3 .list-group").delegate("a", "dragstart",function () {
		return false;
	})
})

/**********************************************************标题样式编辑*************************************************************/
var article_all = {
	editDialogType : 0,// 0为标题，1为摘要，2为副题
	editDialog : null,//主栏目对话框
	init : function() {
		//按钮的响应事件
		$("#btnTitleAdv").click(article_all.editTitle);		//标题编辑样式按钮
//			
		$(window).on('beforeunload',function(){
			$(window).on('unload',function(){
				var dataUrl = "../../e5workspace/after.do?UUID=" + article.UUID;
				$.ajax({url:dataUrl,async:false});
				});
			return '您确定要关闭吗？';
			});
		$(document).keydown(article_all.refreshF5); //F5刷新
		
//		article_all.initChannel();
//		
//		article_all.cookieColumns();
//		
//		article_all.initDateTimePicker();
//		
//		article_all.initTabSelect();
//
//      article_all.checkSenIll();

		//设置验证
		$("#form").validationEngine({
			autoPositionUpdate:true,
			promptPosition:"bottomLeft",
			scroll:true
		});
	},
	openEditDialog : function(){
		var content = '';
		if( article_all.editDialogType == 0 ){
			content = $("#cover-title").val();
		}
		var dataUrl = "../../xy/article/editStyle.jsp?editDialogType="+article_all.editDialogType+"&e_type=articlesetting";
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
}

//获取标题内容的方法，在editorStyle.jsp中调用
function getContent(){
	var content = '';
	if( article_all.editDialogType == 0 ){
		content = $("#cover-title").val();
	}
	return content;
}

//样式编辑取消按钮
function editCancel(){
	article_all.editDialog.close();
}

//样式编辑确定按钮
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
		$("#cover-title").val(contents);
	}
	article_all.editDialog.close();
}

$(function () {
	setTimeout(function () {
		article_all.init();
	},2000)
})
