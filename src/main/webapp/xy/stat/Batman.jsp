<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
    <script type="text/javascript" src="../../e5script/jquery/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
    <script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.min.js" charset="UTF-8"></script>
    <script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
    <script type="text/javascript" src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script>
    <script type="text/javascript" src="./script/batman.js"></script>
    <script src="./script/bootstrap-paginator.js"></script>
    <link rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
    <link href="../script/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet" media="screen">
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
    <link rel="stylesheet" type="text/css" href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>
    <link type="text/css" rel="stylesheet" href="./css/statistics.css"/>
    <style>
        a:focus,a:visited,a:active,a:hover{
            text-decoration: none;
        }

        .time1 #thisWeek,.time1 #thisWeek_App{
            width: 100px;
            display: inline-block;
            text-align: center;
        }
    </style>
</head>
<body>
<div class="division">
        <div id="detail">
            <div class="Batman" >
                <div class="left" style="margin:13px 0px 5px 10px;width:17%;">
                    <span class="publishTime">通讯员:</span>
                    <input id="batmanName" type="text" readonly name="batmanName" value="" />
                    <input id="batmanID" type="hidden" name="batmanID" />
                    <input id="batmanSelect" class="see" type="button" title="选择部门" value="选择" />
                </div>
                <div class="detail_Batman">
                    <div id="chTimeRegion" class="times">
                        <span>发布时间:&nbsp;</span>
                        <a id="current24H" class="time_stat"  href="javascript:batman_stat.setCalender('current24H')">24小时内</a>
                        <a id="current7D" class="time_stat"  href="javascript:batman_stat.setCalender('current7D')">最近7天</a>
                        <a id="current14D" class="time_stat"  href="javascript:batman_stat.setCalender('current14D')">最近14天</a>
                        <a id="current30D" class="time_stat"  href="javascript:batman_stat.setCalender('current30D')">最近30天</a>
                        <a id="lastMonth" class="time_stat" style="width:40px;" href="javascript:batman_stat.setCalender('lastMonth')">上月</a>
                        <a id="thisMonth" class="time_stat" style="width:40px;" href="javascript:batman_stat.setCalender('thisMonth')">本月</a>
                        <input id="pubTime_from" readonly type="text" value="" name="pubTime_from" date-data-format="yyyy-mm-dd hh:ii:ss" /> -
                        <input id="pubTime_to" readonly type="text" value="" name="pubTime_to" date-data-format="yyyy-mm-dd hh:ii:ss"/>
                        <input class="lookBtn" style="margin:0px 5px;" type="submit" onclick="batman_stat.search();" value="查看" />
                        <input class="resetBtn" type="button" id="reset_batman" value="重置" />
                    </div>
                </div>
            </div>
            <div class="exportData">
                <input class="see" style='width:110px;height:30px;margin-right:5px;' type="hidden" value="查看个人明细" onclick="batman_stat.searchDetail()" />
                <input class="see exportBtn" type="button" onclick="batman_stat.outputcsv()" value="导出数据" />
            </div>
            <div class="detail">
                <table class="loadTable" id="BatmanDetailTable">
                    <thead>
                        <tr class="tdtr">
                            <th class="title tdtr1"><input type="checkbox" name="selectAll" id="selectAll" title="全选/取消"/></th>
                            <th class="title tdtr">通讯员名称</th>
                            <th class="title tdtr">发稿量</th>
                            <th class="title tdtr">总点击量</th>
                            <th class="title1 tdtr">Web点击数</th>
                            <th class="title1 tdtr">触屏点击数</th>
                            <th class="title1 tdtr">APP点击数</th>
                            <th class="title tdtr">总分享数</th>
                            <th class="title1 tdtr">Web分享</th>
                            <th class="title1 tdtr">触屏分享</th>
                            <th class="title1 tdtr">APP分享</th>
                            <th class="title tdtr">总评论量</th>
                            <th class="title1 tdtr">Web评论量</th>
                            <th class="title1 tdtr">触屏评论量</th>
                            <th class="title1 tdtr">APP评论量</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                <ul class="foot_page" id="paginator">

                </ul>
                <input type="hidden" id="total_count" name="" value="0" />
            </div>
        </div>
</div>

    <!-- 模态框（Modal） -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                &times;
                </button>
                <h4 class="modal-title" id="myModalLabel">
                通讯人员
                </h4>
            </div>
            <div class="modal-body clearfix">
                <div class="fl">
                    <div class="department-search" id="BatMan">
                        <input type="text" placeholder="输入人员信息查询">
                        <ul>
                            <li>aa</li>
                            <li>bb</li>
                            <li>cc</li>
                        </ul>
                    </div>
                    <button type="button" class="btn btn-default search-btn">
                        <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
                    </button>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="confirm">确定</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>


<input type="hidden" id="siteID" value="${siteID}" /><!-- 站点ID -->
<form id="form" method="post"><!-- csv输出 -->
    <input type="hidden" id="jsonData" name="jsonData" />
</form>
</body>
</html>