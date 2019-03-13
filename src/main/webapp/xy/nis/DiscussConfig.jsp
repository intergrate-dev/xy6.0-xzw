<%@include file="../../e5include/IncludeTag.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title>评论参数设置</title>
	<meta content="IE=edge" http-equiv="X-UA-Compatible" />
	<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css"/>
	<link type="text/css" rel="stylesheet" href="../../xy/css/form-custom.css"/>
	<link type="text/css" rel="stylesheet" href="../../xy/script/bootstrap-3.3.4/css/bootstrap.min.css">

	<script type="text/javascript" src="../../xy/script/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="../../xy/script/bootstrap-3.3.4/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../../xy/script/picupload/upload_api.js"></script>
	<script type="text/javascript" src="../article/script/json2.js"></script>
	<style>
		.tablecontent{
			font-size:15px;
            line-height: 40px;
            margin-top: 30px;
		}
        .tablecontent .custform-label{
            padding-top: 13px;
            font-size: 14px;
            color: #000;
        }
        div.styleColor{
            width: 50px;
            height: 25px;
        }
        #defaultIcon{
            width: 60px;
            border: 1px solid #cdcdcd;
            margin: 5px 0 5px 5px;
        }
        input[type=text]{
            height: 30px;
            width: 200px;
        }
        div.chooseColor{
            border: 2px solid #cdcdcd;
            width: 55px;
            height: 30px;
        }
        #upl#uploadInputoadInput{
            height: 30px;
            margin-top: -5px;
            margin-left: 6px;
        }
        input{line-height:1;}
        #btnFormSave{
            margin-left:15px;
        }
        #btnFormCancel{
            font-size:12px;
            margin-top: 10px;
        }
        .container{
            padding-left:5px;
            width:304px;
        }
	</style>
	<script type="text/javascript">
    var discuss={
        initPics: function(){
            var _imgsrc = $("#defaultIcon").attr("src");
            if(!_imgsrc) return;
            var _html = [];
            _html.push("<ul style='clear: both;margin-left: 96px;margin-top: -12px'>");
            _html.push("<li style='float:left;margin-right: 10px;margin-bottom: 10px;'><img style='max-width:80px;max-height:60px;' src='{img}' /></li>".replace(/\{img\}/g, "../../xy/image.do?path=" + _imgsrc));
            _html.push("</ul>");
            $("#divAtts").append(_html.join(""));
        },
        upload: null,
        initPicPlugin: function(){
            var param = {
                uploadUrl: '../../xy/upload/uploadFileF.do',
                deleteUrl: "../../xy/upload/deletePreviewThumb.do",
                showContent: false,
                autoCommit: true,
                showEditor: false,
                maxFileCount:1,
				allowedFileExtensions:['jpg', 'jpeg', 'gif', 'bmp', 'png'],
                showRemove: false,
                showUpload: false,
                showSort: true,
				rootUrl: '../../'
            };
            param.fileUploaded = function(e, data){
                $("#defaultIcon").attr("src","../../xy/image.do?path="+data.path);
            };

            var _imgsrc = $("#defaultIcon").attr("src");
            if(_imgsrc && $.trim(_imgsrc) != ""){
                    var initialPreview = [];
                    var initialPreviewConfig = [];
                    if($.trim(_imgsrc).indexOf("http")==0){
                        initialPreview.push(_imgsrc);
                    }else{
                        initialPreview.push("../../xy/image.do?path=" + _imgsrc  + ".0.jpg" );
                    }
                        initialPreviewConfig.push({url: "../../xy/upload/deletePreviewThumb.do",
                        imagePath:_imgsrc,
                        key: 0});
                    param.initialPreview = initialPreview;
                    param.initialPreviewConfig = initialPreviewConfig;
            }
            discuss.upload = new Upload("#uploadInput",param);
        },
        save: function(){
            var list = discuss.upload.getDataList();
            if(list.length>0){
                var _imgsrc=list[0].imagePath;
                $("#defaultIcon").attr("src",_imgsrc);
            }else{
            	$("#defaultIcon").attr("src","");
            }
        }
    }

		function beforeSubmit() {
            discuss.save();
			var config = {
				"auditType" : $('input[name="auditType"]:checked').val(),
				"showCount" : $("#showCount").is(':checked'),
				"showPic" : $("#showPic").is(':checked'),
				"showAnonymous" : $("#showAnonymous").is(':checked'),
				"showDebase" : $("#showDebase").is(':checked'),
				
				"defaultTitle" : $("#defaultTitle").val(),
				"defaultHint" : $("#defaultHint").val(),
				
				"countHot" : $("#countHot").val(),
				"countNew" : $("#countNew").val(),
				"countReply" : $("#countReply").val(),

				"defaultIcon": $("#defaultIcon").attr("src"),
				"defaultName": $("#defaultName").val(),
				
				//"styleType" : $('input[name="styleType"]:checked').val(),
				"styleColor" : $('.chooseColor').attr("value")
			}
			config = JSON.stringify(config);

			$("#discussConfig").val(config);

		}
		function doInit(){
            //选择颜色
            $(".styleColor").on("click", function(){
                $(".styleColor").removeClass('chooseColor');
                $(this).addClass('chooseColor');
            });
			/*$("input[name='styleType']").on("change",function(){
				var val=$(this).val();
				$("#colorTab"+val).show().siblings("div").hide();
			});*/

			var config = $("#discussConfig").val();
			if (!config) {discuss.initPicPlugin();return;}
			config = eval("(" + config + ")");
			
			$("input[name='auditType'][value=" + config.auditType + "]").attr("checked",true);
			$("#showCount")[0].checked = config.showCount;
			$("#showPic")[0].checked = config.showPic;
			$("#showAnonymous")[0].checked = config.showAnonymous;
			$("#showDebase")[0].checked = config.showDebase;
			
			$("#defaultTitle").val(config.defaultTitle);
			$("#defaultHint").val(config.defaultHint);
			$("#defaultName").val(config.defaultName);
			$("#defaultIcon").attr("src", config.defaultIcon);

			$("#countHot").val(config.countHot);
			$("#countNew").val(config.countNew);
			$("#countReply").val(config.countReply);
			
			//$("input[name='styleType'][value=" + config.styleType + "]").attr("checked",true);
			//$("#"+config.styleColor).addClass("chooseColor");
			$(".styleColor[value=" + config.styleColor + "]").addClass("chooseColor");
            discuss.initPicPlugin();
		}

	</script>
</head>
<body onload="doInit()">
	<form id="form" method="post" action="DiscussConfigSubmit.do" onsubmit="return beforeSubmit();">
		<input type="hidden" id="UUID" name="UUID" value="${UUID}"/>
		<input type="hidden" id="siteID" name="siteID" value="${siteID}"/>
		<input type="hidden" id="siteLibID" name="siteLibID" value="${siteLibID}"/>
		<input type="hidden" id="discussConfig" name="discussConfig" value="${discussConfig}"/>
		<table class="tablecontent">
			<tr style=" line-height: 20px;"><td colspan="2">
				<label class="custform-label">审核规则</label>  
				<div class="custform-from-wrap">
					<label for="auditType0"><input type="radio" id="auditType0" name="auditType" value="0" checked/>先审后发（通过站长后台审核才显示）</label>
					<br/>
					<label for="auditType1"><input type="radio" id="auditType1" name="auditType" value="1"/>先发后审（默认审核通过，立即显示）</label>
					<br/>
					<label for="auditType2"><input type="radio" id="auditType2" name="auditType" value="2"/>全站关闭评论（所有文章将被关闭评论,且用户不能发表评论）</label>
					<br/><br/>
					
					<label for="showCount"><input type="checkbox" id="showCount" name="showCount" checked/>开启评论互动数显示（点赞，评论及回复等计数）</label>
					<br/>
					<label for="showPic"><input type="checkbox" id="showPic" name="showPic"/>允许发表带图片的评论</label>
					<br/>
					<label for="showAnonymous"><input type="checkbox" id="showAnonymous" name="showAnonymous"/>允许匿名评论</label>
					<br/>
					<label for="showDebase" style="display:none;"><input type="checkbox" id="showDebase" name="showDebase"/>允许对评论“踩”</label>
				</div> 
			</td></tr>
			<tr><td colspan="2">
				<label class="custform-label">官方账号</label>
				<div class="custform-from-wrap">
					<input type="text" id="defaultName" name="defaultName" placeholder="官方账号昵称"/><br/>
					<div style="display:none;" id="defaultIconDiv"><img id="defaultIcon" src="" /></div>
				    <!--<input id="uploadInput" type="file" name="file" style="height: 30px;" multiple>-->
                    <div id="divAtts" style="display:none;"></div>
					<div class="container kv-main">
						<form enctype="multipart/form-data">
							<div class="form-group">
								<input id="uploadInput" type="file" name="file" style="height: 30px;" multiple>
							</div>
						</form>
					</div>

				</div> 
			</td></tr>
			<tr><td colspan="2">
				<label class="custform-label">热门评论数</label>
				<div class="custform-from-wrap">
					<input type="text" id="countHot" name="countHot" placeholder="热门评论的显示个数"/>
				</div>
			</td></tr>
			<tr><td colspan="2">
				<label class="custform-label">最新评论数</label>  
				<div class="custform-from-wrap">
					<input type="text" id="countNew" name="countNew" placeholder="最新评论的显示个数"/>
				</div> 
			</td></tr>
			<tr><td colspan="2">
				<label class="custform-label">评论回复数</label>  
				<div class="custform-from-wrap">
					<input type="text" id="countReply" name="countReply" placeholder="评论回复的显示个数"/>
				</div> 
			</td></tr>
			<tr><td colspan="2">
				<label class="custform-label">评论框标题</label>  
				<div class="custform-from-wrap">
					<input type="text" id="defaultTitle" name="defaultTitle" placeholder="评论"/>
				</div> 
			</td></tr>
			<tr><td colspan="2">
				<label class="custform-label">默认提示语</label>  
				<div class="custform-from-wrap">
					<input type="text" id="defaultHint" name="defaultHint" placeholder="来说两句吧..."/>
				</div> 
			</td></tr>
			<%--<tr><td colspan="2">
				<label class="custform-label">主题风格</label>
				<div class="custform-from-wrap">
					<label for="styleType0"><input type="radio" id="styleType0" name="styleType" value="0" checked/>浅色背景</label>
					<label for="styleType1"><input type="radio" id="styleType1" name="styleType" value="1"/>深色背景</label>
				</div>
			</td></tr>--%>
			<tr><td colspan="2">
				<label class="custform-label">主题色调</label>
				<%--<div class="custform-from-wrap" id="colorTab0">
					<label for="blue"><div class="styleColor" id="blue" name="styleColor" value="blue" style="background-color: #00a0e9" title="蓝色"></div></label>
					<label for="red"><div class="styleColor" id="red" name="styleColor" value="red" style="background-color: #ff3333" title="红色"></div></label>
					<label for="orange"><div class="styleColor" id="orange" name="styleColor" value="orange" style="background-color: #ff5511" title="橙色"></div></label>
					<label for="green"><div class="styleColor" id="green" name="styleColor" value="green"  style="background-color: #66ff66" title="绿色"></div></label>
					<label for="gray"><div class="styleColor" id="gray" name="styleColor" value="gray"  style="background-color: #808080" title="灰色"></div></label>
					<label for="black"><div class="styleColor" id="black" name="styleColor" value="black" style="background-color: #000000" title="黑色"></div></label>
				</div>--%>
				<div class="custform-from-wrap" id="colorTab1">
					<label for="black"><div class="styleColor" id="blue" name="styleColor" value="blue" style="background-color: #2e97e9" title="蓝色"></div></label>
					<label for="blue"><div class="styleColor" id="red" name="styleColor" value="red" style="background-color: #f24548" title="红色"></div></label>
					<label for="red"><div class="styleColor" id="orange" name="styleColor" value="orange" style="background-color: #fdb92c" title="橙色"></div></label>
					<label for="green"><div class="styleColor" id="green" name="styleColor" value="green"  style="background-color: #5ece5e" title="绿色"></div></label>
					<label for="orange"><div class="styleColor" id="gray" name="styleColor" value="gray" style="background-color: #999999" title="灰色"></div></label>
					<label for="gray"><div class="styleColor" id="black" name="styleColor" value="black"  style="background-color: #000000" title="黑色"></div></label>
				</div>
			</td></tr>
			
			<tr><td>
				  <span id="txtFormSave" fieldtype="-1" fieldcode="insertsave" class="ui-draggable">
					<input class="button btn" id="btnFormSave" type="submit" value="保存"/>
				  </span>
				  <span class="custform-aftertxt ui-draggable"/>
				</td>
				<td>
				  <span id="txtFormCancel" fieldtype="-3" fieldcode="insertcancel" class="ui-draggable">
					<input class="button btn" id="btnFormCancel" type="button" onclick="window.close()" value="取消"/>
				  </span>
				</td>
			</tr>  
		</table>
	</form>
</body>
</html>
