var fieldMenu = new WebFXMenu();
fieldMenu.width = 150;
fieldMenu.add(new WebFXMenuItem(i18nInfo.modifyField,
		"page.operation.modifyField()", i18nInfo.modifyField));
fieldMenu.add(new WebFXMenuItem(i18nInfo.deleteField,
		"page.operation.deleteField()", i18nInfo.deleteField));
fieldMenu.add(new WebFXMenuSeparator());
fieldMenu.add(new WebFXMenuItem(i18nInfo.orderByID,
		"page.operation.orderByField('fieldID','')", i18nInfo.orderByID));
fieldMenu.add(new WebFXMenuItem(i18nInfo.orderByIDDesc,
		"page.operation.orderByField('fieldID','desc')",
		i18nInfo.orderByIDDesc));
fieldMenu.add(new WebFXMenuSeparator());
fieldMenu.add(new WebFXMenuItem(i18nInfo.orderByColumnCode,
		"page.operation.orderByField('columnCode','')",
		i18nInfo.orderByColumnCode));
fieldMenu.add(new WebFXMenuItem(i18nInfo.orderByColumnName,
		"page.operation.orderByField('columnName','')",
		i18nInfo.orderByColumnName));
fieldMenu
		.add(new WebFXMenuItem(i18nInfo.orderByDataType,
				"page.operation.orderByField('dataType','')",
				i18nInfo.orderByDataType));
fieldMenu.add(new WebFXMenuItem(i18nInfo.orderByAttribute,
		"page.operation.orderByField('attribute','')",
		i18nInfo.orderByAttribute));
fieldMenu.generate();

var CreateFieldDialog;
var CreateDocTypeDialog;
var CreateDocTypeCustomDialog;
var CreateDocTypeSimpleDialog;
var optionsDialog;
var page = {

	loaded : function() {
		// initial DocType list
		var iniFlag = page.initial.initialDocTypeList();
		page.initial.assignHandlers();
		$("#prompt").html(i18nInfo.docTypeLoaded);
		page.initial.initialDialog();
		page.operation.getEditTypes("");
		
		if (!iniFlag)
			return false;
		
		// choose one DocType
		page.operation.chooseDocType();
	}

}

page.initial = {
	initialDialog : function() {
		optionsDialog = e5.dialog("", {
			title : i18nInfo.EnumTitle,
			id : "optionsDialog",
			width : 350,
			height : 150,
			resizable : true,
			showClose : true,
			ishide : true
		});
		$("#optionsDiv").show();
		optionsDialog.DOM.content.append($("#optionsDiv"));
		
		CreateFieldDialog = e5.dialog("", {
			title : i18nInfo.createDocTypeField,
			id : "CreateFieldFormDIV",
			width : 450,
			height : 450,
			resizable : true,
			showClose : true,
			ishide : true,
			fixed:true
		});
		CreateFieldDialog.DOM.content.append($("#SubCreateFieldFormDIV"));
		
		CreateDocTypeDialog = e5.dialog("", {
			title : i18nInfo.createType,
			id : "CreateDocTypeDialog",
			width : 350,
			height : 200,
			resizable : true,
			showClose : true,
			ishide : true
		});
		CreateDocTypeDialog.DOM.content.append($("#DocTypeForm"));
		
		CreateDocTypeCustomDialog = e5.dialog("", {
			title : i18nInfo.createTypeCustom,
			id : "CreateDocTypeCustomDialog",
			width : 1000,
			height : 570,
			resizable : true,
			showClose : true,
			ishide : true
		});
		CreateDocTypeCustomDialog.DOM.content.append($("#DocTypeCustomForm"));
		
		CreateDocTypeSimpleDialog = e5.dialog("", {
			title : i18nInfo.createTypeSimple,
			id : "CreateDocTypeSimpleDialog",
			width : 1000,
			height : 500,
			resizable : true,
			showClose : true,
			ishide : true
		});
		CreateDocTypeSimpleDialog.DOM.content.append($("#DocTypeSimpleForm"));
		
	},

	initialDocTypeList : function() {
		e5dom.loadDocTypes();
		if (e5dom.docTypes == null || e5dom.docTypes.length == 0)
			return false;
		for ( var i = 0; i < e5dom.docTypes.length; i++) {
			var op = document.createElement("option");
			op.value = e5dom.docTypes[i].docTypeID;
			op.text = e5dom.docTypes[i].docTypeName;
			document.getElementById("DocTypeList").options.add(op);
		}
		return true;
	},

	assignHandlers : function() {
		$("#DocTypeList").change(page.operation.chooseDocType);
		// $("#createDocTypeRelation").mouseover(page.handlers.mouseOverStyle);
		// $("#createDocType").mouseover(page.handlers.mouseOverStyle);
		// $("#createDocTypeField").mouseover(page.handlers.mouseOverStyle);
		// $("#alterDocLib").mouseover(page.handlers.mouseOverStyle);
		// $("#DeleteFieldsMgr").mouseover(page.handlers.mouseOverStyle);
		// $("#exportDocType").mouseover(page.handlers.mouseOverStyle);

		// $("#createDocTypeRelation").mouseout(page.handlers.mouseOutStyle);
		// $("#createDocType").mouseout(page.handlers.mouseOutStyle);
		// $("#createDocTypeField").mouseout(page.handlers.mouseOutStyle);
		// $("#alterDocLib").mouseout(page.handlers.mouseOutStyle);
		// $("#DeleteFieldsMgr").mouseout(page.handlers.mouseOutStyle);
		// $("#exportDocType").mouseout(page.handlers.mouseOutStyle);

		$("#createDocTypeRelation").click(page.handlers.clickCreateDocTypeRelation);
		$("#createDocType").click(page.handlers.clickCreateDocType);
		$("#createDocTypeCustom").click(page.handlers.clickCreateDocTypeCustom);
		$("#createDocTypeSimple").click(page.handlers.clickCreateDocTypeSimple);
		$("#createDocTypeField").click(page.handlers.clickCreateDocTypeField);
		$("#alterDocLib").click(page.handlers.ShowAlterDocLibPage);
		$("#DeleteFieldsMgr").click(page.handlers.DeleteFieldsMgrPage);
		$("#exportDocType").click(page.handlers.exportDocType);

		$("#CreateFieldBtn").click(page.handlers.clickCreateField);
		$("#CancelCreateFieldBtn").click(page.handlers.cancelCreateField);
		$("#CancelDocTypeBtn").click(page.handlers.cancelDocTypeBtn);
		$("#CancelDocTypeCustomBtn").click(page.handlers.cancelDocTypeCustomBtn);
		$("#CancelDocTypeSimpleBtn").click(page.handlers.cancelDocTypeSimpleBtn);
		$("#nullable").change(page.operation.nullableClick);
		$("#cancelOptionsBtn").click(page.handlers.cancelOptions);
	}
}

page.handlers = {
	exportDocType : function() {
		$("#exportFrame").attr(
				"src",
				"./exportDocType.do?docTypeID="
						+ e5dom.currentDocType.docTypeID);
	},

	ShowAlterDocLibPage : function(e) {
		var docTypeId = parseInt(e5dom.currentDocType.docTypeID);
		e5.dialog( {
			type : 'iframe',
			value : "ShowDocLibsToBeUpdate.jsp?docTypeID=" + docTypeId
		}, {
			id : "ShowAlterDocLibPage",
			title : i18nInfo.AlterDocLib,
			width : 450,
			height : 350,
			resizable : true,
			showClose : true,
			ishide : false
		}).show();
		e.preventDefault();
		e.stopPropagation();
	},
	DeleteFieldsMgrPage : function(e) {
		var docTypeId = parseInt(e5dom.currentDocType.docTypeID);
		e5.dialog( {
			type : 'iframe',
			value : "DocTypeFieldRestore.do?docTypeID=" + docTypeId
		}, {
			id : "DeleteFieldsMgrPage",
			title : i18nInfo.RestoreTitle,
			width : 760,
			height : 350,
			resizable : true,
			showClose : true,
			ishide : false
		}).show();
		e.preventDefault();
		e.stopPropagation();
	},
	clickCreateField : function() {
		$("#docTypeID").val(e5dom.currentDocType.docTypeID);
		var dt = $("#dataType").val();
		if (dt == "CHAR" || dt == "VARCHAR" || dt == "EXTFILE") {
			$("#dataLength").attr("class",
					"validate[required,custom[integer]] small");
		} else {
			$("#dataLength").attr("class", "validate[custom[integer]] small");
		}
		var isNew = $("#isNew").val();
		if (isNew == "1") {
			var fieldExisted = page.operation.fieldExisted(
					e5dom.currentDocType.docTypeID, $("#columnCode").val());
			if (!fieldExisted) {
				return true;
			} else {
				alert(i18nInfo.FieldExisted);
				return false;
			}
		} else {
			return true;
		}
	},

	cancelCreateField : function() {// 关闭创建文档类型字段模态窗口
		CreateFieldDialog.hide();
		$("#CreateFieldForm").validationEngine("hideAll");
	},
	cancelDocTypeBtn : function() {// 关闭创建文档类型模态窗口
		CreateDocTypeDialog.hide();
		$("#DocTypeForm").validationEngine("hideAll");
	},
	
	cancelDocTypeCustomBtn : function() {// 关闭创建文档类型模态窗口
		CreateDocTypeCustomDialog.hide();
		$("#DocTypeCustomForm").validationEngine("hideAll");
	},
	cancelDocTypeSimpleBtn : function() {// 关闭创建简单文档类型模态窗口
		CreateDocTypeSimpleDialog.hide();
		$("#DocTypeSimpleForm").validationEngine("hideAll");
	},
	mouseOutStyle : function(evt) {
		var el = e5dom.util.srcEventElement(evt);
		el.style.textDecoration = "none";
		el.style.cursor = "default";
	},
	mouseOverStyle : function(evt) {
		var el = e5dom.util.srcEventElement(evt);
		el.style.textDecoration = "underline";
		el.style.cursor = "pointer";
	},

	clickCreateDocTypeRelation : function(evt) {
		var str = "<form name=\"DocTypeRelationForm\" id=\"DocTypeRelationForm\" method=\"get\"  onsubmit=\"return false;\">";
		str += "<table cellpadding='0' cellspacing='0' class='table'>";
		//str += "<caption>"+ i18nInfo.DocTypeRelation + "</caption>";

		for ( var i = 0; i < e5dom.docTypes.length; i++) {
			var type = e5dom.docTypes[i]
			if (type.docTypeID != e5dom.currentDocType.docTypeID)
				str += "<tr><td width='10%'><input id=\"check" + type.docTypeID
						+ "\" type=\"checkbox\" name=\"types\" value=\" "
						+ type.docTypeID + "\"></td><td width='90%'><label for=\"check"
						+ type.docTypeID + "\">" + type.docTypeName
						+ "</label></td></tr>";
		}

		str += "<tr><td align=\"center\" colspan='2'><input class=\"button\" onclick=\"page.operation.updateDocTypeRelation();\" type=\"submit\" value=\""
				+ i18nInfo.createTypeRelation
				+ "\">"
				+ "&nbsp;<input class=\"button\" onclick=\"e5.dialog.close('CreateDocTypeRelationDialog');\" type=\"button\" value=\""
				+ i18nInfo.cancel + "\"></td></tr></table></form>";

		var CreateDocTypeRelationDialog = e5.dialog("", {
			title : i18nInfo.createDocTypeRelation,
			id : "CreateDocTypeRelationDialog",
			width : 400,
			height : 500,
			resizable : true,
			showClose : true,
			ishide : true
		});
		try {
			CreateDocTypeRelationDialog.DOM.content.empty();
			CreateDocTypeRelationDialog.DOM.content.append(str);
			CreateDocTypeRelationDialog.show();
		} catch (e) {
		}
		evt.preventDefault();
		evt.stopPropagation();
	},

	clickCreateDocType : function(evt) {
		page.handlers.getAllApp("appID");
		CreateDocTypeDialog.show();
		$("#newDocTypeName").val("");
		evt.preventDefault();
		evt.stopPropagation();
	},
	clickCreateDocTypeCustom: function(evt) {
		page.handlers.getAllApp("appIDCustom");
		CreateDocTypeCustomDialog.show();
		$("#newDocTypeNameCustom").val("");
		page.operation.getSysFields();
		evt.preventDefault();
		evt.stopPropagation();
	},
	clickCreateDocTypeSimple: function(evt) {
		page.handlers.getAllApp("appIDSimple");
		CreateDocTypeSimpleDialog.show();
		$("#newDocTypeNameSimple").val("");
		page.operation.getSysSimpleFields();
		evt.preventDefault();
		evt.stopPropagation();
	},
	getAllApp : function(appid) {
		$.ajax( {
			url : "DocTypeController.do?invoke=getApps",
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != "" && data != "-1") {
					$("#"+appid+"").html("");
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					for ( var i = 0; i < datas.length; i++) {
						var op = document.createElement("option");
						op.value = datas[i].appID;
						op.text = datas[i].name;
						document.getElementById(appid).options.add(op);
					}
				}
			}
		});
	},
	addApp : function(appID) {
		e5.dialog( {
			type : 'iframe',
			value : "../e5app/AddApp.jsp?appID=" + appID
		}, {
			id : "AddApp",
			title : i18nInfo.apptitle,
			width : 500,
			height : 230,
			resizable : true,
			showClose : true,
			ishide : false
		}).show();
	},
	clickCreateDocTypeField : function(e) {
		$("#docTypeID").val(e5dom.currentDocType.docTypeID);
		$("#isNew").val("1");
		$("#fieldID").val("");
		$("#CreateFieldBtn").val(i18nInfo.createButton);

		$("#columnName").val("");
		$("#columnCode").val("");
		$("#dataLength").val("");
		$("#scale").val("");
		changeEditType();
		document.getElementById("nullable").checked = true;
		document.getElementById("readonly").checked = false;
		page.operation.setControls(false);
		CreateFieldDialog.show();
		page.operation.getEditTypes("");
		$("#scale").attr("disabled", true);
		e.preventDefault();
		e.stopPropagation();
	},

	overOneTR : function(trE) {
		trE.style.cssText = "cursor:pointer;background-color:#CCCCFF";
	},

	outOneTR : function(trE) {
		trE.style.cssText = "cursor:default;background-color:white";
	},
	//增加枚举值
	addOptions : function(){
		optionsDialog.show();
		optionsDialog.zIndex();
	},
	//删除枚举值
	delOptions : function(){
		$("#options option:selected").remove();   
	},

	clickAddOptions : function(){
		var optionValue = $("#optionValue").val();
		if(optionValue.indexOf("=") > 0 || optionValue.indexOf(",") > 0){
			alert("枚举值不允许输入等号和逗号！");
			return;
		}
		var optionName = $("#optionName").val();
		if(optionName.indexOf("=") > 0 || optionName.indexOf(",") > 0){
			alert("显示名称不允许输入等号和逗号！");
			return;
		}else if(optionName == ''){
			optionName = optionValue;
		}
		var dataType = $("#dataType").val();
		if(dataType=="FLOAT"||dataType=="DOUBLE"){
			if(optionValue.indexOf(".")==-1){
				optionValue = optionValue+".0";
			}
		}
		$("#options").append("<option value='"+optionValue + "=" + optionName +"'>"+optionValue + "=" + optionName+"</option>");
		$("#optionValue").val(""); 
		$("#optionValue").focus();
		$("#optionName").val("");
	},

	cancelOptions : function(){
		optionsDialog.hide();
		$("#optionsForm").validationEngine("hideAll");
	}
	
}

page.operation = {
	setControls : function(flag) {
		$("#columnCode").attr("disabled", flag);
		$("#dataType").attr("disabled", flag);
		$("#dataLength").attr("disabled", flag);
		$("#scale").attr("disabled", flag);
		$("#defaultValue").attr("disabled", flag);
		$("#nullable").attr("disabled", flag);
		$("#readonly").attr("disabled", flag);
	},
	CreateDocType : function() {
		var typeName = $("#newDocTypeName").val();
		if (typeName == null || typeName == "")
			return false;
		var appID = $("#appID").val();
		$.ajax( {
			url : "DocTypeController.do?invoke=createDocType&docTypeName="
					+ encodeURI(typeName) + "&appID=" + appID,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != null) {
					if (data == -1)
						alert(i18nInfo.createDocTypeNo);
					else {
						alert(i18nInfo.createDocTypeOK);
						page.initial.initialDocTypeList();
					}
				}
			}
		});
	},
	orderByField : function(fieldName, order) {
		$("#testTbl2").html("");
		$.ajax( {
			url : "DocTypeController.do?invoke=getFieldsByField&typeID="
					+ e5dom.currentDocType.docTypeID + "&fieldName="
					+ encodeURI(fieldName) + "&order=" + order,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != "-1") {
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					if(datas!=null){
						$(".fieldsRemove").remove();
                		$.each( datas, function(i, d){
                			var trHtml = page.operation.getTableHtml(d);
                			$("#testTbl2").append(trHtml);
                		});
                		// $("#testTbl2").append("<tr class='fieldsRemove'><td class=\"bluetd\" colspan=\"10\">&nbsp;</td></tr>");
                	}
				}
			}
		});
	},
	
	getSysFields : function(){
		$(".fieldsRemove").remove();
		$.ajax({
			url : "DocTypeController.do?invoke=getSysFields&typeID=" + e5dom.currentDocType.docTypeID,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != "-1") {
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					if(datas!=null&&datas.length>0){
						$.each(datas,function(i, field){
							var text = field.dataType;
							if(field.dataLength>0&&field.scale>0&&text!="EXTFILE"){
								text += "("+field.dataLength+","+field.scale+")";
							}else if(field.dataLength>0&&text!="EXTFILE"){
								text += "("+field.dataLength+")";
							}
							var trHtml = "";
							trHtml += "<tr class='fieldsRemove' onmouseover=\"this.bgColor='#E4E8EB';\" onmouseout=\"this.bgColor='#ffffff';\">";
							trHtml += "<td><input type=\"checkbox\" name=\"sysFields\" value=\""+field.columnCode+"\" checked ";
							if(i < 4)
								trHtml += "onclick='return false' style='background-color:#E4E8EB'";
							
							trHtml += "/></td>";
							//trHtml += "<td>"+field.fieldID+"</td>";
							trHtml += "<td>"+field.columnName+"</td>";
							trHtml += "<td>"+field.columnCode+"</td>";
							trHtml += "<td>"+text+"</td>";
							trHtml += "<td>"+field.defaultValue+"</td>";
							trHtml += "<td>";
							if(field.isNull==0){
								trHtml += "N";
							}
							trHtml += "</td></tr>";
							$("#sysFieldsTb").append(trHtml);
						});
					}
				}
			}
		});
	},
	
	getSysSimpleFields : function(){
		$(".fieldsRemove").remove();
		$.ajax({
			url : "DocTypeController.do?invoke=getSysFields",
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != "-1") {
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					if(datas!=null&&datas.length>0){
						$.each(datas,function(i, field){
							var text = field.dataType;
							if(field.dataLength>0&&field.scale>0&&text!="EXTFILE"){
								text += "("+field.dataLength+","+field.scale+")";
							}else if(field.dataLength>0&&text!="EXTFILE"){
								text += "("+field.dataLength+")";
							}
							var trHtml = "";
							if(i < 4){
								trHtml += "<tr class='fieldsRemove' onmouseover=\"this.bgColor='#E4E8EB';\" onmouseout=\"this.bgColor='#ffffff';\">";
								trHtml += "<td>"+field.columnName+"</td>";
								trHtml += "<td><input type=\"hidden\" name=\"sysSimpleFields\" value=\""+field.columnCode+"\"/>"+field.columnCode+"</td>";
								trHtml += "<td>"+text+"</td>";
								trHtml += "<td>"+field.defaultValue+"</td>";
								trHtml += "<td>";
								if(field.isNull==0){
									trHtml += "N";
								}
								trHtml += "</td></tr>";
							}
							
							$("#sysSimpleFieldsTb").append(trHtml);
						});
					}
				}
			}
		});
	},

	showContextMenu : function(fieldid, fieldName, fieldAttr, el) {
		fieldMenu.fieldid = fieldid;
		fieldMenu.fieldName = fieldName;
		fieldMenu.fieldAttr = fieldAttr;
		webFXMenuHandler.showMenu(fieldMenu, el);
	},

	chooseDocType : function() {
		if (docTypeChosen != "") {
			$("#DocTypeList").val(docTypeChosen);
			docTypeChosen = "";
		}
		var docTypeID = $("#DocTypeList").val();
		var docType = e5dom.getDocType(docTypeID);
		e5dom.currentDocType = docType;
		// show DocType Properties
		page.operation.showDocTypeProps();

		// show DocTypeFields
		page.operation.showDocTypeFields();

		$("#prompt").html(i18nInfo.docTypeSelected);
	},

	showDocTypeProps : function() {
		// alert(e5dom.currentDocType);
		$("#TypeDesc").html(e5dom.currentDocType.descInfo);
		$("#TypeApp").html(e5dom.currentDocType.appID);
		$("#TypeFlow").html(e5dom.currentDocType.defaultFlow);
		$("#TypeRelated").html(e5dom.currentDocType.docTypeRelated);
		$("#TypeName").html(e5dom.currentDocType.docTypeName);
		$("#TypeID").html(e5dom.currentDocType.docTypeID);

	},

	showDocTypeFields : function() {
		$("#testTbl2").empty();
		var docTypeID = e5dom.currentDocType.docTypeID;
		// 获取文档类型对应的文档字段信息
		$.ajax({
			url : "DocTypeController.do?invoke=getDocTypeFields&docTypeID="
					+ docTypeID,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != "-1") {
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					$("#FieldCount").html(
							i18nInfo.FieldCount + " " + datas.length);
					if(datas!=null){
						var html = "";
						$(".fieldsRemove").remove();
                		$.each( datas, function(i, d){
                			var trHtml = page.operation.getTableHtml(d);
                			html += trHtml + "\n";
                			$("#testTbl2").append(trHtml);
                		});
                		// $("#testTbl2").append("<tr class='fieldsRemove'><td class=\"bluetd\" colspan=\"10\">&nbsp;</td></tr>");
                	}
				}
			}
		});
	},

	updateDocTypeRelation : function() {
		var types = document.getElementsByName("types");
		var typeArray = new Array();
		for ( var i = 0; i < types.length; i++) {
			if (types[i].checked) {
				typeArray.push(types[i].value);
			}
		}
		$.ajax({
			url : "DocTypeController.do?invoke=updateDocTypeRelation&docTypeID="
					+ e5dom.currentDocType.docTypeID
					+ "&types="
					+ typeArray,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != null && data == "1") {
					var ids = "";
					for ( var i = 0; i < typeArray.length; i++) {
						ids += typeArray[i] + ",";
					}
					ids = ids.substring(0, ids.length - 1);
					e5dom.currentDocType.docTypeRelated = ids;
					$("#TypeRelated")
							.html(
									"<strong>"
											+ e5dom.currentDocType.docTypeRelated
											+ "</strong>");
					e5.dialog.close("CreateDocTypeRelationDialog");
					$("#prompt").html(i18nInfo.docTypeRelationCreated);
				}
			}
		});
	},

	modifyField : function() {
		var fieldid = fieldMenu.fieldid;
		$.ajax({
			url : "DocTypeController.do?invoke=getDocTypeField&fieldID="
					+ fieldid,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != "-1") {
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					if (datas != null && datas.length > 0) {
						var field = datas[0];
						$("#docTypeID").val(e5dom.currentDocType.docTypeID);
						$("#isNew").val("0");
						$("#fieldID").val(fieldid);
						$("#columnName").val(field.columnName);
						$("#columnCode").val(field.columnCode);
						$("#dataType").val(field.dataType);
						$("#dataLength").val(field.dataLength);
						$("#scale").val(field.scale);
						$("#defaultValue").val(field.defaultValue);
						$("#URL").val(field.url);
//						$("#options").val(field.options);
						page.operation.setOptions(field.options);
						//单选或多选时显示
						page.operation.isShowByEditType(field.editType, field.options);
						
						
						$("#CreateFieldBtn").val(i18nInfo.updateButton);
						if (field.isNull == 1){
							$("#nullable").attr("checked", "checked");
						}else{
							$("#nullable").removeAttr("checked");
						}
						if (field.readonly == 1){
							$("#readonly").attr("checked", "checked");
						}else{
							$("#readonly").removeAttr("checked");
						}
						page.operation.setControls(true);
						CreateFieldDialog.show();
						page.operation.getEditTypes(field.editType, 'modify');
					}
				}
			}
		});
	},

	setOptions : function(options){
		$("#options").html("");
		var optionArr = new Array;
		if(options!=null&&options.length>0){
			if(options.indexOf(",")){
				optionArr = options.split(",");
			}else{
				optionArr.push(options);
			}
			if(optionArr!=null&&optionArr.length>0){
				for(var i=0;i<optionArr.length;i++){
					$("#options").append("<option value='"+optionArr[i]+"'>"+optionArr[i]+"</option>");
				}
			}
		}
	},
	deleteField : function() {
		var fieldid = fieldMenu.fieldid;
		var fieldName = fieldMenu.fieldName;
		var fieldAttr = fieldMenu.fieldAttr;
		if (fieldAttr == 1) {
			alert(i18nInfo.deleteSysField);
			return;
		}
		if (fieldAttr == 2) {
			alert(i18nInfo.deleteAppField);
			return;
		}
		if (confirm(i18nInfo.confirmDeleteField + " " + fieldName + " "
				+ i18nInfo.confirmDeleteFieldEnd)) {
			$.ajax({
				url : "DocTypeController.do?invoke=deleteDocTypeField&fieldID="
						+ fieldid,
				dataType : "json",
				async : false,
				success : function(data) {
					if (data != null) {
						if (data == 1) {
							alert(i18nInfo.deleteFieldOK);
							page.operation.showDocTypeFields();
						} else {
							alert(i18nInfo.deleteFieldNo);
						}
					}
				}
			});
		}
	},

	fieldExisted : function(docTypeId, columnCode) {
		var ret = true;
		$.ajax( {
			url : "DocTypeController.do?invoke=getField&docTypeId=" + docTypeId
					+ "&columnCode=" + columnCode,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data == "-1") {
					ret = false;
				}
			}
		});
		return ret;
	},
	getTableHtml : function(field){
		var text = field.dataType;
		if(field.dataLength>0&&field.scale>0&&text!="EXTFILE"){
			text += "("+field.dataLength+","+field.scale+")";
		}else if(field.dataLength>0&&text!="EXTFILE"){
			text += "("+field.dataLength+")";
		}
		var html="";
		html += "<tr title=\""+i18nInfo.RightClick+"\">";
		html += "<td oncontextmenu=\"page.operation.showContextMenu("+field.fieldID+",\'"+field.columnName+"\',"+field.attribute+",this);return false;\">"+field.fieldID+"</td>";
		html += "<td oncontextmenu=\"page.operation.showContextMenu("+field.fieldID+",\'"+field.columnName+"\',"+field.attribute+",this);return false;\">"+field.columnName+"</td>";
		html += "<td oncontextmenu=\"page.operation.showContextMenu("+field.fieldID+",\'"+field.columnName+"\',"+field.attribute+",this);return false;\">"+field.columnCode+"</td>";
		html += "<td oncontextmenu=\"page.operation.showContextMenu("+field.fieldID+",\'"+field.columnName+"\',"+field.attribute+",this);return false;\">"+text+"</td>";
		//填写方式借用DocTypeField中废弃的字段：beanName
		html += "<td oncontextmenu=\"page.operation.showContextMenu("+field.fieldID+",\'"+field.columnName+"\',"+field.attribute+",this);return false;\">"+field.beanName+"</td>";
		html += "<td oncontextmenu=\"page.operation.showContextMenu("+field.fieldID+",\'"+field.columnName+"\',"+field.attribute+",this);return false;\">"+field.defaultValue+"</td>";
		html += "<td oncontextmenu=\"page.operation.showContextMenu("+field.fieldID+",\'"+field.columnName+"\',"+field.attribute+",this);return false;\">";
		if(field.isNull==0){
			html += "N";
		}
		html += "</td>";
		html += "<td oncontextmenu=\"page.operation.showContextMenu("+field.fieldID+",\'"+field.columnName+"\',"+field.attribute+",this);return false;\">";
		//1—系统平台字段 2—应用系统字段 3－用户扩展字段
		if(field.attribute==2){
			html += i18nInfo.Application;
		}else if(field.attribute==3){
			html += i18nInfo.User;
		}
		html += "</td></tr>";
		return html;
	},
	nullableClick : function(){
		if($("#nullable").attr("checked")=="checked"){
			$("#defaultValue").attr("class","small");
        }else{
        	$("#defaultValue").attr("class","validate[required] small");
        }
	},
	getEditTypes : function(editType, modify){
		var dataType = $("#dataType").val();
		var fieldID = $("#fieldID").val();
		if(editType==undefined) editType = "";
		//地址（省市区县）、日期（年月日）、部门（部门树）、用户（用户树）、分类（分类树）、分类（分类树，可多选）、部门（部门树，可多选）、用户（用户树，可多选）：不允许修改
		if(page.operation.isContain(editType)==true){
			$("#cats").attr("disabled",true);
			$("#editType").attr("disabled",true);
		}else{
			$("#cats").attr("disabled",false);
			$("#editType").attr("disabled",false);
		}
		$("#editType").html("");
		$.ajax( {
			url : "DocTypeFieldController.do?invoke=getDocTypeFieldInfos&dataType="+dataType + 
				"&fieldID=" + fieldID + "&editType=" + editType+ "&modify=" + modify,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != "-1") {
					$("#appID").html("");
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					$.each( datas, function(i, fieldType){
						var op = document.createElement("option");
						op.value = fieldType.typeNo;
						op.text = fieldType.typeName;
						if(fieldType.typeNo==editType){
							op.selected = "selected";
						}
						document.getElementById("editType").options.add(op);
            		});
					changeEditType();
				}
			}
		});
	},
	isContain : function(val){
		if(readonlyArray!=null&&readonlyArray.length>0){
			for(var i=0; i<readonlyArray.length; i++){
				if(readonlyArray[i] == val)
					return true;
			}
		}
		return false;
	},
	
	isShowByEditType : function(editType, options){
		//1-单选（下拉框select），11	-单选（单选框radio），7-多选（下拉框select），14-多选（复选框checkbox）
		if (editType == DocTypeField.EDITTYPE_ENUM || editType == DocTypeField.EDITTYPE_SELECT_RADIO || 
				editType == DocTypeField.EDITTYPE_MULTI || editType == DocTypeField.EDITTYPE_MULTI_CHECKBOX) {
			$("#trEnum").show();
			addOptionTD();
		} else {
			$("#trEnum").hide();
		}
		//6-分类（分类树），33-分类（分类树，可多选），16-分类（下拉框select，只可用于单层分类）
		if (editType == DocTypeField.EDITTYPE_TREE || editType == DocTypeField.EDITTYPE_TREE_MULTI || 
				editType == DocTypeField.EDITTYPE_TREE_SELECT) {
			$("#trCat").show();
			if(options != null && options != undefined){
				$("#cats").val(options);
			}
		} else {
			$("#trCat").hide();
		}
		/*动态URL：5-单选（下拉框select，动态取值），12-单选（单选框radio，动态取值），13-多选（下拉框select，动态取值），
		 15-多选（复选框checkbox，动态取值），17-其它数据（下拉框，动态取值，名值对）,9-任意填写（单行，带填写提示），10-任意填写（单行，带填写提示，键值对）
		 */
		
		if (editType == DocTypeField.EDITTYPE_SELECT || editType == DocTypeField.EDITTYPE_SELECT_RADIO_DYNAMIC || 
				editType == DocTypeField.EDITTYPE_MULTI_DYNAMIC || editType == DocTypeField.EDITTYPE_MULTI_CHECKBOX_DYNAMIC || 
				editType == DocTypeField.EDITTYPE_OTHER_DATA || editType == DocTypeField.EDITTYPE_FREE_AUTOCOMPLETE ||
				editType == DocTypeField.EDITTYPE_FREE_AUTOCOMPLETE_KEYVALUE) {
			$("#trUrl").show();
		} else {
			$("#trUrl").hide();
		}
	}
}
function changeEditType() {
	var editType = $("#editType").val();
	page.operation.isShowByEditType(editType);
	if (editType == "5" || editType == "12" || editType == "13" || editType == "15" || editType == "17") {
		var url = $("#URL").val();
		if (!url && !$("#fieldID").val()) {
			url = "e5workspace/Data.do?data=1&amp;type=<--DOCTYPECODE-->&amp;field=<--relatedFieldCode-->";
			$("#URL").val(url);
		}
	} else {
		var url = $("#URL").val();
		if (url.indexOf("e5workspace/Data.do?data=1") == 0) {
			$("#URL").val("");
		}
	}
}
function dataTypeChange(){
	$("#dataLength").val("");
	page.operation.getEditTypes('');
	changeEditType();
	$("#options option").each(function(){
		$(this).remove();
	});
	var dataType = $("#dataType").val();
	if(dataType == "FLOAT"|| dataType == "DOUBLE"){
		$("#scale").removeAttr("disabled");
	}else{
		$("#scale").attr("disabled", true);
	}
	if(dataType == "BLOB" ||
			dataType == "CLOB" ||
			dataType == "DATE" ||
			dataType == "TIME" ||
			dataType == "TIMESTAMP" ||
			dataType == "EXTFILE"){
		$("#dataLength").attr("disabled", true);
	} else {
		$("#dataLength").removeAttr("disabled");
	}
	//
	if(dataType == "BLOB"|| dataType == "EXTFILE"){
		$("#trEditType").hide();
	} else {
		$("#trEditType").show();
	}
}
function removeOptions(){
	$("#options option").each(function(){
		$(this).remove();
	});
} 
function addOptionTD(){
	var dataType = $("#dataType").val();
	if(dataType=="CHAR"||dataType=="VARCHAR"){
		var option = "<input type=\"text\" id=\"optionValue\" name=\"optionValue\" class=\"validate[required,maxSize[100]] small\"/>";
		$("#optionTD").html(option);
	}else if(dataType=="INTEGER"||dataType=="LONG"){
		var option = "<input type=\"text\" id=\"optionValue\" name=\"optionValue\" class=\"validate[required,custom[integer]] small\"/>";
		$("#optionTD").html(option);
	}else if(dataType=="FLOAT"||dataType=="DOUBLE"){
		var option = "<input type=\"text\" id=\"optionValue\" name=\"optionValue\" class=\"validate[required,custom[number]] small\"/>";
		$("#optionTD").html(option);
	}else if(dataType=="DATE"||dataType=="TIMESTAMP"){
		var option = "<input type=\"text\" id=\"optionValue\" name=\"optionValue\" class=\"validate[required,custom[date]] small\" onClick=\"showCalendar('optionValue', 'y-mm-dd');\"/>";
		$("#optionTD").html(option);
	}else if(dataType=="TIME"){
		var option = "<input type=\"text\" id=\"optionValue\" name=\"optionValue\" class=\"validate[required,custom[timeFormat]] small\"/>";
		$("#optionTD").html(option);
	}
}
function setValue(idValue){
	document.getElementById(idValue).value=document.getElementById(idValue).checked;
}
window.onload = page.loaded;