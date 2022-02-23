package uk.ac.ed.yazzzam.Indexer;

import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.TreeMap;

// TODO: should be replaced by specific instances of the inverted index
// created just to bridge Sumair's index with the new architecture

/**
 * Inverted Index internally using Map interface to store the data
 * @param <T>
 */
public abstract class MapBasedInvertedIndex<T extends PostingListIterator> implements InvertedIndex<T> {

    protected final Map<String, TreeMap<Integer, List<Integer>>> inverted_index;

    public MapBasedInvertedIndex(Map<String, Map<Integer, List<Integer>>> inverted_index) {
        this.inverted_index = new TreeMap<>();

        // keep the terms in the sorted order
        for (var indexEntry : inverted_index.entrySet()) {
            this.inverted_index.put(indexEntry.getKey(), new TreeMap<>(indexEntry.getValue()));
        }
    }

    @Override
    public Spliterator<String> getTermSpliterator() {
        return inverted_index.keySet().spliterator();
    }
}
