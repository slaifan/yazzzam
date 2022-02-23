package uk.ac.ed.yazzzam.disk;

import uk.ac.ed.yazzzam.compression.VariableByte;
import uk.ac.ed.yazzzam.index.postinglists.PostingListFactory;
import uk.ac.ed.yazzzam.index.postinglists.ProximityPostingListIterator;
import uk.ac.ed.yazzzam.utils.ByteUtils;
import uk.ac.ed.yazzzam.utils.Pair;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Assumes the posting list entry format: [term], [\0], [posting list...]
 */
public class FullPostingListDiskReader {

    public Map<String, ProximityPostingListIterator> readAll(String filename) {
        String postingListDiskReprString = null;
        PostingListDiskRepr postingListDiskRepr;

        var postingLists = new HashMap<String, ProximityPostingListIterator>();

        try (var metaReader = new BufferedReader(new FileReader(filename + ".meta", StandardCharsets.UTF_8));
             var indexReader = new BufferedInputStream(new FileInputStream(filename))) {

            postingListDiskReprString = metaReader.readLine().strip().split(",")[1];
            postingListDiskRepr = PostingListDiskRepr.valueOf(postingListDiskReprString);

            var currentTermMeta = metaReader.readLine().strip().split(",");

            while (true) {
                var termPair = readTerm(indexReader);
                if (termPair == null) {
                    // TODO: HANDLE ERROR
                    System.out.println("Cannot decode the term: EOF!");
                    System.exit(1001);
                }

                var term = termPair.getFirst();
                var termByteSize = termPair.getSecond();

                if (!term.equalsIgnoreCase(currentTermMeta[0])) {
                    // TODO: HANDLE ERROR
                    System.out.printf("Unexpected term! Got: %s, Expected: %s!\n", term, currentTermMeta[0]);
                    System.exit(1001);
                }

                var nextTermMetaLine = metaReader.readLine();

                if (nextTermMetaLine != null) {
                    var nextTermMeta = nextTermMetaLine.strip().split(",");

                    var bytesToRead = Integer.parseInt(nextTermMeta[1]) - Integer.parseInt(currentTermMeta[1]) - (termByteSize + 1);
                    if (bytesToRead <= 0) {
                        // TODO: HANDLE ERROR
                        System.out.println("Cannot decode bytes to read");
                        System.exit(1002);
                    }

                    var postingListData =  indexReader.readNBytes(bytesToRead);
                    var postingList = PostingListFactory.load(postingListDiskRepr, postingListData);
                    postingLists.put(term, postingList);

                    currentTermMeta = nextTermMeta;

                } else {
                    // last entry
                    var postingListData =  indexReader.readAllBytes();
                    var postingList = PostingListFactory.load(postingListDiskRepr, postingListData);
                    postingLists.put(term, postingList);
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            // TODO: HANDLE ERROR
            System.out.println("IO exception");
            System.exit(1004);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            // TODO: HANDLE ERROR
            System.out.printf("%s is not a valid posting list repr!\n", postingListDiskReprString);
            System.exit(1005);
        }
        return postingLists;
    }

    private Pair<String, Integer> readTerm(InputStream is) throws IOException {
        var buffer = ByteBuffer.allocate(100);

        var nextByte = is.read();
        while (nextByte > 0) {
            buffer.put((byte) nextByte);

            nextByte = is.read();
        }

        buffer.flip();  // prepare buffer for reading
        if (nextByte == 0) {
            byte[] bytes = new byte[buffer.limit()];
            buffer.get(bytes);
            return new Pair<>(ByteUtils.toString(bytes), bytes.length);
        }
        // Otherwise error: EOF
        return null;
    }
}
