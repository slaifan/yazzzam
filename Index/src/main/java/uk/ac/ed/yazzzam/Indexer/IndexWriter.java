package uk.ac.ed.yazzzam.Indexer;

import java.io.IOException;
import java.util.Map;

/**
 * Classes implementing this interface should write the contents of the inverted index to the persistent storage.
 * @param <T> - type of the PostingList used by the Index
 */
public interface IndexWriter<T extends PostingListIterator> {
    Map<String, Integer> writeToFile(InvertedIndex<T> invertedIndex, String fileName) throws IOException;
}
