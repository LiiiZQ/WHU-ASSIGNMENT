<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<results>
			<xsl:for-each select="bib/book/author[not(.=following::author)]">
				<xsl:sort select="last"/>
				<xsl:sort select="first"/>
				<xsl:variable name="vlast" select="last"/>
				<xsl:variable name="vfirst" select="first"/>
				<result>
					<xsl:copy-of select="."/>
					<xsl:for-each select="../../book/author">
						<xsl:if test="last = $vlast and first = $vfirst">
							<xsl:copy-of select="../title"/>
						</xsl:if>
					</xsl:for-each>
				</result>
			</xsl:for-each>
		</results>
	</xsl:template>
</xsl:stylesheet>
