/**
 * User: likg
 * Date: 13-01-17
 * crop image.
 */
var imageCrop = {};
var chosenImg = null;
var parentImgList = null;
var isOtherFn = false;	//是非编译器功能
(function () {
    var g = $G;
    var jcropobj = {};
    var currentOper = 'crop';

    //初始化
    imageCrop.init = function () {
        setImageCropInfo(getCurrentImg());
    };
    var proportion = parseInt($("#imagePumpW").val()) / parseInt($("#imagePumpH").val());
    $('#lockPump').on('change', function () {
        proportion = parseInt($("#imagePumpW").val()) / parseInt($("#imagePumpH").val());
        $("#lockPump").attr('data-proportion', proportion);
    });

    //显示裁剪图片区域
    imageCrop.showCropArea = function (obj) {
        $(".imgcropbut").show();
        $("#remote").hide();
        $(obj).parent().find('input').attr('disabled', false);
        $(obj).attr('disabled', true);
        $('#imgPump').hide();
        $('#imgPumpParam').hide();
        $("#watermark").hide();
        $("#grayDiv").hide();
        $('#imgCrop').show();
        $('#imgCropParam').show();
        $("#rotateDiv").hide();
        $("#mosaic").hide();
        currentOper = 'crop';
    }

    //显示抽大小图区域
    imageCrop.showPumpArea = function (obj) {
        $(".imgcropbut").show();
        $("#remote").hide();

        $(obj).parent().find('input').attr('disabled', false);
        $(obj).attr('disabled', true);
        $('#imgCrop').hide();
        $('#imgCropParam').hide();
        $("#watermark").hide();
        $("#grayDiv").hide();
        $('#imgPump').show();
        $('#imgPumpParam').show();
        $("#rotateDiv").hide();
        $("#mosaic").hide();

        imageCrop.changePumpImgSize($('#imgPumpZoomSize'));
        currentOper = 'pump';
    }

    //改变抽图大小
    imageCrop.changePumpImgSize = function (obj) {
        var maxWidth = 780;
        var maxHeight = 485;
        var percent = Number($(obj).val());
        var oImgW = Number($('#oImagePumpW').val());
        var oImgH = Number($('#oImagePumpH').val());
        var zoomW = parseInt(oImgW * percent);
        var zoomH = parseInt(oImgH * percent);

        if (zoomW < maxWidth && zoomH < maxHeight) {
            $('#imgPumpBox').attr('width', zoomW);
            $('#imgPumpBox').attr('height', zoomH);
        } else {
            $('#imgPumpBox').attr('width', $('#imgCropBox').attr('width'));
            $('#imgPumpBox').attr('height', $('#imgCropBox').attr('height'));
        }
        $('#imagePumpW').val(zoomW);
        $('#imagePumpH').val(zoomH);
    }

    //手动输入抽图的宽度
    imageCrop.changeImgPumpW = function (obj) {
        var thisValue = $(obj).val();
        var oImgW = Number($('#oImagePumpW').val());
        if (thisValue == null || thisValue == '' || isNaN(thisValue)) {
            $(obj).val('');
        } else if (Number(thisValue) > oImgW) {
            $(obj).val(oImgW);
        }
        if ($('#lockPump').is(':checked')) {
            var proportion = $("#lockPump").attr('data-proportion');
            $('#imagePumpH').val(Math.round($(obj).val() / proportion) >= Number($('#oImagePumpH').val()) ? Number($('#oImagePumpH').val()) : Math.round($(obj).val() / proportion));
        }
        imageCrop.drawPreviewPumpImg();
    }
    //手动输入抽图的高度
    imageCrop.changeImgPumpH = function (obj) {
        var thisValue = $(obj).val();
        var oImgH = Number($('#oImagePumpH').val());

        if (thisValue == null || thisValue == '' || isNaN(thisValue)) {
            $(obj).val('');
            return;
        } else if (Number(thisValue) > oImgH) {
            $(obj).val(oImgH);
        }
        if ($('#lockPump').is(':checked')) {
            var proportion = $("#lockPump").attr('data-proportion');
            $('#imagePumpW').val(Math.round($(obj).val() * proportion) >= Number($('#oImagePumpW').val()) ? Number($('#oImagePumpW').val()) : Math.round($(obj).val() * proportion));
        }
        imageCrop.drawPreviewPumpImg();
    }
    //重新调整预览图的大小
    imageCrop.drawPreviewPumpImg = function () {
        var maxWidth = 780;
        var maxHeight = 485;
        var zoomW = parseInt($('#imagePumpW').val());
        var zoomH = parseInt($('#imagePumpH').val());

        if (zoomW < maxWidth && zoomH < maxHeight) {
            $('#imgPumpBox').attr('width', zoomW);
            $('#imgPumpBox').attr('height', zoomH);
        } else {
            $('#imgPumpBox').attr('width', $('#imgCropBox').attr('width'));
            $('#imgPumpBox').attr('height', $('#imgCropBox').attr('height'));
        }
    }

    /**
     * 绑定确认按钮
     */
    imageCrop.cropImgOk = function () {
        if (currentOper == 'crop') {
            imgCrop();
        } else if (currentOper == 'pump') {
            imgPump();
        }
    }

    /**
     * 绑定取消按钮
     */
    imageCrop.cropImgCancle = function () {
        dialog.close();
    }

    /**
     * 设置裁剪图片的信息
     */
    function setImageCropInfo(img) {
        //获取图片路径
        var _src = img.getAttribute("_src");
        if (!_src) _src = img.getAttribute("src");
        if (!_src) _src = img.src;

        if (!_src) return;

        var wordImgFlag = img.getAttribute("word_img");
        var imagePath = wordImgFlag ? wordImgFlag.replace("&amp;", "&")
            : (img.getAttribute('data_ue_src') || img.getAttribute("src", 2).replace("&amp;", "&"));
        if (isOtherFn) {
            imagePath = _src;
        }
        imagePath = CONTEXTPATH + imagePath.substr(imagePath.lastIndexOf("xy/image.do"));

        $('#imageCropPath').val(imagePath);
        $('#imgTitle').val(img.title || "");

        //网络图片不能裁剪,001先注销掉2015/04/28
        //var reg = new RegExp("^/data");
        //if(!reg.test(imagePath)) return false;
        //alert("2015/04/28");
        //加载图片
        var img = new Image();
        img.onload = function () {
            //设置显示原图大小
            g("oImagePumpW").value = this.width;
            g("oImagePumpH").value = this.height;
            showPreviewCropImage(this); //显示图片
            g("imageW").value = this.width;
            g("imageH").value = this.height;
        }
        img.src = imagePath;
        $("#imgCropBox").src = imagePath;
    }

    /**
     * 把裁剪后的图片保存到服务器，并在编辑器中显示
     */
    function imgCrop() {
        var param = {};
        var selectorW = Number($('#selectorW').val());
        var selectorH = Number($('#selectorH').val());
        if (selectorW == 0 || selectorH == 0) {
            alert('请选择裁剪区域！');
            return;
        }

        var orignalImageWidth = Number($('#oImagePumpW').val())
        var zipImageWidth = Number($('#imageW').val());
        var _rateW = zipImageWidth / orignalImageWidth;

        var orignalImageHeight = Number($('#oImagePumpH').val())
        var zipImageHeight = Number($('#imageH').val());
        var _rateH = zipImageHeight / orignalImageHeight;

        param.articleId = editor.currentArticleId;

        param.selectorX = Math.round(Number($('#selectorX').val()) / _rateW);
        param.selectorY = Math.round(Number($('#selectorY').val()) / _rateH);
        //结束的位置
        param.selectorW = Math.round(Number(selectorW) / _rateW);
        param.selectorH = Math.round(Number(selectorH) / _rateH);

        param.imageW = $('#imageW').val();
        param.imageH = $('#imageH').val();
        param.imagePath = decodeURI($('#imageCropPath').val());
        param.imgTitle = $('#imgTitle').val();
        param.command = "cut";
        $.get(CONTEXTPATH + "/xy/ueditor/cropImage.do", param, function (json) {
            var result = eval("(" + json + ")");
            _imagePath = result.imgPath;
            _imagePath = "../.." + _imagePath.substr(_imagePath.lastIndexOf("/xy"));

            if (isOtherFn) {
                window.parent.resetImageInfo(_imagePath);
            } else {
                //插入裁剪后的图片
                var imgObj = {};
                imgObj.src = _imagePath;
                imgObj.data_ue_src = _imagePath;
                imgObj._src = _imagePath;
                imgObj.width = selectorW;
                imgObj.height = selectorH;
                imgObj.style = "width:" + selectorW + "px;height:" + selectorH + "px;";
                insertImage(imgObj);
            }
            //关闭窗口
            imageCrop.cropImgCancle();
        });
    }

    /**
     * 把抽取的小图片保存到服务器，并在编辑器中显示
     */
    function imgPump() {
        var param = {};
        var imagePumpW = $('#imagePumpW').val();
        var imagePumpH = $('#imagePumpH').val();
        if (imagePumpW == '' || isNaN(imagePumpW) || Number(imagePumpW) == 0 || imagePumpH == '' || isNaN(imagePumpH) || Number(imagePumpH) == 0) {
            alert('请输入有效的图片大小！');
            return;
        }
        param.articleId = editor.currentArticleId;
        param.imagePumpW = imagePumpW;
        param.imagePumpH = imagePumpH;
        param.imagePath = $('#imageCropPath').val();
        param.imgTitle = $('#imgTitle').val();
        param.command = "zsize";
        $.get(CONTEXTPATH + "/xy/ueditor/cropImage.do", param, function (json) {
            var result = eval("(" + json + ")");
            _imagePath = result.imgPath;
            _imagePath = "../.." + _imagePath.substr(_imagePath.lastIndexOf("/xy"));

            if (isOtherFn) {
                window.parent.resetImageInfo(_imagePath);
            } else {
                //插入裁剪后的图片
                var imgObj = {};
                imgObj.src = _imagePath;
                imgObj.data_ue_src = _imagePath;
                imgObj._src = _imagePath;
                imgObj.width = imagePumpW;
                imgObj.height = imagePumpH;
                imgObj.border = 0;
                imgObj.style = "width:" + imagePumpW + "px;height:" + imagePumpH + "px;";
                var opt = {};
                opt.href = param.imagePath;
                opt.target = "_blank";
                //insertLinkImage(imgObj, opt);
                insertImage(imgObj);
            }
            //关闭窗口
            imageCrop.cropImgCancle();
        });
    }

    //插入图片
    function insertImage(imgObjs) {
        editor.fireEvent('beforeInsertImage', imgObjs);
        editor.execCommand("insertImage", imgObjs);
    }

    //插入带链接的图片
    function insertLinkImage(imgObjs, linkOpt) {
        editor.fireEvent('beforeInsertImage', imgObjs);
        editor.execCommand("insertImage", imgObjs);
        editor.execCommand("link", linkOpt);
    }

    /**
     * 图片缩放
     * @param img
     * @param maxW 最大宽度
     * @param maxH 最大高度
     */
    function scaleCropImg(img, maxW, maxH) {
        var width = 0, height = 0, percent, ow = img.width, oh = img.height;
        if (ow > maxW || oh > maxH) {
            if (ow / oh >= maxW / maxH) {
                if (width = ow - maxW) {
                    percent = (width / ow).toFixed(2);
                    img.height = oh - oh * percent;
                    img.width = maxW;
                }
            } else {
                if (height = oh - maxH) {
                    percent = (height / oh).toFixed(2);
                    img.width = ow - ow * percent;
                    img.height = maxH;
                }
            }
        }
    }

    /**
     * 将img显示在裁剪预览框
     * @param img
     */
    function showPreviewCropImage(img) {
        var maxWidth = 780;
        var maxHeight = 485;
        scaleCropImg(img, maxWidth, maxHeight);
        g("imgCropBoxDiv").innerHTML = '<img border=1 id="imgCropBox" src="' + img.src + '" width="' + img.width + '" height="' + img.height + '" />';
        g("imgPumpBoxDiv").innerHTML = '<img border=1 id="imgPumpBox" src="' + img.src + '" width="' + img.width + '" height="' + img.height + '" />';

        //$('#imgCropBox').Jcrop({
        //	onChange:   showCoords,
        //	onSelect:   showCoords
        //},function(){
        //	jcropobj = this;
        //});

        jcropobj = $.Jcrop('#imgCropBox', {
            onChange: showCoords,
            onSelect: showCoords
        });
    }

    //刷新裁剪区域
    imageCrop.refreshSelect = function () {
        var imageW = Number($('#imageW').val());
        var imageH = Number($('#imageH').val());
        var selectorX = $('#selectorX').val();
        var selectorY = $('#selectorY').val();
        var inputW = $('#selectorWShow').val();
        var inputH = $('#selectorHShow').val();
        var x1 = 0, y1 = 0;

        if (inputW == null || inputW == '' || inputH == null || inputH == '' || isNaN(inputW) || isNaN(inputH)) {
            return;
        }
        inputW = Number(inputW);
        inputH = Number(inputH);
        if (selectorX != null && selectorX != '' && Number(selectorX) + inputW <= imageW) {
            x1 = Number(selectorX);
        }
        if (selectorY != null && selectorY != '' && Number(selectorY) + inputH <= imageH) {
            y1 = Number(selectorY);
        }
        jcropobj.setSelect([x1, y1, x1 + inputW, y1 + inputH]);
        e.stopPropagation();
        e.preventDefault();
        return false;
    }

    /**
     * 回填裁剪信息
     * @param c
     */
    function showCoords(c) {
        jQuery('#selectorX').val(c.x);
        jQuery('#selectorY').val(c.y);
        jQuery('#selectorW').val(c.w);
        jQuery('#selectorH').val(c.h);
        jQuery('#selectorWShow').val(c.w);
        jQuery('#selectorHShow').val(c.h);
    };

})();


/* 图片 */
function RemoteImage(target) {
    this.container = utils.isString(target) ? document.getElementById(target) : target;
    this.init();
}
RemoteImage.prototype = {
    init: function () {
        this.initContainer();
        this.initEvents();
    },
    initContainer: function () {
        this.dom = {
            'url': $G('url'),
            'width': $G('width'),
            'height': $G('height'),
            'border': $G('border'),
            'vhSpace': $G('vhSpace'),
            'title': $G('title'),
            'align': $G('align')
        };
        this.setImage(getCurrentImg());
    },
    initEvents: function () {
        var _this = this,
            locker = $G('lock');

        /* 改变url */
        domUtils.on($G("url"), 'keyup', updatePreview);
        domUtils.on($G("border"), 'keyup', updatePreview);
        domUtils.on($G("title"), 'keyup', updatePreview);

        domUtils.on($G("width"), 'keyup', function () {
            if (locker.checked) {
                var proportion = locker.getAttribute('data-proportion');
                $G('height').value = Math.round(this.value / proportion);
            } else {
                _this.updateLocker();
            }
            updatePreview();
        });

        domUtils.on($G("height"), 'keyup', function () {
            if (locker.checked) {
                var proportion = locker.getAttribute('data-proportion');
                $G('width').value = Math.round(this.value * proportion);
            } else {
                _this.updateLocker();
            }
            updatePreview();
        });
        domUtils.on($G("lock"), 'change', function () {
            var proportion = parseInt($G("width").value) / parseInt($G("height").value);
            locker.setAttribute('data-proportion', proportion);
        });
        $G("lock").click();
        function updatePreview() {
            _this.setPreview();
        }
    },
    updateLocker: function () {
        var width = $G('width').value,
            height = $G('height').value,
            locker = $G('lock');
        if (width && height && width == parseInt(width) && height == parseInt(height)) {
            locker.disabled = false;
            locker.title = '';
        } else {
            locker.checked = false;
            locker.disabled = 'disabled';
            locker.title = lang.remoteLockError;
        }
    },
    setImage: function (img) {
        /* 不是正常的图片 */
        if (!img.tagName || img.tagName.toLowerCase() != 'img' && !img.getAttribute("src") || !img.src) return;

        var wordImgFlag = img.getAttribute("word_img"),
            src = wordImgFlag ? wordImgFlag.replace("&amp;", "&") : (img.getAttribute('_src') || img.getAttribute("src", 2).replace("&amp;", "&")),
            align = editor.queryCommandValue("imageFloat");
        $("#align").val(align);
        src = CONTEXTPATH + src.substr(src.lastIndexOf("xy/image.do"));

        /* 防止onchange事件循环调用 */
        if (src !== $G("url").value) $G("url").value = src;
        if (src) {
            /* 设置表单内容 */
            $G("width").value = img.width || '';
            $G("height").value = img.height || '';
            $G("border").value = img.getAttribute("border") || '0';
            $G("vhSpace").value = img.getAttribute("vspace") || '0';
            $G("title").value = img.title || img.alt || '';//去掉   写稿——图片修改——修改——描述中的默认值——王华英2015年9月22日
            this.setPreview();
            this.updateLocker();
        }
    },
    getData: function () {
        var data = {};
        for (var k in this.dom) {
            data[k] = this.dom[k].value;
        }
        return data;
    },
    setPreview: function () {
        var url = $G('url').value,
            ow = parseInt($G('width').value),
            oh = parseInt($G('height').value),
            border = $G('border').value,
            title = $G('title').value,
            preview = $G('preview'),
            width,
            height;

        if (ow >= oh) {
            width = ((!ow || !oh) ? preview.offsetWidth : Math.min(ow, preview.offsetWidth));
            width = width + (border * 2) > preview.offsetWidth ? width : (preview.offsetWidth - (border * 2));
            height = (!ow || !oh) ? '' : width * oh / ow;
        } else {
            height = ((!ow || !oh) ? preview.offsetWidth : Math.min(ow, preview.offsetWidth));
            height = height + (border * 2) > preview.offsetWidth ? height : (preview.offsetWidth - (border * 2));
            width = (!ow || !oh) ? '' : height * ow / oh;
        }

        if (url) {
            preview.innerHTML = '<img src="' + url + '" width="' + width + '" height="' + height + '" border="' + border + 'px solid #000" title="' + title + '" />';
        }
    },
    getInsertList: function () {
        var data = this.getData();
        if (data['url']) {
            data['url'] = "../../" + data['url'].substr(data['url'].lastIndexOf("xy/image.do"));

            return [{
                src: data['url'],
                _src: data['url'],
                width: data['width'] || '',
                height: data['height'] || '',
                border: data['border'] || '',
                floatStyle: data['align'] || '',
                vspace: data['vhSpace'] || '',
                title: data['title'] || '',
                alt: data['title'] || '',
                style: "width:" + data['width'] + "px;height:" + data['height'] + "px;"
            }];
        } else {
            return [];
        }

    },
    btnOk: function () {
        var list = remoteImage.getInsertList();

        if (list) {
            editor.execCommand('insertimage', list);
            remote && editor.fireEvent("catchRemoteImage");
        }

        //统一设置 img的alt和title属性
        if ($("#ssTitle:checked").size() > 0) {
            var imgs = editor.document.getElementsByTagName("img");
            for (var i = 0; ii = imgs[i++];) {
                if (ii.className != "kfformula") {
                    ii.title = document.getElementById("title").value;
                    ii.alt = document.getElementById("title").value;
                }
            }
        }

        imageCrop.cropImgCancle();

    },
    showModify: function (obj) {
        $(obj).parent().find('input').attr('disabled', false);
        $(obj).attr('disabled', true);
        $("#imgCrop").hide();
        $(".imgcropbut").hide();
        $("#imgPump").hide();
        $("#grayDiv").hide();
        $("#remote").show();
        $("#watermark").hide();
        $("#rotateDiv").hide();
        $("#mosaic").hide();
    }
};


/* 水印 */
function WaterMark(target) {
    //alert(target);
    //this.$wrap = target.constructor == String ? $('#' + target) : $(target);
    this.init();
}
WaterMark.prototype = {
    imgPath: "../../../../xy/image.do?path=", deviceName: "", orignalImgList: new Array(), //初始化
    font: ['宋体', '楷体', '黑体', '隶书', '微软雅黑', 'arial', 'arial black', 'monospace', 'Helvetica', 'Times', '仿宋', '幼圆', '新宋体'],
    fontsize: [5, 6, 7, 8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72],
    init: function () {
        //初始化透明度
        this.initOpacity();
        //初始化位置按钮
        this.initPositionBtn();

        //初始化图片上传组件
        this.initUploadify();

        this.initSameSetting();

        //初始化水印
        this.initWaterMark();
        //var srcImg = editor.selection.getRange().getClosedNode();
        //初始化稿件中的所有图片
        //this.initOrignalImage();
        this.initWordColor();
        this.initListener();
    },
    initListener: function () {
        //切换 图片水印与
        $("input[name=watermark_type]").click(function () {
            if ($(this).val() == "0") {
                $("#img_wmDiv").show();
                $("#word_wmDiv").hide();
            } else {
                $("#img_wmDiv").hide();
                $("#word_wmDiv").show();
            }
        });

        var _type = xy_cookie.getCookie("watermark_type");
        if (_type == "1") {
            $("#word_waterMark").click();
        } else {
            $("#img_waterMark").click();
        }

    },
    initWordColor: function () {
        var $word = $("#ww_word");

        var _word = xy_cookie.getCookie("dtword");
        _word = _word || "";
        $word.val(_word);

        //字体
        var $font = $("#ww_font");
        for (var c in this.font) {
            $font.append("<option>" + this.font[c] + "</option>");
        }
        $font.change(function (e) {
            $word.css("font-family", $(this).val());
        });

        var _font = xy_cookie.getCookie("dtfont");
        if (_font) {
            $font.val(_font);
            $word.css("font-family", _font);
        }

        //大小
        var $fontsize = $("#ww_fontsize");
        for (var c in this.fontsize) {
            $fontsize.append("<option>" + this.fontsize[c] + "</option>");
        }

        $fontsize.change(function (e) {
            $word.css("font-size", $(this).val() + "px");
        });

        var _fontsize = xy_cookie.getCookie("dtfontsize");
        if (_fontsize) {
            $fontsize.val(_fontsize);
            $word.css("font-size", _fontsize + "px");
        } else {
            $fontsize.val(20);
            $word.css("font-size", "20px");
        }

        var _color = xy_cookie.getCookie("dtcolor");

        _color = _color || "#000000";
        $word.css("color", _color);
        //颜色
        $("#word_color").colorpicker({
            color: _color,
            displayIndicator: false,
            history: false,
            strings: "主题颜色,标准颜色,其他"
        });
        $("#word_color").on("change.color", function (event, color) {
            $word.css("color", $("#word_color").val());
        });

        //加粗
        $("#ww_bold").change(function () {
            if ($(this).attr("checked")) {
                $word.css("font-weight", "bold");
            } else {
                $word.css("font-weight", "normal");
            }
        });

        var _bold = xy_cookie.getCookie("dtbold");
        if (_bold == 1) {
            $("#ww_bold").attr("checked", "checked");
            $word.css("font-weight", "bold");
        }

        //斜体
        $("#ww_italic").change(function () {
            if ($(this).attr("checked")) {
                $word.css("font-style", "italic");
            } else {
                $word.css("font-style", "normal");
            }
        });

        var _italic = xy_cookie.getCookie("dtitalic");
        if (_italic == 2) {
            $("#ww_italic").attr("checked", "checked");
            $word.css("font-style", "italic");
        }

    },
    initSameSetting: function () {
        var _sameSettings = xy_cookie.getCookie("sameSettings") || 0;

        $("#sameSettings_" + _sameSettings).attr("checked", "checked");

    },
    //初始化透明度
    initOpacity: function () {
        var _opacity = xy_cookie.getCookie("opacity");
        var _initVal = _opacity || 100;
        $("#opacity").val(_initVal);
        //初始化滑动组件
        $('#h-slider').slider({
            range: "min", min: 0, max: 100, value: _initVal, slide: function (event, ui) {
                $("#opacity").val(ui.value);
            }

        });
    }, //初始化位置按钮
    initPositionBtn: function () {
        //点击按钮后：1. 按钮变色；2. 给隐藏的position赋值；
        $("input:button[id^=position]").click(function () {
            //1. 按钮变色；
            $("input:button[id^=position]").removeClass("positionBtn");
            $(this).addClass("positionBtn");
            //2. 给隐藏的position赋值；
            $("#position").val($(this).attr("data-position"));
        });
        var _id = xy_cookie.getCookie("position");
        //如果改过位置，就初始化位置
        if (_id) {
            $("#position_" + _id).click();
        } else {
            //默认点击右下角的按钮
            $("input:button[id^=position]:last").click();
        }

    }, //初始化上传文件空间
    initUploadify: function () {
        var _this = this;
        //上传按钮
        $("#uploadFile").uploadify({
            "buttonClass": "uploadbtnStyle",
            "buttonText": "",
            'height': 110,
            'width': 110,
            "method": "post",
            'swf': '../../../../e5script/jquery/uploadify/uploadify.swf',
            'uploader': '../../../../xy/ueditor/uploadwatermark.do?siteID=' + $(window.parent.document.getElementById("a_siteID")).val(),
            'auto': true,
            "removeTimeout": 0,
            'fileObjName': 'file',
            "queueID": "uploadFileQueue",
            "multi": false,
            "onSelect": function () {
                $("#queueLi").show();
            },
            'onUploadSuccess': function (file, data) {
                var _json = eval("(" + data + ")");
                var _imgPath = _this.imgPath + _json.imagePath;
                $("#queueLi").hide();
                _this._addImg("#wmUl", _imgPath);
                $("#wmUl li").click(function () {
                    $(this).siblings().removeClass("chosen");
                    $(this).addClass("chosen");
                });


            }
        });
    }, _addImg: function (_id, _imgPath) {
        var _this = this;
        var _html = new Array();
        _html.push('<li style="float: left;margin-left: 10px; cursor: pointer;">');
        _html.push('<div class="form-inline">');
        _html.push('<div class="thumbnail" style="width: 110px;height: 110px;margin-bottom: 0px;position:relative;background-color: rgba(204,204,204,0.5);">');
        if(window.parent.article && window.parent.article.isAdmin && window.parent.article.isAdmin != 'false'){
            _html.push('<a class="close" herf="#" style="position: absolute;top: 2px;right:4px;opacity: 0.2;">×');
            _html.push('</a>');
        }
        _html.push('<span class="wrapimg">');
        _html.push('<img style="max-width:100px;max-height:90px;" src="' + _imgPath + '">');
        //_html.push('<span class="glyphicon glyphicon-remove closes"></span>');
        _html.push('</span>');
        _html.push('<input id="picUrl" name="picUrl" type="hidden"/>');
        _html.push('</div>');
        _html.push('</div>');
        _html.push('</li>');
        //$("#queueLi").before(_html.join(""));
        // $(_id).append(_html.join(""));
        $("#watermarkPickerBlock").before(_html.join(""));

        //水印删除的点击事件
        $("#wmUl .close").unbind('click').click(_this.wmcloseClick);

    }, //初始化水印
    initWaterMark: function () {
        var _this = this;
        $.ajax({
            async: true,
            url: "../../../../xy/ueditor/initwatermark.do",
            type: 'POST',
            data: { siteID: $(window.parent.document.getElementById("a_siteID")).val() },
            dataType: 'json',
            success: function (data, status) {
                // 根据参数isAdmin判断是否为管理员，如果为true则显示水印上传，如果为false，则隐藏水印上传功能
            	/*if(data.isAdmin == false) {
            		$("#watermarkPickerBlock").hide();
            	}*/

                // 获取到扩展字段就展示， 否则就不展示
                if (data.status == "success") {
                    _this.deviceName = data.deviceName;
                    var _array = data.result;
                    //水印列表
                    for (var _i = 0, _size = _array.length; _i < _size; _i++) {
                        _this._addImg("#wmUl", _this.imgPath + _array[_i]);
                    }
                    //水印的点击事件
                    $("#wmUl li").click(_this.wmImgClick);

                    //水印删除的点击时间
                    //$("#wmUl .close").one.click(_this.wmcloseClick);

                    $("#wmUl .close").unbind('click').click(_this.wmcloseClick);

                    //查询cookie,拿到用户之前选择的水印图片
                    var _img = xy_cookie.getCookie("watermark");
                    if (_img) {
                        $("#wmUl img").each(function () {
                            _src = this.src + "";
                            if (_src.indexOf(_img) != -1) {
                                $(this).click();
                            }
                        });
                    } else {
                        $("#wmUl li:first").click();
                    }

                    // $("#watermarklocation").html("服务器地址：" + data.IP + "；&nbsp;&nbsp;文件夹：" + data.device + "；");

                } else {
                    bResult = false;
                }
            },
            error: function (xhr, textStatus, errorThrown) {
                alert("对不起，无法初始化水印！");
            }
        });
    }, //显示水印模块
    showWaterMark: function (_btn) {
        $(_btn).siblings().attr('disabled', false);
        $(_btn).attr('disabled', true);
        $("#imgCrop").hide();
        $(".imgcropbut").hide();
        $("#imgPump").hide();
        $("#remote").hide();
        $("#grayDiv").hide();
        $("#watermark").show();
        $("#rotateDiv").hide();
        $("#mosaic").hide();
    }, //水印图片的点击事件
    wmImgClick: function () {
        //添加选择效果
        $(this).siblings().removeClass("chosen");
        $(this).addClass("chosen");
    },
    wmcloseClick: function () {

        if (confirm("你确定要删除这个水印文件吗？删除后别人将无法使用！")) {

            var obj = $(this);
            var imgSrc = $(this).next().find('img').attr('src');
            $.ajax({
                async: true,
                url: "../../../../xy/ueditor/deletewatermark.do",
                type: 'POST',
                data: { path: imgSrc },
                dataType: 'json',
                success: function (data, status) {
                    obj.parent().parent().parent().hide(500);
                },
                error: function () {
                    alert("对不起，无法删除！");
                }
            });
        }
    },
    watermarkConfirm: function () {
        //记录cookie
        this.doCookie();
        this.createWaterMark();

    },
    initOrignalImage: function () {
        var _srcImg = chosenImg.getAttribute("_src") || editor.selection.getRange().getClosedNode().src;
        //显示编译器中所有的图片
        var _imgList = editor.getContentPics();
        for (_i in _imgList) {
            //_imgList[_i].path && this._addImg("#orignalImageUl", "../../" + _imgList[_i].path);
            if (_imgList[_i].path) {
                this._addImg("#orignalImageUl", "../../" + _imgList[_i].path);
                this.orignalImgList.push(_imgList[_i].path);
            }

        }
        //把当前图片选中
        if (_srcImg) {
            alert('_srcImg')
            _srcImg = _srcImg.substr(_srcImg.lastIndexOf("/") + 1);
            $("#orignalImageUl li").click(this.wmImgClick);
            $("#orignalImageUl img").each(function () {
                _src = this.src + "";
                if (_src.indexOf(_srcImg) != -1) {
                    $(this).click();
                }
            });

        }
    }, //为图片创建水印
    /**
     * 创建水印的两种方式：
     * 1. 创建当前选中图片的水印
     *        按照以前的路线走，直接替换选中部分
     * 2. 创建本篇文章中所有图片的水印
     *        2.1 发送到服务器本文章的所有图片列表
     *        2.2 替换所有图片的src
     *
     */
    createWaterMark: function () {
        //参数
        var param = {};
        param.command = "watermark";
        if (isOtherFn) {
            param.imagePath = chosenImg.getAttribute("_src")
        } else {
            param.imagePath = editor.selection.getRange().getClosedNode().src
        }

        param.watermark = $("#wmUl .chosen").find("img").attr("src");
        param.transparency = parseInt($("#opacity").val()) / 100;
        param.position = $("#position").val();
        //判断是否是使用相同的设置
        var _isSameSettings = $("input:radio[name^=sameSettings]:checked").val() + "" == "1" ? true : false;
        var imgs = editor.document.getElementsByTagName("img");
        if (_isSameSettings) {
            if (!isOtherFn) {
                //如果文章当中的图片（包括所选图片）大于1
                if (imgs.length > 1) {
                    var imgSrcs = new Array();
                    for (var _i = 0, _size = imgs.length; _i < _size; _i++) {
                        imgSrcs.push(imgs[_i].src);
                    }
                    param.imgList = JSON.stringify(imgSrcs);
                    param.command = "batchwm";
                } else {
                    _isSameSettings = false;
                }
            } else {
                //其他地方引用图片修改
                if (parentImgList && parentImgList.size() > 1) {
                    var imgSrcs = new Array();
                    _$imgs = parentImgList.find(".picfather").find("input");
                    for (var _i = 0, _size = _$imgs.size(); _i < _size; _i++) {
                        imgSrcs.push(_$imgs.eq(_i).val());
                    }
                    param.imgList = JSON.stringify(imgSrcs);
                    param.command = "batchwm";
                } else {
                    _isSameSettings = false;
                }
            }
        }
        param.isSameSetting = _isSameSettings;

        if ($("input[name=watermark_type]:checked").val() == "1") {
            param.command = "drawtext";
            param.content = $("#ww_word").val();
            param.color = $("#word_color").val();
            param.fontName = $("#ww_font").val();
            param.fontSize = $("#ww_fontsize").val();

            param.fontStyle = parseInt($("input[id=ww_bold]:checked").val() || 0) + parseInt($("input[id=ww_italic]:checked").val() || 0);
        }

        //访问后台
        $.post(CONTEXTPATH + "/xy/ueditor/cropImage.do", param, function (json) {
            //获得结果路径
            var result = eval("(" + json + ")");

            if (result.status == "failure") {
                alert("您暂未选择添加的水印图片，请选择后进行添加水印操作！");
                return;
            }


            //只修改选中图片的水印
            if (!_isSameSettings) {
                //获得图片的url路径
                _imagePath = decodeURI(result.imgPath);
                _imagePath = "../.." + _imagePath.substr(_imagePath.lastIndexOf("/xy"));
                if (isOtherFn) {
                    window.parent.resetImageInfo(_imagePath);
                } else {
                    //插入裁剪后的图片
                    var imgObj = {};
                    imgObj.src = _imagePath;
                    imgObj.data_ue_src = _imagePath;
                    imgObj._src = _imagePath;
                    imgObj.border = 0;
                    editor.fireEvent('beforeInsertImage', imgObj);
                    editor.execCommand("insertImage", imgObj);
                }
            } else {
                if (isOtherFn) {
                    //获得图片的url路径
                    window.parent.resetImageListInfo(result.imgList);
                } else {
                    //批量处理所有图片
                    _imgList = result.imgList;
                    if (_imgList) {
                        //替换
                        for (var _i = 0, _size = _imgList.length; _i < _size; _i++) {
                            //从返回的列表中选一个
                            _imagePath = decodeURI(_imgList[_i]);
                            _imagePath = "../.." + _imagePath.substr(_imagePath.lastIndexOf("/xy"));
                            for (var _j = 0, _ize = imgs.length; _j < _ize; _j++) {
                                //获得文章中的文件名
                                _name = imgs[_j].src;
                                _name = _name.substring(_name.lastIndexOf("/") + 1, _name.lastIndexOf("."));
                                //看是否在里面
                                if (_imagePath.indexOf(_name) != -1) {
                                    imgs[_j].src = _imagePath;
                                    imgs[_j].setAttribute("_src", _imagePath);
                                    imgs[_j].setAttribute("data_ue_src", _imagePath);
                                    break;
                                }

                            }
                        }
                    }
                }
            }
            //关闭窗口
            imageCrop.cropImgCancle();
        }, "text");



    },
    //用cookie记录用户的行为
    doCookie: function () {

        //设置cookie
        var _img = $("#wmUl .chosen").find("img").attr("src");
        _img && (_img = _img.substr(_img.lastIndexOf(";") + 1));

        xy_cookie.setCookie("watermark", _img);
        xy_cookie.setCookie("opacity", $("#opacity").val());
        xy_cookie.setCookie("position", $("#position").val());
        xy_cookie.setCookie("sameSettings", ($("input:radio[name^=sameSettings]:checked").val() + ""));

        xy_cookie.setCookie("watermark_type", $("input[name=watermark_type]:checked").val());

        xy_cookie.setCookie("dtword", $("#ww_word").val());
        xy_cookie.setCookie("dtfont", $("#ww_font").val());
        xy_cookie.setCookie("dtfontsize", $("#ww_fontsize").val());
        xy_cookie.setCookie("dtcolor", $("#word_color").val());
        xy_cookie.setCookie("dtbold", $("#ww_bold").is(":checked") ? $("#ww_bold").val() : 0);
        xy_cookie.setCookie("dtitalic", $("#ww_italic").is(":checked") ? $("#ww_italic").val() : 0);
    }
};


function ToGray(target) {
    //this.init(target);
};
ToGray.prototype = {
    init: function (target) {
        var img = editor.selection.getRange().getClosedNode();
        var _src = target || img.src;
        if (img) {
            this.setImage(_src);
        } else {
            this.setImage(_src);
        }
    },
    showToGray: function (_btn) {
        $(_btn).siblings().attr('disabled', false);
        $(_btn).attr('disabled', true);
        $("#imgCrop").hide();
        $(".imgcropbut").hide();
        $("#imgPump").hide();
        $("#remote").hide();
        $("#watermark").hide();
        $("#grayDiv").show();
        $("#rotateDiv").hide();
        $("#mosaic").hide();
    },
    setImage: function (imgSrc) {
        var preview = $G('grayPreviewDiv');
        var grayContainer = $G('grayContainer');
        if (url) {
            preview.innerHTML = '<img src="' + imgSrc + '" id="grayImg" class="gray" style="max-width: 778px; max-height: 498px;"  border="1px solid #000"   />';
        }
    },
    toGrayConfirm: function () {
        var param = {};
        param.imagePath = $('#grayImg').attr('src');
        param.command = "gray";
        $.post(CONTEXTPATH + "/xy/ueditor/cropImage.do", param, function (json) {
            var result = eval("(" + json + ")");
            _imagePath = result.imgPath;
            _imagePath = "../.." + _imagePath.substr(_imagePath.lastIndexOf("/xy"));

            if (isOtherFn) {
                window.parent.resetImageInfo(_imagePath);
            } else {
                //插入裁剪后的图片
                var imgObj = {};
                imgObj.src = _imagePath;
                imgObj.data_ue_src = _imagePath;
                imgObj._src = _imagePath;
                editor.fireEvent('beforeInsertImage', imgObj);
                editor.execCommand("insertImage", imgObj);
            }

            //关闭窗口
            imageCrop.cropImgCancle();
        }, 'text');
    }
};

function Rotate() {

}


Rotate.prototype = {
    init: function (target) {
        var img = editor.selection.getRange().getClosedNode();
        var _src = target || img.src;
        var that = this;
        if (img) {
            this.setImage(_src);
        } else {
            this.setImage(_src);
        }

        $("input[name=rotateRate]").click(function () {
            $("#rotateImg").attr("class", "");
            $("#rotateImg").addClass("_" + $(this).val());
            that.rotateImgParentDivAdapt("rotateImg");
        });
        $("input[name=rotateRate]:first").click();


    },
    rotateImgParentDivAdapt: function (el) {  // 旋转图片时，让父元素div高度随着图片自适应  参数el为图片的id
        var test = document.querySelector("#" + el);
        var rect = test.getBoundingClientRect();
        if (rect.height - rect.width > 0) {

            // alert('width:' + rect.width + 'height:' + rect.height);
            $("#" + el).parent().height(rect.height)
            $("#" + el).css({
                "position": "absolute",
                "top": Math.abs(((document.getElementById(el).clientHeight - document.getElementById(el).clientWidth) / 2)),
                "left": ((document.body.clientWidth) / 2 - (rect.height) / 2)
            });
        } else {
            $("#" + el).parent().css({
                "height": "inherit"
            })
            $("#" + el).css({
                "position": "static"
            });
        }
    },
    setImage: function (imgSrc) {
        var preview = $G('rotatePreviewDiv');
        var rotateContainer = $G('rotateContainer');
        if (url) {
            preview.innerHTML = '<img src="' + imgSrc + '" id="rotateImg" class="" style="max-width: 450px; max-height: 480px; text-align: center;"  border="1px solid #000"   />';
        }
    },
    showRotate: function (_btn) {
        $(_btn).siblings().attr('disabled', false);
        $(_btn).attr('disabled', true);
        $("#imgCrop").hide();
        $(".imgcropbut").hide();
        $("#imgPump").hide();
        $("#remote").hide();
        $("#watermark").hide();
        $("#grayDiv").hide();
        $("#rotateDiv").show();
        $("#mosaic").hide();

    },
    rotateConfirm: function () {
        var imagePumpW = $('#rotateImg').width();
        var imagePumpH = $('#rotateImg').height();
        var param = {};
        param.imagePath = $('#rotateImg').attr('src');
        param.command = "rotate";
        param.rotate = $("input[name=rotateRate]:checked").val();
        $.post(CONTEXTPATH + "/xy/ueditor/cropImage.do", param, function (json) {
            var result = eval("(" + json + ")");
            _imagePath = result.imgPath;
            _imagePath = "../.." + _imagePath.substr(_imagePath.lastIndexOf("/xy"));

            if (isOtherFn) {
                window.parent.resetImageInfo(_imagePath);
            } else {
                //插入裁剪后的图片
                var imgObj = {};
                imgObj.src = _imagePath;
                imgObj.data_ue_src = _imagePath;
                if ($("input[name=rotateRate]:checked").val() == 90 || $("input[name=rotateRate]:checked").val() == 270) {
                    imgObj.style = "width:" + imagePumpH + "px;height:" + imagePumpW + "px;";
                } else {
                    imgObj.style = "width:" + imagePumpW + "px;height:" + imagePumpH + "px;";
                }
                imgObj._src = _imagePath;
                editor.fireEvent('beforeInsertImage', imgObj);
                editor.execCommand("insertImage", imgObj);
            }

            //关闭窗口
            imageCrop.cropImgCancle();
        }, 'text');
    }
}

/*
 function changeDiv(obj){
 $("input[name=tagBtn]").not(obj).each(function(){
 $(this).attr('disabled', false);
 });
 $(obj).attr('disabled', true);
 }
 */


function getCurrentImg() {
    var __img = chosenImg || editor.selection.getRange().getClosedNode();
    return __img;
}

//马赛克
function Mosaic(src) {
    this.img = editor.selection.getRange().getClosedNode();
    this.src = src || this.img.src;
    this.cvs = document.getElementById('cvs-mosaic');
    this.ctx = this.cvs.getContext('2d');
    this.z = $('.imgmosaic-left input:checked').val();
    this.flag = true;
    this.init();
}
Mosaic.prototype = {
    init: function () {
        //canvas绘图
        this.setMosaic();
        $('#btnDiv').on('click', '.mosaic', this.showMosaic);
        $('#mosaic').on('click', '.imgmosaic-vote', this.setVote.bind(this));
        //e.which === 1 表示 点击鼠标左键 下面意思是 点击鼠标左键 绑定 鼠标移动事件 松开左键 解绑鼠标移动事件
        $(this.cvs).on({
            click: this.cvsClick.bind(this),
            mousedown: this.cvsMouseDown.bind(this),
            mouseup: this.cvsMouseUp.bind(this),
            mouseenter: this.cvsMouseEnter,
            mouseleave: this.cvsMouseLeave
        });
    },
    showMosaic: function () {
        $(this).attr('disabled', true).siblings().attr('disabled', false);
        $('#mosaic').show().siblings('.mosaicMsk').hide();
    },
    //获取img
    setMosaic: function () {
        var aImg = new Image();
        aImg.src = this.src;
        aImg.onload = function (e) {
            this.mosaicDraw(e.target, aImg.width, aImg.height);
        }.bind(this);
    },
    //canvas绘图
    mosaicDraw: function (obj, width, height) {
        this.cvs.width = $('#preview').width();
        this.cvs.height = (height * this.cvs.width) / width;
        this.ctx.drawImage(obj, 0, 0, this.cvs.width, this.cvs.height);
    },
    mosaicXY: function (x, y, z) {
        //获取图像的局部坐标的部分像素
        var oImg = this.ctx.getImageData(x - z / 2, y - z / 2, z, z);
        var w = oImg.width;
        var h = oImg.height;
        //马赛克的程度，数字越大越模糊
        var num = 10;
        //等分画布
        var stepW = w / num;
        var stepH = h / num;
        //这里是循环画布的像素点
        for (var i = 0; i < stepH; i++) {
            for (var j = 0; j < stepW; j++) {
                //获取一个小方格的随机颜色，这是小方格的随机位置获取的
                var color = this.getXY(oImg, j * num + Math.floor(Math.random() * num), i * num + Math.floor(Math.random() * num));
                //这里是循环小方格的像素点，
                for (var k = 0; k < num; k++) {
                    for (var l = 0; l < num; l++) {
                        //设置小方格的颜色
                        this.setXY(oImg, j * num + l, i * num + k, color);
                    }
                }

            }
        }
        this.ctx.putImageData(oImg, x - z / 2, y - z / 2);
    },
    //获取颜色
    getXY: function (obj, x, y) {
        var w = obj.width;
        var h = obj.height;
        var d = obj.data;
        var color = [];
        color[0] = obj.data[4 * (y * w + x)];
        color[1] = obj.data[4 * (y * w + x) + 1];
        color[2] = obj.data[4 * (y * w + x) + 2];
        color[3] = obj.data[4 * (y * w + x) + 3];
        return color;
    },
    //设置颜色
    setXY: function (obj, x, y, color) {
        var w = obj.width;
        var h = obj.height;
        var d = obj.data;
        obj.data[4 * (y * w + x)] = color[0];
        obj.data[4 * (y * w + x) + 1] = color[1];
        obj.data[4 * (y * w + x) + 2] = color[2];
        obj.data[4 * (y * w + x) + 3] = color[3];
    },
    //各种事件
    cvsClick: function (e) {
        var x = e.pageX - $(e.target).offset().left;
        var y = e.pageY - $(e.target).offset().top;
        var z = this.z;
        this.mosaicXY(x, y, z);
    },
    cvsMouseDown: function (e) {
        if (e.which === 1) {
            this.z = $('.imgmosaic-left input:checked').val();
            $(e.target).on('mousemove', this.cvsMouseMove.bind(this));
        }
    },
    cvsMouseUp: function (e) {
        if (e.which === 1) {
            $(e.target).off('mousemove', this.cvsMouseMove.bind(this));
        }
    },
    cvsMouseMove: function (e) {
        var x = e.pageX - $(e.target).offset().left;
        var y = e.pageY - $(e.target).offset().top;
        var z = this.z;
        if (e.which === 1) {
            if (!this.flag) return false;
            this.flag = false;
            setTimeout(function () {
                this.mosaicXY(x, y, z);
                this.flag = true;
            }.bind(this), 30);
        }
    },
    cvsMouseEnter: function (e) {
        $(e.target).css('cursor', 'pointer');
    },
    cvsMouseLeave: function (e) {
        $(e.target).css('cursor', 'auto');
    },
    //设置确定和取消按钮
    setVote: function (e) {
        switch (e.target.className) {
            case 'imgmosaic-no':
                dialog.close();
                break;
            case 'imgmosaic-yes':
                this.mosaicConfirm();
                break;
        }
    },
    //确定发送数据
    mosaicConfirm: function () {
        $.post(CONTEXTPATH + "xy/ueditor/Controller.do?action=uploadscrawl", {
            upfile: this.cvs.toDataURL("image/png").substring(22)
        }, function (data) {
            if (data) {
                if (data.state === 'SUCCESS') {
                    var _imagePath = '/xy/image.do?path=' + data.url.replace(/.JPG/g, '.jpg');
                    _imagePath = "../.." + _imagePath;
                    if (isOtherFn) {
                        window.parent.resetImageInfo(_imagePath);
                    } else {
                        //插入马赛克后的图片
                        var imgObj = {
                            src: _imagePath,
                            data_ue_src: _imagePath,
                            _src: _imagePath
                        };
                        editor.fireEvent('beforeInsertImage', imgObj);
                        editor.execCommand("insertImage", imgObj);
                    }
                    //关闭窗口
                    dialog.close();
                }
            }
        }, 'json');
    }
}
