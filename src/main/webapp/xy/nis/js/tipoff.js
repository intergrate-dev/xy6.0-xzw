var xy_subject = {
    init : function() {
        $("#a_content").attr("readonly", true);

        var docid=getQueryString("DocIDs");

        $("#form").attr("target", "iframe");
        $("#form").attr("action", "../../xy/nis/tipoffAnswerSubmit.do?DocIDs="+docid);
    }
};
$(function(){
    xy_subject.init();
});

function getQueryString(name){
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r != null)return unescape(r[2]);
    return null;
}