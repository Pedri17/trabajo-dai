package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class ConfigurationController {
    private Properties properties;

    public ConfigurationController(){
        properties = new Properties();
        properties.setProperty("numClients", "50");
        properties.setProperty("port", "8888");
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/hstestdb");
        properties.setProperty("db.user", "hsdb");
        properties.setProperty("db.password", "hsdbpass");
    }

    public ConfigurationController(File file){
        properties = new Properties();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
            String line = br.readLine();
            String [] words; 
            while(line != null){
                words = line.split("=");
                properties.setProperty(words[0], words[1]);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Properties getProperties(){
        return properties;
    }
}