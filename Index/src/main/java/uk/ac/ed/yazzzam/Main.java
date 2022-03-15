package uk.ac.ed.yazzzam;

import uk.ac.ed.yazzzam.Indexer.Song;
import uk.ac.ed.yazzzam.Preprocessor.Preprocessor;
import uk.ac.ed.yazzzam.Ranker.BM25ProximityFuzzy;
import uk.ac.ed.yazzzam.Ranker.Ranker;
import uk.ac.ed.yazzzam.Search.PhraseSearch;
import uk.ac.ed.yazzzam.WebServer.JsonTransformer;
import uk.ac.ed.yazzzam.WebServer.SearchResult;
import uk.ac.ed.yazzzam.database.SongData;

import java.util.ArrayList;
import java.util.ListIterator;

import static spark.Spark.get;

public class Main {
    public static void main(String[] args) {
        var db = GlobalSettings.getDB();
        ListIterator<SongData> songsIter = db.getAllSongs().listIterator();
        System.out.println("finished reading from db, number of songs = " + GlobalSettings.getDB().getSongCount());
        var i = 0;
        while (songsIter.hasNext()){
            var songData = songsIter.next();
            var song = new Song(songData);
            GlobalSettings.getIndex().preprocessSong(song);
            GlobalSettings.getIndex().indexSong(songData.getId(), song);
            songsIter.remove();
            song = null;
            if (i % 50000 == 0) {
                System.gc();
                System.out.println("number of songs index: " + i);
                System.out.println(memoryState());
            }
            i++;
        }
        System.gc(); // collect garbage

        System.out.println(memoryState());

        var preprocessor = GlobalSettings.getPreprocessor();


        //YAZAN CHANGE RANKER HERE
//        var ranker = new BM25(GlobalSettings.ranker_k1, GlobalSettings.ranker_b, GlobalSettings.ranker_epsilon, GlobalSettings.ranker_n);
//        var ranker = new BM25Proximity(GlobalSettings.ranker_k1, GlobalSettings.ranker_b, GlobalSettings.ranker_epsilon, GlobalSettings.ranker_n, GlobalSettings.proximity_c, GlobalSettings.proximity_threshold);
        var ranker = new BM25ProximityFuzzy(GlobalSettings.ranker_k1, GlobalSettings.ranker_b, GlobalSettings.ranker_epsilon, GlobalSettings.ranker_n, GlobalSettings.proximity_c, GlobalSettings.proximity_threshold);

        get("/search", (request, response) -> {
            var query = request.queryParams("lyrics");
            return testSearch(query, preprocessor, ranker);
        }, new JsonTransformer());

        get("/phraseSearch", (request, response) -> {
            var query = request.queryParams("lyrics");
            return testPhraseSearch(query, preprocessor);
        }, new JsonTransformer());

        get("/song", (request, response) -> {
            var songId = Integer.parseInt(request.queryParams("id"));
            var song = db.getSong(songId);
            return song;
        }, new JsonTransformer());

        get("/allGenres", (request, response) -> {
            var genres = db.getAllGenres();
            return genres;
        }, new JsonTransformer());

        get("/allArtists", (request, response) -> {
            var artists = db.getAllArtists();
            return artists;
        }, new JsonTransformer());

        get("/allTitles", (request, response) -> {
            var titles = db.getAllTitles();
            return titles;
        }, new JsonTransformer());


//        var query = "i love the way you lie";
//        Long startSearch = System.nanoTime();
//        var res =  testSearch(query, preprocessor, ranker);
//        Long endSearch = System.nanoTime();
//        System.out.println("searching took: " + getTimeSeconds(startSearch, endSearch)+ " seconds");
//        System.out.println(res.stream().map(e -> new SearchResult(e)).collect(Collectors.toList()));

    }

    private static ArrayList<SearchResult> testPhraseSearch(String query, Preprocessor preprocessor) {
        return PhraseSearch.getResults(query);
    }

    private static ArrayList<SearchResult> testSearch(String query, Preprocessor prec, Ranker ranker) {
//        var q = prec.preprocess(query);
//        System.out.println(q);
        var res = ranker.getResults(query);
        return res;
    }

    private static Double getTimeSeconds(Long start, Long end) {
        Long durationNano = end - start;
        return (double) durationNano / 1_000_000_000;
    }

    private static String memoryState(){
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
