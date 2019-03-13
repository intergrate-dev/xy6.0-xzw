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
				<th><span></span></th>
			</tr>
		<xsl:for-each select="DocItem">
		<tr>
			<xsl:attribute name="id"><xsl:value-of select="DocID"/></xsl:attribute>
			<xsl:attribute name="libid"><xsl:value-of select="DocLibID"/></xsl:attribute>
			<td>
				<div>
					<xsl:value-of select="wb_content"/>
					<div class="listAttachments">
						<xsl:attribute name="att"><xsl:value-of select="wb_attachments"/></xsl:attribute>
					</div>
				</div>
				<div class="clearfix">
					<xsl:choose>
						<xsl:when test="wb_status[. = '0']"><span style="color:red">未发布</span></xsl:when>
						<xsl:when test="wb_status[. = '1']"><span style="color:green">已发布</span></xsl:when>
						<xsl:when test="wb_status[. = '2']"><span style="color:#ff8800">定时发</span></xsl:when>
					</xsl:choose>　　
					<span class="listDate">
						<span><xsl:value-of select="SYS_AUTHORS"/></span>　
						<xsl:choose>
							<xsl:when test="wb_status[. = '0']"><xsl:value-of select="SYS_CREATED"/></xsl:when>
							<xsl:otherwise><xsl:value-of select="wb_pubTime"/></xsl:otherwise>
						</xsl:choose>　
					</span>　　
					<div class="listOps">
						<xsl:attribute name="docID"><xsl:value-of select="DocID"/></xsl:attribute>
						<xsl:if test="wb_status[. = '0']">
							<!--span class="listop opedit">修改</span-->
							<span class="listop oppublish">发布</span>
						</xsl:if>
					</div>　　
					<xsl:if test="wb_status[. = '1']">
						<span>　wid：<xsl:value-of select="wb_wid"/></span>　　　　
						<span class="viewRepostComment">
							<xsl:attribute name="docID"><xsl:value-of select="DocID"/></xsl:attribute>
							<span class="viewReposts" onclick="showReposts()" style="color:#00a0e6">转发<!--  <xsl:value-of select="wb_countRepost"/> --></span>　|　
							<span class="viewComments" onclick="showComments()" style="color:#00a0e6">评论 <!-- <xsl:value-of select="wb_countDiscuss"/> --></span>
						</span>
					</xsl:if>
				</div>
				<div class="listReposts"></div>
				<div class="listDiscusses"></div>
			</td>
		</tr>
		</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
