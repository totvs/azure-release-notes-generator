package io.tjf.releasenotes.azure.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the Azure Git pull request label.
 * 
 * @author Rubens dos Santos Filho
 */
@Getter
@AllArgsConstructor
public class Label {

	private final String id;
	private final String name;
	private final boolean active;
	private final String url;

}
