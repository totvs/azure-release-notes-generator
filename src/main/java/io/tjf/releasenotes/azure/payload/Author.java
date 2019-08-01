package io.tjf.releasenotes.azure.payload;

import java.time.ZonedDateTime;

/**
 * Represents the Azure Git commit author.
 * 
 * @author Rubens dos Santos Filho
 */
public class Author {

	private final String name;
	private final String email;
	private final ZonedDateTime date;

	public Author(String name, String email, ZonedDateTime date) {
		this.name = name;
		this.email = email;
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public ZonedDateTime getDate() {
		return date;
	}

}
