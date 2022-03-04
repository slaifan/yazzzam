package uk.ac.ed.yazzzam.Ranker;

import uk.ac.ed.yazzzam.Indexer.IndexBuilder;

import java.util.*;

public class BM25 {

    private double k1 = 1.5;
    private double epsilon = 0.25;
    private double b = 0.75;

    private IndexBuilder ib;

    private double avgIdf;
    private int N;
    private double avgDocLen;

    public BM25(double k1, double epsilon, double b, IndexBuilder ib) {
        k1 = k1;
        epsilon = epsilon;
        b = b;

        ib = ib;

        N = ib.getDocLengths().size();
        avgIdf = getAvgIdf();
        avgDocLen = getAvgDocLen();

    }

    public BM25(IndexBuilder ib) {
        ib = ib;

        N = ib.getDocLengths().size();
        avgIdf = getAvgIdf();
        avgDocLen = getAvgDocLen();

    }

    public int score(List<String> query) {
        var best_score = 0.0;
        var res = 0;
        for (int i = 0; i < N; i++) {
            var doc_score = scoreDocument(query, i);
            if (doc_score > best_score) {
                best_score = doc_score;
                res = i;
            }
        }
        return res;
    }

    private double scoreDocument(List<String> query, int document) {
        var index = ib.getIndex();
        var score = 0;
        for (int i = 0; i < query.size(); i++) {
            var word = query.get(i);
            var f_qd = index.get(word).getPostingsList().size(); // how many docs the word occurred in
            var bdlen = b * (ib.getDocLengths().get(i) / avgDocLen);
            var formula = (f_qd * k1 + 1)/(f_qd + k1 * (1 - b + bdlen));
            var word_score = idf(word) * formula;

            score += word_score;
        }
        return score;
    }

    private double getAvgIdf() {
        var idf = 0;
        var index = ib.getIndex();
        for (String word : index.keySet()) {
            var q = index.get(word).getDf();
            idf += Math.log(N - q + 0.5) - Math.log(q + 0.5);
        }
        return idf / index.size();
    }

    private double getAvgDocLen() {
        var docLengths = ib.getDocLengths();
        var totalDocLens = 0;
        for (Integer docLen : docLengths.values()) {
            totalDocLens += docLen;
        }
        return  totalDocLens / docLengths.size();
    }

    private double idf(String word) {
        var q = ib.getIndex().get(word).getDf();
        var idf = Math.log(N - q + 0.5) - Math.log(q + 0.5);
        return idf >= 0? idf : (epsilon * avgIdf);
    }
    
}
