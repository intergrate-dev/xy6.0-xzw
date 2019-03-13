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
        <script type="text/javascript" src="./script/articleBoard.js"></script>

        <link rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
        <link href="../script/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet" media="screen">
        <link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
        <link rel="stylesheet" type="text/css" href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>
        <link type="text/css" rel="stylesheet" href="./css/statistics.css"/>
        <style>
            #channels li:hover{
                border-bottom: 2px solid #ddd;
            }

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
            <div>
                <ul class="channels" id="channels">
                    <li style="width:140px;" class="select channelTab" id="channelWeb">Web发布库</li>
                    <li style="width:140px;" class="channelTab" id="channelApp">App发布库</li>
                </ul>
            </div>
            <!-- Web发布库 -->
            <div id="webTab">
                <div id="detail">
                    <div class="left" style="margin:10px 0px 2px 10px;">
                        <span class="publishTime">栏目:</span>
                        <input id="colName" type="text" placeholder="请选择栏目" readonly name="colName" />
                        <input id="colID_web" type="hidden" name="colID_web" value="" />
                        <input id="colSelect" class="see" type="button" title="选择栏目" value="选择" />
                    </div>
                    <div class="left" style="margin:15px 0px 2px 80px">
                        <span class="publishTime">时间:</span>&nbsp;&nbsp;&nbsp;
                        <a id="thisDay_web" class="time_stat1"  href="javascript:articleBoard_stat.setTime('thisDay', '_web')">24小时内</a>
                        <a id="thisWeek_web" class="time_stat1"  href="javascript:articleBoard_stat.setTime('thisWeek', '_web')">本周</a>
                        <a id="thisMonth_web" class="time_stat1"  href="javascript:articleBoard_stat.setTime('thisMonth', '_web')">本月</a>
                        &nbsp;&nbsp;&nbsp;
                        <input id="time_from_web" type="hidden" value="" name="time_from_web" />
                        <input id="time_to_web" type="hidden" value="" name="time_to_web" />
                        <input id="time_tag_web" type="hidden" value="" name="time_tag_web" />
                    </div>
                    <div style="float: left;">
                        <div class="custform-controls">
                            <select id="monthSelect_web">
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
                        <input class="lookBtn" type="submit" onclick="articleBoard_stat.search('channelWeb','_web');" value="查看" />
                    </div>
                    <div class="exportData1">
                        <input class="see exportBtn"  type="button" value="导出数据" onclick="articleBoard_stat.outputcsv('channelWeb','_web')" />
                    </div>
                    <div class="detail" >
                        <div class="topCount">
                            <div class="topClick">
                                <table id="webClick">
                                    <thead class="thead">
                                        <th class="clickId" id="clickId">点击量TOP100</th>
                                        <th class="clickName" id="clickName">栏目</th>
                                        <th class="clickAuthor" id="clickAuthor">作者</th>
                                    </thead>
                                    <tbody class="tbody">
                                        
                                    </tbody> 
                                </table>
                            </div>
                            <div class="topDiss">
                                <table id="webDiss">
                                    <thead class="thead">
                                        <th class="commentId" id="commentId">评论量TOP100</th>
                                        <th class="commentName" id="commentName">栏目</th>
                                        <th class="commentAuthor" id="commentAuthor">作者</th>
                                    </thead>
                                    <tbody class="tbody">
                                        
                                    </tbody>
                                </table>   
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- App发布库 -->
            <div id="appTab" style="display: none">
                <div id="detail_App">
                    <div class="left" style="margin:10px 0px 2px 10px;">
                        <span class="publishTime">栏目:</span>
                        <input id="colName_app" type="text" placeholder="请选择栏目" readonly name="colName_app" />
                        <input id="colID_App" type="hidden" name="colID_App" value="" />
                        <input id="colSelect_app" class="see" style="background:#00a0e6;" type="button" title="选择栏目" value="选择" />
                    </div>
                    <div class="left" style="margin:15px 0px 2px 80px">
                        <span class="publishTime">时间:</span>&nbsp;&nbsp;&nbsp;
                        <a id="thisDay_App" class="time_stat1"  href="javascript:articleBoard_stat.setTime('thisDay', '_App')">24小时内</a>
                        <a id="thisWeek_App" class="time_stat1"  href="javascript:articleBoard_stat.setTime('thisWeek', '_App')">本周</a>
                        <a id="thisMonth_App" class="time_stat1"  href="javascript:articleBoard_stat.setTime('thisMonth', '_App')">本月</a>
                        &nbsp;&nbsp;&nbsp;
                        <input id="time_from_App" type="hidden" value="" name="time_from_App" />
                        <input id="time_to_App" type="hidden" value="" name="time_to_App" />
                        <input id="time_tag_App" type="hidden" value="" name="time_tag_App" />
                    </div>
                    <div style="float: left;">
                        <div class="custform-controls">
                            <select id="monthSelect_App">
                                <option value="-1" selected></option>
                                <option value="1">1月</option>
                                <option value="2">2月</option>
                                <option value="3">3月</option>
                                <option value="4">4月</option>
                                <option value="5">5月</option>
                                <option value="6">6月</option>
                                <option value="7">7月</option>
                                <option value="8">8月</option>
                                <option value="9">9月</option>
                                <option value="10">10月</option>
                                <option value="11">11月</option>
                                <option value="12">12月</option>
                            </select>
                        </div>
                    </div>
                    <div class="search">
                        <input class="lookBtn" type="submit" onclick="articleBoard_stat.search('channelApp','_App');" value="查看" />
                    </div>
                    <div class="exportData1">
                        <input class="see exportBtn" type="button" value="导出数据" onclick="articleBoard_stat.outputcsv('channelApp','_App')" />
                    </div>
                    <div class="detail" >
                        <div class="topCount">
                            <div class="topClick">
                                <table id="appClick">
                                    <thead class="thead">
                                        <th class="clickId" id="clickId_App">点击量TOP100</th>
                                        <th class="clickName" id="clickName_App">栏目</th>
                                        <th class="clickAuthor" id="clickAuthor_App">作者</th>

                                    </thead>
                                    <tbody class="tbody">

                                    </tbody>
                                </table>
                            </div>
                            <div class="topDiss">
                                <table id="appDiss">
                                    <thead class="thead">
                                        <th class="commentId" id="commentId_App">评论量TOP100</th>
                                        <th class="commentName" id="commentName_App">栏目</th>
                                        <th class="commentAuthor" id="commentAuthor_App">作者</th>
                                    </thead>
                                    <tbody class="tbody">

                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    <!-- <input type="hidden" id="siteID" value="${siteID}" />站点ID -->
    <input type="hidden" id="siteID" value="${siteID}" />
        <form id="form" method="post"><!-- csv输出 -->
            <input type="hidden" id="jsonData" name="jsonData" />
        </form>
    </body>
</html>