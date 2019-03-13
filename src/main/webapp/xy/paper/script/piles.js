$(function(){
    paper_piles.init();
});

var paper_piles = {
    init: function(){
        //容器里放一个记录选项数量的属性
        $("#containerDiv").attr("data-no", "-1");

        //如果是新建，那就默认添加一个选项
        var voArray = eval($("#piles").val());
        if (!voArray || voArray.length == 0){
            //初始-添加两个选项
            paper_piles.addNew();
            paper_piles.addNew();
        } else {
            //如果是修改，显示出来
            for (var i = 0; i < voArray.length; i++){
                paper_piles.addNew();
                $("#name_" + i).val(voArray[i].name);
                $("#code_" + i).val(voArray[i].code);
            }
        }

        //设置验证
        $("#postForm").validationEngine({
            autoPositionUpdate: true,
            promptPosition: "bottomLeft"
        });
    },
    /**
     * 删除按钮的触发事件
     * 1. 判断当前这个选项是否是最后一个
     * 2. 如果不是，删除；如果是，不做处理
     */
    deleteBtnListener: function(_obj){
        var _no = $("#containerDiv").find(".div-border").size();
        if (_no <= 1) return;
		
        var _id = paper_piles.getThisId(_obj);
        $("#optionDiv_" + _id).remove();
    },
    /**
     * 获得当前选项的id
     */
    getThisId: function(_obj){
        var _id = $(_obj).attr("id");
        _id = _id.substr(_id.lastIndexOf("_") + 1);
        return _id;
    },
    /**
     * 添加一个新选项
     */
    addNew: function(_hook){
        //当前计数
        var _no = parseInt($("#containerDiv").attr("data-no")) + 1;
        $("#containerDiv").attr("data-no", _no);
		
        //把backup里面的东西放到内容div里面
        $("#containerDiv").append($("#backupDiv").html());

        //移动到该选项的位置
        if (_hook)
            paper_piles.moveTop($(_hook));

        //给选项div加id
        var div = $("#containerDiv").find(".div-border:last");
		div.attr("id", "optionDiv_" + _no);
		div.attr("no", _no);

        //修改name和id - 在后面加 _no
        div.find("[id$=_]").each(function(){
            $this = $(this);
            $this.attr("id", $this.attr("id") + _no);
            $this.attr("name", $this.attr("name") + _no);
            $("#No_Span_" + _no).html(_no + 1 + ".&nbsp;");
        });
    },
    /**
     * 页面漂移
     * @param $obj
     */
    moveTop: function($obj){
        $('html,body').animate({scrollTop: $obj.offset().top}, 800);
    },
    doCancel: function(){
        var url = "../../e5workspace/after.do?UUID=" + $("#UUID").val();
        $('#closeFrame').attr("src", url);
    },
    doSubmit: function(){
        if ($("#postForm").validationEngine("validate")){
			var result = [];
			$("#containerDiv").find(".div-border").each(function(i) {
				var no = $(this).attr("no");
				var data = {
					"name": $("#name_" + no).val(),
					"code": $("#code_" + no).val()
				}
				result.push(data);
			});
			result = JSON.stringify(result);
			$("#piles").val(result);
			
            $("#postForm").submit();
        }
    }
};