package io.tjf.releasenotes.azure.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a pull request label structure.
 * 
 * @author Rubens dos Santos Filho
 */
@AllArgsConstructor(staticName = "of")
@Getter
public class Label {

	private final String id;
	private final String name;
	private final boolean active;
	private final String url;

}
