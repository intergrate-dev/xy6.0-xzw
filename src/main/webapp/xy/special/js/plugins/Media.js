/**
 * Created by isaac_gu on 2016/3/22.
 */
(function(window, $, LE){
    LE.options["Media"] = {
        selector: "#mediaLi", //标签选择器
        tag: "plugin",
        viewHtmlOld: [
            '<div class="plugin-hint plugin_entity">' +
            '<div class="media" style="padding: 5px;">' +
            '<div class="media-left le-image plugin_entity">' +
                //'<a href="#">' +
                //'</a>' +
            '</div>' +
            '<div class="media-body le-text plugin-hint plugin_entity">' +
            'Cras sit amet nibh libero, in gravida nulla. Nulla vel metus scelerisque ante sollicitudin commodo. Cras purus odio, vestibulum in vulputate at, tempus viverra turpis. Fusce condimentum nunc ac nisi vulputate fringilla. Donec lacinia congue felis in faucibus.' +
            '</div>' +
            '</div>' +
            '</div>'
        ].join(""),
        viewHtml: [
            '<div class="le-columnbox diy-inner plugin_entity plugin-hint clearfix">',
            '<div class="w-col diy-col-3">',
            '<div class="column column_style plugin-hint plugin_entity" position-type="phw" >',
            '<div class="le-image le-media-image plugin_entity plugin-hint" >',
            '</div>',
            '</div>',
            '<div class="diy-col-ctrl" style="left: 100%;"></div>',
            '</div>',
            '<div class="w-col diy-col-9">',
            '<div class="column column_style plugin-hint plugin_entity"  position-type="phw">',
            '<div data-placeholder="双击此处输入文字" class="le-text plugin_entity plugin-hint">',
            '</div>',
            '</div>',
            '<div class="diy-col-ctrl_disabled" style="left: 100%;"></div>',
            '</div>',
            '</div>'
        ].join("")
    };

    LE.plugins["Media"] = function(){
        var initContainer = function(){

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