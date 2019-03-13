<%@include file="../../e5include/IncludeTag.jsp" %>
<%@page pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>直播详情</title>
    <link type="text/css" rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
    <!-- Link Swiper's CSS -->
    <link rel="stylesheet" href="../script/Swiper-3.3.1/dist/css/swiper.min.css">
    <style>
        body {
            /*background: #eee;*/
            font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
            font-size: 14px;
            color: #000;
            margin: 0;
            padding: 0;
        }

        div {
            min-height: 10px;
        }

        .swiper-container {
            width: 100%;
            height: 200px;
            margin: 20px auto;
        }

        .swiper-slide {
            text-align: center;
            font-size: 18px;
            background: #fff;

            /* Center slide text vertically */
            display: -webkit-box;
            display: -ms-flexbox;
            display: -webkit-flex;
            display: flex;
            -webkit-box-pack: center;
            -ms-flex-pack: center;
            -webkit-justify-content: center;
            justify-content: center;
            -webkit-box-align: center;
            -ms-flex-align: center;
            -webkit-align-items: center;
            align-items: center;
        }

        .row {
            padding: 5px;
        }

        .headerBackground {
            background: rgba(242, 242, 242, 1);
        }

        .gray {
            color: #666666;
        }

        .notice {
            background: rgba(255, 204, 153, 0.486274509803922);
            border: 1px solid rgba(255, 0, 0, 1);
            padding: 5px;
            margin: 12px;
            width: 98%;
        }

        .channels {
            margin: 0;
            list-style: none;
            overflow: hidden;
            -webkit-padding-start: 0px;
            border-bottom: 1px solid #ddd;
            padding-top: 5px;
            padding-left: 5px;
            margin-bottom: 10px;

        }

        .channels li {
            float: left;
            text-align: center;
            cursor: pointer;
            width: 140px;
            padding: 3px;

        }

        .channels li:hover {
            border-bottom: 2px solid #ddd;
        }

        .channels li.active {
            color: #333;
            border-bottom: 2px solid #00a0e6;
            background-color: #fff;

        }

        .out-border {
            padding: 9px 14px;
            margin-bottom: 14px;
            background-color: #f7f7f9;
            border: 1px solid #e1e1e8;
            border-radius: 4px;
        }
    </style>
</head>
<body>
<div class="container">
    <input type="hidden" id="a_config" name="a_config" value='${a_config}'/>
    <c:if test="${type == 1}">
        <div class="row">
            <div class="col-sm-8"></div>
            <div class="col-sm-4">
                <c:if test="${status == 1}">
                <button class="btn btn-info" id="shutDownLiveBtn">结束直播</button>
                </c:if>
                <%--<c:if test="${status == 2}">
                    <button class="btn btn-info" id="addReplayUrlBtn">添加回访地址</button>
                </c:if>--%>
            </div>
        </div>
    </c:if>
    <c:if test="${type == 0}">
        <!-- 标题 -->
        <div id="picLiveDiv" class="row headerBackground">
            <!-- 标题左侧 -->
            <div class="col-sm-4">
                <!-- 轮播图 -->
                <div class="swiper-container">
                    <div class="swiper-wrapper">
                        <div class="swiper-slide" style="background-image:url(${webBanner}); background-repeat:no-repeat; background-size:100% 100%;-moz-background-size:100% 100%;color: rgb(223, 220, 220);">PC Banner</div>
                        <div class="swiper-slide" style="background-image:url(${appBanner}); background-repeat:no-repeat; background-size:100% 100%;-moz-background-size:100% 100%;color: rgb(223, 220, 220);">APP Banner</div>
                    </div>
                    <div class="swiper-pagination"></div>
                    <div class="swiper-button-next"></div>
                    <div class="swiper-button-prev"></div>
                </div>
                <!-- 轮播图 END -->
            </div>
            <!-- 标题左侧 END-->
            <!-- 标题右侧 -->
            <div class="col-sm-8">
                <div class="row" style="margin-top: 10px;">
                    <p><strong>${topic}</strong></p>
                </div>
                <div class="row gray">
                    <p>${content}</p>
                </div>

                <div class="row gray">
                    <p>直播类型：<strong>${liveType}</strong></p>
                </div>

                <div class="row gray">
                    <p>直播时间： ${startTime} - ${endTime}</p>
                </div>

                <div class="row gray">
                    <p>创建人：<strong>${createUser}</strong></p>
                </div>

            </div>
            <!-- 标题右侧 END-->
            <!-- 标题 END-->
        </div>
    </c:if>
    <c:if test="${type == 1}">
        <div id="videoLiveDiv" class="row headerBackground">
            <div class="row">
                <div class="col-sm-5"><strong>
                        ${topic}
                </strong></div>
                <div class="col-sm-5 gray">
                    直播时间： ${startTime} - ${endTime}
                </div>
                <div class="col-sm-2 gray">
                    创建人：${createUser}
                </div>
            </div>

            <div class="row">
                <div class="col-sm-12 gray">
                    <p>${content}</p>
                </div>
            </div>

            <div class="row">
                <c:if test="${status == 0}">
                    <div class="col-sm-12 center-block notice">
                        <div class="center-block " style="width:100px;">
                            直播未开始
                        </div>
                    </div>
                </c:if>
                <c:if test="${status == 2}">
                    <div class="col-sm-12 center-block notice">
                        <div class="center-block " style="width:300px;">
                            <strong>直播已结束<%--，请在直播信息页面增加回放地址--%></strong>
                        </div>
                        <%--<div class="col-sm-12 center-block">
                            <div class="center-block " style="width:600px;">
                                <p>注意：由于腾讯云直播转录成点播文件需要一段时间，所以当前端结束15分钟之后再增加回访地址。以免录播文件不完整！</p>
                            </div>
                        </div>--%>
                    </div>
                </c:if>
            </div>
            </div>

            <c:if test="${liveSize == 1}">
                <div class="row">
                    <div class="col-sm-12">
                        <c:choose>
                            <c:when test="${status == 0}">
                             <div><img src="./img/1150-350-1.png" /></div>
                            </c:when>
                            <c:when test="${status == 1 && (liveCurrentStatus_0||type_0==1)}">
                                <div id="player0-video-container" style="margin: 0px auto;"></div>
                            </c:when>
                            <c:when test="${status == 1 && !liveCurrentStatus_0&&type_0==0}">
                                <div><img src="./img/1150-350-2.png" /></div>
                            </c:when>
                            <c:otherwise>
                                <div id="player0-video-container" style="margin: 0px auto;"></div>
                            </c:otherwise>
                        </c:choose>
                        <%--&lt;%&ndash;<c:if test="${status == 1}">
                            <div class="center-block" style="width: 100px;">
                                <button class="btn btn-primary btn-sm cutbtn" style="margin-top: 10px;width: 164px;" data-channelstatus="${channel_status_0}" data-stream="${streamId_0}">
                                    <c:if test="${channel_status_0 == 0 || channel_status_0 == 1}">切&nbsp;&nbsp;&nbsp;&nbsp;断</c:if>
                                    <c:if test="${channel_status_0 == 2}">恢&nbsp;&nbsp;&nbsp;&nbsp;复</c:if>
                                </button>
                            </div>
                        </c:if>&ndash;%&gt;--%>
                    </div>
                </div>
            </c:if>

            <c:if test="${liveSize == 2}">
                <div class="row">
                    <div class="col-sm-6">
                        <c:choose>
                            <c:when test="${status == 0}">
                                <div><img src="./img/560-400-1.png" /></div>
                            </c:when>
                            <c:when test="${status == 1 && (liveCurrentStatus_0||type_0==1)}">
                                <div id="player0-video-container" style="margin: 0px auto;"></div>
                            </c:when>
                            <c:when test="${status == 1 && !liveCurrentStatus_0&&type_0==0}">
                                <div><img src="./img/560-400-2.png" /></div>
                            </c:when>
                            <c:otherwise>
                                <div id="player0-video-container" style="margin: 0px auto;"></div>
                            </c:otherwise>
                        </c:choose>
                        <%--&lt;%&ndash;<c:if test="${status == 1}">
                            <div class="center-block" style="width: 100px;">
                                <button class="btn btn-primary btn-sm cutbtn" style="margin-top: 10px;width: 164px;" data-channelstatus="${channel_status_0}" data-stream="${streamId_0}">
                                    <c:if test="${channel_status_0 == 2 || channel_status_0 == 1}">切&nbsp;&nbsp;&nbsp;&nbsp;断</c:if>
                                    <c:if test="${channel_status_0 == 0}">恢&nbsp;&nbsp;&nbsp;&nbsp;复</c:if>
                                </button>
                            </div>
                        </c:if>&ndash;%&gt;--%>
                    </div>
                    <div class="col-sm-6">
                        <c:choose>
                            <c:when test="${status == 0}">
                                <div><img src="./img/560-400-1.png" /></div>
                            </c:when>
                            <c:when test="${status == 1 && (liveCurrentStatus_1||type_1==1)}">
                                <div id="player1-video-container" style="margin: 0px auto;"></div>
                            </c:when>
                            <c:when test="${status == 1 && !liveCurrentStatus_1&&type_1==0}">
                                <div><img src="./img/560-400-2.png" /></div>
                            </c:when>
                            <c:otherwise>
                                <div id="player1-video-container" style="margin: 0px auto;"></div>
                            </c:otherwise>
                        </c:choose>
                       <%-- &lt;%&ndash;<c:if test="${status == 1}">
                            <div class="center-block" style="width: 100px;">
                                <button class="btn btn-primary btn-sm cutbtn" style="margin-top: 10px;width: 164px;" data-channelstatus="${channel_status_1}" data-stream="${streamId_1}">
                                    <c:if test="${channel_status_1 == 2 || channel_status_1 == 1}">切&nbsp;&nbsp;&nbsp;&nbsp;断</c:if>
                                    <c:if test="${channel_status_1 == 0}">恢&nbsp;&nbsp;&nbsp;&nbsp;复</c:if>
                                </button>
                            </div>
                        </c:if>&ndash;%&gt;--%>
                    </div>
                </div>
            </c:if>
            <c:if test="${liveSize == 3}">
                <div class="row">
                    <div class="col-sm-12">
                        <c:choose>
                            <c:when test="${status == 0}">
                                <div><img src="./img/1150-350-1.png" /></div>
                            </c:when>
                            <c:when test="${status == 1 && (liveCurrentStatus_0||type_0==1)}">
                                <div id="player0-video-container" style="margin: 0px auto;"></div>
                            </c:when>
                            <c:when test="${status == 1 && !liveCurrentStatus_0&&type_0==0}">
                                <div><img src="./img/1150-350-2.png" /></div>
                            </c:when>
                            <c:otherwise>
                                <div id="player0-video-container" style="margin: 0px auto;"></div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="row">
                    <div class="col-sm-6">
                        <c:choose>
                            <c:when test="${status == 0}">
                                <div><img src="./img/560-400-1.png" /></div>
                            </c:when>
                            <c:when test="${status == 1 && (liveCurrentStatus_1||type_1==1)}">
                                <div id="player1-video-container" style="margin: 0px auto;"></div>
                            </c:when>
                            <c:when test="${status == 1 && !liveCurrentStatus_1&&type_1==0}">
                                <div><img src="./img/560-400-2.png" /></div>
                            </c:when>
                            <c:otherwise>
                                <div id="player1-video-container" style="margin: 0px auto;"></div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="col-sm-6">
                        <c:choose>
                            <c:when test="${status == 0}">
                                <div><img src="./img/560-400-1.png" /></div>
                            </c:when>
                            <c:when test="${status == 1 && (liveCurrentStatus_2||type_2==1)}">
                                <div id="player2-video-container" style="margin: 0px auto;"></div>
                            </c:when>
                            <c:when test="${status == 1 && !liveCurrentStatus_2&&type_2==0}">
                                <div><img src="./img/560-400-2.png" /></div>
                            </c:when>
                            <c:otherwise>
                                <div id="player2-video-container" style="margin: 0px auto;"></div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:if>
            <c:if test="${liveSize == 4}">
                <div class="row">
                    <div class="col-sm-6">
                        <c:choose>
                            <c:when test="${status == 0}">
                                <div><img src="./img/560-400-1.png" /></div>
                            </c:when>
                            <c:when test="${status == 1 && (liveCurrentStatus_0||type_0==1)}">
                                <div id="player0-video-container" style="margin: 0px auto;"></div>
                            </c:when>
                            <c:when test="${status == 1 && !liveCurrentStatus_0&&type_0==0}">
                                <div><img src="./img/560-400-2.png" /></div>
                            </c:when>
                            <c:otherwise>
                                <div id="player0-video-container" style="margin: 0px auto;"></div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="col-sm-6">
                        <c:choose>
                            <c:when test="${status == 0}">
                                <div><img src="./img/560-400-1.png" /></div>
                            </c:when>
                            <c:when test="${status == 1 && (liveCurrentStatus_1||type_1==1)}">
                                <div id="player1-video-container" style="margin: 0px auto;"></div>
                            </c:when>
                            <c:when test="${status == 1 && !liveCurrentStatus_1&&type_1==0}">
                                <div><img src="./img/560-400-2.png" /></div>
                            </c:when>
                            <c:otherwise>
                                <div id="player1-video-container" style="margin: 0px auto;"></div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                <div>
                <div class="row">
                    <div class="col-sm-6">
                        <c:choose>
                            <c:when test="${status == 0}">
                                <div><img src="./img/560-400-1.png" /></div>
                            </c:when>
                            <c:when test="${status == 1 && (liveCurrentStatus_2||type_2==1)}">
                                <div id="player2-video-container" style="margin: 0px auto;"></div>
                            </c:when>
                            <c:when test="${status == 1 && !liveCurrentStatus_2&&type_2==0}">
                                <div><img src="./img/560-400-2.png" /></div>
                            </c:when>
                            <c:otherwise>
                                <div id="player2-video-container" style="margin: 0px auto;"></div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="col-sm-6">
                        <c:choose>
                            <c:when test="${status == 0}">
                                <div><img src="./img/560-400-1.png" /></div>
                            </c:when>
                            <c:when test="${status == 1 && (liveCurrentStatus_3||type_3==1)}">
                                <div id="player3-video-container" style="margin: 0px auto;"></div>
                            </c:when>
                            <c:when test="${status == 1 && !liveCurrentStatus_3&&type_3==0}">
                                <div><img src="./img/560-400-2.png" /></div>
                            </c:when>
                            <c:otherwise>
                                <div id="player3-video-container" style="margin: 0px auto;"></div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:if>
        </div>
    </c:if>

    <div class="row">
        <ul class="nav channels" role="tablist">
            <li role="presentation" class="channelTab active">
                <a href="#home" aria-controls="home" role="tab" data-toggle="tab">全部报道</a></li>
            <li role="presentation" class="channelTab">
                <a href="#profile" aria-controls="profile" role="tab" data-toggle="tab">聊天室</a></li>
        </ul>

        <!-- Tab panes -->
        <div class="tab-content">
            <div role="tabpanel" class="tab-pane active" id="home">
                <iframe frameborder="0px" style="width: 100%; height: 600px;" src="../../xy/DataMain.do?type=4&groupID=${DocIDs}&siteID=${siteID}"></iframe>
            </div>
            <div role="tabpanel" class="tab-pane" id="profile">
                <iframe frameborder="0px" style="width: 100%; height: 600px;" src="../../xy/DataMain.do?t=nisdis&DocIDs=${DocIDs}"></iframe>

            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-body" style="overflow: auto; height: 400px;">

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-info" id="getReplayUrlBtn" style="margin-right: 30px; ">获得回放地址</button>
                <button type="button" class="btn btn-primary" id="replaySaveBtn">保存</button>
                <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
            </div>
        </div>
    </div>
</div>

<div id="liveTemplate" style="display: none;">
    <div class="custform-span liveStream newLiveModel">
        <label class="custform-label liveLabel" style="margin-left: 46px;display: none;">直播流</label>
        <div class="custform-from-wrap out-border">
            <div><label class="custform-label custform-label-require">直播员</label>
                <div class="custform-from-wrap">
                    <input type="text" value="" class="custform-input  userName" readonly style="width:500px;background: #eee; color: darkgray;">
                    <input type="hidden" class="liveConfig"/>
                </div>
            </div>
            <div><label class="custform-label ">上传地址</label>
                <div class="custform-from-wrap">
                    <input type="text" value="" class="custform-input uploadUrl" readonly style="width:500px;background: #eee;color: darkgray;">
                </div>
            </div>

            <div><label class="custform-label ">播放地址（app）</label>
                <div class="custform-from-wrap">
                    <input type="text" value="" class="custform-input appLiveUrl" readonly style="width:500px;background: #eee;color: darkgray;">
                </div>
            </div>

            <div><label class="custform-label ">播放地址（浏览器）</label>
                <div class="custform-from-wrap">
                    <input type="text" value="" class="custform-input webLiveUrl" readonly style="width:500px;background: #eee;color: darkgray;">
                </div>
            </div>

            <div><label class="custform-label ">回放地址（app）</label>
                <div class="custform-from-wrap">
                    <input type="text" value="" class="custform-input appPlaybackUrl" style="width:500px;">
                </div>
            </div>

            <div><label class="custform-label ">回放地址（浏览器）</label>
                <div class="custform-from-wrap">
                    <input type="text" value="" class="custform-input webPlaybackUrl" style="width:500px;">
                </div>
            </div>

        </div>
    </div>
</div>


<script type="text/javascript" src="../script/jquery/jquery.min.js"></script>
<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
<script src="../script/Swiper-3.3.1/dist/js/swiper.min.js"></script>
<script src="./js/TcPlayer.js"></script>
<!-- Initialize Swiper -->
<script>
    var url0 = "${url_0}";
    var url1 = "${url_1}";
    var url2 = "${url_2}";
    var url3 = "${url_3}";
    var status = "${status}";
    var liveSize = "${liveSize}";
    var docID = "${DocIDs}";
    var docLibID = "${DocLibID}";
    var canCut = "${canCut}";
    var liveCurrentStatus_0 ="${liveCurrentStatus_0}";
    var liveCurrentStatus_1 ="${liveCurrentStatus_1}";
    var liveCurrentStatus_2 ="${liveCurrentStatus_2}";
    var liveCurrentStatus_3 ="${liveCurrentStatus_3}";
    var type_0 ="${type_0}";
    var type_1 ="${type_1}";
    var type_2 ="${type_2}";
    var type_3 ="${type_3}";

    function initReplayModal(){
        if(+status != 2||type==1) return;
        var $liveTemplate = $("#liveTemplate");
        var $myModal = $("#myModal").find(".modal-body");
        var _c = $("#a_config").val();
        var config = JSON.parse(_c);
        var videos = config.videos;

        if(videos && videos instanceof Array){
            for(var i = 0; vi = videos[i++];){
                $myModal.append($liveTemplate.html());
                var $new = $myModal.find(".newLiveModel");
                $new.find(".userName").val(vi.userName);
                $new.find(".uploadUrl").val(vi.uploadUrl);
                $new.find(".appLiveUrl").val(vi.appLiveUrl);
                $new.find(".webLiveUrl").val(vi.webLiveUrl);
                $new.find(".appPlaybackUrl").val(vi.appPlaybackUrl);
                $new.find(".webPlaybackUrl").val(vi.webPlaybackUrl);
                $new.find(".liveConfig").val(JSON.stringify(vi));
                $new.removeClass("newLiveModel");

            }
        }
    }

    function initSwiper(){
        var swiper = new Swiper('.swiper-container', {
            paginationClickable: true,
            pagination: '.swiper-pagination',
            nextButton: '.swiper-button-next',
            prevButton: '.swiper-button-prev',
            spaceBetween: 30,
            centeredSlides: true,
            autoplay: 2500,
            autoplayDisableOnInteraction: false,
            loop: true
        });

    }

    function initBtnEvent(){
        $("#addReplayUrlBtn").click(function(){
            $("#myModal").modal("show");
        });
        $("#shutDownLiveBtn").click(function(){
            var param = {
                docID: docID,
                docLibID: docLibID
            };

            $.ajax({
                url: "../../xy/nis/shutDownVideoLive.do",
                type: "post",
                dataType: "json",
                data: param,
                async: false,
                success: function(json){
                    if(json){
                        if(json.code == 0){
                            window.location.reload();
                        } else{
                            alert(json.msg);
                        }
                    }
                }
            });
        });

        $("#replaySaveBtn").click(function(){
            var _config = $("#a_config").val();
            _config = JSON.parse(_config);

            var _videos = [];
            $("#myModal").find(".liveStream").each(function(){
                var $this = $(this);
                var _video = $this.find(".liveConfig").val();
                _video = JSON.parse(_video);
                if(_video){
                    _video.appPlaybackUrl = $this.find(".appPlaybackUrl").val();
                    _video.webPlaybackUrl = $this.find(".webPlaybackUrl").val();
                }
                _videos.push(_video);
            });

            _config.videos = _videos;

            var param = {
                docID: docID,
                docLibID: docLibID,
                config: JSON.stringify(_config)
            };
            $.ajax({
                url: "../../xy/nis/saveReplayUrl.do",
                type: "post",
                dataType: "json",
                data: param,
                async: false,
                success: function(json){
                    if(json){
                        if(json.code == 0){
                            window.location.reload();
                        } else{
                            alert(json.msg);
                        }
                    }
                }
            });

        });

        $("#getReplayUrlBtn").click(function(){
            var param = {
                docID: docID,
                docLibID: docLibID
            };
            $.ajax({
                url: "../../xy/nis/queryReplayUrl.do",
                type: "post",
                dataType: "json",
                data: param,
                async: false,
                success: function(json){
                    console.info(json)
                }
            });
        });

        $(".cutbtn").click(function(){
            var obj = this;
            $(obj).attr("disabled", true);
            var status = $(obj).attr("data-channelstatus");
            status = + status ? 0 : 1;
            $.ajax({
                url: "../../xy/nis/cutStream.do",
                type: "post",
                dataType: "json",
                data: {
                    streamId : $(obj).attr("data-stream"),
                    status: status,
                    DocIDs: docID,
                    DocLibID: docLibID
                },
                async: false,
                success: function(json){
                    if(json.code == 0){
                        $(obj).attr("data-channelstatus", status);
                        if(status == 0){
                            $(obj).html("恢&nbsp;&nbsp;&nbsp;&nbsp;复");
                        }else{
                            $(obj).html("切&nbsp;&nbsp;&nbsp;&nbsp;断");
                        }
                    }else{
                        alert("发送请求出错！" + json.error);
                    }
                    $(obj).attr("disabled", false);

                }
            });
        });
    }
    function initPlayer(isLive){
        debugger;
        if((status == "1" && (liveCurrentStatus_0 == "true"||(liveCurrentStatus_0=="false"&&type_0==1))) || status == "2"){
            var player0 = new TcPlayer('player0-video-container', {
                m3u8_sd: url0,
                autoplay: true,
                live: isLive,
                width: '100%',
                height: '400'
            });
            window.player0 = player0;
        }
        if(((status == "1" && (liveCurrentStatus_0 == "true"||(liveCurrentStatus_0=="false"&&type_0==1))) || status == "2")&&liveSize>1){
            var player1 = new TcPlayer('player1-video-container', {
                m3u8_sd: url1,
                autoplay: true,
                live: isLive,
                width: '100%',
                height: '400'
            });
            window.player1 = player1;
        }
        if(((status == "1" && (liveCurrentStatus_0 == "true"||(liveCurrentStatus_0=="false"&&type_0==1))) || status == "2")&&liveSize>2){
            var player2 = new TcPlayer('player2-video-container', {
                m3u8_sd: url2,
                autoplay: true,
                live: isLive,
                width: '100%',
                height: '400'
            });
            window.player2 = player2;
        }
        if(((status == "1" && (liveCurrentStatus_0 == "true"||(liveCurrentStatus_0=="false"&&type_0==1))) || status == "2")&&liveSize>3){
            var player3 = new TcPlayer('player3-video-container', {
                m3u8_sd: url3,
                autoplay: true,
                live: isLive,
                width: '100%',
                height: '400'
            });
            window.player3 = player3;
        }
    }

    /*function initOnePlayer(isLive){
        if((status == "1" && liveCurrentStatus_0 == "true") || status == "2"){
            var player0 = new TcPlayer('video-container', {
                m3u8_sd: url0,
                autoplay: true,
                live: isLive,
                width: '100%',
                height: '400'
            });
            window.player0 = player0;
        }
    }*/

    /*function initTwoPlayer(isLive){
        /!**
         *
         * 视频类型播放优先级
         * mobile ：m3u8>mp4
         * PC ：RTMP>flv>m3u8>mp4
         *!/
        if((status == "1" && liveCurrentStatus_0 == "true") || status == "2"){
            var player0 = new TcPlayer('player0-video-container', {
                m3u8_sd: url0,
                autoplay: true,
                live: isLive,
                width: '100%',
                height: '400'
            });
            window.player0 = player0;
        }

        if((status == "1" && liveCurrentStatus_1 == "true") || status == "2"){
            var player1 = new TcPlayer('player1-video-container', {
                m3u8_sd: url1,
                autoplay: true,
                live: isLive,
                width: '100%',
                height: '400'
            });
            window.player1 = player1;
        }
    }*/

    $(function(){
        var isLive = status == "1";
        /*if(liveSize == "1"){
            initOnePlayer(isLive);
        } else if(liveSize == "2"){
            initTwoPlayer(isLive);
        }*/
        initPlayer(isLive);
        initSwiper();
        initBtnEvent();
        initReplayModal();

        /*if(canCut != "true"){
            $("#shutDownLiveBtn").attr("disabled", true);
            $("#shutDownLiveBtn").attr("title", "采集端停止采集才能结束直播！");
        }*/

    });


</script>
</body>
</html>