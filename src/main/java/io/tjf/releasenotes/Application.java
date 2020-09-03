package io.tjf.releasenotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import io.tjf.releasenotes.properties.ReleaseNotesProperties;

/**
 * Azure DevOps release notes generator.
 * 
 * @author Rubens dos Santos Filho
 */
@SpringBootApplication
@EnableConfigurationProperties(ReleaseNotesProperties.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
