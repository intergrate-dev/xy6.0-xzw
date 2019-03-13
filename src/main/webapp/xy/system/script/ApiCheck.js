var apitest = {

	commitType : null,

	init : function() {
        //初始化,解析json文件并显示
        initApiData();
		//提交按钮触发
		$("#btn").click(function(){
			if(!submitParams())
				return;
			var urlParam = jointParams();
			urlParam = urlParam.replace(/\n/g, "");//去掉\n
            var urlApi = $("#urlRoot").val() + urlParam.split("?")[0];
			var url = "../../xy/system/commit.do?url=" + urlApi;
			var param = {};
			param["_type"] = apitest.commitType.toLowerCase();

			if ("post" == param["_type"]){
				var _param = urlParam.replace("?", "&");
				var paramArr = _param.split("&");
				for(var a=1; a<paramArr.length; a++){
					var KV = paramArr[a].split("=");
					param[KV[0]] = KV[1];
				}
			}else if ("get" == param["_type"]){
				param["url"] = $("#urlRoot").val() + urlParam;
			}
			$.ajax({ url: url, async : false, type:"post",
				data:{"data":JSON.stringify(param)},
				error: function (XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown + ':' + textStatus);  // 错误处理
				},
				success: function (data) {
					if("get" == apitest.commitType){
						$("#result").val(JSON.stringify(data));
					}else
						$("#result").val(data);
				}
			});
		});
	}
}

$(function(){
	apitest.init();
});
//获取json文件里面的数据
function initApiData(){
	if (typeof api_groups == "undefined") {
		alert("请检查script/api.json是否存在，或是否正确定义了api_groups");
		return;
	}
	
	handleApiGroups(api_groups.groups);
	//左侧栏默认显示第一个list的信息
	$("#group_li-0-0").click();
}
//按组处理
function handleApiGroups(apiGroups){
	for(var i in apiGroups){
		var groupName = apiGroups[i].name;
		var className = apiGroups[i].className;
		var apiList = apiGroups[i].list;
		var index = Number(i)+1 + ". ";
		$(".tab_menu").append("<li><ol class='tab_menu_groups' id='group_ol-"+i+"' class-name='"+className+"'><div>"+index+ groupName +"</div></ol></li>");
		var _this = $("#group_ol-"+i);
		handleList(_this, apiList, i);
	}
}
//每个groups的各个List
function handleList(_this, apiList, index){
	for(var i in apiList){
		var listName = apiList[i].name;
		var methodName = apiList[i].methodName;
		var _url = apiList[i].url[0];
		var _params = apiList[i].params;
		var _method = typeof (apiList[i].method)=="object" ? apiList[i].method[0] : apiList[i].method,
			method = formatValue(_method, "get");
		_this.append("<li commitType='"+method+"' param='"+JSON.stringify(_params)+"' apiurl='"+_url+"' methodname='"+methodName+"' onclick='showListDetails("+index+","+i+")' class='tab_menu_list' id='group_li-"+index+"-"+i+"'>"+listName+"</li>");
	}
}
//点击list时触发的事件 右侧栏显示参数等详细信息
function showListDetails(a, b){
	var _this = $("#group_li-"+a+"-"+b);
	$(".tab_menu_list").removeClass("selected");
	_this.addClass("selected");
	var params = JSON.parse(_this.attr("param"));
	// var methodName = _this.attr("methodname");
	var _url = _this.attr("apiurl");
	//处理左边的列表
	handleParams(params, _url);
	apitest.commitType = _this.attr("commitType");
	$("#result").val("");
}
function handleParams(_params, _url){
	//要先清空两个table
	var params_html = "",
		test_html = "";
	for(var i in _params){
		var paramName = formatValue(_params[i].name, ""),
			type = formatValue(_params[i].type, "") ,
			defaultValue = formatValue(_params[i].default, ""),
			value = formatValue(_params[i].value, paramName),
			comment = formatValue(_params[i].comment, ""),
            required = _params[i].required== "undefined" || _params[i].required == null ? "" : _params[i].required;
		params_html += "<tr>" +
			"<td>" + value + "</td>" +
			"<td>" + required + "</td>" +
			"<td>" + defaultValue + "</td>" +
			"<td>" + type + "</td>" +
			"<td>" + comment + "</td>" +
			"</tr>";
		test_html += "<tr>" +
			"<td>" + value + "</td>" +
			"<td contenteditable='true'></td>" +
			"<td style='visibility: hidden;'>" + value + "</td>" +
			"</tr>";
	}
	$("#list-url").val(_url);
	$("#paramExplain tbody").html(params_html);
	$("#test-table tbody").html(test_html);
}
//拼接参数
function jointParams(){
	var params_url = "";
	var _this = document.getElementById ("test-table");
	//表格行数
	var rows = _this.rows.length ;
	for(var i=1; i<rows; i++){
		if(_this.rows[i].cells[1].innerText != ""){
			if (params_url)
				params_url += "&";
			params_url += _this.rows[i].cells[0].innerHTML + "=" + _this.rows[i].cells[1].innerText;
		}
	}
	var _url = document.getElementById("list-url").value;
	return _url + "?" + params_url;
}
//格式化数据 使undefined为默认值
function formatValue(value, defaultValue){
	value = value == "undefined" || value == null ? defaultValue : value;
	value = value.replace('<', "&lt;");
	value = value.replace('>', "&gt;");
	value = value.replace('"', "&quot;");
	value = value.replace(' ', "&nbsp;");
	return value;
}
//当浏览器窗口大小改变时，设置显示内容的高度
window.onresize=function(){
	changeDivHeight();
}
function changeDivHeight(){
	var h = document.documentElement.clientHeight;//获取页面可见高度
	document.getElementById("tab_menu").style.height = h-115+"px";
	document.getElementById("tab_box").style.height = h-115+"px";
}

 //提交时对参数进行判断是否有必填的参数未填写
 function submitParams(){
     var paramExplain = document.getElementById ("paramExplain");
     var paramsTest = document.getElementById ("test-table");
     //表格行数
     var rows = paramsTest.rows.length ;
     // //表格列数
     // var cells = _this.rows.item(0).cells.length ;
     for(var i=1; i<rows; i++){
         if((paramExplain.rows[i].cells[1].innerText==true || paramExplain.rows[i].cells[1].innerText=="true")&&paramsTest.rows[i].cells[1].innerText==""){
             alert("请输入参数的值");
             $(paramsTest.rows[i].cells[1]).focus();
             return false;
         }
     }
     return true;
 }
