///import core
///import plugins/inserthtml.js
///import plugins/undo.js
///import plugins/serialize.js
///commands 粘贴
///commandsName  PastePlain
///commandsTitle  纯文本粘贴模式
/**
 * @description 粘贴
 * @author zhanyi
 */
UE.plugins['paste'] = function (e) {

    //自定义粘贴事件
    function customGetClipboardData(e){
        var clipboardData = e.clipboardData || e.originalEvent && e.originalEvent.clipboardData;
        var pasteText = void 0,
            pasteHtml = void 0;
        if (clipboardData == null) {
            pasteText = window.clipboardData && window.clipboardData.getData('text');
            console.log(pasteText);
        } else {

            pasteText = clipboardData.getData('text/plain');

	        pasteHtml = replaceHtmlMargin(clipboardData.getData('text/html'));

	        if(!pasteHtml){
				 //纯文本检测
				 if(checkText(pasteText,e)){
				 	return;
				 }
			}
        }
        //处理秀米的样式错乱问题
           pasteHtml = replaceHtmlMargin(pasteHtml);
        if (!pasteHtml && pasteText) {
            pasteHtml = '<p>' + replaceHtmlSymbol(pasteText) + '</p>';
        }
        if (!pasteHtml) {
            return;
        }
        //解决135编辑器粘贴进来的稿件样式混乱的问题，原因在于标签循环显示多次，暂时用正则表达式过滤解决
        if(pasteHtml.indexOf('Powered by 135editor.com')!=-1){
            console.log(pasteHtml);
            pasteHtml = pasteHtml.replace(/<section([^>]*(background-image: url)+[^>]*)background-size: 100% 100%;/gi, '$1')
            console.log(pasteHtml);
        }

        // 过滤word中状态过来的无用字符
        var docSplitHtml = pasteHtml.split('</html>');
        if (docSplitHtml.length === 2) {
            pasteHtml = docSplitHtml[0];
        }

        // 过滤无用标签
        pasteHtml = pasteHtml.replace(/<(meta|script|link).+?>/igm, '');
        // 去掉注释
        pasteHtml = pasteHtml.replace(/<!--.*?-->/mg, '');

        // 保留样式
        pasteHtml = pasteHtml.replace(/\s?class=('|").+?('|")/igm, '');

	    //me.execCommand('insertHtml', pasteHtml);
        return pasteHtml;
        //return
    }

    	//TODO 针对翔宇微信编辑器内全文复制  解决默认把纯文本粘贴
	function checkText(html,e) {
		if (html.indexOf("wxBox_title") && html.indexOf("wxBox_content")) {
			e.preventDefault();
			var editor = UE.getEditor("editor");
			var content = decode(html);
			editor.setContent(content);
			return true;
		}
	}
	function decode(content) {
	   content = content.replace(/&amp;/g, "&");
	   content = content.replace(/&lt;/g, "<");
	   content = content.replace(/&gt;/g, ">");
	   //content = content.replace(/&quot;/g, "\"");
	   content = content.replace(/&#034;/g, "\"");
	   return content;
	}


	//TODO 针对秀米网的图片问题 替换 margin-left: 15px;
	function replaceHtmlMargin(html) {
		if (html == null) {
			return '';
		}
		return html.replace('margin-left: -15px;' , '').replace('margin-right: -15px;' , '');
	}

    // 替换 html 特殊字符
    function replaceHtmlSymbol(html) {
        if (html == null) {
            return '';
        }
        return html.replace(/</gm, '&lt;').replace(/>/gm, '&gt;').replace(/"/gm, '&quot;');
    }

    function getClipboardData(callback,e) {

        var customClipboardData= customGetClipboardData(e);
        //同步上面的checkText 方法判定
        if(!customClipboardData){
    		return
    	}
        if(customClipboardData.indexOf('<embed')!=-1){
            e.preventDefault();
        }
        // else if(customClipboardData.indexOf('<section')!=-1){
        //     return;
        // }
        var doc = this.document;
        if (doc.getElementById('baidu_pastebin')) {
            return;
        }
        var range = this.selection.getRange(),
            bk = range.createBookmark(),
        //创建剪贴的容器div
            pastebin = doc.createElement('div');
        pastebin.id = 'baidu_pastebin';
        // Safari 要求div必须有内容，才能粘贴内容进来
        browser.webkit && pastebin.appendChild(doc.createTextNode(domUtils.fillChar + domUtils.fillChar));
        pastebin.innerHTML=customClipboardData;
        doc.body.appendChild(pastebin);
        //trace:717 隐藏的span不能得到top
        //bk.start.innerHTML = '&nbsp;';
        bk.start.style.display = '';
        pastebin.style.cssText = "position:absolute;width:1px;height:1px;overflow:hidden;left:-1000px;white-space:nowrap;top:" +
            //要在现在光标平行的位置加入，否则会出现跳动的问题
            domUtils.getXY(bk.start).y + 'px';

        range.selectNodeContents(pastebin).select(true);

        setTimeout(function () {
            if (browser.webkit) {
                for (var i = 0, pastebins = doc.querySelectorAll('#baidu_pastebin'), pi; pi = pastebins[i++];) {
                    if (domUtils.isEmptyNode(pi)) {
                        domUtils.remove(pi);
                    } else {
                        pastebin = pi;
                        break;
                    }
                }
            }
            try {
                pastebin.parentNode.removeChild(pastebin);
            } catch (e) {
            }
            range.moveToBookmark(bk).select(true);
            callback(pastebin);
        }, 0);
    }

    var me = this;

    me.setOpt({
        retainOnlyLabelPasted : false
    });

    var txtContent, htmlContent, address;

    function getPureHtml(html){
        return html.replace(/<(\/?)([\w\-]+)([^>]*)>/gi, function (a, b, tagName, attrs) {
            tagName = tagName.toLowerCase();
            if ({img: 1}[tagName]) {
                return a;
            }
            attrs = attrs.replace(/([\w\-]*?)\s*=\s*(("([^"]*)")|('([^']*)')|([^\s>]+))/gi, function (str, atr, val) {
                if ({
                    'src': 1,
                    'href': 1,
                    'name': 1
                }[atr.toLowerCase()]) {
                    return atr + '=' + val + ' '
                }
                return ''
            });
            if ({
                'span': 1,
                'div': 1
            }[tagName]) {
                return ''
            } else {

                return '<' + b + tagName + ' ' + utils.trim(attrs) + '>'
            }

        });
    }
    function filter(div) {
        console.log(div)
        var html;
        if (div.firstChild) {
            //去掉cut中添加的边界值

            var nodes = domUtils.getElementsByTagName(div, 'span');
            for (var i = 0, ni; ni = nodes[i++];) {
                if (ni.id == '_baidu_cut_start' || ni.id == '_baidu_cut_end') {
                    domUtils.remove(ni);
                }
            }

            if (browser.webkit) {

                var brs = div.querySelectorAll('div br');
                for (var i = 0, bi; bi = brs[i++];) {
                    var pN = bi.parentNode;
                    if (pN.tagName == 'DIV' && pN.childNodes.length == 1) {
                        pN.innerHTML = '<p><br/></p>';
                        domUtils.remove(pN);
                    }
                }
                var divs = div.querySelectorAll('#baidu_pastebin');
                for (var i = 0, di; di = divs[i++];) {
                    var tmpP = me.document.createElement('p');
                    di.parentNode.insertBefore(tmpP, di);
                    while (di.firstChild) {
                        tmpP.appendChild(di.firstChild);
                    }
                    domUtils.remove(di);
                }

                var metas = div.querySelectorAll('meta');
                for (var i = 0, ci; ci = metas[i++];) {
                    domUtils.remove(ci);
                }

                var brs = div.querySelectorAll('br');
                for (i = 0; ci = brs[i++];) {
                    if (/^apple-/i.test(ci.className)) {
                        domUtils.remove(ci);
                    }
                }
            }
            if (browser.gecko) {
                var dirtyNodes = div.querySelectorAll('[_moz_dirty]');
                for (i = 0; ci = dirtyNodes[i++];) {
                    ci.removeAttribute('_moz_dirty');
                }
            }
            if (!browser.ie) {
                var spans = div.querySelectorAll('span.Apple-style-span');
                for (var i = 0, ci; ci = spans[i++];) {
                    domUtils.remove(ci, true);
                }
            }

            //ie下使用innerHTML会产生多余的\r\n字符，也会产生&nbsp;这里过滤掉
            html = div.innerHTML;//.replace(/>(?:(\s|&nbsp;)*?)</g,'><');
	        html = replaceHtmlMargin(html);
            //过滤word粘贴过来的冗余属性
            zhtml = UE.filterWord(html);
            //取消了忽略空白的第二个参数，粘贴过来的有些是有空白的，会被套上相关的标签
            var root = UE.htmlparser(html);
            //如果给了过滤规则就先进行过滤
            if (me.options.filterRules) {
                UE.filterNode(root, me.options.filterRules);
            }
            //执行默认的处理
            me.filterInputRule(root);
            //针对chrome的处理
            if (browser.webkit) {
                var br = root.lastChild();
                if (br && br.type == 'element' && br.tagName == 'br') {
                    root.removeChild(br)
                }
                utils.each(me.body.querySelectorAll('div'), function (node) {
                    if (domUtils.isEmptyBlock(node)) {
                        domUtils.remove(node,true)
                    }
                })
            }
            html = {'html': root.toHtml()};
            me.fireEvent('beforepaste', html, root);
            //抢了默认的粘贴，那后边的内容就不执行了，比如表格粘贴
            if(!html.html){
                return;
            }
            root = UE.htmlparser(html.html,true);
            //如果开启了纯文本模式
            if (me.queryCommandState('pasteplain') === 1) {
                me.execCommand('insertHtml', UE.filterNode(root, me.options.filterTxtRules).toHtml(), true);
            } else {
                //文本模式
                UE.filterNode(root, me.options.filterTxtRules);
                txtContent = root.toHtml();
                //完全模式
                htmlContent = html.html;

                address = me.selection.getRange().createAddress(true);
                me.execCommand('insertHtml', me.getOpt('retainOnlyLabelPasted') === true ?  getPureHtml(htmlContent) : htmlContent, true);
            }
            me.fireEvent("afterpaste", html);
        }
    }

    me.addListener('pasteTransfer', function (cmd, plainType) {

        if (address && txtContent && htmlContent && txtContent != htmlContent) {
            var range = me.selection.getRange();
            range.moveToAddress(address, true);

            if (!range.collapsed) {

                while (!domUtils.isBody(range.startContainer)
                    ) {
                    var start = range.startContainer;
                    if(start.nodeType == 1){
                        start = start.childNodes[range.startOffset];
                        if(!start){
                            range.setStartBefore(range.startContainer);
                            continue;
                        }
                        var pre = start.previousSibling;

                        if(pre && pre.nodeType == 3 && new RegExp('^[\n\r\t '+domUtils.fillChar+']*$').test(pre.nodeValue)){
                            range.setStartBefore(pre)
                        }
                    }
                    if(range.startOffset == 0){
                        range.setStartBefore(range.startContainer);
                    }else{
                        break;
                    }

                }
                while (!domUtils.isBody(range.endContainer)
                    ) {
                    var end = range.endContainer;
                    if(end.nodeType == 1){
                        end = end.childNodes[range.endOffset];
                        if(!end){
                            range.setEndAfter(range.endContainer);
                            continue;
                        }
                        var next = end.nextSibling;
                        if(next && next.nodeType == 3 && new RegExp('^[\n\r\t'+domUtils.fillChar+']*$').test(next.nodeValue)){
                            range.setEndAfter(next)
                        }
                    }
                    if(range.endOffset == range.endContainer[range.endContainer.nodeType == 3 ? 'nodeValue' : 'childNodes'].length){
                        range.setEndAfter(range.endContainer);
                    }else{
                        break;
                    }

                }

            }

            range.deleteContents();
            range.select(true);
            me.__hasEnterExecCommand = true;
            var html = htmlContent;
            if (plainType === 2 ) {
                html = getPureHtml(html);
            } else if (plainType) {
                html = txtContent;
            }
            me.execCommand('inserthtml', html, true);
            me.__hasEnterExecCommand = false;
            var rng = me.selection.getRange();
            while (!domUtils.isBody(rng.startContainer) && !rng.startOffset &&
                rng.startContainer[rng.startContainer.nodeType == 3 ? 'nodeValue' : 'childNodes'].length
                ) {
                rng.setStartBefore(rng.startContainer);
            }
            var tmpAddress = rng.createAddress(true);
            address.endAddress = tmpAddress.startAddress;
        }
    });

    me.addListener('ready', function () {
        domUtils.on(me.body, 'cut', function () {
            var range = me.selection.getRange();
            if (!range.collapsed && me.undoManger) {
                me.undoManger.save();
            }
        });

        //ie下beforepaste在点击右键时也会触发，所以用监控键盘才处理
        domUtils.on(me.body, browser.ie || browser.opera ? 'keydown' : 'paste', function (e) {
            if ((browser.ie || browser.opera) && ((!e.ctrlKey && !e.metaKey) || e.keyCode != '86')) {
                return;
            }
            //e.preventDefault();
            getClipboardData.call(me, function (div) {
                filter(div);
            },e);
            // customGetClipboardData.call(me, function (div) {
		     //    filter(div);
            // },e);
        });

    });

    me.commands['paste'] = {
        execCommand: function (cmd) {
            if (browser.ie) {
                getClipboardData.call(me, function (div) {
                    filter(div);
                },e);
                // customGetClipboardData.call(me, function (div) {
		         //    filter(div);
                // },e);
                me.document.execCommand('paste');
            } else {
                alert(me.getLang('pastemsg'));
            }
         //   return false;
        }
    }
};
