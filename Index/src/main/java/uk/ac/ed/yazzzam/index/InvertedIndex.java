package uk.ac.ed.yazzzam.index;

import uk.ac.ed.yazzzam.index.postinglists.ProximityPostingListIterator;

import java.util.Spliterator;

public interface InvertedIndex {
    /**
     * Retrieves the posting list for the specified term
     * @param term
     * @return posting list supported by the index
     */
    ProximityPostingListIterator getPostingList(String term);

    /**
     * @return spliterator for the terms stored in the index
     */
    Spliterator<String> getTermSpliterator();
}
