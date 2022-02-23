package uk.ac.ed.yazzzam;

import uk.ac.ed.yazzzam.Indexer.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
//        var documentsFile = new TextFileReader().readFile(args[0]);
        var txtFile = args[0];      // Text file with the songs
        IndexBuilder ib = new IndexBuilder();
        ib.preprocess_text_file(txtFile);
        var invertedIndex = new ProximityInvertedIndex(ib.buildIndex());

        IndexWriter<ProximityPostingList> indexWriter = new BinaryProximityIndexWriter<>();
        indexWriter.writeToFile(invertedIndex, "test");
    }
}
