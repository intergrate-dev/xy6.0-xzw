/**
 * Created by isaac_gu on 2016/1/6.
 */
(function(window, $, LE){
    LE.options["Gallery"] = {
        selector: "#galleryLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-gallery gallery-edit row plugin_entity plugin-hint">',
            /*'<div class="col-xs-6 col-md-3">',
             '<div class="marginItem">',
             '<div class="borderItem">','<a href="http://www.baidu.com" style="position: relative; display: block; overflow: hidden;">',
             '<p class="over-title" style="">我是标题','</p>',
             '<img data-src="holder.js/100%x100%" class="" alt="100%x180" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iMTcxIiBoZWlnaHQ9IjE4MCIgdmlld0JveD0iMCAwIDE3MSAxODAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzEwMCV4MTgwCkNyZWF0ZWQgd2l0aCBIb2xkZXIuanMgMi42LjAuCkxlYXJuIG1vcmUgYXQgaHR0cDovL2hvbGRlcmpzLmNvbQooYykgMjAxMi0yMDE1IEl2YW4gTWFsb3BpbnNreSAtIGh0dHA6Ly9pbXNreS5jbwotLT48ZGVmcz48c3R5bGUgdHlwZT0idGV4dC9jc3MiPjwhW0NEQVRBWyNob2xkZXJfMTU0NTYzMTI5NDcgdGV4dCB7IGZpbGw6I0FBQUFBQTtmb250LXdlaWdodDpib2xkO2ZvbnQtZmFtaWx5OkFyaWFsLCBIZWx2ZXRpY2EsIE9wZW4gU2Fucywgc2Fucy1zZXJpZiwgbW9ub3NwYWNlO2ZvbnQtc2l6ZToxMHB0IH0gXV0+PC9zdHlsZT48L2RlZnM+PGcgaWQ9ImhvbGRlcl8xNTQ1NjMxMjk0NyI+PHJlY3Qgd2lkdGg9IjE3MSIgaGVpZ2h0PSIxODAiIGZpbGw9IiNFRUVFRUUiLz48Zz48dGV4dCB4PSI2MSIgeT0iOTQuNSI+MTcxeDE4MDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" data-holder-rendered="true" style="height: 196px;; width: 100%; display: block;">',
             '</a>',
             '</div>',
             '</div>',
             '<p class="bottom-title" style="">',
             '<a href="http:\\www.baidu.com">我是标题','</a>',
             '</p>',
             '</div>',*/
            '<div class="col-xs-6 col-md-3 g-void">',
            '<div class="marginItem">',
            '<div class="borderItem">',
            '<img data-src="holder.js/100%x100%" class="" alt="" src="export/img/noimg.png" data-holder-rendered="true" style="height: 212px; width: 100%; display: block;">',
            //'<img data-src="holder.js/100%x100%" class="" alt="100%x180" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iMTcxIiBoZWlnaHQ9IjE4MCIgdmlld0JveD0iMCAwIDE3MSAxODAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzEwMCV4MTgwCkNyZWF0ZWQgd2l0aCBIb2xkZXIuanMgMi42LjAuCkxlYXJuIG1vcmUgYXQgaHR0cDovL2hvbGRlcmpzLmNvbQooYykgMjAxMi0yMDE1IEl2YW4gTWFsb3BpbnNreSAtIGh0dHA6Ly9pbXNreS5jbwotLT48ZGVmcz48c3R5bGUgdHlwZT0idGV4dC9jc3MiPjwhW0NEQVRBWyNob2xkZXJfMTU0NTYzMTI5NDcgdGV4dCB7IGZpbGw6I0FBQUFBQTtmb250LXdlaWdodDpib2xkO2ZvbnQtZmFtaWx5OkFyaWFsLCBIZWx2ZXRpY2EsIE9wZW4gU2Fucywgc2Fucy1zZXJpZiwgbW9ub3NwYWNlO2ZvbnQtc2l6ZToxMHB0IH0gXV0+PC9zdHlsZT48L2RlZnM+PGcgaWQ9ImhvbGRlcl8xNTQ1NjMxMjk0NyI+PHJlY3Qgd2lkdGg9IjE3MSIgaGVpZ2h0PSIxODAiIGZpbGw9IiNFRUVFRUUiLz48Zz48dGV4dCB4PSI2MSIgeT0iOTQuNSI+MTcxeDE4MDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" data-holder-rendered="true" style="height: 212px; width: 100%; display: block;">',
            //'<img style="height: 212px; width: 100%; display: block;">',
            '</div>',
            '</div>',
            '</div>',
            '<div class="col-xs-6 col-md-3 g-void"">',
            '<div class="marginItem">',
            '<div class="borderItem">',
            '<img data-src="holder.js/100%x100%" class="" alt="" src="export/img/noimg.png" data-holder-rendered="true" style="height: 212px; width: 100%; display: block;">',
            //'<img style="height: 212px; width: 100%; display: block;">',
            '</div>',
            '</div>',
            '</div>',
            '<div class="col-xs-6 col-md-3 g-void"">',
            '<div class="marginItem">',
            '<div class="borderItem">',
            '<img data-src="holder.js/100%x100%" class="" alt="" src="export/img/noimg.png" data-holder-rendered="true" style="height: 212px; width: 100%; display: block;">',
            //'<img data-src="holder.js/100%x100%" class="" alt="100%x180" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iMTcxIiBoZWlnaHQ9IjE4MCIgdmlld0JveD0iMCAwIDE3MSAxODAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzEwMCV4MTgwCkNyZWF0ZWQgd2l0aCBIb2xkZXIuanMgMi42LjAuCkxlYXJuIG1vcmUgYXQgaHR0cDovL2hvbGRlcmpzLmNvbQooYykgMjAxMi0yMDE1IEl2YW4gTWFsb3BpbnNreSAtIGh0dHA6Ly9pbXNreS5jbwotLT48ZGVmcz48c3R5bGUgdHlwZT0idGV4dC9jc3MiPjwhW0NEQVRBWyNob2xkZXJfMTU0NTYzMTI5NDcgdGV4dCB7IGZpbGw6I0FBQUFBQTtmb250LXdlaWdodDpib2xkO2ZvbnQtZmFtaWx5OkFyaWFsLCBIZWx2ZXRpY2EsIE9wZW4gU2Fucywgc2Fucy1zZXJpZiwgbW9ub3NwYWNlO2ZvbnQtc2l6ZToxMHB0IH0gXV0+PC9zdHlsZT48L2RlZnM+PGcgaWQ9ImhvbGRlcl8xNTQ1NjMxMjk0NyI+PHJlY3Qgd2lkdGg9IjE3MSIgaGVpZ2h0PSIxODAiIGZpbGw9IiNFRUVFRUUiLz48Zz48dGV4dCB4PSI2MSIgeT0iOTQuNSI+MTcxeDE4MDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" data-holder-rendered="true" style="height: 212px; width: 100%; display: block;">',
            //'<img style="height: 212px; width: 100%; display: block;">',
            '</div>',
            '</div>',
            '</div>',
            '<div class="col-xs-6 col-md-3 g-void"">',
            '<div class="marginItem">',
            '<div class="borderItem">',
            '<img data-src="holder.js/100%x100%" class="" alt="" src="export/img/noimg.png" data-holder-rendered="true" style="height: 212px; width: 100%; display: block;">',
            //'<img data-src="holder.js/100%x100%" class="" alt="100%x180" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iMTcxIiBoZWlnaHQ9IjE4MCIgdmlld0JveD0iMCAwIDE3MSAxODAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzEwMCV4MTgwCkNyZWF0ZWQgd2l0aCBIb2xkZXIuanMgMi42LjAuCkxlYXJuIG1vcmUgYXQgaHR0cDovL2hvbGRlcmpzLmNvbQooYykgMjAxMi0yMDE1IEl2YW4gTWFsb3BpbnNreSAtIGh0dHA6Ly9pbXNreS5jbwotLT48ZGVmcz48c3R5bGUgdHlwZT0idGV4dC9jc3MiPjwhW0NEQVRBWyNob2xkZXJfMTU0NTYzMTI5NDcgdGV4dCB7IGZpbGw6I0FBQUFBQTtmb250LXdlaWdodDpib2xkO2ZvbnQtZmFtaWx5OkFyaWFsLCBIZWx2ZXRpY2EsIE9wZW4gU2Fucywgc2Fucy1zZXJpZiwgbW9ub3NwYWNlO2ZvbnQtc2l6ZToxMHB0IH0gXV0+PC9zdHlsZT48L2RlZnM+PGcgaWQ9ImhvbGRlcl8xNTQ1NjMxMjk0NyI+PHJlY3Qgd2lkdGg9IjE3MSIgaGVpZ2h0PSIxODAiIGZpbGw9IiNFRUVFRUUiLz48Zz48dGV4dCB4PSI2MSIgeT0iOTQuNSI+MTcxeDE4MDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" data-holder-rendered="true" style="height: 212px; width: 100%; display: block;">',
            //'<img style="height: 212px; width: 100%; display: block;">',
            '</div>',
            '</div>',
            '</div>',
            '</div>'
        ].join(""),
        viewinnerHtml: [
            '<div class="col-xs-6 col-md-3 g-void">',
            '<div class="marginItem">',
            '<div class="borderItem">',
            '<img data-src="holder.js/100%x100%" class="" alt="" src="export/img/noimg.png" data-holder-rendered="true" style="height: 212px; width: 100%; display: block;">',
            '</div>',
            '</div>',
            '</div>',
            '<div class="col-xs-6 col-md-3 g-void"">',
            '<div class="marginItem">',
            '<div class="borderItem">',
            '<img data-src="holder.js/100%x100%" class="" alt="" src="export/img/noimg.png" data-holder-rendered="true" style="height: 212px; width: 100%; display: block;">',
            '</div>',
            '</div>',
            '</div>',
            '<div class="col-xs-6 col-md-3 g-void"">',
            '<div class="marginItem">',
            '<div class="borderItem">',
            '<img data-src="holder.js/100%x100%" class="" alt="" src="export/img/noimg.png" data-holder-rendered="true" style="height: 212px; width: 100%; display: block;">',
            '</div>',
            '</div>',
            '</div>',
            '<div class="col-xs-6 col-md-3 g-void"">',
            '<div class="marginItem">',
            '<div class="borderItem">',
            '<img data-src="holder.js/100%x100%" class="" alt="" src="export/img/noimg.png" data-holder-rendered="true" style="height: 212px; width: 100%; display: block;">',
            '</div>',
            '</div>',
            '</div>'
        ].join("")
    };

    LE.plugins["Gallery"] = function(){
        var initContainer = function(){
        };
        return {
            init: function(){
                $(".demo").on({
                    click: function(e){
                        var options = {
                            object: $(this),
                            type: "all"
                        };
                        LEStyle.destroyAll($(this).attr("id"));
                        LEStyle.run("Gallery", options).run("css-Position", options, true).run("css-BackGround", options, true).run("css-BolderSetting", options, true).run("css-TextSettingWhole", options, true);
                    }
                }, ".le-gallery");
            },
            afterDrag: function(_id){
            }
        }
    };

})(window, jQuery, LE, undefined);

//TextSetting