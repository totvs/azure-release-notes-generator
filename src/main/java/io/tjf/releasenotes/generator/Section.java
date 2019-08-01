package io.tjf.releasenotes.generator;

import java.util.Arrays;
import java.util.List;

import io.tjf.releasenotes.helper.CommitUtils;
import io.tjf.releasenotes.helper.IssueType;
import io.tjf.releasenotes.helper.PullRequestCommit;

public class Section {

	private final String title;
	private final String emoji;
	private final List<String> labels;

	public Section(String title, String emoji, String... labels) {
		this(title, emoji, Arrays.asList(labels));
	}

	public Section(String title, String emoji, List<String> labels) {
		this.title = title;
		this.emoji = emoji;
		this.labels = labels;
	}

	public String getTitle() {
		return title;
	}

	public String getEmoji() {
		return emoji;
	}

	public List<String> getLabels() {
		return labels;
	}

	public boolean isMatchFor(PullRequestCommit prCommit) {
		IssueType issueType = CommitUtils.getIssueTypeFromCommitComment(prCommit.getCommit().getComment());
		return labels.stream().anyMatch(label -> issueType != null && issueType.name().equalsIgnoreCase(label));
	}

	@Override
	public String toString() {
		return emoji + " " + title;
	}

}
