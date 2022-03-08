package uk.ac.ed.yazzzam.Ranker;

import uk.ac.ed.yazzzam.Indexer.IndexBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BM25Proximity extends BM25 {
    private double c = 20;

    public BM25Proximity(IndexBuilder ib_) {
        super(ib_);
    }

    public BM25Proximity(IndexBuilder ib_, double c_) {
        super(ib_);
        c = c_;
    }

    public BM25Proximity(double k1, double epsilon, double b, IndexBuilder ib, double c_) {
        super(k1, epsilon, b, ib);
        c = c_;
    }

    @Override
    public ArrayList<ScoringResult> score(List<String> query) {
        var res = super.score(query);

        for (ScoringResult result: res) {
            double proximityScore = 0.0;
            for (int i = 0; i < query.size(); i++) {
                for (int j = i; j < query.size(); j++) {
                    if (query.get(i) != query.get(j)) {
                        proximityScore += 1 / findProximity(query.get(i), query.get(j), result.docId);
                    }
                }
            }
            result.score += c * proximityScore;
        }

        Collections.sort(res);
        return res;
    }

    private double findProximity(String word1, String word2, int docId) {
        var postings1 = safeGetPosting(word1, ib, docId);
        var postings2 =  safeGetPosting(word2, ib, docId);

        var idx1 = 0;
        var idx2 = 0;

        double minDist = Math.pow(15, 2); // if dist is more than 15 apart, no use scoring differently

        while (idx1 < postings1.size() && idx2 < postings2.size()) {
            var w1 = postings1.get(idx1);
            var w2 = postings2.get(idx2);

            var dist = Math.pow(w1 - w2, 2);
            minDist = dist < minDist ? dist : minDist;
            if (w1 > w2) {
                idx2++;
            }
            else {
                idx1++;
            }
        }
        return minDist;
    }

    private List<Integer> safeGetPosting(String word1, IndexBuilder ib, int docId) {
        try {
            return ib.getIndex().get(word1).getPostingsList().get(docId).getPositions();
        }
        catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }
}
