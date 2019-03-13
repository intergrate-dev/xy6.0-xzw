<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" omit-xml-declaration="yes" encoding="utf-8"/> 
	<xsl:template match="/">
		<span id="listing" album="true" oncontextmenu="return false">
			<xsl:apply-templates select="DocList"/>
		</span>
	</xsl:template>
	<xsl:template match="DocList">
		<xsl:attribute name="totalCount"><xsl:value-of select="TotalSum" /></xsl:attribute>
		<xsl:for-each select="DocItem">
		<table class="album_table" style="width:130px;height:30px;cursor:pointer;">
			<xsl:attribute name="id"><xsl:value-of select="DocID" /></xsl:attribute>
			<xsl:attribute name="libid"><xsl:value-of select="DocLibID" /></xsl:attribute>
			<tr>
				<!--td class="list-check" style="width: 30px;"><input type="checkbox" name="cb" value="205"/></td-->
				<td nowrap="1">
					<xsl:attribute name="title"><xsl:value-of select="SYS_AUTHORS"/></xsl:attribute>
					<xsl:value-of select="SYS_AUTHORS" />
				</td>
			</tr>
		</table>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
