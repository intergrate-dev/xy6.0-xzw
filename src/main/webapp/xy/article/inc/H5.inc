<form id="form">
    <%@include file="CommonUpper.inc" %>
    <div class="underTop">
        <div id="divMain">
            <%@include file="CommonMiddle.inc" %>
            <li class="mgl abstract1"><span class="left mgr">摘要：</span>
                <textarea id="a_abstract" name="a_abstract" class="validate[maxSize[2000]] a_abstract"><c:out value="${article.summary}"/></textarea>
                <c:if test="${canEditStyle}">
                    <input class="left btn" type="button" id="btnEditAbstract" value="编辑样式"/>
                </c:if>
            </li>
            <li class="link1">
                <span class="left require star">*</span>
                <span class="left mgr">H5作品：</span>
                <input class="specialModelInput validate[required]" type="text" name="a_linkName" id="a_linkName" value="<c:out value="${article.linkName}"/>" readonly/>
                <input type="hidden" name="a_linkID" id="a_linkID" value="<c:out value="${article.linkID}"/>"/>
                <input type="hidden" name="a_url" id="a_url" value="<c:out value="${article.url}"/>"/>
                <input type="hidden" name="a_urlPad" id="a_urlPad" value="<c:out value="${article.urlPad}"/>"/>
                <input class="btn " type="button" id="btnH5" value="选择"/>
            </li>
        </div>
        <div id="divRight">
            <%@include file="CommonRight.inc" %>
        </div>
    </div>
</form>
<style type="text/css">
    .specialModelInput {
        width: 500px !important;
        float: left;
        margin-right: 10px;
    }

    .abstract1 {
        margin-top: 24px;
        height: 160px;
    }

    #a_linkName {
        box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
        width: 513px;
        border: 1px solid #ddd;
        height: 28px;
        margin-left: 0px;
    }

    .star {
        margin-left: 10px;
        margin-top: 0;
    }
</style>
<script>
    var H5 = function(){
    };
    H5.prototype = {
        dialog: null,
        go: function(){
            this.init();
        },
        init: function(){
            var _this = this;
            $("#btnH5").click(function(){
                var url = "../../xy/article/H5file.html?URL=" + article.url;
                var pos = {left: "100px", top: "50px", width: "1000px", height: "500px"};
                _this.dialog = e5.dialog({type: "iframe", value: url}, {
                    showTitle: true,
                    title: "选择H5设计",
                    width: "1000px",
                    height: "500px",
                    pos: pos,
                    resizable: false
                });
                _this.dialog.show();
            });

            $("#a_multimediaLink").attr("readonly", "readonly");
        },
        closeDialog: function(){
            this.dialog.close();
        },
        confirmDialog: function(json){
            //$("#a_linkID").val(json.id);
            $("#a_linkName").val(json.title);
            $("#a_urlPad").val(json.url);
            $("#a_url").val(json.url);
            this.dialog.close();
        }
    };
    var h5 = new H5();
    h5.go();

</script>