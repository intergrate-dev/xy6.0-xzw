<form id="form">
	<%@include file="CommonUpper.inc"%>
	<div class="underTop">
		<div id="divMain">
			<%@include file="CommonMiddle.inc"%>
			
			<li class="abstract1 mgl"><span class="left mgr">摘要：</span>
				<textarea id="a_abstract" name="a_abstract" class="validate[maxSize[2000]] a_abstract"><c:out value="${article.summary}"/></textarea>
				<c:if test="${canEditStyle}">
				<input class="left btn" type="button" id="btnEditAbstract" value="编辑样式" />
				</c:if>
			</li>

			<li class="link1" style="margin-left: -25px;">
				<span class="left require star" style="margin-top:2px;">*</span>
				<span class="left mgr">直播话题：</span>
				<input class="specialModelInput validate[required]" type="text" name="a_linkName" id="a_linkName" value="<c:out value="${article.linkName}"/>" readonly/>
				<input type="hidden" name="a_linkID" id="a_linkID" value="<c:out value="${article.linkID}"/>"/>
				<input class="btn " type="button" id="btnLiveTemplate" value="选择" style="margin-top:-10px;" />
			</li>

			<%--<c:if test="${article.linkID > 0}">
				<li class="link1"><span class="left abstract2">对应话题：</span>
					<c:out value="${article.linkName}"/>
				</li>
			</c:if>--%>
		</div>
		<div id="divRight">
			<%@include file="CommonRight.inc"%>
		</div>
	</div>
</form>
<style type="text/css">
.link1 {
  margin-left: 18px;
  margin-top: 24px;
  
  clear: both;
}
.abstract1{
	
	margin-top: 24px;
}

/*.title1{
	margin-right: 4px !important;
}*/
</style>