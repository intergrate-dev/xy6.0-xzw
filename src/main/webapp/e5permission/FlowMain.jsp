<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<HTML>
<HEAD>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
	<style>
		input{border: 0;}
		th{cursor:hand;background-color:#CCCCCC;}
		#nameDiv{
			height: 32px;
			padding-top:5px;
		}
		#hint{color:gray;}
	</style>
	<script type="text/javascript">
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
		//初始化时为“新建文档”的权限设值
		function doInit()
		{
			try{
				var firstNode = permissionForm.FirstNode.value;
				var mycheck = document.getElementById(firstNode + "_1");

				if (mycheck.checked)
					permissionForm["checkNewDoc"].checked = true;
				else
					permissionForm["checkNewDoc"].checked = false;
			}catch (e){}
		}
		//提交前按上方的“新建文档”checkbox，修改实际的提交值
		function changeNewDoc()
		{
			try{
				var firstNode = permissionForm.FirstNode.value;
				var mycheck = document.getElementById(firstNode + "_1");
				if (permissionForm["checkNewDoc"].checked)
					mycheck.checked = true;
				else
					mycheck.checked = false;
			}catch (e){}
		}
	</Script>
</HEAD>
<BODY onload="doInit()">
	<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
	<BR>
	<div id="nameDiv"><i18n:message key="flow.currentFlow"/><c:out value="${flowName}"/></div>
	<Form Name="permissionForm" Target="iframe" Action="./submitFlow.do" Method="Post" onsubmit="changeNewDoc()">
	<Table border="0" cellpadding="4" cellspacing="0" class="onlyBorder">
		<Input Type="hidden" Name="RoleID" Value="<c:out value="${sessionScope.permissionRoleID}"/>">
		<Input Type="hidden" Name="FlowID" Value="<c:out value="${flowID}"/>">
		<input type="checkbox" name="checkNewDoc" id="checkNewDoc"/>
		<label for="checkNewDoc"><font color="blue"><i18n:message key="flow.newDoc"/></font></label>
		<i18n:message key="flow.newdoc.hint"/><br/>

		<c:forEach var="flowPermission" items="${flowPermissions}" varStatus="nodeIndex">
			<Input Type="hidden" Name="NodeID" Value="<c:out value="${flowPermission.nodeID}"/>">
			<Input Type="hidden" Name="<c:out value="${flowPermission.nodeID}_JUMPCOUNT"/>"
				Value="<c:out value="${flowPermission.jumpCount}"/>">
		<TR>
			<TH class="bottomlinetd" onclick="nodeClick(<c:out value="${flowPermission.nodeID}"/>)">
				<c:out value="${flowPermission.nodeName}"/>
			</TH>
			<TD class="bottomlinetd"><!--Read-->
				<input type="checkbox"
					name="<c:out value="${flowPermission.nodeID}"/>_0"
					<c:if test="${flowPermission.canRead}">checked</c:if>
				/><font color="blue"><i18n:message key="flow.read"/></font>
			</TD>
			<TD class="bottomlinetd"><!--Back or NewDoc-->
			<c:choose>
				<c:when test="${flowPermission.first}">
					<input type="checkbox" style="display:none;"
							name="<c:out value="${flowPermission.nodeID}"/>_1"
							<c:if test="${flowPermission.procBack.enabled}">checked</c:if>
					/>
					<input type="hidden" name="FirstNode" value="<c:out value="${flowPermission.nodeID}"/>" />
				</c:when>
				<c:when test="${flowPermission.procBack.name != ''}">
					<input type="checkbox"
							name="<c:out value="${flowPermission.nodeID}"/>_1"
							<c:if test="${flowPermission.procBack.enabled}">checked</c:if>
					/>
					<c:out value="${flowPermission.procBack.name}"/>
				</c:when>
			</c:choose>
			</TD>
			<TD class="bottomlinetd"><!--Do-->
			<c:if test="${flowPermission.procDo.name != ''}">
				<input type="checkbox"
						name="<c:out value="${flowPermission.nodeID}"/>_2"
						<c:if test="${flowPermission.procDo.enabled}">checked</c:if>
				/>
				<c:out value="${flowPermission.procDo.name}"/>
			</c:if>
			</TD>
			<TD class="bottomlinetd"><!--Go-->
			<c:if test="${flowPermission.procGo.name != ''}">
				<input type="checkbox"
					name="<c:out value="${flowPermission.nodeID}"/>_3"
					<c:if test="${flowPermission.procGo.enabled}">checked</c:if>
				/>
				<c:out value="${flowPermission.procGo.name}"/>
			</c:if>
			</TD>
			<c:forEach var="jumpPermission" items="${flowPermission.procJump}" varStatus="status">
				<TD class="bottomlinetd"><!--Jump-->
					<input type="checkbox"
						name="<c:out value="${flowPermission.nodeID}"/>_<c:out value="${status.index + 4}"/>"
						<c:if test="${jumpPermission.enabled}">checked</c:if>
					/>
					<c:out value="${jumpPermission.name}"/>
				</TD>
			</c:forEach>
		</TR>
		</c:forEach>
	</Table>
	<BR>
	<DIV><input Type="submit" AccessKey="S" class="button" value="<i18n:message key="button.submit"/>"/></DIV>
	</Form>
	<BR>
	<DIV id="hint"><i18n:message key="flow.hint"/></DIV>
</BODY>
</HTML>
