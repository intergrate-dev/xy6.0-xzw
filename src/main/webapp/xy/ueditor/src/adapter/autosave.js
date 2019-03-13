UE.registerUI('autosave', function(editor) {
 

    var timer = null,uid = null;
    editor.on('afterautosave',function(){

        clearTimeout(timer);

        //先加载图片再保存至草稿箱
	
	    // var contentTxt = editor.getContent();
	    // if(contentTxt.indexOf('src="http')!=-1){
	    //
		 //    $('div[title="批量下载图片"]').trigger('click');
		 //    var me = this;
		 //    editor.execCommand("inserthtml");
	    // }
	
	
	    timer = setTimeout(function(){
            if(uid){
                editor.trigger('hidemessage',uid);
            }
            uid = editor.trigger('showmessage',{
                content : editor.getLang('autosave.success'),
                timeout : 1000
            });
            savaDraft(editor);
        },200)
    })
});
//保存草稿箱的方法
function savaDraft(editor){
    var contentTxt = editor.getContent();

    var articleId = $("#edui4_body").attr('artileIdPerson');

    if(articleId == undefined || articleId =='' || articleId == null){
        //重新赋值
        articleId = article.docID;

    }

    $.ajax({
        url : "../../xy/article/Draft.do",
        type : "POST",
        data : {
            "docID" : articleId,
            "content" : contentTxt,
            "title" : $("#SYS_TOPIC").val()
        },
        dataType : "json",
        success : function(data) {
            // alert(1)
            // console.log(data);
            // UE.getEditor('editor').trigger('showmessage',{
            //    content : data.state,
            //    timeout : 2000
            // });
            // alert("稿件保存成功！");
        },
        error :function(data){

        }
    });
}