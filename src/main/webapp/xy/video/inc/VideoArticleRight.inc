﻿<form id="form">

<input type="hidden" id="isNew" name="isNew" value="true">  
<input type="hidden" id="DocID" name="DocID" value="<c:out value="${DocID}"/>">
<input type="hidden" id="a_siteID" name="a_siteID" value="<c:out value="${siteID}"/>">
<input type="hidden" id="a_type" name="a_type" value="<c:out value="${type}"/>">  
<input type="hidden" id="a_channel" name="a_channel" value="<c:out value="${channel}"/>">  
<input type="hidden" id="UUID" name="UUID" value="<c:out value="${UUID}"/>">
<input type="hidden" id="a_source" name="a_source" value=""> 
<input type="hidden" id="a_sourceID" name="a_sourceID" value="0">
<input name="a_isSensitive" id="a_isSensitive" type="hidden" value="0">
<input name="a_isIllegal" id="a_isIllegal" type="hidden" value="0">
<input name="a_catID" id="a_catID" type="hidden" value="0">
<input name="a_columnRel" id="a_columnRel" type="hidden" value="">
<input name="a_columnRelID" id="a_columnRelID" type="hidden" value="">
<input name="a_content" id="a_content" type="hidden" value="">

<table>
	<tr>
		<td class="lab">标题<font color="red">*</font></td>
		<td colspan="3" width=600>
			<input type="text" id="SYS_TOPIC" name="SYS_TOPIC" value="<c:out value="${title}"/>" class="validate[maxSize[1024],required] title-text">
			<label for="SYS_TOPIC" style="display:inline-block;">
				<input style="margin-top: -2px; margin-right: 5px;" type="checkbox" id="a_copyright" name="a_copyright">原创
			</label>
		</td>
	</tr>
	<tr>
		<td class="lab">作者</td>
		<td colspan="3">
			<input type="text" id="SYS_AUTHORS" name="SYS_AUTHORS" class="validate[maxSize[255]] ">
		</td>
	</tr>
	<tr>
		<td class="lab">来源</td>
		<td>
			<input class="left artMeg" type="text" id="findSourceInput" value="" autocomplete="off" disabled="disabled" style="width:152px;">
			<input class="btn" type="button" id="btnSource" title="选择来源" value="选择" style="margin-top:-7px;">
		</td>
		<td class="lab">来源链接</td>
		<td>
			<input type="text" id="a_sourceUrl" name="a_sourceUrl" class="validate[maxSize[255]] ">
		</td>
	</tr>
	<tr>
		<td class="lab">标题图</td>
		<td colspan="3">
			<div class="rightTab" id="tab1">
				<div id="topicPicSmallDiv" itype="small" class="picTopic" title="列表图片">
					<p class="plus">+</p>
					<p class="word"><c:out value="${smallRadio}"/></p>
					<span class="icon-remove"></span>
				</div>
			
			    <div id="topicPicMidDiv" itype="mid" class="picTopic" title="标题图片">
			        <p class="plus">+</p>
			        <p class="word"><c:out value="${midRadio}"/></p>
			        <span class="icon-remove"></span>
			    </div>
			
				<div id="topicPicBigDiv" itype="big" class="picTopic" title="焦点图片">
					<p class="plus">+</p>
					<p class="word"><c:out value="${bigRadio}"/></p>
					<span class="icon-remove"></span>
				</div>
				<div id="localFileDiv"></div>
			</div>
		</td>
	</tr>
	<tr>
		<td class="lab">摘要</td>
		<td colspan="3">
			<textarea class="smallTextarea" id="a_abstract" name="a_abstract"><c:out value="${content}"/></textarea>
		</td>
	</tr>
	<tr>
		<td class="lab">关键词</td>
		<td colspan="3">
			<input type="text" id="a_keyword" name="a_keyword" class="validate[maxSize[255]] long-text"> 
		</td>
	</tr>
	<tr>
		<td class="lab">标签</td>
		<td colspan="3">
			<input type="text" id="a_tag" name="a_tag" value="" class="validate[maxSize[255]] long-text">
		</td>
	</tr>
	<tr>
		<td class="lab">链接标题</td>
		<td colspan="3">
			<input type="text" id="a_linkTitle" name="a_linkTitle" value="" class="long-text">
			<div id="a_ltwc_p"><span id="a_ltwc_span">0</span>/1024</div>
		</td>
	</tr>
	<tr>
		<td class="lab">短标题</td>
		<td colspan="3">
			<input type="text" id="a_shortTitle" name="a_shortTitle" value="" class="long-text">
			<div id="a_stwc_p"><span id="a_stwc_span">0</span>/1024</div>
		</td>
	</tr>
	<tr>
		<td class="lab">稿件属性</td>
		<td colspan="3">
		  <select  name="a_attr" id="a_attr" style="">
			  <option value="63" selected="selected">普通新闻(63)</option>
			  <option value="62">头条新闻(62)</option>
			  <option value="61">图片新闻(61)</option>
			  <option value="64">重要新闻(64)</option>
			  <option value="65">其它新闻(65)</option>
		  </select>
		</td>
	</tr>
	<tr>
		<td class="lab">WEB版模板</td>
		<td colspan="3">
			<span id="tptNameSpan_pc"></span>
            <input id="templateSelect_pc" type="text" class="templateSelect" value="" autocomplete="off" disabled="disabled">
            <input id="btnChgTpt_pc" data-dc="pc" class="btn" type="button" value="切换" style="margin-top:-7px;">
            <input id="btnChgTpt_pc_clear" data-dc="pc" class="btn" type="button" value="清空" style="margin-top:-7px;">
        	<input type="hidden" name="a_templateID" id="a_templateID" value=""/>
        	<input type="hidden" name="a_template" id="a_template" value=""/>
		</td>
	</tr>
	<tr>
		<td class="lab">触屏版模板</td>
		<td colspan="3">
			<span id="tptNameSpan_app" class="stemplate"></span>
            <input id="templateSelect_app" type="text" class="templateSelect" value="" autocomplete="off" disabled="disabled">
            <input id="btnChgTpt_app" data-dc="app" class="btn" type="button" value="切换" style="margin-top:-7px;">
            <input id="btnChgTpt_app_clear" data-dc="app" class="btn" type="button" value="清空" style="margin-top:-7px;">
            <input type="hidden" name="a_templatePadID" id="a_templatePadID" value=""/>
        	<input type="hidden" name="a_templatePad" id="a_templatePad" value=""/>
		</td>
	</tr>
	<tr>
		<td class="lab">编辑</td>
		<td>
			<input class="editors" type="text" id="a_editor" name="a_editor" value="">
		</td>
		<td class="lab">责任编辑</td>
		<td>
			<input class="editors" type="text" id="a_liability" name="a_liability" value="">
		</td>
	</tr>
	<tr>
		<td class="lab">初始阅读数</td>
		<td>
			<input class="editors" type="text" id="a_countClickInitial" name="a_countClickInitial" title="稿件发布后显示的阅读数初始值" value="0">
		</td>
		<td>
			<label for="a_isExclusive">
				<input style="margin-top: -2px; margin-right: 5px;" type="checkbox" value="1" id="a_isExclusive" name="a_isExclusive">独家
			</label>
		</td>
		<td>
			<label for="a_discussClosed">
				<input style="margin-top: -2px; margin-right: 5px;" type="checkbox" value="1" id="a_discussClosed" name="a_discussClosed">关闭评论
			</label>
		</td>
	</tr>
        
</table>
</form>