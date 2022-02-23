package uk.ac.ed.yazzzam.disk;

import uk.ac.ed.yazzzam.compression.VariableByte;
import uk.ac.ed.yazzzam.index.InvertedIndex;
import uk.ac.ed.yazzzam.utils.ByteUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.StreamSupport;

public class BinaryProximityIndexWriter implements IndexWriter {

    public final static PostingListDiskRepr usedDiskRepr = PostingListDiskRepr.PROXIMITY_VARBYTE_COMPRESSED;

    // TODO: SHOULD ADD THE LENGTH OF THE ENTRY?
    @Override
    public Map<String, Integer> writeToFile(InvertedIndex invertedIndex, String fileName) throws IOException {
        // get all terms stored in the index in the sorted order
        var termsStream = StreamSupport.stream(invertedIndex.getTermSpliterator(), false).sorted();
        var termOffsets = new LinkedHashMap<String, Integer>();  // using LinkedHashMap so the offset are in order of writing to the file
        var byteOffset = 0;

        var indexDirectory = System.getProperty("user.dir") + File.separator + "index" + File.separator;
        Files.createDirectories(Paths.get(indexDirectory));

        var indexFileName = indexDirectory + fileName + ".ii";
        try (var indexOutputStream = new BufferedOutputStream(new FileOutputStream(indexFileName));
             var metaFileWriter = new BufferedWriter(new FileWriter(indexFileName + ".meta", StandardCharsets.UTF_8, false))
        ) {
            metaFileWriter.write(String.format("type,%s\n", usedDiskRepr.name()));  // used to read the index

            for (var term : (Iterable<String>) termsStream::iterator) {
                termOffsets.put(term, byteOffset);  // save the offset

                indexOutputStream.write(ByteUtils.fromStringNullTerminated(term));
                byteOffset += term.length() + 1;  // due to the NULL terminator

                var postingList = invertedIndex.getPostingList(term);
                var prevDocId = 0;  // used to delta encode the document numbers

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
                metaFileWriter.append(String.format("%s,%d\n", term, termOffsets.get(term)));
            }
            // flush the contents of the output buffers
            indexOutputStream.flush();
            metaFileWriter.flush();
        }

        return termOffsets;
    }
}

