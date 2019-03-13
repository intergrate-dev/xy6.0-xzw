<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>页面区块树</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
</head>
<body>
	<%@include file="../block/Tree.inc"%>
	<script>
		//设置页面区块树需要的站点参数
		b_tree.siteID = "<c:out value="${param.siteID}"/>";
        b_tree.dataType = "<c:out value="${param.dataType}"/>";
		b_tree.check.ids = "<c:out value="${param.ids}"/>";
        var articleIDs="null";
		if (b_tree.check.ids==""){
			b_tree.check.ids=sessionStorage.getItem("ids");
		}
		if (!b_tree.siteID) b_tree.siteID = 1;
	
		$(function(){
			$("#divBlockBtn").show();
			//点击保存按钮
			$('#btnBlockOK').click(function(){
				getChecks();			});
			//点击取消按钮
			$('#btnBlockCancel').click(function(){
				doCancel();
			});
		});
		
		function getChecks() {
			try {
				//, b_tree.getChecks()
				parent.columnClose(b_tree.getChecks());
			} catch (e) {
				var hint = "父窗口应实现columnClose(filterChecked, checked)方法供栏目树关闭时调用。"
					+ "\n   每个参数的格式是:  [ids, names, cascadeIDs]"
				alert(hint);
			}
		}
		
		function doCancel() {
			try {
				parent.columnCancel();
			} catch (e) {
				var hint = "父窗口应实现columnCancel()方法供栏目树取消时调用。";
				alert(hint);
			}
		}
		
		/** 对特殊字符和中文编码 */
		function encodeU(param1) {
			if (!param1)
				return "";
			var res = "";
			for ( var i = 0; i < param1.length; i++) {
				switch (param1.charCodeAt(i)) {
				case 0x20://space
				case 0x3f://?
				case 0x23://#
				case 0x26://&
				case 0x22://"
				case 0x27://'
				case 0x2a://*
				case 0x3d://=
				case 0x5c:// \
				case 0x2f:// /
				case 0x2e:// .
				case 0x25:// .
					res += escape(param1.charAt(i));
					break;
				case 0x2b:
					res += "%2b";
					break;
				default:
					res += encodeURI(param1.charAt(i));
					break;
				}
			}
			return res;
		}
	</script>
</body>
</html>