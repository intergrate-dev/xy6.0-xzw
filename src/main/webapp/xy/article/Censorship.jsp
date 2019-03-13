<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5workspace" changeResponseLocale="false"/>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
	<title>送审</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<link type="text/css" rel="stylesheet" href="../../xy/css/bootstrap.min.css"/>
	<link type="text/css" rel="stylesheet" href="../script/jquery-autocomplete/styles.css"/>
	<link type="text/css" rel="stylesheet" href="../../e5script/jquery/dialog.style.css"/>
	<link rel="stylesheet" type="text/css" href="../../e5script/jquery/uploadify/uploadify.css"> 
	
	<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
	<script type="text/javascript" src="../script/jquery-autocomplete/jquery.autocomplete.min.js"></script>
	<script type="text/javascript" src="../../e5script/jquery/jquery.dialog.js"></script> 
	<script type="text/javascript" src="../../e5script/jquery/uploadify/jquery.uploadify-3.1.min.js"></script>
	<script type="text/javascript" src="../script/cookie.js"></script>
	<script type="text/javascript" src="script/originalWordcount.js"></script>
	<style>
		li{
			list-style: none;
		}
		#main{width:300px;height:170px;} 
		#censorshipReason
		{
			width:542px;
			height: 115px; 
			border-radius:2px; 
			border: 1px solid #ddd;
			resize: none;
			font-family: "microsoft yahei";
			outline: none;
			padding-left: 5px;
			margin-top: 2px;
		}
		.frm
		{
			display:none;
		}
		.li .label
		{
			font-weight: 400;
		}
		.btnColumnRel
		{
			margin-left: -5px;
		}
		.btnColumn
		{
			margin-left: -5px; 
			margin-right: 30px;
		}
		#doSubmit,#doSubmitAndPub {
		    border-radius: 3px;
		    color: #fff;
		    background: #1bb8fa;
		    height: 30px;
		    border: none;
		    font-size: 12px;
		    cursor: pointer;
		    text-shadow: none;
		    padding: 0 20px;
		    font-family: "microsoft yahei";
		    margin-right: 10px;
		    cursor: pointer;
		}
		#doCancel{
		    height: 30px;
		    background: #b1b1b1;
		    border: none;
		    color: #fff;
		    border-radius: 3px;
		    margin-top: 5px;
		    padding: 0 20px;
		    text-shadow: none;
		    cursor: pointer;
		}
		.checkArt{
			padding: 20px;
			box-sizing: border-box;
			padding-top: -10px;
			margin: -10px 0;
		}
		.table-striped{
			border:1px solid #ddd;
		}
		.mr20{
			margin-right: 20px;
		}
		.checkChoice,.advices{
			margin: 10px 0;
		}
		.li label{
			color: #333;
		}
		input[type='button']{
			height: 27px;
		    margin-top: -2px;
		    margin-left: 2px;
		    line-height: 10px;
		}
		#logTable table tbody td:nth-child(1){
			width: 80px;
		}
		#logTable table tbody td:nth-child(2){
			width: 145px;
		}
		#logTable table tbody td:nth-child(3){
			width: 170px;
		}
		.mt10{
			margin-top: 10px;
		}
		#form div{
			margin-left: 20px;
		}
		#form div li:nth-of-type(2) label{
			margin-right: 3px;
		}
	</style>
	<script language="javascript" type="text/javascript" src="script/originalCensorship.js"></script>
</head>
<body>
	
	<p class="line"></p>
	<form id="form">
		<div>
			<span class="visa">可签发渠道:</span>
			<c:if test="${originaledchsCount > 0}">
				<c:forEach var="ch" items="${originaledchs}">
					<li class="li mt10">
						<label class="label" for="channel<c:out value="${ch.id}"/>">
							<input style="float: left;margin-top: 7px;" type="checkbox" 
								<c:if test="${not empty ch.colPreName}"> checked="checked" </c:if>
								id="channel<c:out value="${ch.colPreID}"/>" name="channel<c:out value="${ch.id}"/>"/>
							<c:out value="${ch.name}"/>
						</label>
						<span>主栏目:</span><input type="hidden" id="<c:out value="${ch.id}"/>_columnID" name="<c:out value="${ch.id}"/>_columnID" value="${ch.colPreID==0?ch.colID:ch.colPreID}">
						<input type="text" id="<c:out value="${ch.id}"/>_column" name="<c:out value="${ch.id}"/>_column" 
								readonly="true" value="${ch.colPreName}" placeholder="请选择栏目">  
						<input  type="button" for="<c:out value="${ch.id}"/>_column" ch="<c:out value="${ch.id}"/>"
								value="..." class="btn btnColumn" title="选择栏目">
						<span>关联栏目:</span><input type="hidden" id="<c:out value="${ch.id}"/>_columnRelID" name="<c:out value="${ch.id}"/>_columnRelID" value="${ch.colPreRelID==null?ch.colRelID:ch.colPreRelID}">
						<input type="text" id="<c:out value="${ch.id}"/>_columnRel" name="<c:out value="${ch.id}"/>_columnRel" 
								readonly="true" value="${ch.colPreRelName}" placeholder="请选择栏目">  
						<input  type="button" for="<c:out value="${ch.id}"/>_columnRel" ch="<c:out value="${ch.id}"/>"
								value="..." class="btn btnColumnRel" title="选择栏目">
						<c:if test="${not empty ch.colName}">
							<br><span>已签发主栏目:${ch.colName}</span><span>。	已签发关联栏目:${ch.colRelName}</span>	
						</c:if>
						<!-- <input  type="button" for="<c:out value="${ch.id}"/>_preview" ch="<c:out value="${ch.id}"/>"
								value="预览" class="btn btnPreview" title="预览">  -->	
					</li>
				</c:forEach>
			</c:if>
			<c:if test="${originaledchsCount == 0}">
				<li class="li mt10">
					<label class="label" for="channel_weixin">
						<input style="float: left;margin-top: 7px;" type="checkbox" 
							<c:if test="${not empty originaledwx.colPreRelName}"> checked="checked" </c:if>
							id="channel_weixin" name="channel_weixin"/>
						<c:out value="微信公众号"/>
					</label>
					<span></span><input type="hidden" id="oldWeixinID" name="oldWeixinID" value="${originaledwx.colPreRelID==null?originaledwx.colRelID:originaledwx.colPreRelID}">
					<input type="hidden" id="weixinID" name="weixinID" value="${originaledwx.colPreRelID}">
					<input type="text" id="weixinName" name="weixinName" 
						readonly="true" value="${originaledwx.colPreRelName}" placeholder="请选择微信公众号">  
					<input type="button" for="weixinName" value="..." class="btn wxColumn" title="选择公众号">	
					<c:if test="${not empty originaledwx.colRelName}">
						<br><span>已签发微信公众号:${originaledwx.colRelName}</span>	
					</c:if>	
				</li>
			</c:if>
		</div>
	</form>
	<div class="checkArt">
		<div id="logTable"></div>
		<div id="selectDiv">
			
		</div>
	 
		<div id="main">
			<div class="advices">留言\意见: <span id="censorshipReasonCount"></span></div>
			<textarea id="censorshipReason" class="text" placeholder="留言\意见"></textarea>
		</div>
		<iframe name="frmCensorship" id="frmCensorship" src="" class="frm"></iframe>
		<div style="text-align: center;">
			<input type='button' id="doSubmit" value='确定'/>
			<input type='button' id="doSubmitAndPub" value='签发并发布'/>
			<input type='button' id="doCancel" value='取消'/>
		</div>
	</div>
	<script type="text/javascript">
		var original = {
			UUID : "<c:out value="${param.UUID}"/>",
			DocIDs : "<c:out value="${param.DocIDs}"/>",
			DocLibID : "<c:out value="${param.DocLibID}"/>",
			IsEditor : "<c:out value="${param.IsEditor}"/>",//是否是编辑器内部送审\预签并送审
			siteID : "<c:out value="${param.siteID}"/>",
			channelCount : "<c:out value="${param.channelCount}"/>",
			type : "<c:out value="${param.type}"/>",
			articleType : "<c:out value="${param.articleType}"/>"
		};
	</script>
	<script>
		function drawTable(data){
			console.log(data);
			if(data.length>0){
				var logTableHtml = "<div>该稿件审核记录:</div><br><table class='table table-striped' border='1'><thead><tr><td>审核人</td>"
					+ "<td>审核操作</td><td>审核时间</td><td>审核意见</td></tr></thead><tbody>";
//				$("#logTable").append("该稿件审核记录<br><table class='table table-striped' border='1'><tr><td>审核人</td>"
//					+ "<td>审核操作</td><td>审核时间</td><td>审核意见</td></tr>");
				for(var i=0;i<data.length;i++){
					logTableHtml += "<tr><td>" + data[i].operator + "</td>"
						+ "<td>" + data[i].fromPosition + data[i].operation + "</td>"
						+ "<td>" + formatCSTDate(data[i].startTime,"yyyy-MM-dd hh:mm:ss") + "</td>";
						if(data[i].detail==null){
							logTableHtml += "<td></td></tr>";
						}else{
							logTableHtml += "<td>" + data[i].detail + "</td></tr>";
						}
				}
				logTableHtml += "</tbody></table>";
				$("#logTable").append(logTableHtml);
			}
		}
		
		function drawSelect(data){
			if(data.length>0){
				$("#selectDiv").append('<div class="checkChoice">送审选择:</div>');
				$("#selectDiv").append("<div class='newCheck pull-left mr20'><input type='radio' name='scope' id='newCensorship' value='0' checked='checked'><span>发起全新审核</span></div>");
				$("#selectDiv").append("<div class='checkStep pull-left mr20'><input type='radio' name='scope' id='oldCensorship' value='1'><span>选择审核阶段</span></div>");
				$("#selectDiv").append("<select id='selectCensorship'></select>");
				for(var i=0;i<data.length;i++){
					var arr = data[i].split(":");
					$("#selectCensorship").append("<option value=" + arr[0] + ">" + arr[1] + "</option>");
				}
			}
		}
		
		//操作成功了调用
		function operationSuccess(str){
			if (original.UUID) {
				var url = "../../e5workspace/after.do?UUID=" + original.UUID + "&DocIDs=" + original.DocIDs
					+ "&DocLibID=" + original.DocLibID
					+ "&Opinion="+str+"备注:"+ encodeU(reason);
				$("#frmCensorship").attr("src", url);
			} else {
				//var tool = opener.e5.mods["workspace.toolkit"];
				//tool.closeOpDialog("OK", 1);
				window.opener.censorshipHide();
				window.close();
			}
		}
		//操作失败了调用
		function operationFailure() {
			if (original.UUID) {
				var url = "../../e5workspace/after.do?UUID=" + original.UUID;
				$("#frmCensorship").attr("src", url);
			} else {
				window.close();
			}
		}
		/** 对特殊字符和中文编码 */
		function encodeU(param1) {
			if (!param1)
				return "";
			var res = "";
			for ( var i = 0; i < param1.length; i++) {
				switch (param1.charCodeAt(i)) {
				case 0x20://space
				case 0x3f://?
				case 0x23://#
				case 0x26://&
				case 0x22://"
				case 0x27://'
				case 0x2a://*
				case 0x3d://=
				case 0x5c:// \
				case 0x2f:// /
				case 0x2e:// .
				case 0x25:// .
					res += escape(param1.charAt(i));
					break;
				case 0x2b:
					res += "%2b";
					break;
				default:
					res += encodeURI(param1.charAt(i));
					break;
				}
			}
			return res;
		}
		
		//格式化CST日期的字串
		function formatCSTDate(strDate,format){
		   return formatDate(new Date(strDate),format);
		}
		       
		//格式化日期
		function formatDate(date,format){
		    var paddNum = function(num){
		    num += "";
		    return num.replace(/^(\d)$/,"0$1");
			};
			//指定格式字符
			var cfg = {
			   yyyy : date.getFullYear() //年 : 4位
			   ,yy : date.getFullYear().toString().substring(2)//年 : 2位
			   ,M  : date.getMonth() + 1  //月 : 如果1位的时候不补0
			   ,MM : paddNum(date.getMonth() + 1) //月 : 如果1位的时候补0
			   ,d  : date.getDate()   //日 : 如果1位的时候不补0
			   ,dd : paddNum(date.getDate())//日 : 如果1位的时候补0
			   ,hh : paddNum(date.getHours())  //时
			   ,mm : paddNum(date.getMinutes()) //分
			   ,ss : paddNum(date.getSeconds()) //秒
			};
		    format || (format = "yyyy-MM-dd hh:mm:ss");
		    return format.replace(/([a-z])(\1)*/ig,function(m){return cfg[m];});
		} 
	</script>
</body>
</html>