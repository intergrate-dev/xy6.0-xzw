var libMenu = new WebFXMenu();
libMenu.width = 150;

libMenu.add(new WebFXMenuItem(i18nInfo.addNewRelation, "page.operation.addNewRelation();", i18nInfo.addNewRelation));
libMenu.add(new WebFXMenuSeparator());
libMenu.add(new WebFXMenuItem(i18nInfo.deleteRelation, "page.operation.deleteRelation();", i18nInfo.deleteRelation));
libMenu.generate();
var CreateRelationDIVDialog;
var page = {	

	loaded : function(){	
		page.initial.assignHandlers();
		page.initial.showRelTableDocLibs();
		page.initial.initialDialog();
	},
	
	relatedTableFields : new Array(),
	relatedLibFields : new Array()
}
page.initial = {
	assignHandlers : function() {
		$("#CreateRelTableDocLib").click(page.handlers.clickCreateRelTableDocLib);
		// $("#CreateRelTableDocLib").mouseout(page.handlers.mouseOutCreateTable);
		// $("#CreateRelTableDocLib").mouseover(page.handlers.mouseOverCreateTable);
		$("#RelTableList").change(page.handlers.chooseRelTableList);
		$("#DocLibList").change(page.handlers.chooseDocLibList);
		$("#CatTypeList").change(page.handlers.chooseCatType);
		$("#ConfirmChozenFieldsBtn").click(page.handlers.confirmChozenFields);
		$("#ConfirmRelationsBtn").click(page.handlers.confirmRelations);
		$("#RelationsCancel").click(page.handlers.cancelCreateRelationDIV);
    },
    initialDialog : function() {
		var dWidth = 1000;
		var dHeight = 500;
		if (dWidth > screen.availWidth - 50) dWidth = screen.availWidth - 50;
		if (dHeight > screen.availHeight - 50) dHeight = screen.availHeight - 50;
		
		CreateRelationDIVDialog = e5.dialog("", {
			title : i18nInfo.CreateDocLibRelTable,
			id : "CreateRelationDIVDialog",
			width : dWidth,
			height : dHeight,
			resizable : true,
			showClose : true,
			ishide : true
		});
		CreateRelationDIVDialog.DOM.content.append($("#CreateRelationDIV"));
	},
	showRelTableDocLibs : function() {
    	$(".fieldsRemove").remove();
		$.ajax({
			url : "RelTableDocLibController.do?invoke=getRelTableDocLibs",
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
						$.each(datas,function(i, vo){
							var id = (vo.relTable==null?"":vo.relTable.id);
							var docLibID = (vo.docLib==null?"":vo.docLib.docLibID);
							var catType = (vo.catType==null?"":vo.catType.catType);
							var name = (vo.catType==null?"":vo.catType.name);
							var tableName = (vo.catType==null?"":vo.catType.tableName);
							var docLibName = (vo.docLib==null?"":vo.docLib.docLibName);
							var docLibTable = (vo.docLib==null?"":vo.docLib.docLibTable);
							var relName = (vo.relTable==null?"":vo.relTable.name);
							var relTableName = (vo.relTable==null?"":vo.relTable.tableName);
							var categoryField = (vo.categoryField==null?"":vo.categoryField);
							var trHtml = "";
							trHtml += "<tr title='"+i18nInfo.ondblclickTitle+"' ondblclick=\"page.handlers.clickRelation('"+
										id+"','"+
										docLibID+"','"+
										catType+"','"+
										categoryField+
										"')\" class='fieldsRemove' onmouseover=\"this.bgColor='#E4E8EB';\" onmouseout=\"this.bgColor='#ffffff';\">";
							trHtml += "<td oncontextmenu=\"page.operation.showContextMenu('"+
										id+"','"+
										docLibID+"','"+
										catType+
										"',this);return false;\">"+
										relName+"[" + 
										relTableName + "]</td>";
							trHtml += "<td oncontextmenu=\"page.operation.showContextMenu('"+
										id+"','"+
										docLibID+"','"+
										catType+
										"',this);return false;\">"+
										docLibName+"[" + 
										docLibTable + "]</td>";
							trHtml += "<td oncontextmenu=\"page.operation.showContextMenu('"+
										id+"','"+
										docLibID+"','"+
										catType+
										"',this);return false;\">"+
										name + "</td></tr>";
										$("#testTbl").append(trHtml);
						});
					}
	            }
			}
		});
	}
}

page.handlers = {
		chooseRelTableList : function(){
			page.handlers.chooseRelTable();
			page.handlers.chooseSameNameFields();
		},
		chooseDocLibList : function(){
			page.handlers.chooseDocLib();
			page.handlers.chooseSameNameFields();
		},

	clickRelation : function(relTableId,doclibId,catTypeId,categoryField) {	
			$("#ConfirmRelationsSpan").attr("style","display:none;");
			$("#fieldsBtn").hide();
			page.operation.showRelationsDIV(relTableId,doclibId,catTypeId,categoryField);	    
	},

	confirmRelations : function() {
		var spans = $("#RelationFieldsDIV .fieldSpan");
		var relFields = new Array();
		for(var i=0;i<spans.length;i++)
		{
			relFields.push(spans[i].name)
		}
		var relTableId = $("#RelTableList").val();
		var docLibId = $("#DocLibList").val();
		var catTypeId = $("#CatTypeList").val();
		var categoryField = $("#CategoryFieldDIV").val();
		if(categoryField == null || categoryField.length==0){
			alert(i18nInfo.NoCategoryField);
			return false;
		}
		var newFlag = $("#newFlag").html();
		//2015.7.8 Gong Lijie 栏目稿件关联表一定是忽略空栏目的，隐藏以免漏选时出错
		//var ignoreFlag = (document.getElementById("ignoreFlag").checked)?1:0;
		var ignoreFlag = 1;
		
		var url = "RelTableDocLibController.do?invoke=createRelation&relTableId="+relTableId+
		"&docLibId="+docLibId+
		"&catTypeId="+catTypeId+
		"&categoryField="+categoryField+
		"&relFields="+relFields+
		"&ignoreFlag="+ignoreFlag+
		"&newFlag="+newFlag;
		$.ajax({
			url : url,
			dataType : "json",
			async : false,
			success : function(data) {
				if(data=="1"){
					page.handlers.cancelCreateRelationDIV();
					alert(i18nInfo.CreateRelationOK);
					page.initial.showRelTableDocLibs();
//					$("#CreateRelationDIV").hide();
	            }else if(data=="0") {
	            	alert(i18nInfo.RelationExisted);
	            }else{
	            	alert(i18nInfo.CreateRelationFailed);
	            }
			}
		});
	},

	doValidate : function(tableFieldType,libFieldType) {

		if (tableFieldType.indexOf("VARCHAR") > -1) {
			if (libFieldType.indexOf("VARCHAR") > -1 || libFieldType.indexOf("EXTFILE") > -1)
				return true;
		}
		//if is INTEGER or LONG
		if (libFieldType.indexOf("INTEGER") > -1 || libFieldType.indexOf("LONG") > -1) {
			if (tableFieldType.indexOf("NUMERIC") >-1
				||tableFieldType.indexOf("DECIMAL") >-1
				||tableFieldType.indexOf("BIGINT") >-1
				||tableFieldType.indexOf("NUMBER") >-1
				||tableFieldType.indexOf("INT") >-1
				||tableFieldType.indexOf("INT8") >-1
				) {
				return true;
			}
		}
		//if is DOUBLE or FLOAT
		if (libFieldType.indexOf("DOUBLE") > -1 || libFieldType.indexOf("FLOAT") > -1) {
			if (tableFieldType.indexOf("NUMERIC") >-1
				||tableFieldType.indexOf("DECIMAL") >-1
				||tableFieldType.indexOf("NUMBER") >-1) {
				return true;
			}		    
		}
		if(tableFieldType.indexOf("DATE") >-1 || tableFieldType.indexOf("TIME") >-1){
			if(libFieldType.indexOf("DATE") >-1 || libFieldType.indexOf("TIME") >-1)
				return true;
		}
		if (tableFieldType.indexOf("IMAGE") >-1 || tableFieldType.indexOf("BLOB") >-1) {
			if (libFieldType.indexOf("BLOB") > -1) {
				return true;
			}		    
		}
		if (tableFieldType.indexOf("TEXT") >-1 || tableFieldType.indexOf("CLOB") >-1) {
			if (libFieldType.indexOf("CLOB") > -1) {
				return true;
			}		    
		}
		
		if (tableFieldType != libFieldType)
			return false;
		return true;
	},

	confirmChozenFields : function() {
		var tableField = $("#RelTableFieldsList").val();
		var libField = $("#DocLibFieldsList").val();
		
		var tableFieldType = tableField.slice(tableField.indexOf(";")+1);
		var libFieldType = libField.slice(libField.indexOf(";")+1);

		if (!page.handlers.doValidate(tableFieldType,libFieldType)){
			alert(i18nInfo.DataTypeMismatch);
			return false;
		}

		var tableFieldName = tableField.slice(0,tableField.indexOf(";"));
		var libFieldName = libField.slice(0,libField.indexOf(";"));
		if (page.operation.fieldsRelated(tableFieldName,libFieldName)){
			alert(i18nInfo.FieldsRelationAlreadyExisted);
			return false;
		}
		page.operation.appendRelationFieldsDIV(tableFieldName,libFieldName);
	},

	clickCreateRelTableDocLib : function() {
		$("#ConfirmRelationsSpan").removeAttr("style");
		$("#fieldsBtn").show();
		page.operation.showRelationsDIV();
	},

	chooseSameNameFields : function() {
		$("#RelationFieldsDIV").html("");
		
		var relTableFields = document.getElementById("RelTableFieldsList").options;
		for (var i = 0; i < relTableFields.length; i++) {
			var tableField = relTableFields[i].value;
			var idx = tableField.indexOf(";");
			//var tableFieldName = tableField.substring(0,idx).toUpperCase();
			var tableFieldName = tableField.substring(0,idx);
			var tableFieldType = tableField.substring(idx+1).toUpperCase();

			var docLibFields = document.getElementById("DocLibFieldsList").options;
			for (var j = 0; j < docLibFields.length; j++) {
				var libField = docLibFields[j].value;
				var idx1 = libField.indexOf(";");
				//var libFieldName = libField.substring(0,idx1).toUpperCase();
				var libFieldName = libField.substring(0,idx1);
				var libFieldType = libField.substring(idx1+1).toUpperCase();
				
                if (tableFieldName.toUpperCase() == libFieldName.toUpperCase()){
					//if same type, then add automatically
					if (page.handlers.doValidate(tableFieldType,libFieldType)){
						if (!page.operation.fieldsRelated(tableFieldName,libFieldName)){
							page.operation.appendRelationFieldsDIV(tableFieldName,libFieldName);
						}
						continue;
					}
               }
			}
		}
	},

	chooseRelTable : function(relTableId) {
		page.operation.clearRelatedFieldsArray();
		
		if(typeof(relTableId) != "undefined"){
			var ops = document.getElementById("RelTableList").options;
			for(var i=0;i<ops.length;i++)
			{
				if(relTableId == ops[i].value)
					ops[i].selected = "selected";				
			}
		}	
		var tableId = parseInt($("#RelTableList").val());
		page.operation.loadRelTableFields(tableId);
		page.operation.clearRelation();
	},
	
	chooseDocLib: function(doclibId) {
		page.operation.clearRelatedFieldsArray();
		
		if(typeof(doclibId) != "undefined"){
			var ops = document.getElementById("DocLibList").options;
			for(var i=0;i<ops.length;i++)
			{
				if(doclibId == ops[i].value)
					ops[i].selected = "selected";
			}
		}
		var docLibId = parseInt($("#DocLibList").val());
		page.operation.loadDocLibFields(docLibId);
		page.operation.clearRelation();
	},
	chooseCategoryField : function(categoryField) {
		if(typeof(categoryField) == "undefined"){
			return false;
		}
		var ops = document.getElementById("CategoryFieldDIV").options;
		for(var i =0;i<ops.length;i++)
		{
			if(categoryField == ops[i].value){
				ops[i].selected = "selected";
			}			
		}
	},
	chooseCatType : function(catTypeId) {
		if(typeof(catTypeId) == "undefined"){
			return false;
		}
		var ops = document.getElementById("CatTypeList").options;
		for(var i =0;i<ops.length;i++)
		{
			if(catTypeId == ops[i].value){
				ops[i].selected = "selected";
			}			
		}
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

	overOneTR : function(trE){
		trE.style.cssText = "cursor:pointer;background-color:#CCCCFF";
	},

	outOneTR : function (trE) {		
		trE.style.cssText = "cursor:default;background-color:white";	    
	},
	cancelCreateRelationDIV : function() {// 关闭创建文档类型字段模态窗口
		CreateRelationDIVDialog.hide();
	}
}


page.operation = {
	
	appendRelationFieldsDIV : function(tableFieldName,libFieldName){
		var oDiv = document.createElement("div");
		
		var oSpan1 = document.createElement("span");
		oSpan1.innerHTML = libFieldName +" <-> "+ tableFieldName;
		oSpan1.name=tableFieldName +":"+ libFieldName;
		oSpan1.className="fieldSpan";
		oDiv.appendChild(oSpan1);

		var oSpan2 = document.createElement("span");
		oSpan2.innerHTML = " ---  delete";
		oSpan2.style.cssText = "color:black";
		oSpan2.onmouseover = function() {
			this.style.cssText = "cursor:pointer;color:red";
		};
		oSpan2.onmouseout = function(){
			this.style.cssText = "color:black";
		};
		oSpan2.onclick = function() {
			this.parentNode.parentNode.removeChild(this.parentNode);
			for(var i=0;i<page.relatedTableFields.length;i++)
			{
				var tableField = page.relatedTableFields[i];
				if(tableField == tableFieldName){
					if(page.relatedLibFields[i] == libFieldName){
						page.relatedTableFields.splice(i,1);
						page.relatedLibFields.splice(i,1);
					}

				}
			}
			
		};
		oDiv.appendChild(oSpan2);
		document.getElementById("RelationFieldsDIV").appendChild(oDiv);
	},
	
	showRelatedFields : function(doclibId,catTypeId) {
		
		var libId = parseInt(doclibId);
		var typeId = parseInt(catTypeId);
		$.ajax({
			url : "RelTableDocLibController.do?invoke=getRelTableDocLib&docLibId="+libId+"&catTypeId="+typeId,
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
						vo = datas[0];
						if(vo.ignoreFlag == 1)
							$("ignoreFlag").checked = true;
						else
							$("ignoreFlag").checked = false;
						
						if(vo.relatedFields.length == 0)
							return false;
						var relations = vo.relatedFields.split(";");
						page.operation.clearRelatedFieldsArray();
						for(var i=0;i<relations.length;i++)
						{
							var tableFieldName = relations[i].slice(0,relations[i].indexOf(":"));
							var libFieldName = relations[i].slice(relations[i].indexOf(":")+1);
							page.operation.appendRelationFieldsDIV(tableFieldName,libFieldName);
							page.relatedTableFields.push(tableFieldName);
							page.relatedLibFields.push(libFieldName);		    					
						}
					}
				}
			}
		});
	},

	clearRelation : function() {
		$("#RelationFieldsDIV").html("");
//		$("CategoryFieldDIV").innerHTML = "";
	},

	loadRelTableFields : function(tableId) {
		$.ajax({
			url : "RelTableDocLibController.do?invoke=getRelTableFields&tableId="+tableId,
			dataType : "json",
			async : false,
			success : function(data) {
				$("#RelTableFieldsList").html("");
				if (data != "-1") {
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					if(datas!=null&&datas.length>0){
						$.each(datas,function(i, field){
							var op = document.createElement("option");
							op.value = field.fieldName+";"+field.dataType;
							op.text = field.fieldName+": "+field.dataType;
							document.getElementById("RelTableFieldsList").options.add(op);
						});
						document.getElementById("RelTableFieldsList").options[0].selected = "selected";
					}
				}
			}
		});
	},

	loadDocLibFields : function(docLibID) {
		var docTypeID=0;
		$.ajax({
			url : "../e5dom/DocLibController.do?invoke=getDocLib&docLibID="+docLibID,
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
						var docLib = datas[0];
						docTypeID = docLib.docTypeID;
					}
				}
			}
		});
		
		$.ajax({
			url : "../e5dom/DocTypeController.do?invoke=getExtFields&docTypeID="+docTypeID,
			dataType : "json",
			async : false,
			success : function(data) {
				$("#DocLibFieldsList").html("");
				$("#CategoryFieldDIV").html("");
				if (data !=""&&data != "-1") {
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					if(datas!=null&&datas.length>0){
						$.each(datas,function(i, field){
							var op = document.createElement("option");
							op.value = field.columnCode+";"+field.dataType;
							op.text = field.columnCode+"   ["+field.columnName+"]";
							document.getElementById("DocLibFieldsList").options.add(op);
							
							if(field.dataType=="VARCHAR"){
								var op = document.createElement("option");
								op.value = field.columnCode;
								op.text = field.columnCode+"   ["+field.columnName+"]";
								document.getElementById("CategoryFieldDIV").options.add(op);
							}
						});
						document.getElementById("DocLibFieldsList").options[0].selected = "selected";
					}
				}
			}
		});
	},

	loadRelTableList : function() {
		$.ajax({
			url : "RelTableController.do?invoke=getRelTables",
			dataType : "json",
			async : false,
			success : function(data) {
				$("#RelTableList").html("");
				if (data != "-1") {
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					if(datas!=null&&datas.length>0){
						$.each(datas,function(i, relTable){
							var op = document.createElement("option");
							op.value = ""+relTable.id;
							op.text = relTable.name;
							document.getElementById("RelTableList").options.add(op);
						});
					}
				}
			}
		});
	},

	loadDocLibList : function() {
		$.ajax({
			url : "../e5dom/DocLibController.do?invoke=getDocLibs",
			dataType : "json",
			async : false,
			success : function(data) {
				$("#DocLibList").html("");
				if (data != "-1") {
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					if(datas!=null&&datas.length>0){
						$.each(datas,function(i, docLib){
							var op = document.createElement("option");
							op.value = ""+docLib.docLibID;
							op.text = docLib.docLibName;
							document.getElementById("DocLibList").options.add(op);
						});
					}
				}
			}
		});
	},

	loadCatTypeList : function() {
		$.ajax({
			url : "RelTableDocLibController.do?invoke=getCatTypes",
			dataType : "json",
			async : false,
			success : function(data) {
				$("#CatTypeList").html("");
				if (data != "-1") {
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					if(datas!=null&&datas.length>0){
						$.each(datas,function(i, catType){
							var op = document.createElement("option");
							op.value = ""+catType.catType;
							op.text = catType.name;
							document.getElementById("CatTypeList").options.add(op);
						});
					}
				}
			}
		});
	},

	loadChooseList : function(relTableId,doclibId,catTypeId,categoryField) {
//		alert(relTableId+" ,"+doclibId+", "+catTypeId+", "+categoryField);
		page.operation.loadCatTypeList();
		page.handlers.chooseCatType(catTypeId);
			
		page.operation.loadRelTableList();
		page.handlers.chooseRelTable(relTableId);
	
		page.operation.loadDocLibList();
		page.handlers.chooseDocLib(doclibId);
		if(doclibId!=undefined&&doclibId.length>0){
			page.operation.loadDocLibFields(doclibId);
			page.handlers.chooseCategoryField(categoryField);
		}
		//当点击创建对应时做自动对应，选择已存在的文档类型时，则取对应过的。
		if(relTableId==undefined&&doclibId==undefined&&catTypeId==undefined&&categoryField==undefined){
			page.handlers.chooseSameNameFields();
		}
		if(typeof(doclibId) != "undefined" && typeof(catTypeId) != "undefined")
		{
			$("#newFlag").html("0");
			page.operation.showRelatedFields(doclibId,catTypeId);
		}else{
			$("#newFlag").html("1");
		}
	},
	
	//right click to show popmenu
    showContextMenu : function (tableId,libId,catTypeId,el) {
		libMenu.tableId		= tableId;
		libMenu.libId		= libId;
		libMenu.catTypeId	= catTypeId;
		webFXMenuHandler.showMenu(libMenu, el);
	},

	showRelationsDIV : function(relTableId,doclibId,catTypeId,categoryField) {
		CreateRelationDIVDialog.show();
		page.operation.loadChooseList(relTableId,doclibId,catTypeId,categoryField);
	},
	deleteRelation : function() {
		if(!confirm(i18nInfo.confirmDeleteRelation))
			return false;		
		$.ajax({
			url : "RelTableDocLibController.do?invoke=removeRelTableDocLib&docLibId="+libMenu.libId+"&catTypeId="+libMenu.catTypeId,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data == "1") {
					alert(i18nInfo.DeleteTableOK);
					page.initial.showRelTableDocLibs();
				}else{
					alert(i18nInfo.DeleteTableFailed);
				}
			}
		});
	},

	addNewRelation : function() {
		$("#ConfirmRelationsSpan").removeAttr("style");
		$("#fieldsBtn").show();
		page.operation.showRelationsDIV();
	},

	fieldsRelated : function(tableFieldName,libFieldName) {
		var ret = false;
		for(var i=0;i<page.relatedTableFields.length;i++)
		{
			var tableField = page.relatedTableFields[i];
			if(tableField == tableFieldName){
				if(page.relatedLibFields[i] == libFieldName)
					ret = true;
			}
		}

		if (!ret) {
			page.relatedTableFields.push(tableFieldName);
			page.relatedLibFields.push(libFieldName);		    
		}

		return ret;
	},
	
	clearRelatedFieldsArray : function () {
		page.relatedTableFields = new Array();
		page.relatedLibFields = new Array();
	}
	
}
window.onload = page.loaded;