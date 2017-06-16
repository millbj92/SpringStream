package hello;

import hello.storage.StorageProperties;
import hello.storage.StorageService;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
	public static DateTimeFormatter formatter;
	

	public static void main(String[] args) throws IOException {
		//SpringApplication.run(Application.class, args);
		
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss.SSS");
		File f = new File(System.getProperty("user.dir") + "/Logs/");
		f.mkdirs();
		File ff = new File(f.getAbsolutePath() + "/" + LocalDateTime.now().format(formatter) + ".txt");
		ff.createNewFile();
		PrintStream out = new PrintStream(new FileOutputStream(ff.getAbsolutePath()));
		System.setOut(out);
		
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
