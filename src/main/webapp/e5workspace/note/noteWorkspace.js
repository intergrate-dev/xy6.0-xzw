function getHistory(){
    window.location.href = "getHistory.do";
}

function noteopen(count){
	var noteid="noteid"+count;
	id=document.getElementById(noteid).value;
	var theURL = "ReadNote.do?noteID="+id;
	e5.dialog({type:"iframe", value:theURL},{id : "noteDialog", title:"查看公告", width:600, height:470, resizable:true}).show();
}
function setType(type){
	var div1=document.getElementById("notetype");
	switch (type)
	{
		case 0:div1.innerHTML="<div>紧急消息</div>";break;
        case 1:div1.innerHTML="<div>普通消息</div>";break;
		case 2:div1.innerHTML="<div>个人消息</div>";break;
	}
}
/*
创建消息
*/
function CreateNote(){
   var checknull=0;
   var topic=document.getElementById("topic").value;
   if((topic==null)||(topic=='')) 
   checknull=1;
   var content=document.getElementById("content").value;
   var useridstr=document.getElementById("useridstr").value;

   if((useridstr==null)||(useridstr=='')) 
   checknull=1;
   if(checknull==0){
   var url="ReleaseNote.do?topic="+topic+"&content="+content+"&noteType=2&receivers="+useridstr;
  doOperation(url);
  window.close();

   }
  else alert("带*项为必添内容，请填写！");
}

function doOperation(url)
  {
	
	if (window.XMLHttpRequest) {
 		req = new XMLHttpRequest();
	}else if (window.ActiveXObject) {
		req = new ActiveXObject("Microsoft.XMLHTTP");
    }

    if(req){
		req.open("POST",url, false);
		req.onreadystatechange = complete;
	    req.send(null);
     }
  }

 function complete(){ 
 }
/*
清空
*/
function setNullToAll()
{
   var topic=document.getElementById("topic");
   topic.value="";
   var content=document.getElementById("content");
   content.value="";
   var useridstr=document.getElementById("userNamestr");
   useridstr.value="";
}

function setValue(){

  var useridstr="";
  var userNamestr="";
  var i=0;
  var ischeck="checkbox"+i;
  var id;
  var name;
  var userid;
  var userName;
  var check=document.getElementById(ischeck);
  while(check!=null)
	  {
     if(check.checked){	 
       userid="userid"+i;
	   userName="userName"+i;
	   id=document.getElementById(userid).value;
	   name=document.getElementById(userName).value;
	  useridstr=useridstr+id+",";
       userNamestr=userNamestr+name+",";

   }		
    i++;
    ischeck="checkbox"+i;
    check=document.getElementById(ischeck);
  }
  if(useridstr!=null&&useridstr!=''){
	
	
	var obj=parent.document.frames("noteframe").document.getElementById("useridstr");
	obj.value=useridstr;
	var showName=userNamestr.substring(0,userNamestr.length-1);
	var vbj=parent.document.frames("noteframe").document.getElementById("userNamestr");
	vbj.value=showName;	
  }
  else
    alert("请选择要删除的note！");
}

function restart(){
  var i=0;
  var ischeck="checkbox"+i;

  var check=document.getElementById(ischeck);
  while(check!=null)
	 {
	
  check.checked=false;		
  i++;
  ischeck="checkbox"+i;
  check=document.getElementById(ischeck);
 }
 }

function changefont(count){
  var timetd="timetd"+count;
  var div1=document.getElementById(timetd);
  div1.style.fontWeight="normal";
  //div1.style.color="red";
  var persontd="persontd"+count;
  var div2=document.getElementById(persontd);
  div2.style.fontWeight="normal";
  //div2.style.color="red";
  var topictd="topictd"+count;
  var div3=document.getElementById(topictd);
  div3.style.fontWeight="normal";
  //div3.style.color="red";
}