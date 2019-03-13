<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5context" changeResponseLocale="false"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><i18n:message key="dataSource.title"/></title>
<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
<script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
<script type="text/javascript" src="../e5script/Function.js"></script>
<script type="text/javaScript">
	var selectedTr,
		bodyFrame,
		dialog;
	$(function(){
		$(".table tr").click(selected);
		$("#add").click(doAdd);
		$("#del").click(doDelete);
		bodyFrame = $("#bodyFrame");
		dialog = e5.dialog("",{
			title:"",
			resizable:false,
			height:350,
			width:500,
			ishide:true
		});
		dialog.DOM.content.append(bodyFrame);
	});
	function selected(){
		var self = $(this).addClass("cur");
		if(selectedTr && selectedTr.length){
			selectedTr.removeClass("cur");
		}
		selectedTr = self;
		doModify();
	}
	function doAdd(){
		bodyFrame.attr("src","DataSourceEdit.do?action=new");
		dialog.show();
	}
	function doModify(){
		if(selectedTr.length){
			bodyFrame.attr("src","DataSourceEdit.do?action=edit&id="+selectedTr.attr("id"));
			dialog.show();
		}else{
			alert('<i18n:message key="dataSource.alert.modify"/>');
		}
	}
	function doDelete(){
		if(selectedTr.length){
			var ok = confirm("<i18n:message key="dataSource.confirm.delete"/>");
			if(ok){
				location.href="DataSource.do?action=delete&id="+selectedTr.attr("id");
			}
		}
		else{
			alert('<i18n:message key="dataSource.alert.delete"/>');
		}
	}
	function dialogClose(){
		dialog.closeEvt();
	}
</script>
<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
<link type="text/css" rel="stylesheet" href="../e5script/jquery/dialog.style.css"/>
<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
<style type="text/css">
	tr.cur{
		background-color:#E4E8EB;
	}
	.table td{
		cursor: pointer;
	}
</style>
</head>
<body style="text-align:center;">
	<div class="mainBodyWrap" style="width:700px;">
		<table cellpadding="0" cellspacing="0" class="table">
		<caption><i18n:message key="dataSource.manager"/></caption>
			<c:forEach var="item" items="${list}" varStatus="dsIndex">
				<tr id="<c:out value="${item.dsID}"/>">
					<td width="100"><c:out value="${dsIndex.index + 1}"/></td>
					<td><c:out value="${item.name}"/></td>
				</tr>
			</c:forEach>
		</table>
		<div class="mt alignCenter">
			<input class="button" type="button" id="add" value="<i18n:message key="dataSource.button.add"/>">
			<input class="button" type="button" id="del" value="<i18n:message key="dataSource.button.delete"/>">
		</div>
	</div>
	<iframe id="bodyFrame" src="" width="100%" height="100%" frameborder="0"></iframe>
</body>
</html>
<%@include file="../e5include/Error.jsp"%>