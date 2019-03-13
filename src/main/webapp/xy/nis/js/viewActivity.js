/**
 * Created by isaac_gu on 2016/11/1.
 */

$(function(){

    //现在不需要显示附件图片，只显示详情
    var a_attachments = $("#a_attachments").html();
    var json = JSON.parse(a_attachments);
    var pics = json.pics;
    var _html = [];
    _html.push("<ul style=clear: both;'>");
    for(var i = 0, pi = null; pi = pics[i]; i++){
        var _pi = "../xy/image.do?path=" + pi;
        _html.push("<li style='float: left;padding-left: 5px'><img src=" + _pi + " style='width: 100px;height: 100px'></li>")
    }
    _html.push("</ul>");
    $("#a_attachments").html(_html.join(""));

    $("#a_content").html($("#a_content").text());

});




