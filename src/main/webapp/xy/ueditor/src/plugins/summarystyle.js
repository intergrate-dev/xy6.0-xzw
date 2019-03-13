/**
 * Created by isaac_gu on 2016/2/23.
 */
UE.commands['insertsummarystyle'] = {
    execCommand: function (cmdName) {
        var me = UE.getEditor('editor'),
            range = me.selection.getRange(),
            _start = range.startContainer,
            sp = domUtils.findParentByTagName(_start, 'p', true),
            _end = range.endContainer,
            ep = domUtils.findParentByTagName(_end, 'p', true);
        var collapsed = range.collapsed,
            common = domUtils.getCommonAncestor(sp, ep),
            isChosenP = false;
        if (collapsed) {
            sp.className = sp.className.indexOf("summary_style") != -1 ? sp.className.replace("summary_style", "") : sp.className + " summary_style";
        } else {
            if (sp === ep) {
                sp.className = sp.className.indexOf("summary_style") != -1 ? sp.className.replace("summary_style", "") : sp.className + " summary_style";
            } else {
                utils.each(domUtils.getElementsByTagName(common, 'p'), function (pp) {
                    if (pp === sp) {
                        isChosenP = true;
                    }
                    if (isChosenP) {
                        pp.className = pp.className.indexOf("summary_style") != -1 ? pp.className.replace("summary_style", "") : pp.className + " summary_style";
                    }
                    if (pp === ep) {
                        isChosenP = false;
                        return;
                    }
                });
            }

        }
        range.select();
    },
    queryCommandState: function () {
        return 0;
    }
};