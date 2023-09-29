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
import java.util.Map;

public class HTTPRequest {
	
	HTTPRequestMethod method;
	String resourceChain;
	String [] resourcePath;
	String resourceName;
	Map<String,String> headerParameters;
	String content;
	int contentLength;
	
  public HTTPRequest(Reader reader) throws IOException, HTTPParseException {
    // TODO Completar. Cualquier error en el procesado debe lanzar una HTTPParseException
	  BufferedReader bReader = new BufferedReader(reader);
	  String [] lineWords =  bReader.readLine().split(" ");
	  
	  //Get HTTPRequest Method
	  switch(lineWords[0]) {
	  	case "GET":
	  		method = HTTPRequestMethod.GET;
	  		break;
	  	case "CONNECT":
	  		method = HTTPRequestMethod.CONNECT;
	  		break;
	  	case "DELETE":
	  		method = HTTPRequestMethod.DELETE;
	  		break;
	  	case "HEAD":
	  		method = HTTPRequestMethod.HEAD;
	  		break;
	  	case "OPTIONS":
	  		method = HTTPRequestMethod.OPTIONS;
	  		break;
	  	case "POST":
	  		method = HTTPRequestMethod.POST;
	  		break;
	  	case "PUT":
	  		method = HTTPRequestMethod.PUT;
	  		break;
	  	case "TRACE":
	  		method = HTTPRequestMethod.TRACE;
	  		break;
	  	default: 
	  		System.err.println("HTTP Request Method not expected.");
	  		throw new HTTPParseException();
	  }
	  
	  this.resourceChain = lineWords[1];
	  this.resourcePath = lineWords[1].split("/");
	  this.resourceName = lineWords[1].substring(1);
  }

  public HTTPRequestMethod getMethod() {
    return this.method;
  }

  public String getResourceChain() {
    // TODO Completar
    return null;
  }

  public String[] getResourcePath() {
    // TODO Completar
    return null;
  }

  public String getResourceName() {
    // TODO Completar
    return null;
  }

  public Map<String, String> getResourceParameters() {
    // TODO Completar
    return null;
  }

  public String getHttpVersion() {
    // TODO Completar
    return null;
  }

  public Map<String, String> getHeaderParameters() {
    // TODO Completar
    return null;
  }

  public String getContent() {
    // TODO Completar
    return null;
  }

  public int getContentLength() {
    // TODO Completar
    return -1;
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
