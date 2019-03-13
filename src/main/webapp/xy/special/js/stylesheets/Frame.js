/**
 * Created by isaac_gu on 2016/1/14.
 */
(function (window, $, LE) {
    LE.stylesheets["Frame"] = function () {
        var $panel = $("#frameSection");
        var $wh = $panel.find(".frameWH");
        var $scroll = $panel.find(".frameScroll");
        var $frameDialog = $("#frameDialog");

        var initAddBtnEvent = function () {
            $("#frameAddBtn").bind("click", function () {
                $frameDialog.modal("show");
            });
            $("#frameDivClose").click(function () {
                $frameDialog.modal("hide");
            });
            $("#frameUrlConfirm").click(function () {
                var _url = $("#frameUrlText").val();
                LECurrentObject.html(
                        '<iframe src="' + _url + '" frameborder="0" allowtransparency="true" ' +
                        'style="overflow: auto; width: ' +
                        '' + $wh.filter("[data-ref='width']").val() +
                        '; height: ' +
                        '' + $wh.filter("[data-ref='height']").val() +
                        ';"></iframe>'
                );
                var _f = LECurrentObject.children("iframe");
                $("#container").scrollTop(0);
                //_f[0].contentWindow.scrollTo(500, 500);
                $frameDialog.modal("hide");
            });
            $("#frameUrlText").keypress(function (e) {
                if (e.keyCode == 13) {
                    var _url = $("#frameUrlText").val();
                    LECurrentObject.html(
                            '<iframe src="' + _url + '" frameborder="0" allowtransparency="true" ' +
                            'style="overflow: auto; width: ' +
                            '' + $wh.filter("[data-ref='width']").val() +
                            '; height: ' +
                            '' + $wh.filter("[data-ref='height']").val() +
                            ';"></iframe>'
                    );
                    $frameDialog.modal("hide");
                }
            });
        };

        var initAddBtnEventOld = function () {
            $("#frameAddBtn").click(function (e) {
                var _url = prompt("请输入链接:", "http://www.baidu.com");
                LECurrentObject.html(
                        '<iframe src="' + _url + '" frameborder="0" allowtransparency="true" ' +
                        'style="overflow: auto; width: ' +
                        '' + $wh.filter("[data-ref='width']").val() +
                        '; height: ' +
                        '' + $wh.filter("[data-ref='height']").val() +
                        ';"></iframe>'
                );
                LEHistory.trigger();
            });
        };

        var initScrollBtnEvent = function () {
            $scroll.click(function (e) {
                var $target = LECurrentObject.find("iframe");
                var _ref = $(this).data("ref");
                $target.css("overflow", _ref);
                _ref == "hidden" ? $target.attr("scrolling", "no") : $target.removeAttr("scrolling");
            });
        };

        var initWHBtnEvent = function () {
            /*$wh.onlyNum().keydown(function(e){
             var $target = LECurrentObject.find("iframe");
             var v = LEKey.upAndDownModifyNum(e, $(this), $target);
             $(this).val(v + "px");
             });*/

            $wh.onlyNum().keydown(function (e) {
                LEKey.numInputKeyDown(e, $(this), LECurrentObject.find("iframe"));
            });
            //失去焦点的时候，置0
            $wh.blur(function (e) {
                LEKey.numInputBlur(e, $(this), LECurrentObject.find("iframe"));
                LEHistory.trigger();
            });

            $wh.focus(function (e) {
                LEKey.numInputFocus(e, $(this), LECurrentObject.find("iframe"));
            });

        };
        return {
            init: function () {
                initAddBtnEvent();
                initScrollBtnEvent();
                initWHBtnEvent();
            },
            run: function (options, doHide) {
                //切换组件时，样式设置部分回顶部
                sliderbarToTop();
                LEDisplay.show($panel, doHide);
            },
            destroy: function () {
                //console.info("PageSetting destroy")
                $panel.hide();
            }
        };
    };
})(window, jQuery, LE, undefined);

