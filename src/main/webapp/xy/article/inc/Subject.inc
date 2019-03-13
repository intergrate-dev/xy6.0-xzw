<form id="form">
	<%@include file="CommonUpper.inc"%>
	<div class="underTop">
		<div id="divMain">
			<%@include file="CommonMiddle.inc"%>
			
			<li class="mgl abstract1"><span class="left mgr">摘要：</span>
				<textarea id="a_abstract" name="a_abstract" class="validate[maxSize[2000]] a_abstract"><c:out value="${article.summary}"/></textarea>
				<c:if test="${canEditStyle}">
				<input class="left btn" type="button" id="btnEditAbstract" value="编辑样式" />
				</c:if>
			</li>
			<c:if test="${ch == 1}">
			<li class="link1">
				<span class="left require star">*</span>
				<span class="left mgr">选择话题：</span>
				<input class="specialModelInput validate[required]" type="text" name="a_template" id="a_template" value="${article.template}" readonly/>
				<input type="hidden" name="a_linkID" id="a_linkID" value="${article.linkID}"/>
				<input class="btn " type="button" id="btnSubject" value="选择" />
			</li>
			</c:if>
		</div>
		<div id="divRight">
			<%@include file="CommonRight.inc"%>
		</div>
	</div>
</form>
<style type="text/css">
	.specialModelInput{
		width: 500px !important;
		float: left;
		margin-right: 10px;
	}
	.specialModel{
		margin-left: 20px;
	
	}
	.modelBtn{
		width: 185px !important;
    	height: 45px;
    	font-size: 25px;
	}
	.abstract1{
		margin-top: 24px;
		height: 160px;
	}
	#a_linkName{
		box-shadow: inset 0 1px 1px rgba(0,0,0,0.075);
		width: 513px;
		border: 1px solid #ddd;
		height: 28px;
		margin-left: 0px;
	}
	#btnlinkColName{
		margin-left: 5px
	}
	
	.abstract2 {
		margin-right: 18px;
	}
	
	
	.star{
		margin-left: 10px;
		margin-top: 0;
	}	
</style>