<form id="form">
	<%@include file="CommonUpper.inc"%>
	<div class="underTop">
		<div id="divMain">
			<%@include file="CommonMiddle.inc"%>
			
			<li class="abstract1"><span class="left mgr">摘要：</span>
				<textarea id="a_abstract" name="a_abstract" class="validate[maxSize[2000]] a_abstract"><c:out value="${article.summary}"/></textarea>
				<c:if test="${canEditStyle}">
				<input class="left btn" type="button" id="btnEditAbstract" value="编辑样式" />
				</c:if>
			</li>

			<%@include file="CommonFooter.inc"%>
		</div>
		<div id="divRight">
			<%@include file="CommonRight.inc"%>
		</div>
	</div>
</form>
<style>
	.abstract1{
		margin-left: 46px;
		margin-top: 17px;
	}
</style>