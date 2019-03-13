
<form id="form">
	<%@include file="CommonUpper.inc"%>
	<div class="underTop">
		<div id="divMain">
			<%@include file="CommonMiddle.inc"%>
			
			<li class="link1"><span class="left require1" style="margin-top: 0;">*</span><span class="left mgr">链接：</span>
				<textarea id="a_url" name="a_url" class="validate[maxSize[1023],required] a_url"><c:out value="${article.contentUrl}"/></textarea>
			</li>
			<li class="abstract1"><span class="left mgr">摘要：</span>
				<textarea id="a_abstract" name="a_abstract" class="validate[maxSize[2000]] a_abstract"><c:out value="${article.summary}"/></textarea>
				<span id="abstractCount"></span>
				<c:if test="${canEditStyle}">
				<input class="left btn" type="button" id="btnEditAbstract" value="编辑样式" />
				</c:if>
			</li>
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
	
	.require1{
		margin-left: 35px;
	    margin-right: 5px;
	   	color: red;
	}
	
</style>