/**
 * Created by isaac on 16-4-13.
 */
(function(window, $, LE){
    LE.stylesheets["Pic"] = function(){
        var $panel = $("#picSection");
        var $target = null;
        var $addBtn = $("#addPic");
        var $resetBtn = $("#resetPic");
        var $editBtn = $("#editPic");
        var $link = $("#picLinkUrlText");
        var $picLinkDialog = $("#picLinkDialog");
        var $toLocalPic = $("#toLocalPic");
        var $wh = $(".picWH");
        var $lock = $("#sidebar-panel").find(".pic-ratio");


        //不保持比例，那只需要考虑，宽是否大于容器的宽就可以了
        function resetImageWH(_$this){
            //容器的宽
            if(_$this.attr("data-ref") == "width"){
                var _cw = parseInt(LECurrentObject.width());
                var _dw = parseInt(_$this.val());

                if(_dw > _cw){
                    _dw = _cw;
                }

                LECurrentObject.find(".ui-wrapper").css({
                    width: _dw
                });
                LECurrentObject.find(".le-image_img").css({
                    width: _dw
                });
            } else{
                var _dh = parseInt(_$this.val());
                LECurrentObject.find(".ui-wrapper").css({
                    height: _dh
                });
                LECurrentObject.find(".le-image_img").css({
                    height: _dh
                });
            }


        }

        var initWH = function(){
            $wh.keydown(function(e){
                LEKey.numInputKeyDown(e, $(this), LECurrentObject.find(".le-image_img"));
                if(event.keyCode == 38 || event.keyCode == 40 || event.keyCode == 13){
                    var islocked = $lock.hasClass("select");
                    if(islocked){
                        resetImageWH_keepRadio($(this));
                    } else{
                        resetImageWH($(this));
                    }
                    resetWH();
                }
            });
            $wh.blur(function(e){
                // 防止多次重复操作
                var item = document.activeElement.id;
                var _thisId = e.currentTarget.id;
                if(item && item===_thisId){
                    return;
                }
                LEKey.numInputBlur(e, $(this), LECurrentObject.find(".le-image_img"));
                var islocked = $lock.hasClass("select");
                if(islocked){
                    resetImageWH_keepRadio($(this));
                } else{
                    resetImageWH($(this));
                }
                resetImageRadio();
                resetWH();
                LEHistory.trigger();
            });
        };


        /**
         * 设置图片的另外一个值，以保持比例
         * 1. 算出，另外一个值
         * @param $this
         */
        var resetImageWH_keepRadio = function(_$this){
            var _radio = parseFloat(LECurrentObject.find(".le-image_img").attr("data-radio"));
            /**
             * 如果是height的话，需要修改高, 但是高不能大于容器
             */
            var _dh, _dw;
            var _cw = parseInt(LECurrentObject.width());
            if(_$this.attr("data-ref") == "height"){
                //容器的高度
                //组件的高
                _dh = parseInt(_$this.val());
                _dw = (_dh * _radio).toFixed(4);

                //如果，组件的宽大于容器的，还是需要使用容器的宽
                if(_cw < _dw){
                    //获得容器的宽
                    _cw = parseInt(LECurrentObject.width());
                    _dw = _cw;
                    _dh = (_dw / _radio).toFixed(4);
                } else{
                    _dw = (_dh * _radio).toFixed(4);
                }

            } else{
                //设置宽
                _dw = parseInt(_$this.val());
                _dh = (_dw / _radio).toFixed(4);
                //如果图片的宽度大于容器宽度的时候
                if(_cw < _dw){
                    //获得容器的宽
                    _cw = parseInt(LECurrentObject.width());
                    _dw = _cw;
                    _dh = (_dw / _radio).toFixed(4);
                }


            }
            setImageWH(_dw, _dh);
        };


        function setImageWH(_dw, _dh){
            LECurrentObject.find(".ui-wrapper").css({
                width: _dw,
                height: _dh
            });
            LECurrentObject.find(".le-image_img").css({
                width: _dw,
                height: Math.round(_dh)
            });
        }


        var initAddLinkEvent = function(){
            $("#linkPic").bind("click", function(){
                $picLinkDialog.modal("show");
            });
            $("#picLinkClose").click(function(){
                $picLinkDialog.modal("hide");
            });
            $("#picLinkUrlConfirm").click(function(){
                var _url = $("#picLinkUrlText").val();
                var img = LECurrentObject.find("img");
                if(img.parent()[0].tagName != "A"){
                    img.wrap('<a href=' + _url + ' ' +
                        ($("#openNewWindowBtn1").is(":checked")?'target="_blank"': "") +
                        '></a>');
                } else if(img.parent()[0].tagName == "A"){
                    img.parent().attr("href", _url)
                }
                //alert(img.parent()[0].tagName=="A");
                $picLinkDialog.modal("hide");
            });
            $("#picLinkUrlText").keypress(function(e){
                if(e.keyCode == 13){
                    var _url = $("#picLinkUrlText").val();
                    var img = LECurrentObject.find("img");
                    if(img.parent()[0].tagName != "A"){
                        img.wrap('<a href=' + _url + ' target="_blank"></a>');
                    } else if(img.parent()[0].tagName == "A"){
                        img.parent().attr("href", _url)
                    }
                    $picLinkDialog.modal("hide");
                }
            });
        };

        var initBtnsEvent = function(){
            $addBtn.click(function(){
                if(LE.canChooseImg){
                    chooseImage();
                    // LEHistory.trigger();
                }else{
                    alert("网络有点慢，请等待上一张上传完毕！")
                }

            });
            $resetBtn.click(function(e){
                if(LE.canChooseImg){
                    var _style=LECurrentObject.find(".le-image_img").attr("style");
                    var obj=$(this);
                    chooseImage(obj,_style);
                    // LEHistory.trigger();
                }else{
                    alert("网络有点慢，请等待上一张上传完毕！")
                }
            });
            $editBtn.click(function(e){
                var data = [];
                data.push(LECurrentObject.find(".le-image_img").attr("src"));
                var timeStamp = new Date().getTime();
                var imgWidth = parseInt(LECurrentObject.find("img").css("width"));
                var imgHeight = parseInt(LECurrentObject.find("img").css("height"));
                var ratio = imgWidth / imgHeight;
                LEDialog.toggleDialog(LE.options["Dialog"].picEditDialog + "ratio=" + ratio + "&timestamp=" + timeStamp, function(imgList){
                        if(imgList.length > 0){
                            //LECurrentObject.css("backgroundImage", "url(" + imgList[0].path + ")");
                            LECurrentObject.find(".le-image_img").attr("src", imgList[0].path);
                            var _$obj = LECurrentObject.find(".le-image_img");
                            LEHistory.trigger();
                            LECurrentObject.trigger("click");
                        }
                    },
                    data
                );
                // LEHistory.trigger();
            });

            $("#container").on({
                dblclick: function(e){
                    var _$this = $(this);
                    LEReset.resizeImage(_$this);
                }
            }, ".le-image_img");
        };


        function chooseImage(obj,_style){
            LEDialog.toggleDialog(LE.options["Dialog"].picUploadDialog,
                function(jsonUploadImg){
                //上传过程中禁止上传下一张，避免组件初始化失败
                    LE.canChooseImg=false;
                    var url = jsonUploadImg.imgPath;
                    var type = jsonUploadImg.type;
                    var _thisObj=LECurrentObject;
                    _thisObj.html(LE.options["Image"].imageHtml);

                    _thisObj.find(".le-image_img").css("max-width", LECurrentObject.css("width"));
                    _thisObj.find(".le-image_img").attr("src", url);
                    _thisObj.find(".le-image_img").attr("data-type", type);

                    _thisObj.find(".le-image_img").load(function(){
                        var _$this = $(this);
                        var _src = _$this.attr("src");
                        if(!_src || _src.indexOf("noimg") != -1 ){
                            return;
                        }
                        var _w = _$this.parent().parent().css("width");
                        _$this.css("width", "");
                        _$this.css("height", "");
                        _$this.css("max-width", _w);
                        //重新选择图片后，保持原图大小
                        if(obj){
                            _thisObj.find(".le-image_img").attr("style",_style);
                        }
                        var islocked = $lock.hasClass("select");
                        refreshImageResizable(islocked,_thisObj);
                        //LEReset.resizeImage(_$this);
                        _thisObj.trigger("click");
                        //上传成功后允许上传下一张
                        LE.canChooseImg=true;
                        // 图片加载出来后再保存该次操作的记录;
                        LEHistory.trigger();
                    });
                    LEReset.changeColumnBoxHeight();
                    // LEHistory.trigger();

                }
            );
        }

        var initLinkEvent = function(){


        };
        var initToLocalEvent = function(){
            $toLocalPic.click(function(){
                var param = {};
                param.siteID = parent.window.special.siteID;
                param.docID = parent.window.special.docID;
                param.docLibID = parent.window.special.docLibID;
                param.imagePath = LECurrentObject.find(".le-image_img").attr("src");
                $.ajax({
                    url: "../special/uploadOnlinePic.do",
                    type: "post",
                    dataType: "json",
                    data: param,
                    async: false,
                    success: function(json){
                        LECurrentObject.find(".le-image_img").attr("src", json.imagePath);
                        LECurrentObject.find(".le-image_img").attr("data-type", "local");
                        if(LECurrentObject.find(".le-image_img").attr("data-type") == "online"){
                            $toLocalPic.show();
                        } else{
                            $toLocalPic.hide();
                        }
                    }
                });
            });
        }

        var resetBtns = function(_$obj){
            _$obj = _$obj || $target;
            if($.trim(_$obj.find("img").attr("src")) != ""){
                $addBtn.hide();
                $resetBtn.show();
                $editBtn.show();
                $(".piclinkbox").show();
                if(LECurrentObject.find(".le-image_img").attr("data-type") == "online"){
                    $toLocalPic.show();
                } else{
                    $toLocalPic.hide();
                }
            } else{
                $addBtn.show();
                $resetBtn.hide();
                $editBtn.hide();
                $(".piclinkbox").hide();
                $toLocalPic.hide();
            }
        };
        var resetLink = function(){
            if($target.find("a").length > 0){
                $link.val($target.find("a").attr("href"));
                $target.find("img").css("cursor", "pointer")
            }
            else{
                $target.find("img").css("cursor", "auto")
            }
        };

        /**
         * 重新设置宽高
         * 1. 先看style属性里面有没有值
         * 2. 如果没有的话，就直接去css样式；如果有的话，取style里面的样式
         */
        var resetWH = function(){
            var _$target = LECurrentObject.find(".le-image_img");
            $wh.each(function(){
                var _$this = $(this);

                var _styleLabel = _$this.attr("data-ref");

                //获得style
                var _style = _$target.attr("style") + "";
                //获得style标签里面的宽高 如果有的话，取style里面的样式
                /* if(_style.indexOf(_styleLabel) != -1 && _style.indexOf("-" + _styleLabel) == -1){
                 var _ss = _style.split(";");
                 var _val = null;
                 for(var i = 0, si = null; si = _ss[i++];){
                 if(si.indexOf(";") != -1){
                 _val = si.substr(si.indexOf(":") + 1);
                 _val = $.trim(_val);
                 _$this.val(_val);
                 }

                 }
                 } else{
                 //如果没有的话，就直接去css样式；
                 _$this.val(_$target.css(_styleLabel));

                 }*/

                _$this.val(_$target.css(_styleLabel));
            });
        };

        function resetImageRadio(){
            var _img = LECurrentObject.find(".le-image_img");
            _img.attr("data-radio", parseInt(_img.css("width")) / parseInt(_img.css("height")));
        }

        //图片是否约束比例
        var initpicRatioEvent = function(){
            $("#sidebar-panel").find(".pic-ratio").click(function(){
                var _this = $(this);
                var _thisObj=LECurrentObject;
                var _$img = _thisObj.find(".le-image_img");
                _this.toggleClass("select");
                //保持比例
                if(_this.hasClass("select")){
                    _$img.resizable("destroy");
                    _$img.attr("data-unlocked", false);
                    refreshImageResizable(true,_thisObj);
                    //_$img.resizable( "option", "aspectRatio", true );
                    _this.attr("title", "约束比例");
                } else{
                    _$img.resizable("destroy");
                    _$img.attr("data-unlocked", true);
                    refreshImageResizable(false,_thisObj);
                    //_$img.resizable("option", "aspectRatio", false);
                    _this.attr("title", "自由比例")
                }
            })
        };

        function refreshImageResizable(keepRatio,_thisObj){
            _thisObj.find(".le-image_img").attr("data-unlocked", !keepRatio);
            //var _$column = LECurrentObject.parent().parent().parent().attr("id") == "container" ? LECurrentObject.parent().parent().parent() : _$this.closest(".column");
            var _maxWidth = _thisObj.width();
            _thisObj.find(".le-image_img").resizable({
                aspectRatio: keepRatio,
                ghost: false,
                maxWidth : _maxWidth,
                start: function(){
                    LEHandler.handler_enableMouseisOver = false;
                    $(this).parent().parent().css("height", "");
                    _thisObj.find(".le-image_img").css("height",_thisObj.find(".le-image_img").parent().css("height")).css("width",_thisObj.find(".le-image_img").parent().css("width"));
                    _thisObj.find(".le-image_img").parent().css("position","relative");
                    LEDrag.hideHint();
                },
                create:function(){
                    _thisObj.find(".le-image_img").css("height",_thisObj.find(".le-image_img").parent().css("height")).css("width",_thisObj.find(".le-image_img").parent().css("width"));
                    _thisObj.find(".le-image_img").parent().css("position","relative");
                },
                resize:function(){
                    _thisObj.find(".le-image_img").css("height",_thisObj.find(".le-image_img").parent().css("height")).css("width",_thisObj.find(".le-image_img").parent().css("width"));
                    _thisObj.find(".le-image_img").parent().css("position","relative");
                },
                stop: function(){
                    LEHandler.handler_enableMouseisOver = true;

                    var _$this = $(this);
                    _thisObj.find(".le-image_img").css("height",_thisObj.find(".le-image_img").parent().css("height")).css("width",_thisObj.find(".le-image_img").parent().css("width"));
                    _thisObj.find(".le-image_img").parent().css("position","relative");
                    // LEHistory.trigger();
                    _$this.click();
                    LEDrag.hideHint();
                }
            });
            _thisObj.find(".le-image_img").css("height",_thisObj.find(".le-image_img").parent().css("height")).css("width",_thisObj.find(".le-image_img").parent().css("width"));
            _thisObj.find(".le-image_img").parent().css("position","relative");
        }

        function resetLock(){
            var unLocked = LECurrentObject.find(".le-image_img").attr("data-unlocked");
            unLocked += "";
            if(unLocked != "true"){
                $("#sidebar-panel").find(".pic-ratio").addClass("select");
            } else{
                $("#sidebar-panel").find(".pic-ratio").removeClass("select");
            }
        }

        return {
            init: function(){
                //是否允许图片上传
                LE.canChooseImg=true;
                initAddLinkEvent();
                initBtnsEvent();
                initLinkEvent();
                initToLocalEvent();
                initWH();
                initpicRatioEvent();
            },
            run: function(options, doHide){
                $target = options.object;
                resetBtns();
                resetLink();
                resetWH();
                resetLock();
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                LEDisplay.show($panel, doHide);
            },
            destroy: function(){
                $panel.hide();
            }
        };
    };
})(window, jQuery, LE, undefined);
