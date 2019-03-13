/**
 * 截屏插件，为UEditor提供插入支持
 * @file
 * @since 1.4.2
 */
UE.plugin.register('snapscreen', function (){

    var me = this;
    var snapplugin;

    function getLocation(url){
        var search,
            a = document.createElement('a'),
            params = utils.serializeParam(me.queryCommandValue('serverparam')) || '';

        a.href = url;
        if (browser.ie) {
            a.href = a.href;
        }


        search = a.search;
        if (params) {
            search = search + (search.indexOf('?') == -1 ? '?':'&')+ params;
            search = search.replace(/[&]+/ig, '&');
        }
        return {
            'port': a.port,
            'hostname': a.hostname,
            'path': a.pathname + search ||  + a.hash
        }
    }

    return {
        commands:{
            /**
             * 字体背景颜色
             * @command snapscreen
             * @method execCommand
             * @param { String } cmd 命令字符串
             * @example
             * ```javascript
             * editor.execCommand('snapscreen');
             * ```
             */
            'snapscreen':{
                execCommand:function (cmd, value) {
                    if(!value){
                        var url, local, res;
                        var lang = me.getLang("snapScreen_plugin");

                        if(!snapplugin){
                            var container = me.container;
                            var doc = me.container.ownerDocument || me.container.document;
                            snapplugin = doc.createElement("object");
                            try{snapplugin.type = "application/x-pluginbaidusnap";}catch(e){
                                return;
                            }
                            snapplugin.style.cssText = "position:absolute;left:-9999px;width:0;height:0;";
                            snapplugin.setAttribute("width","0");
                            snapplugin.setAttribute("height","0");
                            container.appendChild(snapplugin);
                        }

                        function onSuccess(rs){
                            try{
                                rs = eval("("+ rs +")");
                                if(rs.state == 'SUCCESS'){
                                    var opt = me.options;
                                    me.execCommand('insertimage', {
                                        src: opt.snapscreenUrlPrefix + rs.url,
                                        _src: opt.snapscreenUrlPrefix + rs.url,
                                        alt: rs.title || '',
                                        floatStyle: opt.snapscreenImgAlign
                                    });
                                } else {
                                    alert(rs.state);
                                }
                            }catch(e){
                                alert(lang.callBackErrorMsg);
                            }
                        }
                        url = me.getActionUrl(me.getOpt('snapscreenActionName'));
                        local = getLocation(url);
                        setTimeout(function () {
                            try{
                                res =snapplugin.saveSnapshot(local.hostname, local.path, local.port);
                            }catch(e){
                                me.ui._dialogs['snapscreenDialog'].open();
                                return;
                            }

                            onSuccess(res);
                        }, 50);
                    }else if(value == "imagecrop"){
                        window.parent.chosenImg && (window.parent.chosenImg = null);
                        this.getDialog("imagecrop").open();
                    }else if(value == "titleImg"){

                        var img = this.selection.getRange().getClosedNode();
                        var _path = img.src;
                        _path = _path.substr(_path.lastIndexOf("image.do?path=")).replace("image.do?path=","");
                        var pos = {left : "100px",top : "50px",width : "1000px",height : "500px"};
                        //如果之前没有生成dialog
                        //if(! channel_frame.titleDialog){
                        channel_frame.titleDialog = e5.dialog({
                            type : "iframe",
                            value : '../../xy/ueditor/initTitleDialog.do?imagePath=' + _path + "&itype=all"
                        }, {
                            showTitle : true,
                            title: "设置标题图",
                            width : "1000px",
                            height : "600px",
                            pos : pos,
                            resizable : false,
                            fixed:true
                        });
                        //}
                        channel_frame.titleDialog.show();
                    }

                },
                queryCommandState: function(){
                    var range = this.selection.getRange(),
                        startNode;

                    if (range.collapsed)  return -1;

                    startNode = range.getClosedNode();

                    if (startNode && startNode.nodeType == 1 && startNode.tagName == 'IMG'
                        && (startNode.className=='kfformula' || startNode.className.indexOf("edui-faked") != -1)) {
                        return -1;
                    }

                    if (startNode && startNode.nodeType == 1 && startNode.tagName == 'IMG') {
                        return 0;
                    }

                    return (navigator.userAgent.indexOf("Windows",0) != -1) ? 0:-1;
                }
            }
        }
    }
});
