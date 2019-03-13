/**
 * 开发版本的文件导入
 */
(function(){
    var paths = [
            'reset.css',
            'header.css',
            'sliderBar.css',
            'editor.css',
            'sidebar-panel.css',
            'sidebar-panel-two.css',
            'model.css',
            'navMenuSet.css',
            'slidebar-panelTwo.css'
        ],
        baseURL = './export/css/';
    var date = new Date();
    for(var i = 0, pi; pi = paths[i++];){
        var d = date.getTime();
        document.write('<link id="' + pi.substring(0, pi.indexOf(".")) + 'Link' + '" rel="stylesheet" type="text/css" href="' + baseURL + pi + '?timestamp=' + d + '"/>');
    }
})();
