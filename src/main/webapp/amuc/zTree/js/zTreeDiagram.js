/**
 * Created by founder on 2017/4/11.
 */
var setting = {
    view: {
        addHoverDom: addHoverDom, 	//设置鼠标移到节点上，在后面显示一个按钮
        removeHoverDom: removeHoverDom,  //设置鼠标移到节点上，删除按钮
        selectedMulti: false,  //是否可以多选
        dblClickExpand: false  //设置所有节点都可以有编辑功能
    },
    edit: {
        enable: true, //开启 异步加载模式
        editNameSelectAll: true,  //编辑全选模式
        showRemoveBtn: setRemoveBtn  //true / false 分别表示 显示 / 隐藏 删除按钮  //设置所有的父级节点不显示

    },
    data: {
        simpleData: {
            enable: true  //true / fals 使用 / 不使用 简单数据模式
        }
    },
    callback: {
        beforeDrag: beforeDrag,   //拖拽功能 return false  不可
        beforeEditName: beforeEditName,  //编辑节点的name
        beforeRemove: beforeRemove,  //移除操作
        beforeRename: beforeRename,  //更改节点内容出发Esc执行
        onRemove: onRemove,
        onRename: onRename,
        beforeClick: beforeClick
    }
};
var zNodes;
// var zNodes =[
//     { id:1, pId:0, name:"父天津", open:true},
//     { id:11, pId:1, name:"叶子节点 1-1"},
//     { id:12, pId:1, name:"叶子节点 1-2"},
//     { id:13, pId:1, name:"叶子节点 1-3"},
//     { id:2, pId:0, name:"父北京 2", open:false},
//     { id:21, pId:2, name:"叶子节点 2-1"},
//     { id:22, pId:2, name:"叶子节点 2-2"},
//     { id:23, pId:2, name:"叶子节点 2-3"},
//     { id:3, pId:0, name:"父上海 3", open:false},
//     { id:31, pId:3, name:"叶子节点 3-1"},
//     { id:32, pId:3, name:"叶子节点 3-2"},
//     { id:33, pId:3, name:"叶子节点 3-3"},
//     { id:34, pId:3, name:"叶子节点 3-1"},
//     { id:35, pId:3, name:"叶子节点 3-2"},
//     { id:36, pId:3, name:"叶子节点 3-3"}
// ];
var log, className = "dark";
var setPid, setLevel = '';

function setRemoveBtn(treeId, treeNode) {

    return !treeNode.isParent;
}
//拖拽事件
function beforeDrag(treeId, treeNodes) {

    return false;
}
function beforeEditName(treeId, treeNode) {


    if(treeNode.level == 0){
        className = (className === "dark" ? "":"dark");
        showLog("[ "+getTime()+" beforeEditName ]&nbsp;&nbsp;&nbsp;&nbsp; " + treeNode.name);
        var zTree = $.fn.zTree.getZTreeObj("treeDemo");
        zTree.selectNode(treeNode);
        setTimeout(function() {
            if (confirm("进入节点 -- " + treeNode.name + " 的编辑状态吗？")) {
                setTimeout(function() {
                    zTree.editName(treeNode);
                }, 0);
            }
        }, 0);

        return false;
    }else{

        $("#myModal").attr('_childId',treeNode.id);
        // console.log(treeNode.name)
        // console.log(treeNode.icLevelIndex)
        $("#myModal").toggleClass("modal");
        $("#myModalLabel").text(treeNode.name);
        $("#myModal input").eq(0).val(treeNode.name);
        $("#myModal input").eq(1).val(treeNode.icLevelIndex);
        $(".modal-body1 input").val('');

        return false;
    }
}
//修改名称
function modifyTree(id,icLevel,icLevelIndex){
    $.ajax({
        type: "get",
        url: "invitecode/modifyTree.do",
        ContentType: "application/json; charset=utf-8",
        async: false,
        data: {
            id:id,
            icLevel:icLevel,
            icLevelIndex:icLevelIndex
        },
        success: function(data){
            $("#myModal").addClass('modal');
            // alert('请求成功');
            createTree();
        },
        error:function(){
            alert('改变数据失败')
        }
    });
}

//删除节点

function removeTree(id){
    $.ajax({
        type: "get",
        url: "invitecode/delTree.do",
        ContentType: "application/json; charset=utf-8",
        async: false,
        data: {
            id:id
        },
        success: function(data){
            // createTree();
            alert('删除成功');

        },
        error:function(){
            alert('删除失败')
        }
    });
}

// treeDemo_8_remove
function beforeRemove(treeId, treeNode) {


    if($('#'+ treeNode.tId + '_remove').parent().parent().siblings().length < 1){
        alert("默认数据不能删除");
        return false;
    }else{
        className = (className === "dark" ? "":"dark");
        showLog("[ "+getTime()+" beforeRemove ]&nbsp;&nbsp;&nbsp;&nbsp; " + treeNode.name);
        var zTree = $.fn.zTree.getZTreeObj("treeDemo");
        zTree.selectNode(treeNode);
        removeTree(treeNode.id)
    }
    // return confirm("确认删除 节点 -- " + treeNode.name + " 吗？");


}
function onRemove(e, treeId, treeNode) {
    showLog("[ "+getTime()+" onRemove ]&nbsp;&nbsp;&nbsp;&nbsp; " + treeNode.name);
}
function beforeRename(treeId, treeNode, newName, isCancel) {

    //父级修改name
    modifyTree(treeNode.id,newName,'');

    className = (className === "dark" ? "":"dark");
    showLog((isCancel ? "<span style='color:red'>":"") + "[ "+getTime()+" beforeRename ]&nbsp;&nbsp;&nbsp;&nbsp; " + treeNode.name + (isCancel ? "</span>":""));
    if (newName.length == 0) {
        setTimeout(function() {
            var zTree = $.fn.zTree.getZTreeObj("treeDemo");
            zTree.cancelEditName();
            alert("节点名称不能为空.");
        }, 0);
        return false;
    }
    return true;
}
function onRename(e, treeId, treeNode, isCancel) {
    showLog((isCancel ? "<span style='color:red'>":"") + "[ "+getTime()+" onRename ]&nbsp;&nbsp;&nbsp;&nbsp; " + treeNode.name + (isCancel ? "</span>":""));
}
function showRemoveBtn(treeId, treeNode) {
    return !treeNode.isFirstNode;
}
function showRenameBtn(treeId, treeNode) {
    return !treeNode.isLastNode;
}
function showLog(str) {
    if (!log) log = $("#log");
    log.append("<li class='"+className+"'>"+str+"</li>");
    if(log.children("li").length > 8) {
        log.get(0).removeChild(log.children("li")[0]);
    }
}
function getTime() {
    var now= new Date(),
        h=now.getHours(),
        m=now.getMinutes(),
        s=now.getSeconds(),
        ms=now.getMilliseconds();
    return (h+":"+m+":"+s+ " " +ms);
}

var newCount = 1;
//添加新的子集
function addHoverDom(treeId, treeNode) {

    var sObj = $("#" + treeNode.tId + "_span");
    if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;

    if(treeNode.level == 0){
        var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
            + "' title='add node' onfocus='this.blur();'></span>";
    }else{

        setPid = treeNode.pId;

        //要获取节点的pid，直接append 一个节点
        sObj.parent().attr('_hierarchy',treeNode.hierarchy);
        sObj.parent().attr('_pid',treeNode.pId);
        sObj.parent().attr('_id',treeNode.id);
        sObj.parent().attr('_icLevelIndex',treeNode.icLevelIndex);

    }
    sObj.after(addStr);
    var btn = $("#addBtn_"+treeNode.tId);
    if (btn) btn.bind("click", function(){
        $.ajax({
            type: "get",
            url: "invitecode/addTree.do",
            data: {
                pid:treeNode.children[0].pId,
                icLevel:'默认新添加',
                icLevelIndex:'ABC',
                icHierarchy: treeNode.hierarchy
            },
            success: function(data){
                var zTree = $.fn.zTree.getZTreeObj("treeDemo");
                zTree.addNodes(treeNode, {id:(100 + newCount), pId:treeNode.pId, name:"默认新添加" + (newCount++),hierarchy:treeNode.hierarchy});
                //初始化树树桩图
                createTree(treeNode.hierarchy);
                // return false;
            },
            error:function(){
                alert('失败')
            }
        });

    });
};
function removeHoverDom(treeId, treeNode) {
    $("#addBtn_"+treeNode.tId).unbind().remove();
};
function selectAll() {
    var zTree = $.fn.zTree.getZTreeObj("treeDemo");
    zTree.setting.edit.editNameSelectAll =  $("#selectAll").attr("checked");

}
$(document).ready(function(){

    createTree();
    // $.fn.zTree.init($("#treeDemo"), setting, zNodes);
    $("#selectAll").bind("click", selectAll);
});

//模态框的显示隐藏事件
$(".close").on('click',function(e){
    $("#myModal").addClass('modal');
})
$('.btn-default').on('click',function(){
    $("#myModal").addClass('modal');
})

$('.btn-primary').on('click',function(){
    //进行回调请求数据
    // alert('我要请求数据了');
    var childId = $("#myModal").attr("_childId");
    var newName = $("#myModal input").eq(2).val();
    var newCode = $("#myModal input").eq(3).val();
    if(newName == '' || newCode == ''){
        alert('现在名称,现有Code不可为空')
    }else{
        modifyTree(childId,newName,newCode);
    }

})


//父级下可点击但是只能唯一
var arrTreeDemo = [];

var arrStorage = [];
sessionStorage.clear();
$("#treeDemo").on('click',' li li a',function () {

    // alert($(this).attr('_hierarchy'));

    var strTreeDemopId = $(this).attr('_pid');
    var popInfor =  {
        id : $(this).attr('_id'),
        val : $(this).text(),
        code : $(this).attr('_icLevelIndex'),
        icHierarchy : $(this).attr('_hierarchy')
    };

    //装化为字符串再存值
    var  tranStr = JSON.stringify(popInfor);

    sessionStorage.setItem("info" + $(this).attr('_hierarchy'),tranStr);

    if($(this).hasClass('curSelectedNode1')){
        $(this).removeClass('curSelectedNode1');
        sessionStorage.removeItem("info"+  $(this).attr('_hierarchy'));
        //在这里删除数据
    }else{

        if(arrTreeDemo.length>0){

            if(arrTreeDemo.indexOf(strTreeDemopId) == -1){
                //这里开始存储  这里是不同父级下的信息
                arrTreeDemo.push(strTreeDemopId);
                $(this).addClass('curSelectedNode1');
            }else{
                //这里覆盖点中父级下的信息
                $(this).parent().parent().find('a').removeClass('curSelectedNode1')
                $(this).addClass('curSelectedNode1');
            }
        }else{
            //这里开始存储

            // sessionStorage.setItem(strTreeDemopId,strTreeDemoId);

            arrTreeDemo.push(strTreeDemopId);
            $(this).addClass('curSelectedNode1');
        }
    }
});

function beforeClick(treeId, treeNode, clickFlag) {

    return false;
    // className = (className === "dark" ? "":"dark");
    // showLog("[ "+getTime()+" beforeClick ]&nbsp;&nbsp;" + treeNode.name );
    // return (treeNode.click != false);
}
//存储数据 向sessionStroge

function createTree(objBooleans) {
    // alert(objBooleans)
    $.ajax({
        url: "invitecode/getTree.do",
        type: "get",
        dataType: "json",
        ContentType: "application/json; charset=utf-8",
        async: false,
        success: function (data) {
            zNodes = new Array(data.length);
            var objBoo = false;
            for (var i = 0; i < data.length; i++) {
                console.log(data[i].hierarchy);

                if(objBooleans == data[i].hierarchy){
                    // alert(1)
                    objBoo = true;
                }else{
                    objBoo = false;
                }
                zNodes[i] = {
                    id: data[i].id,
                    name: data[i].icLevel,
                    pId: data[i].pid,
                    icLevelIndex : data[i].icLevelIndex,
                    hierarchy : data[i].hierarchy,
                    open: objBoo
                };

                zNodes = eval(zNodes);//序列化json数据
            }
            console.log(zNodes)
            $.fn.zTree.init($("#treeDemo"), setting, zNodes);//初始化树

        },
        error: function () {
            // alert("zNodes");
            alert('获取列表失败')
        }
    });

};
