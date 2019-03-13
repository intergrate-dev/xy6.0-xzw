$(function(){
	xy_subject.init() ;
})
var xy_subject = {
	init : function(){
		xy_subject.initPicPlugin() ;
		xy_subject.initSub() ;
		$("#btnSave").unbind().click(xy_subject.doSave);
        $("#btnCancel").click(xy_subject.doCancel);
        $("#btnUser").click(xy_memSelect.memSelect) ;
		$("#a_answerer").prop("readonly", true);
	},
	//初始化图片插件
	initPicPlugin : function(){
		xy_subject.appPicUpload = new Upload("#appPicInput", {
            uploadUrl: "../../xy/upload/uploadFileF.do",
            showRemove: false,
            showUpload: false,
            showSort: false,
            allowedFileExtensions:['jpg', 'jpeg', 'gif', 'bmp', 'png'],
            maxFileCount: 1,
            minFileCount: 1,
            autoCommit: true,
            rootUrl: '../../',
            fileUploaded: function(e, data){
                $("#appPicSrcHidden").val(data.path);
            }
        });
	},
	initSub:function(){
		if(article.isNew == "true"){
            $("#a_siteID").val(article.siteID);
            $("#SYS_AUTHORS").val(article.author);
		}else{
			var json = $("#a_attachments").val();
	        json = JSON.parse(json);
	        var appBanner = json.appBanner;
	        if(appBanner){
	        	xy_subject.appPicUpload.setOption({
	                initialPreview: [
	                    "../../xy/image.do?path=" + appBanner + ".0.jpg"
	                ],
	                initialPreviewConfig: [
	                    { width: "120px", url: "../../xy/upload/deletePreviewThumb.do", key: 1}
	                ]
	            });
	        }
	        if(appBanner && $.trim(appBanner) != ""){
	            $("#appPicSrcHidden").val(appBanner);
	        }
		}
	},
	//保存提交
    doSave: function(){
        var paths = [];
        var _ap = $("#appPicSrcHidden").val();
        if(_ap && $.trim(_ap) != ""){
            paths.push(_ap);
        }
        xy_subject.collectConfig() ;
        $.ajax({
            url: "../../xy/pic/checkExtractIsFinished.do",
            dataType: "json",
            data: {
                paths: JSON.stringify(paths)
            },
            async: false,
            success: function(json){
                if(json){
                    if(json.code == 0){
                        if(json.isFinished){
                            if(!$("#form").validationEngine("validate")){
                                // 验证提示
                                $("#form").validationEngine("updatePromptsPosition");
                                return false;
                            }

                            $("#form").submit();
                        }else{
                            alert("图片抽图还未完成！请稍后再试！");
                        }
                    } else{
                        alert(json.msg);
                    }
                }
            }
        });
    },
	//退出按钮
    doCancel: function(){
        window.onbeforeunload = null;
        var dataUrl = "../../e5workspace/after.do?UUID=" + $("#form #UUID").val();
        window.location.href = dataUrl;
    },
    collectConfig: function(){
    	var picList = [];
    	var _val = $("#appPicSrcHidden").val();
    	if(_val && $.trim(_val) != ""){
            picList.push(_val);
        }
    	var attachments = {
    		pics : picList
        };
    	$("#a_attachments").val(JSON.stringify(attachments));
    },
}

e5_form.dynamicReader._readCatUrl = function(catType) {
	//改变读数据的url
	var dataUrl = "xy/Cats.do?catType=" + catType + "&siteID=" + e5_form.getParam("siteID");
	dataUrl = e5_form.dealUrl(dataUrl);
	return dataUrl;
};