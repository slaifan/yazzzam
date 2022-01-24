package uk.ac.ed.yazzzam.Indexer;

import uk.ac.ed.yazzzam.compression.VariableByte;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BinaryProximityIndexWriter<T extends TermPositionPostingListIterator> implements IndexWriter<T> {

    // TODO: MUST ADD THE LENGTH OF THE ENTRY
    @Override
    public Map<String, Integer> writeToFile(InvertedIndex<T> invertedIndex, String fileName) throws IOException {
        // get all terms stored in the index in the sorted order
        var terms = StreamSupport.stream(invertedIndex.getTermSpliterator(), false).sorted().collect(Collectors.toList());
        var termOffsets = new LinkedHashMap<String, Integer>();  // using LinkedHashMap so the offset are in order of writing to the file
        var byteOffset = 0;
        var prevDocId = 0;  // used to delta encode the document numbers

        var indexFileName = String.format("%s/%s/%s.%s", System.getProperty("user.dir"), "index", fileName, "ii");
        try (var indexOutputStream = new BufferedOutputStream(new FileOutputStream(indexFileName));
             var metaFileWriter = new BufferedWriter(new FileWriter(indexFileName + ".meta", false))
        ) {
            for (var term : terms) {
                termOffsets.put(term, byteOffset);  // save the offset

                var postingList = invertedIndex.getPostingList(term);

                while (postingList.moveToNextDocument()) {
                    var docId = postingList.getCurrentDocument();
                    var termInDocCount = 0;
                    var data = new LinkedList<Integer>();  // for fast prepends/appends
                    var prevTermPos = 0;  // used to delta encode the term positions

                    while (postingList.moveToNextTermPosition()) {
                        termInDocCount++;
                        var termPos = postingList.getCurrentTermPosition();
                        data.addLast(termPos - prevTermPos);  // delta encoded term position
                        prevTermPos = termPos;
                    }

                    data.addFirst(termInDocCount);
                    data.addFirst(docId - prevDocId);  // delta encoded doc id

                    var bytes = VariableByte.encode(data);  // var-byte compressed index file entry

                    indexOutputStream.write(bytes);

                    byteOffset += bytes.length;  // update the byte offset for the next term entry
                }
                // write index metadata file in csv format:
                // term,offset
                metaFileWriter.append(String.format("%s,%d\n", term, byteOffset));
            }
            // flush the contents of the output buffers
            indexOutputStream.flush();
            metaFileWriter.flush();
        }

        return termOffsets;
    }
}
