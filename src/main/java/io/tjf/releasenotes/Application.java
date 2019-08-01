package io.tjf.releasenotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import io.tjf.releasenotes.properties.ApplicationProperties;

/**
 * Azure Devops release notes generator.
 * 
 * @author Rubens dos Santos Filho
 */
@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
