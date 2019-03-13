/**
 * Created by isaac_gu on 2016/3/22.
 */
(function(window, $, LE){
    LE.options["ListGroup"] = {
        selector: "#listGroupLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-list-group plugin-hint plugin_entity canedit" data-name="listGroup">' +
            '<ul class="list-group list-void">' +
            '<li class="list-group-item"><a href="">请点击右侧“选择文章”按钮</a><span class="timeShow badge">2016-03-22 15:11</span></li>' +
            '<li class="list-group-item"><a href="">请点击右侧“选择文章”按钮</a><span class="timeShow badge">2016-03-22 15:11</span></li>' +
            '<li class="list-group-item"><a href="">请点击右侧“选择文章”按钮</a><span class="timeShow badge">2016-03-22 15:11</span></li>' +
            '<li class="list-group-item"><a href="">请点击右侧“选择文章”按钮</a><span class="timeShow badge">2016-03-22 15:11</span></li>' +
            '<li class="list-group-item"><a href="">请点击右侧“选择文章”按钮</a><span class="timeShow badge">2016-03-22 15:11</span></li>' +
            '</ul>' +
            '</div>'
        ].join(""),
        viewinnerHtml: [
            '<ul class="list-group list-void">' +
            '<li class="list-group-item"><a href="">请点击右侧“选择文章”按钮</a><span class="timeShow badge">2016-03-22 15:11</span></li>' +
            '<li class="list-group-item"><a href="">请点击右侧“选择文章”按钮</a><span class="timeShow badge">2016-03-22 15:11</span></li>' +
            '<li class="list-group-item"><a href="">请点击右侧“选择文章”按钮</a><span class="timeShow badge">2016-03-22 15:11</span></li>' +
            '<li class="list-group-item"><a href="">请点击右侧“选择文章”按钮</a><span class="timeShow badge">2016-03-22 15:11</span></li>' +
            '<li class="list-group-item"><a href="">请点击右侧“选择文章”按钮</a><span class="timeShow badge">2016-03-22 15:11</span></li>' +
            '</ul>'
        ].join("")

        /*[
         '<div class="le-list-group plugin-hint plugin_entity">' +
         '<ul class="list-group">' +
         '<a href="#" class="list-group-item">Cras justo odio<span class="badge">2016-03-22 15:11</span></a>' +
         '<a href="#" class="list-group-item">Dapibus ac facilisis in<span class="badge">2016-03-22 15:11</span></a>' +
         '<a href="#" class="list-group-item">Morbi leo risus<span class="badge">2016-03-22 15:11</span></a>' +
         '<a href="#" class="list-group-item">Porta ac consectetur ac<span class="badge">2016-03-22 15:11</span></a>' +
         '<a href="#" class="list-group-item">Vestibulum at eros<span class="badge">2016-03-22 15:11</span></a>' +
         '</ul>' +
         '</div>'
         ].join("")*/
    };

    LE.plugins["ListGroup"] = function(){
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
                initContainer();
            },
            afterDrag: function(){

            }
        }
    };
})(window, jQuery, LE, undefined);