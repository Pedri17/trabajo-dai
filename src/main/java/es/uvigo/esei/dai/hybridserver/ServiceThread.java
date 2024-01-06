package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.UUID;

import org.xml.sax.SAXException;

import es.uvigo.esei.dai.hybridserver.html.HTMLController;
import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
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
	
	public ServiceThread(Socket socket, HTMLController htmlController, XMLController xmlController, 
						XSDController xsdController, XSLTController xsltController) {
		this.socket = socket;
		this.response = new HTTPResponse();
		this.response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());

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
									if (htmlController.contains(requestUuid)){
										// Known uuid
										String uuidContent = htmlController.getContent(requestUuid);
										
										response.setStatus(HTTPResponseStatus.S200);
										response.putParameter("Content-Type", "text/html");
										response.setContent(uuidContent);
									}else{
										// Unknown uuid
										response.setStatus(HTTPResponseStatus.S404);
										response.setContent("Not Found");
									}
								}else{
									// Request message has no a uuid field
									StringBuilder res = buildHTMLBase();
									
									// Add uuid list to HTML
									for(String uuid : htmlController.getData().list()){
										buildUuidList(res, "html", uuid, port);
									}

									// HTML close
									buildHTMLClose(res);

									response.setStatus(HTTPResponseStatus.S200);
									response.putParameter("Content-Type", "text/html");
									response.setContent(res.toString());
								}
							} else if (request.getResourceChain().equals("/")) {
								// Wellcome page
								response.setContent(buildWelcomePage());		
								response.putParameter("Content-Type", "text/html");		
								response.setStatus(HTTPResponseStatus.S200);
							}else if(request.getResourceName().equals("xml")){
								String uuidXml = request.getResourceParameters().get("uuid");
								String uuidXslt = request.getResourceParameters().get("xslt");

								if(uuidXml != null){
									// Request message has a uuid field
									if(uuidXslt != null){
										// Request message has a xslt field
										if(xsltController.contains(uuidXslt)){
											// xslt found
											String uuidXsd = xsltController.getXsd(uuidXslt);

											if(xsdController.contains(uuidXsd)){ 
												// xsd found
												
												String xmlContent = xmlController.getContent(uuidXml);
												String xsdContent = xsdController.getContent(uuidXsd);
												String xsltContent = xsltController.getContent(uuidXslt);

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
													response.setStatus(HTTPResponseStatus.S400);
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
										if(xmlController.contains(uuidXml)){
											response.setStatus(HTTPResponseStatus.S200);
											response.putParameter("Content-Type", "application/xml");
											response.setContent(xmlController.getContent(uuidXml));
										}else{
											// Unknown uuid
											response.setStatus(HTTPResponseStatus.S404);
											response.setContent("Not Found");
										}
									}
								}else{
									// Request message has no a uuid field
									StringBuilder res = buildHTMLBase();
									
									// Add uuid list to HTML
									for(String uuid : xmlController.getData().list()){
										buildUuidList(res, "xml", uuid, port);
									}

									// HTML close
									buildHTMLClose(res);

									response.setStatus(HTTPResponseStatus.S200);
									response.putParameter("Content-Type", "text/html");
									response.setContent(res.toString());
								}
								
								
							}else if(request.getResourceName().equals("xslt")){

								String uuid = request.getResourceParameters().get("uuid");

								if(uuid != null){
									// Request message has a uuid field
									if(xsltController.contains(uuid)){
										response.setStatus(HTTPResponseStatus.S200);
										response.putParameter("Content-Type", "application/xml");
										response.setContent(xsltController.getContent(uuid));
									}else{
										response.setStatus(HTTPResponseStatus.S404);
										response.setContent("Not Found");
									}
								}else{
									// Request message has not a uuid field
									StringBuilder res = buildHTMLBase();

									// xslt list
									for(String thisUuid : xsltController.getData().list()){
										buildUuidList(res, "xslt", thisUuid, port);
									}

									// close HTML
									buildHTMLClose(res);

									response.setStatus(HTTPResponseStatus.S200);
									response.putParameter("Content-Type", "text/html");
									response.setContent(res.toString());
								}

							}else if(request.getResourceName().equals("xsd")){

								String uuid = request.getResourceParameters().get("uuid");
								if(uuid != null){
									if(xsdController.contains(uuid)){
										response.setStatus(HTTPResponseStatus.S200);
										response.putParameter("Content-Type", "application/xml");
										response.setContent(xsdController.getContent(uuid));
									}else{
										response.setStatus(HTTPResponseStatus.S404);
										response.setContent("Not Found");
									}
								}else{
									StringBuilder res = buildHTMLBase();

									// xsd list
									for(String thisUuid : xsdController.getData().list()){
										buildUuidList(res, "xsd", thisUuid, port);
									}

									// close HTML
									buildHTMLClose(res);

									response.setStatus(HTTPResponseStatus.S200);
									response.putParameter("Content-Type", "text/html");
									response.setContent(res.toString());
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

	public void buildUuidList(StringBuilder base, String type, String uuid, int port){
		base.append("<li><a href=\"http://localhost:").append(port)
					.append("/").append(type).append("?uuid=").append(uuid).append("\">").append(uuid).append("</a></li>");
	}

	public void buildUuidEntry(StringBuilder base, String type, String uuid){
		base.append("<li> <a href=\"").append(type).append("?uuid=").append(uuid).append("\">").append(uuid).append("</a> </li>");
	}

	public void buildHTMLClose(StringBuilder base){
		base.append("</ol>")
			.append("</body>")
		.append("</html>");
	}
}