package io.tjf.releasenotes.azure.payload;

/**
 * Represents the Azure Git commit change counts.
 * 
 * @author Rubens dos Santos Filho
 */
public class ChangeCounts {

	private final int add;
	private final int edit;
	private final int delete;

	public ChangeCounts(int add, int edit, int delete) {
		this.add = add;
		this.edit = edit;
		this.delete = delete;
	}

	public int getAdd() {
		return add;
	}

	public int getEdit() {
		return edit;
	}

	public int getDelete() {
		return delete;
	}

}
