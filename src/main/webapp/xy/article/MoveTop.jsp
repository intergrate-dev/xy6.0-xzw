<%@include file="../../e5include/IncludeTag.jsp" %>
<%@page pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html>
<head>
    <title>置顶</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="../../e5script/jquery/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../../xy/article/script/MoveTop.js"></script>
    <link href="../script/bootstrap-3.3.4/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="../script/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet" media="screen">
    <link href="../../e5style/e5form-custom.css" rel="stylesheet" media="screen">
    <style>
        .custform-label {
            width: 100%;
            font-family: "microsoft yahei";
        }
        .custform-label-cue {
            width: 100%;
            font-family: "microsoft yahei";
            color:#CCCCCC;
            margin-left:100px;
        }
        #btnSave {
            margin-left: 100px;
            margin-right: 10px;
            background-color: #00a0e6;
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
        #btnCancel {
            background-color: #b1b1b1;
        }
        #expireTime{
        	border-radius:3px; 
    		border: 1px solid #ccc;
    		padding-left: 10px;
    		margin-bottom:-2px;
    		width:150px;
    		height:25px;
        }
        .tablecontent {
            width: 70%;
            margin: 0 auto;
            margin-top: 50px;
            margin-left : 15px ;
           	border-bottom: none;
        }
        .tablecontent td {
            border-bottom: none;
        }
        .tablecontent tr {
            border-bottom: none;
        }
		.tablecontent lable {
            cursor: default !important;
        }
        .custform-from-wrap {
            font-family: "microsoft yahei";
        }
        .expireTime {
            padding: 0;
        }
        .isExpire{
        	width:13px;
        	height:13px;
        	position:relative;
        	top: 2px;
        	left: 15px;
        }
    </style>

</head>
<body>
<iframe id="frmColumn" style="display:none;"></iframe>
<form name="caForm" id="caForm" action="<%=basePath%>xy/articleorder/dealMoveTop.do" method="post">
	<input type="hidden" name="siteID" id="siteID" value="<c:out value="${siteID}"/>"/>
    <input type="hidden" name="UUID" id="UUID" value="<c:out value="${UUID}"/>"/>
    <input type="hidden" name="colID" id="colID" value="<c:out value="${colID}"/>"/>
    <input type="hidden" name="docLibID" id="docLibID" value="<c:out value="${docLibID}"/>"/>
    <input type="hidden" name="docIDs" id="docIDs" value="<c:out value="${docIDs}"/>"/>
    <input type="hidden" name="timeTemp" id="timeTemp" value="<c:out value="${expireTime}"/>"/>

	<table class="tablecontent">
		<tr>
			<td width="100px">
				<span class="custform-span">
				<label class="custform-label">
					<input id="isExpire" name="isExpire" type="checkbox" class="isExpire" ${isExpire==1?"checked":""} value="1">
					&nbsp;&nbsp;&nbsp;&nbsp;过期时间
				</label>
				</span>
			</td>
			<td>
				<div>
					<input class="form-control" type="text" id="expireTime" name="expireTime" size="16"  value="${expireTime}" readonly>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<span class="custform-span">
					<label class="custform-label-cue">置顶时间过期,该稿件将自动取消置顶</label>
				</span>
			</td>
		</tr>
		<tr>
			<td style="width:50px;" class="ui-droppable" colspan="2" style="text-align: center; ">
				<span id="txSave" class="ui-draggable" fieldtype="-1" fieldcode="insertsave"> 
					<input id="btnSave" class="button btn" value="保存" type="button"/>
				</span>
				<span class="custform-aftertxt ui-draggable">&nbsp; </span>
				<input id="btnCancel" class="button btn" value="取消" type="button"/>
			</td>
		</tr>
	</table>
</form>
</body>
</html>
