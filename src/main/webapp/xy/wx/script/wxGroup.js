$(function(){
	
/***************初始化编辑器数据******************/
	window.UEDITOR_HOME_URL="../plugin/ueditor";
	var ue = UE.getEditor('editor',{
		//initialFrameWidth :800,//设置编辑器宽度
		initialFrameHeight:525,//设置编辑器高度
		scaleEnabled:true
		});
	var appId;
	window.tw_message = new Object();


	ue.ready(function(){
		appId = getUrlParam("groupID");
		getTwMsgData();
	});
	showListDetail();//显示素材列表
	
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
		var content = $('#currentContent').html();
		/*var url = $('#material-detail-body').data("url");
		 $("#message-link").val(url);*/
		
		ue.execCommand('clearDoc');
		ue.execCommand('insertHtml', content);

		var $editMessage = $('#message-list').find('.message-edit');
		$editMessage.find('input[type=text]').val($('#currentTitle').html());
		$editMessage.find('input[class=message-cover-url]').val($('#currentUrl').val());
		$editMessage.find('input[class=message-cover-checkID]').val($('#currentID').val());
		$editMessage.find('input[class=message-cover-checkLibID]').val($('#currentLibID').val());
		
		$('#material-colse').click(); //点选用后，自动关闭素材显示页，返回素材列表
		
	});
	$('.material-reback').on('click',function(e){
		if (confirm("你确定要将这篇稿件退回到原有渠道吗？")) {
			
			var docIDs=$(this).attr("data-checkID");
			var docLibID = $(this).attr("data-checkLibID");
			var accountID = wxGroup_data.accountID;
			
				$.get('../../xy/article/OriginalWXReback.do',{"DocLibID": docLibID,
					"DocIDs":docIDs, "AccountID":accountID}, function(msg){
					termReload();
					//showListDetail();//显示素材列表
					$("#list_tbody button[data-checkid="+chooseId+"]").parent().parent().remove()
				});
              //alert("退回");
			//$('#material-colse').click(); //点退回后，自动关闭素材显示页，返回素材列表
		}
		//deleteitem(this.dataset.checkid);
		var chooseId=this.dataset.checkid;
		$("#message-list input[value="+chooseId+"]").parent().find(".deleteitem").trigger("click");
		console.log(this.dataset.checkid);
		e.stopPropagation(); //阻止事件冒泡
	});

    console.log(wxGroup_data.docLibID);

	/********************文章列表操作******************/
	$('#material-detail').hide();
	$('#material-colse').click(function(){
		$('#material-list').show();
		$('#material-detail').hide();
	});

	/*******************上传图片*************************/
	//给所有添加图片按钮绑定事件   打开添加图片弹出层
	$('#message-list').delegate('.message-list-cover .tip, .message-list-sub .tip, .message-cover-toolbar .glyphicon-cog, .message-list-sub .message-list-thumbnail a, .message-list-cover .uploadImgDiv a','click',function(e){
		resetPicTarget = e.target.closest(".list-group-item"); // 获取上传图片按钮最近的祖先元素li
//		console.log(resetPicTarget)

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
	//初始化图文消息的对象
	function getTwMsgData(){
		if (wxGroup_data.isNew == "true"){
			tw_message.articles = new Array();
			$("#wxGrapTitle").text("新建")
		} else {
            $("#wxGrapTitle").text("修改")
			var param = {
				groupLibID : wxGroup_data.docLibID,
				groupID : wxGroup_data.groupID
			}
			$.get('GroupArticles.do', param ,function(data){
				tw_message.articles = data;
				console.log(tw_message.articles)


				initMessageList();
				dragAndDrop()
				listStyleInit()
				initEditorData(1);
			});
		}
	}

	//给非编辑的消息绑定事件--鼠标进入覆盖mask
	$('#message-list').delegate('li:not(.message-edit,.message-add)','mouseover',function(){
			$(this).find('div:last').show();
	});
//	$('#message-list').delegate('li','mouseover',function(){
//			$(this).find('div:last').show();
//	});
	//给非编辑的消息绑定事件--鼠标进入隐藏mask
	$('#message-list').delegate('li:not(.message-edit,.message-add)','mouseout',function(){
			$(this).find('div:last').hide();
	});
//	$('#message-list').delegate('li','mouseout',function(){
//			$(this).find('div:last').hide();
//	});
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
	/*$('#message-list').delegate('.message-cover-toolbar .glyphicon-cog','click',function(){
		var tip = ' <div class="tip" >'
					   +' <a href="#">'
							+' <span class=" glyphicon glyphicon-plus"></span>'
							+' <br/>缩略图'
					   +' </a>'
				   +' </div>';
		$(this).parent().parent().find('.message-list-thumbnail img,.message-list-cover .uploadImgDiv').replaceWith(tip);
		$(this).parent().hide();
	});*/

	//保存子消息到tw_message.articles中
	function addMessage(){
		var $editMessage = $('#message-list').find('.message-edit');
		var index = parseInt($editMessage.attr('id').replace('message-menu-',''));

		var title = $editMessage.find('input[type=text]').val();
		var url = $editMessage.find('input[class=message-cover-url]').val();
		var checkID = $editMessage.find('input[class=message-cover-checkID]').val();
		var checkLibID = $editMessage.find('input[class=message-cover-checkLibID]').val();
		var pic = $editMessage.find('img').attr('src');
		if (pic) {
			pic = pic.substr(pic.indexOf("image.do?path=") + "image.do?path=".length);
		}

		var content = ue.getContent();

		if(title==""){
			alert("标题不能为空！");
			return false;
		}else if (!pic){
			alert("封面不能为空！");
			return false;
		}else{
			// tw_message.articles[index-1]={
			//    "pic":pic,
			//    "title":title,
			//    "content":content,
			//    "url":url,
			//    "checkID":checkID,
			//    "checkLibID":checkLibID
			// };
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
		if(tw_message.articles.length>0){
			var  $message;
			for(var i=0;i<tw_message.articles.length;i++){
				var img;
				if (i > 0){
					addSubMessage();
					if(tw_message.articles[i].pic.indexOf("image.do?path=") != -1){
						tw_message.articles[i].pic = tw_message.articles[i].pic.split("image.do?path=")[1];
					}
					img = '<img " src="../image.do?path='+tw_message.articles[i].pic+'"/>';
				} else {
					if(tw_message.articles[i].pic.indexOf("image.do?path=") != -1){
						tw_message.articles[i].pic = tw_message.articles[i].pic.split("image.do?path=")[1];
					}
					img = '<div class="uploadImgDiv"><img src="../image.do?path='+tw_message.articles[i].pic+'"/></div>';
				}
				$message = $('#message-menu-' + (i + 1));
				$message.find('.tip').replaceWith(img);
//				$message.find('.message-cover-title').html(tw_message.articles[i].title);

//				if(tw_message.articles[i].title.length>18){
//                	tw_message.articles[i].title=tw_message.articles[i].title.slice(0,16)+"......";
//            	}

				$message.find('.message-cover-title').length ? $message.find('.message-cover-title').html(tw_message.articles[i].title) : $message.find('.abstractAndTitleBox .message-list-input').html(tw_message.articles[i].title);
				
//				$message.find('.message-cover-title').length ? $message.find('.message-cover-title').html(tw_message.articles[i].title+"你好的") : $message.find('.abstractAndTitleBox .message-list-input').html(tw_message.articles[i].title).append('<span class="glyphicon glyphicon-pencil editTitle" title="编辑标题"></span>');
				
				$message.find('.message-cover-abstract').html(abstractText(tw_message.articles[i].abstract));
				$message.find(".message-cover-url").val(tw_message.articles[i].url);
				$message.find(".message-cover-checkID").val(tw_message.articles[i].checkID);
				$message.find(".message-cover-checkLibID").val(tw_message.articles[i].checkLibID);
//				$("#list_tbody tr[data-checkid = 174273]").find(".key_select key_word").css({display:"inline-block"});
			}
		}
		
		$("#message-list li").each(function (index, item) {
			$("#list_tbody tr[data-checkid = "+$(item).find(".message-cover-checkID").val()+"]").find(".key_select").show()
			
		})
		
	}
	function addSubMessage(){
		var menuIndex = parseInt($('#message-list').find('li').length) + 1;
		if(menuIndex>8){
			alert("最多添加8条消息！");
			return;
		}
		var messageSub = '<li draggable="true" id="message-menu-'+menuIndex+'" class="list-group-item" style="padding:0">'
							+' <span class="ui-icon ui-icon-arrow-4 drag_icon dragAndDrop"></span>'
						   +' <div class="message-list-sub">'
            // 添加删除图标
            +'<div class="deleteitem" onclick="deleteitem(this)"><a href="#" class="close">&times;</a></div>'
            
							   +' <input type="hidden" class="message-cover-url">'
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
								+'<div class="abstractAndTitleBox">'
//							   +' <input type="text" class="message-list-input" placeholder="点此输入标题" aria-describedby="basic-addon1" disabled>'
							   +' <textarea class="message-list-input" placeholder="点此输入标题" aria-describedby="basic-addon1"></textarea>'
//							   +'<span class="glyphicon glyphicon-pencil editTitle" title="编辑标题"></span>'
							   +'<textarea class="message-cover-abstract" placeholder="点此输入摘要" ></textarea>'
//							   +'<span class="glyphicon glyphicon-pencil editAbstract" title="编辑摘要"></span>'
							   +'</div>'
							+' </div>'
							   +' <div class="message-cover-toolbar sub-tool">'
								   +'<span class="glyphicon glyphicon-pencil" title="编辑封面图片"></span>'
								   +'<span class="glyphicon glyphicon-cog" title="设置封面图片" style="margin-left:10px;"></span>'
							   +'</div>'

							   // +' <div class="message-sub-mask">'
								// +' <span class="glyphicon glyphicon-pencil message-sub-edit"> 编辑内容</span>'
							   // +' </div>'
						  +' </li>';
//		$('#message-list li:last').before(messageSub);
		$('#message-list').append(messageSub);
//      editDragF();
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


	//添加子消息
	$('.message-list-add span').on('click',function(){
		if(!addMessage()){
			return false;
		};
		addSubMessage();
		var index = $('#message-list li').length-1;
		setArrow(index);
	 });
	/***************************************编辑器保存/保存并发送/****************************************************/
	$('#edit-preview-doc').on('click',function(){
		previewDoc();
	});
	$('#edit-save-message').click(function(){//保存
		saveMessage(false);
	});
	$('#edit-censorship-message').click(function(){//送审
		saveMessage(true);
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

	$('#edit-cancel-message').on('click',function(){
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
	function saveMessage(flag){//保存图文组
//      console.log("修改")
//      console.log(tw_message.articles)

		// if(!addMessage()){
		// 	return ;
		// };
		// if (!twId) twId = getUrlParam("twId");

		$("#edit-save-message").attr("disabled");
        //wxGroup_data["isCensorship"] = flag;
        // tw_message.articles=[];
        var myTw_messageArticles=[];
        var isflag = true;
        
		$("#message-list li").each(function (index,val) {
		    if(!$(val).hasClass("message-add")){
            	var Wxgroup_globalData={};
				if($(val).hasClass("text-center")){
					// 1、判断是否有标题 没有的话提示
					var firstInput=$(val).find(".message-list-input .message-cover-title").eq(index);
	                var firstTitle=$(val).find(".message-list-input .message-cover-title").eq(index).val();
	                if(!firstTitle){
	                	isflag = false;
                        alert("请为第"+(index+1)+"条添加标题");
                        return;
					}else{
						 Wxgroup_globalData.title=firstTitle;
					}
					
					//2、判断是否有标题图 没有的话提示
					var firstimg=$(val).find(".message-list-input img").attr("src");
	                if(firstimg && firstimg != ""){
	                	firstimg=firstimg.slice(firstimg.indexOf("path=")+5);
	                }
	                if(!firstimg){
	                	isflag = false;
	                	alert("请为第"+(index+1)+"条上传图片");
	                    return
	                }else{
	                	Wxgroup_globalData.pic=firstimg;
	                }
	                
					//3、新加到左侧的需要添加这两个属性 请求content的内容
	                var materialID=$(firstInput).eq(index).attr("data-materialID");
	                var materialLibID=$(firstInput).eq(index).attr("data-materialLibID");
	                if(materialID){
							$.ajax({
								type : "get",
								url : "GroupMaterial.do",
								data : {"materialID": materialID, "materialLibID":materialLibID},
								async : false,
								success : function(data){
									console.log(data);
									Wxgroup_globalData.content=data.content;
									Wxgroup_globalData.url=data.url;
									Wxgroup_globalData.checkID = data.docID;
		                            Wxgroup_globalData.checkLibID = data.docLibID;
	                                Wxgroup_globalData.status =0;
//	                                Wxgroup_globalData.abstract = data.abstract;
								}
							});
	                }else {
	                	// 解决拖拽之后图文组顺序与 tw_message.articles对应的位置不一样 
	                	tw_message.articles.forEach(function (item, i) {
	                		if(item.checkID == $(val).find(".message-cover-checkID").val()) {
	                			Wxgroup_globalData.content=item.content;
			                    Wxgroup_globalData.url=item.url;
			                    Wxgroup_globalData.checkID = item.checkID;
			                    Wxgroup_globalData.checkLibID = item.checkLibID;
			                    Wxgroup_globalData.status =item.status;
	                		}
	                	})
	                	
//	                    Wxgroup_globalData.content=tw_message.articles[index].content;
//	                    Wxgroup_globalData.url=tw_message.articles[index].url;
//	                    Wxgroup_globalData.checkID = tw_message.articles[index].checkID;
//	                    Wxgroup_globalData.checkLibID = tw_message.articles[index].checkLibID;
//	                    Wxgroup_globalData.status =tw_message.articles[index].status;
	                }
	
	            }else if($(val).hasClass("list-group-item")&&!$(val).hasClass("text-center")&&!$(val).hasClass("message-add")){
	            	// 1、判断是否有标题 没有的话提示
	                var otherTitle=$(val).find(".message-list-sub .abstractAndTitleBox .message-list-input").eq(0).val();
	                var otherInput=$(val).find(".message-list-sub .abstractAndTitleBox .message-list-input").eq(0);
	                if(!otherTitle){
	                	isflag = false;
                        alert("请为第"+(index+1)+"条添加标题");
                        return;
					}else{
						 Wxgroup_globalData.title=otherTitle;
					}
					
					//2、判断是否有标题图 没有的话提示
	                var otherimg=$(val).find(".message-list-sub .message-list-thumbnail img").attr("src");
	                if(otherimg && otherimg != ""){
	                	otherimg=otherimg.slice(otherimg.indexOf("path=")+5);
	                }
	                if(!otherimg){
	                	isflag = false;
	                    alert("请为第"+(index+1)+"条上传图片")
	                    return
	                }else{
	                	Wxgroup_globalData.pic=otherimg;
	                }
	                
	                //3、新加到左侧的需要添加这两个属性 请求content的内容
	                var materialID=$(otherInput).attr("data-materialID")
	                var materialLibID=$(otherInput).attr("data-materialLibID")
	                if(materialID) {
	                    $.ajax({
	                        type: "get",
	                        url: "GroupMaterial.do",
	                        data: {"materialID": materialID, "materialLibID": materialLibID},
	                        async: false,
	                        success: function (data) {
	                            console.log(data);
	                            Wxgroup_globalData.content = data.content;
	                            Wxgroup_globalData.url = data.url;
	                            Wxgroup_globalData.checkID = data.docID;
	                            Wxgroup_globalData.checkLibID = data.docLibID;
	                            Wxgroup_globalData.status =0;
	                        }
	                    });
	                }else {
	                	// 解决拖拽之后图文组顺序与 tw_message.articles对应的位置不一样 
	                	tw_message.articles.forEach(function (item, i) {
	                		if(item.checkID == $(val).find(".message-cover-checkID").val()) {
	                			Wxgroup_globalData.content=item.content;
			                    Wxgroup_globalData.url=item.url;
			                    Wxgroup_globalData.checkID = item.checkID;
			                    Wxgroup_globalData.checkLibID = item.checkLibID;
			                    Wxgroup_globalData.status =item.status;
	                		}
	                	})
//	                    Wxgroup_globalData.content=tw_message.articles[index].content;
//	                    Wxgroup_globalData.url=tw_message.articles[index].url;
//	                    Wxgroup_globalData.checkID = tw_message.articles[index].checkID;
//	                    Wxgroup_globalData.checkLibID = tw_message.articles[index].checkLibID;
//	                    Wxgroup_globalData.status =tw_message.articles[index].status;
	                }
				}
				Wxgroup_globalData.abstract = $(val).find(".message-cover-abstract").val();
                myTw_messageArticles.push(Wxgroup_globalData)
            }
        });
     	tw_message.articles=myTw_messageArticles;
		wxGroup_data["data"] = JSON.stringify(tw_message.articles);
		console.log(wxGroup_data)
		if(tw_message.articles.length != 0 && isflag) {
//			alert(111)
			$.post('WXGroupSave.do', wxGroup_data, function(data){
				if (!flag){
					alert("保存成功！");
					wxGroup_data.isNew = false;//是新建还是修改
					$("#edit-save-message").removeAttr('disabled');//让保存可以点击
				} else if (flag){//如果是送审
					wxGroup_data.isNew = false;
					$("#edit-save-message").removeAttr('disabled');
					var dataUrl = "../wx/editorReason.jsp?type=doCensorship";
					window.open(dataUrl, "_blank",
						"top=300,left=400,width=800,height=400,menubar=yes,scrollbars=no,toolbar=yes,status=yes");
				} else {
					alert('保存失败!错误消息:' + data);
					$("#edit-save-message").removeAttr('disabled');
				}
			});
		}
		
	}
	function pushMessage(){
		if(!addMessage()){
			return ;
		};
		$('#confirm-dialog').modal('show');
		$('#operation-confirm-btn').on('click',function(){
			$("#confirm-info").html("发送中，请稍后……<img style='width:16px' src='/newsedit/images/wating.gif'>");
			$('#operation-confirm-btn').attr({disabled:"disabled"});

			wxGroup_data["data"] = JSON.stringify(tw_message.articles);
			$.post('GroupPublish.do', wxGroup_data, function(data){
				if (data == "ok"){
					$("#confirm-info").html("发送成功！");
					$('#operation-confirm-btn').removeAttr('disabled');
					setTimeout(function(){
						$('#confirm-dialog').modal('hide');
					},2000);
				} else {
					$("#confirm-info").html("发送失败！");
					$('#confirm-error').html(data);
				}
			});
		});
		$('#confirm-dialog').on('show.bs.modal',function(){
			$("#confirm-info").text("确定群发此消息？");
			$('#operation-confirm-btn').removeAttr('disabled');
			$('#confirm-error').empty();
		});
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
var resetPicTarget = null; // 定义重置图片按钮，以便上传图片后设置图片到对应的li里

var pageCount = 0;

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

//		var $editMessage = $("#message-list .message-edit");
//		console.log(resetPicTarget)
		var $editMessage = $(resetPicTarget);
//		var index = parseInt($editMessage.attr('id').replace('message-menu-',''));

//		var img = (index == 1)
//			? '<div class="uploadImgDiv"><img src="'+imagepath+'" /></div>'
//			: '<img src="'+imagepath+'"/>';
		var img ='<img src="'+imagepath+'" />';
		
		var imgFirstLi = '<div class="uploadImgDiv"><img src="'+imagepath+'" /></div>';
		var imgOtherLi = '<div class="message-list-input message-list-thumbnail"><img src="'+imagepath+'" /></div>';
		
		if ($editMessage.find('img').length>0){
			$editMessage.find('img').replaceWith(img);
		}
		if ($editMessage.find('.message-list-cover .tip').length>0){
			$editMessage.find('.message-list-cover .tip').replaceWith(imgFirstLi);
		}
		if ($editMessage.find('.message-list-thumbnail').length>0){
			$editMessage.find('.message-list-thumbnail').replaceWith(imgOtherLi);
		}

		$editMessage.find('.message-cover-toolbar').show();
	},
	//请求详细页数据 隐藏列表显示项详细
	showDetail : function(materialID, materialLibID, checkID, checkLibID){
		$.get('GroupMaterial.do',{"materialID": materialID, "materialLibID":materialLibID},function(data){
//			console.log(data);
            return data
			//赋值到左侧图文组
			
			// 标题：data.title；<input type="text" class="message-cover-title" maxlength="64"  placeholder="点此输入标题" aria-describedby="basic-addon1">
			// 内容：data.content； ？
			// checkID： 传入的checkID参数；   <input type="hidden" class="message-cover-checkID">
			// checkLibID： 传入的checkLibID参数；   <input type="hidden" class="message-cover-checkLibID">
			// URL：data.url； <input type="hidden" class="message-cover-url">
		});
	}
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


// hhr
//删除选取的稿件
function deleteitem(item) {
	if($("#message-list li").length>1) {
		$(item).parent().parent().remove();
		console.log(item);
	    //获取删除的对象并把右侧已选状态移除
	    var deleteid=$(item).parent().find(".message-cover-checkID").val();
		//var deleteid=$(item).selector;
        //var deleteid=$(item).attr("data-checkid");
		console.log(deleteid);
	    console.log($(item));

		//$(item).remove();
	  //if($(item).parent().siblings("input[data-checkID="+deleteid+"]")){
	  //    return
		//}
		listInit()
	}else {
		var deleteid=$(item).parent().find(".message-cover-checkID").val();
		$("#message-list").html($("#message-list-template").html())
	}
	
	
	listStyleInit()
	dragAndDrop()
//	alert($("#list_tbody tr[data-checkID="+deleteid+"]").attr("data-upperEdition"))
	if($("#list_tbody tr[data-checkID="+deleteid+"]").attr("data-upperEdition") == 0) {
		$("#list_tbody tr[data-checkID="+deleteid+"]").find(".key_select").css({display:"none"});
	}
	
    
//	console.log(item);
//	console.log(deleteid);
}

//        给input添加onblurf
messageInput();
function messageInput(){
    $(".message-list-input").each(function(index,val){
        $(val).blur(function(){
            //不能为空格和空
            var currentTitle=$(this).val();
            if($(this).attr("title")&&$(this).attr("title").indexOf(currentTitle)!=-1){
                currentTitle=$(this).attr("title");
            }
//          if(currentTitle.length>10){
//              var gramCurTitle=currentTitle.slice(0,10)+"......";
//              $(this).attr({"title":currentTitle});
//              $(this).val(gramCurTitle)
//          }else{
//              $(this).attr({"title":currentTitle});
//              $(this).val(currentTitle)
//          }
			$(this).attr({"title":currentTitle});
            $(this).val(currentTitle)
        });
        $(val).focus(function(){
            var currentTitle=$(this).attr("title");
            if(!currentTitle){
                return
            }
            $(this).attr({"title":currentTitle});
            $(this).val(currentTitle)
            //                if(currentTitle.length>5){
            //                    var gramCurTitle=currentTitle.slice(0,5)+"......";
            //                    $(this).attr({"title":currentTitle});
            //                    $(this).val(gramCurTitle)
            //                }else{
            //                    $(this).val(currentTitle)
            //                    $(this).attr({"title":currentTitle});
            //                }

        })
    });
    $(".message-list-input input").each(function(index,val){
        $(val).blur(function(){
            var currentTitle=$(this).val();
            if($(this).attr("title")&&$(this).attr("title").indexOf(currentTitle)!=-1){
                currentTitle=$(this).attr("title");
            }

//          if(currentTitle.length>10){
//              var gramCurTitle=currentTitle.slice(0,10)+"......";
//              $(this).attr({"title":currentTitle});
//              $(this).val(gramCurTitle)
//          }else{
//              $(this).attr({"title":currentTitle});
//              $(this).val(currentTitle)
//          }

			$(this).attr({"title":currentTitle});
            $(this).val(currentTitle)

        });
        $(val).focus(function(){
            var currentTitle=$(this).attr("title");
            if(!currentTitle){
                return
            }
            $(this).attr({"title":currentTitle});
            $(this).val(currentTitle)
            //                 if(currentTitle.length>5){
            //                     var gramCurTitle=currentTitle.slice(0,5)+"......";
            //                     $(this).attr({"title":currentTitle});
            //                     $(this).val(gramCurTitle)
            //                 }else{
            //                     $(this).val(currentTitle)
            //                     $(this).attr({"title":currentTitle});
            //                 }

        })

    });
}



//交换数组的方法
function swapArray(arr,index1,index2){
    arr[index1] = arr.splice(index2, 1, arr[index1])[0];
    return arr;
}

showListDetail(1);
function showListDetail(page){
	
	if($('#select_keyword').val()) {
		var title = $('#select_keyword').val().trim();
	}
	if($('#select_artID').val()) {
		var articleID = $('#select_artID').val().trim();
	}
	if($('#select_author').val()) {
		var author = $('#select_author').val().trim();
	}
	if($('#select_beginTime').val()) {
		var beginTime = $('#select_beginTime').val().trim();
	}
	if($('#select_endTime').val()) {
		var endTime = $('#select_endTime').val().trim();
	}
    
    var sqlDetail = "SELECT * FROM xy_wxarticle WHERE SYS_DELETEFLAG = 0 AND wx_menuID = 0";
    if(title){
        sqlDetail += " AND SYS_TOPIC LIKE '%" + title + "%'";
    }
    if(articleID){
        sqlDetail += " AND SYS_DOCUMENTID = " + articleID;
    }
    if(author){
        sqlDetail += " AND SYS_AUTHORS = '" + author + "' ";
    }
    if(beginTime){
        sqlDetail += " AND SYS_CREATED >= '" + beginTime + "' ";
    }
    if(endTime){
        sqlDetail += " AND SYS_CREATED <= '" + endTime + "' ";
    }
    sqlDetail += " AND wx_accountID = " + wxGroup_data.accountID;
    sqlDetail += " ORDER BY SYS_DOCUMENTID DESC";
    $.ajax({
        type: "post",
        url: "../../xy/wx/GroupListDetail.do",
        data: {"sqlDetail": sqlDetail, "page": page},
        async: false,
        success: function (data) {
        	if(data.pagecount==1){
        		$(".selectartpager").css("display","none");
			}else if(data.pagecount>1) {
                $(".selectartpager").css("display","block");
			}
			
			pageCount = data.pagecount
			
            //渲染素材列表
            drawMataList(data.list);
            // 素材点击事件
            $("#list_tbody tr input[type='checkbox']").click(function(e){  
			    e.stopPropagation();   
			}); 
            $("#list_tbody tr").on("click", function () {
            	
            	var isNoCheckID = true;
            	// 获取稿件素材信息
            	var currentText=$(this).find(".list_item").text();
	            var curTextGram="";
	            var crrentId=$(this).attr('data-checkID');
	            var materialID=$(this).attr('data-materialID');
	            var materialLibID=$(this).attr('data-materialLibID');
	            var checkLibID=$(this).attr('data-checkLibID');
	            var picUrl=$(this).attr("data-pic");
	            var abstractContent = $(this).attr("data-abstract");
	            
	            $(".message-cover-checkID").each(function (index, ele) {
	            	if($(ele).val() == crrentId) {
	            		alert("稿件重复，请选择其他稿件");
	            		isNoCheckID = false;
	            		return;
	            	}
				})
	            
	            
	            // （需要判断字数，字数过多为省略号）
//	            if(currentText.length>10){
//	                curTextGram=currentText.slice(0,10)+"......"
//	            }else{
//	                curTextGram=currentText
//				}
	            curTextGram=currentText;
		        if(isNoCheckID) {
		            	// 添加到左侧图文组
		            if($("#message-list li:first").find(".message-cover-title").html() != "") {
		            	
		            	var menuIndex = $('#message-list').find('li').length;
		            	var nowMenuIndex = parseInt(menuIndex) + 1;
						if(menuIndex>8){
							alert("最多添加8条消息！");
							return;
						}
						
						if(picUrl.length !=0) {
							var messageSub = '<li draggable="true" id="message-menu-'+nowMenuIndex+'" class="list-group-item" style="padding:0">'
											+' <span class="ui-icon ui-icon-arrow-4 drag_icon dragAndDrop"></span>'
										   	+' <div class="message-list-sub">'
				            // 添加删除图标
				            +'<div class="deleteitem" onclick="deleteitem(this)"><a href="#" class="close">&times;</a></div>'
//											   +' <input type="text" disabled class="message-list-input" placeholder="点此输入标题" aria-describedby="basic-addon1" value='+curTextGram+' data-materialID='+materialID+' data-materialLibID='+materialLibID+'>'
											   +' <input type="hidden" class="message-cover-url">'
											   +' <input type="hidden" class="message-cover-checkID" value='+crrentId+'>'   
											   +' <input type="hidden" class="message-cover-checkLibID" value='+checkLibID+'>'
											   +' <div class="message-list-input message-list-thumbnail">'
	//												   +' <div class="tip" >'
	//													   +' <a href="#">'
	//															+' <span class=" glyphicon glyphicon-plus"></span>'
	//													   +' <br/>缩略图'
	//													   +' </a>'
	//												   +' </div>'
													   +'<img " src="../image.do?path='+picUrl+'"/>'
												+' </div>'
												+'<div class="abstractAndTitleBox">'
												   +' <textarea class="message-list-input" placeholder="点此输入标题" aria-describedby="basic-addon1" data-materialID='+materialID+' data-materialLibID='+materialLibID+'>'+curTextGram+'</textarea>'
												   +'<textarea placeholder="点此输入摘要" class="message-cover-abstract">'+abstractContent+'</textarea>'
												   +'</div>'
											+' </div>'
											   +' <div class="message-cover-toolbar sub-tool">'
												   +'<span class="glyphicon glyphicon-pencil" title="编辑封面图片"></span>'
												   +'<span class="glyphicon glyphicon-cog" title="设置封面图片" style="margin-left:10px;"></span>'
											   +'</div>'
				
											   // +' <div class="message-sub-mask">'
												// +' <span class="glyphicon glyphicon-pencil message-sub-edit"> 编辑内容</span>'
											   // +' </div>'
										  +' </li>';

						}else {
							var messageSub = '<li draggable="true" id="message-menu-'+nowMenuIndex+'" class="list-group-item" style="padding:0">'
											+' <span class="ui-icon ui-icon-arrow-4 drag_icon dragAndDrop"></span>'
										   +' <div class="message-list-sub">'
				            // 添加删除图标
				            +'<div class="deleteitem" onclick="deleteitem(this)"><a href="#" class="close">&times;</a></div>'
//											   +' <input type="text" disabled class="message-list-input" placeholder="点此输入标题" aria-describedby="basic-addon1" value='+curTextGram+' data-materialID='+materialID+' data-materialLibID='+materialLibID+'>'
											   +' <input type="hidden" class="message-cover-url">'
											   +' <input type="hidden" class="message-cover-checkID" value='+crrentId+'>'   
											   +' <input type="hidden" class="message-cover-checkLibID" value='+checkLibID+'>'
											   +' <div class="message-list-input message-list-thumbnail">'
													   +' <div class="tip" >'
														   +' <a href="#">'
																+' <span class=" glyphicon glyphicon-plus"></span>'
														   +' <br/>缩略图'
														   +' </a>'
													   +' </div>'
												+' </div>'
												+'<div class="abstractAndTitleBox">'
												   +' <textarea class="message-list-input" placeholder="点此输入标题" aria-describedby="basic-addon1" data-materialID='+materialID+' data-materialLibID='+materialLibID+'>'+curTextGram+'</textarea>'
												   +'<textarea class="message-cover-abstract" placeholder="点此输入摘要" >'+abstractContent+'</textarea>'
												   +'</div>'
											+' </div>'
											+' </div>'
											   +' <div class="message-cover-toolbar sub-tool">'
												   +'<span class="glyphicon glyphicon-pencil" title="编辑封面图片"></span>'
												   +'<span class="glyphicon glyphicon-cog" title="设置封面图片" style="margin-left:10px;"></span>'
											   +'</div>'
				
											   // +' <div class="message-sub-mask">'
												// +' <span class="glyphicon glyphicon-pencil message-sub-edit"> 编辑内容</span>'
											   // +' </div>'
										  +' </li>';
						}
						$('#message-list').append(messageSub);
//						$('#message-list li:last').before(messageSub);
		            }else {
		            	$("#message-list li:first").find(".message-cover-title").html(curTextGram);
		            	$("#message-list li:first").find(".message-cover-title").attr("data-materialLibID",materialLibID);
		            	$("#message-list li:first").find(".message-cover-title").attr("data-materialID",materialID);
		            	$("#message-list li:first").find('.message-cover-abstract').html(abstractText(abstractContent));
		            	$("#message-list li:first").find(".message-cover-checkID").val(crrentId);
		            	$("#message-list li:first").find(".message-cover-checkLibID").val(checkLibID);
		            	$("#message-list li:first").find(".message-cover-toolbar").show();
		            	
		            	if(picUrl.length !=0 ) {
		            		var img = '<div class="uploadImgDiv"><img src="../image.do?path='+picUrl+'"/></div>';
		            		$("#message-list li:first").find(".tip").replaceWith(img);
		            	}
		            	
		            }
		            $(this).find(".key_select").css({display:"inline-block"});
		            
		            listStyleInit();
		            
		            
		       }
	            // 初始化拖拽
	            dragAndDrop()
			})
            
            $("#message-list li").each(function (index, item) {
				$("#list_tbody tr[data-checkid = "+$(item).find(".message-cover-checkID").val()+"]").find(".key_select").show();
				
			})
        }
    });
    //var theURL = "../../xy/wx/GroupListDetail.do?sqlDetail=" + sqlDetail + "&page=" + page;
    //$.ajax({url:theURL, async:false, success:function(data){
    //        console.log(data);
            //渲染素材列表
    //        drawMataList(data);
    //}});
}


//渲染素材列表
function drawMataList(data){
	console.log(data);
    var MataListHtml="";
	for(var i=0;i<data.length;i++){
		if(data[i].belongto == "") {
			MataListHtml+='<tr draggable="true" data-upperEdition="0" data-materialID="'+data[i].materialID+'" data-materialLibID="'+data[i].materialLibID+'"  data-checkID="'+data[i].checkID+'" data-abstract="'+data[i].abstract+'" data-checkLibID="'+data[i].checkLibID+'" data-pic="'+data[i].pic+'">'+
          //'									<td><input type="checkbox" name="checkNames" value="'+data[i].checkID+'"></td>'+
            '									<td>'+
            '										<div class="list_tbodycon">'+
            '											<span class="key_select key_word"><img src="../img/select.png"></span> <span'+
            '												class="list_item">'+data[i].title+'</span>' + 
//          											'<span class="key_img key_word">图</span> <span'+
//          '												class="key_sensitive key_word">敏</span> <span'+
//          '												class="key_illegal key_word">非</span>'+
            '										</div>'+
            '									</td>'+
            '									<td>'+data[i].author+'</td>'+
            '									<td>'+data[i].lastPerson+'</td>'+
            '									<td>'+data[i].createTime+'</td>'+	
            '									<td>'+data[i].materialID+'</td>'+
			'									<td>'+"<button class='material-reback btn btn-default' data-checkID="+data[i].checkID+" data-checkLibID="+data[i].checkLibID+">退回</button>"+'</td>'+
            '</tr>';
		}else {
			MataListHtml+='<tr draggable="true" data-upperEdition="1" data-materialID="'+data[i].materialID+'" data-materialLibID="'+data[i].materialLibID+'"  data-checkID="'+data[i].checkID+'" data-abstract="'+data[i].abstract+'" data-checkLibID="'+data[i].checkLibID+'" data-pic="'+data[i].pic+'">'+
          //'									<td><input type="checkbox" name="checkNames" value="'+data[i].checkID+'"></td>'+
            '									<td>'+
            '										<div class="list_tbodycon">'+
            '											<span class="key_select key_word" style="display:inline-block;" ><img src="../img/select.png"></span> <span'+
            '												class="list_item">'+data[i].title+'</span>' + 
//          											'<span class="key_img key_word">图</span> <span'+
//          '												class="key_sensitive key_word">敏</span> <span'+
//          '												class="key_illegal key_word">非</span>'+
            '										</div>'+
            '									</td>'+
            '									<td>'+data[i].author+'</td>'+
            '									<td>'+data[i].lastPerson+'</td>'+
            '									<td>'+data[i].createTime+'</td>'+	
            '									<td>'+data[i].materialID+'</td>'+
			'									<td><button class="material-reback btn btn-default" data-checkID="+data[i].checkID+" data-checkLibID="+data[i].checkLibID+" style="display: none;">退回</button></td>'+
            '</tr>';
		}
		
	}
    $("#list_tbody").html(MataListHtml);

};

//清空查询条件
function termReload(){
	$('#select_keyword').val("");
    $('#select_artID').val("");
    $('#select_author').val("");
    $('#select_beginTime').val("");
    $('#select_endTime').val("");
}


//送审
function editorCensorship(reason){
	$.ajax({type: "POST", url: "../../xy/wx/EditorCensorship.do", async:false, 
		data : {
			"GroupID" : wxGroup_data.groupID,
			"GroupLibID" : wxGroup_data.docLibID,
			"Reason" : reason
		},
		success: function(data){	
			if(data == "success"){
				alert("保存并送审成功！");
			}else if(data == "failure"){
				alert("稿件已送审，请勿重复操作！");
			}
			$("#edit-censorship-message").hide();
		}
	});
}

// 图文组拖拽

$(function () {
	
	dragAndDrop();
	console.log(pageCount)
	$('.selectartpager').pagination({
		pageCount: pageCount,
		jump: true,
		coping: true,
		homePage: '首页',
		endPage: '末页',
		prevContent: '上页',
		nextContent: '下页',
		callback: function (api) {
			showListDetail(api.getCurrent())
		}
	});
	
})

// 列表中摘要和标题样式
function listStyleInit() {
	// 判断第一个li里是否存在图文
	if($("#message-list li:first").find(".message-cover-checkID").val() == "") {
		//第一个li下面的摘要和标题显示
    	
    	$(".message-list-cover .abstractAndTitleBox").css({
			"display": "none"
		})
    	
    	$(".message-list-cover").css({
    		"height": "180px"
    	})
	}else {
		
		if($("#message-list li").length < 2) { // 如果只有一个图文
			
			//第一个li下面的摘要和标题显示
	    	$(".message-list-cover .abstractAndTitleBox").css({
				"display": "block",
				"background": "#fff"
			})
	    	$(".message-list-cover .abstractAndTitleBox .message-cover-abstract").css({
				"display": "block"
			})
	    	
	    	$(".message-list-cover .abstractAndTitleBox .message-cover-title").css({
				"color": "inherit"
			})
	    	
	    	if($(".message-list-cover .abstractAndTitleBox .message-cover-title").text().length > 18) {
	    		$(".message-list-cover .abstractAndTitleBox .editTitle").css({
					"margin-left": "10px",
					"color": "inherit",
		    		"position": "position",
		    		"right": "0px",
			    	"top": "10px"
				})
	    	}else {
	    		$(".message-list-cover .abstractAndTitleBox .editTitle").css({
					"margin-left": "10px",
					"color": "inherit",
		    		"position": "static"
				})
	    	}
	    	
//	    	$(".message-list-cover .abstractAndTitleBox .editAbstract").show();
	    	$(".message-list-cover .abstractAndTitleBox .editAbstract").css({
				"margin-left": "10px",
				"color": "#000",
	    		"position": "static"
			})
	    	
	    	$(".message-list-cover").css({
	    		"height": "262px"
	    	})
	    }else {
	    	//第一个li下面的 abstractAndTitleBox 设置样式
		    $(".message-list-cover .abstractAndTitleBox").css({
	    		"position": "absolute",
			    "bottom": "0px",
			    "left": "0px",
			    "background-color": "rgba(0, 0, 0, 0.7)",
			    "width": "100%",
			    "display": "block"
	    	})
		    
		    
	    	$(".message-list-cover .abstractAndTitleBox .message-cover-title").css({
	    		"background-color": "transparent",
	    		"color": "#fff"
	    	})
	    	
	    	$(".message-list-cover .abstractAndTitleBox .editTitle").css({
	    		"color": "#fff",
	    		"position": "absolute",
			    "right": "0px",
			    "top": "10px"
	    	})
	    	
	    	$(".abstractAndTitleBox .message-cover-abstract").css({
				"display": "none"
			})
	    	$(".abstractAndTitleBox .editAbstract").css({
				"display": "none"
			})
	    	
	    	$(".message-list-cover").css({
	    		"height": "180px"
	    	})
	    	
	    	// 从第二个li开始的所有li
	    	$(".message-list-sub").css({
	    		"height": "inherit"
	    	})
	    	
	    	$(".message-list-sub .abstractAndTitleBox").css({
	    		"position": "static",
	    		"background": "#fff"
	    	})
	    	
	    	$(".message-list-sub .abstractAndTitleBox .message-list-input").css({
	    		"color": "inherit"
	    	})
	    	
	    	$(".message-list-sub .abstractAndTitleBox .editTitle").css({
	    		"color": "inherit",
	    		"position": "static",
	    		"margin-left": "10px"
	    	})
	    	
	    }
	}
}


function abstractText(str) {
	if(str.length == 0) {
		return "无摘要";
	}else {
		return str;
	}
}

// 初始化列表样式
function listInit() {
	// 1.先清空所有的li以及li里面标签的所有class

	$("#message-list li, #message-list li>div, #message-list li > div:nth-child(2) > div:last-child > textarea:first-child, #message-list li > div:nth-child(2) > div:nth-child(5), #message-list li > div:nth-child(2)").removeClass();
	
	// 2.给第一个li标签以及li里面标签添加class
	$("#message-list li:first").addClass("addTitle list-group-item message-edit text-center ui-sortable-handle"); //第一个li标签
	$("#message-list li:first > div:first").addClass("message-list-input message-list-cover"); // li标签里第一个子元素div
	$("#message-list li:first > div:nth-child(2) > div:last-child > textarea:first").addClass("message-cover-title"); // li标签里第一个子元素div下的第一个input标签
	
	
//  $("#message-list li:first > div:first div:first-child").hide(); // li标签里第一个子元素div下的第一个div删除按钮隐藏
	$("#message-list li:first > div:nth-child(2) > div:nth-child(5)").addClass("uploadImgDiv"); // li标签里第一个子元素div下的最后一个图片div
	$("#message-list li:first > div:last-child").addClass("message-cover-toolbar").show(); // li标签里最后一个子元素div
	
	// 3.给其他li标签以及li里面标签添加class  注(不包括第一个li和最后一个li)
	$("#message-list li:not(:first)").addClass("list-group-item"); // 非第一个和最后一个所有其他li
	$("#message-list li:not(:first) > div:nth-child(2)").addClass("message-list-sub"); // li标签里第一个子元素div
	$("#message-list li:not(:first) > div:nth-child(2) > div:last-child > textarea:first-child").addClass("message-list-input"); // li标签里第一个子元素div下的第一个input标签
	$("#message-list li:not(:first) > div:nth-child(2) > div:first-child").show(); // li标签里第一个子元素div下的第一个div删除按钮显示
	$("#message-list li:not(:first) > div:nth-child(2) > div:nth-child(5)").addClass("message-list-input message-list-thumbnail"); //li标签里第一个子元素div下的最后一个图片div
	$("#message-list li:not(:first) > div:last-child").addClass("message-cover-toolbar sub-tool").hide(); // li标签里最后一个子元素div
}

function dragAndDrop() {
	
	if($("#message-list li").length>1) {
		$(".dragAndDrop").css({
			"display": "inline"
		})
		
		// 拖拽
	    $( "#message-list" ).sortable({
	    	cursor: "move",
	    	axis : "y",
	    	helper: "clone",
	    	opacity :  "0.6",
	    	distance: 20, // 多大距离开始拖动 px
	    	revert: true, // 拖拽的动画效果
	    	handle: ".dragAndDrop",
	//  	items: ".list-group-item:not(.message-add)", // 最后一个li不参与拖拽排序
	    	stop: function (event, ui) {  //拖拽结束后
	    		listInit()
	    		listStyleInit()
	    		dragAndDrop()
	    	}
	    });
	    $( "#message-list" ).disableSelection();
	}else {
		$(".dragAndDrop").css({
			"display": "none"
		})
	}
}

