/**
 *  主要封装处理投票选项设置所使用的方法
 */

$(function(){
	//初始化tab
	if(editFlag=="1"){//修改
		$("#votesetTab").attr("href","../createVote/editVote.do?action=editVote&voteID="+voteID+"&UUID="+uuid+"&vsOptionType="+voteType+"&addOrEdit=1"+"&DocIDs="+voteID+"&chooseNumType="+chooseType+"&siteID="+getUrlVars("siteID"));
		$("#voteoptionTab").attr("href","../voteOption/initOptions.do?action=initOptions&voteID="+voteID+"&UUID="+uuid+"&vsOptionType="+voteType+"&addOrEdit="+editFlag+"&DocIDs="+voteID+"&chooseNumType="+chooseType+"&siteID="+getUrlVars("siteID"));
		$("#votepublishTab").attr("href","javascript:void(0);");
		$("#pageconfigTab").attr("href","../headersImg/uploadadd.do?action=uploadadd&voteID="+voteID+"&UUID="+uuid+"&vsOptionType="+voteType+"&addOrEdit="+editFlag+"&DocIDs="+voteID+"&chooseNumType="+chooseType+"&siteID="+getUrlVars("siteID"));
		$("#checkvotePageTab").attr("href","javascript:void(0);");
	}else{//新增
		$("#votesetTab").attr("href","../createVote/editVote.do?action=editVote&voteID="+voteID+"&UUID="+uuid+"&vsOptionType="+voteType+"&addOrEdit=1"+"&DocIDs="+voteID+"&chooseNumType="+chooseType+"&siteID="+getUrlVars("siteID"));
		$("#voteoptionTab").attr("href","../voteOption/initOptions.do?action=initOptions&voteID="+voteID+"&UUID="+uuid+"&vsOptionType="+voteType+"&addOrEdit=1"+"&DocIDs="+voteID+"&chooseNumType="+chooseType+"&siteID="+getUrlVars("siteID"));
		$("#votepublishTab").attr("href","javascript:void(0);");
		//$(".votepublishTab").addClass("disabled");
		$("#pageconfigTab").attr("href","javascript:void(0);");
		//$(".pageconfigTab").addClass("disabled");
		$("#checkvotePageTab").attr("href","javascript:void(0);");
		//$(".checkvotePageTab").addClass("disabled");
	}
	// 下一步 按钮赋值
	//$("#nextpublish").attr("href","HeadersImg.do?action=uploadadd&voteID="+voteID+"&UUID="+uuid+"&vsOptionType="+voteType+"&addOrEdit="+editFlag+"&DocIDs="+voteID+"&chooseNumType="+chooseType);
	$(".nextpublish").click(function(){
		var isGONext = "";
		$("#votethemeforms table").each(function(){
			$voteoptionNums = $(this).find("input[id^='showThemeopNum_']").val();  //选项数
			$themechooseNums = $(this).find("input[id^='showMostChooseNum_']").val();  //最多选择数
			$themechooseNums_min = $(this).find("input[id^='showMinChooseNum_']").val();  //最少选择数
			$themeNum = $(this).find("span[id^='themeIndex_']").text();  //主题
			
			if($themechooseNums!="" && parseInt($voteoptionNums)<parseInt($themechooseNums)){   //最多选择数不为空，并且大于选项个数
				alert("主题"+$themeNum+"中，最多选择数应小于或者等于选项个数");
				isGONext = "false";
				return false;
			}else if($themechooseNums_min != '' && parseInt($voteoptionNums)<parseInt($themechooseNums_min)){  //最少选择数不为空，并且大于选项个数
				alert("主题"+$themeNum+"中，最少选择数应小于或者等于选项个数");
				isGONext = "false";
				return false;
			}else{
				isGONext = "true";
			}
		});
		if(isGONext == "true"){
			$(".nextpublish").attr("href","../headersImg/uploadadd.do?voteID="+voteID+"&UUID="+uuid+"&vsOptionType="+voteType+"&addOrEdit="+editFlag+"&DocIDs="+voteID+"&chooseNumType="+chooseType+"&siteID="+getUrlVars("siteID"));
		}
	});
	
	//初始化投票选项，获取选项数据
	if($("#optionslist #accordion").find("table").length==0){
		_opIsnullHtml = "<div class='alert alert-warning ' id='pleaseaddoptionline'><span>请通过上面添加选项</span></div>";
		$("#optionslist").prepend(_opIsnullHtml);
	}
	
	// 初始化投票主题
	if($("#votethemeforms").find("table").length==0){
		_themeIsNullHtml = "<div class='alert alert-warning ' id='pleaseaddvotetheme'><span>请通过下面添加投票主题</span></div>";
		$("#votethemeforms").prepend(_themeIsNullHtml);
	}else{
		$("#votethemeforms table").each(function(){
			$voteoptionNums = $(this).find("input[id^='showThemeopNum_']").val();
			$themechooseNums = $(this).find("input[id^='showMostChooseNum_']").val();
			if($themechooseNums!="-1" && parseInt($voteoptionNums)<parseInt($themechooseNums)){
				$("#votethemeforms #choosenumalertDiv").show();
				setTimeout(function(){
					$("#votethemeforms #choosenumalertDiv").hide();
				},5000);
			}
		});
	}
	//以下变量定义提示显示
	var error = "错误";
	//单击多行文字按钮，单行文本框变多行文本域
	$("#showmoretextinput").click(function(){
		if(moretextinput==0){ //此参数在jsp文本域中定义，初始值为0
			$("#showmoretextinput i").attr("class","icon-check");
			moretextinput = 1;
			$("#voteOptiontextarea").val($("#voteOptioninput").val());
			$("#voteOptiontextarea").show();
			$("#voteOptioninput").hide();
		}else{
			$("#showmoretextinput i").attr("class","icon-check-empty");
			moretextinput = 0;
			$("#voteOptioninput").val($("#voteOptiontextarea").val());
			$("#voteOptiontextarea").hide();
			$("#voteOptioninput").show();
		}
	})
	
	String.prototype.trim=function() {
        return this.replace(/(^\s*)|(\s*$)/g,'');
    }
	//点击添加选项按钮，在已有选项下方添加一行选项信息
	$("#addoption").click(function(){
		_newoptext = $("#voteOptioninput").val();
		if(moretextinput==1){
			_newoptext = $("#voteOptiontextarea").val();
		}
		// 投票选项内容为空，弹框提示
		if(_newoptext.trim() == ""){
			$("#voteOptioninputDiv").addClass("error");
			showAutoOpAlert(error,"请输入投票选项","error",0);
			return false;
		}else{
			$("#voteOptioninputDiv").removeClass("error");
			showHideopAlert();
		}
		_votethemeid = $("#themeidvaluehideinput").val();
		_themesIndex = $("#votethemeforms #themeIndex_"+_votethemeid).text(); 
		_existThemeDiv = false;
		
		if($("#optionslist #optionclasscontainer_"+_votethemeid).length>0){
			_existThemeDiv = true;
		}else{
			_existThemeDiv = false;
		}
		showAutoOpAlert("","正在保存，请稍候...","warning",0);
		// 以下为异步提交添加一条选项数据
		$.ajax({
			type:"POST",
			url:"addVoteOptionByOne.do",
			data:{action:'addVoteOptionByOne',voteoptext:_newoptext,voteid:voteID,themeid:_votethemeid},
			dataType:"json",
			success:function(data){
				if(data.ret =="1"){
					if(moretextinput==0)
						$("#voteOptioninput").val('');
					else
						$("#voteOptiontextarea").val('');
					
					showAutoOpAlert("","添加成功","success",2);
					
					var opinfo = data.retinfo.opinfo;
					
					if(!_existThemeDiv){
						_newopHtml  = "<div class='panel panel-info defaultsclass' id='tableContainer_"+_votethemeid+"'>";
						_newopHtml += "<div class='panel-heading'><h4 class='panel-title'>"+
    				                  "<a id='foldaclick_"+_votethemeid+"' data-toggle='collapse' data-parent='#accordion' href='#collapse_"+_votethemeid+"'>主题 "+_themesIndex+"</a></h4></div>";
						_newopHtml += "<div id='collapse_"+_votethemeid+"' class='panel-collapse collapse in'><div class='panel-body' id='optionclasscontainer_"+
									  _votethemeid+"'>";
					}else{
						_newopHtml = "";
					}
					
					_newopHtml += "<table class='table optable alert-success' id='table_"+opinfo.voteOpId+"'>";
					_newopHtml += "<tr><td class='span1'>"+opinfo.voIndex+".";
					
					if(caneditopindex){
						_newopHtml += "<br><a href='#' class='editoptionindexbtn' title='修改编号' onclick='javascript:editOptionIndex("+opinfo.voteOpId+
						");return false;'>修改</a>";
					}
					_newopHtml += "</td>";
					_newopHtml += "<td><div class='opcontent' id='opcontent_"+opinfo.voteOpId+"'><pre>"+opinfo.voName+"</pre></div>";
					_newopHtml += "<div class='opmedias' id='opmedias_"+opinfo.voteOpId+"'><div class='clear'></div></div>";
					_newopHtml += "<div class='opedits'>";
					_newopHtml += "<label class='control-label' for='optionGroup' style='width:45px'>别名</label>";
					_newopHtml += "<input type='text' name='optionGroup' id='optionGroup' placeholder='请输入别名' disabled='disabled'/>";
					_newopHtml += "<button class='btn btn-primary editclassname' id='editclassname' onclick='javascript:editOptionClass("+opinfo.voteOpId+");return false;'>修改</button>";
				    _newopHtml += "<a href='#' class='btn edittext' onclick='javascript:edit_optext("+opinfo.voteOpId+");return false;' >";
				    _newopHtml += "<i class='icon-edit' title='修改文字' style='width:12px;height:12px'></i>修改文字</a>";
				    _newopHtml += "<a href='#' class='btn addpicbtn ";
				    if(voteType=="0"){
				        _newopHtml +="disabled";
				    }
				    _newopHtml += "' onclick='javascript:addOptionImage("+opinfo.voteOpId+");return false;'>";
				    _newopHtml += "<i class='icon-picture' title='添加图片'></i>添加图片</a>"; 
				    _newopHtml += "<a href='#' class='btn addvideobtn ";
				    if(voteType=="0"||voteType=="1"){
				        _newopHtml +="disabled";
				    }
				    _newopHtml += "'onclick='javascript:addOpVideo("+opinfo.voteOpId+");return false;'><i class='icon-film' title='添加视频地址'></i>添加视频地址</a>";
				    _newopHtml += "<a href='#' class='btn addlinkurlbtn disabled' onclick='javascript:addOpLinkUrl("+opinfo.voteOpId+");return false;'><i class='icon-link' title='添加链接'></i>添加链接</a>";
				    _newopHtml += "<a href='#' class='btn addoppagetextbtn ";
				    if(voteType=="0"){
				        _newopHtml +="disabled";
				    }
				    _newopHtml += "' onclick='javascript:addOpPageText("+opinfo.voteOpId+");return false;'><i class='icon-file-alt' title='添加查看页'></i>添加查看页</a>";
				    _newopHtml += "<a href='#' class='btn btn-danger deleteoptionbtn' onclick='javascript:deleteTheOption("+opinfo.voteOpId+","+_votethemeid+");return false;'>";
				    _newopHtml += "<i class='icon-remove' title='删除选项'></i>删除选项</a>";
				    _newopHtml += "</div></td></tr></table>";
				    
				    if(!_existThemeDiv){
				    	_newopHtml += "</div></div></div>";
				    }
				    
				    // 在表单的最前方添加选项
				    if(!_existThemeDiv){
				    	$("#optionslist #accordion").prepend(_newopHtml);
				    }else{
				    	$("#accordion #optionclasscontainer_"+_votethemeid).prepend(_newopHtml);
				    }
				    // 添加完成后，修改主题里的选项个数
				    $("#votethemeforms #showThemeopNum_"+_votethemeid).attr("value",
				    		parseInt($("#votethemeforms #showThemeopNum_"+_votethemeid).val())+1);
				    addClaToThemeDelBtn(_votethemeid);
				    
				    $("#optionslist #pleaseaddoptionline").remove();
				    
				    setTimeout("removeTableSuccessStyle()",3000);            
				}else{
					showAutoOpAlert("",data.retinfo.errormsg,"error",5);
				}}
		});	
	});
	
	$('#modal').on('hidden', function () {
  	    $(this).removeData('modal');
  	});
	
   })

   //点击"修改文字"按钮
   function edit_optext(_opid){
	
	  _contentDiv = $("#opcontent_"+_opid);
	  if(_contentDiv.find("textarea").is("textarea")){
		  return false;
	  }
	  _content = _contentDiv.find("pre").html();
	  $("#temSaveOpcontent").attr("value",_content);
	  _editHtml  = "<div class='input-prepend'><textarea style='width:430px;height:80px;'>"+_content+"</textarea>";
	  _editHtml += "<button class='btn btn-primary' style='margin-left:5px;' onclick='javascript:save_edit_optext("+_opid+");return false;'>";
	  _editHtml += "<i class='icon-save'></i>保存</button><button class='btn' onclick='javascript:cancel_edit_optext("+_opid+");return false;'>";
	  _editHtml += "<i class='icon-reply' style='margin-left:5px;'></i>取消</button></div>";
	  _contentDiv.html(_editHtml);
	  $("#table_"+_opid).find(".edittext").addClass("disabled");
	}

    // 点击修改文字后保存
    function save_edit_optext(_opid){
    	_contentDiv = $("#opcontent_"+_opid);
    	if(_contentDiv.find("textarea").is("textarea")){
    		_newContentText = _contentDiv.find("textarea").val();
    		
    	    _alertHtml = getAlertDiv(_opid,"","正在保存，请稍候...","warning");
    		_contentDiv.after(_alertHtml);
    		removeAlertDiv(_opid);
    		$.ajax({
    			type:"POST",
    			url:"updatevoteoptiontext.do",
    			data:{action:'updatevoteoptiontext',voteoptext:_newContentText,opid:_opid},
    			dataType:"json",
    			success:function(data){
    				if(data.ret=="1"){
    					_alertHtml = getAlertDiv(_opid,"","保存成功","success");
    					_contentDiv.after(_alertHtml);
    					removeAlertDivByTime(_opid,2);
    					_contentDiv.html("<pre>"+data.retinfo.opinfo.voName+"</pre>");
    					$("#table_"+_opid).find(".edittext").removeClass("disabled");
    				}else{
    					_alertHtml = getAlertDiv(_opid,"",data.retinfo.errormsg,"error");
    					_contentDiv.append(_alertHtml);
    					removeAlertDivByTime(_opid,3);
    				}
    				$("#temSaveOpcontent").attr("value","");
    			}
    		});		
    	}
    }
    // 点击修改文字后取消
    function cancel_edit_optext(_opid){
    	_contentDiv = $("#opcontent_"+_opid);
    	if(_contentDiv.find("textarea").is("textarea")){
    		//_content = _contentDiv.find("textarea").val();
    		_contentDiv.html("<pre>"+$("#temSaveOpcontent").val()+"</pre>");
    		$("#table_"+_opid).find(".edittext").removeClass("disabled");
    	}else{
    		return false;
    	}
    }   
    
    // 点击修改编号，新建选项时，编号根据投票选项数自增，但是也可以手动修改
    function editOptionIndex(_opid){  
    	$("#temOpID").attr("value",_opid);
    	_url = "../vote/pophtml/PopEditOptionIndex.html?opid="+_opid;
    	var modal = $.scojs_modal({remote:_url,title:"修改编号"});
    	modal.show(); 	
    }
    
    // 删除选项
    function deleteTheOption(_opid,_themeid){
    	if($("#table_"+_opid).find(".deleteoptionbtn").hasClass("disabled")){
    		showOptionAlert(_opid,$("#table_"+_opid),
    		"不能删除一个有投票数据的选项，您可以到管理与统计中对这个选项进行作废操作","warning",3);
    		return false;
    	}
    	$("#table_"+_opid+" tr").addClass("error");
    	jConfirm("确定删除这个选项吗？删除后将无法恢复.","警告",function(r){
    		// 点击警告框的取消按钮，将移除样式，并不进行任何操作
    		if(!r){
    			$("#table_"+_opid+" tr").removeClass("error");
    			return false;
    		}
    		showOptionAlert(_opid,$("#table_"+_opid),"正在操作，请稍候...","warning",0);
    		$.ajax({
    			type:"POST",
    			url:"deleteVoteOption.do",
    			data:{action:'deletevoteoption',voteID: voteID,opid:_opid,themeid:_themeid,"operate" : 1},
    			dataType:"json",
    			success:function(data){
    				if(data.ret=="1"){
    					showOptionAlert(_opid,$("#opmedias_"+_opid),"删除成功","success",2);
    					_deleteOpIndex = $("#table_"+_opid).find(".opindextd").html();
    					
    					//_votethemeid = $("#optionslist #table_"+_opid).parents(".defaultsclass").find("input").attr("value");
    					
    					$("#votethemeforms #showThemeopNum_"+_themeid).attr("value",
    				    		parseInt($("#votethemeforms #showThemeopNum_"+_themeid).val())-1);
    				    addClaToThemeDelBtn(_themeid);
    				    
    				    if($("#tableContainer_"+_themeid).find("table").length>1){
    				    	$("#table_"+_opid).remove();
    				    }else{
    				    	$("#tableContainer_"+_themeid).remove();
    				    }
    					_deleteOpIndex = parseInt(_deleteOpIndex);
    					$(".opindextd").each(function(){
    						_theIndex = parseInt($(this).html());
    						if(_theIndex>_deleteOpIndex){
    							_theIndex = _theIndex-1;
    							$(this).html(_theIndex+".");
    						}
    					});
    					
    					if($("#optionslist #accordion").find("table").length==0){
    						_opIsnullHtml = "<div class='alert alert-warning ' id='pleaseaddoptionline'><span>请通过上面添加选项</span></div>";
    						$("#optionslist").prepend(_opIsnullHtml);
    					}
    				}else{
    					$("#table_"+_opid+" tr").removeClass("error");
    					showOptionAlert(_opid,$("#table_"+_opid),data.retinfo.errormsg,"error",2);
    				}
    			}
    		});
    	});
    }
    
    // 修改别名
    function editOptionClass(_opid){
    	$("#temOpID").attr("value",_opid);
    	_url = "../vote/pophtml/PopEditOptionClassName.html?opid="+_opid;
    	var modal = $.scojs_modal({remote:_url,title:"修改别名"});
    	modal.show(); 
    }
    
    // 上传图片后，显示在选项中
    function getOptionImageBox(opimgurl,_opid,_imageid){
    	_imgbox  = "<div class='opmediabox optionimage'>";
    	_imgbox += " <a href='"+opimgurl+"' target='_blank'><img class='uploadedimg img-polaroid' src='"+opimgurl+"' /></a>";
    	_imgbox += "<div class='deliconbtn imgdelbtn' onclick='javascript:delOptionImage("+_opid+","+_imageid
    	           +");return false;'><i title='删除图片' class='icon-remove'></i></div>";
    	_imgbox += "</div>";
    	return _imgbox;
    }
    
    // 删除图片
    function delOptionImage(_opid,_imageId){
    	$("#opmedias_"+_opid+" .optionimage img").addClass("dangerbox");
    	jConfirm("确定删除这张图片吗！","警告",function(r){
    		if(!r){
    			$("#opmedias_"+_opid+" .optionimage img").removeClass("dangerbox");
    			return false;
    		}
    		showOptionAlert(_opid,$("#opmedias_"+_opid),"正在操作，请稍候...","warning",0);
    		$.ajax({
    			type:"POST",
    			url:"delImg.do",
    			data:{action:'delImg',imgID:_imageId,viClassification:1},
    			dataType:"json",
    			success:function(data){
    				if(data.ret=="1"){
    					$("#opmedias_"+_opid+" .optionimage").remove();
    					$("#table_"+_opid).find(".addpicbtn").removeClass("disabled");
    					showOptionAlert(_opid,$("#opmedias_"+_opid),"删除成功","success",2);
    				}else{
    					$("#opmedias_"+_opid+" .optionimage img").removeClass("dangerbox");
    					showOptionAlert(_opid,$("#opmedias_"+_opid),data.retinfo.errormsg,"error",2);
    				}
    			}
    		});
    	});
    }

   //添加选项展示提醒框
    function showAutoOpAlert(title,msg,csstype,autohide){
	  _alertclass = "alert-success"; //成功
	  if(csstype == "error"){
		  _alertclass = "alert-error";
	  }
	  if(csstype == "warning"){
		  _alertclass = "alert-warning";
	  }
	  $("#autoopalertdiv h4").html(title);
	  $("#autoopalertdiv span").html(decodeURIComponent(msg));
	  $("#autoopalertdiv").attr("class","alert "+_alertclass);
	  $("#autoopalertdiv").show();
	  
	  // 多少秒后自动隐藏提示框
	  if(autohide>0){
		  setTimeout("showHideopAlert()",autohide*1000);
	  }
}
    
  //添加主题展示提醒框
    function showAutoThemeAlert(title,msg,csstype,autohide){
	  _alertclass = "alert-success"; //成功
	  if(csstype == "error"){
		  _alertclass = "alert-error";
	  }
	  if(csstype == "warning"){
		  _alertclass = "alert-warning";
	  }
	  $("#autothemealertdiv h4").html(title);
	  $("#autothemealertdiv span").html(decodeURIComponent(msg));
	  $("#autothemealertdiv").attr("class","alert "+_alertclass);
	  $("#autothemealertdiv").show();
	  
	  // 多少秒后自动隐藏提示框
	  if(autohide>0){
		  setTimeout("showHideThemeAlert()",autohide*1000);
	  }
}
    
    //隐藏选项提示框，清空内容
    function showHideopAlert(){
      $("#autoopalertdiv h4").html("");
      $("#autoopalertdiv span").html("");
      $("#autoopalertdiv").hide();
     }
    
  //隐藏主题提示框，清空内容
    function showHideThemeAlert(){
      $("#autothemealertdiv h4").html("");
      $("#autothemealertdiv span").html("");
      $("#autothemealertdiv").hide();
     }
    
    // 点击添加图片按钮，添加图片,参数为投票选项ID
    function addOptionImage(_opid){
    	// 如果选项类型为文字类型，则不能添加图片
    	if(voteType=="0" &&　$("#table_"+_opid).find(".addpicbtn").hasClass("disabled")){
    		_alertHtmlWord = getAlertDiv(_opid,"","文字类型的投票不能添加图片","error");
    		$("#opmedias_"+_opid).after(_alertHtmlWord);
    		removeAlertDivByTime(_opid,3);
    		return false;
    	}
    	//如果放置图片的div中有相关属性，则提示只能存一张图片
    	if($("#opmedias_"+_opid).find(".optionimage").is("div") || 
    			$("#table_"+_opid).find(".addpicbtn").hasClass("disabled")){
    		_alertHtml = getAlertDiv(_opid,"","每个选项只能添加一张图片","error");
    		$("#opmedias_"+_opid).after(_alertHtml);
    		removeAlertDivByTime(_opid,3);
    		return false;
    	}
    	$("#temOpID").attr("value",_opid);
    	_url = "../vote/pophtml/PopUploadImg.html";
    	var modal = $.scojs_modal({remote:_url,title:"添加图片"});
    	modal.show();
    }
    
    //生成提示的div
    function getAlertDiv(_alertid,title,msg,csstype){
    	_alertclass = "alert-success";
    	if(csstype =="error"){
    		_alertclass = "alert-error";
    	}
    	if(csstype =="warning"){
    		_alertclass = "alert-warning";
    	}
    	_alertdivhtml = "<div class='alert "+ _alertclass+"' id='opeditalertdiv_"+_alertid+"'>";
    	_alertdivhtml += "<h4>"+title+"</h4>";
    	_alertdivhtml += " <span>"+msg+"</span>";
    	_alertdivhtml += "</div>"; 
    	return _alertdivhtml;
    }
    
    //移除提醒所使用的div所用时间
    function removeAlertDivByTime(_alertid,sec){
    	setTimeout("removeAlertDiv("+_alertid+")",sec*1000);
    }
    
    //移除提醒所使用的div
    function removeAlertDiv(_alertid){
    	$("#opeditalertdiv_"+_alertid).remove();
    }
    // 添加成功后，移除table上样式
    function removeTableSuccessStyle(){
    	$("table.alert-success").removeClass("alert-success");
    }
    // 展示选项提示框
    function showOptionAlert(_opid,_contentDiv,msg,type,sec){
    	removeAlertDiv(_opid);
    	_alertHtml = getAlertDiv(_opid,"",msg,type);
    	_contentDiv.after(_alertHtml);
    	if(sec>0){
    		removeAlertDivByTime(_opid,sec);
    	}
    }
    
    // 添加投票主题，显示输入区域
    function addVoteThemes(){
    	$("#addthemeandoption").show();
    	if(chooseType==0){  //单选
    		$("#inputThemeChooseNumber").attr("disabled","disabled");
    		$("#inputThemeChooseNumber2").attr("disabled","disabled");
    	}else{  //多选
    		//判断选项是否可以为空 opIsNull：0（不能为空），1（可以为空）
    		if(opIsNull == 1){  //如果选项可以为空，则最少选择数置灰
    			$("#inputThemeChooseNumber2").attr("disabled","disabled");
    		}
    	}
    }
    
    // 点击重新输入，输入框清空
    function reInputTheme(){
        $("#inputTheme").attr("value","");
        $("#inputThemeNumber").attr("value","");
        $("#inputThemeChooseNumber").attr("value","");
        $("#inputThemeChooseNumber2").attr("value","");
        $("#inputTheme").focus();
    }
    
    // 取消编辑主题
    function cancelEditTheme(){
    	$("#inputTheme").attr("value","");
        $("#inputThemeNumber").attr("value","");
        $("#inputThemeChooseNumber").attr("value","");
        $("#inputThemeChooseNumber2").attr("value","");
        //$("#themeidvaluehideinput").attr("value","0");
        $("#addthemeandoption").hide();
        if(addoredittheme == 1){
        	$("#votethemeforms table").each(function(){
        		$(this).find(".edittheme").removeClass("disabled");
        	});
        }
    }
    
    // 点击保存主题，执行保存
    function addVoteThemesAndSave(_voteid){
    	_themeid = $("#themeidvaluehideinput").val();
    	_themeContent = $("#inputTheme").val();
    	_saveafterid = "";
    	if(_themeContent==""){
    		$("#themehelp").html("必填项，请输入投票主题");
    		return false;
    	}else{
    		$("#themehelp").html("");
    	}
    	_maxChooseNum = $("#inputThemeChooseNumber").val();
    	if(_maxChooseNum==""&&$("#inputThemeChooseNumber").attr("disabled")!="disabled"){
    		$("#themechoosenumberhelp").html("必填项，请输入最多选项数");
    		return false;
    	}else{
    		$("#themechoosenumberhelp").html("");
    	}
    	_themeNumber = $("#inputThemeNumber").val();
    	if(_themeNumber!=""){
    		if((!/^(\+|-)?\d+$/.test(_themeNumber)) || _themeNumber<=0|| _themeNumber>=100000){
    			$("#themenumberhelp").html("选填项，请输入一个1-5位正整数，若不填写，则使用系统默认值");
    			return false;
    		}else{
    			$("#themenumberhelp").html("");	
    		}
    	}else{
    		$("#themenumberhelp").html("");
    	}
    	// 最少选择几项
    	_themeChooseNumber2 = $("#inputThemeChooseNumber2").val();
    	if(_themeChooseNumber2!=""){
    		if((!/^(\+|-)?\d+$/.test(_themeChooseNumber2)) || _themeChooseNumber2<=0){
    			$("#themechoosenumberhelp2").html("选填项，请输入一个大于0的整数，若不填写，则使用系统默认值");
    			return false;
    		}else{
    			$("#themechoosenumberhelp2").html("");	
    		}
    	}else{
    		$("#themechoosenumberhelp2").html("");
    	}
    	// 最多选择几项
    	_themeChooseNumber = $("#inputThemeChooseNumber").val();
    	if(_themeChooseNumber!=""){
    		if((!/^(\+|-)?\d+$/.test(_themeChooseNumber)) || _themeChooseNumber<=0){
    			$("#themechoosenumberhelp").html("必填项，请输入一个大于0的整数");
    			return false;
    		}else{
    			$("#themechoosenumberhelp").html("");	
    		}
    	}else{
    		$("#themechoosenumberhelp").html("");
    	}
    	
    	if(_themeChooseNumber2 != "" && _themeChooseNumber != ""){   //最少选择数和最多选择数都不为空值
    		var min_choose_num = Number(_themeChooseNumber2);
    		var max_choose_num = Number(_themeChooseNumber);
    		if(min_choose_num > max_choose_num){   //如果填入的最小选择数大于最大选择数，则不能保存
    			alert("最小选几项的值不能大于最多选几项的值，请重新输入");
    			return false;
    		}
    	}
    	
    	showAutoThemeAlert("","正在保存，请稍候...","warning",0);
    	if(addoredittheme==0){
    	// 开始异步添加主题
    	$.ajax({
			type:"POST",
			url:"addOneTheme.do",
			data:{action:'addOneTheme',votethemetext:_themeContent,themeindex:_themeNumber,voteid:_voteid,
				 choosetype:chooseType,choosenumbers:_themeChooseNumber,minchoosenumbers:_themeChooseNumber2},
			dataType:"json",
			async:false,
			success:function(data){
				if(data.ret =="1"){
					showAutoThemeAlert("","添加成功","success",2);
					var themeinfos = data.retinfo.themeinfo;
					_saveafterid = themeinfos.themeId;
					
					_newthemeHtml  = "<table class='themetable alert-success' id='themetables_"+themeinfos.themeId+"'><tbody><tr><td class='themecontent'>";
					_newthemeHtml += "<span id='spanContent_"+themeinfos.themeId+"'><label class='control-label themeindexlist'>主题 <span id='themeIndex_"+themeinfos.themeId+
					                 "'>"+themeinfos.themeIndex+"</span></label>";
					_newthemeHtml += "<div class='controls themecontentlist'><input type='text' name='showThemecontent_"+themeinfos.themeId+
					                 "' id='showThemecontent_"+themeinfos.themeId+"' value='"+themeinfos.themeName+"' readonly='true'></div></span></td>";
					_newthemeHtml += "<td class='optionnumber' style='width:150px;'><span id='spanOptionnumber_"+themeinfos.themeId+
					                 "'><label class='control-label optionnumberlist'>选项数:</label>";
					_newthemeHtml += "<div class='controls optionnumbersDiv'><input type='text' name='showThemeopNum_"+themeinfos.themeId+
					                 "' id='showThemeopNum_"+themeinfos.themeId+"' value='"+themeinfos.optionNums+"' readonly='true'></div></span></td>";
					
					_newthemeHtml += "<td class='choosenumber' style='width:150px;'><span id='spanchoosenumber2_"+themeinfos.themeId+
    				 "'><label class='control-label choosenumberlist'>最少选择数:</label>";
				    _newthemeHtml += "<div class='controls choosenumbersDiv'><input type='text' name='showMinChooseNum_"+themeinfos.themeId+
				    				 "' id='showMinChooseNum_"+themeinfos.themeId;
				    if(themeinfos.minChooseNums==-1){
				   	 _newthemeHtml += "' value='";
				    }else{
				   	 _newthemeHtml += "' value='"+themeinfos.minChooseNums;
				    }
				    _newthemeHtml += "' readonly='true'></div></span></td>";
					
	                _newthemeHtml += "<td class='choosenumber' style='width:150px;'><span id='spanchoosenumber_"+themeinfos.themeId+
	                 				 "'><label class='control-label choosenumberlist'>最多选择数:</label>";
	                _newthemeHtml += "<div class='controls choosenumbersDiv'><input type='text' name='showMostChooseNum_"+themeinfos.themeId+
	                 				 "' id='showMostChooseNum_"+themeinfos.themeId;
	                if(themeinfos.mostChooseNums==-1){
	                	_newthemeHtml += "' value='";
	                }else{
	                	_newthemeHtml += "' value='"+themeinfos.mostChooseNums;
	                }
	                _newthemeHtml += "' readonly='true'></div></span></td>";	
					_newthemeHtml += "<td class='themeoperation'>";
					_newthemeHtml += "<a href='#' class='btn editaddoption' onclick='javascript:addOptionToTheme("+themeinfos.themeId+");return false;'>"+
			                         "<i class='icon-plus' title='添加选项'></i>添加选项</a>";
					_newthemeHtml += "<a href='#' class='btn edittheme' onclick='javascript:edit_theme("+themeinfos.themeId+");return false;'>"+
			                         "<i class='icon-edit' title='修改主题'></i>修改</a>";
					_newthemeHtml += "<a href='#' class='btn btn-danger deletethemebtn' onclick='javascript:deleteTheTheme("+themeinfos.themeId+");return false;'>"+
			                         "<i class='icon-remove' title='删除主题'></i>删除</a>";
					_newthemeHtml += "</td></tr></tbody></table>";
				    
				    // 在表单的最前方添加选项
				    $("#votethemeforms").append(_newthemeHtml);
				    $(".votethemelist #pleaseaddvotetheme").remove();
				    cancelEditTheme();
				    setTimeout("removeTableSuccessStyle()",3000);            
				}else{
					showAutoThemeAlert("",data.retinfo.errormsg,"error",5);
				}}
		});
    	}else{
    		// 开始异步修改主题
        	$.ajax({
    			type:"POST",
    			url:"eidtOneTheme.do",
    			data:{action:'eidtOneTheme',votethemetext:_themeContent,themeindex:_themeNumber,themeid:_themeid,voteid:_voteid,
    				choosetype:chooseType,choosenumbers:_themeChooseNumber,minchoosenumbers:_themeChooseNumber2},
    			dataType:"json",
    			async:false,
    			success:function(data){
    				if(data.ret =="1"){
    					showAutoThemeAlert("","修改成功","success",3);
    					var themeinfos = data.retinfo.themeinfo;
    					_saveafterid = themeinfos.themeId;
    					$(".votethemelist #themeIndex_"+_themeid).text(themeinfos.themeIndex);
    					$(".votethemelist #showThemecontent_"+_themeid).attr("value",themeinfos.themeName);
    					if(themeinfos.mostChooseNums==-1){
    						$(".votethemelist #showMostChooseNum_"+_themeid).attr("value","");
    					}else{
    						$(".votethemelist #showMostChooseNum_"+_themeid).attr("value",themeinfos.mostChooseNums);
    					}
    					//最少选择数
    					if(themeinfos.minChooseNums==-1){
    						$(".votethemelist #showMinChooseNum_"+_themeid).attr("value","");
    					}else{
    						$(".votethemelist #showMinChooseNum_"+_themeid).attr("value",themeinfos.minChooseNums);
    					}
    					
    				    cancelEditTheme();
    				    if($("#accordion #tableContainer_"+_saveafterid).is("div")){
    				    	$("#accordion #tableContainer_"+_saveafterid+" #foldaclick_"+_saveafterid).text("主题 "+themeinfos.themeIndex);
    				    }
    				    addoredittheme=0;
    				    setTimeout("removeTableSuccessStyle()",3000);            
    				}else{
    					showAutoThemeAlert("",data.retinfo.errormsg,"error",5);
    				}}
    		});
    	}
    	addClaToThemeDelBtn(_saveafterid);
    }
    
  //主题下添加选项时，生成提示的div
    function getThemeAlertDiv(_alertid,title,msg,csstype){
    	_alertclass = "alert-success";
    	if(csstype =="error"){
    		_alertclass = "alert-error";
    	}
    	if(csstype =="warning"){
    		_alertclass = "alert-warning";
    	}
    	_alertdivhtml = "<div class='alert "+ _alertclass+"' id='themeeditalertdiv_"+_alertid+"'>";
    	_alertdivhtml += "<h4>"+title+"</h4>";
    	_alertdivhtml += " <span>"+msg+"</span>";
    	_alertdivhtml += "</div>"; 
    	return _alertdivhtml;
    }
    
    //移除提醒所使用的div所用时间
    function removeThemeAlertDivByTime(_alertid,sec){
    	setTimeout("removeThemeAlertDiv("+_alertid+")",sec*1000);
    }
    
    //移除提醒所使用的div
    function removeThemeAlertDiv(_alertid){
    	$("#themeeditalertdiv_"+_alertid).remove();
    }
    
    function showThemeAlert(_themeid,_contentDiv,msg,type,sec){
    	removeThemeAlertDiv(_themeid);
    	_alertHtml = getThemeAlertDiv(_themeid,"",msg,type);
    	_contentDiv.after(_alertHtml);
    	if(sec>0){
    		removeThemeAlertDivByTime(_themeid,sec);
    	}
    }
    
    // 主题下添加选项
    function addOptionToTheme(_themeid){
    	if($("#votethemeforms #themetables_"+_themeid).find(".editaddoption").hasClass("disabled")){
    		_alertHtml = getThemeAlertDiv(_themeid,"","选项已在添加状态，请先取消","error");
    		$("#votethemeforms").after(_alertHtml);
    		removeThemeAlertDivByTime(_themeid,3);
    		return false;
    	}
    	$("#themeidvaluehideinput").attr("value",_themeid);
    	// 添加选项区域显示
    	$("#addoptionshowDiv").show();
    	// 主题添加按钮全部置灰
    	$("#votethemeforms table").each(function(){
    		$(this).find(".editaddoption").addClass("disabled");
    	});
    }
    
    // 修改主题
    function edit_theme(_themeid){
    	
    	if($("#votethemeforms #themetables_"+_themeid).find(".edittheme").hasClass("disabled")){
    		_alertHtml = getThemeAlertDiv(_themeid,"","已有主题正在编辑中，请先取消","error");
    		$("#votethemeforms").after(_alertHtml);
    		removeThemeAlertDivByTime(_themeid,3);
    		return false;
    	}
    	if(chooseType==0){  //单选
    		$("#inputThemeChooseNumber").attr("disabled","disabled");
    		$("#inputThemeChooseNumber2").attr("disabled","disabled");
    	}else{  //多选
    		//判断选项是否可以为空 opIsNull：0（不能为空），1（可以为空）
    		if(opIsNull == 1){  //如果选项可以为空，则最少选择数置灰
    			$("#inputThemeChooseNumber2").attr("disabled","disabled");
    		}else{
    			$("#inputThemeChooseNumber").removeAttr("disabled");
        		$("#inputThemeChooseNumber2").removeAttr("disabled");
    		}
    	}
    	$("#themeidvaluehideinput").attr("value",_themeid);
    	$("#inputTheme").attr("value",$(".votethemelist #showThemecontent_"+_themeid).val());
    	$("#inputThemeNumber").attr("value",$(".votethemelist #themeIndex_"+_themeid).text());
    	$("#inputThemeChooseNumber").attr("value",$(".votethemelist #showMostChooseNum_"+_themeid).val());
    	//最少选择项
    	$("#inputThemeChooseNumber2").attr("value",$(".votethemelist #showMinChooseNum_"+_themeid).val());
    	// 添加选项区域显示
    	$("#addthemeandoption").show();
    	// 主题添加按钮全部置灰
    	addoredittheme = 1;
    	$("#votethemeforms table").each(function(){
    		$(this).find(".edittheme").addClass("disabled");
    	});
    }
    
    // 删除一个主题
    function deleteTheTheme(_themeid){
    	if($("#votethemeforms #themetables_"+_themeid).find(".deletethemebtn").hasClass("disabled")){
    		showThemeAlert(_themeid,$("#themetables_"+_themeid),
    		"不能删除一个有投票选项的主题，您可以到先删除该主题下全部选项","warning",3);
    		return false;
    	}
    	$("#themetables_"+_themeid+" tr").addClass("alert-error");
    	jConfirm("确定删除这个主题吗？删除后将无法恢复.","警告",function(r){
    		// 点击警告框的取消按钮，将移除样式，并不进行任何操作
    		if(!r){
    			$("#themetables_"+_themeid+" tr").removeClass("error");
    			return false;
    		}
    		showThemeAlert(_themeid,$("#themetables_"+_themeid),"正在操作，请稍候...","warning",0);
    		$.ajax({
    			type:"POST",
    			url:"deleteVoteTheme.do",
    			data:{action:'deletevotetheme',themeid:_themeid},
    			dataType:"json",
    			success:function(data){
    				if(data.ret=="1"){
    					showThemeAlert(_themeid,$("#themetables_"+_themeid),"删除成功","success",2);
    					_deleteThemeIndex = $("#themetables_"+_themeid+" #themeIndex_"+_themeid).text();
    					$("#themetables_"+_themeid).remove();
    					_deleteThemeIndex = parseInt(_deleteThemeIndex);
    					$("#votethemeforms span[id^='themeIndex_']").each(function(){
    						_theIndex = parseInt($(this).text());
    						if(_theIndex>_deleteThemeIndex){
    							_theIndex = _theIndex-1;
    							$(this).text(_theIndex);
    						}
    					});
    					
    					if($("#votethemeforms").find("table").length==0){
    						_themeIsNullHtml = "<div class='alert alert-warning ' id='pleaseaddvotetheme'><span>请通过下面添加投票主题</span></div>";
    						$("#votethemeforms").prepend(_themeIsNullHtml);
    					}
    				}else{
    					$("#themetables_"+_themeid+" tr").removeClass("alert-error");
    					showThemeAlert(_themeid,$("#themetables_"+_themeid),data.retinfo.errormsg,"error",2);
    				}
    			}
    		});
    	});
    }
    
    // 选项添加完成
    function doneEditOptions(){
    	if(moretextinput==0)
			$("#voteOptioninput").val('');
		else
			$("#voteOptiontextarea").val('');
    	
    	$("#votethemeforms table").each(function(){
    		$(this).find(".editaddoption").removeClass("disabled");
    	});
    	$("#themeidvaluehideinput").attr("value",0);
    	$("#addoptionshowDiv").hide();
    }
    
    // 取消添加选项
    function cancelEditOptions(){
    	doneEditOptions();
    }
    
    // 判断选项数，如果该主题的选项数大于0，则删除按钮添加disabled属性，否则，去掉disabled
    function addClaToThemeDelBtn(_themeid){
    	_optionNums = parseInt($("#votethemeforms #showThemeopNum_"+_themeid).val());
    	if(_optionNums>0){
    		$("#themetables_"+_themeid+" .deletethemebtn").addClass("disabled");
    		$("#votethemeforms #showThemeopNum_"+_themeid).css("color","black");
    	}else{
    		$("#themetables_"+_themeid).find(".deletethemebtn").removeClass("disabled");
    		$("#votethemeforms #showThemeopNum_"+_themeid).css("color","red");
    	}
    }
    // 添加视频页
    function addOpVideo(_opid){
    	// 如果选项类型为文字类型，则不能添加视频
    	if(voteType=="0" &&　$("#table_"+_opid).find(".addvideobtn").hasClass("disabled")){
    		_alertHtmlWord = getAlertDiv(_opid,"","文字类型的投票不能添加视频","error");
    		$("#opmedias_"+_opid).after(_alertHtmlWord);
    		removeAlertDivByTime(_opid,3);
    		return false;
    	}
    	// 如果选项类型为图片类型，则不能添加视频
    	if(voteType=="1" &&　$("#table_"+_opid).find(".addvideobtn").hasClass("disabled")){
    		_alertHtmlWord = getAlertDiv(_opid,"","图片类型的投票不能添加视频","error");
    		$("#opmedias_"+_opid).after(_alertHtmlWord);
    		removeAlertDivByTime(_opid,3);
    		return false;
    	}
    	//如果放置视频的div中有相关图标，则提示只能存一个视频
    	if($("#opmedias_"+_opid).find(".optionvideo").is("div") || 
    			$("#table_"+_opid).find(".addvideobtn").hasClass("disabled")){
    		_alertHtml = getAlertDiv(_opid,"","每个选项只能添加一个视频","error");
    		$("#opmedias_"+_opid).after(_alertHtml);
    		removeAlertDivByTime(_opid,3);
    		return false;
    	}
    	$("#temOpID").attr("value",_opid);
    	_url = "../vote/pophtml/PopAddVideo.html?opid="+_opid;
    	var modal = $.scojs_modal({remote:_url,title:"添加视频地址"});
    	modal.show();
    	
    }
 // 生成一个视频查看box
    function getOptionVideoBox(_opid){
    	_pagetextbox = "<div class='opmediabox optionvideo'>";
    	_pagetextbox += "<a href='javascript:void(0);' onclick='javascript:checkOptionVideo("+_opid+");return false;'><i class='icon-film'></i></a>";
    	_pagetextbox += "<div class='deliconbtn pagetextdelbtn' onclick='javascript:delOptionVideo("+_opid+");return false;' ><i title='删除选项视频' class='icon-remove'></i> </div>";
    	_pagetextbox += "</div>";
    	return _pagetextbox;
    }
 // 删除视频
    function delOptionVideo(_opid){
    	$("#opmedias_"+_opid+" .optionvideo i").addClass("dangerbox");
    	jConfirm("确定删除这段视频吗！","警告",function(r){
    		if(!r){
    			$("#opmedias_"+_opid+" .optionvideo i").removeClass("dangerbox");
    			return false;
    		}
    		showOptionAlert(_opid,$("#opmedias_"+_opid),"正在操作，请稍候...","warning",0);
    		$.ajax({
    			type:"POST",
    			url:"delVideoAddr.do",
    			data:{action:'delVideoAddr',opid:_opid},
    			dataType:"json",
    			success:function(data){
    				if(data.ret=="1"){
    					$("#opmedias_"+_opid+" .optionvideo").remove();
    					$("#table_"+_opid).find(".addvideobtn").removeClass("disabled");
    					showOptionAlert(_opid,$("#opmedias_"+_opid),"删除成功","success",2);
    				}else{
    					$("#opmedias_"+_opid+" .optionvideo i").removeClass("dangerbox");
    					showOptionAlert(_opid,$("#opmedias_"+_opid),data.retinfo.errormsg,"error",2);
    				}
    			}
    		});
    	});
    }
    function addOpLinkUrl(_opid){
    	_alertHtmlWord = getAlertDiv(_opid,"","暂不能添加选项链接","error");
		$("#opmedias_"+_opid).after(_alertHtmlWord);
		removeAlertDivByTime(_opid,3);
		return false;
    }
    // 添加查看页
    function addOpPageText(_opid){
    	// 如果选项类型为文字类型，则不能添加图片
    	if(voteType=="0" &&　$("#table_"+_opid).find(".addoppagetextbtn").hasClass("disabled")){
    		_alertHtmlWord = getAlertDiv(_opid,"","文字类型的投票不能添加查看页","error");
    		$("#opmedias_"+_opid).after(_alertHtmlWord);
    		removeAlertDivByTime(_opid,3);
    		return false;
    	}
    	$("#temOpID").attr("value",_opid);
    	_url = "../vote/pophtml/Popaddoppagetext.html?opid="+_opid;
    	var modal = $.scojs_modal({remote:_url,title:"添加选项查看页内容"});
    	modal.show();
    	
    }
    
    // 点击查看页图标查看内容
    function checkViewPageContent(_opid){
    	$("#temOpID").attr("value",_opid);
    	_url = "../vote/pophtml/PopCheckViewPage.html?opid="+_opid;
    	var modal = $.scojs_modal({remote:_url,title:"查看页内容"});
    	modal.show();
    }
    // 点击视频图标查看选项视频
    function checkOptionVideo(_opid){
    	$("#temOpID").attr("value",_opid);
    	_url = "../vote/pophtml/PopCheckVideoPage.html?opid="+_opid;
    	var modal = $.scojs_modal({remote:_url,title:"查看选项视频"});
    	modal.show();
    }
    // 生成一个选项查看页box
    function getOptionPageTextBox(_opid){
    	_pagetextbox = "<div class='opmediabox optionpagetext'>";
    	_pagetextbox += "<a href='javascript:void(0);' onclick='javascript:checkViewPageContent("+_opid+");return false;'><i class='icon-file-alt'></i></a>";
    	_pagetextbox += "<div class='deliconbtn pagetextdelbtn' onclick='javascript:delOptionPageText("+_opid+");return false;' ><i title='删除选项查看页' class='icon-remove'></i> </div>";
    	_pagetextbox += "</div>";
    	return _pagetextbox;
    }
    
    // 删除查看页，相当于清除查看页内容
    function delOptionPageText(_opid){
    	$("#opmedias_"+_opid+" .optionpagetext i").addClass("dangerbox");
    	jConfirm("确认删除这个查看文字吗","提示",function(r){
    		if(!r){
    			$("#opmedias_"+_opid+" .optionpagetext i").removeClass("dangerbox");
    			return false;
    		}
    		showOptionAlert(_opid,$("#opmedias_"+_opid),"正在操作，请稍候...",'warning',0);
    		
    		$.ajax({
     			type:"POST",
      			url:"editOptionPageText.do",
      			data:{action:"editoptionpagetext",opid:_opid,pagetextcontent:"",showimgonpage:1},
      			dataType:"json",
      			success:function(data){
      				if(data.ret=="1"){
      					 $("#opmedias_"+_opid+" .optionpagetext").remove();
      		        	 
      		        	 $("#table_"+_opid).find(".addoppagetextbtn").html("<i class='icon-file-alt' title='添加查看页'></i>添加查看页");
      		        	 showOptionAlert(_opid,$("#opmedias_"+_opid),"删除成功",'success',2);
      				}else{
      					$("#opmedias_"+_opid+" .optionpagetext i").removeClass("dangerbox");
      		        	 showOptionAlert(_opid,$("#opmedias_"+_opid),data.retinfo.errormsg,'error',2);
      				}
      			}
     		 });
    	});
    }
    
  //获取url地址中的参数
    function getUrlVars(name){
    	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg);  //匹配目标参数
        if (r != null) return unescape(r[2]); return null; //返回参数值
    }
  