package hello;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:global.properties") 
@ConfigurationProperties
public class GlobalProperties {

    private String root;

    public String getRoot(){
    	return root;
    }
    
    public void setRoot(String rt){
    	root = rt;
    }
}
