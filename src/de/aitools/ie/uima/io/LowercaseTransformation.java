package de.aitools.ie.uima.io;

public class LowercaseTransformation implements EntryTransformation {

	@Override
	public String transform(String entry) {
		return entry.toLowerCase();
	}

}
