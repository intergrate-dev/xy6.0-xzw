$(function(){
    xy_live.init();
});

xy_live = {
    init: function(){
        $("#btnSave").unbind().click(xy_live.doSave);
        $("#btnCancel").click(xy_live.doCancel);

        //新帖（话题或继续报道），需要自动设置直播员
        if(article.isNew){
            $("#a_siteID").val(article.siteID);
            $("#SYS_AUTHORS").val(article.author);
        }
        //不是主贴，不显示标题、位置、是否关闭评论
        if($("#parentID").val() > 0){
            $("#SPAN_SYS_TOPIC").parent().parent().hide();
            $("#SPAN_a_location").parent().parent().hide();
            $("#SPAN_a_discussClosed").parent().parent().hide();

            if(article.isNew) $("#SYS_TOPIC").val(article.topic);
        }
        //附件
        /*var atts = $("#a_attachments").val();
         if(atts)
         atts = eval("(" + atts + ")");
         else
         atts = {pics: [], videos: []};
         //atts.pics:图片列表，atts.videos：视频列表
         xy_live.initPics(atts.pics);
         xy_live.initVideos(atts.videos);*/

        xy_live.initEvent();

    },
    //修改操作时，先把已有图片显示出来
    initPics: function(pics){
        var datas = [];
        for(var i = 0; i < pics.length; i++){
            var data = {
                "isIndex": false,
                "path": pics[i],
                "pic": "",
                "content": ""
            };
            datas.push(data);
        }
        pic_group.setPicInfo(datas);
    },
    //修改操作时，先把已有视频显示出来
    initVideos: function(videos){
        var datas = [];
        //视频只有Url地址，模拟成视频显示时需要的对象
        for(var i = 0; i < videos.length; i++){
            var v = {};
            v.url = videos[i];
            v.urlApp = videos[i];

            datas.push(v);
        }
        video_form.setVideoInfo(datas);
    },
    //保存提交
    doSave: function(){
        debugger;
        var beginTime=$("#a_startTime").val();
        var endTime=$("#a_endTime").val();
        //确保开始时间小于结束时间
        var beginTimeNum= new Date(beginTime);
        var endTimeNum= new Date(endTime);
        if(beginTimeNum.getTime()>endTimeNum.getTime()){
            alert('“直播开始时间”晚于“直播结束时间”，请重新选择！');
            return
        };
        $("#a_startTime").attr("class", "custform-input validate[required,custom[dateTimeFormat1]]");
        $("#a_endTime").attr("class", "custform-input validate[required,custom[dateTimeFormat1]]");

        var _json = xy_live.collectConfig();

        var paths = [];
        var _type = $("input:radio[name='a_type']:visible:checked").val();
        if(_type == 0 || _type == "0"){
            var _ap = $("#appPicSrcHidden").val();
            if(_ap && $.trim(_ap) != ""){
                paths.push(_ap);
            }
            _ap = $("#pcPicSrcHidden").val();
            if(_ap && $.trim(_ap) != ""){
                paths.push(_ap);
            }

            if(paths.length == 0){
                alert("图文直播需要上传头图！");
                return;
            }
        }else{
            if(!_json || !_json.videos || _json.videos.length == 0){
                alert("视频直播需要直播线路！");
                return;
            }
        }

        var _streamId = "";
        $("#liveUrlDiv").find(".streamId").each(function(){
            if($(this).val()){
                _streamId += $(this).val().trim() + ",";
            }
        });

        $("#a_streamIDs").val(_streamId);

        $.ajax({
            url: "../../xy/pic/checkExtractIsFinished.do",
            dataType: "json",
            data: {
                paths: JSON.stringify(paths)
            },
            async: false,
            success: function(json){
                if(json){
                    if(json.code == 0){
                        if(json.isFinished){
                            if(!$("#form").validationEngine("validate")){
                                // 验证提示
                                $("#form").validationEngine("updatePromptsPosition");
                                return false;
                            }

                            $("#form").submit();
                        } else{
                            alert("图片抽图还未完成！请稍后再试！");
                        }
                    } else{
                        alert(json.msg);
                    }
                }
            }
        });
        $("#liveModel").find(".line_select").find("option").css('display','block');

    },
    //退出按钮
    doCancel: function(){
        debugger;
        $("#liveModel").find(".line_select").find("option").css('display','block');

        window.onbeforeunload = null;
        var dataUrl = "../../e5workspace/after.do?UUID=" + $("#form #UUID").val();
        window.location.href = dataUrl;
    },
    //保存线路设置
    lineSave:function(event){
        debugger;
        var o=event.target;
        var detailLine=$(o).parent().parent().parent()
            ;
        var briefLine=$(detailLine).next();

        var text=detailLine.find(".line_select").find("option:selected").text();
        var value=detailLine.find(".line_select").val();
        var lineName=detailLine.find(".line_name").val();
        var lineUser=detailLine.find(".text-label").html();

        var b=$(detailLine).parent();
        if(b.hasClass("TXliveStream"))
            b.addClass("TXAssigned");
        if(b.hasClass("OtherliveStream"))
            b.addClass("OtherAssigned");

        briefLine.find(".lineID").html(text);
        briefLine.find(".lineName").html(lineName);
        briefLine.find(".lineUser").html(lineUser);
        //briefLine.find(".streamRevise").click(xy_live.lineRevise(event));

        detailLine.hide();
        briefLine.show();
        if($("#liveUrlDiv").find(".liveStream").length <4){
            $("#createTXLiveBtn").removeAttr("disabled");
            $("#createOtherLiveBtn").removeAttr("disabled");
        }
        $("#liveModel").find(".line_select option[value='"+value+"']").css('display','none');
    },
    lineRevise:function(event){
        debugger;
        var prief=$(event.target.parentNode);
        var detail=$(prief.prev("div"));
        var value=detail.find(".line_select").val();
        prief.hide();
        detail.show();

        if($(event.target.parentNode.parentNode).hasClass("TXAssigned"))
            $(event.target.parentNode.parentNode).removeClass("TXAssigned");
        if($(event.target.parentNode.parentNode).hasClass("OtherAssigned"))
            $(event.target.parentNode.parentNode).removeClass("OtherAssigned");

        $("#liveModel").find(".line_select option[value='"+value+"']").css('display','block');
    },

    //保存前组织图片数据
    getPics: function(){
        var picList = new Array();
        $("#ul1").find("img:not(.imgsize1)").each(function(){
            picList.push(decodeURI(xy_live.getImgSrc(this.src)));
        });
        return picList;
    },
    getImgSrc: function(_path){
        if(_path){
            _path = _path.substr(_path.lastIndexOf('image.do?path=')).replace("image.do?path=", "");
            //可能显示的是.0小图
            if(_path.substring(_path.length - 2, _path.length) == ".0")
                _path = _path.substring(0, _path.length - 2);
            else if(_path.substring(_path.length - 6, _path.length) == ".0.jpg")
                _path = _path.substring(0, _path.length - 6);
        }

        return _path || "";
    },
    //保存前组织视频数据
    getVideos: function(){
        var vidoes = new Array();

        var vs = video_form.getVideoInfo();
        if(vs && vs.length > 0){
            for(var i = 0; i < vs.length; i++){
                vidoes.push(vs[i].urlApp);
            }
        }
        return vidoes;
    },

    initEvent: function(){
        xy_live.initLiveRadioEvent();
        xy_live.initCreateLiveBtnEvent();
        xy_live.initChooseUserBtnEvent();
        xy_live.initliveDeleteBtnEvent();
        xy_live.initValidateClass();
        xy_live.initDatePicker();
        xy_live.initPicPlugin();
        xy_live.initTextext($("#userTA"));
        xy_live.initBtnEvent();

        xy_live.initLiveStatus();

        xy_live.reset();
        xy_live.resetTextext();

    },
    reset: function(){
        debugger;
        var _type = $("input:radio[name=a_type]:visible:checked").val();
        if(_type == 0){
            xy_live.resetPic();
            $("#livePicAreaDiv").show();
            $("#hostDiv").hide();
            $("#liveAreaDiv").hide();
        } else{
            xy_live.resetUrl();

            var isFirst = $("#liveUrlDiv").find(".liveStream").length == 0;
            if(isFirst){
                xy_live.createLive("TX");
            }
            $("#livePicAreaDiv").hide();
            $("#liveAreaDiv").show();
            $("#hostDiv").hide();
        }
    },
    resetTextext: function(){
        var json = $("#a_config").val();
        if(!json && $.trim(json) == ""){
            return;
        }
        //转成json对象
        json = JSON.parse(json);
        if(!json || !json.users){
            return;
        }
        var users = JSON.parse(json.users);
        if(users && users instanceof Array && users.length > 0){
            $('#userTA').textext()[0].tags().addTags(users);
        }

    },
    initLiveStatus: function(){
        /*if(article.isNew){
            $("input:radio[name=a_status][value='0']").attr("checked", true);
            $("input:radio[name=a_status]").closest("tr").hide();
        }*/
    },
    initTextext: function(selector, isOne){
        selector.textext({
            plugins: 'tags autocomplete ajax',
            ajax: {
                url: '../../xy/nis/querySuggestion.do',
                dataType: 'json',
                cacheResults: true
            },
            html: {
                Wrap: '<div class="label label-default"></div>'
            },
            ext: {
                itemManager: {
                    items: [],  // A custom array that will be used to lookup id
                    stringToItem: function(str){
                        //Lookup id for the given str from our custom array
                        var _id,_code,_name, _show;
                        for(var i = 0; i < this.items.length; i++)
                            if(this.items[i].showName == str){
                                _id = this.items[i].userId;
                                _code = this.items[i].userCode;
                                _name =this.items[i].userName;
                                break;
                            }
                        return {showName: str, userName: _name, userId: _id, userCode: _code};
                    },

                    itemToString: function(item){
                        //Push items to our custom object
                        if(isOne){
                            selector.siblings(".text-tags").find(".text-remove").each(function(){
                                $(this).click();
                            });
                        }
                        this.items.push(item);
                        return item.showName;

                    },
                    compareItems: function(item1, item2){
                        return item1.showName == item2.showName;
                    }
                }
            }

        });

    },
    initPicPlugin: function(){
        var pcParam = {
            uploadUrl: "../../xy/upload/uploadFileF.do",
            showRemove: false,
            showUpload: false,
            showSort: true,
            allowedFileExtensions:['jpg', 'jpeg', 'gif', 'bmp', 'png'],
            maxFileCount: 1,
            minFileCount: 1,
            autoCommit: true,
            rootUrl: '../../'

        };

        pcParam.fileUploaded = function(e, data){
            $("#pcPicSrcHidden").val(data.path);
        };
        //删除预览图
        pcParam.fileDeleted = function(e, data){
            $("#pcPicSrcHidden").val("");
        };

        //删除已上传的图片
        pcParam.fileRemove = function(e, data){
            $("#pcPicSrcHidden").val("");
        };

        xy_live.pcPicUpload = new Upload("#pcPicInput", pcParam);
        //xy_live.pcPicUpload.resetWidth();

        var appParam = {
            uploadUrl: "../../xy/upload/uploadFileF.do",
            showRemove: false,
            showUpload: false,
            showSort: true,
            allowedFileExtensions:['jpg', 'jpeg', 'gif', 'bmp', 'png'],
            maxFileCount: 1,
            minFileCount: 1,
            autoCommit: true,
            rootUrl: '../../'
        };

        appParam.fileUploaded = function(e, data){
            $("#appPicSrcHidden").val(data.path);
        };
        //删除预览图
        appParam.fileDeleted = function(e, data){
            $("#appPicSrcHidden").val("");
        };

        //删除已上传的图片
        appParam.fileRemove = function(e, data){
            $("#appPicSrcHidden").val("");
        };

        xy_live.appPicUpload = new Upload("#appPicInput", appParam);
        //xy_live.appPicUpload.resetWidth();
    },
    initBtnEvent: function(){
        $("#appPicUploadBtn").click(function(){
            xy_live.type = "appImg";
            xy_live.picDialog();
            xy_live.dialog.show();
        });
        $("#webPicUploadBtn").click(function(){
            xy_live.type = "webImg";
            xy_live.picDialog();
            xy_live.dialog.show();
        });
    },
    buildPicJson: function(json){
        var appBanner = json.appBanner;
        var webBanner = json.webBanner;

        if(webBanner){
            xy_live.pcPicUpload.setOption({
                initialPreview: [
                    "../../xy/image.do?path=" + webBanner
                ],
                initialPreviewConfig: [
                    {width: "120px", url: "../../xy/upload/deletePreviewThumb.do", key: 1}
                ]
            });
        }
        if(webBanner && $.trim(webBanner) != ""){
            $("#pcPicSrcHidden").val(webBanner);
        }

        if(appBanner){
            xy_live.appPicUpload.setOption({
                initialPreview: [
                    "../../xy/image.do?path=" + appBanner
                ],
                initialPreviewConfig: [
                    {width: "120px", url: "../../xy/upload/deletePreviewThumb.do", key: 1}
                ]
            });
        }

        if(appBanner && $.trim(appBanner) != ""){
            $("#appPicSrcHidden").val(appBanner);
        }
    },
    resetPic: function(){
        var _type = $("input:radio[name=a_type]:visible:checked").val();
        //如果是1的话
        if(+_type != 0){
            return;
        }
        var json = $("#a_config").val();
        if(!json && $.trim(json) == ""){
            return;
        }
        //转成json对象
        json = JSON.parse(json);
        xy_live.buildPicJson(json);
    },
    resetUrl: function(){
        var _type = $("input:radio[name=a_type]:visible:checked").val();
        //如果是1的话
        if(+_type != 1){
            return;
        }
        var json = $("#a_config").val();
        if(!json && $.trim(json) == ""){
            return;
        }
        //转成json对象
        json = JSON.parse(json);
        xy_live.buildUrlJson(json.videos);
        $("#liveAreaDiv").show();
    },
    buildUrlJson: function(jsonArr){
        var _html = $("#liveModel").html();
        var $container = $("#liveUrlDiv");
        for(var i = 0, ji = null; ji = jsonArr[i++];){
            $container.append(_html);
            var $newModel = $container.find(".newLiveModel");
            if(!ji.type||ji.type==0){
                $newModel.find(".uploadUrl").val(ji.uploadUrl);
                $newModel.find(".userName").val(ji.userName);
                $newModel.find(".userID").val(ji.userID);
                $newModel.find(".streamId").val(ji.streamID);

                var _ta = $newModel.find(".userNameTA");
                xy_live.initTextext(_ta, true);
                _ta.textext()[0].tags().addTags([{
                    userId: ji.userID,
                    userName: ji.userName,
                    userCode: ji.userCode,
                    showName: ji.userName + "(" + ji.userCode + ")"
                }]);

                $newModel.find(".live_user").show();
                $newModel.find(".live_uploadUrl").show();
                $newModel.find(".live_backUrlApp").hide();
                $newModel.find(".live_backUrlWeb").hide();

                $newModel.find(".appLiveUrl").attr("readonly","true");
                $newModel.find(".webLiveUrl").attr("readonly","true");
                $newModel.addClass("TXliveStream");
                $newModel.addClass("TXAssigned");
            }else{
                $newModel.find(".streamId").val(ji.streamID);
                $newModel.find(".appPlaybackUrl").val(ji.appPlaybackUrl);
                $newModel.find(".webPlaybackUrl").val(ji.webPlaybackUrl);

                $newModel.find(".live_user").hide();
                $newModel.find(".live_uploadUrl").hide();
                $newModel.find(".live_backUrlApp").show();
                $newModel.find(".live_backUrlWeb").show();

                $newModel.find(".appLiveUrl").removeAttr("readonly");
                $newModel.find(".webLiveUrl").removeAttr("readonly");
                $newModel.addClass("OtherliveStream");
                $newModel.addClass("OtherAssigned");

            }

            $newModel.find(".webLiveUrl").val(ji.webLiveUrl);
            $newModel.find(".appLiveUrl").val(ji.appLiveUrl);

            $newModel.find(".line_select option[value='"+ji.line+"']").attr("selected","selected");
            $newModel.find(".line_name").val(ji.lineName);

            $newModel.find(".lineID").html( $newModel.find(".line_select ").find("option:selected").text());
            $newModel.find(".linename").html(ji.lineName);
            // $newModel.find(".lineUser").html(ji.userName + "(" + ji.userCode + ")");
            if (ji.userName && ji.userCode) {
                $newModel.find(".lineUser").html(ji.userName + "(" + ji.userCode + ")");
            } else {
                $newModel.find(".lineUser").html('');
            }

            //$newModel.addClass("assigned");
            $newModel.removeClass("newLiveModel");
            $newModel.find(".detail").hide();
            $newModel.find(".brief").show();

            $("#liveModel").find(".line_select option[value='"+ji.line+"']").css('display','none');
        }
    },
    initDatePicker: function(){
        $('#a_endTime').datetimepicker({
            language: 'zh-CN',
            weekStart: 1,
            todayBtn: 1,
            autoclose: 1,
            todayHighlight: 1,
            startView: 2,
            bootcssVer:3,
            forceParse: 0,
            showMeridian: 1,
            format: 'yyyy-mm-dd hh:ii:ss',
            minView: 0
        }).on('changeDate', function(ev){
            var $this = $("#a_endTime");
            $this.attr("class", "custform-input validate[required,custom[dateTimeFormat1]]");
            var str = $this.val();
            str = str.substring(0, str.length - 2) + "00";
            $this.val(str);
            //$('#a_startTime').datetimepicker('setEndDate', $this.val());
        });

        $('#a_startTime').datetimepicker({
            language: 'zh-CN',
            weekStart: 1,
            todayBtn: 1,
            autoclose: 1,
            todayHighlight: 1,
            startView: 2,
            bootcssVer:3,
            forceParse: 0,
            showMeridian: 1,
            format: 'yyyy-mm-dd hh:ii:ss',
            minView: 0
        }).on('changeDate', function(ev){
            var $this = $("#a_startTime");
            $this.attr("class", "custform-input validate[required,custom[dateTimeFormat1]]");
            var str = $this.val();
            str = str.substring(0, str.length - 2) + "00";
            $this.val(str);
            //$('#a_endTime').datetimepicker('setStartDate', $this.val());
        });
        // 对Date的扩展，将 Date 转化为指定格式的String
    // 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
    // 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
    // 例子：
    // (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
    // (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
        Date.prototype.Format = function(fmt) { //author: meizz
            var o = {
                "M+" : this.getMonth()+1,                 //月份
                "d+" : this.getDate(),                    //日
                "h+" : this.getHours(),                   //小时
                "m+" : this.getMinutes(),                 //分
                "s+" : this.getSeconds(),                 //秒
                "q+" : Math.floor((this.getMonth()+3)/3), //季度
                "S"  : this.getMilliseconds()             //毫秒
            };
            if(/(y+)/.test(fmt))
                fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
            for(var k in o)
                if(new RegExp("("+ k +")").test(fmt))
                    fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
            return fmt;
        }
        $('#a_startTime').datetimepicker('setStartDate', new Date().Format("yyyy-MM-dd HH:mm") + ":00");
    },
    initValidateClass: function(){
        $("#a_startTime").attr("class", "custform-input");
        $("#a_endTime").attr("class", "custform-input");
        $("#a_startTime").attr("onclick", "");
        $("#a_endTime").attr("onclick", "");
        //$("#a_endTime").attr("class", "custform-input validate[required,custom[dateTimeFormat2]]");
    },
    initLiveRadioEvent: function(){
        $("[name=a_type]:visible").change(function(){
            var _$this = $(this);
            var _type = _$this.val();
            if(+_type == 1){
                var isFirst = $("#liveUrlDiv").find(".liveStream").length == 0;
                if(isFirst){
                    xy_live.createLive("TX");
                }

                $("#liveAreaDiv").show();
                $("#hostDiv").hide();
                $("#livePicAreaDiv").hide();
            } else{
                $("#liveAreaDiv").hide();
                $("#hostDiv").hide();
                $("#livePicAreaDiv").show();
            }
        });
    },
    //创建流
    initCreateLiveBtnEvent: function(){
        $("#createTXLiveBtn").click(function(){
            xy_live.createLive("TX");
        });
        $("#createOtherLiveBtn").click(function(){
            xy_live.createLive("OTHER");
        });
    },
    createLive: function(arg){
        var $liveUrlDiv = $("#liveUrlDiv");
        var isFirst = $liveUrlDiv.find(".liveStream").length == 0;
        
        if($liveUrlDiv.find(".liveStream").length >= 4){
            return;
        }
        if(arg=="TX"){
            $("#liveModel").find(".live_user").show();
            $("#liveModel").find(".live_uploadUrl").show();
            $("#liveModel").find(".live_backUrlApp").hide();
            $("#liveModel").find(".live_backUrlWeb").hide();

            $("#liveModel").find(".appLiveUrl").attr("readonly","true");
            $("#liveModel").find(".webLiveUrl").attr("readonly","true");
        }else{
            $("#liveModel").find(".live_user").hide();
            $("#liveModel").find(".live_uploadUrl").hide();
            $("#liveModel").find(".live_backUrlApp").show();
            $("#liveModel").find(".live_backUrlWeb").show();

            $("#liveModel").find(".appLiveUrl").removeAttr("readonly");
            $("#liveModel").find(".webLiveUrl").removeAttr("readonly");
        }
        $liveUrlDiv.append($("#liveModel").html());
        if(isFirst){
            $liveUrlDiv.find(".liveLabel").show();
        }
        if(arg=="TX"){
            xy_live.initTextext($liveUrlDiv.find(".newLiveModel").find(".userNameTA"), true);
            $liveUrlDiv.find(".newLiveModel").addClass("TXliveStream");
        }else{
            $liveUrlDiv.find(".newLiveModel").addClass("OtherliveStream");
            var param = {
                streamId: $("#DocID").val(),
                extDate: $("#a_endTime").val()
            };
            $.ajax({
                url: "../../xy/nis/getStreamID.do",
                type: "get",
                data: param,
                async: false,
                success: function(data){
                    debugger;
                    $liveUrlDiv.find(".newLiveModel").find(".streamId").val(data);
                    $("#a_streamIDs").val($.trim($("#a_streamIDs").val()) + data + ",");
                }
            });
        }
        //$liveUrlDiv.find(".newLiveModel").addClass("liveStream_"+$liveUrlDiv.find(".liveStream").length)
        //$liveUrlDiv.find(".newLiveModel").find(".btnStreamSave").click(xy_live.lineSave(event));
        $liveUrlDiv.find(".newLiveModel").removeClass("newLiveModel");
        //if($liveUrlDiv.find(".liveStream").length >= 4){
        $("#createTXLiveBtn").attr("disabled", "disabled");
        $("#createOtherLiveBtn").attr("disabled", "disabled");
        //}
    },
    createOtherLive: function(){
        var $liveUrlDiv = $("#liveUrlDiv");
        var isFirst = $liveUrlDiv.find(".liveStream").length == 0;

        if($liveUrlDiv.find(".liveStream").length >= 4){
            return;
        }

        $liveUrlDiv.append($("#liveModel").html());
        if(isFirst){
            $liveUrlDiv.find(".liveLabel").show();
        }
        xy_live.initTextext($liveUrlDiv.find(".newLiveModel").find(".userNameTA"), true);

        $liveUrlDiv.find(".newLiveModel").removeClass("newLiveModel");
        if($liveUrlDiv.find(".liveStream").length >= 4){
            $("#createTXLiveBtn").attr("disabled", "disabled");
            $("#createOtherLiveBtn").attr("disabled", "disabled");
        }
    },
    initliveDeleteBtnEvent: function(){
        $("#liveUrlDiv").on("click", ".liveDeleteBtn", function(){
            $(this).closest(".liveStream").remove();
            $("#createTXLiveBtn").attr("disabled", false);
            $("#createOtherLiveBtn").attr("disabled", false);
        });
    },
    initChooseUserBtnEvent: function(){
        //选择完用户之后, 1. 给input赋值；2.创建url
        $("#liveUrlDiv").on("click", ".liveChooseBtn", function(){
            var _$this = $(this);
            //做标记
            var $container = _$this.closest(".liveStream");
            $(".targetStream").removeClass("targetStream");
            $container.addClass("targetStream");
            $container.addClass("assigned");

            if(xy_live.checkTime($("#a_endTime").val())){
                return;
            }

            //1. 为user赋值
            var isAssigned = xy_live.assignUser($container);
            if(isAssigned){
                alert("没有填写直播员！");
                return;
            }
            xy_live.queryUrls($container, $("#DocID").val(), $("#a_endTime").val());
        });

    },
    //1. 为input赋值
    assignUser: function($container){
        var _user = $container.find(".userNameTA").textext()[0].hiddenInput().val();
        _user = JSON.parse(_user);
        _user = _user[0];
        if(!_user || !_user.userId){
            return true;
        }

        return false;
    },
    queryUrls: function($container, streamID, extDate){
        if(xy_live.checkTime(extDate)){
            return;
        }
        var param = {
            streamId: streamID,
            extDate: extDate
        };
        $.ajax({
            url: "../../xy/nis/getLiveUrl.do",
            type: "post",
            dataType: "json",
            data: param,
            async: false,
            success: function(json){
                if(json){
                    if(json.code == 0){
                        xy_live.handleUrlJson($container, json.url);
                    } else{
                        alert(json.msg);
                    }
                }
            }
        });
    },
    checkTime: function(extDate){
        if(!extDate){
            alert("直播结束时间无效！请先选择结束时间!");
            return true;
        }

        if($.trim(extDate) == ""){
            alert("直播结束时间为空！");
            return true;
        }

        var reg = new RegExp(/^((((1[6-9]|[2-9]\d)\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\d|3[01]))|(((1[6-9]|[2-9]\d)\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\d|30))|(((1[6-9]|[2-9]\d)\d{2})-0?2-(0?[1-9]|1\d|2[0-8]))|(((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\d):[0-5]?\d:[0-5]?\d$/);
        if(!reg.test(extDate)){
            alert("直播结束时间格式不对！");
            return true;
        }

        return false;
    },
    handleUrlJson: function($container, url){
        $container.find(".uploadUrl").val(url.uploadUrl);
        $container.find(".appLiveUrl").val(url.appLiveUrl);

        $container.find(".webLiveUrl").val(url.webLiveUrl);
        $container.find(".streamId").val(url.streamID);
        $("#a_streamIDs").val($.trim($("#a_streamIDs").val()) + url.streamID + ",");
    },
    collectConfig: function(){
        var resultJson = {};
        var urlArr = [];
        var _type = $("input:radio[name=a_type]:visible:checked").val();
        if(_type == 1){
            var _vs;
            //放到a_config中
            var _c = $("#a_config").val();
            if(_c && _c != ""){
                _config = JSON.parse(_c);
                if(_config.videos){
                    _vs = _config.videos;
                }
            }

            $("#liveUrlDiv").find(".TXAssigned").each(function(){
                var $this = $(this);
                //videos
                var _user = $this.find(".userNameTA").textext()[0].hiddenInput().val();
                _user = JSON.parse(_user);
                _user = _user[0];
                var json = {
                    streamID: $this.find(".streamId").val(),
                    uploadUrl: $this.find(".uploadUrl").val(),
                    webLiveUrl: $this.find(".webLiveUrl").val(),
                    appLiveUrl: $this.find(".appLiveUrl").val(),
                    userID: _user.userId,
                    userName: _user.userName,
                    userCode: _user.userCode,
                    showName: _user.showName,
                    status: $("input:radio[name=a_status]:checked").val(),
                    line:$this.find(".line_select option:selected").val(),
                    lineName:$this.find(".line_name").val(),
                    type:0
                };
                if(_vs){
                    for(var x = 0, xi = null ; xi = _vs[x++];){
                        if(xi.streamID == json.streamID){
                            if(xi.appPlaybackUrl){
                                json.appPlaybackUrl = xi.appPlaybackUrl;    
                            }
                            if(xi.webPlaybackUrl){
                                json.webPlaybackUrl = xi.webPlaybackUrl;
                            }
                        }
                    }
                }
                urlArr.push(json);
            });
            $("#liveUrlDiv").find(".OtherAssigned").each(function(){
                var $this = $(this);
                //videos
                var json = {
                    webLiveUrl: $this.find(".webLiveUrl").val(),
                    appLiveUrl: $this.find(".appLiveUrl").val(),
                    status: $("input:radio[name=a_status]:checked").val(),
                    appPlaybackUrl:$this.find(".appPlaybackUrl").val(),
                    webPlaybackUrl:$this.find(".webPlaybackUrl").val(),
                    line:$this.find(".line_select option:selected").val(),
                    lineName:$this.find(".line_name").val(),
                    type:1,
                    streamID:$this.find(".streamId").val()
                };
                urlArr.push(json);
            });
            resultJson.videos = urlArr;
        } else {
            //banner
            var picList = [];
            var _val = $("#pcPicSrcHidden").val();
            if(_val && $.trim(_val) != ""){
                resultJson.webBanner = _val;
                picList.push(_val);
            }

            _val = $("#appPicSrcHidden").val();
            if(_val && $.trim(_val) != ""){
                resultJson.appBanner = _val;
                picList.push(_val);
            }

            var attachments = {
                pics: picList
            };
            resultJson.videos = [];
            $("#a_attachments").val(JSON.stringify(attachments));
        }
        //继续直播员
        resultJson.users = $("#userTA").textext()[0].hiddenInput().val();
        var _json = JSON.stringify(resultJson);
        $("#a_config").val(_json);
        return resultJson;
    },

    picDialog: function(){
        xy_live.dialog = e5.dialog({
            type: "iframe",
            value: "upload.html?type=web"
        }, {
            showTitle: true,
            title: "上传图片",
            width: "800px",
            height: "500px",
            resizable: false
        });
    },
    picConfirm: function(path){
        if(path){
            $("#" + xy_live.type).attr("src", "../../xy/image.do?path=" + path);
        }
        xy_live.dialog.close();
    },
    picCancel: function(){
        xy_live.dialog.close();
    }


};
//填充单层分类下拉框的Option。读话题分类时加siteID
e5_form.dynamicReader._readCatUrl = function(catType){
    var dataUrl = "e5workspace/manoeuvre/CatFinder.do?action=single&catType=" + catType;
    if(catType == "5"){
        //改变读数据的url
        dataUrl = "xy/Cats.do?catType=" + catType + "&siteID=" + e5_form.getParam("siteID");
    }
    dataUrl = e5_form.dealUrl(dataUrl);
    return dataUrl;
};
//修改操作里，读表单上的数据时，增加dateFull=1参数，使发布时间显示时分秒
e5_form.dataReader._getDataUrl = function(){
    var theURL = "e5workspace/manoeuvre/FormDocFetcher.do?FormID=" + $("#FormID").val()
        + "&DocLibID=" + $("#DocLibID").val()
        + "&DocID=" + $("#DocID").val()
        + "&dateFull=1";
    return theURL;
};

