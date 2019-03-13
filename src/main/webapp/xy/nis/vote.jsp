<%@include file="../../e5include/IncludeTag.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  User: guzm
  Date: 2015/8/3
  Time: 14:05
  Description: 投票
--%>

<html>
<head>
    <title>投票</title>
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css"/>
    <link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
    <!-- bootstrap -->
    <link type="text/css" rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
    <!-- 上传组件 -->
    <link type="text/css" rel="stylesheet" href="../../e5script/jquery/uploadify/uploadify.orignal.css">
    <!-- 日期组件 -->
    <link type="text/css" rel="stylesheet" href="../script/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css" media="screen">
    <link type="text/css" rel="stylesheet" href="./css/vote.css">

  <script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
  <script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
  <script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>

    <!-- 上传组件 -->
  <script type="text/javascript" src="../../e5script/jquery/uploadify/jquery.uploadify-3.2.min.js"></script>
    <!-- 日期组件 -->
  <script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
  <script type="text/javascript" src="../script/bootstrap-datetimepicker/bootstrap-datetimepicker.min.js" charset="UTF-8"></script>
  <script type="text/javascript" src="../script/bootstrap-datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
    <!-- 投票 js -->
  <script type="text/javascript" src="./js/vote.js"></script>
  <script type="text/javascript">
    var vote_info = {
      UUID: '<c:out value="${UUID}"/>',
      DocLibID: '<c:out value="${DocLibID}"/>',
      DocID: '<c:out value="${DocID}"/>',
      siteID: '<c:out value="${siteID}"/>',
      groupID: '<c:out value="${groupID}"/>',
      vote: {
        docID: '<c:out value="${vote.docID}"/>',
        vote_siteID: '<c:out value="${vote.vote_siteID}"/>',
        vote_groupID: '<c:out value="${vote.vote_groupID}"/>',
        vote_topic: '<c:out value="${vote.vote_topic}"/>',
        vote_type: '<c:out value="${vote.vote_type}"/>',
        vote_selectLimited: '<c:out value="${vote.vote_selectLimited}"/>',
        vote_cycle: '<c:out value="${vote.vote_cycle}"/>',
        vote_cyclenum: '<c:out value="${vote.vote_cyclenum}"/>',
        vote_needlogin: '<c:out value="${vote.vote_needlogin}"/>',
        vote_onlyapp: '<c:out value="${vote.vote_onlyapp}"/>',
        vote_endDate: '<c:out value="${vote.vote_endDate}"/>'
      },
      voteOptionArray : '<c:out value="${voteOptionArray}" escapeXml="false"/>',
      isNew : '<c:out value="${isNew}" escapeXml="false"/>',
      sessionId:'<c:out value="${pageContext.session.id}" escapeXml="false"/>'
    };
  </script>

</head>
<body>
<!-- 主容器 -->
<div class="container-fluid">
  <form name="voteForm" id="voteForm" method="post" action="../../xy/nis/addvote.do">
        <input type="hidden" name="UUID" id="UUID" value="<c:out value="${UUID}"/>"/>
        <input type="hidden" name="DocLibID" id="DocLibID" value="<c:out value="${DocLibID}"/>"/>
        <input type="hidden" name="DocID" id="DocID" value="<c:out value="${DocID}"/>"/>
        <input type="hidden" name="vote_siteID" id="vote_siteID" value="<c:out value="${siteID}"/>"/>
        <input type="hidden" name="vote_groupID" id="vote_groupID" value="<c:out value="${groupID}"/>"/>
        <input type="hidden" name="isNew" id="isNew" value="<c:out value="${isNew}" escapeXml="false"/>"/>
        <input type="hidden" name="lastQuestionNo" id="lastQuestionNo"/>
        <input type="hidden" name="questionNo" id="questionNo"/>
        <input type="hidden" name="vote_cyclenum" id="vote_cyclenum"/>
        <input type="hidden" name="deleteOptionIds" id="deleteOptionIds"/>
        <input type="hidden" name="deleteQuestionIds" id="deleteQuestionIds"/>

        <div class="form-horizontal" style="margin-top: 10px;">
            <!-- 投票标题 -->
            <div class="form-group">
                <label for="vote_topic" class="col-sm-2 control-label">投票名称</label>

                <div class="col-sm-10 padleft">
                    <textarea name="vote_topic" id="vote_topic" class="form-control validate[checkMaxInput[255],required]" rows="2"><c:out value="${vote.vote_topic}"/></textarea>
                </div>
            </div>
            <!-- END 投票标题 -->

            <!-- 截止日期 -->
            <div class="form-group">
                <label for="vote_endDate" class="col-sm-2 control-label">截止时间</label>

                <div class="col-sm-10 padleft">
                    <div id="dateControlDiv" style="width: 300px;" class="input-group date form_datetime col-md-5" data-date-format="yyyy-mm-dd hh:ii">
                        <input name="vote_endDate" id="vote_endDate" class="form-control padleft" size="16" type="text" value="<c:out value="${vote.vote_endDate}"/>" readonly>
                        <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
                        <span class="input-group-addon"><span class="glyphicon glyphicon-th"></span></span>
                    </div>
                </div>
            </div>
            <!-- END 截止日期 -->

            <!-- 投票周期 start-->
            <div class="form-group">
              <label class="col-sm-2 control-label">投票周期</label>
              <div class="col-sm-10  padleft">
                <label class="radio-inline">
                  <input type="radio" id="vote_period_once" name="vote_cycle" value="0" value="<c:out value="${vote.vote_cycle}"/>" <c:if test="${vote!=null&&vote.vote_cycle==0}">checked</c:if>>只允许投一次
                </label>
                <label class="radio-inline">
                  <input type="radio" id="vote_period_once_a_day" name="vote_cycle" value="1" <c:if test="${vote!=null&&vote.vote_cycle==1}">checked="checked"</c:if>>每天一次
                </label>
                <label class="radio-inline">
                  <input type="radio" id="vote_period_num_a_day" name="vote_cycle" value="2" <c:if test="${vote!=null&&vote.vote_cycle==2}">checked="checked"</c:if>>每天
                  <input type="text" id="vote_period_num" class="vote_period_num vote-input" onkeypress="return event.keyCode>=48&&event.keyCode<=57" value="<c:out value="${vote.vote_cyclenum}"/>">次
                </label>
                <label class="radio-inline">
                  <input type="radio" id="vote_period_unlimited" name="vote_cycle" value="3" <c:if test="${vote!=null&&vote.vote_cycle==3}">checked="checked"</c:if>>不限
                </label>
              </div>
            </div>
            <!-- 投票周期 end-->

            <!-- 是否需要登录 start-->
            <div class="form-group">
              <label class="col-sm-2 control-label">需要登录</label>
              <div class="col-sm-10  padleft">
                <label class="radio-inline">
                  <input type="radio" id="vote_with_login" name="vote_needlogin" value="1" <c:if test="${vote!=null&&vote.vote_needlogin==1}">checked="checked"</c:if>>是
                </label>
                <label class="radio-inline">
                  <input type="radio" id="vote_without_login" name="vote_needlogin" value="0" <c:if test="${vote!=null&&vote.vote_needlogin==0}">checked="checked"</c:if>>否
                </label>
              </div>
            </div>
            <!-- 是否需要登录 end-->

            <!-- 仅在APP内投票 start-->
            <div class="form-group">
              <label class="col-sm-2 control-label">仅在APP内投票</label>
              <div class="col-sm-10  padleft">
                <label class="radio-inline">
                  <input type="radio" id="vote_app_only" name="vote_onlyapp" value="1" <c:if test="${vote!=null&&vote.vote_onlyapp=='1'}">checked="checked"</c:if>>是
                </label>
                <label class="radio-inline">
                  <input type="radio" id="vote_all" name="vote_onlyapp" value="0" <c:if test="${vote!=null&&vote.vote_onlyapp=='0'}">checked="checked"</c:if>>否
                </label>
              </div>
            </div>
            <!-- 仅在APP内投票 end-->

            <!-- 问题 start-->
            <div class="form-group">
              <!--添加的问题列表：展开展示：正在修改或者正在编辑的-->
              <div id="questionList" class="question-container" data-num="0"></div>
              <!--添加问题按钮-->
              <div class="question-border" id="addVoteQuestion" onclick="nis_vote.addQuestion()">+ 添加问题</div>
            </div>
            <!-- 问题 end-->

            <!-- 确定 与 取消按钮 -->
            <div class="form-group col-sm-2 btnwidth">
                <button type="button" class="btn btn-info control-label dosave" onclick="nis_vote.doSave(this);">确定</button>
            </div>
            <div class="form-group col-sm-2">
                <button type="button" class="btn btn-default control-label docancle" onclick="nis_vote.doCancel(this);">取消
                </button>
            </div>
            <!-- END 确定 与 取消按钮 -->
        </div>
    </form>
</div>

<!-- END 主容器 -->

<!-- 选项的代码块 - 每添加一个选项就从这个模块中copy过去 -->
<div id="backupDiv" style="display:none;">
    <div class="form-inline div-border option-item">
        <div class="divinfo">
            <input id="docID_" type="hidden" value=""/>
            <!-- 选项名称 -->
            <div class="form-group">
                <label class="custform-label-require label-left">选项<span id="No_Span_"></span></label>
                <input style="padding: 6px 8px;" id="vote_option_" maxlength="255" type="text" class="form-control validate[required,checkMaxInput[255]]" placeholder="选项名称">
            </div>
            <!-- END 选项名称 -->

            <!-- 初始票数 -->
            <div class="form-group" style="margin: 0 40px;">
                <label class="label-left">初始票数</label>
                <input id="vote_countInitial_" class="form-control validate[number]" onKeyUp="this.value=this.value.replace(/[^\.\d]/g,'');if(this.value.split('.').length>2){this.value=this.value.split('.')[0]+'.'+this.value.split('.')[1]}" style="width: 60px!important;" placeholder="">
            </div>
            <!-- END 初始票数 -->

            <!-- 上传按钮 -->
            <div style="display: inline-block;">
                <div id="uploadFile_"></div>
            </div>
            <!-- END 上传按钮 -->

            <!-- 显示图片 -->
            <div class="form-inline">
                <div id="uploadFileQueue_" class="thumbnail" style="width: 160px;display: none;">
	            	<span class="wrapimg">
		            	<img id="thumbImg_" src="">
		                <span class="glyphicon glyphicon-remove closes"></span>
	                </span>
                    <input id="vote_picUrl_" type="hidden"/>
                </div>
            </div>
            <!-- END 显示图片 -->
        </div>
        <div style="display: inline-block;">
            <!-- 删除按钮 -->
            <div class="form-group">
                <button id="deleteBtn_" type="button" class="delete" onclick="nis_vote.deleteBtnListener(this)">
                    删除
                </button>
            </div>
            <!-- END 删除按钮 -->
        </div>
    </div>

</div>
<!-- END 选项的代码块 - 每添加一个选项就从这个模块中copy过去 -->

<div id="questionCode" style="display: none;">
  <div class="question-item question-border">
    <input id="vote_question_docID_" name="docID_" type="hidden" value=""/>
    <input id="vote_question_options_" name="vote_options_" type="hidden" value=""/>
    <div class="question-item-edit">
      <label class="">问题</label>
      <div class="form-group">
        <label class="custform-label-require label-left">标题</label>
        <div class="col-sm-10 padleft">
          <input name="vote_question_" id="vote_question_" class="vote_q_topic form-control validate[checkMaxInput[255],required]" style="width: 90%;"/>
          <div class="warm">请输入标题！</div>
        </div>
      </div>
      <div class="form-group">
        <label class="label-left">选择方式</label>
        <div class="col-sm-10 padleft vote-type">
          <label class="radio-inline">
            <input class="vote_question_radio" type="radio" id="vote_question_radio_" name="vote_type_" value="0">单选
          </label>
          <label class="radio-inline">
            <input class="vote_question_checkbox" type="radio" id="vote_question_checkbox_" name="vote_type_" value="1">多选
          </label>
          <label class="radio-inline select-limited lSelectLimited" id="vote_question_select_limited_" class="radio-inline">最多选&nbsp;
            <input name="vote_selectLimited_" id="vote_question_selectLimited_" class="vote-input lSelectLimited validate[integer,required,min[1]]" type="text" maxlength="2" value="<c:out value="${vote.vote_selectLimited}"/>" onKeyUp="this.value=this.value.replace(/[^\.\d]/g,'');if(this.value.split('.').length>2){this.value=this.value.split('.')[0]+'.'+this.value.split('.')[1]}" >&nbsp;项
          </label>
        </div>
      </div>
      <!-- 投票选项 -->
      <div class="form-group">
        <div style="margin-left: -14px;" data-no="0" class="col-sm-10 containerDiv"></div>
      </div>
      <!-- END 投票选项 div -->
      <!-- 添加选项 div -->
      <div class="form-group">
        <div class="col-sm-12 control-label">
          <button type="button" class="btn-warning" id="vote_question_addNewOption_" onclick="nis_vote.addNewVote(this);">+添加选项</button>
        </div>
      </div>
      <!-- END 添加选项 -->
      <div class="question-delete">
        <span class="q_operation" onclick="nis_vote.packUpQuestion(this)">收起</span>
        <span class="q_operation q_delete" onclick="nis_vote.deleteQuestion(this)">删除</span>
      </div>
    </div>
    <div class="question-item-show">
      <div class="question-item-div">
        <div class="question_name">
          <label class="">问题</label>
          <span class="question-topic"></span>
        </div>
        <div class="question_operation">
          <span class="q_operation q_edit" onclick="nis_vote.editQuestion(this)">编辑</span>
          <span class="q_operation q_delete" onclick="nis_vote.deleteQuestion(this)">删除</span>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- 关闭dialog需要使用的iframe -->
<iframe id="closeFrame" src="" style="display: none;"></iframe>
</body>

</html>
