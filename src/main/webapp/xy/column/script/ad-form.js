var ad_form = {

	init : function() {
		//给栏目ID赋值
        $("#form").attr("target", "iframe");
        $("#form").attr("action", "../../xy/column/AdSubmit.do");
        var siteID=e5_form.getParam("siteID");
        $("#form").prepend("<input type='hidden' id='siteID' name='siteID' value='"+siteID+"'/>");
		var colID = e5_form.getParam("colID");
		if (!colID) {
			//没有传入栏目ID参数，是在启动页广告管理里。不显示广告类型
			colID = 0;
			$("#SPAN_ad_type").parent().parent().hide();
			$("#SPAN_ad_order").parent().parent().hide();
			
			var radio = $("<input type='radio'/>")
					.val(0)
					.attr("name", "ad_type")
					.attr("id", "ad_type__1");
			$("#SPAN_ad_type").append(radio);
			radio[0].checked="true";
			$("#ad_type").attr("oldValue", "0");

			$("#ad_showTime").addClass("validate[min[0],max[99]]");//显示秒数，>=0
		} else {
			//栏目的广告，不显示时长
			$("#SPAN_ad_showTime").parent().parent().hide();
			$("#ad_showTime").removeClass("validate[required,custom[integer]]");
		}
		$("#ad_columnID").val(colID);
		
		//起止时间的验证
		$("#ad_beginDate").removeClass("validate[required,custom[dateFormat]]");
		$("#ad_endDate").removeClass("validate[required,custom[dateFormat]]");
		
		$("#ad_beginDate").addClass("validate[required,custom[dateTimeFormat3]]");
		$("#ad_endDate").addClass("validate[required,custom[dateTimeFormat3]]");
		$("#ad_endDate").addClass("validate[future[#ad_beginDate]]");
		
		/*
		//广告类型的切换响应，显示建议的图片尺寸
		var url = "../../xy/column/PicSizes.do?id=" + e5_form.getParam("siteID");
		$.ajax({url: url, async:false, success: function (data) {
				if (data) {
					ad_form.picSizes = data.split(",");
				}
			}
		});		
		*/
		$("input[name='ad_type']").click(ad_form.typeChange);
		
		var style = $("#ad_type").attr("oldValue");
		if (style == "-") style = "1";
		ad_form.typeChange0(style);

        ad_form.changePicUrl();
	},
	typeChange : function(evt) {
		var style = $(evt.target).val();
		ad_form.typeChange0(style);
	},
	typeChange0 : function(style) {
        var picSizes ={"1":"16 : 9","2":"4 : 1","3":"4 : 1","5":"4 : 3"};
		var size = picSizes[style];
		var span = $("#picSize");
		if (span.length == 0) {
			span = $("<span id='picSize' style='color:red;'/>")
				.html("建议尺寸：" + size);
			$("#ad_picUrl").after(span);
		} else {
			span.html("建议尺寸：" + size);
		}
	},

	changePicUrl : function(){

		var path=$("#img_ad_picUrl").attr("path");
		var pathPic=ad_form.reverse(path);
        var x=ad_form.find(pathPic,"/",2);
		var pic=ad_form.reverse(pathPic.substring(0,x));
        $("#img_ad_picUrl").attr("path","附件存储;"+pic);
	},
    reverse : function( str ){
        var stack = [];//生成一个栈
        for(var len = str.length,i=len;i>=0;i-- ){
            stack.push(str[i]);
        }
        return stack.join('');
    },
    find : function (str,cha,num){
		var x=str.indexOf(cha);
		for(var i=0;i<num;i++){
			x=str.indexOf(cha,x+1);
		}
		return x;
    }

};
e5_form.dataReader._getDataUrl = function() {
	var theURL = "e5workspace/manoeuvre/FormDocFetcher.do?FormID=" + $("#FormID").val()
		+ "&DocLibID=" + $("#DocLibID").val()
		+ "&DocID=" + $("#DocID").val()
		+ "&dateFull=1" //增加这个参数，使读完整的日期时间格式
		;
	return theURL;
}
e5_form.event.otherValidate = function() {

	if (!$("#ad_picUrl").val() && !$("#ad_picUrl").attr("oldValue") && !$("#ad_linkUrl").val()) {
		alert("请上传图片或填写链接地址");
		return false;
	}
	
	//截止日期默认是23:59:59
	var endDate = $("#ad_endDate").val();
	if (endDate && endDate.length == 10) {
		endDate += " 23:59:59";
	}
	$("#ad_endDate").val(endDate);
	return true;
}
$(function() {
	ad_form.init();
});
