;$(function(){
	
	getProcIcons();
	$("#btnSubmit").click(function(){
		saveProcSort();
	});
	$("#btnReset").click(function(){
		resetProcOrder();
	});
	$("#btnGroup").click(function(){
		window.location.href = "./VisualProcGroup.jsp?docTypeID="+$.query.get("docTypeID")+"&flowID="+$.query.get("flowID")+"&flowNodeID="+$.query.get("flowNodeID");
	});
});

function saveProcSort(){
	/// <summary>保存操作顺序 </summary>
	var procids = new Array();
	$("#icon_sortlist").find("img").each(function(i,n){
		
		procids.push($(this).attr("name"));
		if(i<$("#icon_sortlist").find("img").length-1){
			procids.push(",");
		}
	});

		    $.ajax({
		        type: "POST",
		        url: "../e5flow/cust_flowIconGroup.do?method=setProcsSort&ProcID="+procids.join('')+"&docTypeID="+$.query.get("docTypeID")+"&flowNodeID="+$.query.get("flowNodeID")+"&flowID="+$.query.get("flowID"),
		        beforeSend: function () {
		            //设置按钮不可用，防止重复提交
		            $("#btnSubmit").attr("disabled", true);
		        },

		        success: function (data, textStatus) {
		            $("#btnSubmit").attr("disabled", false);
		            if (data == "OK") {
		                alert(i18n.savesucess);
		            } else {
		            	alert(i18n.savefailed);
		            }
		        },
		        error: function () {
		            //请求出错处理
		            $("#btnSubmit").attr("disabled", false);
		            alert(i18n.savefailed);
		        }
		    });
}

function getProcIcons() {
    /// <summary>加载节点操作 </summary>
    $.ajax({
        url: "../e5flow/cust_flowIconGroup.do?method=getSortProcOrders&docTypeID=" + $.query.get("docTypeID") + "&flowNodeID=" + $.query.get("flowNodeID")+"&flowID="+$.query.get("flowID"),
        async: false,
        dataType: "json",
        success: function (data) {
            if (data == null || data == "") {
               return;
            }
            $("#icon_sortlist").html("");
            $.each(data, function (i, n) {
            	var icontemp = "";
            	if(n.iconURL.toString().length>0){
            		icontemp = "../"+n.iconURL.toString();
            	}
            	$("#icon_sortlist").append("<li class='clearfix'><table><tr><td class='l'></td><td class='c'><img name='" + n.procID + "' id='icon_" + n.procID + "' src='" + icontemp + "' alt1='" + n.procName + "' /></td><td class='c'>" + n.procName + "</td><td class='r'></td></tr></table></li>");
            });
            setTabContentListSrotable();
        }
    });

}

function resetProcOrder(){
	/// <summary>重置 </summary>
	var reseturl = "../e5flow/cust_flowIconGroup.do?method=resetProcOrder&docTypeID="+$.query.get("docTypeID")+"&flowNodeID="+$.query.get("flowNodeID")+"&flowID="+$.query.get("flowID");
	
	$.ajax({
        type: "POST",
        url: reseturl,
        beforeSend: function () {
            //设置按钮不可用，防止重复提交
            $("#btnReset").attr("disabled", true);
        },

        success: function (data, textStatus) {
            $("#btnReset").attr("disabled", false);
            if (data == "OK") {
                alert(i18n.resetsuccess);
                getProcIcons();

            } else {
                alert(i18n.resetfailed);
            }
            
        },
        error: function () {
            //请求出错处理
            $("#btnReset").attr("disabled", false);
            alert(i18n.resetfailed);
            window.location.reload();
        }

    });
}

function setTabContentListSrotable() {
    /// <summary>设置可以拖动排序</summary>


	$("#icon_sortlist").sortable({
        items: "> li",
        placeholder: "ui-state-highlight",
        //axis: 'x',
        containment: "#icon_sortlist",
        distance: 2,
        start: function (event, ui) {
            ui.placeholder.css({
                width: ui.helper.width(),
                height: ui.helper.height()
            });

        },
        stop: function () {

        }
    });

}