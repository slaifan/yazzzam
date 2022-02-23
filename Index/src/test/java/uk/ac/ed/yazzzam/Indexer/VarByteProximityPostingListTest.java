package uk.ac.ed.yazzzam.Indexer;

import org.junit.jupiter.api.Test;
import uk.ac.ed.yazzzam.compression.VariableByte;
import uk.ac.ed.yazzzam.disk.PostingListDiskRepr;
import uk.ac.ed.yazzzam.index.postinglists.PostingListFactory;
import uk.ac.ed.yazzzam.index.postinglists.VarByteProximityPostingList;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class VarByteProximityPostingListTest {

    @Test
    void DecodesCompressedPostingList() {
        var originalPostingList = List.of(1,2,1,7,2,3,6,17,197,3,1,1);  // [(1, 2, [1, 7]) (2, 3, [6, 17, 197]) (3, 1, [1])]
        var encodedPostingList = List.of(1,2,1,6,1,3,6,11,180,1,1,1);  // delta encoded
        var compressedPostingList = VariableByte.encode(encodedPostingList);

        var postingList = PostingListFactory.load(PostingListDiskRepr.PROXIMITY_VARBYTE_COMPRESSED, compressedPostingList);

        var reconstructedPostingList = new ArrayList<Integer>();
        while(postingList.moveToNextDocument()) {
            reconstructedPostingList.add(postingList.getCurrentDocument());

            var reconstructedTermPositions = new ArrayList<Integer>();
            while (postingList.moveToNextTermPosition()) {
                reconstructedTermPositions.add(postingList.getCurrentTermPosition());
            }
            reconstructedPostingList.add(reconstructedTermPositions.size());
            reconstructedPostingList.addAll(reconstructedTermPositions);
        }

        assertArrayEquals(originalPostingList.toArray(new Integer[0]), reconstructedPostingList.toArray(new Integer[0]));
    }

    @Test
    void CyclesThroughDocumentsInPostingList() {
        var originalPostingList = List.of(1,2,1,7,2,3,6,17,197,3,1,1);  // [(1, 2, [1, 7]) (2, 3, [6, 17, 197]) (3, 1, [1])]
        var encodedPostingList = List.of(1,2,1,6,1,3,6,11,180,1,1,1);  // delta encoded
        var compressedPostingList = VariableByte.encode(encodedPostingList);

        var postingList = PostingListFactory.load(PostingListDiskRepr.PROXIMITY_VARBYTE_COMPRESSED, compressedPostingList);

        var docs1 = new ArrayList<Integer>();
        while(postingList.moveToNextDocument()) {
            docs1.add(postingList.getCurrentDocument());
        }

        var docs2 = new ArrayList<Integer>();
        while(postingList.moveToNextDocument()) {
            docs2.add(postingList.getCurrentDocument());
        }

        assertArrayEquals(List.of(1, 2, 3).toArray(new Integer[0]), docs1.toArray(new Integer[0]), "Wrong documents retrieved");
        assertArrayEquals(docs1.toArray(new Integer[0]), docs2.toArray(new Integer[0]), "Cycled documents are different");
    }

    @Test
    void CyclesThroughTermPositionsInDocument() {
        var originalPostingList = List.of(1,2,1,7,2,3,6,17,197,3,1,1);  // [(1, 2, [1, 7]) (2, 3, [6, 17, 197]) (3, 1, [1])]
        var encodedPostingList = List.of(1,2,1,6,1,3,6,11,180,1,1,1);  // delta encoded
        var compressedPostingList = VariableByte.encode(encodedPostingList);

        var postingList = PostingListFactory.load(PostingListDiskRepr.PROXIMITY_VARBYTE_COMPRESSED, compressedPostingList);

        postingList.moveToNextDocument();
        postingList.moveToNextDocument();

        var termPos1 = new ArrayList<Integer>();
        while(postingList.moveToNextTermPosition()) {
            termPos1.add(postingList.getCurrentTermPosition());
        }

        var termPos2 = new ArrayList<Integer>();
        while(postingList.moveToNextTermPosition()) {
            termPos2.add(postingList.getCurrentTermPosition());
        }

        assertArrayEquals(List.of(6,17,197).toArray(new Integer[0]), termPos1.toArray(new Integer[0]), "Wrong term positions retrieved");
        assertArrayEquals(termPos1.toArray(new Integer[0]), termPos2.toArray(new Integer[0]), "Cycled term positions are different");
    }
}