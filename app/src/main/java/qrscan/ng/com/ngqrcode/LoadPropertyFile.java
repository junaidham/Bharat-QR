package qrscan.ng.com.ngqrcode;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class LoadPropertyFile {
		 
	    private Properties prop = null;
	     
	    public LoadPropertyFile(){
	         
	        InputStream is = null;
	        try {
	            this.prop = new Properties();
	            is  = getClass().getClassLoader().getResourceAsStream(SystemConstants.configFile);
	            prop.load(is);
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	     
	    public String getPropertyValue(String key){
	        return this.prop.getProperty(key);
	    }
	    public void setPropertyFile(String key,String value){
	    	try {
	    		PropertiesConfiguration config = new PropertiesConfiguration(SystemConstants.configFile);
	    		config.setProperty(key, value);
	    		config.save();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	     
}
