var external_form = {
    init: function(){
        // font_form.queryFilePath();
        // external_form.initExternal();
        external_form.deleteEvent();
        external_form.getList();
    },

    // queryFilePath: function(){
    //     $.ajax({
    //         url: "../../xy/nis/fontFilePath.do",
    //         async: false,
    //         error: function(XMLHttpRequest, textStatus, errorThrown){
    //             alert("读取初始化参数失败。" + errorThrown + ':' + textStatus);  // 错误处理
    //         },
    //         dataType:"json",
    //         success: function(data){
    //             font_form.uploadPath = data.uploadPath;
    //             font_form.savePath = data.savePath;
    //         }
    //     });
    //
    // }
    // ,
    getList:function(){
        var externalData=$.trim($("#external").val());
        if(externalData==null ||externalData=="" ||externalData=="null") return;
        externalData =eval("(" + externalData + ")");
        var str="";
        for(var i in externalData.externals){
            str = str+ '<tr><td class="font-list-content">'+externalData.externals[i].id+'</td>'+
                '<td class="font-list-content">'+externalData.externals[i].name+'</td>'+
                '<td class="font-list-content">'+externalData.externals[i].key+'</td>'+
                '<td class="font-list-content">'+
                '<input type="button" class="delete" value="删除"/></td>'+
                '</tr>';
        }
        $("#externalList").find("tbody").append(str);
    },
    deleteEvent:function(){
        //删除列表项
        $("#externalList").on("click",".delete",function(){
            $(this).closest("tr").remove();
        })
    }

};
function doInit(){
    external_form.init();
}
function beforeSubmit(){
    var externalData={'externals':[]};
    var _tr=$("#externalList").find("tbody").children("tr");
    var _leng=_tr.length;
    var _arr=[];
    for(var i=0;i<_leng;i++){
        var _data={
            'id':_tr.eq(i).find("td").eq(0).text(),
            'name':_tr.eq(i).find("td").eq(1).text(),
            'key':_tr.eq(i).find("td").eq(2).text()
        };
        _arr.push(_data)
    }
    externalData.externals=_arr;
    externalData=JSON.stringify(externalData);

    alert(externalData);

    $("#external").val(externalData)
}

function makesure() {
    var name=$.trim($("#font-name").val());
    if(!name){
        alert("请先输入外部系统名称！");
        return;
    }
    $.ajax({
        url: "../../xy/nis/externalName.do",
        type : 'POST',
        async: false,
        data : {
            "name" : name
        },
        dataType:"json",
        success: function(data){
            //生成列表
            var id = data.id;
            var key = data.key;
            var str='<tr><td class="font-list-content">'+id+'</td>'+
                '<td class="font-list-content">'+name+'</td>'+
                '<td class="font-list-content">'+key+'</td>'+
                '<td class="font-list-content">'+
                '<input type="button" class="delete" value="删除"/></td>'+
                '</tr>';
            $("#externalList").find("tbody").prepend(str);
        }
    });
}