package io.tjf.releasenotes.helper;

import io.tjf.releasenotes.azure.payload.Commit;

public class PullRequestCommit {

	private final int pullRequestId;
	private final IssueType issueType;
	private final String issue;
	private final String message;
	private final Commit commit;

	public PullRequestCommit(int pullRequestId, IssueType issueType, String issue, String message, Commit commit) {
		this.pullRequestId = pullRequestId;
		this.issueType = issueType;
		this.issue = issue;
		this.message = message;
		this.commit = commit;
	}

	public int getPullRequestId() {
		return pullRequestId;
	}

	public IssueType getIssueType() {
		return issueType;
	}

	public String getIssue() {
		return issue;
	}

	public String getMessage() {
		return message;
	}

	public Commit getCommit() {
		return commit;
	}

}
