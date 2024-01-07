package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.SAXException;

import es.uvigo.esei.dai.hybridserver.html.HTMLController;
import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.webservice.WebServiceConnect;
import es.uvigo.esei.dai.hybridserver.webservice.WebServiceDao;
import es.uvigo.esei.dai.hybridserver.xml.XMLController;
import es.uvigo.esei.dai.hybridserver.xsd.XSDController;
import es.uvigo.esei.dai.hybridserver.xslt.XSLTController;

public class ServiceThread implements Runnable {
	private Socket socket;
	private HTTPRequest request;
	private HTTPResponse response;

	private HTMLController htmlController;
  	private XMLController xmlController;
  	private XSDController xsdController;
  	private XSLTController xsltController;

	private List<ServerConfiguration> servers;
	
	public ServiceThread(Socket socket, HTMLController htmlController, XMLController xmlController, 
						XSDController xsdController, XSLTController xsltController, List<ServerConfiguration> servers) {
		this.socket = socket;
		this.response = new HTTPResponse();
		this.response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
					
		this.servers = servers;

		this.htmlController = htmlController;
		this.xmlController = xmlController;
		this.xsdController = xsdController;
		this.xsltController = xsltController;
	}
	
	public void run() {
		try(Socket socket = this.socket){
			try(BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
				try {
					this.request = new HTTPRequest(br);
					int port = socket.getLocalPort();

					System.out.println("-----REQUEST-------");
					System.out.println(request.toString());
					System.out.println("-------------------");

					switch(request.getMethod()) {
						case GET:

							if (request.getResourceName().equals("html")){
								String requestUuid = request.getResourceParameters().get("uuid");
								// Request message has a uuid field
								if (requestUuid != null){
									String htmlContent = getContentIfExists(requestUuid, FileType.HTML);
									
									if (htmlContent != null){
										// Known uuid
										response.setStatus(HTTPResponseStatus.S200);
										response.putParameter("Content-Type", "text/html");
										response.setContent(htmlContent);
									}else{
										// Unknown uuid
										response.setStatus(HTTPResponseStatus.S404);
										response.setContent("Not Found");
									}
								}else{
									response.setStatus(HTTPResponseStatus.S200);
									response.putParameter("Content-Type", "text/html");
									response.setContent(buildUuidListHTML(FileType.HTML, port).toString());
								}
							} else if (request.getResourceChain().equals("/")) {
								// Wellcome page
								response.setContent(buildWelcomePage());		
								response.putParameter("Content-Type", "text/html");		
								response.setStatus(HTTPResponseStatus.S200);
							}else if(request.getResourceName().equals("xml")){
								String uuidXml = request.getResourceParameters().get("uuid");
								String uuidXslt = request.getResourceParameters().get("xslt");
								String xmlContent = getContentIfExists(uuidXml, FileType.XML);

								if(uuidXml != null){
									// Request message has a uuid field
									if(xmlContent != null){
										// xml content exists
										if(uuidXslt != null){
											// Request message has a xslt field
											String xsltContent = getContentIfExists(uuidXslt, FileType.XSLT);

											if(xsltContent != null){
												// xslt found
												String uuidXsd = getAssociatedXSDfromXSLT(uuidXslt);
												String xsdContent = getContentIfExists(uuidXsd, FileType.XSD);

												if(xsdContent != null){ 
													// xsd found
													try{
														XMLConfigurationLoader.parseAndValidateWithExternalXSD(
															xmlContent,
															xsdContent, 
															null
														);
														response.putParameter("Content-Type", "text/html");
														response.setStatus(HTTPResponseStatus.S200);
														response.setContent(XMLConfigurationLoader.transformXMLwithXSLT(xmlContent, xsltContent));
													}catch(SAXException e){
														System.out.println("Validating error");
														response.setStatus(HTTPResponseStatus.S500);
														response.setContent("XML does not validate XSD");
													}
												}else{
													// xsd not found
													response.setStatus(HTTPResponseStatus.S404);
													response.setContent("Not Found");
												}
											}else{
												// xslt not found
												response.setStatus(HTTPResponseStatus.S404);
												response.setContent("Not Found");
											}
										}else{
											// There's not xslt, returns xml content
											response.setStatus(HTTPResponseStatus.S200);
											response.putParameter("Content-Type", "application/xml");
											response.setContent(xmlContent);
										}
									}else{
										// xml not found
										response.setStatus(HTTPResponseStatus.S404);
										response.setContent("Not Found");
									}
								}else{
									response.setStatus(HTTPResponseStatus.S200);
									response.putParameter("Content-Type", "text/html");
									response.setContent(buildUuidListHTML(FileType.XML, port).toString());
								}
								
								
							}else if(request.getResourceName().equals("xslt")){
								String uuid = request.getResourceParameters().get("uuid");

								if(uuid != null){
									// Request message has a uuid field
									String xsltContent = getContentIfExists(uuid, FileType.XSLT);

									if(xsltContent != null){
										response.setStatus(HTTPResponseStatus.S200);
										response.putParameter("Content-Type", "application/xml");
										response.setContent(xsltContent);
									}else{
										response.setStatus(HTTPResponseStatus.S404);
										response.setContent("Not Found");
									}
								}else{
									response.setStatus(HTTPResponseStatus.S200);
									response.putParameter("Content-Type", "text/html");
									System.out.println("BUILDEAR LISTA CON XSLT");
									response.setContent(buildUuidListHTML(FileType.XSLT, port).toString());
								}

							}else if(request.getResourceName().equals("xsd")){

								String uuid = request.getResourceParameters().get("uuid");
								if(uuid != null){
									String xsdContent = getContentIfExists(uuid, FileType.XSD);

									if(xsdContent != null){
										response.setStatus(HTTPResponseStatus.S200);
										response.putParameter("Content-Type", "application/xml");
										response.setContent(xsdContent);
									}else{
										response.setStatus(HTTPResponseStatus.S404);
										response.setContent("Not Found");
									}
								}else{
									response.setStatus(HTTPResponseStatus.S200);
									response.putParameter("Content-Type", "text/html");
									response.setContent(buildUuidListHTML(FileType.XSD, port).toString());
								}
								
							}else{
								response.setStatus(HTTPResponseStatus.S400);
								response.setContent("Bad Request");
							}
							
							break;
					
						case POST:
						
							if (request.getResourceParameters().containsKey("html")){
								
								String newUuid = htmlController.getData().create(request.getResourceParameters().get("html"));

								// HTML
								StringBuilder res = buildHTMLBase();
								buildUuidEntry(res, "html", newUuid);
								buildHTMLClose(res);

								response.setStatus(HTTPResponseStatus.S200);
								response.setContent(res.toString());

							}else if(request.getResourceParameters().containsKey("xml")){

								System.out.println(request.getResourceParameters().get("xml"));
								String newUuid = xmlController.getData().create(request.getResourceParameters().get("xml"));

								StringBuilder res = buildHTMLBase();
								buildUuidEntry(res, "xml", newUuid);
								buildHTMLClose(res);

								response.setStatus(HTTPResponseStatus.S200);
								response.setContent(res.toString());

							}else if(request.getResourceParameters().containsKey("xslt")){

								String xsdUuid = request.getResourceParameters().get("xsd");

								if(xsdUuid != null){
									if(xsdController.contains(xsdUuid)){
										String newUuid = xsltController.getData().create(request.getResourceParameters().get("xslt"), xsdUuid);

										StringBuilder res = buildHTMLBase();
										buildUuidEntry(res, "xslt", newUuid);
										buildHTMLClose(res);

										response.setStatus(HTTPResponseStatus.S200);
										response.setContent(res.toString());
									}else{
										response.setStatus(HTTPResponseStatus.S404);
										response.setContent("Not Found");
									}
								}else{
									response.setStatus(HTTPResponseStatus.S400);
									response.setContent("Bad Request");
								}
							}else if(request.getResourceParameters().containsKey("xsd")){

								String newUuid = xsdController.getData().create(request.getResourceParameters().get("xsd"));

								StringBuilder res = buildHTMLBase();
								buildUuidEntry(res, "xsd", newUuid);
								buildHTMLClose(res);

								response.setStatus(HTTPResponseStatus.S200);
								response.setContent(res.toString());

							}else{
								response.setStatus(HTTPResponseStatus.S400);
								response.setContent("Bad Request");
							}

							break;

						case DELETE:
						
							// UUID is on the data structure
							String uuid = request.getResourceParameters().get("uuid");

							if(htmlController.contains(uuid)){

								htmlController.getData().delete(uuid);
								response.setStatus(HTTPResponseStatus.S200);

							}else if(xmlController.contains(uuid)){

								xmlController.getData().delete(uuid);
								response.setStatus(HTTPResponseStatus.S200);

							}else if(xsltController.contains(uuid)){

								xsltController.getData().delete(uuid);
								response.setStatus(HTTPResponseStatus.S200);

							}else if(xsdController.contains(uuid)){

								xsdController.getData().delete(uuid);
								response.setStatus(HTTPResponseStatus.S200);

							}else{
								// UUID not found
								response.setStatus(HTTPResponseStatus.S404);
								response.setContent("Not Found");
							}

							break;

						default:
							response.setStatus(HTTPResponseStatus.S400);
							response.setContent("Method not implemented");
							break;
					}
					
				}catch(Exception e) {
					response.setStatus(HTTPResponseStatus.S500);
					response.setContent("Internal Server Error");
					e.printStackTrace();
				}finally{
					System.out.println("-----RESPONSE-------");
					System.out.println(response.toString());
					System.out.println("--------------------");
					try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))){
						response.print(writer);
					}catch(IOException e){
						System.err.println("No ha sido posible escribir el socket: "+e.getMessage());
						e.printStackTrace();
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}catch(IOException exc){
			exc.printStackTrace();
		}
		
	}

	// BUILD HTML

	public String buildWelcomePage(){
		StringBuilder aux = new StringBuilder();
									aux.append("<!DOCTYPE html>").append("<html lang=\"es\">").append("<head>")
										.append("  <meta charset=\"utf-8\"/>").append("  <title>Hybrid Server</title>")
										.append("</head>").append("<body>").append("<h1>Hybrid Server</h1>")
										.append("Autor: Pedro Jalda Fonseca.")
										.append("</body>").append("</html>");
		return aux.toString();
	}

	public StringBuilder buildHTMLBase(){
		StringBuilder res = new StringBuilder();
									
		// HTML base
		res.append("<!DOCTYPE html>")
			.append("<html lang=\"es\">")
				.append("<head>")
					.append("<meta charset=\"utf-8\"/>")
					.append("<title>Hybrid Server</title>")
				.append("</head>")
				.append("<body>")
					.append("<h1>UUID List Hybrid Server</h1>")
					.append("<ol>");

		return res;
	}

	public void buildUuidListEntry(StringBuilder base, FileType type, String uuid, int port){
		base.append("<li><a href=\"http://localhost:").append(port)
					.append("/").append(type.getType()).append("?uuid=").append(uuid).append("\">").append(uuid).append("</a></li>");
	}

	public void buildUuidEntry(StringBuilder base, String type, String uuid){
		base.append("<li> <a href=\"").append(type).append("?uuid=").append(uuid).append("\">").append(uuid).append("</a> </li>");
	}

	public void buildHTMLClose(StringBuilder base){
		base.append("</ol>")
			.append("</body>")
		.append("</html>");
	}

	public void buildExternalUuids(StringBuilder res, FileType type, int port){
		// Get external uuids
		for(WebServiceDao ws: WebServiceConnect.connect(servers)){
			for(String thisUuid: ws.list(type)){
				buildUuidListEntry(res, type, thisUuid, port);
			}
		}
	}

	public StringBuilder buildUuidListHTML(FileType type, int port){
		// Request message has no a uuid field
		StringBuilder res = buildHTMLBase();
		List<String> uuidList = new LinkedList<>();;

		switch(type){
			case HTML:
				uuidList = htmlController.list();
				break;
			case XML:
				uuidList = xmlController.list();
				break;
			case XSD:
				uuidList = xsdController.list();
				break;
			case XSLT:
				uuidList = xsltController.list();
				break;
		}
		
		// Add uuid list to HTML
		for(String uuid : uuidList){
			buildUuidListEntry(res, type, uuid, port);
		}

		buildExternalUuids(res, type, port);

		// HTML close
		buildHTMLClose(res);

		return res;
	}

	// EXTERNAL SERVERS

	public String getContentIfExists(String uuid, FileType type){
		ControllerInterface controller = null;
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

		String content = controller.getContent(uuid);
		Iterator<WebServiceDao> iWebservices = (WebServiceConnect.connect(servers)).iterator();
		while(content == null && iWebservices.hasNext()){
			content = iWebservices.next().get(type, uuid);
		}
		return content;
	}

	public String getAssociatedXSDfromXSLT(String uuid){
		String uuidXsd = xsltController.getXsd(uuid);
		Iterator<WebServiceDao> iWebservices = (WebServiceConnect.connect(servers)).iterator();
		while(uuidXsd == null && iWebservices.hasNext()){
			uuidXsd = iWebservices.next().getAssociatedXSD(uuid);
		}
		return uuidXsd;
	}
	
}