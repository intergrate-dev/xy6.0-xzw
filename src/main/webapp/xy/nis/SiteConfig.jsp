<%@include file="../../e5include/IncludeTag.jsp"%>
<!DOCTYPE html>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false" />
<html>
<head>
<title>站点设置</title>
<meta content="IE=edge" http-equiv="X-UA-Compatible" />
<link type="text/css" rel="stylesheet" href="../../xy/nis/css/form-custom.css" />
<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css" />

<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
<style>
    *{box-sizing: border-box;}
    .tablecontent{
        font-size:15px;
        border: 0 none;
        color: #666;
        width:200px;
    }
    .tablecontent .qaform-label {
        padding-top: 13px;
        font-size: 14px;
        color: #000;
        text-align: right;
        margin-right: -80px;
        line-height: 14px;
        height: auto;
        margin-top: 0;
        font-family: "microsoft yahei";
        float: left;
        font-weight: 700;
    }
 
    .custform-from-wrap {
        margin-left: 220px;
    }
    span#txtFormSave {
        margin:0;
    }
    input[type=text] {
        height: 30px;
        font-family:'Helvetica Neue', Helvetica, Arial, sans-serif;
        font-size:15px;
        color:#666;
        width:285px;
    }
    input{
        line-height:1;
    }
    .custform-from-wrap .ui-droppable input[type=button]{
        margin:0;
        padding: 0;
        width: 26px;
    }
    #btnFormSave{
        margin-left:15px;
        border:none;
        border-radius: 3px;
    }
    #btnFormCancel{
        margin-left:15px;
        border:none;
        border-radius: 3px;
        background: #b1b1b1;
    }
</style>
<script type="text/javascript">
	function beforeSubmit() {
		var app = {
			"subscribeArticleCount" : $("#subscribeArticleCount").val(),
			"specialArticleCount" : $("#specialArticleCount").val(),
			"specialCoverWidth" : $("#specialCoverWidth").val(),
			"specialCoverHeight" : $("#specialCoverHeight").val()
		}
		$("#app").val(JSON.stringify(app));
		
		var member = eval("(" + $("#member").val() + ")");
		member.guardLine = $("#guardLine").val();
		$("#member").val(JSON.stringify(member));
	}
	function doInit(){
		//设置验证
		$("#form").validationEngine({
			autoPositionUpdate:true,
			promptPosition:"bottomLeft",
			scroll:true
		});

		var config = $("#app").val();
		if (config) {
			config = eval("(" + config + ")");
			
			$("#subscribeArticleCount").val(config.subscribeArticleCount);
			$("#specialArticleCount").val(config.specialArticleCount);
			$("#specialCoverWidth").val(config.specialCoverWidth);
			$("#specialCoverHeight").val(config.specialCoverHeight);
		}
		config = $("#member").val();
		if (config) {
			config = eval("(" + config + ")");
			
			$("#guardLine").val(config.guardLine);
		}
	}
</script>
</head>
<body onload="doInit()">
	<form id="form" method="post" action="SiteConfigSubmit.do" onsubmit="return beforeSubmit();">
		<input type="hidden" id="UUID" name="UUID" value="${UUID}" />
        <input type="hidden" id="siteID" name="siteID" value="${siteID}" />
        <input type="hidden" id="siteLibID" name="siteLibID" value="${siteLibID}" />
		
		<input type="hidden" id="app" name="app" value="${app}" />
		<input type="hidden" id="member" name="member" value="${member}" />
		<table class="tablecontent">
			<tr>
				<td colspan="2">
                <label class="qaform-label">已订阅栏目稿件显示个数</label>
					<div class="custform-from-wrap">
						<input type="text" id="subscribeArticleCount" name="subscribeArticleCount" value="3"
							maxlength="1" class="validate[custom[integer]] validate[min[0]]"/>
					</div>
                </td>
			</tr>
			<tr>
				<td colspan="2">
                <label class="qaform-label">App专题子栏目下稿件显示个数</label>
					<div class="custform-from-wrap">
						<input type="text" id="specialArticleCount" name="specialArticleCount" value="3"
							maxlength="1" class="validate[custom[integer]] validate[min[0]]"/>
					</div>
                </td>
			</tr>
			<tr>
				<td colspan="2">
                    <label class="qaform-label">App专题封面图推荐宽</label>
					<div class="custform-from-wrap">
						<input type="text" id="specialCoverWidth" name="specialCoverWidth" value=""
							maxlength="4" class="validate[custom[integer]] validate[min[0]]"/>
					</div>
                </td>
			</tr>
			<tr>
				<td colspan="2">
                    <label class="qaform-label">App专题封面图推荐高</label>
					<div class="custform-from-wrap">
						<input type="text" id="specialCoverHeight" name="specialCoverHeight" value=""
							maxlength="3" class="validate[custom[integer]] validate[min[0]]"/>
					</div>
                </td>
			</tr>
			<tr>
				<td colspan="2">
                    <label class="qaform-label">会员积分警戒线</label>
					<div class="custform-from-wrap">
						<input type="text" id="guardLine" name="guardLine" value="1000"
							maxlength="5" class="validate[custom[integer]] validate[min[0]]"/>
					</div>
                </td>
			</tr>
			<tr>
				<td style="float:left;">
                    <span id="txtFormSave" fieldtype="-1" fieldcode="insertsave" class="ui-draggable">
                        <input class="button btn" id="btnFormSave" type="submit" value="保存" />
                    </span>
                    <span class="custform-aftertxt ui-draggable"></span> 
                </td>
				<td style="float:left;">
                    <span id="txtFormCancel" fieldtype="-3" fieldcode="insertcancel" class="ui-draggable">
                        <input class="button btn" id="btnFormCancel" type="button" onclick="window.close()" value="取消" />
                    </span>
                </td>
			</tr>
		</table>
	</form>
</body>
</html>
