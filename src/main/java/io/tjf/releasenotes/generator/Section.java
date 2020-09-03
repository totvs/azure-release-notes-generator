package io.tjf.releasenotes.generator;

import java.util.Arrays;
import java.util.List;

import io.tjf.releasenotes.helper.IssueType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(staticName = "of")
@Getter
public class Section {

	private final String title;
	private final String emoji;
	private final List<String> labels;

	public Section(String title, String emoji, String... labels) {
		this(title, emoji, Arrays.asList(labels));
	}

	public boolean isMatchFor(IssueType issueType) {
		return labels.stream().anyMatch(label -> issueType.name().equalsIgnoreCase(label));
	}

	@Override
	public String toString() {
		return emoji + " " + title;
	}

}
