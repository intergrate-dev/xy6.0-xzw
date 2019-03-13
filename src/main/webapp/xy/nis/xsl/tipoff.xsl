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
				<th width="150" id="TH_SYS_AUTHORS" class="center"><span>用户</span></th>
				<th id="TH_a_content"><span>内容</span></th>
			</tr>
		<xsl:for-each select="DocItem" >
			<tr>
				<xsl:attribute name="id"><xsl:value-of select="DocID"/></xsl:attribute>
				<xsl:attribute name="libid"><xsl:value-of select="DocLibID"/></xsl:attribute>
				<td class="center" style="vertical-align:top;">
					<xsl:attribute name="userID"><xsl:value-of select="SYS_AUTHORID"/></xsl:attribute>
					<xsl:attribute name="userName"><xsl:value-of select="SYS_AUTHORS"/></xsl:attribute>
					
					<xsl:value-of select="SYS_AUTHORS"/><br/>
					<span class="listDate">
					<xsl:value-of select="a_contactNo"/>
					</span>
				</td>
				<td>
					<b><xsl:value-of select="SYS_TOPIC"/></b>
					<br/>
					<xsl:value-of select="a_content"/>
					<div class="listAttachments">
						<xsl:attribute name="att"><xsl:value-of select="a_attachments"/></xsl:attribute>
					</div>
					<span class="listDate">
						<xsl:value-of select="SYS_CREATED"/>　　
						id：<xsl:value-of select="DocID"/>
					</span>
				</td>
			</tr>
		</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
