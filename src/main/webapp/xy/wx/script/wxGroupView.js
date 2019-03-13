$(function(){
/***************初始化编辑器数据******************/

	window.UEDITOR_HOME_URL="../plugin/ueditor";
	var ue = UE.getEditor('editor',{
		//initialFrameWidth :800,//设置编辑器宽度
		initialFrameHeight:525,//设置编辑器高度
		scaleEnabled:true
		});
	var appId;
	var tw_message = new Object();

	ue.ready(function(){
		appId = getUrlParam("groupID");
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

	/*************************图文列表操作*******************/
	//保存图文消息的对象
	function getTwMsgData(){
		if (wxGroup_data.isNew == "true"){
			tw_message.articles = new Array();
		} else {
			var param = {
				groupLibID : wxGroup_data.docLibID,
				groupID : wxGroup_data.groupID
			}
			$.get('GroupArticles.do', param ,function(data){
				console.log(data)
				tw_message.articles = data;

				initMessageList();

				initEditorData(1);
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

	// //保存子消息到tw_message中
	// function addMessage(){
	// 	var $editMessage = $('#message-list').find('.message-edit');
	// 	var index = parseInt($editMessage.attr('id').replace('message-menu-',''));
    //
	// 	var title = $editMessage.find('input').val();
	// 	var checkID = $editMessage.find('input[class=message-cover-checkID]').val();
	// 	var checkLibID = $editMessage.find('input[class=message-cover-checkLibID]').val();
	// 	var pic = $editMessage.find('img').attr('src');
	// 	if (pic) {
	// 		pic = pic.substr(pic.indexOf("image.do?path=") + "image.do?path=".length);
	// 	}
    //
	// 	var content = $('#contentView').html();
	// 	var date = $('#dateView').html();
	// 	if(title==""){
	// 		alert("标题不能为空！");
	// 		return false;
	// 	}else if (!pic){
	// 		alert("封面不能为空！");
	// 		return false;
	// 	}else{
	// 		tw_message.articles[index-1]={
	// 			"pic":pic,
	// 		   "title":title,
	// 		   "content":content,
	// 		   "date":date,
	// 		   "checkID":checkID,
	// 		   "checkLibID":checkLibID
	// 		};
	// 		return true;
	// 	}
	// }
    //保存子消息到tw_message中 hhr
    function addMessage(){

        // var $editMessage = $('#message-list').find('.message-edit');
        // var index = parseInt($editMessage.attr('id').replace('message-menu-',''));
        //
        // var title = $editMessage.find('input').val();
        // var checkID = $editMessage.find('input[class=message-cover-checkID]').val();
        // var checkLibID = $editMessage.find('input[class=message-cover-checkLibID]').val();
        // var pic = $editMessage.find('img').attr('src');
        // if (pic) {
        //     pic = pic.substr(pic.indexOf("image.do?path=") + "image.do?path=".length);
        // }
        //
        // var content = $('#contentView').html();
        // var date = $('#dateView').html();

		var title=globalData[0].title;
    	var pic=globalData[0].pic;
        if(title==""){
            alert("标题不能为空！");
            return false;
        }else if (!pic){
            alert("封面不能为空！");
            return false;
        }else{
            // tw_message.articles[index-1]={
            //     "pic":pic,
            //     "title":title,
            //     "content":content,
            //     "date":date,
            //     "checkID":checkID,
            //     "checkLibID":checkLibID
            // };
            tw_message.articles=globalData;
            return true;
        }
    }
	//初始化编辑器数据
	function initEditorData(index){
		var $editMessage = $('#message-list').find('.message-edit');
		var data = tw_message.articles[index-1];
		
		$('#titileView').html(data.title);
		$('#dateView').html(formatCSTDate(data.date, "yyyy-MM-dd"));
		$('#contentView').html(data.content);
	}
	//初始化图文列表
	function initMessageList(){
		if(tw_message.articles.length>0){
			var  $message;
			for(var i=0;i<tw_message.articles.length;i++){
				var img;
				if (i > 0){
					addSubMessage();

					img = '<img " style="max-width:90px;max-height:90px;" src="../image.do?path='+tw_message.articles[i].pic+'"/>';
				} else {
					img = '<div style="width:384px;height:180px;overflow: hidden;"><img style="width:100%;margin-top:0px;" src="../image.do?path='+tw_message.articles[i].pic+'"/></div>';
				}
				$message = $('#message-menu-' + (i + 1));
				$message.find('.tip').replaceWith(img);
				$message.find('input[type=text]').val(tw_message.articles[i].title);
				$message.find(".message-cover-checkID").val(tw_message.articles[i].checkID);
				$message.find(".message-cover-checkLibID").val(tw_message.articles[i].checkLibID);
				if(i==0) $('.panel-heading').html(formatCSTDate(tw_message.articles[i].date, "MM-dd hh:mm:ss"));
			}
		}
	}
	function addSubMessage(){
		var menuIndex = $('#message-list').find('li').length;
		if(menuIndex>8){
			alert("最多添加8条消息！");
			return;
		}
		var messageSub = '<li id="message-menu-'+menuIndex+'" class="list-group-item" style="padding:0">'
						   +' <div class="message-list-sub">'
							   +' <input type="text" class="message-list-input" placeholder="点此输入标题" aria-describedby="basic-addon1">'
							   +' <input type="hidden" class="message-cover-checkID">'   
							   +' <input type="hidden" class="message-cover-checkLibID">'   
							  	+' <div class="message-list-input message-list-thumbnail">'
									   +' <div class="tip" >'
										   +' <a href="#">'
												+' <span class=" glyphicon glyphicon-plus"></span>'
										   +' <br/>缩略图'
										   +' </a>'
									   +' </div>'
								+' </div>'
							+' </div>'
							   +' <div class="message-sub-mask">'
								+' <span class="glyphicon glyphicon-pencil message-sub-edit"> 详情</span>'
							   +' </div>'
						  +' </li>';
		$('#message-list li:last').before(messageSub);
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

		initEditorData(index);
	}

	/***************************************编辑器保存/保存并发送/****************************************************/
	$('#flowInfo').on('click',function(){
		previewFlow();
	});
	$('#drawHistoryInfo').on('click',function(){
		previewHistory(1);
		//分页
        pageF()
	});
	$('#edit-preview-doc').on('click',function(){
		previewDoc();
	});
	$('#edit-save-message').click(function(){
		saveMessage();
	});
	$('#edit-preview-message').on('click',function(){
		openPreviewDialog();
	});
	$('#message-preview-send').on('click',function(){
		previewMessage();
	});
	$('#edit-push-message').on('click',function(){
		pushMessage();
	});
	$('#alldoThrough').click(function(){
		var dataUrl = "../wx/editorReason.jsp?type=alldoThrough";
		window.open(dataUrl, "_blank",
				"top=300,left=400,width=600,height=400,menubar=yes,scrollbars=no,toolbar=yes,status=yes");
		//editorAlldoflow(1);//审核全部通过
	});
	$('#alldoReject').click(function(){
		var dataUrl = "../wx/editorReason.jsp?type=alldoReject";
		window.open(dataUrl, "_blank",
				"top=300,left=400,width=600,height=400,menubar=yes,scrollbars=no,toolbar=yes,status=yes");
		//editorAlldoflow(2);//驳回全部
	});
	
	$('#doThrough').click(function(){
		var dataUrl = "../wx/editorReason.jsp?type=doThrough";
		window.open(dataUrl, "_blank",
				"top=300,left=400,width=600,height=400,menubar=yes,scrollbars=no,toolbar=yes,status=yes");
		//editordoflow(1);//审核通过
	});
	$('#doReject').click(function(){
		var dataUrl = "../wx/editorReason.jsp?type=doReject";
		window.open(dataUrl, "_blank",
				"top=300,left=400,width=600,height=400,menubar=yes,scrollbars=no,toolbar=yes,status=yes");
		//editordoflow(2);//驳回
	});
	
	$('#edit-cancel-message').on('click',function(){
		window.close();
	});
    // var HistoryInfopage = 1;
	function previewHistory(HistoryInfopage){
		var checkID = groupArticleInfo.checkID;
		var checkLibID = groupArticleInfo.checkLibID;

		var theURL = "../../xy/article/HistoryInfo.do?DocIDs=" + checkID
			+ "&DocLibID=" + checkLibID + "&Page=" + HistoryInfopage;
		$.ajax({url:theURL, async:false, success:function(data){
			drawHistoryTable(data.list);
		}});
	}
	
	//渲染历史版本
	function drawHistoryTable(data){
	    console.log("历史版本")
	    console.log(data)
	if(data.length>0){
        var logTableHtml = "<table id='histotrytable' class='table' border='1' style='width:510px;background:#fff'><thead><tr><td>历史版本时间</td>"
        + "<td>操作人</td><td>操作</td></tr></thead><tbody>";
        for(var i=0;i<data.length;i++){
        logTableHtml += "<tr><td>" +data[i].created+ "</td>"
        + "<td>" + data[i].operator + "</td>"
        + "<td style='width:200px;color:#ccc'>" +data[i].operation+ "</td>";
        }
        logTableHtml += "</tbody></table>";
        //历史版本分页
        logTableHtml+='<div clas="pagebtn" id="pagecotainner">'+
            '   <span>'+
            '        <span id="currentpagenum">1</span>/'+
            '        <span id="pagenum">8</span>'+
            '   </span>'+
            '    <span id="pageleft" class="pagebtn_border">'+
            '        <img src="../img/pageleft.png" alt="">'+
            '    </span>'+
            '    <span id="pageright" class="pagebtn_border">'+
            '        <img src="../img/pageright.png" alt="">'+
            '    </span>'+
            '    <span id="pagejumpval">'+
            '            <input  id="pageinput" type="text" name="text1" >'+
            '    </span>'+
            '    <span id="pagejump" class="pagebtn_border">'+
            '        跳转'+
            '    </span>'+
            '</div>';
        $("#drawHistoryTable").html(logTableHtml);

	}
	}
	
	//修改
	$("#option_btn").click(function(){
//		var url = window.location.href;
//		http://localhost:8080/xy/article/OriginalWX.do?DocLibID=2&DocIDs=187989&groupID=16&siteID=100
		
//		url = url.replace("type=1", "type=0");
//		url += "&status=1";
		var url = "/xy/article/OriginalWX.do?DocLibID="+$(".currentOn").attr("data-grouparticlelibid")+"&DocIDs="+$(".currentOn").attr("data-grouparticleid")+"&groupID="+wxGroup_data.groupID+"&siteID="+
			article.siteID+"&wx_groupid="+wxGroup_data.groupID;
		window.open(url, "_blank",
			"top=300,left=400,width=1280,height=630,menubar=yes,scrollbars=no,toolbar=yes,status=yes");
	});
	
	//全文复制
	$("#copyContent").click(function(){
		var content = $("#wxBox").html();
		var theURL = "../../xy/wx/EditorCopy.do";
		$.ajax({
			type: "post",
			url:theURL,
			data:{Content:content},
			async:false, 
			dataType: "html",
			success:function(data){
                var wxGroupTextarea=document.getElementById("wxGroupTextarea");
                $("#wxGroupTextarea").text(data);
                wxGroupTextarea.select(); // 选择对象
                document.execCommand("Copy");
                alert("复制成功");
			}
		});
	});
	
	function previewFlow(){
		var checkID = groupArticleInfo.checkID;
		var checkLibID = groupArticleInfo.checkLibID;
		var theURL = "../../xy/article/CensorshipLog.do?DocIDs=" + checkID
			+ "&DocLibID=" + checkLibID + "&wxGroupId=" +wxGroup_data.groupID+ "&Type=1";
		$.ajax({url:theURL, async:false, success:function(data){
			drawTable(data.logList);
		}});
	}
	
	//渲染流程记录
	function drawTable(data){
	if(data.length>0){
	var logTableHtml = "<table class='table ' border='1' style='width:510px;background:#fff'><thead><tr><td>审核人</td>"
	+ "<td>审核操作</td><td>审核时间</td><td>审核意见</td></tr></thead><tbody>";
	for(var i=0;i<data.length;i++){
	logTableHtml += "<tr><td>" + data[i].operator + "</td>"
	+ "<td>" + data[i].fromPosition + data[i].operation + "</td>"
	+ "<td sytle='width:200px'>" + formatCSTDate(data[i].startTime,"yyyy-MM-dd hh:mm:ss") + "</td>";
	if(data[i].detail==null){
	logTableHtml += "<td></td></tr>";
	}else{
	logTableHtml += "<td>" + data[i].detail + "</td></tr>";
	}
	}
	logTableHtml += "</tbody></table>";
	$("#logTable").html(logTableHtml);
	}
	}
	
	function previewDoc(){
		var content = ue.getContent();
		$("#previewDoc-content").html(content);
		$("#previewDoc-dialog").modal('show');
	}
	$("#previewDoc-dialog").on('hidden.bs.modal',function(){
		$("#previewDoc-content").empty();
	});

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
	}
}

/**
 * 全部审核通过 或 全部驳回
 * type: 1全部审核通过  2全部驳回
 */
function editorAlldoflow(type, reason){
	var theURL = "../../xy/wx/EditorAlldoflow.do?GroupID=" + wxGroup_data.groupID 
		+ "&GroupLibID=" + wxGroup_data.docLibID + "&Type=" + type + "&Reason=" + reason;
	$.ajax({url:theURL, async:false, success:function(data){
		if(data=='success'){
			$("#alldoThrough").hide();
			$("#alldoReject").hide();
			$("#censorship").hide();
			$("#reject").hide();
			if(type==1){
				wxGroup_data.groupStatus=2;//未发布
				alert("全部审核通过！");
			}
			if(type==2){
				wxGroup_data.groupStatus=4;//已驳回
				alert("全部驳回！");
			}
			
			$("#tabutation > .tab_banner").trigger("click")
		}
	}});
}

/**
 * 图文组中某一篇稿件审核通过 或 驳回
 * type: 1审核通过  2驳回
 */
function editordoflow(type, reason){
	var groupArticleID = groupArticleInfo.groupArticleID;
	var groupArticleLibID = groupArticleInfo.groupArticleLibID;
	var theURL = "../../xy/wx/Editordoflow.do?GroupArticleID=" + groupArticleID
	+ "&GroupArticleLibID=" + groupArticleLibID + "&GroupID=" + wxGroup_data.groupID 
	+ "&GroupLibID=" + wxGroup_data.docLibID + "&Type=" + type + "&Reason=" + reason;
	$.ajax({url:theURL, async:false, success:function(data){
		$("#doThrough").hide();
		$("#doReject").hide();
		var currentIndex=$(groupArticleInfo.item).find(".tabListIndex").text();
		if(type==1){
			globalData[currentIndex].status = 2;
			alert("审核通过！");
		}
		if(type==2){
			globalData[currentIndex].status = 4;
			alert("驳回成功！");
		}
		if(data == "successAll"){//图文组中稿件 已经全部通过 或 全部驳回
			$("#alldoThrough").hide();
			$("#alldoReject").hide();
		}
		
	}});
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


function  pageF(){
    // if(){ //若果只有一页#
    //     $("#pagebtn").css("display","none")
    //     return
    // }
    // 总页数
    $("#pagenum").text("3");

    $("#pageleft").css("display","none");
    $("#pageright").click(function () {
        var pagenum=parseInt($("#currentpagenum").text());
        var pagecout= $("#pagenum").text();
        if(parseInt(pagenum)+1>=pagecout){
            $("#pageright").css("display","none");
            $("#pageleft").css("display","inline-block");
        }
        $("#currentpagenum").text(parseInt(pagenum)+1);

        // 调用接口  parseInt(pagenum)+1;当前页数
        previewHistory(parseInt(pagenum)+1);

    })
    $("#pageleft").click(function () {
        var pagenum=$("#currentpagenum").text();
        var pagecout= $("#pagenum").text();
        if(parseInt(pagenum)-1<=1){
            $("#pageright").css("display","inline-block");
            $("#pageleft").css("display","none");
        }
        $("#currentpagenum").text(parseInt(pagenum)-1);
        // 调用接口  parseInt(pagenum)-1;
        previewHistory(parseInt(pagenum)-1);

    })
    $("#pagejump").click(function () {
        var  inputval=$("#pageinput").val();
        if (inputval=="") {
            alert("请输入页数");
            $("#pageinput").val("");
            return false;
        }
        if (!(/(^[1-9]\d*$)/.test(inputval))) {
            alert("输入不合法");
            $("#pageinput").val("");
            return false;
        }else {
            if(inputval>$("#pagenum").text()){
                alert("请输入正确的页数")
                $("#pageinput").val("");
                return
            }
            var needjump= $("#pageinput").val();
            $("#currentpagenum").text(needjump);
            if(needjump==$("#pagenum").text()){
                $("#pageright").css("display","none");
                $("#pageleft").css("display","inline-block");
            }
            if(needjump==1){
                $("#pageright").css("display","none");
                $("#pageleft").css("display","inline-block");
            }
            //调用接口
            // 当前页数needjump
            previewHistory(needjump);

        }
    })
}





