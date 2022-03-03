package uk.ac.ed.yazzzam;

import uk.ac.ed.yazzzam.Indexer.CSVReader;
import uk.ac.ed.yazzzam.Indexer.Song;
import uk.ac.ed.yazzzam.Preprocessor.BasicPreprocessor;
import uk.ac.ed.yazzzam.Search.PhraseSearch;
import uk.ac.ed.yazzzam.Indexer.IndexBuilder;
import uk.ac.ed.yazzzam.Indexer.TextFileReader;
import uk.ac.ed.yazzzam.database.ConnectDB;
import uk.ac.ed.yazzzam.database.Sql2oModel;
import uk.ac.ed.yazzzam.disk.BinaryProximityIndexWriter;
import uk.ac.ed.yazzzam.index.InvertedIndex;
import uk.ac.ed.yazzzam.index.ProximityInvertedIndex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static spark.Spark.get;

public class Main {

    public static InvertedIndex invertedIndex;

    public static void main(String[] args) throws IOException {
        Long startIndexBuild = System.nanoTime();

        IndexBuilder ib = new IndexBuilder();

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
//        get("/hello", (request, response) -> {
//            var query = request.queryParams("song");
//            testSearch(query);
//            return "thanks for using lol";
//        });
        get("/hello", (req, res) -> "Hello, World!");

        ConnectDB conn = new ConnectDB("jdbc:postgresql://localhost:5432/song", "postgres", "ttds_YAZZZAM123");
        conn.connect();
        Sql2oModel model = conn.getModel();

        var reader = new CSVReader();
        List<Song> songs = reader.readFile(args[0]);

       model.insertBunchOfSongs(songs);




    }

    private static void testSearch(String query) {
        var prec = new BasicPreprocessor();
        var q = prec.preprocess(query);
        var res = PhraseSearch.Search(q, invertedIndex);
        System.out.println(res);
    }

    private static Double getTimeSeconds(Long start, Long end) {
        Long durationNano = end - start;
        return (double) durationNano / 1_000_000_000;
    }








}
