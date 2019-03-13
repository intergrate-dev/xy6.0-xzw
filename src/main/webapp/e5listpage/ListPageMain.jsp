<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5listpage" changeResponseLocale="false"/>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@page import="com.founder.e5.listpage.ListPage"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><i18n:message key="pagelist.title"/></title>
		<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.query.js"></script>
		<script type="text/javascript" src="../e5script/jquery/jquery.resize.js"></script>
		<script type="text/javascript">
			var selectedTr,bodyFrame;
			var sidebarList;

			function init(){
				<c:if test="${ListID != null}">
				var trs = document.getElementsByTagName("tr");
				if (trs != null){
					for (var i = 0; i < trs.length; i++){
						if (trs[i].getAttribute("id") == '<c:out value="${ListID}"/>')
							trs[i].click();
					}
				}
				</c:if>
				bodyFrame = document.getElementById("bodyFrame");
				sidebarList = $("#sidebar");
				$("body").bind("resize",resizeHandle);
				$(".sidebar-list-hide").click(sidebarListHide);
				$(".sidebar-list-show").click(sidebarListShow);
				$(".browserIsHidden").click(sidebarListShow);
				function sidebarListHide(){
					sidebarList.hide();
					$(".bodyFrame").css("left","40px");
					$(".sidebar-list-show").show();
				}
				function sidebarListShow(){
					sidebarList.show();
					$(".bodyFrame").css("left","");
					$(".sidebar-list-show").hide();
				}
				$("body").trigger("resize");
			}
			function resizeHandle(){
				var bodyH = document.documentElement.scrollHeight;
				var sidebarListH = bodyH-sidebarList.offset().top;
				if(sidebarList[0].scrollHeight-10 > sidebarListH){
					sidebarList.css("height",sidebarListH);
					$("#sidebar-scroll").css("height",sidebarListH-70);
				}else{
					sidebarList.css("height","");
				}
			}
			function doAdd(){
				bodyFrame.src = "Wizard.do?action=new";
			}
			function doAddNew(){
				bodyFrame.src = "ListPageCustomList.jsp";
			}
			function selected(src){
				if(selectedTr){
					document.getElementById(selectedTr).style.backgroundColor='';
				}
				src.style.backgroundColor="#e8e8e8";
				selectedTr = src.getAttribute("id");
				doBrowser();
			}
			function doBrowser(){
				if(selectedTr){
					bodyFrame.src = "Browser.do?ListID=" + selectedTr;
				}
			}
			function doMod(){
				if(selectedTr){
					bodyFrame.src = "Wizard.do?action=mod&ListID=" + selectedTr;
				}else{
					alert('<i18n:message key="pagelist.alert.modify"/>');
				}
			}
			function doModNew(){
				if(selectedTr){
					$.ajax({
						url:"../e5listpage/ListSubmit.do?method=getlistpage&id="+selectedTr,
						dataType:"json",
						async:false,
						success:function(data){
							if(data!=null){
								var hasXSL = "0";
								if(data.pathXSL!=""){
									hasXSL = "1";
								}
								var params = {"docTypeID":data.docTypeID,
										"docTypeName":"",
										"listID":data.listID,
										"listName":data.listName,
										"slshowID":data.listBuilderName,
										"slshowName":"",
										"hasXLS":hasXSL,
										"icon":data.icon,
										"xlsPath":data.pathXSL
								};
								bodyFrame.src = "ListPageCustomList.jsp?"+$.param(params);
							}
						}
					});
				}
				else{
					alert('<i18n:message key="pagelist.alert.modify"/>');
				}
			}
			function doDel(){
				if(selectedTr){
					var ok = confirm("<i18n:message key="pagelist.confirm.delete"/>");
					if(ok){
						$.get("ListPageInit.do", { action: "del", ListID: selectedTr } ,function(){
							document.location.reload();
						});
					}
				}
				else{
					alert('<i18n:message key="pagelist.alert.delete"/>');
				}
			}
			
			function parentUpdate(listpageid){
				if(listpageid!="" && parseInt(listpageid.toString(),10)>0){
					document.location.reload();
					bodyFrame.src = "Browser.do?ListID=" + listpageid;
				}
				else{
				   document.location.reload();
				}
			}

			function te_show(e, docTypeID)
			{
				e = e || event;
				e.cancelBubble = true;
				//e.preventDefault();
				//e.stopPropagation();
				
				var expand = false;
				
				var img = document.getElementById("img" + docTypeID);
				var imgSrc = img.src;
				var imgName = imgSrc.substr((imgSrc.lastIndexOf("/")+1),imgSrc.length);
				if(imgName == 'plus.gif'){
					img.src="../images/minus.gif";
					expand = true;
				}else{
					img.src="../images/plus.gif";
				}
				
				var trs = $("#tab1 tr[docTypeID='" + docTypeID + "']");
				$.each(trs, function(i){
					if (i > 0) {
						if (expand)
						   this.style.display = "";
						else
							this.style.display = "none";
					}
				});
				
				if (expand)
					img.parentNode.setAttribute("rowspan",trs.length);
				else
					img.parentNode.setAttribute("rowspan",1);
			}
		</script>
		<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
		<style type="text/css">
			html,body{
				width:100%;
				height:100%;
				overflow: hidden;
			}
			.sidebar-list{
				width:300px;
				position: relative;
				overflow-y:auto;
			}
			.sidebar-list td{
				cursor: pointer;
			}
			.sidebar-list-show{
				background:url("../images/bg-draphelp-button.png") no-repeat right 0;
				width: 20px;
				height:20px;
				display: inline-block;
				cursor: pointer;
				display: none;
				position:absolute;
				left:10px;
				top:20px;
			}
			.sidebar-list-hide{
				background:url("../images/bg-draphelp-button.png") no-repeat 0 0;
				width: 20px;
				height:20px;
				display: inline-block;
				cursor: pointer;
				float: right;
			}
			.bodyFrame{
				position:absolute;
				left:350px;
				top:0;
				right:20px;
				height:100%;
			}
			#bodyFrame{
				width:100%;
				height:100%;
			}
			.table td.fold{
				background:#F5F5F5;
				border-right:1px solid #e8e8e8;
				vertical-align:top;
			}
		#sidebar-scroll{
			height:550px;
		}
		</style>
	</head>
	<body onload="init()">
		<div class="mainBodyWrap" id="mainBodyWrap">
			<div id="sidebar" class="sidebar-list">
				<table id="tab0" cellpadding="0" cellspacing="0" class="table">
					<caption><b class="sidebar-list-hide"></b><i18n:message key="pagelist.manager"/></caption>
				</table>
				<div id="sidebar-scroll" class="sidebar-list">
					<table id="tab1" cellpadding="0" cellspacing="0" class="table">					
						<tr>
							<th><i18n:message key="pagelist.docTypeName"/></th>
							<th><i18n:message key="pagelist.listid"/></th>
							<th><i18n:message key="pagelist.listname"/></th>
						</tr>
						<c:forEach var="listData" items="${listpageMap}" varStatus="var">
							<c:forEach var="list" items="${listData.list}" varStatus="listIndex">
								<tr id="<c:out value="${list.listid}"/>" docTypeID="<c:out value="${listData.docTypeID}"/>" onclick="selected(this)">
									<c:if test="${listIndex.index == 0}">
										<c:if test="${listData.count == 1}">
										<td id="ti0<c:out value="${var.index}"/>" class="fold"><c:out value="${listData.docTypeName}"/></td>
										</c:if>
										<c:if test="${listData.count > 1}">
										<td id="ti0<c:out value="${var.index}"/>" 
										onclick="te_show(event,'<c:out value="${listData.docTypeID}"/>')"
										rowspan="<c:out value="${listData.count}"/>" 
										class="fold">
										<img id="img<c:out value="${listData.docTypeID}"/>" src="../images/minus.gif" />
										<c:out value="${listData.docTypeName}"/>
										</td>
										</c:if>
									</c:if>
									<td><c:out value="${list.listid}"/></td>
									<td><c:out value="${list.listname}"/></td>
								</tr>
							</c:forEach>
						</c:forEach>					
					</table>
				</div>
				<table id="tab2" cellpadding="0" cellspacing="0" class="table">
					<tr>
						<td colspan="3" class="alignCenter">
						    <input class="button" type="button" value="<i18n:message key="pagelist.button.add"/>" onclick="doAddNew()" />
							<!-- <input class="button" type="button" value="<i18n:message key="pagelist.button.add"/>" onclick="doAdd()" /> -->
							<input class="button" type="button" value="<i18n:message key="pagelist.button.mod"/>" onclick="doModNew()" />
							<input class="button" type="button" value="<i18n:message key="pagelist.button.del"/>" onclick="doDel()" />
							
						</td>
					</tr>
				</table>
			</div>
			<b class="sidebar-list-show"></b>
			<div class="bodyFrame">
				<iframe id="bodyFrame" name="bodyFrame" src="" frameborder="0"></iframe>
			</div>
		</div>
	</body>
</html>
<%@include file="../e5include/Error.jsp"%>
