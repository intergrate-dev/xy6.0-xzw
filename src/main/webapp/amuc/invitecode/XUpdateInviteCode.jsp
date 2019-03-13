<%@include file="../../e5include/IncludeTag.jsp"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
<script type="text/javascript" src="../script/bootstrap/js/bootstrap.js"></script>
<script type="text/javascript" src="../../e5workspace/script/form-custom.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/jquery.validationEngine.js"></script>
<script type="text/javascript" src="../../e5script/jquery/jQuery-Validation-Engine/js/languages/jquery.validationEngine-zh_CN.js"></script>

<link type="text/css" rel="stylesheet" href="../../e5script/jquery/jQuery-Validation-Engine/css/validationEngine.jquery.css">
<link type="text/css" rel="stylesheet" href="../../e5style/e5form-custom.css">
<link type="text/css" rel="stylesheet" href="../script/bootstrap/css/bootstrap.css" />
<link type="text/css" rel="stylesheet" href="../css/form-custom.css">
<link type="text/css" rel="stylesheet" href="../css/sub-page.css"/>
<link type="text/css" rel="stylesheet" href="./css/iccodeuse.css" />

<script type="text/javascript" src="./js/xupdateInviteCode.js"></script>
</head>
<body style="background-color:#fff">
	<iframe id="iframe" name="iframe" style="display: none;"></iframe>
	<form id="form" method="post" action="xupdate.do" >
		<div class="wrap">
			<h1 id="title">修改邀请码</h1>
			<div class="mainBodyWrap">
				<table class="tablecontent">
					<tbody customwidth="841" customheight="255">
						<tr>
							<td class="ui-droppable" style="width: 423.090909123421px;">
								<span id="SPAN_icType" class="custform-span"> <label
									id="LABEL_icType" class="custform-label custform-label-require">邀请码层级</label>
									<div class="custform-from-wrap" id="DIV_icType">
										<select id="icType1" name="icType1" oldvalue="-" class="custform-select" style="width: 200px;">
											<c:if test="${icType == 4}">
												<option value="2">两层级</option>
												<option value="3">三层级</option>
												<option value="4" selected>四层级</option>
											</c:if>
											<c:if test="${icType == 3}">
												<option value="2">两层级</option>
												<option value="3" selected>三层级</option>
												<option value="4">四层级</option>
											</c:if>
											<c:if test="${icType == 2}">
												<option value="2" selected>两层级</option>
												<option value="3">三层级</option>
												<option value="4">四层级</option>
											</c:if>
											
										</select> 
										<input type="hidden" id="icType" name="icType"
											value="<c:out value="${icType}"/>">
										<span class="custform-postfix">
										 <span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
								</span>
							</td>
						</tr>
						<tr id="Level1">
							<td class="ui-droppable">
								<span id="SPAN_icLevel1" class="custform-span"> 
								<label id="LABEL_icLevel1" class="custform-label custform-label-require">层级1</label>
									<div class="custform-from-wrap" id="DIV_icLevel1">
										<input type="hidden" id="icLevel1" name="icLevel1"
											value="<c:out value="${icLevel1}"/>">
										<input type="text" id="icLevel1_1" name="icLevel1" value="<%=request.getAttribute("icLevel1")%>"
											   class="custform-input validate[maxSize[255],required]"
											   style="width: 200px;" readonly>
										<span class="custform-postfix"> 
											<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
								</span>
							</td>
							<td rowspan="1" class="ui-droppable"><span
								id="SPAN_icLevel1Index" class="custform-span"> <label
									id="LABEL_icLevel1Index"
									class="custform-label custform-label-require">层级1代号</label>
									<div class="custform-from-wrap" id="DIV_icLevel1Index">
										<input type="text" id="icLevel1Index" name="icLevel1Index"
											value="<%=request.getAttribute("icLevel1Index")%>"
											class="custform-input validate[maxSize[255],required]"
											style="width: 200px;" readonly="true"> <span class="custform-postfix">
											<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
							</span></td>
						</tr>
						<tr id="Level2">
							<td class="ui-droppable"><span id="SPAN_icLevel2"
								class="custform-span"> <label id="LABEL_icLevel2"
									class="custform-label custform-label-require">层级2</label>
									<div class="custform-from-wrap" id="DIV_icLevel2">
										<input type="text" id="icLevel2" name="icLevel2" value="<%=request.getAttribute("icLevel2")%>"
											class="custform-input validate[maxSize[255],required]"
											style="width: 200px;"> <span class="custform-postfix">
											<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
							</span></td>
							<td rowspan="1" class="ui-droppable"><span
								id="SPAN_icLevel2Index" class="custform-span"> <label
									id="LABEL_icLevel2Index" class="custform-label custform-label-require">层级2代号</label>
									<div class="custform-from-wrap" id="DIV_icLevel2Index">
										<input type="text" id="icLevel2Index" name="icLevel2Index"
											value="<%=request.getAttribute("icLevel2Index")%>" class="custform-input validate[maxSize[255],custom[onlyTwoNum],required]"
											style="width: 200px;"> <span class="custform-postfix">
											<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
							</span></td>
						</tr>
						<c:if test="${icType == 3 || icType == 4 }">
						<tr id="Level3">
							<td class="ui-droppable"><span id="SPAN_icLevel3"
								class="custform-span"> <label id="LABEL_icLevel3"
									class="custform-label custform-label-require">层级3</label>
									<div class="custform-from-wrap" id="DIV_icLevel3">
										<input type="text" id="icLevel3" name="icLevel3" value="<%=request.getAttribute("icLevel3")%>"
											class="custform-input validate[maxSize[255],required]"
											style="width: 200px;"> <span class="custform-postfix">
											<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
							</span></td>
							<td rowspan="1" class="ui-droppable"><span
								id="SPAN_icLevel3Index" class="custform-span"> <label
									id="LABEL_icLevel3Index" class="custform-label custform-label-require">层级3代号</label>
									<div class="custform-from-wrap" id="DIV_icLevel3Index">
										<input type="text" id="icLevel3Index" name="icLevel3Index"
											value="<%=request.getAttribute("icLevel3Index")%>" class="custform-input validate[maxSize[255],custom[onlyTwoNum],required]"
											style="width: 200px;"> <span class="custform-postfix">
											<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
							</span></td>
						</tr>
						</c:if>
						<c:if test="${icType == 4}">
						<tr id="Level4">
							<td class="ui-droppable"><span id="SPAN_icLevel4"
								class="custform-span"> <label id="LABEL_icLevel4"
									class="custform-label custform-label-require">层级4</label>
									<div class="custform-from-wrap" id="DIV_icLevel4">
										<input type="text" id="icLevel4" name="icLevel4" value="<%=request.getAttribute("icLevel4")%>"
											class="custform-input validate[maxSize[255],required]"
											style="width: 200px;"> <span class="custform-postfix">
											<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
							</span></td>
							<td rowspan="1" class="ui-droppable"><span
								id="SPAN_icLevel4Index" class="custform-span"> <label
									id="LABEL_icLevel4Index" class="custform-label custform-label-require">层级4代号</label>
									<div class="custform-from-wrap" id="DIV_icLevel4Index">
										<input type="text" id="icLevel4Index" name="icLevel4Index"
											value="<%=request.getAttribute("icLevel4Index")%>" class="custform-input validate[maxSize[255],custom[onlyTwoNum],required]"
											style="width: 200px;"> <span class="custform-postfix">
											<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
							</span></td>
						</tr>
						</c:if>
						<tr>
							<td class="ui-droppable"><span id="SPAN_icMetrics"
								class="custform-span"> <label id="LABEL_icMetrics"
									class="custform-label custform-label-require">指标(数量)</label>
									<div class="custform-from-wrap" id="DIV_icMetrics">
										<input type="text" id="icMetrics" name="icMetrics" value="<%=request.getAttribute("icMetrics")%>"
											class="custform-input validate[custom[integer],required]"
											style="width: 200px;"> <span class="custform-postfix">
											<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
							</span></td>
							<td rowspan="1" class="ui-droppable"><span
								id="SPAN_icInviterName" class="custform-span"> <label
									id="LABEL_icInviterName" class="custform-label custform-label-require">邀请人名称</label>
									<div class="custform-from-wrap" id="DIV_icInviterName">
										<input type="text" id="icInviterName" name="icInviterName"
											value="<%=request.getAttribute("icInviterName")%>" class="custform-input validate[maxSize[255],required]"
											style="width: 200px;"> <span class="custform-postfix">
											<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
							</span></td>
						</tr>
						<tr> 
    					<td class="ui-droppable" colspan="9">
      						<span id="SPAN_icExpiredDay" class="custform-span"> 
       						 <label id="LABEL_icExpiredDay" class="custform-label custform-label-require">有效期（天）</label>  
				         		 <input type="text" id="icExpiredDay" name="icExpiredDay" value="<%=request.getAttribute("icExpiredDay")%>" class="custform-input validate[required,custom[positiveNumber]]" style="margin-left:92px;width:200px;">  
      						</span>
   					    </td> 
   					    <!-- <td style="width:15%"></td>  
   					    <td class="ui-droppable" colspan="5">
   					    	<span class="custform-postfix">
					            	
					       	</span>
    					</td> -->
  					</tr>
						<tr style="display: none">
							<td>
								<div id="hiddenDiv">
									<ul id="tablehide-ul" class="ui-droppable"></ul>
								</div>
							</td>
						</tr>
					</tbody>

				</table>
			</div>
			<hr color="#2E8B57" size="1" width="100%" />
			<div class="btnarea text-center">
				<input class="btn" type="submit" id="btnSave" value="保存" /> <input
					class="btn" type="button" id="btnCancel" value="取消" />
			</div>
		</div>
		<input type="hidden" id="DocLibID" name="DocLibID" value="<%=request.getAttribute("DocLibID")%>">
		<input type="hidden" id="DocIDs" name="DocIDs" value="<%=request.getAttribute("DocIDs")%>">
		<input type="hidden" id="FVID" name="FVID" value="<%=request.getAttribute("FVID")%>">
		<input type="hidden" id="UUID" name="UUID" value="<%=request.getAttribute("UUID")%>">
		<input type="hidden" id="siteID" name="siteID" value="">
	</form>
	<script type="text/javascript">
	var siteID = getUrlVars("siteID");
	$("#siteID").val(siteID);
	//alert(siteID)
	function getUrlVars(name){
	    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
	    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
	    if (r != null) return unescape(r[2]); return null; //返回参数值
	}
		window.alert = function(str){
			return ;
		}

		var icLevel1 = "<c:out value="${icLevel1}"/>";
		$("#icLevel1_1").val(icLevel1);
		$("#icLevel1_1").attr("disabled",true); 
		
		$("#icType1").attr("disabled",true); 
		var icType = "<c:out value="${icType}"/>";
		if(icType == 2){
			/* $("#icLevel2Index").removeClass();
			//, */
			$("#icLevel2").attr("readonly",true);
			$("#icLevel2").removeClass();
			$("#icLevel2Index").removeClass();
			$("#icLevel2Index").addClass('custform-input validate[maxSize[255],custom[onlyTwoThreeNum],required]');
			$("#icLevel2").addClass('custform-input');
		}else if(icType == 3){
			$("#icLevel2Index").attr("readonly",true); 
			$("#icLevel2").attr("readonly",true); 
			$("#icLevel3").attr("readonly",true);
			
			$("#icLevel3Index").removeClass();
			$("#icLevel3").removeClass();
			$("#icLevel3").addClass('custform-input');
			$("#icLevel3Index").addClass('custform-input validate[maxSize[255],custom[onlyTwoThreeNum],required]');
		}else if(icType == 4){
			
			$("#icLevel2Index").attr("readonly",true); 
			$("#icLevel2").attr("readonly",true); 
			$("#icLevel3Index").attr("readonly",true); 
			$("#icLevel3").attr("readonly",true); 
			$("#icLevel4").attr("readonly",true);
			
			$("#icLevel4Index").removeClass();
			$("#icLevel4").removeClass();
			$("#icLevel4").addClass('custform-input');
			$("#icLevel4Index").addClass('custform-input validate[maxSize[255],custom[onlyTwoThreeNum],required]');
		}
	
	</script>
</body>
</html>