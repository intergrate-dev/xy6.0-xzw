/*!
 * docparser
 * version: 1.4.3
 * build: Wed Jul 02 2014 14:05:32 GMT+0800 (CST)
 */

(function () {

//标签属性白名单
    UE._wk_whitelist = {
        a: {
            href: true
        },
        br: {
            type: true
        },
        div: {},
        h1: {},
        h2: {},
        h3: {},
        h4: {},
        h5: {},
        h6: {},
        img: {
            src: true,
            width: true,
            height: true,
            alt: true,
            __trans: {
                'c': 'src',
                'w': 'width',
                'h': 'height'
            }
        },
        math: {}, //公式
        obj: {
            "datatype": true,
            "data": true,
            "bg-img": true,
            "alt": true,
            "width": true,
            "height": true,
            "type": true
        },
        li: {},
        ol: {
            start: true,
            type: true
        },
        p: {
            "datatype": true
        },
        span: {},
        table: {},
        td: {
            rowspan: true,
            colspan: true,
            nowrap: true
        },
        tr: {},
        ul: {
            start: true,
            type: true
        },
        sup: {},
        sub: {},
        ruby: {},
        rt: {},
        rbase: {},
        style: {}
    };
    (function () {

        var EditorExtend = {
            setWkContent: function (bdjson) {
                var me = this, output, root, html;

                me.fireEvent('beforesetwkcontent', bdjson);

                //解析bdjson
                output = UE.parseDoc(bdjson);
                root = output.node;
                //应用文档输出规则
                me.filterWkInputRule(root);
                html = root.toHtml();
                me.setContent(html);

                me.fireEvent('aftersetwkcontent', html);
            },
            /**
             * 添加文档输入过滤
             * @param rule
             */
            addWkInputRule: function (rule) {
                if (this.outputRules === undefined) {
                    this.outputRules = [];
                }
                this.wkInputRules.push(rule);
            },
            /**
             * 执行文档输入过滤
             * @param root
             */
            filterWkInputRule: function (root) {
                if (this.wkInputRules) {
                    for (var i = 0, ci; ci = this.wkInputRules[i++];) {
                        ci.call(this, root)
                    }
                }
            }
        };

        UE.utils.extend(UE.Editor.prototype, EditorExtend);

    })();
// 这里写过文档解析的过滤规则
    UE.plugins['docparserfilters'] = function () {
        var me = this,
            utils = UE.utils,
            inputFilters = [];

        function keys(o) {
            var a = [];
            utils.each(o, function (v, k) {
                a.push(k);
            });
            return a;
        }

        function addinputFilters(fn) {
            inputFilters.push(fn);
        }

        // 清除 styleName 的 style 属性
        me.addInputRule(function (root) {
            root.traversal(function (node) {
                utils.each(inputFilters, function (filter, k) {
                    if (node.parentNode) {
                        return filter.call(me, node);
                    }
                });
            });
        });

        // 处理 h1-h6
        addinputFilters(function (node) {
            var style;
            if (node.type == 'element' && (style = node.getAttr('style')) && 'h1;h2;h3;h4;h5;h6;h7;'.indexOf(node.tagName + ';') != -1) {
                removeParaSizeStyle(node);
                node.traversal(function (child) {
                    removeParaSizeStyle(child);
                });
            }

            function removeParaSizeStyle(node) {
                var style = node.getAttr('style');
                if (node.type == 'element' && style) {
                    style = style.replace(/font-size:[^;]*;/g, ''). //font-size
                        replace(/font-weight:[^;]*;/g, ''). //font-weight
                        replace(/margin(-[\w]+)?:[^;]*;/g, ''). //margin
                        replace(/padding(-[\w]+)?:[^;]*;/g, ''). //padding
                        replace(/line-height:[^;]*;/g, ''); //line-height
                    node.setAttr('style', style);
                }
            }
        });

        // p 标签根据 style 里的 styleName 转换为 引用段落
        addinputFilters(function (node) {
            if (node.type == 'element' && node.tagName == 'p') {
                var style;
                if ((style = node.getAttr('style')) && style.match(/styleName:[^;]*quote;[^;]*;/i)) {
                    node.tagName = 'blockquote';
                }
            }
        });

        // 处理段落 p
        addinputFilters(function (node) {

            var style;
            if (node.type == 'element' && (style = node.getAttr('style'))) {

                // 清除 text-align:left;
                style = utils.trim(style.replace(/text-align:[\s]*left;/g, ''));

                // 清除 font-size:14px
                style = utils.trim(style.replace(/font-size:[\s]*14px;/g, ''));
                node.setAttr('style', style);
            }

        });

        // 清除空的和无属性的 span 标签
        addinputFilters(function (node) {

            if (node.type == 'element' && node.tagName == 'span') {

                // 清除空的 span
                if (node.children.length == 0) {
                    node.parentNode.removeChild(node);
                }

                // 清除无属性的 span 标签
                if (node.parentNode && keys(node.attrs).length == 0 && !node.getData()) {
                    while (node.children[0] !== undefined) {
                        node.parentNode.insertBefore(node.children[0], node);
                    }
                    node.parentNode.removeChild(node);
                }
            }

        });

        // 合并相同属性的 span 标签
        addinputFilters(function (node) {

            if (node.type == 'element' && node.tagName == 'span') {
                var isSame, next;
                while (next = node.nextSibling()) {
                    if (next && next.type == 'element' && next.tagName == node.tagName) {
                        if (keys(node.attrs).length == keys(next.attrs).length) {
                            isSame = true;
                            utils.each(node.attrs, function (v, i) {
                                if (next.attrs[i] !== undefined && next.attrs[i] != v) {
                                    isSame = false;
                                    return false;
                                }
                            });
                        } else {
                            isSame = false;
                        }

                        if (isSame) {
                            while (next.children[0] !== undefined) {
                                node.appendChild(next.children[0]);
                            }
                            next.parentNode.removeChild(next);
                            continue;
                        }
                    }
                    break;
                }
            }

        });

        // 根据样式转换内联标签
        addinputFilters(function (node) {

            var style, tagName;
            if (node.type == 'element' && node.tagName == 'span' && (style = node.getAttr('style'))) {
                // valign:sup; 或 valign:sub; 转换为 sup 或 sub 标签
                style = utils.trim(style.replace(/valign:[\s]*(sup|sub);/g, function (str, type) {
                    tagName = type;
                    return '';
                }));
                if (tagName) {
                    style = style.replace(/font-size:[\s]*[\d]+px;/g, '');
                }

                // font-style:italic; 转换为 em 标签
                style = utils.trim(style.replace(/font-style:[\s]*italic;/g, function (str) {
                    tagName = 'em';
                    return '';
                }));

                // font-weight:700; 转换为 strong 标签
                style = utils.trim(style.replace(/font-weight:[\s]*([\d]+);/g, function (str, val) {
                    if (parseInt(val) >= 700) {
                        tagName = 'strong';
                        return '';
                    }
                    return '';
                }));

                if (tagName) {
                    node.tagName = tagName;
                }

                node.setAttr('style', style);

            }

        });

        // 清除 style 的 styleName 属性
        addinputFilters(function removeStyleName(node) {
            var style;
            if (node.type == 'element' && (style = node.getAttr('style'))) {
                style = utils.trim(style.replace(/styleName:[\s\S]+?(;|$)/g, ''));
                node && node.setAttr('style', style);
            }
        });

    };

    /**
     * 文库文档资源解析
     * Date: 14-06-16
     * Time: 上午10:21
     */
    (function () {
        var uNode = UE.uNode;
        var utils = UE.utils;
        var DEBUG = false;

        //标签白名单
        var TagList = UE._wk_whitelist;
        //标签名mapping
        var ATTR = {
            tagName: 't',
            className: 'r',
            children: 'c'
        };


        /**
         * 转换函数
         * @param source 源数据
         * @return HTML String, 符合w3c 规范的html字符串片段
         */
        function transform(source) {

            var tagName = source[ ATTR['tagName'] ],
                classNames = source[ ATTR['className'] ],
                children = source[ ATTR['children'] ],
                _selfFn = arguments.callee,
                node = null;

            //合法性检测
            if (!isLegalTag(tagName)) {
                if (DEBUG) {
                    throw new Error('Illegal tag, tag name is: ' + tagName);
                } else {
                    return null;
                }
            }

            node = new uNode({
                type: 'element',
                children: [],
                tagName: tagName
            });

            classNames && node.setAttr('class', utils.isArray(classNames) ? classNames.join(" ") : classNames);

            setValidAttr(node, source);

            if (children) {
                if (utils.isArray(children)) {
                    utils.each(children, function (childSource) {
                        var childNode = _selfFn(childSource);
                        childNode && node.appendChild(childNode);
                    });
                } else if (utils.isString(children)) {
                    node.appendChild(new uNode({
                        'type': 'text',
                        'data': utils.unhtml(children || '')
                    }));
                } else if (utils.isObject(children)) {
                    var childNode = _selfFn(children);
                    childNode && node.appendChild(childNode);
                } else {
                    throw new Error('unkonw child type');
                }
            }

            return node;

        }

        /**
         * 给定的标签名是否是合法的标签名
         * 依赖白名单来进行判断
         * @param tagName 表签名
         * @return <boolean> 是否合法
         */
        function isLegalTag(tagName) {
            return tagName in TagList;
        }

        /**
         * 给node应用样式
         * 如果不存在指定的样式, 则什么也不做
         * @param node 节点
         * @param styleMap class和样式对应
         * @param baseStyle 标签名称和class对应
         */
        function applyStyle(node, styleMap, baseStyle) {
            var classNames, i, mapKeys, styleStr, unTransClassNames = [], styleArr = [];

            //按标签名应用样式
            if (node.type == 'element' && (mapKeys = baseStyle[node.tagName.toLowerCase()])) {
                for (i = 0; i < mapKeys.length; i++) {
                    if (mapKeys[i] in styleMap) {
                        styleStr = node.getAttr('style') || '';
                        styleStr = utils.trim(styleStr) && styleStr.substr(styleStr.length - 1) != ';' ? (styleStr + ';') : styleStr;
                        styleStr = styleStr + styleMap[mapKeys[i]];
                        node.setAttr('style', styleStr);
                    }
                }
            }

            //按class应用样式
            classNames = node.getAttr('class');
            classNames = classNames ? utils.trim(classNames).split(/\s+/) : [];
            if (classNames && classNames.length) {
                for (i = 0; i < classNames.length; i++) {
                    var className = classNames[i];
                    if (className in styleMap) {
                        styleArr.push(styleMap[className]);
                    } else {
                        unTransClassNames.push(className);
                    }
                }

                styleStr = (styleArr.join(';') + ';').replace(/(^;)/, '').replace(/;{2,}/g, ';');
                styleStr && node.setAttr('style', styleStr);

                if (unTransClassNames.length) {
                    node.setAttr('class', unTransClassNames.join(" "));
                } else {
                    node.setAttr('class');
                }
            }

            /* 对孩子节点应用样式 */
            if (node.type == 'element') {
                for (i = 0; i < node.children.length; i++) {
                    applyStyle(node.children[i], styleMap, baseStyle);
                }
            }

        }

        function formatStyle(styles) {
            var res = {};

            utils.each(styles, function (style, i) {
                var styleArr = [];
                utils.each(style, function (val, key) {
                    if (/[\d\.]+px/.test(val)) {
                        val = val.replace(/[\d]+\.[\d]*px/g, function (a) {
                            return parseInt(a) + 'px';
                        });
                    }
                    styleArr.push(utils.trim('' + key) + ':' + utils.trim('' + val));
                });
                res[i] = (styleArr.join(';') + ';').replace(/(^;)/, '').replace(/;{2,}/g, ';');
                if (res[i][0] == ';') console.log(res[i]);
            });
            return res;
        }

        /**
         * 通过给定的源数据验证该node节点的属性, 如果该属性符合规则(通过白名单来确定), 则附加该属性到节点上, 否则, 抛弃该属性
         * @param node 需要附加属性的节点
         * @param source 源数据
         * @returns node, 返回作为第一个参数传递进来的node节点, 该节点可能包含有属性值
         */
        function setValidAttr(node, source) {
            var attrList = TagList[ node.tagName ],
                __trans = attrList['__trans'];

            utils.each(source, function (attrValue, attrName) {
                if (attrName.indexOf('__') === 0) {
                    return;
                }
                //属性转换
                if (__trans && __trans[ attrName ]) {
                    var tmp = source[ attrName ];
                    delete source[ attrName ];
                    attrName = __trans[ attrName ];
                    source[ attrName ] = tmp;
                    tmp = null;
                }
                if (attrList[ attrName ]) {
                    node.setAttr(attrName, source[ attrName ] + "");
                }
            });
        }

        /**
         * 装换word内容成html内容
         * @param doc
         * @return String 转换后的字符串
         */
        UE.parseDoc = function (doc) {
            var node, htmlArr = [], htmlStr = '', root;
            if (doc) {
                doc = utils.isArray(doc) ? doc : [doc];
                root = new uNode({
                    type: 'element',
                    children: [],
                    tagName: 'div'
                });
                for (var i = 0; i < doc.length; i++) {
                    //bdjson转换成uNode对象
                    node = transform(doc[i]);
                    //应用样式到node
                    applyStyle(node, formatStyle(doc[i].style), doc[i].baseStyle);
                    //插入到根节点
                    root.children.push(node);
                    //node转换成html
                    htmlArr.push(node.toHtml());
                }
                htmlStr = htmlArr.join('');
            } else {
                alert('文档解析出错');
            }

            return {
                html: htmlStr,
                node: root
            };
        };

    })();

    UE.registerUI('editword', function (editor, uiName) {

        var me = editor;

        var btn = new UE.ui.Button({
            name: uiName,
            title: '编辑word文档',
            cssRules: 'background-position: -301px -40px; display: block!important;',
            onclick: function () {}
        });

        editor.addListener('ready', function () {
            var b = btn.getDom('body'),
                iconSpan = b.children[0];
            editor.afterConfigReady(function () {
                initWordBtn(iconSpan);
            });
        });

        function initWordBtn(containerBtn) {
            var timestrap = (+new Date()).toString(36),
                w = containerBtn.offsetWidth || 20,
                h = containerBtn.offsetHeight || 20,
                homeUrl = me.options.UEDITOR_HOME_URL,
                actionUrl = 'http://convert.wenku.baidu.com/rtcs/convert?pn=1&rn=-1',
                fileVal = 'file',
                btnIframe = document.createElement('iframe'),
                btnIframeId = 'edui_upload_' + timestrap,
                btnStyle = 'display:block;width:' + w + 'px;height:' + h + 'px;min-width:20px;min-height:20px;overflow:hidden;border:0;margin:0;padding:0;position:absolute;top:0;left:0;filter:alpha(opacity=0);-moz-opacity:0;-khtml-opacity: 0;opacity: 0;cursor:pointer;';


            UE['__uploaderEvents_' + timestrap] = function () {
                var args = UE.utils.clone([], arguments);
                args[0] = 'uploader_' + args[0];
                return me.fireEvent.apply(me, args);
            };
            me.on('uploader_filesQueued', function (type, files) {
                if (files && files[0]) {
                    var msgContent = files[0] && files[0].ext == 'doc' ? '.doc文档耗时较长，正在解析...':'正在解析...';
                    ue.trigger('showmessage', {
                        'id': files[0].id,
                        'type': 'info',
                        'content': msgContent,
                        'keepshow': true
                    });
                }
            });
            me.on('uploader_uploadSuccess', function (type, file, r) {

                var bdjson,
                    fileId = file.id;

                try {
                    bdjson = eval('(' + r._raw + ')');
                } catch (e) {
                    showErrorMessage(file);
                    return;
                }

                if (bdjson['document.xml'] && bdjson['document.xml'].length && bdjson['status'] && bdjson['status']['success']) {
                    ue.trigger('updatemessage', fileId, {
                        'type': 'success',
                        'content': '解析成功!',
                        'timeout': 2000
                    });
                    ue.setWkContent(bdjson['document.xml']);
                } else {
                    showErrorMessage(file);
                }

            });
            me.on('uploader_uploadError', function (type, file) {
                showErrorMessage(file);
            });
            me.on('uploader_error', function (type, error, file) {
                if (error == 'F_EXCEED_SIZE') {
                    ue.trigger('showmessage', {
                        'id': file.id,
                        'type': 'error',
                        'content': '上传文档大小超出了 5MB 的限制！'
                    });
                }
            });

            function showErrorMessage(file){
                ue.trigger('updatemessage', file.id, {
                    'type': 'error',
                    'content': file.ext == 'doc' ? '文档解析出错，请重试或转成docx格式再上传。':'文档解析出错，请重试。',
                    'timeout': 2000
                });
            }

            btnIframe.name = btnIframe.id = btnIframeId;
            btnIframe.style.cssText = btnStyle;
            containerBtn.appendChild(btnIframe);

        }

        return btn;
    });

})();