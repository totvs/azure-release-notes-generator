package io.tjf.releasenotes.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import io.tjf.releasenotes.azure.service.CommitService;
import io.tjf.releasenotes.helper.ConventionalCommit;
import io.tjf.releasenotes.properties.ApplicationProperties;

@Configuration
public class Generator {

	private static final String BREAK_LINE = "\n";
	private static final String DOUBLE_BREAK_LINE = BREAK_LINE + BREAK_LINE;
	private static final String HORIZONTAL_RULE = BREAK_LINE + "***" + DOUBLE_BREAK_LINE;

	private final CommitService service;
	private final ApplicationProperties properties;
	private final Sections sections;

	public Generator(CommitService service, ApplicationProperties properties) {
		this.service = service;
		this.properties = properties;
		this.sections = new Sections(properties);
	}

	public void generate() throws IOException {
		String defaultBranch = this.properties.getAzure().getBranch();

		StringBuilder content = new StringBuilder();
		content.append("# ").append(properties.getTitle()).append(DOUBLE_BREAK_LINE);

		for (ApplicationProperties.Release release : this.properties.getReleases()) {
			String branch = release.getBranch();
			branch = StringUtils.isEmpty(branch) ? defaultBranch : branch;

			LocalDateTime fromDate = release.getFromDate();
			LocalDateTime toDate = release.getToDate();

			// Get the pull request commits from this release.
			List<ConventionalCommit> commits = this.service.getConventionalCommitsFromPeriod(fromDate, toDate, branch);

			// Build the map with the release sections.
			Map<Section, List<ConventionalCommit>> releaseSections = this.sections.collate(commits);

			content.append("## ").append(release.getTitle());
			content.append(generateSectionContent(releaseSections)).append(HORIZONTAL_RULE);
		}

		writeContentToFile(content.toString(), this.properties.getFile());
	}

	private StringBuilder generateSectionContent(Map<Section, List<ConventionalCommit>> releaseSections) {
		StringBuilder content = new StringBuilder();

		content.append(BREAK_LINE);

		releaseSections.forEach((section, commits) -> {
			content.append(BREAK_LINE);
			content.append("### ").append(section.toString()).append(DOUBLE_BREAK_LINE);
			commits.stream().map(this::getFormmatedCommitMessage).forEach(content::append);
		});

		return content;
	}

	private String getFormmatedCommitMessage(ConventionalCommit prCommit) {
		String message = "- ";

		String issue = prCommit.getIssue();
		String component = prCommit.getComponent();
		String breakingChange = prCommit.getBreakingChange();

		if (!StringUtils.isEmpty(component)) {
			message += component + ": ";
		}

		message += prCommit.getMessage();

		if (!StringUtils.isEmpty(issue)) {
			message += " (" + getLinkToIssue(issue) + ")";
		}

		message += BREAK_LINE;

		if (!StringUtils.isEmpty(breakingChange)) {
			message += "    * :warning: **BREAKING CHANGE:** " + breakingChange + BREAK_LINE;
		}

		return message;
	}

	private String getLinkToIssue(String issue) {
		return "[#" + issue + "](" + this.properties.getIssueLinkBaseUrl() + "/" + issue + ")";
	}

	private void writeContentToFile(String content, String path) throws IOException {
		File file = new File(path);

		// Append the file content.
		if (properties.isAppend() && file.exists()) {
			Stream<String> lines = Files.lines(file.toPath());
			content += lines.collect(Collectors.joining(BREAK_LINE));
			lines.close();
		}

		FileCopyUtils.copy(content, new FileWriter(file));
	}

}
