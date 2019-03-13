<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${SYS_TOPIC}</title>
    <link rel="stylesheet" href="css/css.css" type="text/css">
    <link href="css/style.css" type="text/css" rel="stylesheet" />
</head>
<body>
<div id="container">
    <div id="header">
        <p></p>
    </div>
    <div id="content">
        <div id="content_head">
            <div  id="content_title">
                <p><span id="title-words">${SYS_TOPIC}</span></p>
            </div>
            <div  id="content_message">
                <div id="first-msg"><p>提问人：<span id="user">${a_realName}</span></p></div>
                <div id="second-msg"><p>手机号：<span id="phone">${a_phone}</span></p></div>
                <div id="third-msg"><p>提问地域：<span id="ask-area">${a_region}</span></p></div>
                <div id="fourth-msg"><p>提问职能部门：<span id="ask-branch">${a_askTo}</span></p></div>
                <div id="fifth-msg"><p>时间：<span id="time">${SYS_CREATED}</span></p></div>
                <%--<div id="six-msg"><p>状态：<span id="state">待审核</span></p></div>--%>
            </div>
        </div>
        <div id="content_body">
            <div id="content_body_word">
                <p id="content-words" >${a_content}</p>
            </div>
            <div id="content_body_banner">
                <div class="banner" id="banner">
                    <div class="large_box" id="large_box">
                        <ul id="first-ul">

                            <c:forEach items="${pics}" varStatus="i" var="item">
                                <li id="f${i.index}-li">
                                    <img src="${item}" width="597" height="300">
                                </li>
                            </c:forEach>

                            <%--<li id="f1-li">
                                <img  id="first-large-img" src="img/img1.jpg" width="597" height="300">
                            </li>
                            <li id="f2-li">
                                <img id="second-large-img" src="img/img2.jpg" width="597" height="300">
                            </li>
                            <li id="f3-li">
                                <img id="third-large-img" src="img/img3.jpg" width="597" height="300">
                            </li>
                            <li id="f4-li">
                                <img id="fourth-large-img" src="img/img4.jpg" width="597" height="300">
                            </li>
                            <li id="f5-li">
                                <img id="fifth-large-img" src="img/img5.jpg" width="597" height="300">
                            </li>
                            <li id="f6-li">
                                <img id="sixth-large-img" src="img/img6.jpg" width="597" height="300">
                            </li>--%>
                        </ul>
                    </div>
                    <div id="small_box_container">
                        <div class="small_box">
                            <span class="btn left_btn" id="left_btn"></span>
                            <div class="small_list">
                                <ul id="second-ul">
                                    <c:forEach items="${pics}" varStatus="i" var="item">
                                        <li <c:if test="${i.index == 0}" >class="on"</c:if> id="f${i.index}-small-li">
                                            <img src="${item}" width="110" height="73">
                                            <div class="bun_bg"></div>
                                        </li>
                                    </c:forEach>

                                    <%--<li class="on" id="f1-small-li">
                                        <img id="f1-img" src="img/thum1.jpg" width="110" height="73">
                                        <div class="bun_bg"></div>
                                    </li>
                                    <li id="f2-small-li">
                                        <img id="f2-img" src="img/thum2.jpg" width="110" height="73">
                                        <div class="bun_bg"></div>
                                    </li>
                                    <li id="f3-small-li">
                                        <img  id="third-img" src="img/thum3.jpg" width="110" height="73">
                                        <div class="bun_bg"></div>
                                    </li>
                                    <li id="f4-small-li">
                                        <img id="fourth-img" src="img/thum4.jpg" width="110" height="73">
                                        <div class="bun_bg"></div>
                                    </li>
                                    <li id="f5-small-li">
                                        <img id="fifth-img" src="img/thum5.jpg" width="110" height="73">
                                        <div class="bun_bg"></div>
                                    </li>
                                    <li id="f6-small-li">
                                        <img id="sixth-img" src="img/thum6.jpg" width="110" height="73">
                                        <div class="bun_bg"></div>
                                    </li>--%>
                                </ul>
                            </div>
                            <span class="btn right_btn" id="right_btn"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div id="footer" style="display: block; margin-bottom: 20px;">
        <div id="footer_head">
            <span id="answer">官方回复</span>
        </div>
        <div id="footer_body">
            <div id="footer-content" style=" margin-bottom: 20px;">
                <div id="content-text" >${a_content}</div>
            </div>
            <div id="btn" style="display: none;">
                <button id="on_btn" type="button" value="确定">确定</button>
                <button id="cancel_btn" type="button" value="放弃">放弃</button>
            </div>
        </div>
    </div>
</div>
<script src="js/jquery-1.10.2.min.js" type="text/javascript"></script>
<script src="js/carousel.min.js" type="text/javascript"></script>
<script type="text/javascript">

    $(function(){
        /* 商品轮播图（带缩略图的轮播效果） */
        $(".banner").thumbnailImg({
            large_elem: ".large_box",
            small_elem: ".small_list",
            left_btn: ".left_btn",
            right_btn: ".right_btn",
            vis: 5,
            speed:0
        });
    });
</script>
</body>
</html>