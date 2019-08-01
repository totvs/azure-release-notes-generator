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

@Validated
@ConfigurationProperties(prefix = "releasenotes")
public class ApplicationProperties {

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
	 * Base URL link for the issues.
	 */
	@URL
	@NotBlank
	private String issueLinkBaseUrl;

	/**
	 * Azure Devops properties.
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getFile() {
		return file;
	}

	public void setIssueLinkBaseUrl(String issueLinkBaseUrl) {
		this.issueLinkBaseUrl = issueLinkBaseUrl;
	}

	public String getIssueLinkBaseUrl() {
		return issueLinkBaseUrl;
	}

	public Azure getAzure() {
		return azure;
	}

	public List<Section> getSections() {
		return this.sections;
	}

	public List<Release> getReleases() {
		return releases;
	}

	/**
	 * Azure Devops properties
	 */
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
		 * Azure instance name.
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

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getOrganization() {
			return organization;
		}

		public void setOrganization(String organization) {
			this.organization = organization;
		}

		public String getProject() {
			return project;
		}

		public void setProject(String project) {
			this.project = project;
		}

		public String getRepository() {
			return repository;
		}

		public void setRepository(String repository) {
			this.repository = repository;
		}

		public String getBranch() {
			return branch;
		}

		public void setBranch(String branch) {
			this.branch = branch;
		}

	}

	/**
	 * Releases properties.
	 */
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

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public LocalDateTime getFromDate() {
			return fromDate;
		}

		public void setFromDate(LocalDateTime fromDate) {
			this.fromDate = fromDate;
		}

		public LocalDateTime getToDate() {
			return toDate;
		}

		public void setToDate(LocalDateTime toDate) {
			this.toDate = toDate;
		}

		public String getBranch() {
			return branch;
		}

		public void setBranch(String branch) {
			this.branch = branch;
		}

	}

	/**
	 * Release notes sections properties.
	 */
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

		public String getTitle() {
			return this.title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getEmoji() {
			return this.emoji;
		}

		public void setEmoji(String emoji) {
			this.emoji = emoji;
		}

		public List<String> getLabels() {
			return this.labels;
		}

		public void setLabels(List<String> labels) {
			this.labels = labels;
		}

	}

}
