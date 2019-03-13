<%@include file="../../e5include/IncludeTag.jsp"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false" />
<html>
<head>
<base href="<%=basePath%>">
<title><i18n:message key="org.user.form.list.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="e5script/jquery/jquery.min.js"></script>
<style>
body,iframe{margin:0; padding:0}
.frm {
	border: 0;
	width: 500px;
	min-height: 450px;
}
</style>
</head>
<body onload="handlerRisize()" onresize="handlerRisize()">
	<iframe name="frmColumn" id="frmColumn" src="" class="frm"></iframe>
	<script>
		//从后台获取参数
		var siteID = "<c:out value="${siteID}"/>";
		var docIDs = "<c:out value="${DocIDs}"/>";
		var docLibID = "<c:out value="${DocLibID}"/>";
		var UUID = "<c:out value="${UUID}"/>";
		var groupID = "<c:out value="${groupID}"/>";
		var ch = "<c:out value="${ch}"/>";

		var type = "<c:out value="${type}"/>";
		var ids = "<c:out value="${ids}"/>";

		var newIds = "";

		//初始化-直接在弹出窗口中设置iframe的src
		$(function() {
			var url = "xy/column/ColumnCheck.jsp?type=" + type
					+ "&siteID=" + siteID
					+ "&ids=" + ids
					+ "&ch=" + ch
					;
			$("#frmColumn").attr("src", url);
		});

		//1. 当用户提交选择的栏目
		function columnClose(filterChecked, allFilterChecked) {
			newIds = allFilterChecked[0];
			$.ajax({
				url : "xy/column/updateColumnPush.do",
				type : 'POST',
				data : {
					"newIds" : newIds,
					"siteID" : siteID,
					"docIDs" : docIDs,
					"docLibID" : docLibID,
					"UUID" : UUID
				},
				dataType : 'html',
				success : function(msg, status) {
					if (status == "success") {
						alert("操作完成。");
					}
				},
				error : function(xhr, textStatus, errorThrown) {
					alert("访问服务器异常：" + errorThrown);
				}
			});

		}

		function columnCancel() {
			var url = "xy/column/ColumnCheck.jsp?type=" + type + "&siteID=" + siteID + "&ids=" + ids + "&ch=" + ch;
			$("#frmColumn").attr("src", url);
		}

		//FIXME******************************* 
		//不好使
		window.onbeforeunload = function(event) {

		};

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
		function handlerRisize(){
			var winH = $(window).height();
			//IE9：窗口高度读为0
			if (winH == 0) {
				var iframes = $(window.parent.document).find("iframe");
				for (var i = 0; i < iframes.length; i++) {
					winH = $(iframes[i]).height();
					if (winH > 0) break;
				}
			}
			$("#frmColumn").height(winH-5);
		}
	</script>
</body>
</html>