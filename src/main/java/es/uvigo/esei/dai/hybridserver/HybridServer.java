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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.lang.Runnable;

public class HybridServer implements AutoCloseable {
  private int SERVICE_PORT = 8888;
  private int MAX_CLIENTS = 10;
  private Thread serverThread;
  private boolean stop;
  
  private HTMLController controller;
  private ExecutorService threadPool;

  public HybridServer() {
    this.stop = false;

    Properties properties = new ConfigurationController().getProperties();

    SERVICE_PORT = Integer.parseInt(properties.getProperty("port"));
    MAX_CLIENTS = Integer.parseInt(properties.getProperty("numClients"));

    String USER = properties.getProperty("db.user");
    String PASSWORD = properties.getProperty("db.password");
    String URL = properties.getProperty("db.url");

    controller = new HTMLController(new HTMLDaoDB(USER, PASSWORD, URL));
    
    System.out.println("Server launched without any parameters.");
  }

  public HybridServer(Map<String, String> pages) {
    this.stop = false;
    controller = new HTMLController(new HTMLDaoMap(pages));

    Properties properties = new ConfigurationController().getProperties();

    SERVICE_PORT = Integer.parseInt(properties.getProperty("port"));
    MAX_CLIENTS = Integer.parseInt(properties.getProperty("numClients"));

    System.out.println("Server lauched with pages parameter.");
  }

  public HybridServer(Properties properties) {
    this.stop = false;
    System.out.println("Server lauched with properities parameter.");

    SERVICE_PORT = Integer.parseInt(properties.getProperty("port"));
    MAX_CLIENTS = Integer.parseInt(properties.getProperty("numClients"));

    String USER = properties.getProperty("db.user");
    String PASSWORD = properties.getProperty("db.password");
    String URL = properties.getProperty("db.url");

    controller = new HTMLController(new HTMLDaoDB(USER, PASSWORD, URL));
  }

  public int getPort() {
    return SERVICE_PORT;
  }

  public void start() {
    this.serverThread = new Thread() {

      @Override
      public void run() {
    	  
        try (final ServerSocket serverSocket = new ServerSocket(SERVICE_PORT)) {
          threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        	
        	while (true) {
        		Socket socket = serverSocket.accept();
        		if (stop) 
              break;
        		threadPool.execute(new ServiceThread(socket, controller));
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

    try (Socket socket = new Socket("localhost", SERVICE_PORT)) {
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
