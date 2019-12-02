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
import io.tjf.releasenotes.helper.ConventionalCommit;
import io.tjf.releasenotes.helper.IssueType;
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
	 * Returns all convetional commits from the given period and branch.
	 * 
	 * @param fromDate Commit start date.
	 * @param toDate   Commit end date.
	 * @param branch   Branch name.
	 * 
	 * @return List of {@link ConventionalCommit}.
	 */
	public List<ConventionalCommit> getConventionalCommitsFromPeriod(LocalDateTime fromDate, LocalDateTime toDate,
			String branch) {
		List<Commit> commits = getCommitsFromPeriod(fromDate, toDate, branch);
		List<Commit> prCommits = CommitUtils.filterPullRequestCommits(commits);

		// If the given period had PR, use them to get the convetional commits.
		if (!prCommits.isEmpty()) {
			return getConventionalCommitsFromCommits(prCommits);
		}

		// Otherwise use all the finded commits.
		return getConventionalCommitsFromCommits(commits);
	}

	/**
	 * Loads all conventional commits from the finded pull requests commits.
	 * 
	 * @param result List of pull requests commits.
	 * @return List of {@link ConventionalCommit}.
	 */
	public List<ConventionalCommit> getConventionalCommitsFromCommits(List<Commit> commits) {
		List<ConventionalCommit> conventionalCommits = new ArrayList<>();

		for (Commit commit : commits) {
			ConventionalCommit conventionalCommit = getConventionalCommit(commit);

			if (conventionalCommit != null) {
				boolean hasIssue = conventionalCommits.stream()
						.anyMatch(c -> c.getIssue().equals(conventionalCommit.getIssue()));
				
				if (!hasIssue) {
					conventionalCommits.add(conventionalCommit);
				}
			}
		}

		return conventionalCommits;
	}

	private ConventionalCommit getConventionalCommit(Commit commit) {
		var comment = commit.getComment();
		var pullRequestId = CommitUtils.getPullRequestIdFromCommitComment(comment);

		List<String> labels = getPullRequestLabels(pullRequestId);

		if (hasSkipLabelOrComment(labels, comment)) {
			return null;
		}

		var issueType = getIssueTypeFromPullRequestLabelsOrFromCommitComment(labels, comment);

		if (issueType == null) {
			return null;
		}

		var issue = CommitUtils.getIssueFromCommitComment(comment);
		var component = CommitUtils.getComponentFromCommitComment(comment);
		var message = CommitUtils.getFormmatedMessageFromCommitComment(comment);
		var description = getPullRequestDescription(pullRequestId);
		var breaking = CommitUtils.getFormmatedBreakingChangeTextFromPullRequestDescription(description);

		return new ConventionalCommit(pullRequestId, issueType, issue, component, message, breaking, commit);
	}

	private boolean isValidPullRequestId(int pullRequestId) {
		return pullRequestId != -1;
	}

	private List<String> getPullRequestLabels(int pullRequestId) {
		if (!isValidPullRequestId(pullRequestId)) {
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
		return hasSkipLabel(labels) || hasSkipComment(comment);
	}

	private boolean hasSkipLabel(List<String> labels) {
		return labels.contains("skip");
	}

	private boolean hasSkipComment(String comment) {
		return comment.endsWith("[skip]");
	}

	public static class CommitResult extends Result<Commit> {
		public CommitResult(int count, List<Commit> value) {
			super(count, value);
		}
	}

}
