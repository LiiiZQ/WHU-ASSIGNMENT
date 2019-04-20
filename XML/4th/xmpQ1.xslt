<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<bib>
			<xsl:apply-templates select="//book"/>
		</bib>
	</xsl:template>
	<xsl:template match="book">
		<xsl:if test="./@year gt '1991' and ./publisher eq 'Addison-Wesley'">
			<xsl:copy>
				<xsl:copy-of select="./@*"/>
				<xsl:copy-of select="./title"/>
			</xsl:copy>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
