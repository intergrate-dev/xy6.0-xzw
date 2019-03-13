///import core
///import uicore    
(function () {
    var utils = baidu.editor.utils,
        UIBase = baidu.editor.ui.UIBase;

    var AutoTypeSetPicker = baidu.editor.ui.AutoTypeSetPicker = function (options) {
        this.initOptions(options);
        this.initAutoTypeSetPicker();
    };
    AutoTypeSetPicker.prototype = {
        initAutoTypeSetPicker:function () {
            this.initUIBase();
        },
        getHtmlTpl:function () {
            var me = this.editor,
                opt = me.options.autotypeset,
                lang = me.getLang("autoTypeSet");

            var textAlignInputName = 'textAlignValue' + me.uid,
                imageBlockInputName = 'imageBlockLineValue' + me.uid,
                symbolConverInputName = 'symbolConverValue' + me.uid,
                symbolConverInputName_num = 'symbolConver_numValue' + me.uid,
                wordConvertInputName = 'wordConvertValue' + me.uid;//-------
            return '<div id="##" class="edui-autotypesetpicker %%">' +
            '<div class="edui-autotypesetpicker-body">' +
            '<table >' +
            '<tr>'+
                    '<td nowrap><input type="checkbox" name="mergeEmptyline" ' + (opt["mergeEmptyline"] ? "checked" : "" ) + '>' + lang.mergeLine + '</td>'+
                    '<td colspan="2"><input type="checkbox" name="removeEmptyline" ' + (opt["removeEmptyline"] ? "checked" : "" ) + '>' + lang.delLine + '</td>'+
            '</tr>' +
            '<tr>'+
                    '<td><input type="checkbox" name="removeClass" ' + (opt["removeClass"] ? "checked" : "" ) + '>' + lang.removeFormat + '</td>'+
                    '<td><input type="checkbox" name="indent" ' + (opt["indent"] ? "checked" : "" ) + '>' + lang.indent +
                '<input type="checkbox" name="keepSpaceBetweenPra" ' + (opt["keepSpaceBetweenPra"] ? "checked" : "" ) + '>'+ lang.keepSpaceBetweenPra +'</td>'+
                    //'<td><input type="checkbox" name="keepSpaceBetweenPra" ' + (opt["keepSpaceBetweenPra"] ? "checked" : "" ) + '>'+ lang.keepSpaceBetweenPra + '</td>'+
            '</tr>' +
            '<tr>' +
                    '<td nowrap><input type="checkbox" name="textAlign" ' + (opt["textAlign"] ? "checked" : "" ) + '>' + lang.alignment + '</td>' +
                    '<td colspan="2" id="' + textAlignInputName + '">' +
                            '<input type="radio" name="'+ textAlignInputName +'" value="left" ' + ((opt["textAlign"] && opt["textAlign"] == "left") ? "checked" : "") + '>' + me.getLang("justifyleft") +
                            '<input type="radio" name="'+ textAlignInputName +'" value="center" ' + ((opt["textAlign"] && opt["textAlign"] == "center") ? "checked" : "") + '>' + me.getLang("justifycenter") +
                            '<input type="radio" name="'+ textAlignInputName +'" value="right" ' + ((opt["textAlign"] && opt["textAlign"] == "right") ? "checked" : "") + '>' + me.getLang("justifyright") +
                    '</td>' +
            '</tr>' +
            '<tr>' +
                    '<td nowrap><input type="checkbox" name="imageBlockLine" ' + (opt["imageBlockLine"] ? "checked" : "" ) + '>' + lang.imageFloat + '</td>' +
                    '<td nowrap id="'+ imageBlockInputName +'">' +
                            '<input type="radio" name="'+ imageBlockInputName +'" value="none" ' + ((opt["imageBlockLine"] && opt["imageBlockLine"] == "none") ? "checked" : "") + '>' + me.getLang("default") +
                            '<input type="radio" name="'+ imageBlockInputName +'" value="left" ' + ((opt["imageBlockLine"] && opt["imageBlockLine"] == "left") ? "checked" : "") + '>' + me.getLang("justifyleft") +
                            '<input type="radio" name="'+ imageBlockInputName +'" value="center" ' + ((opt["imageBlockLine"] && opt["imageBlockLine"] == "center") ? "checked" : "") + '>' + me.getLang("justifycenter") +
                            '<input type="radio" name="'+ imageBlockInputName +'" value="right" ' + ((opt["imageBlockLine"] && opt["imageBlockLine"] == "right") ? "checked" : "") + '>' + me.getLang("justifyright") +
                    '</td>' +

            '</tr>' +
            '<tr>'+
                '<td nowrap colspan="3"><input type="checkbox" name="imgeTitle" ' + (opt["imageTitle"] ? "checked" : "" ) + '>' + lang.imageTitle + '</td>'+
            '</tr>'+
            '<tr>'+
                    '<td nowrap><input type="checkbox" name="clearFontSize" ' + (opt["clearFontSize"] ? "checked" : "" ) + '>' + lang.removeFontsize + '</td>'+
                    '<td colspan="2"><input type="checkbox" name="clearFontFamily" ' + (opt["clearFontFamily"] ? "checked" : "" ) + '>' + lang.removeFontFamily + '</td>'+
            '</tr>' +
            '<tr>'+
                    '<td nowrap colspan="3"><input type="checkbox" name="removeEmptyNode" ' + (opt["removeEmptyNode"] ? "checked" : "" ) + '>' + lang.removeHtml + '</td>'+
            '</tr>' +
            '<tr>'+
                    '<td nowrap colspan="3"><input type="checkbox" name="pasteFilter" ' + (opt["pasteFilter"] ? "checked" : "" ) + '>' + lang.pasteFilter + '</td>'+
            '</tr>' +
            '<tr>' +
                    '<td nowrap><input type="checkbox" name="symbolConver" ' + (opt["bdc2sb"] || opt["tobdc"] ? "checked" : "" ) + '>' + lang.symbol + '</td>' +
                    '<td id="' + symbolConverInputName + '">' +
                            '<input type="radio" name="bdc" value="bdc2sb" ' + (opt["bdc2sb"] ? "checked" : "" ) + '>' + lang.bdc2sb +
                    '<input type="radio" name="bdc" value="tobdc" ' + (opt["tobdc"] ? "checked" : "" ) + '>' + lang.tobdc + '' +
                    '</td>'+
            '</tr>' +
            '<tr>' +       //-----字母全半转换
                    '<td nowrap><input type="checkbox" name="wordConvert" ' + (opt["word_toall"] || opt["word_tohalf"] ? "checked" : "" ) + '>' + lang.wordchange + '</td>' +
                    '<td id="' + wordConvertInputName + '">' +
                            '<input type="radio" name="wordchange" value="word_tohalf" ' + (opt["word_tohalf"] ? "checked" : "" ) + '>' + lang.word_tohalf +
                            '<input type="radio" name="wordchange" value="word_toall" ' + (opt["word_toall"] ? "checked" : "" ) + '>' + lang.word_toall + '' +
                    '</td>'+
            '</tr>' +     
            '<tr>' +
                    '<td nowrap><input type="checkbox" name="symbolConver_num" ' + (opt["bdc2sb_num"] || opt["tobdc_num"] ? "checked" : "" ) + '>' + lang.symbol_num + '</td>' +
                    '<td id="' + symbolConverInputName_num + '">' +
                            '<input type="radio" name="bdc_num" value="bdc2sb_num" ' + (opt["bdc2sb_num"] ? "checked" : "" ) + '>' + lang.bdc2sb_num +
                            '<input type="radio" name="bdc_num" value="tobdc_num" ' + (opt["tobdc_num"] ? "checked" : "" ) + '>' + lang.tobdc_num + '' +
                    '</td>' +

            '</tr>' +
            '<tr>' +
                '<td nowrap colspan="3" align="right"><button >' + lang.run + '</button>&nbsp;<button >' + "取消" + '</button></td>' +
            '</tr>' +
            '</table>' +
            '</div>' +
            '</div>';


        },
        _UIBase_render:UIBase.prototype.render
    };
    utils.inherits(AutoTypeSetPicker, UIBase);
})();