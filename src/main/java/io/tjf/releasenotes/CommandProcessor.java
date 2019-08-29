package io.tjf.releasenotes;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import io.tjf.releasenotes.generator.Generator;

/**
 * {@link ApplicationRunner} that triggers the generation of the release notes
 * by command line.
 *
 * @author Rubens dos Santos Filho
 */
@Component
public class CommandProcessor implements ApplicationRunner {

	private final Generator generator;

	public CommandProcessor(Generator generator) {
		this.generator = generator;
	}

	@Override
	public void run(ApplicationArguments args) throws IOException {
		run(args.getNonOptionArgs());
	}

	private void run(List<String> args) throws IOException {
		this.generator.generate();
	}

}
