package es.uvigo.esei.dai.hybridserver.webservice;

import java.util.List;

import es.uvigo.esei.dai.hybridserver.html.HTMLController;
import es.uvigo.esei.dai.hybridserver.xml.XMLController;
import es.uvigo.esei.dai.hybridserver.xsd.XSDController;
import es.uvigo.esei.dai.hybridserver.xslt.XSLTController;
import jakarta.jws.WebService;

@WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.webservice.WebServiceDao")
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
    public String getAssociatedXSD(String uuidXSLT) {
        return xsltController.getXsd(uuidXSLT);
    }

    @Override
    public String getHTML(String uuid) {
        return htmlController.getContent(uuid);
    }

    @Override
    public String getXML(String uuid) {
        return xmlController.getContent(uuid);
    }

    @Override
    public String getXSD(String uuid) {
        return xsdController.getContent(uuid);
    }

    @Override
    public String getXSLT(String uuid) {
        return xsltController.getContent(uuid);
    }

    @Override
    public List<String> listHTML() {
        return htmlController.getData().list();
    }

    @Override
    public List<String> listXML() {
        return xmlController.getData().list();
    }

    @Override
    public List<String> listXSD() {
        return xsdController.getData().list();
    }

    @Override
    public List<String> listXSLT() {
        return xsltController.getData().list();
    }
    
}
