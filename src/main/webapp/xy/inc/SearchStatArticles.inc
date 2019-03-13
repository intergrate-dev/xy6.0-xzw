
<style type="text/css">
	#wrapMain{margin-left:0px; padding: 0;}
	.selfTab{width:70px;cursor:pointer;padding:3px;}
	#searchList,#queryReset{
		background: none;
		margin-top:5px;
		border: none;
	}

	.detail_employee a:focus,.detail_employee a:visited,.detail_employee a:active,.detail_employee a:hover{
		text-decoration: none;
	}

	#exp_detail ._sel,#exp_detail_app ._sel,#exp_detail_topic ._sel{
		float:left;
		margin:10px 0px 10px 10px;
	}
	._sel .col_sel{
		float:left;
    	width:185px;
	}
	._sel .sel_Id{
		float:left;
    	width:150px;
	}
	._sel .emp_sel{
		float:left;
    	width:240px;
	}
	._sel .src_sel{
		float:left;
    	width:240px;
	}
	._sel .editor_sel{
		float:left;
    	width:240px;
	}
	._sel .articleSelect{
		float:left;
    	width:160px;
	}
	._sel .checkbox_ahthor{
		overflow: hidden;
		float:left;
    	width:65px;
	}
	#exp_src, #exp_src_app{
		margin-bottom: 0;
	}
	#exp_employeeTab .input_select,#exp_detail .input_selectId,#exp_employeeTab_app .input_select,
	#exp_detail_app .input_selectId,#exp_employeeTab_topic .input_select,
    #exp_detail_topic .input_selectId{
	    border: 1px solid #ddd;
	    height: 26px;
	    line-height: 26px;
	    padding-left: 5px;
	    color: #333;
	    width: 115px;

		border-radius: 0;
		background:none;
	}
	
	.publishTime{
		color:#666;
    	font-size:13px;
	}
	.publishTime1{
		float:left;
		color:#666;
    	font-size:13px;
    	height:24px;
    	line-height: 24px;
    	margin-right: 5px;
	}
	.articleSelect #exp_articleSelect{
		float:left;
		width:90px;
	    margin-bottom: 0;
	    height: 24px;
	    padding: 0;
	    border-radius: 0;
	    border: 1px solid #A9A9A9;
	    box-shadow: none;
	    cursor: inherit; 
	}
	.articleSelect #exp_articleSelect_app,.articleSelect #exp_articleSelect_topic{
		float:left;
		width:90px;
	    margin-bottom: 0;
	    height: 24px;
	    padding: 0;
	    border-radius: 0;
	    border: 1px solid #A9A9A9;
	    box-shadow: none;
	    cursor: inherit; 
	}
    #exp_detail .see,#exp_detail_app .see,#exp_detail_topic .see{
		background: #1bb8fa;
	    color: #fff;
	    border-radius: 3px;
	    border: none;
	    width: 67px;
	    height: 26px;
	}
	
	.detail_employee .lookBtn,.detail_employee .resetBtn{
	   margin-top: -10px;
	}
	.ml6{
		margin-left: 6px;
	}
	#exp_chTimeRegionDept .pubtimeTime,#exp_chTimeRegionDept_app .pubtimeTime,#exp_chTimeRegionDept_topic .pubtimeTime{
		border: 1px solid #ddd;
	    height: 26px;
	    font-size: 12px;
	    padding-left: 5px;
	    border-radius: 0;
    	background: #fff;
    	width: 132px;
	}
	.publishTime {
	    color: #666;
	    font-size: 13px;
	    float: left;
	    line-height: 23px;
	}
	/*#divQueryCust .detail_employee{
    	    float:left;
    }*/
	#divQueryCust_app .detail_employee{
	    float:left;
		clear: both;
	    width:1100px;
	}
	#divQueryCust_topic .detail_employee{

    	clear: both;
    	width:1100px;
    }
	#divQueryCust .detail_employee .times{
	    float:left;
	    clear:both;
	    margin-top: 10px;
	}
	#divQueryCust_app .detail_employee .times{
    	    float:left;
    	    clear:both;
    	    margin-top: 6px;
    	    margin-left:9px;
    }
    #divQueryCust_topic .detail_employee .times{
        	float:left;
        	clear:both;
        	margin-top: 6px;
        	margin-left:9px;
    }
	.status{
		border-left: 0;
    	border-right: 0;
    	padding: 10px;
	}
	#doclistframe{
		margin-left: 10px;
	}
	.channels{
		margin-left: 10px;
	}
	#exp_chTimeRegionDept .time_stat,#exp_chTimeRegionDept_app .time_stat,#exp_chTimeRegionDept_topic .time_stat{
	    width: 70px;
	    display: inline-block;
	    text-align: center;
	    color: #333;
	    font-size: 13px;
	    float: left;
	    margin-left: 10px;
	    height: 26px;
	    line-height: 26px;
	    border: 1px solid #ddd;
	    border-radius: 3px;
	}
	.detail_employee .times{
	    float:left;
	    clear:both;
	    margin-top: 10px;
   }
   .page-unselected, .page-selected{
   		height: 22px;
   }
   [class^="icon-"], [class*=" icon-"]{
   		margin-top: 3px;
   }
   #exp_employeeTab .exportBtn{
   		border-radius: 3px;
	    color: #fff;
	    background: #00a0e6;
	    width:93px;
	    height:30px;
	    margin-left:10px;
	    font-size: 13px;
	    cursor: pointer;
	    border: none;
   }
   #exp_employeeTab_app{
        display:none;
   }
   #exp_employeeTab_app .exportBtn{
   		border-radius: 3px;
	    color: #fff;
	    background: #00a0e6;
	    width:93px;
	    height:30px;
	    margin-left:10px;
	    font-size: 13px;
	    cursor: pointer;
	    border: none;
   }
   #exp_employeeTab_topic .exportBtn{
      	border-radius: 3px;
   	    color: #fff;
   	    background: #00a0e6;
   	    width:93px;
   	    height:30px;
   	    margin-left:10px;
   	    font-size: 13px;
   	    cursor: pointer;
   	    border: none;
      }
   #exp_detail .input_selectId{
		width: 120px;
		font-size: 12px;
   }
   .select_time{
      font-size: 13px;
	  BACKGROUND: #1bb8fa;
	  COLOR: #fff !important;
	  border: none !important;
   }

   .modal .modal-body{
        height: 340px;
    }
   .clearfix:after {
        content: ".";
        display: block;
        clear: both;
        visibility:hidden;
        line-height: 0;
        height: 0;
        font-size:0;
   }
   .fl{
        float: left;
   }
   .department-search{
       margin-left: 160px;
   }
   .department-search input{
       width: 220px;
       height: 30px;
       padding: 0 4px;
       line-height: 30px;
       font-size: 14px;
       font-weight: normal;
       border: 1px solid #ddd;
   }
   .department-search ul{
       border: 1px solid #ddd;
       border-top: none;
       max-height: 280px;
       overflow: auto;
   }
   .department-search ul li{
       height: 30px;
       line-height: 30px;
       text-align: left;
       border-bottom: 1px solid #ddd;
       padding: 4px 0 4px 10px;
       cursor: pointer;
       font-size: 14px;
       font-weight: normal;
       background: none;
   }
   .department-search ul li:nth-last-child(1){
       border-bottom: none;
   }
   .department-search ul li:hover{
       background: lightcyan;
   }
   .modal-body>div{
       position: relative;
   }
   .search-btn{
       width: 30px;
       height: 30px;
       border: 1px solid #ddd;
       border-radius: 0;
       display: inline-block;
       position: absolute;
       right: 0;
       top: 0;
       outline:none;
   }
   .department-search .selected{
       background: lightcyan;
   }
   .modal-footer{
       text-align: center;
   }
	#exp_detail,#exp_detail_app,#exp_detail_topic{
		display: table;
	}
	body input[readonly]{
        cursor: pointer;
    }
</style>
<div id="main_search" class="noQueryCust">
	<ul class="channels">
		<c:forEach var="ch" items="${channels}">
			<c:if test="${ch != null}">
			<li class="channelTab" rule="a_status_EQ_1_AND_a_siteID_EQ_<c:out value="${siteID}"/>"
				channel="<c:out value="${ch.id}"/>"><c:out value="${ch.code}"/>稿件</li>
			</c:if>
		</c:forEach>
		<li class="channelTab" id="articleTopic" queryID="3" rule="a_status_EQ_1_AND_a_siteID_EQ_${siteID} AND a_topicID is NOT NULL" channel="2" listID="<c:out value='${ListID3}'/>"/>话题稿件</li>
	</ul>
	<div id="divQueryCust">

		<!-- web渠道 -->
		<section id="exp_employeeTab">
			<div id="exp_detail">
					<div class="_sel">
						<div class="col_sel">
							<span class="publishTime">栏目：</span>
							<input class="input_select" id="exp_column" readonly type="text" name="exp_column" />
							<input id="exp_columnID" type="hidden" name="ID" />
							<!--<input id="exp_columnSelect" class="see" type="button" title="选择栏目" value="选择" />-->
						</div>
						<div class="articleSelect type1 type2">
							<span class="publishTime1">稿件类型:</span>
                            <select id="exp_articleSelect">
                                <option value="-1" selected></option>
                                <option value="0">文章</option>
                                <option value="1">组图</option>
                                <option value="2">视频</option>
                                <option value="3">专题</option>
                                <option value="4">链接</option>
                                <option value="5">多标题</option>
                                <option value="6">直播</option>
                                <option value="7">活动</option>
                                <option value="8">广告</option>
                                <!--<option value="9">文档</option>-->
                                <option value="11">全景图</option>
                                <option value="12">H5</option>

                            </select>
                        </div>
						<div class="sel_Id">
							<input class="input_selectId" id="exp_id" type="text" name="Name" placeholder="输入稿件ID/标题" />
							<input id="exp_articleID" type="hidden" name="ID" />
						</div>
						<div class="checkbox_ahthor type1 type2">
							<input id="exp_checkboxId" type="checkbox" name="checkbox" style="float:left;margin-right:5px;margin-top: 5px;"/><span class="publishTime1">原创</span>
						</div>
						<div class="emp_sel type2 type0">
							<span class="publishTime">人员：</span>
							<input class="input_select" id="exp_employee" readonly type="text" name="Name" />
							<input id="exp_employeeID" type="hidden" name="ID" />
							<!--<input id="exp_employeeSelect" class="see" type="button" title="选择人员" value="选择" />-->
						</div>
						<div class="src_sel type1 type2">
							<span class="publishTime">来源：</span>
							<input class="input_select" id="exp_src" type="text" name="Name" />
							<input id="exp_srcID" type="hidden" name="ID" />
							<input id="exp_srcSelect" class="see" type="button" title="选择来源" value="选择" />
						</div>
						<div class="editor_sel type1 type2">
							<span class="publishTime">编辑：</span>
							<input class="input_select" id="exp_editor" readonly type="text" name="Name" />
							<input id="exp_editorID" type="hidden" name="ID" />
							<!--<input id="exp_editorSelect" class="see" type="button" title="选择编辑" value="选择" />-->
						</div>
					</div>
					<div class="detail_employee">
						<div id="exp_chTimeRegionDept" class="times">
							<span class="publishTime" style="margin-left: 10px;">发布时间：</span>
							<a id="exp_current24HEmp" class="time_stat"  href="javascript:setCalender('current24H','')">24小时内</a>
							<a id="exp_current7DEmp" class="time_stat"  href="javascript:setCalender('current7D','')">本周</a>
							<a id="exp_lastMonthEmp" class="time_stat"  href="javascript:setCalender('lastMonth','')">上月</a>
							<a style="margin-right: 10px;" id="exp_thisMonthEmp" class="time_stat" href="javascript:setCalender('thisMonth','')">本月</a>
							<input id="exp_pubTime_from_emp" class="pubtimeTime" readonly type="text" value="" name="pubTime_from_emp" date-data-format="yyyy-mm-dd hh:ii:ss" /> -
							<input id="exp_pubTime_to_emp" class="pubtimeTime" readonly type="text" value="" name="pubTime_to_emp" date-data-format="yyyy-mm-dd hh:ii:ss"/>
							<!--<input class="lookBtn" style="margin:0px 5px;" type="submit" id="exp_lookMsg" value="查看" />-->
							<img class="lookBtn ml6" src="stat/images/11.png"  id="exp_lookMsg" title="查看" alt="查看" />
							<!--<input class="resetBtn" type="button" id="exp_resetWorkload" value="重置" />-->
							<img class="resetBtn ml6" src="stat/images/12.png"  id="exp_resetWorkload" title="重置" alt="重置" />
							<!--<input class="exportBtn" type="button"  value="导出数据" />-->
						</div>
					</div>
				
			</div>
		</section>

		<!-- App渠道 -->
		<section id="exp_employeeTab_app">
			<div id="exp_detail_app">
					<div class="_sel">
						<div class="col_sel">
							<span class="publishTime">栏目：</span>
							<input class="input_select" id="exp_column_app" readonly type="text" name="exp_column_app" />
							<input id="exp_columnID_app" type="hidden" name="ID" />
							<!--<input id="exp_columnSelect_app" class="see" type="button" title="选择栏目" value="选择" />-->
						</div>
						<div class="articleSelect type1 type2">
							<span class="publishTime1">稿件类型:</span>
                            <select id="exp_articleSelect_app">
                                <option value="-1" selected></option>
                                <option value="0">文章</option>
                                <option value="1">组图</option>
                                <option value="2">视频</option>
                                <option value="3">专题</option>
                                <option value="4">链接</option>
                                <option value="5">多标题</option>
                                <option value="6">直播</option>
                                <option value="7">活动</option>
                                <option value="8">广告</option>
                                <!--<option value="9">文档</option>-->
                                <option value="11">全景图</option>
                                <option value="12">H5</option>

                            </select>
                        </div>
						<div class="sel_Id">
							<input style="width: 120px; font-size: 12px;" class="input_selectId" id="exp_id_app" type="text" name="Name" placeholder="输入稿件ID/标题" />
							<input id="exp_articleID_app" type="hidden" name="ID" />
						</div>
						<div class="checkbox_ahthor type1 type2">
							<input id="exp_checkboxId_app" type="checkbox" name="checkbox" style="float:left;margin-right:5px;margin-top: 5px;"/><span class="publishTime1">原创</span>
						</div>
						<div class="emp_sel type2 type0">
							<span class="publishTime">人员：</span>
							<input class="input_select" id="exp_employee_app" readonly type="text" name="Name" />
							<input id="exp_employeeID_app" type="hidden" name="ID" />
							<!--<input id="exp_employeeSelect_app" class="see" type="button" title="选择人员" value="选择" />-->
						</div>
						<div class="src_sel type1 type2">
							<span class="publishTime">来源：</span>
							<input class="input_select" id="exp_src_app"  type="text" name="Name" />
							<input id="exp_srcID_app" type="hidden" name="ID" />
							<input id="exp_srcSelect_app" class="see" type="button" title="选择来源" value="选择" />
						</div>
						<div class="editor_sel type1 type2">
							<span class="publishTime">编辑：</span>
							<input class="input_select" id="exp_editor_app" readonly type="text" name="Name" />
							<input id="exp_editorID_app" type="hidden" name="ID" />
							<!--<input id="exp_editorSelect_app" class="see" type="button" title="选择编辑" value="选择" />-->
						</div>
					</div>
					<div class="detail_employee">
						<div id="exp_chTimeRegionDept_app" class="times">
							<span class="publishTime" style="margin-left: 10px;">发布时间：</span>
							<a id="exp_current24HEmp_app" class="time_stat"  href="javascript:setCalender('current24H','_app')">24小时内</a>
							<a id="exp_current7DEmp_app" class="time_stat"  href="javascript:setCalender('current7D','_app')">本周</a>
							<a id="exp_lastMonthEmp_app" class="time_stat" href="javascript:setCalender('lastMonth','_app')">上月</a>
							<a style="margin-right: 10px;" id="exp_thisMonthEmp_app" class="time_stat" href="javascript:setCalender('thisMonth','_app')">本月</a>
							<input id="exp_pubTime_from_emp_app" class="pubtimeTime" readonly type="text" value="" name="pubTime_from_emp" date-data-format="yyyy-mm-dd hh:ii:ss" /> -
							<input id="exp_pubTime_to_emp_app" class="pubtimeTime" readonly type="text" value="" name="pubTime_to_emp" date-data-format="yyyy-mm-dd hh:ii:ss"/>
							<!--<input class="lookBtn" style="margin:0px 5px;" type="submit" id="exp_lookMsg_app" value="查看" />-->
							<img class="lookBtn ml6" src="stat/images/11.png" id="exp_lookMsg_app" title="查看" />
							<!--<input class="resetBtn" type="button" id="exp_resetWorkload_app" value="重置" />-->
							
							<img class="resetBtn ml6" src="stat/images/12.png" id="exp_resetWorkload_app" title="重置" alt="重置" />
							<!--<input class="exportBtn" type="button" value="导出数据" />-->
						</div>
					</div>
			</div>
		</section>

		<!-- 话题稿件 start -->
            <section id="exp_employeeTab_topic">
        			<div id="exp_detail_topic">
        					<div class="_sel">

        						<div class="col_sel">
        							<span class="publishTime">话题：</span>
        							<input class="input_select" id="exp_column_topic" readonly type="text" name="exp_column_topic" />
        							<input id="exp_columnID_topic" type="hidden" name="ID" />
        							<!--<input id="exp_columnSelect_topic" class="see" type="button" title="选择栏目" value="选择" />-->
        						</div>

        						<!--<div class="articleSelect type1 type2">
        							<span class="publishTime1">话题:</span>
        							<input style="width: 120px; font-size: 12px;" class="input_selectId" readonly name="topicName" id="select_id_topic" type="text"/>
                                    <select id="exp_articleSelect_topic">
                                        <option value="-1" selected></option>
                                        <option value="0">文章</option>
                                        <option value="1">组图</option>
                                        <option value="2">视频</option>
                                        <option value="3">专题</option>
                                        <option value="4">链接</option>
                                        <option value="5">多标题</option>
                                        <option value="6">直播</option>
                                        <option value="7">活动</option>
                                        <option value="8">广告</option>
                                        <option value="9">文档</option>
                                        <option value="11">全景图</option>
                                        <option value="12">H5</option>

                                    </select>
                                </div>-->
        						<div class="sel_Id">
        							<input style="width: 120px; font-size: 12px;margin-left: 20px;" class="input_selectId" id="exp_id_topic" type="text" name="Name" placeholder="输入话题ID" />
        							<input id="exp_articleID_topic" type="hidden" name="ID" />
        						</div>
        						<div class="sel_Id">
                                    <input style="width: 120px; font-size: 12px;margin-left: 20px;" class="input_selectId" id="exp_title_topic" type="text" name="Name" placeholder="输入话题标题" />
                                    <input id="exp_articleID_title" type="hidden" name="ID" />
                                </div>
        					</div>
        					<div class="detail_employee">
        						<div id="exp_chTimeRegionDept_topic" class="times">
        							<span class="publishTime" style="margin-left: 10px;">发布时间：</span>
        							<a id="exp_current24HEmp_topic" class="time_stat"  href="javascript:setCalender('current24H','_topic')">24小时内</a>
        							<a id="exp_current7DEmp_topic" class="time_stat"  href="javascript:setCalender('current7D','_topic')">本周</a>
        							<a id="exp_lastMonthEmp_topic" class="time_stat" href="javascript:setCalender('lastMonth','_topic')">上月</a>
        							<a style="margin-right: 10px;" id="exp_thisMonthEmp_topic" class="time_stat" href="javascript:setCalender('thisMonth','_topic')">本月</a>
        							<input id="exp_pubTime_from_emp_topic" class="pubtimeTime" readonly type="text" value="" name="pubTime_from_emp" date-data-format="yyyy-mm-dd hh:ii:ss" /> -
        							<input id="exp_pubTime_to_emp_topic" class="pubtimeTime" readonly type="text" value="" name="pubTime_to_emp" date-data-format="yyyy-mm-dd hh:ii:ss"/>
        							<!--<input class="lookBtn" style="margin:0px 5px;" type="submit" id="exp_lookMsg_topic" value="查看" />-->
        							<img class="lookBtn ml6" src="stat/images/11.png" id="exp_lookMsg_topic" title="查看" />
        							<!--<input class="resetBtn" type="button" id="exp_resetWorkload_topic" value="重置" />-->

        							<img class="resetBtn ml6" src="stat/images/12.png" id="exp_resetWorkload_topic" title="重置" alt="重置" />
        							<!--<input class="exportBtn" type="button" value="导出数据" />-->
        							<img class=" lookBtn ml6" id="topicOutputcsv" src="img/13.png" alt="导出数据" title="导出数据" />
        						</div>
        					</div>
        			</div>
        	</section>
            <!-- 话题稿件 end -->

            <input type="hidden" id="siteID" value="${siteID}" /><!-- 站点ID -->
            <form id="form" method="post" style="display: none;"><!-- csv输出 -->
                <input type="hidden" id="jsonData" name="jsonData" />
            </form>

	</div>
</div>
<script type="text/javascript">
	var self_param = {
	<c:forEach var="domInfo" items="${docLibs}" varStatus="varStatus">
		"<c:out value="${varStatus.index}"/>": {
			docLibID: "<c:out value="${domInfo.docLibID}"/>",
			fvID : "<c:out value="${domInfo.folderID}"/>",
			ch : "<c:out value="${varStatus.index}"/>",
			listID : "<c:out value="${domInfo.listID}"/>"
		},
	</c:forEach>
		type: ${type}
	}
</script>

<script type="text/javascript" src="../e5script/jquery/jquery-1.9.1.min.js"></script>
<!-- <script type="text/javascript" src="../e5script/jquery/jquery.dialog.js"></script>
<script type="text/javascript" src="script/bootstrap-3.3.4/js/bootstrap.min.js"></script> -->
<script type="text/javascript" src="script/bootstrap-datetimepicker/bootstrap-datetimepicker.js" charset="UTF-8"></script>
<script type="text/javascript" src="script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
<script type="text/javascript" src="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.js"></script>
<script type="text/javascript" src="script/ResourceSelf.js"></script>
<script type="text/javascript" src="script/SearchStatArticles.js"></script>

<link rel="stylesheet" href="script/bootstrap-3.3.4/css/bootstrap.min.css">
<link rel="stylesheet" href="script/bootstrap-datetimepicker/css/datetimepicker.css" media="screen">
<link type="text/css" rel="stylesheet" href="../e5script/jquery/dialog.style.css"/>
<link rel="stylesheet" type="text/css" href="../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>

<script type="text/javascript">
         // 页面加载时，部分功能的显示和隐藏
            var _type=getQueryString("type");
            if(_type == 1){
                $(".articleSelect").hide();
                $(".checkbox_ahthor").hide();
                $(".emp_sel").show();
                $(".src_sel").hide();
                $(".editor_sel").hide();
            }else if(_type == 2){
                $(".articleSelect").hide();
                $(".checkbox_ahthor").hide();
                $(".emp_sel").hide();
                $(".src_sel").hide();
                $(".editor_sel").hide();
            }else{
                $(".articleSelect").show();
                $(".checkbox_ahthor").show();
                $(".emp_sel").hide();
                $(".src_sel").show();
                $(".editor_sel").show();
            }

            //切换Tab
            $('li.channelTab').click(function(evt){
                if($(this).attr("channel") == "0"){
                    $('#exp_employeeTab').css('display', 'inline');
                    $('#exp_employeeTab_app').css('display', 'none');
                    $('#exp_employeeTab_topic').css('display', 'none');
                    $('#ListExportSpan').show();
                }else if($(this).attr("channel") == "1"){
                    $('#exp_employeeTab_app').css('display', 'inline');
                    $('#exp_employeeTab').css('display', 'none');
                    $('#exp_employeeTab_topic').css('display', 'none');
                    $('#ListExportSpan').show();
                }else if($(this).attr("channel") == "2"){
                    $('#exp_employeeTab_topic').css('display', 'inline');
                    $('#exp_employeeTab_app').css('display', 'none');
                    $('#exp_employeeTab').css('display', 'none');
                    $('#ListExportSpan').hide();
                }else{
                    $('#exp_employeeTab').css('display', 'none');
                    $('#exp_employeeTab_app').css('display', 'none');
                    $('#exp_employeeTab_topic').css('display', 'none');
                }
            });

            $("#exp_articleSelect,#exp_articleSelect_app").change(function(){
                //查询
               $("#exp_lookMsg").click();
            })
            $("#exp_checkboxId,#exp_checkboxId_app").click(function(){
                //查询
               $("#exp_lookMsg").click();
            })

            //默认选择本月
            $("#exp_thisMonthEmp").addClass("select_time");
            $("#exp_thisMonthEmp_app").addClass("select_time");
            $("#exp_thisMonthEmp_topic").addClass("select_time");
            setCalender('thisMonth','');
            setCalender('thisMonth','_app');
            setCalender('thisMonth','_topic');
         //时间选择
            $(".time_stat").click(function(){
                $(this).addClass("select_time").siblings().removeClass("select_time");
            });
        //日期控件
            function initDateTimePicker(){
                    $('[id^="exp_pubTime_from_emp"]').datetimepicker({
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

                    $('[id^="exp_pubTime_from_emp"]').datetimepicker().on('changeDate', function(ev) {});

                    $('[id^="exp_pubTime_to_emp"]').datetimepicker({
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

                    $('[id^="exp_pubTime_to_emp"]').datetimepicker().on('changeDate', function(ev) {});
            };

		 	//类似于require.js 的模板加载
			 $.getScript('script/bootstrap-datetimepicker/bootstrap-datetimepicker.js', function() {
				 initDateTimePicker();
			 });

            // 点击上月本月要同时改变日历的值
            function setCalender(type,data) {
                var now = new Date();
                if(type == 'thisMonth'){
                    var ym = now.getFullYear() + "-" + add_zero(now.getMonth() + 1) + "-";
                    $('#exp_pubTime_from_emp' + data).val(ym + "01" + " 00:00:00");
                    $('#exp_pubTime_to_emp' + data).val(ym + add_zero(now.getDate())+ " 23:59:59");

                } else if(type == 'lastMonth'){
                    var year = now.getFullYear();
                    var month = now.getMonth();
                    if(month == 0){
                        year -= 1;
                        month = 12;
                    }
                    var ym = year + "-" + add_zero(month) + "-";
                    $('#exp_pubTime_from_emp' + data).val(ym + "01"+ " 00:00:00");
                    now.setDate(1);
                    now.setMonth(now.getMonth());
                    var cdt = new Date(now.getTime() - 1000 * 60 * 60 * 24);
                    $('#exp_pubTime_to_emp' + data).val(ym + cdt.getDate()+ " 23:59:59");

                } else if(type == 'current24H'){
                    var fromTime = new Date(now.getTime() - 24 * 3600 * 1000);
                    var fromYear = fromTime.getFullYear();
                    var fromMonth = fromTime.getMonth() + 1;
                    if(fromMonth == 0){
                        fromYear -= 1;
                        fromMonth = 12;
                    }
                    fromMonth = add_zero(fromMonth);
                    var fromDay = add_zero(fromTime.getDate());
                    var fromHour = add_zero(fromTime.getHours());
                    var fromMinutes = add_zero(fromTime.getMinutes());
                    var fromSeconds = add_zero(fromTime.getSeconds());
                    var newFromTime = fromYear + '-' + fromMonth + '-' + fromDay + ' ' + fromHour + ':' + fromMinutes + ':' + fromSeconds;
                    $('#exp_pubTime_from_emp' + data).val(newFromTime);
                    var nowTime = new Date(now.getTime());
                    var nowYear = nowTime.getFullYear();
                    var nowMonth = nowTime.getMonth() + 1;
                    if(nowMonth == 0){
                        nowYear -= 1;
                        nowMonth = 12;
                    }
                    nowMonth = add_zero(nowMonth);
                    var nowDay = add_zero(nowTime.getDate());
                    var nowHour = add_zero(nowTime.getHours());
                    var nowMinutes = add_zero(nowTime.getMinutes());
                    var nowSeconds = add_zero(nowTime.getSeconds());
                    var newNowTime = nowYear + '-' + nowMonth + '-' + nowDay + ' ' + nowHour + ':' + nowMinutes + ':' + nowSeconds;
                    $('#exp_pubTime_to_emp' + data).val(newNowTime);

                } else if(type == 'current7D'){
                    //var fromTime = new Date(now.getTime() - 7 * 24 * 3600 * 1000);
                    var week=now.getDay();
                    var currweek;
                    if(week==0){
                        currweek= now.getTime() - 6 * 24 * 3600 * 1000
                    }else{
                        currweek= now.getTime() - (week-1) * 24 * 3600 * 1000
                    }
                    var fromTime = new Date(currweek);
                    var fromYear = fromTime.getFullYear();
                    var fromMonth = fromTime.getMonth() + 1;
                    if (fromMonth == 0) {
                        fromYear -= 1;
                        fromMonth = 12;
                    }
                    fromMonth = add_zero(fromMonth);
                    var fromDay = add_zero(fromTime.getDate());
                    var newFromTime = fromYear + '-' + fromMonth + '-' + fromDay + " 00:00:00";
                    $('#exp_pubTime_from_emp' + data).val(newFromTime);
                    var nowTime = new Date(now.getTime());
                    var nowYear = nowTime.getFullYear();
                    var nowMonth = nowTime.getMonth() + 1;
                    if (nowMonth == 0) {
                        nowYear -= 1;
                        nowMonth = 12;
                    }
                    nowMonth = add_zero(nowMonth);
                    var nowDay = add_zero(nowTime.getDate());
                    var newNowTime = nowYear + '-' + nowMonth + '-' + nowDay + " 23:59:59";
                    $('#exp_pubTime_to_emp' + data).val(newNowTime);

                }

                //查询
                $("#exp_lookMsg").click();
                $("#topicOutputcsv").click(outputcsv);
                <!-- 导出csv文件start-->
                function outputcsv(){
                    //表头部
                    var thdatas = ["id","类型","稿件类别","标题","作者","所属栏目","发布时间","总点击量","PC点击量","WAP点击量","APP点击量","总转发量","PC转发量","WAP转发量","APP转发量","总评论量","PC评论量","WAP评论量","APP评论量"];

                    //部门id 来源名称   栏目id
                    var particularParam=$("#exp_columnID_topic").val();
                    //console.log(particularParam );

                    //默认时间为本月
                    var now = new Date();
                    var ym = now.getFullYear() + "-" + add_zero(now.getMonth() + 1) + "-";
                    var beginTime=$('#exp_pubTime_from_emp_topic').val();
                    var endTime=$('#exp_pubTime_to_emp_topic').val();
                    beginTime= beginTime=='' ? ym + "01" + " 00:00:00" : beginTime;
                    endTime= endTime=='' ? ym + add_zero(now.getDate()) + " 23:59:59" : endTime;

                    //确保开始时间小于结束时间
                    var beginTimeNum= new Date(beginTime);
                    var endTimeNum= new Date(endTime);
                    if(beginTimeNum.getTime()>endTimeNum.getTime()){
                        alert('“开始时间”晚于“结束时间”，请重新选择！');
                        return;
                    }
                    //topicID,topicName的like查询条件
                    var topicID= $.trim($("#exp_id_topic").val());
                    var topicName=$.trim($("#exp_title_topic").val());



                    var siteID=getQueryString("siteID");
                    var params = {
                        fileName: '导出话题稿件统计.csv',
                        headParam: thdatas,
                        status:1,
                        siteID: siteID,
                        dataParam: {
                            particularParam: particularParam,
                            beginTime: beginTime,
                            endTime: endTime,
                            topicID: topicID,
                            topicName: topicName
                        }
                    };

                    $('#jsonData').val(JSON.stringify(params));
                    $("#form").attr("action", "../../xy/topic/ExportCSV.do");
                    $("#form").submit();
                }
                <!-- 导出csv文件end-->
            }
           function add_zero(param){
                   if(param < 10) return "0" + param;
                   return "" + param;
               }


           //打开主栏目选择对话框
          var department_stat={
               colDialog: null,
            colDialogWebApp: null,
            init:function(){
            var type=getQueryString("type");
            if(type!=1 && type!=2){
                $("#exp_column,#exp_column_app").click(department_stat.colSelect);
            }
            },
            colSelect:function(evt){
                   var ch = 1;
                   department_stat.colDialogWebApp = '_app';
                   var channel = $('#main_search li.select').attr('channel');
                   if (channel == 0) {
                       ch = 0;
                       department_stat.colDialogWebApp = '';
                   }
                   var siteID=getQueryString("siteID");
                   var dataUrl = "../xy/column/ColumnCheck.jsp?type=op&siteID=" + siteID + "&ids=" + $("#colID" + department_stat.colDialogWebApp).val() + "&ch=" + ch+"&style=radio&type=all";
                   //var pos = columns_stat._getDialogPos(document.getElementById("colName" + columns_stat.colDialogWebApp));
                   var pos = {left : "350px",top : "50px",width : "1000px",height : "500px"};
                   department_stat.colDialog = e5.dialog({
                       type: "iframe",
                       value: dataUrl
                   }, {
                       showTitle: false,
                       width: "450px",
                       height: "430px",
                       pos: pos,
                       resizable: false
                   });
                   department_stat.colDialog.show();
                 }

          }
           //当用户提交选择的栏目,实现栏目选择树的接口
           function columnClose(filterChecked, allFilterChecked) {
               $("#exp_column" + department_stat.colDialogWebApp).val(allFilterChecked[1]);
               $("#exp_columnID" + department_stat.colDialogWebApp).val(allFilterChecked[0]);
               //查询
               $("#exp_lookMsg").click();
               columnCancel();
           }
           //点取消
           function columnCancel() {
               department_stat.colDialog.close();
           }
            department_stat.init();

           //来源选择
           var department_source = {
            dialog : null,
            init : function() {
                var type=getQueryString("type");
                if(type!=1 && type!=2){
                    $("#exp_srcSelect").click(department_source.select);
                    $("#exp_srcSelect_app").click(department_source.select);
                }
            },
            //来源选择按钮
            select : function(event) {
                var siteID=getQueryString("siteID");
                var url = "../../xy/GroupSelect.do?type=4&siteID=" + siteID;
                //var url = "../../xy/xy/GroupSelect.do?type=4&siteID=" + siteID;
                var pos = {left : "100px",top : "50px",width : "1000px",height : "500px"};
                department_source.dialog = e5.dialog({type : "iframe", value : url}, {
                    showTitle : true,
                    title: "来源选择",
                    width : "1000px",
                    height : "500px",
                    pos : pos,
                    resizable : false
                });
                department_source.dialog.show();
            },

            //选择窗口：选定后
            groupSelectOK: function(docLibID, docID) {
                department_source.dialog.close();

                $.get("../xy/source/findSource.do", {docLibID:docLibID, docID:docID}, function(data){
                       if($("#main_search").children("ul").find(".channelTab").eq(0).hasClass("select")){
                           $("#exp_src").val(data.name);
                           $("#exp_srcID").val(docID)
                       }else{
                           $("#exp_src_app").val(data.name);
                           $("#exp_srcID_app").val(docID)
                       }
                       //查询
                      $("#exp_lookMsg").click();
                });
            },
            //选择窗口：取消后
            groupSelectCancel : function() {
                department_source.dialog.close();
            }
           }
           //回调函数
           function groupSelectOK(docLibID, docID){
                department_source.groupSelectOK(docLibID, docID);
           }

           function groupSelectCancel(){
                department_source.groupSelectCancel();
           }
           department_source.init();

            //人员选择
              var department_select={
                   colDialog: null,
                colDialogWebApp: null,
                init:function(){
                var type=getQueryString("type");
                if(type!=1 && type!=2){
                    $("#exp_editor,#exp_editor_app").click(department_select.colSelect);
                }
                },
                colSelect:function(evt){
                       var ch = 1;
                       department_select.colDialogWebApp = '_app';
                       var channel = $('#main_search li.select').attr('channel');
                       if (channel == 0) {
                           ch = 0;
                           department_select.colDialogWebApp = '';
                       }
                       var siteID=getQueryString("siteID");
                       var dataUrl = "stat/editorSelect.html?type=op&siteID=" + siteID + "&ids=" + $("#colID" + department_select.colDialogWebApp).val() + "&ch=" + ch+"&style=radio&type=all";
                       //var pos = columns_stat._getDialogPos(document.getElementById("colName" + columns_stat.colDialogWebApp));
                       var pos = {left : "340px",top : "40px",width : "1000px",height : "500px"};
                       department_select.colDialog = e5.dialog({
                           type: "iframe",
                           value: dataUrl
                       }, {
                           showTitle: false,
                           width: "600px",
                           height: "462px",
                           pos: pos,
                           resizable: false
                       });
                       department_select.colDialog.show();
                     }

              }
               //点确定
              function selectClose(userName,userId) {
                   $("#exp_editor" + department_select.colDialogWebApp).val(userName);
                   $("#exp_editorID" + department_select.colDialogWebApp).val(userId);
                   //查询
                  $("#exp_lookMsg").click();
                   selectCancel();
               }
               //点取消
               function selectCancel() {
                   department_select.colDialog.close();
               }
                department_select.init();


               	//从url中获取参数值
               function getQueryString(name){
                    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
                    var r = window.location.search.substr(1).match(reg);
                    if(r != null)return unescape(r[2]);
                    return null;
               }

               // <!-- 话题选择 start -->
               var topic_stat = {
                topicDialog : null,
                init : function() {
                    var type=getQueryString("type");
                    $("#exp_column_topic").click(topic_stat.selectTopicListen);
                },

                winTmp : {},
                //点击选择话题按钮
                selectTopicListen: function(){
                    var _siteID=getQueryString("siteID");
                    //console.log(_siteID);
                    var dataUrl = "stat/SelectArticleTopic.jsp?siteID=" + _siteID;
                    //dataUrl = e5_form.dealUrl(dataUrl);
                    // 顶点位置
                    // var pos = {left : "100px",top : "50px",width : "1000px",height : "500px"};
                    topic_stat.winTmp = e5.dialog({
                        type : "iframe",
                        value : dataUrl
                    }, {
                        showTitle : true,
                        title : "话题选择",
                        showClose : true,
                        // lock:false,
                        // showTitle : false,
                        width : "600px",
                        height : "480px",
                        resizable : false
                    });
                    topic_stat.winTmp.show();
                },
                closeTopic : function(topic, topicID) {
                   $("#exp_column_topic").val(topic);
                   $("#exp_columnID_topic").val(topicID);
                   var rule=$("#articleTopic").attr("rule");
                   //console.log(rule);
                   rule=rule.substring(0,31);
                   rule+="_AND_a_topicID_EQ_" + topicID;
                   //console.log(rule);
                   $("#articleTopic").attr("rule", rule);
                   //查询
                   $("#exp_lookMsg_topic").click();
                   topic_stat.winTmp.hide();
                },

                cancelTopic : function() {
                    topic_stat.winTmp.hide();
                }
               }
               //提供回调函数
               function topicSelectOK(topic, topicID){
                    topic_stat.closeTopic(topic, topicID);
               }

               function topicSelectCancel(){
                    topic_stat.cancelTopic();
               }
               topic_stat.init();
               // <!-- 话题选择 end -->
</script>





  
