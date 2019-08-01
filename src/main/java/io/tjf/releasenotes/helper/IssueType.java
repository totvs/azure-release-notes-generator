package io.tjf.releasenotes.helper;

public enum IssueType {

	FEAT("feat"), FIX("fix"), DOC("doc"), DOCS("docs"), STYLE("style"), REFACTOR("refactor"), PERF("parf"),
	TEST("test"), CHORE("chore");

	private final String type;

	private IssueType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
