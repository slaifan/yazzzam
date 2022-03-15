package uk.ac.ed.yazzzam.Ranker;

import uk.ac.ed.yazzzam.WebServer.SearchResult;

import java.util.ArrayList;
import java.util.List;

public interface Ranker {
    ArrayList<ScoringResult> score(List<String> query);

    ArrayList<SearchResult> getResults(String query);
}
