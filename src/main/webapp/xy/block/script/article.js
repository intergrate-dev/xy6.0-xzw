var artcile_form = {
		blockID : "",
		editDialogType : 0,// 0为标题，1为摘要，2为副题
		canEditStyle : true,
		init : function() {
			//若配置未启动编辑样式，则隐藏按钮
			if(!artcile_form.canEditStyle){
				$("#btnTitleAdv").hide();
				$("#btnEditSubTitle").hide();		
				$("#btnEditAbstract").hide();	
			}
			$("#ba_blockID").val(artcile_form.blockID);
			$("#wx_menuID").val(artcile_form.blockID);
			
			var img = $("#img4Title");
			if (img.attr("src")) {
				var s = img.attr("src");
				if (s.indexOf("http:") < 0) {
					img.attr("src", "../../xy/image.do?path=" + s);
				}
			} else {
				img.hide();
			}

			//$("#ba_url").addClass("validate[custom[url]]");

			$("#DIV_ba_pic").append("<br/><br/><font color='gray'></font>");
			
			$("#btnTitleAdv").click(artcile_form.editTitle);		//标题编辑样式按钮
			$("#btnEditSubTitle").click(artcile_form.editSubTitle);		//副题编辑样式按钮
			$("#btnEditAbstract").click(artcile_form.editAbstract);		//摘要编辑样式按钮

			artcile_form.initDateTimePicker();
			if (!$('#ba_pubTime').val())
				$('#ba_pubTime').val(artcile_form.getNowtime());

			$("#form").attr("target", "iframe");
			$("#form").attr("action", "../../xy/block/FormSave.do");
		},
		initDateTimePicker : function(){
			$('#ba_pubTime').datetimepicker({
				language : 'zh-CN',
				weekStart : 0,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : true,
				startView : 2,
				minView : 0,
				disabledDaysOfCurrentMonth : 0,
				forceParse : 0,
				pickerPosition: "bottom-left",
				format : 'yyyy-mm-dd hh:ii:ss'
			});
			$('#ba_pubTime').datetimepicker().on('changeDate', function(ev) {
			});
		},
    	getNowtime : function() {
    	    var dd = new Date();
    	    dd.setDate(dd.getDate());
    	    var y = dd.getFullYear();
    	    var M = dd.getMonth()+1;//获取当前月份的日期
    	    var d = dd.getDate();
    	    var h = dd.getHours();
    	    var m = dd.getMinutes(); 
    	    var s = dd.getSeconds();

    	    return y + "-" + artcile_form.add_zero(M) + "-" + artcile_form.add_zero(d) + " " 
    	    + artcile_form.add_zero(h) + ":" + artcile_form.add_zero(m) + ":" + artcile_form.add_zero(s);
    	},
    	add_zero:function(param){
    		if(param < 10){
    			return "0" + param;
    		}
    		return param;
    	},
    	editTitle : function(){
    		artcile_form.editDialogType = 0;
    		artcile_form.openEditDialog();
    	},
    	editAbstract : function(){
    		artcile_form.editDialogType = 1;
    		artcile_form.openEditDialog();
    	},
    	editSubTitle : function(){
    		artcile_form.editDialogType = 2;
    		artcile_form.openEditDialog();
    	},
    	openEditDialog : function(){
    		var content = '';
    		if( artcile_form.editDialogType == 0 ){
    			content = $("#ba_topic").val();
    		} else if ( artcile_form.editDialogType == 1 ){
    			content = $("#ba_abstract").val();
    		} else if ( artcile_form.editDialogType == 2 ){
    			content = $("#ba_subTitle").val();
    		}
    		var dataUrl = "../../xy/article/editStyle.jsp?editDialogType="+artcile_form.editDialogType+"&e_type=articlesetting";

    		artcile_form.editDialog = e5.dialog({
    			type : "iframe",
    			value : dataUrl
    		}, {
    			title : "编辑样式",
    			width : "650px",
    			height : "300px",
    			resizable : false,
    			fixed : true
    		});
    		artcile_form.editDialog.show();
    	}
}

$(function(){
	artcile_form.init();
});

function editClose(contents){
	if( artcile_form.editDialogType == 0 ){
		$("#ba_topic").val(contents);
	} else if ( artcile_form.editDialogType == 1 ){
		$("#ba_abstract").val(contents);
	} else if ( artcile_form.editDialogType == 2 ){
		$("#ba_subTitle").val(contents);
	}
	artcile_form.editDialog.close();
}

function editCancel(){
	artcile_form.editDialog.close();
}

function getContent(){
	var content = '';
	if( artcile_form.editDialogType == 0 ){
		content = $("#ba_topic").val();
	} else if ( artcile_form.editDialogType == 1 ){
		content = $("#ba_abstract").val();
	} else if ( artcile_form.editDialogType == 2 ){
		content = $("#ba_subTitle").val();
	}
	return content;
}

//提交按钮
e5_form.event.doSave = function() {
	$("#SYS_TOPIC").val($("#ba_topic").val());
	$("#wx_subTitle").val($("#ba_subTitle").val());
	$("#wx_url").val($("#ba_url").val());
	$("#wx_pubTime").val($("#ba_pubTime").val());
	$("#wx_pic").val($("#ba_pic").val());
	$("#wx_abstract").val($("#ba_abstract").val());
	
	var fileName = $("#ba_pic").val();
	if (fileName){
		var picfileExtension = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length).toLocaleLowerCase();
		// 判断文件格式
		if (picfileExtension!="" && picfileExtension != "jpg" && picfileExtension != "gif" && picfileExtension != "png" && picfileExtension != "jpeg") {	
			if(picfileExtension != "-"){
				$("#DIV_ba_pic").find("font").html("*对不起，图片上传格式支持jpg,gif,png,jpeg");
				$("#DIV_ba_pic").find("font").attr("color","red");
				return false;
			}
		}
	}
	
	if (!$("#form").validationEngine("validate")) {
		// 验证提示
		$("#form").validationEngine("updatePromptsPosition");
		return false;
	}
	window.onbeforeunload = null;

	//若有附件，则先提交
	if (!e5_form.file.upload()) {
		return false;
	}
}

//图片上传的Url变化
e5_form.file._uploadUrl = function() {
	return "xy/block/Data.do?action=upload&DocLibID=" + $("#DocLibID").val();
}

//修改操作里，读表单上的数据时，增加dateFull=1参数，使发布时间显示时分秒
e5_form.dataReader._getDataUrl = function() {
	var theURL = "e5workspace/manoeuvre/FormDocFetcher.do?FormID=" + $("#FormID").val()
		+ "&DocLibID=" + $("#DocLibID").val()
		+ "&DocID=" + $("#DocID").val()
		+ "&dateFull=1";
	return theURL;
}