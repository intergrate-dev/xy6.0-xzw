<%@include file="../../e5include/IncludeTag.jsp"%>
<!DOCTYPE html>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false" />
<html>
<head>
<title>官方回复</title>
<meta content="IE=edge" http-equiv="X-UA-Compatible" />
<link type="text/css" rel="stylesheet" href="../../e5style/reset.css" />
<link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
<link type="text/css" rel="stylesheet" href="../../xy/css/form-custom.css"/>
<link type="text/css" rel="stylesheet" href="../../xy/nis/css/extField.css" />
<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css" />
<script type="text/javascript" src="../../e5script/xmenu/xmenu.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
<script type="text/javascript" src="../article/script/json2.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jquery-autocomplete/jquery.autocomplete.min.js"></script> 
    <script type="text/javascript" src="../../e5script/calendar/usecalendar.js"></script>
    <script type="text/javascript" src="../../e5script/calendar/calendar.js"></script>
<script type="text/javascript" src="../../e5workspace/script/form-custom.js"></script>
<script type="text/javascript" src="js/extField.js"></script>
<style>
.tablecontent {
	font-size: 15px;
}

 #btnFormCancel{
            font-size:12px;
        }
</style>
<script type="text/javascript">
	 	function beforeSubmit() {
	 	var optionsArr = $("#ext_options").val().split(",")
			var config = {
				"wordcount_title" : $("#wordcount_title").val(),
				"wordcount_content" : $("#wordcount_content").val(),
				"askTo" : optionsArr,
				"defaultIcon" : $("#defaultIcon").val()
			}
			
			config = JSON.stringify(config);
			
			$("#qaConfig").val(config);
			
		}
	</script>
</head>
<body onload="doInit()">
<iframe name="iframe" id="iframe" style="display:none;"></iframe>
	<form id="form" method="post" action="DiscussReplySubmit.do"
		onsubmit="return beforeSubmit();">
		<input type="hidden" id="UUID" name="UUID" value="${UUID}" /> 
		<input type="hidden" id="siteID" name="siteID" value="${siteID}" />
		<input type="hidden" id="siteLibID" name="siteLibID" value="${siteLibID}" />
		<input type="hidden" id="DocIDs" name="DocIDs" value="${DocIDs}" />
		<input type="hidden" id="DocLibID" name="DocLibID" value="${DocLibID}" />
		<table class="tablecontent">
		<tr>
                <td colspan="2"><label class="custform-label">所属稿件</label>
                    <div class="custform-from-wrap">
                        <p>${topic}</p>
                    </div></td>
            </tr>
            <tr>
                <td colspan="2"><label class="custform-label">评论内容</label>
                    <div class="custform-from-wrap">
                        <p>${content}</p>
                    </div>
            </div>
                    </td>
            </tr>
			<tr>
				<td colspan="2" ><label class="custform-label">评论人</label>
					<div class="custform-from-wrap">
						<p>${userName}</p>
					</div></td>
			</tr>
			
            <tr>
                <td colspan="2" class="ui-droppable">
                <span id="SPAN_a_answer" class="custform-span"> 
                <label id="LABEL_a_answer" class="custform-label ">官方回复</label>  
                  <div class="custform-from-wrap" id="DIV_a_answer"> 
                <textarea id="a_answer" name="a_answer" class="custform-textarea " style="width:300px;height:150px;"></textarea>  
                <span class="custform-postfix">
                    <span class="custform-aftertxt ui-draggable">
                    </span> 
                </span></div> 
                </span>
                </td>
            </tr>
			<tr>
				<td>
				<span id="txtFormSave" fieldtype="-1" fieldcode="insertsave" class="ui-draggable">
					<input class="button btn" id="btnFormSave" type="submit" value="保存" /> 
				</span>
				<span class="custform-aftertxt ui-draggable" />
				</td>
				<td class="ui-droppable">
                    <span id="txtFormCancel" fieldtype="-3" fieldcode="insertcancel" class="ui-draggable"  fieldtype="-3">
                        <input class="button btn" id="btnFormCancel" type="button" value="取消"/>
                    </span>
                    <span class="custform-aftertxt ui-draggable"/> 
                </td>
			</tr>
		</table>
	</form>

</body>
</html>
