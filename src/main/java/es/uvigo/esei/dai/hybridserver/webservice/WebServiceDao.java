package es.uvigo.esei.dai.hybridserver.webservice;

import java.util.List;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public interface WebServiceDao {
    @WebMethod
    public List<String> listHTML();
    @WebMethod
    public List<String> listXML();
    @WebMethod
    public List<String> listXSD();
    @WebMethod
    public List<String> listXSLT();
    @WebMethod
    public String getHTML(String uuid);
    @WebMethod
    public String getXML(String uuid);
    @WebMethod
    public String getXSD(String uuid);
    @WebMethod
    public String getXSLT(String uuid);
    @WebMethod
    public String getAssociatedXSD(String uuidXSLT);
}
