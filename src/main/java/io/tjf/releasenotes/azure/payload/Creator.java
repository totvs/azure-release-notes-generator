package io.tjf.releasenotes.azure.payload;

/**
 * Represents an Azure Git item creator.
 * 
 * @author Rubens dos Santos Filho
 */
public class Creator {
	private final String displayName;
	private final String url;
	private final String id;
	private final String uniqueName;
	private final String imageUrl;
	private final String descriptor;

	public Creator(String displayName, String url, String id, String uniqueName, String imageUrl, String descriptor) {
		this.displayName = displayName;
		this.url = url;
		this.id = id;
		this.uniqueName = uniqueName;
		this.imageUrl = imageUrl;
		this.descriptor = descriptor;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getUrl() {
		return url;
	}

	public String getId() {
		return id;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getDescriptor() {
		return descriptor;
	}

}
