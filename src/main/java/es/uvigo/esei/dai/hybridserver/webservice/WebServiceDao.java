package es.uvigo.esei.dai.hybridserver.webservice;

import java.util.List;

import es.uvigo.esei.dai.hybridserver.FileType;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public interface WebServiceDao {
    @WebMethod
    public List<String> list(FileType type) throws Exception;
    @WebMethod
    public String get(FileType type, String uuid) throws Exception;
    @WebMethod
    public boolean contains(FileType type, String uuid) throws Exception;
    @WebMethod
    public boolean containsAssociatedXSD(String xsltUuid) throws Exception;
    @WebMethod
    public String getAssociatedXSD(String uuidXSLT) throws Exception;
}
