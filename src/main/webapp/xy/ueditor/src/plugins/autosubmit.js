/**
 * 快捷键提交
 * @file
 * @since 1.2.6.1
 */

/**
 * 提交表单、插入图片、设置水印、设置为标题图片、两端对齐
 * @command autosubmit
 * @method execCommand
 * @param { String } cmd 命令字符串
 * @example
 * ```javascript
 * editor.execCommand( 'autosubmit' );
 * ```
 */

UE.plugin.register('autosubmit', function () {
	return {
		shortcutkey: {
			// "autosubmit":"ctrl+13" //手动提交
			autosubmit: "ctrl+81",//Ctrl+Q //提交
			aotoalign: "ctrl+75",//Ctrl+K 两端对齐
			aotouploaderimage: "ctrl+76",//Ctrl+L 上传图片
			aototitleimage: "ctrl+89",//Ctrl+Y 设置标记图
		},
		commands: {
			'autosubmit': {
				execCommand: function () {
					$("#btnPublish").click();
					/*var me = this,
					 form = domUtils.findParentByTagName(me.iframe, "form", false);
					 if (form) {
					 if (me.fireEvent("beforesubmit") === false) {
					 return;
					 }
					 me.sync();
					 form.submit();
					 }*/
				}
			},
			'aotoalign': {
				execCommand: function () {
					$("#edui83_body").click();
				}
			},
			'aotouploaderimage': {
				execCommand: function () {
					$("#edui91_body").click();
				}
			},
			'aototitleimage': {
				execCommand: function () {
					$("#imageSetTitle").click();
				}
			}
		}
	}
});