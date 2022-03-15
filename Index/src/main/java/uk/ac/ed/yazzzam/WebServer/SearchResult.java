package uk.ac.ed.yazzzam.WebServer;

import uk.ac.ed.yazzzam.GlobalSettings;
import uk.ac.ed.yazzzam.Ranker.ScoringResult;

public class SearchResult {
    private String songTitle;
    private int songId;
    private double score;
    private String excerpt;

    public SearchResult(ScoringResult sr, String excerpt) {
        songId = sr.docId;
        songTitle = GlobalSettings.getIndex().getTitle(songId);
        score = sr.score;
        this.excerpt = excerpt;
    }

    public SearchResult(int id, String excerpt) {
        songId = id;
        songTitle = GlobalSettings.getIndex().getTitle(id);
        this.excerpt = excerpt;
    }
}
