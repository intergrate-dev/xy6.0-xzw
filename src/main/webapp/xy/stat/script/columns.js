var columns_stat = {
	colDialog: null,
	colDialogWebApp: null,
	init: function() {
        //select标签内容限定
        var _nowMonth = new Date().getMonth()+1;
        var _option='<option value="-1" selected>';
        for(var i=2;i>-1;i--){
            var _num=_nowMonth-i;
            if(_num<=0){
                _num=_num+12;
            }
            if(_num<10){
                var _val="0"+_num;
            }else{
                var _val=_num;
            }
            _option +='<option value="'+_val+'">'+_num+'月</option>';
        }

        $("select").html(_option);
        //select标签内容限定结束

        $('#colName_detail_web,#colName_detail_app').click(columns_stat.colSelect);
		$('#monthSelect_top_web,#monthSelect_top_app').change(function() {
            var tag="_web";
            if($(this).attr("id").indexOf("app")!=-1){
                tag="_app";
            }
			var now = new Date();
			var month = $('#monthSelect_top'+tag).val();
			var year = now.getFullYear();
			/*var index = $('#monthSelect_top'+tag).get(0).selectedIndex;
			var nextMonth = index + 1;*/
            var nextMonth = parseInt(month) + 1;

			var nextYear = year;
			if (nextMonth > 12) {
				nextMonth = 1;
				nextYear = nextYear + 1;
			}

            if(nextMonth<10){
                nextMonth="0"+nextMonth;
            }

			var beginDate = year + "-" + month + "-01";
			var endDate = nextYear + "-" + nextMonth + "-01";
			$('#time_from_top'+tag).val(beginDate);
			$('#time_to_top'+tag).val(endDate);
			$('#time_tag_top'+tag).val('day');
            if(tag=="_web"){
                $("#columnTopWeb").find(".time_stat1").removeClass("select_time");
            }else if(tag=="_app"){
                $("#columnTopApp").find(".time_stat1").removeClass("select_time");
            }

            if(tag=="_web"){
                columns_stat.search('ColumnRanking','channelWeb','_top_web','top');
            }else if(tag=="_app"){
                columns_stat.search('ColumnRanking','channelApp','_top_app','top');
            }
		});

        $('#monthSelect_last_web,#monthSelect_last_app').change(function() {
            var tag="_web";
            if($(this).attr("id").indexOf("app")!=-1){
                tag="_app";
            }
            var now = new Date();
            var month = $('#monthSelect_last'+tag).val();
            var year = now.getFullYear();
            /*var index = $('#monthSelect_last'+tag).get(0).selectedIndex;
            var nextMonth = index + 1;*/

            var nextMonth = parseInt(month) + 1;
            var nextYear = year;
            if(nextMonth > 12){
                nextMonth = 1;
                nextYear = nextYear + 1;
            }
            if(nextMonth<10){
                nextMonth="0"+nextMonth;
            }

            var beginDate = year + "-" + month + "-01";
            var endDate = nextYear + "-" + nextMonth + "-01";
            $('#time_from_last'+tag).val(beginDate);
            $('#time_to_last'+tag).val(endDate);
            $('#time_tag_last'+tag).val('day');
            if(tag=="_web"){
                $("#columnLastWeb").find(".time_stat1").removeClass("select_time");
            }else if(tag=="_app"){
                $("#columnLastApp").find(".time_stat1").removeClass("select_time");
            }
            if(tag=="_web"){
                columns_stat.search('ColumnRanking','channelWeb','_last_web','last');
            }else if(tag=="_app"){
                columns_stat.search('ColumnRanking','channelApp','_last_app','last');
            }
        });
		$('#monthSelect_detail_web,#monthSelect_detail_app').change(function() {
            var tag="_web";
            if($(this).attr("id").indexOf("app")!=-1){
                tag="_app";
            }
			var now = new Date();
			var month = $('#monthSelect_detail'+tag).val();
			var year = now.getFullYear();
			/*var index = $('#monthSelect_detail'+tag).get(0).selectedIndex;
			var nextMonth = index + 1;*/

            var nextMonth = parseInt(month) + 1;
            var nextYear = year;
            if(nextMonth > 12){
                nextMonth = 1;
                nextYear = nextYear + 1;
            }
            if(nextMonth<10){
                nextMonth="0"+nextMonth;
            }
			var beginDate = year + "-" + month + "-01";
			var endDate = nextYear + "-" + nextMonth + "-01";
			$('#time_from_detail'+tag).val(beginDate);
			$('#time_to_detail'+tag).val(endDate);
			$('#time_tag_detail'+tag).val('day');
            if(tag=="_web"){
                $("#columnDetailWeb").find(".time_stat").removeClass("select_time");
            }else if(tag=="_app"){
                $("#columnDetailApp").find(".time_stat").removeClass("select_time");
            }
            if(tag=="_web"){
                columns_stat.search('ColumnDetail', 'channelWeb' ,'_detail_web', '');
            }else if(tag=="_app"){
                columns_stat.search('ColumnDetail', 'channelApp' ,'_detail_app', '');
            }
		});

		$('#colSelect,#colSelect_app').click(columns_stat.colSelect);
		$('#Select,#Select_app').click(columns_stat.colSelect);
		$('#srcSelect,#srcSelect_app').click(columns_stat.colSelect);
		$('li.channelTab').click(columns_stat.channelTab);
		// 自动提示控件
		columns_stat.autoCompleter.init();
		columns_stat.autoCompleterApp.init();

		if ($("#ul1 .channelTab").length <= 1) $("#ul1").hide();
		if ($("#ul2 .channelTab").length <= 1) $("#ul2").hide();
		if ($("#ul3 .channelTab").length <= 1) $("#ul3").hide();
		//初始化选中tab第一项
        $('#columnTopChannelWeb').addClass("select1");
        $('#columnTopChannelApp').removeClass("select1");
        $('#columnTopWeb').css('display', 'inline');
        $('#columnTopApp').css('display', 'none');

        $('#columnLastChannelWeb').addClass("select1");
        $('#columnLastChannelApp').removeClass("select1");
        $('#columnLastWeb').css('display', 'inline');
        $('#columnLastApp').css('display', 'none');

        $('#columnDetailChannelWeb').addClass("select1_col");
        $('#columnDetailChannelApp').removeClass("select1_col");
        $('#columnDetailWeb').css('display', 'inline');
        $('#columnDetailApp').css('display', 'none');
        columns_stat.search('ColumnRanking','channelWeb','_top_web','top');
		/*$("#ul1 .channelTab").first().click();
		$("#ul2 .channelTab").first().click();
		$("#ul3 .channelTab").first().click();*/

		//时间选择
		$('.time_stat1,.time_stat').click(function(){
			$(this).addClass("select_time").siblings().removeClass("select_time");
		});

		//重置
        $("#reset_top").click(columns_stat.resetWorkload_top);
        $("#reset_top_app").click(columns_stat.resetWorkload_top_app);
        $("#reset_last").click(columns_stat.resetWorkload_last);
        $("#reset_last_app").click(columns_stat.resetWorkload_last_app);
		$("#reset_col").click(columns_stat.resetWorkload_col);
		$("#reset_col_app").click(columns_stat.resetWorkload_col_app);
	},
    resetWorkload_top:function(){
        $(this).parent().siblings().children('a').removeClass("select_time");
        $("#monthSelect_top_web").val("-1");
        $("#top_click_web").html("");
        $("#top_art_web").html("");
        $("#top_art_click_web").html("");
        $("#top_art_dis_web").html("");
    },
    resetWorkload_top_app:function(){
        $(this).parent().siblings().children('a').removeClass("select_time");
        $("#monthSelect_top_app").val("-1");
        $("#top_click_app").html("");
        $("#top_sub_app").html("");
        $("#top_art_app").html("");
        $("#top_art_click_app").html("");
        $("#top_art_dis_app").html("");
    },
    resetWorkload_last:function(){
        $(this).parent().siblings().children('a').removeClass("select_time");
        $("#monthSelect_last_web").val("-1");
        $("#last_click_web").html("");
        $("#last_art_web").html("");
        $("#last_art_click_web").html("");
        $("#last_art_dis_web").html("");
    },
    resetWorkload_last_app:function(){
        $(this).parent().siblings().children('a').removeClass("select_time");
        $("#monthSelect_last_app").val("-1");
        $("#last_click_app").html("");
        $("#last_sub_app").html("");
        $("#last_art_app").html("");
        $("#last_art_click_app").html("");
        $("#last_art_dis_app").html("");
    },
	resetWorkload_col : function(){
		$("#colName_detail_web").val("");
        $("#colID_detail_web").val("");
		$(this).parent().siblings().children('a').removeClass("select_time");
		$("#monthSelect_detail_web").val("-1");
        $("#time_from_detail_web").val("");
        $("#time_to_detail_web").val("");
        $("#time_tag_detail_web").val("");
		$("#detailWebTable tbody").empty();
	},
	resetWorkload_col_app : function(){
		$("#colName_detail_app").val("");
        $("#colID_detail_app").val("");
		$(this).parent().siblings().children('a').removeClass("select_time");
		$("#monthSelect_detail_app").val("-1");
        $("#time_from_detail_app").val("");
        $("#time_to_detail_app").val("");
        $("#time_tag_detail_app").val("");
		$("#detailAppTable tbody").empty();
	},

	// 切换tab
	channelTab: function(evt) {
		var id = $(evt.target)[0].id;
		/*	if(id.indexOf('ChannelWeb') > -1){
				$('#' + id).addClass("select1");
				$('#' + id.replace('Web', 'App')).removeClass("select1");
				$('#' + id.replace('ChannelWeb', 'Web')).css('display', 'inline');
				$('#' + id.replace('ChannelWeb', 'App')).css('display', 'none');
			}else if(id.indexOf('ChannelApp') > -1){
				$('#' + id).addClass("select1");
				$('#' + id.replace('App', 'Web')).removeClass("select1");
				$('#' + id.replace('ChannelApp', 'App')).css('display', 'inline');
				$('#' + id.replace('ChannelApp', 'Web')).css('display', 'none');
			}   */
		if (id == 'columnTopChannelWeb') {
			$('#columnTopChannelWeb').addClass("select1");
			$('#columnTopChannelApp').removeClass("select1");
			$('#columnTopWeb').css('display', 'inline');
			$('#columnTopApp').css('display', 'none');
            columns_stat.search('ColumnRanking','channelWeb','_top_web','top');
		} else if (id == 'columnTopChannelApp') {
			$('#columnTopChannelApp').addClass("select1");
			$('#columnTopChannelWeb').removeClass("select1");
			$('#columnTopApp').css('display', 'inline');
			$('#columnTopWeb').css('display', 'none');
            columns_stat.search('ColumnRanking','channelApp','_top_app','top');
		} else if (id == 'columnLastChannelWeb') {
			$('#columnLastChannelWeb').addClass("select1");
			$('#columnLastChannelApp').removeClass("select1");
			$('#columnLastWeb').css('display', 'inline');
			$('#columnLastApp').css('display', 'none');
            columns_stat.search('ColumnRanking','channelWeb','_last_web','last');
		} else if (id == 'columnLastChannelApp') {
			$('#columnLastChannelApp').addClass("select1");
			$('#columnLastChannelWeb').removeClass("select1");
			$('#columnLastApp').css('display', 'inline');
			$('#columnLastWeb').css('display', 'none');
            columns_stat.search('ColumnRanking','channelApp','_last_app','last');
		} else if (id == 'columnDetailChannelWeb') {
			$('#columnDetailChannelWeb').addClass("select1_col");
			$('#columnDetailChannelApp').removeClass("select1_col");
			$('#columnDetailWeb').css('display', 'inline');
			$('#columnDetailApp').css('display', 'none');
            columns_stat.search('ColumnDetail', 'channelWeb' ,'_detail_web', '');
		} else if (id == 'columnDetailChannelApp') {
			$('#columnDetailChannelApp').addClass("select1_col");
			$('#columnDetailChannelWeb').removeClass("select1_col");
			$('#columnDetailApp').css('display', 'inline');
			$('#columnDetailWeb').css('display', 'none');
            columns_stat.search('ColumnDetail', 'channelApp' ,'_detail_app', '');
		} else if (id == 'columnTop') {
			$('#columnTop').addClass("select");
			$('#columnLast').removeClass("select");
			$('#columnDetail').removeClass("select");
			$('#columnTopRegion').css("display", "inline");
			$('#columnLastRegion').css("display", "none");
			$('#columnDetailRegion').css("display", "none");
			if($('#columnTopChannelWeb').hasClass("select1")){
                columns_stat.search('ColumnRanking','channelWeb','_top_web','top');
			}else{
                columns_stat.search('ColumnRanking','channelApp','_top_app','top');
			}
		} else if (id == 'columnLast') {
			$('#columnLast').addClass("select");
			$('#columnTop').removeClass("select");
			$('#columnDetail').removeClass("select");
			$('#columnLastRegion').css("display", "inline");
			$('#columnTopRegion').css("display", "none");
			$('#columnDetailRegion').css("display", "none");
			if($('#columnLastChannelWeb').hasClass("select1")){
                columns_stat.search('ColumnRanking','channelWeb','_last_web','last');
			}else{
                columns_stat.search('ColumnRanking','channelApp','_last_app','last');
			}
		} else if (id == 'columnDetail') {
			$('#columnDetail').addClass("select");
			$('#columnTop').removeClass("select");
			$('#columnLast').removeClass("select");
			$('#columnDetailRegion').css("display", "inline");
			$('#columnLastRegion').css("display", "none");
			$('#columnTopRegion').css("display", "none");
			if($('#columnDetailChannelWeb').hasClass("select1_col")){
                columns_stat.search('ColumnDetail', 'channelWeb' ,'_detail_web', '');
			}else{
                columns_stat.search('ColumnDetail', 'channelApp' ,'_detail_app', '');
			}
		}
	},
	// 点击查看 eg 'ColumnRanking','channelWeb','_top_web','top'
	search: function(type, channel, commet, order) {
        var now = new Date();
        var ym = now.getFullYear() + "-" + columns_stat.add_zero(now.getMonth() + 1) + "-";
        var timetag=$('#time_tag' + commet).val();
        var beginTime=$('#time_from' + commet).val();
        var endTime=$('#time_to' + commet).val();
        if(!beginTime){
            $('#time_from' + commet).val(ym + "01" + " 00:00:00");
            $('#time_to' + commet).val(ym + columns_stat.add_zero(now.getDate()) + " 23:59:59");
            $('#time_tag' + commet).val("day");
            $('#time_from' + commet).prev().addClass("select_time");
        }
        beginTime= beginTime=='' ? ym + "01" + " 00:00:00" : beginTime;
        endTime= endTime=='' ? ym + columns_stat.add_zero(now.getDate()) + " 23:59:59" : endTime;
        timetag=timetag==''?"day":timetag;

		var url = "../../xy/statistics/Statistics.do";
		var params;
		var pageNo = 1;
        var siteID=getQueryString("siteID");
		if (type == 'ColumnRanking') {
			params = {
				channelCode: channel,
				siteID: siteID,
				dataParam: {
					statisticsType: type,
					timeTag: timetag,
					beginTime: beginTime,
					endTime:endTime,
					orderClass: order
				}
			};
		} else if (type == 'ColumnDetail') {
            var code = e5.utils.getCookie("l$code");
			params = {
				channelCode: channel,
				siteID: siteID,
				dataParam: {
					statisticsType: type,
					columnID: $('#colID' + commet).val(),
                    userCode:code,
					timeTag: timetag,
					beginTime: beginTime,
					endTime: endTime,
					pageNum: pageNo,
					pageSize: 20
				}
			};
		} else {
			alert('Error!');
			return;
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
				if (channel == 'channelWeb') {
					columns_stat.showResult(data, channel, type, order);
					if (type == 'ColumnDetail') {
						var totalCount = data.totalCount;
						var pageCount = data.pageCount;
						var currentPageNum = pageNo;
						columns_stat.showPaginator(totalCount, pageCount, pageNo, params, '_detail_web', 'detailTable_col', 'ColumnDetail',channel,order);
						$('#total_count' + commet).val(totalCount);
                        $('#total_count' + commet+"_all").text(totalCount);
					}

				} else if (channel == 'channelApp') {
					columns_stat.showResult(data, channel, type, order);
					if (type == 'ColumnDetail') {
						var totalCount = data.totalCount;
						var pageCount = data.pageCount;
						var currentPageNum = pageNo;
						columns_stat.showPaginator(totalCount, pageCount, pageNo, params, '_detail_app', 'detailTable_col_app', 'ColumnDetail',channel,order);
						$('#total_count' + commet).val(totalCount);
                        $('#total_count' + commet+"_all").text(totalCount);
					}

				} else {
					alert('Error During Searching!');
				}
			}
		});
	},
	showPaginator: function(totalCount, pageCount, pageNum, params, commet, tableId, selectType,channel,order) {
        if(totalCount<1) {
            $('#paginator' + commet).hide();
            return;
        };
		var options = {
			bootstrapMajorVersion: 3, //使用的bootstrap版本为3，
			alignment: "center", //居中显示
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
					data: JSON.stringify(params),
					error: function(XMLHttpRequest, textStatus, errorThrown) {
						alert(errorThrown + ':' + textStatus); // 错误处理
					},
					success: function(data) {
						if (data != null) {
							columns_stat.showResult(data, channel, selectType, order);
                            if(pageNum>1&&pageNum<=pageCount){
                                pageNum++;
                                columns_stat.showPaginator(totalCount, pageCount, pageNum, params, commet, tableId, selectType,channel,order);
                            }
						}
					}
				});
			}
		};
        $('#paginator' + commet).show();
		$('#paginator' + commet).bootstrapPaginator(options);
	},

	showResult: function(data, channel, type, order) {
		if (type == 'ColumnRanking') {
			var commet = '';
			if (channel == 'channelWeb') {
				commet = '_web';
			} else if (channel == "channelApp") {
				commet = '_app';
				$('#' + order + '_sub_app').html('');
				var columnSubscribeRank = data.columnSubscribeRank; //app中的栏目订阅量
				//app中的栏目订阅量
				$.each(columnSubscribeRank, function(i, bean) {
					var str = '<ul><span class="lmClickId" id="lmClickId">' + (i + 1) + '</span>';
					str += '<span class="lmClickName" id="lmClickName">' + bean.columnName + '</span>';
					str += '<span class="lmClickCount" id="lmClickCount">' + bean.countSub + '</span></ul>';
					$('#' + order + '_sub_app').append(str);
				});
			} else {
				return;
			}
			$('#' + order + '_click' + commet).html('');
			$('#' + order + '_art' + commet).html('');
			$('#' + order + '_art_click' + commet).html('');
			$('#' + order + '_art_dis' + commet).html('');
			var columnClickRank = data.columnClickRank;
			var columnArticleClickRank = data.columnArticleClickRank;
			var columnArticleDiscussRank = data.columnArticleDiscussRank;
			var columnArticleRank = data.columnArticleRank;
			$.each(columnClickRank, function(i, bean) {
				var str = '<ul title="'+ bean.columnName +'"><span class="lmClickId" id="lmClickId">' + (i + 1) + '</span>';
				str += '<span class="lmClickName" id="lmClickName" title="'+ bean.columnNameAll +'">' + bean.columnName + '</span>';
				str += '<span class="lmClickCount" id="lmClickCount">' + bean.countClick + '</span></ul>';
				$('#' + order + '_click' + commet).append(str);
			});
			$.each(columnArticleClickRank, function(i, bean) {
				var str = '<ul title="'+ bean.columnName +'"><span class="lmClickId" id="lmClickId">' + (i + 1) + '</span>';
				str += '<span class="lmClickName" id="lmClickName" title="'+ bean.columnNameAll +'">' + bean.columnName + '</span>';
				str += '<span class="lmClickCount" id="lmClickCount">' + bean.countArticleClick + '</span></ul>';
				$('#' + order + '_art_click' + commet).append(str);
			});
			$.each(columnArticleDiscussRank, function(i, bean) {
				var str = '<ul title="'+ bean.columnName +'"><span class="lmClickId" id="lmClickId">' + (i + 1) + '</span>';
				str += '<span class="lmClickName" id="lmClickName" title="'+ bean.columnNameAll +'">' + bean.columnName + '</span>';
				str += '<span class="lmClickCount" id="lmClickCount">' + bean.countDiscuss + '</span></ul>';
				$('#' + order + '_art_dis' + commet).append(str);
			});
			$.each(columnArticleRank, function(i, bean) {
				var str = '<ul title="'+ bean.columnName +'"><span class="lmClickId" id="lmClickId">' + (i + 1) + '</span>';
				str += '<span class="lmClickName" id="lmClickName" title="'+ bean.columnNameAll +'">' + bean.columnName + '</span>';
				str += '<span class="lmClickCount" id="lmClickCount">' + bean.countArticle + '</span></ul>';
				$('#' + order + '_art' + commet).append(str);
			});
		} else if (type == 'ColumnDetail') {
			var detailData = data.statisticsData;
			var tableId = 'detail' + channel.slice(-3) + 'Table';
			$('#' + tableId + ' tbody').empty();
			$.each(detailData, function(i, bean) {
				str = '<tr class="tdtr">';
				/*if (channel == 'channelApp') {
					str += '<input type="checkbox" name="checkbox" id="checkbox_app"/></td>';
				} else if (channel == 'channelWeb') {
					str += '<input type="checkbox" name="checkbox" id="checkbox_web"/></td>';
				} else {
					str += '<input type="checkbox" name="checkbox" id=""/></td>';
				}*/
				
				if (typeof(bean.columnID) == "undefined") {
					str += '<td class="tdtr4"></td>';
				} else {
					str += '<td class="tdtr4">' + bean.columnID + '</td>';
				}
				if (typeof(bean.columnName) == "undefined") {
					str += '<td class="tdtr"></td>';
				} else {
					str += '<td class="tdtr" title="'+bean.cascadeName+'">' + bean.columnName + '</td>';
				}
				if (typeof(bean.columnClick) == "undefined") {
					str += '<td class="tdtr">' + 0 + '</td>';
				} else {
					str += '<td class="tdtr">' + bean.columnClick + '</td>';
				}
				if (channel == 'channelApp') {
					if (typeof(bean.columnSub) == "undefined") {
						str += '<td class="tdtr">' + 0 + '</td>';
					} else {
						str += '<td class="tdtr">' + bean.columnSub + '</td>';
					}
				}
				if (typeof(bean.articleNum) == "undefined") {
					str += '<td class="tdtr">' + 0 + '</td>';
				} else {
					str += '<td class="tdtr">' + bean.articleNum + '</td>';
				}
				if (typeof(bean.articleClick) == "undefined") {
					str += '<td class="tdtr">' + 0 + '</td>';
				} else {
					str += '<td class="tdtr">' + bean.articleClick + '</td>';
				}
				if (typeof(bean.articleShare) == "undefined") {
					str += '<td class="tdtr">' + 0 + '</td>';
				} else {
					str += '<td class="tdtr">' + bean.articleShare + '</td>';
				}
				if (typeof(bean.articleDiscuss) == "undefined") {
					str += '<td class="tdtr">' + 0 + '</td>';
				} else {
					str += '<td class="tdtr">' + bean.articleDiscuss + '</td>';
				}
				$('#' + tableId + ' tbody').append(str);

			});
		} else {
			alert('Error!');
			return;
		}

	},
	outputcsv: function(type, channel, commet, order) {
		debugger;
		if(type == 'ColumnDetail'){
            var code = e5.utils.getCookie("l$code");
			var th = $('#detail' + channel.slice(-3) + 'Table tr th');
			var thdatas = [];
			var length = th.length;
			for (var a = 0; a < length; a++) {
				thdatas.push($(th[a]).text());
			}

            var timetag=$('#time_tag' + commet).val();
            var beginTime=$('#time_from' + commet).val();
            var endTime=$('#time_to' + commet).val();

            var now = new Date();
            var ym = now.getFullYear() + "-" + columns_stat.add_zero(now.getMonth() + 1) + "-";
            beginTime= beginTime=='' ? ym + "01" + " 00:00:00" : beginTime;
            endTime= endTime=='' ? ym + columns_stat.add_zero(now.getDate()) + " 23:59:59" : endTime;
            timetag=timetag==''?"day":timetag;
            var siteID=getQueryString("siteID");
			var params = {
				fileName: '栏目明细.csv',
				headParam: thdatas,
				channelCode: channel,
				siteID: siteID,
				dataParam: {
					columnID: $('#colID' + commet).val(),
                    userCode:code,
					exportType: 'ColumnDetail',
					timeTag: timetag,
					beginTime: beginTime,
					endTime: endTime,
					pageNum: 1,
					pageSize: $('#total_count' + commet).val()
				}
			};
			$('#jsonData').val(JSON.stringify(params));
			$("#form").attr("action", "../../xy/statistics/ExportCSV.do");
			$("#form").submit();
		} else if(type == 'ColumnRanking') {
			var divs = $('#column'+order+channel.slice(-3)+' .container .columnRank div');
			var length = divs.length;
			//console.log("divsLength:"+length);
			var dataList = [];
			for(var i=0;i<length;i++){
				if(i%2 == 0){ //根据内外嵌套结构，此DIV为外框
					var p = $(divs[i]).children("p").text();
					//console.log("p:"+p);
					var dataRow = {
						th1: p,
						th2: "数量"
					}
					dataList.push(dataRow);
				} else { //根据内外嵌套结构，此DIV为内框
					var uls = $(divs[i]).children("ul");
					var ulLength = uls.length;
					for(var j=0;j<uls.length;j++){
						var spans = $(uls[j]).children("span");
						var rowSpan = {};
						for(var k=1;k<spans.length;k++){
							rowSpan["sp"+k] = $(spans[k]).text();
						}
						dataList.push(rowSpan);
					}
				}
				
			}
			//console.log(dataList);
			var params = {
				fileName: '栏目明细.csv',
				exportType: 'ColumnRanking',
				exportData: dataList
			}
			$('#jsonData').val(JSON.stringify(params));
			$("#form").attr("action", "../../xy/statistics/ExportCSVWithStaticData.do");
			$("#form").submit();
		} else {
			return;
		}
	},

	setTime: function(type, data, commet,isinit) {
		var now = new Date();
		if (type == 'thisWeek') {
			var startDay = now.getDate() - now.getDay();
			var endDay = now.getDate() + (6 - now.getDay());
			var ym = now.getFullYear() + "-" + columns_stat.add_zero(now.getMonth() + 1) + "-";
			$('#time_from' + commet).val(ym + columns_stat.add_zero(startDay));
			$('#time_to' + commet).val(ym + columns_stat.add_zero(endDay));
			$('#time_tag' + commet).val('day');
		} else if (type == 'thisMonth') {
			var ym = now.getFullYear() + "-" + columns_stat.add_zero(now.getMonth() + 1) + "-";
			$('#time_from' + commet).val(ym + "01");
			$('#time_to' + commet).val(ym + columns_stat.add_zero(now.getDate()));
			$('#time_tag' + commet).val('day');

		} else if (type == 'thisDay') {
			var fromTime = new Date(now.getTime() - 24 * 3600 * 1000);
			var fromYear = fromTime.getFullYear();
			var fromMonth = fromTime.getMonth() + 1;
			if (fromMonth == 0) {
				fromYear -= 1;
				fromMonth = 12;
			}
			fromMonth = columns_stat.add_zero(fromMonth);
			var fromDay = columns_stat.add_zero(fromTime.getDate());
			var fromHour = columns_stat.add_zero(fromTime.getHours());
			var fromMinutes = columns_stat.add_zero(fromTime.getMinutes());
			var fromSeconds = columns_stat.add_zero(fromTime.getSeconds());
			var newFromTime = fromYear + '-' + fromMonth + '-' + fromDay + ' ' + fromHour + ':' + fromMinutes + ':' + fromSeconds;
			$('#time_from' + commet).val(newFromTime);
			var nowTime = new Date(now.getTime());
			var nowYear = nowTime.getFullYear();
			var nowMonth = columns_stat.add_zero(nowTime.getMonth() + 1);
			var nowDay = columns_stat.add_zero(nowTime.getDate());
			var nowHour = columns_stat.add_zero(nowTime.getHours());
			var nowMinutes = columns_stat.add_zero(nowTime.getMinutes());
			var nowSeconds = columns_stat.add_zero(nowTime.getSeconds());
			var newNowTime = nowYear + '-' + nowMonth + '-' + nowDay + ' ' + nowHour + ':' + nowMinutes + ':' + nowSeconds;
			$('#time_to' + commet).val(newNowTime);
			$('#time_tag' + commet).val('hour');
		} else {
			alert('Error!');
			return;
		}
        $("#monthSelect"+ commet).val("-1");
        if(!isinit){
        //触发查询
        if($("#channels").children(".select").attr("id")=="columnTop"){
            if($("#ul1").children(".select1").attr("id")=="columnTopChannelWeb"){
                columns_stat.search('ColumnRanking','channelWeb','_top_web','top');
            }else if($("#ul1").children(".select1").attr("id")=="columnTopChannelApp"){
                columns_stat.search('ColumnRanking','channelApp','_top_app','top');
            }
        }else if($("#channels").children(".select").attr("id")=="columnLast"){
            if($("#ul2").children(".select1").attr("id")=="columnLastChannelWeb"){
                columns_stat.search('ColumnRanking','channelWeb','_last_web','last');
            }else if($("#ul2").children(".select1").attr("id")=="columnLastChannelApp"){
                columns_stat.search('ColumnRanking','channelApp','_last_app','last');
            }
        }else if($("#channels").children(".select").attr("id")=="columnDetail"){
            if($("#ul3").children(".select1_col").attr("id")=="columnDetailChannelWeb"){
                columns_stat.search('ColumnDetail', 'channelWeb' ,'_detail_web', '');
            }else if($("#ul3").children(".select1_col").attr("id")=="columnDetailChannelApp"){
                columns_stat.search('ColumnDetail', 'channelApp' ,'_detail_app', '');
            }
        }
        }

	},

	add_zero: function(param) {
		if (param < 10) return "0" + param;
		return "" + param;
	},

	//打开主栏目选择对话框
	colSelect: function(evt) {
		var ch = 1;
		columns_stat.colDialogWebApp = '_app';
		var id = $('li.select1_col').attr('id');
		if (id && id.indexOf('Web') > -1) {
			ch = 0;
			columns_stat.colDialogWebApp = '_web';
		}
        var siteID=getQueryString("siteID");
		var dataUrl = "../../xy/column/ColumnCheck.jsp?siteID=" + siteID + "&ids=" + $("#colID" + columns_stat.colDialogWebApp).val() + "&ch=" + ch+"&style=checkbox&type=op";
        var pos = {left : "350px",top : "50px",width : "1000px",height : "500px"};
		columns_stat.colDialog = e5.dialog({
			type: "iframe",
			value: dataUrl
		},{
			showTitle: false,
			width: "450px",
			height: "430px",
			pos: pos,
			resizable: false
		});
		columns_stat.colDialog.show();
	},
	//获取对话框显示的位置
	_getDialogPos: function(el) {
		function Pos(x, y) {
			this.x = x;
			this.y = y;
		}

		function getPos(el) {
			var r = new Pos(el.offsetLeft, el.offsetTop);
			if (el.offsetParent) {
				var tmp = getPos(el.offsetParent);
				r.x += tmp.x;
				r.y += tmp.y;
			}
			return r;
		}
		var p = getPos(el);

		//决定弹出窗口的高度和宽度
		var dWidth = 400;
		var dHeight = 300;

		var sWidth = document.body.clientWidth; //窗口的宽和高
		var sHeight = document.body.clientHeight;

		if (dWidth + 10 > sWidth) dWidth = sWidth - 10; //用e5.dialog时会额外加宽和高
		if (dHeight + 30 > sHeight) dHeight = sHeight - 30;

		//顶点位置
		var pos = {
			left: p.x + "px",
			top: (p.y + el.offsetHeight - 1) + "px",
			width: dWidth,
			height: dHeight
		};
		if (pos.left + dWidth > sWidth)
			pos.left = sWidth - dWidth;
		if (pos.top + dHeight > sHeight)
			pos.top = sHeight - dHeight;

		return pos;
	}
};
//---------来源名称查找框-------------
columns_stat.autoCompleter = {
	url: null,
	init: function(evt) {
		columns_stat.autoCompleter.url = "../../xy/stat/Find.do?siteID=" + $('#siteID').val();

		var s = $("#srcName");
		s.autocomplete(columns_stat.autoCompleter.url, columns_stat.autoCompleter.options).result(columns_stat.autoCompleter.getSelectedID);
		s.focus();
	},
	options: {
		minChars: 1,
		delay: 400,
		autoFill: true,
		selectFirst: true,
		matchContains: true,
		cacheLength: 1,
		dataType: 'json',
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
		formatItem: function(row, i, max) {
			return row.value;
		},
		formatMatch: function(row, i, max) {
			return row.value;
		},
		formatResult: function(row, i, max) {
			return row.value;
		}
	},
	getSelectedID: function(event, data, formatted) {
		$("#srcID").val(data.key);
	}
};
columns_stat.autoCompleterApp = {
	url: null,
	init: function(evt) {
		columns_stat.autoCompleterApp.url = "../../xy/stat/Find.do?siteID=" + $('#siteID').val();

		var s = $("#srcName_app");
		s.autocomplete(columns_stat.autoCompleterApp.url, columns_stat.autoCompleterApp.options).result(columns_stat.autoCompleterApp.getSelectedID);
		s.focus();
	},
	options: {
		minChars: 1,
		delay: 400,
		autoFill: true,
		selectFirst: true,
		matchContains: true,
		cacheLength: 1,
		dataType: 'json',
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
		formatItem: function(row, i, max) {
			return row.value;
		},
		formatMatch: function(row, i, max) {
			return row.value;
		},
		formatResult: function(row, i, max) {
			return row.value;
		}
	},
	getSelectedID: function(event, data, formatted) {
		$("#srcID_app").val(data.key);
	}
}
$(function() {
    columns_stat.init();
    //默认显示本月数据 top
    $("#thisMonth").addClass("select_time");
    columns_stat.setTime('thisMonth','','_top_web',true);
    //columns_stat.search('ColumnRanking','channelWeb','_top_web','top');

    $("#thisMonth_app").addClass("select_time");
    columns_stat.setTime('thisMonth','_app','_top_app',true);
    //columns_stat.search('ColumnRanking','channelApp','_top_app','top');

    //默认显示本月数据 last
    $("#thisMonth_last").addClass("select_time");
    columns_stat.setTime('thisMonth','_last','_last_web',true);
    //columns_stat.search('ColumnRanking','channelWeb','_last_web','last');

    $("#thisMonth_last_app").addClass("select_time");
    columns_stat.setTime('thisMonth','_last_app','_last_app',true);
    //columns_stat.search('ColumnRanking','channelApp','_last_app','last');

    //默认显示本月数据 detail
    $("#thisMonth_col").addClass("select_time");
    columns_stat.setTime('thisMonth','_col','_detail_web',true);
    //columns_stat.search('ColumnDetail', 'channelWeb' ,'_detail_web', '');

    $("#thisMonth_col_app").addClass("select_time");
    columns_stat.setTime('thisMonth','_col_app','_detail_app',true);
    //columns_stat.search('ColumnDetail', 'channelApp' ,'_detail_app', '');

});

//当用户提交选择的栏目,实现栏目选择树的接口
function columnClose(filterChecked, allFilterChecked) {
	//$("#colName" + columns_stat.colDialogWebApp).val(allFilterChecked[1]);
    $("#colName_detail" + columns_stat.colDialogWebApp).val(allFilterChecked[1]);
    $("#colID_detail" + columns_stat.colDialogWebApp).val(allFilterChecked[0]);
    //触发查询
    if($("#ul3").children(".select1_col").attr("id")=="columnDetailChannelWeb"){
        columns_stat.search('ColumnDetail', 'channelWeb' ,'_detail_web', '');
    }else if($("#ul3").children(".select1_col").attr("id")=="columnDetailChannelApp"){
        columns_stat.search('ColumnDetail', 'channelApp' ,'_detail_app', '');
    }
	columnCancel();
	//columns_stat.search('colSelect', '_col', columns_stat.colDialogWebApp)
}
//点取消
function columnCancel() {
	columns_stat.colDialog.close();
}

 function getQueryString(name){
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if(r != null)return unescape(r[2]);
	return null;
}