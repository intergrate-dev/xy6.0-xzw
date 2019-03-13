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
				<th width="30" id="TH_Checkbox"><input type="checkbox" name="cbAll" id="cbAll" title="全选/全不选" /></th>
				<th width="30" id="TH_DocOrder"></th>
				<th width="30" id="TH_SYS_ISLOCKED"></th>
				<th id="TH_SYS_TOPIC"><span>标题</span></th>
				<th width="50" id="TH_SYS_CURRENTSTATUS"><span>状态</span></th>
				<th width="50" id="TH_SYS_CURRENTUSERNAME"><span>使用者</span></th>
				<th width="150" id="TH_SYS_LASTMODIFIED"><span>最后修改时间</span></th>
				<th width="50" id="TH_SYS_AUTHORS"><span>作者</span></th>
				<th width="60" id="TH_SYS_DOCUMENTID"><span>ID</span></th>
			</tr>
		<xsl:for-each select="DocItem" >
			<tr>
				<xsl:attribute name="id"><xsl:value-of select="DocID" /></xsl:attribute>
				<xsl:attribute name="libid"><xsl:value-of select="DocLibID" /></xsl:attribute>
				<td><input type="checkbox" name="cb">
					<xsl:attribute name="value"><xsl:value-of select="DocID"/></xsl:attribute>
					</input>
				</td>
				<td><xsl:value-of select="DocOrder"/></td>
				<td>
					<span id="VALUE_ISLOCKED">
						<xsl:choose>
							<xsl:when test="SYS_ISLOCKED[. = '0']"></xsl:when>
							<xsl:when test="SYS_ISLOCKED[. = '1']">
									<img><xsl:attribute name="src">../images/FileLock.gif</xsl:attribute></img>
							</xsl:when>
						</xsl:choose>
					</span>
				</td>
				<td>
					<xsl:element name="span">
						<xsl:attribute name="id">VALUE_TOPIC</xsl:attribute>
						<xsl:attribute name="title"><xsl:value-of select="SYS_TOPIC" /></xsl:attribute>
						<xsl:value-of select="SYS_TOPIC" />
					</xsl:element>
				</td>
				<td><xsl:value-of select="SYS_CURRENTSTATUS" /></td>
				<td><xsl:value-of select="SYS_CURRENTUSERNAME" /></td>
				<td><xsl:value-of select="SYS_LASTMODIFIED" /></td>
				<td><xsl:value-of select="SYS_AUTHORS" /></td>
				<td><xsl:value-of select="SYS_DOCUMENTID" /></td>
			</tr>
		</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
