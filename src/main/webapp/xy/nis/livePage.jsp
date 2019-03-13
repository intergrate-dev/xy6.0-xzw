<%@page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>直播详情</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="../script/Swiper-3.3.1/dist/css/swiper.min.css">
    <style>
        #body_img .swiper-slide {
            background-repeat: no-repeat;
            background-size: 100% 100%;
            /*-moz-background-size: 100% 100%;
            color: rgb(223, 220, 220);
            text-align: center;
            font-size: 18px;
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
            align-items: center;*/
        }
    </style>
</head>
<body>
<div id="container">
    <div id="body">
        <div id="body_head">
            <div id="body_img" class="body_img">
                <div class="swiper-container">
                    <div class="swiper-wrapper">
                        <div class="swiper-slide" :style="webBanner"></div>
                        <div class="swiper-slide" :style="appBanner"></div>
                    </div>
                    <div class="swiper-pagination"></div>
                    <div class="swiper-button-next"></div>
                    <div class="swiper-button-prev"></div>
                </div>
            </div>
            <div id="body_img_alt" class="body_img_alt">
                <p id="p1" v-text="title"></p>
            </div>
        </div>
        <div id="body_content">
            <div id="body_content_left">
                <div id="body_content_title">
                    <img src="img/live.png" alt="">
                    <div id="live_chat">
                        <ul>
                            <li id="live_tab">
                                <a id="live_tab_a" href="javascript:void(0)">直播</a></li>
                        </ul>
                    </div>
                </div>
                <!--live部分-->
                <div id="live_content">
                    <div class="artContent">
                        <div v-for="item in contentList">
                            <div id="time" class="time" v-show="item.isIndex"><span v-text="item.date"></span></div>
                            <div class="specific_content">
                                <div class="specific_text_content">
                                    <div class="host"><p><span v-text="item.user"></span></p></div>
                                    <div class="left_time" v-text="item.time"></div>
                                    <div class="text">
                                        <p v-text="item.content"></p></div>
                                    <div class="text_img">
                                        <ul class="clearfix">
                                            <li v-for="liItem in item.attachments">
                                                <img v-if="liItem.format=='jpg'" :src="liItem.url" :class="liItem.class">
                                                <video v-if="liItem.format=='mp4'" :src="liItem.url" style="width:93%;height: 280px;margin:0 auto;display:block;" controls="controls"></video>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div id="nothing_news" class="nothing_news clearfix" @click="addMore">
            <p>点击加载更多</p>
        </div>
    </div>
</div>

<script src="./js/vue.js"></script>
<script src="./js/vue-resource.js"></script>
<script src="../script/Swiper-3.3.1/dist/js/swiper.min.js"></script>
<script>
    window.onload = function(){
        new Vue({
            el: '#container',
            data: {
                title: "",
                titleImage: "",
                webBanner: "",
                appBanner: "",
                contentList: [],
                start: 0
            },
            created: function(){
                this.addMore();
                this.initSwiper();
            },
            methods: {
                initSwiper: function(){
                    var swiper = new Swiper('.swiper-container', {
                        paginationClickable: true,
                        pagination: '.swiper-pagination',
                        nextButton: '.swiper-button-next',
                        prevButton: '.swiper-button-prev',
                        spaceBetween: 30,
                        centeredSlides: true,
                        autoplay: 2500,
                        autoplayDisableOnInteraction: false
                    });
                },
                addMore: function(){
                    this.$http.get('../../api/app/liveDetail.do', {
                        id: this.getQueryString("id"),
                        page: this.start,
                        siteID: 1
                    }).then(function(res){
                        if(res.data && res.data.list){
                            if(this.start == 0){
                                var main = res.data.main;
                                this.title = main.title;
                                this.appBanner = "background-image: url('" + main.config.appBannerUrl + "')";
                                this.webBanner = "background-image: url('" + main.config.webBannerUrl + "')";
                            }
                            this.assembleContentList(res.data.list);
                            this.start ++;
                        }
                    });
                },
                assembleContentList: function(list){
                    //把time 当成key list 当value
                    var resultList = [];
                    for(var i = 0, li = null; li = list[i++];){
                        var attachmentsList = [];   //附件list
                        var date = li.publishtime.substr(5, 6).trim();
                        var time = li.publishtime.substr(11, 5).trim();

                        //如果有附件，需要添加附件的list
                        if(li.attachments && li.attachments instanceof Array){
                            attachmentsList = this.assembleAttachments(li.attachments);
                        }

                        resultList.push({
                            user: li.user,
                            date: date,
                            time: time,
                            isIndex: false,
                            content: li.content,
                            attachments: attachmentsList
                        });
                    }

                    this.contentList = this.contentList.concat(resultList);
                    // 重新设置日期索引
                    this.assignIndex(this.contentList);

                },
                assembleAttachments: function(attachments){
                    var attachmentsList = [];
                    var _class = attachments.length > 1 ? "small" : "big";
                    for(var j = 0, ljo = null; ljo = attachments[j++];){
                        var _format = ljo.url.substring(ljo.url.lastIndexOf(".") + 1);
                        attachmentsList.push({
                            class: _class,
                            format: _format,
                            url: ljo.url
                        });
                    }
                    return attachmentsList;
                },
                assignIndex: function(list){
                    var nowDate = null;
                    for(var i in list){
                        if(list[i].date != nowDate){
                            nowDate = list[i].date;
                            list[i].isIndex = true;
                        } else{
                            list[i].isIndex = false;
                        }
                    }
                },
                getQueryString: function(name){
                    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
                    var r = window.location.search.substr(1).match(reg);
                    if(r != null)return unescape(r[2]);
                    return null;
                }
            }
        });
    };
</script>
</body>
</html>