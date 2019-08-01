package io.tjf.releasenotes.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import io.tjf.releasenotes.azure.service.CommitService;
import io.tjf.releasenotes.helper.PullRequestCommit;
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
		content.append("# " + properties.getTitle());
		content.append(DOUBLE_BREAK_LINE);

		for (ApplicationProperties.Release release : this.properties.getReleases()) {
			String branch = release.getBranch();
			branch = StringUtils.isEmpty(branch) ? defaultBranch : branch;

			LocalDateTime fromDate = release.getFromDate();
			LocalDateTime toDate = release.getToDate();

			// Get the pull request commits from this release.
			List<PullRequestCommit> commits = this.service.getPullRequestCommitsFromPeriod(fromDate, toDate, branch);

			// Build the map with the release sections.
			Map<Section, List<PullRequestCommit>> releaseSections = this.sections.collate(commits);

			content.append("## " + release.getTitle());
			content.append(generateSectionContent(releaseSections));
			content.append(content.length() > 0 ? HORIZONTAL_RULE : "");
		}

		writeContentToFile(content.toString(), this.properties.getFile());
	}

	private StringBuilder generateSectionContent(Map<Section, List<PullRequestCommit>> releaseSections) {
		StringBuilder content = new StringBuilder();

		content.append(BREAK_LINE);

		releaseSections.forEach((section, commits) -> {
			content.append(BREAK_LINE);
			content.append("### " + section.toString() + DOUBLE_BREAK_LINE);
			commits.stream().map(this::getFormmatedCommitMessage).forEach(content::append);
		});

		return content;
	}

	private String getFormmatedCommitMessage(PullRequestCommit prCommit) {
		String issue = prCommit.getIssue();
		String issueLink = "";

		if (!StringUtils.isEmpty(issue)) {
			issueLink = " (" + getLinkToIssue(issue) + ")";
		}

		return "- " + prCommit.getMessage() + issueLink + BREAK_LINE;
	}

	private String getLinkToIssue(String issue) {
		return "[#" + issue + "](" + this.properties.getIssueLinkBaseUrl() + "/" + issue + ")";
	}

	private void writeContentToFile(String content, String path) throws IOException {
		FileCopyUtils.copy(content, new FileWriter(new File(path)));
	}

}
