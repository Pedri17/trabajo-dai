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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTPResponse {

    private HTTPResponseStatus status;
    private String version;
    private String content;
    private Map<String, String> params;

  public HTTPResponse() {
    version = "";
    content = "";
    params = new HashMap<String,String>(); 
  }

  public HTTPResponseStatus getStatus() {
    return this.status;
  }

  public void setStatus(HTTPResponseStatus status) {
    this.status = status;
  }

  public String getVersion() {
    return this.version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getContent() {
    return this.content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Map<String, String> getParameters() {
    return this.params;
  }

  public String putParameter(String name, String value) {
    return this.params.put(name, value);
  }

  public boolean containsParameter(String name) {
    return this.params.containsKey(name);
  }

  public String removeParameter(String name) {
    return this.params.remove(name);
  }

  public void clearParameters() {
    this.params = new HashMap<String, String>();
  }

  public List<String> listParameters() {
    return new ArrayList<String>(this.params.keySet());
  }

  public void print(Writer writer) throws IOException {

    // First line
    writer.write(this.version);
    writer.write(" ");
    writer.write(Integer.toString(this.status.getCode()));
    writer.write(" ");
    writer.write(this.status.getStatus());
    writer.write("\r\n");

    // Put parameters
    for(String paramName: this.params.keySet()){
      writer.write(paramName);
      writer.write(": ");
      writer.write(this.params.get(paramName));
      writer.write("\r\n");
    }

    // Put content
    if(content.length()>0){
      writer.write("Content-Length: ");
      writer.write(Integer.toString(this.content.length()));
      writer.write("\r\n\r\n");
      writer.write(this.content);
    }else{
      writer.write("\r\n");
    }
  }

  @Override
  public String toString() {
    try (final StringWriter writer = new StringWriter()) {
      this.print(writer);

      return writer.toString();
    } catch (IOException e) {
      throw new RuntimeException("Unexpected I/O exception", e);
    }
  }
}
