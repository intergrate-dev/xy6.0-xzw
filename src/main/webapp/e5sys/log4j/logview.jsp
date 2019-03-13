<%@include file="../../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5syslog" changeResponseLocale="false"/>
<%@page import="java.io.*" %>
<%@page import="java.util.*" %>
<%@page import="com.founder.e5.commons.Log4jUtil" %>
<%@page import="com.founder.e5.commons.io.*" %>
<%@page import="com.founder.e5.commons.Log" %>
<%@page import="com.founder.e5.context.Context" %>

<%@page import="com.founder.e5.web.sys.log4j.LogBean" %>
<%@page import="com.founder.e5.web.WebUtil" %>
<html>
<%
	Log log = Context.getLog( "e5.sys" );

	// all log4j output file
	List logfiles = Log4jUtil.getAllLogFile();
	List availDates = new ArrayList();
	availDates.add( "current" );

	// -------------------- form submit process

	String logfile = request.getParameter( "logfile" );
	String date = request.getParameter( "date" );	//yyyy-mm-dd
	int pageSize = WebUtil.getIntParam( request, "pageSize", 25);
	int pageNo = WebUtil.getIntParam( request, "pageNo", 1);

	// check if paramter valid
	if ( logfile == null && logfiles != null && logfiles.size() > 0 ) {
		logfile = logfiles.get(0).toString();
	} else {
		boolean found = false;
		File curFile = new File(logfile);
		for (int i = 0; i < logfiles.size(); i++) {
			File file0 = (File)logfiles.get(i);
			if (file0.equals(curFile)) {
				found = true;
				break;
			}
		}
		if (!found) {
			out.println("Invalid File:" + logfile);
			return;
		}
	}

	if ( date == null || !( new File( logfile + "." + date ).exists() ) )
		date = "";

	int totalRecord = 0;
	int totalPage = 0;

	if ( logfile != null && !"".equals(logfile)) {
		logfile = logfile.replace( '\\', '/' );
		availDates.addAll( Log4jUtil.getAvailableDates( logfile ) );

		String pathname = logfile;
		if ( !"".equals(date) ) pathname = logfile + "." + date;
		log.debug( "logfile: " + pathname );
		
		RecordBasedReader reader = RecordBasedReaderRepository.getReader( pathname );
		totalRecord = reader.totalRecords();
		totalPage = ( int ) Math.ceil( ( double ) totalRecord / ( double ) pageSize );

		// validate parameters to avoid inconsitient situation
		if ( pageSize < 25 ) pageSize = 25;
		if ( pageNo < 1 ) pageNo = 1;
		if ( pageNo > totalPage ) pageNo = totalPage;
		if ( totalRecord == 0 ) pageNo = 0;

		//System.out.println( "pageNo = " + pageNo );
		//System.out.println( "pageSize = " + pageSize );

		// current page records
		if ( pageNo > 0 ) {
			int beginRecord = ( pageNo - 1 ) * pageSize + 1;
			List logRecords = reader.read( beginRecord, pageSize );

			int recordNum = beginRecord;
			ArrayList result = new ArrayList();

			if ( logRecords != null ) {
				for( Iterator i = logRecords.iterator(); i.hasNext(); ) {
					String record = (String) i.next();
					LogBean bean = new LogBean( recordNum++, record );
					result.add( bean );
				}
			}

			request.setAttribute( "result", result );
		}
	}
	//-----------------------------------------
%>
	<head>
		<title><i18n:message key="log4j.view.title"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript">
			function initForm() {
				document.logquery.logfile.value = "<%=logfile%>";
				document.logquery.date.value = "<%=date%>";
				document.logquery.pageSize.value = <%=pageSize%>;
			}
			function prevPage(){
				document.logquery.pageNo.value--;
				document.logquery.submit();
			}
			function nextPage(){
				document.logquery.pageNo.value++;
				document.logquery.submit();
			}
			function firstPage(){
				document.logquery.pageNo.value = 1;
				document.logquery.submit();
			}
			function lastPage(){
				document.logquery.pageNo.value = <%=totalPage%>;
				document.logquery.submit();
			}
			function refresh(){
				document.logquery.submit();
			}
			function clearDateAndRefresh(){
				document.logquery.date.value="";
				document.logquery.submit();
			}
		</script>
		<link type="text/css" rel="stylesheet" href="../../e5style/reset.css"/>
		<link type="text/css" rel="stylesheet" href="../../e5style/sys-main-body-style.css"/>
		<style type="text/css">
			textarea{
				width:90%;
				height:100%;
			}
			.area{
				background: #F3F3F3;
				border: 1px solid #D1D1D1;
				padding: 5px;
			}
			.area td{
				padding:0 10px;
			}
			.area img{
				cursor: pointer;
			}
			.area td.clear-padding-left{
				padding-left:0;
			}
			.area td.clear-padding-right{
				padding-right:0;
			}
			.table{
				margin:20px auto;
			}
			.fr{
				float: right;
			}
			.fl{
				float: left;
			}
		</style>
	</head>
	<body onload="initForm()">
		<div class="mainBodyWrap">
			<div class="area clearfix">
				<form name="logquery" method="post" action="logview.jsp">
					<input name="pageNo" type="hidden" value="<%=pageNo%>"/>
					<table cellpadding="0" cellspacing="0" class="fl">
						<tr>
							<td class="clear-padding-right"><i18n:message key="log4j.file.choose_file"/></td>
							<td class="clear-padding-left">
								<select name="logfile" onchange="clearDateAndRefresh()">
								<%
								for ( Iterator i = logfiles.iterator(); i.hasNext(); ) {
									File file = (File) i.next();
									String pathname = file.toString().replace( '\\', '/' );
									%>
									<option value="<%=pathname%>"><%=file.getName()%></option>
								<%
								}
								%>
								</select>
							</td>
							<td class="clear-padding-right"><i18n:message key="log4j.file.choose_date"/></td>
							<td class="clear-padding-left">
								<select name="date" onchange="refresh()">
								<%
								for ( Iterator i = availDates.iterator(); i.hasNext(); ) {
									String name = (String) i.next();
									String value = name;
									if ( "current".equals(name) ) {
										value = "";
										name = WebUtil.getLocalString( request,
										"i18n.e5syslog",
										"log4j.view.current" );
									}
									%>
									<option value="<%=value%>"><%=name%></option>
								<%}%>
								</select>
							</td>
						</tr>
					</table>
					<table cellpadding="0" cellspacing="0" class="fr">
						<tr>
							<td>
								<img src="../../images/startpage.gif" width="20" height="20" onclick='firstPage()'/>
							</td>
							<td>
								<img src="../../images/lastpage.gif" width="20" height="20" onclick='prevPage()'/>
							</td>
							<td>
								<select name="pageSize" onChange="refresh()">
									<option value="25">25</option>
									<option value="50">50</option>
									<option value="100">100</option>
									<option value="250">250</option>
									<option value="500">500</option>
								</select>
							</td>
							<td>
								<img src="../../images/nextpage.gif" width="20" height="20" onclick='nextPage()'/>
							</td>
							<td>
								<img src="../../images/endpage.gif" width="20" height="20" onclick='lastPage()'/>
							</td>
							<td>
								<img src="../../images/refresh.gif" width="20" height="20" onclick='refresh()'/>
							</td>
							<td><%=pageNo%>/<%=totalPage%>[<%=totalRecord%>]</td>
						</tr>
					</table>
				</form>
			</div>
			<table cellPadding="0" cellSpacing="0" class="table">
				<caption><i18n:message key="log4j.view.title"/></caption>
				<tr>
					<th class="w90 alignCenter"><i18n:message key="log4j.view.num"/></th>
					<th class="alignCenter"><i18n:message key="log4j.view.message"/></th>
				</tr>
				<c:forEach items="${result}" var="bean" varStatus="status">
					<tr>
						<td class="alignCenter"><c:out value="${bean.lineNum}"/></td>
						<td style="word-wrap:word-break;white-space:normal;">
							<c:choose>
								<c:when test="${bean.multiLineMessage}">
									<textarea><c:out value="${bean.message}"/></textarea>
								</c:when>
								<c:otherwise>
									<c:out value="${bean.message}"/>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
			</table>
			<div class="area clearfix">
				<table cellspacing="0" cellpadding="0" class="fr">
					<tr>
						<td>
							<img src="../../images/startpage.gif" width="20" height="20" onclick='firstPage()'/>
						</td>
						<td>
							<img src="../../images/lastpage.gif" width="20" height="20" onclick='prevPage()'/>
						</td>
						<td>
							<img src="../../images/nextpage.gif" width="20" height="20" onclick='nextPage()'/>
						</td>
						<td>
							<img src="../../images/endpage.gif" width="20" height="20" onclick='lastPage()'/>
						</td>
						<td>
							<img src="../../images/refresh.gif" width="20" height="20" onclick='refresh()'/>
						</td>
						<td><%=pageNo%>/<%=totalPage%>[<%=totalRecord%>]</td>
					</tr>
				</table>
			</div>
		</div>
	</body>
</html>