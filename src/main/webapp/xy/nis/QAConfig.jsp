<%@include file="../../e5include/IncludeTag.jsp"%>
<!DOCTYPE html>
<i18n:bundle baseName="i18n.e5dom" changeResponseLocale="false" />
<html>
<head>
<title>问答参数设置</title>
<meta content="IE=edge" http-equiv="X-UA-Compatible" />
<link type="text/css" rel="stylesheet" href="../../e5style/reset.css" />
<link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css" />
<link type="text/css" rel="stylesheet" href="../../xy/nis/css/form-custom.css" />
<link type="text/css" rel="stylesheet" href="../../xy/css/extField.css" />
<link type="text/css" rel="stylesheet" href="../../xy/script/bootstrap-3.3.4/css/bootstrap.min.css">
<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css" />

<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script>
<script type="text/javascript" src="../../xy/script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jquery-ui/jquery-ui.min.js"></script>
<script type="text/javascript" src="../../xy/script/picupload/upload_api.js"></script>
<script type="text/javascript" src="js/QAConfig.js"></script>
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
        width: 110px;
        float: left;
        font-weight: 700;
    }
    table#contentTable td {
        width: 200px;
        height: 30px;
        line-height:1;
        font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
        font-size: 15px;
        color: #666;
    }

    .sidebar-list{
        margin-left:0
    }
    .custform-from-wrap {
        margin-left: 120px;
    }
    .custform-from-wrap table{
        margin:0;
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
    .container{
        padding-left:5px;
        width:304px;
        margin:0;
    }
</style>
</head>
<body onload="doInit()">
	<form id="form" method="post" action="QaConfigSubmit.do" onsubmit="return beforeSubmit();">
		<input type="hidden" id="UUID" name="UUID" value="${UUID}" />
        <input type="hidden" id="siteID" name="siteID" value="${siteID}" />
        <input type="hidden" id="siteLibID" name="siteLibID" value="${siteLibID}" />
		<input type="hidden" id="qaConfig" name="qaConfig" value="${qaConfig}" />
		<table class="tablecontent">
			<tr>
				<td colspan="2">
                <label class="qaform-label">标题字数限制</label>
					<div class="custform-from-wrap">
						<input type="text" class="validate[min[0],max[300]]" id="wordcount_title" name="wordcount_title" placeholder="默认为100" />
					</div>
                </td>
			</tr>
			<tr>
				<td colspan="2">
                    <label class="qaform-label">内容字数限制</label>
					<div class="custform-from-wrap">
						<input type="text" class="validate[min[0],max[2048]]" id="wordcount_content" name="wordcount_content" placeholder="默认为1024" />
					</div>
                </td>
			</tr>
			<tr>
				<td colspan="2">
                    <label class="qaform-label">职能部门的选项</label>
					<div class="custform-from-wrap" style="width:100%">
						<table>
							<tbody customwidth="529" customheight="228">
								<tr>
									<td class="ui-droppable">
										<div id="contentDiv" class="sidebar-list">
											<table id="contentTable" style="width:100%"></table>
                                            <div id="keyDiv" style="margin-left: 5px; display: none;">
                                                <input id="keyInput" type="text" style="width:150px;height:20px;float:left;margin-right:0">
                                                <button style="float:left;height:20px;width:17px">
                                                    <span id="keyOk" class="glyphicon glyphicon-ok"></span>
                                                </button>
                                                <button style="float:left;height:20px;width:17px">
                                                    <span id="keyRemove" class="glyphicon glyphicon-remove"></span>
                                                </button>
                                            </div>
											<!--<input style="display:none;" id="keyInput" value="" type="text" />-->
                                            <img id="img4Title" src="" style="margin-top:5px;display:block;max-width:150px;max-height:150px;" />
										</div>
                                    </td>
									<td class="ui-droppable" style="vertical-align:top;">
                                        <input value="+" onclick="group_form.addEnum();" type="button" />
                                        <input value="-" onclick="group_form.removeEnum();" type="button" />
									</td>
								</tr>
							</tbody>
						</table>
                    </div>
				</td>
			</tr>
			<tr>
				<td colspan="2"><label class="qaform-label">默认头像</label>
					<div class="custform-from-wrap">
						<img id="img_defaultIcon1" src="../../Icons/attach.gif" style="display:none" />
                        <!--<input type="file" id="defaultIcon1" name="defaultIcon11" oldValue="-" />-->
                        <div id="divAtts" style="display:none;"></div>
                        <div class="container kv-main">
                            <form enctype="multipart/form-data">
                                <div class="form-group">
                                    <input id="uploadInput" type="file" name="file" style="height: 30px;" multiple>
                                </div>
                            </form>
                        </div>
					</div>
                </td>
			</tr>
			<tr style="display:none">
				<td>
					<div id="hiddenDiv">
						<ul id="tablehide-ul" class="ui-droppable">
							<li>
                                <span id="SPAN_ext_options" class="custform-span">
									<label id="LABEL_ext_options" class="qaform-label">选项</label>
									<div class="custform-from-wrap" id="DIV_ext_options">
										<input type="text" id="ext_options" name="ext_options" value="" class="custform-input validate[maxSize[255]]" style="width:133px;" />
                                        <span class="custform-postfix"></span>
                                        <span class="custform-aftertxt"></span>
									</div>
                                </span>
                            </li>
						</ul>
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
