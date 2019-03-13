<%@include file="../../e5include/IncludeTag.jsp"%>
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
<title><i18n:message key="org.user.form.list.title" />
</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="e5script/jquery/jquery.min.js"></script>
<style>
.frm {
	border: 0;
	width: 100%;
	height: 430px;
}
</style>
</head>
<body>
	
	<iframe name="frmColumn" id="frmColumn" src="" class="frm"></iframe>
	<script>
		//从后台获取参数
		var siteID = "<c:out value="${siteID}"/>";
		var ids = "<c:out value="${ids}"/>";
		var type = "<c:out value="${type}"/>";
		var docIDs = "<c:out value="${docIDs}"/>";
		var docLibID = "<c:out value="${docLibID}"/>";
		var templateColumnName = "<c:out value="${templateColumnName}"/>";
		var templateRealName = "<c:out value="${templateRealName}"/>";
		var UUID = "<c:out value="${UUID}"/>";

		var newIds = "";

		//初始化-直接在弹出窗口中设置iframe的src
		$(function() {
			var url = "xy/column/ColumnCheck.jsp?type=" + type + "&siteID="
					+ siteID + "&ids=" + ids;
			if(templateColumnName==""){
				url ="xy/template/FirstPage.jsp";
			}
			
			$("#frmColumn").attr("src", url);
		});

		//1. 当用户提交选择的栏目
		function columnClose(filterChecked, allFilterChecked) {
			newIds = allFilterChecked[0];
			//对比前后Ids是否一样。如果相同，不操作 。
			if (isSameIds(ids, newIds)) {
				operationFailure();
				return;
			}
			//不一样时提交
			$.ajax({
				url : "xy/template/changeColumnTemplateAjax.do",
				type : 'POST',
				data : {
					"ids" : ids,
					"newIds" : newIds,
					"siteID" : siteID,
					"docIDs" : docIDs,
					"docLibID" : docLibID,
					"db_templateColumnName" : templateColumnName,
					"db_templateRealName" : templateRealName,
					"UUID" : UUID,
					"notExpanded"  : filterChecked[3]
				},
				dataType : 'html',
				success : function(msg, status) {
					//成功时，调用operationSuccess(),并且写日志（opinion）
					if (status == "success") {
						if (msg.substr(0, 7) == "success") {
							operationSuccess(msg.substr(7));
						} else if (msg == "NoOperation") {
							operationFailure();
						} else {
							alert(msg);
							operationFailure();
						}

					} else {
						operationFailure();
					}
				},
				error : function(xhr, textStatus, errorThrown) {
					operationFailure();
				}
			});

		}

		//挂接成功调用
		function operationSuccess(opnions) {
			var url = "e5workspace/after.do?UUID=" + UUID + "&DocIDs=" + docIDs
					+ "&DocLibID=" + docLibID + "&Opinion="
					+ encodeU("" + opnions);
			$("#frmColumn").attr("src", url);
		}

		//操作失败了调用
		function operationFailure() {
			var url = "e5workspace/after.do?UUID=" + UUID;
			$("#frmColumn").attr("src", url);

		}

		//用户提交选择栏目之后，对比前后Ids，
		//对比前后Ids是否一样
		function isSameIds(ids, newIds) {
			//如果字符串一样，为true
			if (ids == newIds)
				return true;

			var oldArr = ids.split(",");
			var newArr = newIds.split(",");
			//把数组进行排序
			oldArr.sort();
			newArr.sort();

			//如果排序后，字符串还是一样，为true
			if (oldArr.join(",") == newArr.join(","))
				return true;

			return false;

		}

		//点击取消按钮时
		function columnCancel() {
			operationFailure();
		}

		//FIXME******************************* 
		//不好使
		window.onbeforeunload = function(event) {
			operationFailure();
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
	</script>
</body>
</html>