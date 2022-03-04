package uk.ac.ed.yazzzam.Indexer;

import java.util.HashMap;
import java.util.Map;

public class TermData {
    private int df;
    private Map<Integer, Document> postingsList;

    public TermData(){
        df = 0;
        postingsList = new HashMap<>();
    }

    public int getDf() {
        return df;
    }

    public void setDf(int df) { this.df = df;}

    public Map<Integer, Document> getPostingsList() {
        return postingsList;
    }

    @Override
    public String toString() {
        return "TermData{" +
                "df=" + df +
                ", postingsList=" + postingsList +
                '}';
    }
}
