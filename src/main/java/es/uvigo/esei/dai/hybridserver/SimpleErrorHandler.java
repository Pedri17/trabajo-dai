package es.uvigo.esei.dai.hybridserver;


import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SimpleErrorHandler implements ErrorHandler {
	@Override
	public void error(SAXParseException exception) throws SAXException {
		throw exception;
	}

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        System.out.println("===SAX WARNING===");
        exception.printStackTrace();
        System.out.println("=================");
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        throw exception;
    }
}
