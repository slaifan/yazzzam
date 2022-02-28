package uk.ac.ed.yazzzam;

import uk.ac.ed.yazzzam.Preprocessor.BasicPreprocessor;
import uk.ac.ed.yazzzam.Search.PhraseSearch;
import uk.ac.ed.yazzzam.Indexer.IndexBuilder;
import uk.ac.ed.yazzzam.Indexer.TextFileReader;
import uk.ac.ed.yazzzam.Server.ServerApplication;
import uk.ac.ed.yazzzam.index.InvertedIndex;
import uk.ac.ed.yazzzam.index.ProximityInvertedIndex;

import java.io.IOException;
import java.util.HashSet;
import java.util.stream.StreamSupport;

public class Main {

    private static InvertedIndex invertedIndex;
    public static void main(String[] args) throws IOException {
        var documentsFile = new TextFileReader().readFile(args[0]);

        Long startIndexBuild = System.nanoTime();

        IndexBuilder ib = new IndexBuilder();
        ib.preprocess_documents(documentsFile);

        Long endIndexBuild = System.nanoTime();

        System.out.println("building index took: " + getTimeSeconds(startIndexBuild, endIndexBuild)+ " seconds");


        invertedIndex = new ProximityInvertedIndex(ib.buildIndex());

        StreamSupport.stream(invertedIndex.getTermSpliterator(), false).forEachOrdered(System.out::println);

//        System.out.println(invertedIndex.getPostingList());
        var prec = new BasicPreprocessor();
        var q = prec.preprocess("I have");

        System.out.println(PhraseSearch.Search(q, invertedIndex));
        System.out.println(PhraseSearch.Search(q, invertedIndex));

        System.out.println(PhraseSearch.Search(q, invertedIndex));

        System.out.println(PhraseSearch.Search(q, invertedIndex));


        ServerApplication.main(new String[]{});

//        var similarity = new SimilaritySearch(invertedIndex.inverted_index.keySet()); // to test change visibility of MapBasedInvertedIndex.invertedIndex to public

//        var indexWriter = new BinaryProximityIndexWriter();
//        indexWriter.writeToFile(invertedIndex, "test");
//
//        var indexDiskReader = new FullPostingListDiskReader();
//        var reconstructedInvertedIndex = new CompressedProximityInvertedIndex(indexDiskReader.readAll("index/test.ii"));
//
//        indexWriter.writeToFile(reconstructedInvertedIndex, "test2");

//        var x = invertedIndex.getPostingList("my");
//        System.out.println(x);
//        var q1 = "laptop in my back pocket";
//        var q2 = "I";
//        var q3 = "I have something in my pocket";
//
//        Long startSearch = System.nanoTime();
//
//        System.out.println(similarity.findSimilarWords("odin", 2));
//
//        testSearch(q1, invertedIndex);
//        testSearch(q2, invertedIndex);
//        testSearch(q3, invertedIndex);
//
//        Long endSearch = System.nanoTime();
//
//        System.out.println("searching took: " + getTimeSeconds(startSearch, endSearch)+ " seconds");
    }


    public static HashSet<Integer> testSearch(String query) {
        var prec = new BasicPreprocessor();
        var q = prec.preprocess(query);
        System.out.println(q);

        System.out.println(invertedIndex);

        StreamSupport.stream(invertedIndex.getTermSpliterator(), false).forEachOrdered(System.out::println);

        var res = PhraseSearch.Search(q, invertedIndex);
        return res;
    }

    private static Double getTimeSeconds(Long start, Long end) {
        Long durationNano = end - start;
        return (double) durationNano / 1_000_000_000;
    }
}
