var member_view = {
	init : function() {
		//会员头像
		member_view.showImg();
	},
	showImg : function (){
		//会员基本信息-显示头像
		var basepath = "../amuc/member/headImg/";
		var profilephotohref = $("#mHead a").attr("href");
		var profilephotofilename = profilephotohref.substring(profilephotohref.lastIndexOf("=") + 1);

		if(profilephotofilename != ''){
			//显示上传头像
			var imgsrc = profilephotofilename;
		}else{
			//显示默认头像
			var imgsrc = basepath + "default.bmp";
		}		
		
		$("#mHead").html("<img height='128px' width='128px' src='"+imgsrc+"'/>");
	},
	//脱敏显示（手机号、身份证号等）
	moblieOutSensty : function() {
		//手机号信息脱敏显示
		var mobileNo = $("#mMobile").html();
		var lenMob = mobileNo.length;
		var strMob = "";
		for(var i=0;i<lenMob-7;i++){
			strMob+="*";
		}
		var phoneNum = mobileNo.substring(0,3) + strMob + mobileNo.substring(lenMob-4,lenMob);
		$("#mMobile").html(phoneNum);
	
		//身份证等信息脱敏显示
		var cardNo = $("#mCardNo").html();
		var len = cardNo.length;
		var str = "";
		for(var i=0;i<len-5;i++){
			str+="*";
		}
		var cardNum = cardNo.substring(0,3)+str+cardNo.substring(len-2,len);
		$("#mCardNo").html(cardNum);
	}
}
$(function(){
	member_view.init();
	member_view.moblieOutSensty();
	var td1=$("#inserttxt_1");
	var td2=$("#inserttxt_2");
	td1.css("front-weight","bold").css("padding-left","5px");
	td2.css("front-weight","bold").css("padding-left","5px");
	td1.parent().css("background-color","#F0F0F0 ").css("height","25px");
	td2.parent().css("background-color","#F0F0F0 ").css("height","25px");	
});