var ori_form = {
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
            $("#btnFormSave").click(function(){
                var colVal = $("#col_name").val();
                if (colVal == null || colVal == ""){
                    alert("分类名称不能为空");
                    return false;
                }
                $("#form").attr("action", "../../xy/column/OrgFormSubmit.do?needLocation=true&siteID="+siteID);
                $("#btnFormSave").click();
            });

            $("#process").hide();
            $("#syncprocess").hide();
            $("#sourcegroup").hide();
            $("#syncsourcegroup").hide();
            $("#btnSync").hide();

			var parentID = e5_form.getParam("DocIDs");
			$("#col_parentID").val(parentID);
			
			$("#DocID").val("");
			$("#col_name").val("");
			$("#col_code").val("");
		}else {
            $("#btnFormSave").click(function () {
                var colVal = $("#col_name").val();
                if (colVal == null || colVal == "") {
                    alert("分类名称不能为空");
                    return false;
                }
                $(this).addClass("noDisabled")
                $("#form").attr("target", "iframe");
                $("#form").attr("action", "../../xy/column/OrgFormSubmit.do?siteID="+siteID);
            });
        }
		$("#form").attr("target", "iframe");
		$("#form").attr("action", "../../xy/column/OrgFormSubmit.do");

		var isBat = e5_form.getParam("isBat");
		if(!!isBat){
            $("#btnFormSaveSubmit").hide();
            $("#col_name").attr("placeholder","请输入栏目名称，用英文分号;分开");
			//$("#col_name").style.height="100px";
			$("#form").attr("action", "../../xy/column/OrgFormSubmitBat.do");
            $("#SPAN_col_linkUrl").parent().hide();
            $("#SPAN_col_code").parent().hide();
		}

		//重新让表单读一次checkbox和select的选择项
		e5_form.dynamicReader._readCheckUrl();
		e5_form.dynamicReader._readSelectUrl();
		//重新对已选值进行初始化
		e5_form.dataReader.checkInit();
		e5_form.dataReader.selectInit();
	},
	//表单新建后可继续新建
	newForm : function() {
		$("#col_name").val("");
		$("#col_code").val("");
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
		return "栏目名称不能为null";
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
	
	var theURL = "xy/column/OrgDuplicate.do"
		+ "?DocLibID=" + $("#DocLibID").val()
		+ "&DocIDs=" + $("#DocID").val()
		+ "&parentID=" + parentID
		+ "&siteID=" + siteID
		+ "&value=" + e5_form.encode(field);
	return theURL;
}

$(function(){
	ori_form.init();
});
