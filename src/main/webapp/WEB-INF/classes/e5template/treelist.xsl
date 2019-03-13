<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:template match="/">
<HTML>
<head>
	<link rel="stylesheet" type="text/css" href="../e5style/style.css"/>
	<script type="text/javascript" src="../e5workspace/DocList.js" charset="UTF-8"></script>
	<script type="text/javascript" src="../e5workspace/Menu.js" charset="UTF-8"></script>
</head>
<BODY RefCount="0" onselectstart="doUnSelect()" ondragstart="return false;">
<span id="listing" oncontextmenu="return false">
	<xsl:apply-templates select="DocList"/>
</span>
</BODY>
</HTML>
</xsl:template>
<xsl:template match="DocList">
<TABLE id="TABLE_DOCLIST1" cellSpacing="0" cellPadding="0" width="100%" border="1" Style="table-layout:fixed;">
<TBODY>
<TR borderColor="#ffffff" bgColor="#CBDAF7">
<TD Style="width:30px;"><SPAN id="TEXT_4" onclick="sort('DocOrder')"></SPAN></TD>
<!--header-->
</TR>
<xsl:variable name="pagebegin" select="0"/>
<xsl:variable name="pageend" select = "10"/>
<xsl:for-each select="DocItem" >
<xsl:sort select="aaa" order="ascending"/>
<xsl:if test = "position() &gt; $pagebegin and position() &lt; $pageend">
<TR borderColor="#ffffff" bgColor="#EAEEF1"
	onclick="selectDoc(this)" ondblclick="dClickDoc()" oncontextmenu="callMenu(this)">

	<xsl:attribute name="id"><xsl:value-of select="DocID" /></xsl:attribute>
	<xsl:attribute name="libid"><xsl:value-of select="DocLibID" /></xsl:attribute>
	<TD>
		<SPAN id="VALUE_DOCORDER"><xsl:value-of select="DocOrder" /></SPAN>
	</TD>
	<!--content-->
</TR>
</xsl:if>
</xsl:for-each>
</TBODY></TABLE>
</xsl:template>
</xsl:stylesheet>