package io.tjf.releasenotes.helper;

public enum IssueType {

	FEAT("feat"), FIX("fix"), DOC("doc"), DOCS("docs"), STYLE("style"), REFACTOR("refactor"), PERF("perf"),
	TEST("test"), CHORE("chore");

	private final String type;

	private IssueType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static IssueType safeValueOf(final String value) {
		try {
			return IssueType.valueOf(value.toUpperCase());
		} catch (Exception e) {
			return null;
		}
	}

}
