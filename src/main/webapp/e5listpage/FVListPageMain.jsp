<%@ include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5listpage" changeResponseLocale="false"/>
<html>
<head>
	<title><i18n:message key="fvpagelist.title"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link type="text/css" rel="stylesheet" href="../e5style/sys-main-body-style.css"/>
	<link rel="stylesheet" rev="stylesheet" href="../e5dom/css/filterlist.css" type="text/css" media="all" />
	<script language="javascript" src="../e5dom/script/prototype.js"></script>
	<script language="javascript">
		var fvID = "<c:out value="${FVID}"/>";
		var docTypeID = "<c:out value="${DocTypeID}"/>";
		var type =  "<c:out value="${type}"/>";
		function doSave() {
			var optionz = $("FilterList").options;
			var value = "";
			for(var i=0;i<optionz.length;i++)
			{
				var op = optionz[i];
				if(op.selected)
				{
					value += op.value + ",";
				}
			}
			var theURL = "./FVListPage.do?save=1&FVID=" + fvID + "&DocTypeID=" + docTypeID +"&value=" + value;
			window.parent.frames("mainframe").location.href = theURL;
		}

		function addLists() {
			var optionz = document.getElementById("FilterList").options;
			var selectedOpz = new Array();
			for(var i=0;i<optionz.length;i++)
			{
				var op = optionz[i];
				if(op.selected)
				{
					selectedOpz.push(op);
				}
			}
			window.parent.page.handlers.AssignListPagesOK(selectedOpz,type);
			window.parent.e5.dialog.close('AssignListPages');
		}
		
		////将一条或多条列表条目移上移一位
		function MoveUp()
		{
		    var tempValue="",tempText="";
		    if(selectfilter.selectedIndex>0)
		    {
		        for(var i=1;i<selectfilter.length;i++)
		        {
		            if(selectfilter.options.item(i).selected==true)
		            {
		                 tempValue=selectfilter.options.item(i).value;
		                 tempText=selectfilter.options.item(i).text;
		
		                 selectfilter.options.item(i).value=selectfilter.options.item(i-1).value;
		                 selectfilter.options.item(i).text=selectfilter.options.item(i-1).text;
		
		                 selectfilter.options.item(i-1).value=tempValue;
		                 selectfilter.options.item(i-1).text=tempText;
		
		                 selectfilter.options.item(i-1).selected=true;
		                 selectfilter.options.item(i).selected=false;
		            }
		        }
		    }
		
		}
		
		//将一条或多条列表条目移下移一位
		function MoveDown()
		{
		    var tempValue="",tempText="";
		    var lastSelectedIndex=selectfilter.length;
		
		    for(var i=selectfilter.length-1;i>=0;i--)
		    {
		        if(selectfilter.options.item(i).selected==true)
		        {
		            lastSelectedIndex=i;
		            break;
		        }
		    }
		
		    if(lastSelectedIndex<selectfilter.length-1)
		    {
		        for(var i=selectfilter.length-2;i>=0;i--)
		        {
		            if(selectfilter.options.item(i).selected==true)
		            {
		                 tempValue=selectfilter.options.item(i).value;
		                 tempText=selectfilter.options.item(i).text;
		
		                 selectfilter.options.item(i).value=selectfilter.options.item(i+1).value;
		                 selectfilter.options.item(i).text=selectfilter.options.item(i+1).text;
		
		                 selectfilter.options.item(i+1).value=tempValue;
		                 selectfilter.options.item(i+1).text=tempText;
		
		                 selectfilter.options.item(i+1).selected=true;
		                 selectfilter.options.item(i).selected=false;
		            }
		        }
		    }
		}
		
		////将一条或多条列表条目移到最上
		function MoveToTop()
		{
		    while(selectfilter.selectedIndex>0)
		    {
		        MoveUp();
		    }
		}
		
		////将一条或多条列表条目移到最下
		function MoveToBottom()
		{
			var lastSelectedIndex=selectfilter.length;
		    do
		    {
			    MoveDown();
		    	for(var i=selectfilter.length-1;i>=0;i--)
		    	{
		        	if(selectfilter.options.item(i).selected==true)
		        	{
		            	lastSelectedIndex=i;
		            	break;
		        	}
		    	}
		
		    }while(lastSelectedIndex<selectfilter.length-1);
		}
	</script>
</head>
<body>
	<table border="1px" cellpadding="10" cellspacing="0" class="table">
		<caption><i18n:message key="fvpagelist.form.head"/></caption>
		<tr>
			<td>
				<select style="width:300px" id="FilterList" name="selectfilter" multiple="multiple" size="15">
				<c:forEach var="listPage" items="${listPages}">
				<option value="<c:out value="${listPage.listID}"/>">
					<c:out value="${listPage.listName}"/>
				</option>
				</c:forEach>
				</select>
			</td>
			<td>
				<table border="0" cellspacing="2" cellpadding="1" >
					<tr><td><input class="bluebutton" type="button" onclick="MoveToTop()" name="buttonMoveTop" value="<i18n:message key="fvpagelist.button.movetop"/>"></td></tr>
					<tr><td><input class="bluebutton" type="button" onclick="MoveUp()" name="buttonMoveUp" value="<i18n:message key="fvpagelist.button.moveup"/>"></td></tr>
					<tr><td><input class="bluebutton" type="button" onclick="MoveDown()" name="buttonMoveDown" value="<i18n:message key="fvpagelist.button.movedown"/>"></td></tr>
					<tr><td><input class="bluebutton" type="button" onclick="MoveToBottom()" name="buttonMoveBottom" value="<i18n:message key="fvpagelist.button.movebottom"/>"></td></tr>
				</table>
			</td>
		</tr>
		<tr>
		<td colspan="2" align="center">
		<input class="button" type="button" onclick="addLists();" value="<i18n:message key="fvpagelist.button.AddListPage"/>">
		<input class="button" type="reset" onclick="window.parent.e5.dialog.close('AssignListPages');" name="Submit2" value="<i18n:message key="fvpagelist.button.Cancel"/>">
		</td></tr>
	</table>
</body>
</html>
