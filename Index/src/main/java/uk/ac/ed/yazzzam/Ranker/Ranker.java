package uk.ac.ed.yazzzam.Ranker;

import uk.ac.ed.yazzzam.Indexer.IndexBuilder;
import uk.ac.ed.yazzzam.Indexer.TextFileReader;
import uk.ac.ed.yazzzam.Indexer.TermData;

import java.util.*;
import java.io.IOException;


public class Ranker {
    public Map<String,Integer> docFrequencies = new HashMap<String,Integer>();
    public Map<String,Integer> termFrequencies = new HashMap<String,Integer>();
    public Integer numDocs = 0;
    public Map<Integer,Map<String,Integer>> docTermFrequencies = new HashMap<Integer,Map<String,Integer>>();

    public Integer getNumDocs(Map<String,Integer> numDocsPerTerm) {
        for(String term : docFrequencies.keySet()){
            numDocs += docFrequencies.get(term);
        }
        return numDocs;
    }

    public List<String> updateUniqueTerms(Map<Integer, List<String>> songID_to_Lyrics){
        var unique_terms = new ArrayList<String>();

        for (int i = 0; i < songID_to_lyrics.size(); i++) {
			List<String> terms = songID_to_lyrics.get(i);
			for (String term: terms) {
				if (!unique_terms.contains(term)) {
					unique_terms.add(term);
				}
			}
		}

        return unique_terms;
    }

    public Map<String,Integer> getTermFrequencies(Map<Integer, List<String>> songID_to_Lyrics, List<String> unique_terms){
        var list_of_docs = IndexBuilder.list_of_documents(term,songID_to_lyrics);

        for(String term : unique_terms) {
            termFrequencies.put(term,IndexBuilder.term_frequency(term,songID_to_lyrics.get(list_of_docs.size())));
        }

        return termFrequencies;
    }

    public Map<String, Integer> getDocFrequencies(Map<Integer, List<String>> songID_to_Lyrics, List<String> unique_terms) {
        
        for(String term : unique_terms) {
            docFrequencies.put(term,IndexBuilder.list_of_documents(term,songID_to_lyrics).size());
        }

        return docFrequencies;
    }

    public Map<Integer, Map<String,Integer>> getDocTermFreqList(Map<Integer, List<String>> songID_to_Lyrics, List<String> unique_terms){
        Map<String, Integer> termFreqsInDoc = new HashMap<String, Integer>();
        for(Integer doc:IndexBuilder.list_of_documents(songID_to_lyrics)){
            for(String term:unique_terms){
                docTermFrequencies.put(doc,new HashMap<String, Integer>(term,IndexBuilder.term_frequency(term,songID_to_lyrics.get(doc).toInteger())));
            }
        }
        return docTermFrequencies;
    }

    public int numWords(Map<Integer, List<String>> songID_to_Lyrics){
        var totalWords = 0;
        for(Integer doc_id: IndexBuilder.list_of_documents(songID_to_lyrics)){
            totalWords += songID_to_Lyrics.get(doc_id).size();
        }
        return totalWords;
    }

    public int[] numWordsPerDoc(Map<Integer, List<String>> songID_to_Lyrics){
        int [] numWordsDoc = new int[songID_to_Lyrics.size()];
        for (Integer doc_id: IndexBuilder.list_of_documents(songID_to_lyrics)){
            numWordsDoc[doc_id] = songID_to_lyrics.get(doc_id).size();
        }
        return numWordsDoc;
    }
    public static void main(String[] args) throws IOException{
        var documentsFile = new TextFileReader().readFile(args[0]);


    }
}
