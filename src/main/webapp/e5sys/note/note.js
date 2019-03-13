/*
创建公告
用Ajax的post方法，同步调用
*/

function CreateNote(){
  var topic=getTextValue("topic").value;
  var content=getTextValue("content").value;
  var type=getTextValue("noteType").value;
  if(topic==null|| topic=="")
     alert("带*号项为必添项，请填写！");
  else {
	  var url="CreateNote.do";
	  if (window.XMLHttpRequest) {
 		req = new XMLHttpRequest();
	}else if (window.ActiveXObject) {
		req = new ActiveXObject("Microsoft.XMLHTTP");
    }
    if(req){
		
		req.open("POST",url, false);
		req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		req.onreadystatechange = complete1;
		pram="topic="+encodeSpecialCode(topic)+"&content="+encodeSpecialCode(content)+"&noteType="+type+"&receivers=null";
		req.send(pram);
     }
	
	  }
  }
function complete1(){

  window.location.href="SysNote.do";

}
/*
查看公告

*/

function noteRelease(){
  window.location.href='NoteRelease.jsp';
}

function noteopen(count){
	var noteid="noteid"+count;
	id=getTextValue(noteid).value;
	var theURL = "ReadNote.do?noteID="+id;
	e5.dialog({type:"iframe", value:theURL},{id : "noteDialog", title:"查看公告", width:600, height:470, resizable:true}).show();
}
/*
删除公告
*/

function deleteNote() {
	var noteidstr = "";
	var i = 0;
	var ischeck = "checkbox" + i;
	var noteid;
	var id;
	var check = getTextValue(ischeck);
	while (check != null) {
		if (check.checked) {
			noteid = "noteid" + i;
			id = getTextValue(noteid).value;
			noteidstr = noteidstr + id + ",";
		}
		i++;
		ischeck = "checkbox" + i;
		check = getTextValue(ischeck);
	}
	if (noteidstr != null && noteidstr != '') {
		if (confirm("确定要删除所选中的公告吗？")) {
			var url = "DeNote.do?DeIDstr=" + noteidstr;
			doOperation(url);
		}
	} else {
		alert("请选择要删除的公告！");
	}
}

function doOperation(url)
{
	
	if (window.XMLHttpRequest) {
 		req = new XMLHttpRequest();
	}else if (window.ActiveXObject) {
		req = new ActiveXObject("Microsoft.XMLHTTP");
    }

    if(req){
		req.open("GET",url, false);
		req.onreadystatechange = complete;
	    req.send(null);
     }
 }
 function complete(){
 window.location.reload();
 }

/*
设置消息类型
*/
 function setType(type){
    var div1=getTextValue("notetype1");
    switch (type)
	{
		case 0:div1.innerHTML="<div>紧急消息</div>";break;
        case 1:div1.innerHTML="<div>普通消息</div>";break;
		case 2:div1.innerHTML="<div>个人消息</div>";break;
	}
}
/*
清空
*/
function setnull()
{
  var topic=getTextValue("topic");
  topic.value="";
  var content=getTextValue("content");
  content.value="";
  form1.notetype1[0].checked=true;

}

function setnoteValue(){

  form1.notetype1[0].checked=true;
  var type=getTextValue("noteType");
  type.value=0;

}


function changecolor(name){

  var type=getTextValue(name);

  type.style.fontSize="16";
  type.style.color="red";
  type.style.cursor="hand";

}

function getFormer(name){

  var type=getTextValue(name);
  type.style.fontSize="12";
  type.style.color="black";

}

function setnoteValue2(){

  form1.notetype1[1].checked=true;
  var type=getTextValue("noteType");
  type.value="1";
  

}
/*
按时间段查看公告
*/
function onExpressQuery(src){
	window.location.href= "./SysNote.do?condtion=" + src.value;
}

function setselect(i){
	document.getElementById("expressQuery").selectedIndex = i;
}
/*
*清理公告
*/
function clearNote() {
	var endtime = getTextValue("EndTime").value;
	if (endtime != null && endtime != '') {
		if (confirm("确定删除" + endtime + "日期之前的公告吗？")) {
			var url = "ClearNote.do?EndTime=" + endtime;
			doOperation(url);
		}
	} else {
		alert("请输入时间！");
	}
}
/*
 * 根据元素名称得到值
 */
function getTextValue(name){

    return document.getElementById(name);
}
/*
双击选择所有公告
*/
var type=0;
function allSelect(){
  
  var i=0;
  var ischeck="checkbox"+i;
  switch (type)
	{
		case 0:setCheckTrue();
		       type=1;
			   break;
        case 1:setCheckFalse()
		       type=0;
			   break;
	}   
}

function setCheckTrue(){

  var i=0;
  var ischeck="checkbox"+i;
  var check=getTextValue(ischeck);
  while(check!=null)
	  {
	  check.checked=true;
      i++;
      ischeck="checkbox"+i;
      check=getTextValue(ischeck);
     
  }

}


function setCheckFalse(){

  var i=0;
  var ischeck="checkbox"+i;
  var check=getTextValue(ischeck);
  while(check!=null)
	  {
	  check.checked=false;
	  i++;
      ischeck="checkbox"+i;
      check=getTextValue(ischeck);
      
  }

}


function getXY(t1,t2){
	var x=getTextValue('X');
	var y=getTextValue('Y');
	x.value = t1;
	y.value = t2;
   }

var timer=null;
function HidePopup()
  {
	var popup=getTextValue('Popup');
	popup.style.display="none";
	window.clearTimeout(timer);
  }

function Lazyshow(src)
  {	
	timer=window.setTimeout("showPopup()",500);
  }
  function showPopup(){
    var popup=getTextValue('Popup');
    var x=getTextValue('X');
	var y=getTextValue('Y');
	popup.style.left=x.value;
	popup.style.top=y.value;
	popup.style.display="block";
  }
  function setFocus(){
    var topic=getTextValue('topic');
    topic.focus();
  }