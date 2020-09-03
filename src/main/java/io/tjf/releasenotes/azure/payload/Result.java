package io.tjf.releasenotes.azure.payload;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a result object.
 * 
 * @author Rubens dos Santos Filho
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class Result<T> {

	private final int count;
	private final List<T> value;

}
