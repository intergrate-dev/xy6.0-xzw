<%@include file="../../e5include/IncludeTag.jsp" %>
<%@page language="java" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>
<html>
<head>
    <title>推送设置</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
	<script type="text/javascript" src="../app/script/pushConfig.js"></script>
    <link href="../script/bootstrap-3.3.4/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
    <link href="../../e5style/e5form-custom.css" rel="stylesheet" media="screen">
    <style>
    	.os{
            white-space:nowrap;
			overflow:hidden;
			text-overflow:ellipsis;	
    	}
        .custform-label {
			width: 76%;
            font-family: "microsoft yahei";
            text-align: left;
        }
        .custform-label-cue {
            width: 100%;
            font-family: "microsoft yahei";
            color:#CCCCCC;
        }
        .custform-from-wrap {
            font-family: "microsoft yahei";
        }
        #btnSave {
            margin-left: 120px;
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
           	table-layout:fixed;
        }
        .tablecontent div {
            margin-left: -40px;
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
        .tablecontent input{
        	    border-radius: 5px;
			    border: 1px solid #0088cc;
			    padding-left: 7px;
			    margin-bottom: -2px;
			    width: 200px;
			    height: 25px;
        }
    </style>

</head>
<body>
<form name="caForm" id="caForm" action="<%=basePath%>xy/mobileos/addPushConfig.do" method="post">
	<input type="hidden" name="siteID" id="siteID" value="<c:out value="${siteID}"/>"/>
    <input type="hidden" name="UUID" id="UUID" value="<c:out value="${UUID}"/>"/>
    <input type="hidden" name="DocLibID" id="DocLibID" value="<c:out value="${DocLibID}"/>"/>
    <input type="hidden" name="DocIDs" id="DocIDs" value="<c:out value="${DocIDs}"/>"/>
	
	<table class="tablecontent">
		<tr>
			<td colspan="2">
				<span class="custform-span">
					<label class="custform-label-cue">参数从个推管理平台获取</label>
				</span>
			</td>
		</tr>
		<tr>
			<td colspan="2" style="border-bottom: 1px solid #ddd;">
				<span class="custform-span">
					<label id="os" title="" class="custform-label os">Android</label>
				</span>
			</td>
		</tr>
		<tr>
			<td width="120px">
				<span class="custform-span">
					<label class="custform-label" >AppID</label>
				</span>
			</td>
			<td>
				<div>
                    <input id="androidAppID" name="androidAppID" type="text" class="validate[required]" value="${androidAppID}"/>
				</div>
			</td>
		</tr>
		<tr>
			<td width="120px">
				<span class="custform-span">
					<label class="custform-label" >AppKey</label>
				</span>
			</td>
			<td>
				<div>
                    <input id="androidAppKey" name="androidAppKey" type="text" class="validate[required]" value="${androidAppKey}"/>
				</div>
			</td>
		</tr>
		<tr>
			<td width="120px">
				<span class="custform-span">
					<label class="custform-label" >MasterSecret</label>
				</span>
			</td>
			<td>
				<div>
                    <input id="androidMasterSecret" name="androidMasterSecret" type="text" class="validate[required]" value="${androidMasterSecret}"/>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="2" style="border-bottom: 1px solid #ddd;">
				<span class="custform-span">
					<label id="os" title="" class="custform-label os">IOS</label>
				</span>
			</td>
		</tr>
		<tr>
			<td width="120px">
				<span class="custform-span">
					<label class="custform-label" >AppID</label>
				</span>
			</td>
			<td>
				<div>
                    <input id="iosAppID" name="iosAppID" type="text" class="validate[required]" value="${iosAppID}"/>
				</div>
			</td>
		</tr>
		<tr>
			<td width="120px">
				<span class="custform-span">
					<label class="custform-label" >AppKey</label>
				</span>
			</td>
			<td>
				<div>
                    <input id="iosApKey" name="iosApKey" type="text" class="validate[required]" value="${iosApKey}"/>
				</div>
			</td>
		</tr>
		<tr>
			<td width="120px">
				<span class="custform-span">
					<label class="custform-label" >MasterSecret</label>
				</span>
			</td>
			<td>
				<div>
                    <input id="iosMasterSecret" name="iosMasterSecret" type="text" class="validate[required]" value="${iosMasterSecret}"/>
				</div>
			</td>
		</tr>
		<tr>
			<td width="120px">
				<span class="custform-span">
					<label class="custform-label" >AutoBadge</label>
				</span>
			</td>
			<td>
				<div>
                    <input id="autoBadge" name="autoBadge" type="text" class="validate[required]" value="${autoBadge}"/>
				</div>
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