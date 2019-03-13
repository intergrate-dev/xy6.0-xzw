<!--created by hudie on 2018/4/20-->
<%@page pageEncoding="UTF-8"%>
<%@include file="../../e5include/IncludeTag.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>投票详情</title>
  <link type="text/css" rel="stylesheet" href="../script/bootstrap-3.3.4/css/bootstrap.min.css">
  <script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
  <script type="text/javascript" src="../script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
  <script>
    var voteData = {
      UUID: '<c:out value="${UUID}"/>',
      DocLibID: '<c:out value="${DocLibID}"/>',
      DocID: '<c:out value="${DocID}"/>',
      siteID: '<c:out value="${siteID}"/>',
      groupID: '<c:out value="${groupID}"/>',//所属分组
      vote: {
        docID: '<c:out value="${vote.docID}"/>',
        vote_siteID: '<c:out value="${vote.vote_siteID}"/>',//站点ID
        vote_groupID: '<c:out value="${vote.vote_groupID}"/>',//所属分组
        vote_topic: '<c:out value="${vote.vote_topic}"/>',//标题
        vote_type: '<c:out value="${vote.vote_type}" escapeXml="false"/>',//选择方式
        vote_selectLimited: '<c:out value="${vote.vote_selectLimited}" escapeXml="false"/>',//多选限制选择个数
        vote_cycle: '<c:out value="${vote.vote_cycle}" escapeXml="false"/>',//投票周期
        vote_cyclenum: '<c:out value="${vote.vote_cyclenum}" escapeXml="false"/>',//周期次数
        vote_needlogin: '<c:out value="${vote.vote_needlogin}" escapeXml="false"/>',//是否需要登录
        vote_onlyapp: '<c:out value="${vote.vote_onlyapp}" escapeXml="false"/>',//仅在APP
        vote_endDate: '<c:out value="${vote.vote_endDate}"/>',//截止时间
        vote_totalNum: '<c:out value="${vote.vote_totalNum}" escapeXml="false"/>'//总投票数
      },
      voteOptionArray: '<c:out value="${voteOptionArray}" escapeXml="false"/>',
      isNew: '<c:out value="${isNew}" escapeXml="false"/>',
      sessionId: '<c:out value="${pageContext.session.id}" escapeXml="false"/>'
    };
    var vote_view = {
      //页面初始化
      doInit: function () {
        //vote_cycle: 只允许投一次---0,每天一次---1,每天n次---2,不限---3
        //vote_needlogin: 是---1
        //vote_onlyapp: 是---1
        //vote_count: 后加上单位： 人
        var questionArr = JSON.parse(voteData.voteOptionArray),
          totalNum = voteData.vote.vote_totalNum,//投票总数
          vote_cycle = parseInt(voteData.vote.vote_cycle);//投票周期
        $('#vote_topic').text(voteData.vote.vote_topic);
        $('#vote_endDate').text(voteData.vote.vote_endDate);
        totalNum = totalNum >= 0 ? totalNum : 0;
        switch (vote_cycle) {
          case 0:
            vote_cycle = '只允许投一次';
            break;
          case 1:
            vote_cycle = '每天一次';
            break;
          case 2:
            vote_cycle = '每天 <span class="color-num">' + voteData.vote.vote_cyclenum + '</span> 次';
            break;
          case 3:
            vote_cycle = '不限';
            break;
          default:
            vote_cycle = '';
        }
        $('#vote_period').html(vote_cycle);
        $('#vote_need_login').html(voteData.vote.vote_needlogin&&voteData.vote.vote_needlogin!='0' ? '是' : '否');
        $('#vote_terminal').html(voteData.vote.vote_onlyapp&&voteData.vote.vote_onlyapp!='0' ? '是' : '否');
        //todo:
        $('#vote_count').text(totalNum + '人');
        //循环每一个问题
        vote_view.initQuestion(questionArr);
      },
      initQuestion: function (questionArr) {
        var question_totalNum = 0;
        //循环每一个问题
        questionArr.forEach(function (item, index) {
          question_totalNum = 0;
          vote_view.addQuestion(index);
          //问题的标题前加上序号
          $("#question_name_" + index).html((index + 1) + '.&nbsp;' + item.vote_question);
          //循环问题的每一个选项
          vote_view.initOption(item.vote_options, index, question_totalNum);
        });
      },
      /**
       * 循环问题的每一个选项
       * @param voteOptionArray 问题的选项
       * @param questionIndex 问题的序号
       * @param voteCount 问题选项的票数的总和
       */
      initOption: function (voteOptionArray, questionIndex, voteCount) {
        //循环问题的每一个选项
        voteOptionArray.forEach(function (items, index) {
          var rate, count;
          vote_view.addOption(questionIndex, index);
          // 选项数据回显
          if (items.vote_picUrl) {
            var _imgPath = "../../xy/image.do?path=" + items.vote_picUrl;
            $('#option_img_' + questionIndex + '_' + index).attr('src', _imgPath);
            $("#option_img-div_" + questionIndex + '_' + index).show();
          }
          $("#option_topic_" + questionIndex + '_' + index).html(items.vote_option);
          //投票计算
          /* if(voteCount){
             count = items.vote_count;
             $("#option_count_" + questionIndex + '_' + index).html(count + '票');
             rate = Math.round(count * 10000 / voteCount) / 100 + '%';
           } else {
             rate = 0;
           }*/
          $("#option_percent_" + questionIndex + '_' + index).html(items.vote_rate);
          $("#option_progress_" + questionIndex + '_' + index).css('width', items.vote_rate);
        });
      },
      addQuestion: function (num) {
        var _id = "question_item_" + num;
        $("#questionList").append($("#questionCode").html());
        $("#questionList .question-item").last().attr('id', _id);
        //修改id
        $("#" + _id).find("[id^='question_']").each(function () {
          $this = $(this);
          $this.attr("id", $this.attr("id") + num);
        });
      },
      addOption: function (parentIndex, index) {
        var parentId = "question_option_" + parentIndex;
        var _id = "option_" + parentIndex + '_' + index;
        $("#" + parentId).append($("#voteOptionCode").html());
        $("#" + parentId).find(".option-item").last().attr('id', _id);
        //修改id - 在后面加 num
        $("#" + _id).find("[id^='option_']").each(function () {
          $this = $(this);
          $this.attr("id", $this.attr("id") + parentIndex + '_' + index);
        });
      },
    };
    $(function () {
      vote_view.doInit();
    });
  </script>
</head>
<body>
<div class="vote-container">
  <div id="voteUpper" class="vote-div">
    <!-- 投票标题 start -->
    <div class="form-group">
      <label class="col-sm-2 control-label">投票名称</label>
      <div class="col-sm-10 padleft" id="vote_topic"></div>
    </div>
    <!-- 投票标题 end-->

    <!-- 截止投票时间 start -->
    <div class="form-group">
      <label class="col-sm-2 control-label">截止投票时间</label>
      <div class="col-sm-10 padleft" id="vote_endDate"></div>
    </div>
    <!-- 截止投票时间 end-->

    <!-- 投票周期 start -->
    <div class="form-group">
      <label class="col-sm-2 control-label">投票周期</label>
      <div class="col-sm-10 padleft" id="vote_period"></div>
    </div>
    <!-- 投票周期 end-->

    <!-- 需要登录 start -->
    <div class="form-group">
      <label class="col-sm-2 control-label">需要登录</label>
      <div class="col-sm-10 padleft" id="vote_need_login"></div>
    </div>
    <!-- 需要登录 end-->

    <!-- 仅在App内投票 start -->
    <div class="form-group">
      <label class="col-sm-2 control-label">仅在App内投票</label>
      <div class="col-sm-10 padleft" id="vote_terminal"></div>
    </div>
    <!-- 仅在App内投票 end-->

    <!-- 投票次数 start -->
    <div class="form-group">
      <label class="col-sm-2 control-label">投票次数</label>
      <div class="col-sm-10 padleft" id="vote_count"></div>
    </div>
    <!-- 投票次数 end-->

  </div>
  <div id="questionList" class="vote-div"></div>
</div>

<!-- 问题的代码块 - 每添加一个问题就从这个模块中copy过去 -->
<div id="questionCode" style="display: none;">
  <div class="question-item">
    <label id="question_name_"></label>
    <!-- 投票选项 -->
    <div class="form-group">
      <div class="containerDiv" id="question_option_"></div>
    </div>
    <!-- END 投票选项 div -->
  </div>
</div>

<!-- 选项的代码块 - 每添加一个选项就从这个模块中copy过去 -->
<div id="voteOptionCode" style="display: none;">
  <div class="option-item">
    <div class="option-left">
      <div class="img-div thumbnail" id="option_img-div_" style="display: none;">
        <span class="wrapimg">
          <img src="" id="option_img_">
        </span>
      </div>
      <div class="option-topic" id="option_topic_"></div>
    </div>
    <div class="option-right">
      <div class="progress">
        <div id="option_progress_" class="progress-bar" role="progressbar" aria-valuenow="60" aria-valuemin="0" aria-valuemax="100">
          <span class="sr-only" id="option_progress_percent_"></span>
        </div>
      </div>
      <div class="option-count">
        <span id="option_count_"></span> <span id="option_percent_"></span>
      </div>
    </div>
  </div>
</div>

<style>
  .vote-container {
    margin: 0 20px 30px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    border: 1px solid #ccc;
  }

  .vote-div {
    padding: 20px 30px;
  }

  #voteUpper {
    display: flex;
    flex-direction: column;
    border-bottom: 1px solid #ccc;
  }

  #questionList .question-item + .question-item {
    border-top: 1px solid #ccc;
    padding-top: 10px;
  }

  .option-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    min-height: 100px;
    margin-top: 10px;
  }

  .option-left {
    width: 35%;
    display: flex;
    align-items: center;
  }

  .option-right {
    width: 65%;
    display: flex;
    align-items: center;
  }

  .option-count span {
    margin-left: 15px;
  }

  .option-topic {
    margin-left: 10px;
    max-width: calc(100% - 150px);
  }

  .option-item .img-div {
    width: 120px;
    margin: 0;
    /*background-color: #ddd;*/
  }

  .wrapimg {
    max-width: 100%;
    height: auto;
    position: relative;
    display: block;
  }

  .wrapimg img {
    max-width: 100%;
    height: auto;
    display: block;
  }

  .progress {
    height: 8px;
    width: 75%;
    border-radius: 0;
    margin-bottom: 0;
    background-color: #ccc;
  }

  .progress-bar {
    background-color: #777;
  }

  .color-num {
    color: #00a0e6;
  }
</style>
</body>
</html>