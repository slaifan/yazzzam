package uk.ac.ed.yazzzam.Indexer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var file = Reader.readFile("songs.txt");
        IndexBuilder ib = new IndexBuilder(file);
        var invertedIndex = new ProximityInvertedIndex(ib.buildIndex());

        IndexWriter<ProximityPostingList> indexWriter = new BinaryProximityIndexWriter<>();
        indexWriter.writeToFile(invertedIndex, "test");
    }
}
