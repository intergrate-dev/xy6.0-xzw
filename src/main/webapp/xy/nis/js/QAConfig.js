//定制表单的扩展js：稿件扩展字段
var chooseObject;
var hoverObject ;
var trDiv = "<tr ondblclick='group_form.dbChooseOption(this)' onclick='group_form.chooseOption(this)'>"+
    "<td style='width:100%;margin-left:0;'>"+
    "<div style='width:100%;float:left'>"+
    "<span style='margin-left:10px'></span>"+
    "<span onclick='group_form.removeEnumTwo()' style='display:none;margin-right:10px;float:right' class='glyphicon glyphicon-remove-sign'></span>" +
    "</div>"+
    "<div style='display:none;float:left'>"+
    "<input style='float:left;width:150px;height:20px;margin-right:0' type='text'>"+
    "<button  style='float:left;height:20px;width:17px'>"+
    "<span class='glyphicon glyphicon-ok'></span>"+
    "</button>"+
    "<button  style='float:left;height:20px;width:17px'>"+
    "<span class='glyphicon glyphicon-remove'></span>"+
    "</button>"+
    "</div>" +
    "</td>"+
    "</tr>" ;
var group_form = {
    init: function(){
        group_form.sortable() ;
        group_form.keyDiv() ;
        group_form.optionInit() ;
        $("#form").validationEngine({
			autoPositionUpdate:true,
			promptPosition:"bottomLeft",
			scroll:true
		});
    },
    /**
     * 初始化时，如果ext_options 有值，把这些值显示到枚举值上
     */
    optionInit: function(){
        //获取ext_options的值
        var config = $("#qaConfig").val();
        if (!config) return;
        config = eval("(" + config + ")");
        var optionsArr = config.askTo;

        if(optionsArr != null && optionsArr != ""){
            for(var i = 0, size = optionsArr.length; i < size; i++){
                var option = optionsArr[i].split("=");
                group_form.addTr(option[0]) ;
            }
        }
    },
    /**
     * 新增输入框
     */
    keyDiv :function(){
        // 添加给新增输入框input一个enter的监听，当用户点击enter之后，给ext_options赋值
        $("#keyInput").keypress(function(event){
            if(event.keyCode == 13){
                // 防止其他监听触发
                event.preventDefault();
                event.stopPropagation();
                // 赋值
                group_form.assignEnum();
                group_form.addEnum();
            }
        });
        $("#keyInput").blur(function(){
            // 防止其他监听触发
            event.preventDefault();
            event.stopPropagation();
            // 赋值
            group_form.assignEnum();
        });
        $("#keyOk").click(function(){
            // 防止其他监听触发
            event.preventDefault();
            event.stopPropagation();
            // 赋值
            group_form.assignEnum();
            group_form.addEnum();
        }) ;
        $("#keyRemove").click(function(){
            $("#keyInput").val("") ;
            $("#keyDiv").hide();
        }) ;
    },

    /**
     * 枚举值拖拽排序
     */
    sortable : function(){
        $("#contentTable").sortable({
            axis : 'y' ,
            items : "tr" ,
            revert : true ,
            stop : function(event){
                group_form.resetEnum() ;
            }
        });
        $( "#contentTable" ).disableSelection();
    },

    /**
     * 点击+号时触发,显示input输入框，并且聚焦
     */
    addEnum: function(){
        // 1. 点击+时，显示input输入框，并且聚焦
        $("#keyDiv").show();
        $("#keyInput").focus();
    },

    /**
     * 点击-号时触发
     */
    removeEnum: function(){
        $(chooseObject).remove();
        group_form.resetEnum() ;
    },

    /**
     * 点击枚举值后的删除标志时触发
     */
    removeEnumTwo: function(){
        $(hoverObject).remove();
        group_form.resetEnum() ;
    },

    /**
     * 新增枚举值
     */
    assignEnum: function(){
        // 判断用户是否没有填写任何值就进行提交了
        if($.trim($("#keyInput").val()) == ""){
            $("#keyDiv").hide();
            return;
        }
        var keyInputVar = $.trim($("#keyInput").val());
        group_form.addTr(keyInputVar) ;
    },

    /**
     * 点击枚举值中一个选择时
     * @param trObject
     */
    chooseOption :function(trObject){
        chooseObject = trObject;
        $(document.activeElement).blur() ;
        $("#contentTable tr").removeClass("chosenBackground");
        $(chooseObject).addClass("chosenBackground");
    },

    /**
     * 双击枚举值中一个选择时
     * @param trObject
     */
    dbChooseOption: function(trObject){
        chooseObject = trObject;
        var old = $.trim($(chooseObject).find("td").text()) ;
        $("#contentTable tr").removeClass("chosenBackground");
        $(chooseObject).addClass("chosenBackground");
        var firstDiv = $(chooseObject).find("div").eq(0) ;
        var secondDiv = $(chooseObject).find("div").eq(1) ;
        var input = secondDiv.find("input");
        var okSpan = secondDiv.find("span").eq(0) ;
        var removeSpan = secondDiv.find("span").eq(1) ;
        firstDiv.hide() ;
        secondDiv.show() ;
        input.focus() ;
        input.attr('placeholder',old);
        input.keypress(function(event){
            if(event.keyCode == 13){
                // 防止其他监听触发
                event.preventDefault();
                event.stopPropagation();
                var ne = input.val() ;
                if(ne != ""){
                    firstDiv.find("span").eq(0).text(ne) ;
                }
                input.val("") ;
                firstDiv.show() ;
                secondDiv.hide() ;
                group_form.resetEnum() ;
            }
        });
        input.blur(function(event){
            // 防止其他监听触发
            event.preventDefault();
            event.stopPropagation();
            var ne = input.val() ;
            if(ne != ""){
                firstDiv.find("span").eq(0).text(ne) ;
            }
            input.val("") ;
            firstDiv.show() ;
            secondDiv.hide() ;
            group_form.resetEnum() ;
        }) ;
        okSpan.click(function(event){
            // 防止其他监听触发
            event.preventDefault();
            event.stopPropagation();
            var ne = input.val() ;
            if(ne != ""){
                firstDiv.find("span").eq(0).text(ne) ;
            }
            input.val("") ;
            firstDiv.show() ;
            secondDiv.hide() ;
            group_form.resetEnum() ;
        });
        removeSpan.click(function(event){
            // 防止其他监听触发
            event.preventDefault();
            event.stopPropagation();
            firstDiv.show() ;
            secondDiv.hide() ;
        });
    },

    addTr : function(obj){
        $("#keyDiv").hide();
        $("#contentTable").append(trDiv);
        $("#contentTable tr:last").find("span").eq(0).text(obj) ;
        $("#contentTable tr:last").hover(function(){
            $(this).find("span").eq(1).show() ;
            hoverObject = this ;
        },function(){
            $(this).find("span").eq(1).hide() ;
            hoverObject = null ;
        }) ;

        group_form.moveTo($("#keyInput"));
        $("#keyInput").val("");
        group_form.resetEnum() ;
    },

    resetEnum:function(){
        var optionArr = $("#contentTable td");
        var optionValues = "";
        // 获取所有td的值
        for(var i = 0, size = $("#contentTable td").length; i < size; i++){
            optionTd = $.trim($("#contentTable td").eq(i).text());
            optionValues += optionTd + "=" + optionTd + ",";
        }
        // 把值赋给ext_options
        optionValues = optionValues.substr(0, optionValues.length - 1);
        $("#ext_options").val(optionValues);
    },

    moveTo: function($obj){
        $('html,body').animate({scrollTop: $obj.offset().top}, "fast");
    },

    ext_optionsVar: ""
}
var qa = {
    upload: null,
    initPicPlugin: function(_imgsrc){
        var param = {
            uploadUrl: '../../xy/upload/uploadFileF.do',
            deleteUrl: "../../xy/upload/deletePreviewThumb.do",
            showContent: false,
            autoCommit: true,
            showEditor: false,
            allowedFileExtensions:['jpg', 'jpeg', 'gif', 'bmp', 'png'],
            maxFileCount:1,
            showRemove: false,
            showUpload: false,
            showSort: true,
            rootUrl: '../../'
        };
        if(_imgsrc && $.trim(_imgsrc) != ""){
            var initialPreview = [];
            var initialPreviewConfig = [];
            if($.trim(_imgsrc).indexOf("http")==0){
                initialPreview.push(_imgsrc);
            }else{
                initialPreview.push("../../xy/image.do?path=" + _imgsrc);
            }
            initialPreviewConfig.push({
                url: "../../xy/upload/deletePreviewThumb.do",
                imagePath:_imgsrc,
                key: 0
            });
            param.initialPreview = initialPreview;
            param.initialPreviewConfig = initialPreviewConfig;
        }
        qa.upload = new Upload("#uploadInput",param);
    }
}
function beforeSubmit() {
    var list = qa.upload.getDataList();
    if(list.length){
        var _imgsrc=list[0].imagePath;
    }

    var optionsArr = $("#ext_options").val().split(",");
    var config = {
        "wordcount_title" : $("#wordcount_title").val().trim() != "" ? $("#wordcount_title").val():100,
        "wordcount_content" : $("#wordcount_content").val().trim() != "" ? $("#wordcount_content").val():1024,
        "askTo" : optionsArr,
        "defaultIcon" : _imgsrc
    }
    config = JSON.stringify(config);
    $("#qaConfig").val(config);
}
function doInit(){
    var config = $("#qaConfig").val();
    if (!config){qa.initPicPlugin(config.defaultIcon);return;}

    config = eval("(" + config + ")");
    $("#wordcount_title").val(config.wordcount_title);
    $("#wordcount_content").val(config.wordcount_content);
    $("#ext_options").val(config.askTo);

    qa.initPicPlugin(config.defaultIcon);
}
$(function(){
    group_form.init();
});
