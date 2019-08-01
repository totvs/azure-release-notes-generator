package io.tjf.releasenotes.azure.payload;

/**
 * Represents the Azure Git pull request label.
 * 
 * @author Rubens dos Santos Filho
 */
public class Label {

	private final String id;
	private final String name;
	private final boolean active;
	private final String url;

	public Label(String id, String name, boolean active, String url) {
		this.id = id;
		this.name = name;
		this.active = active;
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isActive() {
		return active;
	}

	public String getUrl() {
		return url;
	}

}
