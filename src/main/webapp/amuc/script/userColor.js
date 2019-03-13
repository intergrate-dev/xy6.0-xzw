/**
 * 用户管理，若是用户过期，则字体颜色变化
 */
e5.mod("userext.userColor", function() {
    var api;
    var listening = function() {
        $("#listing table tr").each(function(e){
            var hiddenColSpan = $(this).find("td .hiddenUvalidDate");
            var uvaliddate = hiddenColSpan.attr("uvaliddate");
            if (!uvaliddate) return;
            var uvaliddateDate = new Date(uvaliddate.replace("-", "/").replace("-", "/"));
            if (new Date()>uvaliddateDate) {
                $(this).addClass("invalidUser");
            }
        });
    };
    var init = function(sandbox) {
        api = sandbox;
        api.listen("workspace.doclist:setDataFinish", listening);
    };
    return {
        init : init
    };
});