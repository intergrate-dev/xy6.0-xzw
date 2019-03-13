function DocLib(docTypeID,dsID) {
	this.docTypeID = docTypeID;
	this.dsID = dsID;
}

var libMenu = new WebFXMenu();
libMenu.width = 150;
libMenu.add(new WebFXMenuItem(i18nInfo.updateDocLib, "page.operation.updateDocLib();", i18nInfo.updateDocLib));
libMenu.add(new WebFXMenuItem(i18nInfo.deleteDocLib, "page.operation.deleteDocLib();", i18nInfo.deleteDocLib));
//libMenu.add(new WebFXMenuSeparator());
libMenu.generate(); 

var page = {	

	loaded : function(){	
		//initial DocType list
		var iniFlag = page.initial.initialDocTypeList();
		//set DocType chosen handler	
		page.initial.assignHandlers();

		if(!iniFlag)
			return false;

		page.initial.initialDSConfigList();
		page.initial.initDevice();
		//choose one DocType
		page.operation.chooseDocType();	
	},
	
	currentDSID:0,
	
	dSConfigs:null

}
var CreateDocLibDialog;
page.initial = {
	initialDSConfigList : function() {
		$.ajax( {
			url : "DocLibController.do?invoke=getAllE5DataSources",
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
					for(var i=0;i<datas.length;i++)
					{
						var op = document.createElement("option");
						op.value = ""+datas[i].dsID;
						op.text = datas[i].name;
//						$("#DSConfigList").append(op);
						document.getElementById("DSConfigList").options.add(op);
					}
					page.currentDSID = datas[0].dsID;
				}
			}
		});
	},

	initialDocTypeList : function() {
		e5dom.loadDocTypes();
		if(e5dom.docTypes.length ==0)
			return false;
		for(var i=0;i<e5dom.docTypes.length;i++)
		{
			var op = document.createElement("option");
			op.value = e5dom.docTypes[i].docTypeID;
			op.text = e5dom.docTypes[i].docTypeName;
//			$("#DocTypeList").append(op);
			document.getElementById("DocTypeList").options.add(op);
		}
		return true;
	},

	assignHandlers : function() {
		$("#DocTypeList").change(page.operation.chooseDocType);
		$("#DSConfigList").change(page.operation.chooseDSConfig);
		$("#createDocLib").click(page.handlers.ClickCreateDocLib);
		// $("#createDocLib").mouseout(page.handlers.mouseOutCreateDocLib);
		// $("#createDocLib").mouseout(page.handlers.mouseOverCreateDocLib);
		$("#ConfirmDDLBtn").click(page.handlers.ConfirmDDL);
//		$("#CreateDocLibBtn").click(page.handlers.CreateDocLib);
	},
	initDevice : function(){
		 $.ajax({ url: "DocLibController.do?invoke=getStorageListForJson",
				async: false,
				dataType:"json",
				success: function (data) {
					if(data!=null&& data!=""){
						var options =[];
						$.each(data,function(i,n){
							options.push("<option value='"+n.deviceName+"'>");
							options.push(n.deviceName);
							options.push("</option>");
						});
						$("#attachDevName1").append(options.join(''));
						$("#attachDevName2").append(options.join(''));
					}
				}
		 });
	}
}

page.handlers = {
	submitUpdateDocLib : function() {
		$.ajax( {
			url : "DocLibController.do?invoke=updateDocLib&docLibID="
					+ e5dom.currentDocLibID + "&docLibName="
					+ encodeURI($("#docLibName").val()) + "&docLibDesc="
					+ encodeURI($("#docLibDesc").val()) + "&attachDevName="
					+ encodeURI($("#attachDevName1").val()),
			dataType : "json",
			async : false,
			success : function(data) {
				if (data == "1") {
					$("#libName" + e5dom.currentDocLibID).html(
							$("#docLibName").val());
					$("#UpdateDocLibDIV").hide();
					$("#CreateDocLibDiv").hide();
				}
			}
		});
	},

	CreateDocLib : function () {
		var newDocTypeID = parseInt($("#newDocTypeID").val());
		var newDSID = parseInt($("#newDSID").val());
		var newDocLibID = parseInt($("#newDocLibID").val());
		var DDL = $("#FieldsDDL").html() + $("#appendDDL").val()+$("#DDLEnd").html()+$("#extendDDL").val();
		
		var docLibTable = $("#docLibTable").val();
		//如果docLibTable不为null，不为空字符串，则替换默认生成的数据库表名称。
		if(docLibTable != null && docLibTable !=''){
			var defaultTable = "DOM_" + newDocLibID + "_DOCLIB";
			if(DDL.indexOf(defaultTable) > 0) {
				re = new RegExp(defaultTable,"g");  
				DDL=DDL.replace(re,docLibTable);
			}
		}
		
		var newLibName = $("#newDocLibName").val();
		var newLibDesc = $("#newDocLibDesc").val();
		var	newDocLibKeepDays = $("#newDocLibKeepDays").val();
		var attachDevName = $("#attachDevName2").val();
		var shareRecordTable = null;
		if($("#shareRecordTable").attr("checked"))
			shareRecordTable = "on";
		
		$.ajax( {
			url : "DocLibController.do?invoke=createDocLib&DDL="
					+ encodeURI(DDL) + "&docTypeID=" + newDocTypeID + "&dsID="
					+ newDSID + "&docLibID=" + newDocLibID + "&docLibName="
					+ encodeURI(newLibName) + "&description="
					+ encodeURI(newLibDesc) + "&keepDay=" + newDocLibKeepDays
					+ "&attachDevName=" +  encodeURI(attachDevName)
					+ "&docLibTable=" +  docLibTable
					+ "&shareRecordTable=" +  shareRecordTable,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data == "1") {
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					page.operation.chooseDocType();
					$("#CreateDocLibDiv").hide();
				} else {
					alert(data);
				}
			}
		});
	},
	
	ReplaceDocLibTable : function () {
		var docLibTable = $("#docLibTable").val();
		//如果docLibTable不为null，不为空字符串，则替换默认生成的数据库表名称。
		if(docLibTable != null && docLibTable !=''){
			var FieldsDDL = $("#FieldsDDL").html();
			var index0 = ddl.indexOf("DOM_");
			var index1 = ddl.indexOf("_DOCLIB");
			var newDocLibID = ddl.slice(index0+4,index1);
			var defaultTable = "DOM_" + newDocLibID + "_DOCLIB";
			re = new RegExp(defaultTable,"g");  
			
			if(FieldsDDL.indexOf(defaultTable) > 0) {
				FieldsDDL=FieldsDDL.replace(re,docLibTable);
				$("#FieldsDDL").html(FieldsDDL);
			}
			var appendDDL = $("#appendDDL").html();
			if(appendDDL.indexOf(defaultTable) > 0) {
				
				appendDDL=appendDDL.replace(re,docLibTable);
				$("#appendDDL").html(appendDDL)
			}
			
		}
	},
	
	ConfirmDDL : function () {
		$("#appendDDL").attr("readOnly", "true");
		$("#extendDDL").attr("readOnly", "true");
		$("#CreateDocLibBtn").removeAttr("disabled");
		$("#newDocLibProps").show();
		$("#newDocLibName").val("");
		$("#newDocLibDesc").val("");
	},

	ClickCreateDocLib : function(e) {
		page.operation.hideAll();
		$("#RightDiv").hide();
		$("#CreateDocLibDiv").show();
		$("#newDocLibProps").show();
		$("#CreateDocLibBtn").removeAttr("disabled");
		$("#shareRecordTable").removeAttr("checked")
		$("#newDocLibName").val("");
		$("#docLibTable").val("");
		$("#newDocLibDesc").val("");
		$("#newDocLibKeepDays").val("");
		$("#attachDevName2").val("");
		$("#UpdateDocLibDIV").hide();
		
		if(document.getElementById("CreateDocLibDiv").style.display !="none")
		{
			var docTypeID = e5dom.currentDocType.docTypeID;
			var dsID = parseInt(page.currentDSID);
			$.ajax({
				url : "DocLibController.do?invoke=genDocLibCreationDDL&docTypeID="+docTypeID+"&dsID="+dsID,
				dataType : "text",
				async : false,
				success : function(data) {
					if (data != null&&data != "-1") {
						ddl = data.toString();
						ddl = ddl.slice(0,ddl.length-1);
						var boundary = ddl.lastIndexOf(",");
						var fieldDDL = ddl.slice(0,boundary);
						var appendDDL = ddl.slice(boundary,ddl.length);
						$("#FieldsDDL").html(fieldDDL);
						$("#appendDDL").val(appendDDL);
						var index0 = ddl.indexOf("DOM_");
						var index1 = ddl.indexOf("_DOCLIB");
						var newDocLibID = ddl.slice(index0+4,index1);
						// alert(newDocLibID);
						$("#newDocLibID").val(newDocLibID);
						$("#docLibTable").val("DOM_" + newDocLibID + "_DOCLIB");
						$("#newDocTypeID").val(e5dom.currentDocType.docTypeID);
						$("#newDSID").val(page.currentDSID);
					}
				}
			});
		}
		e.preventDefault();
		e.stopPropagation();
	},

	mouseOutCreateDocLib : function(evt) {
		var el = e5dom.util.srcEventElement(evt);
		el.style.textDecoration = "none";
		el.style.cursor = "default";
	},
		
	mouseOverCreateDocLib : function(evt) {
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
	updateDocLib : function() {
		page.operation.hideAll();
		$.ajax({
			url : "DocLibController.do?invoke=getDocLib&docLibID="+e5dom.currentDocLibID,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != "-1") {
					var lib = new Array();
					if (!$.isArray(data)) {
						lib.push(data);
					} else {
						lib = data;
					}
					if(lib != null && lib.length>0){
						$("#docLibName").val(lib[0].docLibName);
						$("#docLibDesc").val(lib[0].description);
						$("#attachDevName1").val(lib[0].storageDevice);
					}
					
					$("#UpdateDocLibBtnSave").removeAttr("disabled");
					$("#UpdateDocLibBtnCel").removeAttr("disabled");
					$("#RightDiv").hide();
					$("#UpdateDocLibDIV").show();
					$("#CreateDocLibDiv").hide();
				}
			}
		});
	},

	chooseDSConfig : function () {
		page.currentDSID = $("#DSConfigList").val();
	},

	showContextMenu : function (el,docLibName) {
		libMenu.libID = el.id;
		libMenu.docLibName = docLibName;
		e5dom.currentDocLibID = parseInt(libMenu.libID);
		webFXMenuHandler.showMenu(libMenu, el);
	},

	chooseDocType : function(){
		var docTypeID = $("#DocTypeList").val();
		var docType = e5dom.getDocType(docTypeID);
		e5dom.currentDocType = docType;
		page.operation.showDocLibList();
		$("#RightDiv").hide();

	},
	
	showDocLibList : function () {
		page.operation.hideAll();
		$.ajax({
			url : "DocLibController.do?invoke=getDocLibs&docTypeID="+e5dom.currentDocType.docTypeID,
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
	            			var trHtml = "";
	            			trHtml += "<TR class=\"fieldsRemove\" onmouseover=\"this.bgColor='#E4E8EB';\" onmouseout=\"this.bgColor='#ffffff';\" title=\""+i18nInfo.RightClick+"\" id=\""+d.docLibID+"\" onclick=\"page.operation.chooseDocLib(this.id);\" oncontextmenu=\"page.operation.showContextMenu(this,'"+d.docLibName+"');return false;\">";
	            			trHtml += "<TD align=\"center\" id=\"libName"+d.docLibID+"\">"+d.docLibName+"</TD>";
	            			trHtml += "</TR>";
	            			$("#testTbl").append(trHtml);
	            		});
					}
				}
			}
		});
	},

	chooseDocLib : function (doclibid) {
		page.operation.hideAll();
		var libid = parseInt(doclibid);
		e5dom.currentDocLibID = parseInt(libid);
		$.ajax({
			url : "DocLibController.do?invoke=getDocLib&docLibID="+libid,
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
						$("#RightDiv").show();
						var lib = datas[0];
						$("#docLibID_td").html(lib.docLibID);
						$("#docLibName_td").html(lib.docLibName);
						$("#description_td").html(lib.description);
						$("#docLibTable_td").html(lib.docLibTable);
						$("#attachDevName_td").html(lib.storageDevice);
					}
				}
			}
		});
		
		$.ajax({
			url : "DocLibController.do?invoke=getDocLibAdds&docLibID="+libid,
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
						$("#RightDiv").show();
						var libadds = datas[0];
						$("#libDB_td").html(libadds.libDB);
						$("#libServer_td").html(libadds.libServer);
						$("#libTable_td").html(libadds.libTable);
					}
				}
			}
		});
		$("#RightDiv").show();
		$("#CreateDocLibDiv").hide();
		$("#UpdateDocLibDIV").hide();
	},
	
	showDocLibProps : function () {
	    
	},
	
	deleteDocLib : function() {
		page.operation.hideAll();
		$("#RightDiv").hide();
		$("#CreateDocLibDiv").hide();
		$("#UpdateDocLibDIV").hide();
		var libid = parseInt(libMenu.libID);
		var docLibName = libMenu.docLibName;

		if(confirm(i18nInfo.confirmDeleteDocLib +" "+ docLibName +" " +i18nInfo.confirmDeleteDocLibEnd)){
			$.ajax({
				url : "DocLibController.do?invoke=deleteDoLib&docLibID="+libid,
				dataType : "json",
				async : false,
				success : function(data) {
					if(data == "1"){
						alert(i18nInfo.deleteOK);
					}else {
						alert(data);
					}
					//refresh
					page.operation.chooseDocType();
					page.operation.showDocLibList();
				}
			});
		}
	},
	hideAll : function() {
		$("#DocLibProps").validationEngine("hideAll");
		$("#UpdateDocLibForm").validationEngine("hideAll");
	}
}

window.onload = page.loaded;