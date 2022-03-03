package uk.ac.ed.yazzzam.Indexer;

import uk.ac.ed.yazzzam.Preprocessor.BasicPreprocessor;
import uk.ac.ed.yazzzam.Preprocessor.FullProcessor;
import uk.ac.ed.yazzzam.Preprocessor.Preprocessor;

import java.util.*;

public class IndexBuilder {

	private Preprocessor preprocessor;
	private Map<String, TermData> index = new HashMap<>();
	
	public IndexBuilder() {
		//TODO: optimize full processor
		preprocessor = new FullProcessor("englishST.txt");
		//preprocessor = new BasicPreprocessor();
	}

	public void preprocessSong(Song song) {
		var lyrics = song.getLyrics();
		var preprocessedLyrics = preprocessor.preprocess(lyrics);
		song.setPreprocessedLyrics(preprocessedLyrics);
		song.deleteLyrics();
	}

	public Map<String, TermData> getIndex(){
		return index;
	}

	public void indexSong(int i, Song song) {
		for (var j = 0; j < song.getPreprocessedLyrics().size(); j++){
			var term = song.getPreprocessedLyrics().get(j);
			var termData = index.getOrDefault(term, new TermData());
			termData.setDf(termData.getDf()+1);
			var postingsList = termData.getPostingsList();
			postingsList.putIfAbsent(i, new Document());
			var doc = postingsList.get(i);
			var positions = doc.getPositions();
			positions.add(j);
			doc.setTf(doc.getTf()+1);
			index.put(term, termData);

		}

	}
	///////////////////////////////////////////////////////////
	// REFACTORED CODE
//	 Generates a TermData object for the given term
//	public Doc getTermData(String term) {
//		var list_of_docs = this.list_of_documents(term, songID_to_lyrics);
//		var df = list_of_docs.size();  // Document frequency
//		int tf = 0;  // The total number of times a term occurs in the corpus
//		var occurrences = new HashMap<Integer, List<Integer>>();	// Stores the document IDs and the positions of the occurrences of the term
//
//		for (Integer list_of_doc : list_of_docs) {
//			tf += this.term_frequency(term, songID_to_lyrics.get(list_of_doc));
//		}
//
//		for (int doc_id : list_of_docs) {
//			occurrences.put(doc_id, list_of_positions(term, songID_to_lyrics.get(doc_id)));
//		}
//
//		return new Doc(tf, df, occurrences);
//
//	}

	// How often the term occurs in the given song lyrics
//	public int term_frequency(String term, List<String> tokenized_lyrics) {
//
//		return Collections.frequency(tokenized_lyrics, term);
//
//	}

//	// Returns the list of song IDs that contain the given term
//	public ArrayList<Integer> list_of_documents(String term, Map<Integer, List<String>> preprocessed_data) {
//
//		var docs = new ArrayList<Integer>();
//
//		for (int id: preprocessed_data.keySet()) {
//			if (preprocessed_data.get(id).contains(term)) {
//				docs.add(id);
//			}
//		}
//
//		return docs;
//	}

//	// Returns the positions of the occurrences of the term in the lyrics
//	public ArrayList<Integer> list_of_positions(String term, List<String> tokenized_lyrics){
//		var positions = new ArrayList<Integer>();
//		for (int i = 0; i < tokenized_lyrics.size(); i++) {
//			String element = tokenized_lyrics.get(i);
//
//			if (term.equalsIgnoreCase(element)) {
//				positions.add(i);
//			}
//		}
//		return positions;
//	}

	
}
























