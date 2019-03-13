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
		<input type="checkbox" name="cbAll" id="cbAll" title="全选/取消" />
		<xsl:for-each select="DocItem">
		<table class="album_table">
		<xsl:attribute name="id"><xsl:value-of select="DocID" /></xsl:attribute>
		<xsl:attribute name="libid"><xsl:value-of select="DocLibID" /></xsl:attribute>
			<caption>
					<input type="checkbox" name="cb">
					<xsl:attribute name="value"><xsl:value-of select="DocID"/></xsl:attribute>
					</input>
					<xsl:value-of select="DocOrder"/>
			</caption>
			<tbody>
			<tr>
				<td colspan="2" align="center" class="album_image_td">
				<div style="text-align:left;">
				</div>
				<img src="../images/default_img.png"/>
				<!--
				<img>
					<xsl:attribute name="src">
				./binary.do?TableName=DOM_IMAGE&amp;KeyName=REFID&amp;FieldName=IMG_SMALL&amp;KeyID=<xsl:value-of select="PHOTO_REFID" />
					</xsl:attribute>
				</img>
				-->
				</td>
			</tr>
			<tr>
				<td><xsl:value-of select="SYS_DOCUMENTID"/></td>
				<td><xsl:value-of select="SYS_AUTHORS"/>---<xsl:value-of select="SYS_CURRENTSTATUS"/></td>
			</tr>
			<tr>
				<td colspan="2">
					<xsl:value-of select="SYS_TOPIC" />
					<xsl:choose>
						<xsl:when test="SYS_ISLOCKED[. = '1']"><img src="../images/FileLock.gif"/></xsl:when>
					</xsl:choose>
				</td>
			</tr>
			</tbody>
		</table>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
