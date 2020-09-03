package io.tjf.releasenotes.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import io.tjf.releasenotes.azure.service.CommitService;
import io.tjf.releasenotes.helper.CommitUtils;
import io.tjf.releasenotes.helper.ConventionalCommit;
import io.tjf.releasenotes.properties.ReleaseNotesProperties;

@Service
public class GeneratorService {

	private static final String BREAK_LINE = "\n";
	private static final String DOUBLE_BREAK_LINE = BREAK_LINE + BREAK_LINE;
	private static final String HORIZONTAL_RULE = BREAK_LINE + "***" + DOUBLE_BREAK_LINE;
	private static final String ISSUE_LINK = "[#%s](%s/%s)";
	private static final String PR_LINK = "[#PR %s](%s/%s)";

	private final CommitService commitService;
	private final ReleaseNotesProperties properties;
	private final Sections sections;

	public GeneratorService(final CommitService commitService, final ReleaseNotesProperties properties) {
		this.commitService = commitService;
		this.properties = properties;
		this.sections = new Sections(properties);
	}

	public void generate() throws IOException {
		var defaultBranch = properties.getAzure().getBranch();

		var content = new StringBuilder();
		content.append("# ").append(properties.getTitle()).append(DOUBLE_BREAK_LINE);

		for (ReleaseNotesProperties.Release release : properties.getReleases()) {
			var branch = release.getBranch();
			branch = StringUtils.isEmpty(branch) ? defaultBranch : branch;

			var fromDate = release.getFromDate();
			var toDate = release.getToDate();

			// Get the pull request commits from this release.
			List<ConventionalCommit> commits = commitService.getConventionalCommitsFromPeriod(fromDate, toDate, branch);

			// Build the map with the release sections.
			Map<Section, List<ConventionalCommit>> releaseSections = sections.collate(commits);

			content.append("## ").append(release.getTitle());
			content.append(generateSectionContent(releaseSections)).append(HORIZONTAL_RULE);
		}

		writeContentToFile(content.toString(), properties.getFile());
	}

	private StringBuilder generateSectionContent(final Map<Section, List<ConventionalCommit>> releaseSections) {
		var content = new StringBuilder();
		content.append(BREAK_LINE);

		releaseSections.forEach((section, commits) -> {
			content.append(BREAK_LINE);
			content.append("### ").append(section.toString()).append(DOUBLE_BREAK_LINE);
			commits.stream().map(this::getFormmatedCommitMessage).forEach(content::append);
		});

		return content;
	}

	private String getFormmatedCommitMessage(ConventionalCommit commit) {
		var message = "- ";

		var component = commit.getComponent();
		var pullRequestId = commit.getPullRequestId();
		var issue = commit.getIssue();
		var breakingChange = commit.getBreakingChange();

		if (!StringUtils.isEmpty(component))
			message += component + ": ";

		message += commit.getMessage();

		if (CommitUtils.isPullRequestIdValid(pullRequestId))
			message += " (" + getLinkToPullRequestId(pullRequestId) + ")";

		if (!StringUtils.isEmpty(issue))
			message += " (" + getLinkToIssue(issue) + ")";

		message += BREAK_LINE;

		if (!StringUtils.isEmpty(breakingChange))
			message += "    * :warning: **BREAKING CHANGE:** " + breakingChange + BREAK_LINE;

		return message;
	}

	private String getLinkToIssue(final String issue) {
		return String.format(ISSUE_LINK, issue, properties.getIssueLinkBaseUrl(), issue);
	}

	private String getLinkToPullRequestId(final int pullRequestId) {
		return String.format(PR_LINK, pullRequestId, properties.getPullRequestLinkBaseUrl(), pullRequestId);
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
