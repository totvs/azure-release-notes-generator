package io.tjf.releasenotes.azure.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a pull request structure.
 * 
 * @author Rubens dos Santos Filho
 */
@AllArgsConstructor(staticName = "of")
@Getter
public class PullRequest {

	private final int pullRequestId;
	private final String status;
	private final String title;
	private final String description;

}
