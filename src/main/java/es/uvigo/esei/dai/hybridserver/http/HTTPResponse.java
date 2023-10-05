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
    private Map<String, String> parameters;

    public HTTPResponse() {
        this.parameters = new HashMap<>();
    }

    public HTTPResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HTTPResponseStatus status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void putParameter(String name, String value) {
        parameters.put(name, value);
    }

    public boolean containsParameter(String name) {
        return parameters.containsKey(name);
    }

    public String removeParameter(String name) {
        return parameters.remove(name);
    }

    public void clearParameters() {
        parameters.clear();
    }

    public List<String> listParameters() {
        return new ArrayList<>(parameters.keySet());
    }

    public void print(Writer writer) throws IOException {
        String responseString = this.version + " " + this.status.getCode() + " " + this.status.getStatus() + "\r\n";

        if (this.content != null) {
            // calculate content length 
            int contentLength = this.content.length();
            
            // Add the Content-Length header
            this.parameters.put("Content-Length", Integer.toString(contentLength));

            // Write the headings
            for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
                responseString += entry.getKey() + ": " + entry.getValue() + "\r\n";
            }

            // Add a blank line to separate headers from content
            responseString += "\r\n";

            // add content 
            responseString += this.content;
        } else {
            // If there is no content, just write the headers
            for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
                responseString += entry.getKey() + ": " + entry.getValue() + "\r\n";
            }

            // Add a blank line
            responseString += "\r\n";
        }

        writer.write(responseString);
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

