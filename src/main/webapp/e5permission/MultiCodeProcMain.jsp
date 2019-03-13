<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<HTML>
<HEAD>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
	<style>
		input{border: 0;}
		th{cursor:hand;background-color:#EEEEEE;}
		#hint{color:gray;}
		.bottomlinetd{white-space:nowrap;}
		#nameDIV{height:32px;padding-top:5px;}
		#titleDiv{
			background-color:#666666;
			height: 25px;
			padding-top:2px;
			color:white;
			text-align:center;
			width:300px;
		}
	</style>
	<script type="text/javascript">
	//整行选中和取消
		function nodeClick(id)
		{
			var mycheck = document.getElementById(id + "_1");		//back
			if (mycheck == null) mycheck = document.getElementById(id + "_2");//do
			if (mycheck == null) mycheck = document.getElementById(id + "_3");//go
			if (mycheck == null) mycheck = document.getElementById(id + "_4");//jump 1
			if (mycheck == null) return;

			var setcheck = !mycheck.checked;

			var procCount = document.getElementById(id + "_JUMPCOUNT").value;

			for (var i = 1; i < (4 + procCount); i++)
			{
				var mycheck = document.getElementById(id + "_" + i);
				if (mycheck) mycheck.checked = setcheck;
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
		function fvClick()
		{
			var mycheck = document.getElementById("FVCanRead");
			var setcheck = !mycheck.checked;

			mycheck.checked = setcheck;
			
			mycheck = document.getElementById("FVCanTransfer");
			mycheck.checked = setcheck;
		}
		function doSubmit() {
			permissionForm.Inherit.value = "0";
			permissionForm.submit();
		}
		function doInherit() {
			if (!confirm("<i18n:message key="button.multiproc.inheritsubmit.confirm"/>")) return;
			permissionForm.Inherit.value = "1";
			permissionForm.submit();
		}
	</Script>
</HEAD>
<BODY>
<div id="nameDIV"><i18n:message key="multiproc.title"/></div>
<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
<Form Name="permissionForm" Target="iframe" Action="./MultiCodeProcSubmit.do" Method="Post">
	<Input Type="hidden" Name="RoleID" Value="<c:out value="${roleID}"/>">
	<Input Type="hidden" Name="FVID" Value="<c:out value="${fvID}"/>">
	<Input Type="hidden" Name="Code" Value="<c:out value="${code}"/>">	
	<Input Type="hidden" Name="Inherit" Value="0">
	
	<Table border="0" cellpadding="4" cellspacing="0" class="onlyBorder" style="width:300px;">
		<TR>
			<TH class="bottomlinetd" style="background-color:#8888FF; color:white;width:70px;"
				onclick="fvClick()">
				<i18n:message key="multiproc.fv.title"/>
			</TH>
			<TD class="bottomlinetd" style="background-color:#AAAAFF; color:white;">
				<input type="checkbox" name="FVCanRead" id="FVCanRead"
					<c:if test="${fvCanRead}">checked</c:if>
				/>
				<label for="FVCanRead"><i18n:message key="fvproc.fv.canread"/></label>
			</TD>
			<TD class="bottomlinetd" style="background-color:#AAAAFF; color:white;">
				<input type="checkbox" name="FVCanTransfer" id="FVCanTransfer"
					<c:if test="${fvCanTransfer}">checked</c:if>
				/>
				<label for="FVCanTransfer"><i18n:message key="fvproc.fv.cantransfer"/></label>
			</TD>
		</TR>
	</Table>
	<br/>
<!--每个文档类型-->
<c:forEach var="docType" items="${flowList}" varStatus="docTypeIndex">
	<Input Type="hidden" Name="DocTypeID" Value="<c:out value="${docType.docTypeID}"/>">
	<div id="titleDiv"><c:out value="${docType.docTypeName}"/></div>
	<!--每个流程-->
	<c:forEach var="flow" items="${docType.flows}" varStatus="flowIndex">
		
	<Table border="0" cellpadding="4" cellspacing="0" class="onlyBorder">
		<Input Type="hidden" Name="FlowID_<c:out value="${docType.docTypeID}"/>" 
				Value="<c:out value="${flow.flowID}"/>">
		<font color="blue"><c:out value="${flow.flowName}"/></font>

		<!--每个流程节点-->
		<c:forEach var="flowPermission" items="${flow.procList}" varStatus="nodeIndex">
			<Input Type="hidden" Name="NodeID_<c:out value="${flow.flowID}"/>" 
				Value="<c:out value="${flowPermission.nodeID}"/>">
			<Input Type="hidden" Name="<c:out value="${flowPermission.nodeID}_JUMPCOUNT"/>"
				id="<c:out value="${flowPermission.nodeID}_JUMPCOUNT"/>"
				Value="<c:out value="${flowPermission.jumpCount}"/>">
			<TR>
				<TH class="bottomlinetd" onclick="nodeClick(<c:out value="${flowPermission.nodeID}"/>)">
					<c:out value="${flowPermission.nodeName}"/>
				</TH>
				<TD class="bottomlinetd"><!--Back-->
					<c:if test="${flowPermission.procBack != null}">
						<input type="checkbox"
								name="<c:out value="${flowPermission.nodeID}"/>_1"
								id="<c:out value="${flowPermission.nodeID}"/>_1"
								<c:if test="${flowPermission.procBack.enabled}">checked</c:if>
						/>
						<label for="<c:out value="${flowPermission.nodeID}"/>_1">
							<c:out value="${flowPermission.procBack.name}"/>
						</label>
						<input type="hidden" name="Proc_<c:out value="${flowPermission.nodeID}"/>_1" 
							value="<c:out value="${flowPermission.procBack.id}"/>"/>
					</c:if>
				</TD>
				<TD class="bottomlinetd"><!--Do-->
				<c:if test="${flowPermission.procDo != null}">
					<input type="checkbox"
							name="<c:out value="${flowPermission.nodeID}"/>_2"
							id="<c:out value="${flowPermission.nodeID}"/>_2"
							<c:if test="${flowPermission.procDo.enabled}">checked</c:if>
					/>
					<label for="<c:out value="${flowPermission.nodeID}"/>_2">
						<c:out value="${flowPermission.procDo.name}"/>
					</label>
					<input type="hidden" name="Proc_<c:out value="${flowPermission.nodeID}"/>_2" 
						value="<c:out value="${flowPermission.procDo.id}"/>"/>
				</c:if>
				</TD>
				<TD class="bottomlinetd"><!--Go-->
				<c:if test="${flowPermission.procGo != null}">
					<input type="checkbox"
						name="<c:out value="${flowPermission.nodeID}"/>_3"
						id="<c:out value="${flowPermission.nodeID}"/>_3"
						<c:if test="${flowPermission.procGo.enabled}">checked</c:if>
					/>
					<label for="<c:out value="${flowPermission.nodeID}"/>_3">
						<c:out value="${flowPermission.procGo.name}"/>
					</label>
					<input type="hidden" name="Proc_<c:out value="${flowPermission.nodeID}"/>_3" 
						value="<c:out value="${flowPermission.procGo.id}"/>"/>
				</c:if>
				</TD>
				<c:forEach var="jumpPermission" items="${flowPermission.procJump}" varStatus="status">
					<TD class="bottomlinetd"><!--Jump-->
						<input type="checkbox"
							name="<c:out value="${flowPermission.nodeID}"/>_<c:out value="${status.index + 4}"/>"
							id="<c:out value="${flowPermission.nodeID}"/>_<c:out value="${status.index + 4}"/>"
							<c:if test="${jumpPermission.enabled}">checked</c:if>
						/>
						<label for="<c:out value="${flowPermission.nodeID}"/>_<c:out value="${status.index + 4}"/>">
							<c:out value="${jumpPermission.name}"/>
						</label>
						<input type="hidden" name="Proc_<c:out value="${flowPermission.nodeID}"/>_<c:out value="${status.index + 4}"/>" 
							value="<c:out value="${jumpPermission.id}"/>"/>
					</TD>
				</c:forEach>
			</TR>
		</c:forEach>
	</Table>
	</c:forEach>
	<br/>
	<!--非流程操作-->
	<c:if test="${docType.unflowCount > 0}">
		<Table border="0" cellpadding="4" cellspacing="0" class="onlyborder">
			<Input Type="hidden" Name="UnflowCount_<c:out value="${docType.docTypeID}"/>" 
					Value="<c:out value="${docType.unflowCount}"/>">
			<TR>
				<TH onclick="unflowClick(<c:out value="${docType.docTypeID}"/>)" width="100" nowrap>
					<i18n:message key="flow.unflow"/>
				</TH>
				<c:forEach var="unflow" items="${docType.unflowArr}" varStatus="status">
					<TD class="bottomlinetd">
						<input type="hidden" name="UnflowProc_<c:out value="${docType.docTypeID}"/>_<c:out value="${status.index}"/>" 
								value="<c:out value="${unflow.procID}"/>"/>
						<input type="checkbox" 
							name="Unflow_<c:out value="${docType.docTypeID}"/>_<c:out value="${status.index}"/>"
							id="Unflow_<c:out value="${docType.docTypeID}"/>_<c:out value="${status.index}"/>"
							<c:if test="${unflow.opID == 1}">checked</c:if>
						/>
						<label for="Unflow_<c:out value="${docType.docTypeID}"/>_<c:out value="${status.index}"/>">
							<c:out value="${unflow.procName}"/>
						</label>
					</TD>
				</c:forEach>
			</TR>
		</Table>
	</c:if>
	<BR/>
</c:forEach>
	<DIV>
		<input Type="button" AccessKey="C" class="button" onclick="doInherit()"
			value="<i18n:message key="button.fvproc.inheritsubmit"/>" title="<i18n:message key="button.multiproc.inheritsubmit.hint"/>"/>
		&nbsp;&nbsp;
		<input Type="button" AccessKey="S" class="button"  onclick="doSubmit()"
			value="<i18n:message key="button.submit"/>" title="<i18n:message key="button.multiproc.submit.hint"/>"/>
	</DIV>
</Form>
<BR>
<DIV id="hint"><i18n:message key="flow.hint"/></DIV>
</BODY>
</HTML>
