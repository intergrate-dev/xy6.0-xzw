<%@include file="../../e5include/IncludeTag.jsp" %>
<%@page pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>
<i18n:bundle baseName="i18n.e5org" changeResponseLocale="false"/>
<html>
<head>
    <title><i18n:message key="org.user.form.list.title"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <link href="../script/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="../script/bootstrap-datetimepicker/css/datetimepicker.css" rel="stylesheet" media="screen">
    <link href="../../e5style/e5form-custom.css" rel="stylesheet" media="screen">
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
    <style>
        .tablecontent lable {
            cursor: default !important;
        }

        .custform-label {
            width: 100px;
            font-family: "microsoft yahei";
        }

        #btnFormSave {
            margin-left: 140px;
            margin-right: 20px;
            background-color: #00a0e6;
        }

        #btnFormSave, #btnFormCancel {
            font-family: "microsoft yahei";
            text-shadow: none;
            font-size: 12px;
            border-radius: 3px;
            width: 70px !important;
            border: none;
            color: #fff;
            line-height: 12px;
        }

        #btnFormCancel {
            background-color: #b1b1b1;
        }

        .tablecontent td {
            border-bottom: 1px solid #ddd;
        }

        .tablecontent td:last-child {
            border-bottom: none;
        }

        .tablecontent {
            width: 99%;
            margin: 0 auto;
            margin-top: 5px;
        }

        .tablecontent tr {
            border: 1px solid #ddd;
        }

        .tablecontent tr:last-child {
            border: none;
        }

        .table-condensed tr:last-child {
            border: 1px solid #ddd;
        }

        .table-condensed tr:first-child {
            border: none;
        }

        .table-condensed tbody tr:first-child {
            border: 1px solid #ddd;
        }

        #a_linkTitle {
            font-family: 'microsoft yahei';
            width: 400px;
            border: 1px solid #ddd;
            border-radius: 3px;
            padding-left: 10px;
        }

        .custform-from-wrap {
            font-family: "microsoft yahei";
        }

        .datetimepicker {
            padding: 0;
        }

        select {
            height: 25px;
            margin-top: 6px;
        }

        #editStyleBtn {
            margin-right: 20px;
            background-color: #00a0e6;
            font-family: "microsoft yahei";
            text-shadow: none;
            font-size: 12px;
            border-radius: 3px;
            border: none;
            color: #fff;
        }
    </style>

</head>
<body>
<iframe id="frmColumn" style="display:none;"></iframe>
<form name="caForm" id="caForm" action="../../xy/articleorder/updateArticleSetting.do" method="post">
    <input type="hidden" name="priorityDay" id="priorityDay"/>
    <input type="hidden" name="UUID" id="UUID" value="<c:out value="${UUID}"/>"/>
    <input type="hidden" name="DocIDs" id="DocIDs" value="<c:out value="${DocIDs}"/>"/>
    <input type="hidden" name="colID" id="colID" value="<c:out value="${colID}"/>"/>
    <input type="hidden" name="DocLibID" id="DocLibID" value="<c:out value="${DocLibID}"/>"/>
    <input type="hidden" name="siteID" id="siteID" value="<c:out value="${siteID}"/>"/>

    <table class="tablecontent">
        <tr>
            <td>
			<span id="SPAN_t_channel" class="custform-span"> 
				<label id="LABEL_t_channel" class="custform-label" style="">链接标题</label>			
			</span>
            </td>
            <td>
                <div class="custform-from-wrap">
                    <input class="validate[required]" id="a_linkTitle" name="linkTitle" value="<c:out value="${ a_linkTitle }"/>"/>
                    <span id="countFontSpan" style="display: none;">0/300</span>
                    <input type="button" id="editStyleBtn" value="编辑样式" style="display: none;" onclick="editStyleFn()"/>
                </div>
            </td>
        </tr>
        
        <tr>
            <td>
			<span class="custform-span">
				<label class="custform-label">稿件发布时间</label>
			</span>
            </td>
            <td>
                <div class="custform-from-wrap">
                    <label id="pubTimeLable"><c:out value="${a_pubDate}"/></label>
                    <input name="pubTime" type="hidden" value="<c:out value="${a_pubTime}"/>"/>
                </div>
            </td>
        </tr>
        <tr>
       		<td>
	       		<span class="custform-span">
					<label class="custform-label">置顶</label>
				</span>
       		</td>
       		<td>
                <div class="custform-from-wrap">
                	<input id="isTop" name="isTop" type="checkbox" value="1" <c:if test="${isTop == 1}">checked</c:if>>
                </div>
            </td>
       	</tr>
       	<tr <c:if test="${channel == 0}">style="display:none;"</c:if>>
            <td>
			<span class="custform-span">
				<label class="custform-label">固定显示位置</label>
			</span>
            </td>
            <td>
                <div class="custform-from-wrap">
                    <select id="a_position"  name="position" value="<c:out value='${a_position}'/>">
                        <option value="0"></option>
                        <option value="1">1</option>
                        <option value="2">2</option>
                        <option value="3">3</option>
                        <option value="4">4</option>
                        <option value="5">5</option>
                        <option value="6">6</option>
                        <option value="7">7</option>
                        <option value="8">8</option>
                        <option value="9">9</option>
                        <option value="10">10</option>
                        <option value="11">11</option>
                        <option value="12">12</option>
                        <option value="13">13</option>
                        <option value="14">14</option>
                        <option value="15">15</option>
                        <option value="16">16</option>
                        <option value="17">17</option>
                        <option value="18">18</option>
                        <option value="19">19</option>
                        <option value="20">20</option>
                    </select>
                    &nbsp;&nbsp;&nbsp;&nbsp;*列表上的显示位置（无查询条件时）。该位置若已有稿件，会被替换
                </div>
            </td>
        </tr>
        <tr>
            <td>
			<span class="custform-span">
				<label class="custform-label">优先级延长日期</label>
			</span>
            </td>
            <td>
                <div class="custform-from-wrap">
                    <div id="datetimepicker" class="datetimepicker datetimepicker-inline" data-link-field="priorityDay" data-link-format="yyyy-mm-dd">
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <td>
			<span class="custform-span">
				<label class="custform-label">优先级等级</label>
			</span>
            </td>
            <td>
                <div class="custform-from-wrap">
                    <select id="priority_level"  name="priorityLevel" value="<c:out value="${priority_level}"/>">
                        <option value="a">a</option>
                        <option value="b">b</option>
                        <option value="c">c</option>
                        <option value="d">d</option>
                        <option value="e">e</option>
                        <option value="f">f</option>
                        <option value="g">g</option>
                        <option value="h">h</option>
                        <option value="i">i</option>
                        <option value="j">j</option>
                    </select>
                    &nbsp;&nbsp;&nbsp;&nbsp;*【a】为最低等级，依次到【j】为最高等级
                </div>
            </td>
        </tr>
        <tr>
            <td style="width:50px;" class="ui-droppable" colspan="2" style="text-align: center; ">
				<span id="txtFormSave" class="ui-draggable" fieldtype="-1" fieldcode="insertsave"> 
				<!-- onclick="updateArticleSetting()"  -->
					<input id="btnFormSave" class="button btn" value="保存" type="button" onclick="submitFn()"/>
				</span>
                <span class="custform-aftertxt ui-draggable">&nbsp; </span>
                <input id="btnFormCancel" class="button btn" value="取消" type="button" onclick="operationFailure()"/>

            </td>
        </tr>
    </table>
</form>
</body>

<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>

<script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.js" charset="UTF-8"></script>
<script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>

<script type="text/javascript">
    var publishTime = "<c:out value="${a_pubDate}"/>";
    var priority = "<c:out value="${a_priority}"/>";
    var UUID = "<c:out value="${UUID}"/>";
    var DocLibID = "<c:out value="${DocLibID}"/>";
    var siteID = "<c:out value="${siteID}"/>";
    var DocIDs = "<c:out value="${DocIDs}"/>";
    var colID = "<c:out value="${colID}"/>";
    var priority_level = "<c:out value="${priority_level}"/>";
    var a_position = "<c:out value="${a_position}"/>";
    var doEditStyle = "<c:out value="${doEditStyle}"/>";

    $(function(){
        initLabel();

        titleKeyPressEvent();
        //初始化 select
        $("#priority_level").val(priority_level);
        $("#a_position").val(a_position);

        //日历上的当前时间默认指定为稿件发布时间 + 优先级中的领先天数
        var _initialDate = "";
        if(priority && priority != "" && priority != "null"){
            //获取延时天数
            var _day = priority.substring(0, priority.length - 1);
            //console.info("_day:"+_day);
            var _e = publishTime.split("-");
            //初始化时间
            _initialDate = _e[0] + "-" + _e[1] + "-" + ( parseInt(_e[2]) + parseInt(_day) );
        } else{
            priority = "0a";
            _initialDate = publishTime;
        }
        //优先级延长日期
        $("#priorityDay").val(_initialDate);
        //console.info("priority:" + priority + "| _initialDate:"+ _initialDate);
        //不允许选择早于发布时间的日期，不允许超过发布时间1年以上
        var _startDate = "";
        var _endDate = "";
        if(publishTime && publishTime != "" && publishTime != "null"){
            _startDate = publishTime;
            var _e = publishTime.split("-");
            var _year = parseInt(_e[0]) + 1;
            _endDate = _year + publishTime.substring(publishTime.indexOf("-"));
        }
        //console.info("startDate:"+_startDate+"|endDate:"+_endDate);
        //初始化日期
        $('#datetimepicker').datetimepicker({
            language: 'zh-CN',
            weekStart: 0,
            todayBtn: 1,
            autoclose: 1,
            todayHighlight: false,
            startView: 2,
            minView: 2,
            disabledDaysOfCurrentMonth: 1,
            forceParse: 0,
            daysOfWeekDisabled: [],
            format: 'yyyy-mm-dd',
            initialDate: _initialDate,
            startDate: _startDate,
            endDate: _endDate

        });

        //设置验证
        $("#caForm").validationEngine({
            autoPositionUpdate: true,
            promptPosition: "bottomLeft"
        });

        $("#a_linkTitle").trigger("blur");
        
        if($("#isTop").attr("checked")){
			$("#priority_level").attr("disabled",true) ;
        	$("#a_position").attr("disabled",true) ;
    		$('#datetimepicker').datetimepicker('setEndDate',_startDate);
        }
        $("#isTop").click(function(){
        	var isTop = $("#isTop").attr("checked") ;
        	if(isTop){
				$("#priority_level").attr("disabled",true) ;
        		$("#a_position").attr("disabled",true) ;
        		$('#datetimepicker').datetimepicker('setEndDate',_startDate);
        	}else{
				$("#priority_level").removeAttr("disabled");
         		$("#a_position").removeAttr("disabled");
        		$('#datetimepicker').datetimepicker('setEndDate',_endDate);
        	}
        }) ;
    });

    function titleKeyPressEvent(){
        $("#a_linkTitle").keyup(function(e){
            countCharFn($(this));
        });
        $("#a_linkTitle").blur(function(e){
            countCharFn($(this));
        });
    }

    function countCharFn($this){
        var _value = $this.val();
        var _length = _value.length;
        if(_length<=300){
       	 $("#countFontSpan").html(_length + "/300");
       }else{
       	$("#countFontSpan").html("*最多输入" + 300 + "个字").css("color","red");
			$("#a_linkTitle").val($("#a_linkTitle").val().substr(0, 300));
       }
    }

    function initLabel(){
        doEditStyle = doEditStyle === "true";
        if(doEditStyle){
            $("#countFontSpan").hide();
            $("#editStyleBtn").show();
        } else{
            $("#countFontSpan").show();
            $("#editStyleBtn").hide();
        }
    }


    /**
     *更新稿件设置 - 无用
     */
    function updateArticleSetting(){
        if($("#caForm").validationEngine("validate") && false){
            $.ajax({
                url: "../../xy/articleorder/updateArticleSetting.do",
                type: 'POST',
                data: {
                    "linkTitle": $("#a_linkTitle").val(),
                    "pubTime": $("#pubTimeLable").text(),
                    "priorityDay": $("#priorityDay").val(),
                    "priorityLevel": $("#priority_level").val(),
                    "UUID": UUID,
                    "DocIDs": DocIDs,
                    "colID": colID
                },
                dataType: 'html',
                success: function(msg, status){
                    //成功时，调用operationSuccess(),并且写日志（opinion）
                    if(status == "success"){
                        //operationSuccess("当前栏目稿件设置成功！");
                        var tool = parent.e5.mods["workspace.toolkit"];
                        tool.self.closeOpDialog("OK", 2);
                    } else{
                        alert("修改失败！");
                        operationFailure();
                    }
                },
                error: function(xhr, textStatus, errorThrown){
                    operationFailure();
                }
            });
        }

    }

    function submitFn(){
        if($("#caForm").validationEngine("validate")){
        	//disabled 属性 影响表单提交
        	$("#a_position").removeAttr("disabled");
            $("#caForm").submit();
        }
    }

    //操作失败了调用
    function operationFailure(){
        var url = "../../e5workspace/after.do?UUID=" + UUID;
        $("#frmColumn").attr("src", url);

    }
    var editDialog = null;

    function editStyleFn(){
        var dataUrl = "../article/editStyle.jsp?e_type=articlesetting";
        editDialog = e5.dialog({
            type: "iframe",
            value: dataUrl
        }, {
            title: "编辑样式",
            width: "650px",
            height: "300px",
            resizable: false,
            fixed: true
        });
        editDialog.show();
    }

    function getContent(){
        return $("#a_linkTitle").val();
    }

    function editClose(contents){
        $("#a_linkTitle").val(contents);
        editDialog.close();
    }

    function editCancel(){
        editDialog.close();
    }

</script>
</html>
