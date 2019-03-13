<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>

<script type="text/javascript" src="../../e5script/jquery/jquery.min.js"></script>
<!-- <script type="text/javascript" src="../script/bootstrap/js/bootstrap.js"></script> -->
<%=request.getAttribute("formHead")%>
<link type="text/css" rel="stylesheet" href="../script/bootstrap/css/bootstrap.css" />
<link type="text/css" rel="stylesheet" href="../css/form-custom.css"/>
<link type="text/css" rel="stylesheet" href="../css/sub-page.css"/>
<link type="text/css" rel="stylesheet" href="./css/iccodeuse.css" />

<script type="text/javascript" src="./js/xgenerateInviteCode.js"></script>
</head>
<body style="background-color:#fff">
	<iframe id="iframe" name="iframe" style="display: none;"></iframe>
	<form id="form" method="post" action="xgenerate.do" >
		<div class="wrap">
			<h1 id="title">生成邀请码</h1>
			<%-- <%=request.getAttribute("formContent")%> --%>

			<div class="mainBodyWrap">
				<table class="tablecontent">
					<tbody customwidth="841" customheight="255">
						<tr>
							<td class="ui-droppable" style="">
								<span id="SPAN_icType" class="custform-span"> 
								<label id="LABEL_icType" class="custform-label custform-label-require">邀请码层级</label>
									<div class="custform-from-wrap" id="DIV_icType">

										<select id="icType" name="icType" oldvalue="-" class="custform-select" style="width: 200px;"  disabled="disabled" >
											<option value="2" >两层级</option>
											<option value="3" >三层级</option>
											<option value="4" >四层级</option>
										</select>

										<%--<input type="text" id="icType" name="icType" value="2"--%>
										<%--class="custform-select" disabled="disabled"--%>
										<%--style="width: 200px;">--%>

										<%--<span class="custform-postfix">--%>
										 	<%--<span class="custform-aftertxt ui-draggable"> </span>--%>
										<%--</span>--%>
									</div>
								</span>
								<div id="tips_1" class="tip"> 
    				 				<div id="help_content_1" class="help_content" style="display: none;">
		             					<i class="dropdown_arrow out"></i>
		             					<i class="dropdown_arrow in"></i>
		             					<dl class="help-change-list" id="pop_items_info">
		                					<dt>邀请码层级</dt>
		                    				<dd>邀请码构成的层级数。<br />
		                    				</dd>
		                    				<dt><b style="color:red;">注意：</b></dt>
		                    				<dd>末尾层级代号起始00，系统会根据生成数量递增补全。</dd>
		                				</dl>
		             				</div>
    			  				</div>
							</td>
							<td rowspan="1" class="ui-droppable" style="margin-left:-8px;"><span
								id="SPAN_icInviterName" class="custform-span"> <label
									id="LABEL_icInviterName" class="custform-label custform-label-require">邀请人名称</label>
									<div class="custform-from-wrap" id="DIV_icInviterName">
										<input type="text" id="icInviterName" name="icInviterName"
											value="" class="custform-input validate[maxSize[255],required]"
											style="width: 200px;"> <span class="custform-postfix">
											<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
							</span></td>
						</tr>
						<tr id="Level1">
							<%--<td class="ui-droppable">--%>
								<%--<span id="SPAN_icLevel1" class="custform-span"> --%>
								<%--<label id="LABEL_icLevel1" class="custform-label custform-label-require">层级1</label>--%>
									<%--<div class="custform-from-wrap" id="DIV_icLevel1">--%>
										<%--<select id="icLevel1" name="icLevel1" oldvalue="-"--%>
											<%--class="custform-select" style="width: 200px;">--%>
											<%--<option value="南京">南京</option>--%>
											<%--<option value="镇江">镇江</option>--%>
											<%--<option value="泰州">泰州</option>--%>
											<%--<option value="宿迁">宿迁</option>--%>
											<%--<option value="无锡">无锡</option>--%>
											<%--<option value="徐州">徐州</option>--%>
											<%--<option value="常州">常州</option>--%>
											<%--<option value="苏州">苏州</option>--%>
											<%--<option value="南通">南通</option>--%>
											<%--<option value="连云港">连云港</option>--%>
											<%--<option value="淮安">淮安</option>--%>
											<%--<option value="盐城">盐城</option>--%>
											<%--<option value="扬州">扬州</option>--%>
											<%--<option value="省直">省直</option>--%>
											<%--<option value="集团">集团</option>--%>
											<%--<option value="省团">省团</option>--%>
										<%--</select> <span class="custform-postfix"> <span--%>
											<%--class="custform-aftertxt ui-draggable"> </span>--%>
										<%--</span>--%>
									<%--</div>--%>
								<%--</span>--%>
							<%--</td>--%>
							<%--<td rowspan="1" class="ui-droppable">--%>
							<%--<span id="SPAN_icLevel1Index" class="custform-span"> --%>
								<%--<label id="LABEL_icLevel1Index" class="custform-label custform-label-require">层级1代号</label>--%>
									<%--<div class="custform-from-wrap" id="DIV_icLevel1Index">--%>
										<%--<input type="text" id="icLevel1Index" name="icLevel1Index"--%>
											<%--value="A"--%>
											<%--class="custform-input validate[maxSize[255],required]"--%>
											<%--style="width: 200px;" readonly="true"> <span class="custform-postfix">--%>
											<%--<span class="custform-aftertxt ui-draggable"> </span>--%>
										<%--</span>--%>
									<%--</div>--%>
							<%--</span></td>--%>
							<td class="ui-droppable">
								<span id="SPAN_icLevel1" class="custform-span">
									<label id="LABEL_icLevel1" class="custform-label custform-label-require">层级1</label>
									<div class="custform-from-wrap" id="DIV_icLevel1">
										<input type="text" id="icLevel1" name="icLevel1" value=""
										class="custform-input validate[maxSize[255],required]" onfocus=this.blur()
										style="width: 200px;"> <span class="custform-postfix">
										<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
								</span>
							</td>
							<td rowspan="1" class="ui-droppable">
								<span id="SPAN_icLevel1Index" class="custform-span">
									<label id="LABEL_icLevel1Index" class="custform-label custform-label-require">层级1代号</label>
									<div class="custform-from-wrap" id="DIV_icLevel1Index">
										<input type="text" id="icLevel1Index" name="icLevel1Index" onfocus=this.blur()
										value="" class="custform-input validate[maxSize[255],custom[onlyTwoNum],required]"
										style="width: 200px;"> <span class="custform-postfix">
										<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
								</span>
							</td>
						</tr>
						<tr id="Level2">
							<td class="ui-droppable">
								<span id="SPAN_icLevel2" class="custform-span"> 
									<label id="LABEL_icLevel2" class="custform-label custform-label-require">层级2</label>
									<div class="custform-from-wrap" id="DIV_icLevel2">
										<input type="text" id="icLevel2" name="icLevel2" value=""
											class="custform-input validate[maxSize[255],required]" onfocus=this.blur()
											style="width: 200px;"> <span class="custform-postfix">
											<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
							</span></td>
							<td rowspan="1" class="ui-droppable">
								<span id="SPAN_icLevel2Index" class="custform-span"> 
									<label id="LABEL_icLevel2Index" class="custform-label custform-label-require">层级2代号</label>
									<div class="custform-from-wrap" id="DIV_icLevel2Index">
										<input type="text" id="icLevel2Index" name="icLevel2Index" onfocus=this.blur()
											value="" class="custform-input validate[maxSize[255],custom[onlyTwoNum],required]"
											style="width: 200px;"> <span class="custform-postfix">
											<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
							</span></td>
						</tr>
						<tr id="Level3">
							<td class="ui-droppable">
								<span id="SPAN_icLevel3" class="custform-span"> 
									<label id="LABEL_icLevel3" class="custform-label custform-label-require">层级3</label>
									<div class="custform-from-wrap" id="DIV_icLevel3">
										<input type="text" id="icLevel3" name="icLevel3" value=""
											class="custform-input validate[maxSize[255],required]"  onfocus=this.blur()
											style="width: 200px;"> <span class="custform-postfix">
											<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
							</span></td>
							<td rowspan="1" class="ui-droppable">
								<span id="SPAN_icLevel3Index" class="custform-span"> 
									<label id="LABEL_icLevel3Index" class="custform-label custform-label-require">层级3代号</label>
									<div class="custform-from-wrap" id="DIV_icLevel3Index">
										<input type="text" id="icLevel3Index" name="icLevel3Index"  onfocus=this.blur()
											value="" class="custform-input validate[maxSize[255],custom[onlyTwoNum],required]"
											style="width: 200px;"> <span class="custform-postfix">
											<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
							</span></td>
						</tr>
						<tr>
							<td class="ui-droppable"><span id="SPAN_icMetrics"
								class="custform-span"> <label id="LABEL_icMetrics"
									class="custform-label custform-label-require">指标(数量)</label>
									<div class="custform-from-wrap" id="DIV_icMetrics">
										<input type="text" id="icMetrics" name="icMetrics" value=""
											class="custform-input validate[custom[positiveNumber],required]"
											style="width: 200px;"> <span class="custform-postfix">
											<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
							</span></td>
							<td rowspan="1" class="ui-droppable"><span
								id="SPAN_icGenerateNum" class="custform-span"> <label
									id="LABEL_icGenerateNum" class="custform-label custform-label-require">生成数量</label>
									<div class="custform-from-wrap" id="DIV_icGenerateNum">
										<input type="text" id="icGenerateNum" name="icGenerateNum"
											value="" class="custform-input validate[custom[range1],required]"
											style="width: 200px;"> <span class="custform-postfix">
											<span class="custform-aftertxt ui-draggable"> </span>
										</span>
									</div>
							</span>
							</td>
						</tr>
						<tr> 
    					<td class="ui-droppable" colspan="9">
      						<span id="SPAN_icExpiredDay" class="custform-span"> 
       						 <label id="LABEL_icExpiredDay" class="custform-label custform-label-require">有效期（天）</label>  
				         		 <input type="text" id="icExpiredDay" name="icExpiredDay" value="" class="custform-input validate[required,custom[positiveNumber]]" style="margin-left:93px;width:200px;">  
      						</span>
   					    </td> 
   					    <!-- <td style="width:15%"></td>  
   					    <td class="ui-droppable" colspan="5">
   					    	<span class="custform-postfix">
					            	
					       	</span>
    					</td> -->
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
		<input type="hidden" id="siteID" name="siteID" value="">
		<input type="hidden" id="DocLibID" name="DocLibID" value="<%=request.getAttribute("DocLibID")%>">
		<input type="hidden" id="DocID" name="DocID" value="<%=request.getAttribute("DocIDs")%>">
		<input type="hidden" id="FVID" name="FVID" value="<%=request.getAttribute("FVID")%>">
		<input type="hidden" id="UUID" name="UUID" value="<%=request.getAttribute("UUID")%>">
	</form>
</body>
<script type="text/javascript">
var siteID = getUrlVars("siteID");
$("#siteID").val(siteID);
//alert(siteID)
function getUrlVars(name){
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}
</script>
</html>