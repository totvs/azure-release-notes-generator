package io.tjf.releasenotes.azure.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import io.tjf.releasenotes.azure.payload.Commit;
import io.tjf.releasenotes.azure.payload.Result;
import io.tjf.releasenotes.helper.CommitUtils;
import io.tjf.releasenotes.helper.ConventionalCommit;
import io.tjf.releasenotes.helper.IssueType;
import io.tjf.releasenotes.properties.ReleaseNotesProperties;

/**
 * Azure Commit REST API interactive class.
 * 
 * @author Rubens dos Santos Filho
 */
@Service
public class CommitService extends AzureService {

	private static final String COMMIT_URI = "commits";
	private static final String COMMIT_PERIOD_URI = "searchCriteria.fromDate={fromDate}&searchCriteria.toDate={toDate}";
	private static final String COMMIT_TYPE_URI = "searchCriteria.itemVersion.versionType=branch&searchCriteria.itemVersion.version={branch}";

	private final PullRequestService pullRequestService;

	public CommitService(final RestTemplateBuilder builder, final ReleaseNotesProperties properties,
			final PullRequestService pullRequestService) {
		super(builder, properties);
		this.pullRequestService = pullRequestService;
	}

	/**
	 * Return all commits from the given branch and period.
	 * 
	 * @param fromDate commit start date
	 * @param toDate   commit end date
	 * @param branch   branch name
	 * 
	 * @return list of commits
	 */
	public List<Commit> getCommitsFromPeriod(LocalDateTime fromDate, LocalDateTime toDate, String branch) {
		var url = COMMIT_URI + "?" + COMMIT_PERIOD_URI + "&" + COMMIT_TYPE_URI;
		return get(CommitResult.class, url, fromDate, toDate, branch).getValue();
	}

	/**
	 * Return all conventional commits from the given period and branch.
	 * 
	 * @param fromDate commit start date
	 * @param toDate   commit end date
	 * @param branch   branch name
	 * 
	 * @return list of {@link ConventionalCommit}
	 */
	public List<ConventionalCommit> getConventionalCommitsFromPeriod(final LocalDateTime fromDate,
			final LocalDateTime toDate, final String branch) {
		List<Commit> commits = getCommitsFromPeriod(fromDate, toDate, branch);
		List<Commit> prCommits = CommitUtils.filterPullRequestCommits(commits);

		// If the given period had PR, use them to get the conventional commits.
		// This is necessary because the PR REST API doesn't have a way to
		// filter commits by period.
		if (!prCommits.isEmpty())
			return getConventionalCommitsFromCommits(prCommits);

		// Otherwise use all the found commits.
		return getConventionalCommitsFromCommits(commits);
	}

	private List<ConventionalCommit> getConventionalCommitsFromCommits(final List<Commit> commits) {
		List<ConventionalCommit> conventionalCommits = new ArrayList<>();

		for (Commit commit : commits) {
			ConventionalCommit conventionalCommit = getConventionalCommit(commit);

			if (conventionalCommit != null) {
				var hasIssue = conventionalCommits.stream()
						.anyMatch(c -> c.getIssue().equals(conventionalCommit.getIssue()));

				if (!hasIssue)
					conventionalCommits.add(conventionalCommit);
			}
		}

		return conventionalCommits;
	}

	private ConventionalCommit getConventionalCommit(Commit commit) {
		var comment = commit.getComment();
		var pullRequestId = CommitUtils.getPullRequestIdFromCommitComment(comment);

		List<String> labels = getPullRequestLabels(pullRequestId);

		if (hasSkipLabelOrComment(labels, comment))
			return null;

		var issueType = getIssueTypeFromPullRequestLabelsOrFromCommitComment(labels, comment);

		if (issueType == null)
			return null;

		var issue = CommitUtils.getIssueFromCommitComment(comment);
		var component = CommitUtils.getComponentFromCommitComment(comment);
		var message = CommitUtils.getFormmatedMessageFromCommitComment(comment);
		var description = getPullRequestDescription(pullRequestId);
		var breakingChange = CommitUtils.getFormmatedBreakingChangeTextFromPullRequestDescription(description);

		return ConventionalCommit.of(pullRequestId, issueType, issue, component, message, breakingChange);
	}

	private List<String> getPullRequestLabels(int pullRequestId) {
		if (!CommitUtils.isPullRequestIdValid(pullRequestId))
			return Collections.emptyList();

		return pullRequestService.getPullRequestLabelsNames(pullRequestId);
	}

	private String getPullRequestDescription(int pullRequestId) {
		if (!CommitUtils.isPullRequestIdValid(pullRequestId))
			return "";

		return pullRequestService.getPullRequest(pullRequestId).getDescription();
	}

	private IssueType getIssueTypeFromPullRequestLabelsOrFromCommitComment(List<String> labels, String comment) {
		if (!labels.isEmpty())
			return CommitUtils.getIssueTypeFromPullRequestLabels(labels);

		return CommitUtils.getIssueTypeFromCommitComment(comment);
	}

	private boolean hasSkipLabelOrComment(List<String> labels, String comment) {
		return hasSkipLabel(labels) || hasSkipComment(comment);
	}

	private boolean hasSkipLabel(List<String> labels) {
		return labels.stream().map(String::toLowerCase).collect(Collectors.toList()).contains("skip");
	}

	private boolean hasSkipComment(String comment) {
		return comment.toLowerCase().contains("skip:");
	}

	public static class CommitResult extends Result<Commit> {
		public CommitResult(final int count, final List<Commit> value) {
			super(count, value);
		}
	}

}
