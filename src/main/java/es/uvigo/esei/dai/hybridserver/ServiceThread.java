package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

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
									
									// Add uuid list to HTML
									for(String uuid : htmlController.getData().list()){
										res.append("<li><a href=\"http://localhost:").append(port)
											.append("/html?uuid=").append(uuid).append("\">").append(uuid).append("</a></li>");
									}

									// HTML close
									res.append("</ol>")
										.append("</body>")
									.append("</html>");

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
								// TODO creo que tengo que validar

								if(uuidXslt != null){
									// Search local xslt uuid
									if(xsltController.contains(uuidXslt)){

									}else{
										// Search remote xslt uuid
										// TODO: Literalmente no sé cómo se hace
									}
								}

								// !! Queda pendiente esta parte

							}else{
								response.setStatus(HTTPResponseStatus.S400);
								response.setContent("Bad Request");
							}
							
							break;
					
						case POST:
						
							if (request.getResourceParameters().containsKey("html")){
								String newUuid = htmlController.getData().create(request.getResourceParameters().get("html"));
								StringBuilder aux = new StringBuilder();

								// HTML base
								aux.append("<!DOCTYPE html>")
									.append("<html lang=\"es\">")
										.append("<head>")
											.append("<meta charset=\"utf-8\"/>")
											.append("<title>Hybrid Server</title>")
										.append("</head>")
										.append("<body>")
											.append("<h1>Hybrid Server</h1>")
											.append("<ol>");

								// HTML content
								aux.append("<li> <a href=\"html?uuid=").append(newUuid).append("\">").append(newUuid).append("</a> </li>");

								// HTML close
								aux.append("</ol>")
									.append("</body>")
										.append("</html>");

								response.setStatus(HTTPResponseStatus.S200);
								response.setContent(aux.toString());
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
}