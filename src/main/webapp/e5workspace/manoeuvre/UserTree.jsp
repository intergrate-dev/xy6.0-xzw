<%@include file="../../e5include/IncludeTag.jsp"%>
<%@ page pageEncoding="UTF-8"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title><i18n:message key="workspace.catselect.title"/></title>
	<script type="text/javascript" src="../../e5script/Function.js"></script>
	<script type="text/javascript" src="../../e5script/xtree/xtree.js"></script>
	<script type="text/javascript" src="../../e5script/xtree/xloadtree.js"></script>
	<link type="text/css" rel="stylesheet" href="../../e5script/xtree/xtree.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/work.css"/>
	<style type="text/css">
		body{background-color:#EEEEEE;}
		a.disable{color:gray;}
	</style>
	<script>
		var preValues = "<c:out value="${userIDs}"/>";
		preValues = preValues.split(",");
		var isNew  = <c:out value="${isNew}"/>;
		var multiple = <c:out value="${multiple}"/>;
		var rootIDs = "<c:out value="${rootIDs}"/>";
		var noRoot = "<c:out value="${noRoot}"/>";
		var noPermission = "<c:out value="${noPermission}"/>";
		var treeTitle = "<c:out value="${treeTitle}"/>";
		var treeURL = "<c:out value="${treeURL}"/>";
		
		//tree
		webFXTreeConfig.rootPath = "../../e5script/";
		webFXTreeConfig.multiple = multiple;
		webFXTreeConfig.defaultContextAction = "return false;";
		webFXTreeConfig.defaultAction = "javascript:void(0);";
		webFXTreeConfig.preselect_enable = true;
		webFXTreeConfig.preselect_attr = "userID";
		webFXTreeConfig.preselect_values = preValues;
		
		webFXTreeConfig.getOpenFolderIcon = function(){
			return "../../images/org.gif";
		}
		webFXTreeHandler.setAutoPreselect = webFXTreeHandler.setAllPreselect;

		var tree = new WebFXLoadTree(treeTitle, treeURL + "?View=1&multiple="+multiple+"&rootIDs="+rootIDs+"&noRoot="+noRoot+"&noPermission="+noPermission);
	
		tree.setBehavior('classic');
		function showTree(){
			if (document.getElementById)
			{
				document.write(tree);
			}
		}
	
		//previous selected folder,only use single select.
		var preFolderID = "<c:out value="${userIDs}"/>";
	
		function userClick(src)
		{
			var selectTree = tree.getSelected();
			if(selectTree == null)
			{
				//delete
				doDelete(src.getAttribute("userID"));
				preFolderID = src.getAttribute("userID");
				return;
			}
			else
			{
				//add
				doAdd(src.getAttribute("userID"),src.getAttribute("cascadeName"),src.getAttribute("cascadeID"),selectTree.text);
				
				var theFolder = src.getAttribute("userID");

				//when not multiple,delete previous folder
				if( !multiple )
				{
					if (preFolderID) doDelete(preFolderID);
					setPreFolderID(theFolder);
				}
			}
		}
		function setPreFolderID(theFolder){
			if (theFolder == preFolderID)
				preFolderID = "";
			else
				preFolderID = theFolder;
		}
	
		function doAdd(userID,cascadeName,cascadeID,userName){
			var userIDs       = document.getElementById("userIDs");
			var cascadeNames = document.getElementById("cascadeNames");
			var cascadeIDs = document.getElementById("cascadeIDs");
			var userNames = document.getElementById("userNames");
			if(!userIDs.value){
			   userIDs.value = userID;
			   cascadeNames.value=cascadeName;
			   cascadeIDs.value=cascadeID;
			   userNames.value = userName;
			}
			else{
				userIDs.value = userIDs.value + "," + userID;
				cascadeNames.value=cascadeNames.value + "," + cascadeName;
				cascadeIDs.value=cascadeIDs.value + "," + cascadeID;
				userNames.value = userNames.value + "," + userName;
			}
			dispCascadeName();
		}
		function doDelete(userID){
			var userIDs       = document.getElementById("userIDs");
			var cascadeNames = document.getElementById("cascadeNames");
			var cascadeIDs = document.getElementById("cascadeIDs");
			var userNames = document.getElementById("userNames");
	
			var userIDArray = userIDs.value.split(",");
			var cascadeNameArray = cascadeNames.value.split(",");
			var cascadeIDArray = cascadeIDs.value.split(",");
			var userNameArray = userNames.value.split(",");
	
			var userIDsValue = "";
			var cascadeNamesValue = "";
			var cascadeIDsValue = "";
			var userNamesValue = "";
	
			for(i=0;i<userIDArray.length;i++){
				if(userIDArray[i] != userID){
					userIDsValue += userIDArray[i] + ",";
					cascadeNamesValue += cascadeNameArray[i] + ",";
					cascadeIDsValue += cascadeIDArray[i] + ",";
					userNamesValue += userNameArray[i] + ",";
				}
			}
	
			if (userIDsValue){
				userIDsValue= userIDsValue.substring(0,userIDsValue.length-1);
				cascadeNamesValue= cascadeNamesValue.substring(0,cascadeNamesValue.length-1);
				cascadeIDsValue= cascadeIDsValue.substring(0,cascadeIDsValue.length-1);
				userNamesValue = userNamesValue.substring(0,userNamesValue.length-1);
			}
			userIDs.value = userIDsValue;
			cascadeNames.value = cascadeNamesValue;
			cascadeIDs.value = cascadeIDsValue;
			userNames.value = userNamesValue;
	
			dispCascadeName();
	
			webFXTreeConfig.preselect_values = userIDsValue.split(",");
		}
		function dispCascadeName(){
			var userIDs       = document.getElementById("userIDs");
			var cascadeNames = document.getElementById("cascadeNames");
			var userNames = document.getElementById("userNames");
			
			var userIDArray = userIDs.value.split(",");
			var cascadeNameArray = cascadeNames.value.split(",");
			var userNameArray= userNames.value.split(",");

			var cname = "";
			if(userIDs.value){
				for(i=0;i<userNameArray.length;i++){
					cname += "<li onMouseOver=doMouseOver('"+i+"') style='cursor:hand' onDblclick=liClick('"
						+ userIDArray[i] + "')>"
						+ cascadeNameArray[i]
						+ "(" + userIDArray[i]+")" 
						+ "</li>";
				}
			}
			else
				cname = "&nbsp;";
			var cp = document.getElementById("contentPane");
			cp.innerHTML = cname;
		}
	
		//点击已选的
		function liClick(userID){
			var nodes = tree.getAllSelected();
	
			//最后一个没有加上？
			for(i=0;i<nodes.length;i++){
				var temp = nodes[i].getAttribute("userID");
				//alert("name="+nodes[i].text+",id="+temp);
				if(temp==userID)
				{
					nodes[i].focus();
					break;
				}
			}
			doDelete(userID)
		}
		function doMouseOver(idx){
			var obj  = window.document.getElementsByTagName("li");
			for (var j = 0; j < obj.length; j++){
				var tmp = obj[j];
				//其他的tr都是white，现在停留的是#a6caf0
				tmp.style.backgroundColor = "white";
				if(idx == j)
					tmp.style.backgroundColor = "#a6caf0";
			}
		}
		document.onselectstart=new Function("event.returnValue=false;");
	</script>
</head>

<body oncontextmenu="if (!event.ctrlKey){return false;}" onload="dispCascadeName()">
<c:if test="${noButton == null}">
	<div>
		<input type="button" class="bluebutton" onclick="doReturn()" value="<i18n:message key="workspace.catselect.sure"/>"/>
		<input type="button" class="bluebutton" onclick="doClose()" value="<i18n:message key="workspace.catselect.cancel"/>"/>
	</div>
</c:if>
<table border="1" cellspacing="0" width="100%" borderColor="#E0E0E0" class="work" >
	<tr>
		<td width="200" valign="top" height="400">
		    <script>
				showTree();
			</script>
			&nbsp;
		</td>
		<td valign="top">
			<div id="contentPane"></div>
		</td>
	</tr>
</table>
<br>
<input type="hidden" name="userIDs" id="userIDs" value="<c:out value="${userIDs}"/>">
<input type="hidden" name="userNames" id="userNames" value="<c:out value="${userNames}"/>">
<input type="hidden" name="cascadeIDs" id="cascadeIDs" value="<c:out value="${cascadeIDs}"/>">
<input type="hidden" name="cascadeNames" id="cascadeNames" value="<c:out value="${cascadeNames}"/>">

<c:if test="${noButton == null}">
	<div>
		<input type="button" class="bluebutton" onclick="doReturn()" value="<i18n:message key="workspace.catselect.sure"/>"/>
		<input type="button" class="bluebutton" onclick="doClose()" value="<i18n:message key="workspace.catselect.cancel"/>"/>
	</div>
</c:if>
</body>
</html>
<script type="text/javascript">
/**
 * 关闭窗口
 * 调用者可能是用open方式打开，也可能是按div + iframe方式
 * 所以在点“取消”按钮时，不能简单地调用window.close()
 * 要求使用div + iframe的调用者必须实现一个catWindowHidden方法，
 * 用来响应选择窗口的“取消”按钮

 * 如隐藏选择窗口等
 */
function doClose()
{
	var callback_postfix = "<c:out value="${postfix}"/>";
	var callback_function = "userWindowHidden" + callback_postfix;
	if (parent && parent[callback_function]) parent[callback_function]();
	else window.close();
}
/**
 * 点击“选择”按钮
 * 要求调用者必须实现一个catWindowSelect方法
 * 如给调用者窗口中某字段赋值等
 */
function doReturn()
{
	var selectID  = getUserIDs();
	var selectName = getUserNames();
	var selectCascadeID = getCascadeIDs();

	var callback_postfix = "<c:out value="${postfix}"/>";
	var callback_function = "userWindowSelect" + callback_postfix;
	try {
		if (parent && parent[callback_function]) {
			parent.userIDs = selectID;
			parent[callback_function](selectID, selectName,selectCascadeID);
		}
		else if (opener && opener[callback_function]) {	
			parent.userIDs = selectID;
			opener[callback_function](selectID, selectName,selectCascadeID);
		}else{
			doClose();
		}
	} catch (e){}
}
/**
 * 取选择的名称
 */
function getUserNames(){
	return document.getElementById("userNames").value;
}
/**
 * 取选择的ID串
 */
function getUserIDs(){
	return document.getElementById("userIDs").value;
}
/**
 * 取选择的级联ID串
 */
function getCascadeIDs(){
	return document.getElementById("cascadeIDs").value;
}

/**
 * 格式userName去掉(xxx)部分内容
 */
function formatName(userNames){
	if(userNames == "") return "";

	var userNameArray = userNames.split(",");
	var temp  = "";
	var j     = 0;
	for(;j<userNameArray.length;j++){
		temp = temp + userNameArray[j].substring(0,userNameArray[j].lastIndexOf("(")) + ",";
	}
	if(temp.length>0)
	   temp  = temp.substring(0,temp.length-1);
	return temp;
}
</script>
