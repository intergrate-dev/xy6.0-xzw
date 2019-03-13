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
<!--header-->
</tr>
<xsl:for-each select="DocItem" >
<tr>
	<xsl:attribute name="id"><xsl:value-of select="DocID" /></xsl:attribute>
	<xsl:attribute name="libid"><xsl:value-of select="DocLibID" /></xsl:attribute>
	<!--content-->
</tr>
</xsl:for-each>
</table>
</xsl:template>
</xsl:stylesheet>