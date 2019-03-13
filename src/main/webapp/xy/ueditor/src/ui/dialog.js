///import core
///import uicore
///import ui/mask.js
///import ui/button.js
(function (){
    var utils = baidu.editor.utils,
        domUtils = baidu.editor.dom.domUtils,
        uiUtils = baidu.editor.ui.uiUtils,
        Mask = baidu.editor.ui.Mask,
        UIBase = baidu.editor.ui.UIBase,
        Button = baidu.editor.ui.Button,
        Dialog = baidu.editor.ui.Dialog = function (options){
            if(options.name){
                var name = options.name;
                var cssRules = options.cssRules;
                if(!options.className){
                    options.className =  'edui-for-' + name;
                }
                if(cssRules){
                    options.cssRules = '.edui-default .edui-for-'+ name +' .edui-dialog-content  {'+ cssRules +'}'
                }
            }
            this.initOptions(utils.extend({
                autoReset: true,
                draggable: true,
                onok: function (){
                    var wrapNode = this.getDom();
                    if (wrapNode.classList[1] == 'edui-for-insertimage') {
                    	
                    	var imgPathhtml='';
                    	//var picUploadDiv = document.getElementById("edui85_iframe").contentWindow.document.getElementById("picUploadDiv"); 
                    	var read = JSON.parse(localStorage.getItem('uploadPicAll'));
                    	for(var index=0;index<read.length;index++){
                    		if(localStorage.getItem('page_img') == 'true' && index!=0)
                				imgPathhtml+='_ueditor_page_break_tag_';
                    		var path = "../../xy/image.do?path=" + read[index]['imagePath'];
                    		// imgPathhtml+='<p><img src="'+path+'" style="width；100px;height；100px"></p>';
                            // var disc = read[index]['caption'];
                            // if(disc != '') imgPathhtml+='<p>'+disc+'</p>';  
                            var disc = read[index]['caption'];
                            //描述和图说关联到一起
                            if (disc != '') {
                                var timeTab = new Date().getTime();
                                var myImage = '<img src="'+path+'" style="width；100px;height；100px" imgtime="' + timeTab + '">';
                                myImage += "<figcaption id='figures_" + timeTab + "' class='imgComment_content' style='line-height:20px;padding: 0px 0px 0px;font-size: 0.9em;margin: 5px auto 0px;color: gray;text-align: center;word-wrap: break-word;width:100%;'>" + disc + "</figcaption>";
                                // $(myImage).wrap("<figure class='imgComment_figure' id='" + timeTab + "' style='text-align: center;position:relative;margin: 0px'></figure>");
                                var figure = $("<figure class='imgComment_figure' id='" + timeTab + "' style='text-align: center;position:relative;margin: 0px'></figure>").append(myImage);
                                var pfigure =  $("<p></p>").append(figure);
                            }else{
                                var pfigure = $('<p><img src="'+path+'" style="width；100px;height；100px"></p>');
                            }
                            imgPathhtml += pfigure.prop("outerHTML");
                    	}
//                      this.editor.setContent(imgPathhtml, true);
//                    	this.editor.focus(true);
                    	this.editor.execCommand('inserthtml',imgPathhtml);
                    }
                },
                oncancel: function (){},
                onclose: function (t, ok){
                    return ok ? this.onok() : this.oncancel();
                },
                //是否控制dialog中的scroll事件， 默认为不阻止
                holdScroll: false
            },options));
            this.initDialog();
        };
    var modalMask;
    var dragMask;
    var activeDialog;
    Dialog.prototype = {
        draggable: false,
        uiName: 'dialog',
        initDialog: function (){
            localStorage.setItem('insert_img', false)
            var me = this,
                theme=this.editor.options.theme;
            if(this.cssRules){
                utils.cssRule('edui-customize-'+this.name+'-style',this.cssRules);
            }
            this.initUIBase();
            this.modalMask = (modalMask || (modalMask = new Mask({
                className: 'edui-dialog-modalmask',
                theme:theme,
                onclick: function (){
                    activeDialog && activeDialog.close(false);
                }
            })));
            this.dragMask = (dragMask || (dragMask = new Mask({
                className: 'edui-dialog-dragmask',
                theme:theme
            })));
            this.closeButton = new Button({
                className: 'edui-dialog-closebutton',
                title: me.closeDialog,
                theme:theme,
                onclick: function (){
                    me.close(false);
                }
            });

            this.fullscreen && this.initResizeEvent();

            if (this.buttons) {
                for (var i=0; i<this.buttons.length; i++) {
                    if (!(this.buttons[i] instanceof Button)) {
                        this.buttons[i] = new Button(utils.extend(this.buttons[i],{
                            editor : this.editor
                        },true));
                    }
                }
            }
        },
        initResizeEvent: function () {

            var me = this;

            domUtils.on( window, "resize", function () {

                if ( me._hidden || me._hidden === undefined ) {
                    return;
                }

                if ( me.__resizeTimer ) {
                    window.clearTimeout( me.__resizeTimer );
                }

                me.__resizeTimer = window.setTimeout( function () {

                    me.__resizeTimer = null;

                    var dialogWrapNode = me.getDom(),
                        contentNode = me.getDom('content'),
                        wrapRect = UE.ui.uiUtils.getClientRect( dialogWrapNode ),
                        contentRect = UE.ui.uiUtils.getClientRect( contentNode ),
                        vpRect = uiUtils.getViewportRect();

                    contentNode.style.width = ( vpRect.width - wrapRect.width + contentRect.width ) + "px";
                    contentNode.style.height = ( vpRect.height - wrapRect.height + contentRect.height ) + "px";
//alert( contentNode.style.width);alert( contentNode.style.height);
                    dialogWrapNode.style.width = vpRect.width + "px";
                    dialogWrapNode.style.height = vpRect.height + "px";
//alert( dialogWrapNode.style.width);alert( dialogWrapNode.style.height);
                    me.fireEvent( "resize" );

                }, 100 );

            } );

        },
        fitSize: function (){
            var popBodyEl = this.getDom('body');
//            if (!(baidu.editor.browser.ie && baidu.editor.browser.version == 7)) {
//                uiUtils.removeStyle(popBodyEl, 'width');
//                uiUtils.removeStyle(popBodyEl, 'height');
//            }
            var size = this.mesureSize();
            popBodyEl.style.width = size.width + 'px';
            popBodyEl.style.height = size.height + 'px';
            return size;
        },
        safeSetOffset: function (offset){
            var me = this;
            var el = me.getDom();
            var vpRect = uiUtils.getViewportRect();
            var rect = uiUtils.getClientRect(el);
            var left = offset.left;
            if (left + rect.width > vpRect.right) {
                left = vpRect.right - rect.width;
            }
            var top = offset.top;
            if (top + rect.height > vpRect.bottom) {
                top = vpRect.bottom - rect.height;
            }
            el.style.left = Math.max(left, 0) + 'px';
            el.style.top = Math.max(top, 0) + 'px';
        },
        showAtCenter: function (){

            var vpRect = uiUtils.getViewportRect();

            if ( !this.fullscreen ) {
                this.getDom().style.display = '';
                var popSize = this.fitSize();
                var titleHeight = this.getDom('titlebar').offsetHeight | 0;
                var left = vpRect.width / 2 - popSize.width / 2;
                var top = vpRect.height / 2 - (popSize.height - titleHeight) / 2 - titleHeight;
                var popEl = this.getDom();
                this.safeSetOffset({
                    left: Math.max(left | 0, 0),
                    top: Math.max(top | 0, 0)
                });
                if (!domUtils.hasClass(popEl, 'edui-state-centered')) {
                    popEl.className += ' edui-state-centered';
                }
            } else {
                var dialogWrapNode = this.getDom(),
                    contentNode = this.getDom('content');

                dialogWrapNode.style.display = "block";

                var wrapRect = UE.ui.uiUtils.getClientRect( dialogWrapNode ),
                    contentRect = UE.ui.uiUtils.getClientRect( contentNode );
                dialogWrapNode.style.left = "-100000px";

                contentNode.style.width = ( vpRect.width - wrapRect.width + contentRect.width ) + "px";
                contentNode.style.height = ( vpRect.height - wrapRect.height + contentRect.height ) + "px";

                dialogWrapNode.style.width = vpRect.width + "px";
                dialogWrapNode.style.height = vpRect.height + "px";
                dialogWrapNode.style.left = 0;

                //保存环境的overflow值
                this._originalContext = {
                    html: {
                        overflowX: document.documentElement.style.overflowX,
                        overflowY: document.documentElement.style.overflowY
                    },
                    body: {
                        overflowX: document.body.style.overflowX,
                        overflowY: document.body.style.overflowY
                    }
                };

                document.documentElement.style.overflowX = 'hidden';
                document.documentElement.style.overflowY = 'hidden';
                document.body.style.overflowX = 'hidden';
                document.body.style.overflowY = 'hidden';

            }

            this._show();
        },
        getContentHtml: function (){
            var contentHtml = '';
            if (typeof this.content == 'string') {
                contentHtml = this.content;
            } else if (this.iframeUrl) {
                contentHtml = '<span id="'+ this.id +'_contmask" class="dialogcontmask"></span><iframe id="'+ this.id +
                '_iframe" class="%%-iframe" height="100%" width="100%" frameborder="0" src="'+ this.iframeUrl +'"></iframe>';
            }
            return contentHtml;
        },
        getHtmlTpl: function (){
            var footHtml = '';

            if (this.buttons) {
                var buff = [];
                for (var i=0; i<this.buttons.length; i++) {
                    buff[i] = this.buttons[i].renderHtml();
                }
                footHtml = '<div class="%%-foot">' +
                '<div id="##_buttons" class="%%-buttons">' + buff.join('') + '</div>' +
                '</div>';
            }

            return '<div id="##" class="%%"><div '+ ( !this.fullscreen ? 'class="%%"' : 'class="%%-wrap edui-dialog-fullscreen-flag"' ) +'><div id="##_body" class="%%-body">' +
                '<div class="%%-shadow"></div>' +
                '<div id="##_titlebar" class="%%-titlebar">' +
                '<div class="%%-draghandle" onmousedown="$$._onTitlebarMouseDown(event, this);">' +
                '<span class="%%-caption">' + (this.title || '') + '</span>' +
                '</div>' +
                this.closeButton.renderHtml() +
                '</div>' +
                '<div id="##_content" class="%%-content">'+ ( this.autoReset ? '' : this.getContentHtml()) +'</div>' +
                footHtml +
                '</div></div></div>';
        },
        postRender: function (){
            // todo: 保持居中/记住上次关闭位置选项
            if (!this.modalMask.getDom()) {
                this.modalMask.render();
                this.modalMask.hide();
            }
            if (!this.dragMask.getDom()) {
                this.dragMask.render();
                this.dragMask.hide();
            }
            var me = this;
            this.addListener('show', function (){
                me.modalMask.show(this.getDom().style.zIndex - 2);
            });
            this.addListener('hide', function (){
                me.modalMask.hide();
            });
            if (this.buttons) {
                for (var i=0; i<this.buttons.length; i++) {
                    this.buttons[i].postRender();
                }
            }
            domUtils.on(window, 'resize', function (){
                setTimeout(function (){
                    if (!me.isHidden()) {
                        me.safeSetOffset(uiUtils.getClientRect(me.getDom()));
                    }
                });
            });

            //hold住scroll事件，防止dialog的滚动影响页面
//            if( this.holdScroll ) {
//
//                if( !me.iframeUrl ) {
//                    domUtils.on( document.getElementById( me.id + "_iframe"), !browser.gecko ? "mousewheel" : "DOMMouseScroll", function(e){
//                        domUtils.preventDefault(e);
//                    } );
//                } else {
//                    me.addListener('dialogafterreset', function(){
//                        window.setTimeout(function(){
//                            var iframeWindow = document.getElementById( me.id + "_iframe").contentWindow;
//
//                            if( browser.ie ) {
//
//                                var timer = window.setInterval(function(){
//
//                                    if( iframeWindow.document && iframeWindow.document.body ) {
//                                        window.clearInterval( timer );
//                                        timer = null;
//                                        domUtils.on( iframeWindow.document.body, !browser.gecko ? "mousewheel" : "DOMMouseScroll", function(e){
//                                            domUtils.preventDefault(e);
//                                        } );
//                                    }
//
//                                }, 100);
//
//                            } else {
//                                domUtils.on( iframeWindow, !browser.gecko ? "mousewheel" : "DOMMouseScroll", function(e){
//                                    domUtils.preventDefault(e);
//                                } );
//                            }
//
//                        }, 1);
//                    });
//                }
//
//            }
            this._hide();
        },
        mesureSize: function (){
            var body = this.getDom('body');
            var width = uiUtils.getClientRect(this.getDom('content')).width;
            var dialogBodyStyle = body.style;
            dialogBodyStyle.width = width;
            return uiUtils.getClientRect(body);
        },
        _onTitlebarMouseDown: function (evt, el){
            if (this.draggable) {
                var rect;
                var vpRect = uiUtils.getViewportRect();
                var me = this;
                uiUtils.startDrag(evt, {
                    ondragstart: function (){
                        rect = uiUtils.getClientRect(me.getDom());
                        me.getDom('contmask').style.visibility = 'visible';
                        me.dragMask.show(me.getDom().style.zIndex - 1);
                    },
                    ondragmove: function (x, y){
                        var left = rect.left + x;
                        var top = rect.top + y;
                        me.safeSetOffset({
                            left: left,
                            top: top
                        });
                    },
                    ondragstop: function (){
                        me.getDom('contmask').style.visibility = 'hidden';
                        domUtils.removeClasses(me.getDom(), ['edui-state-centered']);
                        me.dragMask.hide();
                    }
                });
            }
        },
        reset: function (){
            this.getDom('content').innerHTML = this.getContentHtml();
            this.fireEvent('dialogafterreset');
        },
        _show: function (){
            if (this._hidden) {
                this.getDom().style.display = '';

                //要高过编辑器的zindxe
                this.editor.container.style.zIndex && (this.getDom().style.zIndex = this.editor.container.style.zIndex * 1 + 10);
                this._hidden = false;
                this.fireEvent('show');
                baidu.editor.ui.uiUtils.getFixedLayer().style.zIndex = this.getDom().style.zIndex - 4 + 2000;
            }
        },
        isHidden: function (){
            return this._hidden;
        },
        _hide: function (){
            if (!this._hidden) {
                var wrapNode = this.getDom();
                wrapNode.style.display = 'none';
                wrapNode.style.zIndex = '';
                wrapNode.style.width = '';
                wrapNode.style.height = '';
                this._hidden = true;
                this.fireEvent('hide');
            }
        },
        open: function (){
            if (this.autoReset) {
                //有可能还没有渲染
                try{
                    this.reset();
                }catch(e){
                    this.render();
                    this.open()
                }
            }
            this.showAtCenter();
            if (this.iframeUrl) {
                try {
                    this.getDom('iframe').focus();
                } catch(ex){}
            }
            activeDialog = this;
        },
        _onCloseButtonClick: function (evt, el){
            this.close(false);
        },
        close: function (ok){
            //debugger;


            if (this.fireEvent('close', ok) !== false) {
                if(ok == false){
                    //这是为插入分页做的判断,具体可优化
                    localStorage.setItem('page_img',false);
                    localStorage.setItem('img_insert', false);
                }
                //还原环境
                if ( this.fullscreen ) {

                    document.documentElement.style.overflowX = this._originalContext.html.overflowX;
                    document.documentElement.style.overflowY = this._originalContext.html.overflowY;
                    document.body.style.overflowX = this._originalContext.body.overflowX;
                    document.body.style.overflowY = this._originalContext.body.overflowY;
                    delete this._originalContext;

                }
                this._hide();

                //销毁content
                var content = this.getDom('content');
                var iframe = this.getDom('iframe');
                if (content && iframe) {
                    var doc = iframe.contentDocument || iframe.contentWindow.document;
                    doc && (doc.body.innerHTML = '');
                    domUtils.remove(content);
                }
            }
        }
    };
    utils.inherits(Dialog, UIBase);
})();
