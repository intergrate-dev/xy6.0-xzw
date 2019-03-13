<style type="text/css">
	#searchList,#queryReset{
		background: none;
		color: #0B4BAF;
		border: none;
	}
	#a_pubTime_0,{
		  position: absolute;
  			right: 215px;
  			top: 25px;
	}
	#a_pubTime_1{
		
	}
	.first{
		margin-top: 10px;
	}
	#searchList{
		margin-right: 15px;
	}
</style>
<div id="main_search" class="noQueryCust">
	<!-- query -->
	<div id="searchBtnArea">
		<div class="btn-group searchListGroup">
			<!--<div id="searchList" class="btn">
				检索
			</div>-->
			<img id="searchList" title="检索" src="../xy/stat/images/11.png" />
			<img id="queryReset" src="../xy/stat/images/12.png" title="<i18n:message key="workspace.query.reset.hint"/>" />
			<!--<div id="queryReset" class="btn" title="<i18n:message key="workspace.query.reset.hint"/>">
				重置
			</div>-->
			
			<!-- <ul class="dropdown-menu">
				<li id="toggleSearchAdvList"><a href="#"><i18n:message key="workspace.query.advanced"/></a></li>
				<li id="refreshList"><a href="#">刷新列表</a></li>
			</ul> -->
		</div>
		<!--div class="btn-group input-append">
			<input type="text" id="queryWord" value=""/>
			<button type="button" class="btn" id="btnSearch" title="<i18n:message key="workspace.query.quick"/>"><i class="icon icon-search"></i></button>
			<button type="button" class="btn" id="btnMore">...</button>
		</div-->
	</div>
	<div id="divQueryCust"></div>

	<!-- filters -->
	<ul class="filters">
		<li class="first">
			<select class="slct" name="Filters_0" id="Filters_0" style="display:none"title="<i18n:message key="workspace.status.filter.title"/>"></select>
		</li>
		<li>
			<select class="slct" name="Filters_1" id="Filters_1" style="display:none" title="<i18n:message key="workspace.status.filter.title"/>[2]"></select>
		</li>
		<li>
			<select class="slct" name="Filters_2" id="Filters_2" style="display:none" title="<i18n:message key="workspace.status.filter.title"/>[3]"></select>
		</li>
		<li>
			<select class="slct" name="Filters_3" id="Filters_3" style="display:none" title="<i18n:message key="workspace.status.filter.title"/>[4]"></select>
		</li>
		<li>
			<select class="slct" name="Filters_4" id="Filters_4" style="display:none" title="<i18n:message key="workspace.status.filter.title"/>[5]"></select>
		</li>
		<li>
			<select class="slct" name="Filters_5" id="Filters_5" style="display:none" title="<i18n:message key="workspace.status.filter.title"/>[6]"></select>
		</li>
		<li>
			<select class="slct" name="Filters_6" id="Filters_6" style="display:none" title="<i18n:message key="workspace.status.filter.title"/>[7]"></select>
		</li>
		<li>
			<select class="slct" name="Filters_7" id="Filters_7" style="display:none" title="<i18n:message key="workspace.status.filter.title"/>[8]"></select>
		</li>
		<li>
			<select class="slct" name="Filters_8" id="Filters_8" style="display:none" title="<i18n:message key="workspace.status.filter.title"/>[9]"></select>
		</li>
		<li>
			<select class="slct" name="Filters_9" id="Filters_9" style="display:none" title="<i18n:message key="workspace.status.filter.title"/>[10]"></select>
		</li>
	</ul>
</div>
<script type="text/javascript" src="../xy/script/Search.js"></script>