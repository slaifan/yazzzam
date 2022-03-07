package uk.ac.ed.yazzzam.Ranker;

import uk.ac.ed.yazzzam.GlobalSettings;
import uk.ac.ed.yazzzam.Indexer.IndexBuilder;

import java.util.*;

public class BM25 implements Ranker{

    private double k1;
    private double epsilon;
    private double b;
    private double n;

    protected IndexBuilder ib = GlobalSettings.getIndex();

    private double avgIdf;
    private int N;
    private double avgDocLen;

    public BM25(double k1_, double epsilon_, double b_, int n_) {
        k1 = k1_;
        epsilon = epsilon_;
        b = b_;
        n = n_;

        N = ib.getDocLengths().size();
        avgIdf = getAvgIdf();
        avgDocLen = getAvgDocLen();

    }


    public ArrayList<ScoringResult> score(List<String> query) {
        PriorityQueue<ScoringResult> results = new PriorityQueue<>();
        var res = new ArrayList<ScoringResult>();
        for (int i = 0; i < N; i++) {
            var doc_score = scoreDocument(query, i);
            results.add(new ScoringResult(i, doc_score));
        }

        for (int i = 0; i < n; i++) {
            res.add(results.poll());
        }
        return res;
    }

    private double scoreDocument(List<String> query, int document) {
        var index = ib.getIndex();
        double score = 0.0;
        for (int i = 0; i < query.size(); i++) {
            var word = query.get(i);
            var doc_freq = index.get(word).getPostingsList().get(document);
            int f_qd;

            if (doc_freq != null) {
                f_qd = doc_freq.getTf();
            }
            else {
                f_qd = 0;
            }

            double bdlen = b * ((double)ib.getDocLengths().get(document) / avgDocLen);

            double formula = (f_qd * (k1 + 1))/(f_qd + k1 * (1 - b + bdlen));

            double word_score = idf(word) * formula;
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
        return idf / (index.size() + 1);
    }

    private double getAvgDocLen() {
        var docLengths = ib.getDocLengths();
        var totalDocLens = 0;
        for (Integer docLen : docLengths.values()) {
            totalDocLens += docLen;
        }
        return  totalDocLens / (docLengths.size() + 1);
    }

    private double idf(String word) {
        var q = ib.getIndex().get(word).getDf();
        var idf = Math.log((N - q + 0.5 / Math.log(q + 0.5)) + 1);
        if (idf >= 0) {
            return idf;
        }
        return epsilon * avgIdf;
    }


    
}
