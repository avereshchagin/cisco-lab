<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:math="http://exslt.org/math">

    <xsl:template match="/">
        <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" version="1.1">
            <xsl:attribute name="width">
                <xsl:value-of select="(math:max(/topology/device/@x) + 1) * 100"/>
            </xsl:attribute>
            <xsl:attribute name="height">
                <xsl:value-of select="(math:max(/topology/device/@y) + 1) * 100"/>
            </xsl:attribute>
            <xsl:apply-templates select="topology/line"/>
            <xsl:apply-templates select="topology/device"/>
        </svg>
    </xsl:template>

    <xsl:template match="line">
        <xsl:variable name="from" select="@from"/>
        <xsl:variable name="to" select="@to"/>
        <line style="stroke: rgb(0,0,0); stroke-width: 2">
            <xsl:attribute name="x1">
                <xsl:value-of select="/topology/device[@name=$from]/@x * 100 + 50"/>
            </xsl:attribute>
            <xsl:attribute name="y1">
                <xsl:value-of select="/topology/device[@name=$from]/@y * 100 + 50"/>
            </xsl:attribute>
            <xsl:attribute name="x2">
                <xsl:value-of select="/topology/device[@name=$to]/@x * 100 + 50"/>
            </xsl:attribute>
            <xsl:attribute name="y2">
                <xsl:value-of select="/topology/device[@name=$to]/@y * 100 + 50"/>
            </xsl:attribute>
        </line>
    </xsl:template>

    <xsl:template match="device">
        <text text-anchor="middle" font-size="12pt">
            <xsl:attribute name="x">
                <xsl:value-of select="@x * 100 + 50"/>
            </xsl:attribute>
            <xsl:attribute name="y">
                <xsl:value-of select="@y * 100 + 15"/>
            </xsl:attribute>
            <xsl:value-of select="@name"/>
        </text>
        <image class="device" width="100" height="100">
            <xsl:attribute name="x">
                <xsl:value-of select="@x * 100"/>
            </xsl:attribute>
            <xsl:attribute name="y">
                <xsl:value-of select="@y * 100"/>
            </xsl:attribute>
            <xsl:attribute name="id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:attribute name="xlink:href">
                <xsl:choose>
                    <xsl:when test="@type = 'router'">
                        <xsl:value-of select="'images/router.svg'"/>
                    </xsl:when>
                    <xsl:when test="@type = 'switch'">
                        <xsl:value-of select="'images/switch.svg'"/>
                    </xsl:when>
                </xsl:choose>
            </xsl:attribute>
        </image>
    </xsl:template>

</xsl:stylesheet>