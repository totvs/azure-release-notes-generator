package io.tjf.releasenotes.helper;

import java.util.Arrays;
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
	 * Filters and converts the commit result for pull request commits only.
	 * 
	 * @param result Commit result object.
	 * @return Pull request commit result object.
	 */
	public static List<Commit> filterPullRequestCommits(List<Commit> result) {
		return result.stream().filter(c -> c.getComment().indexOf(MERGED_PR_TEXT) >= 0).collect(Collectors.toList());
	}

	/**
	 * Returns the pull request id from the commit comment.
	 * 
	 * @param comment Commit comment.
	 * @return Pull request id.
	 */
	public static int getPullRequestIdFromCommitComment(String comment) {
		int pullRequestId = -1;

		if (comment.indexOf(MERGED_PR_TEXT) >= 0) {
			int len = MERGED_PR_TEXT.length();
			comment = comment.substring(comment.indexOf(MERGED_PR_TEXT) + len, comment.indexOf(':'));
			comment = allTrim(comment);
			pullRequestId = Integer.parseInt(comment);
		}

		return pullRequestId;
	}

	/**
	 * Returns the issue type from the pull request labels names.
	 * 
	 * @param labels List of pull request labels names.
	 * @return Issue type.
	 */
	public static IssueType getIssueTypeFromPullRequestLabels(List<String> labels) {
		return labels.stream().map(CommitUtils::getIssueType).filter(Objects::nonNull).findFirst().orElse(null);
	}

	/**
	 * Returns the issue type from the commit comment.
	 * 
	 * @param comment Commit comment.
	 * @return Issue type.
	 */
	public static IssueType getIssueTypeFromCommitComment(String comment) {
		IssueType issueType = null;

		if (comment.indexOf(MERGED_TEXT) >= 0) {
			comment = comment.substring(comment.indexOf(':') + 1);
		}

		if (comment.indexOf(':') >= 0) {
			comment = comment.substring(0, comment.indexOf(':'));

			if (comment.indexOf('(') >= 0) {
				comment = comment.substring(0, comment.indexOf('('));
			}

			issueType = getIssueType(allTrim(comment));
		}

		return issueType;
	}

	public static String getComponentFromCommitComment(String comment) {
		String component = "";

		if (comment.indexOf(MERGED_TEXT) >= 0) {
			comment = comment.substring(comment.indexOf(':') + 1);
		}

		if (comment.indexOf(':') >= 0) {
			comment = comment.substring(0, comment.indexOf(':'));

			if (comment.indexOf('(') >= 0) {
				component = comment.substring(comment.indexOf('(') + 1, comment.indexOf(')'));
			}
		}

		return component;
	}

	/**
	 * Returns the issue type enum value from the issue type found in the commit.
	 * 
	 * @param type Issue type found in the commit.
	 * @return Issue type.
	 */
	private static IssueType getIssueType(String type) {
		List<IssueType> types = Arrays.asList(IssueType.values());
		return types.stream().filter(t -> t.toString().equalsIgnoreCase(type)).findFirst().orElse(null);
	}

	/**
	 * Returns the formmated message from the commit comment.
	 * 
	 * @param comment Commit comment.
	 * @return Formmated commit message.
	 */
	public static String getFormmatedMessageFromCommitComment(String comment) {
		if (comment.indexOf(MERGED_TEXT) >= 0) {
			comment = comment.substring(comment.indexOf(':') + 1);
		}

		if (comment.indexOf(':') >= 0) {
			comment = comment.substring(comment.indexOf(':') + 1);
		}

		if (comment.substring(comment.length() - 1).equals(")")) {
			comment = comment.substring(0, comment.lastIndexOf('('));
		}

		return capitalizeFirstLetter(allTrim(comment));
	}

	/**
	 * Return the issue code from the commit comment.
	 * 
	 * @param comment Commit comment.
	 * @return Issue code.
	 */
	public static String getIssueFromCommitComment(String comment) {
		if (!comment.substring(comment.length() - 1).equals(")")) {
			return "";
		}

		return comment.substring(comment.lastIndexOf('(') + 1, comment.lastIndexOf(')'));
	}

	/**
	 * Returns the breaking change text from the pull request description (if
	 * available).
	 * 
	 * @param description Pull request description.
	 * @return Breaking change text.
	 */
	public static String getFormmatedBreakingChangeTextFromPullRequestDescription(String description) {
		String text = "";

		if (!description.contains(BREAKING_CHANGE_PREFIX)) {
			return text;
		}

		text = description.substring(description.indexOf(BREAKING_CHANGE_PREFIX) + BREAKING_CHANGE_PREFIX.length());
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
