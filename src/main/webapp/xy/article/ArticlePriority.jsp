<%@include file="../../e5include/IncludeTag.jsp" %>
<%@page language="java" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html>
<head>
    <title>优先级</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="../../e5script/jquery/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../../xy/article/script/ArticlePriority.js"></script>
    <link href="../script/bootstrap-3.3.4/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="../script/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet" media="screen">
    <link href="../../e5style/e5form-custom.css" rel="stylesheet" media="screen">
    <style>
    	.linkTitle{
            white-space:nowrap;
			overflow:hidden;
			text-overflow:ellipsis;	
    	}
        .custform-label {
			width: 76%;
            font-family: "microsoft yahei";
            text-align: left;
        }
        .custform-label-cue {
            width: 100%;
            font-family: "microsoft yahei";
            color:#CCCCCC;
        }
        #btnSave {
            margin-left: 120px;
            margin-right: 20px;
            background-color: #00a0e6;
        }
        #btnSave, #btnCancel {
            font-family: "microsoft yahei";
            text-shadow: none;
            font-size: 12px;
            border-radius: 3px;
            width: 70px !important;
            border: none;
            color: #fff;
            line-height: 12px;
        }
        #btnCancel {
            background-color: #b1b1b1;
        }
        .tablecontent {
            width: 80%;
            margin: 0 auto;
            margin-top: 5px;
            margin-left : 20px ;
           	border-bottom: none;
           	table-layout:fixed;
        }
        .tablecontent div {
            margin-left: -40px;
        }
        .tablecontent td {
            border-bottom: none;
        }
        .tablecontent tr {
            border-bottom: none;
        }
		.tablecontent tr:first-child {
            border-bottom: 1px solid #ddd;
        }		
		.tablecontent lable {
            cursor: default !important;
        }
        .tablecontent input{
        	border-radius:3px; 
    		border: 1px solid #ccc;
    		padding-left: 10px;
    		margin-bottom:-2px;
    		width:150px;
    		height:25px;
        }
        .tablecontent select {
		    display: block;
		    width: 150px;
		    height: 30px;
		    padding: 6px 12px;
		    font-size: 14px;
		    line-height: 1.42857143;
		    color: #555;
		    background-color: #fff;
		    background-image: none;
		    border: 1px solid #ccc;
		    border-radius: 4px;
		}
        .custform-from-wrap {
            font-family: "microsoft yahei";
        }
        .priorityDay {
            padding: 0;
        }
		.tablecontent tr td:nth-of-type(1){
			position: relative;
			left:6px;
		}
		.tablecontent tr td:nth-of-type(2){
			position: relative;
			left:-20px;
		}
		.tablecontent tr:nth-of-type(5) td{
			position: relative;
			left:-20px;
		}
	<%--新加代码--%>
	input[type=range]::-webkit-slider-runnable-track {
	height: 15px;
	border-radius: 10px; /*将轨道设为圆角的*/
	box-shadow: 0 1px 1px #def3f8, inset 0 .125em .125em #0d1112; /*轨道内置阴影效果*/
	}

	input[type=range]:focus {
	outline: none;
	}

	input[type=range]::-webkit-slider-thumb {
	-webkit-appearance: none;
	height: 25px;
	width: 25px;
	margin-top: -5px; /*使滑块超出轨道部分的偏移量相等*/
	background: #ffffff;
	border-radius: 50%; /*外观设置为圆形*/
	border: solid 0.125em rgba(205, 224, 230, 0.5); /*设置边框*/
	box-shadow: 0 .125em .125em #3b4547; /*添加底部阴影*/
	}

	input[type=range]::-moz-range-progress {
	background: linear-gradient(to right, #059CFA, white 100%, white);
	height: 13px;
	border-radius: 10px;
	}

	input[type=range] {
	-webkit-appearance: none;
	width: 150px;
	border-radius: 10px;
	/*新增*/
	background: -webkit-linear-gradient(#059CFA, #059CFA) no-repeat;
	background-size: 0% 100%;
	}

	input[type=range]::-ms-track {
	height: 25px;
	border-radius: 10px;
	box-shadow: 0 1px 1px #def3f8, inset 0 .125em .125em #0d1112;
	border-color: transparent; /*去除原有边框*/
	color: transparent; /*去除轨道内的竖线*/
	}

	input[type=range]::-ms-thumb {
	border: solid 0.125em rgba(205, 224, 230, 0.5);
	height: 25px;
	width: 25px;
	border-radius: 50%;
	background: #ffffff;
	margin-top: -5px;
	box-shadow: 0 .125em .125em #3b4547;
	}

	input[type=range]::-ms-fill-lower {
	/*进度条已填充的部分*/
	height: 22px;
	border-radius: 10px;
	background: linear-gradient(#059CFA, #059CFA) no-repeat;
	}

	input[type=range]::-ms-fill-upper {
	/*进度条未填充的部分*/
	height: 22px;
	border-radius: 10px;
	background: #ffffff;
	}

	input[type=range]:focus::-ms-fill-lower {
	background: linear-gradient(#059CFA, #059CFA) no-repeat;
	}

	input[type=range]:focus::-ms-fill-upper {
	background: #ffffff;
	}
    </style>

</head>
<body>
<iframe id="frmColumn" style="display:none;"></iframe>
<form name="caForm" id="caForm" action="/xy/articleorder/updateArticlePriority.do" method="post">
	<input type="hidden" name="siteID" id="siteID" value="<c:out value="${siteID}"/>"/>
    <input type="hidden" name="UUID" id="UUID" value="<c:out value="${UUID}"/>"/>
    <input type="hidden" name="docLibID" id="docLibID" value="<c:out value="${docLibID}"/>"/>
    <input type="hidden" name="colID" id="colID" value="<c:out value="${colID}"/>"/>
    <input type="hidden" name="docID" id="docID" value="<c:out value="${docID}"/>"/>
	<input type="hidden" name="priority" id="priority" value="<c:out value="${priority}"/>"/>
	
	<table class="tablecontent">
		<tr>
			<td colspan="2">
				<span class="custform-span">
					<label id="linkTitle" title="" class="custform-label linkTitle">${linkTitle}</label>
				</span>
			</td>
		</tr>
		<c:if test="${status == 1}">
			<tr>
			<td colspan="2">
				<span class="custform-span">
					<label class="custform-label-cue">稿件已置顶</label>
				</span>
			</td>
		</tr>
		</c:if>
		<c:if test="${status == 2}">
			<tr>
			<td colspan="2">
				<span class="custform-span">
					<label class="custform-label-cue">稿件已经设置固定位置</label>
				</span>
			</td>
		</tr>
		</c:if>
		<c:if test="${status == 0}">
			<tr>
			<td width="120px">
				<span class="custform-span">
					<label class="custform-label" >稿件发布时间</label>
				</span>
			</td>
			<td>
				<div>
					<label id="pubTimeLable">${pubDate}</label>
                    <input name="pubTime" type="hidden" value="${pubTime}"/>
				</div>
			</td>
		</tr>
		<tr>
			<td width="120px">
				<span class="custform-span">
					<label class="custform-label">延长至日期</label>
				</span>
			</td>
			<td>
				<div>
                    <input class="form-control" type="text" id="priorityDay" name="priorityDay" size="16"  value="" readonly>
                </div>
			</td>
		</tr>
		<tr>
			<td width="120px">
				<span class="custform-span">
					<label class="custform-label">优先级</label>
				</span>
			</td>
			<td>
				<div>
					<%--<select id="priorityLevel" name="priorityLevel">--%>
					  	<%--<option value="e" ${priority_level=='e'?'selected':''}>100</option>--%>
                        <%--<option value="d" ${priority_level=='d'?'selected':''}>80</option>--%>
                        <%--<option value="c" ${priority_level=='c'?'selected':''}>60</option>--%>
                        <%--<option value="b" ${priority_level=='b'?'selected':''}>40</option>--%>
                        <%--<option value="a" ${priority_level=='a'?'selected':''}>20</option>--%>
					<%--</select>--%>

					<%--<input type="range" defaultValue=${priority_level == '' ? 65 : priority_level.toUpperCase().charCodeAt()} id="trackBar" min="1" max="10" step="1" value="1" style="border: 0px;padding: 0px;margin-top: 13px;"/>--%>

					<%--<input type="text" value= ${priority_level == '' ? 'A' : priority_level.toUpperCase()} name="priorityLevel" id="show" style="width: 50px;height: 20px;line-height: 20px;/* text-align: center; */position: relative;left: 155px;top: -18px;text-indent: 10px;"">--%>


					<input type="range"  id="trackBar" min="65" max="74" step="1" style="border: 0px;padding: 0px;margin-top: 13px;"/>
					<input type="text" readonly name="priorityLevel" id="show" style="width: 50px;height: 20px;line-height: 20px;/* text-align: center; */position: relative;left: 160px;top: -18px;text-indent: 10px;"">
					<span style="position: relative;left: -55px;">最低</span><span style="position: relative;left: 52px;">最高</span>
				</div>
			</td>
		</tr>
		</c:if>
		<tr>
			<td style="width:50px;" class="ui-droppable" colspan="2" style="text-align: center; ">
				<c:if test="${status == 0}">
				<span id="txSave" class="ui-draggable" fieldtype="-1" fieldcode="insertsave"> 
					<input id="btnSave" class="button btn" value="保存" type="button"/>
				</span>
				</c:if>
				<span class="custform-aftertxt ui-draggable">&nbsp; </span>
				<input id="btnCancel" class="button btn" value="取消" type="button"/>
			</td>
		</tr>
	</table>
</form>
</body>
</html>
<script>


	$('#show').val('${priority_level}'.toLowerCase());


	//初始化trackBar



	$('#trackBar').val('${priority_level}'.toUpperCase().charCodeAt())

	$('#trackBar').on("change",function(){

		console.log($('#trackBar').val())
		$('#show').val(String.fromCharCode($('#trackBar').val()).toLowerCase());
	})


	//其他功能处理代码，比如图片浏览器的跳转  });

</script>