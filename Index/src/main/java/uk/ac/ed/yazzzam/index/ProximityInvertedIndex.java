package uk.ac.ed.yazzzam.index;

import uk.ac.ed.yazzzam.index.postinglists.ProximityPostingList;

import java.util.List;
import java.util.Map;

/**
 * Map-based implementation of the inverted index that uses ProximityPostingList to represent its posting lists
 */
public class ProximityInvertedIndex extends MapBasedInvertedIndex {

    public ProximityInvertedIndex(Map<String, Map<Integer, List<Integer>>> inverted_index) {
        super(inverted_index);
    }

    @Override
    public ProximityPostingList getPostingList(String term) {
        return new ProximityPostingList(term, inverted_index.get(term));
    }
}
