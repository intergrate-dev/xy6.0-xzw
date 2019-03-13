var articleBoard_stat = {
	colDialog : null,
	colDialogWebApp : null,
	init : function() {
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

        $("#colName_web,#colName_App").click(articleBoard_stat.colSelect);
		$('#monthSelect_web').change(function(){
			var now = new Date();
			var month = $('#monthSelect_web').val();
			var year = now.getFullYear();
			/*var index = $('#monthSelect_web').get(0).selectedIndex;
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
			var endDate = nextYear + "-" + nextMonth +"-01";

			$('#time_from_web').val(beginDate);
			$('#time_to_web').val(endDate);
			$('#time_tag_web').val('day');
            $("#webTab").find(".time_stat1").removeClass("select_time");
            //触发查询
            articleBoard_stat.search('channelWeb','_web');
		});
		$('#monthSelect_App').change(function(){
			var now = new Date();
			var month = $('#monthSelect_App').val();
			var year = now.getFullYear();
			/*var index = $('#monthSelect_App').get(0).selectedIndex;
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
			var endDate = nextYear + "-" + nextMonth +"-01";

			$('#time_from_App').val(beginDate);
			$('#time_to_App').val(endDate);
			$('#time_tag_App').val('day');
            $("#appTab").find(".time_stat1").removeClass("select_time");
            //触发查询
            articleBoard_stat.search('channelApp','_App');
		});
		
		$('#colSelect_App').click(articleBoard_stat.colSelect);
		//$('#Select,#Select_App').click(articleBoard_stat.colSelect);
		//$('#srcSelect,#srcSelect_App').click(articleBoard_stat.colSelect);
		$('li.channelTab').click(articleBoard_stat.channelTab);

		// 自动提示控件
		articleBoard_stat.autoCompleter.init();
		articleBoard_stat.autoCompleterApp.init();

		//时间选择
		$('.time_stat1').click(function(){
			$(this).addClass("select_time").siblings().removeClass("select_time");
		});
	},
    resetVal:function(commet){
        $("#colName"+commet).val("");
        $("#colID"+commet).val("");
        $("#thisDay"+commet).removeClass("select_time");
        $("#thisWeek"+commet).removeClass("select_time");
        $("#thisMonth"+commet).removeClass("select_time");
        $("#monthSelect"+commet).val("-1");
        if(commet=="_web"){
            $("#webClick").find("tbody").html("");
            $("#webDiss").find("tbody").html("");
        }else if(commet=="_App"){
            $("#appClick").find("tbody").html("");
            $("#appDiss").find("tbody").html("");
        }
    },
	// 切换tab
	channelTab: function(evt){
		var id = $(evt.target)[0].id;
		if(id.indexOf('channelWeb') > -1){
			$('#channelWeb').addClass("select");
			$('#channelApp').removeClass("select");
			$('#webTab').css('display', 'inline');
			$('#appTab').css('display', 'none');	
		}else if(id.indexOf('channelApp') > -1){
			$('#channelApp').addClass("select");
			$('#channelWeb').removeClass("select");
			$('#appTab').css('display', 'inline');
			$('#webTab').css('display', 'none');	
		}
	},
	// 点击查看 eg thisMonth, channelWeb, _web
	search: function(channel, commet){
        var timetag=$('#time_tag' + commet).val();
        var beginTime=$('#time_from' + commet).val();
        var endTime=$('#time_to' + commet).val();

        var now = new Date();
        var ym = now.getFullYear() + "-" + articleBoard_stat.add_zero(now.getMonth() + 1) + "-";
        beginTime= beginTime=='' ? ym + "01" + " 00:00:00" : beginTime;
        endTime= endTime=='' ? ym + articleBoard_stat.add_zero(now.getDate()) + " 23:59:59" : endTime;
        timetag=timetag==''?"day":timetag;


		var url = "../../xy/statistics/Statistics.do";
        var columnID=$('#colID' + commet).val();
        var siteID=getQueryString("siteID");
		var params = {
			channelCode : channel,
			siteID : siteID,
			dataParam : {
				statisticsType : 'ArticleRanking',
                columnID:columnID,
				timeTag : timetag,
				beginTime : beginTime,
				endTime : endTime,
				orderClass : 'top'
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
				articleBoard_stat.showResult(data, channel);
			}
		});
	},
	showResult : function(data, channel) {
		var statisticsClick = data.statisticsClick;
		var statisticsDiscuss = data.statisticsDiscuss;
		if(channel == 'channelWeb'){
			$('#webClick tbody').empty();
			$('#webDiss tbody').empty();
			$.each(statisticsClick, function(i, bean){
				var strRow = '<tr><td width="55%" class="clickId"><span style="padding:0px 5px 0px 7px; margin-right:7px;float:left;">';
				strRow += i+1;
				strRow += '</span>';
				strRow += '<span style="height:20px;float: left;width: 230px; overflow:hidden;text-overflow: ellipsis; white-space: nowrap;">' + bean.articleName + '</span>';
				strRow += '</td>';
				strRow += '<td width="15%" class="countClick">'+ bean.countClick +'</td>';
				strRow += '<td width="15%" class="clickName"><span style="height:20px; overflow:hidden;display:block;" title="'+ bean.columnNameAll +'">'+ bean.columnName +'</span></td>';
				strRow += '<td width="15%" class="clickAuthor">'+ bean.authorName +'</td>';
				$('#webClick tbody').append(strRow);
			});
			$.each(statisticsDiscuss, function(i, bean){
				var strRow = '<tr><td width="55%" class="commentId"><span style="padding:0px 5px 0px 7px; margin-right:7px; float:left;">';
				strRow += i+1;
				strRow += '</span>';
				strRow += '<span style="height:20px;float: left;width: 230px; overflow:hidden;text-overflow: ellipsis; white-space: nowrap;">' + bean.articleName + '</span>';
				strRow += '</td>';
				strRow += '<td width="15%" class="countDiscuss">'+ bean.countDiscuss +'</td>';
				strRow += '<td width="15%" class="commentName"><span style="height:20px; display:block; overflow:hidden" title="'+ bean.columnNameAll +'">'+ bean.columnName +'</span></td>';
				strRow += '<td width="15%" class="commentAuthor">'+ bean.authorName +'</td>';
				$('#webDiss tbody').append(strRow);
			});
		} else if(channel == 'channelApp') {
			$('#appClick tbody').empty();
			$('#appDiss tbody').empty();
			$.each(statisticsClick, function(i, bean){
				var strRow = '<tr><td width="55%" class="clickId"><span style="padding:0px 5px 0px 7px; margin-right:7px; float:left;">';
				strRow += i+1;
				strRow += '</span>';
				strRow += '<span style="height:20px;float: left;width: 230px; overflow:hidden;text-overflow: ellipsis; white-space: nowrap;">' + bean.articleName + '</span>';
				strRow += '</td>';
				strRow += '<td width="15%" class="countClick">'+ bean.countClick +'</td>';
				strRow += '<td width="15%" class="clickName"><span style="height:20px; display:block; overflow:hidden">'+ bean.columnName +'</span></td>';
				strRow += '<td width="15%" class="clickAuthor">'+ bean.authorName +'</td>';
				$('#appClick tbody').append(strRow);
			});
			$.each(statisticsDiscuss, function(i, bean){
				var strRow = '<tr><td  width="55%" class="commentId"><span style="padding:0px 5px 0px 7px; margin-right:7px; float:left;">';
				strRow += i+1;
				strRow += '</span>';
				strRow += '<span style="height:20px;float: left;width: 230px; overflow:hidden;text-overflow: ellipsis; white-space: nowrap;">' + bean.articleName + '</span>';
				strRow += '</td>';
				strRow += '<td width="15%" class="countDiscuss">'+ bean.countDiscuss +'</td>';
				strRow += '<td width="15%" class="commentName"><span style="height:20px; display:block; overflow:hidden">'+ bean.columnName +'</span></td>';
				strRow += '<td width="15%" class="commentAuthor">'+ bean.authorName +'</td>';
				$('#appDiss tbody').append(strRow);
			});
		}

	},
	outputcsv: function(channel, commet) {
        var timetag=$('#time_tag' + commet).val();
        var beginTime=$('#time_from' + commet).val();
        var endTime=$('#time_to' + commet).val();

        var now = new Date();
        var ym = now.getFullYear() + "-" + articleBoard_stat.add_zero(now.getMonth() + 1) + "-";
        beginTime= beginTime=='' ? ym + "01" + " 00:00:00" : beginTime;
        endTime= endTime=='' ? ym + articleBoard_stat.add_zero(now.getDate()) + " 23:59:59" : endTime;
        timetag=timetag==''?"day":timetag;
        var siteID=getQueryString("siteID");
		var params = {
			fileName: '稿件排行.csv',
			channelCode: channel,
			siteID: siteID,
			dataParam: {
				particularParam: timetag,
				exportType: 'ArticleRanking',
				timeTag : $('#time_tag' + commet).val(),
				beginTime : beginTime,
				endTime : endTime,
				orderClass : 'top'
			}
		}

		$('#jsonData').val(JSON.stringify(params));
		$("#form").attr("action", "../../xy/statistics/ExportCSVnoHead.do");
		$("#form").submit();
	},
	// 点击上月本月要同时改变日历的值
	setTime : function(type, commet,isinit) {
		var now = new Date();
		if(type == 'thisWeek'){
			var startDay = now.getDate() - now.getDay();
			var endDay = now.getDate() + (6 - now.getDay())
			var ym = now.getFullYear() + "-" + articleBoard_stat.add_zero(now.getMonth() + 1) + "-";
			$('#time_from' + commet).val(ym + articleBoard_stat.add_zero(startDay));
			$('#time_to' + commet).val(ym + articleBoard_stat.add_zero(endDay));
			$('#time_tag' + commet).val('day');

		} else if(type == 'thisMonth'){
			var ym = now.getFullYear() + "-" + articleBoard_stat.add_zero(now.getMonth() + 1) + "-";
			$('#time_from' + commet).val(ym + "01");
			$('#time_to' + commet).val(ym + articleBoard_stat.add_zero(now.getDate()));
			$('#time_tag' + commet).val('day');

		} else if(type == 'thisDay'){
			$('#time_tag' + commet).val('hour');

		} else {
			alert('Error!');
		}
        $("#monthSelect"+ commet).val("-1");
        //触发查询
        if(!isinit){
            if($("#channels").children(".select").attr("id")=="channelWeb"){
                articleBoard_stat.search('channelWeb','_web');
            }else if($("#channels").children(".select").attr("id")=="channelApp"){
                articleBoard_stat.search('channelApp','_App');
            }
        }
	},
	add_zero:function(param){
		if(param < 10) return "0" + param;
		return "" + param;
	},
    //打开主栏目选择对话框
    colSelect: function(evt) {
        var ch = 1;
        articleBoard_stat.colDialogWebApp = '_App';
        var id = $('li.select').attr('id');
        if (id && id.indexOf('Web') > -1) {
            ch = 0;
            articleBoard_stat.colDialogWebApp = '_web';
        }
        var siteID=getQueryString("siteID");
        var dataUrl = "../../xy/column/ColumnCheck.jsp?siteID=" + siteID + "&ids=" + $("#colID" + articleBoard_stat.colDialogWebApp).val() + "&ch=" + ch+"&style=checkbox&type=op";
        var pos = {left : "350px",top : "50px",width : "1000px",height : "500px"};
        articleBoard_stat.colDialog = e5.dialog({
            type: "iframe",
            value: dataUrl
        }, {
            showTitle: false,
            width: "450px",
            height: "430px",
            pos: pos,
            resizable: false
        });
        articleBoard_stat.colDialog.show();
    },
	//获取对话框显示的位置
	_getDialogPos : function(el) {
		function Pos (x, y) {
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
		
		if (dWidth + 10 > sWidth) dWidth = sWidth - 10;//用e5.dialog时会额外加宽和高
		if (dHeight + 30 > sHeight) dHeight = sHeight - 30;
		
		//顶点位置
		var pos = {left : p.x +"px", 
			top : (p.y + el.offsetHeight - 1)+"px",
			width : dWidth,
			height : dHeight
			};
		if (pos.left + dWidth > sWidth)
			pos.left = sWidth - dWidth;
		if (pos.top + dHeight > sHeight)
			pos.top = sHeight - dHeight;
		
		return pos;
	}
};
//---------来源名称查找框-------------
articleBoard_stat.autoCompleter = {
	url : null,
	init : function(evt) {
		articleBoard_stat.autoCompleter.url = "../../xy/stat/Find.do?siteID=" + $('#siteID').val();
		
		var s = $("#srcName");
		s.autocomplete(articleBoard_stat.autoCompleter.url, articleBoard_stat.autoCompleter.options).result(articleBoard_stat.autoCompleter.getSelectedID);
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
		$("#srcID").val(data.key);
	}
};
articleBoard_stat.autoCompleterApp = {
	url : null,
	init : function(evt) {
		articleBoard_stat.autoCompleterApp.url = "../../xy/stat/Find.do?siteID=" + $('#siteID').val();
		
		var s = $("#srcName_App");
		s.autocomplete(articleBoard_stat.autoCompleterApp.url, articleBoard_stat.autoCompleterApp.options).result(articleBoard_stat.autoCompleterApp.getSelectedID);
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
}
$(function(){
    //默认显示24小时内的数据
    $("#thisMonth_web").addClass("select_time");
    articleBoard_stat.setTime('thisMonth', '_web',true);
    articleBoard_stat.search('channelWeb','_web');

    $("#thisMonth_App").addClass("select_time");
    articleBoard_stat.setTime('thisMonth', '_App',true);
    articleBoard_stat.search('channelApp','_App');

	articleBoard_stat.init();
});

//当用户提交选择的栏目,实现栏目选择树的接口
function columnClose(filterChecked, allFilterChecked) {
    $("#colName" + articleBoard_stat.colDialogWebApp).val(allFilterChecked[1]);
	$("#colID" + articleBoard_stat.colDialogWebApp).val(allFilterChecked[0]);
    //触发查询
    if($("#channels").children(".select").attr("id")=="channelWeb"){
        articleBoard_stat.search('channelWeb','_web');
    }else if($("#channels").children(".select").attr("id")=="channelApp"){
        articleBoard_stat.search('channelApp','_App');
    }
	columnCancel();
}
//点取消
function columnCancel() {
    articleBoard_stat.colDialog.close();
}

function getQueryString(name){
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r != null)return unescape(r[2]);
    return null;
}