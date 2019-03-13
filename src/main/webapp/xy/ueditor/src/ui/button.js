///import core
///import uicore
///import ui/stateful.js
(function (){
    var utils = baidu.editor.utils,
        UIBase = baidu.editor.ui.UIBase,
        Stateful = baidu.editor.ui.Stateful,
        Button = baidu.editor.ui.Button = function (options){
            if(options.name){
                var btnName = options.name;
                var cssRules = options.cssRules;
                if(!options.className){
                    options.className =  'edui-for-' + btnName;
                }
                options.cssRules = '.edui-default  .edui-for-'+ btnName +' .edui-icon {'+ cssRules +'}'
            }
            this.initOptions(options);
            this.initButton();
        };
    Button.prototype = {
        uiName: 'button',
        label: '',
        title: '',
        showIcon: true,
        showText: true,
        cssRules:'',
        initButton: function (){
            this.initUIBase();
            this.Stateful_init();
            if(this.cssRules){
                utils.cssRule('edui-customize-'+this.name+'-style',this.cssRules);
            }
        },
        getHtmlTpl: function (){//fzf每个按钮的html结构
            var res = [];
            res.push('<div id="##" class="edui-box %%">');
            res.push('<div id="##_state" stateful>');
            res.push('<div class="%%-wrap"><div id="##_body" unselectable="on" ' + (this.title ? 'title="' + this.title + '"' : ''));
            if( this.label == "确认" || this.label == "取消" || this.label == "确定"){
                res.push(' class="%%-body" onmousedown="return $$._onMouseDown(event, this);" onclick="return $$._onClick(event, this);">');
                res.push((this.showIcon ? '<div class="edui-box edui-icon"></div>' : ''));
                res.push((this.showText ? '<div class="edui-box edui-label">' + this.label + '</div>' : ''));
            }else{
                switch(this.showText){
                    case 1:
                        //左右结构
                        res.push(' class="%%-body" onmousedown="return $$._onMouseDown(event, this);" onclick="return $$._onClick(event, this);">');
                        res.push('<div class="edui-box edui-icon"></div>');
                        res.push('<div class="edui-box edui-label');
                        if(this.title.length > 3){
                            res.push('font-size:8px;');
                        }
                        res.push('">' + this.title + '</div>');
                        break;
                    case 2:
                        //上下结构
                        res.push(' class="%%-body" style="width:46px;height:48px;" onmousedown="return $$._onMouseDown(event, this);" onclick="return $$._onClick(event, this);">');
                        res.push('<div class="edui-box edui-icon-2" style="width:100%;text-align:center;"></div>');
                        res.push('<div class="edui-box edui-label" style="width:100%;padding-left:0px;text-align:center;');
                        if(this.title.length > 3){
                            res.push('font-size:8px;');
                        }
                        res.push('">' + this.title + '</div>');
                        break;
                    default:
                        res.push(' class="%%-body" onmousedown="return $$._onMouseDown(event, this);" onclick="return $$._onClick(event, this);">');
                        res.push('<div class="edui-box edui-icon"></div>');
                }
            }
            res.push('</div></div></div></div>');
            return res.join('');
        },
        postRender: function (){
            this.Stateful_postRender();
            this.setDisabled(this.disabled)
        },
        _onMouseDown: function (e){
            var target = e.target || e.srcElement,
                tagName = target && target.tagName && target.tagName.toLowerCase();
            if (tagName == 'input' || tagName == 'object' || tagName == 'object') {
                return false;
            }
        },
        _onClick: function (){
            if (!this.isDisabled()) {
                this.fireEvent('click');
            }
        },
        setTitle: function(text){
            var label = this.getDom('label');
            label.innerHTML = text;
        }
    };
    utils.inherits(Button, UIBase);
    utils.extend(Button.prototype, Stateful);

})();
