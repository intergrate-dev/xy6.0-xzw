//稿件编辑器中 的 字符数显示功能
var word_count = {
	init : function(){
		//摘要字数
        var arr = [
			{name:'#a_abstract',num:2000 },
			{name:'#a_linkTitle',num:1024},
			{name:'#a_subTitle',num:1024},
			{name:'#a_leadTitle',num:1024},
			{name:'#a_multimediaLink',num:1024},
			{name:'#a_shortTitle',num:1024}
		];
        for(var i = 0; i < arr.length; i++){
            word_count.wordCount($(arr[i].name),arr[i].num) ;
		}
		$("#a_abstract").keyup(function(e){
			word_count.wordCount($(this),2000) ;
		});
		$("#a_abstract").blur(function(){
			word_count.wordCount($(this),2000) ;
		});
		//链接标题字数
		$("#a_linkTitle").keyup(function(e){
			word_count.wordCount($(this),1024) ;
		});
		$("#a_linkTitle").blur(function(){
			word_count.wordCount($(this),1024) ;
		});
		//副题字数
		$("#a_subTitle").keyup(function(e){
			word_count.wordCount($(this),1024) ;
		});
		$("#a_subTitle").blur(function(){
			word_count.wordCount($(this),1024) ;
		});
		//引题字数
		$("#a_leadTitle").keyup(function(e){
			word_count.wordCount($(this),1024) ;
		});
		$("#a_leadTitle").blur(function(){
			word_count.wordCount($(this),1024) ;
		});
		//多媒体链接字数
		$("#a_multimediaLink").keyup(function(e){
			word_count.wordCount($(this),1024) ;
		});
		$("#a_multimediaLink").blur(function(){
			word_count.wordCount($(this),1024) ;
		});
		//短标题字数
		$("#a_shortTitle").keyup(function(e){
			word_count.wordCount($(this),1024) ;
		});
		$("#a_shortTitle").blur(function(){
			word_count.wordCount($(this),1024) ;
		});
		
	},
	wordCount : function($this,count){
        var _value = $this.val();
        if(_value){
			// var _length = _value.length;
			var _id = $this.attr("id"),
				_text,
				len;

			//去除样式
			var reg=/\<[\s\S]*?\>/g;
			_text=_value.replace(reg,"");

			//去除空格
			len = _text.replace(/\s/g,'').length;

			if(len<=count){
                $("#"+_id.split("_")[1]+"Num").html('摘要字数:'+ len + "/"+count).css("color","#333");
                $("#"+_id.split("_")[1]+"Count").html(len + "/"+count).css("color","#333");
			}else{
                $("#"+_id.split("_")[1]+"Num").html("*最多输入" + count + "个字").css("color","red");
				$("#"+_id.split("_")[1]+"Count").html("*最多输入" + count + "个字").css("color","red");
				$("#"+_id).val($this.val().replace(/\s/g,'').substr(0, count));
			}
        }else{
            //这里是解决初始化报错的问题
            if($this.attr("id")){
                var _id = $this.attr("id");
			}else{
                var _id = $this.selector;
            }
            $("#"+_id.split("_")[1]+"Num").html('摘要字数:'+ 0 + "/"+count).css("color","#333");
            $("#"+_id.split("_")[1]+"Count").html(0 + "/"+count).css("color","#333");
		}
	}

};


$(function(){
	word_count.init();
});
		