/**
 * Created by isaac_gu on 2016/1/6.
 * 主要负责初始化 LE所有的核心类及组件
 */

window.LE = window.LE || {};
window.LECurrentObject = null;
window.LE2ndLayerObject = null;
window.LE3rdLayerObject = null;
window.LELayer = 0;
window.isSameObject = false;
LE.version = 1.0;
LE.commands = {};
LE.cores = {};
LE.plugins = {};
LE.settings = {};
LE.options = {};
LE.params = {};

var LayoutEditor = function(){
    var loadedAllPlugins = false;
    var loadCores = function(){
        //粘贴一个隐藏域用来放plugin
        utils.each(LE.cores, function(core, key){
            //确定每个 plugin都实现了一下的方法
            var model = core.call();
            model._$$key$$ = key;
            utils.ensureImplements(model, ["init"]);
            model.init();
        });
    };

    //private
    var loadPlugins = function(){
        //粘贴一个隐藏域用来放plugin
        jQuery("#pluginsDiv").size() < 1 && jQuery(document.body).append(['<div id="pluginsDiv" style="display: none;"></div>'].join(""));
        utils.each(LE.plugins, function(plugin, key){
            //确定每个 plugin都实现了一下的方法
            var model = plugin.call();
            model._$$key$$ = key;
            utils.ensureImplements(model, ["init"]);

            var _option = LE.options[model._$$key$$];

            //先把plugin的html 放到隐藏域里面
            var viewHtml = _option.viewHtml;
            //预览的html
            var $preview = jQuery(_option.selector);
            var previewHtml = $preview.html();
            var tag = _option.tag;
            var _html = [
                '<div class="' + tag + '" data-type="' + key + '">',
                '<div class="preview">',
                previewHtml,
                '</div>',
                '<div class="view">',
                viewHtml,
                '</div>',
                '</div>',
            ].join("");
            $preview.html(_html);
            //给绑定的selector加class
            jQuery(_option.selector).attr("data-plugin", 'plugin_' + key);
            model.init();

        });
        loadedAllPlugins = true;
    };

    var initPlugins = function(){
        jQuery(".le-tabs").tabs();

        jQuery(".dropdown-ul").hover(function(){
            clearTimeout(timer);
            jQuery(this).addClass("open");
        }, function(){
            var _this = jQuery(this);
            clearTimeout(timer);
            timer = setTimeout(function(){
                _this.removeClass("open");
            }, 500);
        });
    };

    //public
    return {
        init: function(){
            loadPlugins();
            loadCores();
            LEStyle.load();
            jQuery("#container").click();
            //LEStyle.destroyAll();
        }
    };
};