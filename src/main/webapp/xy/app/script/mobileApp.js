/**
 * 移动应用
 */
var mobile_app = {
	maId:"",
	api:"",	
	init : function(sandbox) {
		api = sandbox;
		$("#btnAdd").click(mobile_app.appAdd);
		$("#btnDelete").click(mobile_app.doDelete);
		mobile_app.defaultClick();
	},
	appAdd : function() {
		var url = "../e5workspace/manoeuvre/Form.do?code=mobileAppAdd&new=1"
		+ "&DocLibID=" + main_param.docLibID
		+ "&siteID=" + main_param.siteID
		+ "&parentID=0";
		
		mobile_app.dialog = e5.dialog({type:"iframe", value:url}, 
				{title:"增加应用", width:"520px", height:"300px",resizable:false});
		mobile_app.dialog.show();
	},
	doDelete : function(){
			if(confirm("确定删除？")){
				//alert($("div[name='applist'][class*='select']").attr("appID"));
				$.ajax({
					async: false,
					url : "../xy/mobileAppDelete.do",
					type : 'POST',
					data:{
						"id":$("div[name='applist'][class*='select']").attr("appID")
					},
					dataType : 'json',
					success : function(data, status) {
						window.location.reload();
					},
					error : function(xhr, textStatus, errorThrown) {
						alert("对不起，通信出现异常！无法保存！");
					}
				});
		
			}
	},
	treeClick : function(evt) {
		$("#appUl li div").removeClass("select");
		$(this).addClass("select");
		var treeC = e5.mods["workspace.resourcetree"].treeClick;
		treeC($(evt.target).attr("appID"));
		
		//ios隐藏渠道列 
		var channelIndex = $('#tablePinHeader').find('tbody:eq(0) tr:eq(0)').children().index($("#TH_mp_channel"));
			if($(evt.target).attr("apptype")=="0"){
			$("#TH_mp_channel").hide();
			$('#listing table tbody tr').find('td:eq('+channelIndex+')').hide();
		}else{
			$("#TH_mp_channel").show();
			$('#listing table tbody tr').find('td:eq('+channelIndex+')').show();
		}
	},
	defaultClick : function() {
		var statusReady = e5.mods["workspace.doclistMain"].isReady;
		var searchReady = e5.mods["workspace.search"].isReady;
		var ready = !!statusReady && !!searchReady && statusReady() && searchReady();
		if (!ready) {
			setTimeout(mobile_app.defaultClick, 100);
			return;
		}

		$(".group").find("Div").click(mobile_app.treeClick);
		$(".group").find("Div").first().click();
	}
};
e5.mod("workspace.resourcetree",function() {
	var maId = 8;
	var api;
	var treeClick = function(appId) {

		mobile_app.maId=appId;
		var param = new ResourceParam();
		
		for ( var name in main_param){
			param[name] = main_param[name];
		}
		var groupID = appId;
		//var groupID = $(this).attr("groupID");
		param.groupID = groupID;
		param.ruleFormula = "mp_maId_EQ_" + groupID;
		// 当用户点击列表的时候，给隐藏域赋值，以便于修改时知道当前选择的是哪个组
		api.broadcast("resourceTopic", param);
	};
	
	var init = function(sandbox) {
		api = sandbox;
	}
	return {
		init: init,
		treeClick : treeClick
	}
});
$(function() {
	mobile_app.init();
});