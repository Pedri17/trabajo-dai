package es.uvigo.esei.dai.hybridserver.webservice;

import java.util.List;

import es.uvigo.esei.dai.hybridserver.FileType;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public interface WebServiceDao {
    @WebMethod
    public List<String> list(FileType type);
    @WebMethod
    public String get(FileType type, String uuid);
    @WebMethod
    public boolean contains(FileType type, String uuid);
    @WebMethod
    public boolean containsAssociatedXSD(String xsltUuid);
    
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
