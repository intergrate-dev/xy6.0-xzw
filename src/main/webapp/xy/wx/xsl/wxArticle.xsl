<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" omit-xml-declaration="yes" encoding="utf-8"/> 
	<xsl:template match="/">
		<style>
			.status{border-top: 1px solid #ddd;}
		</style>
		<script>
			function choose(evt) {
				evt = evt || event;
				var src = $(evt.target);
				var libID = src.attr("libid");
				var id = src.attr("id");
				
				parent.weixinEditor.showDetail(libID, id);
			}
		</script>
		<div id="divPinHeader" style="overflow:hidden;"><table id="tablePinHeader" cellpadding='0' cellspacing='0' class='doclist'/></div>
		<div id="listing" oncontextmenu="return false">
			<xsl:apply-templates select="DocList"/>
		</div>
	</xsl:template>
	<xsl:template match="DocList">
		<xsl:attribute name="totalCount"><xsl:value-of select="TotalSum" /></xsl:attribute>
		<table cellPadding="0" cellSpacing="0" class="doclist">
			<tr>
				<th style="width:300px;"><span>标题</span></th>
				<th><span>作者</span></th>
				<th><span>最后处理人</span></th>
				<th><span>最后处理时间</span></th>
				<th><span>ID</span></th>
			</tr>
		<xsl:for-each select="DocItem" >
			<tr>
				<xsl:attribute name="id"><xsl:value-of select="DocID"/></xsl:attribute>
				<xsl:attribute name="libid"><xsl:value-of select="DocLibID"/></xsl:attribute>
				<td>
					<span class="list-title"><a onclick="choose()">
						<xsl:attribute name="id"><xsl:value-of select="DocID"/></xsl:attribute>
						<xsl:attribute name="libid"><xsl:value-of select="DocLibID"/></xsl:attribute>
						<xsl:value-of select="SYS_TOPIC"/>
					</a></span>
				</td>
				<td>
					<span class="list-title"><xsl:value-of select="SYS_AUTHORS"/></span>
				</td>
				<td>
					<span class="list-title"><xsl:value-of select="SYS_CURRENTUSERNAME"/></span>
				</td>
				<td>
					<span class="list-pubTime"><xsl:value-of select="substring(SYS_LASTMODIFIED,6,5)"/></span>
				</td>
				<td>
					<span class="list-title"><xsl:value-of select="DocID"/></span>
				</td>
			</tr>
		</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
