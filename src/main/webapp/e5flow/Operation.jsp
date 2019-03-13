<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5flow" changeResponseLocale="false"/>
<html>
	<head>
		<title><i18n:message key="operation.viewop.contentTitle"/></title>
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<style type="text/css">
		.labelTD {
			MARGIN: 5px 10px 5px 5px;
			PADDING:5px 10px 5px 10px;
		}
		.comment{
			color:gray;
		}
		.input-text{
			width:500px !important;
		}
		</style>
		<script type="text/javascript">
			function doInitSelection(e, strValue)
			{
				if (!e) return;
			
				for (var i = 0; i < e.options.length; i++)
				{
					if (e.options[i].value == strValue)
					{
						e.selectedIndex = i;
						break;
					}
				}
			}
			//提交前校验
			function validate(){
				var s1 = (form1.showType1.checked) ? 1 : 0;
				var s2 = (form1.showType2.checked) ? 2 : 0;
				var s3 = (form1.showType3.checked) ? 4 : 0;
				
				var showType = s3 + s2 + s1;
				
				form1.showtype.value=showType;
				if (showType == 0) {
					if (!window.confirm('<i18n:message key="operation.listOP.dispalert"/>'))
						return false;
				}
				var x= form1.operationName.value.replace(/ /g, "");
				if (x==""){
					window.alert("<i18n:message key="operation.listOP.namealert"/>");
					return false;
				}
					x= form1.codeURL.value.replace(/ /g, "");
					if (x==""){
						window.alert('<i18n:message key="operation.listOP.urlalert"/>');
						return false;
					}
				if (!isInt(form1.width.value)){
					alert("<i18n:message key="operation.listOP.walert" />");
					return false;
				}
				if (!isInt(form1.height.value)){
					alert("<i18n:message key="operation.listOP.halert" />");
					return false;
				}
				return true;
			}
			function isInt(x){
				var i, y;
				for (i=0; i< x.length; i++){
					y= x.charCodeAt(i);
					if ((y>57)||(y<48))
						return false;
				}
				return true;
			}
			function doEdit()
			{
				if(!validate()) return false;
			}
			function doDelete()
			{
				if (window.confirm('<i18n:message key="operation.common.delconfirm"/>'))
				{
					form1.action="OpSubmit.do?del=1";
					form1.submit();
				}
			}
			function doInit() {
				doInitSelection(form1.callMode, <c:out value="${operation.callMode}"/>);	
				var ve = <c:out value="${operation.dealCount}" />;
				if (ve == 2)
					document.getElementById("MDoc2").checked = true;
				else if (ve == 0)
					document.getElementById("MDoc3").checked = true;
				else
					document.getElementById("MDoc1").checked = true;
			}
		</script>
	</head>
<body onload="doInit()">
	<div class="mainBodyWrap">
	<form method="post" action="OpSubmit.do" name="form1">
		<table cellpadding="0" cellspacing="0" class="table">
			<caption><i18n:message key="operation.viewop.contentTitle"/></caption>
			<tr>
				<td>
				<!--Name-->
				<div class="labelTD">
					<Label for="OpName" Accesskey="N"><i18n:message key="operation.listOP.name"/>(<U>N</U>)</Label>
					<INPUT Type="text" class="input-text" Name="operationName" ID="operationName" value="<c:out value="${operation.name}"/>">
				</div>
				<!--Description-->
				<div class="labelTD">
					<Label for="description" Accesskey="D"><i18n:message key="operation.listOP.simple"/>(<U>D</U>)</Label>
					<INPUT Type="text" class="input-text" Name="description" ID="description" value="<c:out value="${operation.description}"/>">
				</div>
				</td>
			</tr>
			<tr>
				<td>
				<div class="labelTD">
					<Label for="callMode" Accesskey="T"><i18n:message key="operation.listOP.showType"/>(<U>T</U>)</Label>
					<SELECT Name="callMode" ID="callMode">
						<Option Value="2" Selected><i18n:message key="operation.listOP.dialog"/></Option>
						<Option Value="1"><i18n:message key="operation.listOP.window"/></Option>
						<Option Value="3"><i18n:message key="operation.listOP.nowindow"/></Option>
					</SELECT>
				</div>
				<!--CallMode-->
				<div class="labelTD">
					<i18n:message key="operation.listOP.page"/>
					<Input Name="width" size="5" class="short" value="<c:out value="${operation.width}"/>">
					<i18n:message key="operation.listOP.multi"/>
					<Input Name="height" size="5" class="short" value="<c:out value="${operation.height}"/>">
					<Input class="noborderinput" Type="CheckBox" Name="resizable" ID="resizable"
					<c:if test="${operation.resizable}">
					checked
					</c:if>>
					<Label for="resizable" Accesskey="J"><i18n:message key="operation.listOP.resizable"/>(<U>J</U>)</Label>
					<div class="comment"><i18n:message key="operation.listOP.default"/></div>
				</div>
				</td>
			</tr>
			<tr>
				<td>
				<div class="labelTD">
					<Label for="codeURL" Accesskey="U">URL(<U>U</U>)</Label>
					<Input Name="codeURL" type="text" class="input-text" Value="<c:out value="${operation.codeURL}"/>">
				</div>
				</td>
			</tr>
			<tr>
				<td>
				<!--批处理-->
				<div class="labelTD">
					<span>
						<INPUT class="noborderinput" Name="dealCount" ID="MDoc1" type="Radio"  value="1">
						<Label for="MDoc1" Accesskey="1"><i18n:message key="operation.listOP.onlyone"/>(<U>1</U>)</Label>
					</span>
					<span>
						<INPUT class="noborderinput" Name="dealCount" ID="MDoc2" type="Radio" value="2">
						<Label for="MDoc2" Accesskey="2"><i18n:message key="operation.listOP.many"/>(<U>2</U>)</Label>
					</span>
					<span>
						<INPUT class="noborderinput" Name="dealCount" ID="MDoc3" type="Radio"  value="0">
						<Label for="MDoc3" Accesskey="0"><i18n:message key="operation.listOP.none"/>(<U>0</U>)</Label>
					</span>
				</div>

				<div class="labelTD">
					<INPUT class="noborderinput" Name="needLog" ID="needLog" type=checkbox
					<c:if test="${operation.needLog}">
						CHECKED
					</c:if>
					>
					<Label for="needLog" Accesskey="L"><i18n:message key="operation.listOP.log" />(<U>L</U>)</Label>
				</div>

				<div class="labelTD">
					<INPUT class="noborderinput" Name="needRefresh" ID="needRefresh" type=checkbox
					<c:if test="${operation.needRefresh}">
						CHECKED
					</c:if>>
					<Label for="needRefresh" Accesskey="R"><i18n:message key="operation.listOP.refresh"/>(<U>R</U>)</Label>
				</div>

				<div class="labelTD">
					<INPUT class="noborderinput" Name="needLock" ID="needLock" type=checkbox
					<c:if test="${operation.needLock}">
						CHECKED
					</c:if>>
					<Label for="needLock" Accesskey="K"><i18n:message key="operation.listOP.lock"/>(<U>K</U>)</Label>
				</div>

				<div class="labelTD">
					<INPUT class="noborderinput" Name="showType1" ID="showType1" type=checkbox
						<c:if test="${operation.showType%2 == 1}">CHECKED</c:if>
					>
					<Label for="showType1" Accesskey="S"><i18n:message key="operation.listOP.showtopview"/>(<U>S</U>)</Label></div>

				<div class="labelTD">
					<INPUT class="noborderinput" Name="showType2" ID="showType2" type=checkbox
						<c:if test="${operation.showType == 2 or operation.showType == 3 or operation.showType == 6 or operation.showType == 7}">CHECKED</c:if>
					>
					<Label for="showType2" Accesskey="R"><i18n:message key="operation.listOP.showsideview"/>(<U>R</U>)</Label>
				</div>
				
				<div class="labelTD">
					<INPUT class="noborderinput" Name="showType3" ID="showType3" type=checkbox
						<c:if test="${operation.showType >= 4}">CHECKED</c:if>
					>
					<Label for="showType3" Accesskey="A">可以显示为列操作(<U>A</U>)</Label>
				</div>

				<!--操作前提示-->
				<div class="labelTD">
					<INPUT class="noborderinput" Name="needPrompt" ID="needPrompt" type=checkbox
					<c:if test="${operation.needPrompt}">CHECKED</c:if>>
					<Label for="needPrompt" Accesskey="P"><i18n:message key="operation.listOP.prompt"/>(<U>P</U>)</Label>
				</div>
				</td>
			</tr>
			<tr>
				<td align="center">
					<Input class="button" type="submit" Name="OpUpdate" onclick="return doEdit();"  AccessKey="E"
						value="<i18n:message key="operation.listOP.submit"/>" />
					<c:if test="${operation.ID > 0}">
						<Input class="button" type="button" Name="OpDelete" onclick="doDelete();"  AccessKey="X"
							value="<i18n:message key="operation.listOP.delete"/>"/>
					</c:if>
				</td>
			</tr>
		</table>
		<Input type="hidden" Name="docTypeID" value="<c:out value="${operation.docTypeID}"/>" />
		<input type="hidden" name="operationID" value="<c:out value="${operation.ID}"/>" />
		<input type="hidden" name="showtype" value="<c:out value="${operation.showType}"/>" />
	</form>
	</div>
	</body>
</html>
