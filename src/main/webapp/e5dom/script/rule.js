var ruleMenu = new WebFXMenu();
ruleMenu.width = 150;

ruleMenu.add(new WebFXMenuItem(i18nInfo.newRule, "page.operation.CreateRule();", i18nInfo.newRule));
ruleMenu.add(new WebFXMenuItem(i18nInfo.modifyRule, "page.operation.ModifyRule();", i18nInfo.modifyRule));
ruleMenu.add(new WebFXMenuItem(i18nInfo.deleteRule, "page.operation.DeleteRule();", i18nInfo.deleteRule));

ruleMenu.generate(); 

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

		$("#CreateRule").click(page.operation.CreateRule);
		// $("#CreateRule").mouseout(page.handlers.mouseOutCreateRule);
		// $("#CreateRule").mouseover(page.handlers.mouseOverCreateRule);
	}

}

page.handlers = {

	resetRuleForm : function() {
		$("#ruleName").val("");
		$("#ruleDesc").val("");
		$("#ruleClassName").val("");
		$("#ruleMethod").val("");
		$("#ruleArguments").val("");

	},

	mouseOutCreateRule : function(evt) {
		var el = e5dom.util.srcEventElement(evt);
		el.style.textDecoration = "none";
		el.style.cursor = "default";
	},

	mouseOverCreateRule : function(evt) {
		var el = e5dom.util.srcEventElement(evt);
		el.style.textDecoration = "underline";
		el.style.cursor = "pointer";    
	},

	SubmitCreateRule : function () {
		var docTypeID = $("#docTypeID").val();
		var ruleName = $("#ruleName").val();
		var description = $("#ruleDesc").val();
		var ruleClassName = $("#ruleClassName").val();
		var ruleMethod = $("#ruleMethod").val();
		var ruleArguments = $("#ruleArguments").val();
		var url = "RuleController.do?invoke=createRule&docTypeID="+docTypeID+
				"&ruleName="+encodeURI(ruleName)+
				"&description="+encodeURI(description)+
				"&ruleClassName="+encodeURI(ruleClassName)+
				"&ruleMethod="+ruleMethod+
				"&ruleArguments="+encodeURI(ruleArguments);
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
	
	SubmitModifyRule : function () {
		var ruleID = $("#ruleID").val();
		var docTypeID = $("#docTypeID").val();
		var ruleName = $("#ruleName").val();
		var description = $("#ruleDesc").val();
		var ruleClassName = $("#ruleClassName").val();
		var ruleMethod = $("#ruleMethod").val();
		var ruleArguments = $("#ruleArguments").val();
		var url = "RuleController.do?invoke=updateRule&docTypeID="+docTypeID+
				"&ruleName="+encodeURI(ruleName)+
				"&description="+encodeURI(description)+
				"&ruleClassName="+encodeURI(ruleClassName)+
				"&ruleMethod="+ruleMethod+
				"&ruleArguments="+encodeURI(ruleArguments)+
				"&ruleID="+ruleID;
		$.ajax({
			url : url,
			dataType : "json",
			async : false,
			success : function(data) {
				if(data == "1"){
					alert(i18nInfo.modifyOK);
					page.operation.chooseDocType();
					page.operation.chooseRule(ruleID);
	
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

	CreateRule : function(e) {
		$("#createBtn").removeAttr("disabled");
		$("#cancelBtn").removeAttr("disabled");
		$("#RuleProps").attr("style", "display:none");
		$("#FormDiv").attr("style", "display:block");
		$("#docTypeID").val(e5dom.currentDocType.docTypeID);
		$("#ruleID").val("");
		$("#ruleName").val("");
		$("#ruleDesc").val("");
		$("#ruleClassName").val("");
		$("#ruleMethod").val("");
		$("#ruleArguments").val("");

		$("#createBtn").show();
		$("#modifyBtn").hide();
		e.preventDefault();
		e.stopPropagation();
	},

	ModifyRule : function() {
		$("#modifyBtn").removeAttr("disabled");
		$("#cancelBtn").removeAttr("disabled");
		//show
		var filertID = parseInt(ruleMenu.ruleID);
		$.ajax({
			url : "RuleController.do?invoke=getRule&ruleID="+filertID,
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
						var rule = data[0];
						$("#ruleName").val(rule.ruleName);
						$("#ruleDesc").val(rule.description);
						$("#ruleClassName").val(rule.ruleClassName);
						$("#ruleMethod").val(rule.ruleMethod);
						$("#ruleArguments").val(rule.ruleArguments);
						$("#ruleID").val(ruleMenu.ruleID);

						$("#RuleProps").attr("style", "display:none");
						$("#FormDiv").attr("style", "display:block");
						$("#createBtn").hide();
						$("#modifyBtn").show();
					}
					
				}
			}
		});
	},

	DeleteRule : function() {
		var ruleid = parseInt(ruleMenu.ruleID);
		var ruleName = ruleMenu.ruleName;
		if(confirm(i18nInfo.confirm1+" "+ruleName+" "+i18nInfo.confirm2))
		{
			$.ajax({
				url : "RuleController.do?invoke=deleteRule&ruleID="+ruleid,
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

	showContextMenu : function (el,ruleName) {
		ruleMenu.ruleID = el.id;
		ruleMenu.ruleName = ruleName;
		webFXMenuHandler.showMenu(ruleMenu, el);
	},

	chooseDocType : function(){
		var docTypeID = $("#DocTypeList").val();
		var docType = e5dom.getDocType(docTypeID);
		e5dom.currentDocType = docType;
		page.operation.showRuleList();
		$("#FormDiv").attr("style", "display:none");
		$("#FormDiv").validationEngine("hideAll");
		$("#RuleProps").attr("style", "display:none");
	},
	
	showRuleList : function () {
		$(".fieldsRemove").remove();
		$.ajax({
			url : "RuleController.do?invoke=getRules&docTypeID="+e5dom.currentDocType.docTypeID,
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
						$.each(datas,function(i, rule){
							var trHtml = "";
							trHtml += "<TR class='fieldsRemove' title=\""+i18nInfo.RightClick+"\" id=\""+rule.ruleID+"\" onmouseover=\"this.bgColor='#E4E8EB';\" onmouseout=\"this.bgColor='#ffffff';\"";
							trHtml += "onclick=\"page.operation.chooseRule(this.id);\" oncontextmenu=\"page.operation.showContextMenu(this,'"+rule.ruleName+"');return false;\">";
							trHtml += "<TD align=\"center\">"+rule.ruleName+"</TD></TR>";
							$("#tab1").append(trHtml);
						});
					}
				}
			}
		});
	},

	chooseRule : function (ruleid) {
		var filertID = parseInt(ruleid);
		$.ajax({
			url : "RuleController.do?invoke=getRule&ruleID="+filertID,
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
						var rule = data[0];
						$("#RuleNameSpan").html(rule.ruleName);
						$("#RuleDescSpan").html(rule.description);
						$("#RuleClassNameSpan").html(rule.ruleClassName);
						$("#RuleMethodSpan").html(rule.ruleMethod);
						$("#RuleArgumentsSpan").html(rule.ruleArguments);
						var temp = "";
						for(var i=0;i<rule.fvs.length;i++)
						{
							temp += "<div>"+ rule.fvs[i].FVID +" - "+ rule.fvs[i].FVName + " </div> ";
						}
						$("#RuleFoldersSpan").html(temp);
						$("#FormDiv").attr("style", "display:none");
						$("#FormDiv").validationEngine("hideAll");
						$("#RuleProps").attr("style", "display:block");
						$("#errorDiv").hide();
					}
				}
			}
		});
	}
}

window.onload = page.loaded;