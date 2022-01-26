package uk.ac.ed.yazzzam.Preprocessor;

import opennlp.tools.stemmer.PorterStemmer;
import uk.ac.ed.yazzzam.Indexer.Reader;
import uk.ac.ed.yazzzam.Indexer.TextFileReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FullProcessor implements Preprocessor {


	private String stopWordsFileName;
	private PorterStemmer ps = new PorterStemmer();
	private Reader r = new TextFileReader();
	
	
	public FullProcessor(String stopWordsFileName)  {
		this.stopWordsFileName = stopWordsFileName;
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
		var stopwords = Arrays.asList(r.readFile(stopWordsFileName).split(" "));
		
		for (String word: stopwords) {
			tokens.removeAll(Collections.singleton(word));
		}
		
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






















