<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5permission" changeResponseLocale="false"/>
<HTML>
<HEAD>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
	<style>
		body{overflow:auto;padding-bottom:50px;}
		th{cursor:hand;white-space:nowrap;}
		#hint{color:gray;}
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
		//整列选中和取消
		function lineClick(index, nodes)
		{
			var nodeIDs = nodes.split(",");
			
			var mycheck = null;
			for (var i = 0; i < nodeIDs.length; i++) {
				mycheck = document.getElementById(nodeIDs[i] + "_" + index);
				if (mycheck) break;
			}
			if (!mycheck) return;

			var setcheck = !mycheck.checked;

			for (var i = 0; i < nodeIDs.length; i++) {
				mycheck = document.getElementById(nodeIDs[i] + "_" + index);
				if (mycheck) mycheck.checked = setcheck;
			}
		}
		function unflowClick()
		{
			var mycheck = document.getElementById("Unflow_0");
			if (mycheck == null) return;

			var setcheck = !mycheck.checked;
			var i = 0;
			while (mycheck != null)
			{
				mycheck.checked = setcheck;
				i++;
				mycheck = document.getElementById("Unflow_" + i);
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
			if (!confirm("<i18n:message key="button.fvproc.inheritsubmit.confirm"/>")) return;
			permissionForm.Inherit.value = "1";
			permissionForm.submit();
		}
	</Script>
</HEAD>
<BODY>
<IFrame id="iframe" name="iframe" style="display:none"></IFrame>
<Form Name="permissionForm" Target="iframe" Action="./FVProcPermissionSubmit.do" Method="Post">
	<Input Type="hidden" Name="RoleID" Value="<c:out value="${roleID}"/>">
	<Input Type="hidden" Name="FVID" Value="<c:out value="${fvID}"/>">
	<Input Type="hidden" Name="Code" Value="<c:out value="${code}"/>">	
	<Input Type="hidden" Name="Inherit" Value="0">
	
	<Table class="table">
		<caption><i18n:message key="fvproc.title"/>---<c:out value="${fvName}"/></caption>
		<TR>
			<TH style="background-color:#cccc88; width:120px;" onclick="fvClick()">
				<i18n:message key="fvproc.fv.title"/>
			</TH>
			<TD>
				<input type="checkbox" name="FVCanRead" id="FVCanRead"
					<c:if test="${fvCanRead}">checked</c:if>
				/><label for="FVCanRead">
					<i18n:message key="fvproc.fv.canread"/>
				</label>
			</TD>
			<TD>
				<input type="checkbox" name="FVCanTransfer" id="FVCanTransfer"
					<c:if test="${fvCanTransfer}">checked</c:if>
				/><label for="FVCanTransfer">
					<i18n:message key="fvproc.fv.cantransfer"/>
				</label>
			</TD>
		</TR>
	</Table>
	<!--每个流程-->
	<c:forEach var="flow" items="${docType.flows}" varStatus="flowIndex">
	<Table class="table">
		<caption style="text-align:left;background-color:white;color:black;"><c:out value="${flow.flowName}"/></caption>
		<Input Type="hidden" Name="FlowID" Value="<c:out value="${flow.flowID}"/>">
		<c:if test="${useLineSelect}">
		<tr><!--列全选-->
			<th></th>
			<c:forEach var="x" begin="0" end="${3 + flow.maxJumpCountPerNode - 1}" step="1">
				<th><span title="<i18n:message key="info.fvproc.lineselect.hint"/>"
					onclick="lineClick(<c:out value="${x + 1}"/>, '<c:forEach var="flowPermission" items="${flow.procList}"><c:out value="${flowPermission.nodeID}"/>,</c:forEach>')"
					><i18n:message key="info.fvproc.lineselect"/></span>
				</th>
			</c:forEach>
		</tr>
		</c:if>
		<!--每个流程节点-->
		<c:forEach var="flowPermission" items="${flow.procList}" varStatus="nodeIndex">
			<Input Type="hidden" Name="NodeID_<c:out value="${flow.flowID}"/>" 
				Value="<c:out value="${flowPermission.nodeID}"/>">
			<Input Type="hidden" Name="<c:out value="${flowPermission.nodeID}_JUMPCOUNT"/>"
				id="<c:out value="${flowPermission.nodeID}_JUMPCOUNT"/>"
				Value="<c:out value="${flowPermission.jumpCount}"/>">
			<TR>
				<TH onclick="nodeClick(<c:out value="${flowPermission.nodeID}"/>)" style="width:120px;">
					<c:out value="${flowPermission.nodeName}"/>
				</TH>
				<TD><!--Back-->
					<c:if test="${flowPermission.procBack != null}">
						<input type="checkbox"
								name="<c:out value="${flowPermission.nodeID}"/>_1"
								id="<c:out value="${flowPermission.nodeID}"/>_1"
								<c:if test="${flowPermission.procBack.enabled}">checked</c:if>
						/><label for="<c:out value="${flowPermission.nodeID}"/>_1">
							<c:out value="${flowPermission.procBack.name}"/>
						</label>
						<input type="hidden" name="Proc_<c:out value="${flowPermission.nodeID}"/>_1" 
							value="<c:out value="${flowPermission.procBack.id}"/>"/>
					</c:if>
				</TD>
				<TD><!--Do-->
				<c:if test="${flowPermission.procDo != null}">
					<input type="checkbox"
							name="<c:out value="${flowPermission.nodeID}"/>_2"
							id="<c:out value="${flowPermission.nodeID}"/>_2"
							<c:if test="${flowPermission.procDo.enabled}">checked</c:if>
					/><label for="<c:out value="${flowPermission.nodeID}"/>_2">
						<c:out value="${flowPermission.procDo.name}"/>
					</label>
					<input type="hidden" name="Proc_<c:out value="${flowPermission.nodeID}"/>_2" 
						value="<c:out value="${flowPermission.procDo.id}"/>"/>
				</c:if>
				</TD>
				<TD><!--Go-->
				<c:if test="${flowPermission.procGo != null}">
					<input type="checkbox"
						name="<c:out value="${flowPermission.nodeID}"/>_3"
						id="<c:out value="${flowPermission.nodeID}"/>_3"
						<c:if test="${flowPermission.procGo.enabled}">checked</c:if>
					/><label for="<c:out value="${flowPermission.nodeID}"/>_3">
						<c:out value="${flowPermission.procGo.name}"/>
					</label>
					<input type="hidden" name="Proc_<c:out value="${flowPermission.nodeID}"/>_3" 
						value="<c:out value="${flowPermission.procGo.id}"/>"/>
				</c:if>
				</TD>
				<c:forEach var="jumpPermission" items="${flowPermission.procJump}" varStatus="status">
					<TD><!--Jump-->
						<input type="checkbox"
							name="<c:out value="${flowPermission.nodeID}"/>_<c:out value="${status.index + 4}"/>"
							id="<c:out value="${flowPermission.nodeID}"/>_<c:out value="${status.index + 4}"/>"
							<c:if test="${jumpPermission.enabled}">checked</c:if>
						/><label for="<c:out value="${flowPermission.nodeID}"/>_<c:out value="${status.index + 4}"/>">
							<c:out value="${jumpPermission.name}"/>
						</label>
						<input type="hidden" name="Proc_<c:out value="${flowPermission.nodeID}"/>_<c:out value="${status.index + 4}"/>" 
							value="<c:out value="${jumpPermission.id}"/>"/>
					</TD>
				</c:forEach>
				<c:if test="${flowPermission.fixCount > 0}">
				<c:forEach var="x" begin="0" end="${flowPermission.fixCount - 1}" step="1">
					<td>&nbsp;</td>
				</c:forEach>
				</c:if>
			</TR>
		</c:forEach>
	</Table>
	</c:forEach>
	<br/>
	<!--非流程操作-->
	<c:if test="${docType.unflowCount > 0}">
		<Table class="table">
			<Input Type="hidden" Name="UnflowCount" Value="<c:out value="${docType.unflowCount}"/>">
			<TR>
				<TH onclick="unflowClick()" style="width:80px;">
					<i18n:message key="flow.unflow"/>
				</TH>
				<c:forEach var="unflow" items="${docType.unflowArr}" varStatus="status">
					<TD>
						<input type="hidden" name="UnflowProc_<c:out value="${status.index}"/>" value="<c:out value="${unflow.procID}"/>"/>
						<input type="checkbox" 
							name="Unflow_<c:out value="${status.index}"/>"
							id="Unflow_<c:out value="${status.index}"/>"
							<c:if test="${unflow.opID == 1}">checked</c:if>
						/><label for="Unflow_<c:out value="${status.index}"/>">
							<c:out value="${unflow.procName}"/>
						</label>
					</TD>
				</c:forEach>
			</TR>
		</Table>
	</c:if>
	<BR/>
	<DIV>
		<input Type="button" AccessKey="C" class="button" onclick="doInherit()"
			value="<i18n:message key="button.fvproc.inheritsubmit"/>" title="<i18n:message key="button.fvproc.inheritsubmit.hint"/>"/>
		&nbsp;&nbsp;
		<input Type="button" AccessKey="S" class="button"  onclick="doSubmit()"
			value="<i18n:message key="button.submit"/>" title="<i18n:message key="button.fvproc.submit.hint"/>"/>
	</DIV>
</Form>
<BR>
<DIV id="hint"><i18n:message key="flow.hint"/></DIV>
</BODY>
</HTML>
