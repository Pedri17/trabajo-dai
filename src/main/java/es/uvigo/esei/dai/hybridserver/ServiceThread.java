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
	UuidContentMap content;
	
	public ServiceThread(Socket socket, UuidContentMap content) {
		this.socket = socket;
		this.content = content;
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
						// De momento solo pueden ser get
							String contenidoResultado = content.getContent(request.getResourceParameters().get("uuid"));
							if(contenidoResultado!=null){
								response.setContent(contenidoResultado);
							}else {
								System.out.println("No se encuentra el UUID");
							}
						break;
				default:
					break;
						
				}
				
				
				
				// Respuesta
				response.setStatus(HTTPResponseStatus.S200);
				output.write(response.toString().getBytes());
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
