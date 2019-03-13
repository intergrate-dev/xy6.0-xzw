<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
    <script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
    <script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.min.js" charset="UTF-8"></script>
    <script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
    <script type="text/javascript" src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script>
    <script type="text/javascript" src="./script/columns.js"></script>
    <script src="./script/bootstrap-paginator.js"></script>
    <link rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
    <link href="../script/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet" media="screen">
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
    <link rel="stylesheet" type="text/css" href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>
    <link type="text/css" rel="stylesheet" href="./css/statistics.css"/>
    <style>
        #channels li:hover{
            border-bottom: 2px solid #ddd;
        }
        #channels{
            border-bottom: 1px solid #ddd;
        }
        a:focus,a:visited,a:active,a:hover{
            text-decoration: none;
        }


        .time1 #thisWeek,.time1 #thisWeek_app{
            width: 100px;
            display: inline-block;
            text-align: center;
        }
    </style>
</head>
<body>
<div class="division">
    <div>
        <ul class="channels" id="channels">
            <li style="width:140px;" class="select channelTab" id="columnTop">栏目TOP排行</li>
            <li style="width:140px;" class="channelTab" id="columnLast">栏目末位排行</li>
            <li style="width:140px;" class="channelTab" id="columnDetail">栏目明细</li>
        </ul>
    </div>
    <!-- 栏目TOP排行 -->
    <div id="columnTopRegion">
        <div class="left">
            <ul id="ul1" class="channels">
                <li style="width:70px; margin-left: -2px;" class="ditch publishTime">发布渠道:</li>
                <li name="channel" class="channelTab select1" id="columnTopChannelWeb">Web版</li>
                <li name="channel" class="channelTab" id="columnTopChannelApp">App版</li>
            </ul>
        </div>
        <div id="columnTopWeb">
            <div class="container">
                <div class="row">
                    <div class="left" style="margin:15px 0px 2px 80px">
                        <span class="publishTime">时间:</span>&nbsp;
                        <a id="thisDay" class="time_stat1"  href="javascript:columns_stat.setTime('thisDay','', '_top_web')">24小时内</a>
                        <a id="thisWeek" class="time_stat1"  href="javascript:columns_stat.setTime('thisWeek','', '_top_web')">本周</a>
                        <a id="thisMonth" class="time_stat1"  href="javascript:columns_stat.setTime('thisMonth','','_top_web')">本月</a>
                        &nbsp;&nbsp;&nbsp;
                        <input id="time_from_top_web" type="hidden" value="" name="time_from_top_web" />
                        <input id="time_to_top_web" type="hidden" value="" name="time_to_top_web" />
                        <input id="time_tag_top_web" type="hidden" value="" name="time_tag_top_web" />
                    </div>
                   <div style="float: left;">
                        <div class="custform-controls">
                            <select id="monthSelect_top_web">
                                <option value="-1" selected></option>
                                <option value="01">1月</option>
                                <option value="02">2月</option>
                                <option value="03">3月</option>
                                <option value="04">4月</option>
                                <option value="05">5月</option>
                                <option value="06">6月</option>
                                <option value="07">7月</option>
                                <option value="08">8月</option>
                                <option value="09">9月</option>
                                <option value="10">10月</option>
                                <option value="11">11月</option>
                                <option value="12">12月</option>
                            </select>
                        </div>
                   </div>
                   <div class="search">
                        <input class="lookBtn" type="submit" onclick="columns_stat.search('ColumnRanking','channelWeb','_top_web','top');" value="查看" />
                   </div>
                    <div class="exportData1">
                        <input class="see exportBtn" type="button" value="导出数据" onclick="columns_stat.outputcsv('ColumnRanking', 'channelWeb', '', 'Top')" />
                    </div>
                </div>
                <div class="columnRank">
                    <div class="top_click_web">
                        <p>栏目点击量TOP20</p>
                        <div id="top_click_web">
                        </div>
                    </div>
                    <div class="top_art_web">
                        <p>栏目稿件量TOP20</p>
                        <div id="top_art_web">
                        </div>
                    </div>
                    <div class="top_art_click_web">
                        <p>栏目稿件点击量TOP20</p>
                        <div id="top_art_click_web">
                        </div> 
                    </div>
                    <div class="top_art_dis_web">
                        <p>栏目稿件评论量TOP20</p>
                        <div id="top_art_dis_web">
                        </div>
                    </div>
                </div>
                
            </div>
        </div>
        <div id="columnTopApp">
            <div class="container">
                <div class="row">
                    <div class="left" style="margin:15px 0px 2px 80px">
                        <span class="publishTime">时间:</span>&nbsp;
                        <a id="thisDay_app" class="time_stat1"  href="javascript:columns_stat.setTime('thisDay','_app', '_top_app')">24小时内</a>
                        <a id="thisWeek_app" class="time_stat1"  href="javascript:columns_stat.setTime('thisWeek','_app', '_top_app')">本周</a>
                        <a id="thisMonth_app" class="time_stat1"  href="javascript:columns_stat.setTime('thisMonth','_app','_top_app')">本月</a>
                        &nbsp;&nbsp;&nbsp;
                        <input id="time_from_top_app" type="hidden" value="" name="time_from_top_app" />
                        <input id="time_to_top_app" type="hidden" value="" name="time_to_top_app" />
                        <input id="time_tag_top_app" type="hidden" value="" name="time_tag_top_app" />
                    </div>
                    <div style="float: left;">
                        <div class="custform-controls">
                            <select id="monthSelect_top_app">
                                <option value="-1" selected></option>
                                <option value="01">1月</option>
                                <option value="02">2月</option>
                                <option value="03">3月</option>
                                <option value="04">4月</option>
                                <option value="05">5月</option>
                                <option value="06">6月</option>
                                <option value="07">7月</option>
                                <option value="08">8月</option>
                                <option value="09">9月</option>
                                <option value="10">10月</option>
                                <option value="11">11月</option>
                                <option value="12">12月</option>
                            </select>
                        </div>
                    </div>
                    <div class="search">
                        <input class="lookBtn" type="submit" onclick="columns_stat.search('ColumnRanking','channelApp','_top_app','top');" value="查看" />
                    </div>
                    <div class="exportData1">
                        <input class="see exportBtn" type="button" value="导出数据" onclick="columns_stat.outputcsv('ColumnRanking', 'channelApp', '', 'Top')" />
                    </div>
                </div>
                <div class="columnRank">
                    <div class="top_click_app">
                        <p>栏目点击量TOP20</p>
                        <div id="top_click_app">
                        </div>
                    </div>
                    <div class="top_sub_app">
                        <p>栏目订阅量TOP20</p>
                        <div id="top_sub_app">
                        </div>
                    </div>
                    <div class="top_art_app">
                        <p>栏目稿件量TOP20</p>
                        <div id="top_art_app">
                        </div>
                    </div>
                    <div class="top_art_click_app">
                        <p>栏目稿件点击量TOP20</p>
                        <div id="top_art_click_app">
                        </div>
                    </div>
                    <div class="top_art_dis_app">
                        <p>栏目稿件评论量TOP20</p>
                        <div id="top_art_dis_app">
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>

    <!-- 栏目末位排行 -->
    <div id="columnLastRegion" style="display:none;">
        <div class="left">
            <ul id="ul2" class="channels">
                <li style="width:70px; margin-left: -2px;" class="ditch publishTime">发布渠道:</li>
                <li name="channel" class="channelTab select1" id="columnLastChannelWeb">Web版</li>
                <li name="channel" class="channelTab" id="columnLastChannelApp">App版</li>
            </ul>
        </div>
        <div id="columnLastWeb">
            <div class="container">
                <div class="row">
                    <div class="left" style="margin:15px 0px 2px 80px">
                        <span class="publishTime">时间:</span>&nbsp;
                        <a id="thisDay_last" class="time_stat1"  href="javascript:columns_stat.setTime('thisDay','_last', '_last_web')">24小时内</a>
                        <a id="thisWeek_last" class="time_stat1"  href="javascript:columns_stat.setTime('thisWeek','_last', '_last_web')">本周</a>
                        <a id="thisMonth_last" class="time_stat1"  href="javascript:columns_stat.setTime('thisMonth','_last','_last_web')">本月</a>
                        &nbsp;&nbsp;&nbsp;
                        <input id="time_from_last_web" type="hidden" value="" name="time_from_last_web" />
                        <input id="time_to_last_web" type="hidden" value="" name="time_to_last_web" />
                        <input id="time_tag_last_web" type="hidden" value="" name="time_tag_last_web" />
                    </div>
                   <div style="float: left;">
                        <div class="custform-controls">
                            <select id="monthSelect_last_web">
                                <option value="-1" selected></option>
                                <option value="01">1月</option>
                                <option value="02">2月</option>
                                <option value="03">3月</option>
                                <option value="04">4月</option>
                                <option value="05">5月</option>
                                <option value="06">6月</option>
                                <option value="07">7月</option>
                                <option value="08">8月</option>
                                <option value="09">9月</option>
                                <option value="10">10月</option>
                                <option value="11">11月</option>
                                <option value="12">12月</option>
                            </select>
                        </div>
                   </div>
                   <div class="search">
                        <input class="lookBtn" type="submit" onclick="columns_stat.search('ColumnRanking','channelWeb','_last_web','last');" value="查看" />
                   </div>
                    <div class="exportData1">
                        <input class="see exportBtn" type="button" value="导出数据" onclick="columns_stat.outputcsv('ColumnRanking', 'channelWeb', '', 'Last')" />
                    </div>
                </div>
                <div class="columnRank">
                    <div class="last_click_web">
                        <p>栏目点击量末位20</p>
                        <div id="last_click_web">
                        </div>
                    </div>
                    <div class="last_art_web">
                        <p>栏目稿件量末位20</p>
                        <div id="last_art_web">
                        </div>
                    </div>
                    <div class="last_art_click_web">
                        <p>栏目稿件点击量末位20</p>
                        <div id="last_art_click_web">
                        </div>
                    </div>
                    <div class="last_art_dis_web">
                        <p>栏目稿件评论量末位20</p>
                        <div id="last_art_dis_web">
                        </div>
                    </div>
                </div>

            </div>
        </div>
        <div id="columnLastApp">
            <div class="container">
                <div class="row">
                    <div class="left" style="margin:15px 0px 2px 80px">
                        <span class="publishTime">时间:</span>&nbsp;
                        <a id="thisDay_last_app" class="time_stat1"  href="javascript:columns_stat.setTime('thisDay','_last_app', '_last_app')">24小时内</a>
                        <a id="thisWeek_last_app" class="time_stat1"  href="javascript:columns_stat.setTime('thisWeek','_last_app', '_last_app')">本周</a>
                        <a id="thisMonth_last_app" class="time_stat1"  href="javascript:columns_stat.setTime('thisMonth','_last_app','_last_app')">本月</a>
                        &nbsp;&nbsp;&nbsp;
                        <input id="time_from_last_app" type="hidden" value="" name="time_from_last_app" />
                        <input id="time_to_last_app" type="hidden" value="" name="time_to_last_app" />
                        <input id="time_tag_last_app" type="hidden" value="" name="time_tag_last_app" />
                    </div>
                    <div style="float: left;">
                        <div class="custform-controls">
                            <select id="monthSelect_last_app">
                                <option value="-1" selected></option>
                                <option value="01">1月</option>
                                <option value="02">2月</option>
                                <option value="03">3月</option>
                                <option value="04">4月</option>
                                <option value="05">5月</option>
                                <option value="06">6月</option>
                                <option value="07">7月</option>
                                <option value="08">8月</option>
                                <option value="09">9月</option>
                                <option value="10">10月</option>
                                <option value="11">11月</option>
                                <option value="12">12月</option>
                            </select>
                        </div>
                    </div>
                    <div class="search">
                        <input class="lookBtn" type="submit" onclick="columns_stat.search('ColumnRanking','channelApp','_last_app','last');" value="查看" />
                    </div>
                    <div class="exportData1">
                        <input class="see exportBtn" type="button" value="导出数据" onclick="columns_stat.outputcsv('ColumnRanking', 'channelApp', '', 'Last')" />
                    </div>
                </div>
                <div class="columnRank">
                    <div class="last_click_app">
                        <p>栏目点击量末位20</p>
                        <div id="last_click_app">
                        </div>
                    </div>
                    <div class="last_sub_app">
                        <p>栏目订阅量末位20</p>
                        <div id="last_sub_app">
                        </div>
                    </div>
                    <div class="last_art_app">
                        <p>栏目稿件量末位20</p>
                        <div id="last_art_app">
                        </div>
                    </div>
                    <div class="last_art_click_app">
                        <p>栏目稿件点击量末位20</p>
                        <div id="last_art_click_app">
                        </div>
                    </div>
                    <div class="last_art_dis_app">
                        <p>栏目稿件评论量末位20</p>
                        <div id="last_art_dis_app">
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>

    <!-- 栏目明细 -->
    <div id="columnDetailRegion" style="display:none;">
        <div class="left">
            <ul id="ul3" class="channels">
                <li style="width:70px; margin-left: 3px;" class="ditch publishTime">发布渠道:</li>
                <li name="channel" class="channelTab select1_col" id="columnDetailChannelWeb">Web版</li>
                <li name="channel" class="channelTab " id="columnDetailChannelApp">App版</li>
            </ul>
        </div>
        <div id="columnDetailWeb">
            <div class="container">
                <div class="row">
                    <div class="left" style="margin:10px 0px 2px 30px">
                        栏目:
                        <input id="colName_detail" type="text" placeholder="请选择栏目" readonly name="colName_detail_web" />
                        <input id="colID_detail" type="hidden" name="colID_detail_web" />
                        <input id="colSelect_detail" class="see" type="button" title="选择栏目" value="选择" />
                    </div>
                    <div class="times1">
                        <span class="publishTime">时间:</span>&nbsp;
                        <a id="thisDay_col" class="time_stat"  href="javascript:columns_stat.setTime('thisDay', '_col','_detail_web')">24小时内</a>
                        <a id="thisWeek_col" class="time_stat"  href="javascript:columns_stat.setTime('thisWeek','_col', '_detail_web')">本周</a>
                        <a id="thisMonth_col" class="time_stat"  href="javascript:columns_stat.setTime('thisMonth','_col','_detail_web')">本月</a>
                        &nbsp;&nbsp;&nbsp;
                        <input id="time_from_detail_web" type="hidden" value="" name="time_from_detail_web" />
                        <input id="time_to_detail_web" type="hidden" value="" name="time_to_detail_web" />
                        <input id="time_tag_detail_web" type="hidden" value="" name="time_tag_detail_web" />
                    </div>
                    <div class="custform-controls">
                        <select id="monthSelect_detail_web">
                            <option value="-1" selected></option>
                            <option value="01">1月</option>
                            <option value="02">2月</option>
                            <option value="03">3月</option>
                            <option value="04">4月</option>
                            <option value="05">5月</option>
                            <option value="06">6月</option>
                            <option value="07">7月</option>
                            <option value="08">8月</option>
                            <option value="09">9月</option>
                            <option value="10">10月</option>
                            <option value="11">11月</option>
                            <option value="12">12月</option>
                        </select>
                    </div>
                    <div>
                        <input class="lookBtn" style="margin-top:13px;" type="button" value="查看" onclick="columns_stat.search('ColumnDetail', 'channelWeb' ,'_detail_web', '')" />
                        <input class="resetBtn" style="margin-top:13px;" type="button" value="重置" id="reset_col" />
                        <input class="see exportBtn" type="button" value="导出数据" onclick="columns_stat.outputcsv('ColumnDetail', 'channelWeb', '_detail_web', '')" />
                    </div>
                </div>
            
                <div class="row">
                    <div class="detail">
                        <table class="loadTable" id="detailWebTable">
                            <thead>
                                <tr class="tdtr">
                                    <th class="title tdtr1"><input type="checkbox" name="selectAll" id="selectAllDept" title="全选/取消"/></th>
                                    <th class="title tdtr">栏目名称</th>
                                    <th class="title tdtr">栏目点击量</th>
                                    <th class="title1 tdtr">发稿量</th>
                                    <th class="title1 tdtr">稿件点击量</th>
                                    <th class="title1 tdtr">稿件分享</th>
                                    <th class="title tdtr">稿件评论量</th>
                                </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                        <ul class="foot_page" id="paginator_detail_web">

                        </ul>
                        <input type="hidden" id="total_count_detail_web" name="" value="0" />
                    </div>
                </div>

            </div>
        </div>

        <div id="columnDetailApp">
            <div class="container">
                <div class="row">
                    <div class="left" style="margin:10px 0px 2px 30px">
                        栏目:
                        <input id="colName_detail_app" type="text" placeholder="请选择栏目" readonly name="colName_detail_app" />
                        <input id="colID_detail_app" type="hidden" name="colID_detail_app" />
                        <input id="colSelect_detail_app" class="see" type="button" title="选择栏目" value="选择" />
                    </div>
                    <div class="times1">
                        <span class="publishTime">时间:</span>&nbsp;
                        <a id="thisDay_col_app" class="time_stat"  href="javascript:columns_stat.setTime('thisDay','_col_app','_detail_app')">24小时内</a>
                        <a id="thisWeek_col_app" class="time_stat"  href="javascript:columns_stat.setTime('thisWeek','_col_app', '_detail_app')">本周</a>
                        <a id="thisMonth_col_app" class="time_stat"  href="javascript:columns_stat.setTime('thisMonth','_col_app','_detail_app')">本月</a>
                        &nbsp;&nbsp;&nbsp;
                        <input id="time_from_detail_app" type="hidden" value="" name="time_from_detail_app" />
                        <input id="time_to_detail_app" type="hidden" value="" name="time_to_detail_app" />
                        <input id="time_tag_detail_app" type="hidden" value="" name="time_tag_detail_app" />
                    </div>
                    <div class="custform-controls">
                        <select id="monthSelect_detail_app">
                            <option value="-1" selected></option>
                            <option value="01">1月</option>
                            <option value="02">2月</option>
                            <option value="03">3月</option>
                            <option value="04">4月</option>
                            <option value="05">5月</option>
                            <option value="06">6月</option>
                            <option value="07">7月</option>
                            <option value="08">8月</option>
                            <option value="09">9月</option>
                            <option value="10">10月</option>
                            <option value="11">11月</option>
                            <option value="12">12月</option>
                        </select>
                    </div>
                    <div>
                        <input class="lookBtn" style="margin-top:13px;" type="button" value="查看" onclick="columns_stat.search('ColumnDetail', 'channelApp' ,'_detail_app', '')" />
                        <input class="resetBtn" style="margin-top:13px;" type="button" value="重置" id="reset_col_app" />
                        <input class="see exportBtn" type="button" value="导出数据" onclick="columns_stat.outputcsv('ColumnDetail', 'channelApp', '_detail_app', '')" />
                    </div>
                </div>

                <div class="row">
                    <div class="detail">
                        <table class="loadTable" id="detailAppTable">
                            <thead>
                                <tr class="tdtr">
                                    <th class="title tdtr1"><input type="checkbox" name="selectAll" id="selectAllDept_app" title="全选/取消"/></th>
                                    <th class="title tdtr">栏目ID</th>
                                    <th class="title tdtr">栏目名称</th>
                                    <th class="title tdtr">栏目点击量</th>
                                    <th class="title tdtr">栏目订阅量</th>
                                    <th class="title1 tdtr">发稿量</th>
                                    <th class="title1 tdtr">稿件点击量</th>
                                    <th class="title1 tdtr">稿件分享</th>
                                    <th class="title tdtr">稿件评论量</th>
                                </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                        <ul class="foot_page" id="paginator_detail_app">

                        </ul>
                        <input type="hidden" id="total_count_detail_app" name="" value="0" />
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>
<input type="hidden" id="siteID" value="${siteID}" /><!-- 站点ID -->
<form id="form" method="post"><!-- csv输出 -->
    <input type="hidden" id="jsonData" name="jsonData" />
    <input type="hidden" id="csvName" name="csvName" />
</form>
</body>
</html>