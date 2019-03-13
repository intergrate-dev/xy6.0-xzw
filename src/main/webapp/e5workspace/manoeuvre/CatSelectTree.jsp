<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<HTML>
<HEAD>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<TITLE><i18n:message key="workspace.catselect.title"/></TITLE>
	<script type="text/javascript" src="../../e5script/xtree/xtree.js"></script>
	<script type="text/javascript" src="../../e5script/xtree/xloadtree.js"></script>
	<link type="text/css" rel="stylesheet" href="../../e5script/xtree/xtree.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/work.css"/>
	<style type="text/css">
	body{background-color:#EEEEEE;}
	</style>
</HEAD>

<BODY oncontextmenu="if (!event.ctrlKey){return false;}" onload="dispCascadeName()">
<Script>
	
	var preValues = "<c:out value="${catIDs}"/>";
	preValues = preValues.split(",");

	var catType= <c:out value="${catType}"/>;
	var rootIDs = "<c:out value="${rootIDs}"/>";
	var noRoot = "<c:out value="${noRoot}"/>";
	var noPermission = "<c:out value="${noPermission}"/>";
	var multiple = <c:out value="${multiple}"/>;
	var catLevel = "<c:out value="${catLevel}"/>";

	//tree
	webFXTreeConfig.rootPath = "../../e5script/";
	webFXTreeConfig.multiple = multiple;
	webFXTreeConfig.defaultContextAction = "return false;";
	webFXTreeConfig.defaultAction = "javascript:void(0);";
	webFXTreeConfig.preselect_enable = true;
	webFXTreeConfig.preselect_attr = "catID";
	webFXTreeConfig.preselect_values = preValues;

	var tree = new WebFXLoadTree("<c:out value="${catTypeName}"/>", "./CatSelectTree.do?catType="+catType+"&multiple="+multiple+"&rootIDs="+rootIDs+"&noRoot="+noRoot+"&noPermission="+noPermission + "&catLevel=" + catLevel);

	tree.setBehavior('classic');
	function showTree(){
		if (document.getElementById)
		{
			document.write(tree);
		}
	}
	//previous selected folder,only use single select.
	var preFolderID = "<c:out value="${catIDs}"/>";

	function catClick(src)
	{
		var selectTree = tree.getSelected();
		if(selectTree == null)
		{
			//delete
			doDelete(src.getAttribute("catID"));
			preFolderID = src.getAttribute("catID");
			return;
		}
		else
		{
			//add
			doAdd(src.getAttribute("catID"),src.getAttribute("cascadeName"),src.getAttribute("cascadeID"),selectTree.text);

			var theFolder = src.getAttribute("catID");
			//when not multiple,delete previous folder
			if(!multiple)
			{
				if (preFolderID) doDelete(preFolderID);
				setPreFolderID(theFolder);
			}
		}
	}
	function setPreFolderID(theFolder)
	{
		if (theFolder == preFolderID)
			preFolderID = "";
		else
			preFolderID = theFolder;
	}

	function doAdd(catID,cascadeName,cascadeID,catName)
	{
		var catIDs       = document.getElementById("catIDs");
		var cascadeNames = document.getElementById("cascadeNames");
		var cascadeIDs = document.getElementById("cascadeIDs");
		var catNames = document.getElementById("catNames");
		if(catIDs.value == '')
		{
		   catIDs.value = catID;
		   cascadeNames.value=cascadeName;
		   cascadeIDs.value=cascadeID;
		   catNames.value = catName;

		}
		else
		{
			catIDs.value = catIDs.value + "," + catID;
			cascadeNames.value=cascadeNames.value + "," + cascadeName;
			cascadeIDs.value=cascadeIDs.value + "," + cascadeID;
			catNames.value = catNames.value + "," + catName;
		}
		dispCascadeName();
	}
	function doDelete(catID)
	{
		var catIDs       = document.getElementById("catIDs");
		var cascadeNames = document.getElementById("cascadeNames");
		var cascadeIDs = document.getElementById("cascadeIDs");
		var catNames = document.getElementById("catNames");

		var catIDArray = catIDs.value.split(",");
		var cascadeNameArray = cascadeNames.value.split(",");
		var cascadeIDArray = cascadeIDs.value.split(",");
		var catNameArray = catNames.value.split(",");

		var catIDsValue = "";
		var cascadeNamesValue = "";
		var cascadeIDsValue = "";
		var catNamesValue = "";

		for(i=0;i<catIDArray.length;i++)
		{
			if(catIDArray[i] != catID)
			{
				catIDsValue += catIDArray[i] + ",";
				cascadeNamesValue += cascadeNameArray[i] + ",";
				cascadeIDsValue += cascadeIDArray[i] + ",";
				catNamesValue += catNameArray[i] + ",";

			}
		}

		if(catIDsValue!='')
		{
			catIDsValue= catIDsValue.substring(0,catIDsValue.length-1);
			cascadeNamesValue= cascadeNamesValue.substring(0,cascadeNamesValue.length-1);
			cascadeIDsValue= cascadeIDsValue.substring(0,cascadeIDsValue.length-1);
			catNamesValue = catNamesValue.substring(0,catNamesValue.length-1);
		}
		catIDs.value = catIDsValue;
		cascadeNames.value = cascadeNamesValue;
		cascadeIDs.value = cascadeIDsValue;
		catNames.value = catNamesValue;

		dispCascadeName();

		webFXTreeConfig.preselect_values = catIDsValue.split(",");
	}
	function dispCascadeName()
	{
		var catIDs       = document.getElementById("catIDs");
		var cascadeNames = document.getElementById("cascadeNames");
		var catNames = document.getElementById("catNames");
		var catIDArray = catIDs.value.split(",");
		var cascadeNameArray = cascadeNames.value.split(",");
		var catNameArray= catNames.value.split(",");

		var cname = "";
		if (catIDs.value != '') {
			for (var i = 0; i < catIDArray.length; i++) {
				cname += "<li onMouseOver=doMouseOver('"+i+"') style='cursor:hand' onDblclick=liClick('"+catIDArray[i]+"')>"+cascadeNameArray[i]+"("+catIDArray[i]+")"+"</li>";
			}
		} else
			cname = "&nbsp;";

		var cp = document.getElementById("contentPane");
		cp.innerHTML = cname;
	}

	//点击已选的分类
	function liClick(catID)
	{
		var nodes = tree.getAllSelected();

		//最后一个没有加上？

		for(i=0;i<nodes.length;i++)
		{
			var temp = nodes[i].getAttribute("catID");
			//alert("name="+nodes[i].text+",id="+temp);
			if(temp==catID)
			{
				nodes[i].focus();
				break;
			}
		}

		doDelete(catID)

	}
	function doMouseOver(idx)
	{

		var obj  = window.document.getElementsByTagName("li");
		for (var j = 0; j < obj.length; j++)
		{
			var tmp = obj[j];
			//其他的tr都是white，现在停留的是#a6caf0
			tmp.style.backgroundColor = "white";
			if(idx == j)
				tmp.style.backgroundColor = "#a6caf0";

		}
	}
document.onselectstart=new Function("event.returnValue=false;");
</Script>
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
<input type="hidden" name="catIDs" id="catIDs" value="<c:out value="${catIDs}"/>">
<input type="hidden" name="cascadeNames" id="cascadeNames" value="<c:out value="${cascadeName}"/>">
<input type="hidden" name="cascadeIDs" id="cascadeIDs" value="<c:out value="${cascadeID}"/>">
<input type="hidden" name="catNames" id="catNames" value="<c:out value="${catNames}"/>">
<c:if test="${noButton == null}">
	<div>
		<input type="button" class="bluebutton" onclick="doReturn()" value="<i18n:message key="workspace.catselect.sure"/>"/>
		<input type="button" class="bluebutton" onclick="doClose()" value="<i18n:message key="workspace.catselect.cancel"/>"/>
	</div>
</c:if>
</BODY>
</HTML>
<script type="text/javascript">
/**
 * 关闭窗口
 * 调用者可能是用open方式打开，也可能是按div + iframe方式
 * 所以在点“取消”按钮时，不能简单地调用window.close()
 * 要求使用div + iframe的调用者必须实现一个catWindowHidden方法，

 * 用来响应分类选择窗口的“取消”按钮

 * 如隐藏分类选择窗口等

 */
function doClose()
{
	var callback_postfix = "<c:out value="${postfix}"/>";
	var callback_function = "catWindowHidden" + callback_postfix;
	if (parent && parent[callback_function]) parent[callback_function]();
	else window.close();
}
/**
 * 点击“选择”按钮

 * 要求调用者必须实现一个catWindowSelect方法，

 * 用来响应分类选择
 * 如给调用者窗口中某分类字段赋值等
 */
function doReturn()
{
	var selectID  = getCatIDs();
	var selectName = getCatNames();
	var selectCascadeID = getCascadeIDs();
	var cascadeNames = document.getElementById("cascadeNames").value;

	var callback_postfix = "<c:out value="${postfix}"/>";
	var callback_function = "catWindowSelect" + callback_postfix;
	try {
		if (parent && parent[callback_function]){
			parent[callback_function](selectID, selectName,selectCascadeID, cascadeNames);
		}else if (opener && opener[callback_function]){
			opener[callback_function](selectID, selectName,selectCascadeID, cascadeNames);
		}else{
			doClose();
		}
	} catch (e){}

}
/**
 * 取选择的分类名称

 */
function getCatNames()
{
	return formatName(document.getElementById("catNames").value);
}
/**
 * 取选择的分类ID串

 */
function getCatIDs()
{
	return document.getElementById("catIDs").value;
}
/**
 * 取选择的分类级联ID串

 */
function getCascadeIDs()
{
	return document.getElementById("cascadeIDs").value;
}

/**
 * 格式catName去掉(xxx)部分内容
 */
function formatName(catNames)
{
	if(catNames == "") return "";

	var catNameArray = catNames.split(",");
	var temp  = "";
	var j     = 0;
	for(;j<catNameArray.length;j++)
	{
		var pos = catNameArray[j].lastIndexOf("(");
		if (pos > 0)
			temp = temp + catNameArray[j].substring(0, pos) + ",";
		else
			temp = temp + catNameArray[j] + ",";
	}

	if(temp.length>0)
	   temp  = temp.substring(0,temp.length-1);
	return temp;
}
</script>
