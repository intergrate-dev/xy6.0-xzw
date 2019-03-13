<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>


<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>ListPageCfg</title>
<link type="text/css" rel="stylesheet" href="../../e5style/work.css" />
<SCRIPT LANGUAGE="JavaScript">
<!--
<c:if test="${isGrid}">
	
	var curSel = null;

	function selectTD()
	{
		unSelectTD();
		var e = event.srcElement;
		curSel = e;
		e.style.backgroundColor = "skyblue";
	}
	function unSelectTD()
	{
		if( curSel )
			curSel.style.backgroundColor = "white";
	}
	function hideTD()
	{
		if( curSel ) {
			curSel.innerHTML = "&nbsp;";
			curSel.hide = true;
		}
	}
	function unhideTD()
	{
		if( curSel ) {
			curSel.innerText = curSel.nm;
			curSel.hide = false;
		}
	}

	function initGrid() {
		var table = document.getElementById( "grid" );
		for (var i = 0; i < table.rows.length; i++) {
			for (var j = 0; j < table.rows[i].cells.length; j++){
				var cell = table.rows[i].cells[j];
				cell.onclick = selectTD;
				cell.align = "center";
				if ( !cell.template )
					cell.template = cell.innerText;
				if ( cell.innerHTML != "&nbsp;" )
					cell.innerText = cell.nm;
			}
		}
	}

	function generateXML() {
		var xml = "";
		var table = document.getElementById( "grid" );
		for (var i = 0; i < table.rows.length; i++) {
			xml += "<tr>";

			for (var j = 0; j < table.rows[i].cells.length; j++){
				var cell = table.rows[i].cells[j];
				var hide = cell.getAttribute("hide");
				var xspan = cell.getAttribute("colSpan");
				var yspan = cell.getAttribute("rowSpan");
				var mywidth = cell.getAttribute("mywidth");
				var colname = cell.getAttribute("nm");
				var template = cell.getAttribute("template");

				xml += "<td";
				if (mywidth != null && mywidth != "")
					xml = xml +" mywidth=\""+mywidth+"\"";
				if (colname != null && colname != "")
					xml = xml +" nm=\""+colname+"\"";
				if (xspan > 1)
					xml = xml + " colSpan=\""+xspan+"\"";
				if (yspan > 1)
					xml = xml +" rowSpan=\""+yspan+"\"";
				xml = xml + ">";
				
				if ( !hide )
					xml += template;
				else
					xml += "&nbsp;";
				xml = xml + "</td>";
			
			}

			xml = xml + "</tr>";
		}

		document.form1.u_tableXml.value = xml;
	}
</c:if>

// delete option from selectObj whose value == id
function autoDel(selectObj, id)
{
	for (var i = 0; i < selectObj.options.length; i++)
	{
		var  o = selectObj.options[i];
		if (o != null && o.value == id)
		{
			selectObj.removeChild(o);
			break;
		}
	}
}
function doMove(selectObj, destSelectName)
{
	var srcSelect = selectObj;
	var destSelect = document.form1[destSelectName];
	move(srcSelect, destSelect);
}
//move selected options from srcSelect to destSelect
function move(srcSelect, destSelect)
{
	for( var i = 0; i < srcSelect.options.length; i++)
	{
		var o = srcSelect.options[i];
  		if(o.selected)
		{
			var no = new Option( o.text, o.value );
			if(o.type)
				no.type = o.type;
			if(o.name)
				no.name = o.name;
			destSelect.add(no);
			srcSelect.removeChild(o);
		}
	}
}
function doUp(selectObj)
{
	for( var i = 0; i < selectObj.options.length; i++)
	{
		var o = selectObj.options[i];
  		if(o.selected && i!=0)
		{
			swapOpObj( selectObj.options[i-1], selectObj.options[i] );
			break;
		}
	}
}
function doDown(selectObj)
{
	for( var i = 0; i < selectObj.options.length; i++)
	{
		var o = selectObj.options[i];
  		if(o.selected && i != selectObj.options.length-1)
		{
			swapOpObj( selectObj.options[i+1], selectObj.options[i] );
			break;
		}
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

	if(op1.type) {
		tmp = op1.type;
		op1.type = op2.type;
		op2.type = tmp;
	}

	if(op1.name) {
		tmp = op1.name;
		op1.name = op2.name;
		op2.name = tmp;
	}
}

function doInit() {
	var f = document.form1;
	f.listID.value = "<c:out value="${listPage.listID}" />";

	<c:choose>
	<c:when test="${isGrid}">
	initGrid();
	</c:when>
	<c:otherwise>
	for(var i=0; i<f.field_chosen.options.length; i++)
		autoDel(f.field_available, f.field_chosen.options[i].value);
	</c:otherwise>
	</c:choose>

	for(var i=0; i<f.sort_chosen.options.length; i++)
		autoDel(f.sort_available, f.sort_chosen.options[i].value);
	
	drawSort();
}
function onSubmit() 
{
	var f = document.form1;
	
	<c:choose>
	<c:when test="${isGrid}">
	generateXML();
	</c:when>
	<c:otherwise>
	for(var i=0; i<f.field_chosen.options.length; i++)
	{
		f.field_chosen.options[i].selected = true;
	}
	</c:otherwise>
	</c:choose>

	var fields = "";
	var types = "";
	for(var i=0; i<f.sort_chosen.options.length; i++)
	{
		var o = f.sort_chosen.options[i];
		fields += o.value;
		types += o.type;
		if( i != f.sort_chosen.options.length-1 ) {
			fields += ",";
			types += ",";
		}
	}

	f.action.value = "save";
	f.sort_fields.value = fields;
	f.sort_types.value = types;
}

var asc = "<i18n:message key="workspace.ps.listpage.asc" />";
var desc = "<i18n:message key="workspace.ps.listpage.desc" />";

//correct sort selectObj's display
function drawSort() {
	var f = document.form1;
	for(var i=0; i<f.sort_available.options.length; i++)
	{
		var o = f.sort_available.options[i];
		o.text = o.name;
	}
	for(var i=0; i<f.sort_chosen.options.length; i++)
	{
		var o = f.sort_chosen.options[i];
		if( "0" == o.type )
			o.innerHTML = o.name + "&nbsp;[" + asc + "]";
		else
			o.innerHTML = o.name + "&nbsp;[" + desc + "]";
	}
}

function doChange()
{
	var s = document.form1.sort_chosen;
	var o = s.options[s.selectedIndex];
	if ("0" == o.type)
	{
		o.type = "1";
		o.innerHTML = o.name + "&nbsp;[" + desc + "]";
	} else
	{
		o.type = "0";
		o.innerHTML = o.name + "&nbsp;[" + asc + "]";
	}
}
//-->
</SCRIPT>

</head>


<body onload="doInit()">

	<form name="form1" action="listPageCfg.do" method="post" onsubmit="onSubmit()">
	<input name="action" type="hidden" />
	<input name="listID" type="hidden" />
	<input name="sort_fields" type="hidden" />
	<input name="sort_types" type="hidden" />

	<table width="100%">
	<tr><td>
	<i18n:message key="workspace.ps.listpage.cfg" />
		<c:out value="${listPage.listName}" />
	</td>
	<td align="right">
	<c:if test="${!isGrid}">
		<label><i18n:message key="workspace.ps.listpage.pageSize" />
		<input type="text" name="pageSize" size="3" value="<c:out value="${pageSize}"/>"/></label>
	</c:if>
	</td>
	</table>

	<fieldset>
		<legend><i18n:message key="workspace.ps.listpage.chooseFields" /></legend>
		<c:choose>
		<c:when test="${isGrid}">
			<input name="u_tableXml" type="hidden" />

			<div style="margin:4px">
			<table><tr>
			<td>
			<table id="grid" width="240px" height="240px" border="1" borderColor="red" style="table-layout:fixed">
			<c:if test="${not empty u_tableXml}">
			<c:out value="${u_tableXml}" escapeXml="false"/>
			</c:if>
			</table>
			</td>
			<td width="50px">&nbsp;</td>
			<td>
				<button id="hide" class="butt" onclick="hideTD()">
				<i18n:message key="workspace.ps.listpage.hide" />
				</button><p/>				
				<button id="unhide" class="butt" onclick="unhideTD()">
				<i18n:message key="workspace.ps.listpage.unhide" />
				</button>
			</td>
			</tr></table>
			</div>
		</c:when>
		<c:otherwise>
		<table width="98%" border="0" cellpadding="4" cellspacing="0" class="onlyBorder">
			<tr>
			<td width="40%" class="bottomlinetd"><i18n:message key="workspace.ps.listpage.avails" /></td>
			<td width="40%"  class="bottomlinetd"><i18n:message key="workspace.ps.listpage.shows" /></td>
			<td>&nbsp;</td>
			</tr>
			<tr>
			<td valign="top" class="bottomlinetd">
			<select size="15" style="width:100%" name="field_available" ondblclick="move(this, document.form1.field_chosen)">
			<c:forEach var="item" items="${allColumns}">
				<option value="<c:out value="${item.name}"/>"><c:out value="${item.name}"/></option>
			</c:forEach>
			</select>
			</td>
			<td valign="top" class="bottomlinetd">
			<select SIZE="15" style="width:100%" name="field_chosen" multiple="multiple" ondblclick="move(this, document.form1.field_available)">
			<c:forEach var="item" items="${chosenColumns}">
				<option value="<c:out value="${item.name}"/>"><c:out value="${item.name}"/></option>
			</c:forEach>
			</select>
			</td>
			<td>
			<button name="up" class="butt" onclick="doUp(document.form1.field_chosen)">
			<i18n:message key="workspace.ps.listpage.moveUp" /></button><p/>			
			<button name="down" class="butt" onclick="doDown(document.form1.field_chosen)">
			<i18n:message key="workspace.ps.listpage.moveDown" /></button>
			</td>
			</tr>
		</table>
		</c:otherwise>
		</c:choose>
	</fieldset>

	<p/>

	<fieldset>
		<legend><i18n:message key="workspace.ps.listpage.chooseSorts" /></legend>
		<table width="98%" border="0" cellpadding="4" cellspacing="0" class="onlyBorder">
			<tr>
			<td width="40%" class="bottomlinetd"><i18n:message key="workspace.ps.listpage.avails" /></td>
			<td width="40%"  class="bottomlinetd"><i18n:message key="workspace.ps.listpage.sorts" /></td>
			<td>&nbsp;</td>
			</tr>
			<tr>
			<td valign="top" class="bottomlinetd">
			<select size="10" style="width:100%" name="sort_available" ondblclick="move(this, document.form1.sort_chosen);drawSort();">
			<c:forEach var="item" items="${dbFields}">
				<option value="<c:out value="${item.code}"/>" name="<c:out value="${item.name}"/>" type="0">
				<c:out value="${item.name}"/></option>
			</c:forEach>
			</select>
			</td>
			<td valign="top" class="bottomlinetd">
			<select SIZE="10" style="width:100%" name="sort_chosen" onclick="doChange()" ondblclick="move(this, document.form1.sort_available);drawSort();">
			<c:forEach var="item" items="${sortFields}">
				<option value="<c:out value="${item.code}"/>" name="<c:out value="${item.name}"/>" type="<c:out value="${item.type}"/>"><c:out value="${item.name}"/></option>
			</c:forEach>
			</select>
			</td>
			<td>
			<button name="up" class="butt" onclick="doUp(document.form1.sort_chosen)">
			<i18n:message key="workspace.ps.listpage.moveUp" /></button><p/>			
			<button name="down" class="butt" onclick="doDown(document.form1.sort_chosen)">
			<i18n:message key="workspace.ps.listpage.moveDown" /></button>
			</td>
			</tr>
		</table>
	</fieldset>

	<table border="0" width="80%">
	<tr height="20px"><td>&nbsp;</td></tr>
	<tr align="center">
		<td>
		<input type="button" class="butt" onclick="window.location.href='listPageChoose.do'"
			value="<i18n:message key="workspace.ps.listpage.return" />"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="reset" class="butt" onclick="window.location.reload()"
			value="<i18n:message key="workspace.ps.resTree.reset" />"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="submit" class="butt" value="<i18n:message key="workspace.ps.listpage.save" />"/>
		</td>
	</tr>
	</table>
	</form>

</body>

</html>