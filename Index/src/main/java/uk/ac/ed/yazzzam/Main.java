package uk.ac.ed.yazzzam;

import uk.ac.ed.yazzzam.Indexer.Song;
import uk.ac.ed.yazzzam.Ranker.BM25ProximityFuzzy;
import uk.ac.ed.yazzzam.Search.PhraseSearch;
import uk.ac.ed.yazzzam.WebServer.JsonTransformer;
import uk.ac.ed.yazzzam.database.SongData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

import static spark.Spark.after;
import static spark.Spark.get;

public class Main {
    public static void main(String[] args) {
        var db = GlobalSettings.getDB();
        var numSongs = GlobalSettings.getDB().getSongCount();
        ListIterator<SongData> songsIter = db.getAllSongs().listIterator();
        System.out.println("finished reading from db, number of songs = " + numSongs);
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
//                System.out.println(memoryState());
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

        var phraseSearch = new PhraseSearch();


        get("/search", (request, response) -> {
            var lyrics = request.queryParamOrDefault("lyrics", GlobalSettings.NO_SEARCH);
            var genre = request.queryParamOrDefault("genre", GlobalSettings.NO_SEARCH);
            var year = request.queryParamOrDefault("year", GlobalSettings.NO_SEARCH);
            var title = request.queryParamOrDefault("title", GlobalSettings.NO_SEARCH);
            var artist = request.queryParamOrDefault("artist", GlobalSettings.NO_SEARCH);

            var needsFilter = !((genre.equals(GlobalSettings.NO_SEARCH)) && (title.equals(GlobalSettings.NO_SEARCH))
                    && (artist.equals(GlobalSettings.NO_SEARCH))  && (year.equals(GlobalSettings.NO_SEARCH)));

            if (needsFilter) {
                var filteredIds = db.filterSongs(genre, year, title, artist);
                return ranker.getResultsFiltered(lyrics, filteredIds);
            }
            else {
                return ranker.getResults(lyrics);
            }
        }, new JsonTransformer());

        get("/phraseSearch", (request, response) -> {
            var lyrics = request.queryParamOrDefault("lyrics", GlobalSettings.NO_SEARCH);
            var genre = request.queryParamOrDefault("genre", GlobalSettings.NO_SEARCH);
            var year = request.queryParamOrDefault("year", GlobalSettings.NO_SEARCH);
            var title = request.queryParamOrDefault("title", GlobalSettings.NO_SEARCH);
            var artist = request.queryParamOrDefault("artist", GlobalSettings.NO_SEARCH);

            var needsFilter = !((genre.equals(GlobalSettings.NO_SEARCH)) && (title.equals(GlobalSettings.NO_SEARCH))
                    && (artist.equals(GlobalSettings.NO_SEARCH))  && (year.equals(GlobalSettings.NO_SEARCH)));

            if (needsFilter) {
                var filteredIds = db.filterSongs(genre, year, title, artist);
                return phraseSearch.getResults(lyrics, filteredIds);
            }
            else {
                return phraseSearch.getResults(lyrics);
            }
        }, new JsonTransformer());


        get("/song", (request, response) -> {
            var songId = Integer.parseInt(request.queryParams("id"));
            var song = db.getSong(songId);
            song.lyrics = song.lyrics.replaceAll("\\s*(?<punctuation>[,.]+)\\s+", "${punctuation}\n");
            return song;
        }, new JsonTransformer());

        get("/allGenres", (request, response) -> {
            var genres = db.getAllGenres();
            Collections.sort(genres);
            return genres;
        }, new JsonTransformer());

        get("/allArtists", (request, response) -> {
            var artists = db.getAllArtists();
            Collections.sort(artists);
            return artists;
        }, new JsonTransformer());

        get("/allTitles", (request, response) -> {
            var titles = db.getAllTitles();
            Collections.sort(titles);
            return titles;
        }, new JsonTransformer());

        get("/similarSongs", (request, response) -> {
            var genre = request.queryParams("genre");
            var artist = request.queryParams("artist");
            var year = request.queryParams("year");

            var songs = new ArrayList<SongData>();

            songs.addAll(db.getSameGenre(genre));
            songs.addAll(db.getSameArtist(artist));
            songs.addAll(db.getSameYear(year));
            return songs;
        }, new JsonTransformer());

        after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
        });


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
