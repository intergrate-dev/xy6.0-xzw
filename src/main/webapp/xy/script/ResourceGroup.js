//带分组的导航
e5.mod("workspace.resourcetree", function() {
	var api;
	var isEnterPress = false;
	
	var groupAdd = function() {
		var catTypeID = main_param.catTypeID;
		$("#newGroupInput").val("");
		$("#newGroupInput").show();
		$("#newGroupInput").focus();
		
		$("#groupNameDiv_" + $("#chosenGroupIDInput").val()).show();
		$("#groupIdInput_" + $("#chosenGroupIDInput").val()).hide();
	}
	//提交
	var submitNewGroup = function() {
		// 如果groupId为空不处理
		if ($.trim(main_param.catTypeID) == "") {
			$("#newGroupInput").hide();
			return;
		}
		//未填写不处理
		if ($.trim($("#newGroupInput").val()) == "") {
			$("#newGroupInput").hide();
			return;
		}

		var newGroupName = $.trim($("#newGroupInput").val());
		$.ajax({
			async: false,
			url : "../xy/common/group/addGroupAjax.do",
			type : 'POST',
			data : {
				"categoryTypeId" : main_param.catTypeID,
				"siteID" : main_param.siteID,
				"newGroupName" : newGroupName
			},
			dataType : 'json',
			success : function(data, status) {
				if (data.result == "success") {
					
					$("#newGroupInput").hide();
					$("#newGroupInput").val("");
					/*$("#groupUl").append(
							'<li class="group" groupID="'
									+ data.groupID + '" id="newGroup_'
									+ data.groupID + '">'
									+ newGroupName + '</li>');*/
					var tags = new Array();
					tags.push('<li class="group" groupID="'+data.groupID+'">');
					tags.push('<Div class="active"  name="grouplist" id="groupNameDiv_'+data.groupID+'" groupID="'+data.groupID+'">'+newGroupName+'</Div>');
					tags.push('<input id="groupIdInput_'+data.groupID+'" maxlength="10" class="input" style="display:none;"'+'groupID="'+data.groupID+'" groupName="'+newGroupName+'"'+'value="" />');
					tags.push('</li>');
					$("#groupUl").append(tags.join(""));
					$("#groupNameDiv_" + data.groupID).click(treeClick);
					$("#groupIdInput_" + data.groupID).keypress(
						function(event) {
							if (event.keyCode == 13) {
								submitModifiedGroup();
							}
						});
				} else if (data.result == "HasTheSameGroup") {
					alert("对不起，已经存在【" + newGroupName + "】，请重新添加！");
				} else {
					alert("对不起，创建失败！");
				}
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("对不起，无法添加！(script/ResourceGroup.js)");
			}
		});/*	*/
	}
	
	var groupUpdate = function() {
		$("#newGroupInput").hide();
		var catTypeID = main_param.catTypeID;
		var chosenGroupID = $("#chosenGroupIDInput").val();
		// 点击修改按钮的时候，给显示input赋值
		$("#groupNameDiv_" + chosenGroupID).hide();
		// 给显示input赋值
		$("#groupIdInput_" + chosenGroupID).val(
				$("#groupIdInput_" + chosenGroupID)
						.attr("groupName"));
		// 显示
		$("#groupIdInput_" + chosenGroupID).show();
		$("#groupIdInput_" + chosenGroupID).focus();

	}
	var submitModifiedGroup = function() {
		var chosenGroupID = $("#chosenGroupIDInput").val();
		//如果新修改的名字为空，给input重新赋值，并且不进行修改
		if ($.trim($("#groupIdInput_" + chosenGroupID)
				.val()) == "") {
			// 给显示input重新赋值
			$("#groupIdInput_" + chosenGroupID).val(
					$("#groupIdInput_" + chosenGroupID)
							.attr("groupName"));
			//显示名称，隐藏input
			$("#groupNameDiv_" + chosenGroupID).show();
			$("#groupIdInput_" + chosenGroupID).hide();
			
			return;
		}
		//如果新名字和之前的一样，不操作
		if($.trim($("#groupIdInput_" + chosenGroupID)
				.val()) == $.trim($("#groupIdInput_" + chosenGroupID)
						.attr("groupName"))){
			$("#groupNameDiv_" + chosenGroupID).show();
			$("#groupIdInput_" + chosenGroupID).hide();
			return;
		}
		
		//提交修改的groupName
		$.ajax({
			async: false,
			url : "../xy/common/group/modifyGroupAjax.do",
			type : 'POST',
			data : {
				"categoryTypeId" : main_param.catTypeID,
				"siteID" : main_param.siteID,
				"newGroupName" : $("#groupIdInput_" + chosenGroupID).val(),
				"groupID" : chosenGroupID
			},
			dataType : 'json',
			success : function(data, status) {
				if (data.result == "success") {
					//1.把input中的值赋值给Div
					$("#groupNameDiv_" + chosenGroupID).text($("#groupIdInput_" + chosenGroupID).val());
					//2.input隐藏，Div展示
					$("#groupNameDiv_" + chosenGroupID).show();
					$("#groupIdInput_" + chosenGroupID).hide();
					$("#groupIdInput_" + chosenGroupID)
					.attr("groupName",$("#groupIdInput_" + chosenGroupID).val());
					$("#groupIdInput_" + chosenGroupID).blur();
				}else{
					alert(data.message);
					$("#groupIdInput_" + chosenGroupID).focus();
				}
				
				
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("对不起，无法修改！");
			}
		});/*	*/

	}

	var groupDelete = function() {
		$("#groupNameDiv_" + $("#chosenGroupIDInput").val()).show();
		$("#groupIdInput_" + $("#chosenGroupIDInput").val()).hide();
		//删除分组
		var catTypeID = main_param.catTypeID;
		$.ajax({
			async: false,
			url : "../xy/common/group/deleteGroupAjax.do",
			type : 'POST',
			data : {
				"categoryTypeId" : main_param.catTypeID,
				"siteID" : main_param.siteID,
				"groupID" : $("#chosenGroupIDInput").val()
			},
			dataType : 'json',
			success : function(data, status) {
				if(data.result == "success"){
					//确定删除操作
					if(data.operation == "confirmDelete"||data.operation == "deleteOperation"){
						doDeleteGroup(data);
					}else{
						//如果不能删除
						alert(data.message);
					}
					//$("#groupNameDiv_" + $("#chosenGroupIDInput").val()).parent().remove();
				}else{
					alert(data.message);
				}
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("对不起，通信出现异常！无法删除");
			}
		});
	}
	var doDeleteGroup = function(data){
		//执行操作删除操作
		if(!confirm(data.message)){
			return;
		}else{
			$.ajax({
				async: false,
				url : "../xy/common/group/doDeleteGroupAjax.do",
				type : 'POST',
				data : {
					"categoryTypeId" : main_param.catTypeID,
					"siteID" : main_param.siteID,
					"groupID" : $("#chosenGroupIDInput").val()
				},
				dataType : 'json',
				success : function(data, status) {
					if(data.result == "success"){
						//成功之后，移除这一组
						$("#groupNameDiv_" + $("#chosenGroupIDInput").val()).parent().remove();
						defaultClick();
					}else{
						alert(data.message);
					}
				},
				error : function(xhr, textStatus, errorThrown) {
					alert("对不起，通信出现异常！无法删除！");
				}
			});
		}
	}
	//扩展字段组的“挂接栏目”操作
	var curOpDialog = null;
	var grantColumns = function() {
		var chosenGroupID = $("#chosenGroupIDInput").val();
		var opurl = "extfield/findGrantedColumns.do?siteID=" + main_param.siteID
				+ "&groupID=" + chosenGroupID+"&docLibID="+main_param.docLibID;
		
		var aWidth = 600;
		var aHeight = 450;
		var sWidth = document.body.clientWidth; //窗口的宽和高
		var sHeight = document.body.clientHeight;
		
		if (aWidth + 10 > sWidth) aWidth = sWidth - 10;  //用e5.dialog时会额外加宽和高
		if (aHeight + 70 > sHeight) aHeight = sHeight - 70;
		
		//chrome下点击窗口的关闭时无法正确执行after.do，因此隐藏窗口关闭按钮，并不允许esc关闭
		var showClose = !e5.utils.isChrome();
		curOpDialog = e5.dialog({type:"iframe", value:opurl},
				{title:"挂接栏目", width:aWidth, height:aHeight, resizable:true
				,showClose:showClose,esc:true});
		curOpDialog.show();
	}
	//关闭“挂接栏目”操作窗口，供外部调用
	var close = function() {
		if (curOpDialog) {
			curOpDialog.close();
			curOpDialog = null;
		}
	}
	
	var init = function(sandbox) {
		api = sandbox;
		clickFun = treeClick;
		
		//$(".group").click(treeClick);
		$("#btnAdd").click(groupAdd);
		$("#btnUpdate").click(groupUpdate);
		$("#btnDelete").click(groupDelete);
		$("#btnGrant").click(grantColumns);
	},
	onload = function() {
		defaultClick();
	}

	var treeClick = function(evt) {
		$("#groupUl li div").removeClass("select");
		$(this).addClass("select");
		
		var param = new ResourceParam();
		for ( var name in main_param)
			param[name] = main_param[name];

		var groupID = $(evt.target).attr("groupID");
		//var groupID = $(this).attr("groupID");
		param.groupID = groupID;
		if (!param.ruleFormula)
			param.ruleFormula = param.groupField + "_EQ_" + groupID;
		else
			param.ruleFormula = param.groupField + "_EQ_" + groupID + "_AND_" + param.ruleFormula;

		$("#newGroupInput").hide();
		
		$("#groupNameDiv_" + $("#chosenGroupIDInput").val()).show();
		$("#groupIdInput_" + $("#chosenGroupIDInput").val()).hide();
		
		// 当用户点击列表的时候，给隐藏域赋值，以便于修改时知道当前选择的是哪个组
		$("#chosenGroupIDInput").val(groupID);

		api.broadcast("resourceTopic", param);
	}
	var defaultClick = function() {
		var statusReady = e5.mods["workspace.doclistMain"].isReady;
		var searchReady = e5.mods["workspace.search"].isReady;
		var ready = !!statusReady && !!searchReady && statusReady() && searchReady();
		if (!ready) {
			setTimeout(defaultClick, 100);
			return;
		}

		$(".group").find("Div").click(treeClick);
		
		// 新建input的触发事件
		$("#newGroupInput").keypress(function(event) {
			if (event.keyCode == 13) {
                isEnterPress = true;
				submitNewGroup();
			}
		});
		
		// 绑定点击事件
		$("input[id^=groupIdInput]").keypress(
		function(event) {
			if (event.keyCode == 13) {
				submitModifiedGroup();
			}
		});

        $("#newGroupInput").focus(function(event) {
            if (isEnterPress) isEnterPress = false;
        });

		//鼠标移走，自动提交
		$("#newGroupInput").blur(function(event) {
			// 如果触发了回车事件 就不再重复调用submitNewGroup方法
			if(isEnterPress) return;
			submitNewGroup();
		});
		//鼠标移走，自动提交
		$("input[id^=groupIdInput]").blur(
			function(event) {
				submitModifiedGroup();
			});

		//$(".group").find("Div").click();
		$(".group").find("Div").first().click();
	}
	return {
		init : init,
		onload : onload,
		close: close
	};
});
