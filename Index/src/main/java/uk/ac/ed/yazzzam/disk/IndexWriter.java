package uk.ac.ed.yazzzam.disk;

import uk.ac.ed.yazzzam.index.InvertedIndex;
import uk.ac.ed.yazzzam.index.postinglists.PostingListIterator;

import java.io.IOException;
import java.util.Map;

/**
 * Classes implementing this interface should write the contents of the inverted index to the persistent storage.
 */
public interface IndexWriter {
    Map<String, Integer> writeToFile(InvertedIndex invertedIndex, String fileName) throws IOException;
}
