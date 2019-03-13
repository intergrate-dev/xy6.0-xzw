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
				<th width="250" id="TH_SYS_TOPIC"><span>对应稿件/话题</span></th>
			</tr>
		<xsl:for-each select="DocItem" >
			<tr>
				<xsl:attribute name="id"><xsl:value-of select="DocID"/></xsl:attribute>
				<xsl:attribute name="libid"><xsl:value-of select="DocLibID"/></xsl:attribute>
				<td class="center" style="vertical-align:top;"><input type="checkbox" name="cb">
					<xsl:attribute name="value"><xsl:value-of select="DocID"/></xsl:attribute>
					</input>
				</td>
				<td class="center" style="vertical-align:top;">
					<xsl:attribute name="userID"><xsl:value-of select="SYS_AUTHORID"/></xsl:attribute>
					<xsl:attribute name="userName"><xsl:value-of select="SYS_AUTHORS"/></xsl:attribute>
					<xsl:attribute name="ip"><xsl:value-of select="a_ip"/></xsl:attribute>
					<div>
						<span><xsl:value-of select="SYS_AUTHORS"/></span>
						<xsl:if test="SYS_AUTHORID>0 or a_ip != ''">
							<span class="caret shutOps pull-right"></span>
						</xsl:if>
					</div>
					<span><xsl:value-of select="a_ip"/></span>
				</td>
				<td>
					<xsl:choose>
						<xsl:when test="a_isSensitive='1'"><span title="含敏感词"><img src="../Icons/sensitive.png"/></span></xsl:when>
						<xsl:when test="a_isSensitive='2'"><span title="含非法词"><img src="../Icons/illegal.png"/></span></xsl:when>
						<xsl:when test="a_isSensitive='3'"><span title="含敏感词与非法词"><img src="../Icons/senandill.png"/></span></xsl:when>
					</xsl:choose>
					<xsl:if test="a_isSensitive[. = '0']">
						<xsl:value-of select="a_content"/>
					</xsl:if>

					<xsl:if test="a_isSensitive>0 ">
						<xsl:choose>
							<xsl:when test="a_sensitiveContent != ''">
								<span class="sensitiveSpan">
									<xsl:value-of select="a_sensitiveContent"/>
								</span>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="a_content"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>


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
					<xsl:choose>
						<xsl:when test="a_isExposed = '1'">
							<span title="被举报"><font color="red">　　被举报</font></span>
						</xsl:when>
						<xsl:otherwise>
							<xsl:if test="a_status[. = '1']">
								<span title="已通过审核"><font color="green">　　已通过审核</font></span>
							</xsl:if>
							<xsl:if test="a_status[. = '2']">
								<span title="未通过审核"><font color="gray">　　未通过审核</font></span>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:if test="a_sourceType[. = '6']">
						<xsl:if test="SYS_ISKEEP[. = '1']">
							<span title="入选"><font color="red">　　入选</font></span>
						</xsl:if>
					</xsl:if>
				</td>
				<td style="vertical-align:top; padding-right:10px">
					<xsl:element name="a">
						<xsl:attribute name="title"><xsl:value-of select="SYS_TOPIC" /></xsl:attribute>
						<xsl:attribute name="href">nis/ViewArticle.do?id=<xsl:value-of select="a_articleID"/>&amp;type=<xsl:value-of select="a_sourceType"/></xsl:attribute>
						<xsl:attribute name="target">_blank</xsl:attribute>
						<xsl:value-of select="SYS_TOPIC" />
					</xsl:element>
				</td>
			</tr>
		</xsl:for-each>
		</table>
		<ul id="shutOpArea" class="dropdown-menu" style="display:none;">
			<li class="shutup">加入黑名单</li>
			<li class="shutip">IP 加入黑名单</li>
		</ul>
	</xsl:template>
</xsl:stylesheet>

