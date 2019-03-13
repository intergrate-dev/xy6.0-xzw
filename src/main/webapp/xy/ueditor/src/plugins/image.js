/**
 * Created by YangBin on 2017/11/16.
 */
/**
 * 图片插入、排版插件
 * @file
 * @since 1.2.6.1
 */

/**
 * 图片对齐方式
 * @command imagefloat
 * @method execCommand
 * @remind 值center为独占一行居中
 * @param { String } cmd 命令字符串
 * @param { String } align 对齐方式，可传left、right、none、center
 * @remaind center表示图片独占一行
 * @example
 * ```javascript
 * editor.execCommand( 'imagefloat', 'center' );
 * ```
 */

/**
 * 如果选区所在位置是图片区域
 * @command imagefloat
 * @method queryCommandValue
 * @param { String } cmd 命令字符串
 * @return { String } 返回图片对齐方式
 * @example
 * ```javascript
 * editor.queryCommandValue( 'imagefloat' );
 * ```
 */

UE.commands['imagefloat'] = {
    execCommand: function (cmd, align) {
        var me = this,
            range = me.selection.getRange();
        if (!range.collapsed) {
            var img = range.getClosedNode();
            if (img && img.tagName == 'IMG') {
                switch (align) {
                    case 'left':
                    case 'right':
                    case 'none':
                        var pN = img.parentNode, tmpNode, pre, next;
                        while (dtd.$inline[pN.tagName] || pN.tagName == 'A' || pN.tagName == 'SPAN') {
                            pN = pN.parentNode;
                        }
                        tmpNode = pN;
                        if (tmpNode.tagName == 'P' && domUtils.getStyle(tmpNode, 'text-align') == 'center') {
                            if (!domUtils.isBody(tmpNode) && domUtils.getChildCount(tmpNode, function (node) {
                                return !domUtils.isBr(node) && !domUtils.isWhitespace(node);
                            }) == 1) {
                                pre = tmpNode.previousSibling;
                                next = tmpNode.nextSibling;
                                if (pre && next && pre.nodeType == 1 && next.nodeType == 1 && pre.tagName == next.tagName && domUtils.isBlockElm(pre)) {
                                    pre.appendChild(tmpNode.firstChild);
                                    while (next.firstChild) {
                                        pre.appendChild(next.firstChild);
                                    }
                                    domUtils.remove(tmpNode);
                                    domUtils.remove(next);
                                } else {
                                    domUtils.setStyle(tmpNode, 'text-align', '');
                                }
                            }
                            range.selectNode(img).select();
                        }

                        if (tmpNode.tagName == 'FIGURE') {
                            domUtils.setStyle(tmpNode, 'float', align == 'none' ? '' : align);
                        } else {
                            domUtils.setStyle(img, 'float', align == 'none' ? '' : align);

                        }
                        if (align == 'none') {
                            domUtils.removeAttributes(img, 'align');
                            domUtils.setStyle(img, 'margin-left', '0');
                            domUtils.setStyle(img, 'margin-right', '0');
                        } else {
                            //左浮动时图片右侧加距离，避免图文紧挨着
                            if (align == 'left') {
                                domUtils.setStyle(img, 'margin-right', '10px');
                                domUtils.setStyle(img, 'margin-left', '0');
                            } else if (align == 'right') {
                                domUtils.setStyle(img, 'margin-left', '10px');
                                domUtils.setStyle(img, 'margin-right', '0');
                            }
                            var nextN;
                            if (tmpNode.tagName == 'P' && (domUtils.getStyle(tmpNode, 'text-align') == 'center' || $(tmpNode).attr("id") == "_img_parent_tmp")) {
                                nextN = tmpNode.nextSibling;
                                $(img).unwrap();
                                tmpNode = img.parentNode;
                            }
                            if (tmpNode.tagName == 'FIGURE') {
                                nextN = img.nextSibling;
                            }
                            if (nextN && nextN.nodeName == "FIGCAPTION" && tmpNode && tmpNode.nodeName == "FIGURE") {
                                domUtils.setStyle(nextN, 'top', img.height + "px");
                                domUtils.setStyle(tmpNode, 'position', 'relative');
                                domUtils.setStyle(nextN, 'position', 'absolute');
                                if (align == "right") {
                                    domUtils.removeStyle(nextN, 'left');
                                    domUtils.setStyle(nextN, 'right', "0px");
                                } else if (align == "left") {
                                    domUtils.removeStyle(nextN, 'right');
                                    domUtils.setStyle(nextN, 'left', "0px");
                                }
                            }
                        }
                        break;
                    case 'center':
                        if (me.queryCommandValue('imagefloat') == 'center') {
                            pN = img.parentNode;
                            domUtils.setStyle(img, 'float', '');
                            domUtils.removeAttributes(img, 'align');
                            domUtils.setStyle(img, 'margin-left', '0');
                            domUtils.setStyle(img, 'margin-right', '0');
                            tmpNode = img;
                            var nextN;
                            if (pN.tagName == 'P' && domUtils.getStyle(tmpNode, 'text-align') == 'center') {
                                nextN = tmpNode.nextSibling;
                            }
                            else if (pN.tagName == 'FIGURE') {
                                nextN = img.nextSibling;
                                domUtils.setStyle(pN, 'float', '');
                            }
                            if (nextN && nextN.nodeName == "FIGCAPTION" && pN && pN.nodeName == "FIGURE") {
                                domUtils.setStyle(nextN, 'top', img.height + "px");
                                domUtils.removeStyle(nextN, 'left');
                                domUtils.removeStyle(nextN, 'right');
                                domUtils.removeStyle(nextN, 'position');
                                // domUtils.removeStyle(tmpNode, 'position');
                            }
                            while (pN && domUtils.getChildCount(pN, function (node) {
                                return !domUtils.isBr(node) && !domUtils.isWhitespace(node);
                            }) == 1 && (dtd.$inline[pN.tagName] || pN.tagName == 'A')) {
                                tmpNode = pN;
                                pN = pN.parentNode;
                            }
                            range.setStartBefore(tmpNode).setCursor(false);
                            pN = me.document.createElement('div');
                            pN.appendChild(tmpNode);
                            domUtils.setStyle(tmpNode, 'float', '');

                            var url = img && img.getAttribute("url") ? ' url="' + img.getAttribute("url") + '" ' : '';
                            var urlApp = img && img.getAttribute("urlapp") ? ' urlapp="' + img.getAttribute("urlapp") + '" ' : '';
                            var videoId = img && img.getAttribute("videoid") ? ' videoid="' + img.getAttribute("videoid") + '" ' : '';

                            me.execCommand('insertHtml', '<p' + url + urlApp + videoId + ' id="_img_parent_tmp" style="text-align:center">' + pN.innerHTML + '</p>');

                            tmpNode = me.document.getElementById('_img_parent_tmp');
                            tmpNode.removeAttribute('id');
                            tmpNode = tmpNode.firstChild;
                            range.selectNode(tmpNode).select();
                            //去掉后边多余的元素
                            next = tmpNode.parentNode.nextSibling;
                            if (next && domUtils.isEmptyNode(next)) {
                                domUtils.remove(next);
                            }

                        }

                        break;
                }

            }
        }
    },
    queryCommandValue: function () {
        var range = this.selection.getRange(),
            startNode, floatStyle;
        if (range.collapsed) {
            return 'none';
        }
        startNode = range.getClosedNode();
        if (startNode && startNode.nodeType == 1 && startNode.tagName == 'IMG') {
            floatStyle = domUtils.getComputedStyle(startNode, 'float') || startNode.getAttribute('align');

            if (floatStyle == 'none') {
                floatStyle = domUtils.getComputedStyle(startNode.parentNode, 'text-align') == 'center' ? 'center' : floatStyle;
            }
            return {
                left: 1,
                right: 1,
                center: 1
            }[floatStyle] ? floatStyle : 'none';
        }
        return 'none';


    },
    queryCommandState: function () {
        var range = this.selection.getRange(),
            startNode;

        if (range.collapsed) return -1;

        startNode = range.getClosedNode();
        if (startNode && startNode.nodeType == 1 && startNode.tagName == 'IMG') {
            return 0;
        }
        return -1;
    }
};


/**
 * 插入图片
 * @command insertimage
 * @method execCommand
 * @param { String } cmd 命令字符串
 * @param { Object } opt 属性键值对，这些属性都将被复制到当前插入图片
 * @remind 该命令第二个参数可接受一个图片配置项对象的数组，可以插入多张图片，
 * 此时数组的每一个元素都是一个Object类型的图片属性集合。
 * @example
 * ```javascript
 * editor.execCommand( 'insertimage', {
 *     src:'a/b/c.jpg',
 *     width:'100',
 *     height:'100'
 * } );
 * ```
 * @example
 * ```javascript
 * editor.execCommand( 'insertimage', [{
 *     src:'a/b/c.jpg',
 *     width:'100',
 *     height:'100'
 * },{
 *     src:'a/b/d.jpg',
 *     width:'100',
 *     height:'100'
 * }] );
 * ```
 */

UE.commands['insertimage'] = {
    execCommand: function (cmd, opt) {
        opt = utils.isArray(opt) ? opt : [opt];
        if (!opt.length) {
            return;
        }
        var me = this,
            range = me.selection.getRange(),
            img = range.getClosedNode();

        if (me.fireEvent('beforeinsertimage', opt) === true) {
            return;
        }

        if (img && /img/i.test(img.tagName) && (img.className != "edui-faked-video" || img.className.indexOf("edui-upload-video") != -1) && !img.getAttribute("word_img")) {
            var first = opt.shift();
            var floatStyle = first['floatStyle'];
            delete first['floatStyle'];
            ////                img.style.border = (first.border||0) +"px solid #000";
            ////                img.style.margin = (first.margin||0) +"px";
            //                img.style.cssText += ';margin:' + (first.margin||0) +"px;" + 'border:' + (first.border||0) +"px solid #000";
            domUtils.setAttributes(img, first);
            me.execCommand('imagefloat', floatStyle);
            if (opt.length > 0) {
                range.setStartAfter(img).setCursor(false, true);
                me.execCommand('insertimage', opt);
            }

        } else {
            var html = [], str = '', ci;
            ci = opt[0];
            if (opt.length == 1) {
                str = '<img src="' + ci.src + '" ' + (ci._src ? ' _src="' + ci._src + '" ' : '') +
                    (ci.width ? 'width="' + ci.width + '" ' : '') +
                    (ci.height ? ' height="' + ci.height + '" ' : '') +
                    (ci['floatStyle'] == 'left' || ci['floatStyle'] == 'right' ? ' style="float:' + ci['floatStyle'] + ';"' : '') +
                    (ci.title && ci.title != "" ? ' title="' + ci.title + '"' : '') +
                    (ci.border && ci.border != "0" ? ' border="' + ci.border + '"' : '') +
                    (ci.alt && ci.alt != "" ? ' alt="' + ci.alt + '"' : '') +
                    (ci.style && ci.style != "" ? ' style="' + ci.style + '"' : '') +
                    //001
                    (ci.pic && ci.pic != "" ? ' pic="' + ci.pic + '"' : '') +
                    (ci.hspace && ci.hspace != "0" ? ' hspace = "' + ci.hspace + '"' : '') +
                    (ci.vspace && ci.vspace != "0" ? ' vspace = "' + ci.vspace + '"' : '') + '/>';
                if (ci['floatStyle'] == 'center') {
                    str = '<p style="text-align: center">' + str + '</p>';
                }
                if (ci.content) {
                    str += "<p>" + ci.content + "</p>";
                }
                html.push(str);

            } else {
                for (var i = 0; ci = opt[i++];) {
                    str = '<p ' + (ci['floatStyle'] == 'center' ? 'style="text-align: center" ' : '') + '><img src="' + ci.src + '" ' +
                        (ci.width ? 'width="' + ci.width + '" ' : '') + (ci._src ? ' _src="' + ci._src + '" ' : '') +
                        (ci.height ? ' height="' + ci.height + '" ' : '') +
                        ' style="' + (ci['floatStyle'] && ci['floatStyle'] != 'center' ? 'float:' + ci['floatStyle'] + ';' : '') + (ci.style && ci.style != "" ? ci.style : '') +
                        (ci.border || '') + '" ' +
                        //001
                        (ci.title ? ' title="' + ci.title + '"' : '') + (ci.pic ? ' pic="' + ci.pic + '"' : '') + ' /></p>';
                    if (ci.content) {
                        str += "<p>" + ci.content + "</p>";
                    }
                    html.push(str);
                }
            }

            me.execCommand('insertHtml', html.join(''));
        }

        me.fireEvent('afterinsertimage', opt)
    }
};

UE.commands['imagecrop'] = {
    queryCommandState: function () {
        var range = this.selection.getRange(),
            startNode;

        if (range.collapsed) return -1;

        startNode = range.getClosedNode();
        if (startNode && startNode.nodeType == 1 && startNode.tagName == 'IMG') {
            return 0;
        }
        return -1;
    }
};
//图片缩放
var zoomLevel = 0;
UE.commands['imagescale'] = {
    execCommand: function (cmd, align) {
        var me = this;
        var myImage = getImage(me);
        zoomLevel = myImage.getAttribute("data-ratio");
        var currentWidth = myImage.width;
        var currentHeight = myImage.height;
        if (align == "bigger" && zoomLevel < 5) {
            myImage.style.width = currentWidth * 1.1 + "px";
            myImage.style.height = currentHeight * 1.1 + "px";
            zoomLevel++;
        }
        if (align == "smaller" && zoomLevel > -5) {
            myImage.style.width = currentWidth / 1.1 + "px";
            myImage.style.height = currentHeight / 1.1 + "px";
            zoomLevel--;
        }
        update();
        function update() {
            currentWidth = myImage.width;
            currentHeight = myImage.height;
            myImage.setAttribute("data-ratio", zoomLevel);
        }
    },
};
//删除图片
UE.commands['imagedetele'] = {
    execCommand: function (cmd) {
        var me = this;
        var myImage = getImage(me);
        if ($(myImage).hasClass('edui-faked-video')) {
            if (confirm("是否删除视频？")) {
                myImage.parentNode.remove();
            }
        } else {
            if (confirm("是否删除图片？")) {
                myImage.parentNode.remove();
            }
        } 
    }
};
//图说
UE.commands['imagecomment'] = {
    execCommand: function (cmd) {
        var imageCommentContent = "";
        var me = this;
        console.log(me);
        var myImage = getImage(me);
        window.b = myImage;
        console.log(myImage);
        var imgWidth = myImage.width,
            imgHeight = myImage.height;
        var nNode = myImage.nextSibling,
            pNode = myImage.parentNode,
            timeTab = myImage.getAttribute("imgTime");
        if (!timeTab) {
            timeTab = new Date().getTime();
        }
        //与图片批注冲突，所以去掉
        //myImage.setAttribute("class","imgComment_image");
        //myImage.setAttribute("id","imgComment_image_" + timeTab);
        myImage.setAttribute("imgTime", timeTab);

        //给img标签外面套一个自定义标签<figure>
        if (pNode && pNode.nodeName == "P" && ($(pNode).css("text-align") == "center" || $(pNode).attr("id") == "_img_parent_tmp")) {
            $(myImage).unwrap();
            pNode = myImage.parentNode;
            nNode = myImage.nextSibling;
        }
        if (!pNode || pNode.nodeName != "FIGURE") {
            $(myImage).wrap("<figure class='imgComment_figure' id='" + timeTab + "' style='text-align: center;position:relative;margin: 0px'></figure>");
        } else {
            pNode.setAttribute("class", "imgComment_figure");
            pNode.setAttribute("id", timeTab);
            $(pNode).css("text-align", "center");
            $(pNode).css("position", "relative");
            $(pNode).css("margin", "0px");
        }
        //判断是否存在图说节点
        var isFigures = nNode && nNode.nodeName == "FIGCAPTION";
        //如果存在图说节点 把图说的内容放到textarea里面
        if (isFigures) {
            nNode.setAttribute("class", "imgComment_content");
            nNode.setAttribute("id", "figures_" + timeTab);
            nNode.setAttribute("style", "line-height:20px;padding: 0px 0px 0px;font-size: 0.9em;margin: 5px auto 0px;color: gray;text-align: center;word-wrap: break-word;");
            $(nNode).css("top", imgHeight + "px");
            $(nNode).css("width", imgWidth + "px");
            imageCommentContent = nNode.innerHTML;
        }
        $("#imgComment").remove();
        var pop = document.getElementById("imageComment").parentNode.parentNode.parentNode.parentNode.parentNode,
            div = document.createElement("div");
        div.id = "imgComment";
        pop.appendChild(div);
        div.innerHTML = "<textarea onclick='inputImgComment()' oninput='autoResize()' id='insertComment' class='_imageComment-text'>" + imageCommentContent + "</textarea></div>" +
            "<div id='commentBtnBox'><input onclick='saveImgComment(" + timeTab + "," + isFigures + "," + imgWidth + "," + imgHeight + ")' type='button' id='saveComment' class='_imageComment-button' value='保存'>" +
            "<input onclick='cancelImgComment()' type='button' id='cancelComment' class='_imageComment-button' value='取消'>" +
            "<span style='line-height: 20px;position: absolute;right: 18px;bottom: 7px;' class='imagePage' id='globalCaption'><label id='globaleImgCaption'  name='globaleImgCaption' >全局使用<input  style='margin: 0; margin-left: 6px;' id='globaleImgCaptionInput'  name='globaleImgCaptionInput' type='checkbox'></label></span>" +
            "</div>";
        var _height = $("#imageComment").parent().parent().parent().css("height");
        var _width = $("#imageComment").parent().parent().parent().css("width");
        var _top = $("#imageComment").parent().parent().parent().parent().css("top");
        var _left = $("#imageComment").parent().parent().parent().parent().css("left");
        $("#imgComment").css({ "position": "absolute", "top": parseInt(_top) + parseInt(_height), "left": _left, "width": _width, "background": "#fff", "padding": "6px 0", "border": "1px solid #CDCDCD", "border-top": "none", "box-sizing": "border-box" })
    }
};
//获取选中的图片节点
function getImage(_this) {
    var myImage,
        range = _this.selection.getRange(),
        node = range.startContainer;
    //选中图片
    if (node.tagName == "IMG") {
        myImage = node;
    } else if (node.tagName == "P" && node.firstChild && node.firstChild.nodeName == "IMG") {
        myImage = node.firstChild;
    } else {
        var nodes = node.childNodes;
        for (var i = 0, len = nodes.length; i < len; i++) {
            if (nodes[i].tagName == "IMG") {
                myImage = nodes[i];
            }
        }
        /*nodes.forEach(function(val){
         if(val.tagName == "IMG"){
         myImage = val;
         }
         })*/
    }
    return myImage;
}
function inputImgComment() {
    $("#insertComment").focus();
};

function saveImgComment(timeTab, figures, imgWidth, imgHeight) {

    var ue = UE.getEditor("editor");
    var figuresId = "figures_" + timeTab;
    var imageCommentContent = $("#insertComment").val();

    if ($('#globaleImgCaptionInput').prop("checked")) {
        var imgArr = ue.selection.document.childNodes[1].childNodes[1].getElementsByTagName('img')
        for (var i = 0; i < imgArr.length; i++) {
            var myImage = imgArr[i];
            var imgWidth = myImage.width,
                imgHeight = myImage.height;
            var nNode = getNextElement(myImage),
                pNode = myImage.parentNode,
                timeTab = myImage.getAttribute("imgTime");
            if (!timeTab) {
                timeTab = new Date().getTime();
            }
            myImage.setAttribute("imgTime", timeTab);
            //给img标签外面套一个自定义标签<figure>
            if (pNode && pNode.nodeName == "P" && ($(pNode).css("text-align") == "center" || $(pNode).attr("id") == "_img_parent_tmp")) {
                $(myImage).unwrap();
                pNode = myImage.parentNode;
            }
            if (!pNode || pNode.nodeName != "FIGURE") {
                $(myImage).wrap("<figure class='imgComment_figure' id='" + timeTab + "' style='text-align: center;position:relative;margin: 0px'></figure>");
            } else {
                pNode.setAttribute("class", "imgComment_figure");
                pNode.setAttribute("id", timeTab);
                $(pNode).css("text-align", "center");
                $(pNode).css("position", "relative");
                $(pNode).css("margin", "0px");
            }
            var isFigures = nNode && nNode.nodeName == "FIGCAPTION";
            //如果存在图说节点 把图说的内容放到textarea里面
            if (isFigures) {
                nNode.setAttribute("class", "imgComment_content");
                nNode.setAttribute("id", "figures_" + timeTab);
                nNode.setAttribute("style", "line-height:20px;padding: 0px 0px 0px;font-size: 0.9em;margin: 5px auto 0px;color: gray;text-align: center;word-wrap: break-word;");
                $(nNode).css("top", imgHeight + "px");
                $(nNode).css("width", imgWidth + "px");
            }
            //如果没有图说节点   而且有图说  就在图片下面加一个节点存放图说内容  ---> 这不存在吧

            if (!isFigures && imageCommentContent) {
                $(myImage).after("<figcaption id='" + figuresId + "' class='imgComment_content' style='top:" + imgHeight + "px;line-height:20px;padding: 0px 0px 0px;font-size: 0.9em;margin: 5px auto 0px;color: gray;text-align: center;word-wrap: break-word;width:100%;'>" + imageCommentContent + "</figcaption>");

            }//如果有图说节点  但是没有图说  则删除图说节点
            // else if(figures && !imageCommentContent){
            //     $(ue.body).find("#"+figuresId).remove();
            // }
            //如果有图书节点  而且有图说  则更新一次
            else if (isFigures && imageCommentContent) {
                $(myImage).next().html(imageCommentContent);
            }
        }
    } else {
        //如果没有图说节点   而且有图说  就在图片下面加一个节点存放图说内容
        if (!figures && imageCommentContent) {
            $(ue.body).find("#" + timeTab).append("<figcaption id='" + figuresId + "' class='imgComment_content' style='top:" + imgHeight + "px;line-height:20px;padding: 0px 0px 0px;font-size: 0.9em;margin: 5px auto 0px;color: gray;text-align: center;word-wrap: break-word;width:100%'>" + imageCommentContent + "</figcaption>");
        }//如果有图说节点  但是没有图说  则删除图说节点
        else if (figures && !imageCommentContent) {
            $(ue.body).find("#" + figuresId).remove();
        }
        //如果有图书节点  而且有图说  则更新一次
        else if (figures && imageCommentContent) {
            $(ue.body).find("#" + figuresId).html(imageCommentContent);
        }


    }

    //执行完成后移除下拉窗
    $("div#imgComment").remove();
    function getNextElement(element) {
        var e = element.nextSibling;
        if (e == null) {//测试同胞节点是否存在，否则返回空
            return null;
        }
        if (e.nodeType == 3) {//如果同胞元素为文本节点
            var two = getNextElement(e);
            if (two.nodeType == 1)
                return two;
        } else {
            if (e.nodeType == 1) {//确认节点为元素节点才返回
                return e;
            } else {
                return false;
            }
        }
    }



    // //以下为之前的代码
    // var figuresId = "figures_" + timeTab;
    // var imageCommentContent = $("#insertComment").val();
    // //如果没有图说节点   而且有图说  就在图片下面加一个节点存放图说内容
    // if(!figures && imageCommentContent){
    //     $(ue.body).find("#"+timeTab).append("<figcaption id='"+figuresId+"' class='imgComment_content' style='top:"+imgHeight+"px;line-height:20px;padding: 0px 0px 0px;font-size: 0.9em;margin: 5px auto 0px;color: gray;text-align: center;word-wrap: break-word;width:100%;'>"+imageCommentContent+"</figcaption>");
    // }//如果有图说节点  但是没有图说  则删除图说节点
    // else if(figures && !imageCommentContent){
    //     $(ue.body).find("#"+figuresId).remove();
    // }
    // //如果有图书节点  而且有图说  则更新一次
    // else if(figures && imageCommentContent){
    //     $(ue.body).find("#"+figuresId).html(imageCommentContent);
    // }
    // $("div#imgComment").remove();

};
function cancelImgComment() {
    $("div#imgComment").remove();
};

// 最小高度
var minRows = 2;
// 最大高度，超过则出现滚动条
var maxRows = 20;
function autoResize() {
    var t = document.getElementById("insertComment");
    if (t.scrollTop == 0) t.scrollTop = 1;
    while (t.scrollTop == 0) {
        if (t.rows > minRows)
            t.rows--;
        else
            break;
        t.scrollTop = 1;
        if (t.rows < maxRows)
            t.style.overflowY = "hidden";
        if (t.scrollTop > 0) {
            t.rows++;
            break;
        }
    }
    while (t.scrollTop > 0) {
        if (t.rows < maxRows) {
            t.rows++;
            if (t.scrollTop == 0) t.scrollTop = 1;
        }
        else {
            t.style.overflowY = "auto";
            break;
        }
    }
}
