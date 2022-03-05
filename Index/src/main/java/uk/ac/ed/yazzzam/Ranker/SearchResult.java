package uk.ac.ed.yazzzam.Ranker;

public class SearchResult implements Comparable<SearchResult>{
    public int docId;
    public double score;

    public SearchResult(int docId_, double score_) {
        docId = docId_;
        score = score_;
    }

    @Override
    public int compareTo(SearchResult otherResult) {
        if (score == otherResult.score)
            return 0;
        return score - otherResult.score < 0 ? (1) : -1;

    }
}
