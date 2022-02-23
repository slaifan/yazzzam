package uk.ac.ed.yazzzam.Indexer;

import java.util.List;
import java.util.Map;

public class TermData {

	private int tf;
	private int df;
	private Map<Integer, List<Integer>> document_occurrences;
	
	
	public TermData(int tf, int df, Map<Integer, List<Integer>> positions) {
		this.tf = tf;
		this.df = df;
		this.document_occurrences = positions;
	}


	public int getTf() {
		return tf;
	}


	public int getDf() {
		return df;
	}



	public Map<Integer, List<Integer>> getPostingLists() {
		return document_occurrences;
	}
	
}
