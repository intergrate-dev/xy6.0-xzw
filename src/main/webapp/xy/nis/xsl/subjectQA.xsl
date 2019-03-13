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
				<th width="30" id="TH_DocOrder"></th>
				<th width="30" id="TH_Checkbox" class="center"><input type="checkbox" name="cbAll" id="cbAll" title="全选/全不选" /></th>
				<th id="TH_a_content"><span>问答</span></th>
			</tr>
		<xsl:for-each select="DocItem">
			<tr>
				<xsl:attribute name="id"><xsl:value-of select="DocID"/></xsl:attribute>
				<xsl:attribute name="libid"><xsl:value-of select="DocLibID"/></xsl:attribute>
				<td class="list-order" style="width: 30px;vertical-align:top;"><xsl:value-of select="DocOrder"/></td>
				<td class="center" style="vertical-align:top;">
					<input type="checkbox" name="cb">
					<xsl:attribute name="value"><xsl:value-of select="DocID"/></xsl:attribute>
					</input>
				</td>
				<td>
					<span style="color:black;"><xsl:value-of select="a_content"/></span><br/>
					<span class="listDate">
						<xsl:value-of select="SYS_CREATED"/>　
						<xsl:value-of select="SYS_AUTHORS"/>　
						<xsl:if test="a_status[. = '2']"><font color="red">未通过审核</font></xsl:if>
						<xsl:if test="a_status[. = '1']"><font color="green">已审</font></xsl:if>
						<xsl:if test="a_status[. = '0']">待审</xsl:if>
					</span>
					<xsl:if test="a_answerTime[. != '']">
						<br/>
						<span style="color:green;"><xsl:value-of select="a_answer"/></span><br/>
						<span class="listDate">
							<xsl:value-of select="a_answerTime"/>　
							<xsl:value-of select="a_answerer"/>　
						</span>
					</xsl:if>
				</td>
			</tr>
		</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
