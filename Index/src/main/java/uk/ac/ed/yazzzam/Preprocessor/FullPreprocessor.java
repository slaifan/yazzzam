package uk.ac.ed.yazzzam.Preprocessor;

import opennlp.tools.stemmer.PorterStemmer;
import uk.ac.ed.yazzzam.Indexer.Reader;
import uk.ac.ed.yazzzam.Indexer.TextFileReader;

import java.util.*;

public class FullPreprocessor implements Preprocessor {


	private PorterStemmer ps;
	private Reader r;
	Set<String> stopwords;

	public FullPreprocessor(String stopWordsFileName)  {
		ps = new PorterStemmer();
		r = new TextFileReader();
		this.stopwords = new HashSet<>(Arrays.asList(r.readFile(stopWordsFileName).split(" ")));
	}


	
	// Tokenizes a string at every non-alphanumeric character
	private List<String> tokenize(String document) {
		var tokens = new ArrayList<String>();
		document = document.replaceAll("\\s+"," ");
		var split_docs = document.split("[^\\w]+");
		
		for (int i = 0; i < split_docs.length; i++) {
			tokens.add(split_docs[i]);
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

	public List<String> preprocess(String text) {
		return stem(removeStopWords(tokenize(caseFold(text))));
	}
	
	
	
	

	
	
	
}






















