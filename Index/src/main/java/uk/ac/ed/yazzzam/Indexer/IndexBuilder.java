package uk.ac.ed.yazzzam.Indexer;

import org.w3c.dom.ls.LSOutput;
import uk.ac.ed.yazzzam.Preprocessor.BasicPreprocessor;
import uk.ac.ed.yazzzam.Preprocessor.FullProcessor;
import uk.ac.ed.yazzzam.Preprocessor.Preprocessor;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.util.*;


public class IndexBuilder {

	public Map<Integer, List<String>> songID_to_lyrics;	// Maps the song ID to the preprocessed lyrics

	public IndexBuilder() {
		songID_to_lyrics = new HashMap<>();
	}

	private Preprocessor preprocessor = new BasicPreprocessor();
	//private Preprocessor preprocessor;



	//	// Preprocesses an entire document and adds it to the songID_to_lyrics hashmap
	public void preprocess_documents(String documents, String stopwordsFileName) throws FileNotFoundException {

		//preprocessor = new FullProcessor(stopwordsFileName);
		var all_lyrics = new ArrayList<String>();
		List<Song> songs = CSVReader.readFile(documents);
		for (Song song : songs){
//			var lyrics = song.getLyrics();
//			preprocessed_lyrics.add(preprocessor.preprocess(lyrics));
			all_lyrics.add(song.getLyrics());
		}
		var preprocessed_lyrics = new ArrayList<List<String>>();



		// Pre-process all the lyrics
		for (String song: all_lyrics) {
			preprocessed_lyrics.add(preprocessor.preprocess(song));
		}

		preprocessed_lyrics.remove(0);		// First element is an empty list

		for (int i = 0; i < preprocessed_lyrics.size(); i++) {
			songID_to_lyrics.put(i, preprocessed_lyrics.get(i));
		}

	}


	// Preprocesses a single song
//	public void preprocess_song(Song song) {
//
//		var lyrics = song.getLyrics();
//		//var lyrics = String.join(" ", Arrays.asList(song.split("Title: ")));
//		var preprocessedLyrics = preprocessor.preprocess(lyrics.get(0));
//		song.deleteLyrics(0);
//		for (var word : preprocessedLyrics){
//			song.setLyrics(word);
//		}
	//songID_to_lyrics.put(songID_to_lyrics.size() - 1, preprocessor.preprocess(lyrics));//pottential slow

	//}


	// Returns the hashmap of songID to preprocessed lyrics
	public Map<Integer, List<String>> getMap(){
		return songID_to_lyrics;
	}



	// Returns the list of song IDs that contain the given term
	public ArrayList<Integer> list_of_documents(String term, Map<Integer, List<String>> preprocessed_data) {

		var docs = new ArrayList<Integer>();

		for (int id: preprocessed_data.keySet()) {
			if (preprocessed_data.get(id).contains(term)) {
				docs.add(id);
			}
		}

		return docs;
	}

	// Returns the positions of the occurrences of the term in the lyrics
	public ArrayList<Integer> list_of_positions(String term, List<String> tokenized_lyrics){
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
	public int term_frequency(String term, List<String> tokenized_lyrics) {

		return Collections.frequency(tokenized_lyrics, term);

	}


	// Generates a TermData object for the given term
	public TermData getTermData(String term) {
		var list_of_docs = this.list_of_documents(term, songID_to_lyrics);
		var df = list_of_docs.size();  // Document frequency
		int tf = 0;  // The total number of times a term occurs in the corpus
		var occurrences = new HashMap<Integer, List<Integer>>();	// Stores the document IDs and the positions of the occurrences of the term

		for (Integer list_of_doc : list_of_docs) {
			tf += this.term_frequency(term, songID_to_lyrics.get(list_of_doc));
		}

		for (int doc_id : list_of_docs) {
			occurrences.put(doc_id, list_of_positions(term, songID_to_lyrics.get(doc_id)));
		}

		return new TermData(tf, df, occurrences);

	}

	public Map<String, Map<Integer, List<Integer>>> buildIndex() {
		var unique_terms = new ArrayList<String>();
		var index = new HashMap<String, Map<Integer, List<Integer>>>();

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
























