e5.mod("workspace.search",function() {
	var api, searchparam,ready = false,
		listening = function(msgName, callerId, param) {
			for (var name in searchparam) searchparam[name] = "";
			for (var name in param) searchparam[name] = param[name];
			_reset(searchparam);
			search();

		},
        add_zero=function(param){
        if(param < 10) return "0" + param;
        return "" + param;
        },
        _getSQLtime=function(ch){
            var url = "../xy/statisticsutil/GetTimeSQL.do";
            var now = new Date();
            var ym = now.getFullYear() + "-" + add_zero(now.getMonth() + 1) + "-";
            var beginTime=$("#exp_pubTime_from_emp").val();
            var endTime=$("#exp_pubTime_to_emp").val();
            if(!beginTime && $("#exp_resetWorkload").attr("isreset")=="false"){
                $("#exp_pubTime_from_emp").val(ym + "01" + " 00:00:00");
                $("#exp_pubTime_to_emp").val(ym + add_zero(now.getDate()) + " 23:59:59");
                $("#exp_thisMonthEmp").addClass("select_time");
            }
            if(ch == 1){
                var beginTime=$("#exp_pubTime_from_emp_app").val();
                var endTime=$("#exp_pubTime_to_emp_app").val();
                if(!beginTime && $("#exp_resetWorkload_app").attr("isreset")=="false"){
                    $("#exp_pubTime_from_emp_app").val(ym + "01" + " 00:00:00");
                    $("#exp_pubTime_to_emp_app").val(ym + add_zero(now.getDate()) + " 23:59:59");
                    $("#exp_thisMonthEmp_app").addClass("select_time");
                }
            }else if(ch == 2){
                var beginTime=$("#exp_pubTime_from_emp_topic").val();
                var endTime=$("#exp_pubTime_to_emp_topic").val();
                if(!beginTime && $("#exp_resetWorkload_topic").attr("isreset")=="false"){
                    $("#exp_pubTime_from_emp_topic").val(ym + "01" + " 00:00:00");
                    $("#exp_pubTime_to_emp_topic").val(ym + add_zero(now.getDate()) + " 23:59:59");
                    $("#exp_thisMonthEmp_topic").addClass("select_time");
                }
            }
            beginTime= beginTime=='' ? ym + "01" + " 00:00:00" : beginTime;
            endTime= endTime=='' ? ym + add_zero(now.getDate()) + " 23:59:59" : endTime;

            var params =
            {
                "beginTime": beginTime,
                "endTime": endTime
            }
            var sqlTime="";
            $.ajax({
                url: url,
                data: JSON.stringify(params),
                type: "POST",
                dataType: "json",
                async: false,
                contentType: "application/json;charset=utf-8",
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                    alert(errorThrown + ':' + textStatus); // 错误处理
                },
                success: function(data) {
                    //console.log(data);
                    sqlTime=data.timeSQL;
                }
            });
            return sqlTime;
        },



		_getQuery = function() {
			//TODO:点击查询按钮后，拼查询条件
			//若传入的部门参数>0，应加条件：a_orgID=<传入的deptID>
			//若指定了人员，加SYS_AUTHORID=<人员ID>
			//若选择了栏目，则应加条件a_columnID=<指定的栏目ID>
            //稿件ID/标题 SYS_TOPIC=<稿件ID/标题>
            //话题ID SYS_TOPICID=<话题ID>
            //话题标题 SYS_TOPICTITLE=<话题标题>
            //稿件类型 a_type=<稿件类型>
            //是否原创 a_copyright
            var ch=$('#main_search li.select').attr('channel');
            var flag='_app';
            if(ch == 0){
                flag = '';
            }else if(ch == 2){
                flag = '_topic';
            }
            var orgID,columnID,sysTopic,sysTopicID,sysTopicTitle,userID,a_type,a_copyright,a_source,a_editor,queryString;
            var pubTime=_getSQLtime(ch);
            var _type=getQueryString("type");
            columnID=$("#exp_columnID"+flag).val();
            if(ch == 2){
                sysTopic= "";
                sysTopicID= $.trim($("#exp_id_topic").val());
                sysTopicTitle=$.trim($("#exp_title_topic").val());
            }else{
                sysTopic= $.trim($("#exp_id"+flag).val());
                sysTopicID= "";
                sysTopicTitle= "";
            }
            orgID=$("#dept_list", parent.document).find(".selected").attr("departmentid");
            var org="a_orgID=" + orgID+" AND ";
            var user=" SYS_AUTHORID>0 and a_sourceType<=2 AND "
            if(!orgID){
                org="";
            }
            var col="a_columnID=" + columnID+" AND ";
            if(ch ==2){
                col="a_topicID=" + columnID +" AND ";
            }
            if(!columnID){
                col="";
            }
            var topic="";
            if(!sysTopic){
                topic="";
            }else if( typeof parseInt(sysTopic) === 'number' && !isNaN(parseInt(sysTopic)) ){
                topic="SYS_DOCUMENTID=" + sysTopic+" AND ";
            }else{
                topic="SYS_TOPIC like '%" + sysTopic+"%' AND ";
            }
            var topicID="";
            if(sysTopicID){
                topicID="a_topicID like '%" + sysTopicID+"%' AND ";
            }
            var topicTitle="";
            if(sysTopicTitle){
                topicTitle="a_topicName like '%" + sysTopicTitle+"%' AND ";
            }

            var time="a_pubTime BETWEEN "+pubTime+" AND ";
            if(!pubTime){
                time="";
            }
            if(_type == 1){
                userID=$("#exp_employeeID"+flag).val();
                var uid="SYS_AUTHORID=" + userID+" AND ";
                if(!userID){
                    uid="";
                }
                //queryString="a_columnID=" + columnID+"_AND_SYS_TOPIC=" + sysTopic +"_AND_SYS_AUTHORID=" + userID +"_AND_a_pubTime="+pubTime;
                queryString = time + user + col + topic + uid + org;
            }else if(_type == 2){
                userID=parent.document.getElementById("detail-man").getAttribute("userid");
                var uid="SYS_AUTHORID=" + userID+" AND ";
                if(!userID){
                    uid="";
                }
                queryString = time + topic + uid + col;
            }else if(_type == 0){
                a_type=$("#exp_articleSelect"+flag).val();
                a_copyright=$("#exp_checkboxId"+flag).is(':checked')?1:0;
                a_source=$("#exp_src"+flag).val();
                a_editor=$("#exp_editor"+flag).val();
                var type="a_type=" + a_type+" AND ";
                if(!a_type||a_type<0){
                    type="";
                }

                var copyright="a_copyright=" + a_copyright+" AND ";
                if(!a_copyright){
                    copyright="";
                }

                var source="a_source='" + a_source+"' AND ";
                if(!a_source){
                    source="";
                }

                var editor="a_editor='" + a_editor+"' AND ";
                if(!a_editor){
                    editor="";
                }


                queryString = time + type + topic + topicID + topicTitle + copyright + source + editor +col;
            }else{
              queryString = time;
            }
            if(queryString){
                queryString=queryString.substring(0,queryString.length-5);
            }
            // console.log(queryString);
            return queryString;

			//若指定了发布时间，加a_pubTime限制。
			//这里需把日期选择参数送到后台拼出条件
			//不同数据库类型时sql不同，用DomHelper.getDBType得到数据库类型，DBType类里有常量定义。
			//return "";
		},
		_reset = function(param){
			//TODO:点击重置按钮后，清空查询条件
			//根据self_param.type(0是稿件明细，1是部门稿件明细，2是个人稿件明细)控制一些查询条件的显示/隐藏
            //和产品确认，重置清空所有查询条件
			$("#exp_resetWorkload").click(function(){
		 		var _type=getQueryString("type");
		 		//console.log(_type);
				if(_type == 1){
					$("#exp_column").val("");
                    $("#exp_columnID").val("");
					$("#exp_id").val("");
					$("#exp_employee").val("");
                    $("#exp_employeeID").val("");
				}else if(_type == 2){
					$("#exp_column").val("");
                    $("#exp_columnID").val("");
					$("#exp_id").val("");
				}else{
					$("#exp_column").val("");
                    $("#exp_columnID").val("");
					$("#exp_articleSelect").val("-1");
					$("#exp_id").val("");
					$("#exp_checkboxId").removeClass("checked");
					$("#exp_src").val("");
                    $("#exp_srcID").val("");
					$("#exp_editor").val("");
                    $("#exp_editorID").val("");
                    $("#exp_checkboxId").attr("checked",false)
				}
				$(this).siblings('a').removeClass("select_time");
				$("#exp_pubTime_from_emp").val("");
				$("#exp_pubTime_to_emp").val("");
                $(this).attr("isreset","true");
                // search();
                $("#main #listing").css("display", "none");
                $(this).attr("isreset","false");
			});

			$("#exp_resetWorkload_app").click(function(){
                var _type=getQueryString("type");
                if(_type == 1){
                    $("#exp_column_app").val("");
                    $("#exp_columnID_app").val("");
                    $("#exp_id_app").val("");
                    $("#exp_employee_app").val("");
                    $("#exp_employeeID_app").val("");
                }else if(_type == 2){
                    $("#exp_column_app").val("");
                    $("#exp_columnID_app").val("");
                    $("#exp_id_app").val("");
                }else{
                    $("#exp_column_app").val("");
                    $("#exp_columnID_app").val("");
                    $("#exp_articleSelect_app").val("-1");
                    $("#exp_id_app").val("");
                    $("#exp_checkboxId_app").removeClass("checked");
                    $("#exp_src_app").val("");
                    $("#exp_srcID_app").val("");
                    $("#exp_editor_app").val("");
                    $("#exp_editorID_app").val("");
                    $("#exp_checkboxId_app").attr("checked",false)
                }
                $(this).siblings('a').removeClass("select_time");
                $("#exp_pubTime_from_emp_app").val("");
                $("#exp_pubTime_to_emp_app").val("");
                $(this).attr("isreset","true");
                // search();
                $("#main #listing").css("display", "none");
                $(this).attr("isreset","false");
            });
            $("#exp_resetWorkload_topic").click(function(){
                var _type=getQueryString("type");
                if(_type == 1){
                    $("#exp_column_topic").val("");
                    $("#exp_columnID_topic").val("");
                    $("#exp_id_topic").val("");
                    $("#exp_employee_topic").val("");
                    $("#exp_employeeID_topic").val("");
                }else if(_type == 2){
                    $("#exp_column_topic").val("");
                    $("#exp_columnID_topic").val("");
                    $("#exp_id_topic").val("");
                }else{
                    $("#exp_column_topic").val("");
                    $("#exp_columnID_topic").val("");
                    $("#exp_articleSelect_topic").val("-1");
                    $("#exp_id_topic").val("");
                    $("#exp_checkboxId_topic").removeClass("checked");
                    $("#exp_src_topic").val("");
                    $("#exp_srcID_topic").val("");
                    $("#exp_editor_topic").val("");
                    $("#exp_editorID_topic").val("");
                    $("#exp_checkboxId_topic").attr("checked",false)
                }
                $("#exp_title_topic").val("");
                $(this).siblings('a').removeClass("select_time");
                $("#exp_pubTime_from_emp_topic").val("");
                $("#exp_pubTime_to_emp_topic").val("");
                $(this).attr("isreset","true");
                // search();
                $("#main #listing").css("display", "none");
                $(this).attr("isreset","false");
            })
		},
		search = function(){
			searchparam.query = _getQuery();
			api.broadcast("searchTopic", searchparam);
		},
		keysearch = function(){
			if (event.keyCode == 13) search();
		},
		init = function(sandbox) {
			api = sandbox;
			searchparam = new SearchParam();
            $("#exp_lookMsg,#exp_lookMsg_app,#exp_lookMsg_topic").click(search);

			api.listen("workspace.resourcetree:resourceTopic", listening);
			ready = true;
		},
		isReady = function() {
			return ready;
		};
	return {
		init: init,
		isReady:isReady
	}
});
//从url中获取参数值
function getQueryString(name){
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if(r != null)return unescape(r[2]);
	return null;
}

