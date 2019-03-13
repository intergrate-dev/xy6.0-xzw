e5.mod("workspace.memberR", function(){
	var listening = function(msgName, callerId, param) {
		var id = param.docIDs;
		if (id) 
			id = id.substring(0, id.length - 1);
		postForm["TargetID"].value = id;
	}
	var setCount = function(msgName, callerId, callerData){
		callerData = parseInt(callerData,10);
		if(typeof callerData == "number"){
			$("#privateNums").html(callerData).attr("title",callerData);
		}
	}
	var init = function(sandbox){
		var api = sandbox;
		api.listen("workspace.doclist:doclist", listening);
			api.listen("workspace.doclist:setPrivateNums", setCount);
	}
	return {
		init: init
	}
});

function doSubmit() {
	var postForm=document.getElementById("postForm");
	var r = postForm["relations"].value;
	if (!r) {
		alert("请选择关系");
		return;
	}
	
	var targetID = postForm["TargetID"].value;
	if (!targetID) {
		alert("请选择关系人");
	} else if (targetID == postForm["DocIDs"].value) {
		alert("请不要选择本人");
	} else {
		if(r.indexOf("1") != -1){
			//不对等关系才会查看关系是否存在，且以达到上限。
			var check_rel = checkRelation();
			if(check_rel != "" && check_rel != null){
				//alert("该会员不允许再添加这种关系!");
				alert(check_rel);
				return false;
			}
		}
		postForm.submit();
	}
}
function checkRelation(){
	var check ="false";
	var postForm=document.getElementById("postForm");
	var r = postForm["relations"].value;
	var targetID = postForm["TargetID"].value;
	var memberID = postForm["DocIDs"].value;
	var TargetRole = postForm["TargetRole"].value;
	var Role = postForm["Role"].value;
	//var DocLibID = postForm["DocLibID"].value;
	var param = "&DocIDs=" + memberID +"&targetID=" + targetID + "&relation="+ r +"&Role=" + Role +"&TargetRole=" +TargetRole ;
	var url = encodeURI("../../amuc/member/Member.do?a=checkRelation" + param);
	$.ajax({
        type:"GET", //请求方式  
        url: url, //请求路径  
        cache: false,
        dataType: 'TEXT',   //返回值类型  
        async:false,
        success:function(data){
			check = data;
        } ,
		error:function (XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
    });
	return check;
}
//选择关系
function doChange() {
	var postForm=document.getElementById("postForm");
	var relation = postForm["relations"].value;
	if (!relation) return false;
	
	var arr = relation.split(",");
	if (arr[3] == 0) {
		//对等
		$("#roleTable").hide();
	} else {
		//不对等
		$("#roleTable").show();
	}
	if (!arr[1]) arr[1] = "-";
	if (!arr[2]) arr[2] = "-";
	postForm["Relation"].value = arr[0];
	postForm["Role"].value = arr[1];
	postForm["TargetRole"].value = arr[2];
	
	$("#spanRelation").html(arr[0]);
	$("#spanRole").html(arr[1]);
	$("#spanTargetRole").html(arr[2]);
}
//角色对换
function doExchange() {
	var tmp = postForm["Role"].value;
	postForm["Role"].value = postForm["TargetRole"].value;
	postForm["TargetRole"].value = tmp;
	
	var tmp = $("#spanRole").html();
	$("#spanRole").html($("#spanTargetRole").html());
	$("#spanTargetRole").html(tmp);
}
