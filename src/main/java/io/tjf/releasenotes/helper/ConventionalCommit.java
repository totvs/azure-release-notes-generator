package io.tjf.releasenotes.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a conventional commit structure.
 * 
 * @author Rubens dos Santos Filho
 */
@AllArgsConstructor(staticName = "of")
@Getter
public class ConventionalCommit {

	private final int pullRequestId;
	private final IssueType issueType;
	private final String issue;
	private final String component;
	private final String message;
	private final String breakingChange;

}
