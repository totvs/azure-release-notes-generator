package io.tjf.releasenotes.azure.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;

import io.tjf.releasenotes.azure.payload.Commit;
import io.tjf.releasenotes.azure.payload.Label;
import io.tjf.releasenotes.azure.payload.Result;
import io.tjf.releasenotes.azure.service.CommitService.CommitResult;
import io.tjf.releasenotes.properties.ApplicationProperties;

/**
 * Azure Pull Request REST API interactive class.
 * 
 * @author Rubens dos Santos Filho
 */
@Component
public class PullRequestService extends AzureService {

	private static final String PR_BASE_URI = "pullRequests";
	private static final String PR_URI = PR_BASE_URI + "/{pullRequestId}";
	private static final String PR_COMMITS_URI = PR_URI + "/commits";
	private static final String PR_LABELS_URI = PR_URI + "/labels";

	public PullRequestService(RestTemplateBuilder builder, ApplicationProperties properties) {
		super(builder, properties);
	}

	/**
	 * Returns all commits from the informed pull request.
	 * 
	 * @param pullRequestId Pull request id.
	 * @return List of pull request commits.
	 */
	public List<Commit> getPullRequestCommits(int pullRequestId) {
		return get(CommitResult.class, PR_COMMITS_URI, pullRequestId).getValue();
	}

	/**
	 * Returns all pull request labels.
	 * 
	 * @param pullRequestId Pull request id.
	 * @return List of pull request labels.
	 */
	public List<Label> getPullRequestLabels(int pullRequestId) {
		return get(PullRequestLabelsResult.class, PR_LABELS_URI, pullRequestId).getValue();
	}

	/**
	 * Returns all pull request labels names.
	 * 
	 * @param pullRequestId Pull request id.
	 * @return List of pull request labels names.
	 */
	public List<String> getPullRequestLabelsNames(int pullRequestId) {
		return getPullRequestLabels(pullRequestId).stream().map(label -> label.getName()).collect(Collectors.toList());
	}

	public static class PullRequestLabelsResult extends Result<Label> {

		public PullRequestLabelsResult(int count, List<Label> value) {
			super(count, value);
		}

	}

}
