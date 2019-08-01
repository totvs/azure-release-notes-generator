package io.tjf.releasenotes.azure.payload;

import java.util.List;

/**
 * Represents an Azure result object.
 * 
 * @author Rubens dos Santos Filho
 */
public class Result<T> {

	private final int count;
	private final List<T> value;

	public Result(int count, List<T> value) {
		this.count = count;
		this.value = value;
	}

	public int getCount() {
		return count;
	}

	public List<T> getValue() {
		return value;
	}

}
