
function showDiv() {
    $("#div12").empty();
    $.ajax({
        url: "../xy/article/getAllSrc.do?siteID=" + siteID,
        type: "get",
        cache: false,
        dataType: "json",
        success: function (databack) {
            if (typeof (databack) !== "undefined" && databack !== null) {
                var tagArr = tags.split(",");
                for (var i = 0; i < databack.length; i++) {
                    var gid = databack[i].groupID;
                    var gstr = "<div  id='tagGroup_"+gid+"'  class='tagGroup' onclick='HideDiv(this);' >" + databack[i].groupName + "</div>";
                    var items = databack[i].subs;
                    for (var j = 0; j < items.length; j++) {
                        if (j % 10 === 0) {
                            gstr += "<div>";
                        }
                        if(tagArr.indexOf(items[j].srcName)>-1)
                            gstr += "<div id='tag_"+items[j].srcID+"'  class='tagCheckbox'><input type='checkbox' checked='checked' id='CheckTag_"+items[j].srcID+"' tagID='"+items[j].srcID+"' value='" + items[j].srcName + "' onclick='showDes(this);'/>" + items[j].srcName + "</div>";
                        else
                            gstr += "<div id='tag_"+items[j].srcID+"'  class='tagCheckbox'><input type='checkbox' id='CheckTag_"+items[j].srcID+"' tagID='"+items[j].srcID+"' value='" + items[j].srcName + "' onclick='showDes(this);'/>" + items[j].srcName + "</div>";
                    }
                    gstr += "</div><div style='clear:both;'></div>";
                    $("#div12").append(gstr);
                }
            }
        }
    });
};

function setChecked() {
    if(tags!="") {
        var tagArr = tags.split(",");
        $(tagArr).each(function () {
            var $newDivStr = $("<span class='selectedTag' value='" + this + "' onclick='desevent(this);' >" + this + "</span>&nbsp");
            $("#div11").append($newDivStr);
        })
    }
}

function HideDiv(obj) {
    $(obj).next("div").toggle();
}
function showDes(obj) {
    var SelectedtagID = "Selectedtag_"+obj.getAttributeNode("tagID").nodeValue;
    var $Selectedtag = $(" #div11>span[value='"+obj.value+"']")
    if (obj.checked == true && $Selectedtag.length==0) {
        var $newDivStr = $("<span id='"+SelectedtagID+"' class='selectedTag' value='" + obj.value + "' tagID='"+obj.getAttributeNode("tagID").nodeValue+"' onclick='desevent(this);' >" + obj.value + "</span>&nbsp");
        $("#div11").append($newDivStr);
    }
    else if(obj.checked != true && $Selectedtag.length>0) {
        $(" #div11>span[value='"+obj.value+"']").remove();
    }
}
function desevent(obj) {
    var $obj = $(obj)
    $("#div12 input:checkbox[value='"+$obj.text()+"']").removeAttr("checked");
    $obj.remove();
}

$("#doSave").click(function () {
    tags="";
     $(".selectedTag").each(function () {
         if(tags=="")
         tags+=$(this).text();
         else
             tags+=","+$(this).text();
     })
    parent.tag_form.doSave(tags);
});
//点击取消按钮
$("#doCancel").click(function () {
    parent.tag_form.doCancel();
});
