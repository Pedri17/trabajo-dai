<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:l="http://www.esei.uvigo.es/dai/hybridserver">

    <xsl:output method="html" indent="yes" encoding="utf-8"/>

    <xsl:template match="/">
        <xsl:text disable-output-escaping="yes">&lt;!DOCTYPE HTML&gt;</xsl:text>
        <html>
            <head>
                <meta charset="UTF-8"/>
                <title>HybridServer</title>
            </head>
            <body>
                <h1>Configuración</h1>
                <div id="configuration">
                    <xsl:apply-templates select="l:connections"/>
                    <xsl:apply-templates select="l:database"/>
                    <xsl:apply-templates select="l:servers"/>
                </div>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="l:connections">
        <h2>Conexión del Servidor</h2>
        <div id="connection">
            <li>
                <a>Puerto HTTP: </a><xsl:value-of select="l:http"/>
                <a>Número de clientes: </a><xsl:value-of select="l:webService"/>
                <a>Servicio web: </a><xsl:value-of select="l:numClients"/>
            </li>
        </div>
    </xsl:template>

    <xsl:template match="l:database">
        <h2>Base de datos</h2>
        <div id="database">
            <li>
                <a>Usuario: </a><xsl:value-of select="l:user"/>
                <a>Contraseña: </a><xsl:value-of select="l:password"/>
                <a>URL: </a><xsl:value-of select="l:url"/>
            </li>
        </div>
    </xsl:template>

    <xsl:template match="l:server">
        <h2><xsl:value-of select="@name"/></h2>
        <div id="server">
            <li>
                <a>Nombre: </a><xsl:value-of select="@name"/>
                <a>Wsdl: </a><xsl:value-of select="@wsdl"/>
                <a>Namespace: </a><xsl:value-of select="@namespace"/>
                <a>Servicio: </a><xsl:value-of select="@service"/>
                <a>Dirección HTTP: </a><xsl:value-of select="@httpAddress"/>
            </li>
        </div>
    </xsl:template>

    <xsl:template match="l:servers">
        <h2>Lista de servidores</h2>
        <div id="servers">
            <li>
                <xsl:apply-templates select="l:servers/l:server"/>
            </li>
        </div>
    </xsl:template>

</xsl:stylesheet>