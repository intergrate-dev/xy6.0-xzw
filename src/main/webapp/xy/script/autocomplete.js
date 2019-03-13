/**
 * Created by Administrator on 2017/2/27.
 */
    var siteID=main_param.siteID;
    var catTypeID=main_param.catTypeID;
    var url="common/group/Find.do?siteID="+siteID+"&catTypeID="+catTypeID;
    $("#Search").autocomplete(url,{
        minChars : 1,
        delay : 1000,
        autoFill : true,
        selectFirst : true,
        matchContains: false,
        matchSubset: false,
        cacheLength : 1,
        dataType:'json',
        matchType: 'value',
        //把data转换成json数据格式
        parse: function(data) {
            if (!data)
                return [];

            return $.map(eval(data), function(row) {
                return {
                    data: row,
                    value: row.id+"-"+row.value,
                    result: row.id+"-"+row.value
                }
            });
        },
        //显示在下拉框中的值
        formatItem: function(row, i,max) { return row.id+"-"+row.value; },//下拉列表中显示的内容
        formatMatch: function(row, i,max) { return row.id+"-"+row.value; },
        formatResult: function(row, i,max) { return row.id+"-"+row.value; }
    });
    $("#Search").result(function (event, row, formatted) {
        var id=row.key.split("~")[1]
        $('li[groupid="'+id+'"]').find('div').trigger("click");
       /* var id=row.key.split("~")[1];
        $("#groupUl li div").removeClass("select");
        $('li[groupid="'+id+'"]').find('div').addClass("select");

        var param = new ResourceParam();
        for ( var name in main_param)
            param[name] = main_param[name];

        var groupID = id;
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

        api.broadcast("resourceTopic", param);*/

    });

