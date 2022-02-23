package uk.ac.ed.yazzzam.Indexer;

import java.util.*;

/**
 * Map-based implementation of the
 */
public class ProximityPostingList implements TermPositionPostingListIterator {
    private final String term;
    private final TreeMap<Integer, List<Integer>> docPositions;  // TreeMap to ensure the order of keys
                                                                 // although a Map seems more appropriate,
                                                                 // passing the responsibility of determining the order
                                                                 // to the index

    private Iterator<Integer> docIterator;
    private int currentDocument = -1;

    private Iterator<Integer> positionIterator;
    private int currentPosition = -1;

    protected ProximityPostingList(String term, TreeMap<Integer, List<Integer>> docPositions) {
        this.term = Objects.requireNonNull(term);
        this.docPositions = Objects.requireNonNull(docPositions);
        docIterator = docPositions.keySet().iterator();  // initialize the document iterator
    }

    @Override
    public boolean moveToNextDocument() {
        if (!docIterator.hasNext()) {
            // reset posting list iterator
            currentDocument = -1;
            currentPosition = -1;
            docIterator = docPositions.keySet().iterator();  // instantiate a new iterator to support wrap-around
            positionIterator = null;  // when no document is selected, cannot traverse term positions

            return false; // indicate the end of the posting list
        }
        currentDocument = docIterator.next();
        positionIterator = docPositions.get(currentDocument).iterator();  // start a term position iterator for the new document
        return true;
    }

    @Override
    public int getCurrentDocument() throws NoSuchElementException {
        if (currentDocument == -1) {
            throw new NoSuchElementException("No document is selected!");
        }
        return currentDocument;
    }

    @Override
    public boolean moveToNextTermPosition() {
        if (positionIterator == null) {
            throw new IllegalStateException("Need to move to the document first!");
        }
        if (!positionIterator.hasNext() ) {
            // restart iterator
            currentPosition = -1;
            positionIterator = docPositions.get(currentDocument).iterator();  // to support the wrap around
            return false; // indicate the end of term position entries for a given document;
        }
        currentPosition = positionIterator.next();
        return true;
    }

    @Override
    public int getCurrentTermPosition() {
        if (currentPosition == -1) {
            throw new NoSuchElementException("No position is selected!");
        }
        return currentPosition;
    }
}
