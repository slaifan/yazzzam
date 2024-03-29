package uk.ac.ed.yazzzam.Preprocessor;

import opennlp.tools.stemmer.PorterStemmer;
import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.language.Soundex;

import uk.ac.ed.yazzzam.GlobalSettings;
import uk.ac.ed.yazzzam.Indexer.Reader;
import uk.ac.ed.yazzzam.Indexer.TextFileReader;

import java.util.*;

public class FullPreprocessor implements Preprocessor {


	private PorterStemmer ps;
	private Reader r;
	private Soundex sound;
	private Metaphone mp;
	Set<String> stopwords;

	public FullPreprocessor(String stopWordsFileName)  {
		ps = new PorterStemmer();
		r = new TextFileReader();
		sound = new Soundex();
		mp = new Metaphone();
		this.stopwords = new HashSet<>(Arrays.asList(r.readFile(stopWordsFileName).split(" ")));
	}


	// Tokenizes a string at every non-alphanumeric character
	private ArrayList<String> tokenize(String document) {
		var tokens = new ArrayList<String>();
		document = document.replaceAll("\\s+"," ");
		var split_docs = document.split("[^\\w]+");

		for (String split_doc : split_docs) {
			tokens.add(split_doc);
		}
		
		return tokens;
	}
	
	
	// Case-folding
	private String caseFold(String document){
		return document.toLowerCase();
	}
	
	// Stopping
	private ArrayList<String> removeStopWords(List<String> tokens) {

		tokens.removeIf(t -> stopwords.contains(t));
		return (ArrayList<String>) tokens;
	}
	
	
	private List<String> stem(ArrayList<String> tokens){
		for (int i = 0; i < tokens.size(); i++) {
			tokens.set(i, ps.stem(tokens.get(i)));
		}
		return tokens;
	}

	private List<String> soundex(ArrayList<String> tokens){
		for (int i = 0; i < tokens.size(); i++) {
			tokens.set(i, sound.encode(tokens.get(i)));
		}
		return tokens;
	}

	private List<String> metaphone(ArrayList<String> tokens){
		for (int i = 0; i < tokens.size(); i++) {
			tokens.set(i, mp.encode(tokens.get(i)));
		}
		return tokens;
	}

//	@Override
//	public List<String> preprocess(String text) {
//		return stem(removeStopWords(tokenize(caseFold(text))));
//	}

	@Override
	public List<String> preprocess(String text) {

		if (GlobalSettings.preprocessorMode.equals("stem"))
			return stem(removeStopWords(tokenize(caseFold(text))));
		else if (GlobalSettings.preprocessorMode.equals("soundex"))
			return soundex(tokenize(caseFold(text)));
		else
			return metaphone(tokenize(caseFold(text)));
	}

	

	
	
	
}






















