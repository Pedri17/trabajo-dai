package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {
	private Socket socket;
	private HTMLController controller;
	private HTTPRequest request;
	private HTTPResponse response;
	
	public ServiceThread(Socket socket, HTMLController controller) {
		this.socket = socket;
		this.controller = controller;
		this.response = new HTTPResponse();
		this.response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
	}
	
	public void run() {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
			try{
				try {
					this.request = new HTTPRequest(br);
					System.out.println("-----REQUEST-------");
					System.out.println(request.toString());
					System.out.println("-------------------");

					switch(request.getMethod()) {
						case GET:

							if (request.getResourceName().equals("html")){
								String requestUuid = request.getResourceParameters().get("uuid");
								// Request message has a uuid field
								if (requestUuid != null){
									if (controller.contains(requestUuid)){
										// Known uuid
										String uuidContent = controller.getContent(requestUuid);
										
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
									for(String uuid : controller.getData().list()){
										res.append(uuid);
									}

									response.setStatus(HTTPResponseStatus.S200);
									response.putParameter("Content-Type", "text/html");
									response.setContent(res.toString());
								}
							} else if (request.getResourceChain().equals("/")) {
								// Wellcome page
								StringBuilder aux = new StringBuilder();
								aux.append("<!DOCTYPE html>").append("<html lang=\"es\">").append("<head>")
									.append("  <meta charset=\"utf-8\"/>").append("  <title>Hybrid Server</title>")
									.append("</head>").append("<body>").append("<h1>Hybrid Server</h1>")
									.append("Autor: Pedro Jalda Fonseca.")
									.append("</body>").append("</html>");
													
								response.setStatus(HTTPResponseStatus.S200);
								response.putParameter("Content-Type", "text/html");
								response.setContent(aux.toString());
							}else{
								response.setStatus(HTTPResponseStatus.S400);
								response.setContent("Bad Request");
							}
							
							break;
					
						case POST:
						
							if (request.getResourceParameters().containsKey("html")){
								String newUuid = controller.getData().create(request.getResourceParameters().get("html"));
								StringBuilder aux = new StringBuilder();
								aux.append("<a href=\"html?uuid=").append(newUuid).append("\">").append(newUuid).append("</a>");

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
							if(controller.contains(uuid)){
								controller.getData().delete(uuid);
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
				}

				System.out.println("-----RESPONSE-------");
				System.out.println(response.toString());
				System.out.println("--------------------");

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
	}
}
