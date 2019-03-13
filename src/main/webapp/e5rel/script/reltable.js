var tableMenu = new WebFXMenu();
tableMenu.width = 150;
tableMenu.add(new WebFXMenuItem(i18nInfo.modifyRelTable, "page.operation.modifyRelTable();", i18nInfo.modifyRelTable));
tableMenu.add(new WebFXMenuItem(i18nInfo.addNewField, "page.operation.addNewField();", i18nInfo.addNewField));
tableMenu.add(new WebFXMenuSeparator());
tableMenu.add(new WebFXMenuItem(i18nInfo.deleteRelTable, "page.operation.deleteRelTable();", i18nInfo.deleteRelTable));
tableMenu.generate();

var page = {	

	loaded : function(){	
		//initial DocType list
		var iniFlag = page.initial.initialDocTypeList();

		//set DocType chosen handler	
		page.initial.assignHandlers();

        if(!iniFlag)
			return false;

		page.initial.initialDSConfigList();

		//choose one DocType
		page.operation.chooseDocType();	

	},
	
	currentDSID:0,

	dSConfigs:null
}

page.initial = {
	initialDSConfigList : function() {
		$.ajax({
			url : "../e5dom/DocLibController.do?invoke=getAllE5DataSources",
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
					page.dSConfigs = datas;
					for(var i=0;i<page.dSConfigs.length;i++)
					{
						var op = document.createElement("option");
						op.value = ""+page.dSConfigs[i].dsID;
						op.text = page.dSConfigs[i].name;
						document.getElementById("DSConfigList").options.add(op);
					}
					page.currentDSID = page.dSConfigs[0].dsID;
				}
			}
		});
	},

	initialDocTypeList : function() {
		e5dom.loadDocTypes();
		if(e5dom.docTypes==null || e5dom.docTypes.length ==0)
			return false;
		for(var i=0;i<e5dom.docTypes.length;i++)
		{
			var op = document.createElement("option");
			op.value = e5dom.docTypes[i].docTypeID;
			op.text = e5dom.docTypes[i].docTypeName;
			document.getElementById("DocTypeList").options.add(op);
		}
		return true;
	},

	assignHandlers : function() {
		$("#createRelTable").click(page.handlers.ClickCreateRelTable);
		// $("#createRelTable").mouseout(page.handlers.mouseOutCreateTable);
		// $("#createRelTable").mouseover(page.handlers.mouseOverCreateTable);
		$("#FieldsBtn").click(page.handlers.ConfirmFields);
		$("#DDLBtn").click(page.handlers.ConfirmDDL);
    }
}

page.handlers = {
	submitModifyRelTable : function() {
		var tableId = parseInt(tableMenu.libID);	
		var newName = $("#newRelTableName").val();
		var url = "RelTableController.do?invoke=updateRelTable&tableId="+tableId+
				"&newName="+encodeURI(newName);
		$.ajax({
			url : url,
			dataType : "json",
			async : false,
			success : function(data) {
				if(data=="1"){
					alert(i18nInfo.ModifyTableOK);
					$("#table"+tableId).html(newName);
					$("#ModifyRelTableDiv").attr("style", "display:none");
				}else{
					alert(i18nInfo.ModifyTableNo);
				}
				$("#modifySub").removeAttr("disabled");
				$("#modifyCel").removeAttr("disabled");
			}
		});
	},
	
	resetRelTableForm : function() {
		$("#newRelTableName").val("");
		$("#errorDiv2").html("");
	},

    CreateField : function(){
		var tableId = $("#tableId").val();
		var fieldName = $("#fieldName").val();
		var E5TypeName = $("#fieldType").val();
		var length = $("#fieldLength").val();
		var nullable = $("#nullable").val();
		var url = "RelTableController.do?invoke=addField&tableId="+tableId+
				"&fieldName="+encodeURI(fieldName)+
				"&E5TypeName="+encodeURI(E5TypeName)+
				"&length="+length+
				"&nullable="+nullable;
		$.ajax({
			url : url,
			dataType : "json",
			async : false,
			success : function(data) {
				if(data=="1"){
					alert(i18nInfo.createTableOk);
	                page.operation.chooseRelTable(tableMenu.libID);
	            }else if(data=="0") {
					alert(i18nInfo.sameNameFieldExisted)
	            }else{
	                alert(i18nInfo.createTableNo);
	            }
			}
		});
    },

    ConfirmDDL : function(){
        $("#AppendDDL").attr("readOnly", "true");
        var ddl = document.getElementById("DDLBegin").innerHTML+
        	document.getElementById("AppendDDL").innerHTML+
        	document.getElementById("DDLEnd").innerHTML;
        
        var dsID = $("#dsId").val();
		var name = $("#newName").val();
		var refDocTypeID = $("#docTypeId").val();
		var tableName = $("#tableName").val();
		
		var url = "RelTableController.do?invoke=createRelTable&dsID="+dsID+
				"&name="+encodeURI(name)+
				"&tableName="+encodeURI(tableName)+
				"&ddl="+encodeURI(ddl)+
				"&refDocTypeID="+refDocTypeID;
        $.ajax({
			url : url,
			dataType : "json",
			async : false,
			success : function(data) {
	        	if(parseInt(data)>0){
					alert(i18nInfo.createTableOk);
					page.operation.chooseDocType();
	                page.operation.chooseRelTable(data);
	            }else{
	            	alert(data);
	            }
			}
		});
	},
	
	ConfirmFields : function(){
		$("#newName").val($("#Name").val());
		$("#tableName").val($("#NewTableName").val());
		$("#docTypeId").val($("#DocTypeList").val());
		$("#dsId").val(page.currentDSID);

	    var dsID = $("#dsId").val();
		var name = $("#newName").val();
		var refDocTypeID = $("#docTypeId").val();
		var tableName = $("#tableName").val();
		var extFields = document.getElementsByName("extFields");
		var extFieldsArray = new Array();
		for ( var i = 0; i < extFields.length; i++) {
			if(extFields[i].checked){
				extFieldsArray.push(extFields[i].value);
			}
		}
		var url = "RelTableController.do?invoke=genCreateDDL&dsID="+dsID+
				"&name="+encodeURI(name)+
				"&tableName="+encodeURI(tableName)+
				"&refDocTypeID="+refDocTypeID+
				"&fieldIds="+extFieldsArray;
		$.ajax({
			url : url,
			dataType : "json",
			async : false,
			success : function(data) {
				if(data!="-1"){
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					if(datas!=null&&datas.length>0){
						page.operation.showDDLDiv(datas[0]);
					}
	            }
			}
		});
	},
	
	ConfirmRelTableName  : function (){
        $("#NewTableName").attr("readOnly", "true");
		$("#Name").attr("readOnly", "true");
		
		$("#DocTypeList").attr("disabled", "true");
		$("#DSConfigList").attr("disabled", "true");
        page.operation.ChooseFields();
        $("#ChooseFieldsDIV").show();
	},

	ClickCreateRelTable : function() {
		$("#TableNameBtn").removeAttr("disabled");
		$("#RightDiv").hide();
		$("#ModifyRelTableDiv").attr("style", "display:none");
		$("#CreateRelTableDiv").show();
		$("#AddFieldDiv").hide();
		$("#newRelTableNameDIV").show();
		$("#ChooseFieldsDIV").hide();
		$("#DDLDIV").hide();
		$("#errorDiv").html("");
        $("#NewTableName").val("");
		$("#Name").val("");
		
		$("#chkAll").removeAttr("checked");
		$("#DocTypeList")[0].selectedIndex = 0;
		$("#DSConfigList")[0].selectedIndex = 0;

        $("#NewTableName").removeAttr("readOnly");
		$("#Name").removeAttr("readOnly");
		$("#DocTypeList").removeAttr("disabled");
		$("#DSConfigList").removeAttr("disabled");
    },

	mouseOutCreateTable : function(evt) {
		var el = e5dom.util.srcEventElement(evt);
		el.style.textDecoration = "none";
		el.style.cursor = "default";
	},
		
	mouseOverCreateTable : function(evt) {
		var el = e5dom.util.srcEventElement(evt);
		el.style.textDecoration = "underline";
		el.style.cursor = "pointer";
	},

	mouseOutStyle : function(el) {
		el.style.backgroundColor = 'white';
		el.style.cursor = "default";
	},
		
	mouseOverStyle : function(el) {
		el.style.backgroundColor = '#FFFF99';
		el.style.cursor = "pointer";
	},

	overOneTR : function(trE){
		trE.style.cssText = "cursor:pointer;background-color:#CCCCFF";
	},

	outOneTR : function (trE) {		
		trE.style.cssText = "cursor:default;background-color:white";	    
	}
}


page.operation = {
	tableNameExisted : function() {
		var tableSuffix = $("#NewTableName").val();
		var ret = true;
		var url = "RelTableController.do?invoke=getRelTableBySuffix&tableSuffix="+tableSuffix;
		$.ajax({
			url : url,
			dataType : "json",
			async : false,
			success : function(data) {
		    	if(data == null|| data==""){
		    		ret = false;
		        }
			}
		});
		if(ret){
			alert(i18nInfo.SameNameTableExisted);
		}
		return ret;
	},

	modifyRelTable : function(){
		$("#modifySub").removeAttr("disabled");
		$("#modifyCel").removeAttr("disabled");
		var tableId = parseInt(tableMenu.libID);	
		$.ajax({
			url : "RelTableController.do?invoke=getRelTable&tableId="+tableId,
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
						var relTable = data[0];
						$("#RightDiv").attr("style", "display:none");
						$("#CreateRelTableDiv").attr("style", "display:none");
						$("#AddFieldDiv").attr("style", "display:none");
						$("#ModifyRelTableDiv").attr("style", "display:block");
						$("#newRelTableName").val(relTable.name);
					}
				}
			}
		});
	},
	
	showDDLDiv : function(ddl){
        var ddlBegin = ddl.slice(0,ddl.length-1);
		$("#DDLBegin").html(ddlBegin);
        $("#AppendDDL").html("");
        $("#AppendDDL").removeAttr("readOnly");
        $("#DDLDIV").show();
	},
	
    ChooseFields : function(){
		$(".fieldsRemove").remove();
		$.ajax({
			url : "../e5dom/DocTypeController.do?invoke=getExtFields&docTypeID="+$("#DocTypeList").val(),
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
							var trHtml = "";
							trHtml += "<tr class='fieldsRemove' onmouseover=\"this.bgColor='#E4E8EB';\" onmouseout=\"this.bgColor='#ffffff';\">";
							trHtml += "<td><input type=\"checkbox\" name=\"extFields\" value=\""+field.fieldID+"\" /></td>";
							trHtml += "<td>"+field.fieldID+"</td>";
							trHtml += "<td>"+field.columnName+"</td>";
							trHtml += "<td>"+field.columnCode+"</td>";
							trHtml += "<td>"+field.dataType+"</td></tr>";
							$("#tab1").append(trHtml);
						});
					}
				}
			}
		});
	},

	chooseDSConfig : function () {
		page.currentDSID = parseInt($("#DSConfigList").val());
	},

	//right click to show popmenu
    showContextMenu : function (el) {
		tableMenu.libID = el.id.substring(5);
		tableMenu.libName = el.innerHTML;
		webFXMenuHandler.showMenu(tableMenu, el);
	},

	chooseDocType : function(){
		var docTypeID = $("#DocTypeList").val();
		var docType = e5dom.getDocType(docTypeID);
		e5dom.currentDocType = docType;
		page.operation.showRelTableList();
		$("#RightDiv").attr("style", "display:none");
	},
	
	showRelTableList : function () {
		$(".tablesRemove").remove();
		$.ajax({
			url : "RelTableController.do?invoke=getRelTables",
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
						$.each(datas,function(i, table){
							var trHtml = "";
							trHtml += "<tr class='tablesRemove' title=\""+i18nInfo.RightClick+"\" id=\""+table.id+"\" onmouseover=\"this.bgColor='#E4E8EB';\" onmouseout=\"this.bgColor='#ffffff';\" onclick=\"page.operation.chooseRelTable(this.id);\">";
							trHtml += "<td align=\"center\" id=\"table"+table.id+"\"  oncontextmenu=\"page.operation.showContextMenu(this);return false;\">"+table.name+"</td></tr>";
							$("#testTbl").append(trHtml);
						});
					}
				}
			}
		});
	},

	chooseRelTable : function (tableid) {
		var tbid = parseInt(tableid);
		e5dom.currentRelTableID = tbid;
		$.ajax({
			url : "RelTableController.do?invoke=getRelTable&tableId="+tbid,
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
						$.each(datas,function(i, tb){
							$("#tb_id").html(tb.id);
							$("#tb_name").html(tb.name);
							$("#tb_tableName").html(tb.tableName);
							$("#tb_dsID").html(tb.dsID);
							$.ajax({
								url : "../e5dom/DocTypeController.do?invoke=getDocType&docTypeID="+tb.refDocTypeID,
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
											var docType = datas[0];
											$("#tb_DocType").html(docType.docTypeName);
										}
									}
								}
							});
						});
					}
				}
			}
		});
		
		$("#RightDiv").attr("style", "display:block");
		$("#CreateRelTableDiv").attr("style", "display:none");
        $("#AddFieldDiv").attr("style", "display:none");
		$("#ModifyRelTableDiv").attr("style", "display:none");
    },

    addNewField : function(){
    	$("#CreateFieldBtn").removeAttr("disabled");
        var tableId = parseInt(tableMenu.libID);
		if(page.operation.relationExisted(tableId)){
			alert(i18nInfo.relationExisted);
			return false;
		}
		$("#AddFieldDiv").show();
        $("#RightDiv").attr("style", "display:none");
		$("#CreateRelTableDiv").attr("style", "display:none");
		$("#ModifyRelTableDiv").attr("style", "display:none");
        $("#tableId").val(tableId);
		$("#fieldName").val("");
    },

    deleteRelTable : function() {
		
		var tableId = parseInt(tableMenu.libID);	
		var tableName = tableMenu.libName;
		if(confirm(i18nInfo.confirmDeleteRelTable +" "+ tableName +" " +i18nInfo.confirmDeleteRelTableEnd)){
			$.ajax({
				url : "RelTableController.do?invoke=deleteRelTable&tableId="+tableId,
				dataType : "json",
				async : false,
				success : function(data) {
					if(data == "1"){
						alert(i18nInfo.DeleteTableOK);
					}
					else if(data== "-1") {
						alert(i18nInfo.DeleteTableFailed);
					}
					else if(data == "0"){
						alert(i18nInfo.deleteRelationFirst);
					}
					//refresh
					page.operation.chooseDocType();
				}
			});
		}
	    
	},
	
	relationExisted : function(tableId) {
		var ret = false;
		$.ajax({
			url : "RelTableDocLibController.do?invoke=getRelTableDocLibs",
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
						$.each(datas,function(i, tb){
							if (tb.relTableId == tableId) {
								ret = true;
								return;
							}
						});
					}
				}
			}
		});
		return ret;
	},
	fieldTypeChange : function(){
		var dt = $("#fieldType").val();
		if (dt == "CHAR" || dt == "VARCHAR") {
			$("#fieldLength").attr("class", "validate[required,custom[integer]]");
		} else {
			$("#fieldLength").attr("class", "validate[custom[integer]]");
		}
	}
}

 function CheckAll(form){
	 for (var i=0;i<form.elements.length;i++){
		 var e = form.elements[i];
		 if (e.Name != 'chkAll'&&e.disabled==false)
			 e.checked = form.chkAll.checked;
	 }
 }
window.onload = page.loaded;