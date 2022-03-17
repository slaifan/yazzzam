package uk.ac.ed.yazzzam.Ranker;

import uk.ac.ed.yazzzam.GlobalSettings;
import uk.ac.ed.yazzzam.Indexer.IndexBuilder;
import uk.ac.ed.yazzzam.WebServer.SearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BM25ProximityFuzzy extends BM25 {
    private double c;
    private double threshold;
    private IndexBuilder ib = GlobalSettings.getIndex();



    public BM25ProximityFuzzy(double k1, double b, double epsilon, int n, double c, double threshold) {
        super(k1, epsilon, b, n);
        this.c = c;
        this.threshold = threshold;
    }

    @Override
    public ArrayList<ScoringResult> score(List<String> query) {
        var res = super.score(query);

        for (ScoringResult result: res) {
            double proximityScore = 0.0;
            for (int i = 0; i < query.size(); i++) {
                for (int j = i; j < query.size(); j++) {
                    if (query.get(i) != query.get(j)) {
                        proximityScore += 1 / (0.01 + findProximity(query.get(i), query.get(j), result.docId));
                    }
                }
            }
            result.score += c * proximityScore;
        }

        Collections.sort(res);
        return res;
    }

    public ArrayList<ScoringResult> scoreFiltered(List<String> query, Set<Integer> filteredIds) {
        var res = super.score(query, filteredIds);
        System.out.println(res);
        for (ScoringResult result: res) {
            double proximityScore = 0.0;
            for (int i = 0; i < query.size(); i++) {
                for (int j = i; j < query.size(); j++) {
                    if (query.get(i) != query.get(j)) {
                        proximityScore += 1 / (0.01 + findProximity(query.get(i), query.get(j), result.docId));
                    }
                }
            }
            result.score += c * proximityScore;
        }

        Collections.sort(res);
        return res;
    }

    @Override
    public ArrayList<SearchResult> getResults(String rawQuery) {
        var query = GlobalSettings.getPreprocessor().preprocess(rawQuery);
        var results = score(query);

        var out = results.stream().map(r -> new SearchResult(r, getExcerpt(rawQuery, ib.getSong(r.docId), GlobalSettings.excerpt_size))).collect(Collectors.toList());
        return (ArrayList<SearchResult>) out;
    }

    public ArrayList<SearchResult> getResultsFiltered(String rawQuery, Set<Integer> filteredIds) {
        var query = GlobalSettings.getPreprocessor().preprocess(rawQuery);
        var results = scoreFiltered(query, filteredIds);

        var out = results.stream().map(e -> new SearchResult(e, " ")).collect(Collectors.toList());
        if (out.size() == 0) {
            return null;
        }

        return (ArrayList<SearchResult>) out;
    }

    private double findProximity(String word1, String word2, int docId) {
        var postings1 = safeGetPostingFuzzy(word1, docId);
        var postings2 =  safeGetPostingFuzzy(word2, docId);

        var idx1 = 0;
        var idx2 = 0;

        double minDist = Math.pow(threshold, 2); // if dist is more than 15 apart, no use scoring differently

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

    private List<Integer> safeGetPostingFuzzy(String word1, int docId) {
        var res = new ArrayList<Integer>();
        var simFinder = GlobalSettings.getSimilarWordsFinder();
        try {
            res.addAll(ib.getInvertedIndex().get(word1).getPostingsList().get(docId).getPositions());
        }
        catch (NullPointerException e) {
            var simWords = simFinder.findSimilarWords(word1, word1.length() / 2);
            for (String word : simWords) {
                res.addAll(ib.getInvertedIndex().get(word).getPostingsList().get(docId).getPositions());
            }
            return res;
        }

        var similarWords = simFinder.findSimilarWords(word1, word1.length() / 3);
        for (String word : similarWords) {
            res.addAll(ib.getInvertedIndex().get(word).getPostingsList().get(docId).getPositions());
        }
        Collections.sort(res);
        return res;

    }
}
