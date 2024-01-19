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
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

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
        
    this.resourceParameters = new LinkedHashMap<String, String>();  
    this.headerParameters = new LinkedHashMap<String, String>();
    

    // First line (Method, location, version)
    String [] lineWords =  bReader.readLine().split(" ");
    
    // Exception there's not first line or it doesn't have all the parameters
    if(lineWords==null || lineWords.length<3) throw new HTTPParseException();
    
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

    // Ger Resource Chain
    this.resourceChain = lineWords[1];

    // Get Resource Path
    if(lineWords[1].length()>1) {
        this.resourcePath = lineWords[1].substring(1).split("/");
        if(lineWords[1].contains("?")){
          this.resourcePath[this.resourcePath.length-1] = this.resourcePath[this.resourcePath.length-1].split("\\?")[0];             
        }
    }else {
        this.resourcePath = new String[0];
    }

    // Get Resource Params (on URI)
    if(lineWords[1].contains("?")){
      String [] resourceArray = lineWords[1].split("\\?")[1].split("&");
      for(int i=0; i<resourceArray.length; i++){
        this.resourceParameters.put(resourceArray[i].split("=")[0],resourceArray[i].split("=")[1]);
      }
    }

    // Get Resource Name
    this.resourceName = lineWords[1].substring(1).split("\\?")[0];
    
    // Get HTTP Version
    this.httpVersion = lineWords[2].substring(0, lineWords[2].length());
    
    // Second line (Header params or nothing)
    lineWords =  bReader.readLine().split(" ");

    String newLine = "";

    // Put all header params
    
    while(lineWords != null && lineWords[0].length() > 0) {
      // Exception, invalid header format
      if(lineWords.length<2) throw new HTTPParseException();

      String header = lineWords[0].substring(0,lineWords[0].length()-1);
      String content = lineWords[1];

      // Put a Header Parameter
      this.headerParameters.put(header, content);

      // Get next line (while is not empty)
      newLine = bReader.readLine();

      if(newLine!=null){
        lineWords =  newLine.split(" ");
      }else{
        lineWords = null;
      }
    }

    // Exception null line after header
    if(lineWords==null) throw new HTTPParseException();

    String contLength = headerParameters.get("Content-Length");

    if(contLength != null){
      // Get Content Length
      this.contentLength = Integer.parseInt(contLength);

      StringBuilder message = new StringBuilder();

      for(int i=0; i<this.contentLength; i++){
        char newChar = (char) bReader.read();
        message.append(newChar);
      }
      
      String type = this.headerParameters.get("Content-Type");

      // Decode if content is codified
      if(type!=null && type.startsWith("application/x-www-form-urlencoded")){
        StringBuilder decodedMessage = new StringBuilder();
        decodedMessage.append(URLDecoder.decode(message.toString(), "UTF-8"));
        message = decodedMessage;
        System.out.println("Decoded message: "+message.toString());
      }

      // Get content (Resource Parameters)
      String [] resourceArray = message.toString().split("&");
      for(int i=0; i<resourceArray.length; i++){
        if(!resourceArray[i].contains("=")) throw new HTTPParseException();
        this.resourceParameters.put(resourceArray[i].split("=")[0],resourceArray[i].split("=")[1]);
        System.out.println("Anhadido: "+resourceArray[i].split("=")[0]+" - "+resourceArray[i].split("=")[1]);
      }
      
      // Get content
      if(message.length()!=0) {
          this.content = message.toString();
      }
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