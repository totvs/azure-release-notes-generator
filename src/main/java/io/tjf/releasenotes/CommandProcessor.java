package io.tjf.releasenotes;

import java.io.IOException;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import io.tjf.releasenotes.generator.GeneratorService;

/**
 * {@link ApplicationRunner} that triggers the generation of the release notes
 * by command line.
 *
 * @author Rubens dos Santos Filho
 */
@Component
public class CommandProcessor implements ApplicationRunner {

	private final GeneratorService generator;

	public CommandProcessor(final GeneratorService generator) {
		this.generator = generator;
	}

	@Override
	public void run(final ApplicationArguments args) throws IOException {
		this.generator.generate();
	}

}
