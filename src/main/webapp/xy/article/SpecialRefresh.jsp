<%@include file="../../e5include/IncludeTag.jsp" %>
<%@page language="java" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html>
<head>
    <title>专题更新</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="../../e5script/jquery/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../../xy/article/script/ArticlePriority.js"></script>
    <link href="../script/bootstrap-3.3.4/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="../script/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet" media="screen">
    <link href="../../e5style/e5form-custom.css" rel="stylesheet" media="screen">
    <style>
        .custform-label {
            width: 100%;
            font-family: "microsoft yahei";
            text-align: left;
        }
        .custform-label-cue {
            width: 80%;
            font-family: "microsoft yahei";
            color:#CCCCCC;
            margin-left:90px;
        }
        #btnSave {
            margin-left: 90px;
            margin-right: 20px;
            background-color: #00a0e6;
        }
        #btnCancel {
            background-color: #b1b1b1;
        }
        #btnSave, #btnCancel {
            font-family: "microsoft yahei";
            text-shadow: none;
            font-size: 12px;
            border-radius: 3px;
            width: 70px !important;
            border: none;
            color: #fff;
            line-height: 12px;
        }
        .tablecontent {
            width: 80%;
            margin: 0 auto;
            margin-top: 5px;
            margin-left : 20px ;
           	border-bottom: none;
        }
        .tablecontent td {
            border-bottom: none;
        }
        .tablecontent tr {
            border-bottom: none;
        }
		.tablecontent tr:first-child {
            border-bottom: 1px solid #ddd;
        }		
		.tablecontent lable {
            cursor: default !important;
        }
    </style>
</head>
<body>
<iframe id="frmColumn" style="display:none;"></iframe>
<form name="caForm" id="caForm" action="<%=basePath%>xy/articleorder/dealSpecialRefresh.do" method="post">
    <input type="hidden" name="UUID" id="UUID" value="<c:out value="${UUID}"/>"/>
	<input type="hidden" name="siteID" id="siteID" value="<c:out value="${siteID}"/>"/>
    <input type="hidden" name="docLibID" id="docLibID" value="<c:out value="${docLibID}"/>"/>
    <input type="hidden" name="colID" id="colID" value="<c:out value="${colID}"/>"/>
    <input type="hidden" name="docID" id="docID" value="<c:out value="${docID}"/>"/>
	<input type="hidden" name="linkTitle" id="linkTitle" value="<c:out value="${linkTitle}"/>"/>
	<input type="hidden" name="order" id="linkTitle" value="<c:out value="${order}"/>"/>
	<input type="hidden" name="str" id="str" value="<c:out value="${str}"/>"/>
	
	<table class="tablecontent">
		<tr>
			<td colspan="2">
				<span class="custform-span">
					<label id="linkTitle" class="custform-label">更新所属的专题稿的顺序</label>
				</span>
			</td>
		</tr>
		<c:if test="${flag == false}">
		<tr>
			<td colspan="2">
				稿件&nbsp;[${docID}]&nbsp;不存在所属专题稿
			</td>
		</tr>
		</c:if>
		<c:if test="${flag}">
		<tr>
			<td width="90px">
				<span class="custform-span">
					<label class="custform-label">更新稿件标题</label>
				</span>
			</td>
			<td>
				<div>
					<input id="isCovert" name="isCovert" type="checkbox" value="1">
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<span class="custform-span">
					<label class="custform-label-cue">用本稿件的标题覆盖所属专题稿件的标题</label>
				</span>
			</td>
		</tr>
		</c:if>
		<tr>
			<td style="width:50px;" class="ui-droppable" colspan="2" style="text-align: center; ">
				<span id="txSave" class="ui-draggable" fieldtype="-1" fieldcode="insertsave"> 
				<c:if test="${flag}">
					<input id="btnSave" class="button btn" value="保存" type="button"/>
				</c:if>
				</span>
				<span class="custform-aftertxt ui-draggable">&nbsp; </span>
				<input id="btnCancel" class="button btn" value="取消" type="button"/>
			</td>
		</tr>
	</table>
</form>
</body>
</html>
