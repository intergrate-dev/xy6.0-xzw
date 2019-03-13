<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<html>
<head>
	<title>Flow View</title>
	<meta content="text/html;charset=utf-8" http-equiv="content-type">
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery.query.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
	<link href="../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript">
	var isvalidate = true;
	var issubmit = false;
	var defaulticon = "../images/empty.gif";
		document.onkeydown = function (e) { 
		var theEvent = window.event || e; 
		var code = theEvent.keyCode || theEvent.which; 
			if (code == 13) { 
				$("#btnActionSave").click(); 
			} 
		}
		$(function () {
			
			$("#flowform").validationEngine({
				autoPositionUpdate:true,
				onValidationComplete:function(from,r){
					if(r && isvalidate && !issubmit){
						//start 
						//验证
						var iconurl = $("#action_icon").attr("src");//empty.gif
						if(iconurl.indexOf("empty.gif")>-1){
							iconurl = "";
						}
						var nodeData = {
								id:$("#J_ID").val(),
								name:$("#txtDoName").val(),
								note:"",
								selfnodeid:$.query.get("selfnodeid"),
								nodeid:$.query.get("nodeid"),
								flowid:$.query.get("flowid"),
								moduleid:$("#sl_actions").val(),
								modulename: $("#sl_actions option:selected").text(),
								icon: iconurl,
								iconid:$("#action_icon").attr("name")
							};
						window.parent.e5SelfFlowActionWindowClose(nodeData);
						//end
						issubmit = true;
					}
					else{
						isvalidate = true;
					}
				},
				promptPosition:"centerRight"
			});
			$("#btnActionSave").click(function () {
				
				if($("#sl_actions").val() == null||$("#sl_actions").val()==""){
					alert("<i18n:message key="flow.visual.node.window.noaction"/>");
					
					return;
				}
				$("#flowform").submit();
			});
			 
			$("#btnActionCancle").click(function(){
				var nodeData = {
						id:$("#J_ID").val(),
						name:"",
						note:"",
						selfnodeid:$.query.get("selfnodeid"),
						nodeid:$.query.get("nodeid"),
						flowid:$.query.get("flowid"),
						moduleid:$("#sl_actions").val(),
						modulename: $("#sl_actions option:selected").text(),
						icon: $("#action_icon").attr("src"),
						iconid:$("#action_icon").attr("name")
					};
				window.parent.e5SelfFlowActionWindowClose(nodeData);
			});
			$.ajax({url:"../e5flow/cust_FlowNodeReader.do?method=getFlowNodeActions&docTypeID="+$.query.get("docTypeID"),
				dataType:"json",
				async:true, 
				success:function(data) {
					if(data!=null){
						$("#sl_actions").html("");
						//{"ID":10,"callMode":2,"codeURL":"test.do","dealCount":1,"description":"新建稿件","docTypeID":3,"height":300,"name":"新建稿件","needLock":false,"needLog":false,"needPrompt":false,"needRefresh":false,"resizable":true,"showType":1,"width":400}
						$.each(data,function(i,n){
							$("#sl_actions").append("<option value='"+n.ID+"'>"+n.name+"</option>");
						});
						$.ajax({
							url:"../e5listpage/cust_iconViewReader.do",
							dataType:"json",
							async:true,
							success:function(data){
								if(data!=null){
									//清空内容
									$("#icon-list").empty();
									$.each(data,function(i,n){
										$("#icon-list").append("<li><table><tr><td class='l'></td><td class='c'><img onclick=\"setIcon(this);\" name=\""+n.ID+"\"   alt=\""+n.description+"\" title=\""+n.description+"\" src=\"../"+n.url+"\" /></td><td class='r'></td></tr></table></li>");
									});
									 
									//设置更新数据
									if($.query.get("id")!=""){
										$("#J_ID").val($.query.get("id"));
										$("#txtDoName").val($.query.get("name"))
										//$("#txtDoNote").val($.query.get("note"))
										$("#sl_actions").val($.query.get("moduleid"))
										var iconurl = $.query.get("icon");
										if($.query.get("icon")==null||$.query.get("icon") == ""){
											iconurl = defaulticon;
										}
										$("#action_icon").attr("src",iconurl);
										$("#action_icon").attr("name",$.query.get("iconid"));
									}
									//设置更新数据
									$("#txtDoName").focus();
								}
							}
						});
					}
				}
			});
			$("#txtDoName").focus();
		});
		function setIcon(obj){
			$("#action_icon").attr("src",$(obj).attr("src"));
			$("#action_icon").attr("name",$(obj).attr("name"));
		}
	</script>
	<link rel="stylesheet" type="text/css" href="../e5style/reset.css"/>
	<link rel="stylesheet" type="text/css" href="../e5script/jquery/jquery-ui/jquery-ui.custom.css"/>
	<link rel="stylesheet" type="text/css" href="../e5script/jquery/dialog.style.css"/>
	<link rel="stylesheet" type="text/css" href="../e5style/sys-main-body-style.css"/>
	<link rel="stylesheet" type="text/css" href="../e5style/e5-flow.css"/>
</head>
<body>
	<form name="flowform" id="flowform" method="post" action="">
		<div id="nodewindows" class="nodewindows-flow">
			<table class="table" cellpadding="0" cellspacing="0">
				<tr>
					<td class="w90" style="width:100px"><span class="field-required">*</span><i18n:message key="flow.visual.node.window.doname"/>:</td>
					<td style="width:480px;"><input class="validate[required,custom[onlyCharNumberChiness],maxSize[20]]" type="text" id="txtDoName" />
					<span class="field-help-tootip" title="<i18n:message key="flow.proc.inputvalidate.specialchar"/>">[?]</span>
					</td>
				</tr>
				<tr>
					<td><span class="field-required">*</span><i18n:message key="flow.visual.node.window.domodelname"/>:</td>
					<td><select id="sl_actions"></select></td>
				</tr>
				<tr>
					<td><span class="field-required">*</span><i18n:message key="flow.visual.node.window.actionicon"/>:</td>
					<td><img id="action_icon" alt="" title="" src="../images/empty.gif" /></td>
				</tr>
				<tr>
					<td colspan="2">
						<ul class="m icon-list icon_sortlist clearfix" id="icon-list"></ul>
					</td>
				</tr>
			</table>
		</div>
		<div class="alignCenter m">
			<input id="btnActionSave" class="button"  type="button" value="<i18n:message key="flow.visual.node.window.save"/>" />
			<input id="btnActionCancle"  class="button" type="button" value="<i18n:message key="flow.visual.node.window.cancle"/>" />
		</div>
		<input type="hidden" id="J_ID" value="" />
	</form>
</body>
</html>
