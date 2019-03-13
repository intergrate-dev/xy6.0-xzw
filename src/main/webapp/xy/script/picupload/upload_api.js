
/**
 * Created by isaac_gu on 2017/3/6.
 */
(function(){
    var baseURL = getJsUrl("upload_api.js");
    var paths = [
        {
            type: "css",
            path: "css/fileinput.css"
        },
        {
            type: "css",
            path: "themes/explorer/theme.css"
        },
        {
            type: "css",
            path: "custom.css"
        },
        {
            type: "js",
            path: "js/plugins/sortable.js"
        },
        {
            type: "js",
            // path: "js/fileinput.min.js" //未压缩的js修改了预览图片 若图片以.0.jpg或.0为后缀，则去掉这后缀
            path: "js/fileinput.js"
        },
        {
            type: "js",
            path: "js/locales/zh.js"
        },
        {
            type: "js",
            path: "themes/explorer/theme.js"
        },
        {
            type: "js",
            path: "upload.js"
        }
    ];
    var date = new Date();
    for(var i = 0, pi; pi = paths[i++];){
        var d = date.getTime();
        if(pi.type == "css"){
            document.write('<link href="' + baseURL + pi.path + '?timestamp=' + d + '" media="all" rel="stylesheet" type="text/css"/>');
        } else{
            document.write('<script type="text/javascript" src="' + baseURL + pi.path + '?timestamp=' + d + '"></script>');
        }

    }
    function getJsUrl(jsName){
        var js = document.scripts;
        var jsPath;
        for(var i = js.length; i > 0; i--){
            if(js[i - 1].src.indexOf(jsName) > -1){
                jsPath = js[i - 1].src.substring(0, js[i - 1].src.lastIndexOf("/") + 1);
                return jsPath;
            }
        }
        return null;
    }
})();