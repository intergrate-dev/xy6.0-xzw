<%@include file="../e5include/IncludeTag.jsp"%>
<i18n:bundle baseName="i18n.e5context" changeResponseLocale="false"/>
<%
  /**
   * 触发器设置编辑器
   * wanghc
   * 2006-6-28 
   **/
      
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><i18n:message key="scheduler.helper.title"/></title>
<script type="text/javascript" src="../e5script/Function.js"></script>
<link type="text/css" rel="stylesheet" href="../e5style/style.css"/>
<style>
input{
    border:none;
}
td{white-space:nowrap;}
</style>
<script language="javascript">
	
	/*===============控制页面元素显示还是隐藏=======================================*/
	function schRadioClick(src)
	{
		if(src.value=='1')
		{
			document.getElementById("schDayValueDiv").style.display = "";
			document.getElementById("schWeekValueDiv").style.display = "none";
			document.getElementById("schMonthValueDiv").style.display = "none";
		}
		else if(src.value=='2')
		{
			document.getElementById("schDayValueDiv").style.display = "none";
			document.getElementById("schWeekValueDiv").style.display = "";
			document.getElementById("schMonthValueDiv").style.display = "none";
		}
		else if(src.value=='3')
		{
			document.getElementById("schDayValueDiv").style.display = "none";
			document.getElementById("schWeekValueDiv").style.display = "none";
			document.getElementById("schMonthValueDiv").style.display = "";
		}
	}
	
	function schCheckBoxClick(src)
	{		
		if(src.checked)
		{
			document.getElementById("schTimeTr").style.display = "none";
			f1.schTimeValue.disabled = false;
		}
		else
		{
   		    f1.schTimeValue.disabled = true;
			document.getElementById("schTimeTr").style.display = "";
		}
		
		
	}
	
	
	function schTimeSetClick(src)
	{
		if(src.value=='1')
		{
			//启用设置时间
			f1.schTimeValue.disabled=false
			
			//禁用重复设置
			getEl("schTimeSecond").disabled=true;
			getEl("schTimeMinute").disabled=true;
			getEl("schTimeHour").disabled=true;
			f1.schTimeSecondValue.disabled=true;
			f1.schTimeMinuteValue.disabled=true;
			f1.schTimeHourValue.disabled=true;
			
		}
		else
		{
			//启用设置时间
			f1.schTimeValue.disabled=true
			
			//禁用重复设置
			getEl("schTimeSecond").disabled=false;
			getEl("schTimeMinute").disabled=false;
			getEl("schTimeHour").disabled=false;
			f1.schTimeSecondValue.disabled=false;
			f1.schTimeMinuteValue.disabled=false;
			f1.schTimeHourValue.disabled=false;
			
		}
		//alert(getEl("schTimeSet").value);
	}
	
	//取得选择了那个radio
	function getSchTimeSet()
	{
		var schTimeSet = f1.elements["schTimeSet"];
		if(schTimeSet[0].checked) return schTimeSet[0].value;
		if(schTimeSet[1].checked) return schTimeSet[1].value;
		
	}
	function schTimeRadioClick(src)
	{
		if(src.value=='1')
		{
			document.getElementById("schTimeSecondDiv").style.display = "";
			document.getElementById("schTimeMinuteDiv").style.display = "none";
			document.getElementById("schTimeHourDiv").style.display = "none";
		}
		else if(src.value=='2')
		{
			document.getElementById("schTimeSecondDiv").style.display = "none";
			document.getElementById("schTimeMinuteDiv").style.display = "";
			document.getElementById("schTimeHourDiv").style.display = "none";
		}
		else if(src.value=='3')
		{
			document.getElementById("schTimeSecondDiv").style.display = "none";
			document.getElementById("schTimeMinuteDiv").style.display = "none";
			document.getElementById("schTimeHourDiv").style.display = "";
		}
		
	}
	
	/************************************生成表达式*********************************************/
	
	function dosubmit()
	{
		//1.取时间

		var schTimeValue = f1.schTimeValue.value;
		
		if(!isTime(schTimeValue))
		{
			alert("<i18n:message key="scheduler.helper.alert.timeformat"/>");
			return false;
		}

		var schTimeValueArray = schTimeValue.split(":");
		
		var ss = schTimeValueArray[2];   //秒

		var mm = schTimeValueArray[1];   //分

		var hh = schTimeValueArray[0];   //时

		
		
		//时间重复
		if(getSchTimeSet()==2)
		{
			//设置任意一种方式重复方式，设置的执行时间失效

			//按秒重复
			if(getEl("schTimeSecond").checked)
			{
				if(checkValue("<i18n:message key="scheduler.helper.second"/>",f1.schTimeSecondValue.value,0,300))
				{
					ss = "0" + "/" + f1.schTimeSecondValue.value;
					mm = "*";
					hh = "*";
				}
				else
					return;
			}
			//按分重复
			else if(getEl("schTimeMinute").checked)
			{
				if(checkValue("<i18n:message key="scheduler.helper.minute"/>",f1.schTimeMinuteValue.value,0,300))
				{
					mm = "0" + "/" + f1.schTimeMinuteValue.value;
					ss = "0";
					hh = "*";
				}
				else
					return;
			}
			//按时重复
			else if(getEl("schTimeHour").checked)
			{
				if(checkValue("<i18n:message key="scheduler.helper.hour"/>",f1.schTimeHourValue.value,0,72))
				{
					hh = "0" + "/" + f1.schTimeHourValue.value;
					ss = "0";
					mm = "0";
				}
				else
					return;
			}
		}
		
		//2.取日
		var dd = "*"; //日

		var MM = "*"; //月

		var yy = "*"; //年 
		var ww = "*"; //周

				
		//按日
		if(getEl("schDay").checked)
		{
			//==1直接用*代替
			if(f1.schDayValue.value != 1)
				dd = "1" + "/" + f1.schDayValue.value;		
			ww = "?";	
		}
		//按周
		else if(getEl("schWeek").checked)
		{
			var weeks = f1.elements["schWeekValue"];
			
			ww = "";
			for(i=0;i<7;i++)
			{				
				if(weeks[i].checked)
					ww = ww + weeks[i].value+",";				
			}
			//去掉最后一个,
			if(ww!='')
			{
				ww = ww.substring(0,ww.length-1);
			}
			else
			{
				alert("<i18n:message key="scheduler.helper.alert.select"/><i18n:message key="scheduler.helper.week"/>!");
				return;
			}
			dd = "?";
		}
		else if(getEl("schMonth").checked)
		{
			var months = f1.elements["schMonthValue"];
			dd = "";
			for(i=0;i<31;i++)
			{
				if(months[i].checked)
					dd = dd + months[i].value+",";
			}
			
			if(dd!='')
			{
				dd = dd.substring(0,dd.length-1);
			}
			else
			{
				alert("<i18n:message key="scheduler.helper.alert.select"/><i18n:message key="scheduler.helper.day"/>!");
				return;
			}
			ww = "?";	
		}
		
		//生成quartz CronExpression
		
		var expre = ss+" "+mm+" "+hh+" "+dd+" "+MM+" "+ww+" "+yy;
		window.opener.form1.cronExpression.value = expre;
		window.opener.hideDescription();
		window.close();
		//alert(expre);
	}
	
	//取得元素
	function getEl(name)
	{
		return document.getElementById(name);
	}
	
	//检查取值

	function checkValue(sValueName,sValue,minValue,maxValue)
	{
		if(sValue == '')
		{
		  alert(sValueName+" <i18n:message key="scheduler.helper.alert.required"/>!");
		  return false;
		}
		
		if(!isInt(sValue))
		{
			alert(sValueName+" <i18n:message key="scheduler.helper.alert.integer"/>");
			return false;
		}
		
		if(sValue<minValue || sValue>maxValue)
		{
			alert(sValueName+" <i18n:message key="scheduler.helper.alert.max"/>"+minValue+"~"+maxValue);
			return false;
		}
		return true;
	}

	//上次表达式

	var expression = "<c:out value="${param.expression}"/>";

	//如果已经设置过，初始化页面

	function init()
	{	
		if(expression=="" || expression=="null")
		{
			//默认设置定时执行可用
			schTimeSetClick(getEl("schTimeSet1"));
			return;
		}
		var time = "23:59:59";		
		//alert(expression);
		//分解表达式

		var expArray = expression.split(" ");
		//alert(expArray[0]+"|"+expArray[1]+"|"+expArray[2]);
		
		if(expArray[0]!="*" && expArray[0].indexOf("/")>0)
		{
			//设置了重复秒			
			getEl("schTimeSecond").checked=true;			
			getEl("schTimeSet2").checked=true;			
			schTimeRadioClick(getEl("schTimeSecond"));
			schTimeSetClick(getEl("schTimeSet2"));
			f1.schTimeSecondValue.value = expArray[0].split("/")[1];
		}
		else if(expArray[1]!="*" && expArray[1].indexOf("/")>0)
		{
			//设置了重复分			
			getEl("schTimeMinute").checked=true;
			getEl("schTimeSet2").checked=true;
			schTimeRadioClick(getEl("schTimeMinute"));
			schTimeSetClick(getEl("schTimeSet2"));
			f1.schTimeMinuteValue.value = expArray[1].split("/")[1];
		}
		else if(expArray[2]!="*" && expArray[2].indexOf("/")>0)
		{
			//设置了重复时			
			getEl("schTimeHour").checked=true;
			getEl("schTimeSet2").checked=true;
			schTimeRadioClick(getEl("schTimeHour"));
			schTimeSetClick(getEl("schTimeSet2"));
			f1.schTimeHourValue.value = expArray[2].split("/")[1];
		}
		else
		{
			//指定了时间

			time = expArray[2]+":"+expArray[1]+":"+expArray[0];				
			schTimeSetClick(getEl("schTimeSet1"));
		}
		
		//设置表单值

		f1.schTimeValue.value = time;

		//设置日，周

		if(expArray[3]!="*" && expArray[3]!="?")
		{
			//重复
			if(expArray[3].indexOf("/")>-1)
			{
				var dayValue = expArray[3].split("/")[1];
				f1.schDayValue.value = dayValue;
			}
			else
			{
				var dayValues = expArray[3].split(",");
				var months = f1.elements["schMonthValue"];
				for(i=0;i<dayValues.length;i++)
				{
					for(j=0;j<months.length;j++)
					{
						if(dayValues[i]==months[j].value)
						{
//							alert("ss");
							months[j].checked=true;
							break;
						}
					}
				}

				schRadioClick(getEl("schMonth"));
				getEl("schMonth").checked=true;
			}

		}
		else if(expArray[5]!="*" && expArray[5]!="?")
		{
				var weekValues = expArray[5].split(",");
				var weeks = f1.elements["schWeekValue"];
				for(i=0;i<weekValues.length;i++)
				{
					for(j=0;j<weeks.length;j++)
					{
						if(weekValues[i]==weeks[j].value)
						{
//							alert("ss");
							weeks[j].checked=true;
							break;
						}
					}
				}

				schRadioClick(getEl("schWeek"));
				getEl("schWeek").checked=true;
		}
	}

	//检查时间格式:format=hh:mm:ss
	function isTime(sTime)
	{
		if(sTime=='')
			return false;
		if(sTime.length!=8)
			return false;

		var timeArray = sTime.split(":");
		if(timeArray.length!=3) return false;

		if(!isInt(timeArray[0]) || !isInt(timeArray[1]) || !isInt(timeArray[2]))
			return false;
		if(timeArray[0]>23 || timeArray[0]<0)
			return false;
		if(timeArray[1]>59 || timeArray[1]<0)
			return false;
		if(timeArray[2]>59 || timeArray[2]<0)
			return false;

		return true;
	}
</script>
</head>

<body onload="init()">
<form name="f1" method="post" action="">
  <table width="33%" border="0" cellPadding="2"  cellSpacing="0" >
    <tr align="center">
      <td height="20" align="left">
	  <table width="374" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td width="87" height="26" class="bottomlinetd">
            	<label for="schTimeSet1">
					<input id="schTimeSet1" type="radio" name="schTimeSet" value="1" checked onclick="schTimeSetClick(this)">
					<i18n:message key="scheduler.helper.timeset"/>
				</label>
			</td>
			<td width="292" class="bottomlinetd">
              <input name="schTimeValue" type="text" value="23:59:59" size="10" style="border: #888888 1px solid">
     </td>
          </tr>
    	<tr id="schTimeTr">
             <td height="20" colspan="2">
            	<table width="99%" height="25%" border="0" cellpadding="0" cellspacing="0">
            	          <tr>
				            <td width="85" height="30" class="bottomlinetd">
				            	<label for="schTimeSet2">
									<input id="schTimeSet2" type="radio" name="schTimeSet" value="2" onclick="schTimeSetClick(this)">
									<i18n:message key="scheduler.helper.timerepeat"/>
								</label>
							</td>
							<td width="77%" class="bottomlinetd">
								<label for="schTimeSecond">
									<input id="schTimeSecond" name="schTimeType" type="radio" value="1" checked onclick="schTimeRadioClick(this)">
									<i18n:message key="scheduler.helper.by"/><i18n:message key="scheduler.helper.second"/>
								</label>
								<label for="schTimeMinute">
									<input  id="schTimeMinute"  type="radio" name="schTimeType" value="2" onclick="schTimeRadioClick(this)">
									<i18n:message key="scheduler.helper.by"/><i18n:message key="scheduler.helper.minute"/>
								</label>
								<label for="schTimeHour">
									<input id="schTimeHour"  type="radio" name="schTimeType" value="3" onclick="schTimeRadioClick(this)">
									<i18n:message key="scheduler.helper.by"/><i18n:message key="scheduler.helper.hour"/>
								</label>
							</td>
				          </tr>
				          <tr>
				            <td height="30" class="bottomlinetd">
							&nbsp;
							</td>
							<td valign="middle" class="bottomlinetd">							
				            	<div id="schTimeSecondDiv"> 
				            	<p>
				            	  <i18n:message key="scheduler.helper.every"/>
				                    <input name="schTimeSecondValue" type="text" size="5" style="border: #888888 1px solid" value="10">
				              <i18n:message key="scheduler.helper.every.second"/></p>
			            	  </div>
				            	<div id="schTimeMinuteDiv" style="display:none"> 
				                <p><i18n:message key="scheduler.helper.every"/>
				                    <input name="schTimeMinuteValue" type="text" size="5" style="border: #888888 1px solid" value="10">
				              <i18n:message key="scheduler.helper.every.minute"/></p>
				                </div>
				                <div id="schTimeHourDiv" style="display:none"> 
				                <p><i18n:message key="scheduler.helper.every"/>
				                    <input name="schTimeHourValue" type="text" size="5" style="border: #888888 1px solid" value="1">
				             	<i18n:message key="scheduler.helper.every.hour"/></p>
				                </div>
			                </td>
				          </tr>
			   </table>
            </td>
          </tr>
          <tr>
            <td height="30" class="bottomlinetd">&nbsp;&nbsp;<i18n:message key="scheduler.helper.date"/>
			</td>
			<td class="bottomlinetd">
				<label for="schDay">
					<input id="schDay" name="radiobutton" type="radio" value="1" checked onclick="schRadioClick(this)">
					<i18n:message key="scheduler.helper.by"/><i18n:message key="scheduler.helper.day"/>
				</label>
				<label for="schWeek">
					<input id="schWeek" type="radio" name="radiobutton" value="2" onclick="schRadioClick(this)">
					<i18n:message key="scheduler.helper.by"/><i18n:message key="scheduler.helper.week"/>
				</label>
				<label for="schMonth">
					<input id="schMonth" type="radio" name="radiobutton" value="3" onclick="schRadioClick(this)">
					<i18n:message key="scheduler.helper.by"/><i18n:message key="scheduler.helper.month"/>
				</label>
			</td>
          </tr>
          <tr>
		    <td height="30" class="bottomlinetd">&nbsp;</td>
            <td valign="middle" class="bottomlinetd">
            	<div id="schDayValueDiv">
					<p>
						<i18n:message key="scheduler.helper.every"/>
						<input name="schDayValue" type="text" value="1" size="5" style="border: #888888 1px solid">
						<i18n:message key="scheduler.helper.every.day"/>
					</p>
               </div>
				<div id="schWeekValueDiv" style="display:none">
				<p>
					<label for="schWeekValue1">
						<input id="schWeekValue1" type="checkbox" name="schWeekValue" value="2">
						<i18n:message key="scheduler.helper.week.1"/>
					</label>
					<label for="schWeekValue2">
						<input id="schWeekValue2" type="checkbox" name="schWeekValue" value="3">
						<i18n:message key="scheduler.helper.week.2"/>
					</label>
					<label for="schWeekValue3">
						<input id="schWeekValue3" type="checkbox" name="schWeekValue" value="4">
						<i18n:message key="scheduler.helper.week.3"/>
					</label>
					<label for="schWeekValue4">
						<input id="schWeekValue4" type="checkbox" name="schWeekValue" value="5">
						<i18n:message key="scheduler.helper.week.4"/>
					</label>
				</p>
                <p>
					<label for="schWeekValue5">
						<input id="schWeekValue5" type="checkbox" name="schWeekValue" value="6">
						<i18n:message key="scheduler.helper.week.5"/>
					</label>
					<label for="schWeekValue6">
						<input id="schWeekValue6" type="checkbox" name="schWeekValue" value="7">
						<i18n:message key="scheduler.helper.week.6"/>
					</label>
					<label for="schWeekValue7">
						<input id="schWeekValue7" type="checkbox" name="schWeekValue" value="1">
						<i18n:message key="scheduler.helper.week.7"/>
					</label> 
				</p>
              </div>
              <p>  
              <div id="schMonthValueDiv" style="display:none">            
				  <%for(int i=1;i<=31;i++){%>
					<label for="schMonthValue<%=i%>">
						<input id="schMonthValue<%=i%>" type="checkbox" name="schMonthValue" value="<%=i%>">
						<%=i%><i18n:message key="scheduler.helper.h"/> 
					</label>
					<%if(i%5 == 0) out.print("<br>");
				  }%>    
              </p>
            </div>
            </td>
          </tr>
      </table></td>
    </tr>

    <tr>
      <td width="483" height="21" align="center" class="bottomlinetd"> <input type="button" value="<i18n:message key="scheduler.button.sure"/>" onclick="dosubmit()" class="button">
      	<input type="button" value="<i18n:message key="scheduler.button.cancel"/>" onclick="javascript:window.close()" class="button">
      	</td>
    </tr>
  </table>
 
</form>
<p>&nbsp;</p>
</body>
</html>
