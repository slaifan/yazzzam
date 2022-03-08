package uk.ac.ed.yazzzam;

import uk.ac.ed.yazzzam.Indexer.CSVReader;
import uk.ac.ed.yazzzam.Indexer.IndexBuilder;
import uk.ac.ed.yazzzam.Indexer.Song;
import uk.ac.ed.yazzzam.Preprocessor.FullProcessor;
import uk.ac.ed.yazzzam.Ranker.BM25;

import uk.ac.ed.yazzzam.Search.PhraseSearch;
import uk.ac.ed.yazzzam.Indexer.IndexBuilder;
<<<<<<< HEAD
=======
import uk.ac.ed.yazzzam.Indexer.TextFileReader;
import uk.ac.ed.yazzzam.disk.BinaryProximityIndexWriter;
>>>>>>> b423f5c (Working SPARK!)
import uk.ac.ed.yazzzam.index.InvertedIndex;
import uk.ac.ed.yazzzam.index.ProximityInvertedIndex;
import java.util.Scanner;


import java.io.IOException;
<<<<<<< HEAD
import java.util.ListIterator;

public class Main {

=======
import java.util.stream.StreamSupport;

import static spark.Spark.get;

public class Main {

    public static InvertedIndex invertedIndex;

>>>>>>> b423f5c (Working SPARK!)
    public static void main(String[] args) throws IOException {

        Long startIndexBuild = System.nanoTime();
        ListIterator<Song> songsIter = CSVReader.readFile(args[0]).listIterator();
        Long endStoreDocs = System.nanoTime();
        System.out.println("storing documents into objects took: " + getTimeSeconds(startIndexBuild, endStoreDocs) + " seconds");
        System.out.println(memoryState());

        Long startProcessDocs = System.nanoTime();
        IndexBuilder ib = new IndexBuilder();
        var i = 0;
        while (songsIter.hasNext()){
            var song = songsIter.next();
            ib.preprocessSong(song);
            ib.indexSong(i, song);
            songsIter.remove();
            i++;
        }

        Long endProcessDocs = System.nanoTime();
        System.out.println("processing and indexing took: " + getTimeSeconds(startProcessDocs, endProcessDocs) + " seconds");
        System.out.println(memoryState());


        var preprocessor = new FullProcessor("englishST.txt");

<<<<<<< HEAD

        var ranker = new BM25(ib);
        Scanner keyboard = new Scanner(System.in);
        while (true) {
            System.out.println("enter lyrics");
            var q1 = keyboard.nextLine();
            Long startSearch = System.nanoTime();

            var prec_q = preprocessor.preprocess(q1);
            var out = ranker.score(prec_q);
            Long endSearch = System.nanoTime();
            for (int j = 0; j < out.size(); j++) {
                System.out.println(j + " - " + ib.getTitle(out.get(j).docId));
            }



            System.out.println("searching took: " + getTimeSeconds(startSearch, endSearch) + " seconds");

        }
        //System.out.println(idx);
        //System.out.println(ib.getDocLengths());

=======
        ib.preprocess_documents(args[0], null);

        Long endIndexBuild = System.nanoTime();
        invertedIndex = new ProximityInvertedIndex(ib.buildIndex());

        System.out.println("building index took: " + getTimeSeconds(startIndexBuild, endIndexBuild)+ " seconds");

        new BinaryProximityIndexWriter().writeToFile(invertedIndex, "test");

        testSearch("I feel under your command");

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
        get("/hello", (request, response) -> {
            var query = request.queryParams("song");
            testSearch(query);
            return "thanks for using lol";
        });
    }

    private static void testSearch(String query) {
        var prec = new BasicPreprocessor();
        var q = prec.preprocess(query);
        var res = PhraseSearch.Search(q, invertedIndex);
        System.out.println(res);
>>>>>>> b423f5c (Working SPARK!)
    }


    private static Double getTimeSeconds(Long start, Long end) {
        Long durationNano = end - start;
        return (double) durationNano / 1_000_000_000;
    }

    public static String memoryState(){
        int mb = 1024 * 1024;
        long heapSize = Runtime.getRuntime().totalMemory();
        long heapMaxSize = Runtime.getRuntime().maxMemory();
        long heapFreeSize = Runtime.getRuntime().freeMemory();
        long heapUsed = heapSize - heapFreeSize;
        return "heap size: " + heapSize/mb + "mb" +
                "\nheap max size: " + heapMaxSize/mb + "mb" +
                "\nheap free size: " + heapFreeSize/mb + "mb" +
                "\nheap used: " + heapUsed/mb + "mb";
    }
}
