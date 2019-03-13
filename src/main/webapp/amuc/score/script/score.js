var score_form = {
	init : function() {
		if($("#msType").val() == 0){
			$("#msType").val(1);//手工添加
		}else{
			$("#msType").val(2);//手工扣减
		}
		$("#btnFormSave").click(score_form.timeCheck);
		$("#msScore").blur(score_form.scoreline);
		$("#form").attr("action", "../../amuc/score/FormSubmit.do");//提交保存修改
		$("#msMember").blur(score_form.memberBlur);//会员，带输入提示
		$("#msRule_ID").change(score_form.ruleChange);//积分规则切换
		
		$("#msSource_ID").attr("disabled","true");
		//非负值限制
		$("#msScore").addClass("validate[min[0]]");
		$("#msExperience").addClass("validate[min[0]]");
		
		var values = [0, "", "", 0, "", 0, "", 0];
		score_form.setHideField(values);
	},
	//会员，带输入提示。若不是系统已有名，则清空
	memberBlur : function() {
		var id = $("#msMember_ID").val();
		var name = $("#msMember").val();
		if (!id) {
			$("#msMember").val("");
		} else {
			/*var pos = name.indexOf("（id=");
			if (pos > 0) {
				name = name.substring(0, pos);
				$("#msMember").val(name);
			}*/
		}
	},
	//积分规则切换时，隐含的多个域值变化
	ruleChange : function() {
		//规则ID,积分,经验值,规则类型,数据来源,数据来源ID,行为类型,行为类型ID
		var value = $("#msRule_ID").val();
		var values = (!value) ? [0, "", "", 0, "", 0, "", 0] : value.split(",");
		
		score_form.setHideField(values);
	},
	setHideField : function(values) {
		$("#msScore").val(values[1]); //积分
		$("#msExperience").val(values[2]);//经验值
		$("#msRuleType").val(values[3]); //规则类型
		$("#msSource").val(values[4]); //数据来源
		$("#msSource_ID").val(values[5]);
		$("#msEventType").val(values[6]);//行为类型
		$("#msEventType_ID").val(values[7]);
	},
	timeCheck : function(){
		var msTime = $("#msTime").val();
		$("#msTime").removeClass();
		if(msTime){
			var myDate = new Date();
			var nowTime = myDate.getFullYear()+"-"+(myDate.getMonth() + 1)+"-" +myDate.getDate();
			var time = new Date(msTime.replace("-", "/").replace("-", "/"));  
	        var now = new Date(nowTime.replace("-", "/").replace("-", "/")); 
	        if(time <= now){
	        	$("#msTime").addClass("custform-input validate[custom[dateFormat]]");
	        }  else{
	        	$("#msTime").addClass("custform-input validate[maxSize[255],funcCall[textTips]]");
	        }
		}else{
			$("#msTime").addClass("custform-input validate[required,custom[dateFormat]]");
		}
		return score_form.scoreCheck();
	},
	scoreCheck : function(){
		
		//检验会员积分/经验值是否为负
		var SE = "";
		var membername = $("#msMember").val();
		var url= encodeURI("../../amuc/score/CheckSE.do?mbn=" + membername);
		var result ="";
		$.ajax({
			url: url,
			async:false,
			cache:false,
			success: function(data) {
				SE = data;
		    },
		    error:function (XMLHttpRequest, textStatus, errorThrown) {
		    	alert(textStatus);
		    }
		});
		if(SE == "true" && $("#msType").val() == 2){
			alert("该会员积分为负，不能扣减");
			$("#form").attr("submit", "disabled");//提交保存修改
			return false;
		}else{
			var isline =  score_form.scoreline();//查积分是否超过警戒线
			if(isline){
				$("#form").attr("submit", "disabled");//提交保存修改
				return false;
			}else{
				$("#form").attr("submit", "true");//提交保存修改
				return true;
			}
		}
	},
	scoreline :function (){
		//检验积分是否超过警戒线
		var msScore = $("#msScore").val();
		var url= encodeURI("../../amuc/score/ScoreLine.do?msScore=" + msScore);
		var result ="";
		$.ajax({
			url: url,
			async:false,
			cache:false,
			success: function(data) {
				result = (data);
		    },
		    error:function (XMLHttpRequest, textStatus, errorThrown) {
		    	alert(textStatus);
		    }
		});
		if (result == "true" ){
			alert("积分超过警戒线");
			return true;
		}else{
			return false;
		}
	}
}
function textTips(){
	return "* 日期不能晚于当前日期！";
}
//获取url地址中的参数
function getUrlVars(name){
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}
$(function(){
	score_form.init();
	var siteID = getUrlVars("siteID");
	$("#m_siteID").val(siteID);
	$("#msTime").blur (function(){
		$("#msTime").removeClass();
		$("#msTime").addClass("custform-input validate[custom[dateFormat]]");
	});
});