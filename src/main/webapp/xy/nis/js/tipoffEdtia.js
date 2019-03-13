$(function(){
    var _attachment = $("#a_attachments").val();
    _html = '';
    if(_attachment && $.trim(_attachment) != ""){
        var _json = JSON.parse(_attachment);
        var list = _json.pics;
        var listvideo=_json.videos;
        if(list && list instanceof Array){
            $(".swiper-wrapper").empty();
            for(var i = 0, li = null; li = list[i++];){
                _html += '<li class="disclosePicList image-border-box">' +
                    '<div class="image-box">\n' +
                    '<img src="../../xy/image.do?path='+li+'"  />' +
                    '</div>\n' +
                    '</li>';
                $(".swiper-wrapper").append("<div class='swiper-slide' style='height: 750px;background-color: #000;'><img style='margin-bottom: 50px;width: 318px;height: auto;' src='../../xy/image.do?path="+li+"' /></div>");
            }
            $("#discloseListPic").html(_html);

        }
        if(listvideo && listvideo instanceof Array){
            if(listvideo==null || listvideo==""){
                $(".content-div").empty();
            }else{
                for(var i = 0, li = null; li = listvideo[i++];){
                    $(".videoPlayer").attr('src', li.urlApp).attr('poster', "");
                };
            }


        }

    };

    //回复按钮

    $(".btAnswers").on("click",function(){
        var docID = $("#docID").val();
        var docLibID = $("#docLibID").val();
        var answer= $(this).parent().parent().children(".a_answers").val();
        var siteID= $("#siteID").val();
        $.ajax({
            url: "../../xy/nis/tipoffAnswerSubmit.do?DocIDs="+docID,
            type: 'post',
            data: {
                a_answers:answer,
                DocLibID:docLibID,
                siteID:siteID
            },
            success: function (data) {
                alert("回复成功！");
                $(".a_answers").html(answer)
            }
        });
    });
    //采用按钮
    $(".adopt").on("click",function(){
        var docID = $("#docID").val();
        var docLibID = $("#docLibID").val();
        var UUID = $("#UUID").val();
        $.ajax({
            url: "../../xy/nis/tipoffAdopt.do",
            type: 'post',
            data: {
                DocIDs:docID,
                DocLibID:docLibID,
                UUID:UUID,
                type:1
            },
            success: function (data) {
                if(data!=null){
                    alert("操作成功！");
                    $(".noadopt").css("display","");
                    $(".adopt").css("display","none");
                    $(".status").html("已采用");
                }
            }
        });
    });
    //不采用按钮
    $(".noadopt").on("click",function(){
        var docID = $("#docID").val();
        var docLibID = $("#docLibID").val();
        var UUID = $("#UUID").val();
        $.ajax({
            url: "../../xy/nis/tipoffNoAdopt.do",
            type: 'post',
            data: {
                DocIDs:docID,
                DocLibID:docLibID,
                UUID:UUID,
                type:1
            },
            success: function (data) {
                if(data!=null){
                    alert("操作成功！");
                    $(".noadopt").css("display","none");
                    $(".adopt").css("display","");
                    $(".status").html("未采用");
                }
            }
        });
    });
    $("#btupdate").on("click",function(){
        var docID = $("#docID").val();
        var docLibID = $("#docLibID").val();
        var UUID = $("#UUID").val();
        var FVID = $("#FVID").val();
        var siteID = $("#siteID").val();
        window.location.href='../../xy/nis/TipoffUpdate.do?DocLibID='+docLibID+'&UUID='+UUID+'&DocIDs='+docID+'&FVID='+FVID+'&siteID='+siteID;
    });
    //点击图片显示轮播
    $(".image-box").on("click",function(){

        $(".carousel").css("display","block");
    });
    //点击关闭轮播图
    $("#imgbtn").on("click",function () {
        $(".carousel").css("display","none");
    });
    $(".content-div").on("click",function(){
        $(".con_video").css("display","block");

    });

    $("#imgbtn_video").on("click",function(){
        $(".con_video").css("display","none");
    });
    /*初始化轮播*/
    var swiper = new Swiper('.swiper-container', {
        pagination: {
            el: '.swiper-pagination',
            type: 'fraction',
        },
        navigation: {
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev',
        },
    });

    $(".carousel").css("display","none");


})