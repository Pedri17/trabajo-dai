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

    public HTTPRequest(Reader reader) throws IOException, HTTPParseException {
        
        this.reader = new BufferedReader(reader);
        this.resourceParameters = new LinkedHashMap<String, String>();
        this.resourceHeaderParameters = new LinkedHashMap<String, String>();

        String[] firstLine = this.reader.readLine().split(" ");

        if (firstLine.length != 3) {
            throw new HTTPParseException("Invalid HTTPRequest first line.");
        }

        this.method = HTTPRequestMethod.valueOf(firstLine[0]);
        this.resourceChain = firstLine[1];
        this.version = firstLine[2];

        String line;

        while ((line = this.reader.readLine()) != null && !line.matches("")) {

            if (line.matches(".*:.*")) {
                String[] headerParameters = line.split(": ");
                this.resourceHeaderParameters.put(headerParameters[0], headerParameters[1]);

                if (headerParameters[0].equals("Content-Length")) {
                    this.contentLength = Integer.parseInt(headerParameters[1]);
                }

            } else {
                throw new HTTPParseException("Invalid HTTPRequest.");
            }
        }

        String[] parameters = null;

        if (this.resourceChain.matches(".+\\?.+")) {
            String[] splitResource = this.resourceChain.split("\\?");
            this.resourceName = splitResource[0].substring(1);
            parameters = splitResource[1].split("\\&");

        } else {
            this.resourceName = this.resourceChain.substring(1);

            if (this.contentLength > 0) {
                char[] contentArray = new char[this.contentLength];
                if (this.reader.read(contentArray) != contentArray.length) {
                    throw new HTTPParseException("Invalid content length.");
                }

                this.content = new String(contentArray);

                String type = this.resourceHeaderParameters.get("Content-Type");
                if (type != null && type.startsWith("application/x-www-form-urlencoded")) {
                    this.content = URLDecoder.decode(this.content, "UTF-8");
                }
                parameters = this.content.split("\\&");
            }
        }

        if (parameters != null) {
            String[] splitParameters;
            for (int i = 0; i < parameters.length; i++) {
                splitParameters = parameters[i].split("=");
                this.resourceParameters.put(splitParameters[0], splitParameters[1]);
            }
        }

        resourcePath = new String[0];
        if (!resourceName.equals("")) {
            resourcePath = this.resourceName.split("\\/");
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
        return this.version;
    }

    public Map<String, String> getHeaderParameters() {
        return this.resourceHeaderParameters;
    }

    public String getContent() {
        return this.content;
    }

    public int getContentLength() {
        return this.contentLength;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getMethod().name()).append(' ')
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
