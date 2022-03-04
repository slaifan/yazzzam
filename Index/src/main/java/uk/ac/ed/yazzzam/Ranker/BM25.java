package uk.ac.ed.yazzzam.Ranker;

import uk.ac.ed.yazzzam.Indexer.IndexBuilder;
import java.util.*;
public class BM25 {

    private double k1 = 1.5;
    private double epsilon = 0.25;
    private double b = 0.75;
    private double avgIdf;

    public BM25(double k1, double epsilon, double b) {
        this.k1 = k1;
        this.epsilon = epsilon;
        this.b = b;
    }


    public double score(List<String> query, int document, IndexBuilder index) {
        var score = 0;
        for (int i = 0; i < query.size(); i++) {
            var word = query.get(i);
            var f_qd = index.getTermData(word).getPostingLists().keySet().size(); // how many docs the word occurred in
            var bdlen = b * (index.getDocLen(i) / index.getAvgDocLen());
            var formula = (f_qd * k1 + 1)/(f_qd + k1 * (1 - b + bdlen));
            var word_score = idf(word, index) * formula;

            score += word_score;
        }
        return score;
    }

    public double getAvgIdf(IndexBuilder index) {
        var idf = 0;
        for (int i = 0; i < index.index.keys().size(); i++) {
            var N = 1000;
            var q = index.getTermData(word).getDf();
            idf += Math.log(N - q + 0.5) - Math.log(q + 0.5);
        }
        return idf / index.index.keys().size();
    }

    public double idf(String word, IndexBuilder index) {
        var N = index.keys().size();
        var q = index.getTermData(word).getDf();
        var idf = Math.log(N - q + 0.5) - Math.log(q + 0.5);
        return idf >= 0? idf : (epsilon * avgIdf);
    }
    
}
