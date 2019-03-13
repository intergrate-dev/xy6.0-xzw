<style>
#btnColumnRel{
	height: 16px;
	margin-top: -10px;
	line-height: 16px;
	font-size: 12px;
}
.input{
	font-size:12px !important;
	padding:0 !important; 
	width: 130px !important; 
	background-color: #fff !important;
}
#a_columnRel{
	border-radius: 0 !important; 
	height: 15px !important; 
	width: 255px !important;
}
.btnColumn{
	height: 14px; 
	line-height: 14px;
}

</style>
                                                   
<input type="hidden" id="isNew" name="isNew" value="<c:out value="${isNew}"/>">  
<input type="hidden" id="a_siteID" name="a_siteID" value="<c:out value="${siteID}"/>">
<input type="hidden" id="currentColID" name="currentColID" value="<c:out value="${colID}"/>">
<input type="hidden" id="a_type" name="a_type" value="<c:out value="${article.type}"/>">  
<input type="hidden" id="a_channel" name="a_channel" value="<c:out value="${article.channel}"/>">  
<input type="hidden" id="DocLibID" name="DocLibID" value="<c:out value="${article.docLibID}"/>">  
<input type="hidden" id="DocID" name="DocID" value="<c:out value="${article.docID}"/>">  
<input type="hidden" id="UUID" name="UUID" value="<c:out value="${UUID}"/>">
<input type="hidden" id="a_source" name="a_source" value="<c:out value="${article.source}"/>"> 
<input type="hidden" id="a_sourceID" name="a_sourceID" value="<c:out value="${article.sourceID}"/>">
<input name="a_isSensitive" id="a_isSensitive" type="hidden" value='<c:out value="${article.isSensitive}"/>'/>
<input name="a_isIllegal" id="a_isIllegal" type="hidden" value='<c:out value="${article.isIllegal}"/>'/>
<input name="a_catID" id="a_catID" type="hidden" value='<c:out value="${groupID}"/>'/>

<div class="clearfix" id="headerButton">
	<li class="idName">ID：<c:out value="${article.docID}"/></li>
	<!--
	<c:if test="${article.columnID == 0 and channelCount > 0}">
		<c:forEach var="ch" items="${channels}">
		<c:if test="${ch != null}">
		<li class="columnName">
			<label id="label" for="channel<c:out value="${ch.id}"/>">
				<input style="margin-bottom: 6px;" type="checkbox" id="channel<c:out value="${ch.id}"/>" name="channel<c:out value="${ch.id}"/>"/>
				<c:out value="${ch.name}"/>
			</label>
			<input type="hidden" id="<c:out value="${ch.id}"/>_columnID" name="<c:out value="${ch.id}"/>_columnID">
			<input class="input" type="text" id="<c:out value="${ch.id}"/>_column" name="<c:out value="${ch.id}"/>_column" 
					readonly="true" placeholder="请选择栏目">  
			<input type="button" for="<c:out value="${ch.id}"/>_column" ch="<c:out value="${ch.id}"/>"
					value="选择" class="btn btnColumn" title="选择栏目">
		</li>
		</c:if>
		</c:forEach>
	</c:if>
	-->
	<c:if test="${article.columnID > 0}">
		<li class="columnName">栏目：<span title="${article.column}"><c:out value="${article.column}"/></span>
			<input type="hidden" id="a_columnID" name="a_columnID" value="<c:out value="${article.columnID}"/>">  
			<input type="hidden" id="a_column" name="a_column" value="<c:out value="${article.column}"/>" readonly="true">  
			<input  type="button" id="btnColumn" for="a_column" ch="<c:out value="${ch}"/>"
				value="选择" class="btn btnColumn" title="选择栏目">
		</li>
		<li><span style="margin-top: 2px;" class="left topmargin">关联栏目：</span>
			<c:if test="${!isNew}">
				<span title="<c:out value="${article.columnRel}"/>"><c:out value="${article.columnRel}"/></span>
				<input style="border-radius: 0;" type="hidden" id="a_columnRel" name="a_columnRel" value="<c:out value="${article.columnRel}"/>" readonly="true" class="short">  
			</c:if>
			<c:if test="${isNew}">
				<input style="cursor:pointer;" type="text" id="a_columnRel" for="a_columnRel" ch="<c:out value="${ch}"/>" name="a_columnRel" value="<c:out value="${article.columnRel}"/>" readonly="true" class="short">
				<input type="button" id="btnColumnRel" for="a_columnRel" ch="<c:out value="${ch}"/>"
					value="选择" class="btn" title="选择关联栏目">
			</c:if>
			<input type="hidden" id="a_columnRelID" name="a_columnRelID" value="<c:out value="${article.columnRelID}"/>">  
		</li>
	</c:if>
	<li class="btngroup">
		<input class="btnNoSenIll" type="button" id="btnSenIll" style="display:none;"/>
		<c:if test="${article.columnID > 0}">
			<c:if test="${article.status == 0 or article.status == 4 or article.status == 5 or article.status == 7}">
				<input class="btn btn-success button" type="button" id="btnSave" value="保存"/>
			</c:if>
		</c:if>
		<c:if test="${article.columnID == 0}">
			<input class="btn btn-success button" type="button" id="btnSave" value="保存"/>
		</c:if>
    	<c:if test="${pub_or_audit == 2}">
    	    <input class="btn btn-primary button" type="button" id="btnPublish" value="发布"/>
    	</c:if>
    	<c:if test="${pub_or_audit == 1}">
    	    <input class="btn btn-primary button" type="button" id="btnPublish" value="送审"/>
    	</c:if>
		<input class="btn btn-danger" type="button" id="btnCancel" value="关闭"/>
	</li>
	
</div>
