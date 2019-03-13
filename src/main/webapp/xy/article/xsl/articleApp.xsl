<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" omit-xml-declaration="yes" encoding="utf-8"/> 
	<xsl:template match="/">
		<div id="divPinHeader" style="overflow:hidden;"><table id="tablePinHeader" cellpadding='0' cellspacing='0' class='doclist'/></div>
		<div id="listing" oncontextmenu="return false">
			<xsl:apply-templates select="DocList"/>
		</div>
	</xsl:template>
	<xsl:template match="DocList">
		<xsl:attribute name="totalCount"><xsl:value-of select="TotalSum" /></xsl:attribute>
		<table cellPadding="0" cellSpacing="0" class="doclist">
			<tr><th><span>稿件</span></th></tr>
		<xsl:for-each select="DocItem" >
			<tr>
				<xsl:attribute name="id"><xsl:value-of select="DocID"/></xsl:attribute>
				<xsl:attribute name="libid"><xsl:value-of select="DocLibID"/></xsl:attribute>
				<td>
					<div class="list-line1">
						<span class="list-type">
						<xsl:choose>
							<xsl:when test="a_type[. = '0']"><img src="../Icons/article.png" title="文章"/></xsl:when>
							<xsl:when test="a_type[. = '1']"><img src="../Icons/pic.png" title="组图"/></xsl:when>
							<xsl:when test="a_type[. = '2']"><img src="../Icons/video2.png" title="视频"/></xsl:when>
							<xsl:when test="a_type[. = '3']"><img src="../Icons/special.png" title="专题"/></xsl:when>
							<xsl:when test="a_type[. = '4']"><img src="../Icons/link.png" title="链接"/></xsl:when>
							<xsl:when test="a_type[. = '5']"><img src="../Icons/multi.png" title="多标题"/></xsl:when>
							<xsl:when test="a_type[. = '6']"><img src="../Icons/live.png" title="直播"/></xsl:when>
							<xsl:when test="a_type[. = '7']"><img src="../Icons/activity.png" title="活动"/></xsl:when>
							<xsl:when test="a_type[. = '8']"><img src="../Icons/ad.png" title="广告"/></xsl:when>
						</xsl:choose>
						</span>
						<span class="list-title"><xsl:value-of select="SYS_TOPIC"/></span>
					</div>
					<div class="list-line2">
						<span class="list-pubTime"><xsl:value-of select="substring(a_pubTime,6,14)"/>　</span>
						<span class="list-editor"><xsl:value-of select="a_editor"/>　</span>
						<span class="list-id">ID:<xsl:value-of select="DocID"/>　</span>
						<span class="list-status">
						<xsl:choose>
							<xsl:when test="a_status[. = '0']"><img src="../Icons/txt.png"/><span class="list-status0">未发布</span></xsl:when>
							<xsl:when test="a_status[. = '1']"><img src="../Icons/pubed.png"/><span class="list-status1">已发布</span></xsl:when>
							<xsl:when test="a_status[. = '2']"><img src="../Icons/pubTime.png"/><span class="list-status2">定时发布</span></xsl:when>
							<xsl:when test="a_status[. = '3']"><img src="../Icons/pubing.png"/><span class="list-status3">正在发布</span></xsl:when>
							<xsl:when test="a_status[. = '4']"><img src="../Icons/auditing.png"/><span class="list-status4">待审批</span></xsl:when>
							<xsl:when test="a_status[. = '5']"><img src="../Icons/reject.png"/><span class="list-status5">已驳回</span></xsl:when>
							<xsl:when test="a_status[. = '6']"><img src="../Icons/pubextracting.png"/><span class="list-status6">等待抽图</span></xsl:when>
						</xsl:choose>
						</span>
					</div>
				</td>
			</tr>
		</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
