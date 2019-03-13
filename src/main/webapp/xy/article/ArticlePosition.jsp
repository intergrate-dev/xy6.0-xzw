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
    <title>固定位置</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="../../e5script/jquery/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../../xy/article/script/ArticlePosition.js"></script>
    <link href="../script/bootstrap-3.3.4/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="../script/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet" media="screen">
    <link href="../../e5style/e5form-custom.css" rel="stylesheet" media="screen">
    <style>
    	.linkTitle{
            white-space:nowrap;
			overflow:hidden;
			text-overflow:ellipsis;	
    	}
        .custform-label {
            width: 100%;
            font-family: "microsoft yahei";
            text-align: left;
        }
        .custform-label-cue {
            width: 100%;
            font-family: "microsoft yahei";
            color:#CCCCCC;
            margin-left:100px;
        }
        #btnSave {
            margin-left: 100px;
            margin-right: 5px;
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
        .tablecontent {
            width: 70%;
            margin: 0 auto;
            margin-top: 5px;
            margin-left : 10px ;
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
		.tablecontent tr:first-child {
            border-bottom: 1px solid #ddd;
        }
        .tablecontent input{
        	border-radius:3px; 
    		border: 1px solid #ccc;
    		padding-left: 10px;
    		margin-bottom:-2px;
    		width:150px;
    		height:25px;
        }
        .tablecontent select{
       	    display: block;
		    width: 150px;
		    height: 30px;
		    padding: 6px 12px;
		    font-size: 14px;
		    line-height: 1.42857143;
		    color: #555;
		    background-color: #fff;
		    background-image: none;
		    border: 1px solid #ccc;
		    border-radius: 4px;
        }		
		.tablecontent lable {
            cursor: default !important;
        }
        .custform-from-wrap {
            font-family: "microsoft yahei";
        }
        .endTime {
            padding: 0;
        }
    </style>

</head>
<body>
<iframe id="frmColumn" style="display:none;"></iframe>
<form name="caForm" id="caForm" action="<%=basePath%>xy/articleorder/updateArticlePosition.do" method="post">
	<input type="hidden" name="siteID" id="siteID" value="<c:out value="${siteID}"/>"/>
    <input type="hidden" name="UUID" id="UUID" value="<c:out value="${UUID}"/>"/>
    <input type="hidden" name="docLibID" id="docLibID" value="<c:out value="${docLibID}"/>"/>
    <input type="hidden" name="colID" id="colID" value="<c:out value="${colID}"/>"/>
    <input type="hidden" name="docID" id="docID" value="<c:out value="${docID}"/>"/>
	<input type="hidden" name="timeTemp" id="timeTemp" value="<c:out value="${expireTime}"/>"/>
	
	<table class="tablecontent">
		<tr>
			<td colspan="2" class="">
				<span class="custform-span">
					<label id="linkTitle" title="" class="custform-labelv linkTitle">${linkTitle}</label>
				</span>
			</td>
		</tr>
		<tr>
			<td width="100px">
				<span class="custform-span">
					<label class="custform-label">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;固定位置</label>
				</span>
			</td>
			<td>
				<div>
					<select id="position" name="position" value="">
						<option value="0"></option>
						<c:forEach var="i" begin="1" end="40" step="1">
							<option value="<c:out value="${i}"/>" ${position == i ? "selected":""}>
								<c:out value="${i}"/>
							</option>
						</c:forEach>
					</select>
				</div>
			</td>
		</tr>
		<tr>
			<td width="100px">
				<span class="custform-span">
					<label class="custform-label">
					<input id="isExpire" name="isExpire" style="width:13px;height:13px;position:relative;top:2px;left:15px;" type="checkbox" ${isExpire==1?"checked":""} value="1">
					&nbsp;&nbsp;&nbsp;&nbsp;过期时间
					</label>
				</span>
			</td>
			<td>
				<div>
					<input class="form-control" style="display:inline-block;" type="text" id="expireTime" name="expireTime" size="16"  value="${expireTime}" readonly>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<span class="custform-span">
					<label class="custform-label-cue">过期时间过期,该稿件将自动取消固定位置</label>
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
