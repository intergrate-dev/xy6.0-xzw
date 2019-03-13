<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
<script type="text/javascript" src="../script/bootstrap/js/bootstrap.js"></script>
<script type="text/javascript" src="../script/bootstrap-table/bootstrap-table.js"></script>
<script src="../script/bootstrap-table/bootstrap-table-zh-CN.js"></script>

<link type="text/css" rel="stylesheet" href="../script/bootstrap-table/bootstrap-table.css" />
<link type="text/css" rel="stylesheet" href="../script/bootstrap/css/bootstrap.css" />
<link type="text/css" rel="stylesheet" href="./css/XInviteCodeAnalyze.css" />

<!-- ECharts单文件引入 -->
<script type="text/javascript" src="../script/ECharts/js/esl.js"></script>

<script src="./js/XInviteCodeAnalyze.js"></script>
</head>
<body style="background-color:#e7e8eb">
	<iframe id="iframe" name="iframe" style="display: none;"></iframe>
	<!-- header -->
	<div class="head_box">
	    <div class="wrp">
		</div>
	</div>
	<!-- body  -->
	<div id="body" class="body page_index">
		<div id="js_container_box" class="container_box cell_layout side_1">
			<div class="col_top">
				<div class="inner group">本页根据当前数据实时计算
					<div class="tip"> 
    				 <div class="help_content" style="display: none;">
		             	<i class="dropdown_arrow out"></i>
		             	<i class="dropdown_arrow in"></i>
		             	<dl class="help-change-list" id="pop_items_info">
		                	<dt>指标量</dt>
		                    <dd>该量化单位邀请码分配的使用次数</dd>
		                    <dt>使用量</dt>
		                    <dd>该量化单位邀请码实际推广使用次数</dd>
		                    <dt></dt>
		                </dl>
		             </div>
    			</div>
				</div>
				<br/>
				<div class="form-inline" style="float:left;margin-right:20px;">
					<span>第一层级：
						<select id='level1' class="form-control">
							<option value="all">全部</option>
						</select>
					</span>
					<span class="levelLef">第二层级：
						<select id='level2' class="form-control">
						</select>
					</span>
					<span class="levelLef">第三层级：
						<select id='level3' class="form-control">
						</select>
					</span>
				</div>
				<div>
				 	<input type="button" class="btn" id="btn" value="查询" style="height:30px"/>
				</div>
			</div>	
			<div class="col_main">
				<!-- 列表 -->
				<!-- <table id="table"></table> -->
				<!--echarts-->
				<div id="chart" style="height:400px"></div>   
			</div>
		</div>
	</div>
	<!-- footer -->
	<div class="foot" id="footer">
		<ul class="links ft">
			<li class="links_item no_extra"><a href="" target="_blank"></a></li>
			<li class="links_item"><p class="copyright">Copyright © 2012-2017 . All Rights Reserved.</p> </li>
		</ul>
	</div>
</body>
</html>