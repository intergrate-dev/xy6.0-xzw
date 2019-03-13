<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" omit-xml-declaration="yes" encoding="utf-8"/> 
	<xsl:template match="/">
		<style>
			body,.doclist td{background-color:#eee;}
			
		</style>
		<div id="divPinHeader" style="overflow:hidden;"><table id="tablePinHeader" cellpadding='0' cellspacing='0' class='doclist'/></div>
		<div id="listing" oncontextmenu="return false">
			<xsl:apply-templates select="DocList"/>
		</div>
	</xsl:template>
	<xsl:template match="DocList">
		<xsl:attribute name="totalCount"><xsl:value-of select="TotalSum" /></xsl:attribute>
		<table cellPadding="0" cellSpacing="0" class="doclist">
			<tr>
				<th width="30" id="TH_DocOrder"></th>
				<th id="TH_a_content" class="center"><span>话题</span></th>
			</tr>
		<xsl:for-each select="DocItem">
			<tr>
				<xsl:attribute name="id"><xsl:value-of select="DocID"/></xsl:attribute>
				<xsl:attribute name="libid"><xsl:value-of select="DocLibID"/></xsl:attribute>
				<td><xsl:value-of select="DocOrder"/></td>
				<td>
					<xsl:if test="a_order[. > '100000000000000000']"><span title="置顶帖"><font color="green">【顶】</font></span></xsl:if>
					<xsl:if test="a_good[. = '1']"><span title="精华帖"><font color="red">【精】</font></span></xsl:if>
					<a target="_blank">
						<xsl:attribute name="title"><xsl:value-of select="SYS_TOPIC" /></xsl:attribute>
						<xsl:attribute name="href">nis/livePage.jsp?id=<xsl:value-of select="DocID"/></xsl:attribute>
						<b><xsl:value-of select="SYS_TOPIC"/></b>
					</a>
					<br/>
					<div class="listAttachments">
					</div>
					<div class="listOps">
						<xsl:attribute name="docID"><xsl:value-of select="DocID"/></xsl:attribute>
						<xsl:attribute name="flowNodeID"><xsl:value-of select="SYS_CURRENTNODE"/></xsl:attribute>
					</div>
					<span class="listDate">
						<xsl:value-of select="SYS_CREATED"/>　
						<span>
							<xsl:value-of select="SYS_AUTHORS"/>
						<xsl:choose>
							<xsl:when test="a_sourceType[. = '0']">(嘉宾)</xsl:when>
							<xsl:when test="a_sourceType[. = '1']">(主持人)</xsl:when>
							<xsl:when test="a_sourceType[. = '2']">(网友)</xsl:when>
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
