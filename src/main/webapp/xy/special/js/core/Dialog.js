/**
 * Created by isaac_gu on 2016/4/15.
 */
(function(window, $, LE){
    LE.options["Dialog"] = {
        picUploadDialog: "dialog/imageupload.html?",
        multiPicUploadDialog: "dialog/multiImageupload.html?",
        navigationDialog: "dialog/navigation.jsp?type=special&ch=0",
        picEditDialog: "dialog/image-editing.html?",
        thumbListDialog: "dialog/thumbnailList.jsp?type=0&ch=0",
        listGroupDialog: "dialog/articlelistdilog.jsp?type=1&ch=0",
        detailSettingDialog: "dialog/detailSetting.html?",
        listSettingDialog: "dialog/listSetting.html?",
        exampleDialog: "dialog/thumbnailList.html?",
        codeDialog: "dialog/code.html?",
        listAddandSettingDialog: "dialog/listAddandSetting.html?",
        lineChartDataSettingDialog: "dialog/lineChartDataSetting.html?",
        excelUploadDialog:"dialog/excelupload.html?",
        updaListDialog: "dialog/updateList.jsp?type=special&ch=0"
    };

    LE.cores["Dialog"] = function(){
        var callback = null;
        var data = null;
        var callbackII = null;
        var dataII = null;

        var initDialog = function(){
            $('#LEDialog').on('show.bs.modal', function(){

            });

            $('#LEDialog-II').on('show.bs.modal', function(){

            });
        };

        function toggleDialog(url){
            $('#LEDialog').modal("toggle");
            url && $('#dialogFrame').attr("src", url);
        }

        function openDialog(fn, url, _data){
            callback = fn;
            if(url.indexOf("=") != -1){
                url += "&";
            }

            var timestamp = new Date().getTime();
            url += "lets=" + timestamp;
            toggleDialog(url);
            data = _data;
        }

        function openDialogII(fn, url, _data){
            callbackII = fn;
            if(url.indexOf("=") != -1){
                url += "&";
            }

            var timestamp = new Date().getTime();
            url += "lets=" + timestamp;
            $('#LEDialog-II').modal("show");
            url && $('#dialogFrame-II').attr("src", url);
            dataII = _data;
        }

        return {
            init: function(){
                initDialog();
            },
            toggleDialog: function(url, fn, _data, isII){
                if(fn == undefined || fn == null){
                    throw new Error("没有实现匿名方法！");
                    return;
                }
                if(isII){
                    openDialogII(fn, url, _data);
                } else{
                    openDialog(fn, url, _data);
                }

            },
            getData: function(){
                return data;
            },
            closeDialog: function(){
                if($("#LEDialog-II").is(":visible")){
                    $('#LEDialog-II').modal("hide");
                    dataII = null;
                    $('#dialogFrame-II').attr("src", "");
                    return;
                }

                $('#LEDialog').modal("hide");
                data = null;
                $('#dialogFrame').attr("src", "");
            },
            dialogConfirm: function(args){
                if($("#LEDialog-II").is(":visible")){
                    $('#LEDialog-II').modal("hide");
                    callbackII(args);
                    dataII = null;
                    $('#dialogFrame-II').attr("src", "");
                    return;
                }
                $('#LEDialog').modal("hide");
                callback(args);
                $('#dialogFrame').attr("src", "");
            }
        };
    };
})(window, jQuery, LE, undefined);
LEDialog = LE.cores["Dialog"]();