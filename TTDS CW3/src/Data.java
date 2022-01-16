import java.util.ArrayList;
import java.util.HashMap;

public class Data {

	private int tf;
	private int df;
	private HashMap<Integer, ArrayList<Integer>> document_occurrences;
	
	
	public Data(int tf, int df, HashMap<Integer, ArrayList<Integer>> positions) {
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



	public HashMap<Integer, ArrayList<Integer>> getOccurrences() {
		return document_occurrences;
	}
	
}
