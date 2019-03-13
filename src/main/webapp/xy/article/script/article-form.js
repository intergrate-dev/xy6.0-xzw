//写稿中一个渠道的表单部分的逻辑

//附件上传引用e5_form部分的处理逻辑，把代码复制到本地
var e5_form = {
    pathPrefix: "../../",

    //处理Url的路径深度
    dealUrl: function(url){
        return e5_form.pathPrefix + url;
    },
    /** 对特殊字符和中文编码 */
    encode: function(param1){
        if(!param1) return "";

        var res = "";
        for(var i = 0; i < param1.length; i++){
            switch(param1.charCodeAt(i)){
                case 0x20://space
                case 0x3f://?
                case 0x23://#
                case 0x26://&
                case 0x22://"
                case 0x27://'
                case 0x2a://*
                case 0x3d://=
                case 0x5c:// \
                case 0x2f:// /
                case 0x2e:// .
                case 0x25:// .
                    res += escape(param1.charAt(i));
                    break;
                case 0x2b:
                    res += "%2b";
                    break;
                default:
                    res += encodeURI(param1.charAt(i));
                    break;
            }
        }
        return res;
    }
};
e5_form.file = {
    counter: 0, //counter for form.
    counter_uploaded: 0, // counter for files submitted.
    counter_files: 0, //counter for files all.
    //附件初始化：有附件的话，显示文件图标，并可下载
    init: function(){
        var files = $("#form input[type='file']");
        if(files.length == 0) return;

        files.each(e5_form.file._initOneFile);
    },
    //打开表单时，把已有附件用图标显示出来，并准备好下载方法
    _initOneFile: function(index){
        var file = $(this);
        var filePath = file.attr("oldValue");
        if(filePath && filePath != "-"){
            var img = $("#img_" + file.attr("id"));
            //filePath:附件存储;201504/18/glj_<myfilename>
            var fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            fileName = fileName.substring(fileName.indexOf("_") + 1);
            img.attr("path", filePath)
                .attr("title", fileName)
                .click(e5_form.file.download)
                .show();
        }
    },
    //下载附件
    download: function(evt){
        var path = $(evt.target).attr("path");

        var dataUrl = "e5workspace/Data.do?action=download&path=" + e5_form.encode(path);
        dataUrl = e5_form.dealUrl(dataUrl);
        if(path != undefined && path !="" && path != null)
        window.open(dataUrl);
    },

    //表单提交前先上传附件。一个表单里可能有多个附件
    upload: function(){
        var files = $("#form input[type='file']");
        if(files.length == 0) return true;

        e5_form.file.counter_files = files.length;
        e5_form.file.counter_uploaded = 0;

        files.each(e5_form.file._uploadOneFile);

        return false;
    },
    //上传一个附件
    _uploadOneFile: function(){
        var dataUrl = "e5workspace/Data.do?action=upload&DocLibID=" + $("#DocLibID").val();
        dataUrl = e5_form.dealUrl(dataUrl);

        var fileInput = $(this);
        var inputName = fileInput.attr("name");
        //若没选择文件，则当做成功
        if(!fileInput.val()){
            var oldValue = fileInput.attr("oldValue");
            if(oldValue && oldValue != "-"){
                //若是修改操作，原来有附件，则需要用hidden域仍传回去
                fileInput.removeAttr("form");

                var newInput = $("<input type='hidden'/>")
                    .attr("name", inputName)
                    .attr("id", inputName)
                    .val(oldValue);
                $("#form").append(newInput);
            }
            e5_form.file._oneSuccess();
        }else{
            e5_form.file.send(fileInput, dataUrl, function(success, result){
                //若有上传失败的，则整体认为失败了
                if(e5_form.file.counter_uploaded < 0)
                    return;

                if(success){
                    var newInput = $("<input type='hidden'/>")
                        .attr("name", inputName)
                        .attr("id", inputName)
                        .val(result);
                    $("#form").append(newInput);

                    e5_form.file._oneSuccess();
                }else{
                    alert("上传失败！" + result
                    + "\n请确认已配置好存储设备（自身存储或通用的<附件存储>）。");

                    e5_form.file.counter_uploaded = -1;
                }
            });
        }
    },
    _oneSuccess: function(){
        //修改：只做++
        ++e5_form.file.counter_uploaded;
    },
    //实际的提交
    send: function(fileInput, url, completeCallback){
        var form = $('<form style="display:none;"></form>');
        form.attr('accept-charset', "UTF-8");
        // IE versions below IE8 cannot set the name property of
        // elements that have already been added to the DOM,
        // so we set the name along with the iframe HTML markup:
        e5_form.file.counter += 1;
        var iframe = $('<iframe src="" name="iframe-transport-' + e5_form.file.counter + '"></iframe>');
        iframe.bind('load', function(){
            iframe
                .unbind('load')
                .bind('load', function(){
                    var response, success;
                    // Wrap in a try/catch block to catch exceptions thrown
                    // when trying to access cross-domain iframe contents:
                    try{
                        response = iframe.contents();
                        // Google Chrome and Firefox do not throw an
                        // exception when calling iframe.contents() on
                        // cross-domain requests, so we unify the response:
                        if(!response.length || !response[0].firstChild){
                            throw new Error();
                        }
                        response = response.find("body").text();
                        //格式为   1;附件存储设备;/201504/19/glj_myfilename.txt
                        success = (response.charAt(0) == "1");
                        response = response.substring(2);
                    }catch(e){
                        response = undefined;
                    }
                    // The complete callback returns the
                    // iframe content document as response object:
                    completeCallback(success, response);
                    // Fix for IE endless progress bar activity bug
                    // (happens on form submits to iframe targets):
                    $('<iframe src=""></iframe>')
                        .appendTo(form);
                    window.setTimeout(function(){
                        // Removing the form in a setTimeout call
                        // allows Chrome's developer tools to display
                        // the response result
                        form.remove();
                    }, 0);
                });
            form
                .prop('target', iframe.prop('name'))
                .prop('action', url)
                .prop('method', "POST");
            // Appending the file input fields to the hidden form
            // removes them from their original location:
            form
                .append(fileInput)
                .prop('enctype', 'multipart/form-data')
                // enctype must be set as encoding for IE:
                .prop('encoding', 'multipart/form-data');
            // Remove the HTML5 form attribute from the input(s):
            fileInput.removeAttr('form');

            form.submit();
        });
        form.append(iframe).appendTo(document.body);
    }
}

var article_form = {
    init: function(){
        var colID = parent.article.colID;
        if(colID){
            //稿件扩展字段
            article_form.initExtField(colID);
        }
        article_source.init(); //来源
        column_form.init(); //模板指定
        article_extField.init(); //扩展字段
		article_trade.init(); //地区选择
		article_special.init();//专题模板制作
        article_pic.init();
        article_topic.init();//话题选择
        // article_pic.init();//摘要字数统计
    },
    isValid: function(){
		$("#SYS_TOPIC").val($("#SYS_TOPIC").val().trim());
		var val = $("#pubTime").val();
		if (val) $("#pubTime").val(val.trim()); //原稿时需要
		
		//由于标题、定时发布会被遮住，所以这两个不通过验证时，要固定显示上半部分
		var pinTop = !$("#SYS_TOPIC").val()
			|| $("#pubTimer").is(":checked") && !$("#pubTime").val();
		var options = $("#form").data('jqv');
		if (pinTop) {
			$("#SYS_TOPIC")[0].scrollIntoView(false);
			options.scroll = false; //改变了validationEngine的选项
		} else {
			options.scroll = true;
		}
		
		if (!$("#form").validationEngine("validate")){
            // 验证提示
            $("#form").validationEngine("updatePromptsPosition");
            return false;
        }
        return true;
    },
    // 取到表单form下的所有域（包含Hidden），依次读值，保存在js里的一个变量中
    formData: function(){
        // 读出form各字段，组织成对象 {'name1':'value1', 'name2':'value2'}
        var form = {};

		try {
        var form0 = $("#form select, #form input[type='hidden'], #form input:text, #form textarea").serializeArray();
		}catch(e){
			alert(e.message);
		}
        for(var i = 0; i < form0.length; i++){
            form[form0[i].name] = form0[i].value;
        }

        var form0 = $("#form input[type='checkbox']");
        for(var i = 0; i < form0.length; i++){
           //如果这个checkbox被选中了，在json中的这个对象后面添加
            var value = form[form0[i].name];
			if (!value) value = "";
			
            if (form0[i].checked){
                value = (value) ? value + "," + form0[i].value : form0[i].value;
            }
            form[form0[i].name] = value;
        }

        var form0 = $("#form input[type='radio']");
        for(var i = 0; i < form0.length; i++){
            if(form0[i].checked){
                form[form0[i].name] = form0[i].value;
            }
        }

        // 加上disabled域
        var form0 = $("#form :disabled");
        for(var i = 0; i < form0.length; i++){
            // 在chrome里有不存在name的disable项，造成json串格式不正确
            var name = form0[i].getAttribute("name");
            if(name){
                form[name] = form0[i].value;
            }
        }

        form["hasExtfield"] = (typeof ($("#extContentDiv").attr("id")) != "undefined");
        return form;
    },
    docLibID: 0,
    //-----以下是稿件扩展字段-----
    // 初始化扩展字段
    initExtField: function(colID){
        if($("#extContentDiv").length == 0)
            return;
        var extFieldGroupID = $("#a_extFieldGroupID").val();
        // 初始化两个值
        var docID = parent.article.docID;
        $.ajax({
            async: false,
            url: "../../xy/extfield/initExtFieldAjax.do",
            type: 'POST',
            data: {
                "colID": colID,
                "docIDs": docID,
                "extFieldGroupID": extFieldGroupID,
                "docLibID": article.docLibID
            },
            dataType: 'json',
            success: function(data, status){
                // 获取到扩展字段就展示， 否则就不展示
                if(data.result == "success"){
                    //拼装扩展字段的html
                    article_form.handleExtFieldHtml(data.list);
                }
            },
            error: function(xhr, textStatus, errorThrown){
                alert("对不起，无法初始化扩展字段！");
            }
        });
    },
    ext_dateArray : new Array(),
    //拼装扩展字段的html
    handleExtFieldHtml: function(extList){
        var html = new Array();
        var fileId = new Array();

        for(var x = 0; x < extList.length; x++){
            // 头标签
            html.push('<li class="inputTitle">' + extList[x].ext_name + '：</li>');

            //code 及 value
            var _code = extList[x].ext_code;
            var _defaultValue = extList[x].ext_defaultValue;
            var _value = extList[x].ext_value;
            //如果是第一次
            if(article.isNew==true || article.isNew=="true"){
                _value = _defaultValue || "" ;
            }

            var h = "";

            // 0=单行输入框;21=多行输入框;11=单选;14=多选;1=下拉菜单;38=附件上传
            if(parseInt(extList[x].ext_editType) == 0){
                // 0=单行输入框
                html.push('<li class="ext_text_li"><input class="ext_text validate[maxSize[255]]" type="text" id="' + _code + '" name="' + _code + '" value="' + _value + '"/></li>');
            }else if(parseInt(extList[x].ext_editType) == 21){
                // 21=多行输入框
                html.push('<li class="ext_textarea_li" ><textarea class="ext_textarea validate[maxSize[1024]]" id="' + _code + '" name="' + _code + '">'
                + _value + '</textarea></li>');
            }else if(parseInt(extList[x].ext_editType) == 11){
                //11=单选
                h = article_form.assembleRadioHtml(extList[x].ext_options, _value, _code);
                html.push(h);
            }else if(parseInt(extList[x].ext_editType) == 14){
                //11=多选
                h = article_form.assembleCheckboxHtml(extList[x].ext_options, _value, _code);
                html.push(h);
            }else if(parseInt(extList[x].ext_editType) == 1){
                h = article_form.assembleSelectHtml(extList[x].ext_options, _value, _code);
                html.push(h);

            }else if(parseInt(extList[x].ext_editType) == 38){
                // 稿件上传
                html.push('<li class="ext_file_li"><img  class="ext_file_img" id="img_' + _code
                + '" src="../../Icons/attach.gif" style="display:none; cursor:pointer;">'
                + '<input class="ext_file" type="file" id="' + _code + '" name="' + _code + '" oldvalue="' + extList[x].ext_value + '"></li>');
                fileId.push(_code);
            }
            else if(parseInt(extList[x].ext_editType) == 8){
                //日期
                html.push('<li class="ext_text_li"><input data-notModifyTop="true" data-notmodifyleft="true" type="text" id="' + _code + '" name="' + _code + '" value="' + _value
                + '" class="validate[custom[dateFormat]]" onfocus="$(this).validationEngine(\'hide\');" /></li>');
                article_form.ext_dateArray.push(_code);
            }
        }
        $("#extContentDiv").html(html.join(""));
        // 添加完之后初始化附件上传
        article_form.initOneFile(fileId);

        article_form.initExtDate();
    },

    //拼装单选的 html 标签
    assembleRadioHtml: function(ext_options, _value, _code){
        var html = new Array();
        // 11=单选;
        html.push('<li class="ext_radio_li">');
        if(ext_options != null && $.trim(ext_options) != ""){
            optionArr = ext_options.split(",");
            var checked = "";
            for(var y = 0; y < optionArr.length; y++){
                optionVar = optionArr[y].split("=");
                // 如果 等于ext_value，说明已经选中了该项
                if($.trim(optionVar[0]) == $.trim(_value)){
                    checked = "checked";
                }else{
                    checked = "";
                }
                html.push('<label class="ext_radio_label" for="' + _code + '_' + y + '"> '
                + '<input class="left ext_radio" type="radio" name="' + _code + '" id="' + _code + '_' + y
                + '" value="' + optionVar[0] + '" ' + checked + '><span class="left num">' + optionVar[0] + '</span></label>');
            }
        }
        html.push('</li>');

        return html.join("");
    },
    //拼装多选的html 标签
    assembleCheckboxHtml: function(ext_options, _value, _code){
        var html = new Array();
        // 14=多选;
        var checked = "";
        html.push('<li class="ext_checkbox_li">');
        if(ext_options != null
            && $.trim(ext_options) != ""){
            optionArr = ext_options.split(",");
            checkedArr = _value;
            // 把枚举值取出来做成 checkbox
            for(var y = 0; y < optionArr.length; y++){
                optionVar = optionArr[y].split("=");
                optionA = $.trim(optionVar[0]);
                // 如果 枚举值 = value，就加checked属性
                if(checkedArr.indexOf(optionA) != -1){
                    checked = "checked";
                }else{
                    checked = "";
                }
                html.push('<label class="ext_checkbox_label" for="' + _code + '_' + y
                + '"> <input class="ext_checkbox" type="checkbox" name="'
                + _code + '" id="' + _code + '_' + y + '" value="' + optionVar[0]
                + '" ' + checked + '><span class="num1">' + optionVar[0] + '</span></label>');
            }
        }
        html.push('</li>');

        return html.join("");

    },
    //拼装下拉框的html 标签
    assembleSelectHtml: function(ext_options, _value, _code){
        var html = new Array();
        // 1=下拉菜单 - 名=名
        html.push('<li class="ext_select_li"><select class="ext_select" id="' + _code + '" name="' + _code
        + '" oldvalue="-"><option></option>');
        var selected = "";
        if(ext_options != null
            && $.trim(ext_options) != ""){
            optionArr = ext_options.split(",");
            for(var y = 0; y < optionArr.length; y++){
                optionVar = optionArr[y].split("=");
                // 如果 枚举值 = value，就加selected属性
                if($.trim(optionVar[0]) == $.trim(_value)){
                    selected = "selected";
                }else{
                    selected = "";
                }
                html.push("<option value='" + optionVar[0] + "' " + selected + " >" + optionVar[0] + "</option>");
            }
        }
        html.push("</select></li>");
        return html.join("");
    },

    // 打开表单时，把已有附件用图标显示出来，并准备好下载方法
    initOneFile: function(fileId){
        for(var i = 0, size = fileId.length; i < size; i++){
            var file = $("#" + fileId[i]);

            var filePath = file.attr("oldValue");
            if(filePath && $.trim(filePath) != "" && filePath != "-"){
                var img = $("#img_" + file.attr("id"));
                // filePath:附件存储;201504/18/glj_<myfilename>
                var fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                fileName = fileName.substring(fileName.indexOf("_") + 1);
                img.attr("path", filePath).attr("title", fileName).click(
                    e5_form.file.download).show();
            }
        }
    },
    initExtDate : function(){
        var _dateArray = article_form.ext_dateArray;
        for(var i=0, size = _dateArray.length; i < size; i ++){
            //
            $('#'+_dateArray[i]).datetimepicker({
                language : 'zh-CN',
                weekStart : 0,
                todayBtn : 1,
                autoclose : 1,
                todayHighlight : true,
                startView : 2,
                minView : 2,
                disabledDaysOfCurrentMonth : 0,
                forceParse : 0,
                pickerPosition: "top-left",
                format : 'yyyy-mm-dd'
            });
        }

    },
    fileUploaded: false,
    uploadFileBeforeSubmit: function(){
        //如果 没上传进入，调用upload方法，然后就不再进了
        if(!article_form.fileUploaded){
            e5_form.file.upload();
            article_form.fileUploaded = true;
        }
        //看是否
        var ready = (e5_form.file.counter_uploaded == e5_form.file.counter_files);
        if(!ready){
            //setTimeout("article_form.uploadFileBeforeSubmit()",100);
            return false;
        }
        article_form.fileUploaded = false;
        return true;
    },
    findKeyword: function(){
        $("#findKeywordButton").attr("disabled", true);

        $.ajax({
            async: true,
            url: "../../xy/article/findKeyword.do",
            type: 'POST',
            data: {
                "title": $("#SYS_TOPIC").val(),
                "content": channel_frame.getContentTxt()
            },
            dataType: 'text',
            success: function(data, status){
                // 获取到扩展字段就展示， 否则就不展示
                if(data){
                    $("#a_keyword").val(data.replace(/\s/g,","));
                }
                $("#findKeywordButton").attr("disabled", false);
            },
            error: function(xhr, textStatus, errorThrown){
                $("#findSummaryButton").attr("disabled", false);
            }
        });
    },
    findSummary: function(){
        $("#findSummaryButton").attr("disabled", true);
        
        $.ajax({
            async: true,
            url: "../../xy/article/findSummary.do",
            type: 'POST',
            data: {
                "title": $("#SYS_TOPIC").val(),
                "content": channel_frame.getContentTxt()
            },
            dataType: 'text',
            success: function(data, status){
                // 获取到扩展字段就展示， 否则就不展示
                if(data){
                    $("#a_abstract").val(data);
                    $("#a_abstract").text(data);
                    $("#charCountShows").html($("#a_abstract").val().length);
                    //字数统计
                    var reg=/\<[\s\S]*?\>/g;
                    var _text=data.replace(reg,"");
                    //去除空格
                    var len = _text.replace(/\s/g,'').length;
                    $("#abstractCount").html(len + "/2000").css("color","#333");
                }
                $("#findSummaryButton").attr("disabled", false);
            },
            error: function(xhr, textStatus, errorThrown){
                $("#findSummaryButton").attr("disabled", false);
            }
        });
        

    }
}
/**
 * 来源
 * @type {{url: string, colId: string, isChosen: boolean, init: Function}}
 */
var article_source = {
    url: "../../xy/article/SourceByCache.do",
    colId: "",
    isChosen: false,
	dialog : null,
    init: function(){
        article_source.colId = article.colID;
		$("#btnSource").click(article_source.select);
        $('#findSourceInput').autocomplete({
            noCache: false,
            autoSelectFirst: true,
            paramName: 'query',
            showNoSuggestionNotice: false,
            noSuggestionNotice: "没有相匹配的来源",
            params: {"colID": article_source.colId},
            type: "POST",
            serviceUrl: article_source.url,
            onSelect: function(suggestion){
                $("#a_source").val(suggestion.value);
                $("#a_sourceID").val(suggestion.data);
                $("#a_sourceUrl").val(suggestion.url);
            },
            onInvalidateSelection: function(){
            }
        });
        var isCustom = article.sourceType;
        if(parseInt(isCustom) == 0){
            $("#findSourceInput").attr("disabled", true);
        }

        $("#findSourceInput").blur(function(){
            //$('#findSourceInput').autocomplete("hide");
            //不能手添, 判断输入框里的内容是否等于a_source，如果不相等，去掉id和输入框里的东西
            // if (parseInt(isCustom) == 0){

            //sourceType 为1 可手填 可选择  为0不可手填
            if (parseInt(isCustom) != 0){

                if($('#findSourceInput').val() != $('#a_source').val()){
                    $('#a_source').val($('#findSourceInput').val());
                    $('#a_sourceID').val("");
                }
            }
            //能手添的时候, 判断输入框里的内容是否等于a_source，如果不相等，去掉id，把输入框里的值交给a_source
            else{
                if($('#findSourceInput').val() != $('#a_source').val()){
                    //$('#findSourceInput').val("");
                    $('#a_source').val("");
                    $('#a_sourceID').val("");
                    $('#a_sourceUrl').val("");
                    //$('#findSourceInput').autocomplete("clear");
                }

            }
        });
    },
	select : function(evt) {
		var url = "../../xy/GroupSelect.do?type=4&siteID=" + article.siteID;
		var pos = {left : "100px",top : "50px",width : "1000px",height : "500px"};
		article_source.dialog = e5.dialog({type : "iframe", value : url}, {
			showTitle : true,
			title: "选择来源",
			width : "1000px",
			height : "500px",
			pos : pos,
			resizable : false
		});
		article_source.dialog.show();
	},
	//选择窗口：选定后
	groupSelectOK: function(docLibID, docID) {
		article_source.dialog.close();
		$.get("../source/findSource.do", {docLibID:docLibID, docID:docID}, function(data){
			$("#findSourceInput").val(data.name);
			$("#a_source").val(data.name);
			$("#a_sourceID").val(data.id);
			$("#a_sourceUrl").val(data.url);
		});
	},
	//选择窗口：取消后
	groupSelectCancel : function() {
        if(article_source.dialog) {
            article_source.dialog.close();
        }
	}
};
/**
 * 模板指定
 * 1. 点击切换时，显示select和确定按钮
 * 2. 点击确定时，显示模版名称和切换按钮，给相应的属性赋值
 */
var column_form = {
    dc: "pc",
    articletype: 0,
    switched: false,
    init: function(){
        $("#btnChgTpt_pc").click(column_form.switchButtonListen);
        $("#btnChgTpt_app").click(column_form.switchButtonListen);
    },
    
    winTmp : {},
    switchButtonListen: function(){
        var _dc = $(this).attr("data-dc");
        //隐藏模版名称
        $("#tptNameSpan_" + _dc).hide();
        //$("#btnChgTpt_" + _dc).hide();     
        var _type= 1;
        var _ckeckedID;
        var _channel;
		if(_dc=='pc'){
			_ckeckedID = document.getElementById("a_templateID").value;
			_channel = 0;
		}else{
			_ckeckedID = document.getElementById("a_templatePadID").value;
			_channel = 1;
		}

		if("undefined" != typeof t_type){ 
			if(undefined != t_type){
				_type=t_type;
			}
		}
		var _siteID = document.getElementById("a_siteID").value;
		var dataUrl = "xy/template/TemplateSelect.jsp?type=" + _type
				+ "&channel=" + _channel
				+ "&siteID=" + _siteID
				+ "&ckeckedID=" + _ckeckedID;
		dataUrl = e5_form.dealUrl(dataUrl);
		// 顶点位置
		// var pos = {left : "100px",top : "50px",width : "1000px",height : "500px"};
		column_form.winTmp = e5.dialog({
			type : "iframe",
			value : dataUrl
		}, {
			showTitle : false,
			width : "350px",
			height : "250px",
			resizable : false
		});
		column_form.winTmp.show();
	
        $("#templateSelect_" + _dc).show();
    },
    
    closeTemplate : function(channel, type, name, docID) {
		//赋值
        if(channel==0){
        	$("#templateSelect_pc").val(name);
            $("#a_template").val(name);
            $("#a_templateID").val(docID);
        }else{
        	$("#templateSelect_app").val(name);
            $("#a_templatePad").val(name);
            $("#a_templatePadID").val(docID);
        }
		column_form.winTmp.hide();
	},
	
	cancelTemplate : function(type) {	
		column_form.winTmp.hide();	
	},
    
};
/**
 * 话题指定
 * 1. 点击选择话题时，弹出话题选择框
 * 2. 点击确定时，给话题属性赋值
 */
var article_topic = {
    // dc: "pc",
    // articletype: 0,
    // switched: false,
    init: function(){
        $("#selectTopicButton").click(article_topic.selectTopicButtonListen);
    },

    winTmp : {},
    //点击选择话题按钮
    selectTopicButtonListen: function(){
        var _siteID = $("#a_siteID").val();
        var topicIDs = [];
        var topicNames = [];
        $("#a_topic li").each(function(){
            topicIDs.push($(this).attr("data"));
            topicNames.push($(this).attr("name"));
        });
        var topicIDString = JSON.stringify(topicIDs);
        var topicNameDString = JSON.stringify(topicNames);
        var dataUrl = "xy/articleTopic.do?siteID=" + _siteID +"&topicIDString=" + encodeURI(topicIDString) + "&topicNameDString=" + encodeURI(topicNameDString);
        dataUrl = e5_form.dealUrl(dataUrl);
        // 顶点位置
        // var pos = {left : "100px",top : "50px",width : "1000px",height : "500px"};
        article_topic.winTmp = e5.dialog({
            type : "iframe",
            value : dataUrl
        }, {
            showTitle : true,
            title : "话题选择",
            showClose : true,
            // lock:false,
            width : "850px",
            height : "600px",
            resizable : false
        });
        article_topic.winTmp.show();
    },

    closeTopic : function(html){
        $("#a_topic").html(html);
        article_topic.winTmp.hide();
    },

    cancelTopic : function() {
        article_topic.winTmp.hide();
    },

};
/**
 * 扩展字段 切换相关的方法
 */
var article_extField = {
    siteID: 0,
    switched: false,
    inited: false,
    //点击切换按钮的时候，隐藏扩展字段组名及切换按钮，显示select及确定按钮
    init: function(){

        //切换按钮
        $("#extChgBtn").click(function(){
            //隐藏扩展字段组名及切换按钮
            $("#extNameSpan").hide();
            $("#extChgBtn").hide();

            //显示取消按钮
            $("#extCancelBtn").show();

            if(!article_extField.inited){
                article_extField.extFieldSelectInit();
            }

            //显示select及确定按钮
            $("#selectExtField").show();

        });

        //取消按钮
        $("#extCancelBtn").click(function(){
            //隐藏select及确定按钮
            $("#selectExtField").hide();
            $(this).hide();
            //显示原扩展字段的名称
            $("#extNameSpan").html($("#oldExtFieldGroup").val());
            $("#extNameSpan").show();
            $("#extChgBtn").show();
            //重新初始化扩展字段 -
            //TODO 可做优化：把原扩展字段整体保存在一个div中，当需要取消操作时，再粘回来
            //如果无扩展字段，不做处理
            if($("#a_extFieldGroupID").val()=="0")
                return;
            $("#a_extFieldGroup").val($("#a_extFieldGroup").attr("data-val"));
            $("#a_extFieldGroupID").val($("#a_extFieldGroupID").attr("data-val"));
            article_form.initExtField(parent.article.colID);
        });

        $("#spanExtChange").click(function(){
            //判断先留着
            if(!article_extField.switched){
                //如果没有初始化，就查询一次
                if(!article_extField.inited){
                    article_extField.extFieldSelectInit();
                }
                //显示select，隐藏自定义
                $("#selectExtField").show();
                $("#customExtFieldSpan").hide();
                $("#selectExtField").change();
            }
        });
        //添加监听事件
        $("#selectExtField").change(article_extField.extFieldSelectChange);
    },
    //初始化 扩展字段select
    extFieldSelectInit: function(){
        $.ajax({
            url: "../../xy/article/findExtOption.do",
            async: false,
            data: {
                "siteID": article.siteID
            },
            dataType: 'json',
            success: function(datas){
                if(datas){
                    var html = new Array();
                    html.push("<option></option>");
                    var jsonList = datas.jsonArr;
                    for(var i = 0, size = jsonList.length; i < size; i++){
                        html.push("<option value='" + jsonList[i].code + "'>" + jsonList[i].name + "</option>");
                    }
                    $("#selectExtField").html(html.join(""));
                    article_extField.inited = true;
                }
            }
        });
    },
    //给扩展字段 select 添加监听
    extFieldSelectChange: function(){
        //如果选择空值
        if($("#selectExtField").val() == ""){
            $("#a_extFieldGroup").attr("data-val", $("#a_extFieldGroup").val());
            $("#a_extFieldGroupID").attr("data-val", $("#a_extFieldGroupID").val());
            //清空隐藏域
            $("#a_extFieldGroup").val("");
            $("#a_extFieldGroupID").val(0);
            //清空扩展字段
            $("#extContentDiv").html("");

            return;
        }

        $.ajax({
            url: "../../xy/article/changeExtOption.do",
            async: false,
            data: {
                "extGroupId": $("#selectExtField").val()
            },
            dataType: 'json',
            success: function(datas){
                if(datas){
                    article_form.handleExtFieldHtml(datas.jsonArr);
                }
            }
        });
        $("#a_extFieldGroup").val($("#selectExtField").find("option:selected").text());
        $("#a_extFieldGroupID").val($("#selectExtField").val());
    }
};


var article_sensitive = {
    //敏感词列表
    senList: [],
    illList: [],
    hasSensitive: function(sContent, _ta){//type: 写稿-0;  评论-1
        var bResult = false;
        $.ajax({
            async: false,
            url: "../../xy/wordList/checkSensitiveWord.do",
            type: 'POST',
            data: {
                "content": sContent,
                "title": "",
                "type": "0"
            },
            dataType: 'json',
            success: function(data){

                // 获取到扩展字段就展示，否则就不展示
                if(data.code == "1"){//或者有敏感词或者有非法词或者都有
                    var sensitive = data.sensitiveWord;
                    var illegal = data.illegalWord;
                    if (sensitive != null && sensitive != "" && sensitive.length != 0) {
                        article_sensitive.senList = data.sensitiveWord.keywords;
                        // 把本次的敏感词查询操作的返回结果集保存在localStorage
                        if(_ta == "标题")
                            article_sensitive._storage.senListTit = article_sensitive.senList;
                        else
                            article_sensitive._storage.senListCon = article_sensitive.senList;
                    }
                    if (illegal != null && illegal != "" && illegal.length != 0) {
                        article_sensitive.illList = data.illegalWord.keywords;
                        if(_ta == "标题")
                            article_sensitive._storage.illListTit = article_sensitive.illList;
                        else
                            article_sensitive._storage.illListCon = article_sensitive.illList;
                    }
                    bResult = true;
                } else if(data.code == "2"){//既没有敏感词也没有非法词
                    bResult = false;
                    if(_ta == "标题"){
                        article_sensitive._storage.illListTit = "";
                        article_sensitive._storage.senListTit = "";
                    } else{
                        article_sensitive._storage.illListCon = "";
                        article_sensitive._storage.senListCon = "";
                    }
                } else{
                    //code==0，出错
                    if(data.inform =="no content"){
                        if(_ta == "标题"){
                            article_sensitive._storage.illListTit = "";
                            article_sensitive._storage.senListTit = "";
                        } else{
                            article_sensitive._storage.illListCon = "";
                            article_sensitive._storage.senListCon = "";
                        }
                        return;
                    }
                    alert("Error！对不起，无法查询敏感词/非法词！");
                }
            },
            error: function(xhr, textStatus, errorThrown){
                alert("对不起，无法查询敏感词/非法词！");
            }
        });
        return bResult;
    },
    _storage: window.localStorage,
    //处理敏感词
    handleSensitive: function(_ta){
        if(_ta && _ta=="标题"){
            var _title = $("#SYS_TOPICDIV").html();
            _title = _title ? _title : $("#SYS_TOPIC").val();
            //敏感词标黄
            for(var _i = 0, _size = article_sensitive.senList.length; _i < _size; _i++){
                var _sen = article_sensitive._escape(article_sensitive.senList[_i]);
                _title = _title.replace(new RegExp(_sen, "gm"), '<span class="sensitiveWord">' + article_sensitive.senList[_i] + '</span>');
            }
            $("#SYS_TOPICDIV").html(_title);
        } else {
            // var editor = UE.getEditor("editor");
            var editor = article_sensitive.getUEditor();
            //原文章内容
            var content = editor.getContent();
            //把敏感词替换
            //敏感词标黄
            for(var _i = 0, _size = article_sensitive.senList.length; _i < _size; _i++){
                var _sen = article_sensitive._escape(article_sensitive.senList[_i]);
                content = content.replace(new RegExp(_sen, "gm"), '<span class="sensitiveWord">' + article_sensitive.senList[_i] + '</span>');
            }
            editor.setContent(content);
        }
    },
    cleanSenStyle: function() {
        var _title = $("#SYS_TOPIC").val();
        // var editor = UE.getEditor("editor");
        var editor = article_sensitive.getUEditor();
        //原文章内容
        var content = editor.getContent();
        var _senCon = article_sensitive._storage.senListCon,
            _senTit = article_sensitive._storage.senListTit;

        //把敏感词替换
        if(_senCon){
            var _senListCon = _senCon.split(",");
            for (var _i = _senListCon.length - 1; _i >= 0; _i--) {
                var _sen = article_sensitive._escape(_senListCon[_i]);
                content = content.replace(new RegExp('<span class="sensitiveWord">' + _sen + '</span>', "gm"), _sen);
            }

            article_sensitive.getUEditor().setContent(content);
        }
        if(_senTit) {
            var _senListTit = _senTit.split(",");
            for (var _i = _senListTit.length - 1; _i >= 0; _i--) {
                var _sen = article_sensitive._escape(_senListTit[_i]);
                _title = _title.replace(new RegExp('<span class="sensitiveWord">' + _sen + '</span>', "gm"), _sen);
            }
            $("#SYS_TOPICDIV").html(_title);
        }

    },
    //处理非法词
    handleIllegal: function(_ta){
        if(_ta && _ta=="标题"){
            var _title = $("#SYS_TOPICDIV").html();
            _title = _title ? _title : $("#SYS_TOPIC").val();
            //非法词标红
            for (var _i = 0, _size = article_sensitive.illList.length; _i < _size; _i++) {
                var _ill = article_sensitive._escape(article_sensitive.illList[_i]);
                _title = _title.replace(new RegExp(_ill, "gm"), '<span class="illegalWord">' + article_sensitive.illList[_i] + '</span>');
            }
            $("#SYS_TOPICDIV").html(_title);
        } else {
            // var editor = UE.getEditor("editor");
            var editor = article_sensitive.getUEditor();
            //原文章内容
            var content = editor.getContent();
            //把非法词替换
            //非法词标红
            for (var _i = 0, _size = article_sensitive.illList.length; _i < _size; _i++) {
                var _ill = article_sensitive._escape(article_sensitive.illList[_i]);
                content = content.replace(new RegExp(_ill, "gm"), '<span class="illegalWord">' + article_sensitive.illList[_i] + '</span>');
            }
            article_sensitive.getUEditor().setContent(content);
        }
    },
    cleanIllStyle: function(){
        var _title = $("#SYS_TOPIC").val();
        // var editor = UE.getEditor("editor");
        var editor = article_sensitive.getUEditor();
        //原文章内容
        var content = editor.getContent();
        var _illCon = article_sensitive._storage.illListCon,
            _illTit = article_sensitive._storage.illListTit;
        if(_illCon){
            var _illListCon = _illCon.split(",");
            //把非法词替换
            for(var _i = _illListCon.length - 1; _i >= 0; _i--){
                var _ill = article_sensitive._escape(_illListCon[_i]);
                content = content.replace(new RegExp('<span class="illegalWord">' + _ill + '</span>', "gm"), _ill);
                content = content.replace(new RegExp('<span class="illegalWord">', "gm"), '');
            }
            article_sensitive.getUEditor().setContent(content);
        }
        if(_illTit){
            var _illListTit = _illTit.split(",");
            for(var _i = _illListTit.length - 1; _i >= 0; _i--){
                var _ill = article_sensitive._escape(_illListTit[_i]);
                _title = _title.replace(new RegExp('<span class="illegalWord">' + _ill + '</span>', "gm"), _ill);
                _title = _title.replace(new RegExp('<span class="illegalWord">', "gm"), '');
            }
            $("#SYS_TOPICDIV").html(_title);
        }
    },
    getUEditor : function () {
        if (typeof UE == "undefined") return "";
        var editorName = channel_frame._useEditor() ? "editor" : "simpleEditor";
        var editor = UE.getEditor(editorName);
        return editor;
    },
    //字符转义
    escape:function(_sen) {
        _sen=_escape(_sen);
        _sen=_sen.replace(/\\/g, '\\\\');
        return _sen;
    },
    //字符转义
    _escape:function(_sen) {
    	_sen=_sen.replace(new RegExp('\\(', "gm"), '\\\(');
    	_sen=_sen.replace(new RegExp('\\)', "gm"), '\\\)');
    	_sen=_sen.replace(new RegExp('\\[', "gm"), '\\\[');
    	_sen=_sen.replace(new RegExp('\\]', "gm"), '\\\]');
    	_sen=_sen.replace(new RegExp('\\+', "gm"), '\\\+');
    	_sen=_sen.replace(new RegExp('\\*', "gm"), '\\\*');
    	_sen=_sen.replace(new RegExp('\\.', "gm"), '\\\.');
    	_sen=_sen.replace(new RegExp('\\?', "gm"), '\\\?');
    	_sen=_sen.replace(new RegExp('\\$', "gm"), '\\\$');
    	_sen=_sen.replace(new RegExp('\\^', "gm"), '\\\^');
    	_sen=_sen.replace(new RegExp('\\{', "gm"), '\\\{');
    	_sen=_sen.replace(new RegExp('\\}', "gm"), '\\\}');
    	_sen=_sen.replace(new RegExp('\\|', "gm"), '\\\|');
    	_sen=_sen.replace(new RegExp('\\/', "gm"), '\\\/');
    	return _sen;
    }
}

//行业分类
var article_trade = {
	dialog : {},
	init : function() {
		$("#btnTrade").click(article_trade.showTrade);
	},
	_transformCatIDs : function(catIDs) {
		if (!catIDs) return catIDs;

		var catIDArr = catIDs.split(",");
		catIDs = "";
		for (var i = 0; i < catIDArr.length; i++) {
			var idArr = catIDArr[i].split("~");
			if ((idArr.length > 0) && idArr[idArr.length - 1])
				catIDs += idArr[idArr.length - 1] + ",";
		}
		if (catIDs) catIDs = catIDs.substring(0, catIDs.length - 1);
	
		return catIDs;
	},
	showTrade : function() {
		var _siteID = document.getElementById("a_siteID").value;
	    var _trades = article_trade._transformCatIDs(document.getElementById("a_tradeID").value);
	    var dataUrl = "e5workspace/manoeuvre/CatSelect.do?catType="+article.tradeCatType+"&rootIDs="+article.tradeRootIDs
	    +"&noRoot=true&noPermission=1"+ "&catIDs=" + _trades;
	    dataUrl = e5_form.dealUrl(dataUrl);
	    article_trade.dialog = e5.dialog({
	            type : "iframe",
	            value : dataUrl
	        }, {
	            showTitle : true,
	            title : "选择标签",
	            width : "550px",
	            height : "500px",
	            resizable : true,
	            showClose : true,
	            lock:false,
	        });
	    article_trade.dialog.show();
	}
}
function catWindowSelect(catIDs, catNames, cascadeIDs)
{
	$("#a_trade").val(catNames);
	$("#a_tradeID").val(cascadeIDs);
	article_trade.dialog.close();
}

function catWindowHidden(){
	article_trade.dialog.close();
}

var oldKey,oldUpload,oldPath,imgLiList;
//专题模板选择、话题选择
var article_special = {
	type : 0,
	dialog : null,
	init : function() {
		$("#btnSpecialTemplate").click(article_special.design);	//专题稿的模板设计按钮
		$("#btnSpeciaPreview").click(article_special.preview);	//专题稿的模板设计按钮
		$("#btnSubject").click(article_special.subject);	//问答稿的选择话题按钮
        $("#btnLiveTemplate").click(article_special.live);	//问答稿的选择话题按钮
        this.initPicPlugin();

        //初始化摘要字数
        // countNum($("#a_abstract"));
        function countNum(obj){
            if(obj.val()){
                var len = obj.val().length,
                    _text;
                //过滤摘要字数统计 去掉样式统计
                var contents = $("#a_abstract").val();
                var reg=/\<[\s\S]*?\>/g;
                // if(reg.test(contents)){
                //直接去替换
                _text=contents.replace(reg,"");
                //去掉中间前后的空格
                len = _text.replace(/\s/g,'').length;
                // }
                // if(len > 2000){
                //     len = 2000;
                //     // obj.val(_text.substring(0,100));
                // }
                // }
                $("#abstractNum").text("摘要字数:"+ len +"/2000")
            }
        }


        //摘要字数限制
        $("#a_abstract").on("input",function(){
            // console.log(000)
            // countNum($(this))
            // var len = $(this).val().length;
            // if($("#picLoad").length > 0){
        		//  if(len > 100){
        		//  	 $(this).val($(this).val().substring(0,100));
        		//  }
            // }
            // $("#abstractNum").text("摘要字数:"+$(this).val().length+"/100")

            // var len = $(this).val().length,
            //     _text;
            // //过滤摘要字数统计 去掉样式统计
            // var contents = $(this).val();
            // var reg=/\<[\s\S]*?\>/g;
            //     //直接去替换
            //     _text=contents.replace(reg,"");
            //     len = _text.length;
            // if(len > 100){
            //     len = 100;
            //     $(this).val(_text.substring(0,100));
            // }
            // $("#abstractNum").text("摘要字数:"+ len +"/100")

        })
	},
    //初始化图片上传组件
    upload:null,
    initPicPlugin: function(){
        var param = {
            uploadUrl: '../../xy/upload/uploadFileF.do',
            deleteUrl: "../../xy/upload/deletePreviewThumb.do",
            showContent: false,
            autoCommit: true,
            showEditor: true,
            maxFileCount:1,
            allowedFileExtensions:['jpg', 'jpeg', 'gif', 'bmp', 'png'],
            showRemove: false,
            showUpload: false,
            showSort: true,
            rootUrl: '../../'
        };
        param.fileUploaded = function(e, data){
            $("#columnIcon").val(data.path);
        };
        //点击修改
        param.modifyPicture = function(key, path, _upload,_list){
            oldKey=key;
            oldPath=path;
            oldUpload=_upload;
            imgLiList=_list;
            article_special.imageEditor();
        };
        var _imgsrc = $("#columnIcon").val();
        if(_imgsrc && $.trim(_imgsrc) != ""){
            var initialPreview = [];
            var initialPreviewConfig = [];
            if($.trim(_imgsrc).indexOf("http")==0){
                initialPreview.push(_imgsrc);
            }else{
                initialPreview.push("../../xy/image.do?path=" + _imgsrc + ".0.jpg");
            }
            initialPreviewConfig.push({url: "../../xy/upload/deletePreviewThumb.do",
                imagePath:_imgsrc,
                key: 0});
            param.initialPreview = initialPreview;
            param.initialPreviewConfig = initialPreviewConfig;
        }
        if(article.isNew!="true"){
            param.showEditor=false;
        }
        param.completed=function(){
            if(article.isNew!="true"){
                $(".file-drag-handle").hide();
                $(".kv-file-edit").hide();
                $(".kv-file-remove").hide();
            }
        };
        article_special.upload = new Upload("#uploadInput-special",param);
        if(article.isNew!="true"){
            $(".file-drag-handle").hide();
            $(".kv-file-edit").hide();
            $(".kv-file-remove").hide();
        }
    },
	//专题模板设计按钮
	design : function(event) {
		//隐藏input目录框上的验证信息
		$("#a_template").validationEngine("hide");
		
		var url = "../../xy/GroupSelect.do?type=2&siteID=" + article.siteID;
		var pos = {left : "100px",top : "50px",width : "1000px",height : "500px"};
		article_special.dialog = e5.dialog({type : "iframe", value : url}, {
			showTitle : true,
			title: "选择专题设计",
			width : "1000px",
			height : "500px",
			pos : pos,
			resizable : false
		});
		article_special.type = 0;
		article_special.dialog.show();
	},

    //专题模板预览按钮
    preview : function(event) {
        //隐藏input目录框上的验证信息
        $("#a_template").validationEngine("hide");
        var temID = $("#a_templateID").val()
        if (temID.length==0 || temID==0 ) alert("请先选择专题！")
        else {
            var url = "../../xy/special/specialPreview.do?a_templateID=" + temID;
            var pos = {left: "100px", top: "50px", width: "1000px", height: "500px"};
            article_special.dialog = e5.dialog({type: "iframe", value: url}, {
                showTitle: true,
                title: "专题预览",
                width: "1500px",
                height: "1000px",
                pos: pos,
                resizable: false
            });
            article_special.type = 0;
            article_special.dialog.show();
        }
    },

    //专题模板设计按钮
    live : function(event) {
        //隐藏input目录框上的验证信息
        $("#a_template").validationEngine("hide");

        var url = "../../xy/SimpleSelect.do?type=0&siteID=" + article.siteID;
        var pos = {left : "100px",top : "50px",width : "1000px",height : "500px"};
        article_special.dialog = e5.dialog({type : "iframe", value : url}, {
            showTitle : true,
            title: "选择直播话题",
            width : "1000px",
            height : "500px",
            pos : pos,
            resizable : false
        });
        article_special.type = 2;
        article_special.dialog.show();
    },
    imageEditor:function(event){
        //隐藏input目录框上的验证信息
        $("#a_template").validationEngine("hide");

        var url = "../../xy/script/picupload/imageeditor.jsp";
        var pos = {left : "100px",top : "50px",width : "1000px",height : "500px"};
        article_special.dialog = e5.dialog({type : "iframe", value : url}, {
            showTitle : true,
            title: "图片编辑",
            width : "1000px",
            height : "500px",
            pos : pos,
            resizable : false
        });
        //article_special.type = 2;
        article_special.dialog.show();
    },
	subject : function(event) {
		//隐藏input目录框上的验证信息
		$("#a_template").validationEngine("hide");
		
		var url = "../../xy/GroupSelect.do?type=3&siteID=" + article.siteID;
		var pos = {left : "100px",top : "50px",width : "1000px",height : "500px"};
		article_special.dialog = e5.dialog({type : "iframe", value : url}, {
			showTitle : true,
			title: "选择话题",
			width : "1000px",
			height : "500px",
			pos : pos,
			resizable : false
		});
		article_special.type = 1;
		article_special.dialog.show();
	},
	//选择窗口：选定后
	groupSelectOK: function(docLibID, docID) {
		article_special.dialog.close();
		if (article_special.type == 0) {
			$.get("../special/findTemplate.do", {docLibID:docLibID, docID:docID}, function(data){
				$("#a_templateID").val(data.pcId);
				$("#a_template").val(data.name);
				$("#a_templatePadID").val(data.padId);
				$("#a_templatePad").val(data.name);
			});
		} else if (article_special.type == 1) {
			$.get("../nis/findSubject.do", {docLibID:docLibID, docID:docID}, function(data){
				$("#a_template").val(data);
				$("#a_linkID").val(docID);
			});
		}else if (article_special.type == 2) {
            $.get("../nis/findSubject.do", {docLibID:docLibID, docID:docID}, function(data){
                $("#a_linkName").val(data);
                $("#a_linkID").val(docID);
            });
        }
	},
	//选择窗口：取消后
	groupSelectCancel : function() {
        if(article_special.dialog) {
            article_special.dialog.close();
        }
	}
}
article_pic = {
    upload: null,
    init: function(){
        var param = {
            uploadUrl: '../../xy/upload/uploadFileF.do',
            maxFileCount:50,
            showSort: true,
            showContent: true,
            autoCommit: true,
            showApplyAll: true,
            showEditor: true,
            rootUrl: '../../'
        };
        param.completed = function(){
            article_pic.upload.$selector.closest(".file-input").find(".file-footer-buttons").each(function(){
                if($(this).find(".kv-file-picture").size() == 0){
                    $(this).prepend('<button type="button" class="kv-file-picture btn btn-xs btn-default" title="设置标题图"><i class="glyphicon glyphicon-picture"></i></button>');
                }
            });

            if($("#picUploadDiv").find(".btn-piclib").size() == 0){
                $("#picUploadDiv").find(".btn-file").after("<button type='button' class='btn btn-primary btn-lg btn-piclib' style='margin-left: 10px;'>图片库</button>");
            }
        };
        param.fileDeleted = function(){
            if(article_pic.upload.getDataList().length <= 0){
                $("#picUploadDiv").find(".btn-file").after("<button type='button' class='btn btn-primary btn-lg btn-piclib' style='margin-left: 10px;'>图片库</button>");

            }

        }
        //点击修改
        param.modifyPicture = function(key, path, _upload,_list){
            oldKey=key;
            oldPath=path;
            oldUpload=_upload;
            imgLiList=_list;
            article_pic.imageEditor();
        };
        article_pic.upload = new Upload("#picUploadInput",param);

        $("#picUploadDiv").on("click",".kv-file-picture", function(){
            var key = $(this).siblings(".kv-file-remove").attr("data-key");
            var list = article_pic.upload.getDataList();
            for(var i = 0, li = null ;li = list[i++]; ){
                if(li.key == +key){
                    var url = '../../xy/ueditor/initTitleDialog.do?imagePath=' + li.imagePath + "&itype=all";
                    var title = "设置标题图片";
                    var dialogName = "titleDialog";

                    showOneDialog(dialogName, url, title);
                }
            }
        });

        function showOneDialog(dialogName, url, title) {
            var scrollTop = document.documentElement.scrollTop||document.body.scrollTop;
            var pos = {
                left : "100px",
                top : scrollTop + "px",
                width : "1000px",
                height : "500px"
            };
            // dialog
            channel_frame[dialogName] = e5.dialog({
                type : "iframe",
                value : url
            }, {
                showTitle : true,
                title: title,
                width : "1000px",
                height : "560px",
                pos : pos,
                resizable : false
            });
            channel_frame[dialogName].show();
        }

        setTimeout(function(){
            $("#picUploadDiv").find(".btn-file").after("<button type='button' class='btn btn-primary btn-lg btn-piclib' style='margin-left: 10px;'>图片库</button>");
        }, 350);

        $("#picUploadDiv").on("click",".btn-piclib", function(){
            var url = '../GroupSelect.do?type=0&siteID=' +
                article.siteID;
            var title = "图片库";
            var dialogName = "picLibDialog";
            showOneDialog(dialogName, url, title);
        });

    },
    imageEditor:function(event){
        //隐藏input目录框上的验证信息
        $("#a_template").validationEngine("hide");

        var url = "../../xy/script/picupload/imageeditor.jsp";
        var pos = {left : "100px",top : "50px",width : "1000px",height : "500px"};
        article_pic.dialog = e5.dialog({type : "iframe", value : url}, {
            showTitle : true,
            title: "图片编辑",
            width : "1000px",
            height : "500px",
            pos : pos,
            resizable : false
        });
        //article_special.type = 2;
        article_pic.dialog.show();
    },
    getPics: function(){
        /*var jsonParam = {
            "isIndexed":isIndex,// 是否索引图
            "path":$(div0[1]).children("input").val(),// 图片路径
            "content":$(div0[2]).children("textarea").val()// 图片说明
        }*/

        var list = article_pic.upload.getDataList();
        var pics = [];
        var isIndexed = 2;
        for(var i = 0, li = null; li = list[i++]; ){
        	var imgurl = channel_frame._imgSrc(li.imagePath);
            pics.push({
                path : imgurl,
                content: li.caption,
                isIndexed: 2
            });
            
        }
        return pics;
    },
    setPics: function(list){
        var param = {};
        var initialPreview = [];
        var initialPreviewConfig = [];
        var indexed = null;
        for(var i = 0, li = null; li = list[i++];){
            initialPreview.push("../../xy/image.do?path=" + li.path + ".0.jpg");
            initialPreviewConfig.push({url: "../../xy/upload/deletePreviewThumb.do",
                imagePath: li.path,
                key: i, caption: li.content});
            if(+li.isIndexed == 0 ){
                indexed = i;
            }

        }
        param.initialPreview = initialPreview;
        param.initialPreviewConfig = initialPreviewConfig;
        article_pic.upload.setOption(param);
        setTimeout(function(){
            var obj = article_pic.upload
            if(obj.option.showContent){
                obj.$selector.closest(".file-input").find(".file-footer-caption").addClass("contentStyle");//.css("width", obj.$selector.closest(".file-input").find(".kv-file-content").css("width"));
                obj.$selector.closest(".file-input").find(".file-footer-caption").each(function(){
                    $(this).css("width", $(this).parent().css("width"));
                    $(this).parent().css("height", "45px");
                    $(this).attr("placeholder", "请输入描述内容...");
                    var _content = $(this).html();
                    _content = _content.replaceAll("<br>", "");
                    $(this).html(_content);
                });
            } else{
                obj.$selector.closest(".file-input").addClass("tag-hide");
            }

            if(obj.option.showApplyAll){
                obj.$selector.closest(".file-input").find(".file-footer-buttons").each(function(){
                    if($(this).find(".kv-file-fresh").size() == 0){
                        $(this).prepend('<button type="button" class="kv-file-fresh btn btn-xs btn-default" title="同步图片说明"><i class="glyphicon glyphicon-transfer"></i></button>');
                    }
                });
            }

            if(obj.option.showEditor){
                obj.$selector.closest(".file-input").find(".file-footer-buttons").each(function(){
                    if($(this).find(".kv-file-edit").size() == 0){
                        $(this).prepend('<button type="button" class="kv-file-edit btn btn-xs btn-default" title="修改"><i class="glyphicon glyphicon-pencil"></i></button>');
                    }
                });
            }
            if(obj.option.showTitleImage){
                obj.$selector.closest(".file-input").find(".file-footer-buttons").each(function(){
                    $(this).prepend('<input type=radio class="kv-radio" title="设置标题图" name="indexed" style="margin-right: 2px; cursor:pointer;"/>');
                });
            }
            obj.$selector.closest(".file-input").find(".file-footer-buttons").each(function(){
                if($(this).find(".kv-file-picture").size() == 0){
                    $(this).prepend('<button type="button" class="kv-file-picture btn btn-xs btn-default" title="设置标题图"><i class="glyphicon glyphicon-picture"></i></button>');
                }
            });
        }, 350);

    },
    //选择窗口：取消后
    groupSelectCancel : function() {
        if(article_pic.dialog) {
            article_pic.dialog.close();
        }
    }
}

//回调函数
function groupSelectOK(docLibID, docID, type){
	if (type == 4) {
		article_source.groupSelectOK(docLibID, docID);
	} else {
		article_special.groupSelectOK(docLibID, docID);
	}
}

function groupSelectCancel(type){
	if (type == 4) {
		article_source.groupSelectCancel();
		return;
	}
	article_special.groupSelectCancel();
	article_pic.groupSelectCancel();
}

online_image = {
    //图片库选择：“确定”按钮
    picClose: function(docLibID, docIDs, imgPath){
        if (!docIDs) return;
        docIDs = docIDs.split(",");

        var list = [];
        for (var i = 0; i < docIDs.length; i++) {
            //根据索引图的DocLibID，DocID获取到这组的所有组图
            $.ajax({
                url: "../../xy/pic/getPics.do",
                dataType: "json",
                async: false,
                data : {
                    "DocID" : docIDs[i],
                    "DocLibID" : docLibID
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    alert(errorThrown + ':' + textStatus);
                },
                success: function (data) {
                    if(data && data instanceof Array){
                        list = list.concat(data);
                    }
                }
            });
        }
        var oldList = article_pic.upload.getDataList();
        var _list =[];
        if(!(oldList && oldList instanceof Array)){
            oldList = [];
        }

        for(var i = 0, li ; li = oldList[i++];){
            _list.push({
                path: li.imagePath,
                content: li.caption
            });
        }

        _list = _list.concat(list);
        article_pic.setPics(_list);
        article_pic.upload.resetWidth();
        if($("#picUploadDiv").find(".btn-piclib").size() == 0){
            $("#picUploadDiv").find(".btn-file").after("<button type='button' class='btn btn-primary btn-lg btn-piclib' style='margin-left: 10px;'>图片库</button>");
        }
        channel_frame["picLibDialog"].hide();
    },

    //图片库选择：“取消”按钮
    picCancel: function(){
        channel_frame["picLibDialog"].hide();
    }
};

//图片编辑之后回调
function resetImageMsg(key,path,showPath,_upload,imageList){
    _upload.doModifyPicture(key, path, showPath,imageList);
    //专题 编辑后的图片地址写到隐藏input
    if(_upload.$selector.attr("id")=="uploadInput-special"){
        _upload.$selector.closest("#divMain").find("#columnIcon").val(path);
    }
}