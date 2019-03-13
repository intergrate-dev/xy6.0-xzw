<style>
    ul,li{
        list-style: none;
        padding: 0;
        margin: 0;
    }
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
    #searchBtnArea #selectedTopic{
        /*min-height: 50px;*/
        height: auto!important;
        line-height: 35px;
    }
    .div li{
        display: inline-block;
        height: 21px;
        border-radius:0px;
        padding-right: 10px;
        margin-right: 10px;
        color: #666;
        border-bottom:1px solid #ddd
    }
    .div ul{
        display: inline;
        height: 21px;
        border-radius:0px;
        color: #666;
    }
    .div ul li {
        position: relative;
    }
    .div ul span {
        color: #00a0e6;
        position: absolute;
        right:0;
        top:-5px;
        font-size: 7px;
        z-index: 100;
        border: 1px solid #ddd;
        /*height: 7px;*/
        line-height:7px;
        border-radius: 100%;
        cursor: pointer;
    }
    .div .remove-icon{
        cursor: pointer;
    }
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
</style>
<%@ page pageEncoding="UTF-8"%>
<div id="searchBtnArea">
    <div class="div">
        <!--状态：<select id="statusSelect" >
            <option value='0'>启用</option>
            <option value='1'>禁用</option>
        </select>
        发布时间：<input type="text" id="startDay" name="startDay" size="16"  value="" readonly>
        - <input type="text" id="endDay" name="endDay" size="16"  value="" readonly>-->
        <input id="title" type="text" size="5" title="请输入话题标题，支持模糊查询" placeholder="请输入话题"/>
        <input id="sys_id" type="text" size="5" title="请输入话题ID，支持模糊查询" placeholder="请输入话题ID"/>

        <input type='button' id="search" value='查找'/>
        <input type='button' id="reset" value='清空'/>
        <span class="btngroup">
			<input class="btns dosure" type='button' id="doSave" value='确定'/>
			<input class="btns docancle" type='button' id="doCancel" value='取消'/>
		</span>

    </div>
    <div id="selectedTopic" class="div">
        <span>已选择：</span>
        <ul id="topics">
<!--            <li data='ida'>"+话题话题话题话题话题话题话题话题+"<span>x</span></li>-->
<!--            <li data='ida'>"+话题话题话题话题话题话题话题话题+"<span>x</span></li>-->
<!--            <li data='ida'>"+话题话题话题话题话题话题话题话题+"<span>x</span></li>-->
<!--            <li data='ida'>"+话题话题话题话题话题话题话题话题+"<span>x</span></li>-->
<!--            <li data='ida'>"+话题话题话题话题话题话题话题话题+"<span>x</span></li>-->
<!--            <li data='ida'>"+话题话题话题话题话题话题话题话题+"<span>x</span></li>-->
<!--            <li data='ida'>"+话题话题话题话题话题话题话题话题+"<span>x</span></li>-->
<!--            <li data='ida'>"+话题话题话题话题话题话题话题话题+"<span>x</span></li>-->
<!--            <li data='ida'>"+话题话题话题话题话题话题话题话题+"<span>x</span></li>-->
<!--            <li>话题1</li>-->
<!--            <li>话题2</li>-->
        </ul>

    </div>
</div>
<script>
    // $('#topics').on('click', 'span', function(){
    //     console.log(6666);
    // })
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

            //search_art.initDateTimePickerWeb();
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
            //var statusSelect = $("#statusSelect").val();
            //var startDay = $("#startDay").val();
            //var endDay = $("#endDay").val();
            //statusSelect为0表示启用
            var statusSelect = "0";
            var startDay = "";
            var endDay = "";
            var title = $("#title").val();
            var sys_id = $("#sys_id").val();
            var topic_group_id = $("#siteList .selectTopic").attr("id");
            // console.log(topic_group_id)
            e5.mods["workspace.search"].searchClick(statusSelect,startDay,endDay,title,sys_id,topic_group_id);
        },

        Reset : function(){
            $("#title").val("");
            //$("#statusSelect").val("");
            //$("#startDay").val("");
            //$("#endDay").val("");
            $("#sys_id").val("");
            $('#search').click();
        }
    };
    $(function() {
        search_art.init();
        <!--添加删除话题的点击事件 start -->
        $('#topics').on('click', 'span', function(){
            var id = $(this).parent().attr("data");
            //删除列表中已选择的check样式和selected样式
            $("#"+id + " input").prop("checked", false);
            $("#"+id).toggleClass("selected");
            //删除this（span）的父标签
            $(this).parent().remove();
        })
        <!--添加已选择的删除的点击事件 end -->
    });
    e5.mod("workspace.search",function() {
        var api;
        var searchClick = function(statusSelect, startDay, endDay, title, sys_id, topic_group_id) {
            var searchparam = new SearchParam();
            for (var name in main_param)
                searchparam[name] = main_param[name];

            var rule = "";
            if (statusSelect) rule += "_AND_a_status_EQ_" + statusSelect;
            rule += "_AND_a_siteID_EQ_1";
            //if (sys_id) rule += "_AND_sys_documentID_EQ_" + sys_id;
            if (sys_id) rule += "_AND_sys_documentID like '%" + sys_id+"%'";
            //拼接话题组查询条件
            if(topic_group_id) rule += "_AND_a_1groupID_EQ_" + topic_group_id;
            // console.log(rule.substring(5))
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
            // console.log(searchparam.query);
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
        "../e5script/e5.utils.js"
    ]});
</script>

