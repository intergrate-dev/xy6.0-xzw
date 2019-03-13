<style>
    input{
        font-size: 12px;
    }
    #title,#sys_id{
        font-size: 12px;
        color:#666;
        width:110px;
        margin-top: 10px;
        height: 21px;
        padding: 0;
        border-radius:0px;
        padding-left: 10px;
    }
    #statusSelect{
        font-family:'微软雅黑';
        font-size: 12px;
        color:#666;
        width:80px;
        border-radius:2px;
        margin-top: 10px;
        padding: 0;
        height: 23px;
    }
    .btngroup{
        float: right;
        margin-right: 8px;
    }
    .active {
       padding-left:0px;
        text-decoration: none;

    }
    #startDay, #endDay{
        font-size: 12px;
        color: #666;
        width: 125px;
        margin-top: 10px;
        height: 21px;
        padding: 0;
        border-radius: 0px;
        padding-left: 10px;
        cursor: pointer;
    }
</style>
<script>
    var search_art = {
        init : function() {
            $('#search').click(search_art.Search);
            $('#reset').click(search_art.Reset);
            $("#title").keydown(function(event){
                if(event.keyCode == 13){
                    search_art.Search();
                }
            });
            $('#statusSelect').change(function(){
//                search_art.Reset();
                search_art.Search();
            });

            search_art.initDateTimePickerWeb();
        },

        initDateTimePickerWeb : function(){
            var _startDate = search_art.getDateStr(1);
            var _endDate = search_art.getDateStr(0);;
            $("#startDay").datetimepicker({
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
//                language : 'zh-CN', //语言
//                format: 'yyyy-mm-dd hh:ii:00', //日期格式
//                startDate:new Date(), //开始时间
//                autoclose:true, //选择完后关闭选择器
//                startView:2, //开始视图
//                weekStart : 0,
//                todayBtn:1, //链接当前日期
//                todayHighlight:true,//高亮当前时间
//                pickerPosition: "bottom-left",
//                disabledDaysOfCurrentMonth : 0,
//                forceParse : 0,
//                minView: 0,
//                daysOfWeekDisabled: [],
//                initialDate: _startDate,
//                startDate: _startDate,
//                endDate: _endDate,
            });
            $("#endDay").datetimepicker({
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

//                language : 'zh-CN', //语言
//                format: 'yyyy-mm-dd hh:ii:00', //日期格式
//                startDate:new Date(), //开始时间
//                autoclose:true, //选择完后关闭选择器
//                startView:2, //开始视图
//                weekStart : 0,
//                todayBtn:1, //链接当前日期
//                todayHighlight:true,//高亮当前时间
//                pickerPosition: "bottom-left",
//                disabledDaysOfCurrentMonth : 1,
//                forceParse : 0,
//                minView: 0,
//                daysOfWeekDisabled: [],
//                initialDate: _endDate,
//                startDate: _startDate,
//                endDate: _endDate,
            });
        },
        getDateStr : function(AddMonthCount) {
            var dd = new Date();
            var y = dd.getFullYear();
            var m = dd.getMonth()+1-AddMonthCount;//获取当前月份的日期
            var d = dd.getDate();
            return y+"-"+search_art.add_zero(m)+"-"+search_art.add_zero(d);
        },
        add_zero:function(param){
            if(param < 10){
                return "0" + param;
            }
            return param;
        },

        Search : function() {
            var statusSelect = $("#statusSelect").val();
            var startDay = $("#startDay").val();
            var endDay = $("#endDay").val();
            var title = $("#title").val();
            var sys_id = $("#sys_id").val();
            e5.mods["workspace.search"].searchClick(statusSelect,startDay,endDay,title,sys_id);
        },
        Reset : function(){
            $("#title").val("");
            $("#statusSelect").val("");
            $("#startDay").val("");
            $("#endDay").val("");
            $("#sys_id").val("");
        }
    };
    $(function() {
        search_art.init();
    });
    e5.mod("workspace.search",function() {
        var api;
        var searchClick = function(statusSelect, startDay, endDay, title, sys_id) {
            var searchparam = new SearchParam();
            for (var name in main_param)
                searchparam[name] = main_param[name];

            var rule = "";
            rule += "_AND_sys_documentID!_EQ_" + docID;
            if (statusSelect) rule += "_AND_a_status_EQ_" + statusSelect;
            if (sys_id) rule += " AND sys_documentID like '%" + sys_id +"%'";

            searchparam.ruleFormula = rule.substring(5);

            //标题 发布时间
            if (title != "" || startDay != "" || endDay != ""){
                searchparam.query = "@QUERYCODE@=qTopic";
                if(title != ""){
                    searchparam.query += "&" + "SYS_TOPIC" + "=" + e5.utils.encodeSpecialCode(title);
                }
                if(startDay != ""){
                    searchparam.query += "&" + "SYS_CREATED_0" + "=" + startDay;
                }
                if(endDay != ""){
                    searchparam.query += "&" + "SYS_CREATED_0" + "=" + endDay;
                }
            }
            api.broadcast("searchTopic", searchparam);
        };
        var checkLoad = function() {
            var statusReady = e5.mods["workspace.doclistMain"].isReady;
            var ready = !!statusReady&&statusReady();
            if (!ready) {
                setTimeout(checkLoad, 100);
                return;
            }

            var searchparam = new SearchParam();
            for (var name in main_param)
                searchparam[name] = main_param[name];

            api.broadcast("searchTopic", searchparam);
        }

        var init = function(sandbox) {
            api = sandbox;
        }
        var onload = function(){
            checkLoad();
        }
        return {
            init: init,
            onload: onload,
            searchClick : searchClick
        }
    },{requires:[
        "../e5workspace/script/Param.js",
        "../e5script/e5.utils.js",
        "../xy/script/bootstrap-datetimepicker/css/datetimepicker.css"
    ]});
</script>
<%@ page pageEncoding="UTF-8"%>
<div id="searchBtnArea">
    <div class="div">
        状态：<select id="statusSelect" >
            <option value='0'>启用</option>
            <option value='1'>禁用</option>
        </select>
        发布时间：<input type="text" id="startDay" name="startDay" size="16"  value="" readonly>
        - <input type="text" id="endDay" name="endDay" size="16"  value="" readonly>
        <input id="title" type="text" size="5" title="请输入标题、关键字等" placeholder="关键字查询"/>
        <input id="sys_id" type="text" size="5" title="请输入话题ID" placeholder="话题ID查询"/>

        <input type='button' id="search" value='查找'/>
        <input type='button' id="reset" value='清空'/>
        <span class="btngroup">
			<input class="btns dosure" type='button' id="doSave" value='确定'/>
			<input class="btns docancle" type='button' id="doCancel" value='取消'/>
		</span>
    </div>
</div>
