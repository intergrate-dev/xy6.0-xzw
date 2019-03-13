<%@include file="../../e5include/IncludeTag.jsp" %>
<%@page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
    <script type="text/javascript" src="../../e5script/jquery/jquery-1.9.1.min.js"></script>
    <script src="../../e5script/jquery/jquery.dialog.js"></script>
    <script src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
    <script src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.min.js" charset="UTF-8"></script>
    <script src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
    <script src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script>
    <script src="./script/articleOverview.js"></script>
    <script src="./script/bootstrap-paginator.js"></script>
    <link rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
    <link href="../script/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet" media="screen">
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
    <link rel="stylesheet" type="text/css" href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>
    <link type="text/css" rel="stylesheet" href="./css/dataTables.bootstrap.min.css"/>
    <link type="text/css" rel="stylesheet" href="./css/statistics.css"/>
    <style>
        a:focus,a:visited,a:active,a:hover{
            text-decoration: none;
        }
        #selectType li:hover{
            border-bottom: 2px solid #ddd;
        }
        a,label,input,button{ 
            vertical-align:middle;
        }
        ul{
            margin:0;
        } 
    </style>
</head>
<body>
    <div class="division">
        <div id="typeRegion">
            <ul class="channels" id="selectType">
                <li style="width:140px;" class="select channelTab" id="deptStat">按部门统计</li>
                <li style="width:140px;" class="channelTab" id="srcStat">按来源统计</li>
                <li style="width:140px;" class="channelTab" id="colStat">按栏目统计</li>
            </ul>
        </div>

        <!-- 部门统计 -->
        <div id="dept">
            <div id="channelRegion" class="left">
                <ul id="selectChannel" class="channels">
                    <li style="width:70px;margin-left: -2px;" class="ditch publishTime">发布渠道:</li>
                    <li name="channelWeb" class="channelTab select1" id="channelWeb_dept">Web</li>
                    <li name="channelApp" class="channelTab" id="channelApp_dept">App</li>
                </ul>
            </div>
            
            <div id="detailDept">
                <ul class="left" style="margin:10px 0px 2px 30px">
                    <span class="publishTime">部门:</span>
                    <input id="departmentName" type="text" readonly name="departmentName" value=""/>
                    <!-- <input id="departmentID" type="hidden" name="departmentID"/> -->
                    <input id="departmentButton" class="see departmentBtn" type="button" title="选择部门" value="选择" />
                    <input id="particular_dept_web" type="hidden" name="departmentID" />
                </ul>
                <div id="chTimeRegionDept" class="times">
                    <span class="publishTime">发布时间:&nbsp;</span>
                    <a id="current24HDept" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current24H','_dept_web','')">24小时内</a>&nbsp;
                    <a id="current7DDept" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current7D','_dept_web','')">最近7天</a>&nbsp;
                    <a id="current14DDept" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current14D','_dept_web','')">最近14天</a>&nbsp;
                    <a id="current30DDept" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current30D','_dept_web','')">最近30天</a>&nbsp;
                    <a id="lastMonthDept" class="time_stat1" style="width:40px;" href="javascript:articleOverview_stat.setCalender('lastMonth','_dept_web','')">上月</a>&nbsp;&nbsp;
                    <a id="thisMonthDept" class="time_stat1" style="width:40px;" href="javascript:articleOverview_stat.setCalender('thisMonth','_dept_web','')">本月</a>&nbsp;&nbsp;&nbsp;&nbsp;
                    <input id="pubTime_from_dept_web" readonly type="text" value="" name="pubTime_from_dept_web" date-data-format="yyyy-mm-dd hh:ii:ss" /> -
                    <input id="pubTime_to_dept_web" readonly type="text" value="" name="pubTime_to_dept_web" date-data-format="yyyy-mm-dd hh:ii:ss"/>
                    <input class="lookBtn" style="margin:0px 5px;" type="submit" onclick="articleOverview_stat.search('ArticleDepartment','channelWeb','_dept_web');" value="查看" />
                    <input class="resetBtn" type="button" id="reset" value="重置" />
                    <input class="see exportBtn" style='margin-left:5px;' type="button" onclick="articleOverview_stat.outputcsv('ArticleDepartment','channelWeb', 'department', '', '_dept_web')" value="导出数据" />
                    
                </div>
                <!-- </form> -->
                <div class="detail">
                    <table class="loadTable" id="departmentDetailTable">
                        <thead>
                            <tr class="tdtr">
                                <th class="title tdtr1"><input type="checkbox" name="selectAll" id="selectAll_dept" title="全选/取消"/></th>
                                <th class="title tdtr">部门名称</th>
                                <th class="title tdtr">稿件量</th>
                                <th class="title tdtr">总点击量</th>
                                <th class="title1 tdtr">Web点击数</th>
                                <th class="title1 tdtr">触屏点击数</th>
                                <th class="title1 tdtr">APP点击数</th>
                                <th class="title tdtr">总分享数</th>
                                <th class="title1 tdtr">Web分享</th>
                                <th class="title1 tdtr">触屏分享</th>
                                <th class="title1 tdtr">APP分享</th>
                                <th class="title tdtr">总评论量</th>
                                <th class="title1 tdtr">Web点评量</th>
                                <th class="title1 tdtr">触屏点评量</th>
                                <th class="title1 tdtr">APP点评量</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                    <ul class="foot_page" id="paginator_dept_web">
                    
                    </ul>
                    <input type="hidden" id="total_count_dept_web" name="" value="0" />
                </div>
                
            </div>

            <div id="detailDept_App" style="display:none;">
                <ul class="left" style="margin:10px 0px 2px 30px">
                    <span class="publishTime">部门:</span>
                    <input id="departmentName_app" type="text" readonly name="departmentName_App" value="" />
                    <!-- <input id="departmentID_App" type="hidden" name="departmentID_App"/> -->
                    <input id="departmentButton_App" class="see departmentBtn" type="button" title="选择部门" value="选择" />
                    <input id="particular_dept_App" type="hidden" value="" name="departmentID_App"/>
                </ul>
                <div id="chTimeRegionDept_App" class="times">
                    <span style="width:70px;" class="publishTime">发布时间:&nbsp;</span>
                    <a id="current24HDept_App" class="time_stat1" style="width: 70px" href="javascript:articleOverview_stat.setCalender('current24H','_dept_App','_App')">24小时内</a>&nbsp;
                    <a id="current7DDept_App" class="time_stat1" style="width: 70px" href="javascript:articleOverview_stat.setCalender('current7D','_dept_App','_App')">最近7天</a>&nbsp;
                    <a id="current14DDept_App" class="time_stat1" style="width: 70px" href="javascript:articleOverview_stat.setCalender('current14D','_dept_App','_App')">最近14天</a>&nbsp;
                    <a id="current30DDept_App" class="time_stat1" style="width: 70px" href="javascript:articleOverview_stat.setCalender('current30D','_dept_App','_App')">最近30天</a>&nbsp;
                    <a id="lastMonthDept_App" class="time_stat1" style="width: 40px" href="javascript:articleOverview_stat.setCalender('lastMonth','_dept_App','_App')">上月</a>&nbsp;&nbsp;
                    <a id="thisMonthDept_App" class="time_stat1" style="width: 40px" href="javascript:articleOverview_stat.setCalender('thisMonth','_dept_App','_App')">本月</a>&nbsp;&nbsp;&nbsp;&nbsp;
                    <input id="pubTime_from_dept_App" readonly type="text" value="" name="pubTime_from_dept_App" /> -
                    <input id="pubTime_to_dept_App" readonly type="text" value="" name="pubTime_to_dept_App" />
                    <input class="lookBtn" style="margin:0px 5px;" type="submit" onclick="articleOverview_stat.search('ArticleDepartment','channelApp','_dept_App');" value="查看" />
                    <input class="resetBtn" type="button" id="reset_app" value="重置" />
                    <input class="see exportBtn" style='margin-left:5px;' type="button" onclick="articleOverview_stat.outputcsv('ArticleDepartment','channelApp', 'department', '', '_dept_App')" value="导出数据" />
                </div>
                <!-- </form> -->
                <div class="detail">
                    <table class="loadTable" id="departmentDetailTable_App">
                        <thead>
                            <tr class="tdtr">
                                <th class="title tdtr1"><input type="checkbox" name="selectAll" id="selectAll_dept_App" title="全选/取消"/></th>
                                <th class="title tdtr">部门名称</th>
                                <th class="title tdtr">稿件量</th>
                                <th class="title tdtr">总点击量</th>
                                <th class="title1 tdtr">Web点击数</th>
                                <th class="title1 tdtr">触屏点击数</th>
                                <th class="title1 tdtr">APP点击数</th>
                                <th class="title tdtr">总分享数</th>
                                <th class="title1 tdtr">Web分享</th>
                                <th class="title1 tdtr">触屏分享</th>
                                <th class="title1 tdtr">APP分享</th>
                                <th class="title tdtr">总评论量</th>
                                <th class="title1 tdtr">Web点评量</th>
                                <th class="title1 tdtr">触屏点评量</th>
                                <th class="title1 tdtr">APP点评量</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                    <ul class="foot_page" id="paginator_dept_App">

                    </ul>
                    <input type="hidden" id="total_count_dept_App" name="" value="0" />
                </div>

            </div>
        </div>

        <!-- 来源统计 -->
        <div id="src" style="display: none">
            <div id="channelRegion_src" class="left">
                <ul id="selectChannel_src" class="channels">
                    <li style="width:70px;margin-left: -2px;" class="ditch publishTime">发布渠道:</li>
                    <li name="channelWeb" class="channelTab select1" id="channelWeb_src">Web</li>
                    <li name="channelApp" class="channelTab" id="channelApp_src">App</li>
                </ul>
            </div>

            <div id="detailSrc">
                <ul class="left" style="margin:10px 0px 2px 30px">
                    <span class="publishTime">来源:</span>
                    <input id="sourceName" type="text" readonly name="sourceName"/>
                    <!-- <input id="sourceID" type="hidden" name="sourceID"/> -->
                    <input id="sourceButton" class="see" type="button" title="选择部门" value="选择" />
                    <input id="particular_src_web" type="hidden" value="" name="sourceID"/>
                </ul>
                <div id="chTimeRegionSrc" class="times">
                    <span class="publishTime">发布时间:&nbsp;</span>
                    <a id="current24HSrc" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current24H','_src_web','_src')">24小时内</a>&nbsp;
                    <a id="current7DSrc" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current7D','_src_web','_src')">最近7天</a>&nbsp;
                    <a id="current14DSrc" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current14D','_src_web','_src')">最近14天</a>&nbsp;
                    <a id="current30DSrc" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current30D','_src_web','_src')">最近30天</a>&nbsp;
                    <a id="lastMonthSrc" class="time_stat1" style="width:40px;" href="javascript:articleOverview_stat.setCalender('lastMonth','_src_web','_src')">上月</a>&nbsp;&nbsp;
                    <a id="thisMonthSrc" class="time_stat1" style="width:40px;" href="javascript:articleOverview_stat.setCalender('thisMonth','_src_web','_src')">本月</a>&nbsp;&nbsp;&nbsp;&nbsp;
                    <input id="pubTime_from_src_web" readonly type="text" value="" name="pubTime_from_src_web" date-data-format="yyyy-mm-dd hh:ii:ss" /> -
                    <input id="pubTime_to_src_web" readonly type="text" value="" name="pubTime_to_src_web" date-data-format="yyyy-mm-dd hh:ii:ss"/>
                    <input class="lookBtn" style="margin:0px 5px;" type="submit" onclick="articleOverview_stat.search('ArticleSource','channelWeb','_src_web');" value="查看" />
                    <input class="resetBtn" type="button" id="reset_src" value="重置" />
                    <input class="see exportBtn" style='margin-left:5px;' type="button" onclick="articleOverview_stat.outputcsv('ArticleSource','channelWeb', 'source', '', '_src_web')" value="导出数据" />
                </div>

                <!-- </form> -->
                <div class="detail">
                    <table class="loadTable" id="sourceDetailTable">
                        <thead>
                            <tr class="tdtr">
                                <th class="title tdtr1"><input type="checkbox" name="selectAll" id="selectAll_src" title="全选/取消"/></th>
                                <th class="title tdtr">来源名称</th>
                                <th class="title tdtr">稿件量</th>
                                <th class="title tdtr">总点击量</th>
                                <th class="title1 tdtr">Web点击数</th>
                                <th class="title1 tdtr">触屏点击数</th>
                                <th class="title1 tdtr">APP点击数</th>
                                <th class="title tdtr">总分享数</th>
                                <th class="title1 tdtr">Web分享</th>
                                <th class="title1 tdtr">触屏分享</th>
                                <th class="title1 tdtr">APP分享</th>
                                <th class="title tdtr">总评论量</th>
                                <th class="title1 tdtr">Web点评量</th>
                                <th class="title1 tdtr">触屏点评量</th>
                                <th class="title1 tdtr">APP点评量</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                    <ul class="foot_page" id="paginator_src_web">

                    </ul>
                    <input type="hidden" id="total_count_src_web" name="" value="0" />
                </div>

            </div>

            <div id="detailSrc_App" style="display:none;">
                <ul class="left" style="margin:10px 0px 2px 30px">
                    <span class="publishTime">来源:</span>
                    <input id="sourceName_App" type="text" readonly name="sourceName_App"/>
                    <!-- <input id="sourceID_App" type="hidden" name="sourceID_App"/> -->
                    <input id="sourceButton_App" class="see" type="button" title="选择部门" value="选择" />
                    <input id="particular_src_App" type="hidden" name="sourceID_App" value="" />
                </ul>
                <div id="chTimeRegionSrc_App" class="times">
                    <span class="publishTime">发布时间:&nbsp;</span>
                    <a id="current24HSrc_App" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current24H','_src_App','_src_App')">24小时内</a>&nbsp;
                    <a id="current7DSrc_App" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current7D','_src_App','_src_App')">最近7天</a>&nbsp;
                    <a id="current14DSrc_App" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current14D','_src_App','_src_App')">最近14天</a>&nbsp;
                    <a id="current30DSrc_App" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current30D','_src_App','_src_App')">最近30天</a>&nbsp;
                    <a id="lastMonthSrc_App" class="time_stat1" style="width:40px;" href="javascript:articleOverview_stat.setCalender('lastMonth','_src_App','_src_App')">上月</a>&nbsp;&nbsp;
                    <a id="thisMonthSrc_App" class="time_stat1" style="width:40px;" href="javascript:articleOverview_stat.setCalender('thisMonth','_src_App','_src_App')">本月</a>&nbsp;&nbsp;&nbsp;&nbsp;
                    <input id="pubTime_from_src_App" readonly type="text" value="" name="pubTime_from_src_App" date-data-format="yyyy-mm-dd hh:ii:ss" /> -
                    <input id="pubTime_to_src_App" readonly type="text" value="" name="pubTime_to_src_App" date-data-format="yyyy-mm-dd hh:ii:ss"/>
                    <input class="lookBtn" style="margin:0px 5px;" type="submit" onclick="articleOverview_stat.search('ArticleSource','channelApp','_src_App');" value="查看" />
                    <input class="resetBtn" type="button" id="reset_src_app" value="重置" />
                    <input class="see exportBtn" style='margin-left:5px;' type="button" onclick="articleOverview_stat.outputcsv('ArticleSource','channelApp', 'source', '', '_src_App')" value="导出数据" />
                </div>
                <!-- </form> -->
                <div class="detail">
                    <table class="loadTable" id="sourceDetailTable_App">
                        <thead>
                            <tr class="tdtr">
                                <th class="title tdtr1"><input type="checkbox" name="selectAll" id="selectAll_src_App" title="全选/取消"/></th>
                                <th class="title tdtr">来源名称</th>
                                <th class="title tdtr">稿件量</th>
                                <th class="title tdtr">总点击量</th>
                                <th class="title1 tdtr">Web点击数</th>
                                <th class="title1 tdtr">触屏点击数</th>
                                <th class="title1 tdtr">APP点击数</th>
                                <th class="title tdtr">总分享数</th>
                                <th class="title1 tdtr">Web分享</th>
                                <th class="title1 tdtr">触屏分享</th>
                                <th class="title1 tdtr">APP分享</th>
                                <th class="title tdtr">总评论量</th>
                                <th class="title1 tdtr">Web点评量</th>
                                <th class="title1 tdtr">触屏点评量</th>
                                <th class="title1 tdtr">APP点评量</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                    <ul class="foot_page" id="paginator_src_App">

                    </ul>
                    <input type="hidden" id="total_count_src_App" name="" value="0" />
                </div>

            </div>
        </div>


        <!-- 栏目统计 -->
        <div id="column" style="display: none">
            <div id="channelRegion_col" class="left">
                <ul id="selectChannel_col" class="channels">
                    <li style="width:70px;margin-left: -2px;" class="ditch publishTime">发布渠道:</li>
                    <li name="channelWeb" class="channelTab select1_col" id="channelWeb_col">Web</li>
                    <li name="channelApp" class="channelTab" id="channelApp_col">App</li>
                </ul>
            </div>

            <div id="detailCol">
                <ul class="left" style="margin:10px 0px 2px 30px">
                    <span class="publishTime">栏目:</span>
                    <input id="columnName" readonly type="text" name="columnName"/>
                    <!-- <input id="columnID" type="hidden" name="columnID"/> -->
                    <input id="columnButton" class="see" type="button" title="选择部门" value="选择" />
                    <input id="particular_col" type="hidden" name="columnID" value="" />
                </ul>
                <div id="chTimeRegionCol" class="times">
                    <span class="publishTime">发布时间:&nbsp;</span>
                    <a id="current24HCol" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current24H','_col_web','_col')">24小时内</a>&nbsp;
                    <a id="current7DCol" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current7D','_col_web','_col')">最近7天</a>&nbsp;
                    <a id="current14DCol" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current14D','_col_web','_col')">最近14天</a>&nbsp;
                    <a id="current30DCol" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current30D','_col_web','_col')">最近30天</a>&nbsp;
                    <a id="lastMonthCol" class="time_stat1" style="width:40px;" href="javascript:articleOverview_stat.setCalender('lastMonth','_col_web','_col')">上月</a>&nbsp;&nbsp;
                    <a id="thisMonthCol" class="time_stat1" style="width:40px;" href="javascript:articleOverview_stat.setCalender('thisMonth','_col_web','_col')">本月</a>&nbsp;&nbsp;&nbsp;&nbsp;
                    <input id="pubTime_from_col_web" readonly type="text" value="" name="pubTime_from_col_web" date-data-format="yyyy-mm-dd hh:ii:ss" /> -
                    <input id="pubTime_to_col_web" readonly type="text" value="" name="pubTime_to_col_web" date-data-format="yyyy-mm-dd hh:ii:ss"/>
                    <input class="lookBtn" style="margin:0px 5px;" type="submit" onclick="articleOverview_stat.search('ArticleColumn','channelWeb','_col_web');" value="查看" />
                    <input class="resetBtn" type="button" id="reset_col" value="重置" />
                    <input class="see exportBtn" style='margin-left:5px;' type="button" onclick="articleOverview_stat.outputcsv('ArticleColumn','channelWeb', 'column', '', '_col_web')" value="导出数据" />
                </div>
                <!-- </form> -->
                <div class="detail">
                    <table class="loadTable" id="columnDetailTable">
                        <thead>
                            <tr class="tdtr">
                                <th class="title tdtr1"><input type="checkbox" name="selectAll" id="selectAll_col" title="全选/取消"/></th>
                                <th class="title tdtr">栏目名称</th>
                                <th class="title tdtr">稿件量</th>
                                <th class="title tdtr">总点击量</th>
                                <th class="title1 tdtr">Web点击数</th>
                                <th class="title1 tdtr">触屏点击数</th>
                                <th class="title1 tdtr">APP点击数</th>
                                <th class="title tdtr">总分享数</th>
                                <th class="title1 tdtr">Web分享</th>
                                <th class="title1 tdtr">触屏分享</th>
                                <th class="title1 tdtr">APP分享</th>
                                <th class="title tdtr">总评论量</th>
                                <th class="title1 tdtr">Web点评量</th>
                                <th class="title1 tdtr">触屏点评量</th>
                                <th class="title1 tdtr">APP点评量</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                    <ul class="foot_page" id="paginator_col_web">

                    </ul>
                    <input type="hidden" id="total_count_col_web" name="" value="0" />
                </div>

            </div>

            <div id="detailCol_App" style="display:none;">
                <ul class="left" style="margin:10px 0px 2px 30px">
                    <span class="publishTime">栏目:</span>
                    <input id="columnName_app" type="text" readonly name="columnName_app"/>
                    <!-- <input id="columnID_App" type="hidden" name="columnID_App"/> -->
                    <input id="columnButton_app" class="see" type="button" title="选择栏目" value="选择" />
                    <input id="particular_col_app" type="hidden" name="columnID_app" value="" />
                </ul>
                <div id="chTimeRegionCol_App" class="times">
                    <span class="publishTime">发布时间:&nbsp;</span>
                    <a id="current24HCol_App" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current24H','_col_App','_col_App')">24小时内</a>&nbsp;
                    <a id="current7DCol_App" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current7D','_col_App','_col_App')">最近7天</a>&nbsp;
                    <a id="current14DCol_App" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current14D','_col_App','_col_App')">最近14天</a>&nbsp;
                    <a id="current30DCol_App" class="time_stat1"  href="javascript:articleOverview_stat.setCalender('current30D','_col_App','_col_App')">最近30天</a>&nbsp;
                    <a id="lastMonthCol_App" class="time_stat1" style="width:40px;" href="javascript:articleOverview_stat.setCalender('lastMonth','_col_App','_col_App')">上月</a>&nbsp;&nbsp;
                    <a id="thisMonthCol_App" class="time_stat1" style="width:40px;" href="javascript:articleOverview_stat.setCalender('thisMonth','_col_App','_col_App')">本月</a>&nbsp;&nbsp;&nbsp;&nbsp;
                    <input id="pubTime_from_col_App" readonly type="text" value="" name="pubTime_from_col_App" date-data-format="yyyy-mm-dd hh:ii:ss" /> -
                    <input id="pubTime_to_col_App" readonly type="text" value="" name="pubTime_to_col_App" date-data-format="yyyy-mm-dd hh:ii:ss"/>
                    <input class="lookBtn" style="margin:0px 5px;" type="submit" onclick="articleOverview_stat.search('ArticleColumn','channelApp','_col_App');" value="查看" />
                    <input class="resetBtn" type="button" id="reset_col_app" value="重置" />
                    <input class="see exportBtn" style='margin-left:5px;' type="button" onclick="articleOverview_stat.outputcsv('ArticleColumn','channelApp', 'column', '', '_col_App')" value="导出数据" />
                </div>
                <!-- </form> -->
                <div class="detail">
                    <table class="loadTable" id="columnDetailTable_App">
                        <thead>
                            <tr class="tdtr">
                                <th class="title tdtr1"><input type="checkbox" name="selectAll" id="selectAll_col_App" title="全选/取消"/></th>
                                <th class="title tdtr">栏目名称</th>
                                <th class="title tdtr">稿件量</th>
                                <th class="title tdtr">总点击量</th>
                                <th class="title1 tdtr">Web点击数</th>
                                <th class="title1 tdtr">触屏点击数</th>
                                <th class="title1 tdtr">APP点击数</th>
                                <th class="title tdtr">总分享数</th>
                                <th class="title1 tdtr">Web分享</th>
                                <th class="title1 tdtr">触屏分享</th>
                                <th class="title1 tdtr">APP分享</th>
                                <th class="title tdtr">总评论量</th>
                                <th class="title1 tdtr">Web点评量</th>
                                <th class="title1 tdtr">触屏点评量</th>
                                <th class="title1 tdtr">APP点评量</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                    <ul class="foot_page" id="paginator_col_App">

                    </ul>
                    <input type="hidden" id="total_count_col_App" name="" value="0" />
                </div>
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
                    部门选择
                    </h4>
                </div>
                <div class="modal-body clearfix">
                    <div class="fl">
                        <div class="department-search" id="department">
                        <input type="text" placeholder="输入部门查询">
                        <ul>
                            <li>部门一</li>
                            <li>部门二</li>
                            <li>部门三</li>
                            <li>部门四</li>
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