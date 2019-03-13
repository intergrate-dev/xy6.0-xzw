function FolderView(fvID,fvName) {
	this.fvID = fvID;
	this.fvName = fvName;
}

var page = {
	
	subFVs : new Array(),

	loaded : function(){	
		//initial DocLibList
		page.initial.initialDocLibList();
		//set handler	
		page.initial.assignHandlers();
		//showFolderTree
		page.handlers.showFolderTree();	
	}
}

page.initial = {
	initialDocLibList : function() {
		e5dom.loadDocLibs();
		for(var i=0;i<e5dom.docLibs.length;i++)
		{
			var op = document.createElement("option");
			op.value = e5dom.docLibs[i].docLibID;
			op.text = e5dom.docLibs[i].docLibName;
			document.getElementById("DocLibList").options.add(op);
		}
	},

	assignHandlers : function() {
		$("#DocLibList").change(page.handlers.showFolderTree);
	}
}

page.handlers = {

	resetViewForm : function() {
		$("#newViewName").val("");
		$("#newViewFormula").val("");
		$("#newViewFilters").html("");
	},

	resetFolderForm : function() {
		$("#newFolderName").val("");
		$("#newRules").html("");
		$("#newListPages").html("");
	},

	sortNow : function() {
		var subFVs = page.subFVs;
		var subFVArray = new Array();
		for ( var i = 0; i < subFVs.length; i++) {
			subFVArray.push(subFVs[i].fvID);
		}
		$.ajax( {
			url : "FolderController.do?invoke=reArrangeSubFVs&subFVs="+subFVArray,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != "-1") {
					window.frames["FolderTreeIFrame"].refresh();
				}
			}
		});
	},
	
	cancelSort : function() {
		$("#SortFolderDiv").hide();
	},

	downnum : 0,
	upnum : 0,

	onMouseOver: function(fvid) {
		var tbObj = document.getElementById("testTbl");
		var rowObjs = tbObj.rows;
		for (var j = 0; j < rowObjs.length; j++)
		{
			var tmp = rowObjs[j];
			if(tmp.id.substring(0,2) == "tr")
			{
				//其他的tr都是white，现在停留的是#a6caf0
				tmp.style.backgroundColor = "white";
				if(tmp.id == "tr"+fvid)
					tmp.style.backgroundColor = "#CCCCFF";
			}
		}
	    
	},

	onMouseDown : function(fvid) {
		var tbObj = document.getElementById("testTbl");
		var rowObjs = tbObj.rows;
		var num = 0;
		for (var j = 0; j < rowObjs.length; j++)
		{
			var tmp = rowObjs[j];
			if(tmp.id.substring(0,2) == "tr")
			{
				if(tmp.id.substring(2,tmp.id.length) == fvid)
					page.handlers.downnum = num;//记录下当前节点id
				num++;
			}
		}

	    
	},

	onMouseUp : function(fvid) {
		var tbObj = document.getElementById("testTbl");
		var rowObjs = tbObj.rows;
		var num = 0;
		for (var j = 0; j < rowObjs.length; j++)
		{
			var tmp = rowObjs[j];
			if(tmp.id.substring(0,2) == "tr")
			{
				if(tmp.id.substring(2,tmp.id.length) == fvid){
					page.handlers.upnum = num;
				}
				num++;
			}
		}
		//move up action
		if(page.handlers.downnum > page.handlers.upnum)
		{
			var tmpFV= page.subFVs[page.handlers.downnum];
			for(var i = page.handlers.downnum;i > page.handlers.upnum;i--)
			{
				page.subFVs[i] = page.subFVs[i-1];
				
			}
			page.subFVs[page.handlers.upnum] = tmpFV;
		}
		//move down action
		else if(page.handlers.downnum < page.handlers.upnum)
		{
			var tmpFV= page.subFVs[page.handlers.downnum];
			for(var i = page.handlers.downnum; i <page.handlers.upnum;i++)
			{
				page.subFVs[i] = page.subFVs[i+1];
			}
			page.subFVs[page.handlers.upnum] = tmpFV;
		}

		var tdObjs = $("#testTbl .subFVID");
		
		for (var j = 0;j<tdObjs.length;j++)
		{
			var tmp = tdObjs[j];
			tmp.innerHTML = page.subFVs[j].fvID+"&nbsp;";
		}

		tdObjs = $("#testTbl .subFVName");
		
		for (var j = 0;j < tdObjs.length;j++)
		{
			var tmp = tdObjs[j];
			tmp.innerHTML = page.subFVs[j].fvName+"&nbsp;";
		}
	    
	},

	AssignFilters : function(typeStr,group) {
		var docLib = e5dom.getDocLib($("#DocLibList").val());
		e5.dialog( {
			type : 'iframe',
			value : "FilterList.jsp?docTypeID="+docLib.docTypeID+"&type="+typeStr+"&group="+group
		}, {
			id : "AssignFilters",
			title : i18nInfo.AssignFilters,
			width : 450,
			height : 400,
			resizable : true,
			showClose : true
		}).show();
	},

	AssignFiltersOK : function(ret,flag,group) {
		if(ret){
			var selObj = null;
			if(flag == "folder")
				selObj = document.getElementById("newFilters"+group);
			else {
			    selObj = document.getElementById("newViewFilters"+group);
			}
			selObj.innerHTML = "";
			for(var i=0;i<ret.length;i++)
			{
				var op = document.createElement("option");
				op.value = ret[i].value;
				op.text = ret[i].text;
				op.selected = "selected";
				selObj.options.add(op);
			}
		}
	    
	},

	AssignRules : function(typeStr) {		
		var docLib = e5dom.getDocLib($("#DocLibList").val());
		e5.dialog( {
			type : 'iframe',
			value : "RuleList.jsp?docTypeID="+docLib.docTypeID+"&type="+typeStr
		}, {
			id : "AssignRules",
			title : i18nInfo.AssignRules,
			width : 450,
			height : 400,
			resizable : true,
			showClose : true
		}).show();
	},

	AssignRulesOK : function(ret,flag) {
		if(ret){
			var selObj = null;
			if(flag == "folder")
				selObj = document.getElementById("newRules");
			else{
				selObj = document.getElementById("newViewRules");
			}
			selObj.innerHTML = "";
			for(var i=0;i<ret.length;i++)
			{
				var op = document.createElement("option");
				op.value = ret[i].value;
				op.text = ret[i].text;
				op.selected = "selected";
				selObj.options.add(op);
			}
		}
	},
	AssignListPages : function(typeStr) {		
		var docLib = e5dom.getDocLib($("#DocLibList").val());
		e5.dialog( {
			type : 'iframe',
			value : "../e5listpage/FVListPage.do?DocTypeID="+docLib.docTypeID+"&type="+typeStr
		}, {
			id : "AssignListPages",
			title : i18nInfo.AssignListPages,
			width : 450,
			height : 400,
			resizable : true,
			showClose : true
		}).show();
	},

	AssignListPagesOK : function(ret,flag) {
		if(ret){
			var selObj = null;
			if(flag == "folder")
				selObj = document.getElementById("newListPages");
			else{
				selObj = document.getElementById("newViewListPages");
			}
			selObj.innerHTML = "";
			for(var i=0;i<ret.length;i++)
			{
				var op = document.createElement("option");
				op.value = ret[i].value;
				op.text = ret[i].text;
				op.selected = "selected";
				selObj.options.add(op);
			}
		}
	},
	showFolderTree :  function() {
		var theURL = "FolderTree.jsp?DocLibID=" + $("#DocLibList").val();
		$("#FolderTreeIFrame").attr("src", theURL);
		$("#SortFolderDiv").hide();
	},

	mouseOutStyle : function(el) {
		el.style.backgroundColor = 'white';
		el.style.cursor = "default";
	},
		
	mouseOverStyle : function(el) {
		el.style.backgroundColor = '#FFFF99';
		el.style.cursor = "pointer";
	}
}


page.operation = {
	ModifyFolder : function(fvid) {
		$("#curFilterNum").val("0");
		$("#submitBtn").removeAttr("disabled");
		$("#cancelBtn").removeAttr("disabled");
		$("#ShowFolderDiv").hide();
		$("#CreateFolderDiv").show();
		$("#ShowViewDiv").hide();
		$("#ViewFormDiv").hide();
		$("#ViewForm").validationEngine("hideAll");
		$("#SortFolderDiv").hide();
		$("#folderID").val(fvid);
		$("#submitBtn").val(i18nInfo.modifyFolder);
		$("#folderTitle").html(i18nInfo.modifyFolder);
		var folder = null;
		//get Folder
		$.ajax({
			url : "FolderController.do?invoke=getFolder&fvid=" + fvid,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data!=""&&data != "-1") {
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					if(datas!=null&&datas.length>0){
						folder = data[0];
					}
				}
			}
		});
		
//		if(folder.parentID == 0)
//			$("#newFolderName").attr("readOnly", "true");
//		else
//			$("#newFolderName").removeAttr("readOnly");
		//get Filters
		$.ajax({
			url : "FolderController.do?invoke=getFilters&fvid=" + fvid,
			dataType : "json",
			async : false,
			success : function(data) {
				clearFilter("Filter");
				if (data!=""&&data != "-1") {
					var filterList = new Array();
					if (!$.isArray(data)) {
						filterList.push(data);
					} else {
						filterList = data;
					}
					folder.filterList = filterList;
					for(var i=0;i<filterList.length;i++)
					{
						setFVFilters(i, "newFilters" ,"fvFilter", "folder", "Filter2");
					}
					var o = document.getElementsByName("fvFilter");   
					if(!(o!=undefined && o.length>0)){
						$("#curFilterNum").val("0");
					}
					for(var i=0;i<filterList.length;i++)
					{
						for(var j=0;j<filterList[i].length;j++)
						{
							var filterAttr = filterList[i][j];
							var op = document.createElement("option");
							op.text = filterAttr.filterName;
							op.value = filterAttr.filterID;
							op.selected = "selected";
							document.getElementById("newFilters"+(i+1)).options.add(op);
						}
					}
				}
			}
		});
		//get Rules
		$.ajax({
			url : "FolderController.do?invoke=getRules&fvid=" + fvid,
			dataType : "json",
			async : false,
			success : function(data) {
				$("#newRules").html("");
				if (data!=""&&data != "-1") {
					var ruleAttr = new Array();
					if (!$.isArray(data)) {
						ruleAttr.push(data);
					} else {
						ruleAttr = data;
					}
					folder.ruleAttr = ruleAttr;
					for(var i=0;i<ruleAttr.length;i++)
					{
						var op = document.createElement("option");
						op.text = ruleAttr[i].ruleName;
						op.value = ruleAttr[i].ruleID;
						op.selected = "selected";
						document.getElementById("newRules").options.add(op);
					}
				}
			}
		});
		$.ajax({
			url : "FolderController.do?invoke=getListPages&fvid=" + fvid,
			dataType : "json",
			async : false,
			success : function(data) {
				$("#newListPages").html("");
				if (data!=""&&data != "-1") {
					var listPagesAttr = new Array();
					if (!$.isArray(data)) {
						listPagesAttr.push(data);
					} else {
						listPagesAttr = data;
					}
					folder.listPagesAttr = listPagesAttr;
					for(var i=0;i<listPagesAttr.length;i++)
					{
						var op = document.createElement("option");
						op.text = listPagesAttr[i].listName;
						op.value = listPagesAttr[i].listID;
						op.selected = "selected";
						document.getElementById("newListPages").options.add(op);
					}
				}
			}
		});
		$("#parentID").val(folder.parentID);
		$("#newFolderName").val(folder.FVName);
		$("#newFolderName").focus();
		
		if ($("#newKeepDay"))
			$("#newKeepDay").val(folder.keepDay);
	},

	ModifyView : function(fvid) {
		$("#curFilterNum").val("0");
		$("#submitViewBtn").removeAttr("disabled");
		$("#cancelViewBtn").removeAttr("disabled");
		$("#ShowFolderDiv").hide();
		$("#ShowViewDiv").hide();
		$("#CreateFolderDiv").hide();
		$("#CreateFolderForm").validationEngine("hideAll");
		$("#ViewFormDiv").show();
		$("#SortFolderDiv").hide();
		$("#viewID").val(fvid);
		$("#submitViewBtn").click(page.handlers.SubmitMotifyView);
		$("#submitViewBtn").val(i18nInfo.modifyView);
		$("#viewTitle").html(i18nInfo.modifyView);
		var view = null;
		//get Folder
		$.ajax({
			url : "ViewController.do?invoke=getView&fvid=" + fvid,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data!=""&&data != "-1") {
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					if(datas!=null&&datas.length>0){
						view = data[0];
					}
				}
			}
		});
		//get Filters
		$.ajax({
			url : "ViewController.do?invoke=getFilters&fvid=" + fvid,
			dataType : "json",
			async : false,
			success : function(data) {
				clearFilter("view");
				if (data!=""&&data != "-1") {
					var filterList = new Array();
					if (!$.isArray(data)) {
						filterList.push(data);
					} else {
						filterList = data;
					}
					view.filterList = filterList;
					for(var i=0;i<filterList.length;i++)
					{
						setFVFilters(i, "newViewFilters", "viewFVFilter", "view", "view2");
					}
					var o = document.getElementsByName("viewFVFilter");   
					if(!(o!=undefined && o.length>0)){
						$("#curFilterNum").val("0");
					}
					for(var i=0;i<filterList.length;i++)
					{
						for(var j=0;j<filterList[i].length;j++)
						{
							var filterAttr = filterList[i][j];
							var op = document.createElement("option");
							op.text = filterAttr.filterName;
							op.value = filterAttr.filterID;
							op.selected = "selected";
							document.getElementById("newViewFilters"+(i+1)).options.add(op);
						}
					}
				}
			}
		});
		//get Rules
		$.ajax({
			url : "ViewController.do?invoke=getRules&fvid=" + fvid,
			dataType : "json",
			async : false,
			success : function(data) {
				$("#newViewRules").html("");
				if (data!=""&&data != "-1") {
					var ruleAttr = new Array();
					if (!$.isArray(data)) {
						ruleAttr.push(data);
					} else {
						ruleAttr = data;
					}
					view.ruleAttr = ruleAttr;
					for(var i=0;i<ruleAttr.length;i++)
					{
						var op = document.createElement("option");
						op.text = ruleAttr[i].ruleName;
						op.value = ruleAttr[i].ruleID;
						op.selected = "selected";
						document.getElementById("newViewRules").options.add(op);
					}
				}
			}
		});
		$.ajax({
			url : "ViewController.do?invoke=getListPages&fvid=" + fvid,
			dataType : "json",
			async : false,
			success : function(data) {
				$("#newViewListPages").html("");
				if (data!=""&&data != "-1") {
					var listPagesAttr = new Array();
					if (!$.isArray(data)) {
						listPagesAttr.push(data);
					} else {
						listPagesAttr = data;
					}
					view.listPagesAttr = listPagesAttr;
					for(var i=0;i<listPagesAttr.length;i++)
					{
						var op = document.createElement("option");
						op.text = listPagesAttr[i].listName;
						op.value = listPagesAttr[i].listID;
						op.selected = "selected";
						document.getElementById("newViewListPages").options.add(op);
					}
				}
			}
		});
		$("#parentFolderID").val(view.parentID);
		$("#newViewName").val(view.FVName);
		$("#newViewFormula").val(view.viewFormula);
		$("#newViewName").focus();
	},

	DeleteFolder : function(fvid,fvName,parentID) {
		if(parentID == 0){
			alert(i18nInfo.NotAllowDeleteRoot);
			return false;
		}

		if(confirm(i18nInfo.DeleteConfirm1+" "+fvName+" "+i18nInfo.DeleteConfirm2 )){
			$.ajax({
				url : "FolderController.do?invoke=deleteFolder&fvid=" + fvid,
				dataType : "json",
				async : false,
				success : function(data) {
					if (data != "-1") {
						alert(i18nInfo.DeleteOK);
						page.operation.HideAll();
						window.frames["FolderTreeIFrame"].deleteFolderFromTree();
					}else {
					    alert(i18nInfo.DeleteNo);
					}
				}
			});
		}
	},

	DeleteView : function (fvid,fvName) {
		if(confirm(i18nInfo.DeleteViewConfirm1+" "+fvName+" "+i18nInfo.DeleteViewConfirm2 )){
			$.ajax({
				url : "ViewController.do?invoke=deleteView&fvid=" + fvid,
				dataType : "json",
				async : false,
				success : function(data) {
					if (data != "-1") {
						alert(i18nInfo.DeleteOK);
						page.operation.HideAll();
						window.frames["FolderTreeIFrame"].deleteViewFromTree();
					}else {
					    alert(i18nInfo.DeleteNo);
					}
				}
			});
		}
	},

	HideAll : function(){
		$("#ShowFolderDiv").hide();
		$("#ShowViewDiv").hide();
		$("#CreateFolderDiv").hide();
		$("#CreateFolderForm").validationEngine("hideAll");
		$("#ViewFormDiv").hide();
		$("#ViewForm").validationEngine("hideAll");
		$("#SortFolderDiv").hide();
	},

	addNewFolder : function(parentID) {
		$("#submitBtn").removeAttr("disabled");
		$("#cancelBtn").removeAttr("disabled");
		$("#folderID").val("");
		$("#parentID").val(parentID);

		$("#newFolderName").val("");
		$("#newFolderName").removeAttr("readOnly");
		if ($("#newKeepDay")) $("#newKeepDay").val("");
		clearFilter("Filter");
		$("#curFilterNum").val("0");
		setFVFilters(null, 'newFilters' ,'fvFilter', 'folder', 'Filter2');
		$("#newRules").html("");
		$("#newListPages").html("");
		$("#ShowFolderDiv").hide();
		$("#CreateFolderDiv").show();
		$("#ShowViewDiv").hide();
		$("#ViewFormDiv").hide();
		$("#ViewForm").validationEngine("hideAll");
		$("#SortFolderDiv").hide();

		$("#submitBtn").val(i18nInfo.createFolder);
		$("folderTitle").html(i18nInfo.createFolder);
		$("#newFolderName").focus();
	},

	addNewView : function(parentID) {
		$("#submitViewBtn").removeAttr("disabled");
		$("#cancelViewBtn").removeAttr("disabled");
		$("#viewID").val("");
		$("#parentFolderID").val(parentID);

		$("#newViewName").val("");
		$("#newViewFormula").val("");
		clearFilter("view");
		$("#curFilterNum").val("0");
		setFVFilters(null, 'newViewFilters', 'viewFVFilter', 'view', 'view2');
		$("#newViewRules").html("");
		$("#newViewListPages").html("");
		$("#ShowFolderDiv").hide();
		$("#CreateFolderDiv").hide();
		$("#CreateFolderForm").validationEngine("hideAll");
		$("#ShowViewDiv").hide();
		$("#SortFolderDiv").hide();
		$("#ViewFormDiv").show();

		$("#submitViewBtn").val(i18nInfo.createView);
		$("#viewTitle").html(i18nInfo.createView);

		$("#newViewName").focus();
	},

	clickFolder : function(fvid) {
		$("#ShowFolderDiv").show();
		$("#CreateFolderDiv").hide();
		$("#CreateFolderForm").validationEngine("hideAll");
		$("#ViewFormDiv").hide();
		$("#ViewForm").validationEngine("hideAll");
		$("#ShowViewDiv").hide();
		$("#errorDiv").hide();
		$("#errorDiv1").hide();
		$("#SortFolderDiv").hide();
		var folder = null;
		//get Folder
		$.ajax({
			url : "FolderController.do?invoke=getFolder&fvid=" + fvid,
			dataType : "json",
			async : false,
			success : function(data) {
			if (data!=""&&data != "-1") {
				var datas = new Array();
				if (!$.isArray(data)) {
					datas.push(data);
				} else {
					datas = data;
				}
				if(datas!=null&&datas.length>0){
					folder = data[0];
				}
			}
			}
		});
		//get Filters
		$.ajax({
			url : "FolderController.do?invoke=getFilters&fvid=" + fvid,
			dataType : "json",
			async : false,
			success : function(data) {
				clearFilter("FVFiltersList");
				if (data!=""&&data != "-1") {
					var filterList = new Array();
					if (!$.isArray(data)) {
						filterList.push(data);
					} else {
						filterList = data;
					}
					folder.filterList = filterList;
					for(var i=0;i<filterList.length;i++)
					{
						FVFilterLists(i,"FVFilters","FVFiltersList2");
					}
					for(var i=0;i<filterList.length;i++)
					{
						for(var j=0;j<filterList[i].length;j++)
						{
							var op = document.createElement("div");
							op.innerHTML = filterList[i][j].filterName;
							document.getElementById("FVFilters"+(i+1)).appendChild(op);
						}
					}
				}
			}
		});
		//get Rules
		$.ajax({
			url : "FolderController.do?invoke=getRules&fvid=" + fvid,
			dataType : "json",
			async : false,
			success : function(data) {
				$("#FVRules").html("");
				if (data!=""&&data != "-1") {
					var ruleAttr = new Array();
					if (!$.isArray(data)) {
						ruleAttr.push(data);
					} else {
						ruleAttr = data;
					}
					folder.ruleAttr = ruleAttr;
					for(var i=0;i<ruleAttr.length;i++)
					{
						var op = document.createElement("div");
						op.innerHTML = ruleAttr[i].ruleName;
						document.getElementById("FVRules").appendChild(op);
					}
				}
			}
		});
		$.ajax({
			url : "FolderController.do?invoke=getListPages&fvid=" + fvid,
			dataType : "json",
			async : false,
			success : function(data) {
				$("#FVListPages").html("");
				if (data!=""&&data != "-1") {
					var listPagesAttr = new Array();
					if (!$.isArray(data)) {
						listPagesAttr.push(data);
					} else {
						listPagesAttr = data;
					}
					folder.listPagesAttr = listPagesAttr;
					for(var i=0;i<listPagesAttr.length;i++)
					{
						var op = document.createElement("div");
						op.innerHTML = listPagesAttr[i].listName;
						document.getElementById("FVListPages").appendChild(op);
					}
				}
			}
		});
		$("#folder_id").html(folder.FVID);
		$("#folderName").html(folder.FVName);
		if ($("#newKeepDay")) $("#keepDay").html(folder.keepDay);
	},

	nodeClick : function(fvid,flag) {
		if(flag == 1){
			//Folder
			page.operation.clickFolder(fvid);
		}else{
			//View
			page.operation.clickView(fvid);
		}	
	},

	clickView : function(fvid) {

//		alert(fvid);
		$("#ShowFolderDiv").hide();
		$("#CreateFolderDiv").hide();
		$("#CreateFolderForm").validationEngine("hideAll");
		$("#ViewFormDiv").hide();
		$("#ViewForm").validationEngine("hideAll");
		$("#ShowViewDiv").show();
		$("#errorDiv").hide();
		$("#errorDiv1").hide();
		$("#SortFolderDiv").hide();
		var view = null;
		//get Folder
		$.ajax({
			url : "ViewController.do?invoke=getView&fvid=" + fvid,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data!=""&&data != "-1") {
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					if(datas!=null&&datas.length>0){
						view = data[0];
					}
				}
			}
		});
		
		//get Filters
		$.ajax({
			url : "ViewController.do?invoke=getFilters&fvid=" + fvid,
			dataType : "json",
			async : false,
			success : function(data) {
				clearFilter("viewList");
				if (data!=""&&data != "-1") {

					var filterList = new Array();
					if (!$.isArray(data)) {
						filterList.push(data);
					} else {
						filterList = data;
					}
					view.filterList = filterList;
					for(var i=0;i<filterList.length;i++)
					{
						FVFilterLists(i,"ViewFVFilters","viewList2");
					}
					for(var i=0;i<filterList.length;i++)
					{
						for(var j=0;j<filterList[i].length;j++)
						{
							var op = document.createElement("div");
							op.innerHTML = filterList[i][j].filterName;
							document.getElementById("ViewFVFilters"+(i+1)).appendChild(op);
						}
					}
				}
			}
		});
		//get Rules
		$.ajax({
			url : "FolderController.do?invoke=getRules&fvid=" + fvid,
			dataType : "json",
			async : false,
			success : function(data) {
				$("#ViewFVRules").html("");
				if (data!=""&&data != "-1") {
					var ruleAttr = new Array();
					if (!$.isArray(data)) {
						ruleAttr.push(data);
					} else {
						ruleAttr = data;
					}
					view.ruleAttr = ruleAttr;
					for(var i=0;i<ruleAttr.length;i++)
					{
						var op = document.createElement("div");
						op.innerHTML = ruleAttr[i].ruleName;
						document.getElementById("ViewFVRules").appendChild(op);
					}
				}
			}
		});
		$.ajax({
			url : "FolderController.do?invoke=getListPages&fvid=" + fvid,
			dataType : "json",
			async : false,
			success : function(data) {
				$("#ViewFVListPages").html("");
				if (data!=""&&data != "-1") {
					var listPagesAttr = new Array();
					if (!$.isArray(data)) {
						listPagesAttr.push(data);
					} else {
						listPagesAttr = data;
					}
					view.listPagesAttr = listPagesAttr;
					for(var i=0;i<listPagesAttr.length;i++)
					{
						var op = document.createElement("div");
						op.innerHTML = listPagesAttr[i].listName;
						document.getElementById("ViewFVListPages").appendChild(op);
					}
				}
			}
		});
		$("#view_id").html(view.FVID);
		$("#viewName").html(view.FVName);
		$("#ViewFormula").html(view.viewFormula);
	},

	sortFolders : function(parentID) {

		$("#ShowFolderDiv").hide();
		$("#CreateFolderDiv").hide();
		$("#CreateFolderForm").validationEngine("hideAll");
		$("#ShowViewDiv").hide();
		$("#ViewFormDiv").hide();
		$("#ViewForm").validationEngine("hideAll");
		$("#SortFolderDiv").show();
		$.ajax({
			url : "FolderController.do?invoke=getSubFVs&parentID=" + parentID,
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
						$(".fieldsRemove").remove();
						page.subFVs = new Array();
						$.each(datas,function(i, fv){
							page.subFVs[i] = new FolderView(fv.FVID,fv.FVName);
							var trHtml = "";
							trHtml += "<TR class=\"fieldsRemove\" id=\"tr"+fv.FVID+"\" bgcolor=\"#FFFFF6\" onmouseover=\"page.handlers.onMouseOver("+fv.FVID+");\" onmousedown=\"page.handlers.onMouseDown("+fv.FVID+");\" onmouseup=\"page.handlers.onMouseUp("+fv.FVID+");\">";
							trHtml += "<TD align=\"center\" class=\"subFVID\" fvid=\""+fv.FVID+"\">"+fv.FVID+"</TD>";
							trHtml += "<TD class=\"subFVName\" fvName=\""+fv.FVName+"\">"+fv.FVName+"</TD></TR>";
							$("#testTbl").append(trHtml);
						});
						var btnHtml = "";
						btnHtml += "<tr class=\"fieldsRemove\">";
						btnHtml += "<td colspan=\"2\" align=\"center\">";
						btnHtml += "<div align=\"center\">";
						btnHtml += "<input class=\"button\" onclick=\"page.handlers.sortNow();\" type=\"button\" value=\""+i18nInfo.SortNow+"\">&nbsp;";
						btnHtml += "<input class=\"button\" onclick=\"page.handlers.cancelSort();\" type=\"button\" value=\""+i18nInfo.Cancel+"\">";
						btnHtml += "</div>";
						btnHtml += "</td></tr>";
						$("#testTbl").append(btnHtml);
					}
				}
			}
		});
	},
	SubmitCreateFolder : function() {
		var parentID = $("#parentID").val();
		var folderName = $("#newFolderName").val();
		var keepDay = $("#newKeepDay").val();
		var fvRules = $("#newRules").val();
		var fvListPages =  $("#newListPages").val();
		var fvFilters = assembleSelect("fvFilter");
		var url = "FolderController.do?invoke=createFolder&parentID="+parentID+
				"&folderName="+encodeURI(folderName)+
				"&keepDay="+keepDay+
				"&fvFilters="+encodeURI(fvFilters);
		if(fvRules!=null){
			url += "&fvRules="+encodeURI(fvRules);
		}
		if(fvListPages!=null){
			url += "&fvListPages="+encodeURI(fvListPages);
		}
		$.ajax({
			url : url,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != "-1") {
					alert(i18nInfo.CreateOK);
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					window.frames["FolderTreeIFrame"].addFolderToTree(datas[0]);
					$("#submitBtn").removeAttr("disabled");
					$("#cancelBtn").removeAttr("disabled");
				}
			}
		});
	},
	
	SubmitMotifyFolder : function() {
		var folderID = $("#folderID").val();
		var parentID = $("#parentID").val();
		var folderName = $("#newFolderName").val();
		var keepDay = $("#newKeepDay").val();
		var fvRules = $("#newRules").val();
		var fvListPages = $("#newListPages").val();
		var fvFilters = assembleSelect("fvFilter");
		var url = "FolderController.do?invoke=updateFolder&parentID="+parentID+
				"&folderName=" + encodeURI(folderName) +
				"&keepDay=" + keepDay +
				"&fvFilters=" + encodeURI(fvFilters) +
				"&folderID=" + folderID;
		if(fvRules!=null){
			url += "&fvRules=" + encodeURI(fvRules);
		}
		if(fvListPages!=null){
			url += "&fvListPages=" + encodeURI(fvListPages);
		}
		$.ajax({
			url : url,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != "-1") {
					alert(i18nInfo.ModifyOK);
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					window.frames["FolderTreeIFrame"].updateFolderToTree(datas[0]);
					$("#submitBtn").removeAttr("disabled");
					$("#cancelBtn").removeAttr("disabled");
				}
			}
		});
	},
	
	SubmitCreateView : function () {
		var parentFolderID = $("#parentFolderID").val();
		var viewName = $("#newViewName").val();
		var viewFormula = $("#newViewFormula").val();
		var viewFVRules = $("#newViewRules").val();
		var viewFVListPages = $("#newViewListPages").val();
		var viewFVFilters = assembleSelect("viewFVFilter");
		var url = "ViewController.do?invoke=createView&parentFolderID="+parentFolderID+
				"&viewName="+encodeURI(viewName)+
				"&viewFormula="+encodeURI(viewFormula)+
				"&viewFVFilters="+encodeURI(viewFVFilters);
		if(viewFVRules!=null){
			url += "&viewFVRules="+encodeURI(viewFVRules);
		}
		if(viewFVListPages!=null){
			url += "&viewFVListPages="+encodeURI(viewFVListPages);
		}
		$.ajax({
			url : url,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != "-1") {
					alert(i18nInfo.CreateOK);
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					window.frames["FolderTreeIFrame"].addViewToTree(datas[0]);
					$("#submitViewBtn").removeAttr("disabled");
					$("#cancelViewBtn").removeAttr("disabled");
				}
			}
		});
	},
	
	SubmitMotifyView : function() {	
		var viewID = $("#viewID").val();
		var parentFolderID = $("#parentFolderID").val();
		var viewName = $("#newViewName").val();
		var viewFormula = $("#newViewFormula").val();
		var viewFVRules = $("#newViewRules").val();
		var viewFVListPages = $("#newViewListPages").val();
		var viewFVFilters = assembleSelect("viewFVFilter");
		var url = "ViewController.do?invoke=updateView&parentFolderID="+parentFolderID+
				"&viewName="+encodeURI(viewName)+
				"&viewFormula="+encodeURI(viewFormula)+
				"&viewFVFilters="+encodeURI(viewFVFilters)+
				"&fvid="+viewID;
		if(viewFVRules!=null){
			url += "&viewFVRules="+encodeURI(viewFVRules);
		}
		if(viewFVListPages!=null){
			url += "&viewFVListPages="+encodeURI(viewFVListPages);
		}
		$.ajax({
			url : url,
			dataType : "json",
			async : false,
			success : function(data) {
				if (data != "-1") {
					alert(i18nInfo.ModifyOK);
					var datas = new Array();
					if (!$.isArray(data)) {
						datas.push(data);
					} else {
						datas = data;
					}
					window.frames["FolderTreeIFrame"].updateViewToTree(datas[0]);
					$("#submitViewBtn").removeAttr("disabled");
					$("#cancelViewBtn").removeAttr("disabled");
				}
			}
		});
	}
	
}

window.onload = page.loaded;

//重新点击新增或修改文件夹时清空页面
function clearFilter(idName){
	var Filter1 = document.getElementById(idName+"1");
	var nextFilter = Filter1.nextSibling;
	while(!(nextFilter.nodeType==1)){
		nextFilter=nextFilter.nextSibling;
	}
	if((idName+"2")==nextFilter.id){
		return;
	}else{
		$("#"+nextFilter.id).remove();
		this.clearFilter(idName);
	}
}
//增加一组过滤器
function setFVFilters(i,id,name,value,beforeName){
	var curFilterNum= $("#curFilterNum").val();
	if(i!=undefined&&i!=null&&i.toString()!=""){
		curFilterNum = i.toString();
	}
	var newFilterNum = (parseInt(curFilterNum)+1);
	var trHTML = '<tr id="tr_'+newFilterNum+'">'+
				'<th align="center" class="labelTD">'+
				i18nInfo.FVFilters+'.'+newFilterNum+'</th>'+
				'<td><select class="field" id="'+id+''+newFilterNum+'" name="'+name+'" multiple="multiple" size="5"></SELECT>&nbsp;'+
				'<input class="bluebutton" type="button" onclick="page.handlers.AssignFilters(\''+value+'\','+newFilterNum+');" value="'+
				i18nInfo.AssignFilters+
				'">&nbsp;'+
				'<input class="bluebutton" type="button" onclick="delFilters(this);" value="'+
				i18nInfo.DelFilters+
				'"></td></tr>';
	$("#curFilterNum").val(newFilterNum);
	$("#"+beforeName).before(trHTML);//在table最后面添加一行
}

//删除过滤器组
function delFilters(obj){
	if(confirm(i18nInfo.confirm1 + i18nInfo.confirm2))
	{
		$(obj).parents("tr").remove();
	}
}
//增加一组过滤器
function FVFilterLists(i,id,beforeName){
	var curFilterNum= jQuery("#curFilterNum").val();
	if(i!=undefined&&i!=null&&i.toString()!=""){
		curFilterNum = i.toString();
	}
	var newFilterNum = (parseInt(curFilterNum)+1);
	var trHTML = '<tr id="trList_'+newFilterNum+'">'+
			'<th align="right" class="labelTD">'+i18nInfo.FVFilters+'.'+newFilterNum+'</th>'+
			'<td>'+
			'<div id="'+id+newFilterNum+'"></div>'+
			'</td>'+
			'</tr>';
	$("#curFilterNum").val(newFilterNum);
	$("#"+beforeName).before(trHTML);//在table最后面添加一行
}
function assembleSelect(name){
	var array = new Array();
	var o = document.getElementsByName(name);         
	for(var i=0;i<o.length;i++){   
		var intvalue="";
		var ob = o[i];
		for(var j=0;j<ob.length;j++){
			if(ob.options[j].selected){
				intvalue+=ob.options[j].value+"-";
			}					
		}
		array.push(intvalue.substr(0,intvalue.length-1));
	}
	return array;
}