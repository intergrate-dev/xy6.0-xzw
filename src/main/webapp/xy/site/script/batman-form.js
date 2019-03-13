var batman_form = {
	init : function() {
		
		//修改表单提交功能
		
	    $("#form").attr("target", "iframe");
	    $("#form").attr("action", "../../xy/batman/FormSubmit.do");	
	    $("#u_code").addClass("validate[custom[onlyLetterNumber]]");
	    
		var siteID = e5_form.getParam("siteID");
		if (!siteID) siteID = 1;
		
		$("#bm_siteID").val(siteID);
		
		//如果登录名不为空 则判定为修改 登录名不可修改 否则为新建 可以修改
		if($("#bm_code").val()!=""){
			$("#bm_code").attr("readOnly",true);
		}else{
			$("#bm_code").attr("readOnly",false);
		}
		//默认地址为湖南邵阳
		if($("#bm_regionID").val()==""){
			var theURL = "../../xy/batman/getRegion.do";
			$.ajax({url:theURL, async:false, dataType:"json", success: function(data) {
				$("#bm_regionID").val(data.regionId);
				$("#bm_region").val(data.regionName);
			}});
		}
        
		//上传身份证照片并显示缩略图
		$("#bm_idPic").bind("change",function(){
			uploadOneFileAndShow(this,"iconImg1");
		});
		//上传头像并显示缩略图
		$("#bm_icon").bind("change",function(){
			uploadOneFileAndShow(this,"iconImg0");
		});
		
		//对应用户
		var theURL = "xy/user/Users.do?siteID=" + siteID;
		var sels = $("#form select[url]");
		sels.attr("url", theURL);
		
		icon_form.showIcon("bm_icon", 0);
		icon_form.showIcon("bm_idPic", 1);
		icon_form.showIcon("bm_corpProve", 2);
		
		e5_form.dynamicReader._readSelectUrl();
		e5_form.dataReader.selectInit();
		
		//所属单位
		//$("#bm_corporation").bind("blur",corpBlur);
		

		//给文本框加验证
		batman_form.addValid("bm_email","email");
		batman_form.addValid("bm_phone","phone");
		batman_form.addValid("bm_qq","onlyNumberSp");
		batman_form.addValid("bm_weibo","notChi");
		batman_form.addValid("bm_weixin","notChi");
		batman_form.addValid("bm_idNo","idNo");
		batman_form.addValid("bm_zip","zip");
		
		//给隐藏域对应用户赋值
		$("#bm_user").val($("#bm_user_ID").find("option:selected").text());

		e5_form.autoCompleter.autoCompleteInit();
		
		var pass_word = $("#bm_password").val();
		var stars = "";
		for(var i=0;i<pass_word.length;i++){
			stars+="*";
		}
		 $("#bm_password").val(stars);
	},
	addValid : function(inputId,validName) {
		var bm_emailClass =$("#"+inputId).attr("class");
		var subNo = bm_emailClass.indexOf("validate[")+9;//加9是因为validate[是9个字符
		var validNew = bm_emailClass.substr(0,subNo)+validName+","+bm_emailClass.substr(subNo);
		$("#"+inputId).attr("class",validNew);
	}   

	
};
/*//所属单位验证
var corpBlur=function(){
	var theURL = "../../xy/user/Corporation.do?siteID=" + $("#bm_siteID").val()+"&q="+$("#bm_corporation").val();
	$.ajax({url:theURL, async:false, dataType:"json", success: function(data) {
		if(data==""){
			 $("#bm_corporation_ID").val("");
			 return;
		}
		$("#bm_corporation_ID").val(data[0].key);
	}});
}*/
e5_form.event.otherValidate = function() {
	//验证单位是否存在 如不存在则返回此新建单位
	if($("#bm_corporation").val()!=""){
		var theURL = "../../xy/user/Corporation.do?siteID=" + $("#bm_siteID").val()+"&corpName="+$("#bm_corporation").val();
		$.ajax({url:theURL, async:false, dataType:"json", success: function(data) {
			if(data==""){
				 $("#bm_corporation_ID").val("");
				 alert("单位获取失败！");
				 return false;
			}
			$("#bm_corporation_ID").val(data[0].key);
		}});
	}
//	var corpID = $("#bm_corporation_ID").val();
//	if (!corpID || corpID == '0') {
//		alert("请填写已存在的单位名称");
//		$("#bm_corporation").focus();
//		return false;
//	}
	
	if($("#LABEL_bm_idPic").hasClass('custform-label-require')){
		if($("#iconImg1").is(":hidden")){
			alert("请上传身份证照片");
			$("#bm_idPic").focus();
			return false;
		}
	}
	if($("#LABEL_bm_icon").hasClass('custform-label-require')){
		if($("#iconImg0").is(":hidden")){
			alert("请上传头像");
			$("#bm_icon").focus();
			return false;
		}
	}
	if($("#LABEL_bm_corpProve").hasClass('custform-label-require')){
		if($("#iconImg2").is(":hidden")){
			alert("请上传单位证明");
			$("#bm_corpProve").focus();
			return false;
		}
	}
	return true;
}
var icon_form = {
	//显示头像
	showIcon : function(id, flag) {
		$(".labelIconImg").hide(); //提示Label去掉
		
		var img = $("#iconImg" + flag);
		
		var url = $("#" + id).attr("oldvalue");
		if (url && url != "-") {
			url ="../../xy/image.do?path=" +url;
			img.attr("src", url);
			img.css("max-width", 150);
			img.css("max-height", 150);
		} else {
			img.hide();
		}
	}
}

$(function() {
	batman_form.init();
});