package uk.ac.ed.yazzzam.index.postinglists;

import uk.ac.ed.yazzzam.disk.PostingListDiskRepr;

public class PostingListFactory {
    public static ProximityPostingListIterator load(PostingListDiskRepr postingListDiskRepr, byte[] data) {
        if (postingListDiskRepr == PostingListDiskRepr.PROXIMITY_VARBYTE_COMPRESSED) {
            return new VarByteProximityPostingList(data);
        }
        throw new IllegalArgumentException(String.format("%s is not supported!", postingListDiskRepr));
    }
}
