package uk.ac.ed.yazzzam.index.postinglists;

import java.util.NoSuchElementException;

public class EmptyPostingList implements ProximityPostingListIterator {
    @Override
    public boolean moveToNextDocument() {
        return false;
    }

    @Override
    public int getCurrentDocument() throws NoSuchElementException {
        throw new NoSuchElementException("Empty Posting List");
    }

    @Override
    public boolean moveToNextTermPosition() {
        return false;
    }

    @Override
    public int getCurrentTermPosition() throws NoSuchElementException {
        throw new NoSuchElementException("Empty Posting List");
    }
}
