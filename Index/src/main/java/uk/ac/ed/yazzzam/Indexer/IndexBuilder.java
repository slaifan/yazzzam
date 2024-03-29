package uk.ac.ed.yazzzam.Indexer;

import uk.ac.ed.yazzzam.Preprocessor.BasicPreprocessor;
import uk.ac.ed.yazzzam.Preprocessor.Preprocessor;

import java.util.HashMap;
import java.util.Map;

public class IndexBuilder {
	private Preprocessor preprocessor;
	private Map<String, TermData> index;
	private Map<Integer, Integer> docslengths;
	private Map<Integer, Song> titles;

	public IndexBuilder() {
		index = new HashMap<>();
		docslengths = new HashMap<>();
		titles = new HashMap<>();
		preprocessor = new BasicPreprocessor();
	}

	public IndexBuilder(Preprocessor prec) {
		index = new HashMap<>();
		docslengths = new HashMap<>();
		titles = new HashMap<>();
		preprocessor = prec;
	}

	public void preprocessSong(Song song) {
		var lyrics = song.getLyrics();
		var preprocessedLyrics = preprocessor.preprocess(lyrics);// soundex, metaphone or stem
//		System.out.println(preprocessedLyrics);
		song.setPreprocessedLyrics(preprocessedLyrics);
//		song.deleteLyrics();
	}

	public Map<String, TermData> getInvertedIndex(){
		return index;
	}

	public Map<Integer, Integer> getDocLengths(){
		return docslengths;
	}

	public Song getSong(int id){
		return titles.get(id);
	}

	public void indexSong(int i, Song song) {
		var docSize = song.getPreprocessedLyrics().size();
		docslengths.put(i, docSize);
		titles.put(i, song);

		for (var j = 0; j < docSize; j++){
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
}
























