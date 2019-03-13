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
<title>挂接栏目</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="e5script/jquery/jquery.min.js"></script>
<style>
.frm {
	border: 0;
	width: 100%;
	height: 400px;
}
.channels{
	margin:0;
	list-style: none;
	overflow: hidden;
	  -webkit-padding-start: 0px;
}
.channels li{
	float: left;
	text-align:center;
	cursor:pointer;
	width:70px;
	padding:3px;
	margin-bottom:5px;
	font-family: "微软雅黑";
}
.channels li.select{
	background-color:#00a0e6;
	border-radius:5px;
	-webkit-border-radius: 5px;
	-moz-border-radius: 5px;
	color:white;
	font-size: 14px;
	font-family: "微软雅黑";
}
</style>
</head>
<body>
	<ul class="channels" id="ul">
	<c:forEach var="ch" items="${channels}">
		<c:if test="${ch != null}">
		<li class="channelTab" ch="<c:out value="${ch.id}"/>"><c:out value="${ch.name}"/></li>
		</c:if>
	</c:forEach>
	</ul>
	<iframe name="frmColumn" id="frmColumn" src="" class="frm"></iframe>
	<script>
		//从后台获取参数
		var siteID = "<c:out value="${grantInfo.siteID}"/>";
		var docIDs = "<c:out value="${grantInfo.docIDs}"/>";
		var docLibID = "<c:out value="${grantInfo.docLibID}"/>";
		var UUID = "<c:out value="${grantInfo.UUID}"/>";
		var groupID = "<c:out value="${grantInfo.groupID}"/>";

		var type = "<c:out value="${type}"/>";
		var ids_0 = "<c:out value="${ids_0}"/>";
		var ids_1 = "<c:out value="${ids_1}"/>";		
		var newIds = "";

		//初始化-直接在弹出窗口中设置iframe的src
		$(function() {
			//tab 样式
			$(".channelTab").click(function(e){
				$(".channelTab").removeClass("select");
				$(this).addClass("select");
				
				var ch = $(".channelTab.select").attr("ch");
				var ids = parseInt(ch) ==0 ? ids_0 : ids_1;
				var url = "xy/column/ColumnCheck.jsp?type=" + type + "&siteID="
				+ siteID + "&ids=" + ids + "&ch="+ ch;
				$("#frmColumn").attr("src", url);
				
			});
			var tabs = $("#ul .channelTab");
			if (tabs.length <= 1)
				$("#ul").hide();
			tabs.first().click();
		});

		//1. 当用户提交选择的栏目
		function columnClose(filterChecked, allFilterChecked) {
			var ch = $(".channelTab.select").attr("ch");
			var ids = parseInt(ch) ==0 ? ids_0 : ids_1;
			newIds = allFilterChecked[0];
			//对比前后Ids是否一样。如果相同，不操作 。
			if (isSameIds(ids, newIds)) {
				operationFailure();
				return;
			}
			//不一样时提交
			$.ajax({
				url : "xy/extfield/grantColumnsAjax.do",
				type : 'POST',
				data : {
					"ids" : ids,
					"newIds" : newIds,
					"siteID" : siteID,
					"docIDs" : docIDs,
					"docLibID" : docLibID,
					"UUID" : UUID,
					"groupID" : groupID,
					"ch" : ch
				},
				dataType : 'text/plain',
				success : function(msg, status) {
					//成功时，调用operationSuccess(),并且写日志（opinion）
					if (status == "success") {
						if (msg.substr(0, 7) == "success") {
							//operationSuccess(msg.substr(7));
							operationFailure();
						} else if (msg == "NoOperation") {
							operationFailure();
						} else {
							operationFailure();
						}

					} else {
						operationFailure();
					}
				},
				error : function(xhr, textStatus, errorThrown) {
					operationFailure();
				}
			});/**/
			
		}

		//挂接成功调用
		function operationSuccess(opnions) {
			/*var url = "e5workspace/after.do?UUID=" + UUID + "&groupID="
					+ groupID + "&DocLibID=" + docLibID + "&Opinion="
					+ encodeU("操作成功！" + opnions);
			$("#frmColumn").attr("src", url);*/
			operationFailure();
			
		}

		//操作失败了调用
		function operationFailure() {
			window.parent.e5.mods["workspace.resourcetree"].close();
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
			window.parent.e5.mods["workspace.resourcetree"].close();
		}

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