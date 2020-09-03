package io.tjf.releasenotes.helper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.tjf.releasenotes.azure.payload.Commit;

/**
 * An utility class for commits information manipulation.
 * 
 * @author Rubens dos Santos Filho
 */
public class CommitUtils {

	private static final String MERGED_TEXT = "Merged";
	private static final String MERGED_PR_TEXT = MERGED_TEXT + " PR";
	private static final String BREAKING_CHANGE_PREFIX = "BREAKING CHANGE:";

	private CommitUtils() {
	}

	/**
	 * Filter and convert the commit result to pull request commits only.
	 * 
	 * @param result commit result object
	 * @return pull request commit result object
	 */
	public static List<Commit> filterPullRequestCommits(final List<Commit> result) {
		return result.stream().filter(c -> c.getComment().indexOf(MERGED_PR_TEXT) >= 0).collect(Collectors.toList());
	}

	/**
	 * Return the pull request id from the commit comment.
	 * 
	 * @param comment commit comment
	 * @return pull request id
	 */
	public static int getPullRequestIdFromCommitComment(String comment) {
		var pullRequestId = -1;

		if (comment.indexOf(MERGED_PR_TEXT) >= 0) {
			var len = MERGED_PR_TEXT.length();
			comment = comment.substring(comment.indexOf(MERGED_PR_TEXT) + len, comment.indexOf(':'));
			comment = allTrim(comment);
			pullRequestId = Integer.parseInt(comment);
		}

		return pullRequestId;
	}

	/**
	 * Return {@code true} if the pull request id is valid.
	 * 
	 * @param pullRequestId pull request id
	 * @return {@code true} if the pull request id is valid
	 */
	public static boolean isPullRequestIdValid(int pullRequestId) {
		return pullRequestId != -1;
	}

	/**
	 * Return the issue type from the pull request labels names.
	 * 
	 * @param labels list of pull request labels names
	 * @return issue type
	 */
	public static IssueType getIssueTypeFromPullRequestLabels(final List<String> labels) {
		return labels.stream().map(IssueType::safeValueOf).filter(Objects::nonNull).findFirst().orElse(null);
	}

	/**
	 * Return the issue type from the commit comment.
	 * 
	 * @param comment commit comment
	 * @return issue type
	 */
	public static IssueType getIssueTypeFromCommitComment(String comment) {
		IssueType issueType = null;

		// From: "Merged PR: fix(component): a bug fix"
		// To: "fix(component): a bug fix"
		if (comment.indexOf(MERGED_TEXT) >= 0)
			comment = comment.substring(comment.indexOf(':') + 1);

		if (comment.indexOf(':') >= 0) {
			// From: "fix(component): a bug fix"
			// To: "fix(component)"
			comment = comment.substring(0, comment.indexOf(':'));

			// From: "fix(component)"
			// To: "fix"
			if (comment.indexOf('(') >= 0)
				comment = comment.substring(0, comment.indexOf('('));

			issueType = IssueType.safeValueOf(allTrim(comment));
		}

		return issueType;
	}

	/**
	 * Return the component name from the commit comment.
	 * 
	 * @param comment commit comment
	 * @return component name
	 */
	public static String getComponentFromCommitComment(String comment) {
		var component = "";

		// From: "Merged PR: fix(component): a bug fix"
		// To: "fix(component): a bug fix"
		if (comment.indexOf(MERGED_TEXT) >= 0)
			comment = comment.substring(comment.indexOf(':') + 1);

		if (comment.indexOf(':') >= 0) {
			// From: "fix(component): a bug fix"
			// To: "fix(component)"
			comment = comment.substring(0, comment.indexOf(':'));

			// From: "fix(component)"
			// To: "component"
			if (comment.indexOf('(') >= 0)
				component = comment.substring(comment.indexOf('(') + 1, comment.indexOf(')'));
		}

		return component.toLowerCase();
	}

	/**
	 * Return the formated message from the commit comment.
	 * 
	 * @param comment commit comment
	 * @return formated commit message
	 */
	public static String getFormmatedMessageFromCommitComment(String comment) {
		// From: "Merged PR: fix(component): a bug fix. (ISSUE-123)"
		// To: "fix(component): a bug fix. (ISSUE-123)"
		if (comment.indexOf(MERGED_TEXT) >= 0)
			comment = comment.substring(comment.indexOf(':') + 1);

		// From: "fix(component): a bug fix. (ISSUE-123)"
		// To: " a bug fix. (ISSUE-123)"
		if (comment.indexOf(':') >= 0)
			comment = comment.substring(comment.indexOf(':') + 1);

		// From: " a bug fix. (ISSUE-123)"
		// To: " a bug fix."
		if (comment.substring(comment.length() - 1).equals(")"))
			comment = comment.substring(0, comment.lastIndexOf('('));

		// From: " a bug fix."
		// To: "A bug fix."
		return capitalizeFirstLetter(allTrim(comment));
	}

	/**
	 * Return the issue code from the commit comment.
	 * 
	 * @param comment commit comment
	 * @return issue code
	 */
	public static String getIssueFromCommitComment(String comment) {
		if (!comment.substring(comment.length() - 1).equals(")"))
			return "";

		return comment.substring(comment.lastIndexOf('(') + 1, comment.lastIndexOf(')')).toUpperCase();
	}

	/**
	 * Return the breaking change text from the pull request description - if
	 * available.
	 * 
	 * @param description pull request description.
	 * @return formated breaking change text.
	 */
	public static String getFormmatedBreakingChangeTextFromPullRequestDescription(String description) {
		if (description == null)
			return "";

		if (!description.contains(BREAKING_CHANGE_PREFIX))
			return "";

		var text = description.substring(description.indexOf(BREAKING_CHANGE_PREFIX) + BREAKING_CHANGE_PREFIX.length());
		text = capitalizeFirstLetter(allTrim(text));

		return text;
	}

	private static String allTrim(String text) {
		return text.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
	}

	private static String capitalizeFirstLetter(String text) {
		return text.substring(0, 1).toUpperCase() + text.substring(1);
	}

}
