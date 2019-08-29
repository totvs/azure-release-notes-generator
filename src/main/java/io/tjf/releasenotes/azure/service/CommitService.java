package io.tjf.releasenotes.azure.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;

import io.tjf.releasenotes.azure.payload.Commit;
import io.tjf.releasenotes.azure.payload.Result;
import io.tjf.releasenotes.helper.CommitUtils;
import io.tjf.releasenotes.helper.IssueType;
import io.tjf.releasenotes.helper.PullRequestCommit;
import io.tjf.releasenotes.properties.ApplicationProperties;

/**
 * Azure Commit REST API interactive class.
 * 
 * @author Rubens dos Santos Filho
 */
@Component
public class CommitService extends AzureService {

	private static final String COMMIT_URI = "commits";
	private static final String COMMIT_PERIOD_URI = "searchCriteria.fromDate={fromDate}&searchCriteria.toDate={toDate}";
	private static final String COMMIT_TYPE_URI = "searchCriteria.itemVersion.versionType=branch&searchCriteria.itemVersion.version={branch}";

	private final PullRequestService pullRequestService;

	public CommitService(RestTemplateBuilder builder, ApplicationProperties properties,
			PullRequestService pullRequestService) {
		super(builder, properties);
		this.pullRequestService = pullRequestService;
	}

	/**
	 * Returns all commits from the given period and branch.
	 * 
	 * @param fromDate Commit start date.
	 * @param toDate   Commit end date.
	 * @param branch   Branch name.
	 * @return List of commits.
	 */
	public List<Commit> getCommitsFromPeriod(LocalDateTime fromDate, LocalDateTime toDate, String branch) {
		var url = COMMIT_URI + "?" + COMMIT_PERIOD_URI + "&" + COMMIT_TYPE_URI;
		return get(CommitResult.class, url, fromDate, toDate, branch).getValue();
	}

	/**
	 * Returns all pull requests commits from the given period and branch.
	 * 
	 * @param fromDate Commit start date.
	 * @param toDate   Commit end date.
	 * @param branch   Branch name.
	 * 
	 * @return List of pull requests commits.
	 */
	public List<PullRequestCommit> getPullRequestCommitsFromPeriod(LocalDateTime fromDate, LocalDateTime toDate,
			String branch) {
		return getFormattedPullRequestCommits(getCommitsFromPeriod(fromDate, toDate, branch));
	}

	/**
	 * Filters and converts pull requests commits only.
	 * 
	 * @param result Commit result object.
	 * @return List of pull requests commits.
	 */
	public List<PullRequestCommit> getFormattedPullRequestCommits(List<Commit> commits) {
		List<PullRequestCommit> pullRequests = new ArrayList<>();

		// Filter for pull requests only.
		addFormmatedPullRequestCommit(pullRequests, commits);

		return pullRequests;
	}

	private void addFormmatedPullRequestCommit(List<PullRequestCommit> pullRequests, List<Commit> commits) {
		for (Commit commit : commits) {
			var comment = commit.getComment();

			var pullRequestId = CommitUtils.getPullRequestIdFromCommitComment(comment);
			List<String> labels = getPullRequestLabels(pullRequestId);

			if (hasSkipLabelOrComment(labels, comment)) {
				continue;
			}

			var issueType = getIssueTypeFromPullRequestLabelsOrFromCommitComment(labels, comment);

			if (issueType != null) {
				var issue = CommitUtils.getIssueFromCommitComment(comment);

				if (pullRequestListAlreadyHasIssue(pullRequests, issue)) {
					continue;
				}

				var message = CommitUtils.getFormmatedMessageFromCommitComment(comment);
				var description = getPullRequestDescription(pullRequestId);
				var breaking = CommitUtils.getFormmatedBreakingChangeTextFromPullRequestDescription(description);

				pullRequests.add(new PullRequestCommit(pullRequestId, issueType, issue, message, breaking, commit));
			}
		}
	}

	private boolean isValidPullRequestId(int pullRequestId) {
		return pullRequestId != -1;
	}

	private List<String> getPullRequestLabels(int pullRequestId) {
		if (isValidPullRequestId(pullRequestId)) {
			return Collections.emptyList();
		}

		return pullRequestService.getPullRequestLabelsNames(pullRequestId);
	}

	private String getPullRequestDescription(int pullRequestId) {
		if (isValidPullRequestId(pullRequestId)) {
			return "";
		}

		return pullRequestService.getPullRequest(pullRequestId).getDescription();
	}

	private IssueType getIssueTypeFromPullRequestLabelsOrFromCommitComment(List<String> labels, String comment) {
		if (!labels.isEmpty()) {
			return CommitUtils.getIssueTypeFromPullRequestLabels(labels);
		}

		return CommitUtils.getIssueTypeFromCommitComment(comment);
	}

	private boolean hasSkipLabelOrComment(List<String> labels, String comment) {
		return labels.contains("skip") || comment.endsWith("[skip]");
	}

	private boolean pullRequestListAlreadyHasIssue(List<PullRequestCommit> pullRequests, String issue) {
		boolean hasIssue = false;

		for (PullRequestCommit pullRequest : pullRequests) {
			if (pullRequest.getIssue().equals(issue)) {
				hasIssue = true;
				break;
			}
		}

		return hasIssue;
	}

	public static class CommitResult extends Result<Commit> {
		public CommitResult(int count, List<Commit> value) {
			super(count, value);
		}
	}

}
