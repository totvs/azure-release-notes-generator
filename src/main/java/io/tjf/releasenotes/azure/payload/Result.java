package io.tjf.releasenotes.azure.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents an Azure result object.
 * 
 * @author Rubens dos Santos Filho
 */
@Getter
@AllArgsConstructor
public class Result<T> {

	private final int count;
	private final List<T> value;

}
