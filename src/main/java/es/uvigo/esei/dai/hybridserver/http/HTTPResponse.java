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
    
    private HTTPResponseStatus status;
    private String version;
    private String content;
    private Map<String, String> parameters;

    public HTTPResponse() {
        this.parameters = new LinkedHashMap<>();
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
        int contLength = 0;
        if (content != null) contLength = content.length();
        StringBuilder aux = new StringBuilder();

        aux.append(version).append(" ").append(status.getCode()).append(" ").append(status.getStatus()).append("\r\n");

        if (contLength > 0) parameters.put("Content-Length", Integer.toString(contLength));

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            aux.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }

        aux.append("\r\n");

        if (contLength > 0) aux.append(content);

        writer.write(aux.toString());
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
