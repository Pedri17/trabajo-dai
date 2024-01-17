package es.uvigo.esei.dai.hybridserver.webservice;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import java.net.URL;
import es.uvigo.esei.dai.hybridserver.ServerConfiguration;
import jakarta.xml.ws.Service;

public class WebServiceConnect {

    public static List<WebServiceDao> connect(List<ServerConfiguration> servers){
        List<WebServiceDao> wsDaos = new LinkedList<>();
        for(ServerConfiguration sConfig: servers){
            URL url = null;
            WebServiceDao wsDao = null;
            try{
                System.out.println("Trying to connect to "+sConfig.getName());
                url = new URL(sConfig.getWsdl());
                QName qName = new QName(sConfig.getNamespace(), sConfig.getService());
                Service service = Service.create(url, qName);
                wsDao = service.getPort(WebServiceDao.class);
                wsDaos.add(wsDao);
            }catch(Exception e){
                System.err.println("Could not connect to that web service.");
            }
        }
        return wsDaos;
    }
    
}