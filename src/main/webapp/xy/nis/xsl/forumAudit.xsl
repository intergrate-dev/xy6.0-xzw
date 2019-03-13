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
				<th width="150" id="TH_SYS_AUTHORS" class="center"><span>用户</span></th>
				<th id="TH_a_content"><span>内容</span></th>
				<th width="250" id="TH_SYS_TOPIC"><span>所属主贴</span></th>
			</tr>
		<xsl:for-each select="DocItem" >
			<tr>
				<xsl:attribute name="id"><xsl:value-of select="DocID"/></xsl:attribute>
				<xsl:attribute name="libid"><xsl:value-of select="DocLibID"/></xsl:attribute>
				<td class="center" style="vertical-align:top;">
					<input type="checkbox" name="cb">
					<xsl:attribute name="value"><xsl:value-of select="DocID"/></xsl:attribute>
					</input>
				</td>
				<td class="center" style="vertical-align:top;">
					<xsl:attribute name="userID"><xsl:value-of select="SYS_AUTHORID"/></xsl:attribute>
					<xsl:attribute name="userName"><xsl:value-of select="SYS_AUTHORS"/></xsl:attribute>
					
					<xsl:value-of select="SYS_AUTHORS"/><br/>
					<xsl:if test="a_status[. = '0'] and SYS_AUTHORID[. > '0']">
						<xsl:choose>
							<xsl:when test="a_shutup[. = '0']"><span class="listop shutup">禁言</span></xsl:when>
							<xsl:otherwise><span class="listop shutcancel">取消禁言</span></xsl:otherwise>
						</xsl:choose>
					</xsl:if>
				</td>
				<td>
					<xsl:if test="a_shutup[. = '1']"><span title="被禁言用户发的帖"><font color="red"><b>【禁言用户】</b></font></span></xsl:if>
					<xsl:if test="a_order[. > '100000000000000000']"><span title="置顶帖"><font color="green">【顶】</font></span></xsl:if>
					<xsl:if test="a_good[. = '1']"><span title="精华帖"><font color="red">【精】</font></span></xsl:if>
					
					<xsl:if test="a_parentID[. = '0']"><span title="主帖"><font color="black">【主帖】</font></span></xsl:if>
					<xsl:if test="a_parentID[. = '0']">
						<a target="_blank">
							<xsl:attribute name="title"><xsl:value-of select="SYS_TOPIC" /></xsl:attribute>
							<xsl:attribute name="href">nis/forumPage.jsp?id=<xsl:value-of select="DocID"/></xsl:attribute>
							<b><xsl:value-of select="SYS_TOPIC"/></b>
						</a>
						<br/>
					</xsl:if>
					<xsl:value-of select="a_content"/>
					<div class="listAttachments">
						<xsl:attribute name="att"><xsl:value-of select="a_attachments"/></xsl:attribute>
					</div>
					<div>
						<xsl:attribute name="docID"><xsl:value-of select="DocID"/></xsl:attribute>
						<xsl:attribute name="flowNodeID"><xsl:value-of select="SYS_CURRENTNODE"/></xsl:attribute>
						<xsl:attribute name="class">listOps</xsl:attribute>
					</div>
					<span class="listDate">
						<xsl:value-of select="SYS_CREATED"/>　　
						id：<xsl:value-of select="DocID"/>
					</span>
					<xsl:if test="a_status[. = '1']">
						<span title="已通过审核"><font color="green">　　已通过审核</font></span>
					</xsl:if>
					<xsl:if test="a_status[. = '2']">
						<span title="未通过审核"><font color="red">　　未通过审核</font></span>
					</xsl:if>
				</td>
				<td style="vertical-align:top;padding-right:10px">
					<xsl:if test="a_parentID[. > '0']">
					<xsl:element name="span">
						<xsl:attribute name="title"><xsl:value-of select="SYS_TOPIC" /></xsl:attribute>
							<a target="_blank">
								<xsl:attribute name="title"><xsl:value-of select="SYS_TOPIC" /></xsl:attribute>
								<xsl:attribute name="href">nis/forumPage.jsp?id=<xsl:value-of select="a_rootID"/></xsl:attribute>					
								<b><xsl:value-of select="SYS_TOPIC"/></b>
							</a>
							<br/>
					</xsl:element>
					</xsl:if>
				</td>
			</tr>
		</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
