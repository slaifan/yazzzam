package uk.ac.ed.yazzzam.Indexer;

import java.util.List;
import java.util.Map;

/**
 * Map-based implementation of the inverted index that uses ProximityPostingList to represt its posting lists
 */
public class ProximityInvertedIndex extends MapBasedInvertedIndex<ProximityPostingList> {

    public ProximityInvertedIndex(Map<String, Map<Integer, List<Integer>>> inverted_index) {
        super(inverted_index);
    }

    @Override
    public ProximityPostingList getPostingList(String term) {
        return new ProximityPostingList(term, inverted_index.get(term));
    }
}
