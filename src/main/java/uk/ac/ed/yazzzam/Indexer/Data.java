package uk.ac.ed.yazzzam.Indexer;

import java.util.*;

public class Data {

    private final int tf;
    private final int df;
    private final TreeMap<Integer, List<Integer>> documentOccurrences;

    public Data(int tf, int df, TreeMap<Integer, List<Integer>> positions) {
        this.tf = tf;
        this.df = df;
        this.documentOccurrences = positions;
    }

    public int getTf() {
        return tf;
    }

    public int getDf() {
        return df;
    }

    public TreeMap<Integer, List<Integer>> getOccurrences() {
        return documentOccurrences;
    }

}
