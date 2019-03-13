<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<html>
<head>
	<title>Flow View</title>
	<meta content="text/html;charset=utf-8" http-equiv="content-type">

	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery.query.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript">
	var isvalidate = true;
	var issubmit = false;
	var defaulticon = "../images/empty.gif";
		$(function () {
			$("#txtDoName").focus();
			$("#flowform").validationEngine({
				autoPositionUpdate:true,
				onValidationComplete:function(from,r){
					if(r && isvalidate && !issubmit){
					 //start 
						var iconurl = $("#action_icon").attr("src");//empty.gif
						if(iconurl.indexOf("empty.gif")>-1){
							iconurl = "";
						}
					  var nodeData = {
						 id:$("#J_ID").val(),
						 name:$("#txtDoName").val(),
						 note:"",
						 selfnodeid:$.query.get("selfnodeid"),
						 nodeid:$("#sl_nodes").val(),
						 flowid:$("#sl_flows").val(),
						 moduleid:$("#sl_actions").val(),
						 modulename: $("#sl_actions option:selected").text(),
						 icon: iconurl,
						 iconid:$("#action_icon").attr("name")
					 };
					 window.parent.e5ActionFlowWindowClose(nodeData);
					 issubmit = true;
					 //end
					}
					else{
						isvalidate = true;
					}
				},
				promptPosition:"centerRight"
			});
			 $("#btnActionFlowSave").click(function () {
				 //验证

				 if($("#sl_actions").val() == null||$("#sl_actions").val()==""){
					 alert("<i18n:message key="flow.visual.node.window.noaction"/>");
					 
					 return;
				 }
				 if($("#sl_flows").val() == null||$("#sl_flows").val()==""){
					 alert("<i18n:message key="flow.visual.node.window.selectflow"/>");
					 
					 return;
				 }
				 if($("#sl_nodes").val() == null||$("#sl_nodes").val()==""){
					 alert("<i18n:message key="flow.visual.node.window.selectflownode"/>");
					 
					 return;
				 }
				 $("#flowform").submit();
			 });
			 $("#btnActionFlowCancle").click(function(){
				 window.parent.e5ActionFlowWindowClose(null);
			 });
			 loadIcons();
			 //加载数据
			 $("#sl_flows").change(function(){
				 
				 setFlowNodes($("#sl_flows").val());
			 });
		});
		function loadIcons(){
			 //加载图标
			$.ajax({
				url:"../e5listpage/cust_iconViewReader.do",
				dataType:"json",
				async:true,
				success:function(data) {
					if(data!=null){
						//清空内容
						$("#icon-list").empty();
						$.each(data,function(i,n){
							$("#icon-list").append("<li><table><tr><td class='l'></td><td class='c'><img onclick=\"setIcon(this);\" name=\""+n.ID+"\"   alt=\""+n.description+"\" title=\""+n.description+"\" src=\"../"+n.url+"\" /></td><td class='r'></td></tr></table></li>");
						 });
						 //加载操作
						 $.ajax({
						 	url:"../e5flow/cust_FlowNodeReader.do?method=getFlowNodeActions&docTypeID="+$.query.get("docTypeID"),
							dataType:"json",
							async:false, 
							success:function(data) {
								if(data!=null){
									$("#sl_actions").html("");
									//{"ID":10,"callMode":2,"codeURL":"test.do","dealCount":1,"description":"新建稿件","docTypeID":3,"height":300,"name":"新建稿件","needLock":false,"needLog":false,"needPrompt":false,"needRefresh":false,"resizable":true,"showType":1,"width":400}
									$.each(data,function(i,n){
										$("#sl_actions").append("<option value='"+n.ID+"'>"+n.name+"</option>");
									});
									//加载流程
									$.ajax({
										url:"../e5flow/cust_FlowNodeReader.do?method=getFlows&docTypeID="+$.query.get("docTypeID"),
										dataType:"json",
										async:false, 
										success:function(data) {
											if(data!=null){
												$("#sl_flows").html("");
												//{"ID":3,"description":"稿件审批","docTypeID":3,"firstFlowNodeID":6,"name":"稿件审批"}
												$.each(data,function(i,n){
													$("#sl_flows").append("<option value='"+n.ID+"'>"+n.name+"</option>");
												});
												setFlowNodes($("#sl_flows").val());
												 //设置更新数据
												if($.query.get("id")!=""){
													$("#J_ID").val($.query.get("id"));
													$("#txtDoName").val($.query.get("name"));
													//$("#txtDoNote").val($.query.get("note"));
													$("#sl_actions").val($.query.get("moduleid"));
													var iconurl = $.query.get("icon");
													if($.query.get("icon")==null||$.query.get("icon") == ""){
														iconurl = defaulticon;
													}
													$("#action_icon").attr("src",iconurl);
													$("#action_icon").attr("name",$.query.get("iconid"));
													$("#sl_flows").val($.query.get("flowid"));
													setFlowNodes($.query.get("flowid"));
													$("#sl_nodes").val($.query.get("nodeid"));
												}
											}
										}
									});
									//加载流程
								}
							}
						});
						//加载操作
					}
				}
			});
		}
		function setFlowNodes(flowid){
			$.ajax({url:"../e5flow/cust_FlowNodeReader.do?method=getFlowNodes&flowid="+flowid,
				dataType:"json",
				async:false, 
				success:function(data) {
					$("#sl_nodes").html("");
					//{"ID":3,"description":"稿件审批","docTypeID":3,"firstFlowNodeID":6,"name":"稿件审批"}
					$.each(data,function(i,n){
						$("#sl_nodes").append("<option value='"+n.ID+"'>"+n.name+"</option>");
					});
				}
			});
		}
		function setIcon(obj){
			$("#action_icon").attr("src",$(obj).attr("src"));
			$("#action_icon").attr("name",$(obj).attr("name"));
		}
		document.onkeydown = function (e) { 
			var theEvent = window.event || e; 
			var code = theEvent.keyCode || theEvent.which; 
				if (code == 13) { 
					$("#btnActionFlowSave").click(); 
			} 
		}
	</script>
	<link rel="stylesheet" type="text/css" href="../e5style/reset.css"/>
	<link rel="stylesheet" type="text/css" href="../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
	<link rel="stylesheet" type="text/css" href="../e5script/jquery/jquery-ui/jquery-ui.custom.css"/>
	<link rel="stylesheet" type="text/css" href="../e5style/sys-main-body-style.css"/>
	<link rel="stylesheet" type="text/css" href="../e5style/e5-flow.css"/>
</head>
<body>
	<form name="flowform" id="flowform" method="post" action="">
		<div id="nodewindows" class="nodewindows-flow">
			<table class="table" cellpadding="0" cellspacing="0" style="width:96%">
				<tr>
					<td class="w90" style="width:100px"><span class="field-required">*</span><i18n:message key="flow.visual.node.window.doname"/>:</td>
					<td style="width:460px"><input class="validate[required,custom[onlyCharNumberChiness],maxSize[40]]" type="text" id="txtDoName" />
					<span class="field-help-tootip" title="<i18n:message key="flow.proc.inputvalidate.specialchar"/>">[?]</span>
					</td>
				</tr>
				<tr>
					<td><span class="field-required">*</span><i18n:message key="flow.visual.node.window.domodelname"/>:</td>
					<td><select id="sl_actions"></select></td>
				</tr>
				<tr>
					<td><span class="field-required">*</span><i18n:message key="flow.visual.node.window.flowname"/>:</td>
					<td><select id="sl_flows"></select></td>
				</tr>
				<tr>
					<td><span class="field-required">*</span><i18n:message key="flow.visual.node.window.nodename"/>:</td>
					<td><select id="sl_nodes"></select></td>
				</tr>
				<tr>
					<td><span class="field-required">*</span><i18n:message key="flow.visual.node.window.actionicon"/>:</td>
					<td><img id="action_icon" alt="" title="" src="../images/empty.gif" /></td>
				</tr>
				<tr>
					<td colspan="2" >
						<ul class="m icon-list icon_sortlist clearfix" id="icon-list"></ul>
					</td>
				</tr>
			</table>
			
		</div>
		<div class="alignCenter m">
				<input id="btnActionFlowSave" class="button"  type="button" value="<i18n:message key="flow.visual.node.window.save"/>" />
				<input id="btnActionFlowCancle" class="button"  type="button" value="<i18n:message key="flow.visual.node.window.cancle"/>" />
		</div>
			<input type="hidden" id="J_ID" value="" />
	</form>
</body>
</html>
