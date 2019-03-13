$(function(){
	xy_shutup.init() ;
})
var xy_shutup = {
	init : function(){

		xy_shutup.initSub() ;
		$("#btnFormSave").unbind().click(xy_shutup.doSave);
        $("#btnFormCancel").click(xy_shutup.doCancel);
      
        $("#btnUser").click(xy_shutup.doSelect) ;
	},
	initSub:function(){
		if(type==0){
			
			$("#LABEL_SYS_AUTHORS").html("禁言用户");
			document.getElementById("userName").setAttribute("readOnly",'true'); 
			$("#btnUser").attr("display","");
			
		}else if(type==2){
			$("#LABEL_SYS_AUTHORS").html("白名单");
			document.getElementById("userName").setAttribute("readOnly",'true');
			$("#btnUser").attr("display","");
		}else{
			$("#btnUser").attr("style", "display: none;");
		}
	},

	doSelect:function(){
		window.resizeTo(1100, 680);//改变大小
		xy_memSelect.memSelect();
		
	},
	//保存提交
    doSave: function(){
        /*var paths = [];
        var _ap = $("#appPicSrcHidden").val();
        if(_ap && $.trim(_ap) != ""){
            paths.push(_ap);
        }
        xy_shutup.collectConfig() ;
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
        });*/
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
function groupSelectOK(docLibID, docID){
	xy_memSelect.groupSelectOK(docLibID, docID);
}

function groupSelectCancel(){
	xy_memSelect.groupSelectCancel();
}

e5_form.dynamicReader._readCatUrl = function(catType) {
	var dataUrl = "e5workspace/manoeuvre/CatFinder.do?action=single&catType=" + catType;
	if (catType == "5") {
		//改变读数据的url
		dataUrl = "xy/Cats.do?catType=" + catType + "&siteID=" + e5_form.getParam("siteID");
	}
	dataUrl = e5_form.dealUrl(dataUrl);
	return dataUrl;
};