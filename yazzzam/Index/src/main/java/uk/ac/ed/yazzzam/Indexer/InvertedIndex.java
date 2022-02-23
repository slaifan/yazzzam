package uk.ac.ed.yazzzam.Indexer;

import java.util.Spliterator;

public interface InvertedIndex<T extends PostingListIterator> {
    /**
     * Retrieves the posting list for the specified term
     * @param term
     * @return posting list supported by the index
     */
    public T getPostingList(String term);

    /**
     * @return spliterator for the terms stored in the index
     */
    public Spliterator<String> getTermSpliterator();
}
