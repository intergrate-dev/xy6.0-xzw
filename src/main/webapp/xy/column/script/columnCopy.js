/**
 * Created by Administrator on 2017/2/13.
 */
var colCopy = {
    siteID: "",
    temDoclibID: "",
    webTem: "",
    padTem: "",
    batSubmit: function () {

        var param = {
            copyTem: document.getElementById("copyTem").checked,
            newWebTemName: "",
            newPadTemName: "",
            newColName: document.getElementById("newColName").value,
        }
        if (param.copyTem && colCopy.Duplicate(false)) {
            alert("模板名称重复")
            return;
        }
        else {
            if (colCopy.webTem)  param.newWebTemName =document.getElementById("newWebTemName").value;
            if (colCopy.padTem) param.newPadTemName = document.getElementById("newPadTemName").value;
        }
        if ((param.copyTem && ((colCopy.webTem && param.newWebTemName == "") || (colCopy.padTem && param.newPadTemName == "" )))
            || param.newColName == "") {
            alert("模板和栏目名称不能为空")
            return;
        }
        ;
        document.getElementById("submitBtn").setAttribute("onclick", "");
        window.opener.col_menu.colCopyCallback(param);
        window.close();
    },

    closeWin: function () {
        window.close();
    },
    showTemName: function () {
        if (document.getElementById("copyTem").checked) {
            document.getElementById("newTemNameDiv").style.display = "inline";
        }
        else {
            document.getElementById("newTemNameDiv").style.display = "none";
        }
    },
    Duplicate: function (needAsync) {
        var result = false;
        var $temName;
        if (colCopy.webTem) {
            if (colCopy.padTem) {
                $temName = $("#newWebTemName");
                theURL = "../../xy/Duplicate.do"
                    + "?DocLibID=" + colCopy.temDoclibID
                    + "&siteID=" + colCopy.siteID
                    + "&value=" + $temName.attr("value")
                    + "&groupID=" + $temName.attr("groupID")
                ;
                $.ajax({
                    async: needAsync,
                    url: theURL,
                    success: function (data) {
                        result = result || (data == "1");
                        if (data != "1") {
                            $("#webTemDuplicate").hide();
                        } else {
                            $("#webTemDuplicate").show();
                        }
                    }
                })
            }
            if (colCopy.padTem) {
                $temName = $("#newPadTemName");
                theURL = "../../xy/Duplicate.do"
                    + "?DocLibID=" + colCopy.temDoclibID
                    + "&siteID=" + colCopy.siteID
                    + "&value=" + $temName.attr("value")
                    + "&groupID=" + $temName.attr("groupID")
                ;
                $.ajax({
                    url: theURL,
                    async: needAsync,
                    success: function (data) {
                        result = result || (data == "1");
                        if (data == "1") {
                            $("#padTemDuplicate").show();
                        } else {
                            $("#padTemDuplicate").hide();
                        }
                    }
                })
            }

        }
        return result;
    }
}