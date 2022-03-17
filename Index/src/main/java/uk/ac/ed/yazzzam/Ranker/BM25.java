package uk.ac.ed.yazzzam.Ranker;

import uk.ac.ed.yazzzam.GlobalSettings;
import uk.ac.ed.yazzzam.Indexer.IndexBuilder;
import uk.ac.ed.yazzzam.Indexer.Song;
import uk.ac.ed.yazzzam.WebServer.SearchResult;

import java.util.*;
import java.util.stream.Collectors;

public class BM25 implements Ranker{

    private double k1;
    private double epsilon;
    private double b;
    private int n;

    private int WINDOW_SIZE = 25;

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

        for (int i : ib.getDocLengths().keySet()) {
            var doc_score = scoreDocument(query, i);
            results.add(new ScoringResult(i, doc_score));
        }

        for (int i = 0; i < n; i++) {
            res.add(results.poll());
        }
        return res;
    }

    public ArrayList<ScoringResult> score(List<String> query, Set<Integer> filteredIds) {
        PriorityQueue<ScoringResult> results = new PriorityQueue<>();
        var res = new ArrayList<ScoringResult>();

        for (int i : ib.getDocLengths().keySet()) {
            if (filteredIds.contains(i)) {
                var doc_score = scoreDocument(query, i);
                results.add(new ScoringResult(i, doc_score));
//                System.out.println("document " + i + " added to result " + ib.getTitle(i));
//                System.out.println("queue size is now " + results.size());
            }
            else if (i == 50564) {
                System.out.println("doc was actually filtered out");
            }
        }

        for (int i = 0; i < n; i++) {
            var r = results.poll();
            if (r == null) {
                return res;
            }
//                System.out.println("document " + r.docId + " added to result " + ib.getTitle(r.docId));
//                System.out.println("i is: " + i);
            res.add(r);
        }
        return res;
    }

    public ArrayList<SearchResult> getResults(String rawQuery) {
        var query = GlobalSettings.getPreprocessor().preprocess(rawQuery);
        var results = score(query);
        var out = new ArrayList<SearchResult>();

        var db = GlobalSettings.getDB();

        var songs = db.getSongs(results.stream().map(e -> e.docId).collect(Collectors.toList()));

        for (int i = 0; i < songs.size(); i++) {
            var doc = results.get(i);
//            assert (doc.docId == songs.get(i).getId());
//            var excerpt = i < 40 ? getExcerpt(rawQuery, songs.get(i), WINDOW_SIZE) : " ";
            out.add(new SearchResult(doc, " "));
        }
        return out;
    }




    private double scoreDocument(List<String> query, int document) {
        double score = 0.0;
        if (document == 212918) {
            System.out.println("bdlen is " + b * ((double)ib.getDocLengths().get(document) / avgDocLen));
            System.out.println("avg len is " + avgDocLen);
            System.out.println("doc len is " + (double)ib.getDocLengths().get(document));
        }
        for (int i = 0; i < query.size(); i++) {
            var word = query.get(i);
            var f_qd = safeGetTf(word, document);

            double bdlen = b * ((double)ib.getDocLengths().get(document) / avgDocLen);

            double formula = (f_qd * (k1 + 1))/(f_qd + k1 * (1 - b + bdlen));
            double word_score = idf(word) * formula;
            if (document == 212918) {

                System.out.println("formula for word: " + word + " is " + formula);
                System.out.println("idf for word: " + word + " is " + idf(word));
            }
            score += word_score;
        }
        return score;
    }


    private double getAvgIdf() {
        var idf = 0;
        var index = ib.getInvertedIndex();
        for (String word : index.keySet()) {
            var q = safeGetDf(word);
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
        var q = safeGetDf(word);
        var idf = Math.log((N - q + 0.5 / Math.log(q + 0.5)) + 1);
        if (idf >= 0) {
            return idf;
        }
        return epsilon * avgIdf;
    }

    private int safeGetTf(String word, int document) {
        try {
            return ib.getInvertedIndex().get(word).getPostingsList().get(document).getTf();
        }
        catch (NullPointerException e) {
            return 0;
        }
    }

    private int safeGetDf(String word) {
        try {
            return ib.getInvertedIndex().get(word).getDf();
        }
        catch (NullPointerException e) {
            return 0;
        }
    }

    public String getExcerpt(String rawQuery, Song song, int windowSize) {
//        var query = Arrays.asList(rawQuery.split(" "));
//        var doc = Arrays.asList(song.getLyrics().split(" "));
        var query = GlobalSettings.getPreprocessor().preprocess(rawQuery);
        var doc = song.getPreprocessedLyrics();
        if (doc.size() <= windowSize) {
            return song.getLyrics();
        }
        var bestWindowScore = 0.0;
        var bestWindowIdx = 0;

        var prevWindowScore = 0.0;

        var fstWindow = doc.subList(0, windowSize);
        for (int j = 0; j < fstWindow.size(); j++) {
            var word = fstWindow.get(j);
            if (query.contains(word)) {
                prevWindowScore += idf(word);
            }
        }

        for (int i = 1; i < doc.size() - windowSize; i++) {
            var windowScore = prevWindowScore - idf(doc.get(i -1)) + idf(doc.get(i + windowSize));
            if (windowScore > bestWindowScore) {
                bestWindowScore = windowScore;
                bestWindowIdx = i;
            }
        }
        var raw = Arrays.asList(song.getLyrics().split(" "));
//        System.out.println("best idx: " + bestWindowIdx);
//        System.out.println("raw size: " + raw.size());
//        System.out.println("doc size: " + doc.size());
        var excerpt = doc.subList(bestWindowIdx,  Math.min(doc.size() - 1, bestWindowIdx + windowSize - 1));
        return String.join(" ", excerpt);
    }
}
