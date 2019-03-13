///import core
///commands 全屏
///commandsName FullScreen
///commandsTitle  全屏
(function () {
    var utils = baidu.editor.utils,
        uiUtils = baidu.editor.ui.uiUtils,
        UIBase = baidu.editor.ui.UIBase,
        domUtils = baidu.editor.dom.domUtils;
    var nodeStack = [];

    function EditorUI(options) {
        this.initOptions(options);
        this.initEditorUI();
    }

    EditorUI.prototype = {
        uiName:'editor',
        initEditorUI:function () {
            this.editor.ui = this;
            this._dialogs = {};
            this.initUIBase();
            this._initToolbars();
            var editor = this.editor,
                me = this;

            editor.addListener('ready', function () {
                //初始化tab并加切换事件fzf
                $('.tab_content').eq(0).show();
                $('#tab_list').find("li").eq(0).attr('class', 'confirm');
                $('#tab_list').find('.default, .confirm').each(function(){
                    $(this).click(function(){
                        $('#tab_list').find('.default, .confirm').attr('class', 'default');
                        $(this).attr('class', 'confirm');

                        $('.tab_content').hide();
                        $('#' + $(this).attr('id') + '_container').show();

                        return false;
                    });
                });


                //提供getDialog方法
                editor.getDialog = function (name) {
                    return editor.ui._dialogs[name + "Dialog"];
                };
                domUtils.on(editor.window, 'scroll', function (evt) {
                    baidu.editor.ui.Popup.postHide(evt);
                });
                //提供编辑器实时宽高(全屏时宽高不变化)
                editor.ui._actualFrameWidth = editor.options.initialFrameWidth;

                UE.browser.ie && UE.browser.version === 6 && editor.container.ownerDocument.execCommand("BackgroundImageCache", false, true);

                //display bottom-bar label based on config
                if (editor.options.elementPathEnabled) {
                    editor.ui.getDom('elementpath').innerHTML = '<div class="edui-editor-breadcrumb">' + editor.getLang("elementPathTip") + ':</div>';
                }
                if (editor.options.wordCount) {
                    function countFn() {
                        setCount(editor,me);
                        domUtils.un(editor.document, "click", arguments.callee);
                    }
                    domUtils.on(editor.document, "click", countFn);
                    editor.ui.getDom('wordcount').innerHTML = editor.getLang("wordCountTip");
                }
                editor.ui._scale();
                if (editor.options.scaleEnabled) {
                    if (editor.autoHeightEnabled) {
                        editor.disableAutoHeight();
                    }
                    me.enableScale();
                } else {
                    me.disableScale();
                }
                if (!editor.options.elementPathEnabled && !editor.options.wordCount && !editor.options.scaleEnabled) {
                    editor.ui.getDom('elementpath').style.display = "none";
                    editor.ui.getDom('wordcount').style.display = "none";
                    editor.ui.getDom('scale').style.display = "none";
                }

                if (!editor.selection.isFocus())return;
                editor.fireEvent('selectionchange', false, true);


            });

            editor.addListener('mousedown', function (t, evt) {
                var el = evt.target || evt.srcElement;
                baidu.editor.ui.Popup.postHide(evt, el);
                baidu.editor.ui.ShortCutMenu.postHide(evt);

            });
            editor.addListener("delcells", function () {
                if (UE.ui['edittip']) {
                    new UE.ui['edittip'](editor);
                }
                editor.getDialog('edittip').open();
            });

            var pastePop, isPaste = false, timer;
            editor.addListener("afterpaste", function () {
                if(editor.queryCommandState('pasteplain'))
                    return;
                if(baidu.editor.ui.PastePicker){
                    pastePop = new baidu.editor.ui.Popup({
                        content:new baidu.editor.ui.PastePicker({editor:editor}),
                        editor:editor,
                        className:'edui-wordpastepop'
                    });
                    pastePop.render();
                }
                isPaste = true;
            });

            editor.addListener("afterinserthtml", function () {
                clearTimeout(timer);
                timer = setTimeout(function () {
                    if (pastePop && (isPaste || editor.ui._isTransfer)) {
                        if(pastePop.isHidden()){
                            var span = domUtils.createElement(editor.document, 'span', {
                                    'style':"line-height:0px;",
                                    'innerHTML':'\ufeff'
                                }),
                                range = editor.selection.getRange();
                            range.insertNode(span);
                            var tmp= getDomNode(span, 'firstChild', 'previousSibling');
                            tmp && pastePop.showAnchor(tmp.nodeType == 3 ? tmp.parentNode : tmp);
                            domUtils.remove(span);
                        }else{
                            pastePop.show();
                        }
                        delete editor.ui._isTransfer;
                        isPaste = false;
                    }
                }, 200)
            });
            editor.addListener('contextmenu', function (t, evt) {
                baidu.editor.ui.Popup.postHide(evt);
            });
            editor.addListener('keydown', function (t, evt) {
                if (pastePop)    pastePop.dispose(evt);
                var keyCode = evt.keyCode || evt.which;
                if(evt.altKey&&keyCode==90){
                    UE.ui.buttons['fullscreen'].onclick();
                }
            });
            editor.addListener('wordcount', function (type) {
                setCount(this,me);
            });
            function setCount(editor,ui) {
                editor.setOpt({
                    wordCount:true,
                    maximumWords:10000,
                    wordCountMsg:editor.options.wordCountMsg || editor.getLang("wordCountMsg"),
                    wordOverFlowMsg:editor.options.wordOverFlowMsg || editor.getLang("wordOverFlowMsg")
                });
                var opt = editor.options,
                    max = opt.maximumWords,
                    msg = opt.wordCountMsg ,
                    errMsg = opt.wordOverFlowMsg,
                    countDom = ui.getDom('wordcount');
                if (!opt.wordCount) {
                    return;
                }
                var count = editor.getContentLength(true);
                // if (count > max) {
                //     countDom.innerHTML = errMsg;
                //     editor.fireEvent("wordcountoverflow");
                //
                // } else {
                //     countDom.innerHTML = msg.replace("{#leave}", max - count).replace("{#count}", count);
                // }
                // alert(count);
                // debugger
                countDom.innerHTML = msg.replace("{#leave}", max - count).replace("{#count}", count);
            }

            editor.addListener('selectionchange', function () {
                if (editor.options.elementPathEnabled) {
                    me[(editor.queryCommandState('elementpath') == -1 ? 'dis' : 'en') + 'ableElementPath']()
                }
                if (editor.options.scaleEnabled) {
                    me[(editor.queryCommandState('scale') == -1 ? 'dis' : 'en') + 'ableScale']();

                }
            });
            var popup = new baidu.editor.ui.Popup({
                editor:editor,
                content:'',
                className:'edui-bubble',
                _onEditButtonClick:function () {
                    this.hide();
                    $("#imgComment").remove();
                    editor.ui._dialogs.linkDialog.open();
                },
                // _onCodeButtonClick:function(){
                //
                //     editor.ui._dialogs.insertcodeDialog.open();
                // }
                _onImgEditButtonClick:function (name) {
                    this.hide();
                    $("#imgComment").remove();
                    editor.ui._dialogs[name] && editor.ui._dialogs[name].open();

                },
                _onImgScale:function(value){
                    // this.hide();
                    $("#imgComment").remove();
                    editor.execCommand("imagescale", value);
                },
                _onImgComment:function(value){
                    editor.execCommand("imagecomment", value);
                },
                _onImgDelete:function(){
                    this.hide();
                    $("#imgComment").remove();
                    editor.execCommand("imagedetele");
                },
                _onImgSetFloat:function (value) {
                    this.hide();
                    $("#imgComment").remove();
                    editor.execCommand("imagefloat", value);

                },
                _onImgCropButtonClick:function (name) {
                    //this.hide();
                    $("#imgComment").remove();
                    var name = 'imagecropDialog';
                    editor.ui._dialogs[name] && editor.ui._dialogs[name].open();
                },
                _onTitleImgButtonClick: function (){
                    var img = editor.selection.getRange().getClosedNode();
                    var _path = img.src;
                    _path = _path.substr(_path.lastIndexOf("image.do?path=")).replace("image.do?path=","");
                    var pos = {left : "100px",top : "50px",width : "1170px",height : "500px"};

                    //如果之前没有生成dialog
                    //if(! channel_frame.titleDialog){
                    channel_frame.titleDialog = e5.dialog({
                        type : "iframe",
                        value : '../../xy/ueditor/initTitleDialog.do?imagePath=' + _path + "&itype=all"
                    }, {
                        showTitle : true,
                        title: "设置标题图",
                        width : "1170px",
                        height : "600px",
                        pos : pos,
                        resizable : false,
                        // fixed:true
                    });
                    //}
                    channel_frame.titleDialog.show();

                },
                _onlocalImgClick: function (){
                    this.hide();
                    $("#imgComment").remove();
                    var img = editor.selection.getRange().getClosedNode();
                    $.ajax({
                        url : '../../xy/ueditor/TranslocalImg.do',
                        type : 'POST',
                        data : {
                            'imagePath' : img.src
                        },
                        dataType : 'json',
                        success : function(data) {
                            if('' == data.picPath){
                                alert('图片源不稳定或源地址拒绝自动下载。\r\n请尝试本地手动下载再上传。');
                                return;
                            }
                            var imgObj = {};
                            imgObj.src = data.picPath;
                            imgObj._src = data.picPath;
                            imgObj.alt = img.alt;
                            imgObj.width = img.width;
                            imgObj.height = img.height;
                            imgObj.style = 'width:' + img.width + 'px;height:' + img.height + 'px;';
                            editor.fireEvent('beforeInsertImage', imgObj);
                            editor.execCommand("insertImage", imgObj);
                            alert('图片保存成功！');
                        },
                        error : function(xhr, textStatus, errorThrown) {
                            alert('图片保存失败！');
                        }
                    });
                },
                _setIframeAlign:function (value) {
                    var frame = popup.anchorEl;
                    var newFrame = frame.cloneNode(true);
                    switch (value) {
                        case -2:
                            newFrame.setAttribute("align", " ");
                            break;
                        case -1:
                            newFrame.setAttribute("align", "left");
                            break;
                        case 1:
                            newFrame.setAttribute("align", "right");
                            break;
                    }
                    frame.parentNode.insertBefore(newFrame, frame);
                    domUtils.remove(frame);
                    popup.anchorEl = newFrame;
                    popup.showAnchor(popup.anchorEl);
                },
                _updateIframe:function () {
                    var frame = editor._iframe = popup.anchorEl;
                    if(domUtils.hasClass(frame, 'ueditor_baidumap')) {
                        editor.selection.getRange().selectNode(frame).select();
                        editor.ui._dialogs.mapDialog.open();
                        popup.hide();
                    } else {
                        editor.ui._dialogs.insertframeDialog.open();
                        popup.hide();
                    }
                },
                _onRemoveButtonClick:function (cmdName) {
                    editor.execCommand(cmdName);
                    this.hide();
                    $("#imgComment").remove();
                },

                queryAutoHide:function (el) {
                    if (el && el.ownerDocument == editor.document) {
                        if (el.tagName.toLowerCase() == 'img' || domUtils.findParentByTagName(el, 'a', true)) {
                            return el !== popup.anchorEl;
                        }
                    }
                    return baidu.editor.ui.Popup.prototype.queryAutoHide.call(this, el);
                },

                _onVideoPreviewButtonClick: function(_pUrl, type){

                    (function(me, editorui, type){
                        var dialog = null;
                        dialog = me.getDialog("videoPreviewDialog");
                        if(!dialog){
                            var _sUrl = "../../xy/ueditor/dialogs/video/preview.jsp?url=" + _pUrl + "&type=" + type;
                            dialog = new editorui.Dialog({
                                //指定弹出层中页面的路径，这里只能支持页面,因为跟addCustomizeDialog.js相同目录，所以无需加路径
                                iframeUrl: _sUrl,
                                //需要指定当前的编辑器实例
                                editor:me,
                                //指定dialog的名字
                                name:"videoPreviewDialog",
                                //dialog的标题
                                title:"预览",
                                //指定dialog的外围样式
                                cssRules: type=="video"?"width:440px;height:320px;":"width:320px;height:80px;",
                                //如果给出了buttons就代表dialog有确定和取消
                                buttons:[
                                    {
                                        className:'edui-okbutton',
                                        label:'确认',
                                        onclick:function () {
                                            dialog.close(true);
                                        }
                                    },
                                    {
                                        className:'edui-cancelbutton',
                                        label:'取消',
                                        onclick:function () {
                                            dialog.close(false);
                                        }
                                    }
                                ]});
                            //me.ui._dialogs["videoPreviewDialog"] = dialog;
                        }
                        dialog.render();
                        dialog.open();
                    })(editor, baidu.editor.ui, type);

                }

            });

            popup.render();
            if (editor.options.imagePopup) {
                editor.addListener('mouseover', function (t, evt) {
                    evt = evt || window.event;
                    var el = evt.target || evt.srcElement;
                    if (editor.ui._dialogs.insertframeDialog && /iframe/ig.test(el.tagName)) {
                        var html = popup.formatHtml(
                            '<nobr>' + editor.getLang("property") + ': <span onclick=$$._setIframeAlign(-2) class="edui-clickable">' + editor.getLang("default") + '</span>&nbsp;&nbsp;<span onclick=$$._setIframeAlign(-1) class="edui-clickable">' + editor.getLang("justifyleft") + '</span>&nbsp;&nbsp;<span onclick=$$._setIframeAlign(1) class="edui-clickable">' + editor.getLang("justifyright") + '</span>&nbsp;&nbsp;' +
                            ' <span onclick="$$._updateIframe( this);" class="edui-clickable">' + editor.getLang("modify") + '</span></nobr>');
                        if (html) {
                            popup.getDom('content').innerHTML = html;
                            popup.anchorEl = el;
                            popup.showAnchor(popup.anchorEl);
                        } else {
                            popup.hide();
                            $("#imgComment").remove();
                        }
                    }
                });
                editor.addListener('selectionchange', function (t, causeByUi) {
                    if (!causeByUi) return;
                    var html = '', str = "",
                        img = editor.selection.getRange().getClosedNode(),
                        dialogs = editor.ui._dialogs;
                    if (img && img.tagName == 'IMG') {
                        var dialogName = 'insertimageDialog';
                        if (img.className.indexOf("edui-faked-video") != -1 || img.className.indexOf("edui-upload-video") != -1) {
                            dialogName = "insertvideoDialog"
                        }
                        if (img.className.indexOf("edui-faked-webapp") != -1) {
                            dialogName = "webappDialog"
                        }

                        //xinjiade
                        if (img.className.indexOf("edui-faked-code") != -1) {
                            dialogName = "insertcodeDialog"
                        }

                        if (img.src.indexOf("http://api.map.baidu.com") != -1) {
                            dialogName = "mapDialog"
                        }
                        if (img.className.indexOf("edui-faked-music") != -1) {
                            dialogName = "musicDialog"
                        }
                        if (img.src.indexOf("http://maps.google.com/maps/api/staticmap") != -1) {
                            dialogName = "gmapDialog"
                        }
                        if (img.getAttribute("anchorname")) {
                            dialogName = "anchorDialog";
                            html = popup.formatHtml(
                                '<nobr>' + editor.getLang("property") + ': <span onclick=$$._onImgEditButtonClick("anchorDialog") class="edui-clickable">' + editor.getLang("modify") + '</span>&nbsp;&nbsp;' +
                                '<span onclick=$$._onRemoveButtonClick(\'anchor\') class="edui-clickable">' + editor.getLang("delete") + '</span></nobr>');
                        }
                        if (img.getAttribute("word_img")) {
                            //todo 放到dialog去做查询
                            editor.word_img = [img.getAttribute("word_img")];
                            dialogName = "wordimageDialog"
                        }
                        if(domUtils.hasClass(img, 'loadingclass') || domUtils.hasClass(img, 'loaderrorclass')) {
                            dialogName = "";
                        }
                        if (!dialogs[dialogName]) {
                            return;
                        }

                        //001在图片属性窗口中，添加‘裁剪’按钮 2015/04/27
                        var imgcrophtml = '';
                        var titleImghtml = '';
                        var localImghtml = '';
                        var videoPreview = '';

                        if(img.src.indexOf('image.do?path=')!=-1){
                            imgcrophtml = '&nbsp;<span id="imageEdit" onclick=$$._onImgCropButtonClick() class="edui-clickable" titie="'+editor.getLang("contextMenu.imageeditor")+'" ></span>';
                            titleImghtml = '&nbsp;<span id="imageSetTitle" onclick=$$._onTitleImgButtonClick() class="edui-clickable" titie="'+editor.getLang("contextMenu.imagesettitle")+'" style="width: 62px;"></span>';
                            // imgcrophtml = '&nbsp;<span onclick=$$._onImgCropButtonClick() class="edui-clickable">图片修改</span>';
                            // titleImghtml = '&nbsp;<span onclick=$$._onTitleImgButtonClick() class="edui-clickable">设为标题图片</span>';
                        }

                        if(img.src.indexOf('image.do?path=') ==-1 ){ // 外网图片
                            var _sClassName = img.className;
                            if(_sClassName.indexOf('edui-faked-') == -1){
                                localImghtml = '&nbsp;<span id="imageSetLocal" onclick=$$._onlocalImgClick() class="edui-clickable" style="width: 72px;"></span>';
                            }
                        }

                        if (img.className.indexOf("edui-faked-video") != -1 || img.className.indexOf("edui-upload-video") != -1) {
                            videoPreview = '&nbsp;&nbsp;<span onclick=$$._onVideoPreviewButtonClick("'+img.getAttribute("_url")+'","video") style="overflow:hidden;width:56px;" class="edui-clickable">视频预览</span>';
                        }

                        if (img.className.indexOf("edui-faked-audio") != -1 ) {
                            videoPreview = '&nbsp;&nbsp;<span onclick=$$._onVideoPreviewButtonClick("'+img.getAttribute("_url")+'","audio") style="overflow:hidden;width:56px;" class="edui-clickable">音频试听</span>';
                        }

                        str = '<nobr>' +
                        //图片缩放
                        '<span id="imageBigger" onclick=$$._onImgScale("bigger") class="edui-clickable" titie="'+editor.getLang("contextMenu.imagebigger")+'" style=""></span>&nbsp;' +
                        '<span id="imageSmaller" onclick=$$._onImgScale("smaller") class="edui-clickable" titie="'+ editor.getLang("contextMenu.imagesmaller") +'"></span>&nbsp;' +
                        // '<span onclick=$$._onImgSetFloat("none") class="edui-clickable">' + editor.getLang("default") + '</span>&nbsp;&nbsp;' +
                        '<span id="imageLeft" onclick=$$._onImgSetFloat("left") class="edui-clickable" titie="'+ editor.getLang("contextMenu.imageleft") +'" style="border: 1px;border-color: black;"></span>' +
                        '<span id="imageRight" onclick=$$._onImgSetFloat("right") class="edui-clickable" titie="'+ editor.getLang("contextMenu.imageright") +'" ></span>'+
                        '<span id="imageCenter" onclick=$$._onImgSetFloat("center") class="edui-clickable" titie="'+ editor.getLang("contextMenu.imagecenter") +'" ></span>'
//                          +'<span onclick="$$._onImgEditButtonClick(\''
//                          + dialogName
//                          + '\');" class="edui-clickable">' + editor.getLang("modify") + '</span>'
                            +titleImghtml + imgcrophtml + localImghtml + videoPreview +

                        '&nbsp;<span id="imageComment" onclick=$$._onImgComment("imagecomment") class="edui-clickable" style="width: 25px;" titie="'+ editor.getLang("contextMenu.imagecomment") +'" ></span>' +
                        '&nbsp;<span id="imageDetele" onclick=$$._onImgDelete() class="edui-clickable" titie="'+ editor.getLang("contextMenu.imagedetele") +'" ></span>'
                        + '</nobr>';

                        !html && (html = popup.formatHtml(str))

                    }
                    if (editor.ui._dialogs.linkDialog) {
                        var link = editor.queryCommandValue('link');
                        var url;
                        if (link && (url = (link.getAttribute('_href') || link.getAttribute('href', 2)))) {
                            var txt = url;
                            if (url.length > 30) {
                                txt = url.substring(0, 20) + "...";
                            }
                            if (html) {
                                html += '<div style="height:5px;"></div>'
                            }
                            html += popup.formatHtml(
                                '<nobr>' + editor.getLang("anthorMsg") + ': <a target="_blank" href="' + url + '" title="' + url + '" >' + txt + '</a>' +
                                ' <span class="edui-clickable" onclick="$$._onEditButtonClick();">' + editor.getLang("modify") + '</span>' +
                                ' <span class="edui-clickable" onclick="$$._onRemoveButtonClick(\'unlink\');"> ' + editor.getLang("clear") + '</span></nobr>');
                            popup.showAnchor(link);
                        }
                    }

                    // if (editor.ui._dialogs.codeDialog) {
                    //     var code = editor.queryCommandValue('insertcode');
                    //     var url=editor+'dialogs/code/code.html';
                    //     cssRules:style:"width:500px;height:500px";
                    //     // if (codeDialog) {
                    //     //
                    //     //     if (html) {
                    //     //         html += '<div style="height:5px;"></div>'
                    //     //     }
                    //     //     html += popup.formatHtml(
                    //     //        // '<nobr>' + editor.getLang("anthorMsg") + ': <a target="_blank" href="' + url + '" title="' + url + '" >' + txt + '</a>' +
                    //     //         ' <span class="edui-clickable" onclick="$$._onEditButtonClick();">' + editor.getLang("modify") + '</span>' +
                    //     //         ' <span class="edui-clickable" onclick="$$._onRemoveButtonClick(\'unlink\');"> ' + editor.getLang("clear") + '</span></nobr>');
                    //     //     popup.showAnchor(code);
                    //     // }
                    // }

                    if (html) {
                        popup.getDom('content').innerHTML = html;
                        popup.anchorEl = img || link;
                        popup.showAnchor(popup.anchorEl, true);
                    } else {
                        popup.hide();

                    }
                });
            }

        },
        _initToolbars:function () {//fzf 重写了此方法
            var editor = this.editor;
            var toolbars = this.toolbars || [];
            var toolbarUis = {};//是一个二级对象
            for (var tabKey in toolbars) {//TAB标签
                //alert(tabKey);
                var tab = toolbars[tabKey];
                //alert("tab="+tab);
                var subTypeUis = {};
                for (var subTypeKey in tab) {//小分类
                    //alert("subTypeKey="+subTypeKey);
                    var subType = tab[subTypeKey];
                    var toolbarUi = new baidu.editor.ui.Toolbar({theme:editor.options.theme});
                    for (var j = 0; j < subType.length; j++) {//每一个按钮
                        var toolbarItem = subType[j];
                        var toolbarItemUi = null;
                        if (typeof toolbarItem == 'string') {//自己定义的字符串
                            toolbarItem = toolbarItem.toLowerCase();
                            if (toolbarItem == '|') {
                                toolbarItem = 'Separator';
                            }
                            if(toolbarItem == '||'){
                                toolbarItem = 'Breakline';
                            }
                            if (baidu.editor.ui[toolbarItem]) {
                                toolbarItemUi = new baidu.editor.ui[toolbarItem](editor);
                            }

                        } else {//直接是对象
                            toolbarItemUi = toolbarItem;
                        }

                        if (toolbarItemUi && toolbarItemUi.id) {
                            toolbarUi.add(toolbarItemUi);
                        }
                    }//end button
                    subTypeUis[subTypeKey] = toolbarUi;

                }//end subType
                toolbarUis[tabKey] = subTypeUis;
            }//end tab

            //接受外部定制的UI
            utils.each(UE._customizeUI,function(obj,key){
                var itemUI,index;
                if(obj.id && obj.id != editor.key){
                    return false;
                }
                itemUI = obj.execFn.call(editor,editor,key);
                if(itemUI){
                    index = obj.index;
                    if(index === undefined){
                        index = toolbarUi.items.length;
                    }
                    toolbarUi.add(itemUI,index)
                }
            });

            this.toolbars = toolbarUis;
        },
        getHtmlTpl:function () {//fzf 重写了此方法
            if(UE.type==null){
                return this.renderToolbarBoxHtml();
                debugger
            }else if(UE.type=="activity"){
                return this.renderToolbarBoxHtmlofActivity();
            }

        },
        showWordImageDialog:function () {
            this._dialogs['wordimageDialog'].open();
        },
        _saveUp: function(){
            $('#SYS_TOPIC').val($('#SYS_TOPICDIV').text())
        },
        _savaChange:function(){
            /*
             *title 更变时自动保存
            * */

            $('#SYS_TOPIC').val($('#SYS_TOPICDIV').text())

            var editor = this.editor;
            uid = editor.trigger('showmessage',{
                content : editor.getLang('autosave.success'),
                timeout : 1000
            });

            //先加载图片再保存至草稿箱
            //var contentTxt = editor.getContent();
            // if(contentTxt.indexOf('img src="http')!=-1){
            //     $('div[title="批量下载图片"]').trigger('click');
            // }
            savaDraft(editor);
        },
        _tabOpen: function () {
            $('#tab_other').show();

            if($('#tab_open').hasClass('tab_openShow')){
                localStorage.setItem("toolState",0);
                // alert(111)
                $('#tab_open').removeClass('tab_openShow');
                $('#tab_open').text('展开');
                $('#start_container').css({
                    'height': '38px'
                })
                $('#tab_other').hide()
            }else{
                localStorage.setItem("toolState",1);
                $('#start_container').css({
                    'height': '64px'
                })
                $('#tab_open').text('收起');

                $('#tab_open').addClass('tab_openShow')
            }
        },
	    _tabOpenImage: function(){
		    if($("#tabOpenImage .tab_openImageList").hasClass('tab_openShowImage')){
			    $("#tabOpenImage .tab_openImageList").removeClass('tab_openShowImage');
			    $("#tabOpenImage .tab_openImage").text('选择图片');
			 
            }else{
			    $("#tabOpenImage .tab_openImageList").addClass('tab_openShowImage');
			    $("#tabOpenImage .tab_openImage").text('关闭窗口');
			    var imageClick = this._tab_openImageList();
			    $(".tab_openImageList").html(imageClick)
		    }
        },
        renderToolbarBoxHtmlOld:function () {//fzf 重写了此方法
            var editor = this.editor;
            var buff = [];
            buff.push('<div id="##" class="%%">');
            buff.push('<div id="##_toolbarbox" class="%%-toolbarbox"style="width:100%;">');
            buff.push('<div id="##_toolbarboxouter" class="%%-toolbarboxouter"style="width:100%;"><div class="%%-toolbarboxinner"style="width:100%;">');

            //TAB标签
            buff.push('<div class="tab_head" style="width:100%; height:25px;">');
            buff.push('<ul class="tab_list" id="tab_list">');
            for (var tabKey in this.toolbars) {
                var tab = this.toolbars[tabKey];
                buff.push('<li class="default" id="'+tabKey+'">'+editor.options.labelMap[tabKey]+'</li>');
            }
            buff.push('</ul>');

            //字体大中小
            buff.push('<div id="viewSize">');
            //001重新套了一个ID为font-contianer的div
            buff.push('<div id="font-contianer"style="width:85px;height:26px;line-height:26px;display:block;float:right">');
            buff.push('<div class="font_size font_size_1" style="width:14px;height:14px;" onclick="doResizeFont(document.getElementById(\'ueditor_0\').contentWindow.document.body,4,1)"></div>');
            buff.push('<div class="font_size font_size_select_2" style="width:14px;height:14px;" onclick="doResizeFont(document.getElementById(\'ueditor_0\').contentWindow.document.body,0,2)"></div>');
            buff.push('<div class="font_size font_size_3" style="width:14px;height:14px;" onclick="doResizeFont(document.getElementById(\'ueditor_0\').contentWindow.document.body,-4,3)"></div>');
            buff.push('</div>');
            buff.push('</div>');

            buff.push('</div>');

            //所有操作按钮
            for (var tabKey in this.toolbars) {
                var tab = this.toolbars[tabKey];
                var subTypeUis = {};
                buff.push('<div class="tab_content" style=" display:none;width:100%;height:85px;" id="'+tabKey+'_container">');
                buff.push('<div class="tab_split" style="width:3px; height:82px;"></div>');
                for (var subTypeKey in tab) {//小分类
                    var subType = tab[subTypeKey];
                    //开始生成小分类内容！！！！！
                    buff.push('<div class="tab_outer"');
                    if(editor.options.toolbarsWidth[subTypeKey] != undefined && !isNaN(parseInt(editor.options.toolbarsWidth[subTypeKey])) ){
                        buff.push(' style="width:'+editor.options.toolbarsWidth[subTypeKey]+'px;"');
                    }
                    buff.push('>');
                    buff.push('<div class="tab_t">'+subType.renderHtml()+'</div>');
                    buff.push('<div class="tab_b">'+editor.options.labelMap[subTypeKey]+'</div>');
                    buff.push('</div>');
                    buff.push('<div class="tab_split" style="width:3px; height:82px;"></div>');
                }
                buff.push('</div>');
            }
            buff.push('</div></div>');
            buff.push('<div id="##_toolbarmsg" class="%%-toolbarmsg" style="display:none;">');
            buff.push('<div id = "##_upload_dialog" class="%%-toolbarmsg-upload" onclick="$$.showWordImageDialog();">');
            buff.push(this.editor.getLang("clickToUpload"));
            buff.push('</div>');
            buff.push('<div class="%%-toolbarmsg-close" onclick="$$.hideToolbarMsg();">x</div>');
            buff.push('<div id="##_toolbarmsg_label" class="%%-toolbarmsg-label"></div>');
            buff.push('<div style="height:0;overflow:hidden;clear:both;"></div>');
            buff.push('</div>');
            buff.push('<div id="##_message_holder" class="%%-messageholder"></div>');
            buff.push('</div>');
            //----
            buff.push('<div id="middleInEditor">');
            buff.push('<div id="divTitle"></lable><input onChange = "$$._savaChange();" type="text" id="SYS_TOPIC" name="SYS_TOPIC" value="" class="validate[maxSize[1024],required]" placeholder="请在这里输入文章标题">');
            buff.push('<input type="button" class="btn" id="btnTitleAdv" value="编辑样式"/>');
            buff.push('	<lable id="lbwordcount"></lable><label style="display: inline;" for="a_copyright"><input type="checkbox" id="a_copyright" name="a_copyright">原创</label></div>');
            buff.push('<div id="divAuthor"><li>作者：<input type="text" id="SYS_AUTHORS" name="SYS_AUTHORS" class="validate[maxSize[255]]" placeholder="请输入作者姓名"></li>');
            buff.push('<li>来源：<input type="text" id="findSourceInput" class="validate[maxSize[255]]" placeholder="请输入稿件来源"></li>');
            buff.push('<li><input class="btn" type="button" id="btnSource" title="选择来源" value="选择"/></li>');
            buff.push('<li class="sourceUrl">来源链接：<input type="text" id="a_sourceUrl" name="a_sourceUrl" class="validate[maxSize[255]]" placeholder="来源链接"></li>');
            buff.push('</div>');
            buff.push('</div>'); 
            buff.push('<div id="##_iframeholder" class="%%-iframeholder"></div>');
            buff.push('<div id="##_bottombar" class="%%-bottomContainer"><table><tr>');
            buff.push('<td id="##_elementpath" class="%%-bottombar"></td>');
            buff.push('<td id="##_wordcount" class="%%-wordcount"></td>');
            buff.push('<td id="##_scale" class="%%-scale"><div class="%%-icon"></div></td>');
            buff.push('</tr></table></div>');
            buff.push('<div id="##_scalelayer"></div>');
            buff.push('</div>');
            return buff.join('');
        },
	    _tab_openImageList: function () {
		    var imageList = '';
	        $.ajax({
		        url: "../../../xy/pic/getNewPics.do",
		        type: 'GET',
		        async:false,
		        data: {
			        "siteID": article.siteID
		        },
		        success: function (data) {
			        if(data.length){
				        var arrimageList = data;
				        for(var i = 0; i<arrimageList.length; i++){
					        imageList +='<li><img src="'+ '../../xy/image.do?path=' + arrimageList[i].path +'" ></li>'
				        }
                    }else{
				        
                    }
		        },
                error: function () {
                }
	        })
		    imageList += '<li><p>'+ "更多图片" +'</p></li>'
		    return imageList;
        },
	    _tabChooseImage: function (event) {
	        if(event.target.nodeName == "P"){
		        $("#edui91_body").click();
	            return;
            }
		    var editor = this.editor;
		    var str =  "<p><img src="+ event.toElement.currentSrc +"></p>"
		    //这时候需要将拿到图片插入到编辑器中
		    editor.execCommand('insertHtml', str);
	    },
        renderToolbarBoxHtml:function () {//qxm 重写了此方法
            var editor = this.editor;
            var buff = [];
            buff.push('<div id="##" class="%%">');
            buff.push('<div id="##_toolbarbox" class="%%-toolbarbox" style="width:100%;">');
            buff.push('<div id="##_toolbarboxouter" class="%%-toolbarboxouter" style="width:100%;"><div class="%%-toolbarboxinner"style="width:100%;">');

            //所有操作按钮
            // buff.push('<div class="container"; style="position: absolute;top:100px;left: 50px ;"><table style="width: 500px;" class="table table-striped table-hover table-bordered"><thead><tr><th>' +" 最后处理时间 " +'</th><th>'+ "稿件标题/内容" +'</th></tr></thead><tbody><tr><td>'+ "时间，年月日" +'</td><td>'+ "时间，年月日" +'</td> </tr><tr><td>'+ "时间，年月日" +'</td><td>'+ "时间，年月日" +'</td></tr><tr><td>'+ "时间，年月日" +'</td> <td>'+ "时间，年月日" +'</td></tr></tbody></table></div>')
            buff.push('<div id="box" class="draftNone" style="z-index: 10000;top: 94px;left:0px;position: absolute; background-color:#ffffff"><div class="container" style="width: 500px;height: 400px;"><table style="width: 500px;" class="table  table-hover table-bordered"> <thead style="display:block;border-bottom: 1px solid #ddd;"> <tr><th style="width: 112px;text-align: center;">'+ "最后处理时间" +'</th><th style="text-align: center;width: 354px;">'+ "稿件标题/内容" +'</th></tr></thead><tbody style="overflow-y:scroll;display:block;height: 360px;"></tbody> </table></div><div class="draftNone" id="sure" style="padding-top: 10px;padding-bottom:10px;background-color: #ffffff;border-radius: 4px;"><div class="btn btn-sm edui-default" onclick="sureFun();" style="margin-left: 186px;background: rgb(33,156,229);color: #ffffff;">'+ "打开" +'</div> <div class="btn btn-sm edui-default" style="margin-left:20px;background: rgb(153,153,153);color: #ffffff;">'+ "取消" +' </div></div></div>')
            // buff.push('<div id="drafts" style="display: none;position: absolute;width: 200px;height: 200px;background: red;"></div>');
            // buff.push('<div id="draftsMask" style="display: none;width: 100%;height: 689px;background-color: #d09191;z-index: 100000;position: absolute;top: 62px;opacity: 0.3;" class="edui-default"></div>');

            var toolState = localStorage.getItem('toolState'),
                tab_openShowState = '',
                tab_contentHeight = '',
                tab_otherDisplay = '',
                wordState = '';
            if(toolState == 0){
                tab_openShowState = '';
                tab_contentHeight = '38px';
                tab_otherDisplay = 'tab_other';
                wordState = '展开';
            }else{
                tab_openShowState = 'tab_openShow';
	            tab_contentHeight = '64px';
                tab_otherDisplay = ''
                wordState = '收起'
                // $('#tab_open').text('收起');
                //
                // $('#tab_open').addClass('tab_openShow')
            }
	
	       
	

	        for (var tabKey in this.toolbars) {
                var tab = this.toolbars[tabKey];
                var subTypeUis = {};
               if(tabKey=="start"){
                   buff.push('<div class="tab_content" style=" display:;width:100%;height:'+ tab_contentHeight +'" id="'+tabKey+'_container">');
                   //buff.push('<div class="tab_split" style="width:3px; height:24px;"></div>');
                   for (var subTypeKey in tab) {//小分类
                       var subType = tab[subTypeKey];
                       //开始生成小分类内容！！！！！
                       buff.push('<div class="tab_outer"');
                       if(editor.options.toolbarsWidth[subTypeKey] != undefined && !isNaN(parseInt(editor.options.toolbarsWidth[subTypeKey])) ){
                           //buff.push(' style="width:'+editor.options.toolbarsWidth[subTypeKey]+'px;"');
                       }
                       buff.push('>');
                       buff.push('<div class="tab_t">'+subType.renderHtml()+'</div>');
                       buff.push('</div>');
                       buff.push('<div class="tab_split" style="width:3px; height:30px;"></div>');
                   }
	               buff.push('<div id="tab_open" onclick="$$._tabOpen();" class="tab_open '+  tab_openShowState +'" style="width: 13px;">'+ wordState +'</div>');
	               
                   buff.push('<div id="tabOpenImage">'+
	               '<div class="tab_openImage" onclick="$$._tabOpenImage();" onselectstart="return false">选择图片</div>'+
	               '<ul class="tab_openImageList" onclick="$$._tabChooseImage(event);">'+
	                   //this.imageClick +
	                   // $$._tab_openImageList() +
	                   // imageClick +
	               
	               '</ul>'+
	               '</div>');
               }else if(tabKey=="other"){
                   buff.push('<div id="tab_other" class="'+ tab_otherDisplay +'">');
                   for (var subTypeKey in tab) {//小分类
                       var subType = tab[subTypeKey];
                       buff.push('<div class="tab_t" style="height: 30px;">');
                       //字体大中小
                       buff.push('<div id="viewSize">');
                       //001重新套了一个ID为font-contianer的div
                       buff.push('<div id="font-contianer"style="height:26px;line-height:26px;display:block;">');
                       buff.push('<div class="font_size font_size_1" style="width:14px;height:14px;" onclick="doResizeFont(document.getElementById(\'ueditor_0\').contentWindow.document.body,4,1)"></div>');
                       buff.push('<div class="font_size font_size_select_2" style="width:14px;height:14px;" onclick="doResizeFont(document.getElementById(\'ueditor_0\').contentWindow.document.body,0,2)"></div>');
                       buff.push('<div class="font_size font_size_3" style="width:14px;height:14px;" onclick="doResizeFont(document.getElementById(\'ueditor_0\').contentWindow.document.body,-4,3)"></div>');
                       buff.push('</div>');
                       buff.push('</div>');
                       buff.push(subType.renderHtml());
                       buff.push('</div>');
                   }

                   buff.push('</div>');
               }else{
                   //不需要生成的内容，但是需要占位
                   buff.push('<div style="display: none;">');
                   for (var subTypeKey in tab) {//小分类
                       var subType = tab[subTypeKey];
                       //开始生成小分类内容！！！！！
                       buff.push('<div class="tab_t">'+subType.renderHtml()+'</div>');
                   }
                   buff.push('</div>');
               }
            }

            buff.push('</div>');
            buff.push('</div></div>');
            buff.push('<div id="##_toolbarmsg" class="%%-toolbarmsg" style="display:none;">');
            buff.push('<div id = "##_upload_dialog" class="%%-toolbarmsg-upload" onclick="$$.showWordImageDialog();">');
            buff.push(this.editor.getLang("clickToUpload"));
            buff.push('</div>');
            buff.push('<div class="%%-toolbarmsg-close" onclick="$$.hideToolbarMsg();">x</div>');
            buff.push('<div id="##_toolbarmsg_label" class="%%-toolbarmsg-label"></div>');
            buff.push('<div style="height:0;overflow:hidden;clear:both;"></div>');
            buff.push('</div>');
            buff.push('<div id="##_message_holder" class="%%-messageholder"></div>');
            buff.push('</div>');
            //----
            buff.push('<div id="middleInEditor">');
            //buff.push('<div id="divTitle"></lable><input type="text" onChange = "$$._savaChange();" id="SYS_TOPIC" name="SYS_TOPIC" value="" class="validate[maxSize[1024],required]" placeholder="请在这里输入文章标题002">');
            buff.push('<div id="divTitle"><input type="text"  id="SYS_TOPIC" name="SYS_TOPIC" value="" class="validate[maxSize[1024],required] SYS_TOPIC" placeholder="请在这里输入文章标题">' +
                '<div data-placeholder="请在这里输入文章标题" onkeyup= "$$._saveUp" onblur = "$$._savaChange();" id="SYS_TOPICDIV"  class="le-text"  contenteditable="true" style="line-height: 28px;user-modify: read-write-plaintext-only; -webkit-user-modify: read-write-plaintext-only;"></div>');
            buff.push('<input type="button" class="btn" id="btnTitleAdv" value="编辑样式"/>');
            buff.push('	<lable id="lbwordcount"></lable><label style="display: inline;" for="a_copyright"><input type="checkbox" id="a_copyright" name="a_copyright">原创</label></div>');
            buff.push('<div id="divAuthor"><li>作者：<input type="text" id="SYS_AUTHORS" style="margin-bottom: 0px;padding-bottom: 2px;border-bottom: 1px solid #ddd;" name="SYS_AUTHORS" class="validate[maxSize[255]]" placeholder=""></li>');
            buff.push('<li>来源：<input type="text" id="findSourceInput" style="margin-bottom: 0px;padding-bottom: 2px;border-bottom: 1px solid #ddd;" class="validate[maxSize[255]]" placeholder=""></li>');
            buff.push('<li><input class="btn" type="button" id="btnSource"  title="选择来源" value="选择"/></li>');
            buff.push('<li class="sourceUrl">来源链接：<input type="text" id="a_sourceUrl" style="margin-bottom: 0px;padding-bottom: 2px;border-bottom: 1px solid #ddd;" name="a_sourceUrl" class="validate[maxSize[255]]" placeholder=""></li>');
            buff.push('</div>');
            buff.push('</div>');

            buff.push('<div id="##_bottombar" class="%%-bottomContainer"><table><tr>');
            buff.push('<td id="##_elementpath" class="%%-bottombar"></td>');
            buff.push('<td id="##_wordcount" class="%%-wordcount"></td>');
            buff.push('<td id="##_scale" class="%%-scale"><div class="%%-icon"></div></td>');
            buff.push('</tr></table></div>');

            buff.push('<div id="##_iframeholder" class="%%-iframeholder"></div>');

            buff.push('<div id="##_scalelayer"></div>');
            buff.push('</div>');
            return buff.join('');
        },
        renderToolbarBoxHtmlofActivity:function () {//qxm 重写了此方法
            var editor = this.editor;
            var buff = [];
            buff.push('<div id="##" class="%%">');
            buff.push('<div id="##_toolbarbox" class="%%-toolbarbox"style="width:100%;">');
            buff.push('<div style="color: #333;font-size: 14px;padding: 8px;font-weight: 700;">活动详情</div>');

            buff.push('<div id="##_toolbarboxouter" class="%%-toolbarboxouter"style="width:100%;padding-top:4px;border-radius: 0;background: #eee;"><div class="%%-toolbarboxinner"style="width:100%;">');
            //所有操作按钮
            for (var tabKey in this.toolbars) {
                var tab = this.toolbars[tabKey];
                var subTypeUis = {};
                if(tabKey=="activity"){
                    buff.push('<div class="tab_content" style=" display:none;width:100%;height:26px;background:#eee;" id="'+tabKey+'_container">');
                   /* buff.push('<div class="tab_split" style="width:3px; height:60px;"></div>');*/
                    for (var subTypeKey in tab) {//小分类
                        var subType = tab[subTypeKey];
                        //开始生成小分类内容！！！！！
                        buff.push('<div class="tab_outer"');
                        if(editor.options.toolbarsWidth[subTypeKey] != undefined && !isNaN(parseInt(editor.options.toolbarsWidth[subTypeKey])) ){
                            buff.push(' style="width:'+editor.options.toolbarsWidth[subTypeKey]+'px;"');
                        }
                        buff.push('>');
                        buff.push('<div style="height:26px;" class="tab_t">'+subType.renderHtml()+'</div>');
                        buff.push('</div>');
                       /* buff.push('<div class="tab_split" style="width:3px; height:60px;"></div>');*/
                    }
                }else{
                    //不需要生成的内容，但是需要占位
                    buff.push('<div style="display: none;">');
                    for (var subTypeKey in tab) {//小分类
                        var subType = tab[subTypeKey];
                        //开始生成小分类内容！！！！！
                        buff.push('<div class="tab_t">'+subType.renderHtml()+'</div>');
                    }
                    buff.push('</div>');
                }
            }

            buff.push('</div>');
            buff.push('</div></div>');
            buff.push('<div id="##_toolbarmsg" class="%%-toolbarmsg" style="display:none;">');
            buff.push('<div id = "##_upload_dialog" class="%%-toolbarmsg-upload" onclick="$$.showWordImageDialog();">');
            buff.push(this.editor.getLang("clickToUpload"));
            buff.push('</div>');
            buff.push('<div class="%%-toolbarmsg-close" onclick="$$.hideToolbarMsg();">x</div>');
            buff.push('<div id="##_toolbarmsg_label" class="%%-toolbarmsg-label"></div>');
            buff.push('<div style="height:0;overflow:hidden;clear:both;"></div>');
            buff.push('</div>');
            buff.push('<div id="##_message_holder" class="%%-messageholder"></div>');
            buff.push('</div>');
            //隐藏区域
             buff.push('<div id="middleInEditor"  style="display:none;">');
             buff.push('<div id="divTitle"></lable><input type="text" id="SYS_TOPIC" name="SYS_TOPIC" value="" class="validate[maxSize[1024],required]" placeholder="请在这里输入文章标题">');
             buff.push('<input type="button" class="btn" id="btnTitleAdv" value="编辑样式"/>');
             buff.push('	<lable id="lbwordcount"></lable><label style="display: inline;" for="a_copyright"><input type="checkbox" id="a_copyright" name="a_copyright">原创</label></div>');
             buff.push('<div id="divAuthor"><li>作者：<input type="text" id="SYS_AUTHORS" name="SYS_AUTHORS" class="validate[maxSize[255]]" placeholder="请输入作者姓名"></li>');
             buff.push('<li>来源：<input type="text" id="findSourceInput" class="validate[maxSize[255]]" placeholder="请输入稿件来源"></li>');
            buff.push('<li><input class="btn" type="button" id="btnSource" title="选择来源" value="选择"/></li>');
            buff.push('<li class="sourceUrl">来源链接：<input type="text" id="a_sourceUrl" name="a_sourceUrl" class="validate[maxSize[255]]" placeholder="来源链接"></li>');
             buff.push('</div>');
             buff.push('</div>');

             buff.push('<div id="##_bottombar" style="display:none;" class="%%-bottomContainer"><table><tr>');
             buff.push('<td id="##_elementpath" class="%%-bottombar"></td>');
             buff.push('<td id="##_wordcount" class="%%-wordcount"></td>');
             buff.push('<td id="##_scale" class="%%-scale"><div class="%%-icon"></div></td>');
             buff.push('</tr></table></div>');

            buff.push('<div id="##_iframeholder" class="%%-iframeholder" style="min-height: 300px;"></div>');

            buff.push('<div id="##_scalelayer"></div>');
            buff.push('</div>');
            return buff.join('');
        },
        setFullScreen:function (fullscreen) {
            var editor = this.editor,
                container = editor.container.parentNode.parentNode;
            if (this._fullscreen != fullscreen) {
                this._fullscreen = fullscreen;
                this.editor.fireEvent('beforefullscreenchange', fullscreen);
                if (baidu.editor.browser.gecko) {
                    var bk = editor.selection.getRange().createBookmark();
                }
                if (fullscreen) {
                    while (container.tagName != "BODY") {
                        var position = baidu.editor.dom.domUtils.getComputedStyle(container, "position");
                        nodeStack.push(position);
                        container.style.position = "static";
                        container = container.parentNode;
                    }
                    this._bakHtmlOverflow = document.documentElement.style.overflow;
                    this._bakBodyOverflow = document.body.style.overflow;
                    this._bakAutoHeight = this.editor.autoHeightEnabled;
                    this._bakScrollTop = Math.max(document.documentElement.scrollTop, document.body.scrollTop);

                    this._bakEditorContaninerWidth = editor.iframe.parentNode.offsetWidth;
                    if (this._bakAutoHeight) {
                        //当全屏时不能执行自动长高
                        editor.autoHeightEnabled = false;
                        this.editor.disableAutoHeight();
                    }

                    document.documentElement.style.overflow = 'hidden';
                    //修复，滚动条不收起的问题

                    window.scrollTo(0,window.scrollY);
                    this._bakCssText = this.getDom().style.cssText;
                    this._bakCssText1 = this.getDom('iframeholder').style.cssText;
                    //editor.iframe.parentNode.style.width = '';
                    this._updateFullScreen();
                } else {
                    while (container.tagName != "BODY") {
                        container.style.position = nodeStack.shift();
                        container = container.parentNode;
                    }
                    this.getDom().style.cssText = this._bakCssText;
                    this.getDom('iframeholder').style.cssText = this._bakCssText1;
                    if (this._bakAutoHeight) {
                        editor.autoHeightEnabled = true;
                        this.editor.enableAutoHeight();
                    }

                    document.documentElement.style.overflow = this._bakHtmlOverflow;
                    document.body.style.overflow = this._bakBodyOverflow;
                  //  editor.iframe.parentNode.style.width = this._bakEditorContaninerWidth + 'px';
                    window.scrollTo(0, this._bakScrollTop);
                }
                if (browser.gecko && editor.body.contentEditable === 'true') {
                    var input = document.createElement('input');
                    document.body.appendChild(input);
                    editor.body.contentEditable = false;
                    setTimeout(function () {
                        input.focus();
                        setTimeout(function () {
                            editor.body.contentEditable = true;
                            editor.fireEvent('fullscreenchanged', fullscreen);
                            editor.selection.getRange().moveToBookmark(bk).select(true);
                            baidu.editor.dom.domUtils.remove(input);
                            fullscreen && window.scroll(0, 0);
                        }, 0)
                    }, 0)
                }

                if(editor.body.contentEditable === 'true'){
                    this.editor.fireEvent('fullscreenchanged', fullscreen);
                    this.triggerLayout();
                }

            }
        },
        _updateFullScreen:function () {
            if (this._fullscreen) {
                var vpRect = uiUtils.getViewportRect();
                this.getDom().style.cssText = 'border:0;position:absolute;left:0;top:' + (this.editor.options.topOffset || 0) + 'px;width:' + vpRect.width + 'px;height:' + vpRect.height + 'px;z-index:' + (this.getDom().style.zIndex * 1 + 100);
                uiUtils.setViewportOffset(this.getDom(), { left:0, top:this.editor.options.topOffset || 0 });
                this.editor.setHeight(vpRect.height - this.getDom('toolbarbox').offsetHeight - this.getDom('bottombar').offsetHeight - (this.editor.options.topOffset || 0),true);
                //不手动调一下，会导致全屏失效
                if(browser.gecko){
                    try{
                        window.onresize();
                    }catch(e){

                    }

                }
            }
        },
        _updateElementPath:function () {
            var bottom = this.getDom('elementpath'), list;
            if (this.elementPathEnabled && (list = this.editor.queryCommandValue('elementpath'))) {

                var buff = [];
                for (var i = 0, ci; ci = list[i]; i++) {
                    buff[i] = this.formatHtml('<span unselectable="on" onclick="$$.editor.execCommand(&quot;elementpath&quot;, &quot;' + i + '&quot;);">' + ci + '</span>');
                }
                bottom.innerHTML = '<div class="edui-editor-breadcrumb" onmousedown="return false;">' + this.editor.getLang("elementPathTip") + ': ' + buff.join(' &gt; ') + '</div>';

            } else {
                bottom.style.display = 'none'
            }
        },
        disableElementPath:function () {
            var bottom = this.getDom('elementpath');
            bottom.innerHTML = '';
            bottom.style.display = 'none';
            this.elementPathEnabled = false;

        },
        enableElementPath:function () {
            var bottom = this.getDom('elementpath');
            bottom.style.display = '';
            this.elementPathEnabled = true;
            this._updateElementPath();
        },
        _scale:function () {
            var doc = document,
                editor = this.editor,
                editorHolder = editor.container,
                editorDocument = editor.document,
                toolbarBox = this.getDom("toolbarbox"),
                bottombar = this.getDom("bottombar"),
                scale = this.getDom("scale"),
                scalelayer = this.getDom("scalelayer");

            var isMouseMove = false,
                position = null,
                minEditorHeight = 0,
                minEditorWidth = editor.options.minFrameWidth,
                pageX = 0,
                pageY = 0,
                scaleWidth = 0,
                scaleHeight = 0;

            function down() {
                position = domUtils.getXY(editorHolder);

                if (!minEditorHeight) {
                    
                    minEditorHeight = editor.options.minFrameHeight + toolbarBox.offsetHeight + bottombar.offsetHeight;
                }

                scalelayer.style.cssText = "position:absolute;left:0;display:;top:0;background-color:#41ABFF;opacity:0.4;filter: Alpha(opacity=40);width:" + editorHolder.offsetWidth + "px;height:"
                + editorHolder.offsetHeight + "px;z-index:" + (editor.options.zIndex + 1);

                domUtils.on(doc, "mousemove", move);
                domUtils.on(editorDocument, "mouseup", up);
                domUtils.on(doc, "mouseup", up);
            }

            var me = this;
            //by xuheng 全屏时关掉缩放
            this.editor.addListener('fullscreenchanged', function (e, fullScreen) {
                if (fullScreen) {
                    me.disableScale();

                } else {
                    if (me.editor.options.scaleEnabled) {
                        me.enableScale();
                        var tmpNode = me.editor.document.createElement('span');
                        me.editor.body.appendChild(tmpNode);
                        me.editor.body.style.height = Math.max(domUtils.getXY(tmpNode).y, me.editor.iframe.offsetHeight - 20) + 'px';
                        domUtils.remove(tmpNode)
                    }
                }
            });
            function move(event) {
                clearSelection();
                var e = event || window.event;
                pageX = e.pageX || (doc.documentElement.scrollLeft + e.clientX);
                pageY = e.pageY || (doc.documentElement.scrollTop + e.clientY);
                scaleWidth = pageX - position.x;
                scaleHeight = pageY - position.y;

                if (scaleWidth >= minEditorWidth) {
                    isMouseMove = true;
                    scalelayer.style.width = scaleWidth + 'px';
                }
                if (scaleHeight >= minEditorHeight) {
                    isMouseMove = true;
                    scalelayer.style.height = scaleHeight + "px";
                }
            }

            function up() {
                if (isMouseMove) {
                    isMouseMove = false;
                    editor.ui._actualFrameWidth = scalelayer.offsetWidth - 2;
                    editorHolder.style.width = editor.ui._actualFrameWidth + 'px';

                    editor.setHeight(scalelayer.offsetHeight - bottombar.offsetHeight - toolbarBox.offsetHeight - 2,true);
                }
                if (scalelayer) {
                    scalelayer.style.display = "none";
                }
                clearSelection();
                domUtils.un(doc, "mousemove", move);
                domUtils.un(editorDocument, "mouseup", up);
                domUtils.un(doc, "mouseup", up);
            }

            function clearSelection() {
                if (browser.ie)
                    doc.selection.clear();
                else
                    window.getSelection().removeAllRanges();
            }

            this.enableScale = function () {
                //trace:2868
                if (editor.queryCommandState("source") == 1)    return;
                scale.style.display = "";
                this.scaleEnabled = true;
                domUtils.on(scale, "mousedown", down);
            };
            this.disableScale = function () {
                scale.style.display = "none";
                this.scaleEnabled = false;
                domUtils.un(scale, "mousedown", down);
            };
        },
        isFullScreen:function () {
            return this._fullscreen;
        },
        postRender:function () {
            UIBase.prototype.postRender.call(this);
            for (var i = 0; i < this.toolbars.length; i++) {
                this.toolbars[i].postRender();
            }
            var me = this;
            var timerId,
                domUtils = baidu.editor.dom.domUtils,
                updateFullScreenTime = function () {
                    clearTimeout(timerId);
                    timerId = setTimeout(function () {
                        me._updateFullScreen();
                    });
                };
            domUtils.on(window, 'resize', updateFullScreenTime);

            me.addListener('destroy', function () {
                domUtils.un(window, 'resize', updateFullScreenTime);
                clearTimeout(timerId);
            })
        },
        showToolbarMsg:function (msg, flag) {
            this.getDom('toolbarmsg_label').innerHTML = msg;
            this.getDom('toolbarmsg').style.display = '';
            //
            if (!flag) {
                var w = this.getDom('upload_dialog');
                w.style.display = 'none';
            }
        },
        hideToolbarMsg:function () {
            this.getDom('toolbarmsg').style.display = 'none';
        },
        mapUrl:function (url) {
            return url ? url.replace('~/', this.editor.options.UEDITOR_HOME_URL || '') : ''
        },
        triggerLayout:function () {
            var dom = this.getDom();
            if (dom.style.zoom == '1') {
                dom.style.zoom = '100%';
            } else {
                dom.style.zoom = '1';
            }
        }
    };
    utils.inherits(EditorUI, baidu.editor.ui.UIBase);

    var instances = {};

    UE.ui.Editor = function (options) {
        var editor = new UE.Editor(options);
        editor.options.editor = editor;
        utils.loadFile(document, {
            href:editor.options.themePath + editor.options.theme + "/css/ueditor.css",
            tag:"link",
            type:"text/css",
            rel:"stylesheet"
        });

        var oldRender = editor.render;
        editor.render = function (holder) {
            if (holder.constructor === String) {
                editor.key = holder;
                instances[holder] = editor;
            }
            utils.domReady(function () {
                editor.langIsReady ? renderUI() : editor.addListener("langReady", renderUI);
                function renderUI() {
                    editor.setOpt({
                        labelMap:editor.options.labelMap || editor.getLang('labelMap')
                    });
                    new EditorUI(editor.options);
                    if (holder) {
                        if (holder.constructor === String) {
                            holder = document.getElementById(holder);
                        }
                        holder && holder.getAttribute('name') && ( editor.options.textarea = holder.getAttribute('name'));
                        if (holder && /script|textarea/ig.test(holder.tagName)) {
                            var newDiv = document.createElement('div');
                            holder.parentNode.insertBefore(newDiv, holder);
                            var cont = holder.value || holder.innerHTML;
                            editor.options.initialContent = /^[\t\r\n ]*$/.test(cont) ? editor.options.initialContent :
                                cont.replace(/>[\n\r\t]+([ ]{4})+/g, '>')
                                    .replace(/[\n\r\t]+([ ]{4})+</g, '<')
                                    .replace(/>[\n\r\t]+</g, '><');
                            holder.className && (newDiv.className = holder.className);
                            holder.style.cssText && (newDiv.style.cssText = holder.style.cssText);
                            if (/textarea/i.test(holder.tagName)) {
                                editor.textarea = holder;
                                editor.textarea.style.display = 'none';


                            } else {
                                holder.parentNode.removeChild(holder);


                            }
                            if(holder.id){
                                newDiv.id = holder.id;
                                domUtils.removeAttributes(holder,'id');
                            }
                            holder = newDiv;
                            holder.innerHTML = '';
                        }

                    }
                    domUtils.addClass(holder, "edui-" + editor.options.theme);
                    editor.ui.render(holder);
                    var opt = editor.options;
                    opt.initialFrameHeight = $(window).height();
                    //给实例添加一个编辑器的容器引用
                    editor.container = editor.ui.getDom();
                    var parents = domUtils.findParents(holder,true);
                    var displays = [];
                    for(var i = 0 ,ci;ci=parents[i];i++){
                        displays[i] = ci.style.display;
                        ci.style.display = 'block'
                    }
                    if (opt.initialFrameWidth) {
                        opt.minFrameWidth = opt.initialFrameWidth;
                    } else {
                        opt.minFrameWidth = opt.initialFrameWidth = holder.offsetWidth;
                        var styleWidth = holder.style.width;
                        if(/%$/.test(styleWidth)) {
                            opt.initialFrameWidth = styleWidth;
                        }
                    }
                    if (opt.initialFrameHeight) {
                        opt.minFrameHeight = opt.initialFrameHeight;
                    } else {
                        opt.initialFrameHeight = opt.minFrameHeight = holder.offsetHeight;
                    }
                    for(var i = 0 ,ci;ci=parents[i];i++){
                        ci.style.display =  displays[i]
                    }
                    //编辑器最外容器设置了高度，会导致，编辑器不占位
                    //todo 先去掉，没有找到原因
                    if(holder.style.height){
                        holder.style.height = ''
                    }
                    editor.container.style.width = opt.initialFrameWidth + (/%$/.test(opt.initialFrameWidth) ? '' : 'px');
                    editor.container.style.zIndex = opt.zIndex;
                    oldRender.call(editor, editor.ui.getDom('iframeholder'));
                    editor.fireEvent("afteruiready");
                }
            })
        };
        return editor;
    };


    /**
     * @file
     * @name UE
     * @short UE
     * @desc UEditor的顶部命名空间
     */
    /**
     * @name getEditor
     * @since 1.2.4+
     * @grammar UE.getEditor(id,[opt])  =>  Editor实例
     * @desc 提供一个全局的方法得到编辑器实例
     *
     * * ''id''  放置编辑器的容器id, 如果容器下的编辑器已经存在，就直接返回
     * * ''opt'' 编辑器的可选参数
     * @example
     *  UE.getEditor('containerId',{onready:function(){//创建一个编辑器实例
     *      this.setContent('hello')
     *  }});
     *  UE.getEditor('containerId'); //返回刚创建的实例
     *
     */
    UE.getEditor = function (id, opt) {
        var editor = instances[id];
        if (!editor) {
            editor = instances[id] = new UE.ui.Editor(opt);
            editor.render(id);
        }
        return editor;
    };


    UE.delEditor = function (id) {
        var editor;
        if (editor = instances[id]) {
            editor.key && editor.destroy();
            delete instances[id]
        }
    };

    UE.registerUI = function(uiName,fn,index,editorId){
        utils.each(uiName.split(/\s+/), function (name) {
            UE._customizeUI[name] = {
                id : editorId,
                execFn:fn,
                index:index
            };
        })

    }
    // $(document).on('mouseup',function () {
    //     alert(2)
    // })
})();