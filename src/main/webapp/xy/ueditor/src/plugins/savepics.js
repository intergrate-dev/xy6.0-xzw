/**
 * 批量下载图片
 * @file
 * @since 1.2.6.1
 */

/**
 * 保存图片
 * @command savepics
 * @method execCommand
 * @param { String } cmd 命令字符串
 * @example
 * ```javascript
 * editor.execCommand( 'savepics' );
 * ```
 */
UE.commands['savepics'] = {
    execCommand : function() {
        var _id = UE.getEditor('editor').trigger('showmessage',{
            content : "正在下载图片",
            'keepshow': true
        });
    	var imgs = this.document.getElementsByTagName("img");
    	var editor = UE.getEditor("editor");
        var content = editor.getContent();//原文章内容
        var _num=imgs.length;
        (function doDjax(i,imgs){
            if(i==_num){
                editor.setContent(content);
                setTimeout(function() {
                    UE.getEditor('editor').trigger('hidemessage', _id);
                },1000);
                return;
            }
            if(imgs[i].src.indexOf('/image.do?')!=-1){
	            doDjax(i+1,imgs)
            }else{
                $.ajax({
                    url : '../../xy/ueditor/TranslocalImg.do',
                    type : 'POST',
                    data : {
                        'imagePath' : imgs[i].src
                    },
                    dataType : 'json',
                    async : false,
                    success : function(data) {
                        if('' == data.picPath){
                            alert('无法完成，服务器网络故障，或源地址拒绝自动下载。\r\n请尝试本地手动下载再上传。');
                            return;
                        }
                        content = content.replace(imgs[i].src, data.picPath);
                        //删除html代码中img的alt
                        content = content.replace('alt="'+imgs[i].alt+'"', "");
    
                        setTimeout(function(){
                            UE.getEditor('editor').trigger('updatemessage', _id, {
                                content : "第"+(i+1)+"\/"+_num+"张图片成功下载到本地！",
                                'keepshow': true
                            });
                            doDjax(i+1,imgs)
                        },20)
                    },
                    error : function(xhr, textStatus, errorThrown) {
                        setTimeout(function(){
                            UE.getEditor('editor').trigger('updatemessage', _id, {
                                content : "第"+(i+1)+"\/"+_num+"张图片保存失败！",
                                'keepshow': true
                            });
                            doDjax(i+1,imgs)
                        },20)
                    }
                });
            }
        })(0,imgs);
    },
   
    queryCommandState : function() {
    	var images = this.document.getElementsByTagName("img");
    	if(images.length>0){
    		if(images[0].src.indexOf("image.do?")>=0){
    			return -1;
    		}
    		return 0;
    	}
    	return -1;
    }

};