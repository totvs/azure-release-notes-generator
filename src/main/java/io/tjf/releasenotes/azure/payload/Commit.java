package io.tjf.releasenotes.azure.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a git commit structure.
 * 
 * @author Rubens dos Santos Filho
 */
@AllArgsConstructor(staticName = "of")
@Getter
public class Commit {

	private final String commitId;
	private final String comment;
	private final String url;
	private final String remoteUrl;

}
