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
			<tr>
				<th width="30" id="TH_Checkbox" class="center"><input type="checkbox" name="cbAll" id="cbAll" title="全选/全不选" /></th>
				<th id="TH_a_content"><span>话题</span></th>
			</tr>
		<xsl:for-each select="DocItem">
			<tr>
				<xsl:attribute name="id"><xsl:value-of select="DocID"/></xsl:attribute>
				<xsl:attribute name="libid"><xsl:value-of select="DocLibID"/></xsl:attribute>
				<td class="center" style="vertical-align:top;">
					<input type="checkbox" name="cb">
					<xsl:attribute name="value"><xsl:value-of select="DocID"/></xsl:attribute>
					</input>
				</td>
				<td>
					<xsl:if test="a_order[. > '100000000000000000']"><span title="置顶话题"><font color="green">【顶】</font></span></xsl:if>
					<xsl:if test="a_good[. = '1']"><span title="加精话题"><font color="red">【精】</font></span></xsl:if>
					（<xsl:value-of select="a_group"/>）<b><xsl:value-of select="SYS_TOPIC"/></b>
					<br/>
					<xsl:value-of select="a_content"/>
					<div class="listAttachments">
						<xsl:attribute name="att"><xsl:value-of select="a_attachments"/></xsl:attribute>
					</div>
					<div class="listOps">
						<xsl:attribute name="docID"><xsl:value-of select="DocID"/></xsl:attribute>
						<xsl:attribute name="flowNodeID"><xsl:value-of select="SYS_CURRENTNODE"/></xsl:attribute>
					</div>
					<span class="listDate">
						<xsl:value-of select="SYS_CREATED"/>　
						<xsl:value-of select="SYS_AUTHORS"/>　
						关注(<xsl:value-of select="a_countFollow"/>)　
						<xsl:if test="a_discussClosed[. = '1']">禁评　</xsl:if>
						<xsl:if test="a_questionClosed[. = '1']">禁问　</xsl:if>
						<xsl:if test="a_status[. = '0']"><font color="red">未发布</font>　</xsl:if>
						id：<xsl:value-of select="DocID"/>
					</span>
				</td>
			</tr>
		</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
