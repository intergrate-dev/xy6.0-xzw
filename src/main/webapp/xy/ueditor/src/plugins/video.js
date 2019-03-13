/**
 * video插件， 为UEditor提供视频插入支持
 * @file
 * @since 1.2.6.1
 */

UE.plugins['video'] = function (){
    var me =this;

    /**
     * 创建插入视频字符窜
     * @param url 视频地址
     * @param width 视频宽度
     * @param height 视频高度
     * @param align 视频对齐
     * @param toEmbed 是否以flash代替显示
     * @param addParagraph  是否需要添加P 标签
     */
    function creatInsertStr(url,width,height,id,align,classname,type, vi){
        var str;
        var __videoInfo = vi && ((vi.url ? ' url="' + vi.url + '" ':' ') + (vi.urlApp ? ' urlapp="'+vi.urlApp+'" ' : " ") +
            (vi.videoID?' videoid="' + vi.videoID + '" ':" "));
        //区分本地视频与视频库视频

        var _isLibVideo= vi && vi.videoID?' type="application/x-shockwave-flash" ':' ';
        var _isLibVideoPlugin= vi && vi.videoID?' pluginspage="http://www.macromedia.com/go/getflashplayer" ':' ';

        var videologo = url && url.substring(url.length-3) == 'mp3' ? 'audiolog.gif' : 'videologo.gif';
        
        switch (type){
            case 'image':
                str = '<img ' + (id ? 'id="' + id+'"' : '') + ' width="'+ width +'" height="' + height + '" _url="'+url+'" class="' + classname.replace(/\bvideo-js\b/, '') + '"'  +
                    ' src="' + me.options.UEDITOR_HOME_URL+'themes/default/images/spacer.gif" style="background:url('+me.options.UEDITOR_HOME_URL+'themes/default/images/'+ videologo +') no-repeat center center; border:1px solid gray;'+(align ? 'float:' + align + ';': '')+'" ' +
                __videoInfo + '/>'
                break;
            case 'embed':
                //type="application/x-shockwave-flash"
                str = '<embed'+ _isLibVideo +'  class="' + classname + '"'+ _isLibVideoPlugin +
                    ' src="' +  utils.html(url) + '" width="' + width  + '" height="' + height  + '"'  + (align ? ' style="float:' + align + '"': '') +
                    ' wmode="transparent" play="true" loop="false" menu="false" allowscriptaccess="never" allowfullscreen="true" ' +
                    //' style="background:url('+me.options.UEDITOR_HOME_URL+'themes/default/images/'+ videologo +') no-repeat center center; border:1px solid gray;'+(align ? 'float:' + align + ';': '')+'" ' +
                __videoInfo +
                '>';
                break;
            case 'video':
                var ext = url.substr(url.lastIndexOf('.') + 1);
                if(ext == 'ogv') ext = 'ogg';
                str = '<video' + (id ? ' id="' + id + '"' : '') + ' class="' + classname + ' video-js" ' + (align ? ' style="float:' + align + '"': '') +
                    ' controls preload="none" width="' + width + '" height="' + height + '" src="' + url + '" data-setup="{}" ' +
                __videoInfo +
                '>' +
                    '<source src="' + url + '" type="video/' + ext + '" /></video>';
                break;

            //在线 外网视频
            case 'onlinevideo':
                str = '<img ' + (id ? 'id="' + id+'"' : '') + ' width="'+ width +'" height="' + height + '" _url="'+url+'" class="' + classname.replace(/\bvideo-js\b/, '') + '"'  +
                ' src="' + me.options.UEDITOR_HOME_URL+'themes/default/images/spacer.gif" style="background:url('+me.options.UEDITOR_HOME_URL+'themes/default/images/videologo.gif) no-repeat center center; border:1px solid gray;'+(align ? 'float:' + align + ';': '')+'" ' +
                __videoInfo +
                ' />' ;
                break;

            //视频库
            case 'videolib':
                str = '<img ' + (id ? 'id="' + id+'"' : '') + ' width="'+ width +'" height="' + height +
                '" _url="'+ vi.videoPluginUrl+'?src=' + vi.url +'"' +
                ' class="' + classname.replace(/\bvideo-js\b/, '') + '"'  +
                ' src="' + me.options.UEDITOR_HOME_URL+'themes/default/images/spacer.gif" style="background:url('+me.options.UEDITOR_HOME_URL+'themes/default/images/'+ videologo +') no-repeat center center; border:1px solid gray;'+(align ? 'float:' + align + ';': '')+'" ' +
                __videoInfo +
                '/>';
                break;

            //插入音频
            case 'insertaudio':
                str = '<img ' + (id ? 'id="' + id+'"' : '') + ' width="'+ width +'" height="' + height + '" _url="'+url+'" url="'+url+'" urlapp="'+url+'" class="' + classname.replace(/\bvideo-js\b/, '') + '"'  +
                ' src="' + me.options.UEDITOR_HOME_URL+'themes/default/images/spacer.gif" style="background:url('+me.options.UEDITOR_HOME_URL+'themes/default/images/'+ videologo +') no-repeat center center; border:1px solid gray;'+(align ? 'float:' + align + ';': '')+'" />'
                ;
                break;

            //图片与视频切换时使用
            case 'audioimage':
                str = '<img ' + (id ? 'id="' + id+'"' : '') + ' width="'+ width +'" height="' + height + '" _url="'+url+'" url="'+url+'" urlapp="'+url+'" class="' + classname.replace(/\bvideo-js\b/, '') + '"'  +
                ' src="' + me.options.UEDITOR_HOME_URL+'themes/default/images/spacer.gif" style="background:url('+me.options.UEDITOR_HOME_URL+'themes/default/images/audiolog.png) no-repeat center center; border:1px solid gray;'+(align ? 'float:' + align + ';': '')+'" />';
                break;

            case 'audio':
                var urlIndex = url.indexOf('.swf?src=');
                urlIndex = urlIndex===-1 ? 0 : urlIndex+9;
                url = url.substring(urlIndex);
                str = '<audio ' + (id ? 'id="' + id+'"' : '') + '  url="'+url+'" urlapp="'+url+'" '+'' +
                'width="'+ width +'" height="' + height + '" style="'+(width ?'width:'+width+"px;":"") +( height?'height:'+height+"px;":"")+'"' +
                'class="' + classname + '" src="'+url+'" controls="controls" >您的浏览器不支持 audio 标签。</audio>'

                ;
                break;
        }
        return str;
    }

    function switchImgAndVideo(root,img2video){
        utils.each(root.getNodesByTagName(img2video ? 'img' : 'embed video audio'),function(node){
            var className = node.getAttr('class');
            var vi = {url: null, urlApp: null, videoID: null};
            if(className && className.indexOf('edui-faked-video') != -1){
                vi.url = node.getAttr('url');
                vi.urlApp = node.getAttr('urlapp');
                vi.videoID = node.getAttr('videoid');
                var html = creatInsertStr( img2video ? node.getAttr('_url') : node.getAttr('src'),node.getAttr('width'),node.getAttr('height'),null,node.getStyle('float') || '',className,img2video ? 'embed':'image',vi);
                node.parentNode.replaceChild(UE.uNode.createElement(html),node);
            }
            if(className && className.indexOf('edui-upload-video') != -1){
                vi.url = node.getAttr('url');
                vi.urlApp = node.getAttr('urlapp');
                vi.videoID = node.getAttr('videoid');
                var html = creatInsertStr( img2video ? node.getAttr('_url') : node.getAttr('src'),node.getAttr('width'),node.getAttr('height'),null,node.getStyle('float') || '',className,img2video ? 'video':'image',vi);
                node.parentNode.replaceChild(UE.uNode.createElement(html),node);
            }
            if(className && className.indexOf('edui-faked-audio') != -1){
                var html = creatInsertStr( img2video ? node.getAttr('_url') : node.getAttr('src'),node.getAttr('width'),node.getAttr('height'),null,node.getStyle('float') || '',className,img2video ? 'audio':'audioimage');
                node.parentNode.replaceChild(UE.uNode.createElement(html),node);
            }
        })
    }

    me.addOutputRule(function(root){
        switchImgAndVideo(root,true)
    });
    me.addInputRule(function(root){
        switchImgAndVideo(root)
    });

    /**
     * 插入视频
     * @command insertvideo
     * @method execCommand
     * @param { String } cmd 命令字符串
     * @param { Object } videoAttr 键值对对象， 描述一个视频的所有属性
     * @example
     * ```javascript
     *
     * var videoAttr = {
     *      //视频地址
     *      url: 'http://www.youku.com/xxx',
     *      //视频宽高值， 单位px
     *      width: 200,
     *      height: 100
     * };
     *
     * //editor 是编辑器实例
     * //向编辑器插入单个视频
     * editor.execCommand( 'insertvideo', videoAttr );
     * ```
     */

    /**
     * 插入视频
     * @command insertvideo
     * @method execCommand
     * @param { String } cmd 命令字符串
     * @param { Array } videoArr 需要插入的视频的数组， 其中的每一个元素都是一个键值对对象， 描述了一个视频的所有属性
     * @example
     * ```javascript
     *
     * var videoAttr1 = {
     *      //视频地址
     *      url: 'http://www.youku.com/xxx',
     *      //视频宽高值， 单位px
     *      width: 200,
     *      height: 100
     * },
     * videoAttr2 = {
     *      //视频地址
     *      url: 'http://www.youku.com/xxx',
     *      //视频宽高值， 单位px
     *      width: 200,
     *      height: 100
     * }
     *
     * //editor 是编辑器实例
     * //该方法将会向编辑器内插入两个视频
     * editor.execCommand( 'insertvideo', [ videoAttr1, videoAttr2 ] );
     * ```
     */

    /**
     * 查询当前光标所在处是否是一个视频
     * @command insertvideo
     * @method queryCommandState
     * @param { String } cmd 需要查询的命令字符串
     * @return { int } 如果当前光标所在处的元素是一个视频对象， 则返回1，否则返回0
     * @example
     * ```javascript
     *
     * //editor 是编辑器实例
     * editor.queryCommandState( 'insertvideo' );
     * ```
     */
    me.commands["insertvideo"] = {
        execCommand: function (cmd, videoObjs, type){
            videoObjs = utils.isArray(videoObjs)?videoObjs:[videoObjs];
            var html = [],id = 'tmpVedio', cl;
            for(var i=0,vi,len = videoObjs.length;i<len;i++){
                vi = videoObjs[i];
                cl = (type == 'upload' ? 'edui-upload-video video-js vjs-default-skin':'edui-faked-video');
                html.push(creatInsertStr( vi.url, vi.width || 420,  vi.height || 280, id + i, null, cl, 'image',vi));
            }
            me.execCommand("inserthtml",html.join(""),true);
            var rng = this.selection.getRange();
            for(var i= 0,len=videoObjs.length;i<len;i++){
                var img = this.document.getElementById('tmpVedio'+i);
                domUtils.removeAttributes(img,'id');
                rng.selectNode(img).select();
                me.execCommand('imagefloat',videoObjs[i].align)
            }
        },
        queryCommandState : function(){
            var img = me.selection.getRange().getClosedNode(),
                flag = img && (img.className == "edui-faked-video" || img.className.indexOf("edui-upload-video")!=-1 || img.className.indexOf("edui-faked-audio")!=-1 );
            return flag ? 1 : 0;
        }
    };
    me.commands["insertonlinevideo"] = {
        execCommand: function (cmd, videoObjs, type){
            videoObjs = utils.isArray(videoObjs)?videoObjs:[videoObjs];
            var html = [],id = 'tmpVedio', cl;
            for(var i=0,vi,len = videoObjs.length;i<len;i++){
                vi = videoObjs[i];
                vi.urlApp = vi.url;
                cl = (type == 'upload' ? 'edui-upload-video video-js vjs-default-skin':'edui-faked-video');
                html.push(creatInsertStr( vi.url, vi.width || 420,  vi.height || 280, id + i, null, cl, 'onlinevideo',vi));
            }
            me.execCommand("inserthtml",html.join(""),true);
            var rng = this.selection.getRange();
            for(var i= 0,len=videoObjs.length;i<len;i++){
                var img = this.document.getElementById('tmpVedio'+i);
                domUtils.removeAttributes(img,'id');
                rng.selectNode(img).select();
                me.execCommand('imagefloat',videoObjs[i].align)
            }
        },
        queryCommandState : function(){
            var img = me.selection.getRange().getClosedNode(),
                flag = img && (img.className == "edui-faked-video" || img.className.indexOf("edui-upload-video")!=-1);
            return flag ? 1 : 0;
        }
    };


    /*//001 插入纳加视频
    me.commands["insertvideolib"] = {
        execCommand: function (cmd, videoObjs, type){

            videoObjs = utils.isArray(videoObjs)?videoObjs:[videoObjs];
            var html = [],id = 'tmpVedio', cl;
            for(var i=0,vi,len = videoObjs.length;i<len;i++){
                vi = videoObjs[i];
                cl = (type == 'upload' ? 'edui-upload-video video-js vjs-default-skin':'edui-faked-video');
                html.push(creatInsertNageStr( vi.url, vi.width || 420,  vi.height || 280, id + i, null, cl, 'videolib', vi));
            }
            me.execCommand("inserthtml",html.join(""),true);
            /!*var rng = this.selection.getRange();
             for(var i= 0,len=videoObjs.length;i<len;i++){
             var img = this.document.getElementById('tmpVedio'+i);
             domUtils.removeAttributes(img,'id');
             rng.selectNode(img).select();
             me.execCommand('imagefloat',videoObjs[i].align)
             }*!/
        },
        queryCommandState : function(){
            var img = me.selection.getRange().getClosedNode(),
                flag = img && (img.className == "edui-faked-video" || img.className.indexOf("edui-upload-video")!=-1);
            return flag ? 1 : 0;
        }
    };

    /!**
     * 创建插入视频字符窜
     * @param url 视频地址
     * @param width 视频宽度
     * @param height 视频高度
     * @param align 视频对齐
     * @param toEmbed 是否以flash代替显示
     * @param addParagraph  是否需要添加P 标签
     *!/
    function creatInsertNageStr(url,width,height,id,align,classname,type, vi){
        str = ' <p ' + 'url="'+vi.url+'" urlApp="'+vi.urlApp+'" videoID="' + vi.videoID + '" >'
        + ' <embed type="application/x-shockwave-flash" class="edui-faked-video" '
        + ' pluginspage="http://www.macromedia.com/go/getflashplayer" '
        + ' src="'+vi.videoPluginUrl+'?src='+url+'"'
        + ' _src="'+url+'" '
        + ' width="'+width+'" height="'+height+'" wmode="transparent" play="true" '
            //+ ' url="'+vi.url+'" urlApp="'+vi.urlApp+'" videoID="' + vi.videoID + '"'
        + ' loop="false" menu="false" allowscriptaccess="never" allowfullscreen="true">'
        + ' </p>';
        return str;
    }*/

    //插入视频库中的视频 - 图片
    me.commands["insertvideolib"] = {
        execCommand: function (cmd, videoObjs, type){
            videoObjs = utils.isArray(videoObjs)?videoObjs:[videoObjs];
            var html = [],id = 'tmpVedio', cl;
            for(var i=0,vi,len = videoObjs.length;i<len;i++){
                vi = videoObjs[i];
                // 通过后缀判断是音频还是视频文件
                if(vi && vi.url && vi.url.substring(vi.url.length-3) == 'mp3'){
                    cl = (type == 'upload' ? 'edui-upload-video video-js vjs-default-skin':'edui-faked-audio');
                    html.push(creatInsertStr( vi.url, vi.width || 300,  vi.height || 50, id + i, null, cl, 'videolib', vi));
                } else {
                    cl = (type == 'upload' ? 'edui-upload-video video-js vjs-default-skin':'edui-faked-video');
                    html.push(creatInsertStr( vi.url, vi.width || 420,  vi.height || 280, id + i, null, cl, 'videolib', vi));
                }
            }
            me.execCommand("inserthtml",html.join(""),true);
            var rng = this.selection.getRange();
            for(var i= 0,len=videoObjs.length;i<len;i++){
                var img = this.document.getElementById('tmpVedio'+i);
                domUtils.removeAttributes(img,'id');
                rng.selectNode(img).select();
                me.execCommand('imagefloat',videoObjs[i].align)
            }
        },
        queryCommandState : function(){
            var img = me.selection.getRange().getClosedNode(),
                flag = img && (img.className == "edui-faked-video" || img.className.indexOf("edui-upload-video")!=-1);
            return flag ? 1 : 0;
        }
    };

    //插入音频
    me.commands["insertaudio"] = {
        execCommand: function (cmd, audioObjs, type){
            //所有音频对象
            audioObjs = utils.isArray(audioObjs)?audioObjs:[audioObjs];
            var html = [],id = 'tmpAudie', cl;
            for(var i=0,vi,len = audioObjs.length;i<len;i++){
                vi = audioObjs[i];
                cl = (type == 'upload' ? 'edui-upload-audio audio-js audiojs-default-skin':'edui-faked-audio');
                html.push(creatInsertStr( vi.url, vi.width || 300,  vi.height || 50, id + i, null, cl, 'insertaudio'));
            }
            me.execCommand("inserthtml",html.join(""),true);
            var rng = this.selection.getRange();
            for(var i= 0,len=audioObjs.length;i<len;i++){
                var img = this.document.getElementById('tmpAudie'+i);
                domUtils.removeAttributes(img,'id');
                rng.selectNode(img).select();
                me.execCommand('imagefloat',audioObjs[i].align)
            }
        },
        queryCommandState : function(){
            var img = me.selection.getRange().getClosedNode(),
                flag = img && (img.className == "edui-faked-audio" || img.className.indexOf("edui-upload-audio")!=-1);
            return flag ? 1 : 0;
        }
    };
};