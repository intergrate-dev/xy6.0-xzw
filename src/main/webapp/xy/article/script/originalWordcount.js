//源稿库送审、审核通过、驳回、校对完成 的 字符数显示功能
var original_wordcount = {
	init : function(){
		//意见字数
        var arr = [
			{name:'#censorshipReason',num:300 },
			{name:'#checkReason',num:300},
			{name:'#sendbackReason',num:300}
		];
        for(var i = 0; i < arr.length; i++){
            original_wordcount.wordCount($(arr[i].name),arr[i].num) ;
		}
        //送审 预签并送审
		$("#censorshipReason").keyup(function(e){
			original_wordcount.wordCount($(this),300) ;
		});
		$("#censorshipReason").blur(function(){
			original_wordcount.wordCount($(this),300) ;
		});
		//审核通过 校对完成
		$("#checkReason").keyup(function(e){
			original_wordcount.wordCount($(this),300) ;
		});
		$("#checkReason").blur(function(){
			original_wordcount.wordCount($(this),300) ;
		});
		//驳回
		$("#sendbackReason").keyup(function(e){
			original_wordcount.wordCount($(this),300) ;
		});
		$("#sendbackReason").blur(function(){
			original_wordcount.wordCount($(this),300) ;
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
                $("#"+_id +"Num").html('摘要字数:'+ len + "/"+count).css("color","#333");
                $("#"+_id +"Count").html(len + "/"+count).css("color","#333");
			}else{
                $("#"+_id +"Num").html("*最多输入" + count + "个字").css("color","red");
				$("#"+_id +"Count").html("*最多输入" + count + "个字").css("color","red");
				$("#"+_id).val($this.val().replace(/\s/g,'').substr(0, count));
			}
        }else{
            //这里是解决初始化报错的问题
            if($this.attr("id")){
                var _id = $this.attr("id");
			}else{
                var _id = $this.selector;
            }
            $("#"+_id +"Num").html('摘要字数:'+ 0 + "/"+count).css("color","#333");
            $("#"+_id +"Count").html(0 + "/"+count).css("color","#333");
		}
	}

};


$(function(){
	original_wordcount.init();
});
		