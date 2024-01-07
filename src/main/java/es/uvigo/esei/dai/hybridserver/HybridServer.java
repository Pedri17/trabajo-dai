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
package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import es.uvigo.esei.dai.hybridserver.html.HTMLController;
import es.uvigo.esei.dai.hybridserver.html.HTMLDaoDB;
import es.uvigo.esei.dai.hybridserver.html.HTMLDaoMap;
import es.uvigo.esei.dai.hybridserver.webservice.WebServiceController;
import es.uvigo.esei.dai.hybridserver.xml.XMLController;
import es.uvigo.esei.dai.hybridserver.xml.XMLDaoDB;
import es.uvigo.esei.dai.hybridserver.xsd.XSDController;
import es.uvigo.esei.dai.hybridserver.xsd.XSDDaoDB;
import es.uvigo.esei.dai.hybridserver.xslt.XSLTController;
import es.uvigo.esei.dai.hybridserver.xslt.XSLTDaoDB;
import jakarta.xml.ws.Endpoint;

public class HybridServer implements AutoCloseable {
  private int servicePort = 8888;
  private int maxClients = 10;
  private Thread serverThread;
  private boolean stop;

  private String dbUrl, dbUser, dbPassword, wsUrl;
  private List<ServerConfiguration> servers;
  private Endpoint endpoint;

  private ExecutorService threadPool;

  private HTMLController htmlController;
  private XMLController xmlController;
  private XSDController xsdController;
  private XSLTController xsltController;

  public void initControllers(String user, String password, String url){
    htmlController = new HTMLController(new HTMLDaoDB(user, password, url));
    xmlController = new XMLController(new XMLDaoDB(user, password, url));
    xsdController = new XSDController(new XSDDaoDB(user, password, url));
    xsltController = new XSLTController(new XSLTDaoDB(user, password, url));
  }
  
  public HybridServer() {
    this.stop = false;
    Configuration config = new Configuration();

    this.servicePort = config.getHttpPort();
    this.maxClients = config.getNumClients();
    this.servers = config.getServers();
    
    this.wsUrl = config.getWebServiceURL();
    this.dbUser = config.getDbUser();
    this.dbPassword = config.getDbPassword();
    this.dbUrl = config.getDbURL();
    initControllers(dbUser, dbPassword, dbUrl);
    
    System.out.println("Server launched without any parameters.");
  }
  
  public HybridServer(Map<String, String> pages) {
    // Hasta dónde sé ya no está en uso
    this.stop = false;
    htmlController = new HTMLController(new HTMLDaoMap(pages));

    Properties properties = new ConfigurationController().getProperties();

    servicePort = Integer.parseInt(properties.getProperty("port"));
    maxClients = Integer.parseInt(properties.getProperty("numClients"));

    System.out.println("Server lauched with pages parameter.");
  }

  public HybridServer(Configuration config){
    this.stop = false;

    this.servicePort = config.getHttpPort();
    this.maxClients = config.getNumClients();
    this.servers = config.getServers();
    
    this.wsUrl = config.getWebServiceURL();
    this.dbUser = config.getDbUser();
    this.dbPassword = config.getDbPassword();
    this.dbUrl = config.getDbURL();
    initControllers(dbUser, dbPassword, dbUrl);

    System.out.println("Server launched with configuration");
  }

  public HybridServer(Properties properties) {
    this.stop = false;

    Configuration config = new Configuration();

    this.servicePort = Integer.parseInt(properties.getProperty("port"));
    this.maxClients = Integer.parseInt(properties.getProperty("numClients"));
    this.servers = config.getServers();

    this.wsUrl = config.getWebServiceURL();
    this.dbUser = properties.getProperty("db.user");
    this.dbPassword = properties.getProperty("db.password");
    this.dbUrl = properties.getProperty("db.url");
    initControllers(dbUser, dbPassword, dbUrl);

    System.out.println("Server lauched with properities parameter.");
  }

  public int getPort() {
    return servicePort;
  }

  public void start() {
    this.serverThread = new Thread() {

      @Override
      public void run() {
    	  
        try (final ServerSocket serverSocket = new ServerSocket(servicePort)) {
          threadPool = Executors.newFixedThreadPool(maxClients);

          // Publish webservice endPoints
          try{
            endpoint = Endpoint.publish(wsUrl, 
              new WebServiceController(htmlController, xmlController, xsdController, xsltController)
            );
            endpoint.setExecutor(threadPool);
          }catch(Exception e){
            e.printStackTrace();
          }

        	
        	while (true) {
        		Socket socket = serverSocket.accept();
        		if (stop) 
              break;
        		threadPool.execute(new ServiceThread(socket, htmlController, xmlController, xsdController, xsltController, servers));
        	}

        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };

    this.stop = false;
    this.serverThread.start();
  }

  @Override
  public void close() {
    this.stop = true;

    if (endpoint!=null){
      endpoint.stop();
      System.out.println("Web Services stopped.");
    } 

    try (Socket socket = new Socket("localhost", servicePort)) {
      // Esta conexión se hace, simplemente, para "despertar" el hilo servidor
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try {
      this.serverThread.join();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    this.serverThread = null;

    threadPool.shutdownNow();

    try {
      threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

}
