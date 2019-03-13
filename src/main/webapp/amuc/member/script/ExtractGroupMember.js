//订阅列表事件，用于在表单中显示列表的门店信息
e5.mod("op.em",function() {
	var api,
	listening = function(msgName, callerId, param) {
		select(param);
	},
	//获取列表选中数据的docIDs
	select = function(param) {
		var docLibID = param.docLibIDs;
		var docIDs = param.docIDs;
		$("#docIDs").val(docIDs);
	};
	//-----init & onload--------
	var init = function(sandbox){
		api = sandbox;
		api.listen("workspace.doclist:doclistTopic", listening);
	},
	initEvent = function(){
		var op = $("#op").val();
		if(op != null && op.length > 0 && op == 'extractMember'){
			$("#btnSave").click(extractSave);
		}
	},
	extractSave = function(){
		var type = $("#type").val();
		if(type != null && type == 'GROUP')//按群组抽取
			$("#form").attr('action', "../amuc/member/ExtractMember.do?a=saveByGroup");
		if(type != null && type == 'ACTION')//按活动抽取
			$("#form").attr('action', "../amuc/member/ExtractMember.do?a=saveByAction");
		
		var positiveNumber = /^[0-9]*[1-9][0-9]*$/;
		var memberNum = $("#memberNum").val();
		var memberNumFlag = positiveNumber.test(memberNum);   //返回值为boolean类型
		var docIDs = $("#docIDs").val();
		if(memberNum == null || memberNum == '' || !memberNumFlag){
			alert("抽取数量需大于0且为正整数！");
			$("#memberNum").focus();
			return false;
		} else if(docIDs == null || docIDs ==''){
			alert("请从列表中选择一条数据！");
			return false;
		} else {
			$("#form").submit();
			return true;
		}
	},
	onload = function(){
		initEvent();
	};

	return {
		init: init,
		onload: onload
	}
});