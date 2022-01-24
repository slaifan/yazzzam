package uk.ac.ed.yazzzam.Indexer;


import uk.ac.ed.yazzzam.Preprocessor.BasicPreprocessor;
import uk.ac.ed.yazzzam.Preprocessor.Preprocessor;

import java.io.FileNotFoundException;
import java.util.*;


public class IndexBuilder {

    private final String documents;

    public IndexBuilder(String documents) {
        this.documents = documents;
    }


    // Preprocesses the lyrics and returns a hash-map that maps song ID to lyrics
    public Map<Integer, List<String>> preprocess() {

        var lyrics = documents.split("Title: ");        // Split the lyrics by title
        var songID_to_lyrics = new HashMap<Integer, List<String>>();    // Stores the lyrics of each song mapped to its ID

        Preprocessor p = new BasicPreprocessor();
        var preprocessed_lyrics = new ArrayList<List<String>>();

        // Pre-process all the lyrics
        for (String song : lyrics) {
            preprocessed_lyrics.add(p.preprocess(song));
        }

        preprocessed_lyrics.remove(0);        // First element is an empty list

        for (int i = 0; i < preprocessed_lyrics.size(); i++) {
            songID_to_lyrics.put(i, preprocessed_lyrics.get(i));
        }


        return songID_to_lyrics;
    }


    // Returns the list of song IDs that contain the given term
    public List<Integer> list_of_documents(String term, Map<Integer, List<String>> id_to_lyrics) {

        var docs = new ArrayList<Integer>();

        for (int id : id_to_lyrics.keySet()) {
            if (id_to_lyrics.get(id).contains(term)) {
                docs.add(id);
            }
        }

        return docs;
    }

    // Returns the positions of the occurrences of the term in the lyrics
    public List<Integer> list_of_positions(String term, List<String> tokenized_lyrics) {
        var positions = new ArrayList<Integer>();
        for (int i = 0; i < tokenized_lyrics.size(); i++) {
            String element = tokenized_lyrics.get(i);

            if (term.equalsIgnoreCase(element)) {
                positions.add(i);
            }
        }
        return positions;
    }


    // How often the term occurs in the lyrics for a single song
    public int term_frequency(String term, List<String> tokenized_lyrics) {

        return Collections.frequency(tokenized_lyrics, term);

    }


    // Generates a Data object for the given term
    public Data getData(String term) {
        var songID_to_lyrics = this.preprocess();
        var list_of_docs = this.list_of_documents(term, songID_to_lyrics);
        var df = list_of_docs.size();    // Document frequency
        int tf = 0;        // The total number of times a term occurs in the corpus
        var occurrences = new TreeMap<Integer, List<Integer>>();    // Stores the document IDs and the positions of the occurrences of the term

        for (int i = 0; i < list_of_docs.size(); i++) {
            tf += this.term_frequency(term, songID_to_lyrics.get(list_of_docs.get(i)));
        }

        for (int j = 0; j < list_of_docs.size(); j++) {
            int doc_id = list_of_docs.get(j);
            var positions = this.list_of_positions(term, songID_to_lyrics.get(doc_id));
            int doc_count = positions.size();    // The frequency of the term in the current document
            positions.add(0, doc_count);        // First element is always going to be the tf w.r.t the document
            occurrences.put(doc_id, positions);
        }

        Data data = new Data(tf, df, occurrences);

        return data;

    }

    public Map<String, Map<Integer, List<Integer>>> buildIndex() {
        var songID_to_lyrics = this.preprocess();
        var unique_terms = new ArrayList<String>();
        var index = new HashMap<String, Map<Integer, List<Integer>>>();

        for (int i = 0; i < songID_to_lyrics.size(); i++) {
            var terms = songID_to_lyrics.get(i);
            for (String term : terms) {
                if (!unique_terms.contains(term)) {
                    unique_terms.add(term);
                }
            }
        }

        for (String term : unique_terms) {
            index.put(term, getData(term).getOccurrences());
        }

        return index;
    }

}
























