LE.plugin = function(){
    var _plugins = {};
    return {
        register : function(pluginName, fn){
            _plugins[pluginName] = {
                execFn : fn
            };
        },
        load : function(editor){
            //向下兼容
            utils.each(LE.plugins, function(plugin){
                plugin.call(editor).run();
            });
        },
        run : function(pluginName,editor){
            var plugin = _plugins[pluginName];
            if(plugin){
                plugin.exeFn.call(editor);
            }
        }
    };
}();