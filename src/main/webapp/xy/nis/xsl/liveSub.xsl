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
				<th id="TH_a_content" class="center"><span>内容</span></th>
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
					<xsl:if test="a_parentID[. = '0']"><span title="话题贴"><font color="red">【话题】</font></span></xsl:if>
					<xsl:if test="a_parentID[. = '0']">
						<a target="_blank">
							<xsl:attribute name="title"><xsl:value-of select="SYS_TOPIC" /></xsl:attribute>
							<xsl:attribute name="href">nis/livePage.jsp?id=<xsl:value-of select="DocID"/></xsl:attribute>
							<b><xsl:value-of select="SYS_TOPIC"/></b>
						</a>
						<br/>
					</xsl:if>

                    <xsl:if test="a_isTop[. = '0']">
                        <div><strong><xsl:value-of select="a_title"/></strong></div>
                    </xsl:if>
                    <xsl:if test="a_isTop[. = '1']">
                        <div><span id="VALUE_a_order"><img src="../Icons/top.png" title="置顶"/></span>
                        <strong><xsl:value-of select="a_title"/></strong></div>
                    </xsl:if>

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
						<span><xsl:value-of select="SYS_AUTHORS"/></span>
						<span>
						<xsl:choose>
							<xsl:when test="a_sourceType[. = '0']">(嘉宾)</xsl:when>
							<xsl:when test="a_sourceType[. = '1']">(主持人)</xsl:when>
							<xsl:when test="a_sourceType[. = '2']">(记者)</xsl:when>
						</xsl:choose>
						</span>　
						<xsl:value-of select="a_location"/>　　
						id：<xsl:value-of select="DocID"/>
					</span>
				</td>
			</tr>
		</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
