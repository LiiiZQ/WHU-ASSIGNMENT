<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<results>
			<xsl:apply-templates select="//book"/>
		</results>
	</xsl:template>
	<xsl:template match="book">
		<xsl:for-each select="author">
			<result>
				<xsl:copy-of select="../title"/>
				<xsl:copy-of select="."/>
			</result>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
