package uk.ac.ed.yazzzam;

import uk.ac.ed.yazzzam.disk.BinaryProximityIndexWriter;
import uk.ac.ed.yazzzam.Indexer.IndexBuilder;
import uk.ac.ed.yazzzam.Indexer.TextFileReader;
import uk.ac.ed.yazzzam.disk.FullPostingListDiskReader;
import uk.ac.ed.yazzzam.index.CompressedProximityInvertedIndex;
import uk.ac.ed.yazzzam.index.ProximityInvertedIndex;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var documentsFile = new TextFileReader().readFile(args[0]);

        IndexBuilder ib = new IndexBuilder();
        ib.preprocess_documents(documentsFile);
        var invertedIndex = new ProximityInvertedIndex(ib.buildIndex());

        var indexWriter = new BinaryProximityIndexWriter();
        indexWriter.writeToFile(invertedIndex, "test");

        var indexDiskReader = new FullPostingListDiskReader();
        var reconstructedInvertedIndex = new CompressedProximityInvertedIndex(indexDiskReader.readAll("index/test.ii"));

        indexWriter.writeToFile(reconstructedInvertedIndex, "test2");
    }
}
