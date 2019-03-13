/**
 * Created by isaac_gu on 2016/3/22.
 */
(function(window, $, LE){
    LE.options["listMedia"] = {
        selector: "#mediaLi", //标签选择器
        tag: "plugin",
        viewHtml: [
                '<div class="le-list-group plugin-hint plugin_entity canedit" data-name="listMedia">' +
                '<ul class="list-group list-void">' +
                    '<li class="list-group-item clearfix"><img src="export/img/noimg.png" class="pull-left"/><a href="" class="pull-left">请点击右侧“选择文章”按钮</a><span class="timeShow badge">2016-03-22 15:11</span></li>' +
                    '<li class="list-group-item clearfix"><img src="export/img/noimg.png" class="pull-left"/><a href="" class="pull-left">请点击右侧“选择文章”按钮</a><span class="timeShow badge">2016-03-22 15:11</span></li>' +
                    '<li class="list-group-item clearfix"><img src="export/img/noimg.png" class="pull-left"/><a href="" class="pull-left">请点击右侧“选择文章”按钮</a><span class="timeShow badge">2016-03-22 15:11</span></li>' +
                '</ul>' +
                '</div>'
        ].join(""),
        viewinnerHtml: [
                '<ul class="list-group list-void">' +
                '<li class="list-group-item clearfix"><img src="export/img/noimg.png" class="pull-left"/><a href="" class="pull-left">请点击右侧“选择文章”按钮</a><span class="timeShow badge">2016-03-22 15:11</span></li>' +
                '<li class="list-group-item clearfix"><img src="export/img/noimg.png" class="pull-left"/><a href="" class="pull-left">请点击右侧“选择文章”按钮</a><span class="timeShow badge">2016-03-22 15:11</span></li>' +
                '<li class="list-group-item clearfix"><img src="export/img/noimg.png" class="pull-left"/><a href="" class="pull-left">请点击右侧“选择文章”按钮</a><span class="timeShow badge">2016-03-22 15:11</span></li>' +
                '</ul>'
        ].join("")
    };
    LE.plugins["listMedia"] = function(){
        var initContainer = function(){
            $("#container").on({
                click: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    var options = {
                        object: $(this),
                        type: "all"
                    };
                    LEStyle.destroyAll($(this).attr("id"));
                    LEStyle.run("ListSettingCss", options).run("SlideBar", options).run("WholeSetting",options);
                    /*.run("TextSetting", options, true);*/
                }
            }, ".le-list-group");
        };
        return {
            init: function(){
                //initContainer();
            },
            afterDrag: function(){

            }
        }
    };

})(window, jQuery, LE, undefined);