package uk.ac.ed.yazzzam;

import uk.ac.ed.yazzzam.Indexer.CSVReader;
import uk.ac.ed.yazzzam.Indexer.IndexBuilder;
import uk.ac.ed.yazzzam.Indexer.Song;
import uk.ac.ed.yazzzam.Preprocessor.FullProcessor;
import uk.ac.ed.yazzzam.Preprocessor.Preprocessor;
import uk.ac.ed.yazzzam.Ranker.BM25;
import uk.ac.ed.yazzzam.Ranker.BM25Proximity;
import uk.ac.ed.yazzzam.Ranker.ScoringResult;
import uk.ac.ed.yazzzam.WebServer.JsonTransformer;
import uk.ac.ed.yazzzam.WebServer.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.stream.Collectors;

import static spark.Spark.get;


public class Main {

    public static void main(String[] args) throws IOException {

        Long startIndexBuild = System.nanoTime();
        ListIterator<Song> songsIter = CSVReader.readFile("test_song01.csv").listIterator();
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

        var preprocessor = new FullPreprocessor("englishST.txt");
        var ranker = new BM25Proximity(ib, 50);
//        var ranker = new BM25(ib);

//        var query = "i love the way you lie";
//        Long startSearch = System.nanoTime();
//        var res =  testSearch(query, preprocessor, ranker, ib);
//        Long endSearch = System.nanoTime();
//        System.out.println("searching took: " + getTimeSeconds(startSearch, endSearch)+ " seconds");
//        System.out.println(res.stream().map(e -> new SearchResult(e, ib)).collect(Collectors.toList()));

        get("/search", (request, response) -> {
            var query = request.queryParams("lyrics");
            var res =  testSearch(query, preprocessor, ranker, ib);
            return res.stream().map(e -> new SearchResult(e, ib)).collect(Collectors.toList());
        }, new JsonTransformer());

        var preprocessor = new FullProcessor("englishST.txt");

        var ranker = new BM25(ib);


        Long endIndexBuild = System.nanoTime();

    }


    private static ArrayList<ScoringResult> testSearch(String query, Preprocessor prec, BM25 ranker, IndexBuilder ib) {
        var q = prec.preprocess(query);
        var res = ranker.score(q);
        ArrayList<String> out = new ArrayList<>();
        for (int i = 0; i < res.size(); i++) {
            out.add(i + " - " + ib.getTitle(res.get(i).docId));
        }
        return res;
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
