/**
 *  HybridServer
 *  Copyright (C) 2023 Miguel Reboiro-Jato
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class HTTPRequest {
	
	HTTPRequestMethod method;
	String resourceChain;
	String [] resourcePath;
	String resourceName;
	String httpVersion;
	Map<String,String> headerParameters;
	Map<String, String> resourceParameters;
	String content;
	int contentLength = 0;
	
  public HTTPRequest(Reader reader) throws IOException, HTTPParseException {
	  BufferedReader bReader = new BufferedReader(reader);
	  String [] lineWords =  bReader.readLine().split(" ");
	  this.headerParameters = new LinkedHashMap<String, String>();
	  this.resourceParameters = new LinkedHashMap<String, String>();
	  
	  if(lineWords==null) throw new HTTPParseException();
	  
	//Get HTTPRequest Method
	  switch(lineWords[0]) {
	  	case "GET":
	  		this.method = HTTPRequestMethod.GET;
	  		break;
	  	case "CONNECT":
	  		this.method = HTTPRequestMethod.CONNECT;
	  		break;
	  	case "DELETE":
	  		this.method = HTTPRequestMethod.DELETE;
	  		break;
	  	case "HEAD":
	  		this.method = HTTPRequestMethod.HEAD;
	  		break;
	  	case "OPTIONS":
	  		this.method = HTTPRequestMethod.OPTIONS;
	  		break;
	  	case "POST":
	  		this.method = HTTPRequestMethod.POST;
	  		break;
	  	case "PUT":
	  		this.method = HTTPRequestMethod.PUT;
	  		break;
	  	case "TRACE":
	  		this.method = HTTPRequestMethod.TRACE;
	  		break;
	  	default: 
	  		System.err.println("HTTP Request Method not expected.");
	  		throw new HTTPParseException();
	  }
	  
	  this.resourceChain = lineWords[1];
	  if(lineWords[1].length()>1) {
		  this.resourcePath = new String [2];
		  this.resourcePath[0] = lineWords[1].substring(1).split("/")[0];
		  this.resourcePath[1] = lineWords[1].substring(1).split("/")[1].split("\\?")[0];
	  }else {
		  this.resourcePath = new String[0];
	  }
	  this.resourceName = lineWords[1].substring(1).split("\\?")[0];
	  this.httpVersion = lineWords[2].substring(0, lineWords[2].length());
	  
	  lineWords =  bReader.readLine().split(" ");
	  while(lineWords[0].length() > 0) {
		  String header = lineWords[0].substring(0,lineWords[0].length()-1);
		  String content = lineWords[1];
		  this.headerParameters.put(header, content);
		  lineWords =  bReader.readLine().split(" ");
	  }
	  String line = bReader.readLine();
	  String message = "";
	  String [] messages;
	  
	  while(line != null && !line.isEmpty()) {
		  message += line;
		  messages = line.split("&");
		  for(int i=0; i<messages.length; i++) {
			  this.resourceParameters.put(messages[i].split("=")[0], messages[i].split("=")[1]);
		  }
		  line = bReader.readLine();
	  }
	  if(!message.isEmpty()) {
		  this.content = message;
		  this.contentLength = message.length();
	  }
	  

  }

  public HTTPRequestMethod getMethod() {
    return this.method;
  }

  public String getResourceChain() {
    return this.resourceChain;
  }

  public String[] getResourcePath() {
    return this.resourcePath;
  }

  public String getResourceName() {
    return this.resourceName;
  }

  public Map<String, String> getResourceParameters() {
    return this.resourceParameters;
  }

  public String getHttpVersion() {
    return this.httpVersion;
  }

  public Map<String, String> getHeaderParameters() {
    return this.headerParameters;
  }

  public String getContent() {
    return this.content;
  }

  public int getContentLength() {
    return this.contentLength;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder().append(this.getMethod().name()).append(' ')
      .append(this.getResourceChain()).append(' ').append(this.getHttpVersion()).append("\r\n");

    for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
      sb.append(param.getKey()).append(": ").append(param.getValue()).append("\r\n");
    }

    if (this.getContentLength() > 0) {
      sb.append("\r\n").append(this.getContent());
    }

    return sb.toString();
  }
}