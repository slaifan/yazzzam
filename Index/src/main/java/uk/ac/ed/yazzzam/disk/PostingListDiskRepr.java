package uk.ac.ed.yazzzam.disk;

public enum PostingListDiskRepr {
    PROXIMITY_VARBYTE_COMPRESSED (true);

    private final boolean isCompressed;
    PostingListDiskRepr(boolean isCompressed) {
        this.isCompressed = isCompressed;
    }

    public boolean isCompressed() {
        return isCompressed;
    }
}
