package io.tjf.releasenotes.azure.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
	 * Returns all commits from the informed period and branch.
	 * 
	 * @param fromDate Commit start date.
	 * @param toDate   Commit end date.
	 * @param branch   Branch name.
	 * @return List of commits.
	 */
	public List<Commit> getCommitsFromPeriod(LocalDateTime fromDate, LocalDateTime toDate, String branch) {
		String url = COMMIT_URI + "?" + COMMIT_PERIOD_URI + "&" + COMMIT_TYPE_URI;
		return get(CommitResult.class, url, fromDate, toDate, branch).getValue();
	}

	public List<PullRequestCommit> getPullRequestCommitsFromPeriod(LocalDateTime fromDate, LocalDateTime toDate,
			String branch) {
		return getFormattedPullRequestCommits(getCommitsFromPeriod(fromDate, toDate, branch));
	}

	/**
	 * Filters and converts the commit result for pull request commits only.
	 * 
	 * @param result Commit result object.
	 * @return Pull request commit result object.
	 */
	public List<PullRequestCommit> getFormattedPullRequestCommits(List<Commit> commits) {
		List<PullRequestCommit> pullRequests = new ArrayList<>();

		// Filter for pull requests only.
		addFormmatedPullRequestCommit(pullRequests, -1, CommitUtils.filterPullRequestCommits(commits));

		return pullRequests;
	}

	private void addFormmatedPullRequestCommit(List<PullRequestCommit> prs, int parentPrId, List<Commit> commits) {
		for (Commit commit : commits) {
			String comment = commit.getComment();

			int prId = CommitUtils.getPullRequestIdFromCommitComment(comment);
			IssueType issueType;

			if (prId > -1) {
				List<String> labels = pullRequestService.getPullRequestLabelsNames(prId);
				issueType = CommitUtils.getIssueTypeFromPullRequestLabels(labels);
			} else {
				issueType = CommitUtils.getIssueTypeFromCommitComment(comment);
			}

			if (issueType == null) {
				if (prId > -1) {
					parentPrId = prId;
					List<Commit> prCommits = pullRequestService.getPullRequestCommits(parentPrId);
					addFormmatedPullRequestCommit(prs, parentPrId, prCommits);
				}

				continue;
			}

			String message = CommitUtils.getFormmatedMessageFromCommitComment(comment);
			String issue = CommitUtils.getIssueFromCommitComment(comment);

			prs.add(new PullRequestCommit(parentPrId, issueType, issue, message, commit));
		}
	}

	public static class CommitResult extends Result<Commit> {
		public CommitResult(int count, List<Commit> value) {
			super(count, value);
		}
	}

}
