var column_form = {
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
                $("#form").attr("action", "../../xy/column/FormSubmit.do?needLocation=true");
                $("#btnFormSave").click();
            });

            $("#process").hide();
            $("#syncprocess").hide();
            $("#sourcegroup").hide();
            $("#syncsourcegroup").hide();
            $("#selectAll").hide();
            $("#btnSync").hide();

			var parentID = e5_form.getParam("DocIDs");
			$("#col_parentID").val(parentID);
			
			var ch = e5_form.getParam("ch");
			$("#col_channel").val(ch);

			$("#DocID").val("");
			$("#col_name").val("");
			$("#col_code").val("");
		}
		$("#form").attr("target", "iframe");
		$("#form").attr("action", "../../xy/column/FormSubmit.do");

        $("#selectAll").click(column_form.selectAll);
        $("#btnSync").click(column_form.sync2Children);

		var isBat = e5_form.getParam("isBat");
		if(!!isBat){
            $("#btnFormSaveSubmit").hide();
            $("#col_name").attr("placeholder","请输入栏目名称，用英文分号;分开");
			//$("#col_name").style.height="100px";
			$("#form").attr("action", "../../xy/column/FormSubmitBat.do");
            $("#SPAN_col_linkUrl").parent().hide();
            $("#SPAN_col_code").parent().hide();
		}
		//确定站点后才能够读出来源组、稿件扩展字段组
		$("#col_source").attr("url", "xy/column/Group.do?code=SOURCE&siteID=" + siteID);
		$("#col_extField_ID").attr("url", "xy/column/Group.do?code=EXTFIELD&siteID=" + siteID);

		//重新让表单读一次checkbox和select的选择项
		e5_form.dynamicReader._readCheckUrl();
		e5_form.dynamicReader._readSelectUrl();
		//重新对已选值进行初始化
		e5_form.dataReader.checkInit();
		e5_form.dataReader.selectInit();
	},

    //同步项全选
    selectAll : function(){

        if($("#process").attr('checked') && $("#sourcegroup").attr('checked')){
            $("#process").prop("checked",false);
            $("#sourcegroup").prop("checked",false);
        }else{
            $("#process").prop("checked",true);
            $("#sourcegroup").prop("checked",true);
        }

    },

    //同步到子孙栏目
    sync2Children : function(evt) {
        if (!confirm("确定要把属性同步到子孙栏目吗？")) {
            return;
        }

        var formType = $(evt.target).attr("formtype");
        var process =0,sourcegroup=0;
        if($("#process").attr('checked')){

            process = 1;
        }
        if($("#sourcegroup").attr('checked')){

            sourcegroup = 1;
        }

        if(process==0&&sourcegroup==0){
            alert("请先选择需要的同步项！");
            return;
        }

        var theURL = "../../xy/column/Sync.do?DocLibID=" + e5_form.getParam("DocLibID")
            + "&DocIDs=" + e5_form.getParam("DocIDs")
            + "&SyncType=" + formType
            // + "&siterule=" + siterule
            // + "&columnpl=" + columnpl
            // + "&articlepl=" + articlepl
            // + "&picpl=" + picpl
            // + "&videopl=" + videopl
            // + "&isShowInNav=" + isShowInNav
            // + "&columntype=" + columntype
            // + "&columnstyle=" + columnstyle
            // + "&columntopcount=" + columntopcount
			+ "&process=" + process
			+ "&sourcegroup=" + sourcegroup;

        $.ajax({type: "POST", url: theURL, async:false,
            success: function (data) {
                if (data == "ok") {
                    alert("同步完成");
                } else {
                    alert("操作失败：" + data);
                }
            }
        });
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
	var ch = e5_form.getParam("ch");
	
	var theURL = "xy/column/Duplicate.do"
		+ "?DocLibID=" + $("#DocLibID").val()
		+ "&DocIDs=" + $("#DocID").val()
		+ "&parentID=" + parentID
		+ "&siteID=" + siteID
		+ "&ch=" + ch
		+ "&value=" + e5_form.encode(field);
	return theURL;
}

$(function(){
	column_form.init();
});
