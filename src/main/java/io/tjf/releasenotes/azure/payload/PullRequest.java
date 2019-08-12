package io.tjf.releasenotes.azure.payload;

public class PullRequest {

	private final int pullRequestId;
	private final String status;
	private final String title;
	private final String description;

	public PullRequest(int pullRequestId, String status, String title, String description) {
		this.pullRequestId = pullRequestId;
		this.status = status;
		this.title = title;
		this.description = description;
	}

	public int getPullRequestId() {
		return pullRequestId;
	}

	public String getStatus() {
		return status;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

}
