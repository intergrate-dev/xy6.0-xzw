<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5sysui" changeResponseLocale="false"/>
<html>
<head>
	<title><%=com.founder.e5.context.Context.getSystemName()%>-<i18n:message key="sysui.title"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link type="text/css" rel="stylesheet" href="../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../e5style/sys-style.css"/>
	<script type="text/javascript" src="../e5script/Function.js"></script>
	<script type="text/javascript">
		//功能调用
		function show(theURL){
			if (theURL.indexOf("call:") == 0) {
				showDialog(theURL.substring(5));
			}
			else {
				var SysFrame = getIframe("SysFrame");
				SysFrame.src = theURL;
				if(SysFrame.style.display === "none"){
					SysFrame.style.display = "block";
				}
			}
		}
		//功能调用的新方式：弹出的对话框
		function showDialog(theURL) {
			//chrome等浏览器已经不支持 showModalDialog
			if (typeof(window.showModalDialog) == "undefined" ){
				var SysFrame = getIframe("SysFrame");
				SysFrame.src = theURL;
				if (SysFrame.style.display === "none"){
					SysFrame.style.display = "block";
				}
			} else {
				window.showModalDialog(theURL, "dialogWidth:400px;dialogHeight:550px;");
			}
		}
		//退出
		function logout(){
			if (confirm("<i18n:message key="sysmenu.sys.confirmexit"/>"))
			{
				theURL = "sysLogout.do";
				getDataProvider(theURL, doDefault, false);
				return true;
			}
		}
		//整体界面刷新
		function refresh() {
			window.onbeforeunload = "javascript:void(0);";
			window.location.reload();
		}
		//界面菜单的风格切换
		function viewChange(){
			var viewType = doGetCookie("SysViewType");
			if (viewType == "2")
				viewType = "1";
			else
				viewType = "2";
			doSetCookie("SysViewType", viewType);
			refresh();
		}
		//界面样式切换
		function styleChange(){
		}
		//重新登录
		function reLogin() {
			try{
				window.location.href="SysLogin.jsp";
			}catch (e){}
		}
		function doDefault(){}
		
		function doInit() {
			//若不是内嵌于其它系统，则最大化窗口
			if (parent == null || parent == window) {
				window.moveTo(0,0);
				window.resizeTo(screen.availWidth,screen.availHeight);
			}
		}
	</script>
</head>
<body onload="doInit()" oncontextmenu="return contextControl();" onbeforeunload="if (!logout()) return false;">
<%
if ("2".equals(com.founder.e5.web.WebUtil.getCookie(request, "SysViewType")))
{%>
	<%@include file="./SysMenu2.jsp" %>
<%}
else
{%>
	<%@include file="./SysMenu1.jsp" %>
<%}%>
</body>
</html>
