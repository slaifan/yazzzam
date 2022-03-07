package uk.ac.ed.yazzzam.WebServer;

import uk.ac.ed.yazzzam.Indexer.IndexBuilder;
import uk.ac.ed.yazzzam.Ranker.ScoringResult;

public class SearchResult {
    private String songTitle;
    private int songId;
    private double score;
//    private List<String> excerpt;

    public SearchResult(ScoringResult sr, IndexBuilder ib) {
        songId = sr.docId;
        songTitle = ib.getTitle(songId);
        score = sr.score;
//        excerpt = new ArrayList<String>() {" "  + songTitle};
    }
}
