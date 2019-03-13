//改变e5表单的查重逻辑，账号全局检查，姓名本站点内检查
e5_form._duplicateUrl = function(field) {
	var id = field.attr("id");
	if (id == "u_code") {
		return "xy/tenant/Exist.do?code=" + e5_form.encode(field.val());
	} else {
		var theURL = "xy/Duplicate.do"
			+ "?DocLibID=" + $("#DocLibID").val()
			+ "&DocIDs=" + $("#DocID").val()
			+ "&siteID=" + e5_form.getParam("siteID")
			+ "&value=" + e5_form.encode(field.val());
		return theURL;
	}
}
//提交保存前做检查
e5_form.event.doSave = function() {
	//非空
	$("#u_name").val($("#u_name").val().trim());
	if ($("#u_code").length > 0)
		$("#u_code").val($("#u_code").val().trim());
	if ($("#u_password").length > 0)
		$("#u_password").val($("#u_password").val().trim());
	
	if (!$("#form").validationEngine("validate")) {
		// 验证提示
		$("#form").validationEngine("updatePromptsPosition");
		return false;
	}
	
	if (e5_form.file.notImgFile("u_icon")){
		alert("请选择jpg, png, gif类型的文件");
		return false;
	}
	
	user_form.setHiddenData();
	
	var roles = $("#u_siteRoleIDs").val();
	if (!roles || roles == "[]") {
		$("#roleID")[0].scrollIntoView(false);
		alert("请设置角色");
		return false;
	}
	
	window.onbeforeunload = null;
		
	//若有附件，则先提交
	if (!e5_form.file.upload()) {
		return false;
	}
}

var user_form = {
	maxRoleSize : 100,
	orgDialog : null,
	
	init : function() {
		//修改时显示账号密码
		$("#u_password")[0].type = 'password';
		if ($("#DocID").val() != "0") {
			$("#SPAN_u_code").parent().hide();
			
			var url = "../../xy/user/Pwd.do";
			$.post(url, {id:$("#DocID").val()}, function(data) {
				/*debugger;
				var stars ="";
				var pwdlength=data.length;
				for(var a=0;a<pwdlength;a++){
					stars+="*";
				}*/
				$("#u_password").val(data);
			});
		}
		$("#u_org").attr("readonly", true);
		$("#btnOrg").click(user_form.orgSelect);
		$('#contentDiv').bind("selectstart", function() {return false;});

		// 修改表单的提交功能
		$("#form").attr("target", "iframe");
		$("#form").attr("action", "../../xy/user/FormSubmit.do");
		$("#u_code").addClass("validate[custom[onlyLetterNumber]]");
		$("#u_password").addClass("validate[minSize[8]],validate[custom[strongpass]]");
		//$("#u_password").addClass("");
		//$("#u_password").attr("type","password");
		
/*		if (!$("#u_siteID").val()) {
			var siteID = e5_form.getParam("siteID");
			if (!siteID) siteID = 1;
			$("#u_siteID").val(siteID);
		}*/
		
		user_form.initSelects();
		
		//显示头像
		user_form.showIcon();
		
		//新建时读部门名
		if ($("#DocID").val() == "0") {
			var groupID = e5_form.getParam("groupID");
			if (groupID) {
				user_form.setOrg(groupID);
			}
		}
	},
	setOrg : function(groupID) {
		$("#u_orgID").val(groupID);
		var url = "../../xy/user/OrgName.do?orgID=" + groupID;
		$.ajax({url : url, async: true, success: function(datas) {
			$("#u_org").val(datas);
		}});
	},

	//显示头像
	showIcon : function() {
		var url = $("#u_icon").attr("oldvalue");
		if (url && url != "-") {
			url = "../../xy/image.do?path=" + url;
			$("#iconImg").attr("src", url);
			$("#iconImg").css("max-width", 150);
			$("#iconImg").css("max-height", 150);
			
			$("#img_u_icon").hide();
		} else {
			$("#iconImg").hide();
		}
		$("#labelIconImg").hide();
	},
	orgSelect : function(e) {
		var dataUrl = "../../xy/site/UserOrgTree.jsp?sel=1&auth=&siteID=" + e5_form.getParam("siteID");
		user_form.orgDialog = e5.dialog({
			type : "iframe",
			value : dataUrl
		}, {
			showTitle : false,
			width : "230px",
			height : "400px",
			//pos : pos,
			resizable : false,
			esc:true
		});
		user_form.orgDialog.show();
	},
	//站点、角色下拉框初始化
	initSelects : function() {
		var siteDatas;
		var url = "../../xy/user/Site.do?DocLibID=" + e5_form.getParam("DocLibID");
		$.ajax({url : url, dataType: "json", async: false, success: function(datas) {
			user_form._initSelect("siteID", datas);
			siteDatas = datas;
		}});
		
		var roleDatas;
		var url = "../../xy/user/Role.do?DocLibID=" + e5_form.getParam("DocLibID");
		$.ajax({url : url, dataType: "json", async: false, success: function(datas) {
			user_form._initSelect("roleID", datas);
			roleDatas = datas;
		}});
		
		//修改用户信息的时候，初始化站点角色文字信息
		user_form.initSiteRoleText(siteDatas, roleDatas);
	},
	_initSelect : function(id, datas) {
		if (datas){
			var sel = document.getElementById(id);
			for (var i = 0; i < datas.length; i++) {
				var op = document.createElement("OPTION");
				op.value = datas[i].key;
				op.text = datas[i].value;
				sel.options.add(op);
			}
		}
	},
	initSiteRoleText : function(siteDatas, roleDatas){
		var str = $("#u_siteRoleIDs").val().trim();
		if (!str) return;
		
		var datas = eval(str);
		for (var i= 0;i < datas.length; i++){
			var data = datas[i];
			
			var siteID = data.k;
			var siteName = user_form._find(siteDatas, siteID);
			if (!siteName) continue;
			
			var roleIDs = data.v;
			for (var m = 0; m < roleIDs.length; m++){
				var roleID = roleIDs[m];
				var roleName = user_form._find(roleDatas, roleID);
				if (!roleName) continue;
				
				user_form.addOneSiteRole(siteID, roleID, siteName, roleName);	
			}
		}
	},
	_find : function(datas, key) {
		for (var j = 0; j < datas.length; j++){
			if (datas[j].key == key){
				return datas[j].value;
			}
		}
		return null;
	},
	
	//在下拉框下方添加站点和角色
	addOneSiteRole : function(siteID,roleID,siteName,roleName) {
		var content = "<span class='siteSpan'>" + siteName + "</span>"
			+ "<span class='roleSpan'>"+roleName+"</span>";
		var btnDelete = $("<span/>")
			.attr("class", "handleSpan")
            .attr("title", "删除")
			.click(user_form.oneDelete)
			.html("X");
		var  btnMoveToTop = $("<span/>")
            .attr("class", "topSpan")
            .attr("title", "置顶")
            .click(user_form.moveToTop)
            .html("↑");
		var oneSiteRole = $("<span/>")
			.attr("siteID", siteID)
			.attr("roleID", roleID)
			.attr("class", "rowSpan")
			.attr("title", "拖拽改变顺序")
			.html(content)
			.mousedown(user_form.doMouseDown)
			.mouseup(user_form.doMouseUp);
		
		oneSiteRole.append(btnDelete);
        oneSiteRole.append(btnMoveToTop);
		$("#contentDiv").append(oneSiteRole);
	},
	//删除一个站点角色
	oneDelete : function(evt){
		//(1)删除文字
		var src = $(evt.target);
		src.parent().remove();
	},
    // 置顶角色
    moveToTop: function(evt){
        var obj = $(evt.target).parent();
		var _first = obj.parent().children().first();
		evt.stopPropagation();
        //若本身在第一行，则不处理
		if(_first.attr("siteID") == obj.attr("siteID") && _first.attr("roleID") == obj.attr("roleID"))
			return;

        //remove后事件丢失，再加上
        obj.mousedown(user_form.doMouseDown)
            .mouseup(user_form.doMouseUp);
        obj.find(".handleSpan")
            .click(user_form.oneDelete);
        obj.find(".topSpan")
            .click(user_form.moveToTop);

        _first.before(obj);
        $("#contentDiv").scrollTop(0);
    },
	//拖动改变顺序
	fromObj : null,
	doMouseDown : function(evt){
		var obj = $(evt.target);
		if (!obj.attr("siteID")) obj = obj.parent();
			
		user_form.fromObj = obj;
		
	},
	doMouseUp : function(evt){
		var fromObj  = user_form.fromObj;
		if (!fromObj) return;
		
		var toObj = $(evt.target);
		if (!toObj.attr("siteID")) toObj = toObj.parent();
		
		//同一行，则不处理
		if (fromObj.attr("siteID") == toObj.attr("siteID")
			&& fromObj.attr("roleID") == toObj.attr("roleID")) return;
		
		//remove后事件丢失，再加上
		fromObj.mousedown(user_form.doMouseDown)
			.mouseup(user_form.doMouseUp);
		fromObj.find(".handleSpan")
			.click(user_form.oneDelete);
        fromObj.find(".topSpan")
            .click(user_form.moveToTop);
			
		toObj.before(fromObj);
		
		user_form.fromObj = null;
	},
	//保存前收集数据
	setHiddenData : function() {
		//收集所有的站点角色
		var spans = $("#contentDiv").find("span[siteID][roleID]");
		var datas = [];
		for (var i = 0; i < spans.length; i++) {
			var span = $(spans[i]);
			var siteID = span.attr("siteID");
			var roleID = span.attr("roleID");
			
			user_form._addData(datas, siteID, roleID);
		}
		//设置用户所属站点，按照页面所在站点/第一个用户角色的站点/用户之前设置的站点/缺省设置为站点1
		siteID = e5_form.getParam("siteID");
		if (!siteID)
			siteID = $(spans[0]).attr("siteID");
		if (!siteID)
			siteID =1;
		$("#u_siteID").val(siteID);

		//sites : ,1,2,3,
		var sites = ",";
		for (var i = 0; i < datas.length; i++) {
			sites += datas[i].k + ",";
		}
		var siteRoles = (datas.length == 0) ? "" : JSON.stringify(datas);
		
		$("#u_siteRoleIDs").val(siteRoles);
		$("#u_siteIDs").val(sites);
	},
	//收集一条记录
	_addData : function(datas, siteID, roleID){
		for (var i = 0; i < datas.length; i++) {
			if (datas[i].k == siteID) {
				//找到站点记录了，把角色ID加进去
				datas[i].v.push(roleID);
				return;
			}
		}
		//没找到，新的站点记录
		var one = {"k":siteID,"v":[roleID]};
		datas.push(one);
	}
}
$(function() {
	user_form.init();
});

//点击添加按钮。添加一行数据（站点+角色）
function addSiteRole(){
	var siteID = $("#siteID").val();
	var roleID = $("#roleID").val();

	var span = $("#contentDiv").find("span[siteID]");
	if (span.length > user_form.maxRoleSize){
		alert("角色不要这么多啦！");
		return;
	}
	//判断是否已有站点+角色
	var span = $("#contentDiv").find("span[siteID='" + siteID + "'][roleID='" + roleID + "']");
	if (span.length > 0){
		alert("已有此角色");
	} else {
		//添加文字
		var roleName = $("#roleID").find("option:selected").text();
		var siteName = $("#siteID").find("option:selected").text();
		user_form.addOneSiteRole(siteID,roleID,siteName,roleName);
	}
}
function orgCancel() {
	if (user_form.orgDialog) 
		user_form.orgDialog.close();
}
function orgOK(orgID) {
	user_form.setOrg(orgID);
	orgCancel();
}