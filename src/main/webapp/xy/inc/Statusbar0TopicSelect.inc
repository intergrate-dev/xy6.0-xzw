<%-- ״̬������ҳ���򣩣�ֻ����Statusbar.js����Ҫ��չʱ������ҳ�����js--%>
<style type="text/css">
	.showDiv{
		width: 280px;
	    //height: 100px;
	    background: #fff;
	    position: ABSOLUTE;
	    /*left: 325px;
	    top: 80px;*/
	    z-index: 99;
	    border:1px solid #C0C0C0;
	    box-shadow:4px 4px 10px #C0C0C0;
	    border-radius: 3px;
	    display: none;
	}
	#main_doclist{
		position: relative;
	}


    #TH_CheckBox{
        width: 0.07px !important;
        text-align: center;
    }
    #TH_SYS_DOCUMENTID{
        width: 2.2px !important;
        text-align: center;
    }
    #TH_SYS_TOPIC{
        width: 40px !important;
    }
    #listing tr .list-check{
        width: 0.07px !important;
    }
    #listing tr td:nth-child(2){
        width: 2.2px !important;
        text-align: center;
    }
    #listing tr td:nth-child(3){
        width: 40px !important;
    }


</style>

<div id="main_doclist" style="overflow:hidden;">
	<ul id="status" class="status clearfix">
		<li class="status-pages"><i18n:message key="workspace.status.count"/>&nbsp;[&nbsp;<span id="PageCount"></span>&nbsp;]&nbsp;&nbsp;</li>
		<li style="display:none"><i18n:message key="workspace.status.pages"/>
			[&nbsp;<span id="CurrentPage"></span><span id="pageSepr">&nbsp;/&nbsp;</span><span id="Pages">1</span>&nbsp;]</span>&nbsp;&nbsp;
		</li>
		<li id="btnChangepage">&nbsp;<input type="text" maxlength="10" id="Turn2Page" title="<i18n:message key="workspace.status.turntopage.title"/>"/>&nbsp;</li>
		<li id="btnFirstpage" class="page-unselected" title="<i18n:message key="workspace.status.firstpage"/>"><<</li>
		<li id="btnLastpage" class="page-unselected" title="<i18n:message key="workspace.status.lastpage"/>"><</li>
		<li id="pagesArea"></li>
		<li id="btnNextpage" class="page-unselected" title="<i18n:message key="workspace.status.nextpage"/>">></li>
		<li id="btnFinalpage" class="page-unselected" title="<i18n:message key="workspace.status.finalpage"/>">>></li>
		<li>
			<select id="CountPerPage" name="CountPerPage" title="<i18n:message key="workspace.status.countperpage.title"/>"></select>
			<input name="ListPages" id="ListPages" type="hidden" value=""/>
		</li>
		<li id="listPageArea" title="<i18n:message key="workspace.status.listpage.title"/>"></li>

		<li <c:if test="${param.type!='SUBSCRIBE'}">style="display:none;"</c:if>>
			<a class="page-unselected" id="ListExportSpan" title="<i18n:message key="workspace.status.listpage.export.title"/>">
			<i class="icon-download"></i></a>
		</li>
		<li><a class="page-unselected" id="ListCustSpan"
			title="<i18n:message key="workspace.status.listpage.config.title"/>"><i class="icon-cog"></i></a></li>
		<li><a class="page-unselected" id="btnRefresh"
			title="<i18n:message key="workspace.status.refresh"/>"><i class="icon-refresh"></i></a></li>
	</ul>
	
	<div id="doclistframe" class="doclistframe" unselectable="on" onselectstart="return false;"></div>
<div class="showDiv"></div>
</div>
<script type="text/javascript">
	var statuserror = {
		"invalidPage": "<i18n:message key="workspace.status.error.invalidPage"/>",
		"noListPage" : "<i18n:message key="workspace.status.error.noListPage"/>"
	}
	var statusinfo = {
		"listcustom" : "<i18n:message key="workspace.status.listpage.config.title"/>",
		"export" : "<i18n:message key="workspace.status.listpage.export.title"/>",
		"remember_ok": "<i18n:message key="workspace.status.listpage.remember.ok"/>"
	}
</script>
<script type="text/javascript" src="../xy/script/StatusbarTopicSelect.js"></script>