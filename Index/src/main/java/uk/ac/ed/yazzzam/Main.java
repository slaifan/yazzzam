package uk.ac.ed.yazzzam;

import uk.ac.ed.yazzzam.Indexer.CSVReader;
import uk.ac.ed.yazzzam.Indexer.Song;
import uk.ac.ed.yazzzam.Preprocessor.BasicPreprocessor;
import uk.ac.ed.yazzzam.Preprocessor.FullProcessor;
import uk.ac.ed.yazzzam.Ranker.BM25;
import uk.ac.ed.yazzzam.Search.PhraseSearch;
import uk.ac.ed.yazzzam.Indexer.IndexBuilder;
import uk.ac.ed.yazzzam.index.InvertedIndex;
import uk.ac.ed.yazzzam.index.ProximityInvertedIndex;

import java.io.IOException;
import java.util.ListIterator;

public class Main {

    public static void main(String[] args) throws IOException {

        Long startIndexBuild = System.nanoTime();
        ListIterator<Song> songsIter = CSVReader.readFile(args[0]).listIterator();
        Long endStoreDocs = System.nanoTime();
        System.out.println("storing documents into objects took: " + getTimeSeconds(startIndexBuild, endStoreDocs) + " seconds");
        System.out.println(memoryState());

        Long startProcessDocs = System.nanoTime();
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

        var ranker = new BM25(ib);
        var q1 = "laptop in my back pocket";
        var prec_q = preprocessor.preprocess(q1);
        var out = ranker.score(prec_q);
        System.out.println(out);



        //System.out.println(idx);
        //System.out.println(ib.getDocLengths());

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
