/**
 * 自动排版
 * @file
 * @since 1.2.6.1
 */

/**
 * 对当前编辑器的内容执行自动排版， 排版的行为根据config配置文件里的“autotypeset”选项进行控制。
 * @command autotypeset
 * @method execCommand
 * @param { String } cmd 命令字符串
 * @example
 * ```javascript
 * editor.execCommand( 'autotypeset' );
 * ```
 */

UE.plugins['autotypeset'] = function(){
   	    
    this.setOpt({'autotypeset': { 
      //  keepBlankBetweenP:true,         //保持段落间距离
    	mergeEmptyline: true,           //合并空行
        removeClass: true,              //去掉冗余的class
        removeEmptyline: false,         //去掉空行
        textAlign:"left",               //段落的排版方式，可以是 left,right,center,justify 去掉这个属性表示不执行排版
        imageBlockLine: 'center',       //图片的浮动方式，独占一行剧中,左右浮动，默认: center,left,right,none 去掉这个属性表示不执行排版
        imageTitle:true,
        pasteFilter: false,             //根据规则过滤没事粘贴进来的内容
        clearFontSize: false,           //去掉所有的内嵌字号，使用编辑器默认的字号
        clearFontFamily: false,         //去掉所有的内嵌字体，使用编辑器默认的字体
        removeEmptyNode: false,         // 去掉空节点
        keepSpaceBetweenPra:false,                  //保持段落间距
        //可以去掉的标签
        removeTagNames: utils.extend({div:1},dtd.$removeEmpty),
        indent: false,                  // 行首缩进
        indentValue : '2em',            //行首缩进的大小
        bdc2sb: false,                  //符号  全转半
        tobdc: false,                   //符号 半转全
        bdc2sb_num: false,              //数字 全转半
        tobdc_num: false,                //数字 半转全
        word_toall: false,               //字母全转半
        word_tohalf:false                //字母半转全
    }});

    var me = this,
        opt = me.options.autotypeset,
        remainClass = {
            'selectTdClass':1,
            'pagebreak':1,
            'anchorclass':1
        },
        remainTag = {
            'li':1
        },
        tags = {
            div:1,
            p:1,
            //trace:2183 这些也认为是行
            blockquote:1,center:1,h1:1,h2:1,h3:1,h4:1,h5:1,h6:1,
            span:1
        },
        highlightCont;
    //升级了版本，但配置项目里没有autotypeset
    if(!opt){
        return;
    }

    readLocalOpts();
    function isLine(node,notEmpty){
        if(!node || node.nodeType == 3)
            return 0;
        if(domUtils.isBr(node))
            return 1;
        if(node && node.parentNode && tags[node.tagName.toLowerCase()]){
            if(highlightCont && highlightCont.contains(node)
                ||
                node.getAttribute('pagebreak')
            ){
                return 0;
            }

            return notEmpty ? !domUtils.isEmptyBlock(node) : domUtils.isEmptyBlock(node,new RegExp('[\\s'+domUtils.fillChar
                +']','g'));
        }
    }

    function removeNotAttributeSpan(node){
        if(!node.style.cssText){
            domUtils.removeAttributes(node,['style']);
            if(node.tagName.toLowerCase() == 'span' && domUtils.hasNoAttributes(node)){
                domUtils.remove(node,true);
            }
        }
    }
    /*
     * 点击自动排版的小按钮或者点击进去以后点击“执行”按钮此处代码被调用----------
     */
    function autotype(type,html){
        var me = this,cont;   //this是指UE.Editor,
        if(html){
            if(!opt.pasteFilter){
                return;
            }
            cont = me.document.createElement('div');
            cont.innerHTML = html.html;
        }else{
            cont = me.document.body;//me是指UE.Editor
        }
        //获取文章内容标签节点
        var nodes = domUtils.getElementsByTagName(cont,'*');

        // 行首缩进，段落方向，段间距，段内间距
        for(var i=0,ci;ci=nodes[i++];){
             //font-size
            if(opt.clearFontSize && ci.style.fontSize){
                domUtils.removeStyle(ci,'font-size');
                removeNotAttributeSpan(ci);
            }
            //font-family
            if(opt.clearFontFamily && ci.style.fontFamily){
                domUtils.removeStyle(ci,'font-family');
                removeNotAttributeSpan(ci);
            }
            
            if(isLine(ci)){
                //合并空行
                if(opt.mergeEmptyline ){
                    var next = ci.nextSibling,tmpNode,isBr = domUtils.isBr(ci);
                    while(isLine(next)){
                        tmpNode = next;
                        next = tmpNode.nextSibling;
                        if(isBr && (!next || next && !domUtils.isBr(next))){
                            break;
                        }
                        domUtils.remove(tmpNode);
                    }

                }
                 //去掉空行，保留占位的空行
                if(opt.removeEmptyline && domUtils.inDoc(ci,cont) && !remainTag[ci.parentNode.tagName.toLowerCase()] ){
                    if(domUtils.isBr(ci)){
                        next = ci.nextSibling;
                        if(next && !domUtils.isBr(next)){
                            continue;
                        }
                    }
                    domUtils.remove(ci);
                    continue;

                }

            }
            if(isLine(ci,true) && ci.tagName != 'SPAN'){

            	var re = new RegExp("^((\&nbsp\;)+)*","g");
            	ci.innerHTML=ci.innerHTML.replace(re, "");
            	ci.innerHTML=ci.innerHTML.replace(/(^\s*)/g, "");
            	
            	if(opt.indent){
                    ci.style.textIndent = opt.indentValue;
                }
                if(opt.textAlign){
                    ci.style.textAlign = opt.textAlign;
                }
                if(opt.imageBlockLine){
            		if(ci.firstChild && ci.firstChild.localName=='img'){
            			ci.style.textAlign = opt.imageBlockLine;
            		}
                }
                // if(opt.lineHeight)
                //     ci.style.lineHeight = opt.lineHeight + 'cm';
            }
            
            if(opt.keepLine&&ci.tagName.toLowerCase() === 'p'){
            	ci.style["marginBottom"] = "15px";
            }
            /*
             * 清除样式,清除每个标签的样式-------------
             */
            if(opt.removeClass){
                //处理blockquote
                if(ci.tagName.toLowerCase() === 'blockquote'){
                     $(ci).find("p").each(function(){
                        var _this=$(this);
                         _this.attr("style","");
                     });
                     $(ci).find("span").each(function(){
                        var _this=$(this);
                        _this.replaceWith($.trim(_this.html()));
                     });
                     $(ci).replaceWith($.trim($(ci).html()));
                }
                //h2
                if(ci.tagName.toLowerCase() === 'h2'){
                    $(ci).attr("style","");
                }
                
            	 if(ci.tagName.toLowerCase() === 'p'){
                     //清除样式
                     domUtils.removeAttributes(ci,['style']);

                     //处理空格 删除中文间空格 保留英文间空格 @"\p{P}  --> 现在不删除数字间的空格
                     var strHtml=$(ci).html();
                     strHtml=strHtml.replace(/(&nbsp;)+(?=[\u4e00-\u9fa5]|[0-9]|[，,。.?？:：;；''‘’!！""“”—……、 ])/g,"");
                     // $(ci).html(strHtml.replace(/[ ]+(?=[\u4e00-\u9fa5]|[0-9]|[，,。.?？:：;；''‘’!！""“”—……、 ])/g,""));
                     // $(ci).html(strHtml.replace(/[ ]+(?=[\u4e00-\u9fa5]|[，,。.?？:：;；''‘’!！""“”—……、 ])/g,""));
                     $(ci).html(strHtml.replace(/[ ]+(?=[\u4e00-\u9fa5]|[，,。.?？:：;；''‘’!！“”—……、 ])/g,""));
                     
                     $(ci).html(strHtml.replace(/\"(\s|(&nbsp;))+/g, "\" "));

                     //对p内br的处理
                    $(ci).find("br").each(function(){
                         var _this=$(this);
                        _this.prev().wrap("<p></p>");
                        _this.next().wrap("<p></p>");
                        _this.remove();
                     });

                     //对p内strong的处理
                     $(ci).find("strong").each(function(){
                         var _this=$(this);
                         _this.replaceWith($.trim(_this.html()));

                     });

                     //对p内span的处理
                     $(ci).find("span").each(function(){
                         var _this=$(this);
                             _this.replaceWith($.trim(_this.html()));
                     });

                     //对p内img的处理
                     $(ci).find("img").each(function(){
                         var _this=$(this);
                         _this.attr("style","");
                     });

                     //对生成新p标签的处理
                     $(ci).find("p").each(function(){
                         var _this=$(this);
                         _this.attr("style","");
                         if(opt.indent){
                             _this.css("text-indent",opt.indentValue);
                         }
                         if(opt.keepSpaceBetweenPra){
                             _this.css("margin-bottom","15px");
                         }
                         if(opt.imageBlockLine){
                             if(_this.children() && _this.children().eq(0).prop("tagName")=='IMG'){
                                 _this.css("text-align",opt.imageBlockLine);
                             }
                         }
                     });


                 	//如果首行缩进同时也被选中，则让段落首行缩进2字符
                    if(opt.indent){
                  	    ci.style["text-indent"]=opt.indentValue;
                    }
                     //段落行间距
                    if(opt.keepSpaceBetweenPra){
                          ci.style["marginBottom"] = "15px";
                    }
                     //图片居中
                     if(opt.imageBlockLine){
                         if($(ci).children() && $(ci).children().eq(0).prop("tagName")=='IMG'){
                             $(ci).css("text-align",opt.imageBlockLine);
                         }
                     }

            	 }
               /* if(ci.tagName.toLowerCase() === 'span'){
                   $(ci).replaceWith($.trim($(ci).html()));
            	}*/
                //处理p中嵌套两层span
                 if(ci.tagName.toLowerCase() === 'p'){
                   $(ci).find("span").each(function(){
                         var _this=$(this);
                             _this.replaceWith($.trim(_this.html()));
                     });
                }
                if(ci.tagName.toLowerCase() === 'strong'){
                	$(ci).replaceWith($(ci).html());
                }
                if(ci.tagName.toLowerCase() === 'a'){
                	$(ci).replaceWith($(ci).html());
            	}
                 if(ci.tagName.toLowerCase()==='iframe'){
                	 domUtils.removeAttributes(ci,['src']);
                	 domUtils.remove(ci);
                }
            }
          //去除图片标题(image标签title属性)
            if(opt.imageTitle){
           	 $(ci).find("img").each(function(){
                    var _this=$(this);
                    _this.attr("title","");
                });
           }
           //首行缩进
          if(opt.indent){
        	  if(ci.tagName.toLowerCase() === 'p'){
        		  ci.style["text-indent"]="2em";
        	  }
            }
          //保留行间距
          if(opt.keepSpaceBetweenPra){
        	  if(ci.tagName.toLowerCase()==='p'){
          		ci.style["marginBottom"] = "15px";
          	}
          } 
            
            
          if(opt.removeClass && ci.className && !remainClass[ci.className.toLowerCase()]){

                if(highlightCont && highlightCont.contains(ci)){
                     continue;
                }
                if(ci.className.indexOf("edui-faked-") == -1){
                	domUtils.removeAttributes(ci,['class']);
                }
            }

            //表情不处理
            if(opt.imageBlockLine && ci.tagName.toLowerCase() == 'img' && !ci.getAttribute('emotion')){
                if(html){
                    var img = ci;
                    switch (opt.imageBlockLine){
                        case 'left':
                        case 'right':
                        case 'none':
                            var pN = img.parentNode,tmpNode,pre,next;
                            while(dtd.$inline[pN.tagName] || pN.tagName == 'A'){
                                pN = pN.parentNode;
                            }
                            tmpNode = pN;
                            if(tmpNode.tagName == 'P' && domUtils.getStyle(tmpNode,'text-align') == 'center'){
                                if(!domUtils.isBody(tmpNode) && domUtils.getChildCount(tmpNode,function(node){return !domUtils.isBr(node) && !domUtils.isWhitespace(node)}) == 1){
                                    pre = tmpNode.previousSibling;
                                    next = tmpNode.nextSibling;
                                    if(pre && next && pre.nodeType == 1 &&  next.nodeType == 1 && pre.tagName == next.tagName && domUtils.isBlockElm(pre)){
                                        pre.appendChild(tmpNode.firstChild);
                                        while(next.firstChild){
                                            pre.appendChild(next.firstChild);
                                        }
                                        domUtils.remove(tmpNode);
                                        domUtils.remove(next);
                                    }else{
                                        domUtils.setStyle(tmpNode,'text-align','');
                                    }


                                }


                            }
                            domUtils.setStyle(img,'float', opt.imageBlockLine);
                            break;
                        case 'center':
                            if(me.queryCommandValue('imagefloat') != 'center'){
                                pN = img.parentNode;
                                domUtils.setStyle(img,'float','none');
                                tmpNode = img;
                                while(pN && domUtils.getChildCount(pN,function(node){return !domUtils.isBr(node) && !domUtils.isWhitespace(node)}) == 1
                                    && (dtd.$inline[pN.tagName] || pN.tagName == 'A')){
                                    tmpNode = pN;
                                    pN = pN.parentNode;
                                }
                                var pNode = me.document.createElement('p');
                                domUtils.setAttributes(pNode,{

                                    style:'text-align:center'
                                });
                                if(tmpNode.parentNode){
                                    tmpNode.parentNode.insertBefore(pNode,tmpNode);
                                }

                                pNode.appendChild(tmpNode);
                                domUtils.setStyle(tmpNode,'float','');

                            }


                    }
                } else {
                   /* var range = me.selection.getRange();
                    range.selectNode(ci).select();
                    me.execCommand('imagefloat', opt.imageBlockLine);*/
                }
            }

            //去掉冗余的标签-------
            if(opt.removeEmptyNode){
                if(opt.removeTagNames[ci.tagName.toLowerCase()] && domUtils.hasNoAttributes(ci) && domUtils.isEmptyBlock(ci)){
                    domUtils.remove(ci);
                }
            }
        }


        if(opt.tobdc){
            var root = UE.htmlparser(cont.innerHTML);
            root.traversal(function(node){
                if(node.type == 'text'){
                    node.data = ToDBC(node.data)
                }
            });
            cont.innerHTML = root.toHtml()
        }
        if(opt.bdc2sb){
            var root = UE.htmlparser(cont.innerHTML);
            root.traversal(function(node){
                if(node.type == 'text'){
                    node.data = DBC2SB(node.data)
                }
            });
            cont.innerHTML = root.toHtml()
        }
        
        //20160714---------------
        if(opt.word_tohalf){
        	var root = UE.htmlparser(cont.innerHTML);
            root.traversal(function(node){
                if(node.type == 'text'){
                    node.data = WORD_TOHALF(node.data)
                }
            });
            cont.innerHTML = root.toHtml()
        }
        
        if(opt.word_toall){
        	var root = UE.htmlparser(cont.innerHTML);
            root.traversal(function(node){
                if(node.type == 'text'){
                    node.data = WORD_TOALL(node.data)
                }
            });
            cont.innerHTML = root.toHtml()
        }

        if(opt.tobdc_num){
            var root = UE.htmlparser(cont.innerHTML);
            root.traversal(function(node){
                if(node.type == 'text'){
                    node.data = ToDBC_num(node.data)
                }
            });
            cont.innerHTML = root.toHtml()
        }
        if(opt.bdc2sb_num){
            var root = UE.htmlparser(cont.innerHTML);
            root.traversal(function(node){
                if(node.type == 'text'){
                    node.data = DBC2SB_num(node.data)
                }
            });
            cont.innerHTML = root.toHtml()
        }
      //去除超链接 和 strong字体加粗 去掉图片外层p的缩进----------------
        nodes = domUtils.getElementsByTagName(cont,'*');
        for(var i=0,ci;ci=nodes[i++];){
            if(ci.tagName.toLowerCase() == "a" && opt.removeClass){
            	$(ci).replaceWith($(ci).html());
            }
            if(ci.tagName.toLowerCase() == "strong" && opt.removeClass){
            	$(ci).replaceWith($(ci).html());
            }
            if(ci.tagName.toLowerCase() == "em" && opt.removeClass){
                $(ci).replaceWith($(ci).html());
            }
            if(ci.tagName.toLowerCase() == "img" && ci.parentNode.tagName.toLowerCase() == "p" && ci.parentNode.style.textIndent){
                ci.parentNode.style.textIndent="";
            }
        }
        if(html){
            html.html = cont.innerHTML;
        }

        // 清除所有空的p标签
         if (opt.removeEmptyNode) {
            var allPs = domUtils.getElementsByTagName(cont,'*');
                for (var i = 0, p; p = allPs[i++];) {
                    if(p.tagName.toLowerCase() === 'p') {
                        if (!$(p).html()) {
                            $(p).remove();
                        }
                    }
                }
        }
    }
    if(opt.pasteFilter){
        me.addListener('beforepaste',autotype);
    }

    /*function DBC2SB(str) {
        var result = '';
        for (var i = 0; i < str.length; i++) {
            var code = str.charCodeAt(i); //获取当前字符的unicode编码
            if (code >= 65281 && code <= 65373)//在这个unicode编码范围中的是所有的英文字母已经各种字符
            {
                result += String.fromCharCode(str.charCodeAt(i) - 65248); //把全角字符的unicode编码转换为对应半角字符的unicode码
            } else if (code == 12288)//空格
            {
                result += String.fromCharCode(str.charCodeAt(i) - 12288 + 32);
            } else {
                result += str.charAt(i);
            }
        }
        return result;
    }
    function ToDBC(txtstring) {
        txtstring = utils.html(txtstring);
        var tmp = "";
        var mark = "";/!*用于判断,如果是html尖括里的标记,则不进行全角的转换*!/
        for (var i = 0; i < txtstring.length; i++) {
            if (txtstring.charCodeAt(i) == 32) {
                tmp = tmp + String.fromCharCode(12288);
            }
            else if (txtstring.charCodeAt(i) < 127) {
                tmp = tmp + String.fromCharCode(txtstring.charCodeAt(i) + 65248);
            }
            else {
                tmp += txtstring.charAt(i);
            }
        }
        return tmp;
    }*/
    //符号全转半
    function DBC2SB(str) {
        var result = '';
        for (var i = 0; i < str.length; i++) {
            var code = str.charCodeAt(i); //获取当前字符的unicode编码
            if ((str.charCodeAt(i)>=65281 && str.charCodeAt(i)<=65312) || (str.charCodeAt(i) <= 65344 && str.charCodeAt(i) >= 65339) || (str.charCodeAt(i) >= 65371 && str.charCodeAt(i) < 65375))//在这个unicode编码范围中的是所有的英文字母已经各种字符
            {
                result += String.fromCharCode(str.charCodeAt(i) - 65248); //把全角字符的unicode编码转换为对应半角字符的unicode码
            } else if (code == 12288)//空格
            {
                result += String.fromCharCode(str.charCodeAt(i) - 12288 + 32);
            } else {
                result += str.charAt(i);
            }
        }
        return result;
    }
    //符号 半转全
    function ToDBC(txtstring) {
        txtstring = utils.html(txtstring);
        var tmp = "";
        var mark = "";
        /*用于判断,如果是html尖括里的标记,则不进行全角的转换*/
        for (var i = 0; i < txtstring.length; i++) {
            if (txtstring.charCodeAt(i) == 32) {
                tmp = tmp + String.fromCharCode(12288);
            }
            else if ((txtstring.charCodeAt(i)>=33 && txtstring.charCodeAt(i)<=64) || (txtstring.charCodeAt(i) <= 96 && txtstring.charCodeAt(i) >= 91) || (txtstring.charCodeAt(i) >= 123 && txtstring.charCodeAt(i) < 127)) {
                tmp = tmp + String.fromCharCode(txtstring.charCodeAt(i) + 65248);
            }
            else {
                tmp += txtstring.charAt(i);
            }
        }
        return tmp;
    }
    //20160714----------------
   
    //字母 全转半  
    function WORD_TOHALF(txtContent){
    	var result = '';
        for (var i = 0; i < txtContent.length; i++) {
            var code = txtContent.charCodeAt(i); //获取当前字符的unicode编码
            if ((code >= 65313 && code <= 65338) || (code >= 65345 && code <= 65370))//在这个unicode编码范围中的是所有的英文字母
            {
                result += String.fromCharCode(txtContent.charCodeAt(i)-65248); //把全角字符的unicode编码转换为对应半角字符的unicode码
            } else if (code == 12288)//空格
            {
                result += String.fromCharCode(txtContent.charCodeAt(i) - 12288 + 32);
            } else {
                result += txtContent.charAt(i);
            }
        }
        return result;
    }
    
    //字母 半转全
    function WORD_TOALL(txtContent){
    	txtContent = utils.html(txtContent);
    	var tmp = "";
        var mark = "";
        for(i = 0; i < txtContent.length; i++) {
        	 /*用于判断,如果是html尖括里的标记,则不进行全角的转换*/
        	if (txtContent.charCodeAt(i) == 32) {
                tmp = tmp + String.fromCharCode(12288);
            }
        	 else if ((txtContent.charCodeAt(i)<=122 && txtContent.charCodeAt(i)>=97) || (txtContent.charCodeAt(i)<=90&&txtContent.charCodeAt(i)>=65)) {
                 tmp = tmp + String.fromCharCode(txtContent.charCodeAt(i)+65248);
             }
             else {
                 tmp += txtContent.charAt(i);
             }
         }
         return tmp;
        }
    

    function ToDBC_num(txtstring) {
        txtstring = utils.html(txtstring);
        var tmp = "";
        var mark = "";
        /*用于判断,如果是html尖括里的标记,则不进行全角的转换*/
        for (var i = 0; i < txtstring.length; i++) {
            if (txtstring.charCodeAt(i) == 32) {
                tmp = tmp + String.fromCharCode(12288);
            }
            else if (txtstring.charCodeAt(i) >= 48 && txtstring.charCodeAt(i) <= 57 ) {
                tmp = tmp + String.fromCharCode(txtstring.charCodeAt(i) + 65248);
            }
            else {
                tmp += txtstring.charAt(i);
            }
        }
        return tmp;
    }

    function DBC2SB_num(str) {
        var result = '';
        for (var i = 0; i < str.length; i++) {
            var code = str.charCodeAt(i); //获取当前字符的unicode编码
            if (code >= 65296 && code <= 65305)//在这个unicode编码范围中的是所有的英文字母已经各种字符
            {
                result += String.fromCharCode(str.charCodeAt(i) - 65248); //把全角字符的unicode编码转换为对应半角字符的unicode码
            } else if (code == 12288)//空格
            {
                result += String.fromCharCode(str.charCodeAt(i) - 12288 + 32);
            } else {
                result += str.charAt(i);
            }
        }
        return result;
    }

    function readLocalOpts() {
        var cookieOpt = me.getPreferences('autotypeset');
        utils.extend(me.options.autotypeset, cookieOpt);
    }

    me.commands['autotypeset'] = {
        execCommand:function () {
            me.removeListener('beforepaste',autotype);
            if(opt.pasteFilter){
                me.addListener('beforepaste',autotype);
            }
            autotype.call(me)
        },
        queryCommandState : function() {
            return 0;
        }
    };

    // f9一键排版快捷键
    me.addshortcutkey("autotypeset", "120");

    //解决文字换段又backspace产生span line-height=1.5的bug
    me.on("keydown",function(){
        if(this.window.event.keyCode==8 || this.window.event.keyCode==46){
            setTimeout(function(){
                var cont = me.document.body;
                var nodes = domUtils.getElementsByTagName(cont,'*');
                for(var i=0,ci;ci=nodes[i++];){
                    if(ci.tagName.toLowerCase() === 'p'){
                        $(ci).find("span").each(function(){
                            $(this).css({"line-height":"","text-indent":""});
                        })
                    }
                }
            },100)
        }
    })


};

