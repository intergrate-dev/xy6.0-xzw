var workload_stat = {
    colDialog : null,
    colDialogWebApp : null,
    currentData:null,
    autocompleteData:[],
    init : function() {
        window.onload=function(){
            setTimeout(function(){
            $("#partmentIfram").contents().find("#exp_column,#exp_column_app").on("click",function(){
                workload_stat.colSelect();
            });
            //iframe中人员选择
            $("#partmentIfram").contents().find("#exp_employee,#exp_employee_app").on("click",function(){
                var siteID=getQueryString("siteID");
                var departmentID=$("#dept_list").find(".selected").attr("departmentid");
                var url = "../../xy/statisticsutil/FindUserByDepartmentID.do";
                var params = {
                    siteID: siteID,
                    departmentID: departmentID
                };

                if(!departmentID || departmentID < 2){
                    alert("请选择部门后再选择具体人员");
                    return;
                }
                $("#partmentSearch").val("");
                $("#confirm").attr("data-flag",$(this).attr("id"));
                $('#myModal').modal("show");
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
                 var len=data.userData.length;
                 //var departmentMsg='';
                 var departmentMsg ='<li style="font-weight: 700;" id="alluser" userid="">全部</li>';
                 for (var i = 0; i < len; i++) {
                 departmentMsg +='<li userid="'+data.userData[i].userID+'">'+data.userData[i].userName+'</li>';
                 }

                 $("#departMan").find("ul").html(departmentMsg);
                 $("#departMan").attr("selecttype","one");
                 //缓存部门信息
                 workload_stat.saveDepartmentMsg(data);

                 }
                 })

            });
            },2000)
        }

        //获取部门信息
        workload_stat.getDepartmentData();
        //tab切换
        $('li.channelTab').click(workload_stat.channelTab);
        //部门下拉
        /*$("#dept_all").click(function() {
            $('#dept_list').toggle();
        });*/
        //部门统计
        $("#dept_list").on("click","li",function(){
            var _this=$(this);
            _this.addClass("selected").siblings().removeClass("selected");
            var departmentID=_this.attr("departmentid");
            $("#employee").val("全部");
            workload_stat.search(departmentID,'WorkDepartment');
            //子页面点击事件
            $("#partmentIfram").contents().find("#exp_lookMsg").click();
        });
        //查看
        $("#lookMsg").on("click",function(){
            /*var departmentID=$("#dept_list").find(".selected").attr("departmentid") || "";
            workload_stat.search(departmentID,'WorkDepartment');*/
            var userids=$("#employeeID").val();//"11,9,1"
            workload_stat.searchByUsers(userids,'ArticleUsers');
        });

        //部门人员工作量---时间选择
        $('.time_stat').click(function(){
            $(this).addClass("select_time").siblings().removeClass("select_time");
        });

        //重置
        $("#resetWorkload").click(workload_stat.resetWorkload);
        //各时间段折线图
        $("#thisMonthLine").click(function(){
            return workload_stat.setLineTime('thisMonth','Line')
        });
        $("#thisWeekLine").click(function(){
            return workload_stat.setLineTime('thisWeek','Line')
        });
        $("#thisDayLine").click(function(){
            return workload_stat.setLineTime('thisDay','Line')
        });
        // 日期控件�
        workload_stat.initDateTimePickerWeb();
        // 自动提示控件ؼ�
       // workload_stat.autoCompleter.init();
        workload_stat.autoCompleterApp.init();

    },
    //打开主栏目选择对话框
    colSelect: function(evt) {
        var ch = 1;
        workload_stat.colDialogWebApp = '_app';
        var channel = $("#partmentIfram").contents().find("#main_search li.select").attr('channel');
        if (channel == 0) {
            ch = 0;
            workload_stat.colDialogWebApp = '';
        }
        var siteID=getQueryString("siteID");
        var dataUrl = "../../xy/column/ColumnCheck.jsp?type=op&siteID=" +siteID + "&ids=" + $("#colID" + workload_stat.colDialogWebApp).val() + "&ch=" + ch+"&style=radio&type=all";
        //var pos = columns_stat._getDialogPos(document.getElementById("colName" + columns_stat.colDialogWebApp));
        //获取滚动条滚动的距离
        //var _scrollTop=parseInt($(document).scrollTop());//_scrollTop+
        var pos = {left : "350px",top : "50px",width : "1000px",height : "500px"};
        workload_stat.colDialog = e5.dialog({
            type: "iframe",
            value: dataUrl
        }, {
            showTitle: false,
            width: "450px",
            height: "430px",
            pos: pos,
            resizable: false,
            fixed: true,
        });
        workload_stat.colDialog.show();
        //setTimeout(function(){$(document).scrollTop(_scrollTop);},200);
    },
    saveDepartmentMsg:function(data){
        var autocompleteData=[];
        var len=data.userData.length;
        for (var i = 0; i < len; i++) {
            autocompleteData.push(data.userData[i].userName);
        }
        workload_stat.autocompleteData=autocompleteData;
        workload_stat.autoCompleter.init();

        //模糊查询
       /* $( "#partmentSearch" ).autocomplete({
            source: workload_stat.autocompleteData
        });*/
    },
    resetWorkload:function(){
        $("#employee").val("");
        $("#employeeID").val("");
        $(this).siblings('a').removeClass("select_time");
        $("#pubTime_from_emp").val("");
        $("#pubTime_to_emp").val("");
        $("#EmployeeDetailTable tbody").empty();
    },
    initDateTimePickerWeb : function(){
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

    // 点击发布时间 改变折线图的数值变化
    setLineTime : function(type,commet){
        var currentData;
        if(type == 'thisMonth'){
            $('#thisMonth' +commet).css('color', '#333');
            $('#thisMonth' +commet).css('background', '#E5E5EA');
            $('#thisMonth' +commet).siblings().css('color', '');
            $('#thisMonth' +commet).siblings().css('background', '');
            currentData=workload_stat.currentData.currentMonthData;

        }else if(type == 'thisDay'){
            $('#thisDay' +commet).css('color', '#333');
            $('#thisDay' +commet).css('background', '#E5E5EA');
            $('#thisDay' +commet).siblings().css('color', '');
            $('#thisDay' +commet).siblings().css('background', '');
            currentData=workload_stat.currentData.current24HoursData;

        } else if(type == 'thisWeek'){
            $('#thisWeek' +commet).css('color', '#333');
            $('#thisWeek' +commet).css('background', '#E5E5EA');
            $('#thisWeek' +commet).siblings().css('color', '');
            $('#thisWeek' +commet).siblings().css('background', '');
            currentData=workload_stat.currentData.currentWeekData;

        }
        workload_stat.showLine(currentData,type);
    },

    // 点击上月本月要同时改变日历的值
    setCalender : function(type,data, commet) {
        var now = new Date();
        if(type == 'thisMonth'){
            var ym = now.getFullYear() + "-" + workload_stat.add_zero(now.getMonth() + 1) + "-";
            $('#pubTime_from' + data).val(ym + "01" + " 00:00:00");
            $('#pubTime_to' + data).val(ym + workload_stat.add_zero(now.getDate())+ " 23:59:59");

        } else if(type == 'lastMonth'){
            var year = now.getFullYear();
            var month = now.getMonth();
            if(month == 0){
                year -= 1;
                month = 12;
            }
            var ym = year + "-" + workload_stat.add_zero(month) + "-";
            $('#pubTime_from' + data).val(ym + "01"+ " 00:00:00");
            now.setDate(1);
            now.setMonth(now.getMonth());
            var cdt = new Date(now.getTime() - 1000 * 60 * 60 * 24);
            $('#pubTime_to' + data).val(ym + cdt.getDate()+ " 23:59:59");

        } else if(type == 'current24H'){
            var fromTime = new Date(now.getTime() - 24 * 3600 * 1000);
            var fromYear = fromTime.getFullYear();
            var fromMonth = fromTime.getMonth() + 1;
            if(fromMonth == 0){
                fromYear -= 1;
                fromMonth = 12;
            }
            fromMonth = workload_stat.add_zero(fromMonth);
            var fromDay = workload_stat.add_zero(fromTime.getDate());
            var fromHour = workload_stat.add_zero(fromTime.getHours());
            var fromMinutes = workload_stat.add_zero(fromTime.getMinutes());
            var fromSeconds = workload_stat.add_zero(fromTime.getSeconds());
            var newFromTime = fromYear + '-' + fromMonth + '-' + fromDay + ' ' + fromHour + ':' + fromMinutes + ':' + fromSeconds;
            $('#pubTime_from' + data).val(newFromTime);
            var nowTime = new Date(now.getTime());
            var nowYear = nowTime.getFullYear();
            var nowMonth = nowTime.getMonth() + 1;
            if(nowMonth == 0){
                nowYear -= 1;
                nowMonth = 12;
            }
            nowMonth = workload_stat.add_zero(nowMonth);
            var nowDay = workload_stat.add_zero(nowTime.getDate());
            var nowHour = workload_stat.add_zero(nowTime.getHours());
            var nowMinutes = workload_stat.add_zero(nowTime.getMinutes());
            var nowSeconds = workload_stat.add_zero(nowTime.getSeconds());
            var newNowTime = nowYear + '-' + nowMonth + '-' + nowDay + ' ' + nowHour + ':' + nowMinutes + ':' + nowSeconds;
            $('#pubTime_to' + data).val(newNowTime);

        } else if(type == 'current7D'){
            var week=now.getDay();
            var currweek;
            if(week==0){
                currweek= now.getTime() - 6 * 24 * 3600 * 1000
            }else{
                currweek= now.getTime() - (week-1) * 24 * 3600 * 1000
            }
            var fromTime = new Date(currweek);
            var fromYear = fromTime.getFullYear();
            var fromMonth = fromTime.getMonth() + 1;
            if(fromMonth == 0){
                fromYear -= 1;
                fromMonth = 12;
            }
            fromMonth = workload_stat.add_zero(fromMonth);
            var fromDay = workload_stat.add_zero(fromTime.getDate());
            var newFromTime = fromYear + '-' + fromMonth + '-' + fromDay + " 00:00:00";
            $('#pubTime_from' + data).val(newFromTime);
            var nowTime = new Date(now.getTime());
            var nowYear = nowTime.getFullYear();
            var nowMonth = nowTime.getMonth() + 1;
            if(nowMonth == 0){
                nowYear -= 1;
                nowMonth = 12;
            }
            nowMonth = workload_stat.add_zero(nowMonth);
            var nowDay = workload_stat.add_zero(nowTime.getDate());
            var newNowTime = nowYear + '-' + nowMonth + '-' + nowDay + " 23:59:59";
            $('#pubTime_to' + data).val(newNowTime);
        }
        //触发查询
        var userids=$("#employeeID").val();//"11,9,1"
        workload_stat.searchByUsers(userids,'ArticleUsers');
    },
    searchByUsers: function(userIDs,type){
        var url = "../../xy/statistics/Statistics.do";
        var now = new Date();
        var ym = now.getFullYear() + "-" + workload_stat.add_zero(now.getMonth() + 1) + "-";
        var beginTime=$("#pubTime_from_emp").val();
        var endTime=$("#pubTime_to_emp").val();
        if(!beginTime){
            $("#pubTime_from_emp").val(ym + "01" + " 00:00:00");
            $("#pubTime_to_emp").val(ym + workload_stat.add_zero(now.getDate()) + " 23:59:59");
            $("#thisMonthEmp").addClass("select_time");
            $("#employee").val("全部");
        }
        beginTime= beginTime=='' ? ym + "01" + " 00:00:00" : beginTime;
        endTime= endTime=='' ? ym + workload_stat.add_zero(now.getDate()) + " 23:59:59" : endTime;

        //确保开始时间小于结束时间
        var beginTimeNum= new Date(beginTime);
        var endTimeNum= new Date(endTime);
        if(beginTimeNum.getTime()>endTimeNum.getTime()){
            alert('“开始时间”晚于“结束时间”，请重新选择！');
            return
        };

        var siteID=getQueryString("siteID");
        var departmentID=$("#dept_list").find(".selected").attr("departmentid") || "";

        var pageNo = 1;
        var params = {
            channelCode: "channelAll",
            siteID:siteID,
            dataParam:{
                particularParam:departmentID,
                statisticsType: type,
                beginTime:beginTime,
                endTime:endTime,
                userIDs:userIDs,
                pageSize:40,
                pageNum:pageNo
            }
        }

        $.ajax({
            url: url,
            data : JSON.stringify(params),
            type: "POST",
            dataType: "json",
            async: false,
            contentType: "application/json;charset=utf-8",
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert(errorThrown + ':' + textStatus);  // 错误处理�����
            },
            success: function (data) {
                //表格数据填充
                workload_stat.showResult(data, 'EmployeeDetailTable', 'deptEmployee');
                var totalCount = data.totalCount;
                var pageCount = data.pageCount;
                var currentPageNum = pageNo;
                workload_stat.showPaginator(totalCount, pageCount, pageNo, params, '_emp', 'EmployeeDetailTable', 'deptEmployee');
                $('#total_count_emp').val(totalCount);
                $('#total_count_emp_all').text(totalCount);
            }
        });
    },
    add_zero:function(param){
        if(param < 10) return "0" + param;
        return "" + param;
    },

    //  切换Tab按钮
    channelTab: function(evt){
        var id = $(evt.target)[0].id;
        if(id.indexOf('deptEmployee') > -1){
            $('#deptEmployee').addClass("select");
            $('#deptArticle').removeClass("select");
            $('#employeeTab').css("display", "inline");
            $('#articleTab').css("display", "none");

        }else{
            $('#deptArticle').addClass("select");
            $('#deptEmployee').removeClass("select");
            $('#articleTab').css("display", "inline");
            $('#employeeTab').css("display", "none");
        }
    },

    // 点击查看 eg thisMonth, channelWeb, _dept_web
    search: function(departmentID,type){
        var url = "../../xy/statistics/Statistics.do";
        var now = new Date();
        var ym = now.getFullYear() + "-" + workload_stat.add_zero(now.getMonth() + 1) + "-";
        var beginTime=$("#pubTime_from_emp").val();
        var endTime=$("#pubTime_to_emp").val();
        beginTime= beginTime=='' ? ym + "01" + " 00:00:00" : beginTime;
        endTime= endTime=='' ? ym + workload_stat.add_zero(now.getDate()) + " 23:59:59" : endTime;

        //确保开始时间小于结束时间
        var beginTimeNum= new Date(beginTime);
        var endTimeNum= new Date(endTime);
        if(beginTimeNum.getTime()>endTimeNum.getTime()){
            alert('“开始时间”晚于“结束时间”，请重新选择！');
            return
        };

        var siteID=getQueryString("siteID");
        
        var pageNo = 1;
        var params = {
            channelCode: "channelAll",
            siteID:siteID,
            dataParam:{
                departmentID:departmentID,
                statisticsType: type,
                beginTime:beginTime,
                endTime:endTime,
                pageSize:40,
                pageNum:pageNo
            }
        }

        $.ajax({
            url: url,
            data : JSON.stringify(params),
            type: "POST",
            dataType: "json",
            async: false,
            contentType: "application/json;charset=utf-8",
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert(errorThrown + ':' + textStatus);  // 错误处理�����
            },
            success: function (data) {
                //发布稿件总量
                workload_stat.totalPublish(data);
                //饼图
                workload_stat.showPie(data);
                //缓存本日、本周、本月稿件量 
                workload_stat.saveCurrentData(data);
                //默认显示本月
                $("#thisMonthLine").click();
                //表格数据填充
                workload_stat.showResult(data, 'EmployeeDetailTable', 'deptEmployee');
                var totalCount = data.totalCount;
                var pageCount = data.pageCount;
                var currentPageNum = pageNo;
                workload_stat.showPaginator(totalCount, pageCount, pageNo, params, '_emp', 'EmployeeDetailTable', 'deptEmployee');
                $('#total_count_emp').val(totalCount);
                $('#total_count_emp_all').text(totalCount);
            }
        });
    },
    add_zero: function(param) {
        if (param < 10) return "0" + param;
        return "" + param;
    },
    saveCurrentData:function(data){
        workload_stat.currentData=data;
    },
    totalPublish:function(data){
        var statList = data.statisticsTotalData;
        var total=0;
        if (statList) {
            $.each(statList, function(i, bean){
                bean = parseInt(bean);
                total+=bean
            })
        }else{
            total=0;
        }
       

        $("#total-num").text(total);
    },
    showPie:function(data){
        var statList = data.statisticsTotalData;
        var monthDatas=[];
        var doctypes=[];
        if (statList) {
            $.each(statList, function(i, bean){
                var doctype="";
                if(i==0){
                    doctype="文章";
                }else if(i==1){
                    doctype="组图";
                }else if(i==2){
                    doctype="视频";
                }else if(i==3){
                    doctype="专题";
                }else if(i==4){
                    doctype="链接";
                }else if(i==5){
                    doctype="多标题";
                }else if(i==6){
                    doctype="直播";
                }else if(i==7){
                    doctype="活动";
                }else if(i==8){
                    doctype="广告";
                }else if(i==9){
                    doctype="文档";
                }else if(i==10){
                    doctype="问答";
                }else if(i==11){
                    doctype="全景图";
                }else if(i==12){
                    doctype="H5";
                }
                doctypes.push(doctype);
                bean = parseInt(bean);
                var monthData={value:bean, name:doctype};
                monthDatas.push(monthData);
            })
        }else{
            var monthDatas=[];
            var doctypes=[];
        }
        

        var div = document.getElementById("deptPie");
        if (!div) return;
        // 路径配置
        require.config({
            paths: {
                echarts: '../script/echarts'
            }
        });
        // 使用
        require(
            [
                'echarts',
                'echarts/chart/pie'
            ],
            function (ec) {
                // 基于准备好的dom，初始化echarts图表
                var myChart = ec.init(div);
                var monthData=monthDatas;
                var doctype=doctypes;//demo
                option = {
                    title : {
                       // text: '当月已发稿件类型占比',
                        textStyle:{
                            fontSize:"12",
                            color:"#333333"
                        },
                        //subtext: '纯属虚构',
                        x:'left',
                        //y:'bottom'
                    },
                    tooltip : {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c} ({d}%)"
                    },
                /*    legend: {
                        orient : 'vertical',
                        x : 'right',
                        data: doctype
                    },   */
                    toolbox: {
                        show : false,
                        feature : {
                            mark : {show: true},
                            dataView : {show: true, readOnly: false},
                            magicType : {
                                show: true,
                                type: ['pie', 'funnel'],
                                option: {
                                    funnel: {
                                        x: '25%',
                                        width: '50%',
                                        funnelAlign: 'left',
                                        max: 1548
                                    }
                                }
                            },
                            restore : {show: true},
                            saveAsImage : {show: true}
                        }
                    },
                    calculable : false,
                    series : [
                        {
                            name: '稿件类型',
                            type: 'pie',
                            radius: '60%',
                            center: ['50%', '55%'],
                            data:monthData
                        }
                    ]
                };
                if(monthData.length == 0){
                    myChart.showLoading({
                        text: '本月工作量\n暂无数据',
                        effect : 'bubble',
                        textStyle : {
                            fontSize : 25,
                            fontWeight: 'bold'
                        }
                    });
                }else{
                    // 为echarts对象加载数据
                    myChart.setOption(option);
                }
            }
        );
    },
    getLastDay:function(year,month){
        var new_year = year;  //取当前的年份
        var new_month = month++;//取下一个月的第一天，方便计算（最后一天不固定）
        if(month>12)      //如果当前大于12月，则年份转到下一年
        {
            new_month -=12;    //月份减
            new_year++;      //年份增
        }
        var new_date = new Date(new_year,new_month,1);        //取当年当月中的第一天
        return (new Date(new_date.getTime()-1000*60*60*24)).getDate();//获取当月最后一天日期
    },
    showLine:function(currentData,type){
        //横坐标日期
        var xAxisData=[];
        var _articleNum=[];
        var _totalClick=[];
        var _totalDiscuss=[];
        var _totalShare=[];
        if(type=="thisMonth"){
            var now = new Date();
            var year=now.getFullYear();
            var month=now.getMonth() + 1;
            var ym = now.getFullYear() + workload_stat.add_zero(now.getMonth() + 1);

            var day=workload_stat.getLastDay(year,month);

            for(var i=0;i<day;i++){
                var xAxis=ym+workload_stat.add_zero(i+1);
                xAxis=xAxis.substring(6);
                xAxisData.push(xAxis);
                _articleNum.push(0);
                _totalClick.push(0);
                _totalDiscuss.push(0);
                _totalShare.push(0);
            }
            var len=currentData.length;
            for(var i=0;i<day;i++){
                for(var j=0;j<len;j++){
                	//过滤date为null的项
                	if(currentData[j].date){
                		if(xAxisData[i]==currentData[j].date.substring(6)){
	                        _articleNum.splice(i,1,currentData[j].articleNum);
	                        _totalClick.splice(i,1,currentData[j].totalClick);
	                        _totalDiscuss.splice(i,1,currentData[j].totalDiscuss);
	                        _totalShare.splice(i,1,currentData[j].totalShare);
	                    }
                	}
                    
                }
            }
        }else if(type=="thisWeek"){
            //显示今日之前的本周
            var now = new Date();

            var week=now.getDay();
            var currweek;
            if(week==0){
                currweek= now.getTime() - 6 * 24 * 3600 * 1000;
            }else{
                currweek= now.getTime() - (week-1) * 24 * 3600 * 1000;
            }

            var fromTime = new Date(currweek);
            var fromYear = fromTime.getFullYear();
            var fromMonth = fromTime.getMonth() + 1;
            if(fromMonth == 0){
                fromYear -= 1;
                fromMonth = 12;
            }
            fromMonth = workload_stat.add_zero(fromMonth);
            var fromDay = workload_stat.add_zero(fromTime.getDate());

            var lastday=workload_stat.getLastDay(fromYear,fromMonth);

            var newYear=fromYear;
            var newMonth=fromMonth;
            var newDay=fromDay;
            for(var i=0;i<7;i++){
                var xAxis=newYear + newMonth +newDay;
                //xAxis=xAxis.substring(6);
                xAxisData.push(xAxis);
                _articleNum.push(0);
                _totalClick.push(0);
                _totalDiscuss.push(0);
                _totalShare.push(0);
                if(parseInt(newDay)<parseInt(lastday)){
                    newDay=workload_stat.add_zero(parseInt(newDay)+1);
                }else if(parseInt(newDay)==parseInt(lastday)){
                    newDay=workload_stat.add_zero(1);
                    if(parseInt(newMonth)<12){
                        newMonth=workload_stat.add_zero(parseInt(newMonth)+1);
                    }else if(parseInt(newMonth)==12){
                        newMonth=workload_stat.add_zero(1);
                        newYear=workload_stat.add_zero(parseInt(newYear)+1);
                    }
                }
            }
            var len=currentData.length;
            for(var i=0;i<7;i++){
                for(var j=0;j<len;j++){
                    if(xAxisData[i]==currentData[j].date){
                        _articleNum.splice(i,1,currentData[j].articleNum);
                        _totalClick.splice(i,1,currentData[j].totalClick);
                        _totalDiscuss.splice(i,1,currentData[j].totalDiscuss);
                        _totalShare.splice(i,1,currentData[j].totalShare);
                    }
                }
            }
        }else if(type=="thisDay"){
            var xAxisDataTemp=[];
            var now = new Date();
            var fromTime = new Date(now.getTime() - 24 * 3600 * 1000);
            var fromYear = fromTime.getFullYear();
            var fromMonth = fromTime.getMonth() + 1;
            if(fromMonth == 0){
                fromYear -= 1;
                fromMonth = 12;
            }
            fromMonth = workload_stat.add_zero(fromMonth);
            var fromDay = workload_stat.add_zero(fromTime.getDate());
            var fromHour = workload_stat.add_zero(fromTime.getHours());

            //获取本月最后一天的日期
            var lastday=workload_stat.getLastDay(fromYear,fromMonth);
            var newMonth=fromMonth;
            var newDay=fromDay;
            var newHour=fromHour;
            for(var i=0;i<25;i++){
                var xAxis=newMonth + newDay + newHour;
                var _xAxis=xAxis.substring(4);
                xAxisData.push(_xAxis);
                xAxisDataTemp.push(xAxis);
                _articleNum.push(0);
                _totalClick.push(0);
                _totalDiscuss.push(0);
                _totalShare.push(0);
                if(parseInt(newHour)<23){
                    newHour=workload_stat.add_zero(parseInt(newHour)+1)
                }else if(parseInt(newHour)==23){
                    newHour=workload_stat.add_zero(0);
                    if(parseInt(newDay)<parseInt(lastday)){
                        newDay=workload_stat.add_zero(parseInt(newDay)+1);
                    }else if(parseInt(newDay)==parseInt(lastday)){
                        newDay=workload_stat.add_zero(1);
                        if(parseInt(newMonth)<12){
                            newMonth=workload_stat.add_zero(parseInt(newMonth)+1);
                        }else if(parseInt(newMonth)==12){
                            newMonth=workload_stat.add_zero(1);
                        }
                    }
                }
            }

            var len=currentData.length;
            for(var i=0;i<25;i++){
                for(var j=0;j<len;j++){
                    if(xAxisDataTemp[i]==currentData[j].date){
                        _articleNum.splice(i,1,currentData[j].articleNum);
                        _totalClick.splice(i,1,currentData[j].totalClick);
                        _totalDiscuss.splice(i,1,currentData[j].totalDiscuss);
                        _totalShare.splice(i,1,currentData[j].totalShare);
                    }
                }
            }
        }else if(type=="thisDay"){
            var xAxisDataTemp=[];
            var now = new Date();
            var fromTime = new Date(now.getTime() - 24 * 3600 * 1000);
            var fromYear = fromTime.getFullYear();
            var fromMonth = fromTime.getMonth() + 1;
            if(fromMonth == 0){
                fromYear -= 1;
                fromMonth = 12;
            }
            fromMonth = workload_stat.add_zero(fromMonth);
            var fromDay = workload_stat.add_zero(fromTime.getDate());
            var fromHour = workload_stat.add_zero(fromTime.getHours());

            //获取本月最后一天的日期
            var lastday=workload_stat.getLastDay(fromYear,fromMonth);
            var newMonth=fromMonth;
            var newDay=fromDay;
            var newHour=fromHour;
            for(var i=0;i<25;i++){
                var xAxis=newMonth + newDay + newHour;
                var _xAxis=xAxis.substring(4);
                xAxisData.push(_xAxis);
                xAxisDataTemp.push(xAxis);
                _articleNum.push(0);
                _totalClick.push(0);
                _totalDiscuss.push(0);
                _totalShare.push(0);
                if(parseInt(newHour)<23){
                    newHour=workload_stat.add_zero(parseInt(newHour)+1)
                }else if(parseInt(newHour)==23){
                    newHour=workload_stat.add_zero(0);
                    if(parseInt(newDay)<parseInt(lastday)){
                        newDay=workload_stat.add_zero(parseInt(newDay)+1);
                    }else if(parseInt(newDay)==parseInt(lastday)){
                        newDay=workload_stat.add_zero(1);
                        if(parseInt(newMonth)<12){
                            newMonth=workload_stat.add_zero(parseInt(newMonth)+1);
                        }else if(parseInt(newMonth)==12){
                            newMonth=workload_stat.add_zero(1);
                        }
                    }
                }
            }

            var len=currentData.length;
            for(var i=0;i<25;i++){
                for(var j=0;j<len;j++){
                    if(xAxisDataTemp[i]==currentData[j].date){
                        _articleNum.splice(i,1,currentData[j].articleNum);
                        _totalClick.splice(i,1,currentData[j].totalClick);
                        _totalDiscuss.splice(i,1,currentData[j].totalDiscuss);
                        _totalShare.splice(i,1,currentData[j].totalShare);
                    }
                }
            }
        }else{
            var len=currentData.length;
            //横坐标日期
            var xAxisData=[];
            for(var i=0;i<len;i++){
                xAxisData.push(currentData[i].date);
            }
            //serial
            var _articleNum=[];
            var _totalClick=[];
            var _totalDiscuss=[];
            var _totalShare=[];
            for(var i=0;i<len;i++){
                _articleNum.push(currentData[i].articleNum);
                _totalClick.push(currentData[i].totalClick);
                _totalDiscuss.push(currentData[i].totalDiscuss);
                _totalShare.push(currentData[i].totalShare);
            }
        }

        var serial=[
            {   name:'发稿量',
                type:'line',
                data:_articleNum
            },
            {   name:'点击量',
                type:'line',
                data:_totalClick
            },
            {   name:'分享数',
                type:'line',
                data:_totalShare
            },
            {   name:'评论量',
                type:'line',
                data:_totalDiscuss
            }
        ];

        var div = document.getElementById("deptLine");
        if (!div) return;

        // 路径配置
        require.config({
            paths: {
                echarts: '../script/echarts'
            }
        });
        // 使用
        require(
            [
                'echarts',
                'echarts/chart/line'
            ],
            function (ec) {
                //var types = load_stat.artType;
                /*var _s = [];
                 for (var i = 0; i < types.length; i++) {
                 var one = {
                 name:types[i],
                 type:'line',
                 //stack: '总量',
                 data: stat_data["serialDatas" + suffix][i].split(",")
                 };
                 _s.push(one);
                 }*/
                option = {
                    title : {
                        text: '部门稿件统计',
                        x:'center',
                        textStyle:{
                            fontSize:"12",
                            color:"#333333"
                        }
                    },
                    tooltip : {trigger: 'axis'},
                    legend: {
                     //   orient : 'vertical',
                        x : 'center',
                        y: 'bottom',
                        data:['发稿量','点击量','分享数','评论量']  //load_stat.artType
                    },
                    toolbox: {
                        show : false,
                        feature : {
                            mark : {show: true},
                            dataView : {show: true, readOnly: false},
                            magicType : {show: true, type:['line', 'bar', 'stack', 'tiled']},
                            restore : {show: true},
                            saveAsImage : {show: true}
                        }
                    },
                    calculable : false,
                    xAxis : [{
                        type : 'category',
                        boundaryGap : false,
                        data : xAxisData,//['周一','周二','周三','周四','周五','周六','周日']  //load_stat.serialMonth
                        axisLabel:{
                            show:true,
                            interval: 0,//{number}
                            rotate: 0,
                            margin: 8
                        }
                    }],
                    yAxis : [ {type : 'value', name:'数值'} ],
                    series : serial
                };
                // 基于准备好的dom，初始化echarts图表
                var myChart = ec.init(div);
                // 为echarts对象加载数据
                myChart.setOption(option);
            }
        );
    },
    showResult : function(data, tableId, type) {
        var statList = data.personalTotalData;
        $('#'+ tableId +' tbody').empty();
        if(!statList){return};
        var str;
        $.each(statList, function(i, bean){
             str = '<tr class="tdtr">';
            //+ '<td class="tdtr1"><input type="radio" name="checkbox" authorid="'+bean.authorID+'" id="checkbox_emp"/></td>';
            if(type == 'deptEmployee') {
                str	= str + '<td class="tdtr"><a target="_blank" title="查看个人工作量明细" authorid="'+bean.authorID+'" class="showDetailMsg" href="javascript:void(0)">' + bean.authorName + '</a></td>';
            } else {
                str = str + '<td class="tdtr">Error TD!</td>';
            }
            str = str + '<td class="tdtr">' + bean.totalArticle + '</td>'
            + '<td class="tdtr">' + bean.wapTotalDays +'</td>'
            + '<td class="tdtr">' + bean.appTotalDays +'</td>'
            + '<td class="tdtr">' + bean.totalClick +'</td>'
            + '<td class="tdtr">' + bean.pcClick +'</td>'
            + '<td class="tdtr">' + bean.wapClick +'</td>'
            + '<td class="tdtr">' + bean.appClick +'</td>'
            + '<td class="tdtr">' + bean.totalForward +'</td>'
            + '<td class="tdtr">' + bean.pcForward +'</td>'
            + '<td class="tdtr">' + bean.wapForward +'</td>'
            + '<td class="tdtr">' + bean.appForward +'</td>'
            + '<td class="tdtr">' + bean.totalDiscussion +'</td>'
            + '<td class="tdtr">' + bean.pcDiscussion +'</td>'
            + '<td class="tdtr">' + bean.wapDiscussion +'</td>'
            + '<td class="tdtr">' + bean.appDiscussion +'</td>'
            + '</tr>';
            $('#'+ tableId +' tbody').append(str);
        });
    },
    outputcsv: function(tab) {
        var now = new Date();
        var ym = now.getFullYear() + "-" + workload_stat.add_zero(now.getMonth() + 1) + "-";
        var beginTime=$("#pubTime_from_emp").val();
        var endTime=$("#pubTime_to_emp").val();
        beginTime= beginTime=='' ? ym + "01" + " 00:00:00" : beginTime;
        endTime= endTime=='' ? ym + workload_stat.add_zero(now.getDate()) + " 23:59:59" : endTime;

        //确保开始时间小于结束时间
        var beginTimeNum= new Date(beginTime);
        var endTimeNum= new Date(endTime);
        if(beginTimeNum.getTime()>endTimeNum.getTime()){
            alert('“开始时间”晚于“结束时间”，请重新选择！');
            return
        };

        var departmentID=$("#dept_list").find(".selected").attr("departmentid") || "";

        var th = $('#EmployeeDetailTable' + tab + ' th');
        var thdatas = [];
        var length = th.length;
        for (var a = 1; a < length; a++) {
            thdatas.push($(th[a]).text());
        }
        var userIds=$("#employeeID").val();//"1,9,11"
        var siteID=getQueryString("siteID");
        var params = {
            fileName: '工作量.csv',
            headParam: thdatas,
            channelCode: "channelAll",
            siteID: siteID,
            dataParam: {
                particularParam:departmentID,
                exportType: "ArticleUsers",//exportType: "WorkDepartment",
                userIDs: userIds,//如果为空---查询全部
                beginTime:beginTime,
                endTime: endTime,
                pageNum: 1,
                pageSize:$('#total_count_emp').val()
            }
        }

        $('#jsonData').val(JSON.stringify(params));
        $("#form").attr("action", "../../xy/statistics/ExportCSV.do");
        $("#form").submit();
    },
    /*outputcsv: function(tab){
        var th = $('#EmployeeDetailTable' + tab + ' th');
        var jsonParam = '';
        var datas = [];
        var length = th.length;
        for(var a = 0; a < length; a++){
            jsonParam = {
                "1" : $(th[a++]).text(), //人员名称�
                "2" : $(th[a++]).text(), // 发稿量������
                "3" : $(th[a++]).text(), //总点击量 �ܵ����
                "4" : $(th[a++]).text(), // Web点击数�����
                "5" : $(th[a++]).text(), // 触屏点击数�����
                "6" : $(th[a++]).text(), // APP点击数�����
                "7" : $(th[a++]).text(), //总分享数���
                "8" : $(th[a++]).text(), // PC分享���
                "9" : $(th[a++]).text(), // WAP分享����
                "10" : $(th[a++]).text(), // APP分享����
                "11" : $(th[a++]).text(), // 总评论量��������
                "12" : $(th[a++]).text(), // PC评论量������
                "13" : $(th[a++]).text(), // WAP评论量������
                "14" : $(th[a]).text() // APP评论量������
            };
            datas.push(jsonParam);
        }
        $('#jsonData').val(JSON.stringify(datas));
        $('#csvName').val('工作量统计.csv');
       // $("#form").attr("action", "../../xy/stat/outputcsv.do");
        $("#form").attr("action", "../../xy/statistics/ExportCSV.do");
        $("#form").submit();
    },*/
    //workload_stat.showPaginator(totalCount, pageCount, pageNo, params, '_emp', 'EmployeeDetailTable', 'deptEmployee');
    showPaginator : function(totalCount, pageCount, pageNum, params, commet, tableId, selectType) {
        if(totalCount<1) {
            $('#paginator' + commet).hide();
            return;
        };
        var options = {
            bootstrapMajorVersion : 3, //使用的bootstrap版本为3，�
            alignment : "center", //�居中显示�����ʾ
            currentPage: pageNum, //�当前页数�
            totalPages: pageCount, //总页数�
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
            onPageClicked: function(event, originalEvent, clickType, page) {
                params.dataParam.pageNum = page;
                page++;
                $.ajax({
                    url: "../../xy/statistics/Statistics.do",
                    type: "POST",
                    dataType: "json",
                    async: false,
                    contentType: "application/json;charset=utf-8",
                    data : JSON.stringify(params),
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        alert(errorThrown + ':' + textStatus);  // �错误处理�����
                    },
                    success: function(data) {
                        if (data != null) {
                            workload_stat.showResult(data, tableId, selectType);
                            if(pageNum>1&&pageNum<=pageCount) {
                                pageNum++;
                                workload_stat.showPaginator(totalCount, pageCount, pageNum, params, commet, tableId, selectType);
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
                    "pageNum": 1
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
            success: function(data) {
                var len=data.departmentData.length;
                var departmentMsg='<li departmentID="">全部部门</li>';
                for (var i = 0; i < len; i++) {
                    departmentMsg +='<li departmentID="'+data.departmentData[i].departmentID+'">'+data.departmentData[i].departmentName+'</li>';
                }
                
                $("#dept_list").html(departmentMsg);
                $("#dept_list").find("li").eq(1).addClass("selected");
            }
        })

    },
    getIframeUrl: function(){
        var siteID=getQueryString("siteID");
        var t=getQueryString("t");
        var _url="../StatArticles.do?siteID="+siteID+"&t="+t+"&type=1";
        $("#partmentIfram").attr("src",_url);
    }
};
//---------部门人员名称查找框-------------
workload_stat.autoCompleter = {
    url : null,
    init : function(evt) {
        workload_stat.autoCompleter.url =workload_stat.autocompleteData;
        var s = $("#partmentSearch");
        s.autocomplete(workload_stat.autoCompleter.url, workload_stat.autoCompleter.options).result(workload_stat.autoCompleter.getSelectedID);
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
        var username=$("#partmentSearch").val();
        $("#departMan").find("li").each(function(){
            if($(this).text()==username){
                $(this).addClass("selected").siblings().removeClass("selected");
            }
        })

    }
};
workload_stat.autoCompleterApp = {
    url : null,
    init : function(evt) {
        workload_stat.autoCompleterApp.url = "../../xy/stat/Find.do?siteID=" + $('#siteID').val();

       /* var s = $("#srcName_App");
        s.autocomplete(workload_stat.autoCompleterApp.url, workload_stat.autoCompleterApp.options).result(workload_stat.autoCompleterApp.getSelectedID);
        s.focus();*/
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
        parse: function(data) {
            if (!data)
                return [];

            return $.map(eval(data), function(row) {
                return {
                    data: row,
                    value: row.value,
                    result: row.value
                }
            });
        },
        //显示在下拉框中的值
        formatItem: function(row, i,max) { return row.value; },
        formatMatch: function(row, i,max) { return row.value; },
        formatResult: function(row, i,max) { return row.value; }
    },
    getSelectedID : function(event, data, formatted){
        $("#srcID_App").val(data.key);
    }
};

//dom ready
$(function(){
    //初始化部门iframe地址
    workload_stat.getIframeUrl();
    workload_stat.init();
    //默认显示全部部门统计
    //workload_stat.search('','WorkDepartment');
    $("#thisMonthEmp").addClass("select_time");
   // 默认显示本月时间
    var now = new Date();
    var ym = now.getFullYear() + "-" + workload_stat.add_zero(now.getMonth() + 1) + "-";
    $('#pubTime_from_emp').val(ym + "01" + " 00:00:00");
    $('#pubTime_to_emp').val(ym + workload_stat.add_zero(now.getDate())+ " 23:59:59");

   // workload_stat.setCalender('thisMonth','_emp','Emp');
    workload_stat.search('2','WorkDepartment');
    //人员选择
    $("#employee").on("click",function(){
        var siteID=getQueryString("siteID");
        var departmentID=$("#dept_list").find(".selected").attr("departmentid");
        var url = "../../xy/statisticsutil/FindUserByDepartmentID.do";
        var params = {
                siteID: siteID,
                departmentID: departmentID
            };

        if(!departmentID || departmentID < 2){
            alert("请选择部门后再选择具体人员");
            return;
        }
        $("#partmentSearch").val("");
        $("#confirm").attr("data-flag","employeeSelect");
        $('#myModal').modal("show");
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
             var len=data.userData.length;
                var departmentMsg ='<li style="font-weight: 700;" id="alluser" userid="">全部</li>';
                for (var i = 0; i < len; i++) {
                    departmentMsg +='<li userid="'+data.userData[i].userID+'">'+data.userData[i].userName+'</li>';
                }
                
                $("#departMan").find("ul").html(departmentMsg);
                $("#departMan").attr("selecttype","more");
                //缓存部门信息
                workload_stat.saveDepartmentMsg(data);

            }
        })
        
    });

    //人员点击
    $("#departMan").on("click","li",function(){
        var _selectype=$("#departMan").attr("selecttype");
        if(_selectype=="more"){
            if($(this).attr("id")=="alluser"){
                $(this).addClass("selected").siblings().removeClass("selected");
            }else{
                $(this).toggleClass("selected");
                $("#alluser").removeClass("selected");
            }
        }else if(_selectype=="one"){
            $(this).addClass("selected").siblings().removeClass("selected");
        }
    });

    //确认选择
    $("#confirm").on("click",function(){
        var flag=$("#confirm").attr("data-flag");
        var valInput,uidInput;
        if(flag=="employeeSelect"){
            valInput=$("#employee");
            uidInput=$("#employeeID");
        }else if(flag=="exp_employee"){
            valInput=$("#partmentIfram").contents().find("#exp_employee");
            uidInput=$("#partmentIfram").contents().find("#exp_employeeID");
        }else if(flag=="exp_employee_app"){
            valInput=$("#partmentIfram").contents().find("#exp_employee_app");
            uidInput=$("#partmentIfram").contents().find("#exp_employeeID_app");
        }
        //var _man=$("#departMan").find(".selected").text();
        var _mans=[];
        $("#departMan").find(".selected").each(function(){
            _mans.push($(this).text())
        });
        var _man=_mans.join(",");

        var oldVal=valInput.val();
        if(!_man){
            alert("未选择人员！");
            return;
        }
        valInput.val(_man);
        //var _userid=$("#departMan").find(".selected").attr("userid");
        var _userids=[];
        $("#departMan").find(".selected").each(function(){
            _userids.push($(this).attr("userid"));
        });
        var _userid=_userids.join(",");
        var oldUid=uidInput.val();
       /* if(!_userid){
            _userid=oldUid;
        }*/
        uidInput.val(_userid);
        $('#myModal').modal("hide");

        //触发查询
        if(flag=="employeeSelect"){
            var userids=$("#employeeID").val();//"11,9,1"
            workload_stat.searchByUsers(userids,'ArticleUsers');
        }else if(flag=="exp_employee"){
            //子页面点击事件
            $("#partmentIfram").contents().find("#exp_lookMsg").click();
        }else if(flag=="exp_employee_app"){
            //子页面点击事件
            $("#partmentIfram").contents().find("#exp_lookMsg").click();
        }
    });
    //查看个人工作量明细 方式一
    /*$("#showDetailMsg").on("click",function(){
        $(this).attr("href","javascript:void(0)");
        var userid=$("#EmployeeDetailTable").find("input[type='radio']:checked").attr("authorid");
        var name=$("#EmployeeDetailTable").find("input[type='radio']:checked").parent().next().text();
        var partName=encodeURI(encodeURI($("#dept_list").find(".selected").text()));
        var userName=encodeURI(encodeURI(name));

        if($("#dept_list").find(".selected").attr("departmentid")<2){
            alert("请先选择具体部门！");
            return;
        }else if(!userid){
            alert("请先选择人员！");
            return;
        }
        var _href="../stat/Individual.html?t=stat1&siteID=1&userid="+userid+"&partname="+partName+"&username="+userName;
        $(this).attr("href",_href);
    });*/
    //查看个人工作量明细 方式二
    $("#EmployeeDetailTable").on("click",".showDetailMsg",function(){
        $(this).attr("href","javascript:void(0)");
        var userid=$(this).attr("authorid");
        var name=$(this).text();
        var partName=encodeURI(encodeURI($("#dept_list").find(".selected").text()));
        var userName=encodeURI(encodeURI(name));

        if($("#dept_list").find(".selected").attr("departmentid")<2){
            alert("请先选择具体部门！");
            return;
        }else if(!userid){
            alert("请先选择人员！");
            return;
        }
        var siteID=getQueryString("siteID");
        var _href="../stat/Individual.html?t=stat1&siteID="+siteID+"&userid="+userid+"&partname="+partName+"&username="+userName;
        $(this).attr("href",_href);
    });

});

//从url中读参数。url类似于<OperationURL>?DocLibID=..&DocIDs=...&...
function getParam(name) {
    var params = window.location.href;
    params = params.substring(params.indexOf("?") + 1, params.length);
    params = params.split("&");

    for (var i = 0; i < params.length; i++) {
        var arr = params[i].split("=");
        if (arr[0] == name) {
            return params[i].substring(name.length + 1, params[i].length);
        }
    }
    return null;
}
//从url中获取参数值
function getQueryString(name){
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r != null)return unescape(r[2]);
    return null;
}


//当用户提交选择的栏目,实现栏目选择树的接口
function columnClose(filterChecked, allFilterChecked) {
    $("#partmentIfram").contents().find("#exp_column" + workload_stat.colDialogWebApp).val(allFilterChecked[1]);
    $("#partmentIfram").contents().find("#exp_columnID" + workload_stat.colDialogWebApp).val(allFilterChecked[0]);
    columnCancel();
    //子页面点击事件
    $("#partmentIfram").contents().find("#exp_lookMsg").click();
}
//点取消
function columnCancel() {
    workload_stat.colDialog.close();
}