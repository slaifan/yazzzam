package uk.ac.ed.yazzzam.index.postinglists;

import java.util.NoSuchElementException;

public interface PostingListIterator {

    /**
     * Moves the posting list "iterator" to the next document. After indicating the end of the posting list,
     * it should wrap around the posting list.
     * @return true if there is next document, false otherwise
     */
    boolean moveToNextDocument();

    /**
     * Retrieves the id of the document currently pointed at by the posting list iterator.
     * Should be used after the moveToNextDocument is called at least once to point to the first document.
     * @return id of the currently "selected" document
     * @throws NoSuchElementException if used before "moveToNextDocument" method or the end of the posting list is hit
     */
    int getCurrentDocument() throws NoSuchElementException;
}
