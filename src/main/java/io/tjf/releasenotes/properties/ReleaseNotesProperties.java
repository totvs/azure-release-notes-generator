package io.tjf.releasenotes.properties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "releasenotes")
@Validated
public class ReleaseNotesProperties {

	/**
	 * Document title.
	 */
	@NotBlank
	private String title;

	/**
	 * Release notes file destination.
	 */
	@NotBlank
	private String file;

	/**
	 * {@code true} to append the content into the file.
	 */
	private boolean append = false;

	/**
	 * Base URL link for the issues.
	 */
	@URL
	@NotBlank
	private String issueLinkBaseUrl;

	/**
	 * Azure properties.
	 */
	private final Azure azure = new Azure();

	/**
	 * Section definitions in the order that they should appear.
	 */
	private final List<Section> sections = new ArrayList<>();

	/**
	 * Relase notes properties.
	 */
	@NotEmpty
	private final List<Release> releases = new ArrayList<>();

	public String getPullRequestLinkBaseUrl() {
		var organization = getAzure().getOrganization();
		var project = getAzure().getProject();
		var repository = getAzure().getRepository();
		return String.format("https://%s.visualstudio.com/%s/_git/%s/pullrequest", organization, project, repository);
	}

	/**
	 * Azure properties
	 */
	@Getter
	@Setter
	public static class Azure {

		/**
		 * Azure username.
		 */
		@NotBlank
		private String username;

		/**
		 * Azure password.
		 */
		@NotBlank
		private String password;

		/**
		 * Azure organization name.
		 */
		@NotBlank
		private String organization;

		/**
		 * Azure project name.
		 */
		@NotBlank
		private String project;

		/**
		 * Azure git source repository name.
		 */
		@NotBlank
		private String repository;

		/**
		 * Azure git branch name.
		 */
		private String branch = "master";

	}

	/**
	 * Releases properties.
	 */
	@Getter
	@Setter
	public static class Release {

		/**
		 * Release title.
		 */
		@NotBlank
		private String title;

		/**
		 * Release start date.
		 */
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
		private LocalDateTime fromDate;

		/**
		 * Release end date.
		 */
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
		private LocalDateTime toDate;

		/**
		 * Release branch name.
		 */
		private String branch;

	}

	/**
	 * Release notes sections properties.
	 */
	@Getter
	@Setter
	public static class Section {

		/**
		 * Section title.
		 */
		@NotBlank
		private String title;

		/**
		 * Section emoji character.
		 */
		@NotBlank
		private String emoji;

		/**
		 * Section identifying labels.
		 */
		@NotEmpty
		private List<String> labels = new ArrayList<>();

	}

}
