<style type="text/css">
	#wrapMain{
		margin-left:0px; 
		padding: 0;
	}
	.channels{
		margin-left: 10px;
	}
	#searchList,#queryReset{
		background: none;
		color: #0B4BAF;
		border: none;
	}
	#searchList{
		margin-right: 15px;
	}
	#a_region{
	    border: 1px solid #ddd;
	    line-height: 26px;
	    padding-left: 5px;
	    color: #333;
	    margin: 0 5px;
	    border-radius: 0;
	    background: none;
        cursor: pointer;
	}
	.querycontent .custform-span {
	    margin-left: -8px;
	}
	.querycontent .custform-controls .custform-select {
	    width: 68px;
	    padding-left: 2px;
	}
	.querycontent .custform-controls .custform-input34 {
	    width: 82px;
	}
	.querycontent .custform-controls {
	    margin-left: 71px;
	}
	.querycontent .custform-label {
	    width: 56px;
	}
	.querycontent ul{
		padding-top: 1px;
	} 
</style>
<div id="main_search" class="noQueryCust" style="display:block">
	<ul class="channels">
		<li class="channelTab select" queryID="1" rule="a_status_EQ_0_AND_a_siteID_EQ_${siteID}" listID="<c:out value='${ListID1}'/>"/>待审核</li>
		<li class="channelTab" queryID="2" rule="a_status_EQ_1_AND_a_siteID_EQ_${siteID}" listID="<c:out value='${ListID2}'/>"/>未采用</li>
		<li class="channelTab" queryID="3" rule="a_status_EQ_2_AND_a_siteID_EQ_${siteID}" listID="<c:out value='${ListID3}'/>"/>已采用</li>
	</ul>
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
</div>
<script type="text/javascript" src="script/Search.js"></script>
<script type="text/javascript" src="script/queryAudit.js"></script>
<script>
//页面没有左边资源树，因此页面打开后就发送模拟的点击消息，从而显示列表
e5.mod("workspace.resourcetree",function() {
	var api;
	var defaultClick = function(evt) {
		var src = $(evt.target);
		$(".channelTab").removeClass("select");
		src.addClass("select");
		
		var param = new ResourceParam();
		for (var name in main_param) 
			param[name] = main_param[name];
		param.ruleFormula = src.attr("rule");
		param.listID = src.attr("listID");
		api.broadcast("resourceTopic", param);
		nis_query.init();
	}
	//检查statusbar是否加载完毕，加载完毕才发送点击消息
	var checkLoad = function() {
		var statusReady = e5.mods["workspace.doclistMain"].isReady;
		var searchReady = e5.mods["workspace.search"].isReady;
		var ready = !!statusReady&&!!searchReady&&statusReady()&&searchReady();
		if (!ready) {
			setTimeout(checkLoad, 100);
			return;
		}
		$(".channelTab").first().click();
	}
	var init = function(sandbox) {
		api = sandbox;
		
		var tabs = $(".channelTab");
		tabs.click(defaultClick);
		
		if (tabs.length <= 1) {
			$(".channels").hide();
		}
	},
	onload = function(){
		checkLoad();
	}
	return {
		init: init,
		onload:onload
	}
});
</script>