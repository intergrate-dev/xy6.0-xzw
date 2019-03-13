var topic_form = {
	init : function() {
		//站点ID赋值
		var siteID = e5_form.getParam("siteID");
		$("#col_siteID").val(siteID);
		$("#col_code").addClass("validate[custom[onlyLetterSp]]");
		//$("#col_linkUrl").addClass("validate[custom[urlVal]]");
        $("#btnFormSaveSubmit").hide();
		//若是新建子栏目，则给父栏目ID赋值
		var isNew = e5_form.getParam("new");
		if (isNew == "1") {
			$("#btnFormSave").val("保存并继续添加");

			$("#btnFormSaveSubmit").show();
            $("#btnFormSaveSubmit").click(function(){
                $("#form").attr("action", "../../xy/column/TopicFormSubmit.do?needLocation=true");
                $("#btnFormSave").click();
            });

            $("#process").hide();
            $("#syncprocess").hide();
            $("#sourcegroup").hide();
            $("#syncsourcegroup").hide();
            $("#btnSync").hide();

			var parentID = e5_form.getParam("DocIDs");
			$("#col_parentID").val(parentID);
			
			//新建的时候 隐藏小编推荐栏目和热门栏目选项
			$("#SPAN_col_editorCol").hide();
			$("#SPAN_col_hotCol").hide();
			
			$("#DocID").val("");
			$("#col_name").val("");
			$("#col_code").val("");
		}
		$("#form").attr("target", "iframe");
		$("#form").attr("action", "../../xy/column/TopicFormSubmit.do");

		var isBat = e5_form.getParam("isBat");
		if(!!isBat){
            $("#btnFormSaveSubmit").hide();
            $("#col_name").attr("placeholder","请输入栏目名称，用英文分号;分开");
			//$("#col_name").style.height="100px";
			$("#form").attr("action", "../../xy/column/TopicFormSubmitBat.do");
            $("#SPAN_col_linkUrl").parent().hide();
            $("#SPAN_col_code").parent().hide();
		}

		//重新让表单读一次checkbox和select的选择项
		e5_form.dynamicReader._readCheckUrl();
		e5_form.dynamicReader._readSelectUrl();
		//重新对已选值进行初始化
		e5_form.dataReader.checkInit();
		e5_form.dataReader.selectInit();
		
		// 初始化图标
		topic_form.initIcon();
		
		var parentID = e5_form.getParam("parentID");
		if(parentID > 0){
			$("#SPAN_col_editorCol").hide();
			$("#SPAN_col_hotCol").hide();
		}
		//新增取消按钮，隐藏页面
        $("#btnFormCancel").click(function(){
            $(".tablecontent").hide();
        })

        //新增title样式
        $("#col_name").attr("title",$("#col_name").val());
        $("#col_name").change(function () {
            $("#col_name").attr("title",$("#col_name").val());
        })

	},
	// 初始化图标
	initIcon : function() {
		icon_form.showIcon("col_icon", 1);
	},
	//表单新建后可继续新建
	newForm : function() {
		$("#col_name").val("");
        $("#col_code").val("");
        $("#col_description").val("");
		$("#btnFormSave").attr("disabled", false);
		$("#btnFormCancel").attr("disabled", false);
		$("#btnFormSaveSubmit").attr("disabled", false);
	},
	//表单保存后仍保留页面
	refresh : function(docID) {
		$("#btnFormSave").attr("disabled", false);
		$("#btnFormCancel").attr("disabled", false);
		$("#btnFormSaveSubmit").attr("disabled", false);
		if (docID)
			$("#DocID").val(docID);
	}



}


//查是否重名
e5_form.checkDuplicate = function(field, rules, i, options) {
	field.val(field.val().trim());
	if (!field.val()) return;
	
	var _name = field.val();
	if (_name && typeof _name == "string" && _name.toLowerCase() == "null"){
		return "话题组名称不能为null";
	}

	var colNames = [];
	var colNameObj = {};
	var isBat = e5_form.getParam("isBat");
	if (isBat){
		colNames = field.val().split(";");
		for ( var i=0 ; i < colNames.length ; ++i ) {
			if (!!colNameObj[colNames[i]])
				return "名称中 " + colNames[i] + " 有重复";
			colNameObj[colNames[i]] = colNames[i];
		}
	} else {
		colNameObj[field.val()] = field.val();
	}
	
	for ( var key in colNameObj ) {
		var theURL = e5_form._duplicateUrl(colNameObj[key]);
		theURL = e5_form.dealUrl(theURL);
		var result = null;
		$.ajax({url: theURL, async:false, success: function(data) {
			result = data;
		}});
		if (result == 1)
			return " "+colNameObj[key]+" 已存在";
	}
}

e5_form._duplicateUrl = function(field) {
	var parentID = $("#col_parentID").val();
	
	var isNew = e5_form.getParam("new");
	if (isNew == "1") {
		parentID = e5_form.getParam("DocIDs");
	}
	
	var siteID = e5_form.getParam("siteID");
	
	var theURL = "xy/column/TopicDuplicate.do"
		+ "?DocLibID=" + $("#DocLibID").val()
		+ "&DocIDs=" + $("#DocID").val()
		+ "&parentID=" + parentID
		+ "&siteID=" + siteID
		+ "&value=" + e5_form.encode(field);
	return theURL;
}

var icon_form = {
	//显示头像
	showIcon : function(id, flag) {
		$(".labelIconImg").hide(); //提示Label去掉
			
		var img = $("#iconImg" + flag);
			
		var url = $("#" + id).attr("oldvalue");
		if (url && url != "-") {
			img.attr("src", url);
			img.css("max-width", 150);
			img.css("max-height", 150);
				
			$("#img_" + id).hide()
		} else {
				img.hide();
		}
	}
}

$(function(){
	topic_form.init();
});
