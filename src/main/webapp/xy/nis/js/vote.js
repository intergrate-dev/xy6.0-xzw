/**
 * Created by guzm on 2015/8/3.
 */
'use strict'
$(function () {
  //初始化 投票
  nis_vote.init();
});

/**
 * 投票相关的方法
 */
var nis_vote = {
  currentQuestionNum: 0,
  _vote: {},
  /**
   * 初始化
   */
  init: function () {
    nis_vote.initData();
    $(document).on('change', '.vote_question_radio', nis_vote.hideSelectLimited);
    $(document).on('change', '.vote_question_checkbox', nis_vote.showSelectLimited);
  },
  //请求的数据回显
  initData: function () {
    nis_vote._vote = vote_info.vote;
    //如果是新建，那就默认添加一个选项
    if (vote_info.isNew == "true") {
      nis_vote.addNewQuestion();
    } else {
      //如果是修改，显示出来
      var voArray = eval(vote_info.voteOptionArray);
      console.log(voArray);
      //todo:这里应该是二维数组 应该要循环一次问题的数组 这里暂时写成0
      for (var i = 0; i < voArray.length; i++) {
        nis_vote.addNewQuestion(i, voArray[i]);
      }
    }
    
    //设置验证
    $("#voteForm").validationEngine({
      autoPositionUpdate: true,
      promptPosition: "bottomLeft"
    });
    
    //初始化日期控件
    nis_vote.initDatePicker();
    //鼠标悬浮事件
    $("img[id^=thumbImg_]").parent().mouseover(function () {
      $(this).find("span").show().css({
        "background": "#999",
        "border-radius": "10px",
        "cursor": "pointer"
      });
    });
    $("img[id^=thumbImg_]").parent().mouseout(function () {
      $(this).find("span").hide();
    });
    
    $("img[id^=thumbImg_]").parent().find("span").click(function () {
      $(this).siblings("img").attr("src", "");
      $(this).parent().siblings("input").val("");
      $(this).parent().parent().hide();
    });
    
  },
  //初始化日期控件
  initDatePicker: function () {
    $('#dateControlDiv').datetimepicker({
      language: 'zh-CN',
      weekStart: 1,
      todayBtn: 1,
      autoclose: 1,
      todayHighlight: 1,
      startView: 2,
      forceParse: 0,
      showMeridian: 1,
      minView: 0
    });
  },
  //初始化上传文件空间
  initUploadify: function (_id) {
    //上传按钮
    $("#uploadFile_" + _id).uploadify({
      'buttonText': '上传图片',
      "buttonClass": "uploadbtnStyle",
      'height': 30,
      'width': 80,
      "method": "post",
      'swf': '../../e5script/jquery/uploadify/uploadify.swf',
      'uploader': '../../xy/nis/uploadpic.do;jsessionid=' + vote_info.sessionId,
      'auto': true,
      "removeTimeout": 0,
      'fileObjName': 'file',
      "queueID": "uploadFileQueue_" + _id,
      "multi": false,
      "onSelect": function () {
        //上传过程中，原缩略图隐藏
        $("#thumbImg_" + _id).hide();
        //显示上传进度
        $("#uploadFileQueue_" + _id).show();
      },
      'onUploadSuccess': function (file, data) {
        //上传成功之后
        var _json = eval("(" + data + ")");
        //隐藏上传进度条
        $("#uploadFileQueue_" + _id).find("div[id^=SWFUpload]").hide();
        //显示图片
        var _imgPath = "../../xy/image.do?path=" + _json.imagePath;
        $("#thumbImg_" + _id).attr("src", _imgPath).show();
        //$("#thumbImg_" + _id).show();
        
        //给图片路径赋值
        $("#vote_picUrl_" + _id).val(_json.imagePath);
        
        //移动至选项的头部
        nis_vote.moveTop($("#optionDiv_" + _id));
        
        //给图片加上一个显示删除按钮的事件
        /*$("#thumbImg_" + _id).parent().hover(function(){
  $(this).find("span").toggle().css({
    "background":"red",
    "border-radius":"10px",
    "cursor":"pointer"
  });
});*/
        $("#thumbImg_" + _id).parent().mouseover(function () {
          $(this).find("span").show().css({
            "background": "red",
            "border-radius": "10px",
            "cursor": "pointer"
          });
        });
        $("#thumbImg_" + _id).parent().mouseout(function () {
          $(this).find("span").hide();
        });
        $("#thumbImg_" + _id).parent().find("span").click(function () {
          $(this).siblings("img").attr("src", "");
          $(this).parent().siblings("input").val("");
          $(this).parent().parent().hide();
        });
        
      }
    });
  },
  /**
   * 加上id name等属性
   * @param num 序号
   * @param question 问题
   */
  initQuestion: function (num, question) {
    var parentId = "question_item_" + num,
      $this, voteOptionArray,_name;
    nis_vote.currentQuestionNum = num;
    voteOptionArray = question ? question.vote_options : [];
    //修改name和id - 在后面加 _no
    $("#" + parentId).find("[id^='vote_question_']").each(function () {
      $this = $(this);
      _name = $this.attr("name");
      $this.attr("id", $this.attr("id") + num);
      if(_name){
        $this.attr("name", _name + num);
      }
      //如果label有for，同样设置
      if ($this.attr("for")) {
        $this.attr("for", $this.attr("for") + num);
      }
    });
    if (num < 2) {
      $("#" + parentId).find('.question-delete .q_delete').hide();
    } else {
      $("#" + parentId).find('.question-delete').show();
    }
    //在选项容器里放一个记录选项数量的属性
    $("#" + parentId).find(".containerDiv").attr("data-no", "0");
    //初始-添加两个选项
    if (!voteOptionArray || voteOptionArray.length == 0) {
      nis_vote.addNewVote(null, num);
      nis_vote.addNewVote(null, num);
      $("input[id='vote_period_once']").click();
      $("input[id='vote_with_login']").click();
      $("input[id='vote_app_only']").click();
      //默认勾选 单选 选项
      $("input#vote_question_radio_" + num).click();
    } else {
      //选项
      for (var i = 0, size = voteOptionArray.length; i < size; i++) {
        //添加一个选项 并赋值
        nis_vote.addNewVote(null, num);
        $("#docID_" + num + "_" + i).val(voteOptionArray[i].docID);
        $("#vote_count_" + num + "_" + i).val(voteOptionArray[i].vote_count);
        $("#vote_countInitial_" + num + "_" + i).val(voteOptionArray[i].vote_countInitial);
        $("#vote_option_" + num + "_" + i).val(voteOptionArray[i].vote_option);
        $("#vote_picUrl_" + num + "_" + i).val(voteOptionArray[i].vote_picUrl);
        $("#vote_voteID_" + num + "_" + i).val(voteOptionArray[i].vote_voteID);
        //判断是否有图片，有的话就显示图片
        if (voteOptionArray[i].vote_picUrl && voteOptionArray[i].vote_picUrl != "") {
          //显示图片
          var _imgPath = "../../xy/image.do?path=" + voteOptionArray[i].vote_picUrl;
          $("#thumbImg_" + num + "_" + i).attr("src", _imgPath).show();
          //$("#thumbImg_" + i);
          $("#uploadFileQueue_" + num + "_" + i).show();
        }
      }
      //标题
      $("#vote_question_" + num).val(question.vote_question);
      //问题选择方式
      if (parseInt(question.vote_type) == 0) {
        $("input#vote_question_radio_" + num).click();
      } else {
        $("input#vote_question_checkbox_" + num).click();
        $("input#vote_question_selectLimited_" + num).val(question.vote_selectLimited);
        $("#vote_question_select_limited_" + num).show();
      }
    }
  },
  addQuestion: function () {
    var $qList = $("#questionList"),
      num = $qList.attr('data-num'),
      $item = $("#question_item_" + num);
    if (nis_vote.handlePreOne(num, $item)) {
      //在问题容器里放一个记录选项数量的属性
      $("#questionList").attr("data-num", ++num);
      nis_vote.addNewQuestion(num);
      $qList.find('.q_delete').show();
    }
  },
  handlePreOne: function (num, $item) {
    var $q_topic = $item.find(".vote_q_topic"),
      q_topic = $q_topic.val();
    //验证
    // if(!$("#voteForm").validationEngine("validate")){
    // if(!q_topic){
    //     $q_topic.next('.warm').show();
    //     return false;
    // } else {
    //     $q_topic.next('.warm').hide();
    //todo: 问题需要放在一个数组里面
    //将编辑页面隐藏，显示展示页面
    $item.find(".question-item-edit").hide();
    $item.find(".question-item-show").show();
    //同步topic
    $item.find('.question-item-show .question-topic').text(q_topic);
    //在问题容器里放一个记录选项数量的属性
    return true;
    // }
  },
  /**
   * 添加一个新的问题
   * @param num　问题的序号
   * @param questionArray 该问题下的投票信息
   */
  addNewQuestion: function (num, questionArray) {
    var _id = '',
      $currentQuestion;
    //追加一个问题
    $("#questionList").append($("#questionCode").html());
    if (questionArray) {
      _id = questionArray.docID;
    }
    num = num ? num : 0;
    $currentQuestion = $("#questionList .question-item").last();
    $currentQuestion.attr('id', "question_item_" + num).attr('data-id', _id);
    $currentQuestion.find('#vote_question_docID_').val(_id);
    nis_vote.initQuestion(num, questionArray);
  },
  //点击编辑问题按钮
  editQuestion: function (_this) {
    var num = $("#questionList").attr("data-num"),
      $currentItem = $("#question_item_" + nis_vote.currentQuestionNum);
    //验证
    if (nis_vote.handlePreOne(num, $currentItem)) {
      var $item = $(_this).closest('.question-item');
      //将展示页面隐藏，显示编辑页面
      nis_vote.currentQuestionNum = parseInt($item.attr('id').replace('question_item_', ''));
      $item.find(".question-item-edit").show();
      $item.find(".question-item-show").hide();
    }
  },
  //点击删除问题按钮
  deleteQuestion: function (_this) {
    var $qList = $("#questionList"),
      num = $qList.attr('data-num'),
      $deleteQuestionIds = $('#deleteQuestionIds'),
      count = $qList.find('.question-item').size(),
      deleteQuestionIds;
    if (count && count > 1) {
      var $item = $(_this).closest('.question-item');
      deleteQuestionIds = $deleteQuestionIds.val() ? $deleteQuestionIds.val() + "," + $item.attr('data-id') : $item.attr('data-id');
      $deleteQuestionIds.val(deleteQuestionIds);
      console.log($deleteQuestionIds.val());
      $item.remove();
      // $qList.attr('data-num', --num);
      if (--count < 2) {
        $qList.find('.q_delete').hide();
      } else {
        $qList.find('.q_delete').show();
      }
    }
  },
  //收起问题编辑面板
  packUpQuestion: function (_this) {
    var $item = $(_this).closest('.question-item'),
      num = $("#questionList").attr('data-num'),
      count = $("#questionList").find('.question-item').size();
    $item.find(".question-item-edit").hide();
    $item.find(".question-item-show").show();
    if (count && count > 1) {
      $item.find('.question-item-show .q_delete').show();
    } else {
      $item.find('.question-item-show .q_delete').hide();
    }
  },
  /**
   * 删除按钮的触发事件
   * 1. 判断当前这个选项是否是最后一个
   * 2. 如果不是，删除；如果是，不做处理
   * @param _obj
   */
  deleteBtnListener: function (_obj) {
    //获取当前模块的id
    var _id = nis_vote.getThisId(_obj),
        $node = $("#docID_" + nis_vote.currentQuestionNum + _id),
        //选项的数量
        _no = $('#question_item_' + nis_vote.currentQuestionNum).find(".containerDiv").find(".div-border").size(),
      $deleteOptionIds = $("#deleteOptionIds");
    
    //如果当前这个选项是最后一个，不做处理
    if (_no <= 2){
      return;
    }
    if ($node.val()){
      $deleteOptionIds.val($deleteOptionIds.val() ? $deleteOptionIds.val() + "," + $node.val() : $node.val());
    }
    
    //删除当前选项
    $("#optionDiv_" + nis_vote.currentQuestionNum + _id).remove();
    
  },
  /**
   * 获得当前选项的id
   * @param _obj
   * @returns {*}
   */
  getThisId: function (_obj) {
    var $this = $(_obj);
    var _id = $this.attr("id");
    _id = _id.substr(_id.lastIndexOf("_") + 0);
    return _id;
  },
  /**
   * 添加一个新选项
   * @param _hook
   */
  addNewVote: function (_hook, num) {
    var $this, $container, newId, parentId, _no, _name;
    num = typeof (num) !== 'undefined' ? num : $(_hook).attr('id').replace('vote_question_addNewOption_', '');//问题的序号
    parentId = "question_item_" + num;
    $container = $('#' + parentId);
    _no = parseInt($container.find(".containerDiv").attr("data-no"));//获得当前no
    newId = num + "_" + _no;
    //在把当前的NO放到data-no中
    $container.find(".containerDiv").attr("data-no", ++_no);
    //把backup里面的东西放到内容div里面
    $container.find(".containerDiv").append($("#backupDiv").html());
    
    //移动到该选项的位置
    if (_hook) {
      nis_vote.moveTop($(_hook));
    }
    //给选项div加id
    $container.find(".containerDiv").find(".div-border:last").attr("id", "optionDiv_" + newId);
    
    //修改name和id - 在后面加 _no
    $("#optionDiv_" + newId).find("[id$=_]").each(function () {
      $this = $(this);
      _name = $this.attr("name");
      $this.attr("id", $this.attr("id") + newId);
      if(_name){
        $this.attr("name", _name + newId);
      }
      //如果lable有for，同样设置
      if ($this.attr("for")) {
        $this.attr("for", $this.attr("for") + newId);
      }
      
      $("#No_Span_" + _no).html(_no + 0 + "&nbsp;");
    });
    //初始化按钮
    nis_vote.initUploadify(newId);
    
    
  },
  /**
   * 页面漂移
   * @param $obj
   */
  moveTop: function ($obj) {
    $('html,body').animate({scrollTop: $obj.offset().top}, 800);
  },
  /**
   * 取消按钮
   */
  doCancel: function () {
    var UUID = $("#UUID").val();
    var url = "../../e5workspace/after.do?UUID=" + UUID;
    $('#closeFrame').attr("src", url);
  },
  /**
   * 提交按钮
   */
  doSave: function () {
    var options = [],
      option = {},
      variable,
      questionNode;
    //先校验
    if ($("#voteForm").validationEngine("validate")) {
      //确定一共有多少个选项 - 删除操作会使选项的数量不确定，所以在提交前，重新设置一下
      //这个参数是用来判断是否有选项，（有点多余）
      // $("#optionNo").val($("#containerDiv").find(".div-border").size());
      //这个参数是用来确定后台取参数时循环次数的
      // $("#lastOptionNo").val(nis_vote.getThisId($("#containerDiv").find(".div-border:last")[0]));
      questionNode = $("#questionList").find('.question-item');
      variable = questionNode.length;
      $("#questionNo").val(variable);
      $("#lastQuestionNo").val(variable - 1);
      variable = $('input[name="vote_cycle"]:checked').val();
      if (variable == "2") {
        variable = parseInt($("#vote_period_num").val());
        if (variable) {
          $("#vote_cyclenum").val(variable);
        } else {
          alert('请填写投票周期：每天投票的次数');
          return;
        }
      }
      questionNode.each(function (index, item) {
        options = [];
        //问题选项
        $(item).find('.option-item').each(function (i, t) {
          option = {};
          option.docID = $("#docID_" + index + "_" + i).val();
          option.vote_option = $("#vote_option_" + index + "_" + i).val();
          option.vote_countInitial = $("#vote_countInitial_" + index + "_" + i).val();
          option.vote_picUrl = $("#vote_picUrl_" + index + "_" + i).val();
          options.push(option);
        });
        $('#vote_question_options_' + index).val(JSON.stringify(options));
      });
      //提交表单
      $("#voteForm").submit();
    }
    
  },
  //隐藏问题 选择方式是多选时 “最多选几项”模块
  hideSelectLimited: function () {
    $(this).closest('.vote-type').find('.select-limited').hide();
  },
  //显示问题 选择方式是多选时 “最多选几项”模块
  showSelectLimited: function () {
    $(this).closest('.vote-type').find('.select-limited').show();
  },
};