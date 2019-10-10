package io.tjf.releasenotes.helper;

import io.tjf.releasenotes.azure.payload.Commit;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConventionalCommit {

	private final int pullRequestId;
	private final IssueType issueType;
	private final String issue;
	private final String message;
	private final String breakingChange;
	private final Commit commit;

}
