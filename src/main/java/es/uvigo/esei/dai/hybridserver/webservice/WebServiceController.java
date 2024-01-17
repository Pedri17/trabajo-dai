package es.uvigo.esei.dai.hybridserver.webservice;

import java.util.List;

import es.uvigo.esei.dai.hybridserver.ControllerInterface;
import es.uvigo.esei.dai.hybridserver.FileType;
import es.uvigo.esei.dai.hybridserver.html.HTMLController;
import es.uvigo.esei.dai.hybridserver.xml.XMLController;
import es.uvigo.esei.dai.hybridserver.xsd.XSDController;
import es.uvigo.esei.dai.hybridserver.xslt.XSLTController;
import jakarta.jws.WebService;

@WebService(
    endpointInterface = "es.uvigo.esei.dai.hybridserver.webservice.WebServiceDao",
    serviceName = "HybridServerService",
    targetNamespace = "http://hybridserver.dai.esei.uvigo.es/"
)
public class WebServiceController implements WebServiceDao {

    private HTMLController htmlController;
  	private XMLController xmlController;
  	private XSDController xsdController;
  	private XSLTController xsltController;

    public WebServiceController(HTMLController htmlController, XMLController xmlController, XSDController xsdController, XSLTController xsltController){
        this.htmlController = htmlController;
        this.xmlController = xmlController;
        this.xsdController = xsdController;
        this.xsltController = xsltController;
    }

    @Override
    public String get(FileType type, String uuid) throws Exception {
        ControllerInterface controller = htmlController;
        switch(type){
            case HTML:
                controller = htmlController;
                break;
            case XML:
                controller = xmlController;
                break;
            case XSLT:
                controller = xsltController;
                break;
            case XSD:
                controller = xsdController;
                break;
        }
        try{
            return controller.getContent(uuid);
        }catch(Exception e){
            throw e;
        }
    }

    @Override
    public boolean contains(FileType type, String uuid) throws Exception{
        try {
            return get(type, uuid) != null;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public boolean containsAssociatedXSD(String uuid) throws Exception {
        return getAssociatedXSD(uuid) != null;
    }

    @Override
    public List<String> list(FileType type) throws Exception {
        ControllerInterface controller = htmlController;
        switch(type){
            case HTML:
                controller = htmlController;
                break;
            case XML:
                controller = xmlController;
                break;
            case XSLT:
                controller = xsltController;
                break;
            case XSD:
                controller = xsdController;
                break;
        }
        try{
            return controller.list();
        }catch(Exception e){
            throw e;
        }
        
    }
    
    @Override
    public String getAssociatedXSD(String uuidXSLT) throws Exception {
        return xsltController.getXsd(uuidXSLT);
    }
    
}
