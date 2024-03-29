package uk.ac.ed.yazzzam.WebServer;

import uk.ac.ed.yazzzam.GlobalSettings;
import uk.ac.ed.yazzzam.Ranker.ScoringResult;

public class SearchResult {
    private String songTitle;
    private int songId;
    private String artist;
    private double score;
    private String excerpt;

    public SearchResult(ScoringResult sr, String excerpt) {
        songId = sr.docId;
        songTitle = GlobalSettings.getIndex().getSong(songId).getTitle();
        artist = GlobalSettings.getIndex().getSong(songId).getArtist();
        score = sr.score;
        this.excerpt = excerpt;
    }

    public SearchResult(int id, String excerpt) {
        songId = id;
        songTitle = GlobalSettings.getIndex().getSong(id).getTitle();
        this.excerpt = excerpt;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "songTitle='" + songTitle + '\'' +
                "artist='" + artist + '\'' +
                ", songId=" + songId +
                ", score=" + score +
                '}';
    }
}
