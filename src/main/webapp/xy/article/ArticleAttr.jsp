<%@include file="../../e5include/IncludeTag.jsp" %>
<%@page pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>
<html>
<head>
    <title>稿件属性</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="../../e5script/jquery/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../../xy/article/script/ArticleAttr.js"></script>
    <link href="../script/bootstrap-3.3.4/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="../script/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet" media="screen">
    <link href="../../e5style/e5form-custom.css" rel="stylesheet" media="screen">
    <style>
    	#btnSave, #btnCancel {
            font-family: "microsoft yahei";
            text-shadow: none;
            font-size: 12px;
            border-radius: 3px;
            width: 70px !important;
            border: none;
            color: #fff;
            line-height: 12px;
            margin-top: 20px;
        }
        #btnSave {
            margin-left: 80px;
            margin-right: 20px;
            background-color: #00a0e6;
        }
        #btnCancel {
            background-color: #b1b1b1;
        }
        .tablecontent {
            width: 90%;
            margin: 0 auto;
            margin-top: 5px;
            margin-left : 20px ;
           	border-bottom: none;
        }
        .tablecontent tr {
            border-bottom: none;
        }
        .tablecontent td {
            border-bottom: none;
            margin-left: 80px;
        }
        .tablecontent input{
        	border-radius:3px; 
    		border: 1px solid #ccc;
    		padding-left: 10px;
    		margin-bottom:-2px;
    		width:120px;
    		height:25px;
        }		
		.tablecontent lable {
            cursor: default !important;
        }
        .custform-label {
            width: 100%;
            font-family: "microsoft yahei";
        }
        .custform-label-cue {
            width: 100%;
            font-family: "microsoft yahei";
            color:#CCCCCC;
            margin-left:120px;
        }
		.btn-group-sm>.btn, .btn-sm{
			padding: 5px 9px;
		}
    </style>

</head>
<body>
<iframe id="frmColumn" style="display:none;"></iframe>
<form name="caForm" id="caForm" action="<%=basePath%>xy/article/dealArticleAttr.do" method="post">
	<input type="hidden" name="siteID" id="siteID" value="<c:out value="${siteID}"/>"/>
    <input type="hidden" name="UUID" id="UUID" value="<c:out value="${UUID}"/>"/>
    <input type="hidden" name="docLibID" id="docLibID" value="<c:out value="${docLibID}"/>"/>
    <input type="hidden" name="ch" id="ch" value="<c:out value="${ch}"/>"/>
    <input type="hidden" name="colID" id="colID" value="<c:out value="${colID}"/>"/>
    <input type="hidden" name="DocIDs" id="DocIDs" value="<c:out value="${DocIDs}"/>"/>
    <input type="hidden" name="a_attr" id="a_attr" value="63"/>
	
	<table class="tablecontent">
		
		<tr>
			<td colspan="2">
				<span class="custform-span" >
					<label class="custform-label">请选择稿件属性</label>
				</span>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<div id="attrDiv">
					<button type="button" class="btn btn-default btn-sm active" value="63" >普通新闻</button>
                	<button type="button" class="btn btn-default btn-sm" value="62" >头条新闻</button>
                	<button type="button" class="btn btn-default btn-sm" value="61" >图片新闻</button>
                	<button type="button" class="btn btn-default btn-sm" value="64" >重要新闻</button>
                	<button type="button" class="btn btn-default btn-sm" value="65" >其它新闻</button>
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
