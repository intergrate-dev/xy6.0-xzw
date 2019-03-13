<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" omit-xml-declaration="yes" encoding="utf-8"/> 
	<xsl:template match="/">
		<!-- 瀑布流图册列表 -->
		<span id="listing" album="true" oncontextmenu="return false">
			<xsl:apply-templates select="DocList"/>
		</span>
		<style type="text/css">
			.col{float:left; display:inline; width:200px; height:auto; overflow:hidden; margin-right:10px;}
			.liBox{width:180px; height:auto; overflow:hidden; margin-top:10px; padding:10px; border:1px solid #CCC;}
			.liBox img{vertical-align:bottom; border:none;}
		</style>
		<script type="text/javascript"><![CDATA[
			$("img").load(function(){
				var w = $(this).width();
				var h = $(this).height();
				if(w != 180){
					$(this).width("180px");
					$(this).height((h*180)/w+"px");
				}
			}); 
		]]></script>
	</xsl:template>
<xsl:template match="DocList">
	<xsl:attribute name="totalCount"><xsl:value-of select="TotalSum"/></xsl:attribute>
	<xsl:variable name="albumcolumn" select="5"/>
	<input type="checkbox" name="cbAll" id="cbAll" title="全选/取消" />
	<xsl:param name="idx" select="0"/>
	<xsl:if test="$idx &lt; $albumcolumn">
		<xsl:call-template name="lineControl"> 
			<xsl:with-param name="idx" select="$idx + 1"/>
			<xsl:with-param name="albumcolumn" select="$albumcolumn"/>
		</xsl:call-template>
	</xsl:if>
</xsl:template>
<xsl:template name="lineControl">
    <xsl:param name="idx"/>
	<xsl:param name="albumcolumn"/>
	
 	<div class="col">
		<xsl:for-each select="DocItem[(position() - 1) mod $albumcolumn = ($idx - 1)]">
			<xsl:apply-templates select="."/>
		</xsl:for-each>
	</div>
	<xsl:if test="$idx &lt; $albumcolumn">
		<xsl:call-template name="lineControl">
			<xsl:with-param name="idx" select="$idx + 1"/>
			<xsl:with-param name="albumcolumn" select="$albumcolumn"/>
		</xsl:call-template>
	</xsl:if>
</xsl:template>
<xsl:template match="DocItem">
	<table class="liBox">
	<xsl:attribute name="id"><xsl:value-of select="DocID"/></xsl:attribute>
	<xsl:attribute name="libid"><xsl:value-of select="DocLibID"/></xsl:attribute>
		<caption>
			<input type="checkbox" name="cb">
				<xsl:attribute name="value"><xsl:value-of select="DocID"/></xsl:attribute>
			</input>				
		</caption>
		<tbody>
		<tr>
			<td colspan="2" align="center" class="album_image_td">
				<img src="../images/default_img.png"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<xsl:value-of select="SYS_TOPIC" />
				<xsl:if test="SYS_ISLOCKED[. = '1']"><img src="../images/FileLock.gif"/></xsl:if>
			</td>
		</tr>
		</tbody>
	</table>
</xsl:template>
</xsl:stylesheet>
