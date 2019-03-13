<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" omit-xml-declaration="yes" encoding="utf-8"/> 
	<xsl:template match="/">
		<style>
			.album_table td{padding:0}
			#listing ul{list-style:none;margin:0;padding:0;}
			.mainli{display:block; height:150px;overflow:hidden;position:relative;}
			<!--.mainli img{max-width:100%;margin-top: -25%;margin-left: -25%;}-->
			.mainli img{width:100%; height:100%;}
			.mainli span{display:block;background-color:rgba(0,0,0,0.5);color:#fff;padding: 5px 0;width:100%;
				position:absolute;bottom:0;display: -webkit-box;overflow: hidden;text-overflow: ellipsis;
				word-break: break-all; -webkit-box-orient: vertical; -webkit-line-clamp: 2;<!--text-align: justify;-->
			}
			
			.listli{padding:2px;height:60px;border-bottom: 1px solid #ddd;}
			.listli span{float:left;width:70%;
				display: -webkit-box;
				overflow: hidden;
				text-overflow: ellipsis;
				word-break: break-all;
				-webkit-box-orient: vertical;
				-webkit-line-clamp: 3;
				margin-top: 10px;
			}
			.listli img{max-width:25%;max-height:50px;float:right;margin:5px;}
			
			.opli{background-color:#eee;font-size:15px;height: 40px;line-height: 40px;}
			.opli span{display:inline-block; width:49.8%;text-align:center;cursor: pointer;}
			.opli span:first-child{border-right:1px solid #ddd;}
			
			.dateli{margin:5px;}
			.dateli .notpub{margin-left:70px;color:red;}
			.dateli .pub{margin-left:70px;color:green;}
		</style>
		<span id="listing" album="true" oncontextmenu="return false">
			<xsl:apply-templates select="DocList"/>
		</span>
	</xsl:template>
	<xsl:template match="DocList">
		<xsl:attribute name="totalCount"><xsl:value-of select="TotalSum" /></xsl:attribute>
		<xsl:for-each select="DocItem">
		<table class="album_table" style="width:26%;cursor:pointer;margin:10px 20px;">
			<xsl:attribute name="id"><xsl:value-of select="DocID"/></xsl:attribute>
			<xsl:attribute name="libid"><xsl:value-of select="DocLibID"/></xsl:attribute>
			<tr><td><ul>
				<xsl:attribute name="docID"><xsl:value-of select="DocID"/></xsl:attribute>
				<xsl:attribute name="flowNodeID"><xsl:value-of select="SYS_CURRENTNODE"/></xsl:attribute>
				<xsl:attribute name="members"><xsl:value-of select="wxg_members"/></xsl:attribute>
				<li class="dateli">
					<xsl:value-of select="substring(SYS_CREATED,6,14)"/>
					<xsl:choose>
						<xsl:when test="wxg_status[. = '0']"><span class="pub">草稿</span></xsl:when>
						<xsl:when test="wxg_status[. = '1']"><span class="pub">待审核</span></xsl:when>
						<xsl:when test="wxg_status[. = '2']"><span class="pub">未发布</span></xsl:when>
						<xsl:when test="wxg_status[. = '3']"><span class="notpub">已发布</span></xsl:when>
						<xsl:when test="wxg_status[. = '4']"><span class="pub">已驳回</span></xsl:when>
					</xsl:choose>
					<span class="count"></span>
				</li>
			</ul></td></tr>
		</table>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
