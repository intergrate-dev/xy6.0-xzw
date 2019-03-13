<%@ page pageEncoding = "UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.founder.amuc.commons.FlowProcHelper"%>
<%@page import="net.sf.json.JSONObject"%>
<%@page import="com.founder.e5.context.Context"%>
<%@page import="com.founder.e5.dom.DocLib"%>
<%@page import="com.founder.e5.dom.DocLibReader"%>
<%@page import="com.founder.e5.workspace.app.form.*"%>
<%@page import="com.founder.e5.web.WebUtil"%>
<%@page import="com.founder.e5.workspace.ProcHelper"%>
<%@page import="com.founder.amuc.commons.FormViewerHelper"%>
<%@page import="com.founder.e5.doc.Document"%>
<%@page import="com.founder.e5.doc.DocumentManager"%>
<%@page import="com.founder.e5.doc.DocumentManagerFactory"%>
<%@page import="com.founder.amuc.commons.StringHelper"%>
<%@page import="com.founder.amuc.commons.FlowProcHelper"%>
<%@page import="com.founder.amuc.commons.FormViewerHelper"%>

<%
	DocLibReader libReader = (DocLibReader)Context.getBean(DocLibReader.class);
	int docLibID = FlowProcHelper.getInt(request.getParameter("DocLibID"));
	int docID = FlowProcHelper.getInt(request.getParameter("DocIDs"));
	
// 	String vsOptionType = request.getParameter("vsOptionType");
	String voteId = request.getParameter("voteID");
	String UUID = request.getParameter("UUID");
	String editFlag = request.getParameter("addOrEdit");
// 	String chooseNumType = request.getParameter("chooseNumType");
	//int addOrEdit = FlowProcHelper.getInt(request.getParameter("addOrEdit"));
	String rootURL = WebUtil.getRoot(request);
	
%>

<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<html>
<head>
    <title>创建投票</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jquery-ui/jquery-ui.custom.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
	<link type="text/css" rel="stylesheet" href="../css/form-custom.css"/>
	<link type="text/css" rel="stylesheet" href="../vote/css/datetimepicker.css"/>
	<link type="text/css" rel="stylesheet" href="../script/lhgcalendar/lhgcalendar.bootstrap.css"/>
    <link type="text/css" rel="stylesheet" href="../script/bootstrap/css/bootstrap.min.css"/>
    <link type="text/css" rel="stylesheet" href="../vote/css/font-awesome.min.css"/>
	<link href="../message/includes/appmsgeditor.css" rel="stylesheet" />
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
	<script type="text/javascript" src="../ueditor/ueditor.config.js"></script>
    <script type="text/javascript" src="../ueditor/ueditor.all.js"></script>
    <script type="text/javascript" src="../vote/js/bootstrap-datetimepicker.js"></script>
    <script type="text/javascript" src="../vote/js/bootstrap-datetimepicker.zh-CN.js"></script>
    <script type="text/javascript" src="../vote/js/jquery.alerts.js"></script>
    <!--  
	<script type="text/javascript" src="../../e5script/jquery/jquery-ui/jquery.ui.core.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/jquery-ui/jquery.ui.widget.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/jquery-ui/jquery.ui.button.js"></script>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jquery-ui/jquery.ui.all.css"/>
	-->
	<script type="text/javascript" src="../script/bootstrap/js/bootstrap.min.js"></script>
	
	<script type="text/javascript">
	    var rootURL = '<%=rootURL %>';
    </script> 
	<script type="text/javascript">
	var UUID = "<%=UUID %>";
	var docID = "<%=docID %>";
	
    var docLibID = "<%=docLibID %>";
    var voteID = "<%=voteId %>";
    var editFlag = "<%=editFlag %>";
<%--     var vsTypes = "<%=chooseNumType %>"; --%>
	</script>
	<style type="text/css">
	
		#radio {
			font-size: 62.5%;
			font-family: "Arial", "Helvetica", "Verdana", "sans-serif";
		}
		.control-label {
  float: left;
  width: 160px;
  padding-top: 5px;
  text-align: right;
}
.control-group {
  margin-bottom: 20px;
  *zoom: 1;
}
.controls {
  *display: inline-block;
  *padding-left: 20px;
  margin-left: 180px;
  *margin-left: 0;
}
.alert {
  padding: 8px 35px 8px 14px;
  margin-bottom: 20px;
  text-shadow: 0 1px 0 rgba(255, 255, 255, 0.5);
  background-color: #fcf8e3;
  border: 1px solid #fbeed5;
  -webkit-border-radius: 4px;
     -moz-border-radius: 4px;
          border-radius: 4px;
}

.alert,
.alert h4 {
  color: #c09853;
}

.alert h4 {
  margin: 0;
}

.alert .close {
  position: relative;
  top: -2px;
  right: -21px;
  line-height: 20px;
}

.alert-success {
  color: #468847;
  background-color: #dff0d8;
  border-color: #d6e9c6;
}

.alert-success h4 {
  color: #468847;
}

.alert-danger,
.alert-error {
  color: #b94a48;
  background-color: #f2dede;
  border-color: #eed3d7;
}

.alert-danger h4,
.alert-error h4 {
  color: #b94a48;
}

.alert-info {
  color: #3a87ad;
  background-color: #d9edf7;
  border-color: #bce8f1;
}

.alert-info h4 {
  color: #3a87ad;
}

.alert-block {
  padding-top: 14px;
  padding-bottom: 14px;
}

.alert-block > p,
.alert-block > ul {
  margin-bottom: 0;
}

.alert-block p + p {
  margin-top: 5px;
}
	</style>
</head> 
<body>
	
	<ul class="nav nav-tabs">
			   <li class="active"><a href="javascript:void(0);" id="votesetTab"><i class="icon-edit"></i>投票基本设置</a></li>
			   <li class="voteoptionTab"><a id="voteoptionTab" href="javascript:void(0);"><i class="icon-tasks"></i>投票选项</a></li>
			   <!-- <li class="votepublishTab"><a href="javascript:void(0);" id="votepublishTab"><i class="icon-cogs"></i>发布设置</a></li> -->
			   <li class="pageconfigTab"><a href="javascript:void(0);" id="pageconfigTab"><i class="icon-magic"></i>页面配置</a></li>
			   <!-- <li class="checkvotePageTab"><a href="javascript:void(0);" id="checkvotePageTab"><i class="icon-external-link"></i>查看投票页</a></li> -->
	</ul>
	<div class="span9">
			<div class="control-group" style="margin-bottom: 20px;margin-top:20px;">
		       <a href="javascript:void(0);" class="btn btn-primary save" id="save1">下一步</a>
		   </div>
			<h4>投票基本设置</h4>
		    <div class="control-group" id="inputTitleDiv">
		    <label class="control-label" for="inputTitle">投票标题<span style="color: red;">*</span></label>
			    <div class="controls">
			    <input type="hidden" value="${voteSettings.vsTypes }" id="chooseNumType">
			    <input type="hidden" value="${voteSettings.vsOptionType }" id="vsOption">
			    <input type="text" style="width:264px;" class="span2 voteoptions" name="votetitle" id="inputTitle" value="${voteSettings.vsTitle }"  placeholder="请输入投票标题">
			    <span class="help-inline"></span>
			    </div>
			    <div class="control-group">
		    		<label class="control-label" for="">选项能否为空<span style="color: red;">*</span></label>
		    		 <c:if test="${voteSettings.vsOptionIsNull ==null||voteSettings.vsOptionIsNull ==1 }">
		    		 <div class="controls" id="vsOptionIsNull">
			    	<label class="radio">
			    		<input type="radio" name="vsOptionIsNull" value="1" class='votetype'  checked> 是
					</label>
			   		 <label class="radio">
						<input type="radio" name="vsOptionIsNull" class='votetype' value="0"> 否
					</label>
			    	</div>
			    	 </c:if>
			    	  <c:if test="${voteSettings.vsOptionIsNull==0 }">
		    		 <div class="controls" id="vsOptionIsNull">
			    	<label class="radio">
			    		<input type="radio" name="vsOptionIsNull" value="1" class='votetype'> 是
					</label>
			   		 <label class="radio">
						<input type="radio" name="vsOptionIsNull" class='votetype' value="0" checked> 否
					</label>
			    	</div>
			    	 </c:if>
		    	</div>
			    <div class="control-group">
		    <label class="control-label" for="">投票类型<span style="color: red;">*</span></label>
		    	<c:if test="${voteSettings.vsTypes ==1 }">
			    <div class="controls" id="whetherMore">
			    <label class="radio">
					<input type="radio" id="inputVotetype" name="votetype" class='votetype' value="0"> 单选
				</label>
			    <label class="radio">
					<input type="radio" name="votetype" value="1" class='votetype'  checked> 多选
				</label>
			    </div>
			    </c:if>
			    <c:if test="${voteSettings.vsTypes ==null||voteSettings.vsTypes==0 }">
			    <div class="controls" id="whetherMore">
			    <label class="radio">
					<input type="radio" id="inputVotetype" name="votetype" class='votetype' value="0" checked> 单选
				</label>
			    <label class="radio">
					<input type="radio" name="votetype" value="1" class='votetype'> 多选
				</label>
			    </div>
			    </c:if>
			     <div class="control-group">
		    <label class="control-label" for="">投票选项<span style="color: red;">*</span></label>
		    <c:if test="${voteSettings.vsOptionType ==null||voteSettings.vsOptionType==0 }">
			    <div class="controls" id="voteOption">
			    <label class="radio">
					<input type="radio" id="inputVoteOption" name="voteOption" class='votetype' value="0" checked> 文字
				</label>
			    <label class="radio">
					<input type="radio" name="voteOption" value="1" class='votetype'> 图文
				</label>
				<label class="radio">
					<input type="radio" name="voteOption" value="2" class='votetype'> 视频
				</label>
			    </div>
			     </c:if>
			      <c:if test="${voteSettings.vsOptionType ==1}">
			    <div class="controls" id="voteOption">
			    <label class="radio">
					<input type="radio" id="inputVoteOption" name="voteOption" class='votetype' value="0"> 文字
				</label>
			    <label class="radio">
					<input type="radio" name="voteOption" value="1" class='votetype' checked> 图文
				</label>
				<label class="radio">
					<input type="radio" name="voteOption" value="2" class='votetype'> 视频
				</label>
			    </div>
			     </c:if>
			     <c:if test="${voteSettings.vsOptionType ==2}">
			    <div class="controls" id="voteOption">
			    <label class="radio">
					<input type="radio" id="inputVoteOption" name="voteOption" class='votetype' value="0"> 文字
				</label>
			    <label class="radio">
					<input type="radio" name="voteOption" value="1" class='votetype' > 图文
				</label>
				<label class="radio">
					<input type="radio" name="voteOption" value="2" class='votetype' checked> 视频
				</label>
			    </div>
			     </c:if>
		    </div>
		    <div class="control-group" id="pageStyleDiv" <c:if test="${voteSettings.vsOptionType !=2}">style="display:none;"</c:if>>
		    	<label class="control-label" for="vsVotePageStyle">投票页面样式<span style="color: red;">*</span></label>
			    <div class="controls">
			    	<select id="vsVotePageStyle">
			    		<c:if test="${voteSettings.vsVotePageStyle ==0 ||voteSettings.vsVotePageStyle==null}">
				    		<!--  <option value="0" selected="selected">单选多选样式</option>-->
				    		<option value="1">选项单个提交样式</option>
			    		</c:if>
				    	<c:if test="${voteSettings.vsVotePageStyle ==1 }">
				    		<!--  <option value="0">单选多选样式</option>-->
				    		<option value="1" selected="selected">选项单个提交样式</option>
			    		</c:if>
			    		
			    	</select>
				</div>
			</div>
			<div class="control-group" id="vsVoteRepeatOneDiv"   <%-- <c:if test="${voteSettings.vsVotePageStyle==0 ||voteSettings.vsVotePageStyle==null||voteSettings.vsOptionType !=2}"> --%>style="display: none;"><%-- </c:if> --%>
		    	<label class="control-label" for="vsVoteRepeatOne">重复投一个选项<span style="color: red;">*</span></label>
		    	<c:if test="${voteSettings.vsVoteRepeatOne ==null||voteSettings.vsVoteRepeatOne ==1 }">
		    		 <div class="controls" id="vsVoteRepeatOne">
			   		 <label class="radio">
						<input type="radio" name="vsVoteRepeatOne" class='votetype' value="0"> 不允许
					</label>
			    	<label class="radio">
			    		<input type="radio" name="vsVoteRepeatOne" value="1" class='votetype'  checked> 允许
					</label>
			    	</div>
			    </c:if>
			    <c:if test="${voteSettings.vsVoteRepeatOne==0 }">
		    		 <div class="controls" id="vsVoteRepeatOne">
			   		 <label class="radio">
						<input type="radio" name="vsVoteRepeatOne" class='votetype' value="0" checked> 不允许
					</label>
			    	<label class="radio">
			    		<input type="radio" name="vsVoteRepeatOne" value="1" class='votetype'> 允许
					</label>
			    	</div>
			    </c:if>
		    </div>
		    <div class="control-group">
					<label class="control-label" for="inputExpairtimes">投票开始时间<span style="color: red;">*</span>
						:</label>
					<div style="margin-left: 19px;"
						class="controls input-append date form_datetime"
						data-date-format="yyyy-mm-dd hh:ii:ss"
						data-link-field="dtp_input1">
						<input size="16" type="text" value="${voteSettings.vsStarTime }"
							id="inputExpairtimes" name='expairtimes'> <span
							class="add-on"><i class="icon-remove"></i></span> <span
							class="add-on"><i class="icon-th"></i></span>
					</div>
				</div>
		    <div class="control-group">
			    <label class="control-label" for="inputExpairtime">投票截止时间 <span style="color: red;">*</span>:</label>
				    <div style="margin-left:19px;" class="controls input-append date form_datetime" data-date-format="yyyy-mm-dd hh:ii:ss" data-link-field="dtp_input1">
				                    <input size="16" type="text" value="${voteSettings.vsEndTime }"  id="inputExpairtime"   name='expairtime'>
				                    <span class="add-on"><i class="icon-remove"></i></span>
									<span class="add-on"><i class="icon-th"></i></span>
				    </div>
			    </div>
		    </div>
		    <div class="control-group">
		    <label class="control-label" for="vsHostUnit">主办单位<span style="color: red;">*</span></label>
			    <div class="controls">
			     <textarea class="span3" style="width:300px;height:80px;" id="vsHostUnit">${voteSettings.vsHostUnit }</textarea>
			    <span class="help-inline"></span>
			</div>
			<div class="control-group">
		    	<label class="control-label" for="vsVoteType">投票类别<span style="color: red;">*</span></label>
			    <div class="controls">
			    	<select id="vsVoteType">
			    	 <c:if test="${voteSettings.vsVoteType ==1 }">
			    		<option value="0">实名投票</option>
			    		<option value="1" selected="selected">匿名投票(根据IP区分用户)</option>
			    		<option value="2">匿名投票(根据Cookie区分用户)</option>
			    	</c:if>
			    	<c:if test="${voteSettings.vsVoteType ==0 ||voteSettings.vsVoteType==null}">
			    		<option value="0" selected="selected">实名投票</option>
			    		<option value="1">匿名投票(根据IP区分用户)</option>
			    		<option value="2">匿名投票(根据Cookie区分用户)</option>
			    	</c:if>
			    	<c:if test="${voteSettings.vsVoteType ==2 }">
			    		<option value="0">实名投票</option>
			    		<option value="1">匿名投票(根据IP区分用户)</option>
			    		<option value="2" selected="selected">匿名投票(根据Cookie区分用户)</option>
			    	</c:if>
			    	</select>
				</div>
			</div>
			<div class="control-group">
		    	<label class="control-label" for="vsVoteMode">投票模式<span style="color: red;">*</span></label>
			    <div class="controls">
			    	<select id="vsVoteMode">
				    	<c:if test="${voteSettings.vsVoteMode ==1 }">
				    		<option value="0">一般模式</option>
				    		<option value="1" selected="selected">周期模式</option>
			    		</c:if>
			    		<c:if test="${voteSettings.vsVoteMode ==0 ||voteSettings.vsVoteMode==null}">
				    		<option value="0" selected="selected">一般模式</option>
				    		<option value="1">周期模式</option>
			    		</c:if>
			    	</select>
				</div>
			</div>
			<div class="control-group">
		    	<label class="control-label" for="vsVoteInApp">是否只能在app中参与<span style="color: red;">*</span></label>
			    <div class="controls">
			    	<select id="vsVoteInApp">
			    		<c:if test="${voteSettings.vsVoteInApp ==0 }">
				    		<option value="0" selected="selected">是</option>
				    		<option value="1">否</option>
			    		</c:if>
				    	<c:if test="${voteSettings.vsVoteInApp ==1 ||voteSettings.vsVoteInApp==null}">
				    		<option value="0">是</option>
				    		<option value="1" selected="selected">否</option>
			    		</c:if>
			    	</select>
				</div>
			</div>
		    <div class="control-group">
		   	 <c:if test="${voteSettings.vsActivityIntro !=null}">
					<button type='button' class="btn" id="addcontent" style="display:none">添加活动介绍</button>
					</c:if>					
					 <c:if test="${voteSettings.vsActivityIntro ==null}">
					<button type='button' class="btn" id="addcontent">添加活动介绍</button>
					</c:if>
				</div>
				 <c:if test="${voteSettings.vsActivityIntro !=null}">
						<div id="addcontentdiv">
				<h4>活动介绍</h4>
				<div class='alert alert-error'>内容中禁止出现提示用户先关注再投票之类的提示信息，否则会被微信屏蔽。</div>
				<div class="control-group">
				<textarea name="myEditor" id="myEditor" style="width:600px;height:402px;">${voteSettings.vsActivityIntro }</textarea>
				<script type="text/javascript">
				UE.getEditor('myEditor',{
					 toolbars:[
			                    [ 'undo', 'redo', '|',
			                        'bold', 'italic', 'underline', 'strikethrough', 'removeformat', 'pasteplain', '|', 'forecolor', 'backcolor',
			                        'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify','touppercase','tolowercase','|',
			                        'link', 'unlink',
			                        /* 'simpleupload', */'|','preview', 'searchreplace', 'fontfamily', 'fontsize','fullscreen',
			                        'inserttable', 'deletetable', 'insertparagraphbeforetable', 'insertrow', 'deleterow', 'insertcol', 'deletecol', 'mergecells', 'mergeright', 'mergedown', 'splittocells', 'splittorows', 'splittocols', '|',
			                        'emotion', 'map']
			                ]
				});
				</script>
				</div>
				</c:if>
				 <c:if test="${voteSettings.vsActivityIntro ==null}">
						<div id="addcontentdiv" style="display: none">
				<h4>活动介绍</h4>
				<div class='alert alert-error'>内容中禁止出现提示用户先关注再投票之类的提示信息，否则会被微信屏蔽。</div>
				<div class="control-group">
				<textarea name="myEditor" id="myEditor" style="width:600px;height:402px;">${voteSettings.vsActivityIntro }</textarea>
				<script type="text/javascript">
				UE.getEditor('myEditor',{
					 toolbars:[
			                    [ 'undo', 'redo', '|',
			                        'bold', 'italic', 'underline', 'strikethrough', 'removeformat', 'pasteplain', '|', 'forecolor', 'backcolor',
			                        'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify','touppercase','tolowercase','|',
			                        'link', 'unlink',
			                        /* 'simpleupload', */'|','preview', 'searchreplace', 'fontfamily', 'fontsize','fullscreen',
			                        'inserttable', 'deletetable', 'insertparagraphbeforetable', 'insertrow', 'deleterow', 'insertcol', 'deletecol', 'mergecells', 'mergeright', 'mergedown', 'splittocells', 'splittorows', 'splittocols', '|',
			                        'emotion', 'map']
			                ]
				});
				</script>
				</div>
				</c:if>
			</div>
				<div class="control-group">
					<c:if test="${voteSettings.vsUserInfoRule==null }">
					<button type='button' class="btn" id="adduserinfocollect" >增加用户信息收集功能表单</button>
					 </c:if>
				</div>
					<c:if test="${voteSettings.vsUserInfoRule==null }">
								<div id="adduserinfocollectdiv" style="display: none">
								<h4>收集用户信息</h4>
					<div class="control-group">
							<label class="control-label" >选择要收集的信息</label>
					</c:if>
							 <c:forEach items="${memberInfo }" var="item">
					    	<div class="controls">
					    	<label class="checkbox inline" style="width:200px;">
							  <input type="checkbox" id="chkbox" name="chkbox" value="${item.value}"  >${item.value}
							</label>
							<label class="radio inline" style="color:#666">
							  <input type="radio" id=""  name="${item.key}" value="1"  class="ufop"  >必填
							</label>
							<label class="radio inline" style="color:#666">
							  <input type="radio" id=""  name="${item.key}" value="0" class="ufop"  checked>非必填
							</label>
							</div>
							 </c:forEach>
							
							 <c:if test="${voteSettings.vsUserInfoRule!=null }">
					<button type='button' class="btn" id="adduserinfocollect" style="display: none">增加用户信息收集功能表单</button>
					</c:if>
				</div>
				<c:if test="${voteSettings.vsUserInfoRule!=null }">
								<div id="adduserinfocollectdiv">
									<h4>收集用户信息</h4>
					<div class="control-group">
							<label class="control-label" >选择要收集的信息</label>
								</c:if>
								<c:forEach items="${list }" var="item">
					    	<div class="controls">
					    	<label class="checkbox inline" style="width:200px;">
							  <input type="checkbox" id="chkbox" name="chkbox" checked="checked" value="${item.key}"  >${item.key}
							</label>
							<c:if test="${item.value==0}">
							<label class="radio inline" style="color:#666">
							  <input type="radio" id=""  name="${item.key}" value="1"  class="ufop"  >必填
							</label>
							<label class="radio inline" style="color:#666">
							  <input type="radio" id=""  name="${item.key}" value="0" class="ufop"  checked>非必填
							</label>
							</c:if>
							<c:if test="${item.value==1}">
							<label class="radio inline" style="color:#666">
							  <input type="radio" id=""  name="${item.key}" value="1"  class="ufop" checked>必填
							</label>
							<label class="radio inline" style="color:#666">
							  <input type="radio" id=""  name="${item.key}" value="0" class="ufop">非必填
							</label>
							</c:if>
							</div>
							 </c:forEach>
							 <c:forEach items="${memberInfos }" var="item">
					    	<div class="controls">
					    	<label class="checkbox inline" style="width:200px;">
							  <input type="checkbox" id="chkbox" name="chkbox" value="${item.value}"  >${item.value}
							</label>
							<label class="radio inline" style="color:#666">
							  <input type="radio" id=""  name="${item.key}" value="1"  class="ufop"  >必填
							</label>
							<label class="radio inline" style="color:#666">
							  <input type="radio" id=""  name="${item.key}" value="0" class="ufop"  checked>非必填
							</label>
							</div>
							 </c:forEach>
					</div>
				</div>
				 <div class="control-group" style="margin-bottom: 100px;margin-top:50px;">
			       <a href="javascript:void(0);" class="btn btn-primary save" id="save">下一步</a>
			   </div>
			</div>
	</div>
</body>
<script type="text/javascript">
$(".form_datetime").datetimepicker({
	language:"zh",
    format: "yyyy-mm-dd hh:ii:ss",
    autoclose: true,
    todayBtn: true,
    minuteStep: 10,
    pickerPosition:'top-left',
});
var vsOptionType = $("#vsOption").val();
if(editFlag=="0"){
	$(".voteoptionTab").addClass("disabled");
	$(".votepublishTab").addClass("disabled");
	$(".pageconfigTab").addClass("disabled");
	$(".checkvotePageTab").addClass("disabled");
}
if(editFlag==1){
	$("#voteOption").find($('input[type="radio"]')).attr("disabled","disabled");
	var vo=$("input:radio[name='voteOption']:checked").val();
	if(vo==2)
		$("#pageStyleDiv").css('display','block');
	if($('#vsVotePageStyle').val()==1)
		//$("#vsVoteRepeatOneDiv").css('display','block');
	$("title").html("编辑投票");
}
if(docID>0){
	var vsTypes = $("#chooseNumType").val();
	$("#votesetTab").attr("href","");
	$("#voteoptionTab").attr("href","../voteOption/initOptions.do?action=initOptions&voteID="+docID+"&UUID="+UUID+"&vsOptionType="+vsOptionType+"&addOrEdit="+editFlag+"&chooseNumType="+vsTypes+"&siteID="+getUrlVars("siteID"));
	$("#votepublishTab").attr("href","javascript:void(0);");
	$("#pageconfigTab").attr("href","../headersImg/uploadadd.do?action=uploadadd&voteID="+docID+"&UUID="+UUID+"&vsOptionType="+vsOptionType+"&addOrEdit="+editFlag+"&chooseNumType="+vsTypes+"&siteID="+getUrlVars("siteID"));
	$("#checkvotePageTab").attr("href","javascript:void(0);");
}
$("#addcontent").click(function(){
	$("#addcontentdiv").css("display","block");
	$("#addcontent").css("display","none");
})
$("#adduserinfocollect").click(function(){
	$("#adduserinfocollectdiv").css("display","block");
	$("#adduserinfocollect").css("display","none");
})
//只有视频投票时，显示投票页面样式选项
$("input:radio[name='voteOption']").change(function(){
	var voption=$("input:radio[name='voteOption']:checked").val();
	//alert(voption);
	if(voption==2){
		$("#pageStyleDiv").css('display','block');
		//$("#vsVoteRepeatOneDiv").css('display','block');
	}else{
		$("#vsVotePageStyle").prop('selectedIndex', 0);
		$("#pageStyleDiv").css('display','none');
		$("#vsVoteRepeatOneDiv").css('display','none'); 
	}
});
//选了选项单个提交样式时，显示是否允许重复选单个选项
$("#vsVotePageStyle").change(function(){
	if($("#vsVotePageStyle").val()==0){
		$("#vsVoteRepeatOneDiv").css('display','none'); 
	}else{
		//$("#vsVoteRepeatOneDiv").css('display','block'); 
	}
});
$(".save").click(function(){
	var ue=UE.getEditor('myEditor');
	var myEditor=ue.getContent();
	var vsTitle = $("#inputTitle").val();
	var vsTypes = $("#whetherMore").find($('input[type="radio"]:checked')).val();
	//alert(vsTypes);
	var vsOptionType = $("#voteOption").find($('input[type="radio"]:checked')).val();
	var vsOptionIsNull = $("#vsOptionIsNull").find($('input[type="radio"]:checked')).val();
	var vsEndTime = $("#inputExpairtime").val();
	var vsStarTime = $("#inputExpairtimes").val();
	var DocLibID = $("#DocLibID").val();
	var obj=document.getElementsByName('chkbox'); 
	var userFiled=''; 
	var ifRequired = '';
	var siteId=getUrlVars("siteID");
	for(var i=0; i<obj.length; i++){ 
		if(obj[i].checked){
			userFiled+=obj[i].value+',';
			ifRequired+=$(obj[i]).parent().parent().find($('input[type="radio"]:checked')).val()+',';
		} 
	}
	var vsVoteType = $("#vsVoteType").val();
	var vsVoteMode = $("#vsVoteMode").val();  //模式
	var vsVoteInApp = $("#vsVoteInApp").val();  //是否只能在app中参与
	var vsVotePageStyle = $("#vsVotePageStyle").val(); //投票页面样式
	var vsVoteRepeatOne = $("#vsVoteRepeatOne").find($('input[type="radio"]:checked')).val(); //是否允许重复提交单个选项
	if(","==userFiled.substr(userFiled.length-1)){
		userFiled=userFiled.substring(0,userFiled.length-1)
	}
	if(","==ifRequired.substr(ifRequired.length-1)){
		ifRequired=ifRequired.substring(0,ifRequired.length-1)
	}
	String.prototype.trim=function() {
        return this.replace(/(^\s*)|(\s*$)/g,'');
    }
	if(docID==0){
		var url = "add.do";
	}else{
		var url = "editVoteOp.do";
	}
	var vsHostUnit = $("#vsHostUnit").val();
	if(vsTitle.trim()==""||vsTitle==null){
		alert("投票标题不能为空！");
	}else if (vsEndTime < vsStarTime) {
		alert("截止时间，不能小于开始时间！");
	}else if(vsStarTime.trim()==""||vsStarTime.trim()==null){
		alert("开始时间不能为空");
	}else if(vsEndTime.trim()==""||vsEndTime.trim()==null){
		alert("截止时间不能为空");
	}else if(vsHostUnit.trim()==""||vsHostUnit.trim()==null){
		alert("主办单位不能为空");
	}else{
		$.ajax({
	        type:"post", //请求方式  
	        url: url, //请求路径  
	        cache: false,
	        contentType : "application/x-www-form-urlencoded; charset=utf-8",  
	        data:{"vsVotePageStyle":vsVotePageStyle,"vsVoteRepeatOne":vsVoteRepeatOne,"vsVoteInApp":vsVoteInApp,"vsVoteType":vsVoteType,"vsVoteMode":vsVoteMode,"vsOptionIsNull":vsOptionIsNull,"vsHostUnit":vsHostUnit,"myEditor":myEditor,"vsTitle":vsTitle,"vsTypes":vsTypes,"vsStarTime":vsStarTime,"vsEndTime":vsEndTime,"UUID":UUID,"userFiled":userFiled,"ifRequired":ifRequired,"vsOptionType":vsOptionType,"docID":docID,"siteID":siteId},
	        success:function(data){
	        	//alert(eval(data));
	        	var voteID = data.voteID;
	        	var UUID = data.UUID;  
	        	var vsOptionType = data.vsOptionType;  
	        	var vsTypes = data.vsTypes; 
				window.location.href="../voteOption/initOptions.do?voteID="+voteID+"&UUID="+UUID+"&vsOptionType="+vsOptionType+"&addOrEdit="+editFlag+"&chooseNumType="+vsTypes+"&vsOptionIsNull="+vsOptionIsNull+"&siteID="+getUrlVars("siteID");
	        }
		 });
	}
});


//获取url地址中的参数
function getUrlVars(name){
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}
</script>
</html>
