package uk.ac.ed.yazzzam.index.postinglists;

import uk.ac.ed.yazzzam.compression.VariableByte;

import java.io.EOFException;
import java.util.NoSuchElementException;

// WIP
// TODO: Comment

public class VarByteProximityPostingList implements ProximityPostingListIterator {

    private final byte[] compressedPostingList;

    private int currentDocumentPointer;
    private int nextDocumentPointer;
    private int currentDocumentTermCountPointer;
    private int nextTermPositionPointer;
    private int currentDocumentId;
    private int currentTermPosition;
    private int remainingTermPositions;
    private int currentDocumentTermCount;


    protected VarByteProximityPostingList(byte[] postingList) {
        this.compressedPostingList = postingList;
        resetPostingListIterators();
    }

    @Override
    public boolean moveToNextDocument() {
        if (nextDocumentPointer != -1 && nextDocumentPointer < compressedPostingList.length) {
            currentDocumentPointer = nextDocumentPointer;
            nextDocumentPointer = -1;
        } else {
            try {
                currentDocumentPointer = skipPositions(remainingTermPositions, nextTermPositionPointer);
                if (currentDocumentPointer >= compressedPostingList.length) {
                    resetPostingListIterators();
                    return false;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                resetPostingListIterators();
                return false;
            }
        }
        updateDocumentMeta();
        return true;
    }

    private void updateDocumentMeta() {
        int[] res;
        try {
            res = VariableByte.decodeNumber(compressedPostingList, currentDocumentPointer);
        } catch (EOFException e) {
            e.printStackTrace();
            throw new IllegalStateException("Posting list: EOF exception when decoding the number");
        }

        if (currentDocumentId != -1) {
            currentDocumentId += res[0];
        } else {
            currentDocumentId = res[0];
        }

        currentDocumentTermCountPointer = res[1];

        try {
            res = VariableByte.decodeNumber(compressedPostingList, currentDocumentTermCountPointer);
        } catch (EOFException e) {
            e.printStackTrace();
            throw new IllegalStateException("Posting list: EOF exception when decoding the number");
        }
        currentDocumentTermCount = res[0];
        remainingTermPositions = currentDocumentTermCount;

        nextTermPositionPointer = res[1];
    }

    private void resetPostingListIterators() {
        currentDocumentPointer = -1;
        nextDocumentPointer = 0;
        currentDocumentTermCountPointer = -1;
        nextTermPositionPointer = -1;
        remainingTermPositions = -1;
        currentDocumentId = -1;
        currentTermPosition = -1;
        currentDocumentTermCount = -1;
    }

    @Override
    public int getCurrentDocument() throws NoSuchElementException {
        if (currentDocumentId == -1) {
            throw new NoSuchElementException("No document is selected!");
        }
        return currentDocumentId;
    }

    @Override
    public boolean moveToNextTermPosition() {
        if (remainingTermPositions == 0) {
            // reset term position iterator properties
            nextDocumentPointer = nextTermPositionPointer;
            nextTermPositionPointer = skipPositions(1, currentDocumentTermCountPointer);
            currentTermPosition = -1;
            remainingTermPositions = currentDocumentTermCount;
            return false;
        }

        int[] res;
        try {
            res = VariableByte.decodeNumber(compressedPostingList, nextTermPositionPointer);
        } catch (EOFException e) {
            e.printStackTrace();
            throw new IllegalStateException("Posting list: EOF exception when decoding the number");
        }

        if (currentTermPosition != -1) {
            currentTermPosition += res[0];
        } else {
            currentTermPosition = res[0];
        }

        nextTermPositionPointer = res[1];
        remainingTermPositions--;

        return true;
    }

    @Override
    public int getCurrentTermPosition() throws NoSuchElementException {
        if (currentTermPosition == -1) {
            throw new NoSuchElementException("No position is selected!");
        }
        return currentTermPosition;
    }

    // TODO (ENHANCEMENT): ADD SKIP POINTERS

    /**
     * Skips n integers in the var-byte encoded posting list
     * and returns the index of the first byte of the n+1-th integer.
     * @param n - number of integers to skip
     * @param offset - posting list's index of the first encoded integer to skip
     * @return an offset pointing to the beginning of the (n+1)-th var-byte encoded integer (starting from "offset")
     * @throws ArrayIndexOutOfBoundsException - when
     */
    private int skipPositions(int n, int offset) throws ArrayIndexOutOfBoundsException {
        var newOffset = offset;
        while (n > 0) {
            if (newOffset >= compressedPostingList.length) {
                throw new ArrayIndexOutOfBoundsException("Cannot skip beyond the end of the posting list!");
            }
            if ((compressedPostingList[newOffset] & 0xff) >= 128) {
                n--;
            }
            newOffset++;
        }
        return newOffset;
    }
}
