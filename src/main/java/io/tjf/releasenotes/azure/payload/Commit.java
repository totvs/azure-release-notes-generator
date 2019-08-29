package io.tjf.releasenotes.azure.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents an Azure Git commit.
 * 
 * @author Rubens dos Santos Filho
 */
@Getter
@AllArgsConstructor
public class Commit {

	private final String commitId;
	private final String comment;
	private final String url;
	private final String remoteUrl;

}
