package io.tjf.releasenotes.azure.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import io.tjf.releasenotes.azure.payload.Label;
import io.tjf.releasenotes.azure.payload.PullRequest;
import io.tjf.releasenotes.azure.payload.Result;
import io.tjf.releasenotes.properties.ReleaseNotesProperties;

/**
 * Azure Pull Request REST API interactive class.
 * 
 * @author Rubens dos Santos Filho
 */
@Service
public class PullRequestService extends AzureService {

	private static final String PR_BASE_URI = "pullRequests";
	private static final String PR_URI = PR_BASE_URI + "/{pullRequestId}";
	private static final String PR_LABELS_URI = PR_URI + "/labels";

	public PullRequestService(final RestTemplateBuilder builder, final ReleaseNotesProperties properties) {
		super(builder, properties);
	}

	/**
	 * Return the pull request information.
	 * 
	 * @param pullRequestId pull request id
	 * @return pull request object
	 */
	public PullRequest getPullRequest(final int pullRequestId) {
		return get(PullRequest.class, PR_URI, pullRequestId);
	}

	/**
	 * Return all pull request labels.
	 * 
	 * @param pullRequestId pull request id
	 * @return list of pull request labels
	 */
	public List<Label> getPullRequestLabels(int pullRequestId) {
		return get(PullRequestLabelsResult.class, PR_LABELS_URI, pullRequestId).getValue();
	}

	/**
	 * Return all pull request labels names.
	 * 
	 * @param pullRequestId pull request id
	 * @return list of pull request labels names
	 */
	public List<String> getPullRequestLabelsNames(int pullRequestId) {
		return getPullRequestLabels(pullRequestId).stream().map(Label::getName).collect(Collectors.toList());
	}

	public static class PullRequestLabelsResult extends Result<Label> {
		public PullRequestLabelsResult(final int count, final List<Label> value) {
			super(count, value);
		}
	}

}
