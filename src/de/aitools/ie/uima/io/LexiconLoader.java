package de.aitools.ie.uima.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * 
 * Utility class for loading <i>lexicons</i>, which are newline-separated text
 * files with unique {@link String} entries. Can be supplied with
 * {@link EntryFilter}s and {@link EntryTransformation}s, which are applied to
 * each line from the input file before it is added to the output. The default
 * constructor creates a {@link LexiconLoader} without any filters or
 * transformations. If both exist, all filters are applied before any
 * transformations; other than that, filters and transformations are applied in
 * the order they were added.
 * 
 * @author michael.voelske@uni-weimar.de
 *
 */
public class LexiconLoader {

	private List<EntryFilter> filters = new LinkedList<EntryFilter>();
	private List<EntryTransformation> transformations = new LinkedList<EntryTransformation>();

	/**
	 * Add a new {@link EntryFilter} to this {@link LexiconLoader}, and return
	 * the modified instance.
	 * 
	 * @param filter
	 * @return the {@link LexiconLoader} instance on which this method was
	 *         called.
	 */
	public LexiconLoader withFilter(EntryFilter filter) {
		filters.add(filter);
		return this;
	}

	/**
	 * Add a new {@link EntryTransformation} to this {@link LexiconLoader} and
	 * return the modified instance.
	 * 
	 * @param transformation
	 * @return the {@link LexiconLoader} instance on which this method was
	 *         called.
	 */
	public LexiconLoader withTransformation(EntryTransformation transformation) {
		transformations.add(transformation);
		return this;
	}

	/**
	 * 
	 * Loads the lexicon from the given path. The lexicon file is supposed to
	 * contain one term per line. Any filters and transformations supplied to
	 * the {@link LexiconLoader} instance are applied to each line from the
	 * input file before it is added to the returned set.
	 * 
	 * @param lexiconPath
	 *            a path to a {@link ClassLoader} resource with the lexicon
	 *            file, or a path on the local file system. If the parameter can
	 *            be interpreted as either, {@link ClassLoader} resources take
	 *            precedence
	 * @return unique lines from the input file, after any filtering and
	 *         transformation
	 */
	public Set<String> load(String lexiconPath) {
		Set<String> lexicon = new HashSet<String>();

		try (BufferedReader in = new BufferedReader(new InputStreamReader(new ResourceInputStream(lexiconPath)))) {
			addEntriesFromReader(lexicon, in);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return lexicon;
	}

	private void addEntriesFromReader(Set<String> lexicon, BufferedReader in) throws IOException {
		String str;
		while ((str = in.readLine()) != null) {
			boolean retain = true;
			for (EntryFilter f : filters) {
				if (!f.keep(str)) {
					retain = false;
					break;
				}
			}
			for (EntryTransformation t : transformations) {
				str = t.transform(str);
			}
			if (retain) {
				lexicon.add(str);
			}
		}
	}

	public static void main(String[] args) {

		// Example usage:

		Set<String> stopwords = new LexiconLoader().withFilter(new EntryFilter() {
			@Override
			public boolean keep(String line) {
				return line.toLowerCase().startsWith("k");
			}
		}).withTransformation(new EntryTransformation() {
			@Override
			public String transform(String line) {
				return line.toUpperCase();
			}
		}).withTransformation(new EntryTransformation() {
			@Override
			public String transform(String line) {
				return "\"".concat(line).concat("\" is a stopword that starts with \"k\"!");
			}
		}).load("lexicons/Features/enStopWordList.txt");
		
		// shorter Java8 version:
		/*
		Set<String> stopwords = new LexiconLoader().withFilter(line -> line.toLowerCase().startsWith("k"))
				.withTransformation(line -> line.toUpperCase())
				.withTransformation(line -> "\"".concat(line).concat("\" is a stopword that starts with \"k\"!"))
				.load("lexicons/Features/enStopWordList.txt");
		*/
		System.out.println(stopwords);
	}

}
