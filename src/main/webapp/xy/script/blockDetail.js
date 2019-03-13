var url_view = {
    init : function() {
        var td = $("#SPAN_b_dir").parent();
        var dir = $("#b_dir").html();
        var suffix = $("#b_format").html();
        var docID = $("#SYS_DOCUMENTID").html();
        var url= dir+"/b"+docID+"."+suffix;
        td.html("");
        var spanUrl = "<span id=\"SPAN_b_dir\" class=\"custview-span\"> " +
            " <label id=\"LABEL_b_dir\" class=\"custview-label\">发布地址:</label> " +
            "<label id=\"b_dir\" class=\"custview-field\"><a href='"+url+"'>"+url+"</a></label> </span>"
        td.html(spanUrl);

    }
};

$(function() {
    url_view.init();
});