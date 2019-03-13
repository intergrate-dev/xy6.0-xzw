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
				<th id="TH_a_content"><span>问题</span></th>
				<th id="TH_a_answer"><span>回答</span></th>
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
					<xsl:if test="a_order[. > '100000000000000000']"><span title="置顶话题"><font color="green">【顶】</font></span></xsl:if>
					<xsl:if test="a_good[. = '1']"><span title="加精话题"><font color="red">【精】</font></span></xsl:if>
					（<span style="color:green;"><xsl:value-of select="a_group"/></span>）<b><xsl:value-of select="SYS_TOPIC"/></b>
					<br/>
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
						<xsl:value-of select="a_realName"/>（<xsl:value-of select="a_region"/>）　
						<xsl:if test="a_status[. = '0']"><font color="blue">待审核</font>　</xsl:if>
						<xsl:if test="a_status[. = '1']"><font color="green">已通过</font>　</xsl:if>
						<xsl:if test="a_status[. = '2']"><font color="red">不通过</font>　</xsl:if>
						id：<xsl:value-of select="DocID"/>
					</span>
				</td>
				<td style="vertical-align:top;">
					<span style="color:green;"><xsl:value-of select="a_answer"/></span><br/>
					<span class="listDate">
						<xsl:value-of select="a_answerTime"/>　
						<xsl:value-of select="a_answerer"/>
					</span>
				</td>
			</tr>
		</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
