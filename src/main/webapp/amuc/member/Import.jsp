<%@page import="com.founder.e5.dom.DocLib"%>
<%@page import="com.founder.amuc.commons.Constant"%>
<%@page import="com.founder.amuc.commons.InfoHelper"%>
<%@ include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + 
	":" + request.getServerPort() + request.getContextPath() + "/";
DocLib amDocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBERACTION, InfoHelper.getTenantCode(request));
DocLib gDocLib = InfoHelper.getLib(Constant.DOCTYPE_GROUP, InfoHelper.getTenantCode(request));

DocLib mDocLib = InfoHelper.getLib(Constant.DOCTYPE_MEMBER, InfoHelper.getTenantCode(request));//fanjc
String curDocLibID = request.getParameter("DocLibID");
%>
<html>
<head>
	<title>导入</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link rel="StyleSheet" type="text/css" href="../../e5style/reset.css">
	<link rel="stylesheet" type="text/css" href="../../e5style/sys-main-body-style.css"/>
	<link type="text/css" rel="stylesheet" href="../../amuc/script/bootstrap/css/bootstrap.css"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="script/import.js"></script>
    <style type="text/css">
        .import_hint {
            border: 5px solid #939393;
            width: 250px;
            height: 50px;
            line-height: 55px;
            padding: 0 20px;
            position: absolute;
            left: 50%;
            margin-left: -140px;
            top: 50%;
            margin-top: -40px;
            font-size: 15px;
            color: #333;
            font-weight: bold;
            text-align: center;
            background-color: #f9f9f9;
			display:none;
        }
        .import_hint img {
            position: relative;
            top: 10px;
            left: -8px;
        }
        .output{
        	position: relative;
            top: 10px;
            left: 5px;
            line-height: 30px;
            padding: 0 20px;
        	font-size: 15px;
        	font-weight: bold;
        	width: 250px;
            height: 30px;
        }
    </style>
</head>
<body>
	<div class="output">
	<%if(amDocLib.getDocLibID() == Integer.valueOf(curDocLibID)){ %>
		<a class="btn" href="<%=basePath %>amuc/member/活动会员导入模板.xlsx">下载导入模板</a>
	<%} else if(gDocLib.getDocLibID() == Integer.valueOf(curDocLibID)){ %>
		<a class="btn" href="<%=basePath %>amuc/member/群成员模板.xlsx">下载导入模板</a>
	<%} else if(mDocLib.getDocLibID() == Integer.valueOf(curDocLibID)){ %>
		<a class="btn" href="<%=basePath %>amuc/member/会员导入模板.xlsx">下载导入模板</a>
	<%} %>
	</div>
	<form name="postForm" id="postForm" method="post" action="Import.do">
		<input type="hidden" id="DocLibID" name="DocLibID" value="<c:out value="${param.DocLibID}"/>"/>
		<input type="hidden" id="DocIDs" name="DocIDs" value="<c:out value="${param.DocIDs}"/>"/>
		<input type="hidden" id="UUID" name="UUID" value="<c:out value="${param.UUID}"/>"/>
		<input type="hidden" name="upFile" id="upFile" value=""/>
		<input type="hidden" name="upSheet" id="upSheet" value=""/>
		<input type="hidden" name="upFields" id="upFields" value=""/>
		<input type="hidden" name="upHeaders" id="upHeaders" value=""/>
		<input type="hidden" name="Potential" id="Potential" value=""/>
		<input type="hidden" id="isGroup"name="isGroup"  value="<c:out value="${param.group}"/>"/>
		<input type="hidden" id="isMember"name="isMember"  value="<c:out value="${param.member}"/>"/>
		<input type="hidden" id="isAction" name="isAction" value="<c:out value="${param.action}"/>"/>
		<input type="hidden" id="isApply" name="isApply"  value="<c:out value="${param.apply}"/>"/>
		<input type="hidden" id="isSignin" name="isSignin" value="<c:out value="${param.signin}"/>"/>
		<input type="hidden" id="ruleFormula" name="ruleFormula" value='${param.RuleFormula}'/>
	</form>
	<div class="mainBodyWrap">
		<iframe id="iframe" name="iframe" style="display:none;"></iframe>
		<form name="uploadForm" id="uploadForm" method="post" action="Upload.do" 
			enctype="multipart/form-data" target="iframe">
		<table class="table">
			<tr><th colspan="4">第一步：选择Excel文件</th></tr>
			<tr>
				<td><input type="file" name="excelFile" id="excelFile" style="width:500px;"/></td>
				<td><input type="submit" class="button" name="btnUpload" value="上传" id="sub"/></td>
			</tr>
		</table>
		</form>
		<table class="table">
			<tr><th colspan="4">第二步：选择工作簿</th></tr>
			<tr>
				<td width="40%"><select id="sheets" style="width:200px;"></select></td>
			</tr>
		</table>
		<table class="table">
			<tr><th colspan="2">第三步：选择字段，进行对应</th></tr>
			<tr>
				<td style="vertical-align:top;width:50%;">文档库字段:<br/>
					<select size="8" id="fields" style="width:100%"></select>
				</td>
				<td style="vertical-align:top;width:50%;">工作簿表头:<br/>
					<select size="8" id="headers" style="width:100%"></select>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<input class="button" type="button"  id="btnAutoMatch" value="字段自动匹配"
						title="自动把同名的工作簿表头和文档库字段对应起来"/>
					<input class="button" type="button"  id="btnMatch" value="对应"
						title="把选定的工作簿表头和选定的文档库字段对应起来"/>
				</td>
			</tr>
		</table>
		<table class="table">
			<tr>
			</tr>
			<tr><th>字段对应如下：</th></tr>
			<tr>
				<td><div id="matchDiv"></div></td>
			</tr>
			<tr>
				<td align="center">
					<input class="button" type="button" id="btnCancel" value="取消"/>
					<input class="button" type="button" id="btnSubmit" value="开始导入"/>
				</td>
			</tr>
		</table>
	</div>
    <div id="import_hint" class="import_hint">
        <img src="../../images/loading.gif" />分析时间较长，请耐心等待...
    </div>
</body>
</html>