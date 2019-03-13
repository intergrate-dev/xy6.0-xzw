<%@include file="../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.founder.e5.cat.CatReader"%>
<%@page import="com.founder.e5.context.Context"%>
<%@page import="com.founder.e5.dom.FieldType"%>
<%
	CatReader catReader = (CatReader)Context.getBean(CatReader.class);

	request.setAttribute("catTypes", catReader.getTypes());

	List<FieldType> fieldTypes = FieldType.getAll();
	request.setAttribute("fieldTypes", fieldTypes);
%>
<html>
<head>
<title>属性（字段）自定义</title>
<style type="text/css">
	#titleDiv{
		height: 40px;
		background-color: #DDDDDD;
		padding-top: 10px;
		padding-left: 200px;
	}
	.checkbox{border:0;}
	form{margin:0px;}
}
</style>
<link type="text/css" rel="StyleSheet" href="../e5style/style.css"/>
<script type="text/javascript" src="../e5script/Function.js"></script>
<script type="text/javascript">

function showAddDiv(){
	addDiv.style.display="";
}
function hideAddDiv(){
	addDiv.style.display="none";
	var hiddenItems = ["trEnum","trCat", "trMulti"];
	show_hide(hiddenItems, "none");
}
function show_hide(hiddenItems, displayStyle) {
	for (var i = 0; i < hiddenItems.length; i++) {
		var tr = document.getElementById(hiddenItems[i]);
		if (tr) tr.style.display = displayStyle;
	}
}
function changeEditType(){
	//枚举值内容
	if (addForm.editType.value == "1" || addForm.editType.value == "7") {
		show_hide(["trEnum"], "");
	}
	else {
		show_hide(["trEnum"], "none");
	}
	//分类类型
	if (addForm.editType.value == "6") {
		show_hide(["trCat"], "");
	}
	else {
		show_hide(["trCat"], "none");
	}
	//是否多选
	if (addForm.editType.value == "6" || addForm.editType.value == "29" || addForm.editType.value == "30") {
		show_hide(["trMulti"], "");
	}
	else {
		show_hide(["trMulti"], "none");
	}
}

function addInfo()
{
		addForm.columnName.value = trim(addForm.columnName.value);
		addForm.options.value = trim(addForm.options.value);
		if(addForm.columnName.value == '')
		{
			alert("请输入属性名称.");
			return;
		}
		if(document.getElementById("trEnum").style.display == ""
				&& addForm.options.value == '')
		{
			alert("请输入属性内容.");
			return;
		}
		var display = (addForm.display.checked) ? 1 : 0;
		var isnull = (addForm.nullable.checked)? 1 : 0;
	
		form1.action += '?action=add';
		form1.info_display.value = display;
		form1.info_isnull.value = isnull;
		form1.columnName.value = addForm.columnName.value;
		form1.editType.value = addForm.editType.value;
		form1.columnCode.value = addForm.columnCode.value;
		form1.defaultValue.value = addForm.defaultValue.value;
		
		if(addForm.editType.value == "6") {
			form1.options.value = addForm.cats.value;
			if(document.getElementById("catMulti").checked)
				form1.options.value += ";1";
			else
				form1.options.value += ";0";
		} else if(addForm.editType.value == "29" || addForm.editType.value == "30") {
			form1.options.value = "";
			if(document.getElementById("catMulti").checked)
				form1.options.value += ";1";
			else
				form1.options.value += ";0";
		}
		else
			form1.options.value = addForm.options.value;
		//alert(form1.options.value);
		form1.submit();
}

function saveInfo(tr)
{
	var fieldID = tr.firstChild.innerText;
	var display = (tr.children[5].firstChild.checked) ? 1 : 0;
	var isnull = (tr.children[6].firstChild.checked)? 1 : 0;
    var infoStyle = tr.children[4].firstChild.value;
    var infoOptions = tr.children[3].firstChild.value;

	form1.action += '?action=save';
	form1.fieldID.value = fieldID;
	form1.info_display.value = display;
	form1.info_isnull.value = isnull;
	form1.infoStyle.value = infoStyle;
	form1.options.value = infoOptions;
	form1.submit();
}

function deleteInfo(fieldID, editType)
{
	if (!window.confirm('确定要删除吗？'))
		return false;

	form1.action += "?action=delete";
	form1.fieldID.value = fieldID;
	form1.editType.value = editType;
	form1.submit();
}

function changeType(typeID){
	location.href = 'DocTypeFieldsInfo.do?docTypeID='+typeID;
}

</script>
</head>
<body>
<div id="titleDiv">
	属性自定义：  
	<select name="docTypeID" onchange="changeType(this.value)">
		<c:forEach var="docType" items="${docTypes}">
			<option value="<c:out value="${docType.docTypeID}"/>"
			 <c:if test="${docTypeID==docType.docTypeID}">selected</c:if>>
			<c:out value="${docType.docTypeName}"/>
			</option>
		</c:forEach>
	</select>

<span>
	<button class="button" onclick="showAddDiv();return false;">增加属性</button>
</span>
</div>

<div class="box" id="addDiv" style="display:none;">
<form id="addForm" name="addForm">
	<table id="CreateFieldTB" style="border-collapse:collapse;" border="1px" cellpadding="5" cellspacing="0" bordercolor="#CCCCCC" width="50%">
	<tr>
		<td colspan="2" align="center" class="bluetd2">输入属性信息</td>
	</tr>
	<tr>
		<th align="right" width="100px">属性名称</th>
		<td><input type="text" id="columnName" name="columnName" class="small"></td>
	</tr>
	<tr>
		<th align="right" width="100px">字段名称</th>
		<td><input type="text" id="columnCode" name="columnCode" class="small"> <label class="blueLabel">若不填写则自动生成</label></td>
	</tr>
	<tr>
		<th align="right">填写方式</th>
		<td>
			<select name="editType" id="editType" onchange="changeEditType()" class="small">
			<c:forEach var="fType" items="${fieldTypes}">
				<option value="<c:out value="${fType.typeNo}"/>"><c:out value="${fType.typeName}"/></option>
			</c:forEach>
			</select>
		</td>
	</tr>
	<tr id="trEnum" style="display:none;">
		<th align="right" valign="top">属性内容</th>
		<td>
			<textarea name="options" id="options" class="textarea" style="width:400px"></textarea><br/>
			<label class="blueLabel">多个值之间用分号（;）分隔，例如：乒乓球;羽毛球;台球。注意不要写成全角的分号。</label>
		</td>
	</tr>
	<tr>
		<th align="right">默认值</th>
		<td><input type="text" name="defaultValue" id="defaultValue" class="small"></td>
	</tr>
	<tr>
		<th align="right">是否显示</th>
		<td><input type="checkbox" checked="checked" name="display" id="display" class="checkbox"></td>
	</tr>
	<tr>
		<th align="right">是否可空</th>
		<td><input type="checkbox" checked="checked" name="nullable" id="nullable" class="checkbox"></td>
	</tr>
	<tr id="trCat" style="display:none;">
		<th align="right">对应分类</th>
		<td>
			
			<label class="blueLabel">
			<select name="cats">
				<c:forEach var="cat" items="${catTypes}">
					<option value="<c:out value="${cat.catType}"/>"><c:out value="${cat.name}"/></option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr id="trMulti" style="display:none;">
		<th align="right">是否多选</th>
		<td><input type="checkbox" name="catMulti" id="catMulti" class="checkbox" value="1"></td>
	</tr>
	<tr id="trEnum">
		<th align="right" valign="top">属性样式</th>
		<td>
			<input type="text" name="infoStyle" value="" style="width:400px"/><br>
			<label class="blueLabel">可直接输入css样式，比如:width:200px;height:300px;background-color:red ...</label>
		</td>
	</tr>
	<tr>
		<td align="center" colspan="2">
			<input class="button" id="add" onclick="addInfo()" type="button" value="创建">
			<input class="button" id="cancel" onclick="hideAddDiv()" type="reset" value="取消">
		</td>
	</tr>
	</table>
</form>
</div>

<form name="form1" method="post" action="../e5dom/DocTypeFieldsInfo.do">
	<input type="hidden" name="docTypeID" value="<c:out value="${docTypeID}"/>">
	<input type="hidden" name="fieldID" >
	<input type="hidden" name="info_display" >
	<input type="hidden" name="info_isnull" >
	<input type="hidden" name="columnName" >
	<input type="hidden" name="columnCode" >
	<input type="hidden" name="dataType" >
	<input type="hidden" name="dataLength" >
	<input type="hidden" name="editType" >
	<input type="hidden" name="options" >
	<input type="hidden" name="infoStyle" >
	<input type="hidden" name="defaultValue" >
</form>
<table cellpadding ="4" cellspacing="0" border="0" width="100%">
<tr bgcolor="#FFCC00">
	<td style="display:none" >ID</td>
	<td class="bluetd">字段名称</td>
	<td class="bluetd">填写方式</td>
	<td class="bluetd">属性内容</td>
	<td class="bluetd">属性样式</td>
	<td class="bluetd">是否显示</td>
	<td class="bluetd">是否可空</td>
	<td class="bluetd">级别</td>
	<td class="bluetd">保存</td>
	<td class="bluetd">删除</td>
</tr>
<c:forEach var="item" items="${infos}">
	<tr onmouseover="this.bgColor='#E4E8EB';" onmouseout="this.bgColor='#ffffff';">
		<td style="display:none"><c:out value="${item.fieldID}"/></td>
		<td class="bottomlinetd"><c:out value="${item.columnName}"/></td>
		<td class="bottomlinetd"><c:out value="${item.typeName}"/></td>
		<td class="bottomlinetd">
			<input type="text" value="<c:out value="${item.options}"/>" 
				<c:if test="${item.editType == 6 or item.editType == 29 or item.editType == 30}">disabled</c:if>>
			&nbsp;
		</td>
		<td class="bottomlinetd">
			<input type="text" name="infoStyle" value="<c:out value="${item.infoStyle}"/>">
		</td>
		<td class="bottomlinetd">
			<input type="checkbox" class="checkbox"
			<c:if test="${item.info_display == 1}">checked</c:if>
			/>
		</td>
		<td class="bottomlinetd">
			<input type="checkbox" class="checkbox"
			<c:if test="${item.info_isnull == 1}">checked</c:if>
			/>
		</td>
		<td class="bottomlinetd">
			<c:if test="${item.attribute == 2}">应用级</c:if>
			<c:if test="${item.attribute == 3}">用户级</c:if>
		</td>
		<td class="bottomlinetd">
			<input class="button" onclick="saveInfo(this.parentElement.parentElement)" type="button" name="update" value="保存"/>
		</td>
		<td class="bottomlinetd">
			<c:if test="${item.attribute == 3}">
				<input class="button" onclick="deleteInfo(<c:out value="${item.fieldID}"/>, '<c:out value="${item.editType}"/>')" type="button" name="delete" value="删除"/>
			</c:if>
			&nbsp;
		</td>
	</tr>
</c:forEach>
</table>
</body>
</html>