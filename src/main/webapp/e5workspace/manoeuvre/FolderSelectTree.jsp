<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<HTML>
<HEAD>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<TITLE><i18n:message key="workspace.folderselect.title"/></TITLE>
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
	var preValues = "<c:out value="${selectedID}"/>";
	preValues = preValues.split(",");
	var multiple = <c:out value="${multiple}"/>;
	//tree
	webFXTreeConfig.rootPath = "../../e5script/";
	webFXTreeConfig.multiple = multiple;

	webFXTreeConfig.defaultContextAction = "return false;";
	webFXTreeConfig.defaultAction = "javascript:void(0);";
	webFXTreeConfig.preselect_enable = true;
	webFXTreeConfig.preselect_attr = "folderID";
	webFXTreeConfig.preselect_values = preValues;
	var tree = new WebFXLoadTree("<i18n:message key="workspace.folderselect.title"/>", "FolderSelectTree.do?docTypeID=<c:out value="${docTypeID}"/>&docLibIDs=<c:out value="${docLibIDs}"/>&folderIDs=<c:out value="${folderIDs}"/>");


	tree.setBehavior('classic');

	function showTree(){
		if (document.getElementById)
		{
			document.write(tree);
		}
	}

	//previous selected folder,only use single select.
	var preFolderID = "<c:out value="${selectedID}"/>";

	function doClick(src)
	{
		var selectTree = tree.getSelected();
		if(selectTree == null)
		{
			//delete
			doDelete(src.getAttribute("folderID"));
			preFolderID = src.getAttribute("folderID");
			return;
		}
		//add
		doAdd(src.getAttribute("folderID"),src.getAttribute("folderNameAndDocLibName"),src.getAttribute("folderName"));
		var theFolder = src.getAttribute("folderID");
		//when not multiple,delete previous folder
		if(!multiple)
		{
			if (preFolderID) doDelete(preFolderID);
			setPreFolderID(theFolder);
		}
	}
	function setPreFolderID(theFolder)
	{
		if (theFolder == preFolderID)
			preFolderID = "";
		else
			preFolderID = theFolder;
	}

	function doAdd(folderID,folderNameAndDocLibName,folderName)
	{
		var folderIDs       = document.getElementById("folderIDs");
		var folderNames = document.getElementById("folderNames");
		var folderNameAndDocLibNames = document.getElementById("folderNameAndDocLibNames");

		if(folderIDs.value == '')
		{
		   folderIDs.value = folderID;
		   folderNames.value = folderName;
		   folderNameAndDocLibNames.value = folderNameAndDocLibName;
		}
		else
		{
			folderIDs.value = folderIDs.value + "," + folderID;
			folderNames.value = folderNames.value + "," + folderName;
			folderNameAndDocLibNames.value = folderNameAndDocLibNames.value + "," + folderNameAndDocLibName;
		}
		dispCascadeName();
	}
	function doDelete(folderID)
	{
		var folderIDs       = document.getElementById("folderIDs");
		var folderNames = document.getElementById("folderNames");
		var folderNameAndDocLibNames = document.getElementById("folderNameAndDocLibNames");

		var folderIDArray = folderIDs.value.split(",");
		var folderNameArray = folderNames.value.split(",");
		var folderNameAndDocLibNameArray = folderNameAndDocLibNames.value.split(",");

		var folderIDsValue = "";
		var folderNamesValue = "";
		var folderNameAndDocLibNamesValue = "";

		for(i=0;i<folderIDArray.length;i++)
		{
			if(folderIDArray[i] != folderID)
			{
				folderIDsValue += folderIDArray[i] + ",";
				folderNamesValue += folderNameArray[i] + ",";
				folderNameAndDocLibNamesValue += folderNameAndDocLibNameArray[i] + ",";
			}
		}

		if(folderIDsValue!='')
		{
			folderIDsValue= folderIDsValue.substring(0,folderIDsValue.length-1);
			folderNamesValue = folderNamesValue.substring(0,folderNamesValue.length-1);
			folderNameAndDocLibNamesValue = folderNameAndDocLibNamesValue.substring(0,folderNameAndDocLibNamesValue.length-1);
		}
		folderIDs.value = folderIDsValue;
		folderNames.value = folderNamesValue;
		folderNameAndDocLibNames.value = folderNameAndDocLibNamesValue;

		dispCascadeName();

		webFXTreeConfig.preselect_values = folderIDsValue.split(",");
	}
	function dispCascadeName()
	{
		var folderIDs       = document.getElementById("folderIDs");
		var folderNameAndDocLibNames = document.getElementById("folderNameAndDocLibNames");
		var folderIDArray = folderIDs.value.split(",");
		var folderNameAndDocLibNameArray= folderNameAndDocLibNames.value.split(",");

		var cname = "";
		if(folderIDs.value!='')
		{
			for(i=0;i<folderNameAndDocLibNameArray.length;i++)
			{
				cname += "<li onMouseOver=doMouseOver('"+i+"') style='cursor:hand' onDblclick=liClick('"+folderIDArray[i]+"')>"+folderNameAndDocLibNameArray[i]+"("+folderIDArray[i]+")"+"</li>";
			}
		}
		else
			cname = "&nbsp;";

		var cp = document.getElementById("contentPane");
		cp.innerHTML = cname;
	}

	//点击已选的分类
	function liClick(folderID)
	{
		var nodes = tree.getAllSelected();

		//最后一个没有加上？
		for(i=0;i<nodes.length;i++)
		{
			var temp = nodes[i].getAttribute("folderID");
			//alert("name="+nodes[i].text+",id="+temp);
			if(temp==folderID)
			{
				nodes[i].focus();
				break;
			}
		}
		doDelete(folderID);
		//when not multiple,delete previous folder id
		if(!multiple)
			setPreFolderID(folderID);
	}
	function doMouseOver(idx)
	{

		var obj  = window.document.getElementsByTagName("li");
		for (var j = 0; j < obj.length; j++)
		{
			var tmp = obj[j];
			//其他的TR都是white，现在停留的是#a6caf0
			tmp.style.backgroundColor = "white";
			if(idx == j)
				tmp.style.backgroundColor = "#a6caf0";

		}
	}
	document.onselectstart = new Function("event.returnValue=false;");
</Script>
<c:if test="${noButton == null}">
	<button onclick="doReturn()"><i18n:message key="workspace.folderselect.sure"/></button>
	<button onclick="doClose()"><i18n:message key="workspace.folderselect.cancel"/></button>
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
<input type="hidden" name="folderIDs" id="folderIDs" value="<c:out value="${selectedID}"/>">
<input type="hidden" name="folderNames" id="folderNames" value="<c:out value="${selectedName}"/>">
<input type="hidden" name="folderNameAndDocLibNames" id="folderNameAndDocLibNames" value="<c:out value="${selectedFolderNameAndDocLibName}"/>">
<c:if test="${noButton == null}">
	<button onclick="doReturn()"><i18n:message key="workspace.folderselect.sure"/></button>
	<button onclick="doClose()"><i18n:message key="workspace.folderselect.cancel"/></button>
</c:if>
</BODY>
</HTML>
<script language="javascript">
/**
 * �رմ���
 * �����߿�������open��ʽ�򿪣�Ҳ�����ǰ�div + iframe��ʽ
 * �����ڵ㡰ȡ��ťʱ�����ܼ򵥵ص���window.close()
 * Ҫ��ʹ��div + iframe�ĵ����߱���ʵ��һ��folderWindowHidden������
 * ��4��Ӧ�ļ���ѡ�񴰿ڵġ�ȡ��ť
 * ������ѡ�񴰿ڵ�
 */
function doClose()
{
	if (parent && parent.folderWindowHidden) parent.folderWindowHidden();
	else window.close();
}
/**
 * ���ѡ�񡱰�ť

 * Ҫ������߱���ʵ��һ��folderWindowSelect������
 * ��4��Ӧ����ѡ��
 * �������ߴ�����ĳ�ļ����ֶθ�ֵ��
 */
function doReturn()
{
	var selectID  = getFolderIDs();
	var selectName= getFolderNames();
	if (!selectID)
	{
		alert("<i18n:message key="workspace.alert.folderselect"/>");
		return;
	}
	try {
		if (parent && parent.folderWindowSelect) parent.folderWindowSelect(selectID, selectName);
		else if (opener && opener.folderWindowSelect) opener.folderWindowSelect(selectID, selectName);
		doClose();
	} catch (e){}

}
/**
 * ȡѡ����ļ������
 */
function getFolderNames()
{
	return document.getElementById("folderNames").value;
}
/**
 * ȡѡ����ļ���ID��
 */
function getFolderIDs()
{
	return document.getElementById("folderIDs").value;
}

</script>
