package uk.ac.ed.yazzzam.Indexer;

import java.util.NoSuchElementException;

public interface TermPositionPostingListIterator extends PostingListIterator {
    /**
     * Moves the document's term position list "iterator" to the next document.
     * After indicating that all positions have been visited,
     * it should wrap around the term position list .
     * @return true if there is next document, false otherwise
     */
    boolean moveToNextTermPosition();

    /**
     * Retrieves a position of the term in a document currently pointed at by the term position list iterator.
     * Should be used after the moveToNextTermPosition is called at least once to point to the first entry.
     * @return id of the currently "selected" term positions
     * @throws NoSuchElementException if used before "moveToNextTermPosition" method or the end of the term position list is hit
     */
    int getCurrentTermPosition() throws NoSuchElementException;
}
