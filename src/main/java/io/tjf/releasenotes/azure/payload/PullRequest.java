package io.tjf.releasenotes.azure.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the Azure Git pull request.
 * 
 * @author Rubens dos Santos Filho
 */
@Getter
@AllArgsConstructor
public class PullRequest {

	private final int pullRequestId;
	private final String status;
	private final String title;
	private final String description;

}
