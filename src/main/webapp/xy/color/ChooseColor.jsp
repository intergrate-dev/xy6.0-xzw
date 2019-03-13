<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>颜色</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
</head>
<body>
	<div style="border-bottom:1px solid #ddd; padding:5px 50px 10px 20px;font-size:12px;position: fixed;top: 0;left:200px;background:#FFFFFF;">
	<input type='button' id="doCancel" value='取消' onclick="cancelChooseColor()"/>
	</div>
	<script>

			//点击取消按钮
			function cancelChooseColor(){
				parent.column_form.cancelColor(0);
                // parent.column_form.setColor(333,"#333");//color_id,color 栏目颜色ID,栏目颜色铺值方法
			};
	</script>
</body>
</html>