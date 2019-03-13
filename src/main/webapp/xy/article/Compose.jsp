<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@ page pageEncoding="UTF-8"%>

<html>
<head>
	<title>合成多标题</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../../e5script/e5.js"></script>
	<script type="text/javascript" src="../../e5script/e5.min.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
<style>
	body{
		margin: 30px auto;
	    width: 625px;
	}
	.frm {
	border: 0;
	width: 400px;
	height: 500px;
	}
	#sites ul{
		list-style:none;
	} 
	.btn{
		font-family: "microsoft yahei";
		display: inline-block;
	    padding: 4px 12px;
	    margin-bottom: 0;
	    font-size: 14px;
	    line-height: 20px;
	    color: #333;
	    text-align: center;
	    text-shadow: 0 1px 1px rgba(255,255,255,0.75);
	    vertical-align: middle;
	    cursor: pointer;
	    background-color: #f5f5f5;
	    background-image: -moz-linear-gradient(top,#fff,#e6e6e6);
	    background-image: -webkit-gradient(linear,0 0,0 100%,from(#fff),to(#e6e6e6));
	    background-image: -webkit-linear-gradient(top,#fff,#e6e6e6);
	    background-image: -o-linear-gradient(top,#fff,#e6e6e6);
	    background-image: linear-gradient(to bottom,#fff,#e6e6e6);
	    background-repeat: repeat-x;
	    border: 1px solid #ccc;
	    border-color: #e6e6e6 #e6e6e6 #bfbfbf;
	    border-color: rgba(0,0,0,0.1) rgba(0,0,0,0.1) rgba(0,0,0,0.25);
	    border-bottom-color: #b3b3b3;
	    -webkit-border-radius: 4px;
	    -moz-border-radius: 4px;
	    border-radius: 4px;
	    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ffffffff',endColorstr='#ffe6e6e6',GradientType=0);
	    filter: progid:DXImageTransform.Microsoft.gradient(enabled=false);
	    -webkit-box-shadow: inset 0 1px 0 rgba(255,255,255,0.2),0 1px 2px rgba(0,0,0,0.05);
	    -moz-box-shadow: inset 0 1px 0 rgba(255,255,255,0.2),0 1px 2px rgba(0,0,0,0.05);
	    box-shadow: inset 0 1px 0 rgba(255,255,255,0.2),0 1px 2px rgba(0,0,0,0.05);

	}
	.btn-success {
	    color: #fff;
	    text-shadow: 0 -1px 0 rgba(0,0,0,0.25);
	    background-color: #5bb75b;
	    background-image: -moz-linear-gradient(top,#62c462,#51a351);
	    background-image: -webkit-gradient(linear,0 0,0 100%,from(#62c462),to(#51a351));
	    background-image: -webkit-linear-gradient(top,#62c462,#51a351);
	    background-image: -o-linear-gradient(top,#62c462,#51a351);
	    background-image: linear-gradient(to bottom,#62c462,#51a351);
	    background-repeat: repeat-x;
	    border-color: #51a351 #51a351 #387038;
	    border-color: rgba(0,0,0,0.1) rgba(0,0,0,0.1) rgba(0,0,0,0.25);
    }
	
	.btn-primary {
	    color: #fff;
	    text-shadow: 0 -1px 0 rgba(0,0,0,0.25);
	    background-color: #006dcc;
	    background-image: -moz-linear-gradient(top,#08c,#04c);
	    background-image: -webkit-gradient(linear,0 0,0 100%,from(#08c),to(#04c));
	    background-image: -webkit-linear-gradient(top,#08c,#04c);
	    background-image: -o-linear-gradient(top,#08c,#04c);
	    background-image: linear-gradient(to bottom,#08c,#04c);
	    background-repeat: repeat-x;
	    border-color: #04c #04c #002a80;
	    border-color: rgba(0,0,0,0.1) rgba(0,0,0,0.1) rgba(0,0,0,0.25);
	    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ff0088cc',endColorstr='#ff0044cc',GradientType=0);
	    filter: progid:DXImageTransform.Microsoft.gradient(enabled=false);
	    margin: 0 10px;
	}
	.btn-danger {
	    color: #fff;
	    text-shadow: 0 -1px 0 rgba(0,0,0,0.25);
	    background-color: #D0D0D0;
	    background-image: -moz-linear-gradient(top,#c6c4c4,#676565);
	    background-image: -webkit-gradient(linear,0 0,0 100%,from(#c6c4c4),to(#676565));
	    background-image: -webkit-linear-gradient(top,#c6c4c4,#676565);
	    background-image: -o-linear-gradient(top,#c6c4c4,#676565);
	    background-image: linear-gradient(to bottom,#c6c4c4,#676565);
	    background-repeat: repeat-x;
	    border-color: #676565 #676565 #802420;
	    border-color: rgba(0,0,0,0.1) rgba(0,0,0,0.1) rgba(0,0,0,0.25);
	    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ffc6c4c4',endColorstr='#ff676565',GradientType=0);
	    filter: progid:DXImageTransform.Microsoft.gradient(enabled=false);
	}
	.btn-group{
		width:250px;
		margin: 0 auto;
		margin-top: 25px;
	}
</style>
</head>

<body>
	<iframe name="frmCompose" id="frmCompose" src="" class="frm" style="display:none;"></iframe>
	<script language="javascript" type="text/javascript" src="script/compose.js"></script>
	<div class="frmColumn" style="padding-bottom:5px;float:left; margin-right:5px;">
		主栏目：<input id="colFrm" type="text" value="" title="" /> 
		<input id="colFrmId" type="text" value="" title="" 	style="display:none;"/> 
		<input type='button' id="colSelect" value='选择'/>
		关联栏目：<input id="refFrm" type="text" value="" title="" /> 
		<input id="refFrmId" type="text" value="" title="" 	style="display:none;"/> 
		<input type='button' id="refSelect" value='选择'/>
	</div>
	<input type='button' id="artAdd" value='继续添稿'/>
	<script>
		//从后台获取参数
		var siteID = "<c:out value="${siteID}"/>";
		var colID = "<c:out value="${colID}"/>";
		var colName = "<c:out value="${colName}"/>";
		var UUID = "<c:out value="${UUID}"/>";
		var DocLibID = "<c:out value="${DocLibID}"/>";
		var ch = "<c:out value="${ch}"/>";
		compose_art.DocIDs = "<c:out value="${DocIDs}"/>";
		compose_art.editorcontent = "<c:out value="${content}"/>";
		compose_art.op = "<c:out value="${op}"/>";
		compose_art.currentColID = "<c:out value="${currentColID}"/>";
		//当用户提交选择的栏目,实现栏目选择树的接口
		function columnClose(filterChecked, allFilterChecked) {
			var colIds = allFilterChecked[0];
			var colNames = allFilterChecked[1];
			compose_art.columnClose(colIds,colNames);
		}
		
		function columnCancel() {
			compose_art.columnCancel();
		}
	</script>
	<table class="form-table">
		<tr>
			<td><%@include file="simpleEditor.html"%></td>
		</tr>
	</table>
	<div class="btn-group" >
		<input type='button' class="btn btn-success" id="doSave" value='保存'/>
		<input type='button' class="btn btn-primary" id="doPub" value='发布'/>
		<input type='button' class="btn btn-danger" id="doCancel" value='取消'/>
	</div>
	
</body>
</html>