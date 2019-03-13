<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="com.founder.e5.web.WebUtil"%>
<%
	String path = WebUtil.getRoot(request);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>订单详情</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="apple-mobile-web-app-capable" content="yes"/>
    <link href="css/orderDetial.css" type="text/css" rel="stylesheet"/>
    <script src="js/jquery-1.7.1.min.js"></script>
    <script type="text/javascript" src="./js/jquery.similar.msgbox.js"></script>
     <style type="text/css">   
    .border-table {   
        border-collapse: collapse;   
        border: none; 
        width:700px;
        height:80px;
    }   
    .border-table td {   
        border: solid #000 1px;
        text-align:center;   
        width:700px;
        height:40px;
    }   
</style> 
<script language="JavaScript" type="text/javascript">  
        //第五种方法  
        var idTmr;  
        function  getExplorer() {  
            var explorer = window.navigator.userAgent ;  
            //ie  
            if (explorer.indexOf("MSIE") >= 0) {  
                return 'ie';  
            }  
            //firefox  
            else if (explorer.indexOf("Firefox") >= 0) {  
                return 'Firefox';  
            }  
            //Chrome  
            else if(explorer.indexOf("Chrome") >= 0){  
                return 'Chrome';  
            }  
            //Opera  
            else if(explorer.indexOf("Opera") >= 0){  
                return 'Opera';  
            }  
            //Safari  
            else if(explorer.indexOf("Safari") >= 0){  
                return 'Safari';  
            }  
        }  
        function method5(tableid) {  
            if(getExplorer()=='ie')  
            {  
                var curTbl = document.getElementById(tableid);  
                var oXL = new ActiveXObject("Excel.Application");  
                var oWB = oXL.Workbooks.Add();  
                var xlsheet = oWB.Worksheets(1);  
                var sel = document.body.createTextRange();  
                sel.moveToElementText(curTbl);  
                sel.select();  
                sel.execCommand("Copy");  
                xlsheet.Paste();  
                oXL.Visible = true;  
  
                try {  
                    var fname = oXL.Application.GetSaveAsFilename("Excel.xls", "Excel Spreadsheets (*.xls), *.xls");  
                } catch (e) {  
                    print("Nested catch caught " + e);  
                } finally {  
                    oWB.SaveAs(fname);  
                    oWB.Close(savechanges = false);  
                    oXL.Quit();  
                    oXL = null;  
                    idTmr = window.setInterval("Cleanup();", 1);  
                }  
  
            }  
            else  
            {  
                tableToExcel(tableid)  
            }  
        }  
        function Cleanup() {  
            window.clearInterval(idTmr);  
            CollectGarbage();  
        }  
        var tableToExcel = (function() {  
            var uri = 'data:application/vnd.ms-excel;base64,',  
                    template = '<html><head><meta charset="UTF-8"></head><body><table>{table}</table></body></html>',  
                    base64 = function(s) { return window.btoa(unescape(encodeURIComponent(s))) },  
                    format = function(s, c) {  
                        return s.replace(/{(\w+)}/g,  
                                function(m, p) { return c[p]; }) }  
            return function(table, name) {  
                if (!table.nodeType) table = document.getElementById(table)  
                var ctx = {worksheet: name || 'Worksheet', table: table.innerHTML}  
                window.location.href = uri + base64(format(template, ctx))  
            }  
        })()  
  
    </script>
</head>
<body>
  <div class="base">
    <div>
      <p class="head">订单详情</p>
    </div>
    <div style="margin-left:158px;">
    	<span>以下是生成成功的报卡号，共<span id="num"></span>个&nbsp&nbsp&nbsp</span>
    	<button type="button" onclick="method5('tableExcel')" style="width: 100px;height: 30px;margin-right: 20px;color: #fff;background: rgba(22, 155, 213, 1);border-radius: 5px;border: 0;">导出报卡号</button>
    </div> 
    <div class="order-details clear" style="margin-top:10px;">
    <center>
      	<table id="tableExcel" class="border-table">  
			<tr>  
			   <td>报卡号</td>  
			   <td>报卡号</td>  
			</tr>  
			<!-- <tr>  
			  <td>1001</td>  
			  <td>王德封</td>  
			</tr>    -->
		</table>  
	</center>
    </div>    
	<input type="hidden" id="PCNO" name="PCNO" value=""/>
  </div>
</body>
<script type="text/javascript">
  $(document).ready(function(){
	 var pcno = ${PCNO};
	 $("#num").text(pcno.length);
	 if(pcno == "" || typeof(pcno) == "undefined"){
		 return;
	 }
	 //alert(${PCNO})
	 $("#tableExcel").empty();
	 $("#tableExcel").append("<tr><td>报卡号</td><td>报卡号</td></tr>");
	 for(var i=0;i<pcno.length;i+=2){
		 var pcno2 = typeof(pcno[i+1])=="undefined"?"":pcno[i+1];
		 $("#tableExcel").append("<tr>"  
			      +"<td>"+pcno[i]+"</td>" 
			      +"<td>"+pcno2+"</td>"
				  +"</tr>");
	 }
  });

</script>
</html>