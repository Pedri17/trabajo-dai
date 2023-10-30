package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {
	Socket socket;
	HTMLController controller;
	
	public ServiceThread(Socket socket, HTMLController controller) {
		this.socket = socket;
		this.controller = controller;
	}
	
	public void run() {
		try(Socket socket = this.socket){
			HTTPRequest request;
			HTTPResponse response = new HTTPResponse();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			OutputStream output = socket.getOutputStream();
			
			try {
				request = new HTTPRequest(br);
				
				switch(request.getMethod()) {
					case GET:
						String requestUuid = request.getResourceParameters().get("uuid");
						// Request message has a uuid field
						if (requestUuid != null){
							// Is a known uuid
							if (controller.contains(requestUuid)){
								String uuidContent = controller.getContent(requestUuid);
								
								response.setContent(uuidContent);
								response.setStatus(HTTPResponseStatus.S200);
								response.putParameter("Conten-Type", "text/html");
							}else{
							// TODO: Unknown uuid
							}
						}else{
							StringBuilder res = new StringBuilder();
							for(String uuid : controller.getData().list()){
								res.append(uuid);
							}

							response.setContent(res.toString());
							response.setStatus(HTTPResponseStatus.S200);
						}
						
						break;
				default:
					System.out.println("Metodo no implementado de momento");
					break;
						
				}
				
				// Respuesta
				response.setStatus(HTTPResponseStatus.S200);
				output.close();
			}catch(IOException e) {
				//TODO: Handle exception
			}catch(HTTPParseException e) {
				//TODO: Handle exception
			}		
		}catch(IOException e){
			// TODO: handle exception
		}
	}
}
