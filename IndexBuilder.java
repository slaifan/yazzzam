import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;




public class IndexBuilder {

	private HashMap<Integer, ArrayList<String>> songID_to_lyrics;	// Maps the song ID to the preprocessed lyrics
	
	
	public IndexBuilder() {
		songID_to_lyrics = new HashMap<>();
	}
	
	private FullProcessor p = new FullProcessor("englishST.txt");
	
	
	
	
//	// Preprocesses an entire document and adds it to the songID_to_lyrics hashmap
	public void preprocess_documents(String documents) throws FileNotFoundException{
		
		var all_lyrics = Arrays.asList(documents.split("Title: "));		// Split the document by title		
		
		var preprocessed_lyrics = new ArrayList<ArrayList<String>>();
		
		
	
		// Pre-process all the lyrics
		for (String song: all_lyrics) {
			preprocessed_lyrics.add(p.preprocess(song));
		}
		
		preprocessed_lyrics.remove(0);		// First element is an empty list
		
		for (int i = 0; i < preprocessed_lyrics.size(); i++) {
			songID_to_lyrics.put(i, preprocessed_lyrics.get(i));
		}
		
	}
	
	
	// Preprocesses a single song and adds it to the songID_to_lyrics hashmap
	public void preprocess_song(String song) throws FileNotFoundException {
		
		var lyrics = String.join(" ", Arrays.asList(song.split("Title: ")));
		
		
		songID_to_lyrics.put(songID_to_lyrics.size() - 1, p.preprocess(lyrics));
		
	}
	
	
	// Returns the hashmap of songID to preprocessed lyrics
	public HashMap<Integer, ArrayList<String>> getMap(){
		return songID_to_lyrics;
	}
	
	
	
	// Returns the list of song IDs that contain the given term
	public ArrayList<Integer> list_of_documents(String term, HashMap<Integer, ArrayList<String>> preprocessed_data) {
		
		var docs = new ArrayList<Integer>();
		
		for (int id: preprocessed_data.keySet()) {
			if (preprocessed_data.get(id).contains(term)) {
				docs.add(id);
			}
		}
		
		return docs;
	}
	
	// Returns the positions of the occurrences of the term in the lyrics
	public ArrayList<Integer> list_of_positions(String term, ArrayList<String> tokenized_lyrics){
		var positions = new ArrayList<Integer>();
		for (int i = 0; i < tokenized_lyrics.size(); i++) {
			String element = tokenized_lyrics.get(i);
			
			if (term.equalsIgnoreCase(element)) {
				positions.add(i);
			}
		}
		return positions;
	}
	
	
	// How often the term occurs in the given song lyrics
	public int term_frequency(String term, ArrayList<String> tokenized_lyrics) {
		
		return Collections.frequency(tokenized_lyrics, term);
		
	}
	
	
	// Generates a TermData object for the given term
	public TermData getTermData(String term) throws FileNotFoundException {
		var list_of_docs = this.list_of_documents(term, songID_to_lyrics);
		var df = list_of_docs.size();	// Document frequency
		int tf = 0;		// The total number of times a term occurs in the corpus
		var occurrences = new HashMap<Integer, ArrayList<Integer>>();	// Stores the document IDs and the positions of the occurrences of the term
		
		for (int i = 0; i < list_of_docs.size(); i++) {
			tf += this.term_frequency(term, songID_to_lyrics.get(list_of_docs.get(i)));
		}
		
		for (int j = 0; j < list_of_docs.size(); j++) {
			int doc_id = list_of_docs.get(j);
			var positions = this.list_of_positions(term, songID_to_lyrics.get(doc_id));
			int doc_count = positions.size();	// The frequency of the term in the current document
			positions.add(0, doc_count);		// First element is always going to be the tf w.r.t the document
			occurrences.put(doc_id, positions);
		}
		
		 TermData data = new TermData(tf, df, occurrences);
		
		 return data;
		
	}
	
	public HashMap<String, HashMap<Integer, ArrayList<Integer>>> buildIndex() throws FileNotFoundException {
		var unique_terms = new ArrayList<String>();
		var index = new HashMap<String, HashMap<Integer, ArrayList<Integer>>>();
		
		for (int i = 0; i < songID_to_lyrics.size(); i++) {
			var terms = songID_to_lyrics.get(i);
			for (String term: terms) {
				if (!unique_terms.contains(term)) {
					unique_terms.add(term);
				}
			}
		}
		
		for (String term: unique_terms) {
			index.put(term, getTermData(term).getPostingLists());
		}
		
		return index;
	}
	
}
























