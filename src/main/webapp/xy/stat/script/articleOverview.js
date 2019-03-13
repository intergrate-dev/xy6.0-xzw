var articleOverview_stat = {
    colDialog: null,
    colDialogWebApp: null,
    autocompleteData:[],
    init: function() {
        //默认显示本月数据 部门
        $("#thisMonthDept").addClass("select_time");
        articleOverview_stat.setCalender('thisMonth','_dept_web','',true);
        articleOverview_stat.search('ArticleDepartment','channelWeb','_dept_web');

        $("#thisMonthDept_App").addClass("select_time");
        articleOverview_stat.setCalender('thisMonth','_dept_App','_App',true);
        articleOverview_stat.search('ArticleDepartment','channelApp','_dept_App');

        //默认显示本月数据 来源
        $("#thisMonthSrc").addClass("select_time");
        articleOverview_stat.setCalender('thisMonth','_src_web','_src',true);
        articleOverview_stat.search('ArticleSource','channelWeb','_src_web');

        $("#thisMonthSrc_App").addClass("select_time");
        articleOverview_stat.setCalender('thisMonth','_src_App','_src_App',true);
        articleOverview_stat.search('ArticleSource','channelApp','_src_App');

        //默认显示本月数据 栏目
        $("#thisMonthCol").addClass("select_time");
        articleOverview_stat.setCalender('thisMonth','_col_web','_col',true);
        articleOverview_stat.search('ArticleColumn','channelWeb','_col_web');

        $("#thisMonthCol_App").addClass("select_time");
        articleOverview_stat.setCalender('thisMonth','_col_App','_col_App',true);
        articleOverview_stat.search('ArticleColumn','channelApp','_col_App');

        <!-- 默认显示本月数据 话题 start-->
        $("#thisMonthTop").addClass("select_time");
        articleOverview_stat.setCalender('thisMonth','_top_web','_top',true);
        articleOverview_stat.search('ColumnTopic','channelWeb','_top_web');

        $("#thisMonthTop_App").addClass("select_time");
        articleOverview_stat.setCalender('thisMonth','_top_App','_top_App',true);
        articleOverview_stat.search('ColumnTopic','channelApp','_top_App');
        <!-- 默认显示本月数据 话题 end -->

        $("#columnName_web,#columnName_App").click(articleOverview_stat.colSelect);
        $('li.channelTab').click(articleOverview_stat.channelTab);
        // 日期控件
        articleOverview_stat.initDateTimePickerWeb();

        //时间选择
        $(".time_stat1").click(function(){
            $(this).addClass("select_time").siblings().removeClass("select_time");
        });

        //重置
        $("#reset").click(articleOverview_stat.resetWorkload_reset);
        $("#reset_app").click(articleOverview_stat.resetWorkload_reset_app);
        $("#reset_src").click(articleOverview_stat.resetWorkload_reset_src);
        $("#reset_src_app").click(articleOverview_stat.resetWorkload_reset_src_app);
        $("#reset_col").click(articleOverview_stat.resetWorkload_reset_col);
        $("#reset_col_app").click(articleOverview_stat.resetWorkload_reset_col_app);
        $("#reset_top").click(articleOverview_stat.resetWorkload_reset_top);
        $("#reset_top_app").click(articleOverview_stat.resetWorkload_reset_top_app);

    },
    saveDepartmentMsg:function(data){
        var autocompleteData=[];
        var len=data.departmentData.length;
        if(len<1) return;
        for (var i = 0; i < len; i++) {
            autocompleteData.push(data.departmentData[i].departmentName);
        }
        articleOverview_stat.autocompleteData=autocompleteData;
        articleOverview_stat.autoCompleter.init();

        //模糊查询
        /* $( "#partmentSearch" ).autocomplete({
         source: workload_stat.autocompleteData
         });*/
    },
    resetWorkload_reset : function(){
        $("#departmentName").val("");
        $("#particular_dept_web").val("");
        $(this).siblings('a').removeClass("select_time");
        $("#pubTime_from_dept_web").val("");
        $("#pubTime_to_dept_web").val("");
        $("#departmentDetailTable tbody").empty();
    },
    resetWorkload_reset_app : function(){
        $("#departmentName_app").val("");
        $("#particular_dept_App").val("");
        $(this).siblings('a').removeClass("select_time");
        $("#pubTime_from_dept_App").val("");
        $("#pubTime_to_dept_App").val("");
        $("#departmentDetailTable_App tbody").empty();
    },
    resetWorkload_reset_src : function(){
        $("#sourceName_src_web").val("");
        $("#particular_src_web").val("");
        $(this).siblings('a').removeClass("select_time");
        $("#pubTime_from_src_web").val("");
        $("#pubTime_to_src_web").val("");
        $("#sourceDetailTable tbody").empty();
    },
    resetWorkload_reset_src_app : function(){
        $("#sourceName_src_App").val("");
        $("#particular_src_App").val("");
        $(this).siblings('a').removeClass("select_time");
        $("#pubTime_from_src_App").val("");
        $("#pubTime_to_src_App").val("");
        $("#sourceDetailTable_App tbody").empty();
    },
    resetWorkload_reset_col : function(){
        $("#columnName_web").val("");
        $("#colID_col_web").val("");
        $(this).siblings('a').removeClass("select_time");
        $("#pubTime_from_col_web").val("");
        $("#pubTime_to_col_web").val("");
        $("#columnDetailTable tbody").empty();
    },
    resetWorkload_reset_col_app : function(){
        $("#columnName_App").val("");
        $("#colID_col_App").val("");
        $(this).siblings('a').removeClass("select_time");
        $("#pubTime_from_col_App").val("");
        $("#pubTime_to_col_App").val("");
        $("#columnDetailTable_App tbody").empty();
    },
    resetWorkload_reset_top : function(){
        $("#topicName").val("");
        $("#particular_top_web").val("");
        $(this).siblings('a').removeClass("select_time");
        $("#pubTime_from_top_web").val("");
        $("#pubTime_to_top_web").val("");
        $("#topicDetailTable tbody").empty();
    },
    resetWorkload_reset_top_app : function(){
        $("#topicName-app").val("");
        $("#particular_top_App").val("");
        $(this).siblings('a').removeClass("select_time");
        $("#pubTime_from_top_App").val("");
        $("#pubTime_to_top_App").val("");
        $("#topicDetailTable_App tbody").empty();
    },

    initDateTimePickerWeb: function() {
        $('[id^="pubTime_from"]').datetimepicker({
            language : 'zh-CN',
            weekStart : 0,
            todayBtn : 1,
            autoclose : true,
            todayHighlight : true,
            startView : 2,
            minView : 0,
            disabledDaysOfCurrentMonth : 0,
            forceParse : 0,
            pickerPosition: "bottom-left",
            format : 'yyyy-mm-dd hh:ii:00'
        });

        $('[id^="pubTime_from"]').datetimepicker().on('changeDate', function(ev) {});

        $('[id^="pubTime_to"]').datetimepicker({
            language : 'zh-CN',
            weekStart : 0,
            todayBtn : 1,
            autoclose : true,
            todayHighlight : true,
            startView : 2,
            minView : 0,
            disabledDaysOfCurrentMonth : 0,
            forceParse : 0,
            pickerPosition: "bottom-left",
            format : 'yyyy-mm-dd hh:ii:00'
        });

        $('[id^="pubTime_to"]').datetimepicker().on('changeDate', function(ev) {});
    },

    // 点击上月本月要同时改变日历的值
    setCalender: function(type, data, commet,isinit) {
        var now = new Date();
        if (type == 'thisMonth') {
            var ym = now.getFullYear() + "-" + articleOverview_stat.add_zero(now.getMonth() + 1) + "-";
            $('#pubTime_from' + data).val(ym + "01" + " 00:00:00");
            $('#pubTime_to' + data).val(ym + articleOverview_stat.add_zero(now.getDate()) + " 23:59:59");


        } else if (type == 'lastMonth') {
            var year = now.getFullYear();
            var month = now.getMonth();
            if (month == 0) {
                year -= 1;
                month = 12;
            }
            var ym = year + "-" + articleOverview_stat.add_zero(month) + "-";
            $('#pubTime_from' + data).val(ym + "01" + " 00:00:00");
            now.setDate(1);
            now.setMonth(now.getMonth());
            var cdt = new Date(now.getTime() - 1000 * 60 * 60 * 24);
            $('#pubTime_to' + data).val(ym + cdt.getDate() + " 23:59:59");


        } else if (type == 'current24H') {
            var fromTime = new Date(now.getTime() - 24 * 3600 * 1000);
            var fromYear = fromTime.getFullYear();
            var fromMonth = fromTime.getMonth() + 1;
            if (fromMonth == 0) {
                fromYear -= 1;
                fromMonth = 12;
            }
            fromMonth = articleOverview_stat.add_zero(fromMonth);
            var fromDay = articleOverview_stat.add_zero(fromTime.getDate());
            var fromHour = articleOverview_stat.add_zero(fromTime.getHours());
            var fromMinutes = articleOverview_stat.add_zero(fromTime.getMinutes());
            var fromSeconds = articleOverview_stat.add_zero(fromTime.getSeconds());
            var newFromTime = fromYear + '-' + fromMonth + '-' + fromDay + ' ' + fromHour + ':' + fromMinutes + ':' + fromSeconds;
            $('#pubTime_from' + data).val(newFromTime);
            var nowTime = new Date(now.getTime());
            var nowYear = nowTime.getFullYear();
            var nowMonth = nowTime.getMonth() + 1;
            if (nowMonth == 0) {
                nowYear -= 1;
                nowMonth = 12;
            }
            nowMonth = articleOverview_stat.add_zero(nowMonth);
            var nowDay = articleOverview_stat.add_zero(nowTime.getDate());
            var nowHour = articleOverview_stat.add_zero(nowTime.getHours());
            var nowMinutes = articleOverview_stat.add_zero(nowTime.getMinutes());
            var nowSeconds = articleOverview_stat.add_zero(nowTime.getSeconds());
            var newNowTime = nowYear + '-' + nowMonth + '-' + nowDay + ' ' + nowHour + ':' + nowMinutes + ':' + nowSeconds;
            $('#pubTime_to' + data).val(newNowTime);


        } else if (type == 'current7D') {
            var fromTime = new Date(now.getTime() - 7 * 24 * 3600 * 1000);
            var fromYear = fromTime.getFullYear();
            var fromMonth = fromTime.getMonth() + 1;
            if (fromMonth == 0) {
                fromYear -= 1;
                fromMonth = 12;
            }
            fromMonth = articleOverview_stat.add_zero(fromMonth);
            var fromDay = articleOverview_stat.add_zero(fromTime.getDate());
            var newFromTime = fromYear + '-' + fromMonth + '-' + fromDay + " 00:00:00";
            $('#pubTime_from' + data).val(newFromTime);
            var nowTime = new Date(now.getTime());
            var nowYear = nowTime.getFullYear();
            var nowMonth = nowTime.getMonth() + 1;
            if (nowMonth == 0) {
                nowYear -= 1;
                nowMonth = 12;
            }
            nowMonth = articleOverview_stat.add_zero(nowMonth);
            var nowDay = articleOverview_stat.add_zero(nowTime.getDate());
            var newNowTime = nowYear + '-' + nowMonth + '-' + nowDay + " 23:59:59";
            $('#pubTime_to' + data).val(newNowTime);

        } else if (type == 'current14D') {
            var fromTime = new Date(now.getTime() - 14 * 24 * 3600 * 1000);
            var fromYear = fromTime.getFullYear();
            var fromMonth = fromTime.getMonth() + 1;
            if (fromMonth == 0) {
                fromYear -= 1;
                fromMonth = 12;
            }
            fromMonth = articleOverview_stat.add_zero(fromMonth);
            var fromDay = articleOverview_stat.add_zero(fromTime.getDate());
            var newFromTime = fromYear + '-' + fromMonth + '-' + fromDay + " 00:00:00";
            $('#pubTime_from' + data).val(newFromTime);
            var nowTime = new Date(now.getTime());
            var nowYear = nowTime.getFullYear();
            var nowMonth = nowTime.getMonth() + 1;
            if (nowMonth == 0) {
                nowYear -= 1;
                nowMonth = 12;
            }
            nowMonth = articleOverview_stat.add_zero(nowMonth);
            var nowDay = articleOverview_stat.add_zero(nowTime.getDate());
            var newNowTime = nowYear + '-' + nowMonth + '-' + nowDay + " 23:59:59";
            $('#pubTime_to' + data).val(newNowTime);

        } else if (type == 'current30D') {
            var fromTime = new Date(now.getTime() - 30 * 24 * 3600 * 1000);
            var fromYear = fromTime.getFullYear();
            var fromMonth = fromTime.getMonth() + 1;
            if (fromMonth == 0) {
                fromYear -= 1;
                fromMonth = 12;
            }
            fromMonth = articleOverview_stat.add_zero(fromMonth);
            var fromDay = articleOverview_stat.add_zero(fromTime.getDate());
            var newFromTime = fromYear + '-' + fromMonth + '-' + fromDay + " 00:00:00";
            $('#pubTime_from' + data).val(newFromTime);
            var nowTime = new Date(now.getTime());
            var nowYear = nowTime.getFullYear();
            var nowMonth = nowTime.getMonth() + 1;
            if (nowMonth == 0) {
                nowYear -= 1;
                nowMonth = 12;
            }
            nowMonth = articleOverview_stat.add_zero(nowMonth);
            var nowDay = articleOverview_stat.add_zero(nowTime.getDate());
            var newNowTime = nowYear + '-' + nowMonth + '-' + nowDay + " 23:59:59";
            $('#pubTime_to' + data).val(newNowTime);

        }
        //触发查看
        if(!isinit){
            if($("#selectType").children(".select").attr("id")=="deptStat"){
                if($("#selectChannel").children(".select1").attr("id")=="channelWeb_dept"){
                    articleOverview_stat.search('ArticleDepartment','channelWeb','_dept_web');
                }else if($("#selectChannel").children(".select1").attr("id")=="channelApp_dept"){
                    articleOverview_stat.search('ArticleDepartment','channelApp','_dept_App');
                }
            }else if($("#selectType").children(".select").attr("id")=="srcStat"){
                if($("#selectChannel_src").children(".select1").attr("id")=="channelWeb_src"){
                    articleOverview_stat.search('ArticleSource','channelWeb','_src_web');
                }else if($("#selectChannel_src").children(".select1").attr("id")=="channelApp_src"){
                    articleOverview_stat.search('ArticleSource','channelApp','_src_App');
                }
            }else if($("#selectType").children(".select").attr("id")=="colStat"){
                if($("#selectChannel_col").children(".select1_col").attr("id")=="channelWeb_col"){
                    articleOverview_stat.search('ArticleColumn','channelWeb','_col_web');
                }else if($("#selectChannel_col").children(".select1_col").attr("id")=="channelApp_col"){
                    articleOverview_stat.search('ArticleColumn','channelApp','_col_App');
                }
            }else if($("#selectType").children(".select").attr("id")=="topStat"){
                if($("#selectChannel_top").children(".select1").attr("id")=="channelWeb_top"){
                    articleOverview_stat.search('ColumnTopic','channelWeb','_top_web');
                }else if($("#selectChannel_top").children(".select1").attr("id")=="channelApp_top"){
                    articleOverview_stat.search('ColumnTopic','channelApp','_top_App');
                }
            }
        }
    },

    add_zero: function(param) {
        if (param < 10) return "0" + param;
        return "" + param;
    },

    // 切换tab
    channelTab: function(evt) {
        //var myVal = $(this).attr("data-val");
        var id = $(evt.target)[0].id;
        if (id.indexOf('channelWeb_dept') > -1) {
            //$("#departmentName").val(myVal);
            $('#channelWeb_dept').addClass("select1");
            $('#channelApp_dept').removeClass("select1");
            $('#detailDept').css('display', 'inline');
            $('#detailDept_App').css('display', 'none');

        } else if (id.indexOf('channelApp_dept') > -1) {
            //	$("#departmentName_app").val(myVal);
            $('#channelApp_dept').addClass("select1");
            $('#channelWeb_dept').removeClass("select1");
            $('#detailDept_App').css('display', 'inline');
            $('#detailDept').css('display', 'none');

        } else if (id.indexOf('channelWeb_src') > -1) {
            $('#channelWeb_src').addClass("select1");
            $('#channelApp_src').removeClass("select1");
            $('#detailSrc').css('display', 'inline');
            $('#detailSrc_App').css('display', 'none');

        } else if (id.indexOf('channelApp_src') > -1) {
            $('#channelApp_src').addClass("select1");
            $('#channelWeb_src').removeClass("select1");
            $('#detailSrc_App').css('display', 'inline');
            $('#detailSrc').css('display', 'none');

        } else if (id.indexOf('channelWeb_col') > -1) {
            $('#channelWeb_col').addClass("select1_col");
            $('#channelApp_col').removeClass("select1_col");
            $('#detailCol').css('display', 'inline');
            $('#detailCol_App').css('display', 'none');

        } else if (id.indexOf('channelApp_col') > -1) {
            $('#channelApp_col').addClass("select1_col");
            $('#channelWeb_col').removeClass("select1_col");
            $('#detailCol_App').css('display', 'inline');
            $('#detailCol').css('display', 'none');

        }  else if (id.indexOf('channelWeb_top') > -1) {
            $('#channelWeb_top').addClass("select1");
            $('#channelApp_top').removeClass("select1");
            $('#detailTop').css('display', 'inline');
            $('#detailTop_App').css('display', 'none');

        } else if (id.indexOf('channelApp_top') > -1) {
            $('#channelApp_top').addClass("select1");
            $('#channelWeb_top').removeClass("select1");
            $('#detailTop_App').css('display', 'inline');
            $('#detailTop').css('display', 'none');

        }else if (id.indexOf('deptStat') > -1) {
            $('#' + id).addClass("select");
            $('#' + id.replace('dept', 'src')).removeClass("select");
            $('#' + id.replace('dept', 'col')).removeClass("select");
            $('#' + id.replace('dept', 'top')).removeClass("select");
            $('#dept').css("display", "inline");
            $('#src').css("display", "none");
            $('#column').css("display", "none");
            $('#topic').css("display", "none");

        } else if (id.indexOf('srcStat') > -1) {
            $('#' + id).addClass("select");
            $('#' + id.replace('src', 'dept')).removeClass("select");
            $('#' + id.replace('src', 'col')).removeClass("select");
            $('#' + id.replace('src', 'top')).removeClass("select");
            $('#src').css("display", "inline");
            $('#dept').css("display", "none");
            $('#column').css("display", "none");
            $('#topic').css("display", "none");
        } else if (id.indexOf('colStat') > -1) {
            $('#' + id).addClass("select");
            $('#' + id.replace('col', 'src')).removeClass("select");
            $('#' + id.replace('col', 'dept')).removeClass("select");
            $('#' + id.replace('col', 'top')).removeClass("select");
            $('#column').css("display", "inline");
            $('#dept').css("display", "none");
            $('#src').css("display", "none");
            $('#topic').css("display", "none");
        } else if (id.indexOf('topStat') > -1) {
            $('#' + id).addClass("select");
            $('#' + id.replace('top', 'src')).removeClass("select");
            $('#' + id.replace('top', 'dept')).removeClass("select");
            $('#' + id.replace('top', 'col')).removeClass("select");
            $('#topic').css("display", "inline");
            $('#dept').css("display", "none");
            $('#src').css("display", "none");
            $('#column').css("display", "none");
        } else {
            return;
        }

    },
    // 点击查看 eg thisMonth, channelWeb, _dept_web
    search: function(type, channel, commet) {
        var code = e5.utils.getCookie("l$code");
        var now = new Date();
        var ym = now.getFullYear() + "-" + articleOverview_stat.add_zero(now.getMonth() + 1) + "-";
        var beginTime=$('#pubTime_from' + commet).val();
        var endTime=$('#pubTime_to' + commet).val();
        if(!beginTime){
            $('#pubTime_from' + commet).val(ym + "01" + " 00:00:00");
            $('#pubTime_to' + commet).val(ym + articleOverview_stat.add_zero(now.getDate()) + " 23:59:59");
            $('#pubTime_from' + commet).prev().addClass("select_time");
            if(commet=="_dept_web"){
                $("#departmentName").val("全部");
            }else if(commet=="_dept_App"){
                $("#departmentName_app").val("全部");
            }
        }
        beginTime= beginTime=='' ? ym + "01" + " 00:00:00" : beginTime;
        endTime= endTime=='' ? ym + articleOverview_stat.add_zero(now.getDate()) + " 23:59:59" : endTime;

        //确保开始时间小于结束时间
        var beginTimeNum= new Date(beginTime);
        var endTimeNum= new Date(endTime);
        if(beginTimeNum.getTime()>endTimeNum.getTime()){
            alert('“开始时间”晚于“结束时间”，请重新选择！');
            return;
        }
        var url = "../../xy/statistics/Statistics.do";
        var particularParam="";
        if(commet.indexOf("_dept_")!=-1){
            particularParam=$('#particular' + commet).val();
        }else if(commet.indexOf("_src_")!=-1){
            particularParam=$('#sourceName' + commet).val();
        }else if(commet.indexOf("_col_")!=-1){
            particularParam=$('#colID' + commet).val();
        }else if(commet.indexOf("_top_")!=-1){
            particularParam=$('#particular' + commet).val();
        }


        var pageNo = 1;
        var siteID=getQueryString("siteID");
        var params = {
            channelCode: channel,
            siteID: siteID,
            dataParam: {
                particularParam: particularParam,
                statisticsType: type,
                beginTime: beginTime,
                endTime: endTime,
                userCode:code,
                pageSize:40,
                pageNum: pageNo
            }
        };
        // console.log(params);
        $.ajax({
            url: url,
            data: JSON.stringify(params),
            type: "POST",
            dataType: "json",
            async: false,
            contentType: "application/json;charset=utf-8",
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert(errorThrown + ':' + textStatus); // 错误处理
            },
            success: function(data) {
            	// console.log(data);
                if (type == 'ArticleDepartment') {
                    if (channel == 'channelWeb') {
                        articleOverview_stat.showResult(data, 'departmentDetailTable', 'dept');
                        var totalCount = data.totalCount;
                        var pageCount = data.pageCount;
                        var currentPageNum = pageNo;
                        articleOverview_stat.showPaginator(totalCount, pageCount, pageNo, params, '_dept_web', 'departmentDetailTable', 'dept');
                        $('#total_count' + commet).val(totalCount);
                        $('#total_count' + commet+"_all").text(totalCount);
                    } else if (channel == 'channelApp') {
                        articleOverview_stat.showResult(data, 'departmentDetailTable_App', 'dept');
                        var totalCount = data.totalCount;
                        var pageCount = data.pageCount;
                        var currentPageNum = pageNo;
                        articleOverview_stat.showPaginator(totalCount, pageCount, pageNo, params, '_dept_App', 'departmentDetailTable_App', 'dept');
                        $('#total_count' + commet).val(totalCount);
                        $('#total_count' + commet+"_all").text(totalCount);
                    } else {
                        alert('Error During Searching!');
                    }
                } else if (type == 'ArticleSource') {
                    if (channel == 'channelWeb') {
                        articleOverview_stat.showResult(data, 'sourceDetailTable', 'source');
                        var totalCount = data.totalCount;
                        var pageCount = data.pageCount;
                        var currentPageNum = pageNo;
                        articleOverview_stat.showPaginator(totalCount, pageCount, pageNo, params, '_src_web', 'sourceDetailTable', 'source');
                        $('#total_count' + commet).val(totalCount);
                        $('#total_count' + commet+"_all").text(totalCount);
                    } else if (channel == 'channelApp') {
                        articleOverview_stat.showResult(data, 'sourceDetailTable_App', 'source');
                        var totalCount = data.totalCount;
                        var pageCount = data.pageCount;
                        var currentPageNum = pageNo;
                        articleOverview_stat.showPaginator(totalCount, pageCount, pageNo, params, '_src_App', 'sourceDetailTable_App', 'source');
                        $('#total_count' + commet).val(totalCount);
                        $('#total_count' + commet+"_all").text(totalCount);
                    } else {
                        alert('Error During Searching!');
                    }
                } else if (type == 'ArticleColumn') {
                    if (channel == 'channelWeb') {
                        articleOverview_stat.showResult(data, 'columnDetailTable', 'column');
                        var totalCount = data.totalCount;
                        var pageCount = data.pageCount;
                        var currentPageNum = pageNo;
                        articleOverview_stat.showPaginator(totalCount, pageCount, pageNo, params, '_col_web', 'columnDetailTable', 'column');
                        $('#total_count' + commet).val(totalCount);
                        $('#total_count' + commet+"_all").text(totalCount);
                    } else if (channel == 'channelApp') {
                        articleOverview_stat.showResult(data, 'columnDetailTable_App', 'column');
                        var totalCount = data.totalCount;
                        var pageCount = data.pageCount;
                        var currentPageNum = pageNo;
                        articleOverview_stat.showPaginator(totalCount, pageCount, pageNo, params, '_col_App', 'columnDetailTable_App', 'column');
                        $('#total_count' + commet).val(totalCount);
                        $('#total_count' + commet+"_all").text(totalCount);
                    } else {
                        alert('Error During Searching!');
                    }
                }else if (type == 'ColumnTopic') {
                    if (channel == 'channelWeb') {
                        articleOverview_stat.showResult(data, 'topicDetailTable', 'topic');
                        var totalCount = data.totalCount;
                        var pageCount = data.pageCount;
                        var currentPageNum = pageNo;
                        articleOverview_stat.showPaginator(totalCount, pageCount, pageNo, params, '_top_web', 'topicDetailTable', 'topic');
                        $('#total_count' + commet).val(totalCount);
                        $('#total_count' + commet+"_all").text(totalCount);
                    } else if (channel == 'channelApp') {
                        articleOverview_stat.showResult(data, 'topicDetailTable_App', 'topic');
                        var totalCount = data.totalCount;
                        var pageCount = data.pageCount;
                        var currentPageNum = pageNo;
                        articleOverview_stat.showPaginator(totalCount, pageCount, pageNo, params, '_top_App', 'topicDetailTable_App', 'topic');
                        $('#total_count' + commet).val(totalCount);
                        $('#total_count' + commet+"_all").text(totalCount);
                    } else {
                        alert('Error During Searching!');
                    }
                }

            }
        });
    },
    add_zero: function(param) {
        if (param < 10) return "0" + param;
        return "" + param;
    },
    showResult: function(data, tableId, type) {
        var statList = data.statisticsData;
        if(!statList) return;
        var isChange=false;
        $('#' + tableId + ' tbody').empty();
        $.each(statList, function(i, bean) {
            str = '<tr class="tdtr">' //+ '<td class="tdtr1">';
            if (type == 'dept') {
                //str += '<input type="checkbox" name="checkbox" id="checkbox_dept"/></td>';
                str += '<td class="tdtr">' + bean.departmentName + '</td>';
            } else if (type == 'source') {
                if(i==1 && bean.sourceName==""){
                    isChange=true;
                }
                //str += '<input type="checkbox" name="checkbox" id="checkbox_src"/></td>';
                var sourceName=bean.sourceName?bean.sourceName:"--";
                str += '<td class="tdtr">' + sourceName + '</td>';
            } else if (type == 'column') {
                //str += '<input type="checkbox" name="checkbox" id="checkbox_col"/></td>';
                str += '<td class="tdtr">' + bean.columnName + '</td>';
            } else if (type == 'topic') {
                //str += '<input type="checkbox" name="checkbox" id="checkbox_col"/></td>';
                str += '<td class="tdtr">' + bean.topicName + '</td>';
            } else {
                str = str + '<td class="tdtr">Error TD!</td>';
            }

            str += '<td class="tdtr">' + bean.articleNum + '</td>' + '<td class="tdtr">' + bean.totalClick + '</td>' + '<td class="tdtr">' + bean.pcClick + '</td>' + '<td class="tdtr">' + bean.wapClick + '</td>' + '<td class="tdtr">' + bean.appClick + '</td>' + '<td class="tdtr">' + bean.totalShare + '</td>' + '<td class="tdtr">' + bean.pcShare + '</td>' + '<td class="tdtr">' + bean.wapShare + '</td>' + '<td class="tdtr">' + bean.appShare + '</td>' + '<td class="tdtr">' + bean.totalDiscuss + '</td>' + '<td class="tdtr">' + bean.pcDiscuss + '</td>' + '<td class="tdtr">' + bean.wapDiscuss + '</td>' + '<td class="tdtr">' + bean.appDiscuss + '</td>' + '</tr>';
            $('#' + tableId + ' tbody').append(str);
        });
        if(isChange){
            for(var i=2;i<15;i++){
                articleOverview_stat.setTdVal(tableId,1,i,articleOverview_stat.getTdVal(tableId,0,i)+articleOverview_stat.getTdVal(tableId,1,i));
            }
            $('#' + tableId + ' tbody').find("tr").eq(0).remove();
        }

    },
    getTdVal:function(tableId,row,col){
        return parseInt($('#' + tableId + ' tbody').find("tr").eq(row).find("td").eq(col).text());
    },
    setTdVal:function(tableId,row,col,val){
        $('#' + tableId + ' tbody').find("tr").eq(row).find("td").eq(col).text(val);
    },/**
     articleOverview_stat.outputcsv('ArticleDepartment','channelWeb', 'department', '', '_dept_web')
     articleOverview_stat.outputcsv('ArticleDepartment','channelApp', 'department', '_App', '_dept_App')
     articleOverview_stat.outputcsv('ArticleSource','channelWeb', 'source', '', '_src_web')
     articleOverview_stat.outputcsv('ArticleSource','channelApp', 'source', '_App', '_src_App')
     articleOverview_stat.outputcsv('ArticleColumn','channelWeb', 'column', '', '_col_web')
     articleOverview_stat.outputcsv('ArticleColumn','channelApp', 'column', '_App', '_col_App')
	 articleOverview_stat.outputcsv('ArticleTopic','channelWeb', 'topic', '', '_top_web')
     articleOverview_stat.outputcsv('ArticleTopic','channelApp', 'topic', '_App', '_top_App')*/
    outputcsv: function(type, channel, tab, webApp, commet) {
        var code = e5.utils.getCookie("l$code");
        //头部id
        var th = $('#' + tab + 'DetailTable' + webApp + ' tr th');
        var thdatas = [];
        var length = th.length;
        for (var a = 0; a < length; a++) {
            thdatas.push($(th[a]).text());
        }
        //部门id 来源名称   栏目id
        var particularParam;
        if(type=='ArticleDepartment'){
            particularParam=$('#particular' + commet).val();
        }else if(type=='ArticleSource'){
            particularParam=$('#sourceName' + commet).val();
        }else if(type=='ArticleColumn'){
            particularParam=$('#colID' + commet).val();
        }else{
            particularParam=$('#particular' + commet).val();
        }

        //默认时间为本月
        var now = new Date();
        var ym = now.getFullYear() + "-" + articleOverview_stat.add_zero(now.getMonth() + 1) + "-";
        var beginTime=$('#pubTime_from' + commet).val();
        var endTime=$('#pubTime_to' + commet).val();
        beginTime= beginTime=='' ? ym + "01" + " 00:00:00" : beginTime;
        endTime= endTime=='' ? ym + articleOverview_stat.add_zero(now.getDate()) + " 23:59:59" : endTime;

        //确保开始时间小于结束时间
        var beginTimeNum= new Date(beginTime);
        var endTimeNum= new Date(endTime);
        if(beginTimeNum.getTime()>endTimeNum.getTime()){
            alert('“开始时间”晚于“结束时间”，请重新选择！');
            return;
        }
        var siteID=getQueryString("siteID");
        var params = {
            fileName: '稿件统计.csv',
            headParam: thdatas,
            channelCode: channel,
            siteID: siteID,
            dataParam: {
                particularParam: particularParam,
                exportType: type,
                beginTime: beginTime,
                endTime: endTime,
                userCode:code,
                pageNum: 1,
                pageSize: $('#total_count' + commet).val()
            }
        };

        $('#jsonData').val(JSON.stringify(params));
        $("#form").attr("action", "../../xy/statistics/ExportCSV.do");
        $("#form").submit();
    },
//打开主栏目选择对话框
    colSelect: function(evt) {
        var ch = 1;
        articleOverview_stat.colDialogWebApp = '_App';
        var id = $('li.select1_col').attr('id');
        if (id && id.indexOf('Web') > -1) {
            ch = 0;
            articleOverview_stat.colDialogWebApp = '_web';
        }
        var siteID=getQueryString("siteID");
        var dataUrl = "../../xy/column/ColumnCheck.jsp?siteID=" + siteID + "&ids=" + $("#colID" + articleOverview_stat.colDialogWebApp).val() + "&ch=" + ch+"&style=checkbox&type=op";
        var pos = {left : "350px",top : "50px",width : "1000px",height : "500px"};
        articleOverview_stat.colDialog = e5.dialog({
            type: "iframe",
            value: dataUrl
        }, {
            showTitle: false,
            width: "450px",
            height: "430px",
            pos: pos,
            resizable: false
        });
        articleOverview_stat.colDialog.show();
    },
    showPaginator: function(totalCount, pageCount, pageNum, params, commet, tableId, selectType) {
        if(totalCount<1) {
            $('#paginator' + commet).hide();
            return;
        };
        var options = {
            bootstrapMajorVersion: 3, //使用的bootstrap版本为3，
            alignment: "center", //居中显示
            currentPage:pageNum, //当前页数
            totalPages: pageCount, //总页数


            itemTexts: function(clickType, page, current) {
                switch (clickType) {
                    case "first":
                        return "首页";
                    case "prev":
                        return "上一页";
                    case "next":
                        return "下一页";
                    case "last":
                        return "末页";
                    case "page":
                        return page;
                }
            },
            //点击事件，用于通过Ajax来刷新整个list列表
            onPageClicked: function(event, originalEvent, clickType, page) {//page
                params.dataParam.pageNum = page;
                page++;
                $.ajax({
                    url: "../../xy/statistics/Statistics.do",
                    type: "POST",
                    dataType: "json",
                    async: false,
                    contentType: "application/json;charset=utf-8",
                    data: JSON.stringify(params),
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                        alert(errorThrown + ':' + textStatus); // 错误处理
                    },
                    success: function(data) {
                        if (data != null) {
                            articleOverview_stat.showResult(data, tableId, selectType);
                            if(pageNum>1&&pageNum<=pageCount){
                                pageNum++;
                                articleOverview_stat.showPaginator(totalCount, pageCount, pageNum, params, commet, tableId, selectType);
                            }
                        }
                    }
                });
            }

        };
        $('#paginator' + commet).show();
        $('#paginator' + commet).bootstrapPaginator(options);
    },
    getDepartmentData:function(){
        var url = "../../xy/statisticsutil/FindDepartment.do";
        var siteID=getQueryString("siteID");
        var params = {
            "channelCode": "channelAll",
            "siteID": siteID,
            "dataParam": {
                "departmentID": "",
                "statisticsType": "WorkDepartment",
                "beginTime": "2016-01-01 00:00:00",
                "endTime": "2017-01-01 00:00:00",
                "pageSize": 40,
                "pageNum":1
            }
        }

        $.ajax({
            url: url,
            data: JSON.stringify(params),
            type: "POST",
            dataType: "json",
            async: false,
            contentType: "application/json;charset=utf-8",
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert(errorThrown + ':' + textStatus); // 错误处理
            },
            success: function(data) {var len=data.departmentData.length;
                var departmentMsg='<li style="font-weight: 700;" id="alldepart" departmentid="">全部</li>';
                for (var i = 0; i < len; i++) {
                    departmentMsg +='<li departmentID="'+data.departmentData[i].departmentID+'">'+data.departmentData[i].departmentName+'</li>';
                }

                $("#department").find("ul").html(departmentMsg);
                //缓存部门信息
                articleOverview_stat.saveDepartmentMsg(data);
            }
        })

    },
    <!-- 根据站点得到话题组的信息 start -->
    getTopicData:function(){
        var url = "../../xy/statistics/getColumnTopic.do";
        var siteID=getQueryString("siteID");
        var params = {
            "siteID": siteID,
        }
        $.ajax({
            url: url,
            data: params,
            type: "GET",
            dataType: "json",
            async: false,
            // contentType: "application/json;charset=utf-8",
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert(errorThrown + ':' + textStatus); // 错误处理
            },
            success: function(data) {
                // console.log(data);
                var len=data.length;
                var topicMsg='<li style="font-weight: 700;" id="alltopic" departmentid="">全部</li>';
                for (var i = 0; i < len; i++) {
                    topicMsg +='<li groupID="'+data[i].groupID+'">'+data[i].topicGroupName+'</li>';
                }
                $("#topic-select").find("ul").html(topicMsg);
                //缓存部门信息
                // articleOverview_stat.saveDepartmentMsg(data);

            }
        })

    }
    <!-- 根据站点得到话题组的信息 end -->
};

//当用户提交选择的栏目,实现栏目选择树的接口
function columnClose(filterChecked, allFilterChecked) {
    $("#columnName"+articleOverview_stat.colDialogWebApp).val(allFilterChecked[1]);
    $("#colID_col"+articleOverview_stat.colDialogWebApp).val(allFilterChecked[0]);
    //触发查询
    if($("#selectChannel_col").children(".select1_col").attr("id")=="channelWeb_col"){
        articleOverview_stat.search('ArticleColumn','channelWeb','_col_web');
    }else if($("#selectChannel_col").children(".select1_col").attr("id")=="channelApp_col"){
        articleOverview_stat.search('ArticleColumn','channelApp','_col_App');
    }
    columnCancel();
}
//点取消
function columnCancel() {
    articleOverview_stat.colDialog.close();
}

//来源选择
var stat_source = {
    dialog : null,
    init : function() {
        $("#sourceButton").click(stat_source.select);
        $("#sourceButton_App").click(stat_source.select);
    },
    //来源选择按钮
    select : function(event) {
        var siteID=getQueryString("siteID");
        var url = "../../xy/GroupSelect.do?type=4&siteID=" + siteID;
        var pos = {left : "100px",top : "50px",width : "1000px",height : "500px"};
        stat_source.dialog = e5.dialog({type : "iframe", value : url}, {
            showTitle : true,
            title: "来源选择",
            width : "1000px",
            height : "500px",
            pos : pos,
            resizable : false
        });
        stat_source.dialog.show();
    },

    //选择窗口：选定后
    groupSelectOK: function(docLibID, docID) {
        stat_source.dialog.close();
        //"xy/source/findSource.do?docLibID=&docID=来源ID";
        $.get("../source/findSource.do", {docLibID:docLibID, docID:docID}, function(data){
            if($("#channelWeb_src").hasClass("select1")){
                $("#sourceName_src_web").val(data.name);
                $("#particular_src_web").val(docID);
            }else{
                $("#sourceName_src_App").val(data.name);
                $("#particular_src_App").val(docID);
            }
            //触发查询
            if($("#selectChannel_src").children(".select1").attr("id")=="channelWeb_src"){
                articleOverview_stat.search('ArticleSource','channelWeb','_src_web');
            }else if($("#selectChannel_src").children(".select1").attr("id")=="channelApp_src"){
                articleOverview_stat.search('ArticleSource','channelApp','_src_App');
            }
        });
    },
    //选择窗口：取消后
    groupSelectCancel : function() {
        stat_source.dialog.close();
    }
}
//回调函数
function groupSelectOK(docLibID, docID){
    stat_source.groupSelectOK(docLibID, docID);
}

function groupSelectCancel(){
    stat_source.groupSelectCancel();
}

function getQueryString(name){
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r != null)return unescape(r[2]);
    return null;
}
//---------部门人员名称查找框-------------
articleOverview_stat.autoCompleter = {
    url : null,
    init : function(evt) {
        articleOverview_stat.autoCompleter.url =articleOverview_stat.autocompleteData;
        var s = $("#departmentSearch");
        s.autocomplete(articleOverview_stat.autoCompleter.url, articleOverview_stat.autoCompleter.options).result(articleOverview_stat.autoCompleter.getSelectedID);
        //s.focus();
    },
    options : {
        minChars : 1,
        delay : 400,
        autoFill : true,
        selectFirst : true,
        matchContains: true,
        cacheLength : 1,
        dataType:'json',
        noRecord:"",
        //把data转换成json数据格式
        /*parse: function(data) {
         if (!data)
         return [];

         return $.map(eval(data), function(row) {
         return {
         data: row
         }
         });
         },*/
        //显示在下拉框中的值
        formatItem: function(row, i,max) { return row[0];},
        formatMatch: function(row, i,max) { return row[0];},
        formatResult: function(row, i,max) { return row[0];}
    },
    getSelectedID : function(event, data, formatted){
        var username=$("#departmentSearch").val();
        $("#department").find("li").each(function(){
            if($(this).text()==username){
                $(this).addClass("selected").siblings().removeClass("selected");
            }
        })

    }
};

$(function() {
    articleOverview_stat.init();
    stat_source.init();
    //  部门选择
    $("#departmentName,#departmentName_app").on("click",function(obj){
        $("#departmentSearch").val("");
        $('#myModal').modal("show");
        var myid = obj.currentTarget.id;
        $("#confirm").attr("data-id",myid);
        articleOverview_stat.getDepartmentData();
    });

    //部门选择点击
    $("#department").on("click","li",function(){
        //$(this).addClass("selected").siblings().removeClass("selected");
        if($(this).attr("id")=="alldepart"){
            $(this).addClass("selected").siblings().removeClass("selected");
        }else{
            $(this).toggleClass("selected");
            $("#alldepart").removeClass("selected");
        }
    });

    //确认选择
    $("#confirm").on("click",function(){
//		var _dept=$("#department").find(".selected").text();
//		var _partid=$("#department").find(".selected").attr("departmentid");

        var _depts=[];
        $("#department").find(".selected").each(function(){
            _depts.push($(this).text())
        });
        var _dept=_depts.join(",");
        if(!_dept){
            alert("未选择部门！");
            return;
        }

        var _partids=[];
        $("#department").find(".selected").each(function(){
            _partids.push($(this).attr("departmentid"));
        });
        var _partid=_partids.join(",");

        var myId = $(this).attr("data-id");
        if(myId == "departmentName"){
            /*var oldVal=$("#departmentName").val();
            if(!_dept){
                _dept=oldVal;
            }*/
            $("#departmentName").val(_dept);

            /*var oldUid=$("#particular_dept_web").val();
            if(!_partid){
                _partid=oldUid;
            }*/
            $("#particular_dept_web").val(_partid);
            //触发查询
            articleOverview_stat.search('ArticleDepartment','channelWeb','_dept_web');
        }else{
            /*var oldVal=$("#departmentName_app").val();
            if(!_dept){
                _dept=oldVal;
            }*/
            $("#departmentName_app").val(_dept);

            /*var oldUid=$("#particular_dept_App").val();
            if(!_partid){
                _partid=oldUid;
            }*/
            $("#particular_dept_App").val(_partid);
            //触发查询
            articleOverview_stat.search('ArticleDepartment','channelApp','_dept_App');
        }
        $('#myModal').modal("hide");
    })

    /*<!--点击话题选择按钮 start -->*/
    $("#topicName,#topicName-app").on("click",function(obj){
        // $("#departmentSearch").val("");
        $('#myModal2').modal("show");
        var myid = obj.currentTarget.id;
        $("#confirm2").attr("data-id",myid);
        articleOverview_stat.getTopicData();
    });
    /*<!--点击话题选择按钮 end -->*/

    /*<!--选中某个话题 start -->*/
    $("#topic-select").on("click","li",function(){
        //$(this).addClass("selected").siblings().removeClass("selected");
        if($(this).attr("id")=="alltopic"){
            $(this).addClass("selected").siblings().removeClass("selected");
        }else{
            $(this).toggleClass("selected");
            $("#alltopic").removeClass("selected");
        }
    });
    /*<!--点击话题选择按钮 end -->*/

    /*<!--确认选择的话题 start -->*/
    $("#confirm2").on("click",function(){
//		var _dept=$("#department").find(".selected").text();
//		var _partid=$("#department").find(".selected").attr("departmentid");

        var _topics=[];
        $("#topic-select").find(".selected").each(function(){
            _topics.push($(this).text())
        });
        var _topic=_topics.join(",");
        if(!_topic){
            alert("未选择话题！");
            return;
        }

        var _topicids = [];
        $("#topic-select").find(".selected").each(function(){
            _topicids.push($(this).attr("groupID"));
        });
        var _topicid=_topicids.join(",");

        var myId = $(this).attr("data-id");
        if(myId == "topicName"){
            /*var oldVal=$("#departmentName").val();
            if(!_dept){
                _dept=oldVal;
            }*/
            $("#topicName").val(_topic);

            /*var oldUid=$("#particular_dept_web").val();
            if(!_partid){
                _partid=oldUid;
            }*/
            $("#particular_top_web").val(_topicid);
            //触发查询
            articleOverview_stat.search('ColumnTopic','channelWeb','_top_web');
        }else{
            /*var oldVal=$("#departmentName_app").val();
            if(!_dept){
                _dept=oldVal;
            }*/
            $("#topicName-app").val(_topic);

            /*var oldUid=$("#particular_dept_App").val();
            if(!_partid){
                _partid=oldUid;
            }*/
            $("#particular_top_App").val(_topicid);
            //触发查询
            articleOverview_stat.search('ColumnTopic','channelApp','_top_App');
        }
        $('#myModal2').modal("hide");
    })
    /*<!--确认选择的话题 end -->*/
});
