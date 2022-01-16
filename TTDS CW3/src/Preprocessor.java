import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import opennlp.tools.stemmer.PorterStemmer;


public class Preprocessor {

	// Tokenizes a string at every non-alphanumeric character
	private ArrayList<String> tokenize(String document) {
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
	private ArrayList<String> removeStopWords(List<String> tokens) throws FileNotFoundException {
		var stopwords = Arrays.asList(Reader.readFile("englishST.txt").split(" "));
		
		for (String word: stopwords) {
			tokens.removeAll(Collections.singleton(word));
		}
		
		return (ArrayList<String>) tokens;
	}
	
	private ArrayList<String> stem(ArrayList<String> tokens){
		PorterStemmer ps = new PorterStemmer();
		
		
		for (int i = 0; i < tokens.size(); i++) {
			tokens.set(i, ps.stem(tokens.get(i)));
		}
		
		return tokens;
	}
	
	
	public ArrayList<String> preprocess(String document) throws FileNotFoundException {
		var tokens = stem(removeStopWords(tokenize(caseFold(document))));
		return tokens;
	}
	
	
	
	
}






















