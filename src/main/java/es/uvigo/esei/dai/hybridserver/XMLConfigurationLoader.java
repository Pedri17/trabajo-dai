/**
 *  HybridServer
 *  Copyright (C) 2023 Miguel Reboiro-Jato
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XMLConfigurationLoader {

  public static Configuration load(Reader reader) throws Exception {
    
    XMLConfigurationContentHandler handler = new XMLConfigurationContentHandler();
    try{
      parseAndValidateWithExternalXSD(reader, "./configuration.xsd", handler);
      System.out.println("File succesfully validated.");
    }catch(Exception e){
      System.err.println("An error occurred while reading the configuration file.");
      throw e;
    }
    return handler.getConfiguration();
  }

  public static void parseAndValidateWithExternalXSD(Reader reader, String schemaPath, ContentHandler handler
  ) throws ParserConfigurationException, SAXException, IOException {
    // Schema construction
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = schemaFactory.newSchema(new File(schemaPath));
    // Document parser construction
    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
    parserFactory.setValidating(false);
    parserFactory.setNamespaceAware(true);
    parserFactory.setSchema(schema);

    SAXParser parser = parserFactory.newSAXParser();
    XMLReader xmlReader = parser.getXMLReader();
    xmlReader.setContentHandler(handler);
    xmlReader.setErrorHandler(new SimpleErrorHandler());
    // Parsing
    xmlReader.parse(new InputSource(reader));
  }

  public static void parseAndValidateWithExternalXSD(String xmlContent, String xsdContent, ContentHandler handler
  ) throws ParserConfigurationException, SAXException, IOException {
    // Schema construction
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = schemaFactory.newSchema(new StreamSource(new StringReader(xsdContent)));
    // Document parser construction
    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
    parserFactory.setValidating(false);
    parserFactory.setNamespaceAware(true);
    parserFactory.setSchema(schema);

    SAXParser parser = parserFactory.newSAXParser();
    XMLReader xmlReader = parser.getXMLReader();
    xmlReader.setContentHandler(handler);
    xmlReader.setErrorHandler(new SimpleErrorHandler());
    // Parsing
    xmlReader.parse(new InputSource(new StringReader(xmlContent)));
  }
  
  public static String transformXMLwithXSLT(String xmlContent, String xsltContent) throws TransformerException{
    TransformerFactory tFactory = TransformerFactory.newInstance();
    ByteArrayInputStream arrayXslt = new ByteArrayInputStream(xsltContent.getBytes());
    ByteArrayInputStream streamXml = new ByteArrayInputStream(xmlContent.getBytes());
    Transformer transformer = tFactory.newTransformer(new StreamSource(arrayXslt));
    String result = "";

    try (StringWriter writer = new StringWriter()) {
      transformer.transform(
        new StreamSource(streamXml),
        new StreamResult(writer)
      );
      result = writer.toString();
    }catch(IOException e){
      e.printStackTrace();
    }

    return result;
  }

}


