package es.uvigo.esei.dai.hybridserver;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

public class XMLConfigurationContentHandler extends DefaultHandler {

    private Configuration config;
    private List<ServerConfiguration> servers;

    private String qualifiedName;
    
    private static final String HTTP = "http", WEB_SERVICE = "webservice", NUM_CLIENTS = "numClients";
    private static final String USER = "user", PASSWORD = "password", URL = "url";
    private static final String SERVERS = "servers", SERVER = "server", NAME = "name", WSDL = "wsdl", NAME_SPACE = "namespace", SERVICE = "service", HTTP_ADDRESS = "httpAddress";

    public void startDocument() {
        config = new Configuration();
    }

    public Configuration getConfiguration(){
        return config;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        qualifiedName = qName;
        switch(qName){
            case SERVERS:
                servers = new LinkedList<ServerConfiguration>();
                break;
            case SERVER :
                ServerConfiguration sConfig = new ServerConfiguration(
                    attributes.getValue(NAME),
                    attributes.getValue(WSDL),
                    attributes.getValue(NAME_SPACE),
                    attributes.getValue(SERVICE),
                    attributes.getValue(HTTP_ADDRESS)
                );
                servers.add(sConfig);
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(length > 0){
            var content = String.valueOf(ch, start, length);
            if(!content.isBlank()){
                switch(qualifiedName){
                    case HTTP:
                        config.setHttpPort(Integer.parseInt(content));
                        break;
                    case WEB_SERVICE:
                        config.setWebServiceURL(content);
                        break;
                    case NUM_CLIENTS:
                        config.setNumClients(Integer.parseInt(content));
                        break;
                    case USER:
                        config.setDbUser(content);
                        break;
                    case PASSWORD:
                        config.setDbPassword(content);
                        break;
                    case URL:
                        config.setDbURL(content);
                        break;
                }
            }
        }
    }

    @Override
    public void endDocument() throws SAXException{
        config.setServers(servers);
    }
    
}
