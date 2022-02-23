package uk.ac.ed.yazzzam.index;

import uk.ac.ed.yazzzam.index.postinglists.ProximityPostingListIterator;

import java.util.Map;
import java.util.Spliterator;

public class CompressedProximityInvertedIndex implements InvertedIndex {
    private final Map<String, ProximityPostingListIterator> invertedIndex;

    public CompressedProximityInvertedIndex(Map<String, ProximityPostingListIterator> invertedIndex) {
        this.invertedIndex = invertedIndex;
    }

    @Override
    public ProximityPostingListIterator getPostingList(String term) {
        return invertedIndex.get(term);
    }

    @Override
    public Spliterator<String> getTermSpliterator() {
        return invertedIndex.keySet().spliterator();
    }
}
