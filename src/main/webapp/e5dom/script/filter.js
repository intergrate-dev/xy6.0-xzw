var filterMenu = new WebFXMenu();
filterMenu.width = 150;

filterMenu.add(new WebFXMenuItem(i18nInfo.newFilter, "page.operation.CreateFilter();", i18nInfo.newFilter));
filterMenu.add(new WebFXMenuItem(i18nInfo.modifyFilter, "page.operation.ModifyFilter();", i18nInfo.modifyFilter));
filterMenu.add(new WebFXMenuItem(i18nInfo.deleteFilter, "page.operation.DeleteFilter();", i18nInfo.deleteFilter));

filterMenu.generate(); 

var page = {	

	loaded : function(){	
		//initial DocType list
		var iniFlag = page.initial.initialDocTypeList();

		//set DocType chosen handler	
		page.initial.assignHandlers();

		if(!iniFlag)
			return false;

		//choose one DocType
		page.operation.chooseDocType();	
	}
	

}

page.initial = {

	initialDocTypeList : function() {

		e5dom.loadDocTypes();

		if(e5dom.docTypes.length ==0)
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
		$("#DocTypeList").change(page.operation.chooseDocType);
		$("#CreateFilter").click(page.operation.CreateFilter);
		// $("#CreateFilter").mouseover(page.handlers.mouseOverCreateFilter);
		// $("#CreateFilter").mouseout(page.handlers.mouseOutCreateFilter);
	}

}

page.handlers = {

	resetFilterForm : function() {
		$("#filterName").val("");
		$("#filterDesc").val("");
		$("#filterFormula").val("");  
	},

	mouseOverCreateFilter: function(evt) {
		var el = e5dom.util.srcEventElement(evt);
		el.style.textDecoration = "underline";
		el.style.cursor = "pointer";    	    
	},

	mouseOutCreateFilter : function(evt) {
		var el = e5dom.util.srcEventElement(evt);
		el.style.textDecoration = "none";
		el.style.cursor = "default";
	},

	SubmitCreateFilter : function () {
		var docTypeID = $("#docTypeID").val();
		var filterName = $("#filterName").val();
		var description = $("#filterDesc").val();
		var formula = $("#filterFormula").val();
		var url = "FilterController.do?invoke=createFilter"+
				  "&docTypeID="+docTypeID+
				  "&filterName="+encodeURI(filterName)+
				  "&description="+encodeURI(description)+
				  "&formula="+encodeURI(formula);
		$.ajax({
			url : url,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != "-1") {
					alert(i18nInfo.createOK);
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					page.operation.chooseDocType();
					$("#createBtn").removeAttr("disabled");
					$("#cancelBtn").removeAttr("disabled");
				}
			}
		});
	},
	
	SubmitModifyFilter : function () {
		var filterID = $("#filterID").val();
		var filterName = $("#filterName").val();
		var description = $("#filterDesc").val();
		var formula = $("#filterFormula").val();
		var url = "FilterController.do?invoke=updateFilter"+
				  "&filterName="+encodeURI(filterName)+
				  "&description="+encodeURI(description)+
				  "&formula="+encodeURI(formula)+
				  "&filterID="+filterID;
		$.ajax({
			url : url,
			dataType : "json",
			async : false,
			success : function(data) {
				if(data == "1"){
					alert(i18nInfo.modifyOK);
					page.operation.chooseDocType();
					page.operation.chooseFilter(filterID);
				}else{
					alert(i18nInfo.modifyNo);
				}
				$("#modifyBtn").removeAttr("disabled");
				$("#cancelBtn").removeAttr("disabled");
			}
		});
	},

	overOneTR : function(trE){
		trE.style.cssText = "cursor:pointer;background-color:#CCCCFF";
	},

	outOneTR : function (trE) {		
		trE.style.cssText = "cursor:default;background-color:white";	    
	}
}


page.operation = {

	CreateFilter : function() {
		$("#createBtn").removeAttr("disabled");
		$("#cancelBtn").removeAttr("disabled");
		$("#FilterProps").attr("style", "display:none");
		$("#FormDiv").attr("style", "display:block");
		$("#docTypeID").val(e5dom.currentDocType.docTypeID);
		$("#filterID").val("");
		$("#filterName").val("");
		$("#filterDesc").val("");
		$("#filterFormula").val("");
		$("#createBtn").show();
		$("#modifyBtn").hide();
	},

	ModifyFilter : function() {
		$("#modifyBtn").removeAttr("disabled");
		$("#cancelBtn").removeAttr("disabled");
		var filertID = parseInt(filterMenu.filterID);
		$.ajax({
			url : "FilterController.do?invoke=getFilter&filterID="+filertID,
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
						var filter = data[0];
						$("#filterName").val(filter.filterName);
						$("#filterDesc").val(filter.description);
						$("#filterFormula").val(filter.formula);
						$("#filterID").val(filterMenu.filterID);

						$("#FilterProps").attr("style", "display:none");
						$("#FormDiv").attr("style", "display:block");
						$("#createBtn").hide();
						$("#modifyBtn").show();
					}
					
				}
			}
		});
	},

	DeleteFilter : function() {
		var filterID = parseInt(filterMenu.filterID);
		var filterName = filterMenu.filterName;
		if(confirm(i18nInfo.confirm1+" "+filterName+" "+i18nInfo.confirm2))
		{
			$.ajax({
				url : "FilterController.do?invoke=deleteFilter&filterID="+filterID,
				dataType : "json",
				async : false,
				success : function(data) {
					if(data == "1"){
						alert(i18nInfo.deleteOK);
						page.operation.chooseDocType();
					}else{
						alert(i18nInfo.deleteNo);
					}
				}
			});
		}
	},

	showContextMenu : function (el,filterName) {
		filterMenu.filterID = el.id;
		filterMenu.filterName = filterName;
		webFXMenuHandler.showMenu(filterMenu, el);
	},

	chooseDocType : function(){
		var docTypeID = $("#DocTypeList").val();
		var docType = e5dom.getDocType(docTypeID);
		e5dom.currentDocType = docType;
		page.operation.showFilterList();
		$("#FormDiv").attr("style", "display:none");
		$("#FormDiv").validationEngine("hideAll");
		$("#FilterProps").attr("style", "display:none");
	},
	
	showFilterList : function () {
		$(".fieldsRemove").remove();
		$.ajax({
			url : "FilterController.do?invoke=getFilters&docTypeID="+e5dom.currentDocType.docTypeID,
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
						$.each(datas,function(i, filter){
							var trHtml = "";
							trHtml += "<TR class='fieldsRemove' title=\""+i18nInfo.RightClick+"\" id=\""+filter.filterID+"\" onmouseover=\"this.bgColor='#E4E8EB';\" onmouseout=\"this.bgColor='#ffffff';\"";
							trHtml += "onclick=\"page.operation.chooseFilter(this.id);\" oncontextmenu=\"page.operation.showContextMenu(this,'"+filter.filterName+"');return false;\">";
							trHtml += "<TD align=\"center\">"+filter.filterName+"</TD></TR>";
							$("#tab1").append(trHtml);
						});
					}
				}
			}
		});
	},

	chooseFilter : function (filterid) {
		var filterID = parseInt(filterid);
		$.ajax({
			url : "FilterController.do?invoke=getFilter&filterID="+filterID,
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
						var filter = data[0];
						$("#FilterNameSpan").html(filter.filterName);
						$("#FilterDescSpan").html(filter.description);
						$("#FilterFormulaSpan").html(filter.formula);
						var temp = "";
						for(var i=0;i<filter.fvs.length;i++)
						{
							if(filter.fvs[i]!=null){
								temp += "<div>"+filter.fvs[i].FVID +" - "+ filter.fvs[i].FVName + "</div>";
							}
						}
						$("#FilterFoldersSpan").html(temp);
						$("#FormDiv").attr("style", "display:none");
						$("#FormDiv").validationEngine("hideAll");
						$("#FilterProps").attr("style", "display:block");
						$("#errorDiv").hide();
					}
				}
			}
		});
	}
}
window.onload = page.loaded;