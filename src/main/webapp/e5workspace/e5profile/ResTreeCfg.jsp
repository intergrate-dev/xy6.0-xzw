<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false" />

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><i18n:message key="workspace.ps.resTree.title" /></title>
		<link type="text/css" rel="stylesheet" href="../../e5style/work.css" />
		<script type="text/javascript" src="../../e5script/xtree/xtree.js"></script>
		<script type="text/javascript" src="../../e5script/xtree/xloadtree.js"></script>
		<script type="text/javascript" src="../../e5script/Function.js"></script>
	</head>
	<style>
		input{border:0;}
	</style>
	<script type="text/javascript">
		function doInit(){
			var list2 = document.permissionForm.chosen;
			for (var i = 0; i < list2.options.length; i++)
			{
				var o = list2.options[i];
				autoDel(o.value);
			}
		}

		function autoDel(id){
			var list1 = document.permissionForm.available;
			for (var i = 0; i < list1.options.length; i++)
			{
				var  o = list1.options[i];
				if (o != null && o.value == id)
				{
					list1.removeChild(o);
					break;
				}
			}
		}

	function onAdd(text,value){
		var list2 = document.permissionForm.chosen;
		if(!contain(list2,value))
		{
			var no = new Option( text, value );
			list2.add(no);
		}
	}
	// check if option with value 'value' exist in listbox 'list2'
	function contain( list2, value ){
		for( var i=0; i<list2.options.length; i++ )
		{
			var o = list2.options[i];
			if ( o.value == value ) return true;
		}
		return false;
	}

	function onAddAll(){
		
		var list2 = document.permissionForm.chosen;
		for( var i = 0; i < tree.childNodes.length; i++ )
		{
			var node = tree.childNodes[i];
			if(!contain(list2,node.getAttribute("fvid")))
			{
				var no = new Option( node.text, node.getAttribute("fvid"));
				list2.add(no);
			}
		}
	}

	function onDel(){
		//var list1 = document.permissionForm.available;
		var list2 = document.permissionForm.chosen;
		/*for(var i = 0; i < list2.options.length; i++)
		{
			var o = list2.options[i];
	  		if(o.selected)
			{
				var no = new Option( o.text, o.value );
				list1.add(no);
			}
		}*/
		for(var i = 0; i < list2.options.length; i++)
		{
			var o = list2.options[i];
	  		if(o.selected)
			{
				list2.removeChild(o);
				i = i - 1;
			}
		}
	}

	function onDelAll(){
		//var list1 = document.permissionForm.available;
		var list2 = document.permissionForm.chosen;
		/*for(var i = 0; i < list2.options.length; i++)
		{
			var o = list2.options[i];
			var no = new Option( o.text, o.value );
			list1.add(no);
		}*/
		for(var i = 0; i < list2.options.length; i++)
		{
			var o = list2.options[i];
			list2.removeChild(o);
			i = i - 1;
		}
	}

	function onUp(){
	  var list2 = document.permissionForm.chosen;

	  // swap selected option with previous one by one
	  for( var i=0; i<list2.options.length; i++ )
	  {
		var o = list2.options[i];
	  	if( o.selected && i>0 ){
			swapOpObj( list2.options[i-1], list2.options[i] );
		}
	  }
	}

	function onHead(){
	  var list2 = document.permissionForm.chosen;

	  while ( getFirstSelectedIndex( list2 ) > 0 )
	  	onUp();
	}

	function onDown(){
	  var list2 = document.permissionForm.chosen;

	  for( var i=list2.options.length-1; i>-1; i-- ){
		var o = list2.options[i];
	  	if( o.selected && i<list2.options.length-1 ){
			swapOpObj( list2.options[i+1], list2.options[i] );
		}
	  }
	}

	function onTail(){
	  var list2 = document.permissionForm.chosen;

	  var index = getLastSelectedIndex( list2 );
	  while ( index != -1 && index < list2.options.length-1 ) {
	  	onDown();
		index = getLastSelectedIndex( list2 );
	  }
	}

	// swap too option's attribute
	function swapOpObj( op1, op2 ) {
		var tmp = op1.value;
		op1.value = op2.value;
		op2.value = tmp;

		tmp = op1.text;
		op1.text = op2.text;
		op2.text = tmp;

		tmp = op1.selected;
		op1.selected = op2.selected;
		op2.selected = tmp;
	}

	function getFirstSelectedIndex( list ) {
		for ( var i=0; i<list.options.length; i++ ) {
			if ( list.options[i].selected ) return i;
		}
		return -1;
	}

	function getLastSelectedIndex( list ) {
		for ( var i=list.options.length-1; i>-1; i-- ) {
			if ( list.options[i].selected ) return i;
		}
		return -1;
	}

	function doReset()
	{
		window.location.reload();
	}

	function doSubmit() {
	  var list = document.permissionForm.chosen;
	  if(0 == list.options.length)
	  {
	    alert("<i18n:message key="workspace.ps.resTree.notallownull"/>");
	    return false;
	  }
	  for ( var i=0; i<list.options.length; i++ ) {
		list.options[i].selected = true;
	  }
	  try {
	  	if (parent && parent.setRefresh)
	  		parent.setRefresh(true);
	  }catch (E){}
	}

	function nodeClick(src)
	{
		var id  = src.getAttribute("fvid");//src.id;	
		var name= src.innerHTML;
	//		alert(id);
	//		alert(name);
		if(id !=null && id!='')
			onAdd(name,id);

	}

	</script>
</head>
<body scroll="no">

<form name="permissionForm" action="resTreeRootCfg.do" method="get" onsubmit="return doSubmit()">
<input type="hidden" name="action" value="setResTree">
<table cellspacing="0" cellpadding="0" align="center" style="width:95%;height:90%">
	<tr>
		<td>
		<table style="width:100%;height:100%">
			<tr align="center">
				<td valign="bottom" style="width:45%"><i18n:message
					key="workspace.ps.resTree.available" /></td>
				<td style="width:5%">&nbsp;</td>
				<td valign="bottom" style="width:45%"><i18n:message
					key="workspace.ps.resTree.chosen" /></td>
				<td style="width:5%">&nbsp;</td>
			</tr>
			<tr align="center">
				<td align="left" valign="top">
			<div style="width:180px;height:300px;overflow:auto;background-color:#DDDDDD">
			<script type="text/javascript">
				//tree
				webFXTreeConfig.rootPath = "../../e5script/";

				//webFXTreeConfig.cbRootable 	= false;
				//webFXTreeConfig.cbCount 	= 1;
				//webFXTreeConfig.cbLabels 	= [];
				//webFXTreeConfig.cbPrefix 	= ["show"];
				//webFXTreeConfig.cbRefAttribute = "fvid";
				webFXTreeConfig.defaultClickAction = "javascript:nodeClick(this);";
				webFXTreeConfig.getRootIcon = webFXTreeConfig.getFolderIcon = function(){
					return "../../images/icon/folder_close.gif";
				}
				webFXTreeConfig.getOpenRootIcon = webFXTreeConfig.getOpenFolderIcon = function(){
					return "../../images/icon/folder_open.gif";
				}
				webFXTreeConfig.getFileIcon = function(){
					return "../../images/icon/folder_document.gif";
				}

				var tree = new WebFXLoadTree("<i18n:message key="workspace.ps.resTree.title" />",
						"./fvPermissionTree.do");

				tree.show();
				
			</script>
			</div>
				</td>
				<td align="center">
				<!--<p><input type="button" class="butt" onclick="onAdd()"
					value="<i18n:message key="workspace.ps.resTree.add"/> -&gt;" /></p>-->
				<p><input type="button" class="butt" onclick="onAddAll()"
					value="<i18n:message key="workspace.ps.resTree.addAll"/> -&gt;&gt;" />
				</p>
				<p><input type="button" class="butt" onclick="onDel()"
					value="&lt;- <i18n:message key="workspace.ps.resTree.remove"/>" />
				</p>
				<p><input type="button" class="butt" onclick="onDelAll()"
					value="&lt;&lt;- <i18n:message key="workspace.ps.resTree.removeAll"/>" />
				</p>
				</td>
				<td align="center" valign="top"><select name="chosen"
					multiple="multiple" style="width:180px; height:300px;" ondblclick="onDel()">
					<c:forEach items="${chosenFolders}" var="folder">
						<option value="<c:out value="${folder.FVID}"/>"><c:out
							value="${folder.FVName}" /></option>
					</c:forEach>
				</select></td>
				<td align="center">
				<p><input type="button" class="butt" onclick="onHead()"
					value="<i18n:message key="workspace.ps.resTree.head"/>" /></p>
				<p><input type="button" class="butt" onclick="onUp()"
					ondblclick="onUp()"
					value="<i18n:message key="workspace.ps.resTree.up"/>" /></p>
				<p><input type="button" class="butt" onclick="onDown()"
					ondblclick="onDown()"
					value="<i18n:message key="workspace.ps.resTree.down"/>" /></p>
				<p><input type="button" class="butt" onclick="onTail()"
					value="<i18n:message key="workspace.ps.resTree.tail"/>" /></p>
				</td>
			</tr>
		</table>
		</td>
	</tr>

	<tr>
		<td align="center"><br />
		<p><input type="button" class="butt"
			value="<i18n:message key="workspace.ps.resTree.reset"/>"
			onclick="doReset()" />
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="submit" class="butt"
			value="<i18n:message key="workspace.ps.resTree.submit"/>" /></p>
		</td>
	</tr>
</table>
</form>
<br>
</body>
</html>
