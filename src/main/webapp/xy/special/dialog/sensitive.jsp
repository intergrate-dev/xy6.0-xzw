<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <style>
    	*{
    		margin: 0;
    		padding: 0;
    	}
        body{
            background-color:#fff;
        }
        .rename-btn{
            margin:10px auto;
        }
        .rename-txt{
            margin:0 auto;
        }
        .rename-txt input{
            width:300px;
            height:30px;
            font-family:"microsoft yahei";
            font-size:14px;
            padding:4px;
            border-radius: 3px;
            border: 1px solid #ddd;
        }
        .rename-cancle{
            margin-left:50px;
        }
        .rename-btn input{
            /*width:100px;*/
            /*height:30px;*/
            font-family:"microsoft yahei";
            font-size:14px;
        }
        .rename-radio {
            /*margin-left: 130px;*/
            margin-top: 10px;
            font-size: 14px;
        }
        .rename-radio input {
            width:13px;
            height:13px;
            font-family:"microsoft yahei"";
            font-size:10px;
            margin-top: 4px;
		    float: left;
		    margin-right: 2px;
        }
        .btn-type {
            border: none;
            padding: 5px 20px;
            margin-right: 10px;
            line-height: 16px;
            color: #fff;
            border-radius: 3px;
            cursor: pointer;
            outline: none;
        }
        .tip{
        	background: #f6f6f6; 
        	font-size: 14px;
        	padding: 20px;
        	width: 90%;
        	color: #666;
        }
        .pull-left{
        	float: left;
        }
        .tip div:first-child{
        	width: 10%;
        }
        .tip div:last-child{
        	width: 90%;
        }
        #btn-cancle{
        	background-color: #b1b1b1;
        }
        .clearfix:after{content:".";display:block;height:0;clear:both;visibility:hidden}
    </style>
</head>

<script src="../special/third/jquery-ui-bootstrap-1.0/assets/js/vendor/jquery-1.9.1.min.js" type="text/javascript"></script>
<body style="height:auto; margin: 0 40px;">
<%
    request.setCharacterEncoding("UTF-8") ;
%>
<input type="hidden" id="sentypeHidden" value="${sentype}"/>
<form id="renameForm" name="renameForm" action="${pageContext.request.contextPath }/xy/wordList/sensiSave.do" >
    <input type="hidden" id="isNew" name="isNew" value="${isNew}"/>
    <div style="margin-top:10px;">
        <div class="rename-txt">
            <input id="sensitivename" type="text" name="sensitivename" value="${sensitivename}" >
            <div style="margin-top: 6px;">
                <span style="display: none; color: red; font-size: 12px;" id="nameWarning">对不起，词语名称过长！</span>
            </div>
            <div style="margin-top: 6px;">
                <span style="display: none; color: red; font-size: 12px;" id="nameWarningSpan">对不起，词语不能为空！</span>
            </div>
            <div class="rename-radio clearfix" id="radio" >
            	<label for="sensitive" class="pull-left" style="margin-right: 26px;">
            		<input id="sensitive" class="sensi-radio" type="radio" name="sentype" value="0" checked="true"> 
            		<span class="pull-left">敏感词</span>
            	</label>
            	<label for="Illegal">
            		<input id="Illegal" class="sensi-radio" type="radio" name="sentype" value="1"> 
            		<span class="pull-left">非法词</span>
            	</label>
            	
            </div>

            <input type="hidden" name="DocIDs" value="${docID}">
            <input id="doclibid" type="hidden" name="DocLibID" value="${docLibID}">
            <input id="sensitive-uuid" type="hidden" name="UUID" value="${UUID}">
            <input id="authors" type="hidden" name="authors" value="${authors}">

        </div>
        <div style="margin-left: 92px; margin-top: 6px;">
            <span style="display: none; color: red" id="warningSpan">对不起，该词已存在！</span>
        </div>
        <div class="rename-btn">
            <input type="button" id="btn-confirm" class="btn-type" style="background-color: #00A0E6;" value="确定" />
            <input type="button" id="btn-cancle" class="btn-type" value="取消" onClick="doCancel()"/>
        </div>
        <div class="clearfix tip" style="">
            <div class="pull-left">说明:</div>
            <div class="pull-left">
            	<p class="clearfix" style="margin-bottom: 10px;">
            		<span class="pull-left" style="width: 5%;">1.</span>
            		<span class="pull-left" style="width: 90%;">用户发表包含敏感词的评论后，该评论直接进入待审核列表，该敏感词标红；</span>
            	</p>
            	<p>
            		<span class="pull-left" style="width: 5%;">2.</span>
            		<span class="pull-left" style="width: 90%;">可添加多个敏感词，词与词之间用英文逗号隔开</span>
            	</p>
            </div>
        </div>
    </div>
</form>

</body>
<script type="text/javascript">

    $(function(){
        var sentypeHidden = $("#sentypeHidden").val();
        if(sentypeHidden){
            $("input:radio[name='sentype']").filter("[value=" +
                    sentypeHidden +
                    "]").attr("checked","checked");
        }

        $("#btn-confirm").click(function(){
            save();
        });

    });


    function save(){
        var a = $("#sensitivename").val();
        var b= $("#doclibid").val();
        var c=a.trim();
        var d=$("input[name='sentype']:checked").val();
        $.ajax({
            url : "checkName.do",
            data : {
                "sensitiveName" : a,
                "type" :d,
                "docLibId" : b
            },
            type: "POST",
            dataType: "json",
            success : function(data) {
                if ("1" == data.status || data.status === 1) {
                    $("#warningSpan").html(data.senName + "已经存在！");
                    $("#warningSpan").show();
                } else {
                    if(a.length>=255){
                        $("#nameWarning").show();
                    }else if(c.length==0){
                        $("#nameWarningSpan").show();
                    }else{
                        $("#renameForm").submit();
                    }
                }

            }
        });

    }
    function doCancel() {
        window.onbeforeunload = null;

        $("#btn-confirm").disabled = true;
        $("#btn-cancle").disabled = true;

        beforeExit();
    };

    function beforeExit() {
        var uuid = $("#sensitive-uuid").val();
        var dataUrl = "../../e5workspace/after.do?UUID=" + uuid;

        window.location.href = dataUrl;
    }

    $(function() {
        $("#nameid").blur(function() {

        });
    });
</script>

</html>
