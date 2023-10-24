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
	public ServiceThread(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		try(Socket socket = this.socket){
			HTTPRequest request;
			HTTPResponse response = new HTTPResponse();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			OutputStream output = socket.getOutputStream();
			
			try {
				request = new HTTPRequest(br);
				response.setStatus(HTTPResponseStatus.S200);
				response.setContent(request.toString());
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
