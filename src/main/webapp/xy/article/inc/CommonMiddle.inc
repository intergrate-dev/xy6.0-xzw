<style>
#SYS_TOPIC{
	border: 1px solid #ddd;
}
.author{
	margin-top: 10px;
}
</style>
<li class="title1" id="liTitle"><span class="left require">*</span><span class="left title1">标题：</span>
	<input style="text-align:left;" type="text" id="SYS_TOPIC" name="SYS_TOPIC" value="<c:out value="${article.topic}"/>"
		class="validate[maxSize[1024],required]" placeholder="">

	<c:if test="${canEditStyle}">
	<input type="button" class="btn edui-default" id="btnTitleAdv" value="编辑样式">
	</c:if>
	<lable id="lbwordcount" class="edui-default"></lable>
	<label style="display: inline;" for="a_copyright">
		<input type="checkbox" id="a_copyright" name="a_copyright" <c:if test="${article.copyright == 1}">checked</c:if>>
		原创
	</label>
</li>

<div style="clear: both;"></div>
<li class="author mgl"><span class="left title1">作者：</span><input class="left artMeg" type="text" 
	id="SYS_AUTHORS" name="SYS_AUTHORS" value="<c:out value="${article.author}"/>" class="validate[maxSize[255]]">
</li>
<li class="source"><span class="left title1">来源：</span><input class="left artMeg"  type="text" id="findSourceInput" 
	value="<c:out value="${article.source}"/>" class="validate[maxSize[255]]">
		<!--<input type="hidden" id="a_source" name="a_source" value="<c:out value="${article.source}"/>">
		<input type="hidden" id="a_sourceID" name="a_sourceID" value="<c:out value="${article.sourceID}"/>">  -->
	<input class="left btn btnsource" type="button" id="btnSource" title="选择来源" value="选择"/>
</li>
<li class="sourceUrl"><span class="title1">来源链接：</span>
	<input class="artMeg" type="text" id="a_sourceUrl" name="a_sourceUrl" value="<c:out value="${article.sourceUrl}"/>"
			class="validate[maxSize[255]]">
</li>
