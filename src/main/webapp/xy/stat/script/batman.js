var batman_stat = {
	colDialog : null,
	colDialogWebApp : null,
    autocompleteData:[],
	init : function() {
		// 日期控件
		batman_stat.initDateTimePickerWeb();
		// 自动提示控件
		//batman_stat.autoCompleter.init();
		//batman_stat.autoCompleterApp.init();

		//时间选择
		$(".time_stat").click(function(){
			$(this).addClass("select_time").siblings().removeClass("select_time");
		});

		//重置
		$("#reset_batman").click(batman_stat.resetBatman_reset);

	},
    saveDepartmentMsg:function(data){
        var autocompleteData=[];
        var len=data.batmanData.length;
        for (var i = 0; i < len; i++) {
            autocompleteData.push(data.batmanData[i].batmanName);
        }
        batman_stat.autocompleteData=autocompleteData;
        batman_stat.autoCompleter.init();

        //模糊查询
        /* $( "#partmentSearch" ).autocomplete({
         source: workload_stat.autocompleteData
         });*/
    },
	resetBatman_reset : function(){
		$("#batmanName").val("");
        $("#batmanID").val("");
		$(this).siblings('a').removeClass("select_time");
		$("#pubTime_from").val("");
		$("#pubTime_to").val("");
		$("#BatmanDetailTable tbody").empty();
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

	// 点击上月本月要同时改变日历的值
	setCalender : function(type) {
		var now = new Date();
		if(type == 'thisMonth'){
			var ym = now.getFullYear() + "-" + batman_stat.add_zero(now.getMonth() + 1) + "-";
			$('#pubTime_from').val(ym + "01" + " 00:00:00");
			$('#pubTime_to').val(ym + batman_stat.add_zero(now.getDate())+ " 23:59:59");

		} else if(type == 'lastMonth'){
			var year = now.getFullYear();
			var month = now.getMonth();
			if(month == 0){
				year -= 1;
				month = 12;
			}
			var ym = year + "-" + batman_stat.add_zero(month) + "-";
			$('#pubTime_from').val(ym + "01"+ " 00:00:00");
			now.setDate(1);
			now.setMonth(now.getMonth());
			var cdt = new Date(now.getTime() - 1000 * 60 * 60 * 24);
			$('#pubTime_to').val(ym + cdt.getDate()+ " 23:59:59");

		} else if(type == 'current24H'){
			var fromTime = new Date(now.getTime() - 24 * 3600 * 1000);
			var fromYear = fromTime.getFullYear();
			var fromMonth = fromTime.getMonth() + 1;
			if(fromMonth == 0){
				fromYear -= 1;
				fromMonth = 12;
			}
			fromMonth = batman_stat.add_zero(fromMonth);
			var fromDay = batman_stat.add_zero(fromTime.getDate());
			var fromHour = batman_stat.add_zero(fromTime.getHours());
			var fromMinutes = batman_stat.add_zero(fromTime.getMinutes());
			var fromSeconds = batman_stat.add_zero(fromTime.getSeconds());
			var newFromTime = fromYear + '-' + fromMonth + '-' + fromDay + ' ' + fromHour + ':' + fromMinutes + ':' + fromSeconds;
			$('#pubTime_from').val(newFromTime);
			var nowTime = new Date(now.getTime());
			var nowYear = nowTime.getFullYear();
			var nowMonth = nowTime.getMonth() + 1;
			if(nowMonth == 0){
				nowYear -= 1;
				nowMonth = 12;
			}
			nowMonth = batman_stat.add_zero(nowMonth);
			var nowDay = batman_stat.add_zero(nowTime.getDate());
			var nowHour = batman_stat.add_zero(nowTime.getHours());
			var nowMinutes = batman_stat.add_zero(nowTime.getMinutes());
			var nowSeconds = batman_stat.add_zero(nowTime.getSeconds());
			var newNowTime = nowYear + '-' + nowMonth + '-' + nowDay + ' ' + nowHour + ':' + nowMinutes + ':' + nowSeconds;
			$('#pubTime_to').val(newNowTime);

		} else if(type == 'current7D'){
			var fromTime = new Date(now.getTime() - 7 * 24 * 3600 * 1000);
			var fromYear = fromTime.getFullYear();
			var fromMonth = fromTime.getMonth() + 1;
			if(fromMonth == 0){
				fromYear -= 1;
				fromMonth = 12;
			}
			fromMonth = batman_stat.add_zero(fromMonth);
			var fromDay = batman_stat.add_zero(fromTime.getDate());
			var newFromTime = fromYear + '-' + fromMonth + '-' + fromDay + " 00:00:00";
			$('#pubTime_from').val(newFromTime);
			var nowTime = new Date(now.getTime());
			var nowYear = nowTime.getFullYear();
			var nowMonth = nowTime.getMonth() + 1;
			if(nowMonth == 0){
				nowYear -= 1;
				nowMonth = 12;
			}
			nowMonth = batman_stat.add_zero(nowMonth);
			var nowDay = batman_stat.add_zero(nowTime.getDate());
			var newNowTime = nowYear + '-' + nowMonth + '-' + nowDay + " 23:59:59";
			$('#pubTime_to').val(newNowTime);


		} else if(type == 'current14D'){
			var fromTime = new Date(now.getTime() - 14 * 24 * 3600 * 1000);
			var fromYear = fromTime.getFullYear();
			var fromMonth = fromTime.getMonth() + 1;
			if(fromMonth == 0){
				fromYear -= 1;
				fromMonth = 12;
			}
			fromMonth = batman_stat.add_zero(fromMonth);
			var fromDay = batman_stat.add_zero(fromTime.getDate());
			var newFromTime = fromYear + '-' + fromMonth + '-' + fromDay + " 00:00:00";
			$('#pubTime_from').val(newFromTime);
			var nowTime = new Date(now.getTime());
			var nowYear = nowTime.getFullYear();
			var nowMonth = nowTime.getMonth() + 1;
			if(nowMonth == 0){
				nowYear -= 1;
				nowMonth = 12;
			}
			nowMonth = batman_stat.add_zero(nowMonth);
			var nowDay = batman_stat.add_zero(nowTime.getDate());
			var newNowTime = nowYear + '-' + nowMonth + '-' + nowDay + " 23:59:59";
			$('#pubTime_to').val(newNowTime);

		} else if(type == 'current30D'){
			var fromTime = new Date(now.getTime() - 30 * 24 * 3600 * 1000);
			var fromYear = fromTime.getFullYear();
			var fromMonth = fromTime.getMonth() + 1;
			if(fromMonth == 0){
				fromYear -= 1;
				fromMonth = 12;
			}
			fromMonth = batman_stat.add_zero(fromMonth);
			var fromDay = batman_stat.add_zero(fromTime.getDate());
			var newFromTime = fromYear + '-' + fromMonth + '-' + fromDay + " 00:00:00";
			$('#pubTime_from').val(newFromTime);
			var nowTime = new Date(now.getTime());
			var nowYear = nowTime.getFullYear();
			var nowMonth = nowTime.getMonth() + 1;
			if(nowMonth == 0){
				nowYear -= 1;
				nowMonth = 12;
			}
			nowMonth = batman_stat.add_zero(nowMonth);
			var nowDay = batman_stat.add_zero(nowTime.getDate());
			var newNowTime = nowYear + '-' + nowMonth + '-' + nowDay + " 23:59:59";
			$('#pubTime_to').val(newNowTime);

		}
        batman_stat.search();
	},

	add_zero:function(param){
		if(param < 10) return "0" + param;
		return "" + param;
	},

	// 点击查看 eg thisMonth, channelWeb, _dept_web
	search: function(){
        var now = new Date();
        var ym = now.getFullYear() + "-" + batman_stat.add_zero(now.getMonth() + 1) + "-";
        var beginTime=$('#pubTime_from').val();
        var endTime=$('#pubTime_to').val();
        if(!beginTime){
            $('#pubTime_from').val(ym + "01" + " 00:00:00");
            $('#pubTime_to').val(ym + batman_stat.add_zero(now.getDate()) + " 23:59:59");
            $("#thisMonth").addClass("select_time");
        }
        beginTime= beginTime=='' ? ym + "01" + " 00:00:00" : beginTime;
        endTime= endTime=='' ? ym + batman_stat.add_zero(now.getDate()) + " 23:59:59" : endTime;

        //确保开始时间小于结束时间
        var beginTimeNum= new Date(beginTime);
        var endTimeNum= new Date(endTime);
        if(beginTimeNum.getTime()>endTimeNum.getTime()){
            alert('“开始时间”晚于“结束时间”，请重新选择！');
            return;
        }

		var url = "../../xy/statistics/Statistics.do";
		var pageNo = 1;
        var siteID=getQueryString("siteID");
		var params = {
			channelCode: "channelAll",
			siteID:siteID,
			dataParam:{
				batmanID:$('#batmanID').val(),
				statisticsType: 'ArticleBatman',
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
				alert(errorThrown + ':' + textStatus);  // 错误处理
			},
			success: function (data) {

				batman_stat.showResult(data);
				var totalCount = data.totalCount;
				var pageCount = data.pageCount;
				var currentPageNum = pageNo;
				batman_stat.showPaginator(totalCount, pageCount, pageNo, params);
				$('#total_count').val(totalCount);
                $('#total_coun_all').text(totalCount);
			}
		});
	},
    add_zero: function(param) {
        if (param < 10) return "0" + param;
        return "" + param;
    },
	showResult : function(data) {
		var statList = data.statisticsData;
		$('#BatmanDetailTable tbody').empty();
        var str="";
		$.each(statList, function(i, bean){
			str = str + '<tr class="tdtr">'
			//+ '<td class="tdtr1"><input type="radio" name="radio" authorid="'+bean.authorID+'" /></td>'
			//+ '<td class="tdtr">' + bean.authorName + '</td>'
            + '<td class="tdtr"><a target="_blank" title="查看个人明细" authorid="'+bean.authorID+'" class="showDetail" href="javascript:void(0)">' + bean.authorName + '</a></td>'
			+ '<td class="tdtr">' + bean.totalArticle + '</td>'
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
		});
        $('#BatmanDetailTable tbody').append(str);
	},
	outputcsv: function() {
        var beginTime=$('#pubTime_from').val();
        var endTime=$('#pubTime_to').val();
        var now = new Date();
        var ym = now.getFullYear() + "-" + batman_stat.add_zero(now.getMonth() + 1) + "-";
        beginTime= beginTime=='' ? ym + "01" + " 00:00:00" : beginTime;
        endTime= endTime=='' ? ym + batman_stat.add_zero(now.getDate()) + " 23:59:59" : endTime;

        //确保开始时间小于结束时间
        var beginTimeNum= new Date(beginTime);
        var endTimeNum= new Date(endTime);
        if(beginTimeNum.getTime()>endTimeNum.getTime()){
            alert('“开始时间”晚于“结束时间”，请重新选择！');
            return;
        }

		var th = $('#BatmanDetailTable tr th');
		var thdatas = [];
		var length = th.length;
		for (var a = 1; a < length; a++) {
			thdatas.push($(th[a]).text());
		}
        var siteID=getQueryString("siteID");
		var params = {
			channelCode: "channelAll",
			fileName: '通讯员稿件统计.csv',
			headParam: thdatas,
			siteID:siteID,
			dataParam:{
				batmanID:$('#batmanID').val(),
				exportType: 'ArticleBatman',
				beginTime:beginTime,
				endTime:endTime,
				pageSize:$('#total_count').val(),
				pageNum:1
			}
		}

		$('#jsonData').val(JSON.stringify(params));
		$("#form").attr("action", "../../xy/statistics/ExportCSV.do");
		$("#form").submit();
	},

	showPaginator : function(totalCount, pageCount, pageNum, params) {
        if(totalCount<1) {
            $('#paginator').hide();
            return;
        };
		var options = {
			bootstrapMajorVersion : 3, //使用的bootstrap版本为3，
			alignment : "center", //居中显示
			currentPage: pageNum, //当前页数
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
						alert(errorThrown + ':' + textStatus);  // 错误处理
					},
					success: function(data) {
						if (data != null) {
							batman_stat.showResult(data);
							if(pageNum>1&&pageNum<=pageCount){
								pageNum++;
							batman_stat.showPaginator(totalCount, pageCount, pageNum, params);
							}
						}
					}
				});
			}
		};
        $('#paginator').show();
		$('#paginator').bootstrapPaginator(options);
	}
};
//---------通讯员名称查找框-------------
batman_stat.autoCompleter = {
	url : null,
	init : function(evt) {
		//batman_stat.autoCompleter.url = "../../xy/stat/Find.do?siteID=" + $('#siteID').val();
        batman_stat.autoCompleter.url =batman_stat.autocompleteData;
		var s = $("#BatManSearch");
		s.autocomplete(batman_stat.autoCompleter.url, batman_stat.autoCompleter.options).result(batman_stat.autoCompleter.getSelectedID);
		s.focus();
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
					data: row,
					value: row.value,
					result: row.value
				}
			});
		},*/
		//显示在下拉框中的值
		/*formatItem: function(row, i,max) { return row.value; },
		formatMatch: function(row, i,max) { return row.value; },
		formatResult: function(row, i,max) { return row.value; }*/
        formatItem: function(row, i,max) { return row[0];},
        formatMatch: function(row, i,max) { return row[0];},
        formatResult: function(row, i,max) { return row[0];}
	},
	getSelectedID : function(event, data, formatted){
		//$("#srcID").val(data.key);
        var username=$("#BatManSearch").val();
        $("#BatMan").find("li").each(function(){
            if($(this).text()==username){
                $(this).addClass("selected").siblings().removeClass("selected");
            }
        })
	}
};
batman_stat.autoCompleterApp = {
	url : null,
	init : function(evt) {
		batman_stat.autoCompleterApp.url = "../../xy/stat/Find.do?siteID=" + $('#siteID').val();
		//var s = $("#BatManSearch");
		s.autocomplete(batman_stat.autoCompleterApp.url, batman_stat.autoCompleterApp.options).result(batman_stat.autoCompleterApp.getSelectedID);
		s.focus();
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
		/*formatItem: function(row, i,max) { return row.value; },
		formatMatch: function(row, i,max) { return row.value; },
		formatResult: function(row, i,max) { return row.value; }*/
	},
	getSelectedID : function(event, data, formatted){
		$("#srcID_App").val(data.key);
	}
}
$(function(){
	batman_stat.init();
    //默认显示本月的数据
    $("#thisMonth").click();
    batman_stat.setCalender('thisMonth');
    //batman_stat.search();
	//通讯员选择
	$("#batmanName").on("click",function(){
        $("#BatManSearch").val("");
		$('#myModal').modal("show");
		//获取通讯员信息
		var url = "../../xy/statisticsutil/FindBatman.do";
        var siteID=getQueryString("siteID");
        var params = {
                siteID: siteID
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
                var len=data.batmanData.length;
                var departmentMsg='<li style="font-weight: 700;" id="alluser" userid="">全部</li>';
                for (var i = 0; i < len; i++) {
                    departmentMsg +='<li userid="'+data.batmanData[i].batmanID+'">'+data.batmanData[i].batmanName+'</li>';
                }
                
                $("#BatMan").find("ul").html(departmentMsg);
                //缓存通讯员信息
                batman_stat.saveDepartmentMsg(data);
            }
        })

	});

	//人员点击
	$("#BatMan").on("click","li",function(){
		//$(this).addClass("selected").siblings().removeClass("selected");
        if($(this).attr("id")=="alluser"){
            $(this).addClass("selected").siblings().removeClass("selected");
        }else{
            $(this).toggleClass("selected");
            $("#alluser").removeClass("selected");
        }
	});

	//确认选择
	$("#confirm").on("click",function(){
        var _mans=[];
        $("#BatMan").find(".selected").each(function(){
            _mans.push($(this).text())
        });
        var _man=_mans.join(",");

        var oldVal=$("#batmanName").val();
        if(!_man){
            alert("未选择人员！");
            return;
        }
        $("#batmanName").val(_man);

		//var _man=$("#BatMan").find(".selected").text();
		//var _userid=$("#BatMan").find(".selected").attr("userid");
        var _userids=[];
        $("#BatMan").find(".selected").each(function(){
            _userids.push($(this).attr("userid"));
        });
        var _userid=_userids.join(",");

        /*var oldUid=$("#batmanID").val();
        if(!_userid){
            _userid=oldUid;
        }*/
        $("#batmanID").val(_userid);

		$('#myModal').modal("hide");
        //默认显示本月的数据
        $("#thisMonth").click();
        batman_stat.setCalender('thisMonth');
	})
    //查看个人明细方式一
    /*$("#showDetail").on("click",function(){
        $(this).attr("href","javascript:void(0)");
        var userid=$("#BatmanDetailTable").find("input[type='radio']:checked").attr("authorid");
        //var userid=$("#employeeID").val();
        var name=$("#BatmanDetailTable").find("input[type='radio']:checked").parent().next().text();
        //var partName=encodeURI(encodeURI($("#dept_list").find(".selected").text()));
        var userName=encodeURI(encodeURI(name));

        if(!userid){
            alert("请先选择人员！");
            return;
        }
        var _href="../stat/Individual.html?t=stat1&siteID=1&userid="+userid+"&username="+userName+"&isbatman=batman";
        $(this).attr("href",_href);
    });*/
    //查看个人明细方式二
    $("#BatmanDetailTable").on("click",".showDetail",function(){
        $(this).attr("href","javascript:void(0)");
        var userid=$(this).attr("authorid");
        var name=$(this).text();
        //var partName=encodeURI(encodeURI($("#dept_list").find(".selected").text()));
        var userName=encodeURI(encodeURI(name));

        if(!userid){
            alert("请先选择人员！");
            return;
        }
        var siteID=getQueryString("siteID");
        var _href="../stat/Individual.html?t=stat1&siteID="+siteID+"&userid="+userid+"&username="+userName+"&isbatman=batman";
        $(this).attr("href",_href);
    });
});

//当用户提交选择的栏目,实现栏目选择树的接口
function columnClose(filterChecked, allFilterChecked) {
	$("#colName" + batman_stat.colDialogWebApp).val(allFilterChecked[1]);
	$("#colID" + batman_stat.colDialogWebApp).val(allFilterChecked[0]);
	columnCancel();
	batman_stat.search('colSelect', '_col', batman_stat.colDialogWebApp)
}
//点取消
function columnCancel() {
	batman_stat.colDialog.close();
}
function getQueryString(name){
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r != null)return unescape(r[2]);
    return null;
}