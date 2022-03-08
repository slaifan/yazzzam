package uk.ac.ed.yazzzam.Ranker;

public class ScoringResult implements Comparable<ScoringResult>{
    public int docId;
    public double score;

    public ScoringResult(int docId_, double score_) {
        docId = docId_;
        score = score_;
    }

    @Override
    public int compareTo(ScoringResult otherResult) {
        if (score == otherResult.score)
            return 0;
        return score - otherResult.score < 0 ? (1) : -1;

    }
}
