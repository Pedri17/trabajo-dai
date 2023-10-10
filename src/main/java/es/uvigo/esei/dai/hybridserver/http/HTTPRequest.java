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
    // Declare instance variables
    private BufferedReader reader;
    private HTTPRequestMethod method;
    private String resourceChain;
    private String[] resourcePath;
    private Map<String, String> resourceParameters;
    private Map<String, String> resourceHeaderParameters;
    private String resourceName;
    private String version;
    private String content;
    private int contentLength;

    // Constructor to initialize an HTTPRequest object
    public HTTPRequest(Reader reader) throws IOException, HTTPParseException {
        // Initialize the BufferedReader for reading HTTP requests
        this.reader = new BufferedReader(reader);

        // Call the initValues method to populate the object's values
        initValues();
    }

    // Getter for the HTTP request method
    public HTTPRequestMethod getMethod() {
        return this.method;
    }

    // Getter for the resource chain (URI)
    public String getResourceChain() {
        return this.resourceChain;
    }

    // Getter for the resource path (parts of the URI)
    public String[] getResourcePath() {
        return this.resourcePath;
    }

    // Getter for the resource name (part of the URI)
    public String getResourceName() {
        return this.resourceName;
    }
    
 // Getter for the parameters parsed from the HTTP Request
    public Map<String, String> getResourceParameters() {

		return this.resourceParameters;
	}


    // Getter for the HTTP version
    public String getHttpVersion() {
        return this.version;
    }

    // Getter for the HTTP request headers (parameters)
    public Map<String, String> getHeaderParameters() {
        return this.resourceHeaderParameters;
    }

    // Getter for the content of the HTTP request body
    public String getContent() {
        return this.content;
    }

    // Getter for the length of the content in the HTTP request body
    public int getContentLength() {
        return this.contentLength;
    }

    // Override the toString method to provide a string representation of the HTTP request
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getMethod().name()).append(' ')
                .append(this.getResourceChain()).append(' ').append(this.getHttpVersion()).append("\r\n");

        // Append the HTTP request headers to the string
        for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
            sb.append(param.getKey()).append(": ").append(param.getValue()).append("\r\n");
        }

        // Append the content of the HTTP request body, if it exists
        if (this.getContentLength() > 0) {
            sb.append("\r\n").append(this.getContent());
        }

        return sb.toString();
    }

    /**
     * Retrieves information from an HTTP request.
     *
     * @throws IOException
     * @throws HTTPParseException if the HTTP request is incorrect.
     */
    private void initValues() throws IOException, HTTPParseException {
        // Split the first line of the request to obtain the method, resource chain, and version
        String[] firstLine = this.reader.readLine().split(" ");

        if (firstLine.length != 3) {
            throw new HTTPParseException("Invalid HTTPRequest first line.");
        }

        // Set the HTTP request method, resource chain (URI), and version
        this.method = HTTPRequestMethod.valueOf(firstLine[0]);
        this.resourceChain = firstLine[1];
        this.version = firstLine[2];

        // Initialize the HTTP request header parameters as a LinkedHashMap to preserve order
        this.resourceHeaderParameters = new LinkedHashMap<String, String>();
        String line;

        // Read lines containing HTTP request header parameters until an empty line is encountered
        while ((line = this.reader.readLine()) != null && !line.matches("")) {
            // Check that header parameters are in the correct format (key: value)
            if (line.matches(".*:.*")) {
                String[] headerParameters = line.split(": ");
                this.resourceHeaderParameters.put(headerParameters[0], headerParameters[1]);

                // If "Content-Length" is found, set the content length
                if (headerParameters[0].equals("Content-Length")) {
                    this.contentLength = Integer.parseInt(headerParameters[1]);
                }

            } else {
                throw new HTTPParseException("Invalid HTTPRequest.");
            }
        }

        String[] parameters = null;

        // Check if query parameters are part of the requested resource chain (URI)
        if (this.resourceChain.matches(".+\\?.+")) {
            // Split the resource chain into resource name and query parameters
            String[] splitResource = this.resourceChain.split("\\?");
            this.resourceName = splitResource[0].substring(1);
            parameters = splitResource[1].split("\\&");

        } else {
            this.resourceName = this.resourceChain.substring(1);

            // Check if there is content in the HTTP request body
            if (this.contentLength > 0) {
                char[] contentArray = new char[this.contentLength];
                if (this.reader.read(contentArray) != contentArray.length) {
                    throw new HTTPParseException("Invalid content length.");
                }

                this.content = new String(contentArray);

                // Decode the content if it has a specific content type
                String type = this.resourceHeaderParameters.get("Content-Type");
                if (type != null && type.startsWith("application/x-www-form-urlencoded")) {
                    this.content = URLDecoder.decode(this.content, "UTF-8");
                }
                parameters = this.content.split("\\&");
            }
        }

        // Initialize the HTTP request query parameters as a LinkedHashMap
        this.resourceParameters = new LinkedHashMap<String, String>();

        // Populate the query parameters map
        if (parameters != null) {
            String[] splitParameters;
            for (int i = 0; i < parameters.length; i++) {
                splitParameters = parameters[i].split("=");
                this.resourceParameters.put(splitParameters[0], splitParameters[1]);
            }
        }

        // Initialize the resource path with a length of 0 in case there is no path
        resourcePath = new String[0];
        if (!resourceName.equals("")) {
            // Split the resource name into parts for the resource path
            resourcePath = this.resourceName.split("\\/");
        }
    }
}
