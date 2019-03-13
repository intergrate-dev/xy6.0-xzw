<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<%
	request.setAttribute("onlyWeb", com.founder.xy.config.ConfigReader.onlyWeb());
	request.setAttribute("onlyApp", com.founder.xy.config.ConfigReader.onlyApp());
%>

<HTML>
<HEAD>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
	<style>
		body{
			font-family: "微软雅黑";
			font-size : 14px;
		}
		input{border: 0;}
		#nameDIV{
			background: #F0FAC1;
			padding-top:2px;
			font-size: 15px;
			padding: 3px;
			
	background:#efefef;
	background-image: linear-gradient(bottom, #E4D9D9, #fdfdfd);
	background-image: -o-linear-gradient(bottom, #E4D9D9, #fdfdfd);
	background-image: -moz-linear-gradient(bottom, #E4D9D9, #fdfdfd);
	background-image: -webkit-linear-gradient(bottom, #E4D9D9, #fdfdfd);
	background-image: -ms-linear-gradient(bottom, #E4D9D9, #fdfdfd);

	background-image: -webkit-gradient(
		linear,
		left bottom,
		left top,
		color-stop(0, #E4D9D9),
		color-stop(1, #fdfdfd)
	);
	color:#0078B6;
			
		}
		#hint{color:gray;}
		.th{
			cursor:pointer;
			width:100px;
			white-space:nowrap;
			font-weight: 100;
		}
		label{
			margin: 5px 20px;
		}
		.list{
			width: 93.5%;
		}
		.checkbox{
			margin: 2px;
		}
		.glyphicon-stop{
			font-size : 18px;
			margin: 0 5px 0 15px;
		}
		table{
			margin-top: 10px;
		}
		.font{
			color: #00a0e6;
			float: left;
			margin-left: 15px;
		}
		td span{display:block;width:110px;float:left;}
	</style>
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript">
var p_form = {
	procNames0 : ["推送(渠道)", "推送(客户端)", "专题", "活动", "广告"],
	procNames1 : ["推送(渠道)", "推送(区块)", "合成多标题"],
	onlyWeb : <c:out value="${onlyWeb}"/>,
	onlyApp : <c:out value="${onlyApp}"/>,
	init : function() {
		//取出稿件的所有操作label
		var inputs = $("table[docTypeID='1'] label");
		
		if (p_form.onlyWeb) p_form.tryHide(inputs, p_form.procNames0);
		if (p_form.onlyApp) p_form.tryHide(inputs, p_form.procNames1);
	},
	tryHide : function(inputs, procNames) {
		inputs.each(function(i){
			var text = $(this).text().trim();
			if (p_form.contains(procNames, text)) {
				$(this).hide();
			}
		});
	},
	contains : function(procs, proc) {
		for (var i = 0; i < procs.length; i++) {
			if (procs[i] == proc)
				return true;
		}
		return false;
	}
}
$(function(){
	p_form.init();
});
	//整行选中和取消
		function nodeClick(id)
		{
			var mycheck = document.getElementById(id + "_0");
			if (mycheck == null) return;

			var setcheck = !mycheck.checked;

			var procCount = document.getElementById(id + "_JUMPCOUNT").value;

			for (var i = 0; i < (4 + procCount); i++)
			{
				var mycheck = document.getElementById(id + "_" + i);
				if (mycheck != null) mycheck.checked = setcheck;
			}
		}
		function unflowClick(docTypeID)
		{
			var mycheck = document.getElementById("Unflow_" + docTypeID + "_0");
			if (mycheck == null) return;

			var setcheck = !mycheck.checked;
			var i = 0;
			while (mycheck != null)
			{
				mycheck.checked = setcheck;
				i++;
				mycheck = document.getElementById("Unflow_" + docTypeID + "_" + i);
			}
		}
		//初始化时为“新建文档”的权限设值
		function doInit()
		{
			try{
				var firstNode = permissionForm["FirstNode"];
				if (!firstNode) return;
				for (var i = 0; i < firstNode.length; i++)
				{
					var ids = firstNode[i].value.split("_");
					var flowID = ids[0];
					var nodeID = ids[1];
					
					var mycheck = document.getElementById(nodeID + "_1");
	
					if (mycheck.checked)
						permissionForm["checkNewDoc_" + flowID].checked = true;
					else
						permissionForm["checkNewDoc_" + flowID].checked = false;
				}
			}catch (e){}
		}
		//提交前按上方的“新建文档”checkbox，修改实际的提交值
		function changeNewDoc()
		{
			try{
				var firstNode = permissionForm["FirstNode"];
				if (!firstNode) return;
				
				for (var i = 0; i < firstNode.length; i++)
				{
					var ids = firstNode[i].value.split("_");
					var flowID = ids[0];
					var nodeID = ids[1];
					
					var mycheck = document.getElementById(nodeID + "_1");
					if (permissionForm["checkNewDoc_" + flowID].checked)
						mycheck.checked = true;
					else
						mycheck.checked = false;
				}
			}catch (e){}
		}
	</Script>
</HEAD>
<BODY onload="doInit()">
<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
<Form Name="permissionForm" Target="iframe" Action="./submitFlowBundle.do" Method="Post" onsubmit="changeNewDoc()">
	<Input Type="hidden" Name="RoleID" id="RoleID" Value="<c:out value="${sessionScope.permissionRoleID}"/>">
	<!--每个文档类型-->
	<c:forEach var="docType" items="${flowList}" varStatus="docTypeIndex">
	<c:if test="${docType.docTypeName != '原稿'}">
		<Input Type="hidden" Name="DocTypeID" id="DocTypeID" Value="<c:out value="${docType.docTypeID}"/>">
		<div id="nameDIV"><font class="font" size="4">|</font><c:out value="${docType.docTypeName}"/></div>
		<!--非流程操作-->
		<c:if test="${docType.unflowCount > 0}">
			<Table border="0" cellpadding="4" cellspacing="0" class="onlyborder" docTypeID="<c:out value="${docType.docTypeID}"/>">
				<Input Type="hidden" Name="UnflowCount_<c:out value="${docType.docTypeID}"/>" 
					id="UnflowCount_<c:out value="${docType.docTypeID}"/>" 
					Value="<c:out value="${docType.unflowCount}"/>">
				<TR>
					<TH class="th" onclick="unflowClick(<c:out value="${docType.docTypeID}"/>)"
						style="padding-left:0px;" nowrap>
						<span class="glyphicon-stop">◆</span><font size="2">普通操作</font>
					</TH>
					<td class="list" style="padding-left:20px;">
					<c:forEach var="unflow" items="${docType.unflowArr}" varStatus="status">
						<c:if test="${status.index % 8 == 0 and status.index > 0}"><br style="clear:both;"/></c:if>
						<span>
							<label for="Unflow_<c:out value="${docType.docTypeID}"/>_<c:out value="${status.index}"/>">
								<input style="margin-top: 2px; margin-right: 5px;" class="checkbox" type="checkbox" 
									name="Unflow_<c:out value="${docType.docTypeID}"/>_<c:out value="${status.index}"/>"
									id="Unflow_<c:out value="${docType.docTypeID}"/>_<c:out value="${status.index}"/>"
									<c:if test="${unflow.procID == 1}">checked</c:if>
								/>
								<c:out value="${unflow.procName}"/>
							</label>
						</span>
					</c:forEach>
					</td>
				</TR>
			</Table>
		</c:if>
		<!--每个流程-->
		<c:forEach var="flow" items="${docType.flows}" varStatus="flowIndex">
		<BR/>
		<Table border="0" cellpadding="4" cellspacing="0" class="onlyBorder" docTypeID="<c:out value="${docType.docTypeID}"/>">
			<Input Type="hidden" Name="FlowID_<c:out value="${docType.docTypeID}"/>" 
				id="FlowID_<c:out value="${docType.docTypeID}"/>" 
				Value="<c:out value="${flow.flowID}"/>">
			<span class="glyphicon-stop">◆</span><font color="#333" size="2"><c:out value="${flow.flowName}"/></font>
			<!--新建权限-->
			<div style="display:none">
			<label for="checkNewDoc_<c:out value="${flow.flowID}"/>" 
				title="<i18n:message key="flow.newDoc"/><i18n:message key="flow.newdoc.hint"/>">
				<input style="margin-top: 2px; margin-right: 5px;" type="checkbox" name="checkNewDoc_<c:out value="${flow.flowID}"/>" 
					id="checkNewDoc_<c:out value="${flow.flowID}"/>"/>
				<font color="blue"><i18n:message key="flow.newDoc"/></font>
			</label>
			</div>
	
			<!--每个流程节点-->
			<c:forEach var="flowPermission" items="${flow.procList}" varStatus="nodeIndex">
				<TR>
					<TH class="th" onclick="nodeClick(<c:out value="${flowPermission.nodeID}"/>)">
							<c:out value="${flowPermission.nodeName}"/>
						<Input Type="hidden" Name="NodeID_<c:out value="${flow.flowID}"/>" 
							id="NodeID_<c:out value="${flow.flowID}"/>" 
							Value="<c:out value="${flowPermission.nodeID}"/>">
						<Input Type="hidden" Name="<c:out value="${flowPermission.nodeID}_JUMPCOUNT"/>"
							id="<c:out value="${flowPermission.nodeID}_JUMPCOUNT"/>"
							Value="<c:out value="${flowPermission.jumpCount}"/>">
					</TH>
					<td>
					<span style="display:none"><!--Read-->
						<label for="<c:out value="${flowPermission.nodeID}"/>_0">
							<input style="margin-top: 2px; margin-right: 5px;" class="checkbox"  type="checkbox"
								name="<c:out value="${flowPermission.nodeID}"/>_0"
								id="<c:out value="${flowPermission.nodeID}"/>_0"
								<c:if test="${flowPermission.canRead}">checked</c:if>
							/>
							<font color="blue"><i18n:message key="flow.read"/></font>
						</label>
					</span>
					<span><!--Back or NewDoc-->
					<c:choose>
						<c:when test="${flowPermission.first}">
							<input class="checkbox" type="checkbox" style="display:none;"
									name="<c:out value="${flowPermission.nodeID}"/>_1"
									id="<c:out value="${flowPermission.nodeID}"/>_1"
									<c:if test="${flowPermission.procBack.enabled}">checked</c:if>
							/>
							<input type="hidden" name="FirstNode" id="FirstNode" 
								value="<c:out value="${flow.flowID}"/>_<c:out value="${flowPermission.nodeID}"/>" />
						</c:when>
						<c:when test="${flowPermission.procBack.name != ''}">
							<label for="<c:out value="${flowPermission.nodeID}"/>_1">
								<input style="margin-top: 2px; margin-right: 5px;" class="checkbox" type="checkbox"
										name="<c:out value="${flowPermission.nodeID}"/>_1"
										id="<c:out value="${flowPermission.nodeID}"/>_1"
										<c:if test="${flowPermission.procBack.enabled}">checked</c:if>
								/>
								<c:out value="${flowPermission.procBack.name}"/>
							</label>
						</c:when>
					</c:choose>
					</span>
					<span><!--Do-->
					<c:if test="${flowPermission.procDo.name != ''}">
						<label for="<c:out value="${flowPermission.nodeID}"/>_2">
							<input style="margin-top: 2px; margin-right: 5px;" class="checkbox" type="checkbox"
									name="<c:out value="${flowPermission.nodeID}"/>_2"
									id="<c:out value="${flowPermission.nodeID}"/>_2"
									<c:if test="${flowPermission.procDo.enabled}">checked</c:if>
							/>
							<c:out value="${flowPermission.procDo.name}"/>
						</label>
					</c:if>
					</span>
					<span><!--Go-->
					<c:if test="${flowPermission.procGo.name != ''}">
						<label for="<c:out value="${flowPermission.nodeID}"/>_3">
							<input style="margin-top: 2px; margin-right: 5px;" class="checkbox" type="checkbox"
								name="<c:out value="${flowPermission.nodeID}"/>_3"
								id="<c:out value="${flowPermission.nodeID}"/>_3"
								<c:if test="${flowPermission.procGo.enabled}">checked</c:if>
							/>
							<c:out value="${flowPermission.procGo.name}"/>
						</label>
					</c:if>
					</span>
					<c:forEach var="jumpPermission" items="${flowPermission.procJump}" varStatus="status">
						<span><!--Jump-->
							<label for="<c:out value="${flowPermission.nodeID}"/>_<c:out value="${status.index + 4}"/>">
								<input style="margin-top: 2px; margin-right: 5px;"  class="checkbox" type="checkbox"
									name="<c:out value="${flowPermission.nodeID}"/>_<c:out value="${status.index + 4}"/>"
									id="<c:out value="${flowPermission.nodeID}"/>_<c:out value="${status.index + 4}"/>"
									<c:if test="${jumpPermission.enabled}">checked</c:if>
								/>
								<c:out value="${jumpPermission.name}"/>
							</label>
						</span>
					</c:forEach>
					</td>
				</TR>
			</c:forEach>
		</Table>
		</c:forEach>
		<br/><br/>
	</c:if>
	</c:forEach>
	<DIV><input Type="submit" AccessKey="S" class="button" value="<i18n:message key="button.submit"/>"/></DIV>
</Form>
<BR>
<DIV id="hint"><i18n:message key="flow.hint"/></DIV>
</BODY>
</HTML>
