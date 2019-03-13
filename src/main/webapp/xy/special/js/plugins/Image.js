/**
 * Created by isaac_gu on 2016/1/6.
 */

(function(window, $, LE){
    LE.options["Image"] = {
        selector: "#imageLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-image plugin_entity plugin-hint" >',
            //'<a href="#" >',
            //'</a>',
            '</div>'
        ].join(""),
        imageHtml: '<img class="le-image_img align_mark" onerror="LEImage.error(this);" src="" alt="...">'
    };

    LE.plugins["Image"] = function(){
        var initContainer = function(){
            $("#container").on({
                click: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    var options = {
                        object: $(this)
                    };
                    LEStyle.destroyAll($(this).attr("id"));
                    LEStyle.run("Position", options).run("BolderSetting", options).run("Align", options).run("Pic", options);
                }
            }, ".le-image");
        };
        return {
            init: function(){
                initContainer();
            },
            error: function(_this){
                //在图片后面添加一个隐藏的img 进行加载， 如果加载成功替换img的路径
                $(_this).after('<img class="le-image_test" style="display: none;" onload="LEImage.loadSuccess(this);" onerror="LEImage.reload(this);" src="' + $(_this).attr("src") + '" alt="...">');
                //先添加一个默认的图片
                $(_this).attr("src", "./export/img/noimg.gif");

            },
            reload: function(_this){
                var _$this = $(_this);
                var _count = parseInt(_$this.attr("data-count"));
                if(!!_count){
                    if(_count > 10){
                        _$this.attr("src", "./export/img/noimg.jpg");
                        LE.canChooseImg=true;
                        return;
                    }
                } else{
                    _$this.attr("data-count", 0);
                    _count = 0;
                }
                _count = +_count + 1;
                _$this.attr("data-count", _count);
                utils.sleep(500);
                _$this.attr("src", _$this.attr("src"));
            },
            loadSuccess: function(_this){
                var _$this = $(_this);
                _$this.siblings("img").attr("src", _$this.attr("src"));
                _$this.remove();
                LE.canChooseImg=true;
            }
        }
    };
})(window, jQuery, LE, undefined);
LEImage = LE.plugins["Image"]();