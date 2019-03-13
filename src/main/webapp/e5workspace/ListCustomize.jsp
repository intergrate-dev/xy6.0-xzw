<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false" />
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>当前列表定制</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<script type="text/javascript" src="../e5script/jquery/jquery.min.js"></Script>
	<link rel="stylesheet" type="text/css" href="../e5style/work.css" />
</head>
<Style>
	input{border:0;}
	p{padding:5px;}
</Style>
<script type="text/JavaScript">
function doInit()
{
	addList1();

	var list1 = document.form1.available;
	var list2 = document.form1.chosen;
	<c:forEach items="${chosen}" var="item">
		var idx = getIndex(list1, "<c:out value="${item}"/>");
		if (idx > -1) {
			list2.options.add(newOption(list1.options[idx].text, list1.options[idx].value));
			list1.remove(idx);
		}
	</c:forEach>
}
function addList1() {
	//var alls = top.currentDocMainFrame.getAllLines();
	var alls = window.parent.e5.mods["workspace.doclist"].getListHeaders();
	if (!alls) return;

	var list1 = document.form1.available;
	for (var i = 0; i < alls.length; i++) {
		var text = alls[i].name;
		if (!text)
			text = "[ 列 " + (i + 1) + " ]";
		list1.options.add(newOption(text, alls[i].id));
	}
}
function newOption(text1, value1) {
	var opt = document.createElement("OPTION");
	opt.value = value1;
	opt.text = text1;
	return opt;
}
function onAdd()
{
	var list1 = document.form1.available;
	var list2 = document.form1.chosen;
	for( var i = 0; i < list1.options.length; i++)
	{
		var o = list1.options[i];
  		if(o.selected)
		{
			var no = newOption( o.text, o.value );
			list2.options.add(no);
		}
	}
	for( var i = 0; i < list1.options.length; i++)
	{
		var o = list1.options[i];
  		if(o.selected) {
			list1.removeChild(o);
			i = i - 1;
		}
	}
}
// check if option with value 'value' exist in listbox 'list2'
function contain( list2, value )
{
	for( var i=0; i<list2.options.length; i++ )
	{
		var o = list2.options[i];
		if ( o.value == value ) return true;
	}
	return false;
}
function getIndex( list2, value )
{
	for( var i=0; i<list2.options.length; i++ )
	{
		var o = list2.options[i];
		if ( o.value == value ) return i;
	}
	return -1;
}
function onAddAll()
{
	var list1 = document.form1.available;
	var list2 = document.form1.chosen;
	for( var i = 0; i < list1.options.length; i++ )
	{
		var o = list1.options[i];
		var no = newOption( o.text, o.value );
		list2.options.add(no);
	}
	while (list1.options.length > 0)
		list1.remove(0);
}

function onDel()
{
	var list1 = document.form1.available;
	var list2 = document.form1.chosen;
	for(var i = 0; i < list2.options.length; i++)
	{
		var o = list2.options[i];
  		if(o.selected)
		{
			var no = newOption( o.text, o.value );
			list1.options.add(no);
		}
	}
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

function onDelAll()
{
	var list1 = document.form1.available;
	var list2 = document.form1.chosen;
	for(var i = 0; i < list2.options.length; i++)
	{
		var o = list2.options[i];
		var no = newOption( o.text, o.value );
		list1.options.add(no);
	}
	while (list2.options.length > 0)
		list2.remove(0);
}

function onUp()
{
  var list2 = document.form1.chosen;

  // swap selected option with previous one by one
  for( var i=0; i<list2.options.length; i++ )
  {
	var o = list2.options[i];
  	if( o.selected && i>0 ){
		swapOpObj( list2.options[i-1], list2.options[i] );
	}
  }
}

function onHead() {
  var list2 = document.form1.chosen;

  while ( getFirstSelectedIndex( list2 ) > 0 )
  	onUp();
}

function onDown() {
  var list2 = document.form1.chosen;

  for( var i=list2.options.length-1; i>-1; i-- ){
	var o = list2.options[i];
  	if( o.selected && i<list2.options.length-1 ){
		swapOpObj( list2.options[i+1], list2.options[i] );
	}
  }
}

function onTail() {
  var list2 = document.form1.chosen;

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

function doClose(){
	parent.e5.mods["workspace.doclist"].listCustDone();
}
function doReset(){
	//为了跨浏览器支持，不做刷新，在本页面手工重置。
	var theURL = "./ListpageCust.do?listID=<c:out value="${listID}"/>&reset=1";
	$.get(theURL, function() {
		var list1 = document.form1.available;
		var list2 = document.form1.chosen;
		while (list1.options.length > 0) list1.remove(0);
		while (list2.options.length > 0) list2.remove(0);
			
		addList1();
	});
}

function doSubmit() {
  var list = document.form1.chosen;
  if(0 == list.options.length) {
    alert("<i18n:message key="workspace.ps.resTree.notallownull"/>");
    return false;
  }
  for ( var i=0; i<list.options.length; i++ ) {
	list.options[i].selected = true;
  }
}

</SCRIPT>
<body onload="doInit()">
<form name="form1" method="post" action="ListpageCust.do" onsubmit="doSubmit()">
	<input type="hidden" name="listID" value="<c:out value="${listID}"/>"/>
	<table style="width:100%;">
		<tr align="center">
			<td valign="bottom" style="width:45%"><i18n:message key="workspace.ps.resTree.available" /></td>
			<td style="width:5%">&nbsp;</td>
			<td valign="bottom" style="width:45%"><i18n:message key="workspace.ps.resTree.chosen" /></td>
			<td style="width:5%">&nbsp;</td>
		</tr>
		<tr align="center">
			<td align="center" valign="top">
				<select class="select" name="available" size="20" multiple="multiple" ondblclick="onAdd()">
				</select>
			</td>
			<td align="center">
				<p><input type="button" class="butt" onclick="onAdd()" value="<i18n:message key="workspace.ps.resTree.add"/>" /></p>
				<p><input type="button" class="butt" onclick="onAddAll()" value="<i18n:message key="workspace.ps.resTree.addAll"/>" /></p>
				<p><input type="button" class="butt" onclick="onDel()" value=" <i18n:message key="workspace.ps.resTree.remove"/>" /></p>
				<p><input type="button" class="butt" onclick="onDelAll()" value="<i18n:message key="workspace.ps.resTree.removeAll"/>" /> </p>
			</td>
			<td align="center" valign="top">
				<select class="select1" name="chosen" size="20" multiple="multiple" ondblclick="onDel()"></select>
			</td>
			<td align="center">
				<p><input type="button" class="butt" onclick="onHead()" value="<i18n:message key="workspace.ps.resTree.head"/>" /></p>
				<p><input type="button" class="butt" onclick="onUp()" ondblclick="onUp()" value="<i18n:message key="workspace.ps.resTree.up"/>" /></p>
				<p><input type="button" class="butt" onclick="onDown()" ondblclick="onDown()" value="<i18n:message key="workspace.ps.resTree.down"/>" /></p>
				<p><input type="button" class="butt" onclick="onTail()" value="<i18n:message key="workspace.ps.resTree.tail"/>" /></p>
			</td>
		</tr>
	</table>
	<p align="center">
		<input type="button" class="butt" value="<i18n:message key="workspace.ps.resTree.reset"/>" onclick="doReset()" />
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="submit" class="butt" value="<i18n:message key="workspace.ps.resTree.submit"/>" />
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" class="butt" value="关闭" onclick="doClose()" />
	</p>
</form>
</body>
</html>
