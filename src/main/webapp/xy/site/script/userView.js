var user_form = {
	init : function() {
		var docLibID = user_form.getParam("DocLibID");
		
		var siteDatas;
		var url = "../xy/user/Site.do?DocLibID=" + docLibID;
		$.ajax({url : url, dataType: "json", async: false, success: function(datas) {
			siteDatas = datas;
		}});
		
		var roleDatas;
		var url = "../xy/user/Role.do?DocLibID=" + docLibID;
		$.ajax({url : url, dataType: "json", async: false, success: function(datas) {
			roleDatas = datas;
		}});
		
		//修改用户信息的时候，初始化站点角色文字信息
		var str = $("#u_siteRoleIDs").html();
		if (!str) return;
		
		var datas = eval(str);
		var result = "";
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
				
				result += siteName + "——" + roleName + "<br/>";
			}
		}
		$("#u_siteRoleIDs").html(result);
	},
	_find : function(datas, key) {
		for (var j = 0; j < datas.length; j++){
			if (datas[j].key == key){
				return datas[j].value;
			}
		}
		return null;
	},
	getParam : function(name) {
		var params = window.location.href;
		
		var start = params.indexOf(name + "=");
		if (start < 0) return "";
		
		start += name.length + 1;
		var end = params.indexOf("&", start);
		
		if (end > 0) {
			return params.substring(start, end);
		} else {
			return params.substring(start);
		}
	}
	
}
$(function() {
	user_form.init();
});
