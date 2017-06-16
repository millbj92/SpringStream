package hello;

import hello.storage.StorageProperties;
import hello.storage.StorageService;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Application{ 
	
	public static ConfigurableApplicationContext context;
	

	public static void main(String[] args) {
		//SpringApplication.run(Application.class, args);
		
		SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
	    builder.headless(false);
	    context = builder.run(args);
	    MyTrayIcon.getInstance();
	}
	

    
    
	

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
            storageService.deleteAll();
            storageService.init();
		};
	}
}
