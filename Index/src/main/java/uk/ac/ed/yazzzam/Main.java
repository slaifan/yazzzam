package uk.ac.ed.yazzzam;

import uk.ac.ed.yazzzam.Indexer.CSVReader;
import uk.ac.ed.yazzzam.Indexer.Song;
import uk.ac.ed.yazzzam.Preprocessor.Preprocessor;
import uk.ac.ed.yazzzam.Ranker.BM25;
import uk.ac.ed.yazzzam.Ranker.Ranker;
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
        ListIterator<Song> songsIter = CSVReader.readFile(GlobalSettings.inputFile).listIterator();
        var i = 0;
        while (songsIter.hasNext()){
            var song = songsIter.next();
            GlobalSettings.getIndex().preprocessSong(song);
            GlobalSettings.getIndex().indexSong(i, song);
            songsIter.remove();
            i++;
        }
        System.out.println(memoryState());


        var preprocessor = GlobalSettings.getPreprocessor();


        //YAZAN CHANGE RANKER HERE
        var ranker = new BM25(GlobalSettings.ranker_k1, GlobalSettings.ranker_b, GlobalSettings.ranker_epsilon, GlobalSettings.ranker_n);
//        var ranker = new BM25Proximity(GlobalSettings.ranker_k1, GlobalSettings.ranker_b, GlobalSettings.ranker_epsilon, GlobalSettings.ranker_n, GlobalSettings.proximity_c, GlobalSettings.proximity_threshold);


        get("/search", (request, response) -> {
            var query = request.queryParams("lyrics");
            var res =  testSearch(query, preprocessor, ranker);
            return res.stream().map(e -> new SearchResult(e)).collect(Collectors.toList());
        }, new JsonTransformer());

//        var query = "i love the way you lie";
//        Long startSearch = System.nanoTime();
//        var res =  testSearch(query, preprocessor, ranker);
//        Long endSearch = System.nanoTime();
//        System.out.println("searching took: " + getTimeSeconds(startSearch, endSearch)+ " seconds");
//        System.out.println(res.stream().map(e -> new SearchResult(e)).collect(Collectors.toList()));

    }

    private static ArrayList<ScoringResult> testSearch(String query, Preprocessor prec, Ranker ranker) {
        var q = prec.preprocess(query);
        var res = ranker.score(q);
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
