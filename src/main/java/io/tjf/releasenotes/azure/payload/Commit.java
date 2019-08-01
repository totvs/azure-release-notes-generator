package io.tjf.releasenotes.azure.payload;

/**
 * Represents an Azure Git commit.
 * 
 * @author Rubens dos Santos Filho
 */
public class Commit {

	private final String commitId;
	private final Author author;
	private final Author commiter;
	private String comment;
	private final boolean commentTruncated;
	private final ChangeCounts changeCounts;
	private final String url;
	private final String remoteUrl;

	public Commit(String commitId, Author author, Author commiter, String comment, boolean commentTruncated,
			ChangeCounts changeCounts, String url, String remoteUrl) {
		this.commitId = commitId;
		this.author = author;
		this.commiter = commiter;
		this.comment = comment;
		this.commentTruncated = commentTruncated;
		this.changeCounts = changeCounts;
		this.url = url;
		this.remoteUrl = remoteUrl;
	}

	public String getCommitId() {
		return commitId;
	}

	public Author getAuthor() {
		return author;
	}

	public Author getCommiter() {
		return commiter;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isCommentTruncated() {
		return commentTruncated;
	}

	public ChangeCounts getChangeCounts() {
		return changeCounts;
	}

	public String getUrl() {
		return url;
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

}
