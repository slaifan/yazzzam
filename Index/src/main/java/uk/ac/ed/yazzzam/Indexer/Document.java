package uk.ac.ed.yazzzam.Indexer;

import java.util.ArrayList;
import java.util.List;

public class Document {

	private int tf;
	private List<Integer> termPositions;
	
	
	public Document() {
		tf = 0;
		termPositions = new ArrayList<>();
	}


	public int getTf() {
		return tf;
	}

	public void setTf(int tf) { this.tf = tf; }

	public List<Integer> getPositions(){
		return termPositions;
	}

	@Override
	public String toString() {
		return "Document{" +
				"tf=" + tf +
				", termPositions=" + termPositions +
				'}';
	}
}
