<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5context" changeResponseLocale="false"/>
<HTML>
<HEAD>
	<title><i18n:message key="info.cache"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script language="javascript" type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
	<script language="javascript" type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
	<script language="javascript" type="text/javascript" src="../e5script/Function.js"></script>
	<script type="text/javascript">
		var cacheCount = <c:out value="${groupCount}"/>;
		var serverCount = <c:out value="${serverCount}"/>;
		var divServers;
		$(function(){
			divServers = e5.dialog('',{
				time : 2000,
				autoClose : false,
				ishide : true,
				title : "<i18n:message key="cache.server.title"/>",
				resizable : false,
				width : 400,
				height : 200
			});
			divServers.DOM.content.append($("#divServers"));
		})
		function doRefresh(){
			var checks = "";
			for(var i = 0; i < cacheCount; i++){
				var checkbox = document.getElementById("cache" + i);
				if (checkbox.checked){
					checks += checkbox.value + ",";
				}	
			}
			if (checks == ""){
				return false;
			}
			doSubmit(checks);
		}
		function doSubmit(checks){
			if (!confirm("<i18n:message key="cache.refresh.confirm"/>")){
				return false;
			}
			if (serverCount > 0){
				for (var i = 0; i < serverCount; i++){
					var serverURL = document.getElementById("server" + i).getAttribute("url");
						//url += "/e5sys/cacheSubmit.do?Index=" + i + "&Checks=" + checks;
					var url = "./RemoteCacheSubmit.jsp?serverURL="+serverURL+"&Index="+i+"&Checks=" + checks;
					//alert(url);
					getDataProvider(url, showFinish, false, true);
				}
			}
			else{ //没有配置多服务器时，要刷新本服务器
				var url = "./cacheSubmit.do?Index=local&Checks=" + checks;
				getDataProvider(url, showFinish, false, true);
			}
		}
		function showFinish(type, data, evt){
			//alert(data)
			var result = data.split(",");
			var showtext = "<i18n:message key="cache.refresh.ok"/>";
			if (result.length > 1 && result[1]){
				var failtext = "<i18n:message key="cache.refresh.fail"/>";
				showtext = failtext;
			}
			var oDiv = document.getElementById("server" + result[0]);
			if (oDiv){
				oDiv.innerHTML = showtext;
			}else{
				oDiv = document.getElementById("serverlocal");
				if (oDiv){
					oDiv.innerHTML = showtext;
				}
			}
			divServers.show();
		}
	</script>
	<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
	<link type="text/css" rel="stylesheet" href="../e5style/e5sys-Cache.css"/>
	<link type="text/css" rel="stylesheet" href="../e5script/jquery/dialog.style.css"/>
</head>
<body>
	<!-- <div id="nameDiv"></div> -->
	<!--缓存刷新项-->
	<div class="mainBodyWrap">
	<table cellpadding="0" cellspacing="0" class="table">
		<caption><i18n:message key="cache.title"/></caption>
		<c:forEach var="group" items="${groups}" varStatus="nodeIndex">
		<tr>
			<td class="cacheDiv">
				<input type="checkbox" value="<c:out value="${group.cacheIndexes}"/>"
					name="cache<c:out value="${nodeIndex.index}"/>" id="cache<c:out value="${nodeIndex.index}"/>"/>
			</td>
			<td><label for="cache<c:out value="${nodeIndex.index}"/>"><c:out value="${group.name}"/></label></td>
		</tr>
		</c:forEach>
		<tr>
			<td colspan="2" class="alignCenter"><input type="button" class="button" onclick="doSubmit('All')" value="<i18n:message key="cache.submit.all"/>"/><input type="button" class="button" onclick="doRefresh()" value="<i18n:message key="cache.submit"/>"/></td>
		</tr>
	</table>
	<!-- <div class="cacheDiv"></div> -->
	<!-- <br/> -->
	<!--显示刷新情况的多服务器条目-->
	</div>
	<div id="divServers">
		<c:forEach var="server" items="${servers}" varStatus="serverIndex">
			<div class="cacheDiv"><c:out value="${server.name}"/>:
				<div id="server<c:out value="${serverIndex.index}"/>"
				 url="<c:out value="${server.url}"/>">&nbsp;</div>
			 </div>
		</c:forEach>
		<div id="serverlocal"></div>
	</div>
</body>
</html>
