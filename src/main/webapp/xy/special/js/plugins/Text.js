/**
 * Created by isaac_gu on 2016/1/6.
 */
(function(window, $, LE){
    LE.options["Text"] = {
        selector: "#textLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            //contenteditable="true"
            '<div data-placeholder="双击此处输入文字" class="le-text wrap plugin_entity plugin-hint"></div>'
        ].join("")
    };

    LE.plugins["Text"] = function(){
        var initText = function(){

            $("#container").on({
                click: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    var options = {
                        object: $(this)
                    };
                    LEStyle.destroyAll($(this).attr("id"));
                    LEStyle.run("TextSettingWhole", options).run("Position", options).run("Link", options).run("TextSettingPart", options);
                },
                dblclick: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    var _$this = $(this);
                    LEHandler.disableDragHandler();
                    _$this.attr("contenteditable", true);
                    _$this.addClass("textCanEdit");
                    _$this.focus();
                    //局部编辑文字显示

                    var _top = parseInt(_$this.offset().top);
                    var _height = parseInt(_$this.css("height"));
                    var _left = parseInt(_$this.offset().left);
                    var _winHeight = parseInt($(window).height());
                    //alert(_top+"top"+_height+"height"+_winHeight+"winheight")
                    if(_top > 50 && _winHeight - _top > 360){
                        $("#partText").css({
                            top: _top - 40,
                            left: 222
                        });
                    } else if(_top > 50 && _winHeight - _top < 360){
                        $("#partText").css({
                            top: _winHeight - 380,
                            left: 222
                        });
                    } else if(_height + _top > _winHeight - 360){
                        $("#partText").css({
                            top: _winHeight - 380,
                            left: 222
                        });
                    } else{
                        $("#partText").css({
                            top: _top + _height + 10,
                            left: 222
                        });
                    }

                    $(".partText-set").fadeIn(100);


                },
                paste: function(e){
                    var _$this = $(this);
                    if(_$this.attr("contenteditable") == "true"){
                        setTimeout(function(){
                            //e.preventDefault();
                            e.stopPropagation();
                            LECurrentObject.find("*").not("br").each(function(){
                                $(this).after($(this).text());
                                $(this).remove();
                            });
                            var reg = /&nbsp;/ig;
                            LECurrentObject.html(LECurrentObject.html().replace(reg, ""));
                            //alert(LECurrentObject.html());
                        }, 100)
                    }
                }
                /* blur:function(){
                 var _$this = $(this);
                 //文字变为不可编辑状态
                 _$this.attr("contenteditable", false);
                 _$this.removeClass("textCanEdit");
                 }*/
            }, ".le-text");
        };
        return {
            init: function(){
                initText();
            }

        }
    };

})(window, jQuery, LE, undefined);