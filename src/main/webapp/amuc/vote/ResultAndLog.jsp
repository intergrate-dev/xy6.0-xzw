<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ page pageEncoding = "UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>投票结果详情</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.css"/>
<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jquery-ui/jquery-ui.custom.css"/>
<link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
<link type="text/css" rel="stylesheet" href="../css/form-custom.css"/>
<link type="text/css" rel="stylesheet" href="../script/lhgcalendar/lhgcalendar.bootstrap.css"/>
<link type="text/css" rel="stylesheet" href="../vote/css/bootstrap.min.css"/>
<link type="text/css" rel="stylesheet" href="../vote/css/bootstrap-datetimepicker.min.css"/>
<link type="text/css" rel="stylesheet" href="../vote/css/font-awesome.min.css"/>
<link href="../message/includes/appmsgeditor.css" rel="stylesheet" />
<link type="text/css" rel="stylesheet" href="../vote/css/jquery.alerts.css"/>
<link type="text/css" rel="stylesheet" href="../vote/css/voteoptions.css"/>
<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script> 
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script> 
<script type="text/javascript" src="../../amuc/script/lhgcalendar/lhgcalendar.js"></script>
<script type="text/javascript" src="../../e5script/e5.min.js"></script>
<script type="text/javascript" src="../../e5script/e5.utils.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
<%--<script type="text/javascript" src="../../e5script/jquery/jquery-ui/jquery.ui.core.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jquery-ui/jquery.ui.widget.js"></script> 
<script type="text/javascript" src="../../e5script/jquery/jquery-ui/jquery.ui.button.js"></script>--%>
<script type="text/javascript" src="../script/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../vote/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="../vote/js/bootstrap-datetimepicker.zh-CN.js"></script>
<style type="text/css">
body{
	padding:10px;
}
.opoutbar{float:left;width:85%;background-color:#F7F7F7;margin-bottom:20px;margin-top:0px;position: relative;}
.opoutbar .opshownum{
	background-color: #FFFFFF;
    border: 1px solid #FFFFFF;
    border-radius: 17px;
    font-size: 10px;
    line-height: 15px;
    padding: 0 5px;
    position: absolute;
    right: 5px;
    top: 2px;
    color:#666;
}
.wapwrap .opoutbar{width:70%;}
.opbar{float:left;height:20px;}
.oppi{float:left;height:20px;line-height:20px;margin-left:10px;}	
</style>
</head> 
<body>
	<input type="hidden" id="docID" value="${docID }">
	<input type="hidden" id="DocLibID" value="${DocLibID }">
	<div class="span10">
		<button class="btn btn-small" disabled>已为公布结果</button>
		&nbsp;&nbsp;&nbsp;
		<!-- <a href="" onclick="export1();" target="_blank" class='btn'><i class= 'icon-download-alt'></i> 导出投票结果</a> -->
		<a href="" onclick="exportEveOptUser();" target="_blank" class='btn'><i class= 'icon-download-alt'></i> 导出投票详情</a>
		&nbsp;&nbsp;&nbsp;
		<a href="" onclick="exportUserMsg();" target="_blank" class='btn'><i class= 'icon-download-alt'></i> 导出收集用户信息</a>
		<p class="text-warning">所有被统计到的投票计数结果，共用对外IP的局域网用户的投票会被正常统计</p>
		<!-- 实名用户才显示此项。0：实名，1：匿名-->
		<c:if test="${vsVoteType == 0}">
		<div style="height:86px;">
			<p class="text-info">按时间导出此时间段内参与过投票的用户信息</p>
        	<!-- <label for="dtp_input2" style="float:left;width:100px;height:33px;line-height:33px;margin:0;">时间</label> -->
        	<div class="input-group date form_date" style="float:left;width:230px;" data-date="" data-date-format="" data-link-field="dtp_start" data-link-format="yyyy-mm-dd">
        		<input class="form-control" size="16" type="text" value="" readonly>
        		<!-- <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span> -->
				<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
				&nbsp;&nbsp;至&nbsp;&nbsp;
        	</div>
			<input type="hidden" id="dtp_start" value="" />
			<div class="input-group date form_date" style="float:left;width:200px;" data-date="" data-date-format="" data-link-field="dtp_end" data-link-format="yyyy-mm-dd">
        		<input class="form-control" size="16" type="text" value="" readonly>
        		<!-- <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span> -->
				<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
        	</div>
			<input type="hidden" id="dtp_end" value="" />
			&nbsp;&nbsp;&nbsp;<a id="eptUserByTime" href="" onclick="exportUserByTime()" class='btn'><i class='icon-download-alt'></i> 导出</a>
			<!-- &nbsp;&nbsp;&nbsp;<button onclick="exportUserByTime()" type="button" class="btn" data-toggle="modal" data-target="#myModal">导出</button> -->
        </div>
        </c:if>
		<p class="text-info">请知晓：正常排序是按照选项ID来进行排序，升序和降序为按照投票数量来进行排序</p>
		<button type="button" class="btn btn-default" id="normalNum">正常排序</button>
		<button type="button" class="btn btn-default" id="upNum">升序</button>
		<button type="button" class="btn btn-default" id="downNum">降序</button>
		<p class="text-success">浏览人数为${not empty accesscount?accesscount:"0"}人，参与人数为${not empty peoplecount?peoplecount:"0"}人，总投票数为${not empty votecount?votecount:"0"}票</p>
		<table cellspacing="0" cellpadding="0" width='100%' class="votetable">
			<c:forEach items="${opList }" var="item" varStatus="i">
				<tr>
					<td class="">
						<div class="optext">
							（第${item.voteOpId}项）&nbsp;${item.voName}
						</div>
			
						<div class="optext editbtns"  style="margin-bottom:2px;">
							<span class="label label-warning"></span>
							<a href='#' class='btn btn-small btn-danger closeoption' name='-1'  id='1379149'
							   title='作废1号选项' onclick="deleteOpt(${item.voteOpId}, ${item.voThemeId}, this);">废除此选项</a>
							<c:if test="${item.deleteFlag==0}">
								<a href='#' class='btn btn-small btn-danger closeoption' name='-3'  id='1379150'
								   title='暂停1号选项' onclick="purseOrReact(${item.voteOpId}, ${item.voThemeId}, this);" _flag="1">暂停此选项</a>
							</c:if>
							<c:if test="${item.deleteFlag==1}">
								<a href='#' class='btn btn-small btn-danger closeoption' name='-3'  id='1379150'
								   title='暂停1号选项' onclick="purseOrReact(${item.voteOpId}, ${item.voThemeId}, this);" _flag="0">启用此选项</a>
							</c:if>
							<!-- <a class='btn btn-small btn-info disabled' name='0' data-trigger="modal"  href="" data-title="需升级为专业版"  title=''>导出投票日志</a> -->
						</div>
						<div class="opoutbar">
							<div id="opbar_1" style="width:100%;background-color:#996600;" class="opbar"></div>
							<div class="opshownum">${item.voVotes }票</div>
						</div>
						<div id="oppi_${i.index+1}" style="color:#996600;" class="oppi">
						<script type="text/javascript">
							var voVotes = "${item.voVotes }", voCount = "${votecount }", percent, docID = '${docID}';
							if(isZeroVal(voVotes) || isZeroVal(voCount)){
							    percent = 0;
							} else {
							    //percent = Math.round((voVotes/ voCount * 10000)/100.00 + "%");
							    percent = Math.round((Number(voVotes)/Number(voCount))*1000.00)/10 + "%";
                            }
							$("#oppi_"+${i.index+1}).html(percent);

							function isZeroVal(val) {
								if(Number(val) == 0 || isNaN(Number(val))){
								    return true;
								}
								return false;
                            }

                            //暂停/启用
                            function purseOrReact(optId, themeId, e) {
							    isSet = true;
								var flag = $(e)[0].attributes._flag.value,
									text = flag == '1' ? '启用此选项' : '暂停此选项';
                                $.ajax({
                                    url : '/amuc/voteOption/deleteVoteOption.do',
                                    type : 'POST',
                                    async : true,
                                    data : {
                                        "voteID": docID,
                                        "opid" : optId,
                                        "themeid" : themeId,
										"operate" : flag
                                    },
                                    success : function(data) {
                                        data = JSON.parse(data);
                                        if(data.ret == 1) {
											/*$(e).text(text);
											$(e).attr('_flag', flag^1);*/
                                            location.reload();
                                            // console.log('delete success');
                                        } else{
                                            alert(data.errormsg);
										}
                                    },
                                    error : function(xhr) {
                                        alert('请求失败');
                                    }
                                });
                            }

                            //删除
                            function deleteOpt(optId, themeId) {
                                isSet = true;
                                if (!confirm("确定要删除该投票选项吗？")) return;
                                $.ajax({
                                    url : '/amuc/voteOption/removeVoteOption.do',
                                    type : 'POST',
                                    async : true,
                                    data : {
                                        "voteID": docID,
                                        "opid" : optId,
                                        "themeid" : themeId,
                                    },
                                    success : function(data) {
                                        data = JSON.parse(data);
                                        if(data.ret == 1) {
                                            location.reload();
                                            // console.log('delete success');
                                        } else{
                                            alert(data.errormsg);
                                        }
                                    },
                                    error : function(xhr) {
                                        alert('请求失败5555');
                                    }
                                });
                            }
						</script>
						</div>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>
	<input type="hidden" id="sorttype" value="${sorttype }" />
	<!-- Modal -->
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title" id="myModalLabel">请选择需要导出的用户信息</h4>
	      		</div>
				<div id="modal-body" class="modal-body">
					
				</div>
				<div class="modal-footer">
					 <button type="button" class="btn btn-primary">确定并导出</button>
	        		<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	      		</div>
	    	</div>
		</div>
	</div>
</body>
<script type="text/javascript">
//导出投票选项的结果
/* function export1(){
	var docID= $("#docID").val();
	window.location.href="ResultAndLog.do?action=export&docID="+docID;
} */
/* function getUserInfo(){  //获取用户信息并放入摸态框中
} */

var voteID, isSet;

window.onload = function () {
	voteID = '${docID }'
}

window.onbeforeunload = function() {
    if(!isSet) return;
    //投票选项--刷新缓存
	$.ajax({
		url : '/amuc/voteOption/refreshVoteCache.do',
		type : 'POST',
		async : true,
		data : {
			"voteID" : voteID
		},
		success : function(data) {

		},
		error : function(xhr) {
			//alert('请求失败');
		}
	});
}


$("#downNum").click(function(){
	var docID= $("#docID").val();
	var DocLibID = $("#DocLibID").val();
	window.location.href = "look.do?action=look&DocIDs="+docID+"&DocLibID="+DocLibID+"&sorttype=desc";
});
$("#normalNum").click(function(){
	var docID= $("#docID").val();
	var DocLibID = $("#DocLibID").val();
	window.location.href = "look.do?action=look&DocIDs="+docID+"&DocLibID="+DocLibID;
});
$("#upNum").click(function(){
	var docID= $("#docID").val();
	var DocLibID = $("#DocLibID").val();
	window.location.href = "look.do?action=look&DocIDs="+docID+"&DocLibID="+DocLibID+"&sorttype=asc";
});
//导出选项的投票详情
function exportEveOptUser(){
	var docID= $("#docID").val();
	var DocLibID = $("#DocLibID").val();
	var sorttype = $("#sorttype").val();
	window.location.href="exportEveOptUser.do?action=exportEveOptUser&docID="+docID+"&DocLibID="+DocLibID+"&sorttype="+sorttype;
}
//导出收集用户信息
function exportUserMsg(){
	var docID= $("#docID").val();
	var DocLibID = $("#DocLibID").val();
	window.location.href="exportUserMsg.do?action=exportUserMsg&docID="+docID+"&DocLibID="+DocLibID;
}
//根据时间来导出这段时间参与投票的用户
$('.form_date').datetimepicker({
    language: 'zh-CN',
    weekStart: 1,
    todayBtn: 1,
	autoclose: 1,
	todayHighlight: 1,
	startView: 2,
	minView: 2,
	forceParse: 0,
	pickerPosition: "bottom-left"
});
function exportUserByTime(){
	var dtp_start = $("#dtp_start").val();
	var dtp_end = $("#dtp_end").val();
	if(dtp_start == ""){
		alert("开始时间不能为空");
		return false;
	}else if(dtp_end == ""){
		alert("截止时间不能为空");
		return false;
	}else if(dtp_start > dtp_end){
		alert("截止时间不能小于开始时间");
		return false;
	}
	$('#eptUserByTime').attr("target","_blank");
	var docID= $("#docID").val();
	window.location.href="exportUserByTime.do?action=exportUserByTime&docID="+docID+"&dtp_start="+dtp_start+"&dtp_end="+dtp_end;
}
</script>