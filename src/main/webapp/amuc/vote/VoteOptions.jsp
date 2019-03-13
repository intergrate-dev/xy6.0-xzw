<%@ page pageEncoding = "UTF-8"%>
<%@page import="com.founder.amuc.commons.FlowProcHelper"%>
<%@page import="com.founder.e5.context.Context"%>
<%@page import="com.founder.e5.dom.DocLibReader"%>
<%@page import="com.founder.e5.web.WebUtil"%>
<%@page import="com.founder.amuc.commons.FlowProcHelper"%>
<%
	DocLibReader libReader = (DocLibReader)Context.getBean(DocLibReader.class);
	int docLibID = FlowProcHelper.getInt(request.getParameter("DocLibID"));
	int docID = FlowProcHelper.getInt(request.getParameter("DocIDs"));
	
	String vsOptionType = request.getParameter("vsOptionType");
	String vsOptionIsNull = request.getParameter("vsOptionIsNull");
	String voteId = request.getParameter("voteID");
	String UUID = request.getParameter("UUID");
	String addOrEdit = request.getParameter("addOrEdit");
	String vsTypes = request.getParameter("chooseNumType");
	String webRootURL = WebUtil.getRoot(request);
	
%>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<html>
<head>
    <title>投票选项</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	
	<link type="text/css" rel="stylesheet" href="../script/lhgcalendar/lhgcalendar.bootstrap.css"/>
    <link type="text/css" rel="stylesheet" href="../script/bootstrap/css/bootstrap.min.css"/>
    <link type="text/css" rel="stylesheet" href="../vote/css/font-awesome.min.css"/>
	<link type="text/css" rel="stylesheet" href="../vote/css/jquery.alerts.css"/>
	<link type="text/css" rel="stylesheet" href="../vote/css/voteoptions.css"/>
	
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script> 
	<script type="text/javascript" src="../../amuc/script/lhgcalendar/lhgcalendar.js"></script>
	<script type="text/javascript" src="../../e5script/e5.min.js"></script>
	<script type="text/javascript" src="../../e5script/e5.utils.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
    
    <!-- 
	<script type="text/javascript" src="../../e5script/jquery/jquery-ui/jquery.ui.core.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/jquery-ui/jquery.ui.widget.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/jquery-ui/jquery.ui.button.js"></script>
	 -->
	<script type="text/javascript" src="../script/bootstrap/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../vote/js/sco.modal.js"></script>
	<script type="text/javascript" src="../vote/js/jquery.draggable.js"></script>
	<script type="text/javascript" src="../vote/js/jquery.alerts.js"></script>
	<script type="text/javascript" src="../vote/js/jquery.jalert.js"></script>
	<script type="text/javascript" src="../ueditor/ueditor.config.js"></script>
<script type="text/javascript" src="../ueditor/ueditor.all.js"></script>

	<script type="text/javascript">
	     var rootURL = '<%=webRootURL %>';
    </script> 
	<script type="text/javascript">
	     var uuid = "<%=UUID %>";
	     var docLibID = "<%=docLibID %>";
	     var docID = "<%=docID %>";
	     var voteID = "<%=voteId %>";
	     var voteType = "<%=vsOptionType %>";
 	     var editFlag = "<%=addOrEdit %>";
 	     var chooseType = "<%=vsTypes%>";
 	  	 //选项是否能够为空值 0 （不能为空）,1 （可以为空）
 	     var opIsNull = "<%=vsOptionIsNull%>";
	     var caneditopindex = true;
	     var themesList = "<c:out value="${themelist}"/>"
	</script>
</head> 
<body>
    <div class="container bodycontainer">
		<form id="form" name="form" method="post" target="iframe" action="" enctype="multipart/form-data">
			<!-- 投票选项新增表单 add by libin 2015-12-29 -->
		  <div class="row-fluid">
		   <div class="span1">
			 <ul class="nav nav-tabs" id="optionTab">
			   <li class="votesetTab"><a href="" id="votesetTab"><i class="icon-edit"></i>投票基本设置</a></li>
			   <li class="active voteoptionTab"><a id="voteoptionTab" href=""><i class="icon-tasks"></i>投票选项</a></li>
			   <!-- <li class="votepublishTab"><a href="" id="votepublishTab"><i class="icon-cogs"></i>发布设置</a></li> -->
			   <li class="pageconfigTab"><a href="" id="pageconfigTab"><i class="icon-magic"></i>页面配置</a></li>
			  <!--  <li class="checkvotePageTab"><a href="" id="checkvotePageTab"><i class="icon-external-link"></i>查看投票页</a></li> -->
			 </ul>
			 	<div class="control-group" style="margin-bottom: 20px;margin-top:20px;">
			       <a href="" class="btn btn-primary nextpublish" id="nextpublish1">下一步</a>
			   </div>
			    <h4>添加主题和选项</h4>
			    <div class="alert" style="margin-bottom:10px;">
			                    推荐使用  <font color="red">谷歌浏览器</font> 进行投票编辑创建操作，以确保所有功能的正常使用，谢谢。
			    </div>
			   <form class="form-horizontal" id="temvotethemeform" method="post" action="" onsubmit="return false" autocomplete="off">
			    </form>
			    
			    <div class="votethemelist">
			        <legend style="font-size:16px;">已有投票主题</legend>
			        <form class="form-horizontal" id="votethemeforms" method="post" action="" onsubmit="return false" autocomplete="off">
			          
			         <c:forEach var="vt" items="${themelist}">
			          <table class="themetable" id="themetables_${vt.themeId }">
			             <tbody>
			               <tr>
			                 <td class="themecontent">
			                   <span id="spanContent_${vt.themeId }">
			                     <label class="control-label themeindexlist"><span>主题 </span><span id="themeIndex_${vt.themeId}">${vt.themeIndex }</span></label>
			                     <div class="controls themecontentlist">
			                        <input type="text" name="showThemecontent_${vt.themeId }" id="showThemecontent_${vt.themeId }" value="${vt.themeName }" readonly="true">
			                     </div>
			                   </span>
			                 </td>
			                 <td class="optionnumber" style="width:150px;">
			                   <span id="spanOptionnumber_${vt.themeId}">
			                     <label class="control-label optionnumberlist">选项数:</label>
			                     <div class="controls optionnumbersDiv">
			                        <input type="text" name="showThemeopNum_${vt.themeId }" id="showThemeopNum_${vt.themeId }" 
			                                         value="${vt.optionNums }" readonly="true"
			                                          <c:if test="${vt.optionNums==0 }">
			                                         style="color:red;"</c:if> />                
			                     </div>
			                   </span>
			                 </td>
			                 <td class="choosenumber" style="width:150px;">
			                   <span id="spanchoosenumber2_${vt.themeId}">
			                     <label class="control-label choosenumberlist">最少选择数:</label>
			                     <div class="controls choosenumbersDiv">
			                        <input type="text" name="showMinChooseNum_${vt.themeId }" id="showMinChooseNum_${vt.themeId }" 
			                              <c:choose>  
							                 <c:when test="${vt.minChooseNums==-1}">
							                      value=""
							                 </c:when>
							                 <c:otherwise>
							                      value="${vt.minChooseNums }"  
							                 </c:otherwise>
							              </c:choose> readonly="true"/>                
			                     </div>
			                   </span>
			                 </td>
			                 <td class="choosenumber" style="width:150px;">
			                   <span id="spanchoosenumber_${vt.themeId}">
			                     <label class="control-label choosenumberlist">最多选择数:</label>
			                     <div class="controls choosenumbersDiv">
			                        <input type="text" name="showMostChooseNum_${vt.themeId }" id="showMostChooseNum_${vt.themeId }" 
			                              <c:choose>  
							                 <c:when test="${vt.mostChooseNums==-1}">
							                      value=""
							                 </c:when>
							                 <c:otherwise>
							                      value="${vt.mostChooseNums }"  
							                 </c:otherwise>
							              </c:choose> readonly="true"/>                
			                     </div>
			                   </span>
			                 </td>
			                 <td class="themeoperation">
			                    <a href="#" class="btn editaddoption" onclick="javascript:addOptionToTheme(${vt.themeId });return false;">
			                     <i class="icon-plus" title="添加选项"></i>添加选项
			                   </a>
			                    <a href="#" class="btn edittheme" onclick="javascript:edit_theme(${vt.themeId });return false;">
			                     <i class="icon-edit" title="修改主题"></i>修改
			                   </a>
			                   <a href="#" class="btn btn-danger deletethemebtn <c:if test="${vt.optionNums>0 }">disabled</c:if>" onclick="javascript:deleteTheTheme(${vt.themeId });return false;">
			                     <i class="icon-remove" title="删除主题"></i>删除
			                   </a>
			                 </td>
			               </tr>
			             </tbody>
			          </table>
			          </c:forEach>
			          <div class="alert alert-warning" id="choosenumalertDiv" style="display: none;">
			            <h4></h4><span>提醒：最多选择个数不能多于该主题下的选项个数</span>
			          </div>
			        </form>
			    </div>
			    
			    <div class="control-group" style="margin-bottom: 20px;">
			        <a href="#" class="btn btn-primary" style="margin-left:0px;" onclick="javascript:addVoteThemes();return false;">
			                                        添加投票主题
			        </a>
			    </div>
			    <div id="addthemeandoption" style="display: none;">
			    <form class="form-horizontal" method="post" action="" onsubmit="return false" autocomplete="off">
			      <input type="hidden" id="themeidvaluehideinput" name="themeidvalue" value="0" />
			      <script>
			         	var addoredittheme = 0;  // 添加或者修改主题，0为添加，1为修改
			      </script>
			      <div class="control-group" id="votethemeinputDiv">
			        <label class="control-label" for="inputTheme">投票主题</label>
			        <div class="controls">
			            <input type="text" name="votetheme" id="inputTheme" value="" placeholder="请输入投票主题" style="width:250px;margin-bottom: 0px;">
			            <span class="help-inline" id="themehelp" style="color:#b94a48"></span>
			        </div>
			      </div>
			      <div class="control-group" id="votethemenumberDiv" style="margin-top:10px;">
			        <label class="control-label" for="inputThemeNumber" >主题编号</label>
			        <div class="controls" >
			            <input type="text" name="votethemenumber" id="inputThemeNumber" value="" placeholder="请输入编号" style="width:100px;">
			            <span class="help-inline" id="themenumberhelp" style="color:#b94a48"></span>
			        </div>
			      </div>
			      <div class="control-group" id="votethemechoosenumDiv" style="margin-top:10px;">
			        <label class="control-label" for="inputThemeChooseNumber" >最少选几项</label>
			        <div class="controls" >
			            <input type="text" name="votethemeChoosenumber" id="inputThemeChooseNumber2" value="" placeholder="请输入选项个数" style="width:100px;">
			            <span class="help-inline" id="themechoosenumberhelp2" style="color:#b94a48"></span>
			        </div>
			      </div>
			      <div class="control-group" id="votethemechoosenumDiv" style="margin-top:10px;">
			        <label class="control-label" for="inputThemeChooseNumber" >最多选几项</label>
			        <div class="controls" >
			            <input type="text" name="votethemeChoosenumber" id="inputThemeChooseNumber" value="" placeholder="请输入选项个数" style="width:100px;">
			            <span class="help-inline" id="themechoosenumberhelp" style="color:#b94a48"></span>
			        </div>
			      </div>
			      <div class="control-group">
						<button type="button" class="btn btn-primary " id="dosavevotetheme" onclick="javascript:addVoteThemesAndSave(<%=voteId %>);return false;"
						style="margin-left:115px;margin-right:10px;">
						保存主题</button>
						<button type="button" class="btn  " id="repeatinputvalue" onclick="javascript:reInputTheme();return false;" style="margin-right:10px;">全部重输</button>
						<button type="button" class="btn  " id="canceleditthemes" onclick="javascript:cancelEditTheme();return false;">取消</button> 
			     </div>
			       <div class="alert" id="autothemealertdiv" style="display:none">
			         <h4></h4><span></span>
			       </div>
			    </form>
			   </div>
			   
			   <div id="addoptionshowDiv" style="display: none;">
			      <h4 style="font-size: 16px;">添加选项</h4>
			      <div class="control-group" id="voteOptioninputDiv">
			       <div class="input-prepend">
			         <input class="span2 voteoptions" name="voteoption" id="voteOptioninput" type="text"
			         placeholder="请输入投票选项" style="width:430px;" />
			         <textarea class="span3" style="width:430px;height:80px;display:none" id="voteOptiontextarea">
			         </textarea>
			         <script>
			         	var moretextinput = 0;
			         </script>
			         <button type="button" class="btn" style="margin-left:5px;" id="showmoretextinput">
			            <i class="icon-check-empty"></i>多行文字
			         </button>
			         <button type="button" class="btn" style="margin-left:5px;" id="addoption">
			            <i class="icon-plus"></i>添加选项
			         </button>
			       </div>
			     </div>
			     <div class="control-group">
						<button type="button" class="btn btn-primary " id="doneaddoptions" onclick="javascript:doneEditOptions();return false;"
						style="margin-left:0px;margin-right:10px;">
						完成关闭</button>
						<button type="button" class="btn  " id="canceladdoptions" onclick="javascript:cancelEditOptions();return false;">取消</button> 
			     </div>
			   <div class="alert" id="autoopalertdiv" style="display:none">
			      <h4></h4><span></span>
			   </div>
			   </div>
			   <h4>已有选项</h4>
			   <form class="form-horizontal" id="optionslist" method="post" action="" onsubmit="return false" autocomplete="off">
			      <input id="temSaveOpcontent" name="temSaveOpcontent" value="" style="display:none"/>
			      <input id="temOpID" name="temOpID" value="" style="display:none"/>
			      <div class="panel-group" id="accordion">
			      <!-- TODO 需要循环的部分  -->
			      <c:forEach var="vts" items="${themelist}">
			        <c:if test="${vts.optionNums>0 }">
			         <div class="panel panel-info defaultsclass" id="tableContainer_${vts.themeId }">
    					<div class="panel-heading">
     					   <h4 class="panel-title">
        				      <a id="foldaclick_${vts.themeId }" data-toggle="collapse" data-parent="#accordion" href="#collapse_${vts.themeId }">主题 ${vts.themeIndex }</a>
                           </h4>
                        </div>
                        <div id="collapse_${vts.themeId }" class="panel-collapse collapse in">
                            
                        	<div class="panel-body" id="optionclasscontainer_${vts.themeId }">
					         <!-- table循环部分 -->
			      				<c:forEach var="op" items="${vts.voteOptionList}">
			      
							       <table class="table optable" id="table_${op.voteOpId}">
							        <tbody>
							          <tr>
							             <td class="span1">${op.voIndex}.<br>
							             <a href="#" class="editoptionindexbtn" title="修改编号" onclick="javascript:editOptionIndex(${op.voteOpId});return false;">修改</a>
							             </td>
							             <td>
							                <div class="opcontent" id="opcontent_${op.voteOpId}"><pre>${op.voName}</pre></div>
							                <div class="opmedias" id="opmedias_${op.voteOpId}">
				                               <c:if test="${!empty op.voImgAdd}">
							                   <div class="opmediabox optionimage">
													<a target="_blank" href="${op.voImgAdd}">
														<img src="${op.voImgAdd}" class="uploadedimg img-polaroid">
													</a>	
													<div onclick="javascript:delOptionImage(${op.voteOpId},${op.voImgInfoId});return false;" class="deliconbtn imgdelbtn">
														<i class="icon-remove" title="删除图片"></i> 
													</div>
											  </div>
											  </c:if>
											  <c:if test="${!empty op.voVideoAdd}">
											  <div class="opmediabox optionvideo">
											     <a href="javascript:void(0);" onclick="javascript:checkOptionVideo(${op.voteOpId});return false;">
											        <i class="icon-film"></i>
											     </a>
											     <div class="deliconbtn pagetextdelbtn" onclick="javascript:delOptionVideo(${op.voteOpId});return false;">
											        <i title="删除选项视频" class="icon-remove"></i>
											     </div>
											  </div>
											  </c:if>
											  <c:if test="${(!empty op.voViewContent) || op.voShowOpImgFlag==0}">
											  <div class="opmediabox optionpagetext">
											     <a href="javascript:void(0);" onclick="javascript:checkViewPageContent(${op.voteOpId});return false;">
											        <i class="icon-file-alt"></i>
											     </a>
											     <div class="deliconbtn pagetextdelbtn" onclick="javascript:delOptionPageText(${op.voteOpId});return false;">
											        <i title="删除选项查看页" class="icon-remove"></i>
											     </div>
											  </div>
											  </c:if>
											  
							                  <div class="clear"></div>
							                </div>
							                <div class="opedits">
							                   <label class="control-label" for="optionGroup" style="width:45px">别名</label>
							                   <input type="text" name="optionGroup" id="optionGroup" placeholder="请输入别名" disabled="disabled" value="${op.voClassification}">
							                   <button class="btn btn-primary editclassname" id="editclassname" onclick="javascript:editOptionClass(${op.voteOpId});return false;">修改</button>
							                   <a href="#" class="btn edittext" onclick="javascript:edit_optext(${op.voteOpId});return false;">
							                     <i class="icon-edit" title="修改文字"></i>修改文字
							                   </a>
							                   <a href="#" class="btn addpicbtn" onclick="javascript:addOptionImage(${op.voteOpId});return false;">
							                     <i class="icon-picture" title="添加图片"></i>添加图片
							                   </a>
							                   <a href="#" class="btn addvideobtn" onclick="javascript:addOpVideo(${op.voteOpId});return false;">
							                     <i class="icon-film" title="添加视频地址"></i>添加视频地址
							                   </a>
							                   <a href="#" class="btn addlinkurlbtn disabled" onclick="javascript:addOpLinkUrl(${op.voteOpId});return false;">
							                     <i class="icon-link" title="添加链接"></i>添加链接
							                   </a>
							                   <a href="#" class="btn addoppagetextbtn" onclick="javascript:addOpPageText(${op.voteOpId});return false;">
							                     <c:choose>  
							                        <c:when test="${(!empty op.voViewContent) || op.voShowOpImgFlag==0}">
							                           <i class="icon-file-alt" title="编辑查看页"></i>编辑查看页
							                        </c:when>
							                        <c:otherwise>
							                           <i class="icon-file-alt" title="添加查看页"></i>添加查看页
							                        </c:otherwise>
							                     </c:choose>
							                   </a>
							                   <a href="#" class="btn btn-danger deleteoptionbtn" onclick="javascript:deleteTheOption(${op.voteOpId},${op.voThemeId });return false;">
							                     <i class="icon-remove" title="删除选项"></i>删除选项
							                   </a>
							                </div>
							               </td>
							            </tr>
							         </tbody>
							      </table>
							   </c:forEach>
                        	</div>
                        </div>
                    </div>
                    </c:if>
                    </c:forEach>
                  </div>
			   </form>
			   <div class="control-group" style="margin-bottom: 100px;margin-top:50px;">
			       <a href="" class="btn btn-primary nextpublish" id="nextpublish">下一步</a>
			   </div>
			  </div>
			</div>
		</form>
	</div>
	<script type="text/javascript">
	$(function(){
   	 // 初始化图片地址数据
   	 if(voteType=="0"){
   		 $("#optionslist table").each(function(){
   			$(this).find(".addpicbtn").addClass("disabled"); 
   			$(this).find(".addvideobtn").addClass("disabled");
   			$(this).find(".addoppagetextbtn").addClass("disabled"); 
   		 });
   	 }
   	if(voteType=="1"){
  		 $("#optionslist table").each(function(){
  			$(this).find(".addvideobtn").addClass("disabled");
  		 });
  	 }
   	$("#optionslist table").each(function(){
		if($(this).find(".optionimage").is("div")){
			$(this).find(".addpicbtn").addClass("disabled");
		} 
		if($(this).find(".optionvideo").is("div")){
			$(this).find(".addvideobtn").addClass("disabled");
		}
	});
   	 /* $.ajax({
   			type:"POST",
   			url:"VoteOption.do",
   			data:{action:'initOptions',voteID:voteID},
   			dataType:"json",
   			async:false,
   			success:function(data){
   				if(data.optionsimage!=""){
   				for(var i=0;i<data.optionsimage.length;i++){
   					// 获取选项ID
   					var _optionid = data.optionsimage[i].viOptionId;
   					// 获取图片地址
   					var _imageAddress =rootURL + data.optionsimage[i].viAddress;
   					// 获取图片ID
   					var _imageId = data.optionsimage[i].voteImageId;
   					_imagebox = getOptionImageBox(_imageAddress,_optionid,_imageId);
   					$("#table_"+_optionid+" #opmedias_"+_optionid+" .clear").before(_imagebox);
   					$("#table_"+_optionid).find(".addpicbtn").addClass("disabled");
   				}
   				}		
   			}
   		});	
   	 
   	optionsList = "<c:out value="${optionlist}"/>";
   	alert(optionsList); */
    });
	
	function initOptionImageBox(opimgurl,_opid,_imageid){
    	_imgbox  = "<div class='opmediabox optionimage'>";
    	_imgbox += " <a href='"+opimgurl+"' target='_blank'><img class='uploadedimg img-polaroid' src='"+opimgurl+"' /></a>";
    	_imgbox += "<div class='deliconbtn imgdelbtn' onclick='javascript:delOptionImage("+_opid+","+_imageid
    	           +");return false;'><i title='删除图片' class='icon-remove'></i></div>";
    	_imgbox += "</div>";
    	return _imgbox;
    }
	</script>
	<script type="text/javascript" src="../vote/js/voteoptions.js"></script>
</body>
</html>