package uk.ac.ed.yazzzam.Ranker;

import java.util.ArrayList;
import java.util.List;

public interface Ranker {
    ArrayList<ScoringResult> score(List<String> query);
}
