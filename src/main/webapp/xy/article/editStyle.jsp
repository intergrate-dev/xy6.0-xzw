<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@ page pageEncoding="UTF-8"%>

<html>
<head>
	<title>编辑样式</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
<style>
	
	.btns{
	font-family: "microsoft yahei";
    color: #fff;
    border: none;
    border-radius: 3px;
	padding: 0 20px;
    height: 25px;
    font-size: 12px;
}	
.dosure{
	background: #00a0e6;
	margin-left: 6px;	
}
.docancle{
	background: #b1b1b1;	
}
</style>
</head>

<body>

	<table class="form-table">
		<tr>
			<td><%@include file="simpleEditor.html"%></td>
		</tr>
	</table>
	<input class='btns dosure' type='button' id="btnEditSave" value='确定'/>
	<input class='btns docancle' type='button' id="btnEditCancel" value='取消'/>
	<script language="javascript" type="text/javascript" src="script/editStyle.js"></script>
	<script>
		var content = decodeURIComponent("<c:out value="${param.content}"/>");
		var editDialogType = "<c:out value="${param.editDialogType}"/>";
		var e_type = "<c:out value="${param.e_type}"/>";
		if(e_type ==="articlesetting"){
			content = parent.getContent();
		}

		edit_style.editorcontent = content;
		edit_style.setContent();
		
		$("#btnEditCancel").click(editCancel);
		$("#btnEditSave").click(getContents);
		
		function getContents() {
			try {
				var styleContent = edit_style.getContent();
				//编辑标题和副标题时去掉首尾<p></p> 链接标题也去掉，短标题也去掉
				if(editDialogType == 0 || editDialogType == 2 || editDialogType == 3 || editDialogType == 4 ){
					if ((styleContent.split('<p')).length - 1 > 1) {
						parent.editClose(styleContent);
					} else {
						parent.editClose($.trim(styleContent.substring(3,styleContent.length-4)));
					}
				}else{
					parent.editClose(styleContent);
				}
			} catch (e) {
				var hint = "父窗口应实现editClose(contents)方法供编辑样式关闭时调用。";
				alert(hint);
			}
		}
		
		function editCancel() {
			try {
				parent.editCancel();
			} catch (e) {
				var hint = "父窗口应实现editCancel()方法供编辑样式取消时调用。";
				alert(hint);
			}
		}	
	</script>
</body>
</html>