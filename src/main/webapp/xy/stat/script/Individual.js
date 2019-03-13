var Individual_stat = {
    colDialog : null,
    colDialogWebApp : null,
    currentData:null,
    autocompleteData:[],
    autocompleteData_Man:[],
    init : function() {
		//判断是否为工作量统计模块进入
		var currentUrl=window.location.href;
		var isWork_acount=currentUrl.indexOf("partname");
		if(isWork_acount!=-1){
			$(".font_color.other").css({display:"block"});
		}
        window.onload=function(){
            setTimeout(function(){
                $("#personDetail").contents().find("#exp_column,#exp_column_app").on("click",function(){
                    Individual_stat.colSelect();
                });
            },2000)

        }
        //各时间段折线图
        $("#thisMonth").click(function(){
            return Individual_stat.setLineTime('thisMonth','')
        });
        $("#thisWeek").click(function(){
            return Individual_stat.setLineTime('thisWeek','')
        });
        $("#thisDay").click(function(){
            return Individual_stat.setLineTime('thisDay','')
        });
		

    },
    //打开主栏目选择对话框
    colSelect: function(evt) {
        var ch = 1;
        Individual_stat.colDialogWebApp = '_app';
        var channel = $("#personDetail").contents().find("#main_search li.select").attr('channel');
        if (channel == 0) {
            ch = 0;
            Individual_stat.colDialogWebApp = '';
        }
        var siteID=getQueryString("siteID");
        var dataUrl = "../../xy/column/ColumnCheck.jsp?type=op&siteID=" +siteID + "&ids=" + $("#colID" + Individual_stat.colDialogWebApp).val() + "&ch=" + ch+"&style=radio&type=all";
        //var pos = columns_stat._getDialogPos(document.getElementById("colName" + columns_stat.colDialogWebApp));
        //获取滚动条滚动的距离
        //var _scrollTop=parseInt($(document).scrollTop()); _scrollTop+
        var pos = {left : "350px",top : "50px",width : "1000px",height : "500px"};
        Individual_stat.colDialog = e5.dialog({
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
        Individual_stat.colDialog.show();
       // setTimeout(function(){$(document).scrollTop(_scrollTop);},200);
    },
    saveDepartmentMsg:function(data){
        var autocompleteData=[];
        var len=data.departmentData.length;
        if(len<1) return;
        for (var i = 0; i < len; i++) {
            autocompleteData.push(data.departmentData[i].departmentName);
        }
        Individual_stat.autocompleteData=autocompleteData;
        Individual_stat.autoCompleter.init();

        //模糊查询
        /* $( "#partmentSearch" ).autocomplete({
         source: workload_stat.autocompleteData
         });*/
    },
    saveDepartmentMsg_Man:function(data){
        var autocompleteData_Man=[];
        var len=data.userData.length;
        if(len<1) return;
        for (var i = 0; i < len; i++) {
            autocompleteData_Man.push(data.userData[i].userName);
        }
        Individual_stat.autocompleteData_Man=autocompleteData_Man;
        Individual_stat.autoCompleter_Man.init();

        //模糊查询
        /* $( "#partmentSearch" ).autocomplete({
         source: workload_stat.autocompleteData
         });*/
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
            success: function(data) {var len=data.departmentData.length;
                var departmentMsg='';
                for (var i = 0; i < len; i++) {
                    departmentMsg +='<li departmentID="'+data.departmentData[i].departmentID+'">'+data.departmentData[i].departmentName+'</li>';
                }
                
                $("#departMent").find("ul").html(departmentMsg);

                //缓存部门信息
                Individual_stat.saveDepartmentMsg(data);
            }
        })

    },

    getCurrentUserInfo: function(){
		var url = "../../xy/statisticsutil/FindUserIdByUserCode.do";
		var code = e5.utils.getCookie("l$code");
        //var siteID=getQueryString("siteID");
		var params = {
			//siteID:siteID,
			userCode:code
		};
		
		$.ajax({
			url: url,
			data : JSON.stringify(params),
			type: "POST",
			dataType: "json",
			async: false,
			contentType: "application/json;charset=utf-8",
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				alert(errorThrown + ':' + textStatus);  // 错误处理
			},
			success: function (data) {
				var len=data.userData.length;
                var userMsg='';
                for (var i = 0; i < len; i++) {
                	//userMsg += "当前用户ID："+data.userData[i].userID+"，当前用户所在部门ID："+data.userData[i].departmentID;
                	Individual_stat.search(data.userData[i].userID,'WorkDepartmentWithPerson','');
                    //子页面点击事件
                    $("#personDetail").contents().find("#exp_lookMsg").click();
                    $("#detail-partment").text(data.userData[i].departmentName);
                    $("#detail-man").text(data.userData[i].userName);
                    $("#detail-man").attr("userid",data.userData[i].userID);
                }
			}
		});
	},
    
    // 点击查看 eg thisMonth, channelWeb, _dept_web
    search: function(userID,type,isBatman){
        var url = "../../xy/statistics/Statistics.do";
        /*var now = new Date();
        var ym = now.getFullYear() + "-" + workload_stat.add_zero(now.getMonth() + 1) + "-";
        var beginTime=$("#pubTime_from_emp").val();
        var endTime=$("#pubTime_to_emp").val();
        beginTime= beginTime=='' ? ym + "01" + " 00:00:00" : beginTime;
        endTime= endTime=='' ? ym + workload_stat.add_zero(now.getDate()) + " 23:59:59" : endTime;
*/
        var siteID=getQueryString("siteID");
        
        //var pageNo = 1;
        var params = {
            channelCode: "channelAll",
            siteID:siteID,
            dataParam:{
                userID: userID,
                statisticsType: type,
                isBatman:isBatman

                /*departmentID:departmentID,
                statisticsType: type,
                beginTime:beginTime,
                endTime:endTime,
                pageSize:40,
                pageNum:pageNo*/
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
                Individual_stat.totalPublish(data);
                //饼图
                Individual_stat.showPie(data);
                //缓存本日、本周、本月稿件量 
                Individual_stat.saveCurrentData(data);
                //默认显示本月
                $("#thisMonth").click();
            }
        });
    },
    add_zero: function(param) {
        if (param < 10) return "0" + param;
        return "" + param;
    },
    saveCurrentData:function(data){
        Individual_stat.currentData=data;
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
        
       

        var div = document.getElementById("countPie");
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
                      //  text: '当月已发稿件类型占比',
                        textStyle:{
                            fontSize:"12",
                            color:"#333333"
                        },
                        //subtext: '纯属虚构',
                        x:'left'
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
        /*var len=currentData.length;
        //横坐标日期
        var xAxisData=[];
        for(var i=0;i<len;i++){
            xAxisData.push(currentData[i].date)
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
        }*/
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
            var ym = now.getFullYear() + Individual_stat.add_zero(now.getMonth() + 1);

            var day=Individual_stat.getLastDay(year,month);

            for(var i=0;i<day;i++){
                var xAxis=ym+Individual_stat.add_zero(i+1);
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
                    if(xAxisData[i]==currentData[j].date.substring(6)){
                        _articleNum.splice(i,1,currentData[j].articleNum);
                        _totalClick.splice(i,1,currentData[j].totalClick);
                        _totalDiscuss.splice(i,1,currentData[j].totalDiscuss);
                        _totalShare.splice(i,1,currentData[j].totalShare);
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
            fromMonth = Individual_stat.add_zero(fromMonth);
            var fromDay = Individual_stat.add_zero(fromTime.getDate());

            var lastday=Individual_stat.getLastDay(fromYear,fromMonth);

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
                    newDay=Individual_stat.add_zero(parseInt(newDay)+1);
                }else if(parseInt(newDay)==parseInt(lastday)){
                    newDay=Individual_stat.add_zero(1);
                    if(parseInt(newMonth)<12){
                        newMonth=Individual_stat.add_zero(parseInt(newMonth)+1);
                    }else if(parseInt(newMonth)==12){
                        newMonth=Individual_stat.add_zero(1);
                        newYear=Individual_stat.add_zero(parseInt(newYear)+1);
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
            fromMonth = Individual_stat.add_zero(fromMonth);
            var fromDay = Individual_stat.add_zero(fromTime.getDate());
            var fromHour = Individual_stat.add_zero(fromTime.getHours());

            //获取本月最后一天的日期
            var lastday=Individual_stat.getLastDay(fromYear,fromMonth);
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
                    newHour=Individual_stat.add_zero(parseInt(newHour)+1)
                }else if(parseInt(newHour)==23){
                    newHour=Individual_stat.add_zero(0);
                    if(parseInt(newDay)<parseInt(lastday)){
                        newDay=Individual_stat.add_zero(parseInt(newDay)+1);
                    }else if(parseInt(newDay)==parseInt(lastday)){
                        newDay=Individual_stat.add_zero(1);
                        if(parseInt(newMonth)<12){
                            newMonth=Individual_stat.add_zero(parseInt(newMonth)+1);
                        }else if(parseInt(newMonth)==12){
                            newMonth=Individual_stat.add_zero(1);
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
            fromMonth = Individual_stat.add_zero(fromMonth);
            var fromDay = Individual_stat.add_zero(fromTime.getDate());
            var fromHour = Individual_stat.add_zero(fromTime.getHours());

            //获取本月最后一天的日期
            var lastday=Individual_stat.getLastDay(fromYear,fromMonth);
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
                    newHour=Individual_stat.add_zero(parseInt(newHour)+1)
                }else if(parseInt(newHour)==23){
                    newHour=Individual_stat.add_zero(0);
                    if(parseInt(newDay)<parseInt(lastday)){
                        newDay=Individual_stat.add_zero(parseInt(newDay)+1);
                    }else if(parseInt(newDay)==parseInt(lastday)){
                        newDay=Individual_stat.add_zero(1);
                        if(parseInt(newMonth)<12){
                            newMonth=Individual_stat.add_zero(parseInt(newMonth)+1);
                        }else if(parseInt(newMonth)==12){
                            newMonth=Individual_stat.add_zero(1);
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
            {   name:'总点击量',
                type:'line',
                data:_totalClick
            },
            {   name:'总评论量',
                type:'line',
                data:_totalDiscuss
            },
            {   name:'总分享数',
                type:'line',
                data:_totalShare
            },
        ];

        var div = document.getElementById("countLine");
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
                        text: '个人稿件统计',
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
                        data:['发稿量','总点击量','总评论量','总分享数']  //load_stat.artType
                    },
                    toolbox: {
                        show : false,
                        feature : {
                            mark : {show: true},
                            dataView : {show: true, readOnly: false},
                            magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
                            restore : {show: true},
                            saveAsImage : {show: true}
                        }
                    },
                    calculable : false,
                    xAxis : [{
                        type : 'category',
                        boundaryGap : false,
                        data : xAxisData, //['周一','周二','周三','周四','周五','周六','周日']  //load_stat.serialMonth
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
    // 点击发布时间 改变折线图的数值变化
    setLineTime : function(type,commet){
        var currentData;
        if(type == 'thisMonth'){
            $('#thisMonth' +commet).css('color', '#fff').css('width','70px');
            $('#thisMonth' +commet).css('background', '#1bb8fa');
            $('#thisMonth' +commet).siblings().css('color', '#333');
            $('#thisMonth' +commet).siblings().css('background', '');

            currentData=Individual_stat.currentData.currentMonthData;

        }else if(type == 'thisDay'){
            $('#thisDay' +commet).css('color', '#fff').css('width','70px');
            $('#thisDay' +commet).css('background', '#1bb8fa');
            $('#thisDay' +commet).siblings().css('color', '#333');
            $('#thisDay' +commet).siblings().css('background', '');
            
            currentData=Individual_stat.currentData.current24HoursData;

        } else if(type == 'thisWeek'){
			$('#thisWeek' +commet).css('color', '#fff').css('width','70px');
            $('#thisWeek' +commet).css('background', '#1bb8fa');
            $('#thisWeek' +commet).siblings().css('color', '#333');
            $('#thisWeek' +commet).siblings().css('background', '');
            
            currentData=Individual_stat.currentData.currentWeekData;

        }
        Individual_stat.showLine(currentData,type);
    },
    getEchartData:function(type){
        var url="./demoEchart_"+type+".json";
        $.ajax({
            url:url,
            //data: JSON.stringify(params),
            type: "GET",
            dataType: "json",
            async: false,
            contentType: "application/json;charset=utf-8",
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert(errorThrown + ':' + textStatus); // 错误处理
            },
            success: function(data) {
                var monthData=data.monthData;
                var countType=data.countType;

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
                    ],function(ec){
                        var lineOption= {
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
                                data:countType  //load_stat.artType
                            },
                            toolbox: {
                                show : false,
                                feature : {
                                    mark : {show: true},
                                    dataView : {show: true, readOnly: false},
                                    magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
                                    restore : {show: true},
                                    saveAsImage : {show: true}
                                }
                            },
                            calculable : false,
                            xAxis : [{
                                type : 'category',
                                boundaryGap : false,
                                data : ['周一','周二','周三','周四','周五','周六','周日']  //load_stat.serialMonth
                            }],
                            yAxis : [ {type : 'value', name:'数值'} ],
                            series : monthData
                        };

                        var myChart = ec.init(document.getElementById("countLine"));
                        myChart.setOption(lineOption);
                    })

            }
        });

    },
    getIframeUrl: function(){
        var siteID=getQueryString("siteID");
        var t=getQueryString("t");
        var _url="../StatArticles.do?siteID="+siteID+"&t="+t+"&type=2";
        $("#personDetail").attr("src",_url);
    }

};
//---------部门名称查找框-------------
Individual_stat.autoCompleter = {
    url : null,
    init : function(evt) {
        Individual_stat.autoCompleter.url =Individual_stat.autocompleteData;
        var s = $("#departmentSearch");
        s.autocomplete(Individual_stat.autoCompleter.url, Individual_stat.autoCompleter.options).result(Individual_stat.autoCompleter.getSelectedID);
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
        $("#departMent").find("li").each(function(){
            if($(this).text()==username){
                $(this).addClass("selected").siblings().removeClass("selected");
                $(this).click();
            }
        })

    }
};

//---------人员名称查找框-------------
Individual_stat.autoCompleter_Man = {
    url : null,
    init : function(evt) {
        Individual_stat.autoCompleter_Man.url =Individual_stat.autocompleteData_Man;
        var s = $("#departmanSearch");
        s.autocomplete(Individual_stat.autoCompleter_Man.url, Individual_stat.autoCompleter_Man.options).result(Individual_stat.autoCompleter_Man.getSelectedID);
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
        var username=$("#departmanSearch").val();
        $("#departMan").find("li").each(function(){
            if($(this).text()==username){
                $(this).addClass("selected").siblings().removeClass("selected");
            }
        })

    }
};


$(function(){
    //初始化个人明细iframe地址
    Individual_stat.getIframeUrl();
    Individual_stat.init();
    //m默认显示空页面
    var userid=getQueryString("userid");
    var partname=decodeURI(decodeURI(getQueryString("partname")));
    var username=decodeURI(decodeURI(getQueryString("username")));
    var isBatman=decodeURI(decodeURI(getQueryString("isbatman")));
    if(partname=="null" && userid){ 
        $("#otherPeople").hide();
        $("#detail-partment").hide();
    }else{ 
        $("#otherPeople").show();
        $("#detail-partment").show();
    }

    if(userid == '' || !userid){
        /*partname= "部门";
        username= "人员";*/
        Individual_stat.getCurrentUserInfo();
    } else {
        Individual_stat.search(userid,'WorkDepartmentWithPerson',isBatman);
        //子页面点击事件
        $("#personDetail").contents().find("#exp_lookMsg").click();
        $("#detail-partment").text(partname);
        $("#detail-man").text(username);
        $("#detail-man").attr("userid",userid);
    }



    //其他人员
    $("#otherPeople").on("click",function(){
        $("#departmentSearch").val("");
        $("#departmanSearch").val("");
        $('#myModal').modal("show");
        $("#departMan").find("ul").html("");
        Individual_stat.getDepartmentData();
    });

    //部门点击
    $("#departMent").on("click","li",function(){
        $(this).addClass("selected").siblings().removeClass("selected");

        var siteID=getQueryString("siteID");
        var departmentID=$("#departMent").find(".selected").attr("departmentid");
        var url = "../../xy/statisticsutil/FindUserByDepartmentID.do";
        var params = {
                siteID: siteID,
                departmentID: departmentID
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
             var len=data.userData.length;
                var departmentMsg='';
                for (var i = 0; i < len; i++) {
                    departmentMsg +='<li userid="'+data.userData[i].userID+'">'+data.userData[i].userName+'</li>';
                }
                
                $("#departMan").find("ul").html(departmentMsg);

                //缓存人员信息
                Individual_stat.saveDepartmentMsg_Man(data);
            }
        })
    });

    //人员点击
    $("#departMan").on("click","li",function(){
        $(this).addClass("selected").siblings().removeClass("selected");
    });

    //确认选择
    $("#confirm").on("click",function(){
        var _part=$("#departMent").find(".selected").text();
        var _man=$("#departMan").find(".selected").text();
        var _userID=$("#departMan").find(".selected").attr("userid");

        if(!_part || !_man){return};
        $("#detail-partment").text(_part);
        $("#detail-man").text(_man);
        $("#detail-man").attr("userid",_userID);
        $('#myModal').modal("hide");

        var userID=$("#detail-man").attr("userid");
        Individual_stat.search(userID,'WorkDepartmentWithPerson','');
        //子页面点击事件
        $("#personDetail").contents().find("#exp_lookMsg").click();
    })

});

//从url中获取参数值
function getQueryString(name){
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r != null)return unescape(r[2]);
    return null;
}
//当用户提交选择的栏目,实现栏目选择树的接口
function columnClose(filterChecked, allFilterChecked) {
    $("#personDetail").contents().find("#exp_column" + Individual_stat.colDialogWebApp).val(allFilterChecked[1]);
    $("#personDetail").contents().find("#exp_columnID" + Individual_stat.colDialogWebApp).val(allFilterChecked[0]);
    //子页面点击事件
    $("#personDetail").contents().find("#exp_lookMsg").click();
    columnCancel();
}
//点取消
function columnCancel() {
    Individual_stat.colDialog.close();
}
