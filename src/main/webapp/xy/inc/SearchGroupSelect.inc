<style type="text/css">
	#searchList,#queryReset{
		background: none;
		margin-bottom: 12px;
		border: none;
		background-image: linear-gradient(to bottom, #ffffff, #e6e6e6);
		border: 1px solid #cccccc;
		padding: 2px 12px;
		
		border-radius: 3px;
		font-size:12px;
		width:50px;
	}
	#searchList {
		margin-left: -5px;
	}
	#queryReset{
		margin-left: 2px;
	}
	.first{
		margin-top: 10px;
	}
	#doSave{
		background-color: #00a0e6;
		margin-left: 5px;
	}
	.btngroup {
	    margin: 10px 1px;
	    font-family: microsoft yahei;
	    color: #fff;
	    border: none;
	    background: #b1b1b1;
	    border-radius: 3px;
	    padding: 3px 20px;
	    font-size: 12px;
	    margin-top: -3px;
	    
	}
	#doCancel{
		background: #b1b1b1;
	}
	
</style>
<div id="main_search" class="noQueryCust" style="width: 82%;">
	<!-- query -->
	<div id="searchBtnArea">
		<div class="btn-group searchListGroup">
			<input class="btn" id="searchList" type="button" value="查找" />
			<input class="btn" id="queryReset" type="button" value="清空" />
			<c:if test="${param.editor != '1'}">
            <input class="btngroup" id="doSave" type="button" value="确定" />
            <input class="btngroup" id="doCancel" type="button" value="取消" />
			</c:if>
		</div>
	</div>
	<div id="divQueryCust"></div>
	<!-- filters -->
	<ul class="filters">
		<li class="first">
			<select class="slct" name="Filters_0" id="Filters_0" style="display:none"title="<i18n:message key="workspace.status.filter.title"/>"></select>
		</li>
	</ul>
</div>
<script type="text/javascript" src="../xy/script/Search.js"></script>