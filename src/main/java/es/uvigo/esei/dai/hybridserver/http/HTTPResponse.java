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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HTTPResponse {
    
	// Define instance variables
    private HTTPResponseStatus status;
    private String version;
    private String content;
    private Map<String, String> parameters;

    // Constructor to initialize the object
    public HTTPResponse() {
        // Initialize parameters as a LinkedHashMap to preserve insertion order
        this.parameters = new LinkedHashMap<>();
    }

    // Getter for the HTTP response status
    public HTTPResponseStatus getStatus() {
        return status;
    }

    // Setter for the HTTP response status
    public void setStatus(HTTPResponseStatus status) {
        this.status = status;
    }

    // Getter for the HTTP version
    public String getVersion() {
        return version;
    }

    // Setter for the HTTP version
    public void setVersion(String version) {
        this.version = version;
    }

    // Getter for the content of the HTTP response
    public String getContent() {
        return content;
    }

    // Setter for the content of the HTTP response
    public void setContent(String content) {
        this.content = content;
    }

    // Getter for the parameters (headers) of the HTTP response
    public Map<String, String> getParameters() {
        return parameters;
    }

    // Add a parameter (header) to the HTTP response
    public void putParameter(String name, String value) {
        parameters.put(name, value);
    }

    // Check if a parameter (header) exists in the HTTP response
    public boolean containsParameter(String name) {
        return parameters.containsKey(name);
    }

    // Remove a parameter (header) from the HTTP response
    public String removeParameter(String name) {
        return parameters.remove(name);
    }

    // Clear all parameters (headers) from the HTTP response
    public void clearParameters() {
        parameters.clear();
    }

    // Get a list of parameter (header) names in the HTTP response
    public List<String> listParameters() {
        // Return a new ArrayList containing the keys (parameter names)
        return new ArrayList<>(parameters.keySet());
    }

    // Generate and write the HTTP response to a Writer
    public void print(Writer writer) throws IOException {
        int contentLength = (content != null) ? content.length() : 0;
        StringBuilder responseBuilder = new StringBuilder();

        // Append the HTTP version, status code, and status message to the response
        responseBuilder.append(version)
                      .append(" ")
                      .append(status.getCode())
                      .append(" ")
                      .append(status.getStatus())
                      .append("\r\n");

        if (contentLength > 0) {
            // Add the Content-Length header if content exists
            parameters.put("Content-Length", Integer.toString(contentLength));
        }

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            // Append each parameter (header) to the response
            responseBuilder.append(entry.getKey())
                          .append(": ")
                          .append(entry.getValue())
                          .append("\r\n");
        }

        // Append an empty line to separate headers from content
        responseBuilder.append("\r\n");

        if (contentLength > 0) {
            // Append the content if it exists
            responseBuilder.append(content);
        }

        // Write the generated response to the provided Writer
        writer.write(responseBuilder.toString());
    }

    // Override the toString method to provide a string representation of the HTTP response
    @Override
    public String toString() {
        try (final StringWriter writer = new StringWriter()) {
            // Call the print method to generate the response and convert it to a string
            this.print(writer);
            return writer.toString();
        } catch (IOException e) {
            // Handle unexpected I/O exceptions
            throw new RuntimeException("Unexpected I/O exception", e);
        }
    }
}
