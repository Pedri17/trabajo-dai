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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Launcher {
  public static void main(String[] args) {
    if(args.length == 0){
      new HybridServer().start();
    }else if(args.length==1){
      File configurationFile = new File(args[0]);
      Configuration config = null;
      try(Reader rd = new InputStreamReader(new FileInputStream(configurationFile))){
        config = XMLConfigurationLoader.load(rd);
      }catch(Exception e){
        e.printStackTrace();
      }
      new HybridServer(config).start();
    }else{
      System.err.println("Error: You tried to start the application with more than one configuration parameter.");
    }
  }
}
